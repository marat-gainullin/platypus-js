/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbTablesView.java
 *
 * Created on 05.05.2009, 13:02:21
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.DbClient;
import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.IconCache;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author mg
 */
public class DbTablesView extends JPanel {

    protected FindAction findAction = new FindAction();
    protected FindAgainAction findAgainAction = new FindAgainAction();
    protected DbClient client;
    protected String dialogTitle;
    protected String searchSubject;
    protected String connectionId;
    protected AppElementSelectorCallback connectionSelector;

    protected void refreshButtons() {
        if (connectionId == null) {
            btnDefaultConnection.setSelected(true);
        } else {
            btnSelectConnection.setSelected(true);
        }
    }

    public void refreshControls(String aSchema) throws Exception {
        // refresh controls
        if (connectionId == null) {
            txtConnection.setText(DatamodelDesignUtils.getLocalizedString("defaultDatabase"));
        } else {
            ApplicationElement appElement = client.getAppCache().get(connectionId);
            txtConnection.setText(appElement.getName());
        }
        String schema = aSchema;
        if (schema == null || schema.isEmpty()) {
            schema = client.getConnectionSchema(connectionId);
        }
        int schemaIndx = locateSchema(schema);
        if (schemaIndx != -1) {
            comboSchema.setSelectedIndex(schemaIndx);
        }
        refreshButtons();
    }

    public int locateSchema(String aSchema) {
        ComboBoxModel<String> schemasModel = comboSchema.getModel();
        for (int i = 0; i < schemasModel.getSize(); i++) {
            String sSchema = schemasModel.getElementAt(i);
            if (sSchema.equalsIgnoreCase(aSchema)) {
                return i;
            }
        }
        return -1;
    }

    public class FindAction extends AbstractAction {

