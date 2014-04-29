/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.filter;

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
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; Modules.get(moduleName);";
        //String va2 = "" + ScriptTransformer.SELF_NAME + ".moduleName = 'SOME_MODULE_NAME';\nModules.get(" + ScriptTransformer.SELF_NAME + ".moduleName);\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        //String va3 = transformer.walk();
        //assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies2() {
        String SOME_MODULE_TWO = "SOME_MODULE_TWO";
        String va1 = "var moduleName = 'SOME_MODULE_NAME'; Modules.get(\"" + SOME_MODULE_TWO + "\");";
        //String va2 = "" + ScriptTransformer.SELF_NAME + ".moduleName = 'SOME_MODULE_NAME';\nModules.get(\"" + SOME_MODULE_TWO + "\");\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        Set<String> dependencies = transformer.getDependencies();
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.contains(SOME_MODULE_TWO));
    }

    @Test
    public void testParseDependencies3() {
        String FORM_ID_HERE = "FORM_ID_HERE";
        String va1 = "var form = new Form(\"" + FORM_ID_HERE + "\");";
        //String va2 = "" + ScriptTransformer.SELF_NAME + ".form = new " + FORM_ID_HERE + "();\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        Set<String> dependencies = transformer.getDependencies();
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.contains(FORM_ID_HERE));
    }

    @Test
    public void testParseDependencies4() {
        String va1 = "var report = new Report(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE_NAME\"); var form = new Form(\"123456789123456\");";
        //String va2 = "" + ScriptTransformer.SELF_NAME + ".report = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".module = new ANY_MODULE_NAME();\n" + ScriptTransformer.SELF_NAME + ".form = new Form123456789123456();\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(2, transformer.getDependencies().size());
        assertEquals(1, transformer.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies5() {
        String va1 = "var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");";
        //String va2 = "" + ScriptTransformer.SELF_NAME + ".report = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".module = new ANY_MODULE_NAME();\n" + ScriptTransformer.SELF_NAME + ".report2 = new ServerReportANY_REPORT_NAME();\n" + ScriptTransformer.SELF_NAME + ".servModule = new ServerModuleANY_MODULE_NAME();\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(1, transformer.getDependencies().size());
        assertEquals(2, transformer.getServerDependencies().size());
    }

    @Test
    public void testParseDependencies6() {
        String va1 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE1_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");});";
        //String va2 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\"], function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var module = new ANY_MODULE1_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n  var servModule = new ServerModuleANY_MODULE_NAME();\n});\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        assertFalse(transformer.getDependencies().isEmpty());
        assertEquals(1, transformer.getDependencies().size());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies7() {
        String va1 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var module = new Module(\"ANY_MODULE1_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\"); var servModule = new ServerModule(\"ANY_MODULE_NAME\");});";
        //String va2 = "require([\"ANY_REPORT_NAME\", \"ANY_MODULE_NAME\", \"ANY_MODULE1_NAME\"], function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var module = new ANY_MODULE1_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n  var servModule = new ServerModuleANY_MODULE_NAME();\n});\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }

    @Test
    public void testParseDependencies8() {
        String va1 = "require(\"ANY_REPORT_NAME\", function(){var report = new ServerReport(\"ANY_REPORT_NAME\"); var report2 = new Report(\"ANY_REPORT_NAME\")});";
        //String va2 = "require(\"ANY_REPORT_NAME\", function() {\n  var report = new ServerReportANY_REPORT_NAME();\n  var report2 = new ServerReportANY_REPORT_NAME();\n});\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        transformer.walk();
        //assertEquals(va3, va2);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
    }
    
    @Test
    public void testParseDependencies9() {
        String va1 = "var q = model.loadEntity('someQuery');";
        //String va2 = "_platypusModuleSelf.q = _platypusModuleSelf.model.loadEntity('someQuery');\n";
        DependenciesWalker transformer = new DependenciesWalker(va1);
        //transformer.addExternalVariable("model");
        transformer.walk();
        //assertEquals(va2, va3);
        assertTrue(transformer.getDependencies().isEmpty());
        assertTrue(transformer.getServerDependencies().isEmpty());
        assertEquals(1, transformer.getQueryDependencies().size());
        assertEquals("someQuery", transformer.getQueryDependencies().iterator().next());
    }
}
