/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.design.Designable;
import com.eas.script.ScriptFunction;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author mg
 */
public class PlatypusPersistenceManager extends PersistenceManager {

    static final String XML_FORM = "layout"; // NOI18N

    @Override
    public boolean canLoadForm(PlatypusFormDataObject formObject) throws PersistenceException {
        return false;
    }

    public static class PlatypusFrame extends JFrame {

        protected boolean minimizable = true;
        protected boolean maximizable = true;
        protected boolean resizable = true;

        public PlatypusFrame() {
            super();
        }

        @ScriptFunction
        @Override
        public Color getBackground() {
            return super.getBackground();
        }

        @ScriptFunction
        @Override
        public void setBackground(Color bgColor) {
            super.setBackground(bgColor);
        }

        @ScriptFunction
        @Override
        public String getTitle() {
            return super.getTitle();
        }

        @ScriptFunction
        @Override
        public void setTitle(String title) {
            super.setTitle(title);
        }

        @ScriptFunction
        @Override
        public boolean isUndecorated() {
            return super.isUndecorated();
        }

        @ScriptFunction
        @Override
        public void setUndecorated(boolean undecorated) {
            super.setUndecorated(undecorated);
        }

        @ScriptFunction
        @Override
        public float getOpacity() {
            return super.getOpacity();
        }

        @ScriptFunction
        @Override
        public void setOpacity(float opacity) {
            super.setOpacity(opacity);
        }

        @Override
        @Designable(displayName = "icon", description = "Window's icon")
        @ScriptFunction
        public Image getIconImage() {
            return super.getIconImage();
        }

        @ScriptFunction
        public boolean isMinimizable() {
            return minimizable;
        }

        @ScriptFunction
        public void setMinimizable(boolean aValue) {
            minimizable = aValue;
        }

        @ScriptFunction
        public boolean isMaximizable() {
            return maximizable;
        }

        @ScriptFunction
        public void setMaximizable(boolean aValue) {
            maximizable = aValue;
        }

        @ScriptFunction
        @Override
        public boolean isResizable() {
            return super.isResizable();
        }

        @ScriptFunction
        @Override
        public void setResizable(boolean resizable) {
            super.setResizable(resizable);
        }

        @ScriptFunction
        @Override
        public int getWidth() {
            return super.getWidth();
        }

        @ScriptFunction
        public void setWidth(int aValue) {
            super.setSize(new Dimension(aValue, getHeight()));
        }

        @ScriptFunction
        @Override
        public int getHeight() {
            return super.getHeight();
        }

        @ScriptFunction
        public void setHeight(int aValue) {
            super.setSize(new Dimension(getWidth(), aValue));
        }
    }

    @Override
    public void loadForm(PlatypusFormDataObject formObject, FormModel formModel, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            formModel.setFormBaseClass(PlatypusFrame.class); //Only for design purposes. At runtime it will be any of swing top-level containers
            formModel.setName(formObject.getName());
            RADComponent<?> topComp = formModel.getTopRADComponent();
            assert topComp instanceof RADVisualFormContainer;
            RADVisualFormContainer formComp = (RADVisualFormContainer) topComp;
        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public void saveForm(PlatypusFormDataObject formObject, FormModel formModel, List<Throwable> nonfatalErrors) throws PersistenceException {
    }
}
