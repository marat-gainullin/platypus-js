/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.wrappers;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mg
 */
public class PlatypusCardLayout extends CardLayout {

    protected Map<String, Component> comps = new HashMap<>();

    public PlatypusCardLayout(int hgap, int vgap) {
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
}
