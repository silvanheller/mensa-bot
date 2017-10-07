package com.github.silvanheller.mensabot.util;

import java.util.Calendar;

/**
 * Convenience methods for handling german-localized weekdays
 * @author s3lph
 * @since 2017/10/07
 */
public class GermanWeekdayConv {

    /**
     * @param abbr German two-letter weekday abbreviation
     * @return The corresponding Calendar weekday constant
     * @see java.util.Calendar
     * @throws IllegalArgumentException If the abbreviation is invalid
     */
    public static int abbreviatedWeekdayToCalendar(String abbr) {
        abbr = abbr.trim().toLowerCase().substring(0, 2);

        switch (abbr) {
            case "mo":
                return Calendar.MONDAY;
            case "di":
                return Calendar.TUESDAY;
            case "mi":
                return Calendar.WEDNESDAY;
            case "do":
                return Calendar.THURSDAY;
            case "fr":
                return Calendar.FRIDAY;
            case "sa":
                return Calendar.SATURDAY;
            case "so":
                return Calendar.SUNDAY;
            default:
                throw new IllegalArgumentException("Not a valid day: " + abbr);
        }
    }
}
