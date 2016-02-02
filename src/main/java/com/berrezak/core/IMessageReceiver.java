package com.berrezak.core;

/**
 * Created by michael.berrezak on 02.02.2016.
 */
public interface IMessageReceiver {
    void receivedMessage(String user, String message);
}
