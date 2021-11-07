package dev.machine.polish.helidon.sharedholiday.shared;

import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.machine.polish.helidon.sharedholiday.holidays.IHolidaysDataProvider;
import io.helidon.common.reactive.Single;

public class SharedHolidaySearchProcessor {

    private static final Logger LOGGER = Logger.getLogger(SharedHolidaySearchProcessor.class.getName());

    private final int maxComingYearsChecked;
    private final IHolidaysDataProvider holidaysProvider;

    public SharedHolidaySearchProcessor(int maxFollowingYearsChecked, IHolidaysDataProvider holidaysProvider) {
        this.maxComingYearsChecked = maxFollowingYearsChecked;
        this.holidaysProvider = holidaysProvider;
    }

    public CompletionStage<SharedHolidayResponse> searchFor(SharedHolidayRequest reqData) {
        LOGGER.log(Level.FINER, "scheduling request processing");

        return Single.just(new SharedHolidayFinder(reqData, maxComingYearsChecked))
                .thenCompose(this::searchIterationSteps)
                .thenApply(SharedHolidayFinder::createResponse);
    }

    private CompletionStage<SharedHolidayFinder> searchIterationSteps(SharedHolidayFinder finder) {
        return Single.just(finder)
                .thenCombine(
                        holidaysProvider.getHolidays(finder.getCurrentlyChecked().getValue(), finder.getCountryCode1()),
                        SharedHolidayFinder::setHolidaysInCountry01)
                .thenCombine(
                        holidaysProvider.getHolidays(finder.getCurrentlyChecked().getValue(), finder.getCountryCode2()),
                        SharedHolidayFinder::setHolidaysInCountry02)
                .thenApply(SharedHolidayFinder::determineSharedHoliday)
                .thenCompose(this::concludeSearchIteration);
    }

    private CompletionStage<SharedHolidayFinder> concludeSearchIteration(SharedHolidayFinder finder) {
        if (finder.getSharedHoliday() == null && finder.shouldSearchInNextYear()) {
            LOGGER.log(Level.FINER, String.format("composing next processing steps for year: %d",
                    finder.getCurrentlyChecked().getValue()));
            
            finder.prepareForSearchInNextYear();
            return searchIterationSteps(finder);
        }
        // if found then return new Single with itself
        // otherwise return itself without solution
        return Single.just(finder).toStage();
    }
}
