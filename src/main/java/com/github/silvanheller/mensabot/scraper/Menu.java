package com.github.silvanheller.mensabot.scraper;

import java.util.List;

/**
 * Internal Representation of the Menu
 *
 * @author silvan on 04.10.17.
 */
public class Menu {

    private List<Food> items;


    public List<Food> getItems() {
        return items;
    }


    @Override
    public String toString() {
        return "Menu{" +
                "items=" + items +
                '}';
    }


    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Heutiges Menü:" ).append( "\n" );
        for ( Food food : getItems() ) {
            builder.append( food.displayString() );
            builder.append( "\n" );
        }
        return builder.toString();
    }


    public void addFood( Food food ) {
        items.add( food );
    }


    public Menu( List<Food> items ) {

        this.items = items;
    }
}
