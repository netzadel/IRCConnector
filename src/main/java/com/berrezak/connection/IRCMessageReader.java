package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                this.interpreteLine(line);
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void interpreteLine(String line) {
        System.out.println(line);
        if (line.startsWith(":")) {
            int codeStart = profile.getServer().length() + 2;
            int codeEnd = profile.getServer().length() + 5;

            String responseCode = line.substring(codeStart, codeEnd);

            if (StringUtils.isNumeric(responseCode)) {
                this.interpretStatusNumber(responseCode, line);
            }

            if (line.contains("PRIVMSG")) {
                IRCChannel a = new IRCChannel(null, false, null);
                profile.getChannels().add(a);
                List<IRCChannel> c = profile.getChannels().stream().filter(channel -> line.toLowerCase().contains(channel.getChannelName().toLowerCase())).collect(Collectors.toList());
                if (c != null && !c.isEmpty()) {
                    c.stream().forEach(channel -> channel.receivedMessage(line));
                }
            }

        }

        if (line.startsWith("PING"))

        {
            try {
                sender.sendPing(line.substring(5));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void interpretPlainText(String line) {

    }

    private void interpretStatusNumber(String responseCode, String line) {
        int numberStarts;
        switch (responseCode) {
            //Try to get the amount of visible and invisible user online on server
            case "251":
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

            //Try to get operators
            case "252":
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

            //Try to get channels open on server
            case "254":
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

            //Try to get channels open on server
            case "001":
                profile.getServerInfo().setConnected(true);
                System.out.println("logged in!");
                break;

            //Could not connect to channel, does not exist
            case "403":
                for (IRCChannel ircChannel : profile.getChannels()) {
                    if (line.contains(ircChannel.getChannelName())) {
                        ircChannel.setConnected(false);
                        System.out.println("<< could not connect to channel. Channel does not exist");
                    }
                }
                break;

            case "332":
                for (IRCChannel ircChannel : profile.getChannels()) {
                    String lowerCaseline = line.toLowerCase();
                    if (lowerCaseline.contains(ircChannel.getChannelName().toLowerCase())) {
                        ircChannel.setConnected(true);
                        System.out.println("<< connected to channel");
                    }
                }
                break;

            case "353":
                for (IRCChannel ircChannel : profile.getChannels()) {
                    String lowerCaseLine = line.toLowerCase();
                    if (lowerCaseLine.contains(ircChannel.getChannelName().toLowerCase())) {
                        if (ircChannel.isConnected()) {
                            String names = line.split(":")[2];
                            if (!names.isEmpty() && names.trim().length() > 0) {
                                String[] nameList = names.split(" ");
                                ircChannel.setUserInChannel(Arrays.asList(nameList));
                            }
                        }
                        System.out.println("<< updated user list of channel");
                    }
                }
                break;
        }
    }

    private List<String> mapServerData(String incLine, int incNumberStarts) {
        String data = incLine.substring(incNumberStarts);
        data = data.replaceAll("[^0-9]+", " ");
        if (!data.isEmpty()) {
            List<String> serverUserData = Arrays.asList(data.trim().split(" "));
            return serverUserData;
        }
        return null;
    }
}



