package com.berrezak.connection;

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

    public void initializeConnection() throws IOException {
        this.writeRawMessage("CAP LS");
    }

    public void sendPassword() throws IOException {
        if (profile.getPassword() != null && !profile.getPassword().equals("")) {
            this.writeRawMessage("PASS " + profile.getPassword());
        }
    }

    public void sendLogin() throws IOException {
        this.writeRawMessage("NICK " + this.profile.getUsername());
        this.writeRawMessage("USER " + this.profile.getUsername() + " 0 * :...");
        this.writeRawMessage("MODE " + this.profile.getUsername() + " -i");
    }

    public void sendVersion() throws IOException {
        this.writeRawMessage("NOTICE evMON :VERSION berryIRC 1.0");
    }

    public void sendPing(String incRequest) throws IOException {
        this.writeRawMessage(":" + this.profile.getServer() + " PONG " + incRequest);
    }

    public void sendNames(String channelName) throws IOException {
        if (channelName != null && !channelName.equals("")) {
            this.writeRawMessage("NAMES " + channelName);
        }

    }

    public void writeRawMessage(String message) throws IOException {
        if (this.bWriter != null) {
            System.out.println(">> " + message);
            this.bWriter.write(message + "\r\n");
            this.bWriter.flush();
        }

    }
}
