package util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static String formatEpochSecondsToTime(long epochSeconds, String timeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ZonedDateTime dateTime = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.of("UTC"));
        return dateTime.withZoneSameInstant(ZoneId.of(timeZone)).format(formatter);
    }
}
