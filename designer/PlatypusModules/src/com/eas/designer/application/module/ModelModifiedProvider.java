/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

/**
 *
 * @author mg
 */
public interface ModelModifiedProvider {

    public boolean isModelModified();

    public void setModelModified(boolean aValue);
}
