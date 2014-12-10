/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author mg
 */
public class CardLayout extends java.awt.CardLayout {

    protected Set<ItemListener> changeListeners = new HashSet<>();
    protected Map<String, Component> comps = new HashMap<>();

    public CardLayout() {
        this(0, 0);
    }
    
    public CardLayout(int hgap, int vgap) {
        super(hgap, vgap);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        super.addLayoutComponent(comp, constraints);
        if (constraints instanceof String) {
            comps.put((String) constraints, comp);
        }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        super.removeLayoutComponent(comp);
        for (Entry<String, Component> entry : comps.entrySet()) {
            if (entry.getValue() == comp) {
                comps.remove(entry.getKey());
                break;
            }
        }
    }

    public Component getComponent(String aCardName) {
        return comps.get(aCardName);
    }

    @Override
    public void show(Container parent, String name) {
        super.show(parent, name);
        fireStateChanged();
    }

    protected void fireStateChanged() {
        
        ItemEvent e = new ItemEvent(new ItemSelectable() {

            @Override
            public Object[] getSelectedObjects() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void addItemListener(ItemListener l) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeItemListener(ItemListener l) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }, 0, null, 0);
        changeListeners.stream().forEach((l) -> {
            l.itemStateChanged(e);
        });
    }

    public void addChangeListener(ItemListener l) {
        changeListeners.add(l);
    }

    public void removeChangeListener(ItemListener l) {
        changeListeners.remove(l);
    }
}
