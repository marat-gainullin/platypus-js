/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

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
        for(Map.Entry<String, ScriptDocument.ModuleDocument> moduleEntry : doc.getModules().entrySet()){
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
}
