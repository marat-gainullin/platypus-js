/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram;

import com.eas.client.dbstructure.gui.DbSchemeEditorView;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.dbstructure.gui.view.DbSchemeModelView;
import com.eas.client.metadata.Field;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.selectors.TablesSelector;
import com.eas.util.ListenerRegistration;
import java.awt.BorderLayout;
import java.awt.EventQueue;
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
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
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
@TopComponent.Description(preferredID = "platypus-db-diagram-view", persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
        iconBase = "com/eas/designer/application/dbdiagram/dbScheme.png")
public class PlatypusDbDiagramView extends CloneableTopComponent {

    protected class DataObjectListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("modified".equals(evt.getPropertyName())) {
                updateTitle();
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
                        Map<EntityView<FieldsEntity>, Set<Field>> toSelectFields = new HashMap<>();
                        for (Node node : nodes) {
                            EntityView<FieldsEntity> ev;
                            if (node instanceof EntityNode) {
                                ev = getModelView().getEntityView(((EntityNode<FieldsEntity>) node).getEntity());
                                getModelView().silentSelectView(ev);
                                editor.checkActions();
                            } else if (node instanceof FieldNode) {
                                ev = getModelView().getEntityView(((EntityNode<FieldsEntity>) node.getParentNode()).getEntity());
                                FieldNode fieldNode = (FieldNode) node;
                                if (!toSelectFields.containsKey(ev)) {
                                    toSelectFields.put(ev, new HashSet<>());
                                }
                                toSelectFields.get(ev).add(fieldNode.getField());
                                //ev.addSelectedField(fieldNode.getField());
                            }
                        }
                        toSelectFields.entrySet().stream().forEach((fEntry) -> {
                            EntityView<FieldsEntity> ev = fEntry.getKey();
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
    public static final String PLATYPUS_DIAGRAMS_GROUP_NAME = "PlatypusModel";
    static final long serialVersionUID = 1141132023402024L;
    /**
     * path to the icon used by the component and its open action
     */
    protected PlatypusDbDiagramDataObject dataObject;
    protected transient DbSchemeEditorView editor;
    protected transient DataObjectListener dataObjectListener;
    protected transient NodeSelectionListener exlorerSelectionListener = new NodeSelectionListener();
    protected transient ListenerRegistration clientChangeListener;
    protected transient ListenerRegistration modelValidChangeListener;

    public PlatypusDbDiagramView() throws Exception {
        super();
    }

    @Override
    public Lookup getLookup() {
        return new ProxyLookup(super.getLookup(), Lookups.singleton(getDataObject()));
    }

    public PlatypusDbDiagramDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(PlatypusDbDiagramDataObject aDataObject) throws Exception {
        dataObject = aDataObject;
        dataObjectListener = new DataObjectListener();
        setName(dataObject.getPrimaryFile().getName());
        setToolTipText(NbBundle.getMessage(PlatypusDbDiagramView.class, "HINT_PlatypusDbDiagramTopComponent", dataObject.getPrimaryFile().getPath()));
        initDbRelatedViews();

        modelValidChangeListener = dataObject.addModelValidChangeListener(() -> {
            try {
                initDbRelatedViews();
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        });
        clientChangeListener = dataObject.addClientChangeListener(new PlatypusProject.ClientChangeListener() {

            @Override
            public void connected(String aDatasourceName) {
                try {
                    String dsName = dataObject.getModel().getDatasourceName();
                    if (dsName == null) {
                        dsName = dataObject.getProject().getSettings().getDefaultDataSourceName();
                    }
                    if (dsName == null ? aDatasourceName == null : dsName.equals(aDatasourceName)) {
                        initDbRelatedViews();
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public void disconnected(String aDatasourceName) {
                try {
                    String dsName = dataObject.getModel().getDatasourceName();
                    if (dsName == null) {
                        dsName = dataObject.getProject().getSettings().getDefaultDataSourceName();
                    }
                    if (dsName == null ? aDatasourceName == null : dsName.equals(aDatasourceName)) {
                        initDbRelatedViews();
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public void defaultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName) {
                try {
                    if (dataObject.getModel().getDatasourceName()== null && dataObject.isModelValid()) {
                        dataObject.setModelValid(false);
                        dataObject.startModelValidating();
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        try {
            super.readExternal(oi);
            setDataObject((PlatypusDbDiagramDataObject) oi.readObject());
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
        String boldTitleMask = "<html><b>%s [%s.%s]</b>";
        String plainTitleMask = "<html>%s [%s.%s]";
        String titleMask = plainTitleMask;
        if (dataObject.isModified()) {
            titleMask = boldTitleMask;
        }        
        final String newTitle = String.format(titleMask, getName(), dataObject.getResolvedDatasourceName(), dataObject.getResolvedSchemaName());
        if (EventQueue.isDispatchThread()) {
            setHtmlDisplayName(newTitle);
        } else {
            EventQueue.invokeLater(() -> {
                setHtmlDisplayName(newTitle);
            });
        }
    }

    private void initDbRelatedViews() throws Exception {
        removeAll();
        setLayout(new BorderLayout());
        if (dataObject.getProject().isDbConnected(dataObject.getModel().getDatasourceName())) {
            if (dataObject.isModelValid()) {
                if (editor != null) {
                    editor.setModel(null);
                }
                editor = new DbSchemeEditorView(dataObject.getModel(),
                        new TablesSelector(dataObject.getProject(),
                                NbBundle.getMessage(PlatypusDbDiagramView.class, "HINT_PlatypusDbDiagramTopComponent", dataObject.getPrimaryFile().getName()), PlatypusDbDiagramView.this),
                        new UndoManager() {
                            @Override
                            public synchronized boolean addEdit(UndoableEdit anEdit) {
                                ((UndoRedo.Manager) getUndoRedo()).undoableEditHappened(new UndoableEditEvent(this, anEdit));
                                return true;
                            }
                        });
                add(editor, BorderLayout.CENTER);
                updateTitle();
                dataObject.addPropertyChangeListener(dataObjectListener);
                getModelView().resolveTables();
                getModelView().resolveRelations();
                getModelView().addModelSelectionListener(new ModelSelectionListener<FieldsEntity>() {
                    @Override
                    public void selectionChanged(Set<FieldsEntity> oldSelected, Set<FieldsEntity> newSelected) {
                        try {
                            Node[] oldNodes = getActivatedNodes();
                            Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getModelNode(), oldNodes, oldSelected, newSelected);
                            setActivatedNodes(newNodes);
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }

                    @Override
                    public void selectionChanged(List<SelectedField<FieldsEntity>> aParameters, List<SelectedField<FieldsEntity>> aFields) {
                        try {
                            Node[] oldNodes = getActivatedNodes();
                            Node[] newNodes = ModelInspector.convertSelectedToNodes(dataObject.getModelNode(), oldNodes, aParameters, aFields);
                            setActivatedNodes(newNodes);
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }

                    @Override
                    public void selectionChanged(Collection<Relation<FieldsEntity>> clctn, Collection<Relation<FieldsEntity>> clctn1) {
                    }
                });
                UndoRedo ur = getUndoRedo();
                if (ur instanceof UndoRedo.Manager) {
                    ((UndoRedo.Manager) ur).discardAllEdits();
                }
                dataObject.getModel().fireAllQueriesChanged();
                componentActivated();
            } else {
                add(dataObject.getProject().generateDbValidatePlaceholder(), BorderLayout.CENTER);
            }
        } else {
            add(dataObject.getProject().generateDbPlaceholder(dataObject.getModel().getDatasourceName()), BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    @Override
    public UndoRedo getUndoRedo() {
        PlatypusDbDiagramSupport support = dataObject.getLookup().lookup(PlatypusDbDiagramSupport.class);
        return support.getModelUndo();
    }

    @Override
    public boolean canClose() {
        PlatypusDbDiagramSupport support = dataObject.getLookup().lookup(PlatypusDbDiagramSupport.class);
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
            if (editor != null) {
                editor.setModel(null);
            }
            PlatypusDbDiagramSupport support = dataObject.getLookup().lookup(PlatypusDbDiagramSupport.class);
            support.shrink();
            if (clientChangeListener != null) {
                clientChangeListener.remove();
            }
            if (modelValidChangeListener != null) {
                modelValidChangeListener.remove();
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    protected DbSchemeModelView getModelView() {
        return editor != null ? (DbSchemeModelView) editor.getEntitiesView() : null;
    }

    @Override
    protected void componentActivated() {
        try {
            if (dataObject.isValid() && dataObject.getBasesProxy()!= null && dataObject.isModelValid()) {
                ModelInspector.getInstance().setNodesReflector(exlorerSelectionListener);
                ModelInspector.getInstance().setViewData(new ModelInspector.ViewData<>(getModelView(), getUndoRedo(), dataObject.getModelNode()));
                WindowManager wm = WindowManager.getDefault();
                final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_DIAGRAMS_GROUP_NAME);
                if (group != null) {
                    group.open();
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        super.componentActivated();
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
    protected void componentHidden() {
        ModelInspector.getInstance().setNodesReflector(null);
        ModelInspector.getInstance().setViewData(null);
        WindowManager wm = WindowManager.getDefault();
        final TopComponentGroup group = wm.findTopComponentGroup(PLATYPUS_DIAGRAMS_GROUP_NAME);
        if (group != null) {
            group.close();
        }
        super.componentHidden();
    }
}
