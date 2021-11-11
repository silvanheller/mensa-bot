package com.github.silvanheller.mensabot.main;

import com.github.silvanheller.mensabot.core.MensaBot;
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

        try {
            telegramBotsApi.registerBot(new MensaBot());
            LOGGER.info("Successfully registered Bot!");
        } catch (TelegramApiException e) {
            LOGGER.error(e);
        }
    }
}
