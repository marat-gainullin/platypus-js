/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.DummyRelation;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewRelationEdit;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.plaf.ListUI;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public class RelationsFieldsDragHandler<E extends Entity<?, SqlQuery, E>> extends TransferHandler {

    protected static final String SCALABLE_COVER_DROP_LOCATION_TAG = "ScalableDropLocation";
    protected static final String SCALABLE_COVER_DROP_ACTION_TAG = "ScalableDropAction";
    protected ModelView<E, ?> modelView;
    protected EntityView<E> entityView;

    public RelationsFieldsDragHandler(ModelView<E, ?> aModelView, EntityView<E> aEntityView) {
        super();
        modelView = aModelView;
        entityView = aEntityView;
    }

    public EntityView<E> getEntityView() {
        return entityView;
    }

    public void setEntityView(EntityView<E> aView) {
        entityView = aView;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        Field lField = entityView.getSelectedField();
        Parameter lParameter = entityView.getSelectedParameter();
        return new TransferableRelation<>(entityView.getEntity(), lField != null ? lField : lParameter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY | MOVE;
    }

    protected Point getDropLocationPoint(TransferSupport support, JList<? extends Field> list) {
        DropLocation lDropLoc = null;
        Point lpt = null;
        if (support.isDrop()) {
            lDropLoc = support.getDropLocation();
        }
        if (lDropLoc != null) {
            lpt = lDropLoc.getDropPoint();
        } else {
            Object ldroploc = list.getClientProperty(SCALABLE_COVER_DROP_LOCATION_TAG);
            if (ldroploc != null && ldroploc instanceof DropLocation) {
                DropLocation sdroploc = (DropLocation) ldroploc;
                lpt = sdroploc.getDropPoint();
            }
        }
        return lpt;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        Transferable tr = support.getTransferable();
        if (tr != null && tr.isDataFlavorSupported(TransferableRelation.getDefaultDataFlavor())) {
            try {
                Object ldata = tr.getTransferData(TransferableRelation.getDefaultDataFlavor());
                if (ldata != null && ldata instanceof TransferableRelation) {
                    TransferableRelation<E> trRel = (TransferableRelation<E>) ldata;
                    E leftEntity = trRel.getEntity();
                    if (leftEntity != entityView.getEntity() || modelView.getModel() instanceof DbSchemeModel) {
                        Field leftField = trRel.getField();
                        Component lcomp = support.getComponent();
                        if (lcomp != null && lcomp instanceof JList<?>) {
                            JList<? extends Field> list = (JList<? extends Field>) lcomp;
                            EntityView<E> eView = entityView;
                            if (eView != null) {
                                Point lpt = getDropLocationPoint(support, list);
                                if (lpt != null) {
                                    E rightEntity = eView.getEntity();
                                    int dropAction = extractDropAction(support);
                                    if (dropAction == MOVE) {
                                        ListUI lUi = list.getUI();
                                        int targetItemIndex = lUi.locationToIndex(list, lpt);
                                        if (targetItemIndex > -1) {
                                            Field rightField = null;
                                            if (list.getModel() instanceof FieldsListModel.ParametersModel) {
                                                rightField = ((EntityView.ParametersMetadataModel) list.getModel()).getElementAt(targetItemIndex);
                                            } else if (list.getModel() instanceof FieldsListModel.FieldsModel) {
                                                rightField = ((FieldsListModel.FieldsModel) list.getModel()).getElementAt(targetItemIndex);
                                            }
                                            if (leftEntity != null && rightEntity != null && leftField != null && rightField != null
                                                    && !DatamodelDesignUtils.<E>isRelationAlreadyDefined(leftEntity, leftField, rightEntity, rightField)) {
                                                if (modelView.getModel() instanceof DbSchemeModel) {
                                                    return leftField.getType() != null && leftField.getType().equalsIgnoreCase(rightField.getType()) && rightField.isPk() && !leftField.isFk();
                                                } else { // dmv.getModel() instanceof DbSchemeModel
                                                    return true;
                                                    /*
                                                     if ((leftField.isPk() || leftField.isFk()) && (rightField.isPk() || rightField.isFk())) {
                                                     return SQLUtils.isKeysCompatible(leftField, rightField);
                                                     } else {
                                                     return SQLUtils.isSimpleTypesCompatible(leftField.getTypeInfo().getSqlType(), rightField.getTypeInfo().getSqlType());
                                                     }
                                                     */
                                                }
                                            }
                                        }
                                    } else if (dropAction == COPY) {
                                        return !(leftEntity instanceof QueryParametersEntity)
                                                && !(rightEntity instanceof QueryParametersEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(RelationsFieldsDragHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(RelationsFieldsDragHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        Transferable tr = support.getTransferable();
        if (tr != null && tr.isDataFlavorSupported(TransferableRelation.getDefaultDataFlavor())) {
            try {
                Object ldata = tr.getTransferData(TransferableRelation.getDefaultDataFlavor());
                if (ldata != null && ldata instanceof TransferableRelation) {
                    TransferableRelation<E> trRel = (TransferableRelation<E>) ldata;
                    E leftEntity = trRel.getEntity();
                    Field leftField = trRel.getField();
                    Component lcomp = support.getComponent();
                    if (lcomp != null && lcomp instanceof JList<?>) {
                        JList<? extends Field> list = (JList<? extends Field>) lcomp;
                        EntityView<E> eView = entityView;
                        if (eView != null) {
                            Point lpt = getDropLocationPoint(support, list);
                            if (lpt != null) {
                                E rightEntity = eView.getEntity();
                                int dropAction = extractDropAction(support);
                                if (dropAction == MOVE) {
                                    ListUI lUi = list.getUI();
                                    int targetItemIndex = lUi.locationToIndex(list, lpt);
                                    if (targetItemIndex > -1) {
                                        Field rightField = null;
                                        if (list.getModel() instanceof FieldsListModel.ParametersModel) {
                                            rightField = ((FieldsListModel.ParametersModel) list.getModel()).getElementAt(targetItemIndex);
                                        } else if (list.getModel() instanceof FieldsListModel.FieldsModel) {
                                            rightField = ((FieldsListModel.FieldsModel) list.getModel()).getElementAt(targetItemIndex);
                                        }
                                        if (leftEntity != null && rightEntity != null && leftField != null && rightField != null) {
                                            if (rightEntity instanceof QueryParametersEntity) {
                                                E tmpEntity = leftEntity;
                                                leftEntity = rightEntity;
                                                rightEntity = tmpEntity;

                                                Field tmpField = leftField;
                                                leftField = rightField;
                                                rightField = tmpField;
                                            }
                                            if (!(modelView.getModel() instanceof DbSchemeModel) && !(modelView.getModel() instanceof QueryModel) && willFormCycle(leftEntity, rightEntity)) {
                                                JOptionPane.showMessageDialog(modelView, DatamodelDesignUtils.getLocalizedString("willFormCycle"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.WARNING_MESSAGE);
                                            } else {
                                                Relation<E> alreadyInRelation = findAlreadyInRelation(rightEntity, rightField);
                                                Relation<E> newRel = new Relation<>(leftEntity, leftField, rightEntity, rightField);
                                                if ((modelView.getModel().checkRelationRemovingValid(alreadyInRelation) || modelView.getModel() instanceof QueryModel)
                                                        && modelView.getModel().checkRelationAddingValid(newRel)) {
                                                    editModelField2FieldRelation(modelView.getUndoSupport(), ldata, alreadyInRelation, newRel);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                Logger.getLogger(RelationsFieldsDragHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.importData(support);
    }

    protected Relation<E> findAlreadyInRelation(E rightEntity, Field rightField) {
        if (rightEntity != null && rightField != null) {
            Set<Relation<E>> inRels = rightEntity.getInRelations();
            if (inRels != null && !inRels.isEmpty()) {
                for (Relation<E> rel : inRels) {
                    assert !(rel instanceof ReferenceRelation<?>);
                    if (rightField == rel.getRightField()) {
                        return rel;
                    }
                }
            }
        }
        return null;
    }

    protected void editModelField2FieldRelation(UndoableEditSupport aUndoSupport, Object aTransferrableData, Relation<E> alreadyInRelation, Relation<E> newRel) throws CannotRedoException {
        aUndoSupport.beginUpdate();
        try {
            assert !(alreadyInRelation instanceof ReferenceRelation<?>);
            if (alreadyInRelation != null && !(modelView.getModel() instanceof QueryModel)) {
                DeleteRelationEdit<E> dre = new DeleteRelationEdit<>(alreadyInRelation);
                dre.redo();
                aUndoSupport.postEdit(dre);
            }
            NewRelationEdit<E> ledit = new NewRelationEdit<>(newRel);
            ledit.redo();
            aUndoSupport.postEdit(ledit);
        } finally {
            aUndoSupport.endUpdate();
        }
    }

    private int extractDropAction(TransferSupport support) {
        int dropAction = NONE;
        if (support != null) {
            if (support.isDrop()) {
                dropAction = support.getUserDropAction();
            } else if (support.getComponent() != null && support.getComponent() instanceof JComponent) {
                JComponent jcomp = (JComponent) support.getComponent();
                Object oAction = jcomp.getClientProperty(SCALABLE_COVER_DROP_ACTION_TAG);
                if (oAction != null && oAction instanceof Integer) {
                    dropAction = (Integer) oAction;
                }
            }
        }
        return dropAction;
    }

    /**
     * Checks if a field (not a parameter) in query entity is from virtual
     * query's table and unwraps it using main table of the query. Fallback is
     * in returning an argument passed.
     *
     * @param aField A field to examine.
     * @param aEntity Entity examined field got from.
     * @return A Field instance. private Field checkWrapedTableKeyField(Field
     * aField, Entity aEntity) throws Exception { if ((aField.isPk() ||
     * aField.isFk()) && !(aField instanceof Parameter) && aEntity.getQueryId()
     * != null && aEntity.getQuery() != null) { SqlQuery query =
     * aEntity.getQuery(); String mainTable = query.getMainTable(); if
     * (mainTable != null && !mainTable.isEmpty()) { DbMetadataCacheIntf mdCache
     * = aEntity.getModel().getClient().getDbMetadataCache(query.getDbId()); if
     * (mdCache != null) { Fields mainTableFields =
     * mdCache.getTableMetadata(mainTable); if (mainTableFields != null &&
     * mainTableFields.contains(aField.getName())) { return
     * mainTableFields.get(aField.getName()); } } } } return aField; }
     */
    private class CycledGraphNode {

        public CycledGraphNode(E aEntity) {
            super();
            entity = aEntity;
        }
        public E entity = null;
        public boolean deleted = false;
        public List<CycledGraphNode> in = new ArrayList<>();
        public List<CycledGraphNode> out = new ArrayList<>();
    }

    private boolean willFormCycle(E leftEntity, E rightEntity) {
        if (modelView != null) {
            Model<E, SqlQuery> model = modelView.getModel();
            if (model != null) {
                Relation<E> dummyRel = new DummyRelation<>(leftEntity, null, rightEntity, null);
                try {
                    model.addRelation(dummyRel);
                    Map<Long, E> lets = model.getEntities();
                    if (lets != null) {
                        Collection<E> entCol = lets.values();
                        if (entCol != null) {
                            if (model instanceof QueryModel) {
                                entCol.add((E)((QueryModel) model).getParametersEntity());
                            }
                            // Build the graph to control cycles
                            List<CycledGraphNode> graph = new ArrayList<>();
                            Map<E, CycledGraphNode> ent2gn = new HashMap<>();
                            for (E ent : entCol) {
                                CycledGraphNode cgn = new CycledGraphNode(ent);
                                graph.add(cgn);
                                ent2gn.put(ent, cgn);
                            }
                            for (int i = 0; i < graph.size(); i++) {
                                CycledGraphNode gn = graph.get(i);
                                Set<Relation<E>> lin = gn.entity.getInRelations();
                                if (lin != null && !lin.isEmpty()) {
                                    for (Relation<E> rel : lin) {
                                        E lEnt = rel.getLeftEntity();
                                        CycledGraphNode lgn = ent2gn.get(lEnt);
                                        gn.in.add(lgn);
                                    }
                                }
                                Set<Relation<E>> lout = gn.entity.getOutRelations();
                                if (lout != null && !lout.isEmpty()) {
                                    for (Relation<E> rel : lout) {
                                        E lEnt = rel.getRightEntity();
                                        CycledGraphNode lgn = ent2gn.get(lEnt);
                                        gn.out.add(lgn);
                                    }
                                }
                            }
                            // Graph si built
                            while (true) {
                                if (deleteNonFlowableNodes(graph) <= 0) {
                                    break;
                                }
                            }
                            for (int i = 0; i < graph.size(); i++) {
                                if (!graph.get(i).deleted) {
                                    return true;
                                }
                            }
                        }
                    }
                } finally {
                    model.removeRelation(dummyRel);
                }
            }
        }
        return false;
    }

    private int deleteNonFlowableNodes(List<CycledGraphNode> graph) {
        int ldeleted = 0;
        for (int i = 0; i < graph.size(); i++) {
            int linNodes = 0;
            int loutNodes = 0;
            CycledGraphNode lnode = graph.get(i);
            if (lnode != null && !lnode.deleted) {
                for (int j = 0; j < lnode.in.size(); j++) {
                    if (!lnode.in.get(j).deleted) {
                        linNodes++;
                    }
                }
                for (int j = 0; j < lnode.out.size(); j++) {
                    if (!lnode.out.get(j).deleted) {
                        loutNodes++;
                    }
                }
                if (linNodes == 0 || loutNodes == 0) {
                    lnode.deleted = true;
                    ldeleted++;
                }
            }
        }
        return ldeleted;
    }
//    private List<Relation> groupRelationsByRightEntity(HashSet<Relation> rels) {
//        List<Relation> rV = new ArrayList<>();
//        Set<Entity> entGroups = new HashSet<>();
//        
//        Iterator<Relation> rIt = rels.iterator();
//        if(rIt != null)
//        {
//            while(rIt.hasNext())
//            {
//                Relation rel = rIt.next();
//                Entity rEntity = rel.getRightEntity();
//                if(!entGroups.contains(rEntity))
//                {
//                    entGroups.add(rEntity);
//                    rV.add(rel);
//                }
//            }
//        }
//        return rV;
//    }
}
