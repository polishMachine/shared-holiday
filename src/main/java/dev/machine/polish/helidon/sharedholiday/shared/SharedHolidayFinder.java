package dev.machine.polish.helidon.sharedholiday.shared;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import dev.machine.polish.helidon.sharedholiday.holidays.HolidayEntry;

class SharedHolidayFinder {

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
        if (CollectionUtils.isNotEmpty(holidaysIn1) && CollectionUtils.isNotEmpty(holidaysIn2)) {
            Collection<LocalDate> intersection = CollectionUtils.intersection(
                    getHolidaysDates(holidaysIn1),
                    getHolidaysDates(holidaysIn2));
            if (CollectionUtils.isNotEmpty(intersection)) {
                LocalDate minIntersection = Collections.min(intersection);
                sharedHoliday = Pair.of(
                        getByDate(holidaysIn1, minIntersection).get(),
                        getByDate(holidaysIn2, minIntersection).get());
            }
        }
        return this;
    }

    private List<LocalDate> getHolidaysDates(List<HolidayEntry> holidays) {
        return holidays == null ? List.of()
                : holidays.stream().map(h -> h.getDate())
                        .filter(h -> dateLowLimit == null ? true : h.isAfter(dateLowLimit))
                        .collect(Collectors.toList());
    }

    private Optional<HolidayEntry> getByDate(List<HolidayEntry> holidays, LocalDate date) {
        return holidays.stream().filter(h -> h.getDate().isEqual(date)).findFirst();
    }
}
