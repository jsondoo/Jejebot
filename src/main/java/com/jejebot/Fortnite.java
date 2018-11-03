package com.jejebot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.util.EmbedBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Fortnite {
    private static String sendGETrequest(String username, String platform) {
        String url = "https://api.fortnitetracker.com/v1/profile/" + platform + "/" + username;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("TRN-Api-Key", Config.FN_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    // returns an EmbedObject with fortnite stats for a username, platform
    // returns null if URL is malfunctioned or user does not exist
    public static EmbedObject getFortniteStats(String username, String platform){
        String response = sendGETrequest(username, platform);

        if (response == null) {
            return null;
        }

        try {
            JSONObject obj = new JSONObject(response);
            JSONArray lifetime = obj.getJSONArray("lifeTimeStats");
            String matches_played = lifetime.getJSONObject(7).getString("value");
            String wins = lifetime.getJSONObject(8).getString("value");
            String kills = lifetime.getJSONObject(10).getString("value");
            String kd = lifetime.getJSONObject(11).getString("value");

            EmbedBuilder eb = new EmbedBuilder();
            eb.withColor(137, 207, 240); // baby blue
            eb.withTitle("Fortnite stats for " + username);

            eb.appendField("Lifetime stats",
                    "Matches Played: " + matches_played + "\n" +
                            "Wins: " + wins + "\n" +
                            "Kills: " + kills + "\n" +
                            "KD: " + kd + "\n"
                    ,
                    true);

            eb.withThumbnail("https://i.imgur.com/59hzUmO.gif");
            eb.withFooterText("Powered by Fortnite Tracker API :)");

            EmbedObject eo = eb.build();
            return eo;
        } catch (Exception e) {
            // missing JSON fields
            return null;
        }
    }


}
