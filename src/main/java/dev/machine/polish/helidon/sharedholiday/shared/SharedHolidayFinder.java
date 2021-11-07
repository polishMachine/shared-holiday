package dev.machine.polish.helidon.sharedholiday.shared;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import dev.machine.polish.helidon.sharedholiday.holidays.HolidayEntry;

public class SharedHolidayFinder {

    private static final Logger LOGGER = Logger.getLogger(SharedHolidayFinder.class.getName());
    
    private List<HolidayEntry> holidaysIn1 = null;
    private List<HolidayEntry> holidaysIn2 = null;
    
    private LocalDate dateLowLimit = null;
    private final String countryCode1;
    private final String countryCode2;

    private Year currentlyChecked = null;
    private final Year lastChecked;
    
    private Pair<HolidayEntry, HolidayEntry> sharedHoliday = null;

    SharedHolidayFinder(SharedHolidayRequest forRequest, int maxComingYearsChecked) {
        dateLowLimit = forRequest.getDate();
        currentlyChecked = Year.from(forRequest.getDate());
        lastChecked = Year.of(forRequest.getDate().getYear() + maxComingYearsChecked);
        countryCode1 = forRequest.getCountryCode1();
        countryCode2 = forRequest.getCountryCode2();
    }

    String getCountryCode1() {
        return countryCode1;
    }

    String getCountryCode2() {
        return countryCode2;
    }

    Year getCurrentlyChecked() {
        return currentlyChecked;
    }

    Pair<HolidayEntry, HolidayEntry> getSharedHoliday() {
        return sharedHoliday;
    }

    boolean shouldSearchInNextYear() {
        return currentlyChecked.isBefore(lastChecked);
    }

    void prepareForSearchInNextYear() {
        dateLowLimit = null;
        currentlyChecked = currentlyChecked.plusYears(1);
        holidaysIn1 = null;
        holidaysIn2 = null;
    }

    SharedHolidayResponse createResponse() {
        if (sharedHoliday == null) {
            return null;
        }
        
        SharedHolidayResponse response = new SharedHolidayResponse(sharedHoliday.getLeft().getDate(),
                sharedHoliday.getLeft().getLocalName(), sharedHoliday.getRight().getLocalName());
        return response;
    }

    SharedHolidayFinder setHolidaysInCountry01(List<HolidayEntry> holidays) {
        holidaysIn1 = holidays;
        LOGGER.log(Level.FINER, "adding items for CC1: " + holidays.size());
        return this;
    }

    SharedHolidayFinder setHolidaysInCountry02(List<HolidayEntry> holidays) {
        holidaysIn2 = holidays;
        LOGGER.log(Level.FINER, "adding items for CC2: " + holidays.size());
        return this;
    }

    SharedHolidayFinder determineSharedHoliday() {
        // TODO
        return this;
    }
}
