/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.eas.client.SQLUtils;
import com.eas.script.ScriptUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class ConvertionsTest {

    @Test
    public void valuesConvertionsTest() {
        System.out.println("valuesConvertionsTest");
        ContextFactory cf = ContextFactory.getGlobal();
        Context cx = cf.enterContext();
        try {
            ScriptableObject scope = ScriptUtils.getScope();

            Object toConvert = BigDecimal.valueOf(1640.853049588f);
            Object jsObject = ScriptUtils.javaToJS(toConvert, scope);
            Object unconverted = ScriptUtils.js2Java(jsObject);
            assertTrue(toConvert.equals(SQLUtils.number2BigDecimal((Double) unconverted)));
            toConvert = BigInteger.valueOf(1640);
            jsObject = ScriptUtils.javaToJS(toConvert, scope);
            unconverted = ScriptUtils.js2Java(jsObject);
            assertTrue(SQLUtils.number2BigDecimal((BigInteger) toConvert).equals(SQLUtils.number2BigDecimal((Long) unconverted)));
        } finally {
            Context.exit();
        }
    }

    @Test
    public void scriptNativesConvertionsTest() {
        System.out.println("scriptNativesConvertionsTest");
        ContextFactory cf = ContextFactory.getGlobal();
        Context cx = cf.enterContext();
        try {
            ScriptableObject scope = ScriptUtils.getScope();

            Integer javaIntVar = new Integer(85);
            Double javaFloatVar = new Double(57.3f);
            String javaStringVar = "sample script string";
            Date javaDateVar = new Date();
            Boolean javaBoolVar = true;

            String scriptSource1 = String.format(
                    "var intVar = %d;\n"
                    + "var floatVar = %f;\n"
                    + "var strVar = '%s';\n"
                    + "var dateVar = new Date(%d);\n"
                    + "var boolVar = %b;\n", javaIntVar, javaFloatVar, javaStringVar, javaDateVar.getTime(), javaBoolVar);

            Script script1 = cx.compileString(scriptSource1.replace(",", "."), "s1", 0, null);
            script1.exec(cx, scope);

            assertEquals(javaIntVar, ScriptUtils.js2Java(scope.get("intVar", scope)));
            assertTrue(Math.abs(javaFloatVar - (Double)ScriptUtils.js2Java(scope.get("floatVar", scope))) < 1e-6);
            assertEquals(javaStringVar, ScriptUtils.js2Java(scope.get("strVar", scope)));
            assertEquals(javaDateVar, ((Date)ScriptUtils.js2Java(scope.get("dateVar", scope))) );
            assertEquals(javaBoolVar, ScriptUtils.js2Java(scope.get("boolVar", scope)));
        } finally {
            Context.exit();
        }
    }
}
