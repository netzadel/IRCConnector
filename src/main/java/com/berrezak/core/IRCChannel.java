package com.berrezak.core;

import com.berrezak.connection.IRCMessageSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ElBerro on 25.01.2016.
 */
public class IRCChannel {

    private String channelName;
    private boolean enableHistory;
    private List<String> history;
    private List<String> userInChannel;
    private List<IMessageReceiver> receivers;
    private IRCMessageSender sender;
    private boolean connected;


    public IRCChannel(String channelName, boolean enableHistory, IRCMessageSender sender) {
        receivers = new ArrayList<>();
        history = new ArrayList<>();
        userInChannel = new ArrayList<>();
        this.channelName = "#" + channelName;
        this.enableHistory = enableHistory;
        this.sender = sender;
    }

    public void receivedMessage(String message){
        this.history.add(message);

        //get username and message only.
        String[] parted = message.split(":");
        String userTMP = parted[1].split("!")[0];
        String messageTMP = parted[2];

        for (IMessageReceiver receiver : receivers) {
            receiver.receivedMessage(userTMP, messageTMP);
        }
    }

    public void registerForChatMessages(IMessageReceiver receiver){
        receivers.add(receiver);
    }

    public void sendMessage(String message) {
        sender.sendMessage(this.channelName, message);
    }

    public List<String> getUserInChannel() {
        return userInChannel;
    }

    public void setUserInChannel(List<String> userInChannel) {
        this.userInChannel = userInChannel;
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

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
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
