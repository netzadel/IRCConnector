package com.berrezak.connection;

/**
 * Created by ElBerro on 25.01.2016.
 */
public class IRCServerInformation {

    private int operators;
    private int channels;
    private int visibleUser;
    private int invisibleUser;
    private boolean isConnected;

    public int getOperators() {
        return operators;
    }

    public void setOperators(int operators) {
        this.operators = operators;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }


    public int getVisibleUser() {
        return visibleUser;
    }

    public void setVisibleUser(int visibleUser) {
        this.visibleUser = visibleUser;
    }

    public int getInvisibleUser() {
        return invisibleUser;
    }

    public void setInvisibleUser(int invisibleUser) {
        this.invisibleUser = invisibleUser;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
