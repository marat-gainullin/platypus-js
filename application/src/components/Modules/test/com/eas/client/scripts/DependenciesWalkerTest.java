/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class DependenciesWalkerTest {

    @Test
    public void testParseDependencies5() {
        String va1 = "var report = new P.ServerModule(\"ANY_REPORT_NAME\"); var module = new ANY_MODULE_NAME(); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");";
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependency)->{
            return "ANY_MODULE_NAME".equals(aIfDependency);
        });
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(1, walker.getDependencies().size());
        assertEquals(2, walker.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies6() {
        String va1 = "P.require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function(){var report = new RPC.Proxy(\"ANY_REPORT_NAME\"); var module = new ANY_MODULE1_NAME(); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency)->{
            return "ANY_MODULE1_NAME".equals(aIfDependency);
        });
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(1, walker.getDependencies().size());
        assertEquals(2, walker.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies7() {
        String va1 = "P.require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function(){var report = new P.ServerModule(\"ANY_REPORT_NAME\"); var module = new ANY_MODULE1_NAME(); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");});";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertEquals(2, walker.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies8() {
        String va1 = "P.require(\"ANY_REPORT_NAME\", function(){var m = new ANY_REPORT_NAME(); var report = new P.ServerModule(\"ANY_REPORT_NAME\");});";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertFalse(walker.getServerDependencies().isEmpty());
    }
    
    @Test
    public void testParseAmdDependencies1() {
        String va1 = ""
                + "P.define(['AnyModule'], function(AnyModule){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule();"
                + "        var sm = P.ServerModule('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency)->{
            return "AnyModule".equals(aIfDependency);
        });
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertFalse(walker.getServerDependencies().isEmpty());
    }
    
    @Test
    public void testParseAmdDependencies2() {
        String va1 = ""
                + "define(['AnyModule'], function(AnyModule){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule();"
                + "        var sm = RPC.Proxy('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency)->{
            return "AnyModule".equals(aIfDependency);
        });
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertFalse(walker.getServerDependencies().isEmpty());
    }
    
    @Test
    public void testParseDependencies9() {
        String va1 = "var q = model.loadEntity('someQuery');";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertEquals(1, walker.getQueryDependencies().size());
        assertEquals("someQuery", walker.getQueryDependencies().iterator().next());
    }
    
    @Test
    public void testParseDependencies10() {
        String va1 = "var m = HY.HT.IO.PK.SomeModule;";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertEquals(2/*[m HY]*/, walker.getDependenceLikeIdentifiers().size());
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }
    
    @Test
    public void testParseDependencies11() {
        String va1 = "var m = new SomeModule();";
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependence)->{
            return "SomeModule".equals(aIfDependence);
        });
        walker.walk();
        assertEquals(2, walker.getDependenceLikeIdentifiers().size());
        assertEquals(1, walker.getDependencies().size());
        assertEquals("SomeModule", walker.getDependencies().iterator().next());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }
    
    @Test
    public void testParseDependencies12() {
        String va1 = "var m = SomeConstructor;";
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependency)->{
            return "SomeConstructor".equals(aIfDependency);
        });
        walker.walk();
        assertEquals(2, walker.getDependenceLikeIdentifiers().size());
        assertEquals(1, walker.getDependencies().size());
        assertEquals("SomeConstructor", walker.getDependencies().iterator().next());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }
}
