/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author mg
 */
public class Cookie {

    public static final String COOKIE_EXPIRES_DATE_FORMAT1 = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String COOKIE_EXPIRES_DATE_FORMAT2 = "EEE, dd-MMM-yyyy HH:mm:ss z";
    public static final String DOMAIN_ATTRIBUTE_NAME = "Domain";
    public static final String EXPIRES_ATTRIBUTE_NAME = "Expires";
    public static final String HTTPONLY_ATTRIBUTE_NAME = "HttpOnly";
    public static final String MAXAGE_ATTRIBUTE_NAME = "Max-Age";
    public static final String PATH_ATTRIBUTE_NAME = "Path";
    public static final String SECURE_ATTRIBUTE_NAME = "Secure";
    // Essensial attributes
    protected String name;
    protected String value;
    // Valueless attributes
    protected boolean secure;
    protected boolean httpOnly;
    // Other attributes
    protected String domain;
    protected String path;
    protected Date cDate = new Date();// current time
    protected long maxAge;

    public Cookie(String aName, String aValue, boolean aSecure, boolean aHttpOnly, String aDomain, String aPath, Long aMaxAge) {
        name = aName;
        value = aValue;
        secure = aSecure;
        httpOnly = aHttpOnly;
        domain = aDomain;
        path = aPath;
        if (aMaxAge != null) {
            maxAge = aMaxAge * 1000;
        } else {
            maxAge = Long.MAX_VALUE;// Never expires
        }
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    public Date getExpires() {
        long expires = cDate.getTime() + maxAge;
        return new Date(expires > 0 ? expires : Long.MAX_VALUE);
    }

    public boolean isActual() {
        return System.currentTimeMillis()-cDate.getTime() < maxAge;
    }

    public static Cookie parse(String aHeaderValue) throws ParseException, NumberFormatException {
        String[] cValues = aHeaderValue.split(";");
        // Name and Value attributes
        String cName = null;
        String cValue = null;
        // Valueless attributes                    
        Boolean cSecure = false;
        Boolean cHttpOnly = false;
        String cDomain = null;
        String cPath = null;
        Long cMaxAge = null;
        for (int i = 0; i < cValues.length; i++) {
            String cNameValue = cValues[i];
            String[] nameVal = cNameValue.split("=");
            if (nameVal != null && nameVal.length > 0) {
                String cAttrName = nameVal[0].trim();
                if (!cAttrName.isEmpty()) {
                    if (SECURE_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                        cSecure = true;
                    } else if (HTTPONLY_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                        cHttpOnly = true;
                    }
                    if (nameVal.length > 1) {
                        String cAttrValue = nameVal[1].trim();
                        if (!cAttrValue.isEmpty()) {
                            if (i == 0) {
                                // Name attribute should be the first in accordance to http
                                cName = cAttrName;
                                cValue = cAttrValue;
                            } else {
                                // Other attributes
                                if (DOMAIN_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                                    cDomain = cAttrValue;
                                } else if (PATH_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                                    cPath = cAttrValue;
                                } else if (EXPIRES_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                                    SimpleDateFormat sdf1 = new SimpleDateFormat(COOKIE_EXPIRES_DATE_FORMAT1, Locale.UK);
                                    SimpleDateFormat sdf2 = new SimpleDateFormat(COOKIE_EXPIRES_DATE_FORMAT2, Locale.UK);
                                    Date expires;
                                    try {
                                        expires = sdf1.parse(cAttrValue);
                                    } catch (Exception ex) {
                                        expires = sdf2.parse(cAttrValue);
                                    }
                                    cMaxAge = expires.getTime() - System.currentTimeMillis();
                                } else if (MAXAGE_ATTRIBUTE_NAME.equalsIgnoreCase(cAttrName)) {
                                    cMaxAge = Long.valueOf(cAttrValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cName != null && !cName.isEmpty()) {
            return new Cookie(cName, cValue, cSecure, cHttpOnly, cDomain, cPath, cMaxAge);
        } else {
            return null;
        }
    }
}
