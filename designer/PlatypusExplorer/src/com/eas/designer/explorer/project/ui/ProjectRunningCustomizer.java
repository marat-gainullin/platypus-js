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
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerInstance;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class ProjectRunningCustomizer extends javax.swing.JPanel {

    protected final PlatypusProject project;
    protected final FileObject appRoot;
    protected final PlatypusProjectSettings projectSettings;
    protected final PlatypusSettings appSettings;
    protected ComboBoxModel<ServerInstance> j2eeServersModel;
    private boolean isInit = true;

    /**
     * Creates new form ProjectRunningCustomizer
     */
    public ProjectRunningCustomizer(PlatypusProject aProject) throws Exception {
        initComponents();
        project = aProject;
        appRoot = aProject.getApplicationRoot();
        projectSettings = aProject.getSettings();
        appSettings = projectSettings.getAppSettings();
        if (appSettings.getRunElement() != null) {
            txtRunPath.setText(projectSettings.getAppSettings().getRunElement());
        }
        chDbAppSources.setSelected(projectSettings.isDbAppSources());
        //chAppServerMode.setSelected(projectSettings.isUseAppServer());

        if (projectSettings.getRunUser() != null) {
            txtUserName.setText(projectSettings.getRunUser());
        }
        if (projectSettings.getRunPassword() != null) {
            txtPassword.setText(projectSettings.getRunPassword());
        }
        if (projectSettings.getRunClientOptions() != null) {
            txtClientOptions.setText(projectSettings.getRunClientOptions());
        }
        if (projectSettings.getServerProtocol() != null) {
            cbProtocol.setSelectedItem(projectSettings.getServerProtocol());
        }
        if (projectSettings.getServerHost() != null) {
            txtServerHost.setText(projectSettings.getServerHost());
        }
        spServerPort.setValue(projectSettings.getServerPort());
        if (projectSettings.getRunServerOptions() != null) {
            txtServerOptions.setText(projectSettings.getRunServerOptions());
        }
        cbStartServer.setSelected(projectSettings.isStartServer());
        cbClientType.setSelectedItem(projectSettings.getRunClientType());
        cbAppServerType.setSelectedItem(projectSettings.getRunAppServerType());
        checkRunClientServerConfiguration();

        String serverInstanceId = projectSettings.getJ2eeServerId();
        if (serverInstanceId != null && !serverInstanceId.isEmpty()) {
            for (int i = 0; i < cbj2eeServer.getItemCount(); i++) {
                if (((J2eePlatformAdapter) cbj2eeServer.getItemAt(i)).getServerInstanceID().equals(serverInstanceId)) {
                    cbj2eeServer.setSelectedIndex(i);
                }
            }
        }

        if (projectSettings.getServerContext() != null) {
            txtContext.setText(projectSettings.getServerContext());
        }
        cbEnableSecurityRealm.setSelected(projectSettings.isSecurityRealmEnabled());
        isInit = false;
    }

    private J2eePlatformAdapter[] getJ2eePlatforms() {
        String[] serverInstanceIDs = Deployment.getDefault().getServerInstanceIDs();
        List<J2eePlatformAdapter> j2eePlatforms = new ArrayList<>();
        for (String serverInstance : serverInstanceIDs) {
            try {
                j2eePlatforms.add(new J2eePlatformAdapter(Deployment.getDefault().getServerInstance(serverInstance).getJ2eePlatform(), serverInstance));
            } catch (InstanceRemovedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Server instance has been removed.", ex); //NOI18N
            }
        }
        return j2eePlatforms.toArray(new J2eePlatformAdapter[0]);
    }

    private void checkRunClientServerConfiguration() {
        lblClientServerMessage.setVisible(isValidRunClientServerConfiguration());
    }

    private boolean isValidRunClientServerConfiguration() {
        return ClientType.WEB_BROWSER.equals(cbClientType.getSelectedItem()) && !AppServerType.J2EE_SERVER.equals(cbAppServerType.getSelectedItem());
    }

    private static final class J2eePlatformAdapter implements Comparable {

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
            String s = platform.getDisplayName();
            if (s == null) {
                s = ""; // NOI18N
            }
            return s;
        }

        public int compareTo(Object o) {
            J2eePlatformAdapter oa = (J2eePlatformAdapter) o;
            return toString().compareTo(oa.toString());
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
        tabbedPane1 = new javax.swing.JTabbedPane();
        clientPanel = new javax.swing.JPanel();
        txtUserName = new javax.swing.JTextField();
        txtClientOptions = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblClientOptions = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        serverPanel = new javax.swing.JPanel();
        lblHost = new javax.swing.JLabel();
        txtServerHost = new javax.swing.JTextField();
        lblServerOptions = new javax.swing.JLabel();
        txtServerOptions = new javax.swing.JTextField();
        lblProtocol = new javax.swing.JLabel();
        cbProtocol = new javax.swing.JComboBox();
        lblServerPort = new javax.swing.JLabel();
        spServerPort = new javax.swing.JSpinner();
        cbStartServer = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        lblJ2eeServer = new javax.swing.JLabel();
        cbj2eeServer = new javax.swing.JComboBox();
        lblContext = new javax.swing.JLabel();
        txtContext = new javax.swing.JTextField();
        cbEnableSecurityRealm = new javax.swing.JCheckBox();
        chDbAppSources = new javax.swing.JCheckBox();
        cbClientType = new javax.swing.JComboBox();
        lblClientType = new javax.swing.JLabel();
        lblServeType = new javax.swing.JLabel();
        cbAppServerType = new javax.swing.JComboBox();
        lblClientServerMessage = new javax.swing.JLabel();

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

        javax.swing.GroupLayout clientPanelLayout = new javax.swing.GroupLayout(clientPanel);
        clientPanel.setLayout(clientPanelLayout);
        clientPanelLayout.setHorizontalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPassword)
                    .addComponent(lblClientOptions)
                    .addComponent(lblUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUserName)
                    .addComponent(txtClientOptions)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );
        clientPanelLayout.setVerticalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
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
                .addContainerGap(176, Short.MAX_VALUE))
        );

        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.clientPanel.TabConstraints.tabTitle"), clientPanel); // NOI18N

        lblHost.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblHost.text")); // NOI18N

        txtServerHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServerHostActionPerformed(evt);
            }
        });
        txtServerHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServerHostFocusLost(evt);
            }
        });

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

        lblProtocol.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblProtocol.text")); // NOI18N

        cbProtocol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "platypus", "http", "https" }));
        cbProtocol.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbProtocolItemStateChanged(evt);
            }
        });
        cbProtocol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbProtocolFocusLost(evt);
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

        cbStartServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbStartServer.text")); // NOI18N
        cbStartServer.setActionCommand("");
        cbStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStartServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHost)
                    .addComponent(cbStartServer)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProtocol)
                            .addComponent(lblServerPort)
                            .addComponent(lblServerOptions))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtServerOptions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addComponent(txtServerHost)
                            .addGroup(serverPanelLayout.createSequentialGroup()
                                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProtocol)
                    .addComponent(cbProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHost)
                    .addComponent(txtServerHost, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerPort)
                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbStartServer)
                .addContainerGap(117, Short.MAX_VALUE))
        );

        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.serverPanel.TabConstraints.tabTitle"), serverPanel); // NOI18N

        lblJ2eeServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblJ2eeServer.text")); // NOI18N

        cbj2eeServer.setModel(new javax.swing.DefaultComboBoxModel(getJ2eePlatforms()));
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

        cbEnableSecurityRealm.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.cbEnableSecurityRealm.text")); // NOI18N
        cbEnableSecurityRealm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEnableSecurityRealmActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJ2eeServer)
                            .addComponent(lblContext))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtContext, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbj2eeServer, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbEnableSecurityRealm)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJ2eeServer)
                    .addComponent(cbj2eeServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContext)
                    .addComponent(txtContext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEnableSecurityRealm)
                .addContainerGap(183, Short.MAX_VALUE))
        );

        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane1)
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
                                .addComponent(btnBrowse))
                            .addComponent(chDbAppSources))
                        .addContainerGap())))
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
                .addComponent(tabbedPane1)
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
            allowedTypes.add("text/javascript");
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

    private void txtServerHostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerHostActionPerformed
        projectSettings.setServerHost(txtServerHost.getText());
    }//GEN-LAST:event_txtServerHostActionPerformed

    private void txtServerHostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerHostFocusLost
        projectSettings.setServerHost(txtServerHost.getText());
    }//GEN-LAST:event_txtServerHostFocusLost

    private void txtServerOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerOptionsActionPerformed
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsActionPerformed

    private void txtServerOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerOptionsFocusLost
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsFocusLost

    private void cbStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStartServerActionPerformed
        projectSettings.setStartServer(cbStartServer.isSelected());
    }//GEN-LAST:event_cbStartServerActionPerformed

    private void cbProtocolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbProtocolFocusLost
        projectSettings.setServerProtocol(String.valueOf(cbProtocol.getSelectedItem()));
    }//GEN-LAST:event_cbProtocolFocusLost

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

    private void cbProtocolItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbProtocolItemStateChanged
        if (!isInit && evt.getStateChange() == ItemEvent.SELECTED) {
            projectSettings.setServerProtocol(String.valueOf(cbProtocol.getSelectedItem()));
        }
    }//GEN-LAST:event_cbProtocolItemStateChanged

    private void txtContextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContextActionPerformed
        projectSettings.setServerContext(txtContext.getText());
    }//GEN-LAST:event_txtContextActionPerformed

    private void txtContextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContextFocusLost
        projectSettings.setServerContext(txtContext.getText());
    }//GEN-LAST:event_txtContextFocusLost

    private void cbEnableSecurityRealmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEnableSecurityRealmActionPerformed
        projectSettings.setSecurityRealmEnabled(cbEnableSecurityRealm.isSelected());
    }//GEN-LAST:event_cbEnableSecurityRealmActionPerformed

    private void cbj2eeServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbj2eeServerActionPerformed
        projectSettings.setJ2eeServerId(((J2eePlatformAdapter) cbj2eeServer.getSelectedItem()).serverInstanceID);
    }//GEN-LAST:event_cbj2eeServerActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JComboBox cbAppServerType;
    private javax.swing.JComboBox cbClientType;
    private javax.swing.JCheckBox cbEnableSecurityRealm;
    private javax.swing.JComboBox cbProtocol;
    private javax.swing.JCheckBox cbStartServer;
    private javax.swing.JComboBox cbj2eeServer;
    private javax.swing.JCheckBox chDbAppSources;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblClientOptions;
    private javax.swing.JLabel lblClientServerMessage;
    private javax.swing.JLabel lblClientType;
    private javax.swing.JLabel lblContext;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblJ2eeServer;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblProtocol;
    private javax.swing.JLabel lblRunPath;
    private javax.swing.JLabel lblServeType;
    private javax.swing.JLabel lblServerOptions;
    private javax.swing.JLabel lblServerPort;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JSpinner spServerPort;
    private javax.swing.JTabbedPane tabbedPane1;
    private javax.swing.JTextField txtClientOptions;
    private javax.swing.JTextField txtContext;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtRunPath;
    private javax.swing.JTextField txtServerHost;
    private javax.swing.JTextField txtServerOptions;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
