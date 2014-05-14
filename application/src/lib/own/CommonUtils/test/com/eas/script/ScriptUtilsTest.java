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
        Object jsString1 = ScriptUtils.exec("new String('Sample');");
        Object javaString1 = ScriptUtils.toJava(jsString1);
        assertTrue(javaString1 instanceof String);
        Object jsString2 = ScriptUtils.exec("'Sample'");
        Object javaString2 = ScriptUtils.toJava(jsString2);
        assertTrue(javaString2 instanceof String);
        Object jsNumber1 = ScriptUtils.exec("new Number(90.5);");
        Object javaDouble1 = ScriptUtils.toJava(jsNumber1);
        assertTrue(javaDouble1 instanceof Double);
        Object jsNumber2 = ScriptUtils.exec("90.5");
        Object javaDouble2 = ScriptUtils.toJava(jsNumber2);
        assertTrue(javaDouble2 instanceof Double);
        Object jsBoolean1 = ScriptUtils.exec("new Boolean(true);");
        Object javaBoolean1 = ScriptUtils.toJava(jsBoolean1);
        assertTrue(javaBoolean1 instanceof Boolean);
        Object jsBoolean2 = ScriptUtils.exec("true");
        Object javaBoolean2 = ScriptUtils.toJava(jsBoolean2);
        assertTrue(javaBoolean2 instanceof Boolean);
        Object jsLiteralString = ScriptUtils.exec("('Sam' + 'p' + 'le');");
        Object javaUtilString = ScriptUtils.toJava(jsLiteralString);
        assertTrue(javaUtilString instanceof String);
        String jsComments = "/* a */ function() { } //b";
        String jsRemovedComments = ScriptUtils.removeComments(jsComments);
        assertEquals(jsRemovedComments, "        function() { }    ");
   }
    
    @Test
    public void indentifiersCheckTest() {
        assertTrue(ScriptUtils.isValidJsIdentifier("testFunc"));
        assertFalse(ScriptUtils.isValidJsIdentifier(""));
        assertFalse(ScriptUtils.isValidJsIdentifier(null));
        assertFalse(ScriptUtils.isValidJsIdentifier("t-estFunc"));
    }
}
