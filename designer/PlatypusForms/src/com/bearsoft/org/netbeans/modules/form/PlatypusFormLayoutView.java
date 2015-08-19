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

import com.bearsoft.org.netbeans.modules.form.assistant.AssistantModel;
import com.bearsoft.org.netbeans.modules.form.assistant.AssistantView;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstants;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportManager;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutOperations;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport.MarginLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.menu.MenuEditLayer;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.designer.explorer.PlatypusDataObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.spi.palette.PaletteController;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.Utilities;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

/**
 * This is a TopComponent subclass holding the form designer. It consist of two
 * layers - HandleLayer (responsible for interaction with user) and
 * ComponentLayer (presenting the components, not accessible to the user).
 *
 * PlatypusFormLayoutView +- AssistantView +- JScrollPane +- JLayeredPane +-
 * HandleLayer +- ComponentLayer
 *
 * @author Tran Duc Trung, Tomas Pavek, Josef Kozak
 */
@TopComponent.Description(preferredID = "platypus-layout-view", persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
public class PlatypusFormLayoutView extends CloneableTopComponent implements MultiViewElement {

    // UI components composition
    private transient JLayeredPane layeredPane;
    private transient ComponentLayer componentLayer;
    private transient HandleLayer handleLayer;
    private transient FormToolBar formToolBar;
    // in-place editing
    private transient InPlaceEditLayer textEditLayer;
    private transient FormProperty<String> editedProperty;
    private transient InPlaceEditLayer.FinishListener finnishListener;
    private transient MenuEditLayer menuEditLayer;
    // metadata
    private transient FormModel formModel;
    private transient FormModelListener formModelListener;
    private transient RADVisualContainer<?> topDesignComponent;
    private transient FormEditor formEditor;
    // layout visualization and interaction
    private transient final List<RADComponent<?>> selectedComponents = new ArrayList<>();
    private transient final List<RADVisualComponent<?>> selectedLayoutComponents = new ArrayList<>();
    private transient VisualReplicator replicator;
    private transient List<Action> alignActions;
    private transient List<Action> resizabilityActions;
    private transient JToggleButton[] resizabilityButtons;
    private transient List<Action> anchorActions;
    private transient JToggleButton[] anchorButtons;
    private transient int designerMode;
    public static final int MODE_SELECT = 0;
    public static final int MODE_ADD = 2;
    private transient boolean initialized = false;
    transient MultiViewElementCallback multiViewObserver;
    private transient ExplorerManager explorerManager;
    private transient AssistantView assistantView;
    private transient PreferenceChangeListener settingsListener;
    private transient PropertyChangeListener paletteListener;

    private static final String SAVE_ACTION_KEY = "save";

    public PlatypusFormLayoutView() {
        super();
    }

    PlatypusFormLayoutView(FormEditor aFormEditor) {
        this();
        setFormEditor(aFormEditor);
    }

    protected final void setFormEditor(FormEditor aFormEditor) {
        setIcon(aFormEditor.getFormDataObject().getNodeDelegate().getIcon(java.beans.BeanInfo.ICON_COLOR_16x16));
        setDisplayName(aFormEditor.getFormDataObject().getPrimaryFile().getName());
        setLayout(new BorderLayout());

        FormLoaderSettings settings = FormLoaderSettings.getInstance();
        Color backgroundColor = settings.getFormDesignerBackgroundColor();
        Color borderColor = settings.getFormDesignerBorderColor();

        JPanel loadingPanel = new JPanel();
        loadingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12 + (settings.getAssistantShown() ? 40 : 0)));
        loadingPanel.setBackground(backgroundColor);
        JLabel loadingLbl = new JLabel(FormUtils.getBundleString("LBL_FormLoading")); // NOI18N
        loadingLbl.setOpaque(true);
        loadingLbl.setPreferredSize(new Dimension(410, 310));
        loadingLbl.setHorizontalAlignment(SwingConstants.CENTER);
        loadingPanel.add(loadingLbl);
        loadingLbl.setBorder(new CompoundBorder(new LineBorder(borderColor, 5), new EmptyBorder(new Insets(6, 6, 6, 6))));
        add(loadingPanel, BorderLayout.CENTER);

        formEditor = aFormEditor;

        // add PlatypusFormDataObject to lookup so it can be obtained from multiview TopComponent
        final PlatypusDataObject formDataObject = formEditor.getFormDataObject();
        ActionMap map = FormInspector.getInstance().setupActionMap(getActionMap());
        explorerManager = new ExplorerManager();
        associateLookup(new ProxyLookup(new Lookup[]{
            ExplorerUtils.createLookup(explorerManager, map),
            PaletteUtils.getPaletteLookup(formDataObject.getPrimaryFile())}));
        formToolBar = new FormToolBar(this);
        setMinimumSize(new Dimension(10, 10));

        InputMap iMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap aMap = getActionMap();
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), SAVE_ACTION_KEY);
        aMap.put(SAVE_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    formModel.getDataObject().getLookup().lookup(SaveCookie.class).save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    void initialize() {
        if (!initialized) {
            initialized = true;
            removeAll();

            formModel = formEditor.getFormModel();

            componentLayer = new ComponentLayer(formModel);
            handleLayer = new HandleLayer(this);
            JPanel designPanel = new JPanel(new BorderLayout());
            designPanel.add(componentLayer, BorderLayout.CENTER);
            layeredPane = new JLayeredPane() {
                // hack: before each paint make sure the dragged components have
                // bounds set out of visible area (as they physically stay in their
                // container and the layout manager may lay them back if some
                // validation occurs)
                @Override
                protected void paintChildren(Graphics g) {
                    handleLayer.maskDraggingComponents();
                    super.paintChildren(g);
                }
            };
            layeredPane.setLayout(new OverlayLayout(layeredPane));
            layeredPane.add(designPanel, new Integer(1000));
            layeredPane.add(handleLayer, new Integer(1001));
            updateAssistant();
            JScrollPane scrollPane = new JScrollPane(layeredPane);
            scrollPane.setBorder(null); // disable border, winsys will handle borders itself
            scrollPane.setViewportBorder(null); // disable also GTK L&F viewport border 
            scrollPane.getVerticalScrollBar().setUnitIncrement(5); // Issue 50054
            scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
            add(scrollPane, BorderLayout.CENTER);
            if (formEditor.getFormDataObject() instanceof PlatypusLayoutDataObject) {
                add(formToolBar, BorderLayout.NORTH);
            }
            explorerManager.setRootContext(formEditor.getFormRootNode());
            if (formModelListener == null) {
                formModelListener = new FormListener();
            }
            formModel.addFormModelListener(formModelListener);

            replicator = new VisualReplicator(formEditor, true);

            resetTopDesignComponent(false);
            handleLayer.setViewOnly(formModel.isReadOnly());

            updateWholeDesigner();
            //force the menu edit layer to be created
            getMenuEditLayer();
            //force the text edit layer to be created
            getInPlaceEditLayer();

            // vlv: print
            designPanel.putClientProperty("print.printable", Boolean.TRUE); // NOI18N
            attachSettingsListener();
            attachPaletteListener();

            FormInspector inspector = FormInspector.getInstance();
            inspector.focusForm(this);
            // Issue 137741
            RADVisualComponent<?> topRadComp = formModel.getTopRADComponent();
            if (topRadComp == null) {
                try {
                    inspector.setSelectedNodes(new Node[]{formEditor.getFormRootNode()}, this);
                } catch (PropertyVetoException pvex) {
                }
            } else {
                setSelectedComponent(topRadComp);
            }
        }
    }

    void reset(FormEditor aFormEditor) {
        if (menuEditLayer != null) {
            menuEditLayer.hideMenuLayer();
            menuEditLayer = null;
        }
        if (initialized) {
            clearSelection();
            explorerManager.setRootContext(new AbstractNode(Children.LEAF));
        }
        initialized = false;
        removeAll();
        componentLayer = null;
        handleLayer = null;
        layeredPane = null;
        if (textEditLayer != null) {
            if (textEditLayer.isVisible()) {
                textEditLayer.finishEditing(false);
            }
            textEditLayer.removeFinishListener(getFinishListener());
            textEditLayer = null;
        }
        if (formModel != null) {
            if (formModelListener != null) {
                formModel.removeFormModelListener(formModelListener);
            }
            detachSettingsListener();
            detachPaletteListener();
            topDesignComponent = null;
            formModel = null;
        }
        replicator = null;
        formEditor = aFormEditor;
    }

    private void updateAssistant() {
        if (formEditor.getFormDataObject() instanceof PlatypusFormDataObject) {
            if (FormLoaderSettings.getInstance().getAssistantShown()) {
                AssistantModel assistant = formModel.getAssistantModel();
                assistantView = new AssistantView(assistant);
                assistant.setContext("select"); // NOI18N
                add(assistantView, BorderLayout.NORTH);
            } else {
                if (assistantView != null) {
                    remove(assistantView);
                    assistantView = null;
                }
            }
            revalidate();
        }
    }

    // ------
    // important getters
    public FormModel getFormModel() {
        return formModel;
    }

    public HandleLayer getHandleLayer() {
        return handleLayer;
    }

    ComponentLayer getComponentLayer() {
        return componentLayer;
    }

    FormToolBar getFormToolBar() {
        return formToolBar;
    }

    public FormEditor getFormEditor() {
        return formEditor;
    }

    @Override
    public javax.swing.Action[] getActions() {
        List<Action> actions = new ArrayList<>(Arrays.asList(super.getActions()));
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Editors/TabActions"));
        return actions.toArray(new javax.swing.Action[]{});
    }

    // ------------
    // designer content
    public JComponent getComponent(RADComponent<?> radComp) {
        Object replica = getReplicant(radComp.getName());
        return replica instanceof JComponent ? (JComponent) replica : null;
    }

    public Object getReplicant(String aComponentName) {
        return replicator.getClonedComponent(aComponentName);
    }

    public RADComponent<?> getRadComponent(Component comp) {
        String id = replicator.getClonedComponentId(comp);
        return id != null ? formModel.getRADComponent(id) : null;
    }

    public RADVisualComponent<?> getTopDesignComponent() {
        return topDesignComponent;
    }

    boolean isTopRADComponent() {
        RADComponent<?> topRadComp = formModel.getTopRADComponent();
        return topRadComp != null && topRadComp == topDesignComponent;
    }

    public void setTopDesignComponent(RADVisualContainer<?> component, boolean update) {
        highlightTopDesignComponentName(false);
        // TODO need to remove bindings of the current cloned view (or clone bound components as well)
        topDesignComponent = component;
        highlightTopDesignComponentName(!isTopRADComponent());
        if (formEditor.getFormDataObject() instanceof PlatypusFormDataObject) {
            formEditor.getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).updateTitles();
        }
        if (update) {
            setSelectedComponent(topDesignComponent);
            updateWholeDesigner();
        }
    }

    private void highlightTopDesignComponentName(boolean bl) {
        if (topDesignComponent != null) {
            RADComponentNode node = topDesignComponent.getNodeReference();
            if (node != null) {
                node.highlightDisplayName(bl);
            }
        }
    }

    public void resetTopDesignComponent(boolean update) {
        RADComponent<?> top = formModel.getTopRADComponent();
        setTopDesignComponent(top instanceof RADVisualFormContainer ? (RADVisualFormContainer) top : null,
                update);
    }

    /**
     * Tests whether top designed container is some parent of given component
     * (whether the component is in the tree under top designed container).
     *
     * @param radComp component.
     * @return <code>true</code> if the component is in designer, * * * *
     * returns <code>false</code> otherwise.
     */
    public boolean isInDesigner(RADVisualComponent<?> radComp) {
        if (replicator != null) {
            Object comp = replicator.getClonedComponent(radComp);
            return comp instanceof Component ? componentLayer.isAncestorOf((Component) comp) : false;
        } else {
            return false;
        }
    }

    void updateWholeDesigner() {
        if (formModelListener != null) {
            formModelListener.formChanged(null);
        }
    }

    // updates layout of a container in designer to match current model - used
    // by HandleLayer when canceling component dragging
    void updateContainerLayout(RADVisualContainer<?> radCont) {
        replicator.updateContainerLayout(radCont);
        componentLayer.revalidate();
        componentLayer.repaint();
    }

    private void attachSettingsListener() {
        if (settingsListener == null) {
            settingsListener = (PreferenceChangeEvent evt) -> {
                String propName = evt.getKey();
                switch (propName) {
                    case FormLoaderSettings.PROP_ASSISTANT_SHOWN:
                        updateAssistant();
                        break;
                    case FormLoaderSettings.PROP_SELECTION_BORDER_SIZE:
                    case FormLoaderSettings.PROP_SELECTION_BORDER_COLOR:
                    case FormLoaderSettings.PROP_CONNECTION_BORDER_COLOR:
                    case FormLoaderSettings.PROP_FORMDESIGNER_BACKGROUND_COLOR:
                    case FormLoaderSettings.PROP_FORMDESIGNER_BORDER_COLOR: {
                        updateVisualSettings();
                        break;
                    }
                    case FormLoaderSettings.PROP_PALETTE_IN_TOOLBAR: {
                        getFormToolBar().showPaletteButton(FormLoaderSettings.getInstance().isPaletteInToolBar());
                        break;
                    }
                    case FormLoaderSettings.PROP_GRID_X:
                    case FormLoaderSettings.PROP_GRID_Y:
                        updateVisualSettings();
                        break;
                }
            };
            FormLoaderSettings.getPreferences().addPreferenceChangeListener(settingsListener);
        }
    }

    private void detachSettingsListener() {
        if (settingsListener != null) {
            FormLoaderSettings.getPreferences().removePreferenceChangeListener(settingsListener);
            settingsListener = null;
        }
    }

    private void attachPaletteListener() {
        if (paletteListener == null) {
            paletteListener = (PropertyChangeEvent evt) -> {
                if (PaletteController.PROP_SELECTED_ITEM.equals(evt.getPropertyName())) {
                    if (formModel != null && formModel.isFormLoaded() && !formModel.isReadOnly()) {
                        // PENDING should be done for all cloned designers
                        if (evt.getNewValue() == null) {
                            if (getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                                setDesignerMode(PlatypusFormLayoutView.MODE_SELECT);
                            }
                        } else {
                            if (getDesignerMode() == PlatypusFormLayoutView.MODE_ADD) {
                                // Change in the selected palette item means unselection
                                // of the old item and selection of the new one
                                setDesignerMode(PlatypusFormLayoutView.MODE_SELECT);
                            }
                            setDesignerMode(PlatypusFormLayoutView.MODE_ADD);
                        }
                    }
                }
            };
            PaletteUtils.addPaletteListener(paletteListener, formModel.getDataObject().getPrimaryFile());
        }
    }

    private void detachPaletteListener() {
        if (paletteListener != null) {
            PaletteUtils.removePaletteListener(paletteListener, formModel.getDataObject().getPrimaryFile());
            paletteListener = null;
        }
    }

    /**
     * Данный метод предназначен для использования вне дизайнера форм.
     *
     * @param radComp
     * @param aFormEditor
     * @param previewInfo
     * @return
     * @throws Exception
     */
    public static Container createFormView(final RADComponent<?> radComp, final FormEditor aFormEditor, final FormLAF.PreviewInfo previewInfo)
            throws Exception {
        Container result = null;
        FormModel formModel = radComp.getFormModel();
        final ClassLoader classLoader = Lookup.getDefault().lookup(ClassLoader.class);
        try {
            FormLAF.setUsePreviewDefaults(classLoader, previewInfo);
            result = FormLAF.<Container>executeWithLookAndFeel(formModel,
                    new Mutex.ExceptionAction<Container>() {
                        @Override
                        public Container run() throws Exception {
                            VisualReplicator r = new VisualReplicator(aFormEditor, false);
                            Container rootView = (Container) r.createClone();
                            Container container = new JFrame();
                            container.add(rootView);
                            if (container instanceof RootPaneContainer) {
                                JRootPane rootPane = ((RootPaneContainer) container).getRootPane();
                                JLayeredPane newPane = new JLayeredPane() {
                                    @Override
                                    public void paint(Graphics g) {
                                        try {
                                            FormLAF.setUsePreviewDefaults(classLoader, previewInfo);
                                            super.paint(g);
                                        } finally {
                                            FormLAF.setUsePreviewDefaults(null, null);
                                        }
                                    }
                                };
                                // Copy components from the original layered pane into our one
                                JLayeredPane oldPane = rootPane.getLayeredPane();
                                Component[] comps = oldPane.getComponents();
                                for (Component comp : comps) {
                                    newPane.add(comp, Integer.valueOf(oldPane.getLayer(comp)));
                                }
                                // Use our layered pane that knows about LAF switching
                                rootPane.setLayeredPane(newPane);
                                // Make the glass pane visible to force repaint of the whole layered pane
                                rootPane.getGlassPane().setVisible(true);
                                // Mark it as design preview
                                rootPane.putClientProperty("designPreview", Boolean.TRUE); // NOI18N
                            } // else AWT Frame - we don't care that the L&F of the Swing
                            // components may not look good - it is a strange use case
                            return container;
                        }
                    });
        } finally {
            FormLAF.setUsePreviewDefaults(null, null);
        }
        return result;
    }

    Component getTopDesignComponentView() {
        return topDesignComponent != null
                ? (Component) replicator.getClonedComponent(topDesignComponent)
                : null;
    }

    // NOTE: does not create a new Point instance
    Point pointFromComponentToHandleLayer(Point p, Component sourceComp) {
        Component commonParent = layeredPane;
        Component comp = sourceComp;
        while (comp != null && comp != commonParent) {
            p.x += comp.getX();
            p.y += comp.getY();
            comp = comp.getParent();
        }
        comp = handleLayer;
        while (comp != null && comp != commonParent) {
            p.x -= comp.getX();
            p.y -= comp.getY();
            comp = comp.getParent();
        }
        return p;
    }

    // NOTE: does not create a new Point instance
    Point pointFromHandleToComponentLayer(Point p, Component targetComp) {
        Component commonParent = layeredPane;
        Component comp = handleLayer;
        while (comp != commonParent) {
            p.x += comp.getX();
            p.y += comp.getY();
            comp = comp.getParent();
        }
        comp = targetComp;
        while (comp != commonParent) {
            p.x -= comp.getX();
            p.y -= comp.getY();
            comp = comp.getParent();
        }
        return p;
    }

    boolean isCoordinatesRoot(Component comp) {
        return (layeredPane == comp);
    }

    private Rectangle componentBoundsToTop(Component component) {
        if (component == null) {
            return null;
        }

        Component top = getTopDesignComponentView();

        int dx = 0;
        int dy = 0;

        if (component != top) {
            Component comp = component.getParent();
            while (comp != top) {
                if (comp == null) {
                    break;//return null;
                }
                dx += comp.getX();
                dy += comp.getY();
                comp = comp.getParent();
            }
        } else {
            dx = -top.getX();
            dy = -top.getY();
        }

        Rectangle bounds = component.getBounds();
        bounds.x += dx;
        bounds.y += dy;

        return bounds;
    }

    // -------
    // designer mode
    void setDesignerMode(int mode) {
        formToolBar.updateDesignerMode(mode);
        if (mode != designerMode && initialized) {
            designerMode = mode;
            handleLayer.endDragging(null);
            AssistantModel aModel = formModel.getAssistantModel();
            if (mode == MODE_SELECT) {
                aModel.setContext("select");
            }
        }
    }

    public int getDesignerMode() {
        return designerMode;
    }

    public void toggleSelectionMode() {
        setDesignerMode(MODE_SELECT);
        PaletteUtils.clearPaletteSelection();
    }

    void toggleAddMode() {
        setDesignerMode(MODE_ADD);
        PaletteUtils.clearPaletteSelection();
    }

    /*
     // -------
     // designer size
     Dimension getDesignerSize() {
     return componentLayer.getDesignerSize();
     }

     void setDesignerSize(Dimension size, Dimension oldSize) {
     componentLayer.setDesignerSize(size);
     }
    
     public void resetDesignerSize() {
     setDesignerSize(null, null);
     }

     private void setupDesignerSize() {
     if (topDesignComponent instanceof RADVisualFormContainer) {
     Dimension size = ((RADVisualFormContainer) topDesignComponent).getDesignerSize();
     if (size == null) {   // use default size if no stored size is available and
     // layout form or top design comp is root in the form (but not a container)
     size = new Dimension(400, 300);
     }
     Dimension setSize = componentLayer.setDesignerSize(size); // null computes preferred size
     storeDesignerSize(setSize);
     }
     }
     */
    /*
     private void checkDesignerSize() {
     if (formModel.isFreeDesignDefaultLayout()
     && topDesignComponent instanceof RADVisualComponent
     && !(topDesignComponent instanceof RADVisualFormContainer)) {   // new layout container defining designer size
     // designer size not defined explicitly - check minimum size
     Component topComp = getTopDesignComponentView();
     Component topCont = null;
     if (topDesignComponent instanceof RADVisualContainer<?>) {
     topCont = ((RADVisualContainer<?>) topDesignComponent).getContainerDelegate(topComp);
     }
     if (topCont == null) {
     topCont = topComp;
     }
     // can't rely on minimum size of the container wrap - e.g. menu bar
     // returns wrong min height
     int wDiff = topComp.getWidth() - topCont.getWidth();
     int hDiff = topComp.getHeight() - topCont.getHeight();

     Dimension designerSize = new Dimension(getDesignerSize());
     designerSize.width -= wDiff;
     designerSize.height -= hDiff;
     Dimension minSize = topCont.getMinimumSize();
     boolean corrected = false;
     if (designerSize.width < minSize.width) {
     designerSize.width = minSize.width;
     corrected = true;
     }
     if (designerSize.height < minSize.height) {
     designerSize.height = minSize.height;
     corrected = true;
     }

     if (corrected) {
     designerSize.width += wDiff;
     designerSize.height += hDiff;

     // hack: we need the size correction in the undo/redo
     if (formModel.isCompoundEditInProgress()) {
     FormModelEvent ev = new FormModelEvent(formModel, FormModelEvent.SYNTHETIC_PROPERTY_CHANGED);
     ev.setComponentAndContainer(topDesignComponent, null);
     ev.setProperty(FormModelEvent.PROP_DESIGNER_SIZE, getDesignerSize(), designerSize);
     formModel.addUndoableEdit(ev.getUndoableEdit());
     }
     componentLayer.setDesignerSize(designerSize);
     storeDesignerSize(designerSize);
     }
     }
     }
     */
    // ---------
    // components selection
    public java.util.List<RADComponent<?>> getSelectedComponents() {
        return selectedComponents;
    }

    Node[] getSelectedComponentNodes() {
        List<Node> selectedNodes = new ArrayList<>(selectedComponents.size());
        for (RADComponent<?> c : selectedComponents) {
            if (c.getNodeReference() != null) { // issue 126192 workaround
                selectedNodes.add(c.getNodeReference());
            }
        }
        return selectedNodes.toArray(new Node[selectedNodes.size()]);
    }

    java.util.List<RADVisualComponent<?>> getSelectedLayoutComponents() {
        return selectedLayoutComponents;
    }

    boolean isComponentSelected(RADVisualComponent<?> radComp) {
        return selectedComponents.contains(radComp);
    }

    public void setSelectedComponent(RADComponent<?> radComp) {
        clearSelectionImpl();
        addComponentToSelectionImpl(radComp);
        repaintSelection();
        updateNodesSelection();
    }

    public void setSelectedComponents(RADComponent<?>[] radComps) {
        clearSelectionImpl();
        for (RADComponent<?> radComp : radComps) {
            addComponentToSelectionImpl(radComp);
        }
        repaintSelection();
        updateNodesSelection();
    }

    void setSelectedNode(FormNode node) {
        if (node instanceof RADComponentNode) {
            setSelectedComponent(((RADComponentNode) node).getRADComponent());
        } else {
            clearSelectionImpl();
            repaintSelection();

            FormInspector ci = FormInspector.getInstance();
            if (ci.getFocusedForm() == this) {
                Node[] selectedNodes = new Node[]{node};
                try {
                    ci.setSelectedNodes(selectedNodes, this);
                    // sets also the activated nodes (both for FormInspector
                    // and PlatypusFormLayoutView)
                } catch (java.beans.PropertyVetoException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
                }
            }
        }
    }

    public void addComponentToSelection(RADVisualComponent<?> radComp) {
        addComponentToSelectionImpl(radComp);
        repaintSelection();
        updateNodesSelection();
    }

    void addComponentsToSelection(RADVisualComponent<?>[] radComps) {
        for (RADVisualComponent<?> radComp : radComps) {
            addComponentToSelectionImpl(radComp);
        }
        repaintSelection();
        updateNodesSelection();
    }

    void removeComponentFromSelection(RADVisualComponent<?> radComp) {
        removeComponentFromSelectionImpl(radComp);
        repaintSelection();
        updateNodesSelection();
    }

    public void clearSelection() {
        clearSelectionImpl();
        repaintSelection();
        updateNodesSelection();
    }

    void addComponentToSelectionImpl(RADComponent<?> radComp) {
        if (radComp != null) {
            selectedComponents.add(radComp);
            RADVisualComponent<?> layoutComponent = componentToLayoutComponent(radComp);
            if (layoutComponent != null) {
                selectedLayoutComponents.add(layoutComponent);
                ensureComponentIsShown((RADVisualComponent<?>) radComp);
                selectionChanged();
            }
        }
    }

    RADVisualComponent<?> componentToLayoutComponent(RADComponent<?> radComp) {
        if (radComp instanceof RADVisualComponent<?>) {
            RADVisualComponent<?> visualComp = (RADVisualComponent<?>) radComp;
            if (!visualComp.isMenuComponent()) {
                RADVisualContainer<?> radCont = visualComp.getParentComponent();
                if ((radCont != null) && ScrollPane.class.isAssignableFrom(radCont.getBeanInstance().getClass())
                        && isInDesigner(radCont)) {   // substitute with scroll pane...
                    return radCont;
                }
                // otherwise just check if it is visible in the designer
                return isInDesigner(visualComp) ? visualComp : null;
            }
        }
        return null;
    }

    void removeComponentFromSelectionImpl(RADComponent<?> radComp) {
        selectedComponents.remove(radComp);
        if (radComp instanceof RADVisualComponent<?>) {
            selectedLayoutComponents.remove((RADVisualComponent<?>) radComp);
        }
        selectionChanged();
    }

    void clearSelectionImpl() {
        selectedComponents.clear();
        selectedLayoutComponents.clear();
        selectionChanged();
    }

    void selectionChanged() {
        if (formModel != null) {
            // Some (redundant) postponed update => ignore
            // See, for example, issue 153953 - the formDesigner is reset
            // during refactoring. The selection is cleared, but the corresponding
            // event arrives after formModel is cleared and before it is
            // initialized again
            updateAlignActions();
            updateResizabilityActions();
            updateAnchorActions();
            updateAssistantContext();
        }
    }

    void repaintSelection() {
        if (handleLayer != null) { // Issue 174373
            handleLayer.repaint();
        }
    }

    private void updateAlignActions() {
        Collection<Action> actions = getAlignActions();
        List<RADVisualComponent<?>> selected = getSelectedLayoutComponents();
        boolean enable = false;
        if (selected != null && selected.size() > 1) {
            RADVisualContainer<?> parent = FormUtils.getSameParent(selected);
            if (parent != null && parent.getLayoutSupport() != null) {
                if (parent.getLayoutSupport().getLayoutDelegate() instanceof MarginLayoutSupport) {
                    enable = true;
                }
            }
        }
        for (Action action : actions) {
            action.setEnabled(enable);
        }
    }

    void setAnchorButtons(JToggleButton[] buttons) {
        anchorButtons = buttons;
    }

    public JToggleButton[] getAnchorButtons() {
        return anchorButtons;
    }

    public void updateAnchorActions() {
        List<RADVisualComponent<?>> components = getSelectedLayoutComponents();
        if (components != null && !components.isEmpty()) {
            for (AbstractButton item : anchorButtons) {
                item.setEnabled(true);
                item.setSelected(false);
            }
            boolean checkLeft = true;
            boolean checkRight = true;
            boolean checkTop = true;
            boolean checkBottom = true;
            boolean leftSelected = false;
            boolean rightSelected = false;
            boolean topSelected = false;
            boolean bottomSelected = false;
            boolean oldLeftSelected;
            boolean oldRightSelected;
            boolean oldTopSelected;
            boolean oldBottomSelected;
            for (int i = 0; i < components.size(); i++) {
                oldLeftSelected = leftSelected;
                oldRightSelected = rightSelected;
                oldTopSelected = topSelected;
                oldBottomSelected = bottomSelected;
                RADVisualComponent<?> vc = components.get(i);
                RADVisualContainer<?> parent = vc.getParentComponent();
                if (parent != null && parent.getLayoutSupport() != null) {
                    LayoutSupportDelegate lsd = parent.getLayoutSupport().getLayoutDelegate();
                    if (lsd instanceof MarginLayoutSupport) {
                        MarginLayoutSupport.MarginLayoutConstraints mlc = (MarginLayoutSupport.MarginLayoutConstraints) lsd.getConstraints(vc.getComponentIndex());
                        MarginConstraints mc = mlc.getConstraintsObject();
                        leftSelected = mc.getLeft() != null;
                        rightSelected = mc.getRight() != null;
                        topSelected = mc.getTop() != null;
                        bottomSelected = mc.getBottom() != null;
                        if (checkLeft) {
                            if (i > 0 && leftSelected != oldLeftSelected) {
                                anchorButtons[0].setEnabled(false);
                                anchorButtons[0].setSelected(false);
                                checkLeft = false;
                            } else {
                                anchorButtons[0].setEnabled(true);
                                anchorButtons[0].setSelected(leftSelected);
                            }
                        }
                        if (checkRight) {
                            if (i > 0 && rightSelected != oldRightSelected) {
                                anchorButtons[1].setEnabled(false);
                                anchorButtons[1].setSelected(false);
                                checkRight = false;
                            } else {
                                anchorButtons[1].setEnabled(true);
                                anchorButtons[1].setSelected(rightSelected);
                            }
                        }
                        if (checkTop) {
                            if (i > 0 && topSelected != oldTopSelected) {
                                anchorButtons[2].setEnabled(false);
                                anchorButtons[2].setSelected(false);
                                checkTop = false;
                            } else {
                                anchorButtons[2].setEnabled(true);
                                anchorButtons[2].setSelected(topSelected);
                            }
                        }
                        if (checkBottom) {
                            if (i > 0 && bottomSelected != oldBottomSelected) {
                                anchorButtons[3].setEnabled(false);
                                anchorButtons[3].setSelected(false);
                                checkBottom = false;
                            } else {
                                anchorButtons[3].setEnabled(true);
                                anchorButtons[3].setSelected(bottomSelected);
                            }
                        }
                    } else {
                        for (AbstractButton item : anchorButtons) {
                            item.setEnabled(false);
                            item.setSelected(false);
                        }
                        break;
                    }
                } else {
                    for (AbstractButton item : anchorButtons) {
                        item.setEnabled(false);
                        item.setSelected(false);
                    }
                    break;
                }
            }
        } else {
            for (AbstractButton item : anchorButtons) {
                item.setEnabled(false);
                item.setSelected(false);
            }
        }
    }

    void setResizabilityButtons(JToggleButton[] buttons) {
        resizabilityButtons = buttons;
    }

    public JToggleButton[] getResizabilityButtons() {
        return resizabilityButtons;
    }

    public void updateResizabilityActions() {
        List<RADVisualComponent<?>> selected = getSelectedLayoutComponents();
        Collection<String> componentIds = selectedComponentNames();
        Action[] actions = getResizabilityActions().toArray(new Action[]{});
        RADComponent<?> top = getTopDesignComponent();
        if (selected.isEmpty() || top == null || componentIds.contains(top.getName())) {
            actions[0].setEnabled(false);
            actions[1].setEnabled(false);
            getResizabilityButtons()[0].setSelected(false);
            getResizabilityButtons()[1].setSelected(false);
        } else {
            actions[0].setEnabled(true);
            actions[1].setEnabled(true);
            getResizabilityButtons()[0].setSelected(false);
            getResizabilityButtons()[1].setSelected(false);
            boolean checkHorizontal = true;
            boolean checkVertical = true;
            boolean oldHSelected;
            boolean oldVSelected;
            boolean hSelected = false;
            boolean vSelected = false;
            for (int i = 0; i < selected.size(); i++) {
                oldHSelected = hSelected;
                oldVSelected = vSelected;
                RADVisualComponent<?> visComp = selected.get(i);
                RADVisualContainer<?> parent = visComp.getParentComponent();
                if (parent != null && parent.getLayoutSupport() != null) {
                    LayoutSupportDelegate layoutDelegate = parent.getLayoutSupport().getLayoutDelegate();
                    if (layoutDelegate instanceof MarginLayoutSupport) {
                        MarginLayoutConstraints mlc = (MarginLayoutConstraints) layoutDelegate.getConstraints(visComp.getComponentIndex());
                        hSelected = mlc.getConstraintsObject().getWidth() == null;
                        vSelected = mlc.getConstraintsObject().getHeight() == null;
                    } else if (layoutDelegate != null) {
                        actions[0].setEnabled(false);
                        actions[1].setEnabled(false);
                        getResizabilityButtons()[0].setSelected(false);
                        getResizabilityButtons()[1].setSelected(false);
                        break;
                    }
                    if (checkHorizontal) {
                        if (i > 0 && hSelected != oldHSelected) {
                            actions[0].setEnabled(false);
                            getResizabilityButtons()[0].setSelected(false);
                            checkHorizontal = false;
                        } else {
                            actions[0].setEnabled(true);
                            getResizabilityButtons()[0].setSelected(hSelected);
                        }
                    }
                    if (checkVertical) {
                        if (i > 0 && vSelected != oldVSelected) {
                            actions[1].setEnabled(false);
                            getResizabilityButtons()[1].setSelected(false);
                            checkVertical = false;
                        } else {
                            actions[1].setEnabled(true);
                            getResizabilityButtons()[1].setSelected(vSelected);
                        }
                    }
                } else {
                    actions[0].setEnabled(false);
                    actions[1].setEnabled(false);
                    getResizabilityButtons()[0].setSelected(false);
                    getResizabilityButtons()[1].setSelected(false);
                    break;
                }
            }
        }
    }

    private void updateAssistantContext() {
        String context = null;
        String additionalCtx = null;
        List<RADComponent<?>> selComps = getSelectedComponents();
        int selCount = selComps.size();
        if (selCount > 0) {
            RADComponent<?> radComp = selComps.get(0);
            if (selCount == 1 && context == null) {
                Object bean = radComp.getBeanInstance();
                if (bean instanceof JTabbedPane) {
                    JTabbedPane pane = (JTabbedPane) bean;
                    int count = pane.getTabCount();
                    switch (count) {
                        case 0:
                            context = "tabbedPaneEmpty";
                            break; // NOI18N
                        case 1:
                            context = "tabbedPaneOne";
                            break; // NOI18N
                        default:
                            context = "tabbedPane";
                            break; // NOI18N
                    }
                } else if (bean instanceof JRadioButton) {
                    RADProperty<?> property = radComp.<RADProperty<?>>getProperty("buttonGroup"); // NOI18N
                    try {
                        if ((property != null) && (property.getValue() == null)) {
                            context = "buttonGroup"; // NOI18N
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
                    }
                } else if ((bean instanceof JPanel) && (getTopDesignComponent() != radComp) && (Math.random() < 0.2)) {
                    context = "designThisContainer"; // NOI18N
                } else if (bean instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) bean;
                    if ((scrollPane.getViewport() != null)
                            && (scrollPane.getViewport().getView() == null)) {
                        context = "scrollPaneEmpty"; // NOI18N
                    } else if (Math.random() < 0.5) {
                        context = "scrollPane"; // NOI18N
                    }
                }
            }
        }
        if (context == null) {
            context = "select"; // NOI18N
        }
        formModel.getAssistantModel().setContext(context, additionalCtx);
    }

    /**
     * Finds out what component follows after currently selected component when
     * TAB (forward true) or Shift+TAB (forward false) is pressed.
     *
     * @return the next or previous component for selection
     */
    RADVisualComponent<?> getNextVisualComponent(boolean forward) {
        if (selectedComponents.size() > 1) {
            return null;
        } else {
            RADVisualComponent<?> visComp = (!selectedComponents.isEmpty() && selectedComponents.get(0) instanceof RADVisualComponent<?>) ? (RADVisualComponent<?>) selectedComponents.get(0) : null;
            return getNextVisualComponent(visComp, forward);
        }
    }

    /**
     * @return the next or prevoius component to component comp
     */
    RADVisualComponent<?> getNextVisualComponent(RADVisualComponent<?> comp, boolean forward) {
        if (comp == null) {
            return topDesignComponent;
        }
        if (getComponent(comp) == null) {
            return null;
        }

        RADVisualContainer<?> cont;
        RADVisualComponent<?>[] subComps;

        if (forward) {
            // try the first sub-component
            subComps = getVisualSubComponents(comp);
            if (subComps.length > 0) {
                return subComps[0];
            }

            // try the next component (or the next of the parent then)
            if (comp == topDesignComponent) {
                return topDesignComponent;
            }
            cont = (RADVisualContainer<?>) comp.getParentComponent();
            if (cont == null) {
                return null;
            }
            int i = cont.getIndexOf(comp);
            while (i >= 0) {
                subComps = cont.getSubComponents();
                if (i + 1 < subComps.length) {
                    return subComps[i + 1];
                }

                if (cont == topDesignComponent) {
                    break;
                }
                comp = cont; // one level up
                cont = (RADVisualContainer<?>) comp.getParentComponent();
                if (cont == null) {
                    return null; // should not happen
                }
                i = cont.getIndexOf(comp);
            }

            return topDesignComponent;
        } else { // backward
            // take the previuos component
            if (comp != topDesignComponent) {
                cont = (RADVisualContainer<?>) comp.getParentComponent();
                if (cont == null) {
                    return null;
                }
                int i = cont.getIndexOf(comp);
                if (i >= 0) { // should be always true
                    if (i == 0) {
                        return cont; // the opposite to the 1st forward step
                    }
                    subComps = cont.getSubComponents();
                    comp = subComps[i - 1];
                } else {
                    comp = topDesignComponent;
                }
            }

            // find the last subcomponent of it
            do {
                subComps = getVisualSubComponents(comp);
                if (subComps.length > 0) {
                    comp = subComps[subComps.length - 1];
                    continue;
                } else {
                    break;
                }
            } while (true);
            return comp;
        }
    }

    private RADVisualComponent<?>[] getVisualSubComponents(RADComponent<?> radComp) {
        return radComp instanceof RADVisualContainer
                ? ((RADVisualContainer<?>) radComp).getSubComponents() : new RADVisualComponent<?>[]{};
        // TBD components set as properties
    }

    /**
     * Aligns selected components in the specified direction.
     *
     * @param closed determines if closed group should be created.
     * @param dimension dimension to align in.
     * @param alignment requested alignment.
     */
    void align(boolean closed, int dimension, int alignment) {
        // Check that the action is enabled
        Action action = null;
        for (Action candidate : getAlignActions()) {
            if (candidate instanceof AlignAction) {
                AlignAction alignCandidate = (AlignAction) candidate;
                if ((alignCandidate.getAlignment() == alignment) && (alignCandidate.getDimension() == dimension)) {
                    action = alignCandidate;
                    break;
                }
            }
        }
        if ((action == null) || (!action.isEnabled())) {
            return;
        }
        Collection<String> selectedIds = selectedLayoutComponentNames();
        RADComponent<?> parent = commonParent(selectedIds);
        formModel.fireContainerLayoutChanged((RADVisualContainer<?>) parent, null, null, null);
    }

    public Collection<Action> getAlignActions() {
        if (alignActions == null) {
            alignActions = new ArrayList<>();
            // Grouping actions
            alignActions.add(new AlignAction(LayoutConstants.HORIZONTAL, LayoutConstants.LEADING, true));
            alignActions.add(new AlignAction(LayoutConstants.HORIZONTAL, LayoutConstants.TRAILING, true));
            alignActions.add(new AlignAction(LayoutConstants.HORIZONTAL, LayoutConstants.CENTER, true));
            alignActions.add(new AlignAction(LayoutConstants.VERTICAL, LayoutConstants.LEADING, true));
            alignActions.add(new AlignAction(LayoutConstants.VERTICAL, LayoutConstants.TRAILING, true));
            alignActions.add(new AlignAction(LayoutConstants.VERTICAL, LayoutConstants.CENTER, true));
        }
        return alignActions;
    }

    public Collection<Action> getResizabilityActions() {
        if (resizabilityActions == null) {
            resizabilityActions = new ArrayList<>();
            resizabilityActions.add(new ResizabilityAction(LayoutConstants.HORIZONTAL));
            resizabilityActions.add(new ResizabilityAction(LayoutConstants.VERTICAL));
        }
        return resizabilityActions;
    }

    public Collection<Action> getAnchorActions() {
        if (anchorActions == null) {
            anchorActions = new ArrayList<>();
            anchorActions.add(new AnchorAction(LayoutConstants.HORIZONTAL, LayoutConstants.LEADING));
            anchorActions.add(new AnchorAction(LayoutConstants.HORIZONTAL, LayoutConstants.TRAILING));
            anchorActions.add(new AnchorAction(LayoutConstants.VERTICAL, LayoutConstants.LEADING));
            anchorActions.add(new AnchorAction(LayoutConstants.VERTICAL, LayoutConstants.TRAILING));
        }
        return anchorActions;
    }

    /**
     * Returns collection of ids of the selected layout components.
     *
     * @return <code>Collection</code> of <code>String</code> objects.
     */
    Collection<String> selectedLayoutComponentNames() {
        List<String> selectedIds = new ArrayList<>();
        getSelectedLayoutComponents().stream().forEach((radComp) -> {
            selectedIds.add(radComp.getName());
        });
        return selectedIds;
    }

    /**
     * Checks whether the given components are in the same containter.
     *
     * @param compIds <code>Collection</code> of component IDs.
     * @return common container parent or <code>null</code> if the components
     * are not from the same container.
     */
    private RADComponent<?> commonParent(Collection<String> compIds) {
        RADComponent<?> parent = null;
        FormModel lformModel = getFormModel();
        for (String compId : compIds) {
            RADComponent<?> radComp = lformModel.getRADComponent(compId);
            RADComponent<?> radCont = radComp.getParentComponent();
            if (parent == null) {
                parent = radCont;
            }
            if ((radCont == null) || (parent != radCont)) {
                return null;
            }
        }
        return parent;
    }

    // ---------
    // visibility update
    // synchronizes FormInspector with selection in PlatypusFormLayoutView
    // [there is a hardcoded relationship between these two views]
    void updateNodesSelection() {
        Node[] selectedNodes = getSelectedComponentNodes();
        try {
            FormInspector fi = FormInspector.getInstance();
            explorerManager.setSelectedNodes(selectedNodes);
            fi.setSelectedNodes(selectedNodes, this);
            setActivatedNodes(selectedNodes);
            // Hack! NetBeans doesn't properly handle activated nodes in multi view's elements
            // So, we need to use dummy explorer manager and it's lookup, associated with this multiview element TopComponent
            // to produce satisfactory events.
            // Moreover, this code shouldn't be here at all, but NetBeans doesn't refresh
            // property sheet on multi-view's activated element change, so we need to simulate
            // activated and selected nodes change.
            Node[] activated = getActivatedNodes();
            Node[] empty = new Node[]{};
            explorerManager.setSelectedNodes(empty);
            setActivatedNodes(empty);
            // Hack. When multi-view element with no any activated node is activated,
            // NetBeans' property sheet stay with a node from previous multi-view element.
            // So, we need to simulate non-empty activated nodes.
            if (activated == null || activated.length <= 0) {
                activated = new Node[]{formModel.getTopDesignComponent().getNodeReference()};
            }
            explorerManager.setSelectedNodes(activated);
            setActivatedNodes(activated);
        } catch (java.beans.PropertyVetoException ex) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
        }
    }

    void updateVisualSettings() {
        if (componentLayer != null) {
            componentLayer.updateVisualSettings();
        }
        if (layeredPane != null) {
            layeredPane.revalidate();
            layeredPane.repaint(); // repaints both HanleLayer and ComponentLayer
        }
    }

    private void ensureComponentIsShown(RADVisualComponent<?> radComp) {
        Component comp = getComponent(radComp);
        if (comp != null && !comp.isShowing() && isInDesigner(radComp)) {
            Component topComp = getComponent(topDesignComponent);
            if (topComp != null && topComp.isShowing()) {
                RADVisualContainer<?> radCont = radComp.getParentComponent();
                RADVisualComponent<?> child = radComp;
                while (radCont != null) {
                    Container cont = getComponent(radCont);
                    LayoutSupportManager laysup = radCont.getLayoutSupport();
                    if (laysup != null) {
                        Container contDelegate = radCont.getContainerDelegate(cont);
                        laysup.selectComponent(child.getComponentIndex());
                        laysup.arrangeContainer(cont, contDelegate);
                    }
                    if (radCont == topDesignComponent || cont.isShowing()) {
                        break;
                    }
                    child = radCont;
                    radCont = radCont.getParentComponent();
                }
            }
        }
    }

    // -----------------
    // in-place editing
    public void startInPlaceEditing(RADComponent<?> radComp) {
        if (!formModel.isReadOnly() && isEditableInPlace(radComp)
                && (textEditLayer == null || !textEditLayer.isVisible())) {
            Component comp = getComponent(radComp);
            if (comp == null) { // component is not visible
                notifyCannotEditInPlace();
                return;
            }
            FormProperty<String> property = null;
            if (JTabbedPane.class.isAssignableFrom(radComp.getBeanClass())) {
                JTabbedPane tabbedPane = (JTabbedPane) comp;
                int index = tabbedPane.getSelectedIndex();
                RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp;
                RADVisualComponent<?> tabComp = radCont.getSubComponent(index);
                FormProperty<?>[] props = tabComp.getConstraintsProperties();
                for (int i = 0; i < props.length; i++) {
                    if (props[i].getName().equals("TabConstraints.tabTitle")) { // NOI18N
                        property = (FormProperty<String>) props[i];
                        break;
                    }
                }
                if (property == null) {
                    return;
                }
            } else {
                property = radComp.<RADProperty<String>>getProperty("text"); // NOI18N
                if (property == null) {
                    return; // should not happen
                }
            }

            editedProperty = property;

            getInPlaceEditLayer();
            try {
                textEditLayer.setEditedComponent(comp, property.getValue());
                textEditLayer.setVisible(true);
                handleLayer.setVisible(false);
                textEditLayer.requestFocus();
            } catch (Exception ex) {
                notifyCannotEditInPlace();
            }
        }
    }

    private InPlaceEditLayer.FinishListener getFinishListener() {
        if (finnishListener == null) {
            finnishListener = new InPlaceEditLayer.FinishListener() {
                @Override
                public void editingFinished(boolean textChanged) {
                    finishInPlaceEditing(textEditLayer.isTextChanged());
                }
            };
        }
        return finnishListener;
    }

    private void finishInPlaceEditing(boolean applyChanges) {
        if (applyChanges) {
            try {
                Object value = editedProperty.getValue();
                if (value instanceof String) {
                    editedProperty.setValue(textEditLayer.getEditedText());
                }
            } catch (Exception ex) { // should not happen
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
            }
        }
        if (handleLayer != null) {
            textEditLayer.setVisible(false);
            handleLayer.setVisible(true);
            handleLayer.requestFocus();
        }
        editedProperty = null;
    }

    public boolean isEditableInPlace(RADComponent<?> radComp) {
        if (radComp == null) {
            return false;
        }
        Component comp = getComponent(radComp);

        // don't allow in-place editing if there's some AWT parent (it may
        // cause problems with fake peers on some platforms)
        RADComponent<?> parent = radComp.getParentComponent();
        while (parent != null) {
            if (!JComponent.class.isAssignableFrom(parent.getBeanClass())
                    && !RootPaneContainer.class.isAssignableFrom(
                            parent.getBeanClass())) {
                return false;
            }
            parent = parent.getParentComponent();
        }

        Class<?> beanClass = radComp.getBeanClass();
        return InPlaceEditLayer.supportsEditingFor(beanClass, false)
                && (!JTabbedPane.class.isAssignableFrom(beanClass) || ((JTabbedPane) comp).getTabCount() != 0);
    }

    private void notifyCannotEditInPlace() {
        DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(
                        FormUtils.getBundleString("MSG_ComponentNotShown"), // NOI18N
                        NotifyDescriptor.WARNING_MESSAGE));
    }

    // -----------------
    // menu editing
    public void openMenu(RADComponent<?> radComp) {
        MenuEditLayer lmenuEditLayer = getMenuEditLayer();
        Component comp = getComponent(radComp);
        lmenuEditLayer.setVisible(true);
        lmenuEditLayer.openAndShowMenu(radComp, comp);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gui.formeditor"); // NOI18N
    }

    @Override
    public void componentActivated() {
        if (formModel != null) {
            FormInspector fi = FormInspector.getInstance();
            if (fi.getFocusedForm() != this) {
                fi.focusForm(this);
            }
            fi.attachActions();
            updateNodesSelection();
            if (textEditLayer == null || !textEditLayer.isVisible()) {
                handleLayer.requestFocus();
            }
        }
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        if (formModel != null) {
            if (textEditLayer != null && textEditLayer.isVisible()) {
                textEditLayer.finishEditing(false);
            }
        }
    }

    @Override
    public UndoRedo getUndoRedo() {
        UndoRedo ur = formModel != null ? formModel.getUndoRedoManager() : null;
        return ur != null ? ur : super.getUndoRedo();
    }

    // multiview stuff
    @Override
    public JComponent getToolbarRepresentation() {
        return getFormToolBar();
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        multiViewObserver = callback;
    }

    @Override
    public void requestVisible() {
        if (multiViewObserver != null) {
            multiViewObserver.requestVisible();
        } else {
            super.requestVisible();
        }
    }

    @Override
    public void requestActive() {
        if (multiViewObserver != null) {
            multiViewObserver.requestActive();
        } else {
            super.requestActive();
        }
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        // Closed PlatypusFormLayoutView is not going to be reused.
        // Clear all references to prevent memory leaks - even if PlatypusFormLayoutView
        // is kept for some reason, make sure FormModel is not held from it.
        if (formEditor.getFormDataObject() instanceof PlatypusFormDataObject) {
            formEditor.getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).shrink();
        }
        reset(null);
    }

    @Override
    public void componentShowing() {
        try {
            super.componentShowing();
            finishComponentShowing();
        } catch (PersistenceException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
        }
    }

    private void finishComponentShowing() throws PersistenceException {
        if (!formEditor.isFormLoaded()) {
            long ms = System.currentTimeMillis();
            formEditor.loadForm();
            formEditor.reportErrors(FormEditor.FormOperation.LOADING);
            Logger.getLogger(FormEditor.class.getName()).log(Level.FINER, "Opening form time 3: {0}ms", (System.currentTimeMillis() - ms)); // NOI18N
        }
        if (formEditor.isFormLoaded()) {
            initialize();
            PlatypusFormSupport.checkFormGroupVisibility();
            // hack: after IDE starts, if some form is opened but not active in
            // winsys, we need to select it in FormInspector
            EventQueue.invokeLater(() -> {
                if (formEditor != null && formEditor.isFormLoaded()
                        && FormInspector.exists()
                        && FormInspector.getInstance().getFocusedForm() == null) {
                    FormInspector.getInstance().focusForm(PlatypusFormLayoutView.this);
                }
            });
        }
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
        PlatypusFormSupport.checkFormGroupVisibility();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        if ((formEditor == null) && (multiViewObserver != null)) { // Issue 67879
            multiViewObserver.getTopComponent().close();
            EventQueue.invokeLater(() -> {
                PlatypusFormSupport.checkFormGroupVisibility();
            });
        }
    }

    @Override
    protected CloneableTopComponent createClonedObject() {
        return new PlatypusFormLayoutView(formEditor);
    }

    @Override
    protected boolean closeLast() {
        if (multiViewObserver != null) {
            return true;
        } else if (formEditor.getFormDataObject().isModified()) {
            PlatypusLayoutSupport singleLayoutSupport = formEditor.getFormDataObject().getLookup().lookup(PlatypusLayoutSupport.class);
            return singleLayoutSupport.canClose();
        } else {
            return true;
        }
    }

    @Override
    public CloseOperationState canCloseElement() {
        // if this is not the last cloned designer, closing is OK
        if (!PlatypusFormSupport.isLastView(multiViewObserver.getTopComponent())) {
            return CloseOperationState.STATE_OK;
        }

        // return a placeholder state - to be sure our CloseHandler is called
        return MultiViewFactory.createUnsafeCloseState(
                "ID_FORM_CLOSING", // dummy ID // NOI18N
                MultiViewFactory.NOOP_CLOSE_ACTION,
                MultiViewFactory.NOOP_CLOSE_ACTION);
    }

    public InPlaceEditLayer getInPlaceEditLayer() {
        if (textEditLayer == null) {
            textEditLayer = new InPlaceEditLayer();
            textEditLayer.setVisible(false);
            textEditLayer.addFinishListener(getFinishListener());
            layeredPane.add(textEditLayer, new Integer(2001));
        }
        return textEditLayer;
    }

    MenuEditLayer getMenuEditLayer() {
        if (menuEditLayer == null) {
            menuEditLayer = new MenuEditLayer(this);
            menuEditLayer.setVisible(false);
            layeredPane.add(menuEditLayer, new Integer(2000));
        }
        return menuEditLayer;
    }

    // --------
    private Collection<String> selectedComponentNames() {
        List<String> componentIds = new ArrayList<>();
        for (RADVisualComponent<?> visualComp : getSelectedLayoutComponents()) {
            if (visualComp.getParentComponent() != null
                    && visualComp.getParentLayoutSupport() == null) {
                componentIds.add(visualComp.getName());
            }
        }
        return componentIds;
    }

    /**
     * Updates (sub)nodes of a container (in Component Inspector) after a change
     * has been made (like component added or removed).
     */
    void updateNodeChildren(ComponentContainer radCont) {
        FormNode node = null;

        if (radCont == null || radCont == formModel.getModelContainer()) {
            node = (formEditor.getFormRootNode() != null ? formEditor.getOthersContainerNode() : null);
        } else if (radCont instanceof RADComponent) {
            node = ((RADComponent) radCont).getNodeReference();
        }

        if (node != null) {
            node.updateChildren();
        }
    }

    // Listener on FormModel - ensures updating of designer view.
    private class FormListener implements FormModelListener, Runnable {

        private FormModelEvent[] events;

        @Override
        public void formChanged(final FormModelEvent[] events) {
            if (!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(() -> {
                    processEvents(events);
                });
            } else {
                processEvents(events);
            }
        }

        private void processEvents(FormModelEvent[] aEvents) {
            boolean lafBlock;
            if (aEvents == null) {
                lafBlock = true;
            } else {
                lafBlock = false;
                boolean modifying = false;
                for (int i = 0; i < aEvents.length; i++) {
                    FormModelEvent ev = aEvents[i];
                    if (ev.isModifying()) {
                        modifying = true;
                    }
                    if ((ev.getChangeType() == FormModelEvent.COMPONENT_ADDED)
                            || (ev.getChangeType() == FormModelEvent.COMPONENT_PROPERTY_CHANGED) /*|| (ev.getChangeType() == FormModelEvent.BINDING_PROPERTY_CHANGED)*/) {
                        lafBlock = true;
                        break;
                    }
                }
                if (!modifying) {
                    return;
                }
                assert EventQueue.isDispatchThread();
            }
            events = aEvents;
            if (lafBlock) { // Look&Feel UI defaults remapping needed
                FormLAF.executeWithLookAndFeel(formModel, this);
            } else {
                run();
            }
        }

        @Override
        public void run() {
            if (events == null) {
                replicator.setTopDesignComponent(topDesignComponent);
                JComponent formClone = (JComponent) replicator.createClone();
                if (formClone != null) {
                    formClone.setVisible(true);
                    componentLayer.setTopDesignComponent(formClone);
                }
            } else {
                FormModelEvent[] levents = events;
                events = null;

                int prevType = 0;
                ComponentContainer prevContainer = null;
                boolean updateDone = false;

                Set<RADComponent<?>> compsToSelect = null;
                FormNode nodeToSelect = null;

                for (int i = 0; i < levents.length; i++) {
                    FormModelEvent ev = levents[i];
                    if (ev.isModifying()) {
                        formEditor.getFormDataObject().getLookup().lookup(ModifiedProvider.class).notifyModified();
                    }
                    int type = ev.getChangeType();
                    ComponentContainer radContainer = ev.getContainer();

                    if (type == FormModelEvent.CONTAINER_LAYOUT_EXCHANGED
                            || type == FormModelEvent.CONTAINER_LAYOUT_CHANGED
                            || type == FormModelEvent.COMPONENT_LAYOUT_CHANGED) {
                        if (type == FormModelEvent.CONTAINER_LAYOUT_EXCHANGED || type == FormModelEvent.CONTAINER_LAYOUT_CHANGED) {
                            ComponentContainer cont = ev.getContainer();
                            assert cont instanceof RADVisualContainer<?>;
                            RADVisualContainer<?> visCont = (RADVisualContainer<?>) cont;
                            if (type == FormModelEvent.CONTAINER_LAYOUT_EXCHANGED && visCont.shouldHaveLayoutNode()) {
                                visCont.getNodeReference().fireChildrenChange();
                            }
                            nodeToSelect = ((RADVisualContainer<?>) cont).getLayoutNodeReference();
                        }
                        replicator.updateContainerLayout((RADVisualContainer<?>) radContainer);
                        updateDone = true;
                        updateAnchorActions();
                        updateResizabilityActions();
                    } else if (type == FormModelEvent.COMPONENT_ADDED) {
                        if (ev.getComponent().isInModel()) {
                            if (compsToSelect == null) {
                                compsToSelect = new HashSet<>();
                            }
                            compsToSelect.add(ev.getComponent());
                            if (ev.getContainer() instanceof RADVisualContainer<?>) {
                                RADVisualContainer<?> visCont = (RADVisualContainer<?>) ev.getContainer();
                                compsToSelect.remove(visCont);
                            }
                        }
                        if (prevType != FormModelEvent.COMPONENT_ADDED
                                || prevContainer != radContainer) {
                            try {
                                replicator.updateAddedComponents(radContainer);
                                updateDone = true;
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        updateNodeChildren(radContainer);
                    } else if (type == FormModelEvent.COMPONENT_REMOVED) {
                        RADComponent<?> removed = ev.getComponent();
                        FormNode select;
                        if (radContainer instanceof RADComponent) {
                            select = ((RADComponent) radContainer).getNodeReference();
                        } else {
                            select = formEditor.getOthersContainerNode();
                        }

                        if (!(nodeToSelect instanceof RADComponentNode)) {
                            if (nodeToSelect != formEditor.getFormRootNode()) {
                                nodeToSelect = select;
                            }
                        } else if (nodeToSelect != select) {
                            nodeToSelect = formEditor.getFormRootNode();
                        }

                        // if the top designed component (or some of its parents)
                        // was removed then whole designer view must be recreated
                        if (removed instanceof RADVisualComponent
                                && (removed == topDesignComponent
                                || removed.isParentComponent(topDesignComponent))) {
                            resetTopDesignComponent(false);
                            updateWholeDesigner();
                            return;
                        } else {
                            try {
                                replicator.removeComponent(ev.getComponent(), ev.getContainer());
                                updateDone = true;
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        updateNodeChildren(radContainer);
                    } else if (type == FormModelEvent.COMPONENTS_REORDERED) {
                        if (prevType != FormModelEvent.COMPONENTS_REORDERED
                                || prevContainer != radContainer) {
                            try {
                                replicator.reorderComponents(radContainer);
                                updateDone = true;
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        updateNodeChildren(radContainer);
                    } else if (type == FormModelEvent.COMPONENT_PROPERTY_CHANGED) {
                        try {
                            RADProperty<?> eventProperty = ev.getComponentProperty();
                            replicator.updateComponentProperty(eventProperty);
                            updateDone = true;
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    } else if (type == FormModelEvent.TOP_DESIGN_COMPONENT_CHANGED) {
                        setTopDesignComponent((RADVisualContainer<?>) ev.getComponent(), true);
                    } else if (type == FormModelEvent.SYNTHETIC_PROPERTY_CHANGED) {
                        switch (ev.getPropertyName()) {
                            /*
                             case FormModelEvent.PROP_DESIGNER_SIZE:
                             //Dimension oldSize = (Dimension) ev.getOldPropertyValue();
                             if (ev.getComponent() == topDesignComponent) {
                             Dimension size = (Dimension) ev.getNewPropertyValue();
                             componentLayer.setDesignerSize(size);
                             componentLayer.revalidate();
                             updateDone = true;
                             }
                             break;
                             */
                            case RADComponent.COMPONENT_NAME_PROP_NAME:
                                try {
                                    String oldName = (String) ev.getOldPropertyValue();
                                    String newName = (String) ev.getNewPropertyValue();
                                    replicator.renameComponent(oldName, newName);
                                    /*
                                     RADComponent<?> comp = ev.getComponent();
                                     comp.setStoredName(oldName);
                                     replicator.removeComponent(comp, null);
                                     comp.setStoredName(newName);
                                     replicator.addComponent(comp);
                                     */
                                    updateDone = true;
                                } catch (Exception ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                                break;
                        }
                    } else if (type == FormModelEvent.COLUMN_VIEW_EXCHANGED) {
                        updateNodeChildren(ev.getColumn());
                        nodeToSelect = ev.getColumn().getViewControl().getNodeReference();
                    }

                    prevType = type;
                    prevContainer = radContainer;
                }
                updateVisualSettings();
                if (compsToSelect != null) {
                    clearSelectionImpl();
                    for (RADComponent<?> comp : compsToSelect) {
                        addComponentToSelectionImpl(comp);
                    }
                    updateNodesSelection();
                } else if (nodeToSelect != null) {
                    setSelectedNode(nodeToSelect);
                }

                /*
                 if (updateDone) {
                 // check if not smaller than minimum size
                 checkDesignerSize();
                 }
                 */
            }
        }
    }

    /**
     * Action that aligns selected components in the specified direction.
     */
    private class AlignAction extends AbstractAction {
        // PENDING change to icons provided by Dusan

        private static final String ICON_BASE = "com/bearsoft/org/netbeans/modules/form/resources/align_"; // NOI18N
        /**
         * Dimension to align in.
         */
        private final int dimension;
        /**
         * Requested alignment.
         */
        private final int alignment;
        /**
         * Group/Align action.
         */
        private final boolean closed;

        /**
         * Creates action that aligns selected components in the specified
         * direction.
         *
         * @param aDimension dimension to align in.
         * @param aAlignment requested alignment.
         */
        AlignAction(int aDimension, int aAlignment, boolean aClosed) {
            super();
            dimension = aDimension;
            alignment = aAlignment;
            closed = aClosed;
            boolean horizontal = (aDimension == LayoutConstants.HORIZONTAL);
            boolean leading = (aAlignment == LayoutConstants.LEADING);
            String code;
            if (aAlignment == LayoutConstants.CENTER) {
                code = (horizontal ? "ch" : "cv"); // NOI18N
            } else {
                code = (horizontal ? (leading ? "l" : "r") : (leading ? "u" : "d")); // NOI18N
            }
            String iconResource = ICON_BASE + code + ".png"; // NOI18N
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon(iconResource, false));
            putValue(Action.SHORT_DESCRIPTION, FormUtils.getBundleString("CTL_AlignAction_" + code)); // NOI18N
            setEnabled(false);
        }

        /**
         * Performs the alignment of selected components.
         *
         * @param e event that invoked the action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // для MarginLayout
            MarginLayoutOperations mAlign = new MarginLayoutOperations(getSelectedLayoutComponents());
            if (mAlign.canAlign()) {
                // Check that the action is enabled
                Action action = null;
                for (Action candidate : getAlignActions()) {
                    if (candidate instanceof AlignAction) {
                        AlignAction alignCandidate = (AlignAction) candidate;
                        if ((alignCandidate.getAlignment() == alignment) && (alignCandidate.getDimension() == dimension)) {
                            action = alignCandidate;
                            break;
                        }
                    }
                }
                if (action != null && action.isEnabled()) {
                    try {
                        mAlign.align(dimension, alignment);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            } else //*****************************************
            {
                align(closed, dimension, alignment);
            }
        }

        public int getDimension() {
            return dimension;
        }

        public int getAlignment() {
            return alignment;
        }
    }

    /**
     * Action that sets a specified anchor for selected components.
     */
    private class AnchorAction extends AbstractAction {

        private static final String ICON_BASE = "com/bearsoft/org/netbeans/modules/form/resources/alignment_"; // NOI18N
        protected int dimension;
        protected int align;

        /**
         * Creates action that changes the resizability of the component.
         *
         * @param aDimension dimension of the resizability
         */
        AnchorAction(int aDimension, int aAlign) {
            super();
            dimension = aDimension;
            align = aAlign;
            String code = (aDimension == LayoutConstants.HORIZONTAL) ? aAlign == LayoutConstants.LEADING ? "l" : "r" : aAlign == LayoutConstants.LEADING ? "u" : "d"; // NOI18N
            String iconResource = ICON_BASE + code + ".gif"; // NOI18N
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon(iconResource, false));
            putValue(Action.SHORT_DESCRIPTION, FormUtils.getBundleString("CTL_AnchorButton_" + code)); // NOI18N
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            assert e.getSource() instanceof AbstractButton;
            AbstractButton ab = (AbstractButton) e.getSource();
            MarginLayoutOperations operations = new MarginLayoutOperations(getSelectedLayoutComponents());
            try {
                if (dimension == LayoutConstants.HORIZONTAL) {
                    if (align == LayoutConstants.LEADING)// left
                    {
                        if (ab.isSelected()) {
                            operations.setAllLeft();
                        } else {
                            operations.clearAllLeft();
                        }
                    } else// right
                    {
                        if (ab.isSelected()) {
                            operations.setAllRight();
                        } else {
                            operations.clearAllRight();
                        }
                    }
                } else {
                    if (align == LayoutConstants.LEADING)// top
                    {
                        if (ab.isSelected()) {
                            operations.setAllTop();
                        } else {
                            operations.clearAllTop();
                        }
                    } else// bottom
                    {
                        if (ab.isSelected()) {
                            operations.setAllBottom();
                        } else {
                            operations.clearAllBottom();
                        }
                    }
                }
                updateAnchorActions();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    /**
     * Action that aligns selected components in the specified direction.
     */
    private class ResizabilityAction extends AbstractAction {
        // PENDING change to icons provided by Dusan

        private static final String ICON_BASE = "com/bearsoft/org/netbeans/modules/form/resources/resize_"; // NOI18N
        /**
         * Dimension of resizability.
         */
        private final int dimension;

        /**
         * Creates action that changes the resizability of the component.
         *
         * @param aDimension dimension of the resizability
         */
        ResizabilityAction(int aDimension) {
            super();
            dimension = aDimension;
            String code = (aDimension == LayoutConstants.HORIZONTAL) ? "h" : "v"; // NOI18N
            String iconResource = ICON_BASE + code + ".png"; // NOI18N
            putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon(iconResource, false));
            putValue(Action.SHORT_DESCRIPTION, FormUtils.getBundleString("CTL_ResizeButton_" + code)); // NOI18N
            setEnabled(false);
        }

        /**
         * Performs the resizability change of selected components.
         *
         * @param e event that invoked the action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            MarginLayoutOperations mAlign = new MarginLayoutOperations(getSelectedLayoutComponents());
            if (mAlign.canResize()) {
                try {
                    mAlign.resize(dimension);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
    }

    static final long serialVersionUID = 23142032923497120L;

    @Override
    public void writeExternal(ObjectOutput out) throws java.io.IOException {
        super.writeExternal(out);
        out.writeObject(formEditor.getFormDataObject());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        try {
            PlatypusDataObject dataObject = (PlatypusDataObject) in.readObject();
            setFormEditor(dataObject.getLookup().lookup(FormEditorProvider.class).getFormEditor());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

}
