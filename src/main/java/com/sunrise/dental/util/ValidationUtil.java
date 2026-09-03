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
}
