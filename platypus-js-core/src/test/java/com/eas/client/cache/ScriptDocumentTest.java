/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.SettingsConstants;
import com.eas.script.JsDoc;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @rolesAllowed role1
 */
public class ScriptDocumentTest {

    @Test
    public void annotatedGlobalAndAmd1ArgDefineTest() {
        String jsText = ""
                + "function GlobalModule1(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "function GlobalModule2(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "define('AmdDefineTest1', [], function(){"
                + "    function module_constructor(){"
                + "        var self = this;"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        self.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define('AmdDefineTest2', [], function(){"
                + "    function module_constructor(){"
                + "        var self = this;"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        self.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define(function(){"
                + "    function module_constructor(){"
                + "        var self = this;"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        self.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "AmdDefineTestDefault");
        verifyModulesDocuments(doc);
    }
    
    @Test
    public void globalAndAmd3ArgDefineTest() {
        String jsText = ""
                + "function GlobalModule1(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "function GlobalModule2(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "define('AmdDefineTest1',[], function(){"
                + "    function module_constructor(){"
                + "         var self = this;"
                + "         /**\n"
                + "          * @rolesAllowed rolea\n"
                + "          */\n"
                + "         this.a = {};"
                + "         self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define([], function(){"
                + "    function module_constructor(){"
                + "         var self = this;"
                + "         /**\n"
                + "          * @rolesAllowed rolea\n"
                + "          */\n"
                + "         this.a = {};"
                + "         self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define('AmdDefineTest2', [], function(){"
                + "    function module_constructor(){"
                + "        var self = this;"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "AmdDefineTestDefault");
        verifyModulesDocuments(doc);
    }

    @Test
    public void globalAndAmd3ArgDefineWOModuleAnnotationTest() {
        String jsText = ""
                + "/*\n"
                + " * @resident\n"
                + " * @rolesAllowed meh1\n"
                + " */"
                + "define([], function(){"
                + "    function module_constructor(){"
                + "         var self = this;"
                + "         /**\n"
                + "          * @rolesAllowed rolea\n"
                + "          */\n"
                + "         this.a = {};"
                + "         self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "function GlobalModule1(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "/*\n"
                + " * @resident\n"
                + " * @rolesAllowed meh2\n"
                + " */"
                + "function GlobalModule2(){"
                + "    var self = this;"
                + "    /**\n"
                + "     * @rolesAllowed rolea\n"
                + "     */\n"
                + "    self.a = {};"
                + "    this.m = function(){};"
                + "}"
                + "/*\n"
                + " * @resident\n"
                + " * @rolesAllowed meh3\n"
                + " */"
                + "define('AmdDefineTest1',[], function(){"
                + "    function module_constructor(){"
                + "         var self = this;"
                + "         /**\n"
                + "          * @rolesAllowed rolea\n"
                + "          */\n"
                + "         this.a = {};"
                + "         self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define('AmdDefineTest2', [], function(){"
                + "    function module_constructor(){"
                + "        var self = this;"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        self.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "AmdDefineTestDefault");
        Map<String, ScriptDocument.ModuleDocument> moduleDocuments = doc.getModules();
        assertEquals(5, doc.getModules().size());
        assertTrue(moduleDocuments.containsKey("AmdDefineTestDefault"));
        assertTrue(moduleDocuments.containsKey("AmdDefineTest1"));
        assertTrue(moduleDocuments.containsKey("AmdDefineTest2"));
        assertTrue(moduleDocuments.containsKey("GlobalModule1"));
        assertTrue(moduleDocuments.containsKey("GlobalModule2"));
        ScriptDocument.ModuleDocument defModule = moduleDocuments.get("AmdDefineTestDefault");
        assertTrue(defModule.hasAnnotation(JsDoc.Tag.RESIDENT_TAG));
        assertTrue(defModule.hasAnnotation(JsDoc.Tag.ROLES_ALLOWED_TAG));
        assertEquals(1, defModule.getAllowedRoles().size());
        assertTrue(defModule.getAllowedRoles().contains("meh1"));
        ScriptDocument.ModuleDocument globalModule1 = moduleDocuments.get("GlobalModule1");
        assertTrue(globalModule1.getAnnotations().isEmpty());
        assertTrue(globalModule1.getAllowedRoles().isEmpty());
        ScriptDocument.ModuleDocument globalModule2 = moduleDocuments.get("GlobalModule2");
        assertTrue(globalModule2.hasAnnotation(JsDoc.Tag.RESIDENT_TAG));
        assertTrue(globalModule2.hasAnnotation(JsDoc.Tag.ROLES_ALLOWED_TAG));
        assertEquals(1, globalModule2.getAllowedRoles().size());
        assertTrue(globalModule2.getAllowedRoles().contains("meh2"));
        ScriptDocument.ModuleDocument amdModule1 = moduleDocuments.get("AmdDefineTest1");
        assertTrue(amdModule1.hasAnnotation(JsDoc.Tag.RESIDENT_TAG));
        assertTrue(amdModule1.hasAnnotation(JsDoc.Tag.ROLES_ALLOWED_TAG));
        assertEquals(1, amdModule1.getAllowedRoles().size());
        assertTrue(amdModule1.getAllowedRoles().contains("meh3"));
        ScriptDocument.ModuleDocument amdModule2 = moduleDocuments.get("AmdDefineTest2");
        assertTrue(amdModule2.getAnnotations().isEmpty());
        assertTrue(amdModule2.getAllowedRoles().isEmpty());
    }

    private static void verifyModulesDocuments(ScriptDocument doc) {
        assertEquals(5, doc.getModules().size());
        assertTrue(doc.getModules().keySet().contains("AmdDefineTestDefault"));
        assertTrue(doc.getModules().keySet().contains("AmdDefineTest1"));
        assertTrue(doc.getModules().keySet().contains("AmdDefineTest2"));
        assertTrue(doc.getModules().keySet().contains("GlobalModule1"));
        assertTrue(doc.getModules().keySet().contains("GlobalModule2"));
        for (Map.Entry<String, ScriptDocument.ModuleDocument> moduleEntry : doc.getModules().entrySet()) {
            ScriptDocument.ModuleDocument moduleDoc = moduleEntry.getValue();
            assertEquals(2, moduleDoc.getFunctionProperties().size());
            assertTrue(moduleDoc.getFunctionProperties().contains("m"));
            assertTrue(moduleDoc.getFunctionProperties().contains("a"));
            moduleDoc.getPropertyAnnotations().containsKey("a");
            moduleDoc.getPropertyAllowedRoles().containsKey("a");
            Set<String> rolesOfA = moduleDoc.getPropertyAllowedRoles().get("a");
            assertEquals(1, rolesOfA.size());
            assertTrue(rolesOfA.contains("rolea"));
        }
    }

    @Test
    public void jQueryLargeFileMinified() throws IOException {
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/client/cache/reg-exp-literal.min-jquery.js");
        ScriptDocument.parse(readUrl(resource), "jquery-large-min");
    }

    @Test
    public void jQueryLargeFile() throws IOException {
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/client/cache/reg-exp-literal-jquery.js");
        ScriptDocument.parse(readUrl(resource), "leaflet-large");
    }

    private String readUrl(URL resource) throws IOException {
        try (InputStream is = resource.openStream()) {
            byte[] content = BinaryUtils.readStream(is, -1);
            return new String(content, SettingsConstants.COMMON_ENCODING);
        }
    }

    @Test
    public void leafletLargeFile() throws IOException {
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/client/cache/numeric-literal-leaflet.js");
        ScriptDocument.parse(readUrl(resource), "jquery-large");
    }
}
