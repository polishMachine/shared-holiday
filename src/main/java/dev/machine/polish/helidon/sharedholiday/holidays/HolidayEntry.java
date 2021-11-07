package dev.machine.polish.helidon.sharedholiday.holidays;

import java.time.LocalDate;

public class HolidayEntry {
    
    private LocalDate date;
    private String localName;
    private String countryCode;
    
    public HolidayEntry() {
    }

    public HolidayEntry(LocalDate date, String localName, String countryCode) {
        this.date = date;
        this.localName = localName;
        this.countryCode = countryCode;
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
