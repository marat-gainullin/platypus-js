package com.eas.samples;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.explorer.platform.PlatypusPlatformDialog;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.CommonServerUIs;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class PlatypusSamplesPanelVisual extends JPanel implements DocumentListener {

    private static final class J2eePlatformAdapter {

        public static final J2eePlatformAdapter UNKNOWN_PLATFORM_ADAPRER = new J2eePlatformAdapter(null, null);
        private final J2eePlatform platform;
        private final String serverInstanceId;

        public J2eePlatformAdapter(J2eePlatform aPlatform, String aServerInstanceId) {
            super();
            platform = aPlatform;
            serverInstanceId = aServerInstanceId;
        }

        public J2eePlatform getJ2eePlatform() {
            return platform;
        }

        public String getServerInstanceId() {
            return serverInstanceId;
        }

        @Override
        public String toString() {
            if (platform != null) {
                return platform.getDisplayName() != null ? platform.getDisplayName() : "";// NOI18N
            } else {
                return NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "cbj2eeServer.NoneText");// NOI18N
            }
        }
    }

    private class ServerRegistryChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            serversModel.removeAllElements();
            for (J2eePlatformAdapter server : getJ2eePlatforms()) {
                serversModel.addElement(server);
            }
            selectServerInstance();
        }

    }

    private boolean withDB;
    private PlatypusSamplesWizardPanel panel;
    private final DefaultComboBoxModel serversModel;
    private final ServerRegistryChangeListener serverRegistryListener = new ServerRegistryChangeListener();
    private String serverInstanceId;

    /**
     * Creates new form PanelProjectLocationVisual
     */
    public PlatypusSamplesPanelVisual(PlatypusSamplesWizardPanel aPanel, boolean aWithDB) {
        super();
        panel = aPanel;
        serversModel = new DefaultComboBoxModel(getJ2eePlatforms());
        initComponents();
        EventQueue.invokeLater(() -> {
            cbj2eeServer.setSelectedItem(J2eePlatformAdapter.UNKNOWN_PLATFORM_ADAPRER);
        });
        withDB = aWithDB;
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener(this);
        projectLocationTextField.getDocument().addDocumentListener(this);
        txtDBName.getDocument().addDocumentListener(this);

        if (!aWithDB) {
            txtDBName.setVisible(false);
            lblDBName.setVisible(false);
            txtDBLocation.setVisible(false);
            lblDBLocation.setVisible(false);
        }
    }

    private void selectServerInstance() {
        if (serverInstanceId != null && !serverInstanceId.isEmpty()) {
            for (int i = 0; i < cbj2eeServer.getItemCount(); i++) {
                if (serverInstanceId.equals(((J2eePlatformAdapter) cbj2eeServer.getItemAt(i)).getServerInstanceId())) {
                    cbj2eeServer.setSelectedIndex(i);
                }
            }
        }
    }

    private J2eePlatformAdapter[] getJ2eePlatforms() {
        String[] serverInstanceIDs = Deployment.getDefault().getServerInstanceIDs();
        List<J2eePlatformAdapter> j2eePlatforms = new ArrayList<>();
        j2eePlatforms.add(J2eePlatformAdapter.UNKNOWN_PLATFORM_ADAPRER);
        for (String serverInstance : serverInstanceIDs) {
            try {
                j2eePlatforms.add(new J2eePlatformAdapter(Deployment.getDefault().getServerInstance(serverInstance).getJ2eePlatform(), serverInstance));
            } catch (InstanceRemovedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Server instance has been removed.", ex); //NOI18N
            }
        }
        return j2eePlatforms.toArray(new J2eePlatformAdapter[]{});
    }

    public String getProjectName() {
        return this.projectNameTextField.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        lblDBName = new javax.swing.JLabel();
        txtDBName = new javax.swing.JTextField();
        lblDBLocation = new javax.swing.JLabel();
        txtDBLocation = new javax.swing.JTextField();
        btnManageServers = new javax.swing.JButton();
        btnPickPlatypusHome = new javax.swing.JButton();
        cbj2eeServer = new javax.swing.JComboBox();
        lblServer = new javax.swing.JLabel();

        projectNameLabel.setLabelFor(projectNameTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/samples/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, bundle.getString("LBL_ProjectName")); // NOI18N

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, bundle.getString("LBL_ProjectLocation")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, bundle.getString("LBL_Browse")); // NOI18N
        browseButton.setActionCommand("BROWSE");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, bundle.getString("LBL_ProjectFolder")); // NOI18N

        createdFolderTextField.setEditable(false);
        createdFolderTextField.setEnabled(false);

        lblDBName.setLabelFor(txtDBName);
        org.openide.awt.Mnemonics.setLocalizedText(lblDBName, org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_DBName")); // NOI18N

        lblDBLocation.setLabelFor(txtDBLocation);
        org.openide.awt.Mnemonics.setLocalizedText(lblDBLocation, org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_DBLocation")); // NOI18N

        txtDBLocation.setEditable(false);
        txtDBLocation.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(btnManageServers, org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "btnManageServers.text")); // NOI18N
        btnManageServers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageServersActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnPickPlatypusHome, org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "btnPickPlatypusHome.text")); // NOI18N
        btnPickPlatypusHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPickPlatypusHomeActionPerformed(evt);
            }
        });

        cbj2eeServer.setModel(serversModel);
        cbj2eeServer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbj2eeServerItemStateChanged(evt);
            }
        });
        cbj2eeServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbj2eeServerActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblServer, org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "lblServer.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblDBLocation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(lblDBName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createdFolderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectLocationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectNameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbj2eeServer, 0, 295, Short.MAX_VALUE)
                    .addComponent(projectNameTextField)
                    .addComponent(txtDBName)
                    .addComponent(createdFolderTextField)
                    .addComponent(txtDBLocation)
                    .addComponent(projectLocationTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPickPlatypusHome, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(browseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnManageServers, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectNameLabel)
                    .addComponent(projectNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(createdFolderLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(projectLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(browseButton)
                            .addComponent(projectLocationLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createdFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbj2eeServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnManageServers)
                    .addComponent(lblServer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDBName)
                    .addComponent(btnPickPlatypusHome))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDBLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDBLocation))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        projectLocationTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_ProjectLocation_A11YDesc")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_Browse_A11YDesc")); // NOI18N
        txtDBName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_DBName_A11YDesc")); // NOI18N
        txtDBLocation.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_DBLocation_A11YDesc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String command = evt.getActionCommand();
        if ("BROWSE".equals(command)) { //NOI18N
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(null);
            chooser.setDialogTitle(NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "LBL_TITLE"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
            }
            panel.fireChangeEvent();
        }

    }//GEN-LAST:event_browseButtonActionPerformed

    private void btnManageServersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageServersActionPerformed
        Lookup.Result<ServerInstanceProvider> result = Lookups.forPath("Servers").lookupResult(ServerInstanceProvider.class);
        result.allInstances().stream().forEach((provider) -> {
            provider.addChangeListener(serverRegistryListener);
        });
        try {
            CommonServerUIs.showCustomizer(null);
        } finally {
            result.allInstances().stream().forEach((provider) -> {
                provider.removeChangeListener(serverRegistryListener);
            });
        }
    }//GEN-LAST:event_btnManageServersActionPerformed

    private void btnPickPlatypusHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPickPlatypusHomeActionPerformed
        if (PlatypusPlatformDialog.showPlatformHomeDialog()) {
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_btnPickPlatypusHomeActionPerformed

    private void cbj2eeServerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbj2eeServerItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_cbj2eeServerItemStateChanged

    private void cbj2eeServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbj2eeServerActionPerformed
        Object selectedItem = cbj2eeServer.getSelectedItem();
        if (selectedItem != null) {
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_cbj2eeServerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton btnManageServers;
    private javax.swing.JButton btnPickPlatypusHome;
    private javax.swing.JComboBox cbj2eeServer;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JLabel lblDBLocation;
    private javax.swing.JLabel lblDBName;
    private javax.swing.JLabel lblServer;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JTextField txtDBLocation;
    private javax.swing.JTextField txtDBName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
        selectServerInstance();
    }

    boolean valid(WizardDescriptor wizardDescriptor) {

        String p = PlatypusPlatform.getPlatformHomePath();
        if (p == null || p.isEmpty()) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "ERR_MissingPlatypusHome"));
            btnPickPlatypusHome.setVisible(true);
            return false;
        } else {
            btnPickPlatypusHome.setVisible(false);
        }

        if (projectNameTextField.getText().length() == 0) {

            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_InvalidProjectName"));

            return false; // Display name not specified
        }
        String projectLocation = projectLocationTextField.getText();
        File f = FileUtil.normalizeFile(new File(projectLocation).getAbsoluteFile());
        if (!f.isDirectory() || projectLocation.length() == 0) {
            String message = NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_InvalidPath");
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, message);
            return false;
        }
        final File destFolder = FileUtil.normalizeFile(new File(createdFolderTextField.getText()).getAbsoluteFile());

        File projLoc = destFolder;
        while (projLoc != null && !projLoc.exists()) {
            projLoc = projLoc.getParentFile();
        }
        if (projLoc == null || !projLoc.canWrite()) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_FolderCannotBeCreated"));

            return false;
        }

        if (FileUtil.toFileObject(projLoc) == null) {
            String message = NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_InvalidPath");
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, message);
            return false;
        }

        File[] kids = destFolder.listFiles();
        if (destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_FolderAlreadyExists"));

            return false;
        }

        if (cbj2eeServer.getSelectedItem() == null || cbj2eeServer.getSelectedItem() == J2eePlatformAdapter.UNKNOWN_PLATFORM_ADAPRER) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "ERR_MissingJavaEE6AppServer"));

            return false;
        }

        if (withDB) {
            File newDbFile = new File(txtDBLocation.getText());
            if (newDbFile.exists()) {
                String message = NbBundle.getMessage(PlatypusSamplesPanelVisual.class, "MSG_DatabaseAlreadyExists");
                wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, message);
                return false;
            }
        }

        wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        return true;
    }

    void store(WizardDescriptor settings) {
        String name = projectNameTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();

        settings.putProperty(PlatypusSamples.PROJ_DIR, new File(folder));
        settings.putProperty(PlatypusSamples.NAME, name);
        settings.putProperty(PlatypusSamples.SERVER_ID, cbj2eeServer.getSelectedItem());

        if (withDB) {
            String dbName = txtDBName.getText().trim();
            settings.putProperty(PlatypusSamples.DB_NAME, dbName);
        }
    }

    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty(PlatypusSamples.PROJ_DIR);
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        projectLocationTextField.setText(projectLocation.getAbsolutePath());

        String projectName = (String) settings.getProperty(PlatypusSamples.NAME);
        if (projectName == null) {
            projectName = "sample"; //NOI18N
        }
        projectNameTextField.setText(projectName);
        projectNameTextField.selectAll();
        cbj2eeServer.setSelectedItem(settings.getProperty(PlatypusSamples.SERVER_ID));

        if (withDB) {
            String dbName = getFirstFreeDatabaseName(projectName);
            txtDBName.setText(dbName);
            txtDBName.selectAll();
            updateDBPath(dbName);
        }
    }

    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    // Implementation of DocumentListener --------------------------------------
    @Override
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    /**
     * Handles changes in the Project name and project directory,
     */
    private void updateTexts(DocumentEvent e) {

        if (projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PlatypusSamples.NAME, null, projectNameTextField.getText());
        }

        if (projectLocationTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PlatypusSamples.PROJ_DIR, null, projectLocationTextField.getText());
        }

        Document doc = e.getDocument();

        if (doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument()) {
            // Change in the project name

            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText();

            //if (projectFolder.trim().length() == 0 || projectFolder.equals(oldName)) {
            createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
            //}

        } else if (doc == txtDBName.getDocument()) {
            String dbName = txtDBName.getText();
            firePropertyChange(PlatypusSamples.DB_NAME, null, dbName);
            updateDBPath(dbName);
        }

        panel.fireChangeEvent(); // Notify that the panel changed
    }

    private static final String USER_HOME_PROPERTY = System.getProperty("user.home");

    private void updateDBPath(String dbName) {
        String dbPath = new File(USER_HOME_PROPERTY, dbName).getPath();
        txtDBLocation.setText(dbPath + H2DB_SUFFIX);
    }
    private static final String H2DB_SUFFIX = ".h2.db";

    private String getFirstFreeDatabaseName(String aDefaultName) {
        String freeName = aDefaultName;
        File dbCandidate = new File(USER_HOME_PROPERTY, freeName + H2DB_SUFFIX);
        int counter = 0;
        while (dbCandidate.exists()) {
            freeName = aDefaultName + "_" + ++counter;
            dbCandidate = new File(USER_HOME_PROPERTY, freeName + H2DB_SUFFIX);
        }
        return freeName;
    }

    private boolean isJavaEECapableServerRegistered() {
        for (String serverInstanceID : Deployment.getDefault().getServerInstanceIDs()) {
            try {
                J2eePlatform j2eePlatform = Deployment.getDefault().getServerInstance(serverInstanceID).getJ2eePlatform();

                if (j2eePlatform.getSupportedProfiles().contains(Profile.JAVA_EE_6_WEB)) {
                    return true;
                }
            } catch (InstanceRemovedException ex) {
                Logger.getLogger(PlatypusSamplesPanelVisual.class.getName()).log(Level.INFO, "Cannot find registerred JavaEE 5/JavaEE 6  server", ex);
            }
        }

        return false;
    }
}
