/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.beans.Introspector;
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
        String result = StringUtils.replaceFileNamesInvalidCharacters(str);
        assertEquals(expResult, result);
        str = " test";
        expResult = "test";
        result = StringUtils.replaceFileNamesInvalidCharacters(str);
        assertEquals(expResult, result);
        str = "te st";
        expResult = "te st";
        result = StringUtils.replaceFileNamesInvalidCharacters(str);
        assertEquals(expResult, result);
        str = " test  ";
        expResult = "test__";
        result = StringUtils.replaceFileNamesInvalidCharacters(str);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCapitalizeStabilityMethod(){
        String methodName = "getX";
        String propName = Introspector.decapitalize(methodName.substring(3));
        String methodName1 = "get"+StringUtils.capitalize(propName);
        assertEquals(methodName, methodName1);
        String _methodName = "getXYZ";
        String _propName = Introspector.decapitalize(_methodName.substring(3));
        String _methodName1 = "get"+StringUtils.capitalize(_propName);
        assertEquals(_methodName, _methodName1);
    }
    
    @Test
    public void testCapitalizeStabilityProperty(){
        String propName = "x";
        String methodName = "get"+StringUtils.capitalize(propName);
        String propName1 = Introspector.decapitalize(methodName.substring(3));
        assertEquals(propName, propName1);
        String _propName = "XYZ";
        String _methodName = "get"+StringUtils.capitalize(_propName);
        String _propName1 = Introspector.decapitalize(_methodName.substring(3));
        assertEquals(_propName, _propName1);
    }
}
