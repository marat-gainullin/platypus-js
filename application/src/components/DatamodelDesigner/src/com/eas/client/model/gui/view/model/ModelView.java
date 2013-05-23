/*
 * DatamodelView.java
 *
 * Created on 13 Август 2008 г., 9:16
 */
package com.eas.client.model.gui.view.model;

import com.bearsoft.routing.Connector;
import com.bearsoft.routing.PathFragment;
import com.bearsoft.routing.Paths;
import com.bearsoft.routing.QuadTree;
import com.bearsoft.routing.Sweeper;
import com.bearsoft.routing.graph.Vertex;
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
import com.eas.client.model.gui.view.FieldSelectionListener;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.ModelViewDragHandler;
import com.eas.client.model.gui.view.RelationDesignInfo;
import com.eas.client.model.gui.view.RelationsFieldsDragHandler;
import com.eas.client.model.gui.view.entities.EntityView;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    protected final static Stroke slotsStroke = new BasicStroke(slotWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    protected final static Stroke connectorsStroke = new BasicStroke(connectorWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke connectorsOuterStroke = new BasicStroke(connectorWidth + 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke hittedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke selectedConnectorsStroke = new BasicStroke(connectorWidth + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke hittedConnectorsOuterStroke = new BasicStroke(connectorWidth + 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    protected final static Stroke selectedConnectorsOuterStroke = new BasicStroke(connectorWidth + 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    // style (colors, width etc.)
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
    protected FindFrame<E, P, M> finder;
    protected Component dragTarget;
    protected boolean reallyDragged;
    protected boolean needRerouteConnectors = true;
    protected QuadTree<EntityView<E>> entitiesIndex = new QuadTree<>();
    protected QuadTree<Relation<E>> connectorsIndex = new QuadTree<>();
    protected Paths paths;
    // selection
    protected Set<Relation<E>> hittedRelations = new HashSet<>();
    protected Set<Relation<E>> selectedRelations = new HashSet<>();
    protected Set<E> selectedEntities = new HashSet<>();
    protected Set<EntityFieldTuple> selectedFields = new HashSet<>();
    // interaction
    protected ModelChangesReflector modelListener = new ModelChangesReflector();
    protected NotSavableUndoableEditSupport undoSupport;
    protected TablesSelectorCallback tablesSelector;
    // events
    protected EntityViewPropagator entityViewPropagator = new EntityViewPropagator();
    protected EntitiesViewsMovesManager entitiesViewsMover = new EntitiesViewsMovesManager();
    protected Set<EntityViewDoubleClickListener<E>> entityViewDoubleClickListeners = new HashSet<>();
    protected Set<ModelSelectionListener<E>> entitySelectionListeners = new HashSet<>();

    protected void refreshView() {
        entitiesIndex = new QuadTree<>();
        connectorsIndex = new QuadTree<>();
        for (EntityView<E> view : entityViews.values()) {
            entitiesIndex.insert(view.getBounds(), view);
        }
        preparePaths();
        rerouteConnectors();
        checkActions();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (!isPreferredSizeSet()) {
            for (Component c : getComponents()) {
                Rectangle b = c.getBounds();
                if (b.x + b.width > size.width) {
                    size.width = b.x + b.width;
                }
                if (b.y + b.height > size.height) {
                    size.height = b.y + b.height;
                }
            }
            size.width += EntityView.INSET_ZONE;
            size.height += EntityView.INSET_ZONE;
        }
        return size;
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
            EntityView<E> eView = entityViews.get(e.getEntityId());
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

    public Rectangle getUnscaledBounds() {
        Rectangle res = getBounds();
        if (getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof JScalablePanel) {
            JScalablePanel sp = (JScalablePanel) getParent().getParent();
            res.width /= sp.getScale();
            res.height /= sp.getScale();
        }
        return res;
    }

    public Rectangle findPlaceForEntityAdd(int aInitialX, int aInitialY) {
        Set<EntityView<E>> hitted = hittestEntitiesViews(new Point(aInitialX, aInitialY));
        while (haveSameLeftTop(hitted, new Point(aInitialX, aInitialY))) {
            aInitialX += EntityView.INSET_ZONE;
            aInitialY += EntityView.INSET_ZONE * 3;
            hitted = hittestEntitiesViews(new Point(aInitialX, aInitialY));
        }
        return new Rectangle(aInitialX, aInitialY, EntityView.ENTITY_VIEW_DEFAULT_WIDTH, EntityView.ENTITY_VIEW_DEFAULT_HEIGHT);
    }

    protected void findPlaceForEntityPaste(E aEntity) {
        Set<EntityView<E>> hitted = hittestEntitiesViews(new Point(aEntity.getX(), aEntity.getY()));
        while (haveSameLeftTop(hitted, new Point(aEntity.getX(), aEntity.getY()))) {
            aEntity.setX(aEntity.getX() + EntityView.INSET_ZONE);
            aEntity.setY(aEntity.getY() + EntityView.INSET_ZONE * 3);
            hitted = hittestEntitiesViews(new Point(aEntity.getX(), aEntity.getY()));
        }
    }

    private boolean haveSameLeftTop(Set<EntityView<E>> aViews, Point aPoint) {
        if (aViews != null) {
            for (EntityView<E> eView : aViews) {
                if (eView.getLocation().equals(aPoint)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<EntityView<E>> hittestEntitiesViews(Point aPoint) {
        Set<EntityView<E>> filtered = new HashSet<>();
        List<EntityView<E>> hitted = entitiesIndex.query(aPoint);
        for (EntityView<E> eView : hitted) {
            Rectangle rect = eView.getBounds();
            if (rect.x <= aPoint.x && aPoint.x <= rect.x + rect.width - 1
                    && rect.y <= aPoint.y && aPoint.y <= rect.y + rect.height - 1) {
                filtered.add(eView);
            }
        }
        return filtered;
    }

    protected class ModelChangesReflector implements ModelEditingListener<E> {

        @Override
        public void entityAdded(E aEntity) {
            final EntityView<E> eView = createEntityView(aEntity);
            eView.reLayout();
            addEntityView(eView);
        }

        @Override
        public void entityRemoved(E removed) {
            clearRelationsSelection();
            removeEntityView(removed);
        }

        @Override
        public void relationAdded(Relation<E> added) {
            RelationDesignInfo rdesign = new RelationDesignInfo();
            relationsDesignInfo.put(added, rdesign);
            // let's populate relation's design info
            if (paths != null) {
                calcConnector(added);
            }
            repaint();
        }

        @Override
        public void relationRemoved(Relation<E> removed) {
            RelationDesignInfo rdesign = relationsDesignInfo.get(removed);
            if (rdesign != null) {
                connectorsIndex.remove(getUnscaledBounds(), removed);
                relationsDesignInfo.remove(removed);
                clearRelationsSelection();
                repaint();
            }
        }

        @Override
        public void entityIndexesChanged(E e) {
        }
    }

    public EntityView<E> getParametersView() {
        if (entityViews != null && model != null && model.getParametersEntity() != null) {
            return entityViews.get(model.getParametersEntity().getEntityId());
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

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    protected boolean calcSlots(Segment fslot, Segment lslot, Relation<E> lrel, Map<Long, EntityView<E>> aViews) {
        boolean isBothOnTheRight = false;

        EntityView<E> lView = aViews.get(lrel.getLeftEntity().getEntityId());
        EntityView<E> rView = aViews.get(lrel.getRightEntity().getEntityId());

        Point lpt = null;
        Point lpt1 = null;
        Point rpt = null;
        Point rpt1 = null;

        Rectangle lViewBounds = lView.getBounds();
        //lViewBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
        Rectangle rViewBounds = rView.getBounds();
        //rViewBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
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
        lslot.firstPoint = rpt;
        lslot.lastPoint = rpt1;
        return isBothOnTheRight;
    }

    public EntityView<E> getEntityView(E aEnt) {
        return aEnt != null ? entityViews.get(aEnt.getEntityId()) : null;
    }

    protected void calcConnectors(Set<Relation<E>> rels) {
        if (rels != null) {
            for (Relation<E> rel : rels) {
                calcConnector(rel);
            }
        }
    }

    protected void calcConnector(Relation<E> rel) {
        if (rel != null) {
            RelationDesignInfo rdesign = getRelationDesignInfo(rel);
            connectorsIndex.remove(getUnscaledBounds(), rel);
            Segment fslot = new Segment();
            Segment lslot = new Segment();
            calcSlots(fslot, lslot, rel, entityViews);
            rdesign.setFirstSlot(fslot);
            rdesign.setLastSlot(lslot);
            if (rdesign.getConnector() == null || !rdesign.getConnector().isManual()) {
                rdesign.setConnector(paths.find(fslot.lastPoint, lslot.lastPoint));
            }
            DatamodelDesignUtils.addToQuadTree(connectorsIndex, rdesign.getConnector(), rel);
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
                    Field lField = rel.getLeftField();
                    if (rel.isLeftField()) {
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
                    Field rField = rel.getRightField();
                    if (rView != null) {
                        if (rel.isRightField()) {
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
        public void collapseExpand(EntityView<E> aView, int dy) {
            if (dy != 0) {
                entitiesIndex.remove(aView.getBounds(), aView);
                try {
                    if (dy > 0) {
                        CollapserExpander.expand(entitiesIndex, aView, aView.getParent().getBounds(), Math.abs(dy), aView.getUndoSupport());
                    } else if (dy < 0) {
                        // Nothing can obstruct collapse, so we do nothing
                        //CollapserExpander.collapse(entitiesIndex, aView, aView.getParent().getBounds(), Math.abs(dy), aView.getUndoSupport());
                    }
                } finally {
                    entitiesIndex.insert(aView.getBounds(), aView);
                }
            }
        }

        @Override
        public void invalidateConnectors() {
            rerouteConnectors();
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
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                preparePaths();
                rerouteConnectors();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                preparePaths();
                rerouteConnectors();
            }
        });
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
                model.removeEditingListener(modelListener);
            }
            model = aModel;
            recreateEntityViews();
            if (model != null) {
                model.addEditingListener(modelListener);
            }
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

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            paintConnectors(g2d, model.getRelations(), connectorsStroke);
            Color old2FieldColor = toFieldConnectorColor;
            toFieldConnectorColor = toFieldConnectorColor.darker();
            Color old2ParameterColor = toParameterConnectorColor;
            toParameterConnectorColor = toParameterConnectorColor.darker();
            try {
                paintConnectors(g2d, selectedRelations, selectedConnectorsStroke);
                paintConnectors(g2d, hittedRelations, hittedConnectorsStroke);
            } finally {
                toFieldConnectorColor = old2FieldColor;
                toParameterConnectorColor = old2ParameterColor;
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
                    if (lrel.getRightEntity() != null && lrel.getRightField() != null) {
                        Field toField = lrel.getRightEntity().getFields().get(lrel.getRightField().getName());
                        if (toField == null || toField != lrel.getRightField()) {
                            g2d.setColor(toParameterConnectorColor);
                        } else {
                            g2d.setColor(toFieldConnectorColor);
                        }
                    }
                    RelationDesignInfo designInfo = getRelationDesignInfo(lrel);
                    if (designInfo.getConnector() != null) {
                        g2d.drawPolyline(designInfo.getConnector().getX(), designInfo.getConnector().getY(), designInfo.getConnector().getSize());
                    }
                    if (designInfo.getLastSlot() != null) {
                        paintLastSlot(g2d, designInfo.getLastSlot(), 0);
                    }
                }
            } finally {
                g2d.setStroke(loldStroke);
                g2d.setColor(loldColor);
            }
        }
    }

    protected void paintLastSlots(Graphics2D g2d, Set<Relation<E>> aRels, Stroke aConnectorsStroke, int aWide) {
        if (aRels != null && !aRels.isEmpty()) {
            Color loldColor = g2d.getColor();
            Stroke loldStroke = g2d.getStroke();
            g2d.setStroke(aConnectorsStroke);
            try {
                for (Relation<E> rel : aRels) {
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

    protected abstract EntityView<E> createGenericEntityView(E aEntity);

    protected abstract boolean isPasteable(E aEntityToPaste);

    protected abstract boolean isSelectedDeletableFields();

    protected void deleteSelectedFields() {
        if (isSelectedDeletableFields()) {
            /*
             Set<Relation<E>> toConfirm = new HashSet<>();
             for (EntityFieldTuple t : selectedFields) {
             Set<Relation<E>> toDel = FieldsEntity.getInOutRelationsByEntityField(t.entity, t.field);
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
             */
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

    public void addEntityView(final EntityView<E> eView) {
        entityViews.put(eView.getEntityID(), eView);
        eView.addFieldSelectionListener(entityViewPropagator);
        eView.getUndoSupport().addUndoableEditListener(entityViewPropagator);
        eView.addMouseListener(entityViewPropagator);
        if (isParametersEntity(eView.getEntity())) {
            modelParametersEntityView = eView;
        }
        add(eView, 0);
        entitiesIndex.insert(eView.getBounds(), eView);
        preparePaths();
        rerouteConnectors();
        eView.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent e) {
                entitiesIndex.remove(eView.getBounds(), eView);
                preparePaths();
                rerouteConnectors();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                entitiesIndex.remove(getUnscaledBounds(), eView);
                entitiesIndex.insert(eView.getBounds(), eView);
                for (Relation rel : eView.getEntity().getInOutRelations()) {
                    RelationDesignInfo rdesign = getRelationDesignInfo(rel);
                    if (rdesign.getConnector() != null) {
                        rdesign.getConnector().setManual(false);
                    }
                }
                preparePaths();
                rerouteConnectors();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                entitiesIndex.remove(getUnscaledBounds(), eView);
                entitiesIndex.insert(eView.getBounds(), eView);
                for (Relation rel : eView.getEntity().getInOutRelations()) {
                    RelationDesignInfo rdesign = getRelationDesignInfo(rel);
                    if (rdesign.getConnector() != null) {
                        rdesign.getConnector().setManual(false);
                    }
                }
                preparePaths();
                rerouteConnectors();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                entitiesIndex.insert(eView.getBounds(), eView);
                preparePaths();
                rerouteConnectors();
            }
        });
        if (eView.getFieldsList().getParent().getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) eView.getFieldsList().getParent().getParent();
            viewport.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (paths != null) {
                        rerouteConnectors();
                    }
                }
            });
        }
    }

    public void removeEntityView(E aEntity) {
        if (aEntity != null && !isParametersEntity(aEntity)) {
            Long entityId = aEntity.getEntityId();
            EntityView<E> eView = entityViews.get(entityId);
            if (eView != null) {
                if (isViewSelected(eView)) {
                    unselectView(eView);
                }
                entityViews.remove(entityId);
                eView.removeFieldSelectionListener(entityViewPropagator);
                eView.getUndoSupport().removeUndoableEditListener(entityViewPropagator);
                eView.removeMouseListener(entityViewPropagator);
                eView.getFieldsList().removeMouseListener(entityViewPropagator);
                eView.getParametersList().removeMouseListener(entityViewPropagator);
                remove(eView);
                entitiesIndex.remove(eView.getBounds(), eView);
                eView.shrink();
                preparePaths();
            }
        }
    }

    public void removeEntityViews() {
        needRerouteConnectors = false;
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
            createEntityViews();
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
        removeEntityViews();
        needRerouteConnectors = false;
        try {
            Map<Long, E> entMap = model.getAllEntities();
            if (entMap != null && !entMap.isEmpty()) {
                Collection<E> entCol = entMap.values();
                if (entCol != null) {
                    List<E> entities = new ArrayList<>();
                    entities.addAll(entCol);
                    for (E entity : entities) {
                        EntityView<E> eView = createEntityView(entity);
                        addEntityView(eView);
                    }
                }
            }
        } finally {
            needRerouteConnectors = true;
        }
    }

    protected void preparePaths() {
        if (needRerouteConnectors) {
            Set<Rectangle> obstacles = new HashSet<>();
            for (EntityView<E> eView : entityViews.values()) {
                obstacles.add(eView.getBounds());
            }
            QuadTree<Vertex<PathFragment>> verticesIndex = new QuadTree<>();
            List<Vertex<PathFragment>> graph = Sweeper.build(getWidth(), getHeight(), obstacles, verticesIndex);
            paths = new Paths(graph, verticesIndex);
        }
    }

    public void rerouteConnectors() {
        if (model != null && needRerouteConnectors) {
            Set<Relation<E>> modelRels = model.getRelations();
            Set<Relation<E>> filteredRels = new HashSet<>();
            if (modelRels != null) {
                for (Relation<E> rel : modelRels) {
                    if (rel != null && getEntityView(rel.getLeftEntity()) != null
                            && getEntityView(rel.getRightEntity()) != null) {
                        filteredRels.add(rel);
                    }
                }
            }
            // Calc connectors
            calcConnectors(filteredRels);
            repaint();
        }
    }

    protected class ModelViewMouseHandler implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e != null) {
                Point lPt = e.getPoint();
                Object lo = e.getSource();
                if (lo != null && lo instanceof Component) {
                    Set<Relation<E>> oldSelectedRelations = new HashSet<>();
                    oldSelectedRelations.addAll(selectedRelations);
                    if (!(lo instanceof ModelView)) {
                        lPt = SwingUtilities.convertPoint((Component) lo, lPt, ModelView.this);
                    }
                    if (e.isControlDown()) {
                        Set<Relation<E>> lselectedRelations = hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);
                        if (selectedRelations != null && lselectedRelations != null) {
                            selectedRelations.addAll(lselectedRelations);
                        } else if (selectedRelations == null && lselectedRelations != null) {
                            selectedRelations = lselectedRelations;
                        }
                    } else {
                        clearSelection();
                        selectedRelations = hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);
                    }
                    hittedRelations = selectedRelations;
                    repaint();
                    fireRelationsSelectionChanged(oldSelectedRelations, selectedRelations);
                }
            }
        }
        private HittedRelationSegment hittedSegment;

        @Override
        public void mousePressed(MouseEvent e) {
            hittedSegment = selectHittedRelationSegment(e.getPoint(), EntityView.HALF_INSET_ZONE);
            if (hittedSegment != null) {
                connectorsIndex.remove(getUnscaledBounds(), hittedSegment.relation);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (hittedSegment != null) {
                DatamodelDesignUtils.addToQuadTree(connectorsIndex, hittedSegment.rdesign.getConnector(), hittedSegment.relation);
            }
            hittedSegment = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (hittedSegment != null) {
                hittedSegment.move(e.getPoint());
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (e != null) {
                Point lPt = e.getPoint();
                Object lo = e.getSource();
                if (lo != null && lo instanceof Component) {
                    if (!(lo instanceof ModelView)) {
                        lPt = SwingUtilities.convertPoint((Component) lo, lPt, ModelView.this);
                    }
                    hittedRelations = hittestRelationsConnectors(lPt, EntityView.HALF_INSET_ZONE);
                    HittedRelationSegment hittedSegment = selectHittedRelationSegment(lPt, EntityView.HALF_INSET_ZONE);
                    if (hittedSegment != null) {
                        if (hittedSegment.isHMovable()) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                        } else if (hittedSegment.isVMovable()) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                        } else {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    } else {
                        setCursor(Cursor.getDefaultCursor());
                    }
                    repaint();
                }
                if (hittedRelations != null && !hittedRelations.isEmpty()) {
                    setToolTipText(generateHintFromRelations(hittedRelations));
                } else {
                    setToolTipText(null);
                }
            }
        }

        private Set<Relation<E>> hittestRelationsConnectors(Point aPoint, int aEpsilon) {
            Set<Relation<E>> hitted = new HashSet<>();
            List<Relation<E>> res = connectorsIndex.query(new Rectangle(aPoint.x, aPoint.y, aEpsilon, aEpsilon));
            for (Relation rel : res) {
                RelationDesignInfo rdesign = getRelationDesignInfo(rel);
                if (rdesign != null) {
                    Connector connector = rdesign.getConnector();
                    if (DatamodelDesignUtils.hittestConnector(connector, aPoint, aEpsilon)) {
                        hitted.add(rel);
                    }
                }
            }
            return hitted;
        }

        private class HittedRelationSegment {

            public Point pressedPoint;
            public Relation relation;
            public RelationDesignInfo rdesign;
            public int segmentNumber = -1;

            public HittedRelationSegment() {
            }

            public boolean isMovable() {
                return segmentNumber > 0 && segmentNumber < rdesign.getConnector().getSize() - 2;
            }

            public boolean isHMovable() {
                if (isMovable()) {
                    int x1 = rdesign.getConnector().getX()[segmentNumber];
                    int x2 = rdesign.getConnector().getX()[segmentNumber + 1];
                    return x1 == x2;
                } else {
                    return false;
                }
            }

            public boolean isVMovable() {
                if (isMovable()) {
                    int y1 = rdesign.getConnector().getY()[segmentNumber];
                    int y2 = rdesign.getConnector().getY()[segmentNumber + 1];
                    return y1 == y2;
                } else {
                    return false;
                }
            }

            private void move(Point point) {
                int dx = point.x - pressedPoint.x;
                int dy = point.y - pressedPoint.y;
                if (isHMovable()) {
                    if (dx != 0) {
                        rdesign.getConnector().getX()[segmentNumber] += dx;
                        rdesign.getConnector().getX()[segmentNumber + 1] += dx;
                        rdesign.getConnector().setManual(true);
                        pressedPoint = point;
                    }
                } else if (isVMovable()) {
                    if (dy != 0) {
                        rdesign.getConnector().getY()[segmentNumber] += dy;
                        rdesign.getConnector().getY()[segmentNumber + 1] += dy;
                        rdesign.getConnector().setManual(true);
                        pressedPoint = point;
                    }
                }
            }
        }

        private HittedRelationSegment selectHittedRelationSegment(Point aPoint, int aEpsilon) {
            if (hittedRelations != null && !hittedRelations.isEmpty()) {
                HittedRelationSegment res = new HittedRelationSegment();
                res.pressedPoint = aPoint;
                res.relation = hittedRelations.iterator().next();
                res.rdesign = getRelationDesignInfo(res.relation);
                res.segmentNumber = DatamodelDesignUtils.hittestConnectorSegment(res.rdesign.getConnector(), aPoint, aEpsilon);
                return res;
            } else {
                return null;
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
                        int x = ALLOCATION_STEP_X;
                        int y = ALLOCATION_STEP_Y;
                        for (TableRef rSelected : selected) {
                            Rectangle rect = findPlaceForEntityAdd(x, y);
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
                            x = rect.x + rect.width + ALLOCATION_STEP_X;
                            if (x > getWidth() * 2) {
                                x = ALLOCATION_STEP_X;
                                y = rect.y + rect.height + ALLOCATION_STEP_Y;
                            }
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
            Rectangle rect = findPlaceForEntityAdd(0, 0);
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
                        && ((field == null && t.field == null) || (field != null && field.isEqual(t.field)));
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