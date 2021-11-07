package dev.machine.polish.helidon.sharedholiday.holidays;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayEntry {
    
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    private String localName;
    
    public HolidayEntry() {
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocalName() {
        return localName;
    }
}
