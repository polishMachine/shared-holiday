
package dev.machine.polish.helidon.sharedholiday;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.WebServer;

public class MainTest {

    private static WebServer webServer;
    private static WebClient webClient;

    @BeforeAll
    public static void startTheServer() throws Exception {
        webServer = Main.startServer();

        long timeout = 2000; // 2 seconds should be enough to start the server
        long now = System.currentTimeMillis();

        while (!webServer.isRunning()) {
            Thread.sleep(100);
            if ((System.currentTimeMillis() - now) > timeout) {
                Assertions.fail("Failed to start webserver");
            }
        }

        webClient = WebClient.builder().baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(JsonpSupport.create()).build();
    }

    @AfterAll
    public static void stopServer() throws Exception {
        if (webServer != null) {
            webServer.shutdown().toCompletableFuture().get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testHelloWorld() throws Exception {
        webClient.get().path("/health").request()
                .thenAccept(response -> Assertions.assertEquals(200, response.status().code())).toCompletableFuture()
                .get();

        webClient.get().path("/metrics").request()
                .thenAccept(response -> Assertions.assertEquals(200, response.status().code())).toCompletableFuture()
                .get();
    }
}
