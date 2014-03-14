/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.script.converter;

import com.eas.script.ScriptFunction;

/**
 * The support class to mock a JS API.
 * @author vv
 */
public class JsClass {
    
    private static final String CONSTRUCTOR_JS_DOC = "/**\n"
            + "* Constructor\n"
            + "*/";
    
    private static final String METHOD_A_JS_DOC = "/**\n"
            + "* Method A\n"
            + "*/";
    
    private static final String METHOD_B_JS_DOC = "/**\n"
            + "* Method B\n"
            + "*/";
    
    @ScriptFunction(name = "ConsructorA", jsDoc = CONSTRUCTOR_JS_DOC, params = {"paramA", "paramB"})
    public JsClass(String paramA) {        
    }
    
    @ScriptFunction(name = "methodA", jsDoc = METHOD_A_JS_DOC, params = {"paramA", "paramB"})
    public void methA() {
    }
    
    @ScriptFunction(name = "methodB", jsDoc = METHOD_B_JS_DOC, params = {"paramC", "paramD"})
    public String methB() {
        return "";
    }
    
}
