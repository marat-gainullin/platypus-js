/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public interface TablesGridContainer {

    public boolean try2StopAnyEditing();

    public boolean try2CancelAnyEditing();

    public boolean isAutoRedraw();

    public void enqueueRedraw();

    public void redraw();

    public void reindexColumns();

    public JSObject getOnRender();

    public JSObject elementByViewIndex(int aViewIndex);

    public JSObject elementByModelIndex(int aIdx);
}
