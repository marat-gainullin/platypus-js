/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

/**
 * Exception class intended to throw in the cases of any resources
 * unavailability.
 *
 * @author mg
 */
public class ResourceUnavalableException extends Exception {

    public ResourceUnavalableException(String aMsg) {
        super(aMsg);
    }

    public ResourceUnavalableException(Exception aCourse) {
        super(aCourse);
    }
}
