/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import jdk.nashorn.api.scripting.JSObject;

/**
 * Interace for array data aware widget.
 * It views and edits an array of script objects.
 * Example is ModelGrid.
 * @author mg
 */
public interface ArrayModelWidget {

    public boolean makeVisible(JSObject anElement, boolean needToSelect) throws Exception;

    /**
     * Adds an element to processed elements set. For example as tree expanded element in lazy mode.
     * @param anElement An element to add;
     */
    public void addProcessedElement(JSObject anElement);

    /**
     * Removes an element from processed elements set.
     * @param anElement A element to remove;
     */
    public void removeProcessedElement(JSObject anElement);

    public JSObject[] getProcessedElements();

    public boolean isElementProcessed(JSObject anElement);
}
