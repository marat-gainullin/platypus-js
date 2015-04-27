/*
 * PlatypusQueryTopComponent.java
 *
 * Created on 04.04.2011, 11:01:28
 */
package com.eas.designer.application.query;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.ModelViewDragHandler;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.gui.view.model.QueryModelView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.utils.scalableui.JScalableScrollPane;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.query.actions.QueryResultsAction;
import com.eas.designer.application.query.lexer.SqlLanguageHierarchy;
import com.eas.designer.application.query.nodes.QueryRootNode;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.model.windows.QueriesDragHandler;
import com.eas.designer.explorer.model.windows.QueryDocumentJumper;
import com.eas.designer.explorer.selectors.QueriesSelector;
import com.eas.designer.explorer.selectors.TablesSelector;
import com.eas.gui.JDropDownButton;
import com.eas.util.ListenerRegistration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.text.EditorKit;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;

/**
 *
 * @author mg
 */
public class PlatypusQueryView extends CloneableTopComponent {

    public static final String SQL_SYNTAX_OK = "Sql - OK";

    protected class DataObjectListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PlatypusQueryDataObject.PROP_MODIFIED.equals(evt.getPropertyName())) {
                updateTitle();
            } else if (PlatypusQueryDataObject.CONN_PROP_NAME.equals(evt.getPropertyName())) {
                try {
                    dataObject.setModelValid(false);
                    dataObject.startModelValidating();
                    initDbRelatedViews();
                    if (dataObject.isModelValid()) {
                        dataObject.refreshOutputFields();
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    protected class NodeSelectionListener implements PropertyChangeListener {

        protected boolean processing;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()) /*
                     * if yout whant to uncomment the following line, you should ensure, that ccp
                     * operation on the nodes will not become slow
                     || ExplorerManager.PROP_NODE_CHANGE.equals(evt.getPropertyName())*/) {
                if (!processing) {
                    processing = true;
                    try {
                        Node[] nodes = ModelInspector.getInstance().getExplorerManager().getSelectedNodes();
                        getModelView().silentClearSelection();
                        getModelView().clearEntitiesFieldsSelection();
                        Map<EntityView<QueryEntity>, Set<Field>> toSelectFields = new HashMap<>();
                        Map<EntityView<QueryEntity>, Set<Parameter>> toSelectParameters = new HashMap<>();
                        for (Node node : nodes) {
                            EntityView<QueryEntity> ev;
                            if (node instanceof EntityNode<?>) {
                                ev = getModelView().getEntityView(((EntityNode<QueryEntity>) node).getEntity());
                                getModelView().silentSelectView(ev);
                            } else if (node instanceof FieldNode && node.getParentNode() != null) {
                                ev = getModelView().getEntityView(((EntityNode<QueryEntity>) node.getParentNode()).getEntity());
                                FieldNode fieldNode = (FieldNode) node;
                                if ((fieldNode.getField() instanceof Parameter) && !(ev.getEntity() instanceof QueryParametersEntity)) {
                                    if (!toSelectParameters.containsKey(ev)) {
                                        toSelectParameters.put(ev, new HashSet<>());
                                    }
                                    toSelectParameters.get(ev).add((Parameter) fieldNode.getField());
                                    //ev.addSelectedParameter((Parameter) fieldNode.getField());
                                } else {
                                    if (!toSelectFields.containsKey(ev)) {
                                        toSelectFields.put(ev, new HashSet<>());
                                    }
                                    toSelectFields.get(ev).add(fieldNode.getField());
                                    //ev.addSelectedField(fieldNode.getField());
                                }
                            }
                        }
                        toSelectParameters.entrySet().stream().forEach((pEntry) -> {
                            EntityView<QueryEntity> ev = pEntry.getKey();
                            ev.addSelectedParameters(pEntry.getValue());
                        });
                        toSelectFields.entrySet().stream().forEach((fEntry) -> {
                            EntityView<QueryEntity> ev = fEntry.getKey();
                            ev.addSelectedFields(fEntry.getValue());
                        });
                        setActivatedNodes(nodes);
                    } finally {
                        processing = false;
                    }
                }
            }
        }
    }

    public class RunQueryAction extends AbstractAction {

        public RunQueryAction() {
            super();
            putValue(Action.NAME, NbBundle.getMessage(PlatypusQueryView.class, RunQueryAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(PlatypusQueryView.class, RunQueryAction.class.getSimpleName() + ".hint"));
            putValue(Action.SMALL_ICON, com.eas.client.model.gui.IconCache.getIcon("runsql.png")); //NOI18N
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                QueryResultsAction.runQuery(dataObject);
            }
        }

        @Override
        public boolean isEnabled() {
            return dataObject.getBasesProxy() != null;
        }
    }
    public static final String PLATYPUS_QUERIES_GROUP_NAME = "PlatypusModel";
    static final long serialVersionUID = 1041132023802024L;
    /**
     * path to the icon used by the component and its open action
     */
    private static final String PREFERRED_ID = "PlatypusQueryTopComponent";
    private static final String SAVE_ACTION_KEY = "save";
    private static final String CTRL_S_KEY_STROKE = "control S";
    protected static final String[] zoomLevelsData = new String[]{"25%", "50%", "75%", "100%", "150%", "200%", "300%"};
    protected transient DataObjectListener dataObjectListener;
    protected transient NodeSelectionListener explorerSelectionListener = new NodeSelectionListener();
    protected transient QueryModelView modelView;
    protected transient TablesSelector tablesSelector;
    protected transient JScalableScrollPane querySchemeScroll;
    protected transient JComboBox<String> comboZoom = new JComboBox<>();
    protected transient ListenerRegistration clientChangeListener;
    protected transient ListenerRegistration modelValidChangeListener;

    protected PlatypusQueryDataObject dataObject;
    protected static final Dimension BTN_DIMENSION = new Dimension(28, 28);

    public PlatypusQueryView() throws Exception {
        super();
        setIcon(ImageUtilities.loadImage(QueryRootNode.ICON_PATH, true));

        InputMap iMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap aMap = getActionMap();
        iMap.put(KeyStroke.getKeyStroke(CTRL_S_KEY_STROKE), SAVE_ACTION_KEY);
        aMap.put(SAVE_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataObject.getLookup().lookup(SaveCookie.class).save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public PlatypusQueryDataObject getDataObject() {
        return dataObject;
    }

    @Override
    public Lookup getLookup() {
        return new ProxyLookup(super.getLookup(), Lookups.fixed(getDataObject()));
    }

    public void setDataObject(PlatypusQueryDataObject aDataObject) throws Exception {
        dataObject = aDataObject;
        dataObjectListener = new DataObjectListener();
        setName(dataObject.getPrimaryFile().getName());
        setToolTipText(NbBundle.getMessage(PlatypusQueryView.class, "HINT_PlatypusQueryTopComponent", dataObject.getPrimaryFile().getPath()));
        tablesSelector = new TablesSelector(dataObject.getProject(), false, true, NbBundle.getMessage(PlatypusQueryView.class, "selectTable"), this);

        initComponents();

        EditorKit editorKit = CloneableEditorSupport.getEditorKit(SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        txtSqlPane.setEditorKit(editorKit);
        txtSqlPane.setDocument(dataObject.getSqlTextDocument());
        Component refinedComponent = initCustomEditor(txtSqlPane);
        pnlSqlSource.add(refinedComponent, BorderLayout.CENTER);

        txtSqlDialectPane.setEditorKit(editorKit);
        txtSqlDialectPane.setDocument(dataObject.getSqlFullTextDocument());
        refinedComponent = initCustomEditor(txtSqlDialectPane);
        pnlSqlDialectSource.add(refinedComponent, BorderLayout.CENTER);
        initDbRelatedViews();

        modelValidChangeListener = dataObject.addModelValidChangeListener(() -> {
            try {
                initDbRelatedViews();
                if (dataObject.isModelValid()) {
                    dataObject.refreshOutputFields();
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        });
        clientChangeListener = dataObject.addClientChangeListener(new PlatypusProject.ClientChangeListener() {

            @Override
            public void connected(String aDatasourceName) {
                String dsName = dataObject.getDatasourceName();
                if (dsName == null) {
                    dsName = dataObject.getProject().getSettings().getDefaultDataSourceName();
                }
                if (dsName == null ? aDatasourceName == null : dsName.equals(aDatasourceName)) {
                    try {
                        dataObject.setModelValid(false);
                        dataObject.startModelValidating();
                        initDbRelatedViews();
                        if (dataObject.isModelValid()) {
                            dataObject.refreshOutputFields();
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            @Override
            public void disconnected(String aDatasourceName) {
                String dsName = dataObject.getDatasourceName();
                if (dsName == null) {
                    dsName = dataObject.getProject().getSettings().getDefaultDataSourceName();
                }
                if (dsName == null ? aDatasourceName == null : dsName.equals(aDatasourceName)) {
                    try {
                        initDbRelatedViews();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            @Override
            public void defaultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName) {
                if (dataObject.getDatasourceName() == null) {
                    try {
                        initDbRelatedViews();
                        if (dataObject.isModelValid()) {
                            dataObject.refreshOutputFields();
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }

    protected void initDbRelatedViews() throws Exception {
        boolean isConnected = dataObject.getProject().isDbConnected(dataObject.getDatasourceName());
        pnlFromNWhere.removeAll();
        if (isConnected) {
            if (dataObject.isModelValid()) {
                if (modelView != null) {
                    modelView.setModel(null);
                }
                modelView = new QueryModelView(dataObject.getModel(), tablesSelector, new QueriesSelector(dataObject.getAppRoot()));
                modelView.addEntityViewDoubleClickListener(new QueryDocumentJumper<>(dataObject.getProject()));
                TransferHandler modelViewOriginalTrnadferHandler = modelView.getTransferHandler();
                if (modelViewOriginalTrnadferHandler instanceof ModelViewDragHandler) {
                    modelView.setTransferHandler(new QueriesDragHandler((ModelViewDragHandler) modelViewOriginalTrnadferHandler, modelView));
                }
                modelView.getUndoSupport().addUndoableEditListener(dataObject.getLookup().lookup(PlatypusQuerySupport.class).getUndo());
                modelView.addContainerListener(new ContainerListener() {
                    @Override
                    public void componentAdded(ContainerEvent e) {
                        querySchemeScroll.checkComponents();
                    }

                    @Override
                    public void componentRemoved(ContainerEvent e) {
                    }
                });

                mnu2Left.setAction(modelView.getActionMap().get(ModelView.GoLeft.class.getSimpleName()));
                mnu2Right.setAction(modelView.getActionMap().get(ModelView.GoRight.class.getSimpleName()));
                mnuDelete.setAction(modelView.getActionMap().get(ModelView.Delete.class.getSimpleName()));
                mnuCut.setAction(modelView.getActionMap().get(ModelView.Cut.class.getSimpleName()));
                mnuCopy.setAction(modelView.getActionMap().get(ModelView.Copy.class.getSimpleName()));
                mnuPaste.setAction(modelView.getActionMap().get(ModelView.Paste.class.getSimpleName()));

                querySchemeScroll = new JScalableScrollPane();
                querySchemeScroll.setViewportView(modelView);
                querySchemeScroll.getScalablePanel().getDrawWall().setComponentPopupMenu(popupFromNWhere);

                querySchemeScroll.addScaleListener((float oldScale, float newScale) -> {
                    comboZoom.setModel(new DefaultComboBoxModel<>(zoomLevelsData));
                    String newSelectedZoom = String.valueOf(Math.round(newScale * 100)) + "%";
                    if (((DefaultComboBoxModel<String>) comboZoom.getModel()).getIndexOf(newSelectedZoom) == -1) {
                        ((DefaultComboBoxModel<String>) comboZoom.getModel()).insertElementAt(newSelectedZoom, 0);
                    }
                    comboZoom.setSelectedItem(newSelectedZoom);
                });
                tablesSelector.setBasesProxy(dataObject.getBasesProxy());
                setupDiagramToolbar();
                pnlFromNWhere.add(querySchemeScroll, BorderLayout.CENTER);
                modelView.addModelSelectionListener(new ModelSelectionListener<QueryEntity>() {
                    @Override
                    public void selectionChanged(Set<QueryEntity> oldSelected, Set<QueryEntity> newSelected) {
                        try {
                            Node[] oldNodes = getActivatedNodes();
                            Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getModelNode(), oldNodes, oldSelected, newSelected);
                            setActivatedNodes(newNodes);
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }

                    @Override
                    public void selectionChanged(List<SelectedField<QueryEntity>> aParameters, List<SelectedField<QueryEntity>> aFields) {
                        try {
                            Node[] oldNodes = getActivatedNodes();
                            Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getModelNode(), oldNodes, aParameters, aFields);
                            setActivatedNodes(newNodes);
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }

                    @Override
                    public void selectionChanged(Collection<Relation<QueryEntity>> clctn, Collection<Relation<QueryEntity>> clctn1) {
                    }
                });
                UndoRedo ur = getUndoRedo();
                if (ur instanceof UndoRedo.Manager) {
                    ((UndoRedo.Manager) ur).discardAllEdits();
                }
                boolean oldModelModified = dataObject.isModelModified();
                try {
                    dataObject.getModel().fireAllQueriesChanged();
                } finally {
                    dataObject.setModelModified(oldModelModified);
                }
                componentActivated();
            } else {
                String dbValidatingMissingMessage = NbBundle.getMessage(PlatypusQueryView.class, "LBL_Db_Validating");
                mnu2Left.setText(dbValidatingMissingMessage);
                mnu2Right.setText(dbValidatingMissingMessage);
                mnuDelete.setText(dbValidatingMissingMessage);
                mnuCut.setText(dbValidatingMissingMessage);
                mnuCopy.setText(dbValidatingMissingMessage);
                mnuPaste.setText(dbValidatingMissingMessage);
                tablesSelector.setBasesProxy(null);
                pnlFromNWhere.add(dataObject.getProject().generateDbValidatePlaceholder(), BorderLayout.CENTER);
            }
        } else {
            String clientMissingMessage = NbBundle.getMessage(PlatypusQueryView.class, "LBL_Client_Missing");
            mnu2Left.setText(clientMissingMessage);
            mnu2Right.setText(clientMissingMessage);
            mnuDelete.setText(clientMissingMessage);
            mnuCut.setText(clientMissingMessage);
            mnuCopy.setText(clientMissingMessage);
            mnuPaste.setText(clientMissingMessage);
            tablesSelector.setBasesProxy(null);
            pnlFromNWhere.add(dataObject.getProject().generateDbPlaceholder(dataObject.getDatasourceName()), BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    protected Component initCustomEditor(JEditorPane aPane) {
        if (aPane.getDocument() instanceof NbDocument.CustomEditor) {
            NbDocument.CustomEditor ce = (NbDocument.CustomEditor) aPane.getDocument();
            Component customComponent = ce.createEditor(aPane);

            if (customComponent == null) {
                throw new IllegalStateException(
                        "Document:" + aPane.getDocument() // NOI18N
                        + " implementing NbDocument.CustomEditor may not" // NOI18N
                        + " return null component" // NOI18N
                );
            }
            return customComponent;
        }
        return null;
    }

    public static JButton createToolbarButton() {
        JButton aBtn = new JButton();
        aBtn.setFocusable(false);
        aBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aBtn.setHideActionText(true);
        aBtn.setMaximumSize(BTN_DIMENSION);
        aBtn.setMaximumSize(BTN_DIMENSION);
        aBtn.setPreferredSize(BTN_DIMENSION);
        return aBtn;
    }

    private JMenuItem createMenuItem() {
        JMenuItem aBtn = new JMenuItem();
        return aBtn;
    }

    protected void setupDiagramToolbar() {
        JToolBar toolsDbEntities = new JToolBar();

        JDropDownButton addBtn = new JDropDownButton();
        addBtn.setFocusable(false);
        addBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addBtn.setMaximumSize(BTN_DIMENSION);
        addBtn.setMaximumSize(BTN_DIMENSION);
        addBtn.setPreferredSize(BTN_DIMENSION);

        addBtn.setHideActionText(true);

        JButton btnAddField = new JButton();
        btnAddField.setAction(modelView.getActionMap().get(ModelView.AddField.class.getSimpleName()));
        btnAddField.setFocusable(false);
        btnAddField.setHideActionText(true);
        btnAddField.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddField.setMaximumSize(BTN_DIMENSION);
        btnAddField.setMaximumSize(BTN_DIMENSION);
        btnAddField.setPreferredSize(BTN_DIMENSION);
        btnAddField.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        JButton delBtn = new JButton();
        delBtn.setFocusable(false);
        delBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        delBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        delBtn.setMaximumSize(BTN_DIMENSION);
        delBtn.setMaximumSize(BTN_DIMENSION);
        delBtn.setPreferredSize(BTN_DIMENSION);
        delBtn.setHideActionText(true);

        JPopupMenu addMenu = new JPopupMenu();
        JMenuItem mnuAddTable = createMenuItem();
        JMenuItem mnuAddQuery = createMenuItem();
        JButton bntRunQuery = createToolbarButton();
        JButton btnZoomIn = createToolbarButton();
        JButton btnZoomOut = createToolbarButton();
        JButton btnFind = createToolbarButton();

        JLabel lblZoom = new JLabel();
        lblZoom.setText(NbBundle.getMessage(PlatypusQueryView.class, "lblZoom"));
        JPanel pnlZoom = new JPanel(new BorderLayout());
        JPanel pnlZoom1 = new JPanel(new BorderLayout());
        pnlZoom1.add(lblZoom, BorderLayout.CENTER);
        pnlZoom1.add(comboZoom, BorderLayout.EAST);
        pnlZoom.add(pnlZoom1, BorderLayout.EAST);
        comboZoom.setModel(new DefaultComboBoxModel<>(zoomLevelsData));
        comboZoom.setSelectedItem("100%");

        mnuAddTable.setAction(modelView.getActionMap().get(ModelView.AddTable.class.getSimpleName()));
        mnuAddQuery.setAction(modelView.getActionMap().get(AddQueryAction.class.getSimpleName()));

        comboZoom.setAction(modelView.getActionMap().get(ModelView.Zoom.class.getSimpleName()));
        bntRunQuery.setAction(new RunQueryAction());
        btnZoomIn.setAction(modelView.getActionMap().get(ModelView.ZoomIn.class.getSimpleName()));
        btnZoomOut.setAction(modelView.getActionMap().get(ModelView.ZoomOut.class.getSimpleName()));
        btnFind.setAction(modelView.getActionMap().get(ModelView.Find.class.getSimpleName()));

        addMenu.add(mnuAddTable);
        addMenu.add(mnuAddQuery);

        addBtn.setAction(modelView.getActionMap().get(ModelView.AddTable.class.getSimpleName()));
        addBtn.setDropDownMenu(addMenu);
        delBtn.setAction(modelView.getActionMap().get(ModelView.Delete.class.getSimpleName()));

        toolsDbEntities.add(addBtn);
        toolsDbEntities.add(btnAddField);
        toolsDbEntities.add(delBtn);
        toolsDbEntities.add(bntRunQuery);
        toolsDbEntities.add(btnZoomIn);
        toolsDbEntities.add(btnZoomOut);
        toolsDbEntities.add(btnFind);
        toolsDbEntities.add(pnlZoom);

        toolsDbEntities.setRollover(true);
        toolsDbEntities.setFloatable(false);
        pnlFromNWhere.add(toolsDbEntities, BorderLayout.NORTH);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        try {
            super.readExternal(oi);
            setDataObject((PlatypusQueryDataObject) oi.readObject());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
        oo.writeObject(dataObject);
    }

    public void updateTitle() {
        String boldTitleMask = "<html><b>%s</b>";
        String plainTitleMask = "<html>%s";
        String titleMask = plainTitleMask;
        if (dataObject.isModified()) {
            titleMask = boldTitleMask;
        }
        final String newTitle = String.format(titleMask, getName());
        if (EventQueue.isDispatchThread()) {
            setHtmlDisplayName(newTitle);
        } else {
            EventQueue.invokeLater(() -> {
                setHtmlDisplayName(newTitle);
            });
        }
    }

    @Override
    public UndoRedo getUndoRedo() {
        PlatypusQuerySupport support = dataObject.getLookup().lookup(PlatypusQuerySupport.class);
        return support.getUndo();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public void componentOpened() {
        try {
            super.componentOpened();
            updateTitle();
            dataObject.addPropertyChangeListener(dataObjectListener);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public boolean canClose() {
        PlatypusQuerySupport support = dataObject.getLookup().lookup(PlatypusQuerySupport.class);
        List<CloneableTopComponent> views = support.getAllViews();
        if (views != null && views.size() == 1) {
            return support.canClose();
        }
        return super.canClose();
    }

    @Override
    public void componentClosed() {
        try {
            super.componentClosed();
            // unsubscribe view from the dataObject
            dataObject.removePropertyChangeListener(dataObjectListener);
            // unsubscribe view from the model
            if (modelView != null) {
                modelView.setModel(null);
            }
            if (clientChangeListener != null) {
                clientChangeListener.remove();
            }
            if (modelValidChangeListener != null) {
                modelValidChangeListener.remove();
            }
            PlatypusQuerySupport support = dataObject.getLookup().lookup(PlatypusQuerySupport.class);
            support.shrink();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    protected QueryModelView getModelView() {
        return modelView;
    }

    @Override
    public Action[] getActions() {
        List<Action> actions = new ArrayList<>(Arrays.asList(super.getActions()));
        // XXX nicer to use MimeLookup for type-specific actions, but not easy; see org.netbeans.modules.editor.impl.EditorActionsProvider
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Editors/TabActions")); //NOI18N
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    protected void componentActivated() {
        try {
            if (dataObject.isValid()) {
                if (getModelView() != null) {
                    ModelInspector.getInstance().setNodesReflector(explorerSelectionListener);
                    ModelInspector.getInstance().setViewData(new ModelInspector.ViewData<>(getModelView(), getUndoRedo(), dataObject.getModelNode()));
                    WindowManager wm = WindowManager.getDefault();
                    final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_QUERIES_GROUP_NAME); // NOI18N
                    if (group != null) {
                        group.open();
                    }
                }
            }
            super.componentActivated();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    private void setOpenedSqlEditorPane() {
        PlatypusQuerySupport support = dataObject.getLookup().lookup(PlatypusQuerySupport.class);
        if (support != null) {
            if (tabContent.getSelectedComponent() == pnlQuerySource) {
                support.setOpenedPane(txtSqlPane);
            } else if (tabContent.getSelectedComponent() == pnlQueryDialectSource) {
                support.setOpenedPane(txtSqlDialectPane);
            }
        }
    }

    @Override
    protected void componentHidden() {
        super.componentHidden();
        if (ModelInspector.getInstance().getViewData() != null
                && ModelInspector.getInstance().getViewData().getModelView() == getModelView()) {
            ModelInspector.getInstance().setNodesReflector(null);
            ModelInspector.getInstance().setViewData(null);
            WindowManager wm = WindowManager.getDefault();
            final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_QUERIES_GROUP_NAME); // NOI18N
            if (group != null) {
                group.close();
            }
        }
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupFromNWhere = new javax.swing.JPopupMenu();
        mnu2Left = new javax.swing.JMenuItem();
        mnu2Right = new javax.swing.JMenuItem();
        sepNav = new javax.swing.JSeparator();
        mnuDelete = new javax.swing.JMenuItem();
        sepNav2 = new javax.swing.JPopupMenu.Separator();
        mnuCut = new javax.swing.JMenuItem();
        mnuCopy = new javax.swing.JMenuItem();
        mnuPaste = new javax.swing.JMenuItem();
        pnlDiagram = new javax.swing.JPanel();
        splitMain = new javax.swing.JSplitPane();
        pnlFromNWhere = new javax.swing.JPanel();
        tabContent = new javax.swing.JTabbedPane();
        pnlQuerySource = new javax.swing.JPanel();
        pnlSqlSource = new javax.swing.JPanel();
        scrollSqlPane = new javax.swing.JScrollPane();
        txtSqlPane = new javax.swing.JEditorPane();
        pnlQueryDialectSource = new javax.swing.JPanel();
        pnlSqlDialectSource = new javax.swing.JPanel();
        scrollSqlDialectPane = new javax.swing.JScrollPane();
        txtSqlDialectPane = new javax.swing.JEditorPane();

        popupFromNWhere.add(mnu2Left);
        popupFromNWhere.add(mnu2Right);
        popupFromNWhere.add(sepNav);
        popupFromNWhere.add(mnuDelete);
        popupFromNWhere.add(sepNav2);
        popupFromNWhere.add(mnuCut);
        popupFromNWhere.add(mnuCopy);
        popupFromNWhere.add(mnuPaste);

        setLayout(new java.awt.BorderLayout());

        splitMain.setDividerLocation(300);
        splitMain.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlFromNWhere.setLayout(new java.awt.BorderLayout());
        splitMain.setTopComponent(pnlFromNWhere);

        tabContent.setPreferredSize(new java.awt.Dimension(141, 100));
        tabContent.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabContentStateChanged(evt);
            }
        });

        pnlQuerySource.setLayout(new java.awt.BorderLayout());

        pnlSqlSource.setLayout(new java.awt.BorderLayout());

        scrollSqlPane.setViewportView(txtSqlPane);

        pnlSqlSource.add(scrollSqlPane, java.awt.BorderLayout.CENTER);

        pnlQuerySource.add(pnlSqlSource, java.awt.BorderLayout.CENTER);

        tabContent.addTab(NbBundle.getMessage(PlatypusQueryView.class, "PlatypusQueryView.pnlQuerySource.TabConstraints.tabTitle"), pnlQuerySource); // NOI18N

        pnlQueryDialectSource.setLayout(new java.awt.BorderLayout());

        pnlSqlDialectSource.setLayout(new java.awt.BorderLayout());

        scrollSqlDialectPane.setViewportView(txtSqlDialectPane);

        pnlSqlDialectSource.add(scrollSqlDialectPane, java.awt.BorderLayout.CENTER);

        pnlQueryDialectSource.add(pnlSqlDialectSource, java.awt.BorderLayout.CENTER);

        tabContent.addTab(NbBundle.getMessage(PlatypusQueryView.class, "PlatypusQueryView.pnlQueryDialectSource.TabConstraints.tabTitle"), pnlQueryDialectSource); // NOI18N

        splitMain.setBottomComponent(tabContent);

        javax.swing.GroupLayout pnlDiagramLayout = new javax.swing.GroupLayout(pnlDiagram);
        pnlDiagram.setLayout(pnlDiagramLayout);
        pnlDiagramLayout.setHorizontalGroup(
            pnlDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
        );
        pnlDiagramLayout.setVerticalGroup(
            pnlDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
        );

        add(pnlDiagram, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tabContentStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabContentStateChanged
        setOpenedSqlEditorPane();
    }//GEN-LAST:event_tabContentStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem mnu2Left;
    private javax.swing.JMenuItem mnu2Right;
    private javax.swing.JMenuItem mnuCopy;
    private javax.swing.JMenuItem mnuCut;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenuItem mnuPaste;
    private javax.swing.JPanel pnlDiagram;
    private javax.swing.JPanel pnlFromNWhere;
    private javax.swing.JPanel pnlQueryDialectSource;
    private javax.swing.JPanel pnlQuerySource;
    private javax.swing.JPanel pnlSqlDialectSource;
    private javax.swing.JPanel pnlSqlSource;
    private javax.swing.JPopupMenu popupFromNWhere;
    private javax.swing.JScrollPane scrollSqlDialectPane;
    private javax.swing.JScrollPane scrollSqlPane;
    private javax.swing.JSeparator sepNav;
    private javax.swing.JSeparator sepNav2;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTabbedPane tabContent;
    private javax.swing.JEditorPane txtSqlDialectPane;
    private javax.swing.JEditorPane txtSqlPane;
    // End of variables declaration//GEN-END:variables
}
