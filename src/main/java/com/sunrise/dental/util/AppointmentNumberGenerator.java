package com.sunrise.dental.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class AppointmentNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private AppointmentNumberGenerator() {
    }

    public static String generate() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "APT-" + datePart + "-" + randomPart;
    }
}
