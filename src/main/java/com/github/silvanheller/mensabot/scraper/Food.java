package com.github.silvanheller.mensabot.scraper;

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


    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append( title ).append( "\n" );
        builder.append( description ).append( "\n" );
        if ( !studentPrice.equals( PRICE_NOT_FOUND ) ) {
            builder.append( "Und das ganze zum sagenhaften Preis von " ).append( studentPrice ).append( "\n" );
        }
        return builder.toString();
    }
}
