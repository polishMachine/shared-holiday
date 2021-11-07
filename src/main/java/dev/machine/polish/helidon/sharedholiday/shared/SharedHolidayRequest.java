package dev.machine.polish.helidon.sharedholiday.shared;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

public class SharedHolidayRequest {
    
    private LocalDate date;
    private String countryCode1;
    private String countryCode2;
    
    public SharedHolidayRequest() {
    }

    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getDate() {
        return date;
    }

    public String getCountryCode1() {
        return countryCode1;
    }

    public String getCountryCode2() {
        return countryCode2;
    }
}
