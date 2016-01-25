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
                Thread.sleep(100);
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

            switch (responseCode) {
                //Try to get the amount of visible and invisible user online on server
                case "251":
                    int numberStarts = profile.getServer().length() + profile.getUsername().length() + 18;
                    try {
                        String user = line.substring(numberStarts);
                        user = user.replaceAll("[^0-9]+", " ");
                        if (!user.isEmpty()) {
                            List<String> serverUserData = Arrays.asList(user.trim().split(" "));
                            profile.getServerInfo().setVisibleUser(Integer.parseInt(serverUserData.get(0)));
                            profile.getServerInfo().setInvisibleUser(Integer.parseInt(serverUserData.get(1)));
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
            }

        }

        if (line.startsWith("PING")) {
            try {
                sender.sendPing(line.substring(5));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

