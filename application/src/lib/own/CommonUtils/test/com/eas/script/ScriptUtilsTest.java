/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.Date;
import javax.script.ScriptException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class ScriptUtilsTest {

    @Test
    public void toJavaTest() throws ScriptException{
        ScriptUtils.init();
        Object jsDate = ScriptUtils.exec("new Date();");
        Object javaDate = ScriptUtils.toJava(jsDate);
        assertTrue(javaDate instanceof Date);
        Object jsString = ScriptUtils.exec("new String('Sample');");
        Object javaString = ScriptUtils.toJava(jsString);
        assertTrue(javaString instanceof String);
        Object jsNumber = ScriptUtils.exec("new Number(90.5);");
        Object javaDouble = ScriptUtils.toJava(jsNumber);
        assertTrue(javaDouble instanceof Double);
        Object jsBoolean = ScriptUtils.exec("new Boolean(true);");
        Object javaBoolean = ScriptUtils.toJava(jsBoolean);
        assertTrue(javaBoolean instanceof Boolean);
        Object jsLiteralString = ScriptUtils.exec("('Sam' + 'p' + 'le');");
        Object javaUtilString = ScriptUtils.toJava(jsLiteralString);
        assertTrue(javaUtilString instanceof String);
   }
    
    @Test
    public void indentifiersCheckTest() {
        assertTrue(ScriptUtils.isValidJsIdentifier("testFunc"));
        assertFalse(ScriptUtils.isValidJsIdentifier(""));
        assertFalse(ScriptUtils.isValidJsIdentifier(null));
        assertFalse(ScriptUtils.isValidJsIdentifier("t-estFunc"));
    }
}
