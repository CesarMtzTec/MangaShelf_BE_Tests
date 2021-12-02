/**
 * 
 */
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
import org.apache.http.HttpStatus;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.minidev.json.JSONValue;

/**
 * @author Cesar
 *
 */
@SpringBootTest
public class UserControllerTests {
	HttpClient client = HttpClient.newHttpClient();

	@Test
	public void givenALoginRequest_WhenCorrectCredentials_ThenReceiveToken() throws Exception {
		// Given
		Map<String, String> body = new HashMap<String, String>();
		body.put("username", "test");
		body.put("password", "password");

		// When
		HttpRequest request = HttpRequest.newBuilder(new URI("https://manga-shelf-app.herokuapp.com/api/authenticate"))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(JSONValue.toJSONString(body)))
				.build();

		// Then
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		Map<String, String> responseBody = new ObjectMapper().readValue(response.body(), HashMap.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
		assertThat(responseBody.get("token")).isNotNull();
	}

	@Test
	public void givenALoginRequest_WhenIncorrectCredentials_ThenBadCredentials() throws Exception {
		// Given
		Map<String, String> body = new HashMap<String, String>();
		body.put("username", "test");
		body.put("password", "wrongpassword");

		// When
		HttpRequest request = HttpRequest.newBuilder(new URI("https://manga-shelf-app.herokuapp.com/api/authenticate"))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(JSONValue.toJSONString(body)))
				.build();

		// Then
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
		assertThat(response.body()).isEqualTo("Bad credentials");
	}
}
