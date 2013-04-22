/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author AB
 */
public class StringUtilsTest {
    
    public StringUtilsTest() {
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
     * Test of replaceUnsupportedSymbols method, of class StringUtils.
     */
    @Test
    public void testReplaceUnsupportedSymbols() {
        System.out.println("replaceUnsupportedSymbols");
        String str = "test";
        String expResult = "test";
        String result = StringUtils.replaceUnsupportedSymbols(str);
        assertEquals(expResult, result);
        str = "1test";
        expResult = "_test";
        result = StringUtils.replaceUnsupportedSymbols(str);
        assertEquals(expResult, result);
        str = "te st";
        expResult = "te_st";
        result = StringUtils.replaceUnsupportedSymbols(str);
        assertEquals(expResult, result);
        str = "te*st";
        expResult = "te_st";
        result = StringUtils.replaceUnsupportedSymbols(str);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testReplaceUnsupportedFileNamesSymbols() {
        System.out.println("replaceUnsupportedFileNamesSymbols");
        String str = "test";
        String expResult = "test";
        String result = StringUtils.replaceUnsupportedSymbolsinFileNames(str);
        assertEquals(expResult, result);
        str = " test";
        expResult = "test";
        result = StringUtils.replaceUnsupportedSymbolsinFileNames(str);
        assertEquals(expResult, result);
        str = "te st";
        expResult = "te st";
        result = StringUtils.replaceUnsupportedSymbolsinFileNames(str);
        assertEquals(expResult, result);
        str = " test  ";
        expResult = "test__";
        result = StringUtils.replaceUnsupportedSymbolsinFileNames(str);
        assertEquals(expResult, result);
    }
}
