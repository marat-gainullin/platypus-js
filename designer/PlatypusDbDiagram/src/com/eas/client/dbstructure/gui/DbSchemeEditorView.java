/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbStructureEditorView.java
 *
 * Created on 09.08.2009, 21:39:25
 */
package com.eas.client.dbstructure.gui;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.model.ModelEditingValidator;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.dbstructure.gui.view.DbSchemeModelView;
import com.eas.client.metadata.Field;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.utils.scalableui.JScalableScrollPane;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.utils.scalableui.ScaleListener;
import com.eas.designer.application.query.result.QueryResultTopComponent;
import com.eas.designer.application.query.result.QueryResultsView;
import com.eas.gui.JDropDownButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Марат
 */
public class DbSchemeEditorView extends JPanel implements ContainerListener {

    private static final String QUERY_RESULT_TOPCOMPONENT_PREFFERED_ID = "QueryResultTopComponent";

    private void checkFkDragHandlers() {
        assert modelView != null;
        Collection<EntityView<FieldsEntity>> eViews = modelView.getViews();
        if (eViews != null) {
            eViews.stream().forEach((eView) -> {
                checkFkDragHandler(eView);
            });
        }
    }

    protected class DbSchemeEditingValidator implements ModelEditingValidator<FieldsEntity> {

        public DbSchemeEditingValidator() {
            super();
        }

