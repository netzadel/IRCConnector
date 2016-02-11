package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class IRCMessageSender {
    private BufferedWriter bWriter;
    private IRCProfile profile;
    private static Logger logger = LoggerFactory.getLogger(IRCMessageSender.class);

    public IRCMessageSender(BufferedWriter incWriter, IRCProfile incProfile) {
        this.profile = incProfile;
        this.bWriter = incWriter;
        logger.info("Initialized message sender.");
    }

    public void initializeConnection() {
        this.writeRawMessage("CAP LS");
    }

    public void sendPassword() {
        logger.info("Sending password to server.");
        if (profile.getPassword() != null && !profile.getPassword().equals("")) {
            this.writeRawMessage("PASS " + profile.getPassword());
        }
    }

    public void sendLogin() {
        logger.info("Sending login to server.");
        this.writeRawMessage("NICK " + this.profile.getUsername());
        this.writeRawMessage("USER " + this.profile.getUsername() + " 0 * :...");
        this.writeRawMessage("MODE " + this.profile.getUsername() + " -i");
    }

    public void sendVersion() {
        this.writeRawMessage("NOTICE evMON :VERSION berryIRC 1.0");
    }

    public void sendPing(String incRequest) {
        logger.info("Sending ping to server.");
        this.writeRawMessage(":" + this.profile.getServer() + " PONG " + incRequest);
    }

    public void sendNames(String channelName) {
        logger.info("Sending names request to server.");
        if (channelName != null && !channelName.equals("")) {
            this.writeRawMessage("NAMES " + channelName);
        }
    }

    public void joinChannel(String channelName) {
        logger.info("Sending join to server, for channel: {}", channelName);
        writeRawMessage("JOIN " + channelName);
    }

    public void sendMessage(String channelName, String message) {
        logger.info("Sending message '{}' to channel {}", message, channelName);
        writeRawMessage("PRIVMSG " + channelName + " " + message);
    }

    public void writeRawMessage(String message) {
        if (this.bWriter != null) {
            try {
                this.bWriter.write(message + "\r\n");
                this.bWriter.flush();
                logger.info("Call successfully sent.");
            } catch (IOException e) {
                logger.error("failed to send command to: {} reason: {}", profile.getServer(), e.getMessage());
                logger.error("failed to communicate.", e);
            }
        }
    }
}
