package com.github.silvanheller.mensabot.scraper;

import com.github.silvanheller.mensabot.util.GermanWeekdayConv;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Internal Representation of the Menu
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

    private final List<Food> items;

    private final int dayOfWeek;


    Menu(String dayOfWeek) {
        this(new ArrayList<>(), dayOfWeek);
    }

    Menu(List<Food> items, String dayOfWeek) {
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
        var builder = new StringBuilder();
        builder.append(getTitle()).append("\n \n");
        for (Food food : getItems()) {
            builder.append(food.displayString());
            builder.append("\n");
        }
        return builder.toString();
    }


    void addFood(Food food) {
        items.add(food);
    }


    /**
     * MD-Formatted String
     */
    public String markdownString() {
        var builder = new StringBuilder();
        builder.append(getTitle()).append("\n \n");
        for (Food food : getItems()) {
            builder.append(food.markdownString());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * See https://core.telegram.org/bots/api#inputmessagecontent for Documentation
     */
    public InputMessageContent getInputMessageMarkdownContent() {
        var msg = new InputTextMessageContent();
        msg.setMessageText(markdownString());
        msg.setParseMode("Markdown");
        return msg;
    }

    private String getTitle() {
        var today = Calendar.getInstance();
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
