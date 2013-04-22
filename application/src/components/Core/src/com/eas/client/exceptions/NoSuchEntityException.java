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

    public NoSuchEntityException(Throwable cause)
    {
        super(cause);
    }

    public NoSuchEntityException(Long entityId, Throwable cause)
    {
        super(entityId.toString(), cause);
    }

    public NoSuchEntityException(Long entityId)
    {
        super(entityId.toString());
    }

    public NoSuchEntityException()
    {
    }



}
