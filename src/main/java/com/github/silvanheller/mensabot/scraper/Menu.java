package com.github.silvanheller.mensabot.scraper;

import com.github.silvanheller.mensabot.util.GermanWeekdayConv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Internal Representation of the Menu
 *
 * @author silvan on 04.10.17.
 */
public class Menu {

    public static final Menu CLOSED = new Menu("sa");

    public static final Menu PAST = new Menu("sa") {
        @Override
        public String displayString() {
            return "Ich kann leider nicht in die Vergangenheit schauen.";
        }

        @Override
        public String markdownString() {
            return "Ich kann leider nicht in die Vergangenheit schauen.";
        }
    };

    public static final Menu FUTURE = new Menu("sa") {
        @Override
        public String displayString() {
            return "Ich kann leider nicht so weit in die Zukunft schauen.";
        }

        @Override
        public String markdownString() {
            return "Ich kann leider nicht so weit in die Zukunft schauen.";
        }

    };

    private List<Food> items;

    private int dayOfWeek;



    Menu( String dayOfWeek ) {
        this( new ArrayList<>(), dayOfWeek );
    }

    Menu( List<Food> items, String dayOfWeek ) {
        this.items = items;
        this.dayOfWeek = GermanWeekdayConv.abbreviatedWeekdayToCalendar(dayOfWeek);
    }


    public List<Food> getItems() {
        return items;
    }


    @Override
    public String toString() {
        return "Menu{" +
                "weekday=" + this.dayOfWeek +
                "items=" + this.items +
                '}';
    }


    /**
     * Simple, nonformatted display String
     */
    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append( getTitle() ).append( "\n \n" );
        for ( Food food : getItems() ) {
            builder.append( food.displayString() );
            builder.append( "\n" );
        }
        return builder.toString();
    }


    void addFood( Food food ) {
        items.add( food );
    }


    /**
     * MD-Formatted String
     */
    public String markdownString() {
        StringBuilder builder = new StringBuilder();
        builder.append( getTitle() ).append( "\n \n" );
        for ( Food food : getItems() ) {
            builder.append( food.markdownString() );
            builder.append( "\n" );
        }
        return builder.toString();
    }

    private String getTitle() {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.DAY_OF_WEEK) == this.dayOfWeek &&
                this.dayOfWeek != Calendar.SATURDAY &&
                this.dayOfWeek != Calendar.SUNDAY) {
            return "Heutiges Menü:";
        }

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Am Montag gibt es:";
            case Calendar.TUESDAY:
                return "Am Dienstag gibt es:";
            case Calendar.WEDNESDAY:
                return "Am Mittwoch gibt es:";
            case Calendar.THURSDAY:
                return "Am Donnerstag gibt es:";
            case Calendar.FRIDAY:
                return "Am Freitag gibt es:";
            default:
                return "Am Wochenende ist die Mensa zu, geh zur Spiga.";
        }
    }

}
