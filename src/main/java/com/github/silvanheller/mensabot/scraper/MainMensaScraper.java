package com.github.silvanheller.mensabot.scraper;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * @author silvan on 04.10.17.
 */
public class MainMensaScraper {

    private static final String url = "http://universitaet-basel.sv-restaurant.ch/de/menuplan/";
    private static final Logger LOGGER = LogManager.getLogger();


    public static void main( String[] args ) throws IOException {
        Menu menu = getMenu();
        System.out.println( menu.displayString() );
    }


    public static Menu getMenu() throws IOException {
        Element body = Jsoup.connect( url ).get().body();
        LOGGER.trace( body );
        Menu menu = new Menu( new ArrayList<>() );
        Element today = body.getElementById( "menu-plan-tab1" );
        for ( Element item : today.getElementsByClass( "menu-item" ) ) {
            String title = getElementByClass( item, "menu-title" ).ownText();
            LOGGER.trace( "Title extracted {} from {}", title, getElementByClass( item, "menu-title" ).toString() );
            String description = getElementByClass( item, "menu-description" ).ownText();
            LOGGER.trace( "Description extracted {} from {}", description, getElementByClass( item, "menu-description" ).toString() );
            String price = Food.PRICE_NOT_FOUND;
            for ( Element priceDiv : item.getElementsByClass( "price" ) ) {
                if ( getElementByClass( priceDiv, "desc" ).text().equals( "STUD" ) ) {
                    price = getElementByClass( priceDiv, "val" ).text();
                } else {
                    LOGGER.trace( getElementByClass( priceDiv, "desc" ).text() );
                }
            }
            if ( price.equals( Food.PRICE_NOT_FOUND ) && item.getElementsByClass( "val" ).size() > 0 ) {
                price = getElementByClass( item, "val" ).text();
            }
            Food food = new Food( title, description, price );
            menu.addFood( food );
        }
        return menu;

    }


    public static Element getElementByClass( Element element, String className ) {
        return element.getElementsByClass( className ).get( 0 );
    }

}
