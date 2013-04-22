/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.connection;

import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.application.PlatypusUtils;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
public final class PlatypusConnectionView extends CloneableTopComponent {

    static final long serialVersionUID = 3141132923400028L;
    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "com/eas/designer/application/connection/connection.png";
    private static final String PREFERRED_ID = "PlatypusConnectionTopComponent";
    protected PlatypusConnectionDataObject dataObject;
    protected transient DefaultComboBoxModel schemasModel;
    protected transient ControlsUpdater controlsUpdater = new ControlsUpdater();
    protected transient boolean settingsControls;
    protected transient HandlerRegistration clientChangeListener;

    public void refreshSchemas() throws Exception {
        try {
            List<String> schemas = PlatypusUtils.achieveSchemas(dataObject.getUrl(), dataObject.getUser(), dataObject.getPassword());
            schemasModel = new DefaultComboBoxModel(schemas.toArray(new String[0]));
            comboSchema.setModel(schemasModel);
        } catch (Exception ex) {
            Logger.getLogger(PlatypusConnectionView.class.getName()).log(Level.SEVERE, ex.getMessage());
            schemasModel = new DefaultComboBoxModel(new String[0]);
            comboSchema.setModel(schemasModel);
        }
    }

    protected class ControlsUpdater implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                switch (evt.getPropertyName()) {
                    case PlatypusConnectionDataObject.PROP_APP_ELEMENT_NAME:
                        txtAppElementId.setText(dataObject.getAppElementName());
                        break;
                    case PlatypusConnectionDataObject.PROP_URL:
                        txtUrl.setText(dataObject.getUrl());
                        refreshSchemas();
                        break;
                    case PlatypusConnectionDataObject.PROP_USER:
                        txtUser.setText(dataObject.getUser());
                        refreshSchemas();
                        break;
                    case PlatypusConnectionDataObject.PROP_PASSWORD:
                        txtDbPassword.setText(dataObject.getPassword());
                        refreshSchemas();
                        break;
                    case PlatypusConnectionDataObject.PROP_SCHEMA:
                        int schemaIndx = locateSchema(dataObject.getSchema());
                        settingsControls = true;
                        try {
                            comboSchema.setSelectedIndex(schemaIndx);
                        } finally {
                            settingsControls = false;
                        }
                        break;
                    case PlatypusConnectionDataObject.PROP_MODIFIED:
                        updateTitle();
                        break;
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public PlatypusConnectionView() throws Exception {
        super();
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
    }

    private void initView() throws Exception {
        removeAll();
        setLayout(new BorderLayout());
        if (dataObject.getClient() != null) {
            initComponents();
            refreshSchemas();
            setAllControls();
        } else {
            add(dataObject.getProject().generateDbPlaceholder(), BorderLayout.CENTER);
        }
    }

