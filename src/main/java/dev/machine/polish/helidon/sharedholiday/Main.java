
package dev.machine.polish.helidon.sharedholiday;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.apache.commons.lang3.Validate;

import dev.machine.polish.helidon.sharedholiday.controllers.ErrorResponse;
import dev.machine.polish.helidon.sharedholiday.controllers.SharedHolidayController;
import dev.machine.polish.helidon.sharedholiday.holidays.provider.HolidayDataProvider;
import dev.machine.polish.helidon.sharedholiday.shared.SharedHolidaySearchProcessor;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jackson.JacksonRuntimeException;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

public final class Main {

    private Main() {
    }

    public static void main(final String[] args) throws IOException {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static WebServer startServer() throws IOException {
        setupLogging();

        // By default this will pick up application.yaml from the classpath
        Config config = Config.create();

        // Build WebClient with Jackson support
        WebClient holidaysWebClient = WebClient.builder()
                .baseUri(config.get("holidayDataService.baseURI").asString()
                            .orElseThrow(() -> new IllegalStateException("holidays provider base URI is not configured")))
                .addReader(JacksonSupport.reader())
                .build();


        WebServer server = WebServer.builder(createRouting(config, holidaysWebClient))
                .config(config.get("server"))
                .addMediaSupport(JacksonSupport.create())
                .build();

        server.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://localhost:" + ws.port() + "/sharedholiday/v1");
                    ws.whenShutdown().thenRun(()
                            -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });

        // Server threads are not daemon. No need to block. Just react.
        return server;
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    private static Routing createRouting(Config config, WebClient holidaysWebClient) {
        Validate.notNull(holidaysWebClient);

        MetricsSupport metrics = MetricsSupport.create();
        
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        String apiPathFormat = config.get("holidayDataService.pathFormat").asString().orElseThrow(() -> new IllegalStateException("holidays provider API pathFormat is not configured"));
        HolidayDataProvider holidayDataProvider = new HolidayDataProvider(holidaysWebClient, apiPathFormat);
        
        SharedHolidaySearchProcessor searchProcessor = new SharedHolidaySearchProcessor(
                config.get("maxFollowingYearsChecked").asInt().orElse(2), holidayDataProvider);

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/sharedholiday/v1", new SharedHolidayController(searchProcessor))
                .error(JacksonRuntimeException.class, (req, res, ex) -> {
                    if (ex.getCause() instanceof UnrecognizedPropertyException) {
                        UnrecognizedPropertyException upe = (UnrecognizedPropertyException)ex.getCause();
                        
                        Logger.getLogger(Main.class.getName()).log(Level.FINE,
                        String.format("request content deserialization error, caused by unrecognized property: %s", upe.getPropertyName()));
                        
                        res.status(Http.Status.BAD_REQUEST_400);
                        res.send(new ErrorResponse(
                                String.format("Unable to parse request. Unrecognized property: %s. Expected properties: %s", 
                                        upe.getPropertyName(), upe.getKnownPropertyIds())));
                    } else {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                            "processing request failed",
                            ex.getCause());
                        
                        res.status(Http.Status.INTERNAL_SERVER_ERROR_500);
                        res.send(new ErrorResponse("Unable to handle request."));
                    }
                })
                .build();
    }

    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
