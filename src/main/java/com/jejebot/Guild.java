package com.jejebot;

import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by jsondoo on 2017-04-01.
 */
public class Guild {
    private IGuild iGuild;
    private String name;
    private String prefix = "!";
    private boolean unoGame = false;

    public Guild (IGuild guild){
        this.iGuild = guild;
        this.name = iGuild.getName();
    }

    public boolean setPrefix(String prefix){
        if(prefix.length() >= 1){
            this.prefix = prefix;
            return true;
        }
        return false;
    }

    public String getPrefix(){
        return this.prefix;
    }

    public boolean isPlayingUno(){
        return unoGame;
    }

    public void startUno(){
        unoGame = true;
    }

    public void endUno(){
        unoGame = false;
    }

    public IGuild getiGuild(){
        return this.iGuild;
    }

    public String getGuildName(){
        return this.name;
    }
}
