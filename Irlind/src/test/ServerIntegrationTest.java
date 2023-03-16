package test;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class ServerIntegrationTest {
    @Test
    void integrationTest() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = getHttpRequest("http://localhost:10001/users%22");
        try {
            getCompletableFuture(client, request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static HttpRequest getHttpRequest(String s) {
        return HttpRequest.newBuilder()
                .uri(URI.create(s))
                .build();
    }

    private static CompletableFuture<HttpResponse<String>> getCompletableFuture(HttpClient client, HttpRequest request2) {
        final CompletableFuture<HttpResponse<String>> call =
                client.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        call
                .thenApply(extractBody())
                .thenAccept(body -> System.out.println(body.substring(0, 100)));
        return call;
    }

    private static Function<HttpResponse<String>, String> extractBody() {
        return (HttpResponse<String> body) -> {
            return body.body();
        };
    }
}
