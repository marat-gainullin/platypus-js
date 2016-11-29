/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.exceptions;

/**
 *
 * @author pk
 */
public class NoSuchEntityException extends Exception {

    public NoSuchEntityException(Throwable cause) {
        super(cause);
    }

    public NoSuchEntityException(String entityId, Throwable cause) {
        super(entityId, cause);
    }

    public NoSuchEntityException(String entityId) {
        super(entityId);
    }

    public NoSuchEntityException() {
    }

}
