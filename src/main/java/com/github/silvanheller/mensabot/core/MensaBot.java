package com.github.silvanheller.mensabot.core;

import com.github.silvanheller.mensabot.config.BotConfig;
import com.github.silvanheller.mensabot.scraper.Food;
import com.github.silvanheller.mensabot.scraper.MainMensaScraper;
import com.github.silvanheller.mensabot.scraper.Menu;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author silvan on 04.10.17.
 */
public class MensaBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger();


    @Override
    public void onUpdateReceived( Update update ) {
        if ( update.hasInlineQuery() ) {
            String text = update.getInlineQuery().getQuery();
            if ( text.contains( "/menu" ) || text.isEmpty() ) {
                try {
                    LOGGER.debug( "Answering Inline Query" );
                    Menu menu = MainMensaScraper.getMenu();
                    List<InlineQueryResult> results = new ArrayList<>();
                    for ( Food food : menu.getItems() ) {
                        InlineQueryResultArticle item = new InlineQueryResultArticle().setTitle( food.getTitle() ).setDescription( food.getDescriptionString() ).setInputMessageContent( food.getInputMessageMarkdownContent() ).setId( UUID.randomUUID().toString() );
                        results.add( item );
                    }

                    AnswerInlineQuery answer = new AnswerInlineQuery().setResults( results ).setInlineQueryId( update.getInlineQuery().getId() ).setCacheTime( 5 );
                    execute( answer );
                } catch ( IOException | TelegramApiException e ) {
                    LOGGER.error( e );
                }
            }
        }
        if ( update.hasMessage() && update.getMessage().hasText() ) {
            String message_text = update.getMessage().getText();
            if ( message_text.contains( "/menu" ) ) {
                LOGGER.debug( "Scraping menu" );
                try {
                    Menu menu = MainMensaScraper.getMenu();
                    SendMessage message = generateMarkdownMessage( menu.markdownString(), update );
                    execute( message ); // Call method to send the message
                } catch ( TelegramApiException | IOException e ) {
                    LOGGER.error( e );
                }
            }

        }
    }


    private SendMessage generateMarkdownMessage( String text, Update update ) {
        return new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId( update.getMessage().getChatId() ).setParseMode( "Markdown" )
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
