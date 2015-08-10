/*
 * DbSchemeSettingsVisualPanel.java
 *
 * Created on 25.03.2011, 11:54:03
 */
package com.eas.designer.application.dbdiagram.templates;

import com.eas.client.ClientConstants;
import com.eas.designer.application.utils.DatabaseConnectionRenderer;
import com.eas.designer.application.utils.DatabaseConnections;
import java.sql.ResultSet;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class DbSchemeSettingsVisualPanel extends javax.swing.JPanel {

    protected NewDbSchemeWizardSettingsPanel panel;
    protected String datasourceName;
    protected DefaultComboBoxModel schemasModel;

    /**
     * Creates new form DbSchemeSettingsVisualPanel
     */
    public DbSchemeSettingsVisualPanel(NewDbSchemeWizardSettingsPanel aWizardStep) {
        initComponents();
        panel = aWizardStep;
        schemasModel = new DefaultComboBoxModel();
        comboSchema.setModel(schemasModel);
        txtConnection.setModel(new DefaultComboBoxModel(ConnectionManager.getDefault().getConnections()));
        txtConnection.setRenderer(new DatabaseConnectionRenderer(panel.getProject()));
        refreshControls(null);
    }

    public String getDefaultSchema() throws Exception {
        return panel.getProject().getBasesProxy().getConnectionSchema(datasourceName);
    }

    private boolean refreshingControls;

    public void refreshControls(String schema) {
        refreshingControls = true;
        try {
            String dsName = datasourceName;
            if (dsName == null) {
                dsName = panel.getProject().getSettings().getDefaultDataSourceName();
            }
            DatabaseConnection conn = DatabaseConnections.lookup(dsName);
            txtConnection.setSelectedItem(dsName == null ? null : conn);
            try {
                refreshSchemas(conn);
                if (schema == null || schema.isEmpty()) {
                    schema = getDefaultSchema();
                }
                comboSchema.setSelectedIndex(locateSchema(schema));
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } finally {
            refreshingControls = false;
        }
    }

    protected void refreshSchemas(DatabaseConnection conn) throws Exception {
        schemasModel.removeAllElements();
        if (conn != null && conn.getJDBCConnection() != null) {
            try (ResultSet rs = conn.getJDBCConnection().getMetaData().getSchemas()) {
                while (rs.next()) {
                    String schema = rs.getString(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                    if (schema != null) {
                        schemasModel.addElement(schema);
                    }
                }
            }
        }
    }

    public int locateSchema(String aSchema) throws Exception {
        String schemaToLocate = aSchema;
        if (schemaToLocate == null || schemaToLocate.isEmpty()) {
            schemaToLocate = getDefaultSchema();
        }
        for (int i = 0; i < schemasModel.getSize(); i++) {
            Object oSchema = schemasModel.getElementAt(i);
            String sSchema = (String) oSchema;
            if (sSchema.equalsIgnoreCase(schemaToLocate)) {
                return i;
            }
        }
        return -1;
    }

    public boolean valid(WizardDescriptor wd) throws Exception {
        String lDatasourceName = datasourceName;
        if (lDatasourceName == null) {
            lDatasourceName = panel.getProject().getSettings().getDefaultDataSourceName();
        }
        if (lDatasourceName == null || !panel.datasourceExist(lDatasourceName)) {
            wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "nonConnectionFile"));
            return false;
        }
        if (!panel.datasourceConnected(lDatasourceName)) {
            wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "schemasUnavailable"));
            return false;
        }
        wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "readyForNextStep"));
        return true;
    }

    void store(WizardDescriptor wd) throws Exception {
        String schema = (String) schemasModel.getSelectedItem();
        if (schema != null && !schema.isEmpty()) {
            schema = checkDefaultSchema(schema);
        }
        wd.putProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME, datasourceName);
        wd.putProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME, schema);
    }

    void read(WizardDescriptor wd) throws Exception {
        datasourceName = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME);
        String schema = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME);
        refreshControls(schema);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConnection = new javax.swing.JLabel();
        lblSchema = new javax.swing.JLabel();
        comboSchema = new javax.swing.JComboBox();
        btnDefaultSchema = new javax.swing.JButton();
        txtConnection = new javax.swing.JComboBox();
        btnApplicationConnection = new javax.swing.JButton();

        lblConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblConnection.text")); // NOI18N

        lblSchema.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblSchema.text")); // NOI18N

        btnDefaultSchema.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnDefaultSchema.text")); // NOI18N
        btnDefaultSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultSchemaActionPerformed(evt);
            }
        });

        txtConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConnectionActionPerformed(evt);
            }
        });

        btnApplicationConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnApplicationConnection.text")); // NOI18N
        btnApplicationConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplicationConnectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConnection)
                    .addComponent(lblSchema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboSchema, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtConnection, 0, 248, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnApplicationConnection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDefaultSchema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApplicationConnection))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSchema)
                    .addComponent(comboSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultSchema))
                .addContainerGap(232, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDefaultSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultSchemaActionPerformed
        try {
            String schema = getDefaultSchema();
            comboSchema.setSelectedIndex(locateSchema(schema));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnDefaultSchemaActionPerformed

    private void btnApplicationConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplicationConnectionActionPerformed
        try {
            datasourceName = null;
            refreshControls(null);
            panel.fireChangeEvent();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnApplicationConnectionActionPerformed

    private void txtConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConnectionActionPerformed
        if (!refreshingControls) {
            try {
                DatabaseConnection conn = (DatabaseConnection) txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
                refreshSchemas(conn);
                String schema = getDefaultSchema();
                comboSchema.setSelectedIndex(locateSchema(schema));
                panel.fireChangeEvent();
            } catch (Exception ex) {
                panel.fireChangeEvent();
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_txtConnectionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplicationConnection;
    private javax.swing.JButton btnDefaultSchema;
    private javax.swing.JComboBox comboSchema;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblSchema;
    private javax.swing.JComboBox txtConnection;
    // End of variables declaration//GEN-END:variables

    private String checkDefaultSchema(String schema) throws Exception {
        String defSchema = getDefaultSchema();
        if (schema != null && !schema.isEmpty() && schema.equalsIgnoreCase(defSchema)) {
            return null;
        } else {
            return schema;
        }
    }
}
