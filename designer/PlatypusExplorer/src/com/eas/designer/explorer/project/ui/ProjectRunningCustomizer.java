/*
 * ProjectRunningCustomizer.java
 *
 * Created on 17.03.2011, 18:07:31
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.FileChooser;
import com.eas.designer.application.project.AppServerType;
import com.eas.designer.application.project.ClientType;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import com.eas.designer.application.utils.DatabaseConnectionRenderer;
import com.eas.designer.application.utils.DatabaseConnections;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.server.CommonServerUIs;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerInstance;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author mg
 */
public class ProjectRunningCustomizer extends javax.swing.JPanel {

    public static final String SERVERS_PATH = "Servers"; // NOI18N
    protected final PlatypusProjectImpl project;
    protected final FileObject appRoot;
    protected final PlatypusProjectSettingsImpl projectSettings;
    protected ComboBoxModel<ServerInstance> j2eeServersModel;
    private boolean isInit;
    private final DefaultComboBoxModel serversModel;
    private final ServerRegistryChangeListener serverRegistryListener = new ServerRegistryChangeListener();

    /**
     * Creates new form ProjectRunningCustomizer
     */
    public ProjectRunningCustomizer(PlatypusProjectImpl aProject) throws Exception {
        project = aProject;
        appRoot = aProject.getSrcRoot();
        projectSettings = aProject.getSettings();
        serversModel = new DefaultComboBoxModel(getJ2eePlatforms());
        initComponents();
        setupConnectionsModel();
        cbConnections.setRenderer(new DatabaseConnectionRenderer(null));
        cbConnections.setSelectedItem(projectSettings.getDefaultDataSourceName() != null ? DatabaseConnections.lookup(projectSettings.getDefaultDataSourceName()) : null);
        cbConnections.addActionListener((ActionEvent e) -> {
            DatabaseConnection conn = (DatabaseConnection) cbConnections.getSelectedItem();
            projectSettings.setDefaultDatasourceName(conn != null ? conn.getDisplayName() : null);
        });
        cbConnections.addItemListener((ItemEvent e) -> {
            DatabaseConnection conn = (DatabaseConnection) cbConnections.getSelectedItem();
            projectSettings.setDefaultDatasourceName(conn != null ? conn.getDisplayName() : null);
        });
        EventQueue.invokeLater(() -> {
            isInit = true;
            try {
                if (projectSettings.getRunElement() != null) {
                    txtRunPath.setText(projectSettings.getRunElement());
                }
                if (projectSettings.getSourcePath() != null) {
                    txtSourcePath.setText(projectSettings.getSourcePath());
                }
                if (projectSettings.getRunUser() != null) {
                    txtUserName.setText(projectSettings.getRunUser());
                }
                if (projectSettings.getRunPassword() != null) {
                    txtPassword.setText(projectSettings.getRunPassword());
                }
                if (projectSettings.getRunClientOptions() != null) {
                    txtClientOptions.setText(projectSettings.getRunClientOptions());
                }
                if (projectSettings.getRunClientVmOptions() != null) {
                    txtClientVmOptions.setText(projectSettings.getRunClientVmOptions());
                }
                if (projectSettings.getClientUrl() != null) {
                    txtClientUrl.setText(projectSettings.getClientUrl());
                }
                spServerPort.setValue(projectSettings.getServerPort());
                if (projectSettings.getRunServerOptions() != null) {
                    txtServerOptions.setText(projectSettings.getRunServerOptions());
                }
                if (projectSettings.getRunServerVmOptions() != null) {
                    txtServerVmOptions.setText(projectSettings.getRunServerVmOptions());
                }
                chCacheBust.setSelected(projectSettings.getBrowserCacheBusting());
                chGlobalApi.setSelected(projectSettings.getGlobalAPI());
                cbNotStartServer.setSelected(projectSettings.isNotStartServer());
                cbEnableWebSecurity.setSelected(projectSettings.isSecurityRealmEnabled());
                cbAutoApply.setSelected(projectSettings.isAutoApplyWebSettings());
                cbAutoUpdatePlatypusJs.setSelected(projectSettings.isAutoUpdatePlatypusJs());
                enablePlatypusClientCustomSettings();
                cbClientType.setSelectedItem(projectSettings.getRunClientType());
                cbAppServerType.setSelectedItem(projectSettings.getRunAppServerType());
                cbClientLogLevel.setSelectedItem(projectSettings.getClientLogLevel());
                cbServerLogLevel.setSelectedItem(projectSettings.getServerLogLevel());
                selectServerInstance();

                if (projectSettings.getServerContext() != null) {
                    txtContext.setText(projectSettings.getServerContext());
                }
                spClientDebugPort.setValue(projectSettings.getDebugClientPort());
                spServerDebugPort.setValue(projectSettings.getDebugServerPort());
                checkRunClientServerConfiguration();
            } finally {
                isInit = false;
            }
        });
    }

