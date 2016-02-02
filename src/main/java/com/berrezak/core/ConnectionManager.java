package com.berrezak.core;

import com.berrezak.connection.IRCConnection;
import com.berrezak.connection.IRCMessageSender;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class ConnectionManager {

    //TODO: Add history for chat
    //TODO: Add event handling

    private IRCProfile profile;
    private IRCConnection connection;


    public ConnectionManager(IRCProfile profile) {
        this.profile = profile;
    }

    public void openConnection() {
        connection = new IRCConnection(profile);
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IRCChannel connectChannel(String channelName, Boolean enableHistory) throws IOException {
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
                        //TODO: Hope that there wont be an exception
                    }
                }
            }
        }
        return null;
    }
}
