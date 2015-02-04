/*
 * DbTablesView.java
 *
 * Created on 05.05.2009, 13:02:21
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.DatabasesClient;
import com.eas.client.model.gui.IconCache;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.utils.DatabaseConnectionRenderer;
import com.eas.designer.application.utils.DatabaseConnections;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class DbTablesView extends JPanel {

    protected FindAction findAction = new FindAction();
    protected FindAgainAction findAgainAction = new FindAgainAction();
    protected DatabasesClient client;
    protected String dialogTitle;
    protected String searchSubject;
    protected String datasourceName;
    protected PlatypusProject project;

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
     * Creates DbTablesView
     *
     * @param aProject
     * @param aDatasourceName
     * @param aSchema
     * @param aDialogTitle
     * @param allowSchemaChange
     * @param allowConnectionChange
     */
    public DbTablesView(PlatypusProject aProject, String aDatasourceName, String aSchema, String aDialogTitle, boolean allowSchemaChange, boolean allowConnectionChange) {
        super();
        project = aProject;
        datasourceName = aDatasourceName;
        if (datasourceName == null) {
            datasourceName = project.getSettings().getDefaultDataSourceName();
        }
        initComponents();
        client = project.getBasesProxy();
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
            txtConnection.setEnabled(allowConnectionChange);
            DatabaseConnection defaultConn = DatabaseConnections.lookup(project.getSettings().getDefaultDataSourceName());
            DatabaseConnection currentConn = DatabaseConnections.lookup(datasourceName);
            List<DatabaseConnection> conns = new ArrayList<>();
            for (DatabaseConnection conn : ConnectionManager.getDefault().getConnections()) {
                if (conn.getJDBCConnection() != null) {
                    conns.add(conn);
                }
            }
            DefaultComboBoxModel<DatabaseConnection> connectionsModel = new DefaultComboBoxModel<>(conns.toArray(new DatabaseConnection[]{}));
            if (conns.contains(currentConn)) {
                connectionsModel.setSelectedItem(currentConn);
            } else {
                DatabaseConnection selectedConn = (DatabaseConnection) connectionsModel.getSelectedItem();
                if (selectedConn != null) {
                    datasourceName = selectedConn.getDisplayName();
                }
            }
            txtConnection.setModel(connectionsModel);
            txtConnection.setRenderer(new DatabaseConnectionRenderer(null));
            DbTablesListModel listModel = new DbTablesListModel(client, datasourceName);
            lstTables.setModel(listModel);
            lstTables.setCellRenderer(new DbTablesListRenderer());
            btnFind.setEnabled(findAction.isEnabled());
            DbSchemasComboModel comboModel = new DbSchemasComboModel(client, datasourceName, listModel);
            comboSchema.setModel(comboModel);
            if (aSchema == null || aSchema.isEmpty()) {
                aSchema = client.getConnectionSchema(datasourceName);
            }
            if (aSchema != null) {
                comboSchema.setSelectedIndex(locateSchema(aSchema));
            }
            comboSchema.setEnabled(allowSchemaChange);
            btnDefaultSchema.setEnabled(allowSchemaChange);

            btnDefaultConnection.setEnabled(allowConnectionChange && conns.contains(defaultConn));
        } catch (Exception ex) {
            Logger.getLogger(DbTablesView.class.getName()).log(Level.WARNING, null, ex);
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

        comboSchema = new javax.swing.JComboBox<String>();
        scrollTablesList = new javax.swing.JScrollPane();
        lstTables = new javax.swing.JList<String>();
        tools = new javax.swing.JToolBar();
        btnFind = new javax.swing.JButton();
        btnDefaultSchema = new javax.swing.JButton();
        lblSchema = new javax.swing.JLabel();
        lblConnection = new javax.swing.JLabel();
        btnDefaultConnection = new javax.swing.JButton();
        txtConnection = new javax.swing.JComboBox();

        comboSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSchemaActionPerformed(evt);
            }
        });

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

        btnDefaultSchema.setText(NbBundle.getMessage(DbTablesView.class, "btnDefaultSchema.text")); // NOI18N
        btnDefaultSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultSchemaActionPerformed(evt);
            }
        });

        lblSchema.setText(NbBundle.getMessage(DbTablesView.class, "lblSchema.text")); // NOI18N

        lblConnection.setText(NbBundle.getMessage(DbTablesView.class, "lblConnection.text")); // NOI18N

        btnDefaultConnection.setText(NbBundle.getMessage(DbTablesView.class, "btnDefaultConnection.text")); // NOI18N
        btnDefaultConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultConnectionActionPerformed(evt);
            }
        });

        txtConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConnectionActionPerformed(evt);
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
                            .addComponent(comboSchema, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtConnection, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnDefaultSchema)
                            .addComponent(btnDefaultConnection)))
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
                    .addComponent(lblConnection)
                    .addComponent(btnDefaultConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnDefaultSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultSchemaActionPerformed
        try {
            String schema = client.getConnectionSchema(datasourceName);
            comboSchema.setSelectedIndex(locateSchema(schema));
            comboSchema.invalidate();
            comboSchema.repaint();
        } catch (Exception ex) {
            Logger.getLogger(DbTablesView.class.getName()).log(Level.WARNING, null, ex);
        }
    }//GEN-LAST:event_btnDefaultSchemaActionPerformed

    private void btnDefaultConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultConnectionActionPerformed
        if (datasourceName != null) {
            try {
                datasourceName = project.getSettings().getDefaultDataSourceName();
                DatabaseConnection defaultConn = DatabaseConnections.lookup(datasourceName);
                txtConnection.setSelectedItem(defaultConn);
                lstTables.setModel(new DbTablesListModel(client, datasourceName));
                DbSchemasComboModel comboModel = new DbSchemasComboModel(client, datasourceName, (DbTablesListModel) lstTables.getModel());
                comboSchema.setModel(comboModel);
                btnDefaultSchemaActionPerformed(null);
            } catch (Exception ex) {
                Logger.getLogger(DbTablesView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_btnDefaultConnectionActionPerformed

    private void txtConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConnectionActionPerformed
        try {
            datasourceName = txtConnection.getSelectedItem() != null ? ((DatabaseConnection) txtConnection.getSelectedItem()).getDisplayName() : null;
            lstTables.setModel(new DbTablesListModel(client, datasourceName));
            DbSchemasComboModel comboModel = new DbSchemasComboModel(client, datasourceName, (DbTablesListModel) lstTables.getModel());
            comboSchema.setModel(comboModel);
            btnDefaultSchemaActionPerformed(null);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_txtConnectionActionPerformed

    private void comboSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSchemaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboSchemaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDefaultConnection;
    private javax.swing.JButton btnDefaultSchema;
    private javax.swing.JButton btnFind;
    public javax.swing.JComboBox<String> comboSchema;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblSchema;
    public javax.swing.JList<String> lstTables;
    private javax.swing.JScrollPane scrollTablesList;
    private javax.swing.JToolBar tools;
    public javax.swing.JComboBox txtConnection;
    // End of variables declaration//GEN-END:variables
}
