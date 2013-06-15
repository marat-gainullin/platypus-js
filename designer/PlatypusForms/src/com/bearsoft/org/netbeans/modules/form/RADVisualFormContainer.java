/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;

/**
 * RADVisualFormContainer represents the top-level container of the form and the
 * form itself during design time.
 *
 * @author Ian Formanek
 */
public class RADVisualFormContainer extends RADVisualContainer<Container> {

    public static final String PROP_FORM_SIZE = "formSize"; // NOI18N
    public static final String PROP_FORM_POSITION = "formPosition"; // NOI18N
    public static final String PROP_GENERATE_POSITION = "generatePosition"; // NOI18N
    public static final String PROP_GENERATE_SIZE = "generateSize"; // NOI18N
    public static final String PROP_GENERATE_CENTER = "generateCenter"; // NOI18N
    public static final String FORM_NAME = "Form";
    // Synthetic properties of form
    private Dimension designerSize;
    private Dimension formSize;// = new Dimension(FormEditor.DEFAULT_FORM_WIDTH, FormEditor.DEFAULT_FORM_HEIGHT);
    private Point formPosition;
    private boolean generatePosition = true;
    private boolean generateSize = true;
    private boolean generateCenter = true;

    public RADVisualFormContainer() {
        super();
    }

    // ------------------------------------------------------------------------------
    // Form synthetic properties
    /**
     * Getter for the Name property of the component - overriden to provide
     * non-null value, as the top-level component does not have a variable
     *
     * @return current value of the Name property
     */
    @Override
    public String getName() {
        return FORM_NAME;// special name for top-level container. It's no i18n because of references in the form.
    }

    /**
     * Setter for the Name property of the component - usually maps to variable
     * declaration for holding the instance of the component
     *
     * @param value new value of the Name property
     */
    @Override
    public void setName(String value) {
        // noop in forms
    }

    @Override
    protected RADProperty<?> createBeanProperty(PropertyDescriptor desc, Object[] propAccessClsf, Object[] propParentChildDepClsf) {
        if (!"visible".equals(desc.getName())) {
            return super.createBeanProperty(desc, propAccessClsf, propParentChildDepClsf);
        } else {
            return null;
        }
    }

    public Point getFormPosition() {
        if (formPosition == null) {
            formPosition = new Point(0, 0);//topContainer.getLocation();
        }
        return formPosition;
    }

    public void setFormPosition(Point value) {
        Object old = formPosition;
        formPosition = value;
        getFormModel().fireSyntheticPropertyChanged(this, PROP_FORM_POSITION,
                old, value);
    }

    public Dimension getFormSize() {
        return formSize;
    }

    public void setFormSize(Dimension value) {
        Dimension old = setFormSizeImpl(value);

        // this is called when the property is enabled for writing (i.e. policy
        // is GEN_BOUNDS), but also when loading form (when policy might be not
        // set yet) - so always propagate to designer size
        Dimension ldesignerSize;
        if (getBeanInstance() instanceof Dialog
                || getBeanInstance() instanceof Frame) {
            Dimension diffDim = getWindowContentDimensionDiff();
            ldesignerSize = new Dimension(value.width - diffDim.width,
                    value.height - diffDim.height);
        } else {
            ldesignerSize = value;
        }
        setDesignerSizeImpl(ldesignerSize);
        getFormModel().fireSyntheticPropertyChanged(this, PROP_FORM_SIZE, old, value);
        getFormModel().fireSyntheticPropertyChanged(this, PlatypusFormLayoutView.PROP_DESIGNER_SIZE, null, null);
    }

    private Dimension setFormSizeImpl(Dimension value) {
        Dimension old = formSize;
        formSize = value;
        if (getNodeReference() != null) { // propagate the change to node
            getNodeReference().firePropertyChangeHelper(PROP_FORM_SIZE, old, value);
        }
        return old;
    }

    public Dimension getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(Dimension value) {
        Dimension old = setDesignerSizeImpl(value);
        Dimension lformSize;
        if (getBeanInstance() instanceof Dialog
                || getBeanInstance() instanceof Frame) {
            Dimension diffDim = getWindowContentDimensionDiff();
            lformSize = new Dimension(value.width + diffDim.width,
                    value.height + diffDim.height);
        } else {
            lformSize = value;
        }
        setFormSizeImpl(lformSize);
        getFormModel().fireSyntheticPropertyChanged(this, PlatypusFormLayoutView.PROP_DESIGNER_SIZE, old, value);
    }

