/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public class ButtonGroupWrapper extends JComponent {

    protected ItemListener itemListener = (ItemEvent e) -> {
        fireItemSelected(e);
    };
    protected ButtonGroup group;
    protected Set<ItemListener> itemListeners = new HashSet<>();

    public ButtonGroupWrapper() {
        super();
        group = new ButtonGroup();
    }

    public ButtonGroupWrapper(ButtonGroup aGroup) {
        super();
        group = aGroup;
    }

    public void addItemListener(ItemListener l) {
        itemListeners.add(l);
    }

    public void removeItemListener(ItemListener l) {
        itemListeners.remove(l);
    }

    protected void fireItemSelected(ItemEvent e) {
        if (e.getItem() instanceof AbstractButton) {
            AbstractButton ab = (AbstractButton) e.getItem();
            if (ab.isSelected()) {
                itemListeners.stream().forEach((l) -> {
                    l.itemStateChanged(e);
                });
            }
        }
    }

    @Override
    public Component add(Component comp, int index) {
        if (comp instanceof AbstractButton) {
            group.add((AbstractButton) comp);
            ((AbstractButton) comp).addItemListener(itemListener);
        }
        return comp;
    }

    @Override
    public Component add(Component comp) {
        return add(comp, 0);
    }

    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, 0);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, 0);
    }

    @Override
    public void remove(Component comp) {
        if (comp instanceof AbstractButton) {
            ((AbstractButton) comp).removeItemListener(itemListener);
            group.remove((AbstractButton) comp);
        }
        super.remove(comp);
    }

    @Override
    public void remove(int index) {
        if (index >= 0 && index < group.getButtonCount()) {
            remove(Collections.list(group.getElements()).get(index));
        } else {
            super.remove(index);
        }
    }

    @Override
    public void removeAll() {
        Collections.list(group.getElements()).stream().forEach((ab) -> {
            group.remove(ab);
        });
    }

    @Override
    public int getComponentCount() {
        return Collections.list(group.getElements()).size();
    }

    @Override
    public Component getComponent(int index) {
        if (index >= 0 && index < group.getButtonCount()) {
            return Collections.list(group.getElements()).get(index);
        } else {
            return null;
        }
    }

    public void clearSelection() {
        group.clearSelection();
    }
}
