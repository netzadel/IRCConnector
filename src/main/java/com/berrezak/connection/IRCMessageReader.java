package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ElBerro on 25.01.2016.
 */
public class IRCMessageReader implements Runnable {

    private BufferedReader reader;
    private IRCMessageSender sender;
    private IRCProfile profile;
    private static Logger logger = LoggerFactory.getLogger(IRCMessageReader.class);

    public IRCMessageReader(BufferedReader incReader, IRCMessageSender incSender, IRCProfile incProfile) {
        reader = incReader;
        sender = incSender;
        profile = incProfile;
        logger.info("Initialized IRCMessageReader.");
    }

    public void run() {
        logger.info("Starting message reader thread.");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                //logger.info("Received message from server: {}", line);
                this.interpretLine(line);
                Thread.sleep(10);
            }
        } catch (Exception e) {
            logger.error("Failed to read stream from: {} reason: {}", profile.getServer(), e.getMessage());
            logger.error("Failed to read message.", e);
        }
    }

    private void interpretLine(String line) {
        if (line.startsWith("PING")) {
            logger.info("Received PING request from server.");
            sender.sendPing(line.substring(5));
        } else if (line.startsWith(":")) {
            int codeStart = profile.getServer().length() + 2;
            int codeEnd = profile.getServer().length() + 5;

            String responseCode = line.substring(codeStart, codeEnd);

            if (StringUtils.isNumeric(responseCode)) {
                this.interpretStatusNumber(responseCode, line);
            } else
                this.interpretStatusText(line);
        }
    }

    private void interpretStatusText(String line) {
        if (line.contains("PRIVMSG")) {
            IRCChannel channel = this.getAppropriateChannel(line);
            if (channel != null) {
                channel.receivedMessage(line);
            }
        }
        //Add user he joins the channel and is not already in the user list
        else if (line.contains("JOIN")) {
            String[] parted = line.split(":");
            String userTMP = parted[1].split("!")[0];

            if (!userTMP.equals(profile.getUsername())) {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null && !channel.getUserInChannel().contains(userTMP)) {
                    channel.getUserInChannel().add(userTMP);
                    logger.info("Added user {} to user list in channel: {}", userTMP, channel.getChannelName());
                }
            }
        }
        //Remove user from user list if he is part of the list
        else if (line.contains("QUIT")) {
            String[] parted = line.split(":");
            String userTMP = parted[1].split("!")[0];

            if (!userTMP.equals(profile.getUsername())) {
                profile.getChannels().stream().sorted().forEach(channel -> channel.getUserInChannel().stream().sorted().forEach(user -> {
                    if (user.equals(userTMP)) {
                        channel.getUserInChannel().remove(userTMP);
                        logger.info("Removed user {} from user list in channel: {}", userTMP, channel.getChannelName());
                    }
                }));
            }
        }
    }

    private void interpretStatusNumber(String responseCode, String line) {
        int numberStarts;
        switch (responseCode) {
            //Try to get the amount of visible and invisible user online on server
            case "251": {
                numberStarts = profile.getServer().length() + profile.getUsername().length() + 18;
                try {
                    List<String> serverUserData = this.mapServerData(line, numberStarts);
                    if (serverUserData != null) {
                        profile.getServerInfo().setVisibleUser(Integer.parseInt(serverUserData.get(0)));
                        profile.getServerInfo().setInvisibleUser(Integer.parseInt(serverUserData.get(1)));
                        logger.info("Updated server user data. Added visible and invisible user.");
                    }

                } catch (Exception e) {
                    logger.error("Failed to generate list of visible and invisible server user. Reason: {}", e.getMessage());
                    logger.error("Failed to update server info.", e);
                }
                break;
            }

            //Try to get operators
            case "252": {
                numberStarts = profile.getServer().length() + profile.getUsername().length() + 7;
                try {
                    List<String> serverUserData = this.mapServerData(line, numberStarts);
                    if (serverUserData != null) {
                        profile.getServerInfo().setOperators(Integer.parseInt(serverUserData.get(0)));
                        logger.info("Updated server user data. Added server operators.");
                    }
                } catch (Exception e) {
                    logger.error("Failed to set list of observers. Reason: {}", e.getMessage());
                    logger.error("Failed to update server info.", e);
                }
                break;
            }

            //Try to get channels open on server
            case "254": {
                numberStarts = profile.getServer().length() + profile.getUsername().length() + 7;
                try {
                    List<String> serverUserData = this.mapServerData(line, numberStarts);
                    if (serverUserData != null) {
                        profile.getServerInfo().setChannels(Integer.parseInt(serverUserData.get(0)));
                        logger.info("Updated server user data. Added amount of open channels.");
                    }
                } catch (Exception e) {
                    logger.error("Failed to add amount of open channels. Reason: {}", e.getMessage());
                    logger.error("Failed to update server info.", e);
                }
                break;
            }

            //set server info connection state
            case "001": {
                profile.getServerInfo().setConnected(true);
                logger.info("Connection to server established.");
                break;
            }

            //Could not connect to channel, does not exist
            case "403": {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null) {
                    channel.setConnected(false);
                    logger.error("Could not join channel: {}", channel.getChannelName());
                }
                break;
            }

            //Set connection state of channel
            case "332": {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null) {
                    channel.setConnected(true);
                    logger.info("Joined channel: {}", channel.getChannelName());
                }
                break;
            }

            //Get list of users
            case "353": {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null) {
                    if (channel.isConnected()) {
                        String names = line.split(":")[2];
                        if (!names.isEmpty() && names.trim().length() > 0) {
                            String[] nameList = names.split(" ");
                            channel.getUserInChannel().addAll(Arrays.asList(nameList));
                            logger.info("Added users to the user list of channel: {}. Users: {}", channel.getChannelName(), channel.getUserInChannel().toString());
                        }
                    }
                }
                break;
            }
        }
    }

    private List<String> mapServerData(String incLine, int incNumberStarts) {
        String data = incLine.substring(incNumberStarts);
        data = data.replaceAll("[^0-9]+", " ");
        if (!data.isEmpty()) {
            return Arrays.asList(data.trim().split(" "));
        }
        return null;
    }

    private IRCChannel getAppropriateChannel(String incLine) {
        ArrayList<IRCChannel> channels = profile.getChannels();

        if (channels != null && channels.size() > 0) {
            return channels.stream().filter(channel -> incLine.toLowerCase().contains(channel.getChannelName().toLowerCase())).findFirst().get();
        }
        return null;
    }
}



