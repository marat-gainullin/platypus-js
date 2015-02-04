/*
 * DatamodelEditorView.java
 *
 * Created on 13 Август 2008 г., 9:17
 */
package com.eas.client.model.gui;

import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.AddQueryAction;
import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.model.gui.view.model.ApplicationModelView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.utils.scalableui.JScalableScrollPane;
import com.eas.client.utils.scalableui.ScaleListener;
import com.eas.gui.JDropDownButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.Customizer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 *
 * @author mg
 */
public class ApplicationModelEditorView extends JPanel implements Customizer {

    protected class ViewsEntitySelectionListener implements ModelSelectionListener<ApplicationDbEntity> {

        @Override
        public void selectionChanged(List<SelectedField<ApplicationDbEntity>> params, List<SelectedField<ApplicationDbEntity>> fields) {
        }

        @Override
        public void selectionChanged(Collection<Relation<ApplicationDbEntity>> oldSelected, Collection<Relation<ApplicationDbEntity>> newSelected) {
            modelView.checkActions();
        }

        @Override
        public void selectionChanged(Set<ApplicationDbEntity> oldSelected, Set<ApplicationDbEntity> newSelected) {
            try {
                modelView.checkActions();
            } catch (Exception ex) {
                Logger.getLogger(ApplicationModelEditorView.class.getName()).log(Level.SEVERE, null, ex);
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

    protected class ScalableValidator implements ContainerListener {

        @Override
        public void componentAdded(ContainerEvent e) {
            scalablePane.checkComponents();
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
        }
    }

    protected class ScaleChangesReflector implements ScaleListener {

        @Override
        public void scaleChanged(float oldScale, float newScale) {
            comboZoom.setModel(new DefaultComboBoxModel<>(zoomLevelsData));
            String newSelectedZoom = String.valueOf(Math.round(newScale * 100)) + "%";
            if (((DefaultComboBoxModel<String>) comboZoom.getModel()).getIndexOf(newSelectedZoom) == -1) {
                ((DefaultComboBoxModel<String>) comboZoom.getModel()).insertElementAt(newSelectedZoom, 0);
            }
            comboZoom.setSelectedItem(newSelectedZoom);
        }
    }
    private final JScalableScrollPane scalablePane = new JScalableScrollPane();
    //private final JScrollPane scalablePane = new JScrollPane();
    private ApplicationModelView modelView;
    private JDropDownButton btnAddQuery;
    private JComboBox<String> comboZoom;
//    protected ScriptHost scriptHost;
//    protected NamespaceHost namespaceHost;
    protected DbUndoableEditListener undoListener = new DbUndoableEditListener();
    protected ViewsEntitySelectionListener entitySelectionListener = new ViewsEntitySelectionListener();
    protected UndoManager undo;
    protected static final String[] zoomLevelsData = new String[]{"25%", "50%", "75%", "100%", "150%", "200%", "300%"};
    protected static final Dimension BTN_DIMENSION = new Dimension(28, 28);

    /** Creates new form ApplicationModelEditorView */
    public ApplicationModelEditorView(TablesSelectorCallback aTablesSelectorCallback, AppElementSelectorCallback aAppElementSelector) throws Exception {
        super();
        // create views
        modelView = new ApplicationModelView(aTablesSelectorCallback, aAppElementSelector);
        initComponents();

        // incorporate views
        modelView.addModelSelectionListener(entitySelectionListener);

        // configure views
        modelView.setAutoscrolls(true);
        modelView.addContainerListener(new ScalableValidator());

        btnAddQuery = new JDropDownButton(modelView.getActionMap().get(AddQueryAction.class.getSimpleName()));
        btnAddQuery.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddQuery.setHideActionText(true);
        btnAddQuery.setDropDownMenu(popupNew);
        btnAddQuery.setMinimumSize(BTN_DIMENSION);
        btnAddQuery.setMaximumSize(BTN_DIMENSION);
        btnAddQuery.setPreferredSize(BTN_DIMENSION);
        tlbDses.add(btnAddQuery, 0);

        setupZoomControls();

        scalablePane.setViewportView(modelView);
        scalablePane.addScaleListener(new ScaleChangesReflector());
        scalablePane.getScalablePanel().getDrawWall().setComponentPopupMenu(popupModel);

        mainPanel.add(scalablePane, BorderLayout.CENTER);
        registerUndoListeners();
    }

    private void setupZoomControls() {
        JLabel lblZoom = new JLabel();
        lblZoom.setText(getLocalizedString("Zoom"));
        JPanel pnlZoom = new JPanel(new BorderLayout());
        JPanel pnlZoom1 = new JPanel(new BorderLayout());
        pnlZoom1.add(lblZoom, BorderLayout.CENTER);
        comboZoom = new JComboBox<>(new DefaultComboBoxModel<>(zoomLevelsData));
        pnlZoom1.add(comboZoom, BorderLayout.EAST);
        pnlZoom.add(pnlZoom1, BorderLayout.EAST);
        comboZoom.setSelectedItem("100%");
        comboZoom.setAction(modelView.getActionMap().get(ModelView.Zoom.class.getSimpleName()));
        tlbDses.add(pnlZoom);

        tlbDses.setRollover(true);
    }

    public UndoManager getUndo() {
        return undo;
    }

    public void setUndo(UndoManager editor) {
        undo = editor;
    }

    protected void registerUndoListeners() {
        modelView.addUndoableEditListener(undoListener);
    }
/*
    public void rerouteConnectors() {
        modelView.rerouteConnectors();
    }
*/
    public void setModel(ApplicationDbModel aModel) throws Exception {
        if (modelView.getModel() != aModel) {
            modelView.setModel(aModel);
            scalablePane.checkComponents();
            if (undo != null) {
                undo.discardAllEdits();
            }
        }
    }

    public ApplicationDbModel getModel() {
        return modelView.getModel();
    }

    public JToolBar getToolbar() {
        return tlbDses;
    }

    protected void checkAddActionControls() {
        btnAddQuery.setAction(modelView.getActionMap().get(AddQueryAction.class.getSimpleName()));
    }

    public ApplicationModelView getModelView() {
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

        popupModel = new javax.swing.JPopupMenu();
        mnuRelationPropsNames = new javax.swing.JMenuItem();
        mnuGo2LeftEntity = new javax.swing.JMenuItem();
        mnuGo2RightEntity = new javax.swing.JMenuItem();
        mnuSepRels2Entities = new javax.swing.JSeparator();
        mnuDeleteEntity = new javax.swing.JMenuItem();
        mnuSepBuffer = new javax.swing.JSeparator();
        mnuCutEntity = new javax.swing.JMenuItem();
        mnuCopyEntity = new javax.swing.JMenuItem();
        mnuPasteEntity = new javax.swing.JMenuItem();
        popupNew = new javax.swing.JPopupMenu();
        mnuAddQuery = new javax.swing.JMenuItem();
        mnuAddTable = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        tlbDses = new javax.swing.JToolBar();
        btnDelete = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();

        mnuRelationPropsNames.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ApplicationModelView.PropertiesNamesAction.class.getSimpleName()));
        popupModel.add(mnuRelationPropsNames);

        mnuGo2LeftEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.GoLeft.class.getSimpleName()));
        popupModel.add(mnuGo2LeftEntity);

