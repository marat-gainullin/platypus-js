/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

/**
 * Interface, intended to support listeners of cache operations
 * @author mg
 */
public interface CacheListener<K> {

    public void removed(K aId) throws Exception;

    public void cleared() throws Exception;

    public void added(K aId) throws Exception;
}