    public PlatypusConnectionDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(PlatypusConnectionDataObject aDataObject) throws Exception {
        dataObject = aDataObject;
        setName(dataObject.getPrimaryFile().getName());
        setToolTipText(NbBundle.getMessage(PlatypusConnectionView.class, "HINT_PlatypusConnectionTopComponent", dataObject.getPrimaryFile().getPath()));
        dataObject.addPropertyChangeListener(controlsUpdater);
        initView();
        clientChangeListener = dataObject.addClientChangeListener(new Runnable() {
            @Override
            public void run() {
                try {
                    initView();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        });
    }

    public int locateSchema(String aSchema) {
        for (int i = 0; i < schemasModel.getSize(); i++) {
            Object oSchema = schemasModel.getElementAt(i);
            String sSchema = (String) oSchema;
            if (sSchema.equalsIgnoreCase(aSchema)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        try {
            super.readExternal(oi);
            setDataObject((PlatypusConnectionDataObject) oi.readObject());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        super.writeExternal(oo);
        oo.writeObject(dataObject);
    }

    public void updateTitle() {
        String boldTitleMask = "<html><b>%s</b>";
        String plainTitleMask = "<html>%s";
        String titleMask = plainTitleMask;
        if (dataObject.isModified()) {
            titleMask = boldTitleMask;
        }
        final String newTitle = String.format(titleMask, getName());
        if (EventQueue.isDispatchThread()) {
            setHtmlDisplayName(newTitle);
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setHtmlDisplayName(newTitle);
                }
            });
        }
    }

    protected void setAllControls() {
        try {
            txtAppElementId.setText(dataObject.getAppElementName());
            txtUrl.setText(dataObject.getUrl());
            txtUser.setText(dataObject.getUser());
            txtDbPassword.setText(dataObject.getPassword());
            int schemaIndx = locateSchema(dataObject.getSchema());
            settingsControls = true;
            try {
                comboSchema.setSelectedIndex(schemaIndx);
            } finally {
                settingsControls = false;
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblUrl = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblDbPassword = new javax.swing.JLabel();
        txtDbPassword = new javax.swing.JPasswordField();
        lblSchema = new javax.swing.JLabel();
        btnTestConnection = new javax.swing.JButton();
        comboSchema = new javax.swing.JComboBox();
        lblName = new javax.swing.JLabel();
        txtAppElementId = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(lblUrl, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.lblUrl.text")); // NOI18N

        txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });
        txtUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUrlFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblUser, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.lblUser.text")); // NOI18N

        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });
        txtUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUserFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblDbPassword, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.lblDbPassword.text")); // NOI18N

        txtDbPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDbPasswordActionPerformed(evt);
            }
        });
        txtDbPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDbPasswordFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblSchema, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.lblSchema.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnTestConnection, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.btnTestConnection.text")); // NOI18N
        btnTestConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestConnectionActionPerformed(evt);
            }
        });

        comboSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSchemaActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblName, org.openide.util.NbBundle.getMessage(PlatypusConnectionView.class, "PlatypusConnectionView.lblName.text")); // NOI18N

        txtAppElementId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAppElementIdActionPerformed(evt);
            }
        });
        txtAppElementId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAppElementIdFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTestConnection))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDbPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                            .addComponent(txtUrl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                            .addComponent(comboSchema, 0, 318, Short.MAX_VALUE)
                            .addComponent(txtAppElementId)
                            .addComponent(txtUser, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtAppElementId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUrl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDbPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSchema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTestConnection)
                .addContainerGap(135, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        try {
            dataObject.setUrl(txtUrl.getText());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_txtUrlActionPerformed

    private void txtUrlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUrlFocusLost
        if (isDisplayable()) {
            try {
                dataObject.setUrl(txtUrl.getText());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_txtUrlFocusLost

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        try {
            dataObject.setUser(txtUser.getText());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtUserFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserFocusLost
        if (isDisplayable()) {
            try {
                dataObject.setUser(txtUser.getText());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_txtUserFocusLost

    private void txtDbPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDbPasswordActionPerformed
        try {
            dataObject.setPassword(new String(txtDbPassword.getPassword()));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_txtDbPasswordActionPerformed

    private void txtDbPasswordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDbPasswordFocusLost
        if (isDisplayable()) {
            try {
                dataObject.setPassword(new String(txtDbPassword.getPassword()));
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_txtDbPasswordFocusLost

    private void btnTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestConnectionActionPerformed
        try {
            java.sql.Connection conn = DriverManager.getConnection(dataObject.getUrl(), dataObject.getUser(), dataObject.getPassword());
            conn.close();
            DialogDescriptor.Message msg = new DialogDescriptor.Message(NbBundle.getMessage(PlatypusConnectionView.class, "DbConnectionTestSucceded"), DialogDescriptor.Message.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(msg);
        } catch (Exception ex) {
            String errorString = MessageFormat.format(NbBundle.getMessage(PlatypusConnectionView.class, "DbConnectionTestFaild"), ex.getMessage());
            DialogDescriptor.Message msg = new DialogDescriptor.Message(errorString, DialogDescriptor.Message.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(msg);
        }
    }//GEN-LAST:event_btnTestConnectionActionPerformed

    private void comboSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSchemaActionPerformed
        if (!settingsControls) {
            try {
                dataObject.setSchema((String) comboSchema.getSelectedItem());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_comboSchemaActionPerformed

    private void txtAppElementIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAppElementIdActionPerformed
        try {
            dataObject.setAppElementName(txtAppElementId.getText().trim());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_txtAppElementIdActionPerformed

    private void txtAppElementIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAppElementIdFocusLost
        if (isDisplayable()) {
            try {
                dataObject.setAppElementName(txtAppElementId.getText().trim());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_txtAppElementIdFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTestConnection;
    private javax.swing.JComboBox comboSchema;
    private javax.swing.JLabel lblDbPassword;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSchema;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTextField txtAppElementId;
    private javax.swing.JPasswordField txtDbPassword;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public void componentActivated() {
        try {
            super.componentActivated();
            if (dataObject.isValid() && dataObject.getClient() != null) {
                setActivatedNodes(new Node[0]);
                setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }

    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        updateTitle();
    }

    @Override
    public boolean canClose() {
        PlatypusConnectionSupport support = dataObject.getLookup().lookup(PlatypusConnectionSupport.class);
        List<CloneableTopComponent> views = support.getAllViews();
        if (views != null && views.size() == 1) {
            return support.canClose();
        }
        return super.canClose();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        dataObject.removePropertyChangeListener(controlsUpdater);
        if (clientChangeListener != null) {
            clientChangeListener.remove();
        }
        PlatypusConnectionSupport support = dataObject.getLookup().lookup(PlatypusConnectionSupport.class);
        support.shrink();
    }

    @Override
    public Action[] getActions() {
        List<Action> actions = new ArrayList<>(Arrays.asList(super.getActions()));
        // XXX nicer to use MimeLookup for type-specific actions, but not easy; see org.netbeans.modules.editor.impl.EditorActionsProvider
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Editors/TabActions")); //NOI18N
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
}