        mnuGo2RightEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.GoRight.class.getSimpleName()));
        popupModel.add(mnuGo2RightEntity);
        popupModel.add(mnuSepRels2Entities);

        mnuDeleteEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Delete.class.getSimpleName()));
        popupModel.add(mnuDeleteEntity);
        popupModel.add(mnuSepBuffer);

        mnuCutEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Cut.class.getSimpleName()));
        popupModel.add(mnuCutEntity);

        mnuCopyEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Copy.class.getSimpleName()));
        popupModel.add(mnuCopyEntity);

        mnuPasteEntity.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Paste.class.getSimpleName()));
        popupModel.add(mnuPasteEntity);

        mnuAddQuery.setAction(modelView.getActionMap().get(AddQueryAction.class.getSimpleName()));
        popupNew.add(mnuAddQuery);

        mnuAddTable.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.AddTable.class.getSimpleName()));
        popupNew.add(mnuAddTable);

        setPreferredSize(new java.awt.Dimension(540, 335));
        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.BorderLayout());

        tlbDses.setFloatable(false);
        tlbDses.setRollover(true);

        btnDelete.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Delete.class.getSimpleName()));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/client/model/gui/resources/delete.png"))); // NOI18N
        btnDelete.setFocusable(false);
        btnDelete.setHideActionText(true);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setMaximumSize(new java.awt.Dimension(28, 28));
        btnDelete.setMinimumSize(new java.awt.Dimension(28, 28));
        btnDelete.setPreferredSize(new java.awt.Dimension(28, 28));
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tlbDses.add(btnDelete);

        btnZoomIn.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.ZoomIn.class.getSimpleName()));
        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/client/model/gui/resources/zoom+.png"))); // NOI18N
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHideActionText(true);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setMaximumSize(new java.awt.Dimension(28, 28));
        btnZoomIn.setMinimumSize(new java.awt.Dimension(28, 28));
        btnZoomIn.setPreferredSize(new java.awt.Dimension(28, 28));
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tlbDses.add(btnZoomIn);

        btnZoomOut.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.ZoomOut.class.getSimpleName()));
        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/client/model/gui/resources/zoom-.png"))); // NOI18N
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHideActionText(true);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setMaximumSize(new java.awt.Dimension(28, 28));
        btnZoomOut.setMinimumSize(new java.awt.Dimension(28, 28));
        btnZoomOut.setPreferredSize(new java.awt.Dimension(28, 28));
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tlbDses.add(btnZoomOut);

        btnFind.setAction(modelView.getActionMap().get(com.eas.client.model.gui.view.model.ModelView.Find.class.getSimpleName()));
        btnFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/client/model/gui/resources/find.png"))); // NOI18N
        btnFind.setFocusable(false);
        btnFind.setHideActionText(true);
        btnFind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind.setMaximumSize(new java.awt.Dimension(28, 28));
        btnFind.setMinimumSize(new java.awt.Dimension(28, 28));
        btnFind.setPreferredSize(new java.awt.Dimension(28, 28));
        btnFind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tlbDses.add(btnFind);

        mainPanel.add(tlbDses, java.awt.BorderLayout.NORTH);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem mnuAddQuery;
    private javax.swing.JMenuItem mnuAddTable;
    private javax.swing.JMenuItem mnuCopyEntity;
    private javax.swing.JMenuItem mnuCutEntity;
    private javax.swing.JMenuItem mnuDeleteEntity;
    private javax.swing.JMenuItem mnuGo2LeftEntity;
    private javax.swing.JMenuItem mnuGo2RightEntity;
    private javax.swing.JMenuItem mnuPasteEntity;
    private javax.swing.JMenuItem mnuRelationPropsNames;
    private javax.swing.JSeparator mnuSepBuffer;
    private javax.swing.JSeparator mnuSepRels2Entities;
    private javax.swing.JPopupMenu popupModel;
    private javax.swing.JPopupMenu popupNew;
    private javax.swing.JToolBar tlbDses;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setObject(Object object) {
        if (object == null) {
            try {
                setModel(null);
                undo = null;
                /*
                 if (namespaceHost != null) {
                 namespaceHost.removeElement(this);
                 }
                 setNamespaceHost(null);
                 *
                 */
            } catch (Exception ex) {
                Logger.getLogger(ApplicationModelEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                if (object instanceof UndoManager) {
                    setUndo((UndoManager) object);
                }
                /*
                 if (object instanceof ScriptHost) {
                 setScriptHost((ScriptHost) object);
                 }
                 if (object instanceof NamespaceHost) {
                 setNamespaceHost((NamespaceHost) object);
                 }
                 */
                if (object instanceof ApplicationDbModel) {
                    setModel((ApplicationDbModel) object);
                }
                /*
                 if (object instanceof FormsDatamodel) {
                 setModel(((FormsDatamodel) object).getDatamodel());
                 }
                 */
            } catch (Exception ex) {
                Logger.getLogger(ApplicationModelEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getLocalizedString(String aKey) {
        return DatamodelDesignUtils.getLocalizedString(aKey);
    }
    
}
