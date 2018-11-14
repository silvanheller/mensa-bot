package com.github.silvanheller.mensabot.scraper;

import com.github.silvanheller.mensabot.util.MarkdownUtil;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;

/**
 * Internal Representation of a Menu Item
 *
 * @author silvan on 04.10.17.
 */
public class Food {

    public static final String PRICE_NOT_FOUND = "nicht verfügbar";
    private String title;
    private String description;
    private String studentPrice;


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getStudentPrice() {
        return studentPrice;
    }


    Food( String title, String description, String studentPrice ) {
        this.title = title;
        this.description = description;
        this.studentPrice = studentPrice;
    }


    @Override
    public String toString() {
        return "Food{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", studentPrice='" + studentPrice + '\'' +
                '}';
    }


    /**
     * Beautiful display String. No formatting Applied
     */
    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append( title ).append( "\n" );
        builder.append( description ).append( "\n" );
        if ( !studentPrice.equals( PRICE_NOT_FOUND ) ) {
            builder.append( "Und das ganze zum sagenhaften Preis von " ).append( studentPrice ).append( "\n" );
        }
        return builder.toString();
    }


    /**
     * Short Description and Price for Inline Queries
     */
    public String getDescriptionString() {
        StringBuilder builder = new StringBuilder();
        builder.append( description ).append( "\n" );
        if ( !studentPrice.equals( PRICE_NOT_FOUND ) ) {
            builder.append( "Preis: " ).append( studentPrice ).append( "\n" );
        }
        return builder.toString();
    }

    /**
     * See https://core.telegram.org/bots/api#inputmessagecontent for Documentation
     */
    public InputMessageContent getInputMessageMarkdownContent() {
        return new InputTextMessageContent().setMessageText( markdownString() ).setParseMode( "Markdown" );
    }


    public String markdownString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "*" ).append( title ).append( "*" ).append( "\n" );
        builder.append( description ).append( "\n" );
        if ( !studentPrice.equals( PRICE_NOT_FOUND ) ) {
            builder.append( "Preis: " ).append( studentPrice ).append( "\n" );
        }
        return MarkdownUtil.escapeMarkdown(builder.toString());
    }
}
