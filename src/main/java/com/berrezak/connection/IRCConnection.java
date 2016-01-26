package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class IRCConnection {

    private IRCProfile profile;
    private IRCMessageSender sender;
    private IRCMessageReader reader;

    public IRCConnection(IRCProfile profile) {
        this.profile = profile;
    }

    public void connect() throws IOException {

        // Connect to the server.
        Socket socket = new Socket(profile.getServer(), profile.getPort());

        InetAddress intetAddress = socket.getLocalAddress();

        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader buffReader = new BufferedReader(inputStreamReader);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter buffWriter = new BufferedWriter(outputStreamWriter);

        sender = new IRCMessageSender(buffWriter, profile);
        //send password
        sender.sendPassword();
        //send login
        //TODO add queue handling
        sender.sendLogin();

        reader = new IRCMessageReader(buffReader, sender, profile);
        Thread t = new Thread(reader);
        t.start();

        int timeout = 10; //Seconds
        while (timeout > 0) {
            if (profile.getServerInfo().isConnected())
                break;
            timeout--;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                //TODO: Hope that there wont be an exception
            }
        }

        if (profile.getServerInfo().isConnected())
            System.out.println(">> Connected.");
        else
            System.out.println(">> Not Connected.");
    }

    public IRCMessageSender getSender() {
        return sender;
    }

    public IRCMessageReader getReader() {
        return reader;
    }
}


