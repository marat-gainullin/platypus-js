/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.scripts;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mozilla.javascript.BaseFunction;

/**
 *
 * @author Andrew
 */
public class ScriptRunnerTest {
    
    public ScriptRunnerTest() {
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
     * Test of hasModuleAnnotation method, of class ScriptRunner.
     */
    @Test
    public void testTemplateRoles() {
        System.out.println("testTemplateRoles");
        ScriptRunner instance = new ScriptRunner();
        ScriptRunner.SecureFunction func = instance.new SecureFunction("test", new BaseFunction());
        Date d = new Date();
        Set<String> roles = new HashSet<>(Arrays.asList(new String[]{"test:$1tost", "car$2$3un", "bar$4", "$0lox", "$5"}));
        Set<String> expectedRoles = new TreeSet<>(Arrays.asList(new String[]{"test:eattost", "car144nullun", "bartrue", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d)+"lox", "undefined"}));
        Set<String> filteredRoles = new TreeSet<>(func.filterRoles(roles, new Object[]{d, "eat", 144, null, true}));
        assertArrayEquals(expectedRoles.toArray(new String[]{}), filteredRoles.toArray(new String[]{}));
    }
    
    @Test
    public void testRoles() {
        System.out.println("testRoles");
        ScriptRunner instance = new ScriptRunner();
        ScriptRunner.SecureFunction func = instance.new SecureFunction("test", new BaseFunction());
        Set<String> roles = new HashSet<>(Arrays.asList(new String[]{"admin", "buh", "hr", "view", "mech"}));
        Set<String> expectedRoles = new TreeSet<>(Arrays.asList(new String[]{"admin", "buh", "hr", "view", "mech"}));
        Set<String> filteredRoles = new TreeSet<>(func.filterRoles(roles, new Object[]{new Date(), "eat", 144, null, true}));
        assertArrayEquals(expectedRoles.toArray(new String[]{}), filteredRoles.toArray(new String[]{}));
    }
   
    @Test
    public void testFormatNumberRoles() {
        System.out.println("testFormatNumberRoles");
        ScriptRunner instance = new ScriptRunner();
        ScriptRunner.SecureFunction func = instance.new SecureFunction("test", new BaseFunction());
        Set<String> roles = new HashSet<>(Arrays.asList(new String[]{"test:$1tost", "car$2$3un", "bar$4", "$0lox", "$5"}));
        Set<String> expectedRoles = new TreeSet<>(Arrays.asList(new String[]{"test:475.0734tost", "car144.2984289472740.23764un", "bar0", "15lox", "undefined"}));
        Set<String> filteredRoles = new TreeSet<>(func.filterRoles(roles, new Object[]{15, 475.0734, 144.298428947274, 0.23764, 0.0}));
        assertArrayEquals(expectedRoles.toArray(new String[]{}), filteredRoles.toArray(new String[]{}));
    }
    
}
