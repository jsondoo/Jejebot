package com.jejebot;

import sx.blah.discord.handle.obj.IGuild;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jsondoo on 2017-02-04.
 * Manages properties of guilds (e.g. quote text files, chatmode)
 * A singleton class
 */

public class GuildManager {
    private static final GuildManager INSTANCE = new GuildManager();
    private Set<Guild> guilds = new HashSet<>();

    private GuildManager() {
    }

    public static GuildManager getInstance() {
        return INSTANCE;
    }

    // called by ReadyHandler
    public void setGuilds(List<IGuild> guilds) {
        for(IGuild ig : guilds){
            Guild g = new Guild(ig);
            this.guilds.add(g);
        }
    }

    public Guild getGuildWithIGuild(IGuild iGuild){
        for(Guild g : guilds) {
            if (g.getiGuild().equals(iGuild))
                return g;
        }
        return null;
    }


}
