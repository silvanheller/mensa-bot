package com.github.silvanheller.mensabot.config;

import com.typesafe.config.ConfigFactory;

/**
 * Config model, which only includes two things: bot token and username
 */
public class BotConfig {

    public static final String SECRET_TOKEN = ConfigFactory.load("mensabot").getString("mensa-bot.token");
    public static final String BOT_USERNAME = ConfigFactory.load("mensabot").getString("mensa-bot.botname");
    public static final String BING_TOKEN = ConfigFactory.load("mensabot").getString("bing.token");
    public static final String IMAGE_FINDER = ConfigFactory.load("mensabot").getString("mensa-bot.imagefinder");
    public static final Boolean THUMBNAILS_ENABLED = ConfigFactory.load("mensabot").getBoolean("thumbnails.enabled");

}
