/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas;

import com.eas.util.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class StringUtilsTest
{
    public StringUtilsTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testJoin()
    {
        String empty = StringUtils.join("/");
        assertEquals("", empty);
        String single = StringUtils.join("/", "a");
        assertEquals("a", single);
        String two = StringUtils.join("/", "a", "b");
        assertEquals("a/b", two);
        String three = StringUtils.join("/", "a", "b", "c");
        assertEquals("a/b/c", three);
        String absolute = StringUtils.join("/", "", "a", "b", "c");
        assertEquals("/a/b/c", absolute);
        String directory = StringUtils.join("/", "a", "b", "");
        assertEquals("a/b/", directory);
    }

}