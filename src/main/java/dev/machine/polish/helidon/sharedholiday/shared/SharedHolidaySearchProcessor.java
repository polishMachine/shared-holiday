package dev.machine.polish.helidon.sharedholiday.shared;

import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.helidon.common.reactive.Single;

public class SharedHolidaySearchProcessor {
    
    private static final Logger LOGGER = Logger.getLogger(SharedHolidaySearchProcessor.class.getName());

    public CompletionStage<SharedHolidayResponse> searchFor(SharedHolidayRequest reqData) {
        LOGGER.log(Level.FINER, "scheduling request processing");
        return Single.just(new SharedHolidayResponse(reqData.getDate(), "still", "nothing"));
    }
}
