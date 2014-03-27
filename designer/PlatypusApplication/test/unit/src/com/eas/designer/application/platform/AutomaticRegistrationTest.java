/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.platform;

import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author vv
 */
public class AutomaticRegistrationTest {

    /**
     * Test of main method, of class AutomaticRegistration.
     */
    @Test
    public void testRegisterPlatypusRuntime() {
        AutomaticRegistration.registerPlatypusRuntime("/home/vv/clusterTest", "/home/vv/Platypus");
    }
    
}
