/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AB
 */
public class VersionTest {
        
    private Version ver=new Version("1.2.4");

    /**
     * Test of toString method, of class Version.
     */
    @Test
    public void testToString() {
        System.out.println("Version convert to string");
        String result = ver.toString();
        assertEquals("1.2.4", result);
        
    }

    /**
     * Test of compareTo method, of class Version.
     */
    @Test
    public void testCompareTo() {
        System.out.println("Compare to version object");
        Version obj = new Version("1.2.5");
        int result = obj.compareTo(ver);
        assertEquals(UpdaterConstants.NOT_EQUALS, result);
        
        obj = new Version("1.2.4");
         result = obj.compareTo(ver);
        assertEquals(UpdaterConstants.EQUALS, result);
        
        obj = new Version("2.2.5");
        result = obj.compareTo(ver);
        assertEquals(UpdaterConstants.FATAL_NOT_EQUALS, result);
    }

}
