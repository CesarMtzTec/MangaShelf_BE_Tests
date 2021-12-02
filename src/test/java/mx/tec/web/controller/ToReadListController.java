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
public class ToReadListController {
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
    public void givenAToReadAndToken_WhenToReadListRequest_ThenOK() throws Exception {
        // Given
        String token = loginAndGetToken();
        Map<String, String> toRead = new HashMap<String, String>();
        toRead.put("userId", "0");
        toRead.put("mandaId", "1");
        toRead.put("dateAdded", "2021-01-01");

        // When
        HttpRequest request = HttpRequest.newBuilder(new URI(endpoint + "/api/to-read-list"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(BodyPublishers.ofString(JSONValue.toJSONString(toRead)))
                .build();

        // Then
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.body()).isNotNull();
    }
    
    @Test
    public void givenNoToken_WhenToReadListRequest_ThenUnauthorized() throws Exception {
        // Given
        Map<String, String> toRead = new HashMap<String, String>();
        toRead.put("userId", "0");
        toRead.put("mandaId", "1");
        toRead.put("dateAdded", "2021-01-01");

        // When
        HttpRequest request = HttpRequest.newBuilder(new URI(endpoint + "/api/to-read-list"))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(BodyPublishers.ofString(JSONValue.toJSONString(toRead)))
                .build();

        // Then
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
    }
    
}
