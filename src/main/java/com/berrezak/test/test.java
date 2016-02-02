package com.berrezak.test;

import com.berrezak.core.ConnectionManager;
import com.berrezak.core.IMessageReceiver;
import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;

import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class test implements IMessageReceiver{

    public  void start() {
        IRCProfile profile = new IRCProfile("elberro", "underworld2.no.quakenet.org", 6667);
        ConnectionManager manager = new ConnectionManager(profile);
        manager.openConnection();

        try {
            IRCChannel beginner = manager.connectChannel("beginner", false);
            if (beginner != null) {
                beginner.registerForChatMessages(this);
                beginner.sendMessage("Hello!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedMessage(String user, String message) {

    }
}
