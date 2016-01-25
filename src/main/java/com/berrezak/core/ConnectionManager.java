package com.berrezak.core;

import com.berrezak.connection.IRCConnection;

import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class ConnectionManager {

    private IRCProfile profile;
    private IRCConnection connection;

    public ConnectionManager(IRCProfile profile) {
        this.profile = profile;
    }

    public void openConnection(){
        IRCConnection connection = new IRCConnection(profile);

        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
