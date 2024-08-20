package com.kelp_6.banking_apps.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

public class DateUtil {
    private static final ZoneId zoneId = ZoneId.of("Asia/Jakarta");
    private static final Map<String, DayOfWeek> dayOfWeekMap = new HashMap<>();
    private static final Map<DayOfWeek, String> dayNameMap = new HashMap<>();

    static {
        dayOfWeekMap.put("senin", DayOfWeek.MONDAY);
        dayOfWeekMap.put("selasa", DayOfWeek.TUESDAY);
        dayOfWeekMap.put("rabu", DayOfWeek.WEDNESDAY);
        dayOfWeekMap.put("kamis", DayOfWeek.THURSDAY);
        dayOfWeekMap.put("jum'at", DayOfWeek.FRIDAY);
        dayOfWeekMap.put("jumat", DayOfWeek.FRIDAY);
        dayOfWeekMap.put("sabtu", DayOfWeek.SATURDAY);
        dayOfWeekMap.put("minggu", DayOfWeek.SUNDAY);

        // Mapping from DayOfWeek to Indonesian day name
        dayNameMap.put(DayOfWeek.MONDAY, "Senin");
        dayNameMap.put(DayOfWeek.TUESDAY, "Selasa");
        dayNameMap.put(DayOfWeek.WEDNESDAY, "Rabu");
        dayNameMap.put(DayOfWeek.THURSDAY, "Kamis");
        dayNameMap.put(DayOfWeek.FRIDAY, "Jum'at");
        dayNameMap.put(DayOfWeek.SATURDAY, "Sabtu");
        dayNameMap.put(DayOfWeek.SUNDAY, "Minggu");
    }

    public static Date getStartOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);;
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 1000);;
        return calendar.getTime();
    }

    public static Date getStartDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);;
        return calendar.getTime();
    }

    public static Date getEndDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 1000);;
        return calendar.getTime();
    }

    public static DayOfWeek convertToDayOfWeek(String day){
        DayOfWeek dayOfWeek = dayOfWeekMap.get(day.toLowerCase());

        if(dayOfWeek == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid day name");

        return dayOfWeekMap.get(day.toLowerCase());
    }

    public static String convertToIndonesiaName(DayOfWeek dayOfWeek){
        return dayNameMap.get(dayOfWeek);
    }

    public static Date getNextDay(Date startDate, DayOfWeek chosenDay){
        LocalDate localStartDate = startDate.toInstant()
                .atZone(zoneId)
                .toLocalDate();

        if(localStartDate.getDayOfWeek() == chosenDay){
            return Date.from(localStartDate.atStartOfDay(zoneId).toInstant());
        }

        LocalDate nextChoosenDay = localStartDate;
        while(nextChoosenDay.getDayOfWeek() != chosenDay){
            nextChoosenDay = nextChoosenDay.plusDays(1);
        }

        return Date.from(nextChoosenDay.atStartOfDay(zoneId).toInstant());
    }

    public static Date getSpecificDate(Date startDate, int dayOfMonth){
        LocalDate localStartDate = startDate.toInstant()
                .atZone(zoneId)
                .toLocalDate();

        if(dayOfMonth >= localStartDate.getDayOfMonth() && localStartDate.lengthOfMonth() >= dayOfMonth){
            return Date.from(localStartDate.withDayOfMonth(dayOfMonth).atStartOfDay(zoneId).toInstant());
        }

        LocalDate dateInNextMonth;

        try {
            dateInNextMonth = localStartDate.plusMonths(1).withDayOfMonth(dayOfMonth);
        }catch (DateTimeException exception){
            dateInNextMonth = localStartDate.plusMonths(2).withDayOfMonth(dayOfMonth);
        }

        return Date.from(dateInNextMonth.atStartOfDay(zoneId).toInstant());
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getCurrentDateInJakartaTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        return calendar.getTime();
    }
}
