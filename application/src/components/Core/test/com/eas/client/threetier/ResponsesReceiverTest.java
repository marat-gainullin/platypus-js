/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.HelloRequest;
import com.eas.client.threetier.platypus.PlatypusNativeConnection;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk
 */
public class ResponsesReceiverTest {

    private ConcurrentHashMap<Long, Request> activeRequests;

    private class PlatypusTestConnection extends PlatypusNativeConnection {

        protected InputStream inputStream;

        public PlatypusTestConnection(InputStream aInputStream) {
            super(null, null, 0);
            inputStream = aInputStream;
        }

        @Override
        public InputStream getSocketInputStream() throws IOException {
            return inputStream;
        }
    }

    public ResponsesReceiverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        activeRequests = new ConcurrentHashMap<>();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class ResponsesReceiver.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testRun() throws Exception {
        // a simple test with no response data.
        HelloRequest rq = new HelloRequest(IDGenerator.genID());
        RequestWaiter waiter = new RequestWaiter(rq);
        Thread waiterThread = new Thread(waiter, "Waits for a request to complete");
        waiterThread.start();
        activeRequests.put(rq.getID(), rq);
        Response rsp = new HelloRequest.Response(rq.getID());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.putSignature();
        PlatypusResponseWriter.write(rsp, writer);

        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());

        ResponsesReceiver receiver = new ResponsesReceiver(new PlatypusTestConnection(inStream), activeRequests);
        Thread receiverThread = new Thread(receiver, "Response Receiver");
        receiverThread.start();
        Thread.sleep(300);
        receiverThread.interrupt();
        receiverThread.join();

        assertFalse(activeRequests.containsKey(rq.getID()));
        assertNotNull(rq.getResponse());
        assertEquals(rq.getID(), rq.getResponse().getRequestID());
        waiterThread.join();
        assertTrue(waiter.notified);

        //test the case with error response.
        rq = new HelloRequest(IDGenerator.genID());
        waiter = new RequestWaiter(rq);
        waiterThread = new Thread(waiter, "Waits for request");
        waiterThread.start();
        activeRequests.put(rq.getID(), rq);
        rsp = new ErrorResponse(rq.getID(), "Test error");

        outStream = new ByteArrayOutputStream();
        writer = new ProtoWriter(outStream);
        writer.putSignature();
        PlatypusResponseWriter.write(rsp, writer);
        writer.flush();

        inStream = new ByteArrayInputStream(outStream.toByteArray());

        receiver = new ResponsesReceiver(new PlatypusTestConnection(inStream), activeRequests);
        receiverThread = new Thread(receiver, "Response Receiver");
        receiverThread.start();
        Thread.sleep(300);
        receiverThread.interrupt();
        receiverThread.join();

        assertFalse(activeRequests.containsKey(rq.getID()));
        assertNotNull(rq.getResponse());
        assertEquals(rq.getID(), rq.getResponse().getRequestID());
        assertTrue(rq.getResponse() instanceof ErrorResponse);
        assertEquals("Test error", ((ErrorResponse) rq.getResponse()).getErrorMessage());
        waiterThread.join();
        assertTrue(waiter.notified);
    }

    private class RequestWaiter implements Runnable {

        private final Request rq;
        private boolean notified;

        public RequestWaiter(Request aRequest) {
            super();
            rq = aRequest;
        }

        @Override
        public void run() {
            try {
                synchronized (rq) {
                    while (!rq.isDone()) {
                        rq.wait();
                    }
                    notified = true;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ResponsesReceiverTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
