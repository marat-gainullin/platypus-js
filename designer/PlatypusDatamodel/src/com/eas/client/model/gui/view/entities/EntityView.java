/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view.entities;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.CollectionListener;
import com.eas.client.SQLUtils;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.IconCache;
import com.eas.client.model.gui.edits.HideEntityFieldsEdit;
import com.eas.client.model.gui.edits.MoveEntityEdit;
import com.eas.client.model.gui.edits.RelationPolylineEdit;
import com.eas.client.model.gui.edits.ResizeEntityEdit;
import com.eas.client.model.gui.edits.ShowEntityFieldsEdit;
import com.eas.client.model.gui.view.EntityViewsManager;
import com.eas.client.model.gui.view.FieldSelectionListener;
import com.eas.client.model.gui.view.FieldsListModel;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import com.eas.client.model.gui.view.IconsListCellRenderer;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.gui.CascadedStyle;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public abstract class EntityView<E extends Entity<?, ?, E>> extends JPanel {

    // Constants
    public static final Color selectedColor = (new JButton()).getBackground().darker();//(new JTextField()).getSelectionColor();
    public static final Color unselectedColor = (new JButton()).getBackground();
    public static final Color borderColor = selectedColor;
    public static final Color selectedBorderColor = selectedColor.darker();
    public static final Stroke allwaysBorderStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2, 2}, 0);
    public static final int ENTITY_VIEW_DEFAULT_WIDTH = 150;
    public static final int ENTITY_VIEW_DEFAULT_HEIGHT = 200;
    public static final Border ordinaryBorder = new CompoundBorder(new LineBorder(borderColor, 1, false), new LineBorder(borderColor.brighter().brighter().brighter().brighter(), 1, false));
    public static final Border selectedBorder = new CompoundBorder(new LineBorder(selectedBorderColor.darker(), 1, false), new LineBorder(borderColor.brighter().brighter().brighter().brighter(), 1, false));
    // Zone around of each view to guarantee accessibility
    // to any view
    public static final Integer INSET_ZONE = 6; //minimum 3
    public static final Integer HALF_INSET_ZONE = INSET_ZONE >>> 1;
    // Data
    protected E entity = null;
    // Controls
    protected Action minimizeRestoreAction;
    protected EntityViewMoverResizer viewMover = new EntityViewMoverResizer();
    protected JToolBar toolsToolbar;
    protected JToolBar titleToolbar;
    protected JLabel titleLabel;
    protected JPanel toolbarPanel;
    protected JPanel paramsFieldsPanel;
    protected JList<Field> fieldsList = new JList() {
        @Override
        public void setSelectionInterval(int anchor, int lead) {
            entitiesManager.fieldParamSelectionResetted(entity, (Field) getModel().getElementAt(lead), null);
            super.setSelectionInterval(anchor, lead);
            int oldAnchor = getAnchorSelectionIndex();
            int oldLead = getLeadSelectionIndex();
            if (oldAnchor != anchor || oldLead != lead) {
            }
        }
    };
    protected FieldsListModel<Field> fieldsModel = new FieldsListModel.FieldsModel();
    protected JList<Parameter> parametersList = new JList() {
        @Override
        public void setSelectionInterval(int anchor, int lead) {
            entitiesManager.fieldParamSelectionResetted(entity, null, (Parameter) getModel().getElementAt(lead));
            super.setSelectionInterval(anchor, lead);
            int oldAnchor = getAnchorSelectionIndex();
            int oldLead = getLeadSelectionIndex();
            if (oldAnchor != anchor || oldLead != lead) {
            }
        }
    };
    protected ParametersMetadataModel parametersModel = new ParametersMetadataModel();
    protected FieldsParametersListCellRenderer<E> fieldsParamsRenderer;
    protected FieldsParamsSelectionPropagator selelctionPropagator = new FieldsParamsSelectionPropagator();
    protected EntityChangesReflector entityChangesReflector = new EntityChangesReflector();
    protected FieldsChangeListener fieldsChangesReflector = new FieldsChangeListener();
    protected FieldsCollectionListener fieldsCollectionReflector = new FieldsCollectionListener();
    // Interaction
    protected UndoableEditSupport undoSupport = new UndoableEditSupport();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected Set<FieldSelectionListener<E>> fieldsSelectionListeners = new HashSet<>();
    protected EntityViewsManager<E> entitiesManager;

    public void addFieldSelectionListener(FieldSelectionListener<E> l) {
        fieldsSelectionListeners.add(l);
    }

    public void removeFieldSelectionListener(FieldSelectionListener<E> l) {
        fieldsSelectionListeners.remove(l);
    }

    public void fireFieldSelectionChanged(java.util.List<Parameter> aParameters, java.util.List<Field> aFields) {
        for (FieldSelectionListener<E> l : fieldsSelectionListeners) {
            l.selected((EntityView<E>) this, aParameters, aFields);
        }
    }

    public String getFieldDisplayLabel(Field aField) {
        if (aField != null) {
            int lidx = fieldsModel.getFieldNameIndex(aField.getName());
            if (lidx > -1) {
                Field lfield = fieldsModel.getElementAt(lidx);
                Component lcomp = fieldsParamsRenderer.getListCellRendererComponent(fieldsList, lfield, lidx, false, false);
                if (lcomp != null && lcomp instanceof IconsListCellRenderer) {
                    IconsListCellRenderer fRenderer = (IconsListCellRenderer) lcomp;
                    return fRenderer.getText();
                }
            }
        }
        return null;
    }

    public String getParameterDisplayLabel(Field aParameter) {
        if (aParameter != null) {
            int lidx = parametersModel.getFieldNameIndex(aParameter.getName());
            if (lidx > -1) {
                Field lfield = parametersModel.getElementAt(lidx);
                Component lcomp = fieldsParamsRenderer.getListCellRendererComponent(parametersList, lfield, lidx, false, false);
                if (lcomp != null && lcomp instanceof IconsListCellRenderer) {
                    IconsListCellRenderer fRenderer = (IconsListCellRenderer) lcomp;
                    return fRenderer.getText();
                }
            }
        }
        return null;
    }

    public Parameters getParameters() {
        try {
            if (entity != null && entity.getQuery() != null) {
                return entity.getQuery().getParameters();
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(EntityView.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Fields getFields() {
        return entity.getFields();
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public void setEntityViewSelectedLook() {
        setBorder(selectedBorder);
        titleLabel.setForeground(selectedColor.brighter().brighter());
        toolbarPanel.setBackground(selectedColor);
    }

    public void setEntityViewUnselectedLook() {
        setBorder(ordinaryBorder);
        titleLabel.setForeground(selectedColor.darker().darker());
        toolbarPanel.setBackground(unselectedColor);
    }

    public void clearListsSelection() {
        parametersList.clearSelection();
        fieldsList.clearSelection();
    }

    public int calcIconifiedHeight() {
        Insets borderInsets = getBorder().getBorderInsets(this);
        int icoHeight = toolbarPanel.getHeight() + borderInsets.bottom + borderInsets.top;
        return icoHeight;
    }

    private void initComponents() {
        paramsFieldsPanel = new JPanel(new BorderLayout());
        fieldsParamsRenderer = new FieldsParametersListCellRenderer<>(DatamodelDesignUtils.getFieldsFont(), DatamodelDesignUtils.getBindedFieldsFont(), entity);
        setDoubleBuffered(true);
        addMouseListener(viewMover);
        addMouseMotionListener(viewMover);

        titleLabel = new JLabel(getCheckedEntityTitle(), initViewIcon(entity), SwingConstants.LEFT);
        titleLabel.setFont(DatamodelDesignUtils.getFieldsFont().deriveFont(Font.BOLD));
        titleLabel.setForeground(selectedColor.darker().darker().darker());
        titleLabel.setOpaque(false);

        titleToolbar = new JToolBar();
        titleToolbar.setFloatable(false);
        titleToolbar.setOpaque(false);
        JLabel blank = new JLabel(" ");
        blank.setOpaque(false);
        titleToolbar.add(blank);
        titleToolbar.add(titleLabel);

        MousePropagator propagator = new MousePropagator();

        titleToolbar.addMouseListener(propagator);
        titleToolbar.addMouseMotionListener(propagator);
        titleLabel.addMouseListener(propagator);
        titleLabel.addMouseMotionListener(propagator);

        titleToolbar.setBorder(new MatteBorder(0, 0, 1, 0, borderColor));

        toolsToolbar = new JToolBar();
        toolsToolbar.setFloatable(false);
        toolsToolbar.addMouseListener(propagator);
        JButton minimizeButton = new JButton(minimizeRestoreAction);
        minimizeButton.setOpaque(false);
        toolsToolbar.add(minimizeButton);
        toolsToolbar.setBorder(new MatteBorder(0, 1, 1, 0, borderColor));
        toolsToolbar.setOpaque(false);
        toolbarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isOpaque() && g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    Color backColor = getBackground();
                    Color ltBackColor = CascadedStyle.brighterColor(backColor, 0.85);
                    Color dkBackColor = CascadedStyle.darkerColor(backColor, 0.95);
                    Dimension size = getSize();
                    Paint gradient1 = new GradientPaint(new Point2D.Float(0, 0), ltBackColor, new Point2D.Float(0, size.height / 2), backColor);
                    Paint gradient2 = new GradientPaint(new Point2D.Float(0, size.height / 2 + 1), dkBackColor, new Point2D.Float(0, size.height), backColor);
                    g2d.setPaint(gradient1);
                    g2d.fillRect(0, 0, size.width, size.height >>> 1);
                    g2d.setPaint(gradient2);
                    g2d.fillRect(0, size.height >>> 1, size.width, size.height >>> 1);
                }
            }
        };
        toolbarPanel.add(titleToolbar, BorderLayout.CENTER);
        toolbarPanel.add(toolsToolbar, BorderLayout.EAST);
        add(toolbarPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(paramsFieldsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(scroll, BorderLayout.CENTER);
        if (isParameterized()) {
            parametersModel.setFields(entity.getFields());
            parametersList.setModel(parametersModel);
            parametersList.setCellRenderer(fieldsParamsRenderer);
            parametersList.setBorder(new MatteBorder(0, 0, 1, 0, getForeground()));
            parametersList.addListSelectionListener(selelctionPropagator);
            parametersList.setAutoscrolls(true);
            parametersList.addMouseListener(new FieldsParamsDoubleClickPropagator());
            paramsFieldsPanel.add(parametersList, BorderLayout.NORTH);
        }
        fieldsModel.setFields(entity.getFields());
        fieldsList.setModel(fieldsModel);
        fieldsList.setCellRenderer(fieldsParamsRenderer);
        fieldsList.addListSelectionListener(selelctionPropagator);
        fieldsList.setAutoscrolls(true);
        fieldsList.setBorder(new EmptyBorder(0, 0, 0, 0));
        fieldsList.addMouseListener(new FieldsParamsDoubleClickPropagator());

        paramsFieldsPanel.add(fieldsList, BorderLayout.CENTER);
        paramsFieldsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        setBorder(ordinaryBorder);
        setOpaque(false);
    }

    protected abstract boolean isEditable();

    protected abstract boolean isParameterized();

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (titleToolbar != null) {
            titleToolbar.setBackground(bg);
        }
    }

    private String getCheckedEntityTitle() {
        String text;
        if (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) {
            text = DatamodelDesignUtils.getLocalizedString("Parameters");
        } else if (entity.getName() != null && !entity.getName().isEmpty() && entity.getTitle() != null && !entity.getTitle().isEmpty()) {
            text = String.format("%s [%s]", entity.getName(), entity.getTitle());
        } else if (entity.getName() != null && !entity.getName().isEmpty()) {
            text = entity.getName();
        } else if (entity.getTitle() != null && !entity.getTitle().isEmpty()) {
            text = entity.getTitle();
        } else {
            text = DatamodelDesignUtils.getLocalizedString("noName");
        }
        return text;
    }

    public static <E extends Entity<?, ?, E>> Icon initViewIcon(E entity) {
        if (!(entity instanceof ApplicationParametersEntity) && !(entity instanceof QueryParametersEntity)) {
            return (entity.getTableName() != null && !entity.getTableName().isEmpty()) ? IconCache.getIcon("table.png") : IconCache.getIcon("query.png");
        } else {
            return IconCache.getIcon("edit-list.png");
        }
    }

    /**
     * Resets selection in parameters list and selects specified parameter.
     *
     * @param aValue
     */
    public void setSelectedParameter(Parameter aValue) {
        parametersList.setSelectedValue(aValue, true);
    }

    public void addSelectedParameters(Collection<Parameter> aValues) {
        parametersList.getSelectionModel().setValueIsAdjusting(true);
        try {
            for (Parameter param : aValues) {
                int idx = parametersModel.getFieldNameIndex(param.getName());
                if (idx != -1) {
                    parametersList.addSelectionInterval(idx, idx);
                }
            }
        } finally {
            parametersList.getSelectionModel().setValueIsAdjusting(false);
        }
    }

    /**
     * Selects parameter in parameters list control.
     *
     * @param aValue
     */
    public void addSelectedParameter(Parameter aValue) {
        int idx = parametersModel.getFieldNameIndex(aValue.getName());
        if (idx != -1) {
            parametersList.addSelectionInterval(idx, idx);
        }
    }

    /**
     * Resets selection in fields list and selects specified field.
     *
     * @param aValue
     */
    public void setSelectedField(Field aValue) {
        fieldsList.setSelectedValue(aValue, true);
    }

    public void addSelectedFields(Collection<Field> aValues) {
        fieldsList.getSelectionModel().setValueIsAdjusting(true);
        try {
            for (Field field : aValues) {
                int idx = fieldsModel.getFieldNameIndex(field.getName());
                if (idx != -1) {
                    fieldsList.addSelectionInterval(idx, idx);
                }
            }
        } finally {
            fieldsList.getSelectionModel().setValueIsAdjusting(false);
        }
    }

    /**
     * Selects specified field in fields list.
     *
     * @param aValue
     */
    public void addSelectedField(Field aValue) {
        int idx = fieldsModel.getFieldNameIndex(aValue.getName());
        if (idx != -1) {
            fieldsList.addSelectionInterval(idx, idx);
        }
    }

    public Field getSelectedField() {
        return fieldsList.getSelectedValue();
    }

    public Parameter getSelectedParameter() {
        return parametersList.getSelectedValue();
    }

    public java.util.List<Field> getSelectedFields() {
        return fieldsList.getSelectedValuesList();
    }

    public java.util.List<Parameter> getSelectedParameters() {
        return parametersList.getSelectedValuesList();
    }

    public JList<Field> getFieldsList() {
        return fieldsList;
    }

    public JList<Parameter> getParametersList() {
        return parametersList;
    }

    public void reLayout() {
        synchronized (getTreeLock()) {
            validateTree();
        }
    }

    /**
     * Differs from entity.getInOutRelations(). It may return additional relations in descendants.
     * @return Set of relations
     */
    public Set<Relation<E>> getInOutRelations() {
        return getEntity().getInOutRelations();
    }

    protected class MinimizeRestoreAction extends AbstractAction {

        public MinimizeRestoreAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
            if (entity.isIconified()) {
                putValue(Action.SMALL_ICON, IconCache.getIcon("restore.png"));
            } else {
                putValue(Action.SMALL_ICON, IconCache.getIcon("minimize.png"));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undoSupport.beginUpdate();
            try {
                HideEntityFieldsEdit<E> edit = entity.isIconified() ? new ShowEntityFieldsEdit<>(entity) : new HideEntityFieldsEdit<>(entity);
                entity.setIconified(!entity.isIconified());
                undoSupport.postEdit(edit);
            } finally {
                undoSupport.endUpdate();
            }
        }
    }

    protected class FieldInformAction extends AbstractAction {

        public FieldInformAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (fieldsList.hasFocus() && fieldsList.getSelectedValue() != null && fieldsList.getSelectedValue() instanceof Field) {
                Field fmd = (Field) fieldsList.getSelectedValue();
                String fieldName = fmd.getName();

                assert (fieldName != null && !fieldName.isEmpty());

                String ldesc = fmd.getDescription();
                String lTypeName = SQLUtils.getLocalizedTypeName(fmd.getTypeInfo().getSqlType());
                if (fmd.isPk()) {
                    lTypeName = SQLUtils.getLocalizedPkName() + "." + lTypeName;
                }
                if (fmd.isFk()) {
                    lTypeName = SQLUtils.getLocalizedFkName() + "." + lTypeName;
                }
                String lText = (((ldesc != null && !ldesc.isEmpty()) ? fieldName + " (" + ldesc + ") " : fieldName) + " : " + lTypeName);
                JOptionPane.showInputDialog(EntityView.this, null, entity.getTitle(), JOptionPane.INFORMATION_MESSAGE, null, null, lText);
            } else if (!(EntityView.this.getEntity() instanceof ApplicationParametersEntity) && !(EntityView.this.getEntity() instanceof QueryParametersEntity)) {
                final E ent = EntityView.this.getEntity();
                final String info;
                String name = ent.getName();
                if (name == null) {
                    if (ent instanceof QueryEntity) {
                        name = ((QueryEntity) ent).getAlias();
                    }
                    if (name == null) {
                        name = entity.getTitle();
                        if (name == null) {
                            name = "<no name>";
                        }
                    }
                }
                if (ent.getQueryId() != null) {
                    info = String.format("%s %s", ent.getQueryId(), name);
                } else {
                    final String schema = ent.getTableSchemaName();
                    if (schema != null && !schema.isEmpty()) {
                        info = String.format("%s.%s %s", schema, ent.getTableName(), name);
                    } else {
                        info = String.format("%s %s", ent.getTableName(), name);
                    }
                }
                JOptionPane.showInputDialog(EntityView.this, null, entity.getTitle(), JOptionPane.INFORMATION_MESSAGE, null, null, info);
            }
        }
    }

    protected class EntityViewMoverResizer extends MouseAdapter {

        protected class MoveInfo {

            boolean inLeft;
            boolean inRight;
            boolean inTop;
            boolean inBottom;

            public boolean isMove() {
                return !inLeft && !inRight && !inTop && !inBottom;
            }

            public boolean isResize() {
                return !isMove();
            }
        }
        protected MoveEntityEdit<E> moveEdit = null;
        protected Point dMousePt = null;
        protected MoveInfo dMoveInfo = null;

        @Override
        public void mouseMoved(MouseEvent e) {
            int cursorNumber = calcCursor(e.getPoint());
            Cursor cursor = Cursor.getPredefinedCursor(cursorNumber);
            titleLabel.setCursor(cursor);
            titleToolbar.setCursor(cursor);
            setCursor(cursor);
        }

        private MoveInfo calcMoveInfo(Point pt) {
            MoveInfo res = new MoveInfo();
            Dimension size = getSize();
            Insets borderInsets = EntityView.this.getBorder().getBorderInsets(EntityView.this);
            borderInsets.top = borderInsets.bottom;
            res.inLeft = pt.x >= 0 && pt.x < borderInsets.left;
            res.inRight = pt.x < size.width && pt.x >= size.width - borderInsets.right;
            res.inTop = pt.y >= 0 && pt.y < borderInsets.top;
            res.inBottom = pt.y < size.height && pt.y >= size.height - borderInsets.bottom;
            return res;
        }

        private int calcCursor(Point pt) {
            MoveInfo mInfo = calcMoveInfo(pt);
            if (mInfo.inLeft && mInfo.inTop) {
                return Cursor.NW_RESIZE_CURSOR;
            } else if (mInfo.inLeft && mInfo.inBottom) {
                return Cursor.SW_RESIZE_CURSOR;
            } else if (mInfo.inRight && mInfo.inTop) {
                return Cursor.NE_RESIZE_CURSOR;
            } else if (mInfo.inRight && mInfo.inBottom) {
                return Cursor.SE_RESIZE_CURSOR;
            } else if (mInfo.inLeft) {
                return Cursor.W_RESIZE_CURSOR;
            } else if (mInfo.inRight) {
                return Cursor.E_RESIZE_CURSOR;
            } else if (mInfo.inTop) {
                return Cursor.N_RESIZE_CURSOR;
            } else if (mInfo.inBottom) {
                return Cursor.S_RESIZE_CURSOR;
            } else {
                return Cursor.HAND_CURSOR;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dMousePt = e.getPoint();
            dMoveInfo = calcMoveInfo(dMousePt);
            if (dMoveInfo.isMove()) {
                beginDraggingEntityView(EntityView.this);
            } else {
                Point loc = getLocation();
                dMousePt.x += loc.x;
                dMousePt.y += loc.y;
                beginResizingEntityView(EntityView.this);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dMoveInfo != null) {
                if (dMoveInfo.isMove()) {
                    endDraggingEntityView(EntityView.this);
                } else {
                    endResizingEntityView(EntityView.this);
                }
            }
            dMousePt = null;
            dMoveInfo = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            assert e.getSource() == EntityView.this;
            if (dMoveInfo != null) {
                Point mousePt = e.getPoint();
                Point loc = getLocation();
                if (dMoveInfo.isMove()) {
                    mousePt.x += loc.x - dMousePt.x;
                    mousePt.y += loc.y - dMousePt.y;
                    dragEntityView(EntityView.this, mousePt.x, mousePt.y);
                } else {
                    mousePt.x = dMousePt.x;
                    mousePt.y += loc.y;
                    Dimension size = getSize();
                    int dx = (dMoveInfo.inLeft || dMoveInfo.inRight) ? mousePt.x - dMousePt.x : 0;
                    int dy = (dMoveInfo.inTop || dMoveInfo.inBottom) ? mousePt.y - dMousePt.y : 0;
                    if (dMoveInfo.inLeft) {
                        loc.x += dx;
                        dx = -dx;
                    }
                    if (dMoveInfo.inTop) {
                        loc.y += dy;
                        dy = -dy;
                    }
                    if (resizeEntityView(EntityView.this, loc.x, loc.y, size.width + dx, size.height + dy)) {
                        dMousePt = mousePt;
                    }
                    loc = getLocation();
                    mousePt = e.getPoint();
                    mousePt.x += loc.x;
                    mousePt.y = dMousePt.y;
                    size = getSize();
                    dx = (dMoveInfo.inLeft || dMoveInfo.inRight) ? mousePt.x - dMousePt.x : 0;
                    dy = (dMoveInfo.inTop || dMoveInfo.inBottom) ? mousePt.y - dMousePt.y : 0;
                    if (dMoveInfo.inLeft) {
                        loc.x += dx;
                        dx = -dx;
                    }
                    if (dMoveInfo.inTop) {
                        loc.y += dy;
                        dy = -dy;
                    }
                    if (resizeEntityView(EntityView.this, loc.x, loc.y, size.width + dx, size.height + dy)) {
                        dMousePt = mousePt;
                    }
                }
            }
        }

        public void beginDraggingEntityView(EntityView<E> aView) {
            E entity = aView.getEntity();
            moveEdit = new MoveEntityEdit<>(entity, aView.getLocation(), aView.getLocation());
            for (Relation rel : aView.getInOutRelations()) {
                if (rel.isManual()) {
                    RelationPolylineEdit edit = new RelationPolylineEdit(rel, rel.getXs(), rel.getYs(), null, null);
                    edit.redo();
                    relationsPolylinesEdits.add(edit);
                }
            }
        }

        public void endDraggingEntityView(EntityView<E> aView) {
            if (moveEdit != null) {
                Point location = aView.getLocation();
                moveEdit.setAfterLocation(location);
                if (moveEdit.isSignificant()) {
                    E entity = moveEdit.getEntity();
                    assert entity != null;
                    entity.setX(location.x);
                    entity.setY(location.y);
                    undoSupport.beginUpdate();
                    try {
                        undoSupport.postEdit(moveEdit);
                        for (RelationPolylineEdit edit : relationsPolylinesEdits) {
                            undoSupport.postEdit(edit);
                        }
                    } finally {
                        undoSupport.endUpdate();
                    }
                }
                moveEdit = null;
                relationsPolylinesEdits.clear();
            }
        }

        public void dragEntityView(EntityView<E> aView, int newX, int newY) {
            aView.setLocation(newX, newY);
        }
        protected ResizeEntityEdit<E> resizeEdit = null;
        protected Set<RelationPolylineEdit> relationsPolylinesEdits = new HashSet<>();

        public void beginResizingEntityView(EntityView<E> aView) {
            E entity = aView.getEntity();
            resizeEdit = new ResizeEntityEdit<>(entity, aView.getBounds(), aView.getBounds());
            for (Relation rel : aView.getInOutRelations()) {
                if (rel.isManual()) {
                    RelationPolylineEdit edit = new RelationPolylineEdit(rel, rel.getXs(), rel.getYs(), null, null);
                    edit.redo();
                    relationsPolylinesEdits.add(edit);
                }
            }
        }

        public void endResizingEntityView(EntityView<E> aView) {
            if (resizeEdit != null) {
                Rectangle afterRect = aView.getBounds();
                resizeEdit.setAfterBounds(afterRect);
                if (resizeEdit.isSignificant()) {
                    E entity = resizeEdit.getEntity();
                    entity.setX(afterRect.x);
                    entity.setY(afterRect.y);
                    entity.setWidth(afterRect.width);
                    entity.setHeight(afterRect.height);
                    undoSupport.beginUpdate();
                    try {
                        undoSupport.postEdit(resizeEdit);
                        for (RelationPolylineEdit edit : relationsPolylinesEdits) {
                            undoSupport.postEdit(edit);
                        }
                    } finally {
                        undoSupport.endUpdate();
                    }
                }
                resizeEdit = null;
                relationsPolylinesEdits.clear();
            }
        }

        public boolean resizeEntityView(EntityView<E> aView, int newX, int newY, int newWidth, int newHeight) {
            if (newHeight >= calcIconifiedHeight() && newWidth >= 100) {
                Rectangle loldBounds = aView.getBounds();
                Rectangle lnewBounds = new Rectangle(newX, newY, newWidth, newHeight);
                if (!lnewBounds.equals(loldBounds)) {
                    aView.setBounds(lnewBounds);
                    aView.invalidate();
                    aView.validate();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    protected class EntityEditActionWrapper extends AbstractAction {

        protected Action delegate;

        public EntityEditActionWrapper(Action aDelegate) {
            delegate = aDelegate;
        }

        @Override
        public Object getValue(String key) {
            return delegate.getValue(key);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MouseEvent event = new MouseEvent(EntityView.this, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 3, 3, 1, false);
            for (MouseListener l : getMouseListeners()) {
                l.mousePressed(event);
            }
            delegate.actionPerformed(e);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class EntityChangesReflector implements PropertyChangeListener {

        public EntityChangesReflector() {
            super();
        }

        public int calcLightIconifiedChange(boolean aNewValue) {
            int newHeight;// = entity.getHeight();
            if (Boolean.TRUE.equals(aNewValue)) {
                newHeight = calcIconifiedHeight();
                minimizeRestoreAction.putValue(Action.SMALL_ICON, IconCache.getIcon("restore.png"));
            } else {
                newHeight = ENTITY_VIEW_DEFAULT_HEIGHT;
                minimizeRestoreAction.putValue(Action.SMALL_ICON, IconCache.getIcon("minimize.png"));
            }
            return newHeight;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case "iconified":
                    int newHeight = calcLightIconifiedChange((Boolean) evt.getNewValue());
                    if (evt.getSource() == entity) {
                        int dy = newHeight - entity.getHeight();
                        entitiesManager.collapseExpand(EntityView.this, dy);
                    }
                    entity.setHeight(newHeight);
                    break;
                case "name":
                case "title":
                    titleLabel.setText(getCheckedEntityTitle());
                    titleLabel.invalidate();
                    reLayout();
                    break;
                default:
                    switch (evt.getPropertyName()) {
                        case "x":
                            EntityView.this.setLocation((Integer) evt.getNewValue(), EntityView.this.getLocation().y);
                            break;
                        case "y":
                            EntityView.this.setLocation(EntityView.this.getLocation().x, (Integer) evt.getNewValue());
                            break;
                        case "width":
                            EntityView.this.setSize((Integer) evt.getNewValue(), EntityView.this.getHeight());
                            break;
                        case "height":
                            EntityView.this.setSize(EntityView.this.getWidth(), (Integer) evt.getNewValue());
                            break;
                    }
                    reLayout();
                    break;
            }
        }
    }

    protected class FieldsChangeListener implements PropertyChangeListener {

        public void addFields(Fields aFields) {
            if (aFields != null) {
                removeFields(aFields);
                for (Field field : aFields.toCollection()) {
                    field.getChangeSupport().addPropertyChangeListener(FieldsChangeListener.this);
                }
            }
        }

        public void removeFields(Fields aFields) {
            if (aFields != null) {
                for (Field field : aFields.toCollection()) {
                    field.getChangeSupport().removePropertyChangeListener(FieldsChangeListener.this);
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fieldsModel.getFields().invalidateFieldsHash();
            repaint();
        }
    }

    protected interface CancellableRunnable extends Runnable {

        public void cancel();
    }

    protected class FieldsCollectionListener implements CollectionListener<Fields, Field> {

        @Override
        public void added(Fields fields, Field field) {
            field.getChangeSupport().addPropertyChangeListener(fieldsChangesReflector);
            invalidateConnectors();
        }

        @Override
        public void added(Fields fields, Collection<Field> fieldsCollection) {
            for (Field field : fieldsCollection) {
                field.getChangeSupport().addPropertyChangeListener(fieldsChangesReflector);
            }
        }

        @Override
        public void removed(Fields fields, Field field) {
            field.getChangeSupport().removePropertyChangeListener(entityChangesReflector);
            invalidateConnectors();
        }

        @Override
        public void removed(Fields fields, Collection<Field> fieldsCollection) {
            for (Field field : fieldsCollection) {
                field.getChangeSupport().removePropertyChangeListener(fieldsChangesReflector);
            }
            invalidateConnectors();
        }

        @Override
        public void cleared(Fields fields) {
            fieldsChangesReflector.removeFields(fields);
            invalidateConnectors();
        }

        @Override
        public void reodered(Fields aValue) {
            invalidateConnectors();
        }
        protected CancellableRunnable connectorsTask;

        protected void invalidateConnectors() {
            if (connectorsTask != null) {
                connectorsTask.cancel();
            }
            connectorsTask = new CancellableRunnable() {
                protected boolean cancelled;

                @Override
                public void run() {
                    if (!cancelled) {
                        entitiesManager.invalidateConnectors();
                        connectorsTask = null;
                    }
                }

                @Override
                public void cancel() {
                    cancelled = true;
                }
            };
            SwingUtilities.invokeLater(connectorsTask);
        }
    }

    public void refreshContent() {
        if (fieldsModel != null) {
            fieldsModel.fireDataChanged();
        }
        if (parametersModel != null) {
            parametersModel.fireDataChanged();
        }
    }

    private void setupActionMap() {
        ActionMap am = getActionMap();
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (am != null && im != null) {
            FieldInformAction fia = new FieldInformAction();
            am.put(FieldInformAction.class.getSimpleName(), fia);
            im.put((KeyStroke) fia.getValue(Action.ACCELERATOR_KEY), FieldInformAction.class.getSimpleName());
            am.put(MinimizeRestoreAction.class.getSimpleName(), minimizeRestoreAction);
            im.put((KeyStroke) minimizeRestoreAction.getValue(Action.ACCELERATOR_KEY), MinimizeRestoreAction.class.getSimpleName());
        }
    }

    public class ParametersMetadataModel extends FieldsListModel.ParametersModel {

        public ParametersMetadataModel() {
            super();
        }

        @Override
        public int getSize() {
            try {
                if (entity != null && entity.getQuery() != null) {
                    return entity.getQuery().getParameters().getFieldsCount();
                } else {
                    return 0;
                }
            } catch (Exception ex) {
                Logger.getLogger(EntityView.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }

        @Override
        public Parameter getElementAt(int index) {
            if (index >= 0 && index < getSize()) {
                try {
                    return entity.getQuery().getParameters().get(index + 1);
                } catch (Exception ex) {
                    Logger.getLogger(EntityView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }

        @Override
        public int getFieldNameIndex(String aFieldName) {
            try {
                if (entity != null && entity.getQuery() != null) {
                    return entity.getQuery().getParameters().find(aFieldName) - 1;
                } else {
                    return 0;
                }
            } catch (Exception ex) {
                Logger.getLogger(EntityView.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
    }

    public EntityView(E aEntity, EntityViewsManager<E> aMovesManager) {
        super(new BorderLayout());
        entity = aEntity;
        minimizeRestoreAction = new MinimizeRestoreAction();
        entitiesManager = aMovesManager;
        initComponents();
        setupActionMap();
        if (entity != null) {
            entity.getChangeSupport().addPropertyChangeListener(entityChangesReflector);
            if (entity.getFields() != null) {
                entity.getFields().getCollectionSupport().addListener(fieldsCollectionReflector);
                fieldsChangesReflector.addFields(entity.getFields());
            }
        }
    }

    public void shrink() {
        if (entity != null) {
            entity.getChangeSupport().removePropertyChangeListener(entityChangesReflector);
            if (entity.getFields() != null) {
                entity.getFields().getCollectionSupport().removeListener(fieldsCollectionReflector);
                fieldsChangesReflector.removeFields(entity.getFields());
            }
        }
    }

    public void setIconified(boolean aValue) throws PropertyVetoException {
        entity.setIconified(aValue);
    }

    public String getTitle() {
        if (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) {
            return DatamodelDesignUtils.getLocalizedString("parametersTabTitle");
        } else {
            return entity.getTitle();
        }
    }

    public Icon getIcon() {
        return titleLabel.getIcon();
    }

    public Long getEntityID() {
        return entity.getEntityId();
    }

    public E getEntity() {
        return entity;
    }

    public void setTooltipRecursivly(JComponent aRoot, String aTooltip) {
        if (aRoot != paramsFieldsPanel) {
            for (int i = 0; i < aRoot.getComponentCount(); i++) {
                Component lcomp = aRoot.getComponent(i);
                if (lcomp != null && lcomp instanceof JComponent) {
                    JComponent jcomp = (JComponent) lcomp;
                    jcomp.setToolTipText(aTooltip);
                    setTooltipRecursivly(jcomp, aTooltip);
                }
            }
        }
    }

    public void setTitle(String aTitle) {
        entity.setTitle(aTitle);
        setTooltipRecursivly(this, aTitle);
    }

    protected Point getListItemPosition(String aFieldName, boolean isLeft, JList<? extends Field> aList) {
        if (/*
                 * entity.isIconified() ||
                 */aFieldName == null || aFieldName.isEmpty()) {
            Rectangle lfieldRect = getBounds();
            Point lResPt = lfieldRect.getLocation();
            int icoHeight = calcIconifiedHeight();
            lfieldRect.height = icoHeight;
            lResPt.y = lfieldRect.y + Math.round(lfieldRect.height / 2.0f);
            if (!isLeft) {
                lResPt.x = lfieldRect.x + lfieldRect.width;
            }
            return lResPt;
        } else {
            ListModel<? extends Field> lm = aList.getModel();
            if (lm instanceof FieldsListModel<?>) {
                FieldsListModel<Field> lmm = (FieldsListModel<Field>) lm;
                int lModelIndex = lmm.getFieldNameIndex(aFieldName);
                if (lModelIndex > -1) {
                    Rectangle lfieldRect = aList.getCellBounds(lModelIndex, lModelIndex);
                    if (lfieldRect != null) {
                        if (lfieldRect.height < 0 && lm.getSize() > 0) {
                            Rectangle lfieldRect1 = aList.getCellBounds(0, 0);
                            lfieldRect.height = lfieldRect1.height;
                            lfieldRect.y = lModelIndex * lfieldRect.height;
                        }
                        Point lResPt = lfieldRect.getLocation();
                        lResPt.y = lfieldRect.y + Math.round(lfieldRect.height / 2.0f);
                        lResPt.x = lfieldRect.x + Math.round(lfieldRect.width / 2.0f);
                        lResPt = SwingUtilities.convertPoint(aList, lResPt, this);
                        lResPt.x = 0;
                        if (!isLeft) {
                            lResPt.x = getWidth();
                        }
                        if (lResPt.y < 0) {
                            lResPt.y = 0;
                        }
                        if (lResPt.y > getHeight() - INSET_ZONE) {
                            lResPt.y = getHeight() - INSET_ZONE;
                        }
                        if (getParent() != null) {
                            lResPt = SwingUtilities.convertPoint(this, lResPt, getParent());
                        }
                        return lResPt;
                    }
                }
            }
        }
        Rectangle lfieldRect = getBounds();
        Point loc = lfieldRect.getLocation();
        if (!isLeft) {
            loc.x = lfieldRect.x + lfieldRect.width;
        }
        return loc;
    }

    public Point getFieldPosition(Field aField, boolean isLeft) {
        return getListItemPosition(aField != null ? aField.getName() : null, isLeft, fieldsList);
    }

    public Point getParameterPosition(Parameter aParameter, boolean isLeft) {
        return getListItemPosition(aParameter != null ? aParameter.getName() : null, isLeft, parametersList);
    }

    protected class MouseSelectionPropagator extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            for (MouseListener l : getMouseListeners()) {
                l.mousePressed(e);
            }
        }
    }

    protected class FieldsParamsDoubleClickPropagator extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
                EntityView.this.dispatchEvent(e);
            }
        }
    }

    protected class MousePropagator implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, EntityView.this);
            EntityView.this.dispatchEvent(e);
        }
    }

    protected class FieldsParamsSelectionPropagator implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                fireFieldSelectionChanged(parametersList.getSelectedValuesList(), fieldsList.getSelectedValuesList());
            }
        }
    }
}
