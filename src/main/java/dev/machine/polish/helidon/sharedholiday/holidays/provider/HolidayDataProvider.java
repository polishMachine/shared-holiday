package dev.machine.polish.helidon.sharedholiday.holidays.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;

import dev.machine.polish.helidon.sharedholiday.holidays.HolidayEntry;
import dev.machine.polish.helidon.sharedholiday.holidays.IHolidaysDataProvider;
import io.helidon.common.GenericType;
import io.helidon.common.reactive.Single;
import io.helidon.webclient.WebClient;

public class HolidayDataProvider implements IHolidaysDataProvider {

    private static final Logger LOGGER = Logger.getLogger(HolidayDataProvider.class.getName());

    private final WebClient holidaysWebClient;
    private final String pathFormat;

    private static Map<Integer, Map<String, List<HolidayEntry>>> holidaysByCountryAndYear = new HashMap<>();

    public HolidayDataProvider(WebClient holidaysWebClient, String pathFormat) {
        this.holidaysWebClient = holidaysWebClient;
        this.pathFormat = pathFormat;
    }

    @Override
    public CompletionStage<List<HolidayEntry>> getHolidays(int year, String countryCode) {
        Map<String, List<HolidayEntry>> byCountry = holidaysByCountryAndYear.get(year);
        if (byCountry != null && byCountry.size() > 0) {
            List<HolidayEntry> holidays = byCountry.get(countryCode);
            if (CollectionUtils.isNotEmpty(holidays)) {
                return Single.just(holidays).toStage();
            }
        }

        String path = String.format(pathFormat, year, countryCode);
        LOGGER.log(Level.FINER,"registering call to publicholidays API with path: " + path);
        return holidaysWebClient.get()
                .path(path)
                .request(new GenericType<List<HolidayEntry>>() {})
                .thenApply(h -> HolidayDataProvider.registerInCache(h, year, countryCode));
    }

    private static List<HolidayEntry> registerInCache(List<HolidayEntry> holidays, int year, String countryCode) {
        holidaysByCountryAndYear.putIfAbsent(year, new HashMap<>());
        Map<String, List<HolidayEntry>> byCountry = holidaysByCountryAndYear.get(year);
        byCountry.putIfAbsent(countryCode, holidays);
        return holidays;
    }
}
