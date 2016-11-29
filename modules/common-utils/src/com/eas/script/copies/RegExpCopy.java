/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script.copies;

/**
 *
 * @author mg
 */
public class RegExpCopy {

    protected String source;
    protected String flags;

    public RegExpCopy(String aSource, String aFlags) {
        super();
        source = aSource;
        flags = aFlags;
    }

    public String getSource() {
        return source;
    }

    public String getFlags() {
        return flags;
    }
    
}
