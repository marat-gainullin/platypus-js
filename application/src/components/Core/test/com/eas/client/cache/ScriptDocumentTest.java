/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.SettingsConstants;
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
    public void globalAndAmd2ArgDefineTest() {
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
                + "/**\n"
                + " * @module AmdDefineTest1\n"
                + " */\n"
                + "\n"
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
                + "/**\n"
                + " * @module AmdDefineTest2\n"
                + " */\n"
                + ""
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
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/script/reg-exp-literal.min-jquery.js");
        ScriptDocument doc = ScriptDocument.parse(readUrl(resource), "jquery-large-min");
    }

    private String readUrl(URL resource) throws IOException {
        try (InputStream is = resource.openStream()) {
            byte[] content = BinaryUtils.readStream(is, -1);
            return new String(content, SettingsConstants.COMMON_ENCODING);
        }
    }

    @Test
    public void jQueryLargeFile() throws IOException {
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/script/reg-exp-literal-jquery.js");
        ScriptDocument.parse(readUrl(resource), "leaflet-large");
    }

    @Test
    public void leafletLargeFile() throws IOException {
        URL resource = ScriptDocumentTest.class.getClassLoader().getResource("com/eas/script/numeric-literal-leaflet.js");
        ScriptDocument.parse(readUrl(resource), "jquery-large");
    }
}
