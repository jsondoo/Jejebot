package com.jejebot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

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
                case voicejoin:
                    IVoiceChannel voiceChannel = message.getAuthor().getConnectedVoiceChannels().get(0);
                    if (message.getGuild().getVoiceChannels().contains(voiceChannel)) { // ensure that the voice
                        // channel exists in the iGuild the message was sent from
                        voiceChannel.join();
                        channel.sendMessage("Joined " + voiceChannel.getName() + ".");
                    }
                    break;
                case voiceleave:
                    IVoiceChannel connected = message.getGuild().getConnectedVoiceChannel();
                    if (connected != null) {
                        channel.sendMessage("Leaving " + connected.getName() + ".");
                        connected.leave();
                    }
                    break;
                case unlikesuika:
                    message.addReaction("\uD83C\uDDFC");
                    message.addReaction("\uD83C\uDDE6");
                    message.addReaction("\uD83C\uDDFA");
                    break;
                case ding:
                    channel.sendMessage(":flag_cn:");
                    break;
                case noremacc:
                    channel.sendMessage("**You must have Discord Nitro™ to view this message.**");
                    break;
                case queue:
                    if (nextWord == null) {
                        channel.sendMessage("Please type the file name to play.");
                    } else {
                        queueFile(nextWord);
                    }
                    break;
                case changeprefix:
                    if (guild.setPrefix(nextWord)) {
                        channel.sendMessage("Prefix changed to " + nextWord);
                    } else {
                        channel.sendMessage("Prefix change unsuccessful.");
                    }
                    break;
                case poke:
                    channel.sendMessage(nextWord + " " + user.getDisplayName(iGuild) + " poked you with their " +
                            getRandomNoun() + ".");
                    break;
                case love:
                    int loveChance = (int) (Math.random() * 101);
                    channel.sendMessage("Chance of " + user.getDisplayName(iGuild) + " being in a relationship with "
                            + nextWord + " is "
                            + loveChance + "%.");
                    break;
                case chat:
                    guild.toggleChatMode();
                    if (guild.getChatMode())
                        channel.sendMessage("Talk to me.");
                    else
                        channel.sendMessage("I'll go back to being a bot");
                    break;
                case markov:
                case shutup:
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
        String[] nouns = {"stick", "child", "tea", "wife", "church", "pizza", "bird", "map", "pencil",
                "crown", "chopstick", "sword", "highlighter", "pancake", "wallet", "ruler", "nail clipper",
                "coin", "pillow", "cup", "fish", "rock", "song", "shirt", "umbrella", "mosquito", "cow",
                "China", "glue", "mouse", "tissue", "hot sauce", "shuttlecock", "tongue", "toe", "syrup",
                "laptop", "homework", "laundry", "code", "cowboy", "truck", "stripper", "peasant", "hobo",
                "frog", "barber", "rabbit", "scissors", "grandma", "cherry", "chair", "poop", "library",
                "data", "wealth", "branch", "dinner", "beef", "claw", "kimchi", "basketball", "egg"};

        int index = (int) (Math.random() * nouns.length);
        return nouns[index];
    }

    // TODO streaming music locally sucks
    private void queueFile(String fileName) throws RateLimitException, MissingPermissionsException, DiscordException {
        fileName = Config.PATH + fileName;
        File f = new File(fileName);
        if (!f.exists())
            channel.sendMessage("That file doesn't exist!");
        else if (!f.canRead())
            channel.sendMessage("I don't have access to that file!");
        else {
            try {
                AudioPlayer ap = AudioPlayer.getAudioPlayerForGuild(iGuild);
                ap.queue(f);
            } catch (IOException e) {
                channel.sendMessage("An IO exception occured: " + e.getMessage());
            } catch (UnsupportedAudioFileException e) {
                channel.sendMessage("That type of file is not supported!");
            }
        }
    }

    private void sendHelpMessage() throws MissingPermissionsException, RateLimitException, DiscordException {
        EmbedBuilder eb = new EmbedBuilder();
        /*
        eb.withAuthorIcon(message.getAuthor().getAvatarURL());
        eb.withAuthorName("Hello, " + message.getAuthor().getDisplayName(iGuild)+"!");
        */
        eb.withColor(0, 255, 128); // light green color
        eb.withTitle("Command Prefix");
        eb.withDescription(guild.getPrefix());

        // TODO add more commands
        eb.appendField("Commands",
                "help, queue, ping, voicejoin, voiceleave, markov, chat, shutup, " +
                        "uptime, quote, unlikesuika, ding, noremacc", true);


        eb.withFooterIcon(message.getAuthor().getAvatarURL());
        eb.withFooterText("Have fun :)");

        EmbedObject eo = eb.build();
        channel.sendMessage("Here are the commands.", eo, false); // boolean toggles tts
    }

    // TODO add explanations for each command as a embed message

}
