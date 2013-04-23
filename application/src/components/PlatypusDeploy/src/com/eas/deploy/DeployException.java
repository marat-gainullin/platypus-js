/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

/**
 * Custom exception shows error in deploy process
 * @author vv
 */
public class DeployException extends Exception {
    
    public DeployException(String message) {
        super(message);
    }
}
