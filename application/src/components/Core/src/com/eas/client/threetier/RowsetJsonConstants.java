/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author kl
 */
public class RowsetJsonConstants {
    
    public static final String JSON_CONTENTTYPE = "text/javascript";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
}
