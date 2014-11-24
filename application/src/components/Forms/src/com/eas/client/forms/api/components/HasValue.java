/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import java.beans.PropertyChangeListener;

/**
 *
 * @author mg
 * @param <V>
 */
public interface HasValue<V> {

    public V getValue();

    public void setValue(V aValue);

    public void addValueChangeListener(PropertyChangeListener listener);

}
