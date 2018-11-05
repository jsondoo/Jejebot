package com.jejebot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles messages and does command parsing
 */
public class MessageHandler {
    private IMessage message;
    private IChannel channel;
    private IGuild iGuild;
    private Guild guild;
    private IUser user;
    private Pandorabot bot = new Pandorabot();

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) throws MissingPermissionsException, RateLimitException,
            DiscordException {
        this.message = event.getMessage();
        this.channel = message.getChannel();
        this.iGuild = message.getGuild();
        this.guild = GuildManager.getInstance().getGuildWithIGuild(iGuild);
        this.user = message.getAuthor();

        if (user.isBot()) return;

        // user entered a command
        if (message.getContent().startsWith(guild.getPrefix())) {
            parseCommand(message.getContent());
        } else if (guild.getChatMode()) { // check if chatmode is turned on
            try {
                channel.sendMessage(bot.getResponse(message.getContent()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message.getContent().trim().toLowerCase().equals("ayy")) { // ayy lmao
            channel.sendMessage("lmao");
        }

    }

    // handles commands
    private void parseCommand(String msg) throws MissingPermissionsException, RateLimitException, DiscordException {
        String[] words = msg.trim().split(" ");
        String commandWord = words[0].replace(guild.getPrefix(), ""); // get command without the command_prefix
        String nextWord = words.length >= 2 ? words[1] : null;

        try {
            Command command = Command.valueOf(commandWord);
            switch (command) {
                case help:
                    sendHelpMessage();
                    break;
                case ping:
                    channel.sendMessage("pong!");
                    break;
                case uptime:
                    RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                    long totalSeconds = rb.getUptime() / 1000;
                    long hours = totalSeconds / 3600;
                    totalSeconds -= hours * 3600;
                    long minutes = totalSeconds / 60; // number of minutes
                    totalSeconds -= minutes * 60;
                    long seconds = totalSeconds;
                    if (hours > 0)
                        channel.sendMessage("Uptime for jejebot: " + hours + "h " + minutes + "m " + seconds + "s");
                    else
                        channel.sendMessage("Uptime for jejebot: " + minutes + "m " + seconds + "s");
                    break;
                case ding:
                    channel.sendMessage(":flag_cn:");
                    break;
                case noremacc:
                    channel.sendMessage("**You must have Discord Nitro™ to view this message.**");
                    break;
                case changeprefix:
                    if (guild.setPrefix(nextWord)) {
                        channel.sendMessage("Prefix changed to " + nextWord);
                    } else {
                        channel.sendMessage("Prefix change unsuccessful.");
                    }
                    break;
                case poke:
                    if (nextWord == null) {
                        channel.sendMessage(user.getDisplayName(iGuild) + " pokes a " +
                                getRandomNoun() + " with their " + getRandomNoun());
                    } else {
                        channel.sendMessage(user.getDisplayName(iGuild) + " pokes " + nextWord+ " with their " +
                                getRandomNoun() + ".");
                    }
                    break;
                case love:
                    int loveChance = (int) (Math.random() * 101);
                    String reaction;
                    if (loveChance > 70) {
                        reaction = ":heart_eyes:";
                    } else if (loveChance < 50) {
                        reaction = ":confounded:";
                    } else {
                        reaction = ":thinking:";
                    }
                    if (words.length <= 2) {
                        channel.sendMessage("Chance of " + user.getDisplayName(iGuild) + " being in a relationship with "
                                + nextWord + " is "
                                + loveChance + "%. " + reaction);
                    } else {
                        channel.sendMessage("Chance of " + nextWord + " being in a relationship with "
                                + words[2] + " is "
                                + loveChance + "%. " + reaction);
                    }
                    break;
                case chat:
                    guild.toggleChatMode();
                    if (guild.getChatMode())
                        channel.sendMessage("Talk to me.");
                    else
                        channel.sendMessage("I'll go back to being a bot");
                    break;
                case fn:
                    EmbedObject fn_stats;
                    if (words.length == 1) {
                        channel.sendMessage(guild.getPrefix() + "fn " + "<username> <platform>");
                        break;
                    } else {
                        String platform = words[words.length - 1];
                        // username is words[1] to words[len(words) - 2] joined
                        List<String> list = new ArrayList<>();
                        for(int i = 1; i <= words.length - 2; ++i) {
                            list.add(words[i]);
                        }
                        String username = String.join(" ", list);
                        fn_stats = Fortnite.getFortniteStats(username, platform);
                    }
                    if (fn_stats == null) {
                        channel.sendMessage("Error getting fortnite data!");
                    } else {
                        channel.sendMessage("", fn_stats, false);
                    }
                    break;
                case markov:
                case quote:
                default:
                    channel.sendMessage("Command is not available.");
                    break;
            }
        } catch (MissingPermissionsException | DiscordException e) {
            e.printStackTrace();
        } catch (RateLimitException re) {
            re.printStackTrace();
            System.out.println("Messages being sent too fast...");
        } catch (IllegalArgumentException e) { // if command doesnt exist
            channel.sendMessage("Command is not available.");
        }

    }

    private String getRandomNoun() {
        String[] nouns = {"broom", "teapot", "iPhone", "halloween candies", "pitchfork", "tongue", "stapler", "pencil",
                "crown", "chopstick", "katana", "highlighter", "nail clipper", "shuttlecock", "toe", "fanny pack",
                "laptop", "homework", "laundry", "code", "cowboy", "truck", "stripper", "peasant", "hobo",
                "banana", "frozen grapes", "scissors", "power of mathematical induction", "stupidity",
                "code", "claw", "kimchi", "icecream sandwich", "egg", "chicken wing"};

        int index = (int) (Math.random() * nouns.length);
        return nouns[index];
    }

    // streaming music locally sucks
//    private void queueFile(String fileName) throws RateLimitException, MissingPermissionsException, DiscordException {
//        fileName = Config.PATH + fileName;
//        File f = new File(fileName);
//        if (!f.exists())
//            channel.sendMessage("That file doesn't exist!");
//        else if (!f.canRead())
//            channel.sendMessage("I don't have access to that file!");
//        else {
//            try {
//                AudioPlayer ap = AudioPlayer.getAudioPlayerForGuild(iGuild);
//                ap.queue(f);
//            } catch (IOException e) {
//                channel.sendMessage("An IO exception occured: " + e.getMessage());
//            } catch (UnsupportedAudioFileException e) {
//                channel.sendMessage("That type of file is not supported!");
//            }
//        }
//    }

    private void sendHelpMessage() throws MissingPermissionsException, RateLimitException, DiscordException {
        EmbedBuilder eb = new EmbedBuilder();
        /*
        eb.withAuthorIcon(message.getAuthor().getAvatarURL());
        eb.withAuthorName("Hello, " + message.getAuthor().getDisplayName(iGuild)+"!");
        */
        eb.withColor(0, 255, 128); // light green color
        eb.withTitle("Command Prefix");
        eb.withDescription(guild.getPrefix());

        // TODO use Command Enum to generate this
        eb.appendField("Commands",
                "ping, markov, fn, chat, love, poke," +
                        "uptime, quote, ding, noremacc", true);


        eb.withFooterIcon(message.getAuthor().getAvatarURL());
        eb.withFooterText("Have fun :)");

        EmbedObject eo = eb.build();
        channel.sendMessage("Here are the commands.", eo, false); // boolean toggles tts
    }
}
