/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

/**
 *
 * @author mg
 */
public class RowsetMissingException extends Exception {

    public RowsetMissingException() {
        super();
    }

    public RowsetMissingException(String aMessage) {
        super(aMessage);
    }
}
