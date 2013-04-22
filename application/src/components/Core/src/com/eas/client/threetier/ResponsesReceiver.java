/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.client.threetier.requests.PlatypusResponsesFactory;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class ResponsesReceiver implements Runnable {

    private PlatypusNativeConnection connection;
    private Map<Long, Request> activeRequests;
    private int reconnectionCount = 0;

    public ResponsesReceiver(PlatypusNativeConnection aConnection, Map<Long, Request> aActiveRequests) {
        connection = aConnection;
        activeRequests = aActiveRequests;
    }

    @Override
    public void run() {
        Long requestId = null;
        byte[] responseData = null;
        boolean error = false;
        ProtoReader reader = null;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (reader == null) {
                        reader = new ProtoReader(new BufferedInputStream(connection.getSocketInputStream()));
                    }
                    try {
                        switch (reader.getNextTag()) {
                            case RequestsTags.TAG_RESPONSE:
                                requestId = reader.getLong();
                                break;
                            case RequestsTags.TAG_ERROR_RESPONSE:
                                requestId = reader.getLong();
                                error = true;
                                break;
                            case RequestsTags.TAG_RESPONSE_DATA:
                                responseData = reader.getSubStreamData();
                                break;
                            case RequestsTags.TAG_RESPONSE_END:
                                respond(requestId, responseData, error);
                                requestId = null;
                                responseData = null;
                                error = false;
                                break;
                        }
                    } catch (ProtoReaderException ex) {
                        Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.SEVERE, "Error reading response on request " + requestId, ex);
                        respond(requestId, null, true);
                        requestId = null;
                        responseData = null;
                        error = false;
                    }
                } catch (IOException ex) {
                    Long savedRequestID = requestId;
                    requestId = null;
                    reader = null;
                    responseData = null;
                    error = false;
                    if (ex instanceof SocketException) {
                        if (Thread.currentThread().isInterrupted()) {
                            Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.INFO, "Shutting down responses recieving.");
                        } else {
                            try {
                                reconnectionCount = connection.reconnect(reconnectionCount);
                            } catch (InterruptedException rex) {
                                Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.INFO, "Shutting down responses recieving.");
                                break;
                            } catch (ProtoReaderException rex) {
                                // Some net failure. Try to reconnect within next iteration.
                            } catch (IOException rex) {
                                // Some net failure. Try to reconnect within next iteration.
                            }
                        }
                    } else {
                        Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.SEVERE, "Error reading response on request " + String.valueOf(savedRequestID), ex);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.SEVERE, "Error running responses reciever thread", ex);
        }
    }

    private void respond(Long aRequestId, byte[] aResponseData, boolean aError) throws Exception {
        if (aRequestId != null) {
            Request request = activeRequests.get(aRequestId);
            if (request != null) {
                Response response;
                if (aError) {
                    response = new ErrorResponse(aRequestId, "");
                } else {
                    PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory(request.getID());
                    request.accept(responseFactory);
                    response = responseFactory.getResponse();
                }                
                PlatypusResponseReader responseReader = new PlatypusResponseReader(aResponseData);
                response.accept(responseReader);
                synchronized (request) {
                    request.setResponse(response);
                    activeRequests.remove(request.getID());
                    request.setDone(true);
                    request.notifyAll();
                }
            } else {
                Logger.getLogger(ResponsesReceiver.class.getName()).log(Level.INFO, "Got response for unknown request {0}", aRequestId);
            }
        }
    }
}
