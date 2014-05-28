/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptFunction;


/**
 * Class, aggregating row data, style information and substituting information,
 * that should be displayed instead of real data.
 * @author Gala
 */
public class CellData implements Comparable<Object> {

    public CascadedStyle style = null;
    public Object data = null;
    public Object display = null;

    /**
     * Simple constructor for controls models data.
     * @param aStyle CascadedStyle instance, holding information about style of the cell, row or other unit of a control.
     * @param aData Real data, took from subject area data.
     * @param aDisplay Data, thet should be displayed instead of real data
     * @see CascadedStyle
     */
    public CellData(CascadedStyle aStyle, Object aData, Object aDisplay) {
        super();
        style = aStyle;
        data = aData;
        display = aDisplay;
    }

    /**
     * Returns CascadedStyle instance of this data unit.
     * @return CascadedStyle instance.
     * @see CascadedStyle
     */
    @ScriptFunction(jsDoc = ""
                + "/**\n"
                + " * The cell's style.\n"
                + " */")
    public CascadedStyle getStyle() {
        return style;
    }

    /**
     * Returns real data from subject area.
     * @return Real data.
     */
    @ScriptFunction(jsDoc = ""
                + "/**\n"
                + " * The cell's data.\n"
                + " */")
    public Object getData() {
        return data;
    }

    /**
     * Returns data, that should be displayed instead of rela data.
     * @return
     */
    @ScriptFunction(jsDoc = ""
                + "/**\n"
                + " * The displayed text.\n"
                + " */")
    public Object getDisplay() {
        return display;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CellData) {
            CellData cd = (CellData) o;
            Object o1 = display != null ? display : data;
            Object o2 = cd.display != null ? cd.display : cd.data;
            if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
                Comparable<Object> c1 = (Comparable<Object>) o1;
                Comparable<Object> c2 = (Comparable<Object>) o2;
                return c1.compareTo(c2);
            } else if (o1 == null && o2 != null) {
                return -1;
            } else if (o2 == null && o1 != null) {
                return 1;
            }
        }
        return 0;
    }
}
