/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.SQLUtils;
import com.eas.client.model.DummyRelation;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationParametersEntity;
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
public class RelationsFieldsDragHandler<E extends Entity<?, ?, E>> extends TransferHandler {

    protected static final String SCALABLE_COVER_DROP_LOCATION_TAG = "ScalableDropLocation";
    protected static final String SCALABLE_COVER_DROP_ACTION_TAG = "ScalableDropAction";
    protected ModelView<E, ?, ?> modelView = null;
    protected EntityView<E> entityView = null;

    public RelationsFieldsDragHandler(ModelView<E, ?, ?> aModelView, EntityView<E> aEntityView) {
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
        return new TransferableRelation<>(entityView.getEntity(), lField, lParameter);
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

    protected String extractParameterName(FieldsListModel.ParametersModel aModel, int targetItemIndex) {
        String parameterName = null;
        Parameter aItem = aModel.getElementAt(targetItemIndex);
        if (aItem != null) {
            parameterName = aItem.getName();
        }
        return parameterName;
    }

    protected int extractParameterType(FieldsListModel.ParametersModel aModel, int targetItemIndex) {
        int parameterType = RowsetUtils.INOPERABLE_TYPE_MARKER;
        Parameter aItem = aModel.getElementAt(targetItemIndex);
        if (aItem != null) {
            parameterType = aItem.getTypeInfo().getSqlType();
        }
        return parameterType;
    }

    protected String extractFieldName(FieldsListModel<?> lmodel, int targetItemIndex) {
        String fieldName = null;
        Object aItem = lmodel.getElementAt(targetItemIndex);
        if (aItem != null && aItem instanceof Field) {
            Field lfield = (Field) aItem;
            fieldName = lfield.getName();
        }
        return fieldName;
    }

    protected int extractFieldType(FieldsListModel<?> lmodel, int targetItemIndex) {
        int fieldType = RowsetUtils.INOPERABLE_TYPE_MARKER;
        Object aItem = lmodel.getElementAt(targetItemIndex);
        if (aItem != null && aItem instanceof Field) {
            Field lfield = (Field) aItem;
            fieldType = lfield.getTypeInfo().getSqlType();
        }
        return fieldType;
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
                        String leftFieldName = trRel.getFieldName();
                        String leftParameterName = trRel.getParamName();
                        int leftFieldType = trRel.getFieldType();
                        int leftParameterType = trRel.getParamType();
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
                                            String rightFieldName = null;
                                            String rightParameterName = null;
                                            if (list.getModel() instanceof FieldsListModel.ParametersModel) {
                                                rightParameterName = extractParameterName((EntityView.ParametersMetadataModel) list.getModel(), targetItemIndex);
                                            } else if (list.getModel() instanceof FieldsListModel.FieldsModel) {
                                                rightFieldName = extractFieldName((FieldsListModel.FieldsModel) list.getModel(), targetItemIndex);
                                            }
                                            if (!isRelationAlreadyDefined(leftEntity, leftFieldName, leftParameterName, rightEntity, rightFieldName, rightParameterName)) {
                                                int rightFieldType = RowsetUtils.INOPERABLE_TYPE_MARKER;
                                                int rightParameterType = RowsetUtils.INOPERABLE_TYPE_MARKER;
                                                if (list.getModel() instanceof EntityView.ParametersMetadataModel) {
                                                    rightParameterType = extractParameterType((EntityView.ParametersMetadataModel) list.getModel(), targetItemIndex);
                                                } else if (list.getModel() instanceof FieldsListModel) {
                                                    rightFieldType = extractFieldType((FieldsListModel) list.getModel(), targetItemIndex);
                                                }

                                                if (leftEntity != null && rightEntity != null && (leftFieldName != null || leftParameterName != null) && (rightFieldName != null || rightParameterName != null)) {
                                                    EntityView<E> lView = modelView.getEntityView(leftEntity);
                                                    EntityView<E> rView = modelView.getEntityView(rightEntity);

                                                    if (!(modelView.getModel() instanceof DbSchemeModel)) {
                                                        if ((lView.isKeyField(leftFieldName) || lView.isKeyParameter(leftParameterName))
                                                                && (rView.isKeyField(rightFieldName) || rView.isKeyParameter(rightParameterName))) {
                                                            Field leftParamField = null;
                                                            Field rightParamField = null;
                                                            if (lView.isKeyField(leftFieldName)) {
                                                                leftParamField = lView.getFieldByName(leftFieldName);
                                                            } else {
                                                                leftParamField = lView.getParameterByName(leftParameterName);
                                                            }

                                                            if (rView.isKeyField(rightFieldName)) {
                                                                rightParamField = rView.getFieldByName(rightFieldName);
                                                            } else {
                                                                rightParamField = rView.getParameterByName(rightParameterName);
                                                            }
                                                            //leftParamField = checkWrapedTableKeyField(leftParamField, lView.getEntity());
                                                            //rightParamField = checkWrapedTableKeyField(rightParamField, rView.getEntity());
                                                            return SQLUtils.isKeysCompatible(leftParamField, rightParamField);
                                                        } else {
                                                            return SQLUtils.isSimpleTypesCompatible((leftFieldName != null) ? leftFieldType : leftParameterType, (rightFieldName != null) ? rightFieldType : rightParameterType);
                                                        }
                                                    } else { // dmv.getModel() instanceof DbSchemeModel
                                                        assert leftFieldName != null;
                                                        assert rightFieldName != null;
                                                        Field leftField = lView.getFieldByName(leftFieldName);
                                                        Field rightField = rView.getFieldByName(rightFieldName);
                                                        if (leftField != null && rightField != null) {
                                                            assert leftFieldType == leftField.getTypeInfo().getSqlType();
                                                            assert rightFieldType == rightField.getTypeInfo().getSqlType();
                                                            return leftFieldType == rightFieldType && rightField.isPk() && !leftField.isFk();
                                                        } else {
                                                            return false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (dropAction == COPY) {
                                        return !(leftEntity instanceof ApplicationParametersEntity)
                                                && !(leftEntity instanceof QueryParametersEntity)
                                                && !(rightEntity instanceof ApplicationParametersEntity)
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
                    String leftFieldName = trRel.getFieldName();
                    String leftParameterName = trRel.getParamName();
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
                                        String rightFieldName = null;
                                        String rightParameterName = null;
                                        if (list.getModel() instanceof FieldsListModel.ParametersModel) {
                                            rightParameterName = extractParameterName((FieldsListModel.ParametersModel) list.getModel(), targetItemIndex);
                                        } else if (list.getModel() instanceof FieldsListModel.FieldsModel) {
                                            rightFieldName = extractFieldName((FieldsListModel) list.getModel(), targetItemIndex);
                                        }
                                        if (leftEntity != null && rightEntity != null && (leftFieldName != null || leftParameterName != null) && (rightFieldName != null || rightParameterName != null)) {

                                            if (rightEntity instanceof ApplicationParametersEntity || rightEntity instanceof QueryParametersEntity) {
                                                E tempEntity = leftEntity;
                                                String tempFieldName = leftFieldName;
                                                String tempParameterName = leftParameterName;

                                                leftEntity = rightEntity;
                                                leftFieldName = rightFieldName;
                                                leftParameterName = rightParameterName;

                                                rightEntity = tempEntity;
                                                rightFieldName = tempFieldName;
                                                rightParameterName = tempParameterName;
                                            }
                                            if (!(modelView.getModel() instanceof DbSchemeModel) && !(modelView.getModel() instanceof QueryModel) && willFormCycle(leftEntity, rightEntity)) {
                                                JOptionPane.showMessageDialog(modelView, DatamodelDesignUtils.getLocalizedString("willFormCycle"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.WARNING_MESSAGE);
                                            } else {
                                                UndoableEditSupport undoSupport = modelView.getUndoSupport();

                                                Relation<E> alreadyInRelation = findAlreadyInRelation(rightEntity, rightFieldName, rightParameterName);
                                                Relation<E> newRel = new Relation<>(leftEntity, (leftFieldName != null), (leftFieldName != null) ? leftFieldName : leftParameterName, rightEntity, (rightFieldName != null), (rightFieldName != null) ? rightFieldName : rightParameterName);
                                                if ((modelView.getModel().checkRelationRemovingValid(alreadyInRelation) || modelView.getModel() instanceof QueryModel)
                                                        && modelView.getModel().checkRelationAddingValid(newRel)) {
                                                    editModelField2FieldRelation(undoSupport, ldata, alreadyInRelation, newRel);
                                                    modelView.rerouteConnectors();
                                                    modelView.repaint();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(RelationsFieldsDragHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RelationsFieldsDragHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.importData(support);
    }

    protected Relation<E> findAlreadyInRelation(E rightEntity, String rightFieldName, String rightParameterName) {
        if (rightEntity != null && (rightFieldName != null || rightParameterName != null)) {
            Set<Relation<E>> inRels = rightEntity.getInRelations();
            if (inRels != null && !inRels.isEmpty()) {
                for (Relation<E> rel : inRels) {
                    if ((rightFieldName != null && rightFieldName.equals(rel.getRightField()))
                            || (rightParameterName != null && rightParameterName.equals(rel.getRightParameter()))) {
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

    protected void editModelEntity2EntityRelation(UndoableEditSupport aUndoSupport, Relation<E> rel) throws CannotRedoException {
        NewRelationEdit<E> ledit = new NewRelationEdit<>(rel);
        ledit.redo();
        aUndoSupport.postEdit(ledit);
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

    private boolean isRelationAlreadyDefined(E leftEntity, String leftFieldName, String leftParameterName, E rightEntity, String rightFieldName, String rightParameterName) {
        if (leftEntity != null && rightEntity != null
                && (leftFieldName != null || leftParameterName != null)
                && (rightFieldName != null || rightParameterName != null)) {
            Set<Relation<E>> inRels = rightEntity.getInRelations();
            if (inRels != null) {
                for (Relation<E> rel : inRels) {
                    if (rel != null) {
                        E lEntity = rel.getLeftEntity();
                        if (lEntity == leftEntity) {
                            String lFieldName = rel.getLeftField();
                            String lParameterName = rel.getLeftParameter();
                            String rFieldName = rel.getRightField();
                            String rParameterName = rel.getRightParameter();

                            boolean lfield1 = leftFieldName != null;
                            boolean rfield1 = rightFieldName != null;
                            String lfieldName1 = lfield1 ? leftFieldName : leftParameterName;
                            String rfieldName1 = rfield1 ? rightFieldName : rightParameterName;

                            boolean lfield2 = lFieldName != null;
                            boolean rfield2 = rFieldName != null;
                            String lfieldName2 = lfield2 ? lFieldName : lParameterName;
                            String rfieldName2 = rfield2 ? rFieldName : rParameterName;

                            if (lfield1 == lfield2
                                    && rfield1 == rfield2
                                    && lfieldName1.equals(lfieldName2)
                                    && rfieldName1.equals(rfieldName2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
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
            Model<E, ?, ?, ?> model = modelView.getModel();
            if (model != null) {
                Relation<E> dummyRel = new DummyRelation<>(leftEntity, true, "dummyfieldLeft", rightEntity, true, "dummyfieldright");
                try {
                    model.addRelation(dummyRel);
                    Map<Long, E> lets = model.getAllEntities();
                    if (lets != null) {
                        Collection<E> entCol = lets.values();
                        if (entCol != null) {
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
