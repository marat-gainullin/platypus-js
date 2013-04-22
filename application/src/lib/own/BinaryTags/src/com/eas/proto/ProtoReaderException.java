/*
 * ProtoReaderException.java
 *
 * Created on 18 September 2009, 13:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.eas.proto;

/**
 * The exception indicating a malformed platypus stream.
 *
 * @author pk, mg
 */
public class ProtoReaderException extends java.lang.Exception {

    /**
     * Creates a new instance of ProtoReaderException, using a <code>aCause</code> as a cause
     * @param aCause A cause of the exception
     */
    public ProtoReaderException(Throwable aCause) {
        super(aCause);
    }

    /**
     * Creates a new instance of ProtoReaderException
     *
     * @param msg the human-readable message of exception.
     */
    public ProtoReaderException(String msg) {
        super(msg);
    }
}
