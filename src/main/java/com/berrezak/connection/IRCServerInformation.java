package com.berrezak.connection;

/**
 * Created by ElBerro on 25.01.2016.
 */
public class IRCServerInformation {

    private int operators;
    private int channels;
    private int clients;
    private int visibleUser;
    private int invisibleUser;

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

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
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
}
