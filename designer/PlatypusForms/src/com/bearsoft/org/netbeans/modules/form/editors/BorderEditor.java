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
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.palette.*;
import java.awt.*;
import java.beans.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.explorer.*;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.explorer.view.ListView;
import org.openide.nodes.*;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.NbBundle;

/**
 * A property editor for swing border class.
 *
 * This editor should be in some subpackage under developerx package, but it is
 * not possible now, because this package is only package where are property
 * editors searched.
 */
public final class BorderEditor extends PropertyEditorSupport
        implements FormAwareEditor,
        NamedPropertyEditor,
        BeanPropertyEditor<Border> {

    /**
     * Icon base for unknown border node.
     */
    private static final String UNKNOWN_BORDER_BASE =
            "com/bearsoft/org/netbeans/modules/form/editors/unknownBorder.gif"; // NOI18N
    /**
     * Icon base for no border node.
     */
    private static final String NO_BORDER_BASE =
            "com/bearsoft/org/netbeans/modules/form/editors/nullBorder.gif"; // NOI18N
    private static FormProperty<?>[] EMPTY_PROPERTIES = new FormProperty<?>[0];
    // --------------
    // variables
    private FormPropertyContext propertyContext;
    private NbBorder value;
    // customizer
    private BorderPanel bPanel;

    // --------------
    // init
    public BorderEditor() {
        super();
    }

    // FormAwareEditor implementation
    @Override
    public void setContext(FormModel model, FormProperty<?> property) {
        propertyContext = new FormPropertyContext.SubProperty(property);
    }

    // ------------------
    // main methods
    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object aValue) {
        NbBorder oldValue = value;
        NbBorder newValue;
        if (aValue instanceof NbBorder) {
            newValue = (NbBorder) aValue;
        } else if (aValue != null) {
            if (!(aValue instanceof javax.swing.plaf.UIResource)) {
                newValue = new NbBorder((Border) aValue);
            } else {
                newValue = null;
            }
        } else {
            newValue = null;
        }
        if (oldValue != newValue) {
            value = newValue;
            if (value != null) {
                value.setPropertyContext(propertyContext);
            }
            firePropertyChange();
        }
    }

    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public void setAsText(String string) {
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics g, Rectangle rectangle) {
        String valueText;
        if (value == null) {
            valueText = getBundle().getString("LAB_NoBorder"); // NOI18N
        } else if (value != null) {
            valueText = "[" + value.getDisplayName() + "]"; // NOI18N
        } else {
            valueText = "[" + org.openide.util.Utilities.getShortClassName( // NOI18N
                    value.getClass()) + "]"; // NOI18N
        }

        FontMetrics fm = g.getFontMetrics();
        g.drawString(valueText, rectangle.x,
                rectangle.y + (rectangle.height - fm.getHeight()) / 2 + fm.getAscent());
    }

    @Override
    public String getJavaInitializationString() {
        if (value == null) {
            return "null"; // NOI18N
        }
        if (value != null) {
            return value.getJavaInitializationString();
        }
        // nothing to generate otherwise
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        if (bPanel == null) {
            bPanel = new BorderPanel();
        }
        bPanel.setupNodes();
        return bPanel;
    }

    // ------------------------------------------
    // NamedPropertyEditor implementation
    /**
     * @return display name of the property editor
     */
    @Override
    public String getDisplayName() {
        return getBundle().getString("CTL_BorderEditor_DisplayName"); // NOI18N
    }

    // ----------------
    /**
     * Update the BorderDesignSupport object according to recent changes. This
     * is needed when another border was selected or some property of currently
     * selected border was changed.
     */
    void updateBorder(Node node) {
        if (node instanceof NoBorderNode) {
            value = null;
        } else if (node instanceof UnknownBorderNode) {
            value = new NbBorder(((UnknownBorderNode) node).getBorder());
        } else {
            value = ((BorderNode) node).getBorderSupport();
        }
    }

    // ---------
    private static ResourceBundle getBundle() {
        return NbBundle.getBundle(BorderEditor.class);
    }

    // --------------------------
    // innerclasses
    final class BorderPanel extends JPanel
            implements PropertyChangeListener,
            VetoableChangeListener,
            ExplorerManager.Provider {

        private ExplorerManager manager = new ExplorerManager();
        private Node selectNode = null;

        private BorderPanel() {
            getExplorerManager().addPropertyChangeListener(this);
            getExplorerManager().addVetoableChangeListener(this);

            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(5, 5, 5, 5));

            ResourceBundle bundle = getBundle();

            ListView listView = new ListView();
            listView.getAccessibleContext().setAccessibleDescription(
                    bundle.getString("ACSD_AvailableBorders")); // NOI18N

            JLabel label = new JLabel();
            Mnemonics.setLocalizedText(label, bundle.getString("LAB_AvailableBorders")); // NOI18N
            label.setLabelFor(listView);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(0, 2));
            panel.add(label, BorderLayout.NORTH);
            panel.add(BorderLayout.CENTER, listView);

            PropertySheetView sheetView = new PropertySheetView();

            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            split.setTopComponent(panel);
            split.setBottomComponent(sheetView);
            split.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI());
            split.setBorder(BorderFactory.createEmptyBorder());
            split.setDividerLocation(170);
            split.setContinuousLayout(true);

            add(BorderLayout.CENTER, split);

            getAccessibleContext().setAccessibleDescription(
                    bundle.getString("ACSD_BorderCustomEditor")); // NOI18N
        }

        @Override
        public void addNotify() {
            super.addNotify();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        getExplorerManager().setSelectedNodes(new Node[]{selectNode});
                    } catch (PropertyVetoException e) {
                    } // should not happen            
                }
            });
        }

        void setupNodes() {
            final Node root = new AbstractNode(new Children.Array());
            Node noBorder = new NoBorderNode();
            selectNode = noBorder;

            java.util.List<Node> bordersList = new ArrayList<>(10);
            PaletteItem[] items = PaletteUtils.getAllItems();
            for (int i = 0; i < items.length; i++) {
                PaletteItem paletteItem = items[i];
                if (!paletteItem.isBorder()) {
                    continue;
                }

                NbBorder nodeBDS = null;
                try {
                    // PENDING ClassSource should be used (and project classpath
                    // updated like in RADComponentCreator.prepareClass)
                    // [now not needed - until custom borders are supported]
                    nodeBDS = new NbBorder((Class<Border>) paletteItem.getComponentClass());
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                }
                if (nodeBDS == null) {
                    continue;
                }

                Node borderNode;
                if (value != null
                        && value.getBorderClass() == nodeBDS.getBorderClass()) {
                    try {
                        nodeBDS.setPropertyContext(propertyContext);
                        FormUtils.copyProperties(value.getProperties(),
                                nodeBDS.getProperties(),
                                FormUtils.CHANGED_ONLY | FormUtils.DISABLE_CHANGE_FIRING | FormUtils.DONT_CLONE_VALUES);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                        continue;
                    }
                    borderNode = new BorderNode(nodeBDS, paletteItem.getNode());
                    selectNode = borderNode;
                } else {
                    nodeBDS.setPropertyContext(propertyContext);
                    borderNode = new BorderNode(nodeBDS, paletteItem.getNode());
                }

                bordersList.add(borderNode);
            }

            root.getChildren().add(new Node[]{noBorder});

            Node[] bordersArray = new Node[bordersList.size()];
            bordersList.toArray(bordersArray);
            Arrays.sort(bordersArray, new Comparator<Node>() {
                @Override
                public int compare(Node n1, Node n2) {
                    return n1.getDisplayName().compareTo(
                            n2.getDisplayName());
                }
            });
            root.getChildren().add(bordersArray);
            getExplorerManager().setRootContext(root);
        }

        // track changes in nodes selection
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                if (nodes.length == 1) {
                    updateBorder(nodes[0]);
                }
