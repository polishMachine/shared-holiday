package dev.machine.polish.helidon.sharedholiday.provider;

import java.time.LocalDate;

public class HolidayEntry {
    
    private LocalDate date;
    private String localName;
    private String countryCode;
    
    public HolidayEntry() {
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocalName() {
        return localName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
