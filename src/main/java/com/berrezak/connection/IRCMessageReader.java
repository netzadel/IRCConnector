package com.berrezak.connection;

import com.berrezak.core.IRCProfile;

import java.io.BufferedReader;
import java.io.IOException;
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
            System.out.println(responseCode);


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



