package com.berrezak.connection;

import com.berrezak.core.IRCChannel;
import com.berrezak.core.IRCProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(IRCConnection.class);

    public IRCConnection(IRCProfile profile) {
        this.profile = profile;
        logger.info("Initialized connection object.");
    }

    public void connect() {
        // Connect to the server.
        BufferedReader buffReader = null;
        BufferedWriter buffWriter = null;
        try {
            Socket socket = new Socket(profile.getServer(), profile.getPort());

            InetAddress intetAddress = socket.getLocalAddress();

            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            buffReader = new BufferedReader(inputStreamReader);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            buffWriter = new BufferedWriter(outputStreamWriter);

            logger.info("Connected successfully to server: ", profile.getServer());
        } catch (IOException e) {
            logger.error("failed to connect to: {} reason: {}", profile.getServer(), e.getMessage());
            logger.error("failed to connect.", e);
        }

        sender = new IRCMessageSender(buffWriter, profile);
        //send password
        sender.sendPassword();
        //send login
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
                logger.error("Failed to logon on server. Reason: {}", e.getMessage());
                logger.error("Failed to logon.", e);
            }
        }
    }

    public IRCMessageSender getSender() {
        return sender;
    }
}


