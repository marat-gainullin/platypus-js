/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;


/**
 *
 * @author mg
 */
public interface Callback<T> extends Cancellable {

    public void run(T aResult) throws Exception;
}