        @Override
        public boolean validateRelationAdding(Relation<FieldsEntity> arg0) {
            try {
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean validateRelationRemoving(Relation<FieldsEntity> arg0) {
            try {
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean validateEntityAdding(FieldsEntity entity) {
            try {
                boolean alreadyPresent = schemeModel.getEntityByTableName(entity.getTableName()) != null;
                if (alreadyPresent) {
                    JOptionPane.showMessageDialog(DbSchemeEditorView.this, NbBundle.getMessage(DbStructureUtils.class, "EAS_TABLE_ALREADY_PRESENT", entity.getTableName(), null), getLocalizedString("datamodelTab"), JOptionPane.ERROR_MESSAGE);
                }
                return !alreadyPresent;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean validateEntityRemoving(FieldsEntity entity) {
            try {
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private void checkFkDragHandler(EntityView<FieldsEntity> eVeiw) {
        if (eVeiw != null) {
            JList<Field> fieldsList = eVeiw.getFieldsList();
            if (fieldsList.getTransferHandler() != null && !(fieldsList.getTransferHandler() instanceof FksFieldsDragHandler)) {
                fieldsList.setTransferHandler(new FksFieldsDragHandler(sqlController, modelView, eVeiw));
            }
        }
    }

    public class RunQueryAction extends AbstractAction {

        public RunQueryAction() {
            super();
            setEnabled(false);
            putValue(Action.NAME, NbBundle.getMessage(DbStructureUtils.class, RunQueryAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(DbStructureUtils.class, RunQueryAction.class.getSimpleName() + ".hint"));
            putValue(Action.SMALL_ICON, com.eas.client.model.gui.IconCache.getIcon("runsql.png")); //NOI18N
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                FieldsEntity ent = modelView.getSelectedEntities().iterator().next();
                if (ent != null) {
                    try {
                        QueryResultsView resultsView = new QueryResultsView(ent.getModel().getBasesProxy(), ent.getTableDatasourceName(), ent.getTableSchemaName(), ent.getTableName());
                        QueryResultTopComponent window = (QueryResultTopComponent) WindowManager.getDefault().findTopComponent(QUERY_RESULT_TOPCOMPONENT_PREFFERED_ID);
                        window.openAtTabPosition(0);
                        window.addResultsView(resultsView);
                        window.requestActive();
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return modelView.isAnySelectedEntities() && modelView.getSelectedEntities().size() == 1;
        }
    }

    public class UndoAction extends AbstractAction {

        public UndoAction() {
            putValue(Action.NAME, NbBundle.getMessage(DbStructureUtils.class, UndoAction.class.getSimpleName()));
            //putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(UndoAction.class.getSimpleName() + ".hint"));
            putValue(Action.SMALL_ICON, com.eas.client.model.gui.IconCache.getIcon("undo.png"));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return undo != null && undo.canRedo();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                undo.undo();
            }
        }
    }

    public class RedoAction extends AbstractAction {

        public RedoAction() {
            putValue(Action.NAME, NbBundle.getMessage(DbStructureUtils.class, RedoAction.class.getSimpleName()));
            //putValue(Action.SHORT_DESCRIPTION, DbStructureUtils.getString(RedoAction.class.getSimpleName() + ".hint"));
            putValue(Action.SMALL_ICON, com.eas.client.model.gui.IconCache.getIcon("redo.png"));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return undo != null && undo.canRedo();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                undo.redo();
            }
        }
    }

    protected class DbUndoableEditListener implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            if (undo != null) {
                undo.addEdit(e.getEdit());
            }
        }
    }

    protected class FieldsEntitiesViewsSelectionListener implements ModelSelectionListener<FieldsEntity> {

        @Override
        public void selectionChanged(List<SelectedField<FieldsEntity>> parameters, List<SelectedField<FieldsEntity>> fields) {
            checkActions();
        }

        @Override
        public void selectionChanged(Collection<Relation<FieldsEntity>> rltns, Collection<Relation<FieldsEntity>> rltns1) {
            checkActions();
        }

        @Override
        public void selectionChanged(Set<FieldsEntity> oldSelected, Set<FieldsEntity> newSelected) {
            checkActions();
        }
    }
    // data structure
    protected DbSchemeModel schemeModel;
    // editing
    protected UndoManager undo = new UndoManager();
    protected DbUndoableEditListener undoListener = new DbUndoableEditListener();
    // views
    protected JComboBox<String> comboZoom = new JComboBox<>();
    protected DbSchemeModelView modelView;
    //protected TableFieldsView fView = null;
    protected FieldsEntitiesViewsSelectionListener entitySelectionListener = new FieldsEntitiesViewsSelectionListener();
    // misc
    protected JScalableScrollPane dbSchemeScroll;
//    protected JScrollPane dbSchemeScroll = null;
    protected SqlActionsController sqlController;
    protected static final String[] zoomLevelsData = new String[]{"25%", "50%", "75%", "100%", "150%", "200%", "300%"};
    protected static final Dimension BTN_DIMENSION = new Dimension(28, 28);

    /**
     * Creates new form DbStructureEditorView
     */
    public DbSchemeEditorView(DbSchemeModel aModel, TablesSelectorCallback aSelectorCallback) throws Exception {
        super();
        schemeModel = aModel;
        sqlController = new SqlActionsController(schemeModel);

        // fill actions
        fillActions();
        // create views
        modelView = new DbSchemeModelView(schemeModel, sqlController, aSelectorCallback);
        initComponents();

        // configure views
        modelView.setAutoscrolls(true);
        modelView.addModelSelectionListener(entitySelectionListener);
        setupToolbars();

        DbSchemeModelView.RelationPropertiesAction relPropsAction = (DbSchemeModelView.RelationPropertiesAction) modelView.getActionMap().get(DbSchemeModelView.RelationPropertiesAction.class.getSimpleName());
        mnuRelationProps.setAction(relPropsAction);

        dbSchemeScroll = new JScalableScrollPane();
        //dbSchemeScroll = new JScrollPane();
        dbSchemeScroll.setViewportView(modelView);
        dbSchemeScroll.getScalablePanel().getDrawWall().setComponentPopupMenu(popupSchema);

        dbSchemeScroll.addScaleListener(new ScaleListener() {
            @Override
            public void scaleChanged(float oldScale, float newScale) {
                comboZoom.setModel(new DefaultComboBoxModel<>(zoomLevelsData));
                String newSelectedZoom = String.valueOf(Math.round(newScale * 100)) + "%";
                if (((DefaultComboBoxModel<String>) comboZoom.getModel()).getIndexOf(newSelectedZoom) == -1) {
                    ((DefaultComboBoxModel<String>) comboZoom.getModel()).insertElementAt(newSelectedZoom, 0);
                }
                comboZoom.setSelectedItem(newSelectedZoom);
            }
        });
        pnlScheme.add(dbSchemeScroll, BorderLayout.CENTER);
        modelView.addContainerListener(this);
        modelView.setModel(schemeModel);
        // install validator
        schemeModel.addEditingValidator(new DbSchemeEditingValidator());
        checkFkDragHandlers();
        registerUndoListeners();
        fillProxyActions();
    }

    public DbSchemeEditorView(DbSchemeModel aModel, TablesSelectorCallback aSelectorCallback, UndoManager aUndo) throws Exception {
        this(aModel, aSelectorCallback);
        setUndo(aUndo);
    }

    protected void registerUndoListeners() {
        modelView.addUndoableEditListener(undoListener);
    }

    // Подлежит удалению при переходе на NB
    protected class DocumentCommonAction extends AbstractAction implements PropertyChangeListener {

        protected String docActionName = null;

        public DocumentCommonAction(String aDocActionName) {
            super();
            docActionName = aDocActionName;
            ActionMap am = getEntitiesView().getActionMap();
            Action entitiesAction = am.get(docActionName);
            if (entitiesAction != null) {
                entitiesAction.addPropertyChangeListener(DocumentCommonAction.this);
            }
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ActionMap am = getEntitiesView().getActionMap();
            Action entitiesAction = am.get(docActionName);
            if (entitiesAction != null && entitiesAction.isEnabled()) {
                entitiesAction.actionPerformed(e);

            }
        }

        @Override
        public boolean isEnabled() {
            ActionMap am = getEntitiesView().getActionMap();
            Action entitiesAction = am.get(docActionName);
            return entitiesAction != null && entitiesAction.isEnabled();

        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ActionMap am = getEntitiesView().getActionMap();
            if ("enabled".equals(evt.getPropertyName())) {
                setEnabled((Boolean) evt.getNewValue());
            } else {
                putValue(evt.getPropertyName(), evt.getNewValue());
            }

        }

        @Override
        public Object getValue(String aKey) {
            ActionMap am = getEntitiesView().getActionMap();
            Action entitiesAction = am.get(docActionName);
            return entitiesAction.getValue(aKey);
        }
    }

    protected void fillActions() {
        ActionMap am = getActionMap();
        am.put(UndoAction.class.getSimpleName(), new UndoAction());
        am.put(RedoAction.class.getSimpleName(), new RedoAction());
        am.put(RunQueryAction.class.getSimpleName(), new RunQueryAction());
    }

    /**
     * Proxy actions - actions that exist onthe leve of document, but thier's
     * work is performed on inner levels by other inner actions.
     */
    protected void fillProxyActions() {
        ActionMap am = getActionMap();
        am.put("Cut", new DocumentCommonAction("Cut"));
        am.put("Copy", new DocumentCommonAction("Copy"));
        am.put("Paste", new DocumentCommonAction("Paste"));
    }

    public void checkActions() {
        DatamodelDesignUtils.checkActions(this);
        modelView.checkActions();
    }

    public static JButton createToolbarButton() {
        JButton aBtn = new JButton();
        aBtn.setFocusable(false);
        aBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aBtn.setMinimumSize(BTN_DIMENSION);
        aBtn.setMaximumSize(BTN_DIMENSION);
        aBtn.setPreferredSize(BTN_DIMENSION);
        aBtn.setHideActionText(true);
        return aBtn;
    }

    private JMenuItem createMenuItem() {
        JMenuItem aBtn = new JMenuItem();
        return aBtn;
    }

    protected void setupToolbars() {
        JToolBar toolsDbEntities = new JToolBar();

        JDropDownButton addBtn = new JDropDownButton();
        addBtn.setFocusable(false);
        addBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addBtn.setMinimumSize(BTN_DIMENSION);
        addBtn.setMaximumSize(BTN_DIMENSION);
        addBtn.setPreferredSize(BTN_DIMENSION);
        addBtn.setHideActionText(true);

        JPopupMenu addMenu = new JPopupMenu();
        JMenuItem mnuAddTable = createMenuItem();
        JMenuItem mnuCreateTable = createMenuItem();
        JButton btnRunQuery = createToolbarButton();
        JButton btnZoomIn = createToolbarButton();
        JButton btnZoomOut = createToolbarButton();
        JButton btnFind = createToolbarButton();

        JLabel lblZoom = new JLabel();
        lblZoom.setText(NbBundle.getMessage(DbStructureUtils.class, "lblZoom"));
        JPanel pnlZoom = new JPanel(new BorderLayout());
        JPanel pnlZoom1 = new JPanel(new BorderLayout());
        pnlZoom1.add(lblZoom, BorderLayout.CENTER);
        pnlZoom1.add(comboZoom, BorderLayout.EAST);
        pnlZoom.add(pnlZoom1, BorderLayout.EAST);
        comboZoom.setModel(new DefaultComboBoxModel<>(zoomLevelsData));
        comboZoom.setSelectedItem("100%");

        mnuCreateTable.setAction(modelView.getActionMap().get(DbSchemeModelView.CreateTableAction.class.getSimpleName()));
        mnuAddTable.setAction(modelView.getActionMap().get(ModelView.AddTable.class.getSimpleName()));
        btnRunQuery.setAction(getActionMap().get(RunQueryAction.class.getSimpleName()));
        comboZoom.setAction(modelView.getActionMap().get(ModelView.Zoom.class.getSimpleName()));
        btnZoomIn.setAction(modelView.getActionMap().get(ModelView.ZoomIn.class.getSimpleName()));
        btnZoomOut.setAction(modelView.getActionMap().get(ModelView.ZoomOut.class.getSimpleName()));
        btnFind.setAction(modelView.getActionMap().get(ModelView.Find.class.getSimpleName()));

        addMenu.add(mnuAddTable);
        addMenu.add(mnuCreateTable);

        addBtn.setAction(modelView.getActionMap().get(ModelView.AddTable.class.getSimpleName()));
        addBtn.setDropDownMenu(addMenu);

        toolsDbEntities.add(addBtn);

        JButton btnAddField2 = createToolbarButton();
        btnAddField2.setAction(modelView.getActionMap().get(ModelView.AddField.class.getSimpleName()));
        toolsDbEntities.add(btnAddField2);

        JButton btnDelete = createToolbarButton();
        btnDelete.setAction(modelView.getActionMap().get(ModelView.Delete.class.getSimpleName()));
        toolsDbEntities.add(btnDelete);

        toolsDbEntities.add(btnRunQuery);
        toolsDbEntities.add(btnZoomIn);
        toolsDbEntities.add(btnZoomOut);
        toolsDbEntities.add(btnFind);
        toolsDbEntities.add(pnlZoom);

        toolsDbEntities.setRollover(true);
        toolsDbEntities.setFloatable(false);

        pnlScheme.add(toolsDbEntities, BorderLayout.NORTH);
        // end of entities section
    }

    public UndoManager getUndo() {
        return undo;
    }

    public void setUndo(UndoManager aUndo) {
        undo = aUndo;
    }

    @Override
    public void updateUI() {
        super.updateUI();
    }

    public SqlActionsController getSqlController() {
        return sqlController;
    }

    public DbSchemeModelView getDmView() {
        return modelView;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupSchema = new javax.swing.JPopupMenu();
        mnuRelationProps = new javax.swing.JMenuItem();
        sepEditing = new javax.swing.JSeparator();
        mnu2Left = new javax.swing.JMenuItem();
        mnu2Right = new javax.swing.JMenuItem();
        sepNav = new javax.swing.JSeparator();
        mnuDelete = new javax.swing.JMenuItem();
        pnlScheme = new javax.swing.JPanel();

        popupSchema.add(mnuRelationProps);
        popupSchema.add(sepEditing);

        mnu2Left.setAction(modelView.getActionMap().get(ModelView.GoLeft.class.getSimpleName()));
        popupSchema.add(mnu2Left);

        mnu2Right.setAction(modelView.getActionMap().get(ModelView.GoRight.class.getSimpleName()));
        popupSchema.add(mnu2Right);
        popupSchema.add(sepNav);

        mnuDelete.setAction(modelView.getActionMap().get(ModelView.Delete.class.getSimpleName()));
        popupSchema.add(mnuDelete);

        setLayout(new java.awt.BorderLayout());

        pnlScheme.setLayout(new java.awt.BorderLayout());
        add(pnlScheme, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem mnu2Left;
    private javax.swing.JMenuItem mnu2Right;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenuItem mnuRelationProps;
    private javax.swing.JPanel pnlScheme;
    private javax.swing.JPopupMenu popupSchema;
    private javax.swing.JSeparator sepEditing;
    private javax.swing.JSeparator sepNav;
    // End of variables declaration//GEN-END:variables

    public String getLocalizedString(String aKey) {
        try {
            return NbBundle.getMessage(DbStructureUtils.class, aKey);
        } catch (Exception ex) {
            return aKey;
        }
    }

    public ModelView<FieldsEntity, DbSchemeModel> getEntitiesView() {
        return modelView;
    }

    public DbSchemeModel getModel() {
        return modelView.getModel();
    }

    public void setModel(DbSchemeModel aModel) throws Exception {
        modelView.setModel(aModel);
        if (aModel != null) {
            sqlController.setBasesProxy(aModel.getBasesProxy());
            sqlController.setDatasourceName(aModel.getDatasourceName());
            sqlController.setSchema(aModel.getSchema());
        }
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        dbSchemeScroll.checkComponents();
        Component comp = e.getChild();
        if (comp != null && comp instanceof EntityView<?>) {
            EntityView<FieldsEntity> ef = (EntityView<FieldsEntity>) comp;
            checkFkDragHandler(ef);
        }
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
    }
}
