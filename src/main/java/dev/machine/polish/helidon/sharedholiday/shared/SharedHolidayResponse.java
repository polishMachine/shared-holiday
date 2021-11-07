package dev.machine.polish.helidon.sharedholiday.shared;

import java.time.LocalDate;

public class SharedHolidayResponse {
    
    public final LocalDate date;
    public final String name1;
    public final String name2;
    
    public SharedHolidayResponse(LocalDate date, String name1, String name2) {
        this.date = date;
        this.name1 = name1;
        this.name2 = name2;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }
}
