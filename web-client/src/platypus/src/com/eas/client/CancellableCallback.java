/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Cancellable;

/**
 *
 * @author mg
 */
public interface CancellableCallback extends Cancellable {
    
    public void run() throws Exception;
}
