package com.eas.script;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.Test;

/**
 *
 * @author vv
 */
public class Classes2ScriptTest {
    
    @Test
    public void testGetClassJs() {
        Classes2Scripts converter = new Classes2Scripts();
        String js = converter.getClassJs(JsClass.class);
        System.out.println(js);
    }
}
