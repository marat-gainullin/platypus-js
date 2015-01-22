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

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.*;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * A JPanel subclass holding components presented in FormDesigner (contains also
 * the resizable border and the white area around). Technically, this is a layer
 * in FormDesigner, placed under HandleLayer.
 *
 * ComponentLayer +- DesignerPanel +- ComponentContainer +- top visual component
 * of the designed form (by VisualReplicator) +- subcomponents of the designed
 * form +- ...
 *
 * @author Tomas Pavek
 */
class ComponentLayer extends JPanel {

    private static final int HORIZONTAL_MARGIN = 10;
    private static final int VERTICAL_MARGIN = 10;
    private static final Color BLACK_TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
    private static final FormLoaderSettings formSettings = FormLoaderSettings.getInstance();
    /**
     * The container holding the top visual component of the form.
     */
    private JPanel componentContainer;
    /**
     * A panel (with a resizable border) positioning the component container in
     * the whole ComponentLayer area.
     */
    private final DesignerPanel designerPanel;

    ComponentLayer(FormModel aFormModel) {
        componentContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color oldColor = g.getColor();
                try {
                    g.setColor(formSettings.getGuidingLineColor());
                    Dimension size = getSize();
                    for (int x = 0; x <= size.width; x += formSettings.getGridX()) {
                        for (int y = 0; y <= size.height; y += formSettings.getGridY()) {
                            g.drawRect(x, y, 0, 0);
                        }
                    }
                } finally {
                    g.setColor(oldColor);
                }
            }
        };
        componentContainer.setLayout(new BorderLayout());
        componentContainer.setFont(FormUtils.getDefaultAWTFont());

        designerPanel = new DesignerPanel(aFormModel);
        designerPanel.setLayout(new BorderLayout());
        designerPanel.add(componentContainer, BorderLayout.CENTER);

        setLayout(new FlowLayout(FlowLayout.LEFT,
                HORIZONTAL_MARGIN,
                VERTICAL_MARGIN));
        add(designerPanel);
        updateBackground();
    }

    Container getComponentContainer() {
        return componentContainer;
    }

    Rectangle getDesignerInnerBounds() {
        Rectangle r = new Rectangle(designerPanel.getSize());
        Insets i = designerPanel.getInsets();
        r.x = HORIZONTAL_MARGIN + i.left;
        r.y = VERTICAL_MARGIN + i.top;
        return r;
    }

    Rectangle getDesignerOuterBounds() {
        return designerPanel.getBounds();
    }

    Insets getDesignerOutsets() {
        return designerPanel.getInsets();
    }

    protected void ensureBackgroundOpaque(JComponent aComponent) {
        if (aComponent instanceof JRootPane) {
            if(((JRootPane) aComponent).isBackgroundSet())
                componentContainer.setBackground(((JRootPane) aComponent).getBackground());
            else
                componentContainer.setBackground(FormLoaderSettings.getInstance().getFormDesignerBackgroundColor());
            ((JRootPane) aComponent).setOpaque(false);
            ((JComponent) ((JRootPane) aComponent).getContentPane()).setOpaque(false);
            ((JRootPane) aComponent).setBackground(BLACK_TRANSPARENT_COLOR);
            ((JComponent) ((JRootPane) aComponent).getContentPane()).setBackground(BLACK_TRANSPARENT_COLOR);
        } else {
            if(aComponent.isBackgroundSet())
                componentContainer.setBackground(aComponent.getBackground());
            else
                componentContainer.setBackground(FormLoaderSettings.getInstance().getFormDesignerBackgroundColor());
            aComponent.setOpaque(false);
            aComponent.setBackground(BLACK_TRANSPARENT_COLOR);
        }
    }
    protected PropertyChangeListener backgroundOpaqueWatchdog = new PropertyChangeListener() {
        protected boolean processing;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!processing && (evt == null || ("opaque".equals(evt.getPropertyName()) || "background".equals(evt.getPropertyName()))) && componentContainer.getComponentCount() > 0 && componentContainer.getComponent(0) instanceof JComponent) {
                processing = true;
                try {
                    JComponent topChild = (JComponent) componentContainer.getComponent(0);
                    ensureBackgroundOpaque(topChild);
                } finally {
                    processing = false;
                }
            }
        }
    };

    void setTopDesignComponent(JComponent newComponent) {
        JComponent oldComponent = null;
        if (componentContainer.getComponentCount() > 0 && componentContainer.getComponent(0) instanceof JComponent) {
            oldComponent = (JComponent) componentContainer.getComponent(0);
        }
        if (oldComponent != null) {
            oldComponent.removePropertyChangeListener(backgroundOpaqueWatchdog);
        }
        componentContainer.removeAll();
        if (newComponent != null) {
            componentContainer.add(newComponent, BorderLayout.CENTER);
            newComponent.addPropertyChangeListener(backgroundOpaqueWatchdog);
            backgroundOpaqueWatchdog.propertyChange(null);
        }
    }

    void updateVisualSettings() {
        updateBackground();
        designerPanel.updateBorder();
    }

    private void updateBackground() {
        setBackground(FormLoaderSettings.getInstance().getFormDesignerBackgroundColor());
    }

    // ---------
    private static class DesignerPanel extends JPanel {

        private static final int BORDER_THICKNESS = 4; // [could be changeable]
        private final FormModel formModel;

        DesignerPanel(FormModel aModel) {
            super();
            formModel = aModel;
            updateBorder();
        }

        final void updateBorder() {
            setBorder(new javax.swing.border.LineBorder(
                    FormLoaderSettings.getInstance().getFormDesignerBorderColor(),
                    BORDER_THICKNESS));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = new Dimension(formModel.getTopDesignComponent().getBeanInstance().getSize());
            Insets insets = getInsets();
            size.width += insets.left + insets.right;
            size.height += insets.top + insets.bottom;
            return size;
        }

        @Override
        public void paint(Graphics g) {
            try {
                FormLAF.setUseDesignerDefaults(formModel);
                super.paint(g);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                // Issue 68776
                String msg = NbBundle.getMessage(ComponentLayer.class, "MSG_Paiting_Exception"); // NOI18N
                msg = "<html><b>" + msg + "</b><br><br>"; // NOI18N
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                msg += sw.toString().replaceAll("\n", "<br>"); // NOI18N
                Insets insets = getInsets();
                JLabel label = new JLabel(msg);
                label.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
                label.setOpaque(true);
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setSize(getWidth() - (insets.left + insets.top),
                        getHeight() - (insets.top + insets.bottom));
                Shape oldClip = g.getClip();
                Rectangle newClip = new Rectangle(insets.left, insets.top, label.getWidth(), label.getHeight());
                Rectangle clipBounds = g.getClipBounds();
                if (clipBounds != null) {
                    newClip = newClip.intersection(clipBounds);
                }
                g.setClip(newClip);
                g.translate(insets.left, insets.top);
                label.paint(g);
                g.translate(-insets.left, -insets.top);
                g.setClip(oldClip);
            } finally {
                FormLAF.setUseDesignerDefaults(null);
            }
        }
    }
}
