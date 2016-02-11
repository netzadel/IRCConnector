package com.berrezak.core;

import com.berrezak.connection.IRCServerInformation;

import java.util.ArrayList;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class IRCProfile {

    private String username;
    private String server;
    private String password;
    private int port;
    private IRCServerInformation serverInfo;
    private ArrayList<IRCChannel> channels;

    public IRCProfile(String username, String server, int port) {
        this.username = username;
        this.server = server;
        this.port = port;

        serverInfo = new IRCServerInformation();
        channels = new ArrayList<IRCChannel>();
    }

    public IRCProfile(String username, String server, String password, int port) {
        this.username = username;
        this.server = server;
        this.password = password;
        this.port = port;

        serverInfo = new IRCServerInformation();
        channels = new ArrayList<IRCChannel>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public IRCServerInformation getServerInfo() {
        return serverInfo;
    }

    public ArrayList<IRCChannel> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "profile data: " + server + port + " user:" + username;
    }
}
