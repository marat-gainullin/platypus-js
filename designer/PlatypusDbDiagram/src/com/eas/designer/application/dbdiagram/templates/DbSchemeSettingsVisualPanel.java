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

import com.eas.client.ClientConstants;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.XmlDom2ConnectionSettings;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.FileChooser;
import com.eas.xml.dom.Source2XmlDom;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class DbSchemeSettingsVisualPanel extends javax.swing.JPanel {

    protected NewDbSchemeWizardSettingsPanel panel;
    protected FileObject connectionFile;
    protected DefaultComboBoxModel schemasModel;

    /**
     * Creates new form DbSchemeSettingsVisualPanel
     */
    public DbSchemeSettingsVisualPanel(NewDbSchemeWizardSettingsPanel aWizardStep) {
        initComponents();
        panel = aWizardStep;
    }

    public String getDefaultSchema() throws Exception {
        DbConnectionSettings settings = connectionFile != null ? readSettings(connectionFile) : panel.getProject().getSettings().getAppSettings().getDbSettings();
        return settings.getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME);
    }

    protected DbConnectionSettings readSettings(FileObject aFile) throws Exception {
        EasSettings settings = null;
        if (aFile != null) {
            String sContent = aFile.asText(PlatypusUtils.COMMON_ENCODING_NAME);
            settings = XmlDom2ConnectionSettings.document2Settings(Source2XmlDom.transform(sContent));
        } else {
            if (panel.getProject().getClient() != null) {
                settings = panel.getProject().getClient().getSettings();
            }
        }
        assert settings == null || settings instanceof DbConnectionSettings : "Platypus application designer must work only in two tier mode.";
        return (DbConnectionSettings) settings;
    }

    protected void refreshButtons() {
        if (connectionFile == null) {
            btnApplicationConnection.setSelected(true);
        } else {
            btnSpecificConnection.setSelected(true);
        }
    }

    public void refreshControls(String schema) throws Exception {
        DbConnectionSettings settings = connectionFile != null ? readSettings(connectionFile) : panel.getProject().getSettings().getAppSettings().getDbSettings();
        List<String> schemas = PlatypusUtils.achieveSchemas(settings.getUrl(), settings.getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME), settings.getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));
        schemasModel = new DefaultComboBoxModel(schemas.toArray(new String[0]));
        comboSchema.setModel(schemasModel);
        int schemaIndx = locateSchema(schema);
        if (schemaIndx != -1) {
            comboSchema.setSelectedIndex(schemaIndx);
        }
        if (connectionFile == null) {
            txtConnection.setText(panel.getProject().getDisplayName());
        } else {
            assert connectionFile != null;
            txtConnection.setText(connectionFile.getName());
        }
        refreshButtons();
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
        FileObject lconnectionFile = (FileObject) wd.getProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME);
        //String lschema = (String) wd.getProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME);
        if (lconnectionFile != null && !panel.isConnectionElement(lconnectionFile)) {
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
            if (connectionFile == null && schema != null && !schema.isEmpty()) {
                schema = checkDefaultSchema(schema);
            }
        }
        wd.putProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME, connectionFile);
        wd.putProperty(NewDbSchemeWizardSettingsPanel.SCHEMA_PROP_NAME, schema);
    }

    void read(WizardDescriptor wd) throws Exception {
        connectionFile = (FileObject) wd.getProperty(NewDbSchemeWizardSettingsPanel.CONNECTION_PROP_NAME);
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

        groupConnectionType = new javax.swing.ButtonGroup();
        lblConnection = new javax.swing.JLabel();
        txtConnection = new javax.swing.JTextField();
        lblSchema = new javax.swing.JLabel();
        comboSchema = new javax.swing.JComboBox();
        btnApplicationConnection = new javax.swing.JToggleButton();
        btnSpecificConnection = new javax.swing.JToggleButton();
        btnDefaultSchema = new javax.swing.JButton();

        lblConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblConnection.text")); // NOI18N

        txtConnection.setEditable(false);

        lblSchema.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.lblSchema.text")); // NOI18N

        groupConnectionType.add(btnApplicationConnection);
        btnApplicationConnection.setSelected(true);
        btnApplicationConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnApplicationConnection.text")); // NOI18N
        btnApplicationConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplicationConnectionActionPerformed(evt);
            }
        });

        groupConnectionType.add(btnSpecificConnection);
        btnSpecificConnection.setText(org.openide.util.NbBundle.getMessage(DbSchemeSettingsVisualPanel.class, "DbSchemeSettingsVisualPanel.btnSpecificConnection.text")); // NOI18N
        btnSpecificConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpecificConnectionActionPerformed(evt);
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
                    .addComponent(comboSchema, 0, 242, Short.MAX_VALUE)
                    .addComponent(txtConnection, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnApplicationConnection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSpecificConnection))
                    .addComponent(btnDefaultSchema))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnection)
                    .addComponent(txtConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApplicationConnection)
                    .addComponent(btnSpecificConnection))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSchema)
                    .addComponent(comboSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDefaultSchema))
                .addContainerGap(232, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSpecificConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpecificConnectionActionPerformed
        try {
            selectNewConnection();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnSpecificConnectionActionPerformed

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
            connectionFile = null;
            refreshControls(getDefaultSchema());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_btnApplicationConnectionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnApplicationConnection;
    private javax.swing.JButton btnDefaultSchema;
    private javax.swing.JToggleButton btnSpecificConnection;
    private javax.swing.JComboBox comboSchema;
    private javax.swing.ButtonGroup groupConnectionType;
    private javax.swing.JLabel lblConnection;
    private javax.swing.JLabel lblSchema;
    private javax.swing.JTextField txtConnection;
    // End of variables declaration//GEN-END:variables

    private void selectNewConnection() throws Exception {
        Set<String> allowedMimeTypes = new HashSet<>();
        allowedMimeTypes.add("text/connection+xml");
        FileObject fo = FileChooser.selectAppElement(panel.getProject().getSrcRoot(), connectionFile, allowedMimeTypes);
        if (fo != connectionFile) {
            // let's test connection capability
            try {
                DbConnectionSettings settings = readSettings(connectionFile);
                java.sql.Connection conn = DriverManager.getConnection(settings.getUrl(), settings.getInfo());
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

    private String checkDefaultSchema(String schema) throws Exception {
        String defSchema = getDefaultSchema();
        if (schema != null && !schema.isEmpty() && schema.equalsIgnoreCase(defSchema)) {
            return null;
        } else {
            return schema;
        }
    }
}
