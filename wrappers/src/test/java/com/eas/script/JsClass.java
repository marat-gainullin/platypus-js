package com.eas.script;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * The support class to mock a JS API.
 * @author vv
 */
public class JsClass {
    
    private static final String CONSTRUCTOR_JS_DOC = "/**\n"
            + "* Constructor A\n"
            + "*/";
    
    private static final String METHOD_A_JS_DOC = "/**\n"
            + "* Method A\n"
            + "*/";
    
    private static final String METHOD_B_JS_DOC = "/**\n"
            + "* Method B\n"
            + "*/";
    
    private static final String PROPERTY_X_JS_DOC = "/**\n"
            + "* Property X\n"
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
    
    @ScriptFunction(name = "property", jsDoc = PROPERTY_X_JS_DOC)
    public String getPropX() {
        return "";
    }
    
    @ScriptFunction
    public void setPropX(String val) {
    }
    
    @ScriptFunction(name = "readOnlyProperty", jsDoc = PROPERTY_X_JS_DOC)
    public String getRoPropX() {
        return "";
    }
    
}
