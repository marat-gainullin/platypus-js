/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.platypus.PlatypusRequestWriter;
import com.eas.client.threetier.platypus.RequestsTags;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class RequestTest {

    public RequestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of write method, of class Request.
     *
     * @throws Exception
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        Request instance = new KeepAliveRequest(1);
        PlatypusRequestWriter.write(instance, writer);
        writer.flush();
        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        checkRequest(inStream, instance, new byte[]{});
    }

    /**
     * Test of getID method, of class Request.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        Request instance = new RequestImpl(1, 2);
        long expResult = 1L;
        long result = instance.getID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class Request.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Request instance = new RequestImpl(1, 2);
        int expResult = 2;
        int result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResponse method, of class Request.
     */
    @Test
    public void testGetResponse() throws Exception {
        System.out.println("getResponse");
        Request instance = new RequestImpl(1, 2);
        Response expResult = null;
        Response result = instance.getResponse();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResponse method, of class Request.
     */
    @Test
    public void testSetResponse() throws Exception {
        System.out.println("setResponse");
        Request instance = new RequestImpl(1, 2);
        instance.setResponse(new ErrorResponse(1, "Test error"));
        assertTrue(instance.getResponse() instanceof ErrorResponse);
        assertEquals("Test error", ((ErrorResponse) instance.getResponse()).getErrorMessage());
        assertEquals(instance.getID(), instance.getResponse().getRequestID());
        try {
            instance.setResponse(new ErrorResponse(2, ""));
            fail("Alien response is allowed. It's nto legal !!!");
        } catch (IllegalArgumentException ex) {
            //Should be thrown.
        }
    }

    /**
     * Test of isDone method, of class Request.
     */
    @Test
    public void testIsDone() {
        System.out.println("isDone");
        Request instance = new RequestImpl(1, 2);
        boolean expResult = false;
        boolean result = instance.isDone();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDone method, of class Request.
     */
    @Test
    public void testSetDone() {
        System.out.println("setDone");
        Request instance = new RequestImpl(1, 2);
        boolean expResult = false;
        boolean result = instance.isDone();
        assertEquals(expResult, result);
        expResult = true;
        instance.setDone(true);
        assertEquals(expResult, instance.isDone());
    }

    /**
     * Test of waitCompletion method, of class Request.
     *
     * @throws Exception
     */
    @Test
    public void testWaitCompletion() throws Exception {
        System.out.println("waitCompletion");
        Request instance = new RequestImpl(1, 2);
        Thread doerThread = new Thread(new RequestDoer(instance));
        final RequestWaiter waiter = new RequestWaiter(instance);
        Thread waiterThread = new Thread(waiter);
        waiterThread.start();
        assertFalse(waiter.done);
        assertTrue(waiterThread.isAlive());
        doerThread.start();
        waiterThread.join();
        assertTrue(waiter.done);
        assertFalse(waiterThread.isAlive());
        assertTrue(instance.isDone());
    }

    private void checkRequest(ByteArrayInputStream inStream, Request instance, byte[] data) throws IOException, ProtoReaderException {
        ProtoReader reader = new ProtoReader(inStream);
        assertEquals(RequestsTags.TAG_REQUEST, reader.getNextTag());
        assertEquals(instance.getID(), reader.getLong());
        assertEquals(RequestsTags.TAG_REQUEST_TYPE, reader.getNextTag());
        assertEquals(instance.getType(), reader.getInt());
        assertEquals(RequestsTags.TAG_REQUEST_DATA, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
        byte[] writtenData = reader.getSubStreamData();
        assertArrayEquals(data, writtenData);
        assertEquals(RequestsTags.TAG_REQUEST_END, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
        assertEquals(CoreTags.TAG_EOF, reader.getNextTag());
        assertEquals(0, reader.getCurrentTagSize());
    }

    protected class RequestImpl extends Request {

        public RequestImpl(int aId, int aType) {
            super(aId, aType);
        }

        @Override
        public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public class RequestWaiter implements Runnable {

        private boolean done;
        private Request rq;

        public RequestWaiter(Request rq) {
            this.rq = rq;
        }

        @Override
        public void run() {
            try {
                rq.waitCompletion();
                done = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(RequestTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class RequestDoer implements Runnable {

        private final Request rq;

        public RequestDoer(Request rq) {
            this.rq = rq;
        }

        @Override
        public void run() {
            synchronized (rq) {
                rq.setDone(true);
                rq.notifyAll();
            }
        }
    }
}
