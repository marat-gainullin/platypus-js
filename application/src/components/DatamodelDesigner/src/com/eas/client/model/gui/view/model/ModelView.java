/*
 * DatamodelView.java
 *
 * Created on 13 Август 2008 г., 9:16
 */
package com.eas.client.model.gui.view.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.DbClient;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.*;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.FindFrame;
import com.eas.client.model.gui.IconCache;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.NotSavableUndoableEditSupport;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.edits.fields.NewFieldEdit;
import com.eas.client.model.gui.selectors.SelectedField;
import com.eas.client.model.gui.selectors.SelectedParameter;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.CollapserExpander;
import com.eas.client.model.gui.view.EntityViewDoubleClickListener;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.gui.view.EstimatedArc;
import com.eas.client.model.gui.view.FieldSelectionListener;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.ModelViewDragHandler;
import com.eas.client.model.gui.view.PathsFinder;
import com.eas.client.model.gui.view.RelationDesignInfo;
import com.eas.client.model.gui.view.RelationsFieldsDragHandler;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.visibilitygraph.Segment;
import com.eas.client.utils.scalableui.JScalablePanel;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public abstract class ModelView<E extends Entity<?, ?, E>, P extends E, M extends Model<E, P, DbClient, ?>> extends JPanel {

    // settings
    public final static int slotWidth = 3;
    public final static int connectorWidth = 1;
    private static final boolean DEBUG_MODE = false;
    protected final static Stroke slotsStroke = new BasicStroke(slotWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    protected final static Stroke connectorsStroke = new BasicStroke(connectorWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke connectorsOuterStroke = new BasicStroke(connectorWidth + 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke hittedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke selectedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke hittedConnectorsOuterStroke = new BasicStroke(connectorWidth + 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke selectedConnectorsOuterStroke = new BasicStroke(connectorWidth + 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    // Style (colors, width etc.)
    public Color toParameterConnectorColor = Color.magenta;
    public Color toFieldConnectorColor = (new JButton()).getBackground().darker().darker().darker();//new Color(41, 110, 255);
    // data 
    protected M model = null;
    // view
    protected Map<Long, EntityView<E>> entityViews = new HashMap<>();
    // model's parameters entity view
    protected EntityView modelParametersEntityView;
    // processing
    protected ModelViewMouseHandler mouseHandler;
    protected int viewRefreshable = 0;
    protected PathsFinder<E, EntityView<E>> pathsFinder = new PathsFinder<>(this);
    protected FindFrame<E, P, M> finder = null;
    protected Component dragTarget = null;
    protected Rectangle oldBounds = null;
    protected boolean needRerouteConnectors = true;
    protected boolean reallyDragged = false;
    // selection
    protected Set<Relation<E>> hittedRelations = new HashSet<>();
    protected Set<Relation<E>> selectedRelations = new HashSet<>();
    protected Set<E> selectedEntities = new HashSet<>();
    protected Set<EntityFieldTuple> selectedFields = new HashSet<>();
    // interaction
    protected ModelChangesReflector dmListener = new ModelChangesReflector();
    protected NotSavableUndoableEditSupport undoSupport;
    protected TablesSelectorCallback tablesSelector;
    // events
    protected EntityViewPropagator entityViewPropagator = new EntityViewPropagator();
    protected EntitiesViewsMovesManager entitiesViewsMover = new EntitiesViewsMovesManager();
    protected Set<EntityViewDoubleClickListener<E>> entityViewDoubleClickListeners = new HashSet<>();
    protected Set<ModelSelectionListener<E>> entitySelectionListeners = new HashSet<>();

    protected void refreshView() {
        assert viewRefreshable >= 0;
        if (viewRefreshable == 0) {
            recalcViewsBounds();
            regenerateIndexes();
            forceRerouteConnectors();
            checkActions();
            repaint();
        }
    }

    public void checkActions() {
        DatamodelDesignUtils.checkActions(this);
    }

    public boolean isViewSelected(EntityView<E> aView) {
        return selectedEntities.contains(aView.getEntity());
    }

    public void selectView(EntityView<E> aView) {
        Set<E> oldSelection = new HashSet<>();
        oldSelection.addAll(selectedEntities);
        silentSelectView(aView);
        fireEntitiesSelectionChanged(oldSelection, selectedEntities);
    }

    public void silentSelectView(EntityView<E> aView) {
        aView.setEntityViewSelectedLook();
        selectedEntities.add(aView.getEntity());
        checkActions();
    }

    public void unselectView(EntityView<E> aView) {
        Set<E> oldSelection = new HashSet<>();
        oldSelection.addAll(selectedEntities);
        silentUnselectView(aView);
        fireEntitiesSelectionChanged(oldSelection, selectedEntities);
    }

    public void silentUnselectView(EntityView<E> aView) {
        aView.setEntityViewUnselectedLook();
        selectedEntities.remove(aView.getEntity());
        checkActions();
    }

    public void clearSelection() {
        clearEntitiesSelection(null);
        clearRelationsSelection();
        clearEntitiesFieldsSelection();
        checkActions();
    }

    public void clearRelationsSelection() {
        Set<Relation<E>> oldSelection = new HashSet<>();
        oldSelection.addAll(selectedRelations);
        hittedRelations.clear();
        selectedRelations.clear();
        fireRelationsSelectionChanged(oldSelection, selectedRelations);
    }

    public void clearEntitiesSelection(EntityView<E> viewToRetain) {
        Set<E> oldSelection = new HashSet<>();
        oldSelection.addAll(selectedEntities);
        for (E e : selectedEntities) {
            EntityView<E> eView = entityViews.get(e.getEntityID());
            if (viewToRetain != eView) {
                eView.setEntityViewUnselectedLook();
            }
        }
        selectedEntities.clear();
        if (viewToRetain != null) {
            selectedEntities.add(viewToRetain.getEntity());
        }
        fireEntitiesSelectionChanged(oldSelection, selectedEntities);
    }

    public void silentClearEntitiesSelection() {
        Set<ModelSelectionListener<E>> oldSelectionListeners = entitySelectionListeners;
        entitySelectionListeners = null;
        try {
            clearEntitiesSelection(null);
        } finally {
            entitySelectionListeners = oldSelectionListeners;
        }
        checkActions();
    }

    public void clearEntitiesFieldsSelection() {
        selectedFields.clear();
        for (EntityView<E> eView : entityViews.values()) {
            eView.clearListsSelection();
        }
    }

    public void silentClearRelationsSelection() {
        Set<ModelSelectionListener<E>> oldSelectionListeners = entitySelectionListeners;
        entitySelectionListeners = null;
        try {
            clearRelationsSelection();
        } finally {
            entitySelectionListeners = oldSelectionListeners;
        }
        checkActions();
    }

    public void silentClearSelection() {
        Set<ModelSelectionListener<E>> oldSelectionListeners = entitySelectionListeners;
        entitySelectionListeners = null;
        try {
            clearSelection();
        } finally {
            entitySelectionListeners = oldSelectionListeners;
        }
        checkActions();
    }

    public Collection<EntityView<E>> getViews() {
        return entityViews.values();
    }

    public Rectangle findAnyFreeSpace(int aInitialX, int aInitialY) {
        if (pathsFinder.isValid()) {
            int xcrat = 0;
            int ycrat = 0;
            // find any free space
            Rectangle suspectedBounds = new Rectangle(10, 10, EntityView.ENTITY_VIEW_DEFAULT_WIDTH, EntityView.ENTITY_VIEW_DEFAULT_HEIGHT);
            suspectedBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            while (pathsFinder.insetsContains(suspectedBounds)) {
                aInitialX += ALLOCATION_STEP_X;
                xcrat++;
                if (xcrat > ALLOCATION_GRANULARITY_X) {
                    xcrat = 0;
                    aInitialX -= ALLOCATION_STEP_X * ALLOCATION_GRANULARITY_X;
                    aInitialY += ALLOCATION_STEP_Y;
                    ycrat++;
                    if (ycrat > ALLOCATION_GRANULARITY_Y) {
                        ycrat = 0;
                        aInitialX += ALLOCATION_STEP_X * ALLOCATION_GRANULARITY_X;
                        aInitialY -= ALLOCATION_STEP_Y * ALLOCATION_GRANULARITY_Y;
                    }
                }
                suspectedBounds.x = aInitialX;
                suspectedBounds.y = aInitialY;
            }
            suspectedBounds.grow(-EntityView.INSET_ZONE, -EntityView.INSET_ZONE);
            return suspectedBounds;
        } else {
            return new Rectangle(aInitialX, aInitialY, EntityView.ENTITY_VIEW_DEFAULT_WIDTH, EntityView.ENTITY_VIEW_DEFAULT_HEIGHT);
        }
    }

    protected class ModelChangesReflector implements ModelEditingListener<E> {

        @Override
        public void entityAdded(E aEntity) {
            final EntityView<E> eView = createEntityView(aEntity);
            addEntityView(eView);
            refreshView();
            eView.reLayout();
            //fieldsListener.addFields(aEntity.getFields());
        }

        @Override
        public void entityRemoved(E removed) {
            //fieldsListener.removeFields(removed.getFields());
            clearRelationsSelection();
            removeEntityView(removed);
            refreshView();
        }

        @Override
        public void relationAdded(Relation<E> added) {
            RelationDesignInfo designInfo = relationsDesignInfo.get(added);
            if (designInfo == null) {
                designInfo = new RelationDesignInfo();
                relationsDesignInfo.put(added, designInfo);
            }
            refreshView();
        }

        @Override
        public void relationRemoved(Relation<E> removed) {
            relationsDesignInfo.remove(removed);
            clearRelationsSelection();
            refreshView();
        }

        @Override
        public void entityIndexesChanged(E e) {
        }
    }

    public EntityView<E> getParametersView() {
        if (entityViews != null && model != null && model.getParametersEntity() != null) {
            return entityViews.get(model.getParametersEntity().getEntityID());
        } else {
            return null;
        }
    }

    public void fireEntityViewDoubleClicked(EntityView<E> eView, boolean fieldsClicked, boolean paramsClicked) {
        if (entityViewDoubleClickListeners != null) {
            for (EntityViewDoubleClickListener<E> l : entityViewDoubleClickListeners) {
                l.clicked(eView, fieldsClicked, paramsClicked);
            }
        }
    }

    public void addEntityViewDoubleClickListener(EntityViewDoubleClickListener<E> l) {
        entityViewDoubleClickListeners.add(l);
    }

    public void removeEntityViewDoubleClickListener(EntityViewDoubleClickListener<E> l) {
        entityViewDoubleClickListeners.remove(l);
    }

    public void fireRelationsSelectionChanged(Set<Relation<E>> oldSelection, Set<Relation<E>> newSelection) {
        if (entitySelectionListeners != null) {
            for (ModelSelectionListener<E> l : entitySelectionListeners) {
                l.selectionChanged(oldSelection, newSelection);
            }
        }
    }

    public void fireEntitiesSelectionChanged(Set<E> oldSelection, Set<E> newSelection) {
        if (!oldSelection.equals(newSelection)) {
            if (entitySelectionListeners != null) {
                for (ModelSelectionListener<E> l : entitySelectionListeners) {
                    l.selectionChanged(oldSelection, newSelection);
                }
            }
        }
    }

    public void fireFieldParamSelected(List<SelectedParameter<E>> aParameters, List<SelectedField<E>> aFields) {
        if (entitySelectionListeners != null) {
            try {
                for (ModelSelectionListener<E> l : entitySelectionListeners) {
                    l.selectionChanged(aParameters, aFields);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Exception while firing selection event (fireFieldParamSelected) " + ex.getMessage(), "datamodel", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, "Exception while firing selection event (fireFieldParamSelected) ", ex);
            }
        }
    }

    public void reallocateEntitiesInPlain(List<E> vEntities, int lx, int ly) {
        // let's fix any intersections
        if (pathsFinder.isValid()) {
            pathsFinder.clearIndexes();
        }
        for (E lent : vEntities) {
            EntityView<E> eView = getEntityView(lent);
            if (pathsFinder.isValid()) {
                int xcrat = 0;
                int ycrat = 0;
                // find any free space
                Rectangle suspectedBounds = eView.getBounds();
                suspectedBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                while (pathsFinder.insetsContains(suspectedBounds)) {
                    lx += ALLOCATION_STEP_X;
                    xcrat++;
                    if (xcrat > ALLOCATION_GRANULARITY_X) {
                        xcrat = 0;
                        lx -= ALLOCATION_STEP_X * ALLOCATION_GRANULARITY_X;
                        ly += ALLOCATION_STEP_Y;
                        ycrat++;
                        if (ycrat > ALLOCATION_GRANULARITY_Y) {
                            ycrat = 0;
                            lx += ALLOCATION_STEP_X * ALLOCATION_GRANULARITY_X;
                            ly -= ALLOCATION_STEP_Y * ALLOCATION_GRANULARITY_Y;
                        }
                    }
                    eView.setLocation(lx, ly);
                    suspectedBounds = eView.getBounds();
                    suspectedBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                }
                if (pathsFinder.isValid()) {
                    pathsFinder.put(eView);
                }
            }
        }
    }

    public void cancelDragging(MouseEvent e) {
        mouseHandler.cancelDragging(e);
    }

    private boolean compareSearchStrings(String dataString, boolean matchCase, String mask, boolean wholeWords) {
        if (dataString != null) {
            if (!matchCase) {
                dataString = dataString.toUpperCase();
                mask = mask.toUpperCase();
            }
            if (wholeWords) {
                String cuttedTitle = dataString.replaceAll("\\b" + mask + "\\b", "");
                if (dataString.length() != cuttedTitle.length()) {
                    return true;
                }
            } else {
                if (dataString.indexOf(mask) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public class FindResult {

        public EntityView<E> viewfound = null;
        public int paramIndex = -1;
        public int fieldIndex = -1;

        public FindResult() {
            super();
        }

        public void show() {
            makeVisible(viewfound, true);
            if (paramIndex > -1) {
                viewfound.setSelectedParameter(viewfound.getParameters().get(paramIndex));
            }
            if (fieldIndex > -1) {
                viewfound.setSelectedField(viewfound.getFields().get(fieldIndex));
            }
        }
    }

    public List<FindResult> findEntitiesFieldsParams(String aText, boolean matchCase, boolean wholeWords, SearchSubject subjects) {
        if (aText != null && !aText.isEmpty()) {
            String mask = aText;
            Collection<EntityView<E>> eCol = entityViews.values();
            if (eCol != null && mask != null) {
                Iterator<EntityView<E>> eIt = eCol.iterator();
                if (eIt != null) {
                    List<FindResult> lfound = new ArrayList<>();
                    while (eIt.hasNext()) {
                        EntityView<E> eView = eIt.next();
                        if (eView != null) {
                            if (subjects == SearchSubject.FIND_SUBJECT_DATASOURCES) {
                                String title = eView.getEntity().getTitle();
                                if (compareSearchStrings(title, matchCase, mask, wholeWords)
                                        || compareSearchStrings(eView.getEntity().getName(), matchCase, mask, wholeWords)) {
                                    FindResult fr = new FindResult();
                                    fr.viewfound = eView;
                                    lfound.add(fr);
                                }
                            } else if (subjects == SearchSubject.FIND_SUBJECT_FIELDS || subjects == SearchSubject.FIND_SUBJECT_PARAMS) {
                                Fields fieldsToExamine = null;
                                if (subjects == SearchSubject.FIND_SUBJECT_FIELDS) {
                                    fieldsToExamine = eView.getFields();
                                } else {
                                    fieldsToExamine = eView.getParameters();
                                }
                                if (fieldsToExamine != null) {
                                    for (int i = 1; i <= fieldsToExamine.getFieldsCount(); i++) {
                                        Field fieldOrParameter = fieldsToExamine.get(i);
                                        if (compareSearchStrings(fieldOrParameter.getDescription(), matchCase, mask, wholeWords)
                                                || compareSearchStrings(fieldOrParameter.getName(), matchCase, mask, wholeWords)) {
                                            FindResult fr = new FindResult();
                                            fr.viewfound = eView;
                                            if (subjects == SearchSubject.FIND_SUBJECT_FIELDS) {
                                                fr.fieldIndex = i;
                                            } else {
                                                fr.paramIndex = i;
                                            }
                                            lfound.add(fr);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return lfound;
                }
            }
        }
        return null;
    }

    public boolean isSelectedRelations() {
        return selectedRelations != null && !selectedRelations.isEmpty();
    }

    public boolean isSelectedFieldsOnOneEntity() {
        if (selectedFields == null || selectedFields.isEmpty()) {
            return false;
        }
        Set<Entity> s = new HashSet<>();
        for (EntityFieldTuple eft : selectedFields) {
            s.add(eft.entity);
        }
        return s.size() == 1;
    }

    public Set<Relation<E>> getSelectedRelations() {
        return selectedRelations;
    }

    public boolean isSelectedEntities() {
        return (selectedEntities != null && !selectedEntities.isEmpty());
    }

    public Set<E> getSelectedEntities() {
        return Collections.unmodifiableSet(selectedEntities);
    }

    public Set<EntityFieldTuple> getSelectedFields() {
        return Collections.unmodifiableSet(selectedFields);
    }

    public void beginUpdate() {
        viewRefreshable++;
    }

    public void endUpdate() {
        assert viewRefreshable > 0;
        viewRefreshable--;
        if (viewRefreshable == 0) {
            refreshView();
        }
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    protected boolean calcSlots(Segment fslot, Segment lslot, Relation<E> lrel, Map<Long, EntityView<E>> aViews) {
        boolean isBothOnTheRight = false;

        EntityView<E> lView = aViews.get(lrel.getLeftEntity().getEntityID());
        EntityView<E> rView = aViews.get(lrel.getRightEntity().getEntityID());

        Point lpt = null;
        Point lpt1 = null;
        Point rpt = null;
        Point rpt1 = null;

        Rectangle lViewBounds = lView.getBounds();
        lViewBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
        Rectangle rViewBounds = rView.getBounds();
        rViewBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
        if (lViewBounds.x + lViewBounds.width <= rViewBounds.x) {// lfr is left to rfr. 1 right and 2 left
            if (lrel.isLeftField()) {
                lpt = lView.getFieldPosition(lrel.getLeftField(), false);
            } else {
                lpt = lView.getParameterPosition(lrel.getLeftParameter(), false);
            }
            lpt1 = new Point(lpt);
            lpt1.x += EntityView.INSET_ZONE;
            if (lrel.isRightField()) {
                rpt = rView.getFieldPosition(lrel.getRightField(), true);
            } else {
                rpt = rView.getParameterPosition(lrel.getRightParameter(), true);
            }
            rpt1 = new Point(rpt);
            rpt1.x -= EntityView.INSET_ZONE;
        } else if (rViewBounds.x + rViewBounds.width <= lViewBounds.x) {// lfr is right to rfr. 1 left and 2 right
            if (lrel.isLeftField()) {
                lpt = lView.getFieldPosition(lrel.getLeftField(), true);
            } else {
                lpt = lView.getParameterPosition(lrel.getLeftParameter(), true);
            }
            lpt1 = new Point(lpt);
            lpt1.x -= EntityView.INSET_ZONE;
            if (lrel.isRightField()) {
                rpt = rView.getFieldPosition(lrel.getRightField(), false);
            } else {
                rpt = rView.getParameterPosition(lrel.getRightParameter(), false);
            }
            rpt1 = new Point(rpt);
            rpt1.x += EntityView.INSET_ZONE;
        } else {// they are intersecting on horizontal axis. 1 right and 2 right
            if (lrel.isLeftField()) {
                lpt = lView.getFieldPosition(lrel.getLeftField(), false);
            } else {
                lpt = lView.getParameterPosition(lrel.getLeftParameter(), false);
            }
            lpt1 = new Point(lpt);
            lpt1.x += EntityView.INSET_ZONE;
            if (lrel.isRightField()) {
                rpt = rView.getFieldPosition(lrel.getRightField(), false);
            } else {
                rpt = rView.getParameterPosition(lrel.getRightParameter(), false);
            }
            rpt1 = new Point(rpt);
            rpt1.x += EntityView.INSET_ZONE;
            isBothOnTheRight = true;
        }
        fslot.firstPoint = lpt;
        fslot.lastPoint = lpt1;
        pathsFinder.addSlot2Occupied(lpt, lpt1);
        lslot.firstPoint = rpt;
        lslot.lastPoint = rpt1;
        pathsFinder.addSlot2Occupied(rpt, rpt1);
        return isBothOnTheRight;
    }

    public EntityView<E> getEntityView(E aEnt) {
        return entityViews.get(aEnt.getEntityID());
    }

    protected void calcConnectorsSlots(Set<Relation<E>> rels) {
        if (rels != null) {
            for (Relation<E> rel : rels) {
                if (rel != null) {
                    Segment fslot = new Segment();
                    Segment lslot = new Segment();
                    calcSlots(fslot, lslot, rel, entityViews);
                    RelationDesignInfo designInfo = getRelationDesignInfo(rel);
                    designInfo.setFirstSlot(fslot);
                    designInfo.setLastSlot(lslot);
                }
            }
        }
    }

    public void lightRerouteConnectorLines(EntityView<E> aView) {
        Rectangle lbounds = lightRerouteConnectors(aView.getEntity().getInOutRelations());
        repaint(lbounds);
    }

    protected Rectangle lightRerouteConnectors(Set<Relation<E>> aRels) {
        if (aRels != null && !aRels.isEmpty()) {
            Rectangle beforeRelsBounds = calcRelationsBounds(aRels);
            Rectangle relsBounds = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Relation<E> rel : aRels) {
                Segment fslot = new Segment();
                Segment lslot = new Segment();
                calcSlots(fslot, lslot, rel, entityViews);
                RelationDesignInfo designInfo = getRelationDesignInfo(rel);
                designInfo.setFirstSlot(fslot);
                designInfo.setLastSlot(lslot);
                int x[] = new int[4];
                int y[] = new int[4];
                Point flPt = fslot.lastPoint;
                Point llPt = lslot.lastPoint;
                x[0] = flPt.x;
                y[0] = flPt.y;
                x[3] = llPt.x;
                y[3] = llPt.y;

                y[1] = y[0];
                y[2] = y[3];
                x[1] = (flPt.x + llPt.x) / 2;
                x[2] = x[1];

                designInfo.setConnectorX(x);
                designInfo.setConnectorY(y);
                if (flPt.x < relsBounds.x) {
                    relsBounds.x = flPt.x;
                }
                if (llPt.x < relsBounds.x) {
                    relsBounds.x = llPt.x;
                }
                if (flPt.y < relsBounds.y) {
                    relsBounds.y = flPt.y;
                }
                if (llPt.y < relsBounds.y) {
                    relsBounds.y = llPt.y;
                }
                if (flPt.x > relsBounds.width) {
                    relsBounds.width = flPt.x;
                }
                if (llPt.x > relsBounds.x) {
                    relsBounds.width = llPt.x;
                }
                if (flPt.y > relsBounds.height) {
                    relsBounds.height = flPt.y;
                }
                if (llPt.y > relsBounds.y) {
                    relsBounds.height = llPt.y;
                }
            }
            relsBounds.width = relsBounds.width - relsBounds.x;
            relsBounds.height = relsBounds.height - relsBounds.y;
            relsBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            if (beforeRelsBounds != null) {
                return relsBounds.union(beforeRelsBounds);
            }
            return relsBounds;
        } else {
            return new Rectangle(0, 0, 1, 1);
        }
    }
    protected Map<Relation<E>, RelationDesignInfo> relationsDesignInfo = new HashMap<>();

    public RelationDesignInfo getRelationDesignInfo(Relation<E> aRelation) {
        RelationDesignInfo designInfo = relationsDesignInfo.get(aRelation);
        if (designInfo == null) {
            designInfo = new RelationDesignInfo();
            relationsDesignInfo.put(aRelation, designInfo);
        }
        return designInfo;
    }

    protected void prepareCoordinates4Relations(Set<Relation<E>> rels) {
        if (rels != null && !rels.isEmpty()) {
            for (Relation<E> rel : rels) {
                if (rel != null) {
                    RelationDesignInfo designInfo = getRelationDesignInfo(rel);
                    List<Point> lconnector = designInfo.getConnector();
                    List<EstimatedArc> larcs = designInfo.getConnectorEstimatedArcs();
                    if (lconnector != null) {
                        if (larcs != null) {
                            int lInitialSize = lconnector.size();
                            pathsFinder.addArcs2Connector(lconnector, larcs, lInitialSize);
                        }
                        int[] lconnPtsX = new int[lconnector.size()];
                        int[] lconnPtsY = new int[lconnector.size()];
                        for (int j = 0; j < lconnector.size(); j++) {
                            Point lcurrPt = lconnector.get(j);
                            lconnPtsX[j] = lcurrPt.x;
                            lconnPtsY[j] = lcurrPt.y;
                        }
                        designInfo.setConnectorX(lconnPtsX);
                        designInfo.setConnectorY(lconnPtsY);
                        designInfo.setConnector(null);
                        designInfo.setConnectorEstimatedArcs(null);
                    }
                }
            }
        }
    }

    protected Rectangle calcRelationsBounds(Set<Relation<E>> aRels) {
        if (aRels != null && !aRels.isEmpty()) {
            Rectangle relsBounds = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Relation<E> rel : aRels) {
                RelationDesignInfo designInfo = getRelationDesignInfo(rel);
                int[] x = designInfo.getConnectorX();
                int[] y = designInfo.getConnectorY();
                if (x != null && y != null) {
                    if (x.length == y.length) {
                        for (int i = 0; i < x.length; i++) {
                            if (x[i] < relsBounds.x) {
                                relsBounds.x = x[i];
                            }
                            if (y[i] < relsBounds.y) {
                                relsBounds.y = y[i];
                            }
                            if (x[i] > relsBounds.width) {
                                relsBounds.width = x[i];
                            }
                            if (y[i] > relsBounds.height) {
                                relsBounds.height = y[i];
                            }
                        }
                    }
                }
            }
            relsBounds.width = relsBounds.width - relsBounds.x;
            relsBounds.height = relsBounds.height - relsBounds.y;
            relsBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            return relsBounds;
        }
        return null;
    }

    /**
     * Registers an
     * <code>UndoableEditListener</code>. The listener is notified whenever an
     * edit occurs which can be undone.
     *
     * @param l an <code>UndoableEditListener</code> object
     * @see #removeUndoableEditListener
     */
    public synchronized void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

    /**
     * Removes an
     * <code>UndoableEditListener</code>.
     *
     * @param l the <code>UndoableEditListener</code> object to be removed
     * @see #addUndoableEditListener
     */
    public synchronized void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    /**
     * Registers an
     * <code>ModelSelectionListener</code>. The listener is notified whenever an
     * entity is selected.
     *
     * @param l an <code>ModelSelectionListener</code> object
     * @see #removeEntitySelectionListener
     */
    public synchronized void addModelSelectionListener(ModelSelectionListener<E> l) {
        entitySelectionListeners.add(l);
    }

    /**
     * Removes an
     * <code>EntitySelectionListener</code>.
     *
     * @param l the <code>EntitySelectionListener</code> object to be removed
     * @see #addEntitySelectionListener
     */
    public synchronized void removeModelSelectionListener(ModelSelectionListener<E> l) {
        entitySelectionListeners.remove(l);
    }

    protected String generateHintFromRelations(Set<Relation<E>> aRelations) {
        if (aRelations != null) {
            String lhint = "";
            boolean lfirst = false;
            for (Relation<E> rel : aRelations) {
                if (rel != null) {
                    String leftFieldLabel = null;
                    E lEntity = rel.getLeftEntity();
                    EntityView<E> lView = getEntityView(lEntity);
                    String lField = rel.getLeftField();
                    if (lField != null && !lField.isEmpty()) {
                        leftFieldLabel = lView.getFieldDisplayLabel(lField);
                    } else {
                        lField = rel.getLeftParameter();
                        if (lField != null) {
                            leftFieldLabel = lView.getParameterDisplayLabel(lField);
                        }
                    }
                    if (leftFieldLabel != null && !leftFieldLabel.isEmpty()) {
                        String[] flist = leftFieldLabel.split(":");
                        if (flist != null && flist.length > 0) {
                            leftFieldLabel = flist[0];
                        }
                    }
                    String rightFieldLabel = null;
                    E rEntity = rel.getRightEntity();
                    EntityView<E> rView = getEntityView(rEntity);
                    String rField = rel.getRightField();
                    if (rView != null) {
                        if (rField != null && !rField.isEmpty()) {
                            rightFieldLabel = rView.getFieldDisplayLabel(rField);
                        } else {
                            rField = rel.getRightParameter();
                            if (rField != null) {
                                rightFieldLabel = rView.getParameterDisplayLabel(rField);
                            }
                        }
                    }
                    if (rightFieldLabel != null && !rightFieldLabel.isEmpty()) {
                        String[] flist = rightFieldLabel.split(":");
                        if (flist != null && flist.length > 0) {
                            rightFieldLabel = flist[0];
                        }
                    }
                    if (lfirst) {
                        lhint += "<p>";
                    }
                    String ltitle = lView.getEntity().getTitle();
                    if (isParametersEntity(lView.getEntity())) {
                        ltitle = DatamodelDesignUtils.getLocalizedString("Parameters");
                    }
                    lhint += ltitle;
                    if (leftFieldLabel != null && !leftFieldLabel.isEmpty()) {
                        lhint += "." + leftFieldLabel;
                    }
                    String rtitle = rView.getEntity().getTitle();
                    if (isParametersEntity(rView.getEntity())) {
                        rtitle = DatamodelDesignUtils.getLocalizedString("Parameters");
                    }
                    lhint += " -> " + rtitle;
                    if (rightFieldLabel != null && !rightFieldLabel.isEmpty()) {
                        lhint += "." + rightFieldLabel;
                    }
                    lfirst = true;
                }
            }
            if (!lhint.isEmpty()) {
                return "<html>" + lhint + "</html>";
            }
        }
        return "";
    }

    public void dispatchKeyStroke(KeyEvent event) {
        if (!event.isConsumed()) {
            Object[] actions = getActionMap().allKeys();
            if (actions != null) {
                for (int i = 0; i < actions.length; i++) {
                    Action actn = getActionMap().get(actions[i]);
                    if (actn != null && actn.isEnabled()) {
                        KeyStroke ks = KeyStroke.getKeyStroke(event.getKeyCode(), event.getModifiers());
                        Object accO = actn.getValue(Action.ACCELERATOR_KEY);
                        Object acnO = actn.getValue(Action.NAME);
                        if (accO != null && accO instanceof KeyStroke
                                && acnO != null && acnO instanceof String) {
                            KeyStroke aKs = (KeyStroke) accO;
                            if (aKs.equals(ks)) {
                                event.consume();
                                actn.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, (String) acnO));
                            }
                        }
                    }
                }
            }
        }
    }

    protected class EntitiesViewsMovesManager implements EntityViewsManager<E> {

        @Override
        public void beforeMove(EntityView<E> aView) {
            pathsFinder.remove(aView);
        }

        @Override
        public void afterMove(EntityView<E> aView) {
            pathsFinder.put(aView);
        }

        @Override
        public void collapseExpand(EntityView<E> aView, int dy) {
            pathsFinder.remove(aView);
            try {
                if (dy > 0) {
                    CollapserExpander.expand(pathsFinder, aView, aView.getParent().getBounds(), Math.abs(dy), aView.getUndoSupport());
                } else {
                    CollapserExpander.collapse(pathsFinder, aView, aView.getParent().getBounds(), Math.abs(dy), aView.getUndoSupport());
                }
            } finally {
                pathsFinder.put(aView);
            }
        }

        @Override
        public boolean isLegalBounds(Rectangle aRect) {
            Rectangle rt = (Rectangle) aRect.clone();
            rt.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            return rt.x >= 0 && rt.y >= 0 && !pathsFinder.insetsContains(rt);
        }

        @Override
        public void invalidateConnectors() {
            forceRerouteConnectors();
            repaint();
        }

        @Override
        public void beginMoveSession(EntityView<E> aView) {
        }

        @Override
        public void endMoveSession(EntityView<E> aView) {
            forceRerouteConnectors();
            checkActions();
            repaint();
        }

        @Override
        public void fieldParamSelectionResetted(E aEntity, Field aField, Parameter aParameter) {
            clearEntitiesSelection(null);
            clearEntitiesFieldsSelection();
        }
    }

    public ModelView(TablesSelectorCallback aSelectorCallback) {
        super(null, true);
        undoSupport = new NotSavableUndoableEditSupport() {
            @Override
            protected CompoundEdit createCompoundEdit() {
                return new AccessibleCompoundEdit();
            }
        };
        tablesSelector = aSelectorCallback;
        mouseHandler = new ModelViewMouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        setTransferHandler(new ModelViewDragHandler(this));
        putNavigatingActions();
        putEditingActions();
        InputMap im = DatamodelDesignUtils.fillInputMap(getActionMap());
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        setInputMap(WHEN_FOCUSED, im);
        setBackground(Color.white);
    }

    public ModelView(M aModel, TablesSelectorCallback aSelectorCallback) {
        this(aSelectorCallback);
        setModel(aModel);
    }

    protected void putEditingActions() {
        ActionMap am = getActionMap();
        am.put(AddTable.class.getSimpleName(), new AddTable());
        am.put(AddField.class.getSimpleName(), new AddField());
        am.put(Delete.class.getSimpleName(), new Delete());
        am.put(Cut.class.getSimpleName(), new Cut());
        am.put(Copy.class.getSimpleName(), new Copy());
        am.put(Paste.class.getSimpleName(), new Paste());
    }

    private void putNavigatingActions() {
        ActionMap am = getActionMap();
        am.put(Move.class.getSimpleName(), new Move());
        am.put(ZoomSmooth.class.getSimpleName(), new ZoomSmooth());
        am.put(Zoom.class.getSimpleName(), new Zoom());
        am.put(ZoomIn.class.getSimpleName(), new ZoomIn());
        am.put(ZoomOut.class.getSimpleName(), new ZoomOut());
        am.put(GoLeft.class.getSimpleName(), new GoLeft());
        am.put(GoRight.class.getSimpleName(), new GoRight());
        am.put(Find.class.getSimpleName(), new Find());
    }

    public M getModel() {
        return model;
    }

    public void setModel(M aModel) {
        if (aModel != model) {
            if (model != null) {
                model.removeEditingListener(dmListener);
//                for (Entity e : model.getAllEntities().values()) {
//                    fieldsListener.removeFields(e.getFields());
//                }
            }
            model = aModel;
            recreateEntityViews();
            if (model != null) {
                model.addEditingListener(dmListener);
//                for (Entity e : model.getAllEntities().values()) {
//                    fieldsListener.addFields(e.getFields());
//                }
            }
        }
    }

    protected boolean isIdxesInitialized() {
        return oldBounds != null;
    }

    protected void forceRegenerateIndexes() {
        Component loldDragTarget = dragTarget;
        dragTarget = null;
        try {
            regenerateIndexes();
        } finally {
            dragTarget = loldDragTarget;
        }
    }

    public void regenerateIndexes() {
        if (isIdxesInitialized() && dragTarget == null) {
            pathsFinder.clearIndexes();
            for (EntityView<E> eView : entityViews.values()) {
                pathsFinder.put(eView);
            }
            pathsFinder.wideIndexes();
        }
    }

    public void makeVisible(EntityView<E> aView, boolean needToSelect) {
        if (aView != null) {
            if (getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof JScalablePanel) {
                JScalablePanel sp = (JScalablePanel) getParent().getParent();
                Rectangle rect2Fit = null;
                //Container cp = aView.getParent();
                rect2Fit = aView.getBounds();
                //rect2Fit.height -= cp.getHeight();
                sp.makeVisible(rect2Fit);
                if (needToSelect) {
                    clearSelection();
                    selectView(aView);
                }
                DatamodelDesignUtils.checkActions(this);
            }
        }
    }
    private boolean rerouteChecked = false;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            if (DEBUG_MODE) {
                paintInsets(g2d);
                paintGrid(g2d);
                pathsFinder.paintVisibilityGraph(g2d);
            }
            if (!rerouteChecked) {
                checkConnectorsRerouted(model.getRelations());
                rerouteChecked = true;
            }

            /*
             * Color old2FieldColor = toFieldConnectorColor;
             * toFieldConnectorColor = toFieldConnectorColor.darker(); Color
             * old2ParameterColor = toParameterConnectorColor;
             * toParameterConnectorColor = toParameterConnectorColor.darker();
             * paintConnectors(g2d, model.getRelations(),
             * connectorsOuterStroke); toFieldConnectorColor = old2FieldColor;
             * toParameterConnectorColor = old2ParameterColor;
             */
            paintConnectors(g2d, model.getRelations(), connectorsStroke);

            //paintConnectors(g2d, selectedRelations, selectedConnectorsOuterStroke);
            //paintConnectors(g2d, hittedRelations, hittedConnectorsOuterStroke);
            Color old2FieldColor = toFieldConnectorColor;
            toFieldConnectorColor = toFieldConnectorColor.darker();
            Color old2ParameterColor = toParameterConnectorColor;
            toParameterConnectorColor = toParameterConnectorColor.darker();
            paintConnectors(g2d, selectedRelations, selectedConnectorsStroke);
            paintConnectors(g2d, hittedRelations, hittedConnectorsStroke);
            toFieldConnectorColor = old2FieldColor;
            toParameterConnectorColor = old2ParameterColor;
        }
    }

    protected void checkConnectorsRerouted(Set<Relation<E>> aRels) {
        for (Relation<E> lrel : aRels) {
            RelationDesignInfo designInfo = getRelationDesignInfo(lrel);
            Segment fslot = designInfo.getFirstSlot();
            int[] lconnPointsX = designInfo.getConnectorX();
            int[] lconnPointsY = designInfo.getConnectorY();
            Segment lslot = designInfo.getLastSlot();
            if (fslot == null || lslot == null
                    || lconnPointsX == null || lconnPointsY == null) {
                rerouteConnectors();
                break;
            }
        }
    }

    protected void paintConnectors(Graphics2D g2d, Set<Relation<E>> aRels, Stroke aConnectorsStroke) {
        if (aRels != null && !aRels.isEmpty()) {
            Stroke loldStroke = g2d.getStroke();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(slotsStroke);
            g2d.setColor(Color.BLACK);
            try {
                for (Relation<E> lrel : aRels) {
                    RelationDesignInfo designInfo = getRelationDesignInfo(lrel);
                    Segment fslot = designInfo.getFirstSlot();
                    if (fslot != null) {
                        Point lpt = fslot.firstPoint;
                        Point lpt1 = fslot.lastPoint;
                        g2d.drawLine(lpt.x, lpt.y, lpt1.x, lpt1.y);
                    }
                }
            } finally {
                g2d.setStroke(loldStroke);
            }
            Color loldColor = g2d.getColor();
            loldStroke = g2d.getStroke();
            g2d.setColor(toFieldConnectorColor);
            g2d.setStroke(aConnectorsStroke);
            try {
                for (Relation<E> lrel : aRels) {
                    if (lrel.isRightField()) {
                        g2d.setColor(toFieldConnectorColor);
                    } else {
                        g2d.setColor(toParameterConnectorColor);
                    }
                    RelationDesignInfo designInfo = getRelationDesignInfo(lrel);
                    int[] lconnPointsX = designInfo.getConnectorX();
                    int[] lconnPointsY = designInfo.getConnectorY();
                    if (lconnPointsX != null && lconnPointsY != null
                            && lconnPointsX.length == lconnPointsY.length) {
                        g2d.drawPolyline(lconnPointsX, lconnPointsY, lconnPointsX.length);
                    }
                    paintLastSlot(g2d, designInfo.getLastSlot(), 0);
                }
            } finally {
                g2d.setStroke(loldStroke);
                g2d.setColor(loldColor);
            }
        }
    }

    protected void paintLastSlots(Graphics2D g2d, Set<PathsFinder<E, EntityView<E>>.ConnectorSegmentRectangle> aSlots, Stroke aConnectorsStroke, int aWide) {
        if (aSlots != null && !aSlots.isEmpty()) {
            Color loldColor = g2d.getColor();
            Stroke loldStroke = g2d.getStroke();
            g2d.setStroke(aConnectorsStroke);
            try {
                for (PathsFinder<E, EntityView<E>>.ConnectorSegmentRectangle csr : aSlots) {
                    Relation<E> rel = csr.getRelation();
                    if (rel.isRightField()) {
                        g2d.setColor(toFieldConnectorColor);
                    } else {
                        g2d.setColor(toParameterConnectorColor);
                    }
                    RelationDesignInfo designInfo = getRelationDesignInfo(rel);
                    paintLastSlot(g2d, designInfo.getLastSlot(), aWide);
                }
            } finally {
                g2d.setStroke(loldStroke);
                g2d.setColor(loldColor);
            }
        }
    }

    protected void paintLastSlot(Graphics2D g2d, Segment aSlot, int aWide) {
        if (aSlot != null) {
            int[] xDirectionPoints = new int[3];
            int[] yDirectionPoints = new int[3];
            Point lpt = aSlot.firstPoint;
            Point lpt1 = aSlot.lastPoint;
            int dx = EntityView.HALF_INSET_ZONE / 2 + 1;
            if (lpt.x > lpt1.x) {
                dx = -dx;
            }
            xDirectionPoints[0] = lpt.x - 2 * dx;
            yDirectionPoints[0] = lpt.y;
            xDirectionPoints[1] = lpt1.x - dx + 2;
            yDirectionPoints[1] = lpt1.y - EntityView.HALF_INSET_ZONE - aWide;
            xDirectionPoints[2] = lpt1.x - dx + 2;
            yDirectionPoints[2] = lpt1.y + EntityView.HALF_INSET_ZONE + aWide;
            if (lpt.x > lpt1.x) {
                xDirectionPoints[0] += aWide;
                xDirectionPoints[1] -= aWide;
                xDirectionPoints[2] -= aWide;
            } else {
                xDirectionPoints[0] -= aWide;
                xDirectionPoints[1] += aWide;
                xDirectionPoints[2] += aWide;
            }
            g2d.drawLine(lpt1.x, lpt.y, lpt.x, lpt.y);
            Color oldColor = g2d.getColor();
            g2d.setColor(getBackground());
            g2d.fillPolygon(xDirectionPoints, yDirectionPoints, xDirectionPoints.length);
            g2d.setColor(oldColor);
            g2d.drawPolygon(xDirectionPoints, yDirectionPoints, xDirectionPoints.length);
        }
    }

    protected void paintGrid(Graphics2D g2d) {
        Rectangle rt = getBounds();
        Color cl = g2d.getColor();
        try {
            Color lLinesColor = new Color(0, 200, 0);
            g2d.setColor(lLinesColor);
            Iterator<PathsFinder<E, EntityView<E>>.AxisElement> xIt = pathsFinder.getXAxis();
            Iterator<PathsFinder<E, EntityView<E>>.AxisElement> yIt = pathsFinder.getYAxis();
            if (xIt != null) {
                while (xIt.hasNext()) {
                    PathsFinder<E, EntityView<E>>.AxisElement ael = xIt.next();
                    Integer lx = ael.getCoordinate();
                    g2d.drawLine(lx, rt.y, lx, rt.y + rt.height);
                }
            }
            if (yIt != null) {
                while (yIt.hasNext()) {
                    PathsFinder<E, EntityView<E>>.AxisElement ael = yIt.next();
                    Integer ly = ael.getCoordinate();
                    g2d.drawLine(rt.x, ly, rt.x + rt.width, ly);
                }
            }
        } finally {
            g2d.setColor(cl);
        }
    }

    protected void paintInsets(Graphics2D g2d) {
        Rectangle clip = g2d.getClipBounds();
        Color col = g2d.getColor();
        Composite composite = g2d.getComposite();
        try {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.setColor(Color.BLUE);
            for (EntityView<E> eView : entityViews.values()) {
                Rectangle rt = eView.getBounds();
                rt.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                if (clip.intersects(rt)) {
                    g2d.fillRect(rt.x, rt.y, rt.width, rt.height);
                }
            }
        } finally {
            g2d.setComposite(composite);
            g2d.setColor(col);
        }
    }

    public void recalcViewsBounds() {
        calcViewsBounds();
        invalidate();
    }

    protected Dimension calcViewsBounds() {
        int leps = Integer.MAX_VALUE;
        Dimension lmin = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Dimension lmax = new Dimension(Integer.MIN_VALUE, Integer.MIN_VALUE);
        if (entityViews.isEmpty()) {
            return super.getPreferredSize();
        } else {
            for (EntityView<E> eView : entityViews.values()) {
                Rectangle lrt = eView.getBounds();
                if (lrt.width < leps) {
                    leps = lrt.width;
                }
                if (lrt.height < leps) {
                    leps = lrt.height;
                }
                lrt.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                if (lrt.x < lmin.width) {
                    lmin.width = lrt.x;
                }
                if (lrt.y < lmin.height) {
                    lmin.height = lrt.y;
                }
                if (lrt.x + lrt.width > lmax.width) {
                    lmax.width = lrt.x + lrt.width;
                }
                if (lrt.y + lrt.height > lmax.height) {
                    lmax.height = lrt.y + lrt.height;
                }
            }
            Rectangle lbounds = new Rectangle(0, 0, lmax.width, lmax.height);
            if (dragTarget == null || (lbounds.width >= oldBounds.width && lbounds.height >= oldBounds.height)) {
                if (oldBounds == null || !oldBounds.equals(lbounds)) {
                    pathsFinder.setupInsetsIndex(lbounds, Math.max(2 * EntityView.INSET_ZONE, leps + 2 * EntityView.INSET_ZONE), entityViews);
                    forceRegenerateIndexes();
                    forceRerouteConnectors();
                }
                oldBounds = lbounds;
                if (isPreferredSizeSet()) {
                    return super.getPreferredSize();
                } else {
                    return lmax;
                }
            } else {
                if (isPreferredSizeSet()) {
                    return super.getPreferredSize();
                } else {
                    return new Dimension(oldBounds.width, oldBounds.height);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        } else {
            return calcViewsBounds();
        }
    }

    protected abstract EntityView<E> createGenericEntityView(E aEntity);

    protected abstract boolean isPasteable(E aEntityToPaste);

    protected abstract boolean isSelectedDeletableFields();

    protected void deleteSelectedFields() {
        if (isSelectedDeletableFields()) {
            Set<Relation> toConfirm = new HashSet<>();
            for (EntityFieldTuple t : selectedFields) {
                Set<Relation> toDel = FieldsEntity.getInOutRelationsByEntityField(t.entity, t.field);
                toConfirm.addAll(toDel);
            }
            if (!toConfirm.isEmpty()) {
                if (JOptionPane.showConfirmDialog(ModelView.this,
                        DatamodelDesignUtils.getLocalizedString("ifDeleteRelationsReferences"), //NOI18N
                        DatamodelDesignUtils.getLocalizedString("datamodel"), //NOI18N
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            AccessibleCompoundEdit section = new AccessibleCompoundEdit();
            for (EntityFieldTuple t : new HashSet<>(selectedFields)) {
                Set<Relation> toDel = FieldsEntity.getInOutRelationsByEntityField(t.entity, t.field);
                for (Relation rel : toDel) {
                    DeleteRelationEdit drEdit = new DeleteRelationEdit(rel);
                    drEdit.redo();
                    section.addEdit(drEdit);
                }
                DeleteFieldEdit edit = new DeleteFieldEdit(t.entity, t.field);
                edit.redo();
                section.addEdit(edit);
            }
            section.end();
            undoSupport.postEdit(section);
        }
    }

    public EntityView<E> createEntityView(E aEntity) {
        EntityView<E> eView = createGenericEntityView(aEntity);
        RelationsFieldsDragHandler<E> dragHandler = new RelationsFieldsDragHandler<>(this, eView);
        eView.getFieldsList().setTransferHandler(dragHandler);
        eView.getParametersList().setTransferHandler(dragHandler);
        eView.getFieldsList().setDragEnabled(true);
        eView.getParametersList().setDragEnabled(true);
        eView.setSize(aEntity.getWidth(), aEntity.getHeight());
        eView.setLocation(aEntity.getX(), aEntity.getY());
        return eView;
    }

    public void addEntityView(EntityView<E> eView) {
        entityViews.put(eView.getEntityID(), eView);
        if (pathsFinder.isValid()) {
            pathsFinder.put(eView);
        }
        eView.addFieldSelectionListener(entityViewPropagator);
        eView.getUndoSupport().addUndoableEditListener(entityViewPropagator);
        eView.addMouseListener(entityViewPropagator);
        if (isParametersEntity(eView.getEntity())) {
            modelParametersEntityView = eView;
        }
        add(eView);
    }

    public void removeEntityView(E aEntity) {
        if (aEntity != null && !isParametersEntity(aEntity)) {
            Long entityId = aEntity.getEntityID();
            EntityView<E> eView = entityViews.get(entityId);
            if (eView != null) {
                if (isViewSelected(eView)) {
                    unselectView(eView);
                }
                pathsFinder.remove(eView);
                entityViews.remove(entityId);
                eView.removeFieldSelectionListener(entityViewPropagator);
                eView.getUndoSupport().removeUndoableEditListener(entityViewPropagator);
                eView.removeMouseListener(entityViewPropagator);
                eView.getFieldsList().removeMouseListener(entityViewPropagator);
                eView.getParametersList().removeMouseListener(entityViewPropagator);
                remove(eView);
                eView.shrink();
            }
        }
    }

    public void removeEntityViews() {
        clearSelection();
        if (!entityViews.isEmpty()) {
            EntityView<?>[] toRemove = entityViews.values().toArray(new EntityView<?>[]{});
            for (EntityView<?> eView : toRemove) {
                removeEntityView((E) eView.getEntity());
            }
            entityViews.clear();
        }
        removeAll();
    }

    public void recreateEntityViews() {
        if (model != null) {
            beginUpdate();
            try {
                createEntityViews();
            } finally {
                endUpdate();
            }
        } else {
            removeEntityViews();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        closeFinder();
    }
    public static final int ALLOCATION_GRANULARITY_X = 30;
    public static final int ALLOCATION_GRANULARITY_Y = 10;
    public static final int ALLOCATION_STEP_X = 10;
    public static final int ALLOCATION_STEP_Y = 20;

    public void createEntityViews() {
        relationsDesignInfo.clear();
        if (pathsFinder.isValid()) {
            pathsFinder.clearIndexes();
        }
        removeEntityViews();
        Map<Long, E> entMap = model.getAllEntities();
        if (entMap != null && !entMap.isEmpty()) {
            Collection<E> entCol = entMap.values();
            if (entCol != null) {
                List<E> entities = new ArrayList<>();
                entities.addAll(entCol);
                //int lx = ALLOCATION_STEP_X;
                //int ly = ALLOCATION_STEP_Y;
                for (E entity : entities) {
                    EntityView<E> eView = createEntityView(entity);
                    addEntityView(eView);
                }
                // check any intersections
                //reallocateEntitiesInPlain(entities, lx, ly);
            }
        }
    }

    public void rerouteConnectors() {
        if (isShowing() && needRerouteConnectors) {
            forceRerouteConnectors();
        }
    }

    public void forceRerouteConnectors() {
        if (model != null) {
            Set<Relation<E>> modelRels = model.getRelations();
            Set<Relation<E>> filteredRels = new HashSet<>();
            if (modelRels != null && !modelRels.isEmpty()) {
                for (Relation<E> rel : modelRels) {
                    if (rel != null && getEntityView(rel.getLeftEntity()) != null
                            && getEntityView(rel.getRightEntity()) != null) {
                        filteredRels.add(rel);
                    }
                }
            }
            // Clear hash of cells occupied by arced connectors segments
            pathsFinder.clearOccupiedCells();
            // Calc slots
            calcConnectorsSlots(filteredRels);
            // Build visibility graph
            pathsFinder.rebuildVisibilityGraph(entityViews);
            // Calc connectors with visibility graph
            pathsFinder.calcConnectors(filteredRels);
            // Bus connectors with occupied
            pathsFinder.busConnectors(filteredRels);
            // Arc connectors with occupied
            //pf.arcConnectors(filteredRels);
            // Prepare tags for relation
            prepareCoordinates4Relations(filteredRels);
        }
    }

    protected class ModelViewMouseHandler implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e != null && pathsFinder.isValid()) {
                cancelDragging(e);
                Point lPt = e.getPoint();
                Object lo = e.getSource();
                if (lo != null && lo instanceof Component) {
                    Set<Relation<E>> oldSelectedRelations = new HashSet<>();
                    oldSelectedRelations.addAll(selectedRelations);
                    if (!(lo instanceof ModelView)) {
                        lPt = SwingUtilities.convertPoint((Component) lo, lPt, ModelView.this);
                    }
                    Rectangle selectedBoundsBefore = calcRelationsBounds(selectedRelations);
                    Rectangle hittedBoundsBefore = calcRelationsBounds(hittedRelations);
                    if (selectedBoundsBefore != null && hittedBoundsBefore != null) {
                        selectedBoundsBefore = selectedBoundsBefore.union(hittedBoundsBefore);
                    } else if (selectedBoundsBefore == null && hittedBoundsBefore != null) {
                        selectedBoundsBefore = hittedBoundsBefore;
                    }

                    if (e.isControlDown()) {
                        Set<Relation<E>> lselectedRelations = pathsFinder.hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);
                        if (selectedRelations != null && lselectedRelations != null) {
                            selectedRelations.addAll(lselectedRelations);
                        } else if (selectedRelations == null && lselectedRelations != null) {
                            selectedRelations = lselectedRelations;
                        }
                    } else {
                        clearSelection();
                        selectedRelations = pathsFinder.hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);
                    }
                    hittedRelations = selectedRelations;

                    Rectangle selectedBoundsAfter = calcRelationsBounds(hittedRelations);
                    if (selectedBoundsAfter != null && selectedBoundsBefore != null) {
                        repaint(selectedBoundsAfter.union(selectedBoundsBefore));
                    } else if (selectedBoundsBefore != null) {
                        repaint(selectedBoundsBefore);
                    } else if (selectedBoundsAfter != null) {
                        repaint(selectedBoundsAfter);
                    }
                    fireRelationsSelectionChanged(oldSelectedRelations, selectedRelations);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == this || dragging) {
                cancelDragging(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() == this || dragging) {
                cancelDragging(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        private boolean dragging = false;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() == this) {
                TransferHandler th = getTransferHandler();
                if (th != null) {
                    if (!dragging) {
                        th.exportAsDrag(ModelView.this, e, e.isControlDown() ? TransferHandler.COPY : TransferHandler.MOVE);
                        dragging = true;
                        if (DEBUG_MODE) {
                            System.out.println("Dragging started");
                        }
                    }
                } else {
                    cancelDragging(e);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e != null && pathsFinder.isValid()) {
                cancelDragging(e);
                Point lPt = e.getPoint();
                Object lo = e.getSource();
                if (lo != null && lo instanceof Component) {
                    if (!(lo instanceof ModelView)) {
                        lPt = SwingUtilities.convertPoint((Component) lo, lPt, ModelView.this);
                    }
                    Rectangle hittedBoundsBefore = calcRelationsBounds(hittedRelations);
                    hittedRelations = pathsFinder.hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);

                    Rectangle hittedBoundsAfter = calcRelationsBounds(hittedRelations);

                    if (hittedBoundsAfter != null && hittedBoundsBefore != null) {
                        repaint(hittedBoundsAfter.union(hittedBoundsBefore));
                    } else if (hittedBoundsBefore != null) {
                        repaint(hittedBoundsBefore);
                    } else if (hittedBoundsAfter != null) {
                        repaint(hittedBoundsAfter);
                    }
                    setCursor(Cursor.getDefaultCursor());
                }
                if (hittedRelations != null && !hittedRelations.isEmpty()) {
                    setToolTipText(generateHintFromRelations(hittedRelations));
                } else {
                    setToolTipText(null);
                }
            }
        }

        protected void cancelDragging(MouseEvent e) {
            if (e.getSource() == this) {
                dragging = false;
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    public void closeFinder() {
        if (finder != null) {
            finder.dispose();
        }
    }

    public boolean isAnySelectedEntities() {
        return !selectedEntities.isEmpty();
    }

    protected TableRef[] selectTableRef(TableRef oldValue) throws Exception {
        return tablesSelector.selectTableRef(oldValue);
    }

    protected abstract TableRef prepareTableRef4Selection();

    protected abstract M transformDocToModel(Document aDoc) throws Exception;

    protected abstract boolean isParametersEntity(E aEntity);

    protected abstract void prepareEntityForPaste(E aEntity);

    protected abstract M newModelInstance();

    protected void checkPastingName(E toPaste) {
        if (toPaste.getName() != null && model.isNamePresent(toPaste.getName(), toPaste, null)) {
            toPaste.setName(null);
        }
    }

    protected class EntityViewPropagator extends MouseAdapter implements UndoableEditListener, FieldSelectionListener<E> {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoSupport.postEdit(e.getEdit());
        }

        @Override
        public void selected(EntityView<E> aView, List<Parameter> aParameters, List<Field> aFields) {
            List<SelectedParameter<E>> parameters = new ArrayList<>();
            List<SelectedField<E>> fields = new ArrayList<>();
            for (EntityView<E> ev : entityViews.values()) {
                if (ev != aView) {
                    List<Parameter> pl = ev.getSelectedParameters();
                    for (Parameter p : pl) {
                        parameters.add(new SelectedParameter<>(ev.getEntity(), p));
                    }
                    List<Field> fl = ev.getSelectedFields();
                    for (Field f : fl) {
                        fields.add(new SelectedField<>(ev.getEntity(), f));
                    }
                }
            }
            for (Parameter p : aParameters) {
                parameters.add(new SelectedParameter<>(aView.getEntity(), p));
            }
            for (Field f : aFields) {
                fields.add(new SelectedField<>(aView.getEntity(), f));
            }
            for (Field f : aParameters) {
                selectedFields.add(new EntityFieldTuple(aView.getEntity(), f));
            }
            for (Field f : aFields) {
                selectedFields.add(new EntityFieldTuple(aView.getEntity(), f));
            }
            checkActions();
            fireFieldParamSelected(parameters, fields);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                EntityView<E> ev = DatamodelDesignUtils.lookupEntityView((Component) e.getSource());
                if (ev != null) {
                    if (!isViewSelected(ev)) {
                        if (!e.isControlDown()) {
                            clearEntitiesSelection(null);
                            clearEntitiesFieldsSelection();
                        }
                        selectView(ev);
                    } else {
                        if (e.isControlDown()) {
                            unselectView(ev);
                        } else {
                            clearEntitiesSelection(ev);
                            clearEntitiesFieldsSelection();
                        }
                    }
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof Component) {
                EntityView<E> ev = DatamodelDesignUtils.lookupEntityView((Component) e.getSource());
                if (ev != null) {
                    if (e.getClickCount() > 1) {
                        fireEntityViewDoubleClicked(ev, e.getSource() == ev.getFieldsList(), e.getSource() == ev.getParametersList());
                    }
                }
            }
        }
    }

    public enum SearchSubject {

        FIND_SUBJECT_DATASOURCES,
        FIND_SUBJECT_FIELDS,
        FIND_SUBJECT_PARAMS
    }

    public class Move extends DmAction {

        public Move() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Move.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Move.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("hand.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }
    }

    public class ZoomSmooth extends DmAction {

        public ZoomSmooth() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(ZoomSmooth.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(ZoomSmooth.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("zoom.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }
    }

    public class ZoomIn extends DmAction {

        public ZoomIn() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getParent() != null && getParent().getParent() != null
                    && getParent().getParent() instanceof JScalablePanel) {
                JScalablePanel sp = (JScalablePanel) getParent().getParent();
                sp.ZoomIn();
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(ZoomIn.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(ZoomIn.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("zoom+.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0);
        }
    }

    public class ZoomOut extends DmAction {

        public ZoomOut() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getParent() != null && getParent().getParent() != null
                    && getParent().getParent() instanceof JScalablePanel) {
                JScalablePanel sp = (JScalablePanel) getParent().getParent();
                sp.ZoomOut();
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(ZoomOut.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(ZoomOut.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("zoom-.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0);
        }
    }

    public class Zoom extends DmAction {

        public Zoom() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() != null && e.getSource() instanceof JComboBox) {
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                Object so = cb.getSelectedItem();
                if (so != null && so instanceof String) {
                    String zS = (String) so;
                    try {
                        if (zS.length() > 1) {
                            zS = zS.substring(0, zS.length() - 1);
                            zS = zS.replace(',', '.');
                            float scaleFactor = 1;
                            try {
                                scaleFactor = Float.valueOf(zS);
                            } catch (NumberFormatException nfe) {
                                zS = zS.replace('.', ',');
                                scaleFactor = Float.valueOf(zS);
                            }
                            if (getParent() != null && getParent().getParent() != null
                                    && getParent().getParent() instanceof JScalablePanel) {
                                JScalablePanel sp = (JScalablePanel) getParent().getParent();
                                sp.setScale(scaleFactor / 100);
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(ModelView.this, DatamodelDesignUtils.getLocalizedString("Non_a_number_typed"), DatamodelDesignUtils.getLocalizedString("Datamodel"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Zoom.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Zoom.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("zoom.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return null;
        }
    }

    public class GoRight extends GoLeft {

        public GoRight() {
            super();
        }

        @Override
        protected E getTargetEntity(Relation<E> aRel) {
            return aRel.getRightEntity();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(GoRight.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(GoRight.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("toRight.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public class GoLeft extends DmAction {

        public GoLeft() {
            super();
        }

        protected E getTargetEntity(Relation<E> aRel) {
            return aRel.getLeftEntity();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Set<Relation<E>> rels = getSelectedRelations();
            if (rels != null && rels.size() == 1) {
                Relation<E> rel = rels.iterator().next();
                if (rel != null && rel.getLeftEntity() != null) {
                    E lEntity = getTargetEntity(rel);
                    EntityView<E> eView = getEntityView(lEntity);
                    makeVisible(eView, true);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return isSelectedRelations();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(GoLeft.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(GoLeft.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("toLeft.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public class Find extends DmAction {

        public Find() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (finder == null) {
                JFrame pFr = DatamodelDesignUtils.getRootFrame(ModelView.this);
                if (pFr != null) {
                    finder = new FindFrame<>(ModelView.this, pFr);
                } else {
                    JDialog pDlg = DatamodelDesignUtils.getRootDialog(ModelView.this);
                    if (pDlg != null) {
                        finder = new FindFrame<>(ModelView.this, pDlg);
                    } else {
                        finder = new FindFrame<>(ModelView.this);
                    }
                }
            }
            if (finder != null) {
                finder.setVisible(true);
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Find.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Find.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("find.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public class AddTable extends DmAction {

        protected TableRef[] justSelected = null;

        public AddTable() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            justSelected = null;
            try {
                if (isEnabled() && model != null) {
                    TableRef oldValue = prepareTableRef4Selection();
                    TableRef[] selected = selectTableRef(oldValue);
                    if (selected != null) {
                        for (TableRef rSelected : selected) {
                            Rectangle rect = findAnyFreeSpace(0, 0);
                            E entity = model.newGenericEntity();
                            entity.setX(rect.x);
                            entity.setY(rect.y);
                            entity.setWidth(rect.width);
                            entity.setHeight(rect.height);
                            entity.setTableDbId(rSelected.dbId);
                            entity.setTableSchemaName(rSelected.schema);
                            entity.setTableName(rSelected.tableName);
                            NewEntityEdit<E, M> edit = new NewEntityEdit<>(model, entity);
                            edit.redo();
                            undoSupport.postEdit(edit);
                        }
                        justSelected = selected;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(AddTable.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(AddTable.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("table-plus.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        }
    }

    public void doAddQuery(String aApplicationElementId) throws Exception {
        if (aApplicationElementId != null && model != null) {
            Rectangle rect = findAnyFreeSpace(0, 0);
            E entity = model.newGenericEntity();
            entity.setX(rect.x);
            entity.setY(rect.y);
            entity.setWidth(rect.width);
            entity.setHeight(rect.height);
            entity.setQueryId(aApplicationElementId);
            NewEntityEdit<E, M> edit = new NewEntityEdit<>(model, entity);
            edit.redo();
            undoSupport.postEdit(edit);
        }
    }

    protected abstract boolean isAnyDeletableEntities();

    public class Delete extends DmAction {

        public Delete() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                if (isAnySelectedEntities()) {
                    List<Object> selected = Arrays.asList(selectedEntities.toArray());
                    for (Object oEntity : selected) {
                        if (!isParametersEntity((E) oEntity)) {
                            E entity = (E) oEntity;
                            if (((M) entity.getModel()).checkEntityRemovingValid(entity)) {
                                Set<Relation<E>> lbinded = model.collectRelationsByEntity(entity);
                                if (lbinded.isEmpty()) {
                                    DeleteEntityEdit<E, M> ledit = new DeleteEntityEdit<>(model, entity);
                                    ledit.redo();
                                    undoSupport.postEdit(ledit);
                                } else {
                                    undoSupport.beginUpdate();
                                    try {
                                        for (Relation<E> lrel : lbinded) {
                                            DeleteRelationEdit<E> ldredit = new DeleteRelationEdit<>(lrel);
                                            ldredit.redo();
                                            undoSupport.postEdit(ldredit);
                                        }
                                        DeleteEntityEdit<E, M> ledit = new DeleteEntityEdit<>(model, entity);
                                        ledit.redo();
                                        undoSupport.postEdit(ledit);
                                    } finally {
                                        undoSupport.endUpdate();
                                    }
                                }
                            }
                        }
                    }
                } else if (isSelectedRelations()) {
                    Set<Relation<E>> rels = getSelectedRelations();
                    if (rels != null && !rels.isEmpty()) {
                        for (Relation<E> l2DelRel : rels) {
                            if (!model.checkRelationRemovingValid(l2DelRel)) {
                                return;
                            }
                        }
                        if (rels.size() == 1) {
                            DeleteRelationEdit<E> drEdit = new DeleteRelationEdit<>(rels.iterator().next());
                            drEdit.redo();
                            undoSupport.postEdit(drEdit);
                        } else {
                            undoSupport.beginUpdate();
                            try {
                                DeleteRelationEdit<?>[] ledits = new DeleteRelationEdit<?>[rels.size()];
                                int j = 0;
                                for (Relation<E> rel : rels) {
                                    ledits[j++] = new DeleteRelationEdit<>(rel);
                                }
                                for (j = 0; j < ledits.length; j++) {
                                    undoSupport.postEdit(ledits[j]);
                                    ledits[j].redo();
                                }
                            } finally {
                                undoSupport.endUpdate();
                                rerouteConnectors();
                            }
                        }
                    }
                } else if (isSelectedDeletableFields()) {
                    deleteSelectedFields();
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return isShowing() && (isAnyDeletableEntities() ^ isSelectedRelations() ^ isSelectedDeletableFields());
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Delete.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Delete.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("delete.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        }
    }

    public class AddField extends DmAction {

        public AddField() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                NewFieldEdit edit = new NewFieldEdit(getEntity());
                edit.redo();
                undoSupport.postEdit(edit);
            }
            checkActions();
        }

        protected Entity getEntity() {
            return getParametersView().getEntity();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(AddField.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(AddField.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("new.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        }
    }

    public class Cut extends DmAction {

        public Cut() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Action copy = getActionMap().get(Copy.class.getSimpleName());
            Action delete = getActionMap().get(Delete.class.getSimpleName());
            if (copy != null && delete != null) {
                ActionEvent ae = new ActionEvent(e.getSource(), 0, "");
                copy.actionPerformed(ae);
                delete.actionPerformed(ae);
            }
        }

        @Override
        public boolean isEnabled() {
            return isAnyDeletableEntities();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Cut.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Cut.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("cut.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public class Copy extends DmAction {

        public Copy() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                try {
                    M model = newModelInstance();
                    model.setClient(model.getClient());
                    for (E ent : selectedEntities) {
                        if (ent != null && !isParametersEntity(ent)) {
                            model.addEntity(ent);
                        }
                    }
                    try {
                        Document doc = model.toXML();
                        if (doc != null) {
                            String sEntity = XmlDom2String.transform(doc);
                            string2SystemClipboard(sEntity);
                        }
                    } finally {
                        model.getEntities().clear();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return selectedEntities != null && !selectedEntities.isEmpty() && (selectedEntities.size() > 1 || !isParametersEntity(selectedEntities.iterator().next()));
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Copy.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Copy.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("copy.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
        }

        private void string2SystemClipboard(String sEntity) throws HeadlessException {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (sEntity != null && clipboard != null) {
                StringSelection ss = new StringSelection(sEntity);
                clipboard.setContents(ss, ss);
            }
        }
    }

    public class Paste extends DmAction {

        public Paste() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                doWork();
            } catch (Exception ex) {
                Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        protected List<E> doWork() throws Exception {
            List<E> entitiesPasted = new ArrayList<>();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard != null) {
                Transferable tr = clipboard.getContents(null);
                if (tr != null) {
                    try {
                        Object oData = tr.getTransferData(DataFlavor.stringFlavor);
                        if (oData != null && oData instanceof String) {
                            String sDm = (String) oData;
                            Document doc = Source2XmlDom.transform(sDm);
                            if (doc != null) {
                                M pastedModel = transformDocToModel(doc);
                                if (pastedModel != null && model != null) {
                                    Map<Long, E> entities = pastedModel.getEntities();
                                    boolean isSection = entities != null && !entities.isEmpty();
                                    if (isSection) {
                                        undoSupport.beginUpdate();
                                    }
                                    try {
                                        if (entities != null && !entities.isEmpty()) {
                                            Set<Map.Entry<Long, E>> entSet = entities.entrySet();
                                            if (entSet != null) {
                                                for (Map.Entry<Long, E> entEntry : entSet) {
                                                    E toPaste = entEntry.getValue();
                                                    if (isPasteable(toPaste)) {
                                                        prepareEntityForPaste(toPaste);
                                                        checkPastingName(toPaste);
                                                        if (model.checkEntityAddingValid(toPaste)) {
                                                            toPaste.setX(Integer.MAX_VALUE);
                                                            toPaste.setY(Integer.MAX_VALUE);
                                                            Rectangle rect = findAnyFreeSpace(0, 0);
                                                            toPaste.setX(rect.x);
                                                            toPaste.setY(rect.y);
                                                            toPaste.setWidth(rect.width);
                                                            toPaste.setHeight(rect.height);
                                                            NewEntityEdit<E, M> edit = new NewEntityEdit<>(model, toPaste);
                                                            edit.redo();
                                                            undoSupport.postEdit(edit);
                                                            entitiesPasted.add(toPaste);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } finally {
                                        if (isSection) {
                                            undoSupport.endUpdate();
                                        }
                                    }
                                    model.resolveReferences();
                                }
                            } else {
                                JOptionPane.showMessageDialog(ModelView.this, DatamodelDesignUtils.getLocalizedString("BadClipboardData"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                        JOptionPane.showMessageDialog(ModelView.this, DatamodelDesignUtils.getLocalizedString("BadClipboardData"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(ModelView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return entitiesPasted;
        }

        @Override
        public boolean isEnabled() {
            if (Toolkit.getDefaultToolkit() != null) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard != null) {
                    return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
                }
            }
            return false;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Paste.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Paste.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("paste.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public static class EntityFieldTuple {

        public final Entity entity;
        public final Field field;

        public EntityFieldTuple(Entity anEntity, Field aField) {
            entity = anEntity;
            field = aField;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof EntityFieldTuple) {
                EntityFieldTuple t = (EntityFieldTuple) obj;
                return ((entity == null && t.entity == null) || (entity != null && entity.equals(t.entity)))
                        && ((field == null && t.field == null) || (field != null && field.equals(t.field)));
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + Objects.hashCode(this.entity);
            hash = 53 * hash + Objects.hashCode(this.field);
            return hash;
        }
    }
}