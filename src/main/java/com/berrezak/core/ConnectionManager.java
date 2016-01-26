package com.berrezak.core;

import com.berrezak.connection.IRCConnection;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class ConnectionManager {

    private IRCProfile profile;
    private IRCConnection connection;
    private ArrayList<IRCChannel> channels;

    public ConnectionManager(IRCProfile profile) {
        this.profile = profile;
    }

    public void openConnection(){
        connection = new IRCConnection(profile);
        channels = new ArrayList<>();
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IRCChannel createChannel(String channelName, Boolean enableHistory){
        IRCChannel channel = new IRCChannel(channelName, enableHistory, connection.getSender());
        channels.add(channel);
        return channel;
    }

    public void joinChannel(){}
}
