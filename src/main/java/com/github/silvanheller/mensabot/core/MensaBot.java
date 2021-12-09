package com.github.silvanheller.mensabot.core;

import com.github.silvanheller.mensabot.config.BotConfig;
import com.github.silvanheller.mensabot.scraper.Food;
import com.github.silvanheller.mensabot.scraper.MainMensaScraper;
import com.github.silvanheller.mensabot.scraper.Menu;
import com.github.silvanheller.mensabot.visuals.ImageFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main Bot Implementation.
 */
public class MensaBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ImageFinder finder;

    public MensaBot(ImageFinder finder) {
        this.finder = finder;
        initCache();
    }

    private void initCache() {
        LOGGER.debug("initializing cache");
        try {
            var menu = MainMensaScraper.getMenu();
            menu.getItems().forEach(food -> {
                LOGGER.debug("initializing cache for {}", food.getTitle());
                finder.getThumbURLForFood(food.getTitle());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Takes a /menu command passed to the bot. The following forms are supported:
     * `/menu` - Returns the menu for the current day (or the next day the mensa is open) (default and fallback)
     * `/menu +X` , X <- [0,4] - Returns the menu for the Xth day from today (or the next day the mensa is open)
     * `/menu ab` , ab <- [mo, di, mi, do, fr, sa, so] - Returns the menu for the next instance of the given weekday
     *
     * @param request The command sent to the MensaBot
     * @return The menu scraped from the website
     * @throws IOException If some web stuff goes wrong
     */
    private Menu getMenu(String request) throws IOException {
        Menu menu;
        if (request.contains("+")) {
            String[] tokens = request.split("\\+");
            if (tokens.length >= 2) {
                int dayOffset = Integer.parseInt(tokens[1].trim());
                menu = MainMensaScraper.getMenu(dayOffset);
            } else {
                menu = MainMensaScraper.getMenu();
            }
        } else if (request.contains(" ")) {
            String[] tokens = request.split(" ");
            if (tokens.length >= 2) {
                menu = MainMensaScraper.getMenu(tokens[1]);
            } else {
                menu = MainMensaScraper.getMenu();
            }
        } else {
            menu = MainMensaScraper.getMenu();
        }
        return menu;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            String text = update.getInlineQuery().getQuery();
            if (text.contains("/menu") || text.isEmpty()) {
                try {
                    LOGGER.debug("Answering Inline Query");
                    var menu = getMenu(text);
                    List<InlineQueryResult> results = new ArrayList<>();
                    for (Food food : menu.getItems()) {
                        var item = new InlineQueryResultArticle();
                        item.setTitle(food.getTitle());
                        item.setDescription(food.getDescriptionString());
                        item.setInputMessageContent(food.getInputMessageMarkdownContent());
                        item.setId(UUID.randomUUID().toString());
                        if (BotConfig.THUMBNAILS_ENABLED) {
                            var url = finder.getThumbURLForFood(food.getTitle());
                            if (url != null) {
                                item.setThumbUrl(url);
                            }
                        }
                        results.add(item);
                    }
                    // all
                    var item = new InlineQueryResultArticle();
                    item.setTitle("Komplettes Menü");
                    item.setDescription("Komplettes Menü des heutigen Tages");
                    item.setInputMessageContent(menu.getInputMessageMarkdownContent());
                    item.setId(UUID.randomUUID().toString());
                    results.add(item);

                    var answer = new AnswerInlineQuery();
                    answer.setResults(results);
                    answer.setInlineQueryId(update.getInlineQuery().getId());
                    answer.setCacheTime(5);
                    execute(answer);
                } catch (IOException | TelegramApiException e) {
                    LOGGER.error(e);
                }
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message_text = update.getMessage().getText();
            if (message_text.contains("/menu")) {
                LOGGER.trace("Scraping menu");
                try {
                    var menu = getMenu(message_text);
                    LOGGER.trace("Generated menu: {} with md-string {}", menu, menu.markdownString());
                    var message = generateMarkdownMessage(menu.markdownString(), update);
                    execute(message); // Call method to send the message
                } catch (TelegramApiException | IOException e) {
                    LOGGER.error(e);
                }
            }

        }
    }


    private SendMessage generateMarkdownMessage(String text, Update update) {
        var msg = new SendMessage(); // Create a SendMessage object with mandatory fields
        msg.setChatId(String.valueOf(update.getMessage().getChatId()));
        msg.setParseMode("Markdown");
        msg.setText(text);
        return msg;
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
