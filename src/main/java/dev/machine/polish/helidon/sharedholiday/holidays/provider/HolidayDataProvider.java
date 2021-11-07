package dev.machine.polish.helidon.sharedholiday.holidays.provider;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;

import dev.machine.polish.helidon.sharedholiday.holidays.HolidayEntry;
import dev.machine.polish.helidon.sharedholiday.holidays.IHolidaysDataProvider;
import io.helidon.common.reactive.Single;

public class HolidayDataProvider implements IHolidaysDataProvider {

    @Override
    public CompletionStage<List<HolidayEntry>> getHolidays(int year, String countryCode) {
        // TODO

        // mock data
        List<HolidayEntry> holidays = List.of(
            new HolidayEntry(LocalDate.now().plusWeeks(countryCode.hashCode()), "rather not shared", "cc"), 
            new HolidayEntry(LocalDate.now().plusWeeks(countryCode.hashCode()), "shared", "cc"));

        return Single.just(holidays);
    }
    
}
