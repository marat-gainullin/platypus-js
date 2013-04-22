/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.query;

import net.sf.jsqlparser.parser.ParseException;

/**
 *
 * @author mg
 */
public class AbsentTableParseException extends ParseException{

    public AbsentTableParseException(String aMessage)
    {
        super(aMessage);
    }
}
