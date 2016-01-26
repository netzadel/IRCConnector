package com.berrezak.test;

import com.berrezak.core.ConnectionManager;
import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;

import java.io.IOException;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class test {

    public static void main(String[] args) {

        IRCProfile profile = new IRCProfile("elberrro", "underworld2.no.quakenet.org", 6667);
        ConnectionManager manager = new ConnectionManager(profile);
        manager.openConnection();

        try {
            IRCChannel beginner = manager.connectChannel("beginner", false);
            if (beginner != null) {
                beginner.sendMessage("Hallo!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newMessage(String message) {

    }
}
