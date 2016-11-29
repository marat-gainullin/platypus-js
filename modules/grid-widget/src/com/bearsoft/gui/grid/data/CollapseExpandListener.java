/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

/**
 *
 * @author mg
 */
public interface CollapseExpandListener<T> {

    public void collapsed(T aItem);

    public void expanded(T aItem);

}
