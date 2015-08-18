/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

/**
 *
 * @author mg
 */
public interface ModifiedProvider {

    public boolean notifyModified();

    public void notifyUnmodified();
}
