package util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static String formatEpochSecondsToTime(long epochSeconds, String timeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ZonedDateTime dateTime = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.of("UTC"));
        return dateTime.withZoneSameInstant(ZoneId.of(timeZone)).format(formatter);
    }

    public static String convertUtcStringToTimezone(String utcDateTime, String targetTimeZone, String pattern) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(utcDateTime, inputFormatter);
        ZonedDateTime utcZonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime targetZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(targetTimeZone));

        return targetZonedDateTime.format(outputFormatter);
    }
}
