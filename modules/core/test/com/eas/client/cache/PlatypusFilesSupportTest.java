/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.ClientConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AB
 */
public class PlatypusFilesSupportTest {

    protected static final String CRLF = System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME);
    
    public PlatypusFilesSupportTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getAnnotationValue method, of class PlatypusFilesSupport.
     */
    @Test
    public void testGetAnnotationValue() {
        System.out.println("getAnnotationValue");
        String aContent = "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        String aAnnotationName = "@name";
        String expResult = "2222222222222222222";
        String result = PlatypusFilesSupport.getAnnotationValue(aContent, aAnnotationName);
        assertEquals(expResult, result);

        aContent = "        /**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        result = PlatypusFilesSupport.getAnnotationValue(aContent, aAnnotationName);
        assertEquals(expResult, result);
        aContent = "        "+CRLF
                + "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        result = PlatypusFilesSupport.getAnnotationValue(aContent, aAnnotationName);
        assertEquals(expResult, result);
        aContent = " function test(){} "+CRLF
                + "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        expResult = null;
        result = PlatypusFilesSupport.getAnnotationValue(aContent, aAnnotationName);
        assertEquals(expResult, result);
    }

    /**
     * Test of replaceAnnotationValue method, of class PlatypusFilesSupport.
     */
    @Test
    public void testReplaceAnnotationValue() {
        System.out.println("replaceAnnotationValue");
        String aContent = "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        String aAnnotationName = "@name";
        String aValue = "11111111111111111";
        String expResult = "/**"+CRLF
                + "* @name 11111111111111111"+CRLF
                + "*/";
        String result = PlatypusFilesSupport.replaceAnnotationValue(aContent, aAnnotationName, aValue);
        assertEquals(expResult, result);

        aContent = "        /**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        expResult = "        /**"+CRLF
                + "* @name 11111111111111111"+CRLF
                + "*/";
        result = PlatypusFilesSupport.replaceAnnotationValue(aContent, aAnnotationName, aValue);
        assertEquals(expResult, result);

        aContent = "        "+CRLF
                + "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        expResult = "        "+CRLF
                + "/**"+CRLF
                + "* @name 11111111111111111"+CRLF
                + "*/";
        result = PlatypusFilesSupport.replaceAnnotationValue(aContent, aAnnotationName, aValue);
        assertEquals(expResult, result);

        aContent = " function test(){} "+CRLF
                + "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        expResult = ""
                + "/**"+CRLF
                + " * @name 11111111111111111"+CRLF
                + " */"+CRLF
                + " function test(){} "+CRLF
                + "/**"+CRLF
                + "* @name 2222222222222222222"+CRLF
                + "*/";
        result = PlatypusFilesSupport.replaceAnnotationValue(aContent, aAnnotationName, aValue);
        assertEquals(expResult, result);
    }
}
