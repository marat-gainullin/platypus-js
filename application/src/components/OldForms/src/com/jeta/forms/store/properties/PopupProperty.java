/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Marat
 */
public class PopupProperty extends ComponentRefProperty {

    static final long serialVersionUID = -8205688873141104577L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public static final String POPUP_PROPERTY_NAME = "popupMenu";
    public static final String PROPERTY_4REPLACE_NAME = "componentPopupMenu";
    protected JPopupMenu m_PopupMenu = null;

    public PopupProperty() {
        super();
        m_name = POPUP_PROPERTY_NAME;
    }

    @Override
    public Class getNeededClass() {
        return JPopupMenu.class;
    }

    @Override
    public void updateBean(JETABean jbean) {
        if (jbean != null) {
            Component comp = jbean.getDelegate();
            if (comp != null && comp instanceof JComponent) {
                JComponent jcomp = (JComponent) comp;
                jcomp.setComponentPopupMenu(m_PopupMenu);
            }
        }
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        GridComponent gc = aAllComps.get(m_ComponentID);
        if (gc != null) {
            setGridComponent(gc);
            Component comp = gc.getBeanDelegate();
            if (comp != null && comp instanceof JPopupMenu) {
                m_PopupMenu = (JPopupMenu) comp;
                updateBean(jbean);
            }
        }
    }
}
