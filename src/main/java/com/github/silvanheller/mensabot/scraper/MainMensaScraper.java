package com.github.silvanheller.mensabot.scraper;

import com.github.silvanheller.mensabot.util.GermanWeekdayConv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

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
        return getMenu(0);
    }

    public static Menu getMenu(int dayOffset) throws IOException {
        if ( dayOffset < 0 ) {
            return Menu.PAST;
        }
        if ( dayOffset > 4 ) {
            return Menu.FUTURE;
        }
        //The website uses 1-based indices
        dayOffset++;
        Element body = Jsoup.connect( url ).get().body();
        LOGGER.trace( body );
        String dayOfWeekSelector = String.format( "label[for=mp-tab%d] > span.day", dayOffset );
        String menuDivId = String.format( "menu-plan-tab%d", dayOffset );
        String dayOfWeek = body.select(dayOfWeekSelector).first().html();
        Element todaysMenu = body.getElementById( menuDivId );
        Menu menu = new Menu( dayOfWeek );
        parseBody(menu, todaysMenu);
        return menu;
    }

    public static Menu getMenu(String weekday) throws IOException, IndexOutOfBoundsException {
        int dow = GermanWeekdayConv.abbreviatedWeekdayToCalendar(weekday);
        Element body = Jsoup.connect( url ).get().body();
        LOGGER.trace( body );
        String dayOfWeekSelector = "label > span.day";
        int dayOffset = -1;
        String dayOfWeek = null;
        for (Element dayOfWeekElem : body.select(dayOfWeekSelector)) {
            String inner = dayOfWeekElem.html();
            if (GermanWeekdayConv.abbreviatedWeekdayToCalendar(inner) == dow) {
                String forAttr = dayOfWeekElem.parent().attr("for");
                forAttr = forAttr.substring(forAttr.length() - 1);
                dayOffset = Integer.parseInt(forAttr);
                dayOfWeek = inner;
                break;
            }
        }
        if (dayOffset == -1) {
            return Menu.CLOSED;
        }
        String menuDivId = String.format( "menu-plan-tab%d", dayOffset );
        Element todaysMenu = body.getElementById( menuDivId );
        Menu menu = new Menu( dayOfWeek );
        parseBody(menu, todaysMenu);
        return menu;
    }

    public static void parseBody(Menu menu, Element menuElement) throws IOException {

        for ( Element item : menuElement.getElementsByClass( "menu-item" ) ) {
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

    }


    public static Element getElementByClass( Element element, String className ) {
        return element.getElementsByClass( className ).get( 0 );
    }

}
