package com.berrezak.connection;

import com.berrezak.core.IRCProfile;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by ElBerro on 17.01.2016.
 */
public class IRCConnection {

    private IRCProfile profile;

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

        IRCMessageSender sender = new IRCMessageSender(buffWriter, profile);

        //send password
        sender.sendPassword();
        //send login
        //TODO add queue handling
        sender.sendLogin();

        IRCMessageReader reader = new IRCMessageReader(buffReader, sender, profile);
        Thread t = new Thread(reader);
        t.run();


//        String nick = this.getName();
//        OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
//        OutputThread.sendRawLine(this, bwriter, "USER " + this.getLogin() + " 8 * :" + this.getVersion());
//
//        _inputThread = new InputThread(this, socket, breader, bwriter);
//
//        // Read stuff back from the server to see if we connected.
//        String line = null;
//        int tries = 1;
//        while ((line = breader.readLine()) != null) {
//
//            this.handleLine(line);
//
//            int firstSpace = line.indexOf(" ");
//            int secondSpace = line.indexOf(" ", firstSpace + 1);
//            if (secondSpace >= 0) {
//                String code = line.substring(firstSpace + 1, secondSpace);
//
//                if (code.equals("004")) {
//                    // We're connected to the server.
//                    break;
//                } else if (code.equals("433")) {
//                    if (_autoNickChange) {
//                        tries++;
//                        nick = getName() + tries;
//                        OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
//                    } else {
//                        socket.close();
//                        _inputThread = null;
//                        throw new NickAlreadyInUseException(line);
//                    }
//                } else if (code.equals("439")) {
//                    // No action required.
//                } else if (code.startsWith("5") || code.startsWith("4")) {
//                    socket.close();
//                    _inputThread = null;
//                    throw new IrcException("Could not log into the IRC server: " + line);
//                }
//            }
//            this.setNick(nick);
//
//        }
//
//        this.log("*** Logged onto server.");
//
//        // This makes the socket timeout on read operations after 5 minutes.
//        // Maybe in some future version I will let the user change this at runtime.
//        socket.setSoTimeout(5 * 60 * 1000);
//
//        // Now start the InputThread to read all other lines from the server.
//        _inputThread.start();
//
//        // Now start the outputThread that will be used to send all messages.
//        if (_outputThread == null) {
//            _outputThread = new OutputThread(this, _outQueue);
//            _outputThread.start();
//        }
//
//        this.onConnect();


    }
}
