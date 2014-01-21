/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbSchemeSettingsVisualPanel.java
 *
 * Created on 25.03.2011, 11:54:03
 */
package com.eas.designer.application.dbdiagram.templates;

import com.eas.client.DatabasesClient;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.utils.DatabaseConnectionComboBoxModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
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
        txtConnection.setModel(new DatabaseConnectionComboBoxModel());
        txtConnection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConnection conn = (DatabaseConnection) txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
            }

        });
        txtConnection.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                DatabaseConnection conn = (DatabaseConnection) txtConnection.getSelectedItem();
                datasourceName = conn != null ? conn.getDisplayName() : null;
            }
        });
    }

    public String getDefaultSchema() throws Exception {
        DatabaseConnection conn = ConnectionManager.getDefault().getConnection(datasourceName);
        Connection jdbcConn = conn.getJDBCConnection();
        if (jdbcConn != null) {
            return DatabasesClient.schemaByConnection(jdbcConn);
        } else {
            return null;
        }
    }

    public void refreshControls(String schema) throws Exception {
        DatabaseConnection conn = ConnectionManager.getDefault().getConnection(datasourceName);
        if (conn != null) {
            List<String> schemas = PlatypusUtils.achieveSchemas(conn.getDatabaseURL(), conn.getUser(), conn.getPassword());
            schemasModel = new DefaultComboBoxModel(schemas.toArray(new String[0]));
            comboSchema.setModel(schemasModel);
            int schemaIndx = locateSchema(schema);
            if (schemaIndx != -1) {
                comboSchema.setSelectedIndex(schemaIndx);
            }
            txtConnection.setSelectedItem(conn);
        } else {
            txtConnection.setSelectedItem(null);
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
        String lDatasourceName = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME);
        //String lschema = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME);
        if (lDatasourceName != null && !panel.connectionExist(lDatasourceName)) {
            wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "nonConnectionFile"));
            return false;
        }
        wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "readyForNextStep"));
        return true;
    }

    void store(WizardDescriptor wd) throws Exception {
        String schema = null;
        if (schemasModel != null) {
            schema = (String) schemasModel.getSelectedItem();
            if (datasourceName == null && schema != null && !schema.isEmpty()) {
                schema = checkDefaultSchema(schema);
            }
        }
        wd.putProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME, datasourceName);
        wd.putProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME, schema);
    }

    void read(WizardDescriptor wd) throws Exception {
        datasourceName = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME);
        String schema = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME);
        if (schema == null || schema.isEmpty()) {
            schema = getDefaultSchema();
        }
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
        btnApplicationConnection = new javax.swing.JToggleButton();
        btnDefaultSchema = new javax.swing.JButton();
        txtConnection = new javax.swing.JComboBox();

        lblConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblConnection.text")); // NOI18N

        lblSchema.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblSchema.text")); // NOI18N

        btnApplicationConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnApplicationConnection.text")); // NOI18N
        btnApplicationConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplicationConnectionActionPerformed(evt);
            }
        });

        btnDefaultSchema.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnDefaultSchema.text")); // NOI18N
        btnDefaultSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultSchemaActionPerformed(evt);
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
                    .addComponent(txtConnection, 0, 274, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnApplicationConnection)
                    .addComponent(btnDefaultSchema))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(btnApplicationConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            int schemaIndx = locateSchema(schema);
            if (schemaIndx != -1) {
                comboSchema.setSelectedIndex(schemaIndx);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnDefaultSchemaActionPerformed

    private void btnApplicationConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplicationConnectionActionPerformed
        try {
            datasourceName = null;
            refreshControls(getDefaultSchema());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnApplicationConnectionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnApplicationConnection;
    private javax.swing.JButton btnDefaultSchema;
    private javax.swing.JComboBox comboSchema;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblSchema;
    private javax.swing.JComboBox txtConnection;
    // End of variables declaration//GEN-END:variables

    /*
     private void selectNewConnection() throws Exception {
     Set<String> allowedMimeTypes = new HashSet<>();
     allowedMimeTypes.add("text/connection+xml");
     FileObject fo = FileChooser.selectAppElement(panel.getProject().getSrcRoot(), connectionFile, allowedMimeTypes);
     if (fo != connectionFile) {
     // let's test connection capability
     try {
     DbConnectionSettings settings = readSettings(connectionFile);
     Properties props = new Properties();
     props.put("user", settings.getUser());
     props.put("password", settings.getPassword());
     props.put("schema", settings.getSchema());
     java.sql.Connection conn = DriverManager.getConnection(settings.getUrl(), props);
     conn.close();
     } catch (Exception ex) {
     refreshButtons();
     DialogDescriptor.Message dd = new DialogDescriptor.Message(ex.getMessage(), DialogDescriptor.Message.ERROR_MESSAGE);
     DialogDisplayer.getDefault().notify(dd);
     return;
     }
     connectionFile = fo;
     String schema = getDefaultSchema();
     refreshControls(schema);
     }
     }
     */
    private String checkDefaultSchema(String schema) throws Exception {
        String defSchema = getDefaultSchema();
        if (schema != null && !schema.isEmpty() && schema.equalsIgnoreCase(defSchema)) {
            return null;
        } else {
            return schema;
        }
    }
}
