/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.AppPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class SessionTest {

    public SessionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of cleanup method, of class Session.
     * @throws IOException 
     */
    @Test
    public void testCleanup() throws IOException {
        System.out.println("cleanup");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        instance.cleanup();
    }

    /**
     * Test of getSessionId method, of class Session.
     */
    @Test
    public void testGetSessionId() {
        System.out.println("getSessionId");
        String sessionID = String.valueOf(IDGenerator.genID());
        Session instance = new Session(null, sessionID, new AppPlatypusPrincipal("test", null));
        String result = instance.getId();
        assertEquals(sessionID, result);
    }

    /**
     * Test of getCTime method, of class Session.
     */
    @Test
    public void testGetCTime() {
        System.out.println("getCTime");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long result = instance.getCTime();
        assertTrue(result > 0);
    }

    /**
     * Test of getATime method, of class Session.
     */
    @Test
    public void testGetATime() {
        System.out.println("getATime");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long expResult = instance.accessed();
        long result = instance.getATime();
        assertEquals(expResult, result);
    }

    /**
     * Test of accessed method, of class Session.
     */
    @Test
    public void testAccessed() {
        System.out.println("accessed");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        long result = instance.accessed();
    }

    /**
     * Test of getUser method, of class Session.
     */
    @Test
    public void testGetUser() {
        System.out.println("getUser");
        Session instance = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("test", null));
        assertEquals("test", instance.getUser());
    }

    protected static class DummyCompiledScriptDocumentsHost implements CompiledScriptDocumentsHost{

        @Override
        public CompiledScriptDocuments getDocuments() {
            return new CompiledScriptDocuments(null) {

                @Override
                protected ScriptDocument appElement2Document(ApplicationElement ae) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        @Override
        public void defineJsClass(String string, ApplicationElement ae) {
        }
    }
    
    protected static class DummyServerModule extends ServerScriptRunner {

        public DummyServerModule(PlatypusServerCore aServerCore, Session aSession, String aModuleId) throws Exception {
            super(aServerCore, aSession, aModuleId, ScriptRunner.initializePlatypusStandardLibScope(), new PrincipalHost() {

                @Override
                public PlatypusPrincipal getPrincipal() {
                    return new AppPlatypusPrincipal("dummy", null);
                }
            }, new DummyCompiledScriptDocumentsHost(),
            new Object[]{});
        }

        @Override
        public void loadApplicationElement(String aAppElementId, Object[] args) throws Exception {
            super.appElementId = aAppElementId;
        }

        @Override
        protected void prepare(ScriptDocument scriptDoc, Object[] args) throws Exception {
        }
    }

    /**
     * Test of registerModule method, of class Session.
     * @throws Exception 
     */
    @Test
    public void testRegisterModule() throws Exception {
        System.out.println("registerModule");
        Session session = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("su", null));
        String mId = IDGenerator.genID().toString();
        Set<String> tasks = new HashSet<>(); 
        tasks.add(mId);
        ServerScriptRunner serverModule = new DummyServerModule(
                new PlatypusServerCore(null, tasks, null),
                session,
                mId);

        session.registerModule(serverModule);
        ServerScriptRunner s = session.getModule(mId);
        assertSame(s, serverModule);
    }

    /**
     * Test of unregisterModule method, of class Session.
     * @throws Exception 
     */
    @Test
    public void testUnregisterModule() throws Exception {
        System.out.println("unregisterModule");
        Session session = new Session(null, String.valueOf(IDGenerator.genID()), new AppPlatypusPrincipal("su", null));
        String mId = IDGenerator.genID().toString();
        Set<String> tasks = new HashSet<>(); 
        tasks.add(mId);
        ServerScriptRunner serverModule = new DummyServerModule(
                new PlatypusServerCore(null, tasks, null),
                session, 
                mId);
        session.registerModule(serverModule);
        ServerScriptRunner s = session.getModule(mId);
        assertSame(s, serverModule);
        session.unregisterModule(String.valueOf(mId));
        s = session.getModule(String.valueOf(mId));
        assertNull(s);
    }
}