/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.syntax;

import java.util.ArrayList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author mg
 */
public interface SyntaxLookup {
    /**
     * Fills a drop down syntax lookup list with some objects
     * @param aModel List model to be filled.
     * @return A vector with lookuped items.
     */
    public ArrayList<Object> getAvailableItems(String toTheLeft, String aLine);
    /**
     * Returns list cell renderer for model's items.
     * @return List cell renderer for model's items.
     */
    public ListCellRenderer getRenderer();
    /**
     * Converts selected item to string to insert it in document.
     * @param anItem Model's item to be converted.
     * @return String to be inserted into the editing document.
     */
    public String convertItem2String(Object anItem);
}
