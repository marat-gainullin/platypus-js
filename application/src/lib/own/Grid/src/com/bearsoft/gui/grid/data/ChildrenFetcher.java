/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

/**
 * Interface, intended to fetch children of the element asynchronously.
 * @author mg
 */
public interface ChildrenFetcher<T> {
    
    /**
     * Fetches children elements on demand. Accepts Runnable instance to be used as task completer.
     * @param anElement An element whos children is to be fetched
     * @param aFetchCompleter Runnable instance, that completes fetching task (fires events, invalidates table front, etc.).
     */
    public void fetch(T anElement, Runnable aFetchCompleter, boolean aAsynchronous);
}
