package com.github.silvanheller.mensabot.config;

import com.typesafe.config.ConfigFactory;

/**
 * @author silvan on 04.10.17.
 */
public class BotConfig {

    public static final String SECRET_TOKEN = ConfigFactory.load( "mensabot" ).getString( "mensa-bot.token" );
    public static final String BOT_USERNAME = ConfigFactory.load( "mensabot" ).getString( "mensa-bot.botname" );

}
