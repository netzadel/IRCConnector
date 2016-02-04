package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
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

    public IRCMessageReader(BufferedReader incReader, IRCMessageSender incSender, IRCProfile incProfile) {
        reader = incReader;
        sender = incSender;
        profile = incProfile;
    }

    public void run() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                this.interpretLine(line);
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void interpretLine(String line) {
        //System.out.println(line);
        if (line.startsWith("PING"))
            sender.sendPing(line.substring(5));
        else if (line.startsWith(":")) {
            int codeStart = profile.getServer().length() + 2;
            int codeEnd = profile.getServer().length() + 5;

            String responseCode = line.substring(codeStart, codeEnd);

            if (StringUtils.isNumeric(responseCode)) {
                this.interpretStatusNumber(responseCode, line);
            } else
                this.interpreteStatusText(line);
        }
    }

    private void interpreteStatusText(String line) {
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
                    System.out.println("<< added user " + userTMP + " to channels user list");
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
                        System.out.println("<< removed user " + userTMP + " from channels user list");
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
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            }

            //Try to get channels open on server
            case "001": {
                profile.getServerInfo().setConnected(true);
                System.out.println("<< successfully connected to server " + profile.getServer());
                break;
            }

            //Could not connect to channel, does not exist
            case "403": {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null) {
                    channel.setConnected(false);
                    System.out.println("<< could not connect to channel. Channel does not exist");
                }
                break;
            }

            //Set connection state of channel
            case "332": {
                IRCChannel channel = this.getAppropriateChannel(line);
                if (channel != null) {
                    channel.setConnected(true);
                    System.out.println("<< connected to channel " + channel.getChannelName());
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
                        }
                    }
                    System.out.println("<< updated user list of channel");
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



