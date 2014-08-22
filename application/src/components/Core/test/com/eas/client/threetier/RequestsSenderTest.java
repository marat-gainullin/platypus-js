/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.platypus.PlatypusNativeConnection;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class RequestsSenderTest {

    private LinkedBlockingDeque<Request> rqQueue;
    private ConcurrentHashMap<Long, Request> activeRequests;
    private ByteArrayOutputStream outStream;
    private RequestsSender sender;
    private Thread senderThread;

    private class PlatypusTestConnection extends PlatypusNativeConnection {

        protected OutputStream outputStream;

        public PlatypusTestConnection(OutputStream aOutputStream) {
            super(null, null, 0);
            outputStream = aOutputStream;
        }

        @Override
        public OutputStream getSocketOutputStream() throws IOException {
            return outputStream;
        }
    }

    public RequestsSenderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        rqQueue = new LinkedBlockingDeque<>();
        activeRequests = new ConcurrentHashMap<>();
        outStream = new ByteArrayOutputStream();
        sender = new RequestsSender(rqQueue, activeRequests, new PlatypusTestConnection(outStream));
        senderThread = new Thread(sender, "Requests Sender test thread");
        senderThread.start();
    }

    @After
    public void tearDown() throws InterruptedException {
        if (senderThread == null) {
            fail("senderThread must present. It should be setted up in call to setup method of this test");
        }
        senderThread.interrupt();
        senderThread.join(5000);
        if (senderThread.isAlive()) {
            System.err.println("Sender thread still not died.");
        }
    }

    /**
     * Test of run method, of class RequestsSender.
     * @throws InterruptedException
     * @throws IOException
     * @throws ProtoReaderException
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        LoginRequest rq = new LoginRequest(IDGenerator.genID(), "testLogin", "testPassword");
        rqQueue.put(rq);
        while (rqQueue.contains(rq)) {
            Thread.sleep(100);
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        ProtoReader reader = new ProtoReader(inStream);
        assertEquals(RequestsTags.TAG_REQUEST, reader.getNextTag());
        assertEquals(rq.getID(), reader.getLong());
        assertEquals(RequestsTags.TAG_REQUEST_TYPE, reader.getNextTag());
        assertEquals(rq.getType(), reader.getInt());
        assertEquals(RequestsTags.TAG_REQUEST_DATA, reader.getNextTag());
        byte[] ddd = reader.getSubStreamData();
        LoginRequest rq1 = new LoginRequest(IDGenerator.genID());
        PlatypusRequestReader reqBodyReader = new PlatypusRequestReader(ddd);
        rq1.accept(reqBodyReader);
        assertEquals("testLogin", rq1.getLogin());
        assertEquals("testPassword", rq1.getPassword());
        assertEquals(RequestsTags.TAG_REQUEST_END, reader.getNextTag());
    }

    /**
     * Test of getException method, of class RequestsSender.
     */
    @Test
    public void testGetException() {
        assertNull(sender.getException());
        //TODO test such that output stream will throw an IOException. Check that the request is back in queue.
    }
}
