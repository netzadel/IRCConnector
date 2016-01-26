package com.berrezak.core;

import com.berrezak.connection.IRCMessageReader;
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

    public IRCChannel(String channelName, boolean enableHistory, IRCMessageSender sender) {
        this.channelName = channelName;
        this.enableHistory = enableHistory;
        this.sender = sender;
    }

    public void connectToChannel(){
        try {
            if(!channelName.isEmpty() && sender != null)
                sender.joinChannel(this.channelName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){

    }
}
