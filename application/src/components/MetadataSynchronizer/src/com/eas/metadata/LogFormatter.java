/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author vy
 */
public class LogFormatter extends Formatter
{

    @Override
    public String format(LogRecord record) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(record.getMessage());
        sb.append("\n");
        return sb.toString();
    }
    
}
