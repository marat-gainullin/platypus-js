/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.concurrent;

import java.util.Timer;

/**
 *
 * @author AB
 */
public class DeamonTimer extends Timer {

    public DeamonTimer() {
        super(true);
    }
}
