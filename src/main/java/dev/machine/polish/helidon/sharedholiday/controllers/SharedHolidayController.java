package dev.machine.polish.helidon.sharedholiday.controllers;

import io.helidon.webserver.Routing.Rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import dev.machine.polish.helidon.sharedholiday.shared.SharedHolidayRequest;
import dev.machine.polish.helidon.sharedholiday.shared.SharedHolidayResponse;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class SharedHolidayController implements Service {

    private static final Logger LOGGER = Logger.getLogger(SharedHolidayController.class.getName());

    @Override
    public void update(Rules rules) {
        rules.get("/", Handler.create(SharedHolidayRequest.class, this::handleGet));
    }

    public void handleGet(ServerRequest req, ServerResponse res, SharedHolidayRequest reqContent) {
        LOGGER.log(Level.FINER, String.format("About to handle a request. Date: %tF, countries: %s & %s", reqContent.getDate(), reqContent.getCountryCode1(), reqContent.getCountryCode2()));
        
        res.send(new SharedHolidayResponse(reqContent.getDate(), "yet", "nothing"));
    }
    
}
