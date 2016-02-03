package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class IRCMessageSender {
    private BufferedWriter bWriter;
    private IRCProfile profile;

    public IRCMessageSender(BufferedWriter incWriter, IRCProfile incProfile) {
        this.profile = incProfile;
        this.bWriter = incWriter;
    }

    public void initializeConnection() {
        this.writeRawMessage("CAP LS");
    }

    public void sendPassword() {
        if (profile.getPassword() != null && !profile.getPassword().equals("")) {
            this.writeRawMessage("PASS " + profile.getPassword());
        }
    }

    public void sendLogin() {
        this.writeRawMessage("NICK " + this.profile.getUsername());
        this.writeRawMessage("USER " + this.profile.getUsername() + " 0 * :...");
        this.writeRawMessage("MODE " + this.profile.getUsername() + " -i");
    }

    public void sendVersion() {
        this.writeRawMessage("NOTICE evMON :VERSION berryIRC 1.0");
    }

    public void sendPing(String incRequest) {
        this.writeRawMessage(":" + this.profile.getServer() + " PONG " + incRequest);
    }

    public void sendNames(String channelName) {
        if (channelName != null && !channelName.equals("")) {
            this.writeRawMessage("NAMES " + channelName);
        }
    }

    public void joinChannel(String channelName) {
        writeRawMessage("JOIN " + channelName);
    }

    public void sendMessage(String channelName, String message) {
        writeRawMessage("PRIVMSG " + channelName + " " + message);
    }

    public void writeRawMessage(String message) {
        if (this.bWriter != null) {
            System.out.println(">> " + message);
            try {
                this.bWriter.write(message + "\r\n");
                this.bWriter.flush();
            } catch (IOException e) {
                //TODO: better exception handling
                e.printStackTrace();
            }
        }

    }
}
