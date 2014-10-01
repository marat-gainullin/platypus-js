/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import java.util.concurrent.Callable;

/**
 *
 * @author mg
 */
public interface Sequence {

    public void in(Callable<Void> onAuthenticate) throws Exception;
    
}
