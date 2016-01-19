/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import org.junit.Test;

/**
 * @rolesAllowed role1
 */
public class ScriptDocumentTest {

    @Test
    public void amdModulesAnnotationsTest() {
        String jsText = ""
                + "function garbageGlobalFunction(){}"
                + "/**\n"
                + " * @module AmdDefineTest1\n"
                + " */\n"
                + ""
                + "define(function(){"
                + "    function module_constructor(){"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define(function(){"
                + "    function module_constructor(){"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
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
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "AmdDefineTestDefault");
    }

    @Test
    public void amdModulesNamedDefineTest() {
        String jsText = ""
                + "function garbageGlobalFunction(){}"
                + "define('AmdDefineTest1',[], function(){"
                + "    function module_constructor(){"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define([], function(){"
                + "    function module_constructor(){"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "define('AmdDefineTest2',[], function(){"
                + "    function module_constructor(){"
                + "        /**\n"
                + "         * @rolesAllowed rolea\n"
                + "         */\n"
                + "        this.a = {};"
                + "        this.m = function(){};"
                + "    }"
                + "    return module_constructor;"
                + "});"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "AmdDefineTestDefault");
    }
    
    @Test
    public void globalModulesTest(){
        String jsText = ""
                + "function garbageGlobalFunction(){}"
                + "function ModuleA(){"
                + "}"
                + "function ModuleB(){"
                + "}"
                + "function ModuleC(){"
                + "}"
                + "";
        ScriptDocument doc = ScriptDocument.parse(jsText, "GlobalTestDefault");
    }
}
