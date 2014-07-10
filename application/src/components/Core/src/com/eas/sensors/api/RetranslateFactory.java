/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.sensors.api;

/**
 *
 * @author Andrew
 */
public abstract class RetranslateFactory {

    private static RetranslateFactory instance;

    public static RetranslateFactory getInstance() {
        return instance;
    }

    public static void init(RetranslateFactory aValue) {
        if (instance == null) {
            instance = aValue;
        } else {
            throw new IllegalStateException("Already initialized.");
        }
    }

    public abstract void send(Packet aPacket, String aURL);

    public abstract void send(Object aPacket, String aURL);
}