    private void setupConnectionsModel() {
        String selectedName = cbConnections.getSelectedItem() != null ? ((DatabaseConnection) cbConnections.getSelectedItem()).getDisplayName() : null;
        cbConnections.setModel(new DefaultComboBoxModel(ConnectionManager.getDefault().getConnections()));
        ((DefaultComboBoxModel) cbConnections.getModel()).insertElementAt(null, 0);
        if (selectedName != null) {
            for (DatabaseConnection c : ConnectionManager.getDefault().getConnections()) {
                if (c.getDisplayName() != null && c.getDisplayName().equals(selectedName)) {
                    cbConnections.setSelectedItem(c);
                    return;
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
                if (serverInstance.contains("tomcat")) {
                    j2eePlatforms.add(new J2eePlatformAdapter(Deployment.getDefault().getServerInstance(serverInstance).getJ2eePlatform(), serverInstance));
                }
            } catch (InstanceRemovedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Server instance has been removed.", ex); //NOI18N
            }
        }
        return j2eePlatforms.toArray(new J2eePlatformAdapter[0]);
    }

    private Level[] getLogLevels() {
        return new Level[]{Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST};
    }

    private void selectServerInstance() {
        String serverInstanceId = projectSettings.getJ2eeServerId();
        if (serverInstanceId != null && !serverInstanceId.isEmpty()) {
            for (int i = 0; i < cbj2eeServer.getItemCount(); i++) {
                if (serverInstanceId.equals(((J2eePlatformAdapter) cbj2eeServer.getItemAt(i)).getServerInstanceID())) {
                    cbj2eeServer.setSelectedIndex(i);
                }
            }
        }
    }

    private void checkRunClientServerConfiguration() {
        boolean invalid = isInvalidRunClientServerConfiguration();
        lblClientServerMessage.setVisible(invalid);
    }

    private boolean isInvalidRunClientServerConfiguration() {
        return ClientType.WEB_BROWSER.equals(cbClientType.getSelectedItem()) && !AppServerType.J2EE_SERVER.equals(cbAppServerType.getSelectedItem());
    }

    private void enablePlatypusClientCustomSettings() {
        txtClientUrl.setEnabled(cbNotStartServer.isSelected());
    }

    private static final class J2eePlatformAdapter {

        public static final J2eePlatformAdapter UNKNOWN_PLATFORM_ADAPRER = new J2eePlatformAdapter(null, null);
        private final J2eePlatform platform;
        private final String serverInstanceID;

        public J2eePlatformAdapter(J2eePlatform platform, String serverInstanceID) {
            this.platform = platform;
            this.serverInstanceID = serverInstanceID;
        }

        public J2eePlatform getJ2eePlatform() {
            return platform;
        }

        public String getServerInstanceID() {
            return serverInstanceID;
        }

        @Override
        public String toString() {
            if (platform != null) {
                return platform.getDisplayName() != null ? platform.getDisplayName() : "";// NOI18N
            } else {
                return NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbj2eeServer.NoneText");// NOI18N
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtRunPath = new javax.swing.JTextField();
        lblRunPath = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        clientPanel = new javax.swing.JPanel();
        txtUserName = new javax.swing.JTextField();
        txtClientOptions = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblClientOptions = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtClientUrl = new javax.swing.JTextField();
        lblClientUrl = new javax.swing.JLabel();
        lblClientVmOptions = new javax.swing.JLabel();
        txtClientVmOptions = new javax.swing.JTextField();
        lblClientDebugPort = new javax.swing.JLabel();
        spClientDebugPort = new javax.swing.JSpinner();
        lblClientLogLevel = new javax.swing.JLabel();
        cbClientLogLevel = new javax.swing.JComboBox();
        serverPanel = new javax.swing.JPanel();
        lblServerOptions = new javax.swing.JLabel();
        txtServerOptions = new javax.swing.JTextField();
        lblServerPort = new javax.swing.JLabel();
        spServerPort = new javax.swing.JSpinner();
        lblServerVmOptions = new javax.swing.JLabel();
        txtServerVmOptions = new javax.swing.JTextField();
        lblServerDebugPort = new javax.swing.JLabel();
        spServerDebugPort = new javax.swing.JSpinner();
        lblServerLogLevel = new javax.swing.JLabel();
        cbServerLogLevel = new javax.swing.JComboBox();
        cbNotStartServer = new javax.swing.JCheckBox();
        j2eeServerPanel = new javax.swing.JPanel();
        lblJ2eeServer = new javax.swing.JLabel();
        cbj2eeServer = new javax.swing.JComboBox();
        lblContext = new javax.swing.JLabel();
        txtContext = new javax.swing.JTextField();
        btnManageServers = new javax.swing.JButton();
        cbEnableWebSecurity = new javax.swing.JCheckBox();
        cbAutoApply = new javax.swing.JCheckBox();
        cbClientType = new javax.swing.JComboBox();
        lblClientType = new javax.swing.JLabel();
        lblServeType = new javax.swing.JLabel();
        cbAppServerType = new javax.swing.JComboBox();
        lblClientServerMessage = new javax.swing.JLabel();
        cbConnections = new javax.swing.JComboBox();
        lblDefDatasource = new javax.swing.JLabel();
        btnAddDatasource = new javax.swing.JButton();
        chCacheBust = new javax.swing.JCheckBox();
        chGlobalApi = new javax.swing.JCheckBox();
        cbAutoUpdatePlatypusJs = new javax.swing.JCheckBox();
        txtSourcePath = new javax.swing.JTextField();
        lblSourcePath = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        txtRunPath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRunPathFocusLost(evt);
            }
        });
        txtRunPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRunPathActionPerformed(evt);
            }
        });

        lblRunPath.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblRunPath.text")); // NOI18N

        btnBrowse.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.btnBrowse.text")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        txtUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserNameActionPerformed(evt);
            }
        });
        txtUserName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUserNameFocusLost(evt);
            }
        });

        txtClientOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientOptionsActionPerformed(evt);
            }
        });
        txtClientOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClientOptionsFocusLost(evt);
            }
        });

        lblPassword.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblPassword.text")); // NOI18N

        lblUserName.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblUserName.text")); // NOI18N

        lblClientOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientOptions.text")); // NOI18N

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPasswordFocusLost(evt);
            }
        });

        txtClientUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClientUrlFocusLost(evt);
            }
        });
        txtClientUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientUrlActionPerformed(evt);
            }
        });

        lblClientUrl.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientUrl.text")); // NOI18N

        lblClientVmOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientVmOptions.text")); // NOI18N

        txtClientVmOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientVmOptionsActionPerformed(evt);
            }
        });
        txtClientVmOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClientVmOptionsFocusLost(evt);
            }
        });

        lblClientDebugPort.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientDebugPort.text")); // NOI18N

        spClientDebugPort.setMinimumSize(new java.awt.Dimension(36, 26));
        spClientDebugPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spClientDebugPortStateChanged(evt);
            }
        });
        spClientDebugPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spClientDebugPortFocusLost(evt);
            }
        });

        lblClientLogLevel.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientLogLevel.text")); // NOI18N

        cbClientLogLevel.setModel(new javax.swing.DefaultComboBoxModel(getLogLevels()));
        cbClientLogLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbClientLogLevelItemStateChanged(evt);
            }
        });
        cbClientLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbClientLogLevelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clientPanelLayout = new javax.swing.GroupLayout(clientPanel);
        clientPanel.setLayout(clientPanelLayout);
        clientPanelLayout.setHorizontalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblClientDebugPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblClientOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblClientVmOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblClientUrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblClientLogLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtClientOptions, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtClientUrl, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUserName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPassword)
                            .addComponent(txtClientVmOptions, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18))
                    .addGroup(clientPanelLayout.createSequentialGroup()
                        .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spClientDebugPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbClientLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(366, Short.MAX_VALUE))))
        );
        clientPanelLayout.setVerticalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClientUrl)
                    .addComponent(txtClientUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClientOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClientVmOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientVmOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbClientLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientLogLevel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblClientDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spClientDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.clientPanel.TabConstraints.tabTitle"), clientPanel); // NOI18N

        lblServerOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerOptions.text")); // NOI18N

        txtServerOptions.setToolTipText(""); // NOI18N
        txtServerOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServerOptionsActionPerformed(evt);
            }
        });
        txtServerOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServerOptionsFocusLost(evt);
            }
        });

        lblServerPort.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerPort.text")); // NOI18N

        spServerPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spServerPortStateChanged(evt);
            }
        });
        spServerPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spServerPortFocusLost(evt);
            }
        });

        lblServerVmOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerVmOptions.text")); // NOI18N

        txtServerVmOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServerVmOptionsActionPerformed(evt);
            }
        });
        txtServerVmOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServerVmOptionsFocusLost(evt);
            }
        });

        lblServerDebugPort.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerDebugPort.text")); // NOI18N

        spServerDebugPort.setMinimumSize(new java.awt.Dimension(36, 26));
        spServerDebugPort.setPreferredSize(new java.awt.Dimension(36, 26));
        spServerDebugPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spServerDebugPortStateChanged(evt);
            }
        });
        spServerDebugPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spServerDebugPortFocusLost(evt);
            }
        });

        lblServerLogLevel.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerLogLevel.text")); // NOI18N

        cbServerLogLevel.setModel(new javax.swing.DefaultComboBoxModel(getLogLevels()));
        cbServerLogLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbServerLogLevelItemStateChanged(evt);
            }
        });
        cbServerLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbServerLogLevelActionPerformed(evt);
            }
        });

        cbNotStartServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbNotStartServer.text")); // NOI18N
        cbNotStartServer.setActionCommand("");
        cbNotStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNotStartServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(cbNotStartServer)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblServerPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblServerVmOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblServerOptions, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(lblServerLogLevel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblServerDebugPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtServerOptions)
                            .addComponent(txtServerVmOptions)
                            .addGroup(serverPanelLayout.createSequentialGroup()
                                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbServerLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spServerDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 345, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServerPort)
                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerVmOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerVmOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerLogLevel)
                    .addComponent(cbServerLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spServerDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerDebugPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbNotStartServer)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.serverPanel.TabConstraints.tabTitle"), serverPanel); // NOI18N

        lblJ2eeServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblJ2eeServer.text")); // NOI18N

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

        lblContext.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblContext.text")); // NOI18N

        txtContext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContextActionPerformed(evt);
            }
        });
        txtContext.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtContextFocusLost(evt);
            }
        });

        btnManageServers.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.btnManageServers.text")); // NOI18N
        btnManageServers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageServersActionPerformed(evt);
            }
        });

        cbEnableWebSecurity.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbEnableWebSecurity.text")); // NOI18N
        cbEnableWebSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEnableWebSecurityActionPerformed(evt);
            }
        });

        cbAutoApply.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbAutoApply.text")); // NOI18N
        cbAutoApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAutoApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout j2eeServerPanelLayout = new javax.swing.GroupLayout(j2eeServerPanel);
        j2eeServerPanel.setLayout(j2eeServerPanelLayout);
        j2eeServerPanelLayout.setHorizontalGroup(
            j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, j2eeServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbEnableWebSecurity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbAutoApply, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                        .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJ2eeServer)
                            .addComponent(lblContext))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbj2eeServer, 0, 284, Short.MAX_VALUE)
                            .addComponent(txtContext))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnManageServers)
                .addGap(102, 102, 102))
        );
        j2eeServerPanelLayout.setVerticalGroup(
            j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJ2eeServer)
                    .addComponent(cbj2eeServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnManageServers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContext)
                    .addComponent(txtContext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbEnableWebSecurity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbAutoApply)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.j2eeServerPanel.TabConstraints.tabTitle"), j2eeServerPanel); // NOI18N

        cbClientType.setModel(new javax.swing.DefaultComboBoxModel(ClientType.values()));
        cbClientType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbClientTypeItemStateChanged(evt);
            }
        });

        lblClientType.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientType.text")); // NOI18N

        lblServeType.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServeType.text")); // NOI18N

        cbAppServerType.setModel(new javax.swing.DefaultComboBoxModel(AppServerType.values()));
        cbAppServerType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAppServerTypeItemStateChanged(evt);
            }
        });
        cbAppServerType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAppServerTypeActionPerformed(evt);
            }
        });

        lblClientServerMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblClientServerMessage.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientServerMessage.text")); // NOI18N

        lblDefDatasource.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblDefDatasource.text")); // NOI18N

        btnAddDatasource.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.btnAddDatasource.text")); // NOI18N
        btnAddDatasource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDatasourceActionPerformed(evt);
            }
        });

        chCacheBust.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chCacheBust.text")); // NOI18N
        chCacheBust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chCacheBustActionPerformed(evt);
            }
        });

        chGlobalApi.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chGlobalApi.text")); // NOI18N
        chGlobalApi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chGlobalApiActionPerformed(evt);
            }
        });

        cbAutoUpdatePlatypusJs.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbAutoUpdatePlatypusJs.text")); // NOI18N
        cbAutoUpdatePlatypusJs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAutoUpdatePlatypusJsActionPerformed(evt);
            }
        });

        txtSourcePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSourcePathFocusLost(evt);
            }
        });
        txtSourcePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSourcePathActionPerformed(evt);
            }
        });

        lblSourcePath.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblSourcePath.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbAutoUpdatePlatypusJs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chCacheBust, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(chGlobalApi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblServeType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblClientType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSourcePath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRunPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(lblDefDatasource, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblClientServerMessage)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtSourcePath, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbConnections, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbClientType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbAppServerType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtRunPath))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAddDatasource, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBrowse, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRunPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRunPath)
                    .addComponent(btnBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSourcePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSourcePath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbClientType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServeType)
                    .addComponent(cbAppServerType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbConnections, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDefDatasource)
                    .addComponent(btnAddDatasource))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblClientServerMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chCacheBust)
                    .addComponent(chGlobalApi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAutoUpdatePlatypusJs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtRunPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRunPathActionPerformed
        try {
            projectSettings.setRunElement(txtRunPath.getText());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_txtRunPathActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        try {
            FileObject selectedFile = null;// TODO Rework app element selector
            Set<String> allowedTypes = new HashSet<>();
            allowedTypes.add("text/javascript");//NOI18N
            FileObject newSelectedFile = FileChooser.selectAppElement(appRoot, selectedFile, allowedTypes);
            if (newSelectedFile != null && newSelectedFile != selectedFile) {
                String appElementName = IndexerQuery.file2AppElementId(newSelectedFile);
                projectSettings.setRunElement(appElementName);
                txtRunPath.setText(appElementName);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void txtRunPathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRunPathFocusLost
        try {
            projectSettings.setRunElement(txtRunPath.getText());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_txtRunPathFocusLost

    private void txtClientOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClientOptionsFocusLost
        projectSettings.setClientOptions(txtClientOptions.getText());
    }//GEN-LAST:event_txtClientOptionsFocusLost

    private void txtClientOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientOptionsActionPerformed
        projectSettings.setClientOptions(txtClientOptions.getText());
    }//GEN-LAST:event_txtClientOptionsActionPerformed

    private void txtUserNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserNameFocusLost
        projectSettings.setRunUser(txtUserName.getText());
    }//GEN-LAST:event_txtUserNameFocusLost

    private void txtUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserNameActionPerformed
        projectSettings.setRunUser(txtUserName.getText());
    }//GEN-LAST:event_txtUserNameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        projectSettings.setRunPassword(new String(txtPassword.getPassword()));
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void txtPasswordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPasswordFocusLost
        projectSettings.setRunPassword(new String(txtPassword.getPassword()));
    }//GEN-LAST:event_txtPasswordFocusLost

    private void txtClientUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientUrlActionPerformed
        projectSettings.setClientUrl(txtClientUrl.getText());
    }//GEN-LAST:event_txtClientUrlActionPerformed

    private void txtClientUrlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClientUrlFocusLost
        projectSettings.setClientUrl(txtClientUrl.getText());
    }//GEN-LAST:event_txtClientUrlFocusLost

    private void txtServerOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerOptionsActionPerformed
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsActionPerformed

    private void txtServerOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerOptionsFocusLost
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsFocusLost

    private void cbNotStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNotStartServerActionPerformed
        projectSettings.setNotStartServer(cbNotStartServer.isSelected());
        enablePlatypusClientCustomSettings();
    }//GEN-LAST:event_cbNotStartServerActionPerformed

    private void spServerPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spServerPortFocusLost
        projectSettings.setServerPort((Integer) spServerPort.getValue());
    }//GEN-LAST:event_spServerPortFocusLost

    private void spServerPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spServerPortStateChanged
        if (!isInit) {
            projectSettings.setServerPort((Integer) spServerPort.getValue());
        }
    }//GEN-LAST:event_spServerPortStateChanged

    private void cbj2eeServerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbj2eeServerItemStateChanged
        if (!isInit && evt.getStateChange() == ItemEvent.SELECTED) {
            projectSettings.setJ2eeServerId(((J2eePlatformAdapter) cbj2eeServer.getSelectedItem()).serverInstanceID);
        }
    }//GEN-LAST:event_cbj2eeServerItemStateChanged

    private void cbClientTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClientTypeItemStateChanged
        if (!isInit && evt.getStateChange() == ItemEvent.SELECTED) {
            projectSettings.setRunClientType((ClientType) cbClientType.getSelectedItem());
            if (!AppServerType.J2EE_SERVER.equals((AppServerType) cbAppServerType.getSelectedItem()) && ClientType.WEB_BROWSER.equals((ClientType) cbClientType.getSelectedItem())) {
                cbAppServerType.setSelectedItem(AppServerType.J2EE_SERVER);
            }
            checkRunClientServerConfiguration();
        }
    }//GEN-LAST:event_cbClientTypeItemStateChanged

    private void cbAppServerTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAppServerTypeItemStateChanged
        if (!isInit && evt.getStateChange() == ItemEvent.SELECTED) {
            projectSettings.setRunAppServerType((AppServerType) cbAppServerType.getSelectedItem());
            if (!AppServerType.J2EE_SERVER.equals((AppServerType) cbAppServerType.getSelectedItem()) && ClientType.WEB_BROWSER.equals((ClientType) cbClientType.getSelectedItem())) {
                cbClientType.setSelectedItem(ClientType.PLATYPUS_CLIENT);
            }
            checkRunClientServerConfiguration();
        }
    }//GEN-LAST:event_cbAppServerTypeItemStateChanged

    private void txtContextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContextActionPerformed
        projectSettings.setServerContext(txtContext.getText());
    }//GEN-LAST:event_txtContextActionPerformed

    private void txtContextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContextFocusLost
        projectSettings.setServerContext(txtContext.getText());
    }//GEN-LAST:event_txtContextFocusLost

    private void cbj2eeServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbj2eeServerActionPerformed
        Object selectedItem = cbj2eeServer.getSelectedItem();
        if (selectedItem != null) {
            projectSettings.setJ2eeServerId(((J2eePlatformAdapter) selectedItem).serverInstanceID);
        }
    }//GEN-LAST:event_cbj2eeServerActionPerformed

    private void txtClientVmOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientVmOptionsActionPerformed
        projectSettings.setClientVmOptions(txtClientVmOptions.getText());
    }//GEN-LAST:event_txtClientVmOptionsActionPerformed

    private void txtClientVmOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClientVmOptionsFocusLost
        projectSettings.setClientVmOptions(txtClientVmOptions.getText());
    }//GEN-LAST:event_txtClientVmOptionsFocusLost

    private void txtServerVmOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerVmOptionsActionPerformed
        projectSettings.setServerVmOptions(txtServerVmOptions.getText());
    }//GEN-LAST:event_txtServerVmOptionsActionPerformed

    private void txtServerVmOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerVmOptionsFocusLost
        projectSettings.setServerVmOptions(txtServerVmOptions.getText());
    }//GEN-LAST:event_txtServerVmOptionsFocusLost

    private void btnManageServersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageServersActionPerformed
        Lookup.Result<ServerInstanceProvider> result = Lookups.forPath(SERVERS_PATH).lookupResult(ServerInstanceProvider.class);
        result.allInstances().stream().forEach((provider) -> {
            provider.addChangeListener(serverRegistryListener);
        });
        CommonServerUIs.showCustomizer(null);
        result.allInstances().stream().forEach((provider) -> {
            provider.removeChangeListener(serverRegistryListener);
        });
    }//GEN-LAST:event_btnManageServersActionPerformed

    private void spClientDebugPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spClientDebugPortStateChanged
        projectSettings.setDebugClientPort((int) spClientDebugPort.getValue());
    }//GEN-LAST:event_spClientDebugPortStateChanged

    private void spClientDebugPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spClientDebugPortFocusLost
        projectSettings.setDebugClientPort((int) spClientDebugPort.getValue());
    }//GEN-LAST:event_spClientDebugPortFocusLost

    private void spServerDebugPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spServerDebugPortFocusLost
        projectSettings.setDebugServerPort((int) spServerDebugPort.getValue());
    }//GEN-LAST:event_spServerDebugPortFocusLost

    private void spServerDebugPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spServerDebugPortStateChanged
        projectSettings.setDebugServerPort((int) spServerDebugPort.getValue());
    }//GEN-LAST:event_spServerDebugPortStateChanged

    private void cbClientLogLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbClientLogLevelItemStateChanged
        projectSettings.setClientLogLevel((Level) cbClientLogLevel.getSelectedItem());
    }//GEN-LAST:event_cbClientLogLevelItemStateChanged

    private void cbServerLogLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbServerLogLevelItemStateChanged
        projectSettings.setServerLogLevel((Level) cbServerLogLevel.getSelectedItem());
    }//GEN-LAST:event_cbServerLogLevelItemStateChanged

    private void cbServerLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbServerLogLevelActionPerformed
        projectSettings.setServerLogLevel((Level) cbServerLogLevel.getSelectedItem());
    }//GEN-LAST:event_cbServerLogLevelActionPerformed

    private void cbClientLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbClientLogLevelActionPerformed
        projectSettings.setClientLogLevel((Level) cbClientLogLevel.getSelectedItem());
    }//GEN-LAST:event_cbClientLogLevelActionPerformed

    private void btnAddDatasourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDatasourceActionPerformed
        ConnectionManager.getDefault().showAddConnectionDialog(null);
        setupConnectionsModel();
    }//GEN-LAST:event_btnAddDatasourceActionPerformed

    private void cbEnableWebSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEnableWebSecurityActionPerformed
        projectSettings.setSecurityRealmEnabled(cbEnableWebSecurity.isSelected());
    }//GEN-LAST:event_cbEnableWebSecurityActionPerformed

    private void chCacheBustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chCacheBustActionPerformed
        projectSettings.setBrowserCacheBusting(chCacheBust.isSelected());
    }//GEN-LAST:event_chCacheBustActionPerformed

    private void chGlobalApiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chGlobalApiActionPerformed
        projectSettings.setGlobalAPI(chGlobalApi.isSelected());
    }//GEN-LAST:event_chGlobalApiActionPerformed

    private void cbAutoApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAutoApplyActionPerformed
        projectSettings.setAutoApplyWebSettings(cbAutoApply.isSelected());
    }//GEN-LAST:event_cbAutoApplyActionPerformed

    private void cbAutoUpdatePlatypusJsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAutoUpdatePlatypusJsActionPerformed
        projectSettings.setAutoUpdatePlatypusJs(cbAutoUpdatePlatypusJs.isSelected());
    }//GEN-LAST:event_cbAutoUpdatePlatypusJsActionPerformed

    private void cbAppServerTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAppServerTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAppServerTypeActionPerformed

    private void txtSourcePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSourcePathActionPerformed
        projectSettings.setSourcePath(txtSourcePath.getText());
    }//GEN-LAST:event_txtSourcePathActionPerformed

    private void txtSourcePathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSourcePathFocusLost
        projectSettings.setSourcePath(txtSourcePath.getText());
    }//GEN-LAST:event_txtSourcePathFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDatasource;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnManageServers;
    private javax.swing.JComboBox cbAppServerType;
    private javax.swing.JCheckBox cbAutoApply;
    private javax.swing.JCheckBox cbAutoUpdatePlatypusJs;
    private javax.swing.JComboBox cbClientLogLevel;
    private javax.swing.JComboBox cbClientType;
    private javax.swing.JComboBox cbConnections;
    private javax.swing.JCheckBox cbEnableWebSecurity;
    private javax.swing.JCheckBox cbNotStartServer;
    private javax.swing.JComboBox cbServerLogLevel;
    private javax.swing.JComboBox cbj2eeServer;
    private javax.swing.JCheckBox chCacheBust;
    private javax.swing.JCheckBox chGlobalApi;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPanel j2eeServerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblClientDebugPort;
    private javax.swing.JLabel lblClientLogLevel;
    private javax.swing.JLabel lblClientOptions;
    private javax.swing.JLabel lblClientServerMessage;
    private javax.swing.JLabel lblClientType;
    private javax.swing.JLabel lblClientUrl;
    private javax.swing.JLabel lblClientVmOptions;
    private javax.swing.JLabel lblContext;
    private javax.swing.JLabel lblDefDatasource;
    private javax.swing.JLabel lblJ2eeServer;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRunPath;
    private javax.swing.JLabel lblServeType;
    private javax.swing.JLabel lblServerDebugPort;
    private javax.swing.JLabel lblServerLogLevel;
    private javax.swing.JLabel lblServerOptions;
    private javax.swing.JLabel lblServerPort;
    private javax.swing.JLabel lblServerVmOptions;
    private javax.swing.JLabel lblSourcePath;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JSpinner spClientDebugPort;
    private javax.swing.JSpinner spServerDebugPort;
    private javax.swing.JSpinner spServerPort;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField txtClientOptions;
    private javax.swing.JTextField txtClientUrl;
    private javax.swing.JTextField txtClientVmOptions;
    private javax.swing.JTextField txtContext;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtRunPath;
    private javax.swing.JTextField txtServerOptions;
    private javax.swing.JTextField txtServerVmOptions;
    private javax.swing.JTextField txtSourcePath;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
