package com.jejebot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 * Stuff to do when the bot is ready
 */
public class ReadyHandler implements IListener<ReadyEvent> {

    public void handle(ReadyEvent event) {
        IDiscordClient client = event.getClient();

        GuildManager gm = GuildManager.getInstance();
        gm.setGuilds(client.getGuilds());
    }
}
