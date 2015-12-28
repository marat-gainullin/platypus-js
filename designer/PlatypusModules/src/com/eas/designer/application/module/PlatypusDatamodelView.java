/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.gui.ApplicationModelEditorView;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.ModelViewDragHandler;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ApplicationModelView;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import com.eas.designer.datamodel.ModelUndoProvider;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.model.windows.QueriesDragHandler;
import com.eas.designer.explorer.model.windows.QueryDocumentJumper;
import com.eas.designer.explorer.selectors.QueriesSelector;
import com.eas.designer.explorer.selectors.TablesSelector;
import com.eas.util.ListenerRegistration;
import java.awt.BorderLayout;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
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
 * Top component which displays model
 */
@TopComponent.Description(preferredID = "platypus-model-view", persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
public final class PlatypusDatamodelView extends CloneableTopComponent implements MultiViewElement {

    protected class NodeSelectionListener implements PropertyChangeListener {

        protected boolean processing;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()) /*
                     * if you whant to uncomment the following line, you should ensure, that ccp
                     * operation on the nodes will not become slow
                     || ExplorerManager.PROP_NODE_CHANGE.equals(evt.getPropertyName())*/) {
                if (!processing) {
                    processing = true;
                    try {
                        Node[] nodes = ModelInspector.getInstance().getExplorerManager().getSelectedNodes();
                        appModelEditor.getModelView().silentClearSelection();
                        appModelEditor.getModelView().clearEntitiesFieldsSelection();
                        Map<EntityView<ApplicationDbEntity>, Set<Field>> toSelectFields = new HashMap<>();
                        Map<EntityView<ApplicationDbEntity>, Set<Parameter>> toSelectParameters = new HashMap<>();
                        for (Node node : nodes) {
                            EntityView<ApplicationDbEntity> ev;
                            if (node instanceof EntityNode<?>) {
                                ev = appModelEditor.getModelView().getEntityView(((EntityNode<ApplicationDbEntity>) node).getEntity());
                                appModelEditor.getModelView().silentSelectView(ev);
                            } else if (node instanceof FieldNode) {
                                if (node.getParentNode() != null) {
                                    ev = appModelEditor.getModelView().getEntityView(((EntityNode<ApplicationDbEntity>) node.getParentNode()).getEntity());
                                    FieldNode fieldNode = (FieldNode) node;
                                    if ((fieldNode.getField() instanceof Parameter)) {
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
                        }
                        toSelectParameters.entrySet().stream().forEach((pEntry) -> {
                            EntityView<ApplicationDbEntity> ev = pEntry.getKey();
                            ev.addSelectedParameters(pEntry.getValue());
                        });
                        toSelectFields.entrySet().stream().forEach((fEntry) -> {
                            EntityView<ApplicationDbEntity> ev = fEntry.getKey();
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

    static final long serialVersionUID = 53142032923497728L;

    public static final String PLATYPUS_MODULES_GROUP_NAME = "PlatypusModel";
    protected transient JPanel toolsPnl = new JPanel();
    protected transient MultiViewElementCallback multiViewObserver;
    protected transient ApplicationModelEditorView appModelEditor;
    protected transient NodeSelectionListener exlorerSelectionListener = new NodeSelectionListener();
    //protected transient ListenerRegistration clientChangeListener;
    protected transient ListenerRegistration modelValidChangeListener;
    protected transient ExplorerManager explorerManager;
    protected PlatypusDataObject dataObject;

    public PlatypusDatamodelView() {
        super();
    }

    public PlatypusDatamodelView(PlatypusDataObject aDataObject) throws Exception {
        this();
        setDataObject(aDataObject);
    }

    protected void setDataObject(PlatypusDataObject aDataObject) throws Exception {
        if (dataObject != aDataObject) {
            dataObject = aDataObject;
            setIcon(aDataObject.getNodeDelegate().getIcon(java.beans.BeanInfo.ICON_COLOR_16x16));
            // Hack! NetBeans doesn't properly handle activated nodes in multi view's elements
            // So, we need to use dummy explorer manager and it's lookup, associated with this multiview element TopComponent
            // to produce satisfactory events.
            explorerManager = new ExplorerManager();
            associateLookup(new ProxyLookup(new Lookup[]{
                ExplorerUtils.createLookup(explorerManager, getActionMap()),
                Lookups.singleton(aDataObject)
            }));
            initDbRelatedViews();
            modelValidChangeListener = dataObject.addModelValidChangeListener(() -> {
                try {
                    initDbRelatedViews();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            });
            /*
            clientChangeListener = dataObject.addClientChangeListener(new PlatypusProject.ClientChangeListener() {

                @Override
                public void connected(String aDatasourceName) {
                    try {
                        dataObject.setModelValid(false);
                        dataObject.startModelValidating();
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }

                @Override
                public void disconnected(String aDatasourceName) {
                    try {
                        dataObject.setModelValid(false);
                        dataObject.startModelValidating();
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }

                @Override
                public void defaultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName) {
                    try {
                        dataObject.setModelValid(false);
                        dataObject.startModelValidating();
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            });
            */
        }
    }

    public ApplicationModelView getModelView() {
        return appModelEditor != null ? appModelEditor.getModelView() : null;
    }

    protected void initDbRelatedViews() throws Exception {
        removeAll();
        setLayout(new BorderLayout());
        assert dataObject.getBasesProxy() != null;
        if (dataObject.isModelValid()) {
            TablesSelector tablesSelector = new TablesSelector(dataObject.getProject(), true, true, NbBundle.getMessage(PlatypusDatamodelView.class, "LBL_PlatypusModule_View_Name"), PlatypusDatamodelView.this);
            QueriesSelector queriesSelector = new QueriesSelector(dataObject.getAppRoot()) {
                @Override
                public void fillAllowedMimeTypes(Set<String> allowedMimeTypes) {
                    super.fillAllowedMimeTypes(allowedMimeTypes);
                    allowedMimeTypes.add(PlatypusPathRecognizer.JAVASCRIPT_MIME_TYPE);
                }
            };
            if (appModelEditor != null) {
                appModelEditor.setModel(null);
            }
            appModelEditor = new ApplicationModelEditorView(tablesSelector, queriesSelector);
            appModelEditor.getModelView().addEntityViewDoubleClickListener(new QueryDocumentJumper<>(dataObject.getProject()));
            ApplicationDbModel model = dataObject.getLookup().lookup(ModelProvider.class).getModel();
            appModelEditor.setModel(model);
            appModelEditor.setBorder(new EmptyBorder(0, 0, 0, 0));
            add(appModelEditor, BorderLayout.CENTER);
            TransferHandler modelViewOriginalTrnadferHandler = appModelEditor.getModelView().getTransferHandler();
            if (modelViewOriginalTrnadferHandler instanceof ModelViewDragHandler) {
                appModelEditor.getModelView().setTransferHandler(new QueriesDragHandler((ModelViewDragHandler) modelViewOriginalTrnadferHandler, appModelEditor.getModelView()));
            }

            appModelEditor.setUndo(new UndoManager() {
                @Override
                public synchronized boolean addEdit(UndoableEdit anEdit) {
                    ModelUndoProvider mup = dataObject.getLookup().lookup(ModelUndoProvider.class);
                    mup.getModelUndo().undoableEditHappened(new UndoableEditEvent(this, anEdit));
                    return true;
                }
            });
            appModelEditor.getModelView().addModelSelectionListener(new ModelSelectionListener<ApplicationDbEntity>() {
                @Override
                public void selectionChanged(Set<ApplicationDbEntity> oldSelected, Set<ApplicationDbEntity> newSelected) {
                    try {
                        Node[] oldNodes = getActivatedNodes();
                        // Hack. When multi-view element with no any activated node is activated,
                        // NetBeans' property sheet stay with a node from previous multi-view element.
                        // So, we need to simulate non-empty activated nodes and take this into account
                        // here.
                        if (oldNodes != null && oldNodes.length == 1 && oldNodes[0] == (dataObject.getBasesProxy() != null ? dataObject.getLookup().lookup(ModelProvider.class).getModelNode() : dataObject.getNodeDelegate())) {
                            oldNodes = new Node[]{};
                        }
                        Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getLookup().lookup(ModelProvider.class).getModelNode(), oldNodes, oldSelected, newSelected);
                        // Hack! NetBeans doesn't properly handle activated nodes in multi view's elements
                        // So, we need to use dummy explorer manager and it's lookup, associated with this multiview element TopComponent
                        // to produce satisfactory events.
                        explorerManager.setSelectedNodes(newNodes);
                        setActivatedNodes(newNodes);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }

                @Override
                public void selectionChanged(List<SelectedField<ApplicationDbEntity>> aParameters, List<SelectedField<ApplicationDbEntity>> aFields) {
                    try {
                        Node[] oldNodes = getActivatedNodes();
                        // Hack. When multi-view element with no any activated node is activated,
                        // NetBeans' property sheet stay with a node from previous multi-view element.
                        // So, we need to simulate non-empty activated nodes and take this into account
                        // here.
                        if (oldNodes != null && oldNodes.length == 1 && oldNodes[0] == (dataObject.getBasesProxy() != null ? dataObject.getLookup().lookup(ModelProvider.class).getModelNode() : dataObject.getNodeDelegate())) {
                            oldNodes = new Node[]{};
                        }
                        Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getLookup().lookup(ModelProvider.class).getModelNode(), oldNodes, aParameters, aFields);
                        // Hack! NetBeans doesn't properly handle activated nodes in multi view's elements
                        // So, we need to use dummy explorer manager and it's lookup, associated with this multiview element TopComponent
                        // to produce satisfactory events.
                        explorerManager.setSelectedNodes(newNodes);
                        setActivatedNodes(newNodes);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }

                @Override
                public void selectionChanged(Collection<Relation<ApplicationDbEntity>> clctn, Collection<Relation<ApplicationDbEntity>> clctn1) {
                }
            });
            explorerManager.setRootContext(dataObject.getLookup().lookup(ModelProvider.class).getModelNode());
            getModelView().complementReferenceRelationsByKeys((ReferenceRelation<ApplicationDbEntity> aRelation) -> {
                try {
                    dataObject.getLookup().lookup(ModelProvider.class).getModel().addReferenceRelation(aRelation);
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusDatamodelView.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            });
            UndoRedo ur = getUndoRedo();
            if (ur instanceof UndoRedo.Manager) {
                ((UndoRedo.Manager) ur).discardAllEdits();
            }
            boolean oldModelModified = dataObject.getLookup().lookup(ModelModifiedProvider.class).isModelModified();
            try {
                dataObject.getLookup().lookup(ModelProvider.class).getModel().fireAllQueriesChanged();
            } finally {
                dataObject.getLookup().lookup(ModelModifiedProvider.class).setModelModified(oldModelModified);
            }
            componentActivated();
        } else {
            explorerManager.setRootContext(dataObject.getNodeDelegate());
            add(dataObject.getProject().generateDbValidatePlaceholder(), BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws java.io.IOException {
        super.writeExternal(out);
        out.writeObject(dataObject);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        try {
            setDataObject((PlatypusDataObject) in.readObject());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
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
    public void componentOpened() {
        super.componentOpened();
        updateName();
    }

    //Let's show the same popup menu items as for source editor
    @Override
    public Action[] getActions() {
        List<Action> actions = new ArrayList<>(Arrays.asList(super.getActions()));
        // XXX nicer to use MimeLookup for type-specific actions, but not easy; see org.netbeans.modules.editor.impl.EditorActionsProvider
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Editors/TabActions")); //NOI18N
        return actions.toArray(new Action[]{});
    }

    public void updateName() {
        setHtmlDisplayName(getHtmlDisplayName());
        setDisplayName(getDisplayName());
        if (multiViewObserver != null) {
            multiViewObserver.getTopComponent().setHtmlDisplayName(getHtmlDisplayName());
            multiViewObserver.updateTitle(dataObject.getName());
        }
    }

    @Override
    public String getHtmlDisplayName() {
        String ldisplayName = "<html>" + dataObject.getName();
        if (dataObject.isModified()) {
            ldisplayName = "<html><b>" + dataObject.getName() + "</b>";
        }
        return ldisplayName;
    }

    @Override
    public void componentActivated() {
        try {
            if (dataObject.isValid() && dataObject.getBasesProxy() != null) {
                ModelInspector.getInstance().setNodesReflector(exlorerSelectionListener);
                ModelInspector.getInstance().setViewData(new ModelInspector.ViewData<>(getModelView(), getUndoRedo(), dataObject.getLookup().lookup(ModelProvider.class).getModelNode()));
                WindowManager wm = WindowManager.getDefault();
                final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_MODULES_GROUP_NAME);
                if (group != null) {
                    group.open();
                }
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
                    activated = new Node[]{dataObject.getBasesProxy() != null ? dataObject.getLookup().lookup(ModelProvider.class).getModelNode() : dataObject.getNodeDelegate()};
                }
                explorerManager.setSelectedNodes(activated);
                setActivatedNodes(activated);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
        if (dataObject.getBasesProxy() != null && ModelInspector.getInstance() != null && ModelInspector.getInstance().getViewData() != null && ModelInspector.getInstance().getViewData().getModelView() == getModelView()) {
            ModelInspector.getInstance().setNodesReflector(null);
            ModelInspector.getInstance().setViewData(null);
            WindowManager wm = WindowManager.getDefault();
            final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_MODULES_GROUP_NAME); // NOI18N
            if (group != null) {
                group.close();
            }
        }
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        PlatypusModuleSupport pms = dataObject.getLookup().lookup(PlatypusModuleSupport.class);
        if (pms != null) {
            pms.shrink();
        }
        /*
        if (clientChangeListener != null) {
            clientChangeListener.remove();
        }
        */
        if (modelValidChangeListener != null) {
            modelValidChangeListener.remove();
        }
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolsPnl;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback aCallback) {
        multiViewObserver = aCallback;
    }

    @Override
    protected CloneableTopComponent createClonedObject() {
        try {
            return new PlatypusDatamodelView(dataObject);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected boolean closeLast() {
        if (multiViewObserver != null) {
            return true;
        } else if (dataObject.getLookup().lookup(ModelModifiedProvider.class).isModelModified()) {
            PlatypusModelSupport singleLayoutSupport = dataObject.getLookup().lookup(PlatypusModelSupport.class);
            return singleLayoutSupport.canClose();
        } else {
            return true;
        }
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public UndoRedo getUndoRedo() {
        return dataObject.getLookup().lookup(ModelUndoProvider.class).getModelUndo();
    }
}
