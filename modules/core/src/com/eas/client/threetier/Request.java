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

    protected int type;
    private boolean done;
    private boolean cancelled;

    protected Request(int aType) {
        super();
        type = aType;
    }

    public abstract void accept(PlatypusRequestVisitor aVisitor) throws Exception;

    /**
     * @return the type
     */
    public int getType() {
        return type;
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

    @Override
    public String toString() {
        return getClass().getName() + "[TYPE: " + type + "]";
    }
}
