/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.script;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vv
 */
public class FileNameSupportTest {
    
    public FileNameSupportTest() {
    }

    @Test
    public void testGetFileName() {
        assertEquals(FileNameSupport.getFileName("aSimpleName"), "a-simple-name");
        assertEquals(FileNameSupport.getFileName("aSimpleNameX"), "a-simple-name-x");
        assertEquals(FileNameSupport.getFileName("SimpleNameXY"), "simple-name-xy");
        assertEquals(FileNameSupport.getFileName("SimpleNameXY-7.8.9"), "simple-name-xy");
        assertEquals(FileNameSupport.getFileName("platypus-js-SimpleNameXY-7.8.9"), "simple-name-xy");
        assertEquals(FileNameSupport.getFileName("SimpleNameXY"), "simple-name-xy");
    }
    
}
