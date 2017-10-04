package com.github.silvanheller.mensabot.core;

import com.github.silvanheller.mensabot.config.BotConfig;
import com.github.silvanheller.mensabot.scraper.MainMensaScraper;
import com.github.silvanheller.mensabot.scraper.Menu;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author silvan on 04.10.17.
 */
public class MensaBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger();


    @Override
    public void onUpdateReceived( Update update ) {
        if ( update.hasMessage() && update.getMessage().hasText() ) {
            String message_text = update.getMessage().getText();
            if ( message_text.equals( "/menu" ) ) {
                LOGGER.debug( "Scraping menu" );
                SendMessage message = generateMessage( "Error occured", update );
                try {
                    Menu menu = MainMensaScraper.getMenu();
                    message = generateMessage( menu.displayString(), update );
                } catch ( IOException e ) {
                    LOGGER.error( e );
                }
                try {
                    execute( message ); // Call method to send the message
                } catch ( TelegramApiException e ) {
                    LOGGER.error( e );
                }
            }

        }
    }


    private SendMessage generateMessage( String text, Update update ) {
        return new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId( update.getMessage().getChatId() )
                .setText( text );
    }


    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }


    @Override
    public String getBotToken() {
        return BotConfig.SECRET_TOKEN;
    }
}
