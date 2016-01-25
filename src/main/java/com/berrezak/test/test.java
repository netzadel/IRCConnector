package com.berrezak.test;

import com.berrezak.core.ConnectionManager;
import com.berrezak.core.IRCProfile;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class test{

    public static void main(String[] args) {

        IRCProfile profile = new IRCProfile("elberro2", "b0rk.uk.quakenet.org", 6667);
        ConnectionManager manager = new ConnectionManager(profile);

        manager.openConnection();
    }

    public void newMessage(String message) {

    }
}
