/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Mg
 */
public class MenuItemsItem extends ToolButtonsItem {

    static final long serialVersionUID = 2375974406561270626L;
    public final static String id_Children = "Children";
    public LinkedList<ToolButtonsItem> m_Children = new LinkedList<ToolButtonsItem>();
    private MenuItemsItem m_Parent = null;

    public MenuItemsItem() {
        super();
    }

    public MenuItemsItem(MenuItemsItem aParent, String aName, String aCaption, String aHint,
            String aPictureFile, String aScriptAction, ComponentActionRefProperty aAction, Boolean aToggle,
            Integer aGroupNumber, Integer aMnemonic, Integer aMnemonicModifiers, boolean aVisible) {
        super(aName, aCaption, aHint, aPictureFile, aScriptAction, aAction, aToggle, aGroupNumber, aMnemonic, aMnemonicModifiers, aVisible);
        m_Parent = aParent;
    }

    public void setParent(MenuItemsItem aParent) {
        m_Parent = aParent;
    }

    public MenuItemsItem getParent() {
        return m_Parent;
    }

    public Object[] buildPath(Object aRoot, boolean aSelfInclude) {
        LinkedList lPath = new LinkedList();
        MenuItemsItem lParent = this;
        if (!aSelfInclude) {
            lParent = lParent.m_Parent;
        }
        lPath.add(lParent);
        while (lParent.m_Parent != null) {
            lParent = lParent.m_Parent;
            lPath.add(lParent);
        }
        if (aRoot != null) {
            lPath.add(aRoot);
        }
        Object[] larrayPath = new Object[lPath.size()];
        for (int i = lPath.size() - 1; i >= 0; i--) {
            larrayPath[lPath.size() - 1 - i] = lPath.get(i);
        }
        return larrayPath;
    }

    @Override
    public String toString() {
        return m_Caption;
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        m_Children = (LinkedList<ToolButtonsItem>) in.readObject(id_Children, ToolButtonsProperty.EMPTY_TOOL_BUTTON_LIST);
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(ToolButtonsItem.class));
        out.writeObject(id_Children, m_Children);
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        super.resolveReferences(jbean, aAllComps, propName);
        if (m_Children != null) {
            for (int i = 0; i < m_Children.size(); i++) {
                ToolButtonsItem tbi = m_Children.get(i);
                if (tbi != null) {
                    tbi.resolveReferences(jbean, aAllComps, propName);
                }
            }
        }
    }
}
