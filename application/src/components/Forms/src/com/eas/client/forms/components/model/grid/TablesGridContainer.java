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

    public void reindexColumns();

    public JSObject getOnRender();

    public JSObject veiwIndex2Row(int aViewIndex);

    public JSObject index2Row(int aIdx);
}
