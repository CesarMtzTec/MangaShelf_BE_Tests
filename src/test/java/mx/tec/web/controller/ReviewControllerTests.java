package mx.tec.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.minidev.json.JSONValue;

@SpringBootTest
public class ReviewControllerTests {
    String endpoint = "https://manga-shelf-app.herokuapp.com";
    HttpClient client = HttpClient.newHttpClient();

    private String loginAndGetToken() throws Exception {
        Map<String, String> body = new HashMap<String, String>();
		body.put("username", "test");
        body.put("password", "password");
        
		HttpRequest loginRequest = HttpRequest.newBuilder(new URI(endpoint + "/api/authenticate"))
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.POST(BodyPublishers.ofString(JSONValue.toJSONString(body)))
				.build();

		HttpResponse<String> loginResponse = client.send(loginRequest, BodyHandlers.ofString());
		Map<String, String> loginResponseBody = new ObjectMapper().readValue(loginResponse.body(), HashMap.class);
        String token = loginResponseBody.get("token");
        
        return token;
    }
    
    @Test
	public void givenAToken_WhenReviewsRequest_ThenReceiveReviews() throws Exception {
        // Given
        String token = loginAndGetToken();

		// When
		HttpRequest request = HttpRequest.newBuilder(new URI(endpoint + "/api/reviews"))
				.header(HttpHeaders.AUTHORIZATION, "Bearer ".concat(token))
				.GET()
				.build();

		// Then
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		
		assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
		assertThat(response.body()).startsWith("[");
		assertThat(response.body()).endsWith("]");
	}

	@Test
    public void givenNoToken_WhenReviewsRequest_ThenUnauthorized() throws Exception {
        // Given
        HttpRequest request = HttpRequest.newBuilder(new URI(endpoint + "/api/reviews"))
                .GET()
                .build();

        // When
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);

    }
    
    @Test
    public void givenAReviewAndToken_WhenPublishReviewRequest_ThenOK() throws Exception {
        // Given
        String token = loginAndGetToken();

        Map<String, String> newReview = new HashMap<String, String>();
        newReview.put("userId", "0");
        newReview.put("mangaId", "2");
        newReview.put("description", "Test description");
        newReview.put("rate", "8");
        newReview.put("date", "2022-01-01");
        newReview.put("username", "cero");
        newReview.put("mangaTitle", "Chainsaw Man");


		// When
        HttpRequest request = HttpRequest.newBuilder(new URI(endpoint + "/api/reviews"))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .POST(BodyPublishers.ofString(JSONValue.toJSONString(newReview)))
                .build();

        // Then
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        Map<String, String> responseBody = new ObjectMapper().readValue(response.body(), HashMap.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(responseBody).isNotNull();

    }
    
}
