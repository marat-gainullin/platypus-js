/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PostGISWKTTest {

    @Test
    public void regExpTest() {
        String sample = "((45.5,56.8),(45.5,.8),(455,568),(45.5,56.8),(45.5,56.8)),((45.5,56.8),(455,56.8),(45.5,56.8),(45.5,568),(45.5,56.8))";
        String res = sample.replaceAll(",([\\d\\.])", " $1");
        String expected = "((45.5 56.8),(45.5 .8),(455 568),(45.5 56.8),(45.5 56.8)),((45.5 56.8),(455 56.8),(45.5 56.8),(45.5 568),(45.5 56.8))";
        assertEquals(expected, res);
    }
}
