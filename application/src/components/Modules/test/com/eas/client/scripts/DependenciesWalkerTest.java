/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class DependenciesWalkerTest {

    @Test
    public void testParseDependencies1() {
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; P.Modules.get(moduleName);";
        DependenciesWalker walker = new DependenciesWalker(va1);
        assertTrue(walker.getDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies2() {
        String SOME_MODULE_TWO = "SOME_MODULE_TWO";
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; P.Modules.get(\"" + SOME_MODULE_TWO + "\");";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        Set<String> dependencies = walker.getDependencies();
        assertEquals(1, dependencies.size());
        assertTrue(dependencies.contains(SOME_MODULE_TWO));
    }

    @Test
    public void testParseDependencies3() {
        String FORM_ID_HERE = "FORM_ID_HERE";
        String va1 = "var form = new P.Form(\"" + FORM_ID_HERE + "\");";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        Set<String> dependencies = walker.getDependencies();
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.contains(FORM_ID_HERE));
    }

    @Test
    public void testParseDependencies4() {
        String va1 = "var report = new P.Report(\"ANY_REPORT_NAME\"); var module = new P.Module(\"ANY_MODULE_NAME\"); var form = new P.Form(\"123456789123456\");";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(2, walker.getDependencies().size());
        assertEquals(1, walker.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies5() {
        String va1 = "var report = new P.ServerReport(\"ANY_REPORT_NAME\"); var module = new P.Module(\"ANY_MODULE_NAME\"); var report2 = new P.Report(\"ANY_REPORT_NAME\"); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(1, walker.getDependencies().size());
        assertEquals(2, walker.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies6() {
        String va1 = "P.require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function(){var report = new P.ServerReport(\"ANY_REPORT_NAME\"); var module = new P.Module(\"ANY_MODULE1_NAME\"); var report2 = new P.Report(\"ANY_REPORT_NAME\"); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");});";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertFalse(walker.getDependencies().isEmpty());
        assertEquals(1, walker.getDependencies().size());
        assertTrue(walker.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies7() {
        String va1 = "P.require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function(){var report = new P.ServerReport(\"ANY_REPORT_NAME\"); var module = new P.Module(\"ANY_MODULE1_NAME\"); var report2 = new P.Report(\"ANY_REPORT_NAME\"); var servModule = new P.ServerModule(\"ANY_MODULE_NAME\");});";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies8() {
        String va1 = "P.require(\"ANY_REPORT_NAME\", function(){var report = new P.ServerReport(\"ANY_REPORT_NAME\"); var report2 = new P.Report(\"ANY_REPORT_NAME\")});";
        DependenciesWalker walker = new DependenciesWalker(va1);
        walker.walk();
        assertTrue(walker.getDependencies().isEmpty());
        assertTrue(walker.getServerDependencies().isEmpty());
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
    
}
