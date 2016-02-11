package com.berrezak.core;

import com.berrezak.connection.IRCConnection;
import com.berrezak.connection.IRCMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class ConnectionManager {
    //TODO: Better exception handling
    //TODO: Better logging
    //TODO: Allow private messages
    //TODO: Testing

    private IRCProfile profile;
    private IRCConnection connection;
    private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    public ConnectionManager(IRCProfile profile) {
        this.profile = profile;
        logger.info("Initialized connection manager." + profile.toString());
    }

    public void openConnection() {
        connection = new IRCConnection(profile);
        connection.connect();
    }

    public IRCChannel connectChannel(String channelName, Boolean enableHistory) {
        if (profile.getServerInfo().isConnected()) {
            IRCChannel channel = new IRCChannel(channelName, enableHistory, connection.getSender());
            IRCMessageSender sender = connection.getSender();

            if (!channelName.isEmpty() && sender != null) {
                profile.getChannels().add(channel);
                sender.joinChannel(channel.getChannelName());

                int timeout = 10; //Seconds
                while (timeout > 0) {
                    //The reader will set the isConnected flag of the channel to true as soon as it receives the connection result from IRCServer
                    if (channel.isConnected()) {
                        return channel;
                    }
                    timeout--;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
