package ru.nchernetsov.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {
    public static LocalDateTime unixTimeToUTC(long unixTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTime), ZoneId.of("UTC"));
    }
}
