/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class JsFilterTest {

    @Test
    public void testSimpleFilter() {
        ScriptDocument sDoc = new ScriptDocument(null, "function TestFilteredModule(){ function eventHandler(){}}");
        sDoc.readScriptAnnotations();
        String filtered = sDoc.filterSource();
        assertEquals("function TestFilteredModule(){ function eventHandler(){}; this[\"-x-handlers-funcs-\"]={\"eventHandler\":eventHandler};}", filtered);
    }
    
    @Test
    public void testFilterWithSideEffects() {
        ScriptDocument sDoc = new ScriptDocument(null, "function TestFilteredModule(){ function eventHandler(){}}");
        sDoc.readScriptAnnotations();
        String filtered = sDoc.filterSource("var s = 90;", "var t = 65;");
        assertEquals("function TestFilteredModule(){var s = 90; function eventHandler(){}; this[\"-x-handlers-funcs-\"]={\"eventHandler\":eventHandler};var t = 65;}", filtered);
    }
}
