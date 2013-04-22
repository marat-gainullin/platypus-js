/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.server;

import org.junit.*;

/**
 *
 * @author pk
 */
public class ServerModuleTest {

    public ServerModuleTest() {
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
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getModuleID method, of class ServerModule.
     */
    /*
    @Test
    public void testGetModuleID()
    {
        System.out.println("getModuleID");
        ServerModule instance = null;
        String expResult = "";
        String result = instance.getModuleID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScope method, of class ServerModule.
     */
    /*
    @Test
    public void testGetScope()
    {
        System.out.println("getScope");
        ServerModule instance = null;
        Scriptable expResult = null;
        Scriptable result = instance.getScope();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScript method, of class ServerModule.
     */
    /*
    @Test
    public void testGetScript()
    {
        System.out.println("getScript");
        ServerModule instance = null;
        ScriptRuntimeDocument expResult = null;
        ScriptRuntimeDocument result = instance.getScript();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSession method, of class ServerModule.
     */
    /*
    @Test
    public void testGetSession()
    {
        System.out.println("getSession");
        ServerModule instance = null;
        Session expResult = null;
        Session result = instance.getSession();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeMethod method, of class ServerModule.
     */
    /*
    @Test
    public void testExecuteMethod() throws IOException, SQLException
    {
        System.out.println("executeMethod");
        String methodName = "";
        Object[] arguments = null;
        ServerModule instance = null;
        Object expResult = null;
        Object result = instance.executeMethod(methodName, arguments);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    @Test
    public void abstractTest()
    {
        System.out.println("This tests should work after ScriptRuntime and DatamodelRuntime modules refactoring");
    }
}