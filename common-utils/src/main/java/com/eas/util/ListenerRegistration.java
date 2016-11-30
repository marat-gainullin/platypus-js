/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

/**
 * An interface to support listener removal without need of a <code>removeListener(Listener a)</code> method.
 * The object of this type has to be returned by a <code>addListener(Listener a)</code> method.
 * This object can store a reference to a listener object and implement the cleaning details.
 * A client can remove its listener by calling <code>remove()</code>.
 * @author vv
 */
public interface ListenerRegistration {

    void remove();
}
