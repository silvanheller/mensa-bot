package com.github.silvanheller.mensabot.main;

import com.github.silvanheller.mensabot.config.BotConfig;
import com.github.silvanheller.mensabot.core.MensaBot;
import com.github.silvanheller.mensabot.visuals.BingImageFinder;
import com.github.silvanheller.mensabot.visuals.ImageFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Main entry point from the jar.
 *
 * @author Silvan Heller (silvan.heller@unibas.ch) on 03.08.17.
 */
public class Main {

    private static final Logger LOGGER = LogManager.getLogger();


    public static void main(String[] args) throws TelegramApiException {
        var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        ImageFinder finder = null;
        switch (BotConfig.IMAGE_FINDER) {
            case "bing":
                finder = new BingImageFinder();
                break;
            case "google":
                throw new UnsupportedOperationException();
        }
        try {
            telegramBotsApi.registerBot(new MensaBot(finder));
            LOGGER.info("Successfully registered Bot!");
        } catch (TelegramApiException e) {
            LOGGER.error(e);
        }
    }
}