    Dimension setDesignerSizeImpl(Dimension value) {
        Dimension old = designerSize;
        designerSize = value;
        if (getNodeReference() != null) { // propagate the change to node
            getNodeReference().firePropertyChangeHelper(PlatypusFormLayoutView.PROP_DESIGNER_SIZE, old, value);
        }
        return old;
    }

    public boolean getGeneratePosition() {
        return generatePosition;
    }

    public void setGeneratePosition(boolean value) {
        boolean old = generatePosition;
        generatePosition = value;
        getFormModel().fireSyntheticPropertyChanged(this, PROP_GENERATE_POSITION,
                old ? Boolean.TRUE : Boolean.FALSE, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public boolean getGenerateSize() {
        return generateSize;
    }

    public void setGenerateSize(boolean value) {
        boolean old = generateSize;
        generateSize = value;
        getFormModel().fireSyntheticPropertyChanged(this, PROP_GENERATE_SIZE,
                old ? Boolean.TRUE : Boolean.FALSE, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public boolean getGenerateCenter() {
        return generateCenter;
    }

    public void setGenerateCenter(boolean value) {
        boolean old = generateCenter;
        generateCenter = value;
        getFormModel().fireSyntheticPropertyChanged(this, PROP_GENERATE_CENTER,
                old ? Boolean.TRUE : Boolean.FALSE, value ? Boolean.TRUE : Boolean.FALSE);
    }

    // ---------
    // providing the difference of the whole frame/dialog size and the size
    // of the content pane
    private static Dimension windowContentDimensionDiff;

    public Dimension getWindowContentDimensionDiff() {
        boolean undecorated = true;
        Object beanInstance = getBeanInstance();
        if (beanInstance instanceof java.awt.Frame) {
            undecorated = ((java.awt.Frame) beanInstance).isUndecorated();
        } else if (beanInstance instanceof java.awt.Dialog) {
            undecorated = ((java.awt.Dialog) beanInstance).isUndecorated();
        }
        return undecorated ? new Dimension(0, 0) : getDecoratedWindowContentDimensionDiff();
    }

    public static Dimension getDecoratedWindowContentDimensionDiff() {
        if (windowContentDimensionDiff == null) {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            frame.pack();
            Dimension d1 = frame.getSize();
            Dimension d2 = frame.getRootPane().getSize();
            windowContentDimensionDiff =
                    new Dimension(d1.width - d2.width, d1.height - d2.height);
        }
        return windowContentDimensionDiff;
    }

    @Override
    void setNodeReference(RADComponentNode node) {
        super.setNodeReference(node);
        if (node != null) {
            Object beanInstance = getBeanInstance();
            if ((beanInstance instanceof java.awt.Frame)
                    || (beanInstance instanceof java.awt.Dialog)) {
                // undecorated is not a bound property => it is not possible to
                // listen on the beanInstance => we have to listen on the node
                node.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("undecorated".equals(evt.getPropertyName())) { // NOI18N
                            // Keep current designer size and force update of form size
                            setDesignerSize(getDesignerSize());
                        }
                    }
                });
            }
        }
    }

    // ------------------------------------------------------------------------------------------
    // Innerclasses
    final public static class SizePolicyEditor extends java.beans.PropertyEditorSupport {

        /**
         * Display Names for alignment.
         */
        private static final String[] names = {
            FormUtils.getBundleString("VALUE_sizepolicy_full"), // NOI18N
            FormUtils.getBundleString("VALUE_sizepolicy_pack"), // NOI18N
            FormUtils.getBundleString("VALUE_sizepolicy_none"), // NOI18N
        };

        /**
         * @return names of the possible directions
         */
        @Override
        public String[] getTags() {
            return names;
        }

        /**
         * @return text for the current value
         */
        @Override
        public String getAsText() {
            int value = ((Integer) getValue()).intValue();
            return names[value];
        }

        /**
         * Setter.
         *
         * @param str string equal to one value from directions array
         */
        @Override
        public void setAsText(String str) {
            if (names[0].equals(str)) {
                setValue(new Integer(0));
            } else if (names[1].equals(str)) {
                setValue(new Integer(1));
            } else if (names[2].equals(str)) {
                setValue(new Integer(2));
            }
        }
    }
}
