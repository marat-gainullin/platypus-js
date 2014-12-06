/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.design.Designable;
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

        public PlatypusFrame() {
            super();
        }

        @Override
        @Designable(displayName = "icon", description = "Window's icon")
        public Image getIconImage() {
            return super.getIconImage();
        }

        public boolean isMinimizable() {
            return minimizable;
        }

        public void setMinimizable(boolean aValue) {
            minimizable = aValue;
        }

        public boolean isMaximizable() {
            return maximizable;
        }

        public void setMaximizable(boolean aValue) {
            maximizable = aValue;
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
