package com.jejebot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 * Main class for logging into the client and starting the bot
 */
public class Main {
    private final static String token = Config.TOKEN;

    public static void main(String[] args) {
        IDiscordClient client = createClient();
        if (client == null) return;

        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new MessageHandler());
        dispatcher.registerListener(new ReadyHandler());

    }

    private static IDiscordClient createClient() {
        ClientBuilder cb = new ClientBuilder();
        cb.withToken(token);
        try {
            return cb.login();
        } catch (DiscordException e) {
            e.printStackTrace();
            System.out.println("Problem with login.");
            return null;
        }
    }
}
