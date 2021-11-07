package dev.machine.polish.helidon.sharedholiday.provider;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface IHolidaysDataProvider {
    
    CompletionStage<List<HolidayEntry>> getHolidays(int year, String countryCode);
    
}
