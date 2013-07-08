/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProjectRunningCustomizer.java
 *
 * Created on 17.03.2011, 18:07:31
 */
package com.eas.designer.explorer.project.ui;

import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.FileChooser;
import com.eas.designer.explorer.project.AppServerType;
import com.eas.designer.explorer.project.ClientType;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.designer.explorer.project.PlatypusProjectSettings;
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
    protected final PlatypusProject project;
    protected final FileObject appRoot;
    protected final PlatypusProjectSettings projectSettings;
    protected final PlatypusSettings appSettings;
    protected ComboBoxModel<ServerInstance> j2eeServersModel;
    private boolean isInit = true;
    private DefaultComboBoxModel serversModel;
    private ServerRegistryChangeListener serverRegistryLister = new ServerRegistryChangeListener();

    /**
     * Creates new form ProjectRunningCustomizer
     */
    public ProjectRunningCustomizer(PlatypusProject aProject) throws Exception {
        project = aProject;
        appRoot = aProject.getSrcRoot();
        projectSettings = aProject.getSettings();
        appSettings = projectSettings.getAppSettings();
        serversModel = new DefaultComboBoxModel(getJ2eePlatforms());
        initComponents();
        if (appSettings.getRunElement() != null) {
            txtRunPath.setText(projectSettings.getAppSettings().getRunElement());
        }
        chDbAppSources.setSelected(projectSettings.isDbAppSources());

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
        cbNotStartServer.setSelected(projectSettings.isNotStartServer());
        enablePlatypusClientCustomSettings();
        cbClientType.setSelectedItem(projectSettings.getRunClientType());
        cbAppServerType.setSelectedItem(projectSettings.getRunAppServerType());

        selectServerInstance();

        if (projectSettings.getServerContext() != null) {
            txtContext.setText(projectSettings.getServerContext());
        }
        cbEnableSecurity.setSelected(projectSettings.isWebSecurityEnabled());
        checkRunClientServerConfiguration();
        isInit = false;
//        result.addLookupListener(new LookupListener() {
//            @Override
//            public void resultChanged(LookupEvent ev) {
//                serversModel.removeAllElements();
//                for (J2eePlatformAdapter server : getJ2eePlatforms()) {
//                    serversModel.addElement(server);
//                }
//                selectServerInstance();
//            }
//        });
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
        return j2eePlatforms.toArray(new J2eePlatformAdapter[0]);
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
        lblClientServerMessage.setVisible(isValidRunClientServerConfiguration());
        if (isSecurityForceOn()) {
            cbEnableSecurity.setSelected(true);
            cbEnableSecurity.setEnabled(false);
        } else {
            cbEnableSecurity.setSelected(projectSettings.isWebSecurityEnabled());
            cbEnableSecurity.setEnabled(true);
        }
    }

    private boolean isValidRunClientServerConfiguration() {
        return ClientType.WEB_BROWSER.equals(cbClientType.getSelectedItem()) && !AppServerType.J2EE_SERVER.equals(cbAppServerType.getSelectedItem());
    }

    private boolean isSecurityForceOn() {
        return ClientType.PLATYPUS_CLIENT.equals(cbClientType.getSelectedItem()) && AppServerType.J2EE_SERVER.equals(cbAppServerType.getSelectedItem());
    }

    private void enablePlatypusClientCustomSettings() {
        txtClientUrl.setEnabled(cbNotStartServer.isSelected());
    }

    private static final class J2eePlatformAdapter {

        public static final J2eePlatformAdapter UNKNOWN_PLATFORM_ADAPRER = new J2eePlatformAdapter(null, null);
        private J2eePlatform platform;
        private String serverInstanceID;

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
        serverPanel = new javax.swing.JPanel();
        lblServerOptions = new javax.swing.JLabel();
        txtServerOptions = new javax.swing.JTextField();
        lblServerPort = new javax.swing.JLabel();
        spServerPort = new javax.swing.JSpinner();
        lblServerVmOptions = new javax.swing.JLabel();
        txtServerVmOptions = new javax.swing.JTextField();
        j2eeServerPanel = new javax.swing.JPanel();
        lblJ2eeServer = new javax.swing.JLabel();
        cbj2eeServer = new javax.swing.JComboBox();
        lblContext = new javax.swing.JLabel();
        txtContext = new javax.swing.JTextField();
        cbEnableSecurity = new javax.swing.JCheckBox();
        btnManageServers = new javax.swing.JButton();
        chDbAppSources = new javax.swing.JCheckBox();
        cbClientType = new javax.swing.JComboBox();
        lblClientType = new javax.swing.JLabel();
        lblServeType = new javax.swing.JLabel();
        cbAppServerType = new javax.swing.JComboBox();
        lblClientServerMessage = new javax.swing.JLabel();
        cbNotStartServer = new javax.swing.JCheckBox();

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

        txtRunPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRunPathActionPerformed(evt);
            }
        });
        txtRunPath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRunPathFocusLost(evt);
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

        txtClientUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientUrlActionPerformed(evt);
            }
        });
        txtClientUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClientUrlFocusLost(evt);
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

        javax.swing.GroupLayout clientPanelLayout = new javax.swing.GroupLayout(clientPanel);
        clientPanel.setLayout(clientPanelLayout);
        clientPanelLayout.setHorizontalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPassword)
                    .addComponent(lblClientOptions)
                    .addComponent(lblUserName)
                    .addComponent(lblClientUrl)
                    .addComponent(lblClientVmOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClientVmOptions)
                    .addComponent(txtClientUrl)
                    .addComponent(txtUserName)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addComponent(txtClientOptions))
                .addGap(18, 18, 18))
        );
        clientPanelLayout.setVerticalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClientUrl)
                    .addComponent(txtClientUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(94, Short.MAX_VALUE))
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

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblServerPort)
                    .addComponent(lblServerOptions)
                    .addComponent(lblServerVmOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtServerVmOptions)
                    .addComponent(txtServerOptions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 416, Short.MAX_VALUE)))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerPort)
                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerVmOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerVmOptions))
                .addContainerGap(159, Short.MAX_VALUE))
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

        cbEnableSecurity.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbEnableSecurity.text")); // NOI18N
        cbEnableSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEnableSecurityActionPerformed(evt);
            }
        });

        btnManageServers.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.btnManageServers.text")); // NOI18N
        btnManageServers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageServersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout j2eeServerPanelLayout = new javax.swing.GroupLayout(j2eeServerPanel);
        j2eeServerPanel.setLayout(j2eeServerPanelLayout);
        j2eeServerPanelLayout.setHorizontalGroup(
            j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                        .addComponent(cbEnableSecurity)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                        .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJ2eeServer)
                            .addComponent(lblContext))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(j2eeServerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtContext, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                            .addGroup(j2eeServerPanelLayout.createSequentialGroup()
                                .addComponent(cbj2eeServer, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnManageServers)
                                .addGap(0, 83, Short.MAX_VALUE)))))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEnableSecurity)
                .addContainerGap(167, Short.MAX_VALUE))
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.j2eeServerPanel.TabConstraints.tabTitle"), j2eeServerPanel); // NOI18N

        chDbAppSources.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chDbAppSources.text")); // NOI18N
        chDbAppSources.setActionCommand("");
        chDbAppSources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chDbAppSourcesActionPerformed(evt);
            }
        });

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

        lblClientServerMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblClientServerMessage.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientServerMessage.text")); // NOI18N

        cbNotStartServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbNotStartServer.text")); // NOI18N
        cbNotStartServer.setActionCommand("");
        cbNotStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNotStartServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblClientType, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblServeType)
                                    .addComponent(lblRunPath))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblClientServerMessage)
                                    .addComponent(cbClientType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtRunPath)
                                    .addComponent(cbAppServerType, 0, 305, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chDbAppSources))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbNotStartServer)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRunPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRunPath)
                    .addComponent(btnBrowse))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbClientType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServeType)
                    .addComponent(cbAppServerType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblClientServerMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chDbAppSources)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbNotStartServer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtRunPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRunPathActionPerformed
        projectSettings.getAppSettings().setRunElement(txtRunPath.getText());
    }//GEN-LAST:event_txtRunPathActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        try {
            FileObject selectedFile = null;// TODO Rework app element selector
            Set<String> allowedTypes = new HashSet<>();
            allowedTypes.add("text/javascript");//NOI18N
            FileObject newSelectedFile = FileChooser.selectFile(appRoot, selectedFile, allowedTypes);
            if (newSelectedFile != null && newSelectedFile != selectedFile) {
                String appElementId = IndexerQuery.file2AppElementId(newSelectedFile);
                projectSettings.getAppSettings().setRunElement(appElementId);
                txtRunPath.setText(appElementId);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void txtRunPathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRunPathFocusLost
        appSettings.setRunElement(txtRunPath.getText());
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

    private void chDbAppSourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chDbAppSourcesActionPerformed
        projectSettings.setDbAppSources(chDbAppSources.isSelected());
    }//GEN-LAST:event_chDbAppSourcesActionPerformed

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

    private void cbEnableSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEnableSecurityActionPerformed
        projectSettings.setSecurityRealmEnabled(cbEnableSecurity.isSelected());
    }//GEN-LAST:event_cbEnableSecurityActionPerformed

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
        for (ServerInstanceProvider provider : result.allInstances()) {
            provider.addChangeListener(serverRegistryLister);
        }
        CommonServerUIs.showCustomizer(null);
        for (ServerInstanceProvider provider : result.allInstances()) {
            provider.removeChangeListener(serverRegistryLister);
        }
    }//GEN-LAST:event_btnManageServersActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnManageServers;
    private javax.swing.JComboBox cbAppServerType;
    private javax.swing.JComboBox cbClientType;
    private javax.swing.JCheckBox cbEnableSecurity;
    private javax.swing.JCheckBox cbNotStartServer;
    private javax.swing.JComboBox cbj2eeServer;
    private javax.swing.JCheckBox chDbAppSources;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPanel j2eeServerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblClientOptions;
    private javax.swing.JLabel lblClientServerMessage;
    private javax.swing.JLabel lblClientType;
    private javax.swing.JLabel lblClientUrl;
    private javax.swing.JLabel lblClientVmOptions;
    private javax.swing.JLabel lblContext;
    private javax.swing.JLabel lblJ2eeServer;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRunPath;
    private javax.swing.JLabel lblServeType;
    private javax.swing.JLabel lblServerOptions;
    private javax.swing.JLabel lblServerPort;
    private javax.swing.JLabel lblServerVmOptions;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel serverPanel;
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
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
