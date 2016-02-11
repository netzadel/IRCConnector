package com.berrezak.test;

import com.berrezak.core.ConnectionManager;
import com.berrezak.core.IMessageReceiver;
import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class test implements IMessageReceiver {

    public void start() {
        BasicConfigurator.configure();
        //Create a profile with server and user info
        IRCProfile profile = new IRCProfile("mike1973", "underworld2.no.quakenet.org", 6667);

        //Create an instance of ConnectionManager and open the connection to the server
        ConnectionManager manager = new ConnectionManager(profile);
        manager.openConnection();

        //Create/Join a channel with the ConnectionManager object
        IRCChannel beginner = manager.connectChannel("beginner", false);

        //Start interacting with the channel
        if (beginner != null) {
            beginner.registerForChatMessages(this);
            beginner.sendMessage("Hello!");
        }
    }

    //Receive chat massages here
    @Override
    public void receivedMessage(String user, String message) {
        System.out.println("<< test class received the following from: " + user + " message: " + message);
    }
}
