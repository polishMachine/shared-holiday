package dev.machine.polish.helidon.sharedholiday.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import dev.machine.polish.helidon.sharedholiday.shared.SharedHolidayRequest;
import dev.machine.polish.helidon.sharedholiday.shared.SharedHolidaySearchProcessor;
import io.helidon.common.http.Http;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.webclient.WebClientException;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class SharedHolidayController implements Service {

    private static final Logger LOGGER = Logger.getLogger(SharedHolidayController.class.getName());

    private final SharedHolidaySearchProcessor searchProcessor;

    public SharedHolidayController(SharedHolidaySearchProcessor sharedHolidaySearchProcessor) {
        Validate.notNull(sharedHolidaySearchProcessor);
        searchProcessor = sharedHolidaySearchProcessor;
    }

    @Override
    public void update(Rules rules) {
        rules.get("/", WebSecurity.rolesAllowed("user"), Handler.create(SharedHolidayRequest.class, this::handleGet));
    }

    public void handleGet(ServerRequest req, ServerResponse res, SharedHolidayRequest reqContent) {
        LOGGER.log(Level.FINER, String.format("About to handle a request. Date: %tF, countries: %s & %s", reqContent.getDate(), reqContent.getCountryCode1(), reqContent.getCountryCode2()));
        
        searchProcessor.searchFor(reqContent).thenAccept(r -> {
            if (r == null) {
                res.status(Http.Status.NOT_FOUND_404);
                res.send(new ErrorResponse("shared holiday not found"));
            } else {
                res.send(r);
            }
        }).exceptionallyAccept(e -> {
            if (e.getCause() instanceof WebClientException) {
                res.status(Http.Status.SERVICE_UNAVAILABLE_503);
                res.send(new ErrorResponse("cannot load holidays data from a provider"));
            } else {
                LOGGER.log(Level.SEVERE, "request processing failure, caused by: " + e.getCause().getClass().getSimpleName());
                res.status(Http.Status.INTERNAL_SERVER_ERROR_500);
                res.send(new ErrorResponse("error on handling request for shared holiday search: " + e.getMessage()));
            }
        });
    }
}
