/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class ContextTest {

    @Test
    public void indentifierCheckTest() {
        assertTrue(ScriptUtils.isValidJsIdentifier("testFunc"));
        assertFalse(ScriptUtils.isValidJsIdentifier(""));
        assertFalse(ScriptUtils.isValidJsIdentifier(null));
        assertFalse(ScriptUtils.isValidJsIdentifier("t-estFunc"));
    }
}
