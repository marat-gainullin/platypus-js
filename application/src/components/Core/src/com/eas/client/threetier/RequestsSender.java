/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.ProtoWriter;
import java.io.*;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk, mg refactoring
 */
public class RequestsSender implements Runnable {

    private BlockingQueue<Request> queue;
    private Map<Long, Request> activeRequests;
    private Throwable exception;
    private final PlatypusNativeConnection connection;
    private OutputStream sockOutStream;
    int reconnectionCount = 0;

    public RequestsSender(BlockingQueue<Request> aQueue, Map<Long, Request> aActiveRequests, PlatypusNativeConnection aConnection) {
        super();
        queue = aQueue;
        connection = aConnection;
        activeRequests = aActiveRequests;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Request request = queue.poll(1, TimeUnit.MINUTES);
                if (request != null) {
                    try {
                        sendRequest(request);
                    } catch (Exception ex) {
                        Logger.getLogger(RequestsSender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // Nothing happened, send keep-alive request.
                    /*
                    // Let's insert some harmful code to imitate a bad net.
                    try {
                    connection.getSocket().close();
                    } catch (Exception ex) {
                    }
                     */
                    try {
                        sendRequest(new KeepAliveRequest(IDGenerator.genID()));
                    } catch (Exception ex) {
                        Logger.getLogger(RequestsSender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (InterruptedException ex) {
            // no op. Interrupted is normal thread state and so, we have to end the tread.
        }
    }

    public Throwable getException() {
        return exception;
    }

    private void sendRequest(Request request) throws InterruptedException, Exception {
        boolean requestSent = false;
        while (!requestSent) {
            synchronized (request) {
                if (!request.isCancelled()) {
                    try {
                        pushRequestToNet(request);
                        requestSent = true;
                    } catch (InterruptedIOException ex) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedException();
                    } catch (Exception ex) {
                        if (ex instanceof SocketException) {
                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException();
                            }
                            sockOutStream = null;
                            try {
                                reconnectionCount = connection.reconnect(reconnectionCount);
                                // relogin
                                Request rq = new LoginRequest(IDGenerator.genID(), connection.getLogin(), connection.getPassword(), connection.getSessionId());
                                pushRequestToNet(rq);
                                rq.waitCompletion();
                                if (rq.getResponse() instanceof ErrorResponse) {
                                    rq = new LoginRequest(IDGenerator.genID(), connection.getLogin(), connection.getPassword());
                                    pushRequestToNet(rq);
                                    rq.waitCompletion();
                                    if (rq.getResponse() instanceof ErrorResponse) {
                                        connection.handleErrorResponse((ErrorResponse) rq.getResponse());
                                    } else {
                                        assert rq.getResponse() instanceof LoginRequest.Response;
                                        connection.setSessionId(((LoginRequest.Response) rq.getResponse()).getSessionId());
                                    }
                                }
                                resendActiveRequests();
                            } catch (IOException | ProtoReaderException ex1) {
                                Logger.getLogger(RequestsSender.class.getName()).log(Level.SEVERE, "Error while reconnecting", ex1);
                            }
                        } else {
                            Logger.getLogger(RequestsSender.class.getName()).log(Level.SEVERE, "Error while sending request", ex);
                            //respond to request with error message.
                            activeRequests.remove(request.getID());
                            requestSent = true;
                            request.setResponse(new ErrorResponse(request.getID(), ex.getLocalizedMessage()));
                            request.setDone(true);
                            request.notifyAll();
                        }
                    }
                } else {
                    activeRequests.remove(request.getID());
                }
            }
        }
    }

    private void pushRequestToNet(Request request) throws IOException {
        if (sockOutStream == null) {
            sockOutStream = new BufferedOutputStream(connection.getSocketOutputStream());
        }
        Logger.getLogger(RequestsSender.class.getName()).log(Level.FINEST, "Request {0}", request);
        activeRequests.put(request.getID(), request);
        ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(bufOutStream);
        PlatypusRequestWriter.write(request, writer);
        writer.flush();
        byte[] buf = bufOutStream.toByteArray();
        sockOutStream.write(buf);
        sockOutStream.flush();
    }

    private void resendActiveRequests() throws IOException {
        ProtoWriter activeRequestsWriter = new ProtoWriter(new BufferedOutputStream(connection.getSocketOutputStream()));
        for (Request activeRequest : activeRequests.values()) {
            synchronized (activeRequest) {
                if (!activeRequest.isCancelled()) {
                    Logger.getLogger(RequestsSender.class.getName()).log(Level.FINE, "Resent request {0}", activeRequest);
                    PlatypusRequestWriter.write(activeRequest, activeRequestsWriter);
                } else {
                    activeRequests.remove(activeRequest.getID());
                }
            }
        }
        activeRequestsWriter.flush();
    }
}
