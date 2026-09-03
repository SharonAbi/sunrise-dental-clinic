package com.sunrise.dental.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.trim().matches("[A-Za-z. ]{2,100}");
    }

    public static boolean isValidContactNumber(String contact) {
        return isNotEmpty(contact) && contact.trim().matches("0\\d{9}");
    }

    public static boolean isValidDate(String date) {
        if (!isNotEmpty(date)) {
            return false;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        if (!isNotEmpty(time)) {
            return false;
        }
        try {
            LocalTime.parse(time);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isFutureOrTodayDate(LocalDate date) {
        return !date.isBefore(LocalDate.now());
    }

    private static final LocalTime CLINIC_OPENS = LocalTime.of(9, 0);
    private static final LocalTime CLINIC_CLOSES = LocalTime.of(17, 0);

    /** Clinic operates 09:00 (inclusive) to 17:00 (exclusive), Mon-Sat. */
    public static boolean isWithinClinicHours(LocalTime time) {
        return !time.isBefore(CLINIC_OPENS) && time.isBefore(CLINIC_CLOSES);
    }
}
