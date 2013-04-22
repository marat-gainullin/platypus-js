/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.PlatypusRequestVisitor;

/**
 *
 * @author pk, mg refactoring
 */
public abstract class Request {

    private long id;
    protected int type;
    private Response response;
    private boolean done;
    private boolean cancelled;

    protected Request(long aRequestId, int aType) {
        id = aRequestId;
        type = aType;
    }

    public abstract void accept(PlatypusRequestVisitor aVisitor) throws Exception;

    /**
     * @return the id
     */
    public long getID() {
        return id;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the response
     */
    public Response getResponse() throws Exception {
        return response;
    }

    /**
     * @param aResponse the response to set
     */
    public void setResponse(Response aResponse) {
        if (aResponse.getRequestID() != id) {
            throw new IllegalArgumentException("Alien response. Our ID " + id + " its requestID " + aResponse.getRequestID());
        }
        response = aResponse;
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean aValue) {
        done = aValue;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean aValue) {
        cancelled = aValue;
    }

    public synchronized void waitCompletion() throws InterruptedException {
        while (!isDone()) {
            wait();
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ID: " + id + ", TYPE: " + type + "]";
    }
}
