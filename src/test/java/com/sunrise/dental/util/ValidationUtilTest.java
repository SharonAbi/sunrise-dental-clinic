package com.sunrise.dental.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilTest {

    @Test
    void validName_acceptsLettersAndSpaces() {
        assertTrue(ValidationUtil.isValidName("Kasun Perera"));
    }

    @Test
    void validName_rejectsEmptyString() {
        assertFalse(ValidationUtil.isValidName(""));
    }

    @Test
    void validName_rejectsDigits() {
        assertFalse(ValidationUtil.isValidName("Kasun123"));
    }

    @Test
    void validContactNumber_acceptsTenDigitsStartingWithZero() {
        assertTrue(ValidationUtil.isValidContactNumber("0771234567"));
    }

    @Test
    void validContactNumber_rejectsWrongLength() {
        assertFalse(ValidationUtil.isValidContactNumber("12345"));
    }

    @Test
    void validContactNumber_rejectsNonNumericCharacters() {
        assertFalse(ValidationUtil.isValidContactNumber("077abc4567"));
    }

    @Test
    void validDate_acceptsIsoFormat() {
        assertTrue(ValidationUtil.isValidDate("2026-09-05"));
    }

    @Test
    void validDate_rejectsGarbageInput() {
        assertFalse(ValidationUtil.isValidDate("not-a-date"));
    }

    @Test
    void isFutureOrTodayDate_rejectsPastDate() {
        assertFalse(ValidationUtil.isFutureOrTodayDate(java.time.LocalDate.now().minusDays(1)));
    }

    @Test
    void isFutureOrTodayDate_acceptsToday() {
        assertTrue(ValidationUtil.isFutureOrTodayDate(java.time.LocalDate.now()));
    }

    @Test
    void isWithinClinicHours_acceptsTimeDuringOperatingHours() {
        assertTrue(ValidationUtil.isWithinClinicHours(java.time.LocalTime.of(10, 30)));
    }

    @Test
    void isWithinClinicHours_acceptsOpeningTimeExactly() {
        assertTrue(ValidationUtil.isWithinClinicHours(java.time.LocalTime.of(9, 0)));
    }

    @Test
    void isWithinClinicHours_rejectsBeforeOpening() {
        assertFalse(ValidationUtil.isWithinClinicHours(java.time.LocalTime.of(8, 59)));
    }

    @Test
    void isWithinClinicHours_rejectsAtOrAfterClosing() {
        assertFalse(ValidationUtil.isWithinClinicHours(java.time.LocalTime.of(17, 0)));
    }
}
