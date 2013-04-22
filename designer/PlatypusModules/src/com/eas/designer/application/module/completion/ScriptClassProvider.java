/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

/**
 *
 * @author vv
 */
public interface ScriptClassProvider {

    public Class<?> getClassByName(String name);
}
