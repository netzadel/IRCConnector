package com.berrezak.core;

import com.berrezak.connection.IRCMessageSender;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ElBerro on 25.01.2016.
 */
public class IRCChannel {

    private String channelName;
    private boolean enableHistory;
    private ArrayList<String> history;
    private IRCMessageSender sender;
    private boolean connected;

    public IRCChannel(String channelName, boolean enableHistory, IRCMessageSender sender) {
        this.channelName = "#" + channelName;
        this.enableHistory = enableHistory;
        this.sender = sender;
    }

    public void sendMessage(String message) throws IOException {
        sender.sendMessage(this.channelName, message);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = "#" + channelName;
    }

    public boolean isEnableHistory() {
        return enableHistory;
    }

    public void setEnableHistory(boolean enableHistory) {
        this.enableHistory = enableHistory;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }

    public IRCMessageSender getSender() {
        return sender;
    }

    public void setSender(IRCMessageSender sender) {
        this.sender = sender;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
