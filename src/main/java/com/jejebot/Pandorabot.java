package com.jejebot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class Pandorabot {
    ChatterBotSession session;

    public Pandorabot(){
        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            ChatterBot bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            session = bot.createSession();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getResponse (String msg) throws Exception{
        if(session == null){
            return "Something went wrong";
        }
        return session.think(msg);
    }
}