        public FindAction() {
            super();
            putValue(Action.NAME, TableNameSelector.messages.getString(getClass().getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
            putValue(Action.SMALL_ICON, IconCache.getIcon("find.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Object oSubject = JOptionPane.showInputDialog(btnFind.getParent(), TableNameSelector.messages.getString(getClass().getSimpleName()), dialogTitle != null ? dialogTitle : TableNameSelector.messages.getString("DbTableSelecting"), JOptionPane.QUESTION_MESSAGE, null, null, searchSubject);
                if (oSubject != null && oSubject instanceof String) {
                    searchSubject = (String) oSubject;
                    findAgainAction.actionPerformed(null);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return lstTables.getModel().getSize() > 0;
        }
    }

    public class FindAgainAction extends AbstractAction {

        public FindAgainAction() {
            super();
            putValue(Action.NAME, TableNameSelector.messages.getString(getClass().getSimpleName()));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled() && searchSubject != null && !searchSubject.isEmpty()) {
                findInList(searchSubject);
            }
        }

        public void findInList(String aPattern) {
            int selectedIdx = lstTables.getSelectedIndex();
            ListModel<String> lm = lstTables.getModel();
            ListCellRenderer<? super String> lr = lstTables.getCellRenderer();
            if (lm != null && lr != null) {
                for (int i = selectedIdx + 1; i < lm.getSize(); i++) {
                    Component rComp = lr.getListCellRendererComponent(lstTables, lm.getElementAt(i), i, false, false);
                    if (rComp != null && rComp instanceof JLabel) {
                        JLabel rLabel = (JLabel) rComp;
                        String currentText = rLabel.getText();
                        if (currentText != null
                                && currentText.toLowerCase().contains(aPattern.toLowerCase())) {
                            lstTables.setSelectedIndex(i);
                            lstTables.requestFocus();
                            Rectangle cellBounds = lstTables.getUI().getCellBounds(lstTables, i, i);
                            if (cellBounds != null) {
                                lstTables.scrollRectToVisible(cellBounds);
                            }
                            return;
                        }
                    }
                }
                JOptionPane.showMessageDialog(btnFind.getParent(), TableNameSelector.messages.getString("NotFound"), dialogTitle != null ? dialogTitle : TableNameSelector.messages.getString("DbTableSelecting"), JOptionPane.INFORMATION_MESSAGE);
            }
        }

        @Override
        public boolean isEnabled() {
            return searchSubject != null && !searchSubject.isEmpty();
        }
    }

    /**
     * Creates new form DbTablesView
     */
    public DbTablesView(DbClient aClient, String aDbId, String aSchema, String aDialogTitle, boolean allowSchemaChange, boolean allowConnectionChange, AppElementSelectorCallback aConnectionSelector) {
        super();
        connectionId = aDbId;
        connectionSelector = aConnectionSelector;
        initComponents();
        client = aClient;
        dialogTitle = aDialogTitle;
        ActionMap am = lstTables.getActionMap();
        if (am != null) {
            am.put(FindAction.class.getSimpleName(), findAction);
            am.put(FindAgainAction.class.getSimpleName(), findAgainAction);
        }
        InputMap im = lstTables.getInputMap();
        if (im != null) {
            im.put((KeyStroke) findAction.getValue(Action.ACCELERATOR_KEY), FindAction.class.getSimpleName());
            im.put((KeyStroke) findAgainAction.getValue(Action.ACCELERATOR_KEY), FindAgainAction.class.getSimpleName());
        }
        try {
            DbTablesListModel listModel = new DbTablesListModel(client, aDbId);
            lstTables.setModel(listModel);
            lstTables.setCellRenderer(new DbTablesListRenderer());
            btnFind.setEnabled(findAction.isEnabled());
            DbSchemasComboModel comboModel = new DbSchemasComboModel(client, aDbId, listModel);
            comboSchema.setModel(comboModel);

            comboSchema.setEnabled(allowSchemaChange);
            btnDefaultSchema.setEnabled(allowSchemaChange);

            btnDefaultConnection.setEnabled(allowConnectionChange);
            btnSelectConnection.setEnabled(allowConnectionChange);
            refreshControls(aSchema);
        } catch (Exception ex) {
            Logger.getLogger(DbTablesView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FindAction getFindAction() {
        return findAction;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnsGroupConnection = new javax.swing.ButtonGroup();
        comboSchema = new javax.swing.JComboBox<String>();
        scrollTablesList = new javax.swing.JScrollPane();
        lstTables = new javax.swing.JList<String>();
        tools = new javax.swing.JToolBar();
        btnFind = new javax.swing.JButton();
        btnDefaultSchema = new javax.swing.JButton();
        lblSchema = new javax.swing.JLabel();
        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        btnDefaultConnection = new javax.swing.JToggleButton();
        btnSelectConnection = new javax.swing.JToggleButton();

        lstTables.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        scrollTablesList.setViewportView(lstTables);

        tools.setFloatable(false);
        tools.setRollover(true);

        btnFind.setAction(getFindAction());
        tools.add(btnFind);

        btnDefaultSchema.setText(DbStructureUtils.getString("btnDefaultSchema.text")); // NOI18N
        btnDefaultSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultSchemaActionPerformed(evt);
            }
        });

        lblSchema.setText(DbStructureUtils.getString("lblSchema.text")); // NOI18N

        lblConnection.setText(DbStructureUtils.getString("lblConnection.text")); // NOI18N

        txtConnection.setEditable(false);

        btnsGroupConnection.add(btnDefaultConnection);
        btnDefaultConnection.setText(DbStructureUtils.getString("btnDefaultConnection.text")); // NOI18N
        btnDefaultConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultConnectionActionPerformed(evt);
            }
        });

        btnsGroupConnection.add(btnSelectConnection);
        btnSelectConnection.setText(DbStructureUtils.getString("btnSelectConnection.text")); // NOI18N
        btnSelectConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectConnectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tools, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblConnection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSchema))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboSchema, 0, 158, Short.MAX_VALUE)
                            .addComponent(txtConnection, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnDefaultConnection)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectConnection))
                            .addComponent(btnDefaultSchema))
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollTablesList, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                        .addGap(10, 10, 10)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultConnection)
                    .addComponent(lblConnection)
                    .addComponent(btnSelectConnection))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultSchema)
                    .addComponent(lblSchema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tools, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTablesList, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDefaultConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultConnectionActionPerformed
        if (connectionId != null) {
            try {
                connectionId = null;
                refreshControls(null);
            } catch (Exception ex) {
                Logger.getLogger(DbTablesView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnDefaultConnectionActionPerformed

    private void btnSelectConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectConnectionActionPerformed
        try {
            String oldValue = connectionId;
            connectionId = connectionSelector.select(connectionId);
            if ((connectionId == null && oldValue != null) || (connectionId != null && !connectionId.equals(oldValue))) {
                refreshControls(null);
            }
        } catch (Exception ex) {
            Logger.getLogger(DbTablesView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSelectConnectionActionPerformed

    private void btnDefaultSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultSchemaActionPerformed
        try {
            String schema = client.getConnectionSchema(connectionId);
            int schemaIdx = locateSchema(schema);
            if (schemaIdx != -1) {
                comboSchema.setSelectedIndex(schemaIdx);
            }
        } catch (Exception ex) {
            Logger.getLogger(DbTablesView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDefaultSchemaActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnDefaultConnection;
    private javax.swing.JButton btnDefaultSchema;
    private javax.swing.JButton btnFind;
    private javax.swing.JToggleButton btnSelectConnection;
    private javax.swing.ButtonGroup btnsGroupConnection;
    public javax.swing.JComboBox<String> comboSchema;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblSchema;
    public javax.swing.JList<String> lstTables;
    private javax.swing.JScrollPane scrollTablesList;
    private javax.swing.JToolBar tools;
    private javax.swing.JTextField txtConnection;
    // End of variables declaration//GEN-END:variables
}
