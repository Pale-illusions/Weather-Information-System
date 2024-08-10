package utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class DateUtils {
    public static boolean isWithinOneHour(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        long differenceInMinutes = Math.abs(ChronoUnit.MINUTES.between(dateTime1, dateTime2));
        return differenceInMinutes <= 60;
    }

    public static boolean isWithinTenMinutes(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        long differenceInMinutes = Math.abs(ChronoUnit.MINUTES.between(dateTime1, dateTime2));
        return differenceInMinutes <= 10;
    }

    public static boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.toLocalDate().equals(dateTime2.toLocalDate());
    }
}
