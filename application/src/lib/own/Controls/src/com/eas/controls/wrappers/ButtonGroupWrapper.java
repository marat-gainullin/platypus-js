/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.wrappers;

import java.awt.Component;
import java.util.Collections;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public class ButtonGroupWrapper extends JComponent {

    protected ButtonGroup group;

    public ButtonGroupWrapper() {
        super();
        group = new ButtonGroup();
    }

    public ButtonGroupWrapper(ButtonGroup aGroup) {
        super();
        group = aGroup;
    }

    @Override
    public Component add(Component comp, int index) {
        if (comp instanceof AbstractButton) {
            group.add((AbstractButton) comp);
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
