/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.NashornException;

/**
 *
 * @author mg
 * @param <T> Type of complete success argument
 * @param <F> Type of callback argument
 */
public abstract class AsyncProcess<T, F> {

    protected int expected;
    protected int completed;
    protected Set<Exception> exceptions = new HashSet<>();
    protected Consumer<F> onSuccess;
    protected Consumer<Exception> onFailure;

    public AsyncProcess(int aExpected, Consumer<F> aOnSuccess, Consumer<Exception> aOnFailure) {
        super();
        expected = aExpected;
        onSuccess = aOnSuccess;
        onFailure = aOnFailure;
    }

    protected void doComplete(F aResult) {
        if (exceptions.isEmpty()) {
            if (onSuccess != null) {
                onSuccess.accept(aResult);
            }
        } else {
            if (onFailure != null) {
                StringBuilder eMessagesSum = new StringBuilder();
                exceptions.stream().forEach((ex) -> {
                    if (eMessagesSum.length() > 0) {
                        eMessagesSum.append("\n");
                    }
                    String message = ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.toString();
                    if (ex instanceof FileNotFoundException) {
                        message = "File or module not found: " + message;
                    } else if(ex instanceof NashornException){
                        NashornException nex = (NashornException)ex;
                        message = nex.getFileName() + ":" + nex.getLineNumber() + " " + message;
                    }
                    eMessagesSum.append(message);
                });
                onFailure.accept(new IllegalStateException(eMessagesSum.toString()));
            }
        }
    }

    public abstract void complete(T aValue, Exception aFailureCause);
}
