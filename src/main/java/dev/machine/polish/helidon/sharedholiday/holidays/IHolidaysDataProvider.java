package dev.machine.polish.helidon.sharedholiday.holidays;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface IHolidaysDataProvider {
    
    CompletionStage<List<HolidayEntry>> getHolidays(int year, String countryCode);

}
