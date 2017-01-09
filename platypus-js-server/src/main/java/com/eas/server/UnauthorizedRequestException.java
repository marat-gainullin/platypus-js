/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

/**
 *
 * @author pk
 */
public class UnauthorizedRequestException extends Exception { //TODO extends PlatypusException or so.

    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