//                else if (nodes.length == 0) {
//                    try {
//                        getExplorerManager().setSelectedNodes(new Node[] { noBorder });
//                    } 
//                    catch (PropertyVetoException e) {
//                    }
//                }
            }
        }

        // only one border can be selected
        @Override
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                if (nodes.length != 1) {
                    throw new PropertyVetoException("", evt); // NOI18N
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(360, 440);
        }

        @Override
        public ExplorerManager getExplorerManager() {
            return manager;
        }
    }

    final class BorderNode extends FilterNode implements PropertyChangeListener {

        private NbBorder nodeBorder;
        private PropertySet[] properties;

        BorderNode(NbBorder bds, Node paletteItemNode) {
            super(paletteItemNode, Children.LEAF);
            nodeBorder = bds;
        }

        @Override
        public PropertySet[] getPropertySets() {
            if (properties == null) {
                FormProperty<?>[] props = nodeBorder.getProperties();
                Sheet.Set propSet = Sheet.createPropertiesSet();
                propSet.put(props);

                for (int i = 0; i < props.length; i++) {
                    props[i].addPropertyChangeListener(this);
                }

                properties = new PropertySet[]{propSet};
            }
            return properties;
        }

        public NbBorder getBorderSupport() {
            return nodeBorder;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // update the border
            updateBorder(this);
        }
    }

    static final class NoBorderNode extends AbstractNode {

        NoBorderNode() {
            super(Children.LEAF);
            setDisplayName(getBundle().getString("LAB_NoBorder")); // NOI18N
            setIconBaseWithExtension(NO_BORDER_BASE);
        }
    }

    static final class UnknownBorderNode extends AbstractNode {

        private Border border;

        UnknownBorderNode(Border border) {
            super(Children.LEAF);
            setBorder(border);
            setIconBaseWithExtension(UNKNOWN_BORDER_BASE);
        }

        void setBorder(Border border) {
            this.border = border;
            String longName = border.getClass().getName();
            int dot = longName.lastIndexOf('.');
            String shortName = (dot < 0) ? longName : longName.substring(dot + 1);
            setDisplayName(new MessageFormat(
                    getBundle().getString("LAB_FMT_UnknownBorder")) // NOI18N
                    .format(new Object[]{longName, shortName}));
        }

        Border getBorder() {
            return border;
        }
    }

    @Override
    public boolean valueIsBeanProperty() {
        return !isSupportedBorder();
    }

    private boolean isSupportedBorder() {
        if ((value == null) || (value instanceof javax.swing.plaf.UIResource)) {
            // supports also null value - see storeNullBorder()
            return true;
        }
        Class<?> borderClass = value.getBorderClass();
        return borderClass.isAssignableFrom(TitledBorder.class)
                || borderClass.isAssignableFrom(EtchedBorder.class)
                || borderClass.isAssignableFrom(LineBorder.class)
                || borderClass.isAssignableFrom(EmptyBorder.class)
                || borderClass.isAssignableFrom(CompoundBorder.class)
                || SoftBevelBorder.class.isAssignableFrom(borderClass)
                || BevelBorder.class.isAssignableFrom(borderClass)
                || borderClass.isAssignableFrom(MatteBorder.class);
    }

    @Override
    public FormProperty<?>[] getProperties() {
        if ((value == null) || (value instanceof javax.swing.plaf.UIResource)) {
            // supports also null value - see storeNullBorder()
            return EMPTY_PROPERTIES;
        }
        return value.getProperties();
    }

    @Override
    public void intializeFromType(Class<Border> type) throws Exception {
        value = new NbBorder(type);
        value.setPropertyContext(propertyContext);
    }
}
