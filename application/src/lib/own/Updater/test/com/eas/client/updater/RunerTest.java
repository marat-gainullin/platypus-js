/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AB
 */
public class RunerTest {
    
    
    @Test
    public void testRunThis() {
        System.out.println("RunThis");
        String args = "ping";
        Runner r=new Runner();
        boolean result = r.runThis(args);
        assertTrue(result);
    }
}
