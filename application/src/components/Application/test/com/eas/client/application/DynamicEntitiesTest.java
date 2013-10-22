/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import org.junit.Test;

/**
 *
 * @author mg
 */
public class DynamicEntitiesTest {
    
    @Test
    public void scriptCreateEntityTest() throws Exception{
        ApplicationScriptsTest.scriptTest("Create_Entity_Test");
    }
    
    @Test
    public void scriptLoadEntityTest() throws Exception{
        ApplicationScriptsTest.scriptTest("Load_Entity_Test");
    }
}
