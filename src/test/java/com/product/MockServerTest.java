package com.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerTest {

    private ClientAndServer mockServer;

    @BeforeEach
    public void startServer() {
        mockServer = startClientAndServer(1080); // Start on a port, e.g., 1080
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void exampleTest() {
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/some/path"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("some response"));

        // Here you would perform your test logic that interacts with the mock server
    }
}
