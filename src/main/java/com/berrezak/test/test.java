package com.berrezak.test;

import com.berrezak.core.ConnectionManager;
import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class test{

    public static void main(String[] args) {

        IRCProfile profile = new IRCProfile("elberrro", "euroserv.fr.quakenet.org", 6667);
        ConnectionManager manager = new ConnectionManager(profile);
        manager.openConnection();

        IRCChannel beginner = manager.createChannel("beginner", false);
        beginner.connectToChannel();

        beginner.sendMessage("Hallo!");
    }

    public void newMessage(String message) {

    }
}
