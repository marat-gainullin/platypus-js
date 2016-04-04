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
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependency) -> {
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
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency) -> {
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
                + "P.define(['AnyModule'], function(AnyModule1){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule1();"
                + "        var sm = P.ServerModule('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency) -> {
            return "AnyModule1".equals(aIfDependency);
        });
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertFalse(walker.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseAmdDependencies2() {
        String va1 = ""
                + "define(['AnyModule'], function(AnyModule2){"
                + "    return function(){"
                + "        var self = this;"
                + "        var pm = new AnyModule1();"
                + "        var am = new AnyModule2();"
                + "        var sm = RPC.Proxy('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (ifDependency) -> {
            return "AnyModule1".equals(ifDependency) || "AnyModule2".equals(ifDependency);
        });
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(1, walker.getDependencies().size());
        assertTrue(walker.getDependencies().contains("AnyModule1"));
        assertEquals(1, walker.getServerDependencies().size());
        assertTrue(walker.getServerDependencies().contains("ServerCalc"));
    }

    @Test
    public void testParseAmdDependencies3() {
        String va1 = ""
                + "var pm = new AnyModule1();"
                + "define(['AnyModule'], function(AnyModule2){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule2();"
                + "        var am = new AnyModule3();"
                + "        var sm = RPC.Proxy('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (ifDependency) -> {
            return "AnyModule1".equals(ifDependency) || "AnyModule2".equals(ifDependency) || "AnyModule3".equals(ifDependency);
        });
        walker.walk();
        assertEquals(2, walker.getDependencies().size());
        assertTrue(walker.getDependencies().contains("AnyModule1"));
        assertTrue(walker.getDependencies().contains("AnyModule3"));
        assertEquals(1, walker.getServerDependencies().size());
        assertTrue(walker.getServerDependencies().contains("ServerCalc"));
    }

    @Test
    public void testParseAmdDependencies4() {
        String va1 = ""
                + "P.require(['AnyModule'], function(AnyModule1){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule1();"
                + "        var sm = P.ServerModule('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency) -> {
            return "AnyModule1".equals(aIfDependency);
        });
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertFalse(walker.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseAmdDependencies5() {
        String va1 = ""
                + "require(['AnyModule'], function(AnyModule2){"
                + "    return function(){"
                + "        var self = this;"
                + "        var am = new AnyModule2();"
                + "        var sm = RPC.Proxy('ServerCalc');"
                + "    };"
                + "});";
        DependenciesWalker walker = new DependenciesWalker(va1, (aIfDependency) -> {
            return "AnyModule2".equals(aIfDependency);
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
        assertTrue(walker.getQueryDependencies().contains("someQuery"));
    }

    @Test
    public void testParseDependencies10() {
        String va1 = "var m = HY.HT.IO.PK.SomeModule;";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies11() {
        String va1 = "var m = new SomeModule();";
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependence) -> {
            return "SomeModule".equals(aIfDependence);
        });
        walker.walk();
        assertEquals(1, walker.getDependencies().size());
        assertTrue(walker.getDependencies().contains("SomeModule"));
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies12() {
        String va1 = "var m = SomeConstructor;";
        DependenciesWalker walker = new DependenciesWalker(va1, (String aIfDependency) -> {
            return "SomeConstructor".equals(aIfDependency) || "m".equals(aIfDependency);
        });
        walker.walk();
        assertEquals(1, walker.getDependencies().size());
        assertTrue(walker.getDependencies().contains("SomeConstructor"));
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }

    @Test
    public void testTryCatchTryFinally() {
        String sample = ""
                + "try{"
                + "}catch(e){"
                + "  try{"
                + "  }finally{"
                + "    try{"
                + "    }catch(e1){"
                + "    }"
                + "  }"
                + "}";
        DependenciesWalker walker = new DependenciesWalker(sample, null);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }

    @Test
    public void testTryFinallyTryCatch() {
        String sample = ""
                + "try{"
                + "}finally{"
                + "  try{"
                + "  }catch(e){"
                + "    try{"
                + "    }finally{"
                + "      try{"
                + "      }catch(e1){"
                + "      }"
                + "    }"
                + "  }"
                + "}";
        DependenciesWalker walker = new DependenciesWalker(sample, null);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
        assertTrue(walker.getQueryDependencies().isEmpty());
    }

}
