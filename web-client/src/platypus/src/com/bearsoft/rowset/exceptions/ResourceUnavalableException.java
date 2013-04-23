/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.exceptions;

/**
 * Exception class intended to throw in the cases of any resources unavailability.
 * @author mg
 */
public class ResourceUnavalableException extends RowsetException{

    public ResourceUnavalableException(String aMsg)
    {
        super(aMsg);
    }

    public ResourceUnavalableException(Exception aCourse)
    {
        super(aCourse);
    }
}
