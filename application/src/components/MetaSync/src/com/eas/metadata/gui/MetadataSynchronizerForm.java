/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import com.eas.client.settings.DbConnectionSettings;
import com.eas.metadata.LineLogFormatter;
import com.eas.metadata.MetadataSynchronizer;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author vy
 */
public class MetadataSynchronizerForm extends javax.swing.JFrame {

    private final static String SOURCE_DATABASE_PROPERTIES_FILE = "source_databases.properies";
    private final static String DESTINATION_DATABASE_PROPERTIES_FILE = "destination_databases.properies";
    private final Map<String, String[]> srcDatabases = new HashMap<>();
    private final Map<String, String[]> destDatabases = new HashMap<>();
    public final static int DATABASE_PROPERTIES_SIZE = 4;
    public final static int DATABASE_PROPERTIES_URL_INDEX = 0;
    public final static int DATABASE_PROPERTIES_SCHEMA_INDEX = 1;
    public final static int DATABASE_PROPERTIES_USER_INDEX = 2;
    public final static int DATABASE_PROPERTIES_PASSWORD_INDEX = 3;
    public final static String DATABASE_PROPERTIES_PREFIX_NAME = "item";
    public final static String DATABASE_PROPERTIES_URL_NAME = "url";
    public final static String DATABASE_PROPERTIES_SCHEMA_NAME = "schema";
    public final static String DATABASE_PROPERTIES_USER_NAME = "user";
    public final static String DATABASE_PROPERTIES_PASSWORD_NAME = "password";
    public final static String DATABASE_PROPERTIES_NAME = "name";
    private String[] argsSrcDbSetting;
    private String[] argsDestDbSetting;
    // localized vars
    private String commandLineArgumentsName;
    private String noXmlMessage;
    private String noXmlTitle;
    private String noSourceMessage;
    private String noSourceTitle;
    private String noDestinationMessage;
    private String noDestinationTitle;
    private String saveDbSettingsErrorTitle;
    private String removeDbSettingsMessage;
    private String removeDbSettingsTitle;

    /**
     * Creates new form MetadataSynchronizerForm
     */
    public MetadataSynchronizerForm() {
        initComponents();
        initLocalizedNames();

        loadDatabasesSettings(SOURCE_DATABASE_PROPERTIES_FILE, srcDatabases, fldSrcDb);
        loadDatabasesSettings(DESTINATION_DATABASE_PROPERTIES_FILE, destDatabases, fldDestDb);
        btnSrcRemove.setEnabled(fldSrcDb.getItemCount() > 0);
        btnDestRemove.setEnabled(fldDestDb.getItemCount() > 0);

        fldLogLevel.removeAllItems();
        fldLogLevel.addItem(Level.ALL);
        fldLogLevel.addItem(Level.FINEST);
        fldLogLevel.addItem(Level.FINER);
        fldLogLevel.addItem(Level.FINE);
        fldLogLevel.addItem(Level.CONFIG);
        fldLogLevel.addItem(Level.INFO);
        fldLogLevel.addItem(Level.WARNING);
        fldLogLevel.addItem(Level.SEVERE);
        fldLogLevel.addItem(Level.OFF);
        fldLogLevel.setSelectedItem(Level.INFO);

        Rectangle screen = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(screen.width, screen.height);
        jSplitPane1.setDividerLocation(0.5d);
    }

    private void loadDatabasesSettings(String aFileName, Map<String, String[]> aDatabasesSettings, JComboBox aDatabasesNames) {
        aDatabasesNames.removeAllItems();
        aDatabasesSettings.clear();

        if (new File(aFileName).exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(aFileName));
                for (int i = 0;; i++) {
                    String startName = DATABASE_PROPERTIES_PREFIX_NAME + i + ".";
                    String databaseName = startName + DATABASE_PROPERTIES_NAME;
                    if (properties.containsKey(databaseName)) {
                        String name = properties.getProperty(databaseName);
                        String[] settings = new String[DATABASE_PROPERTIES_SIZE];
                        settings[DATABASE_PROPERTIES_URL_INDEX] = properties.getProperty(startName + DATABASE_PROPERTIES_URL_NAME, "");
                        settings[DATABASE_PROPERTIES_SCHEMA_INDEX] = properties.getProperty(startName + DATABASE_PROPERTIES_SCHEMA_NAME, "");
                        settings[DATABASE_PROPERTIES_USER_INDEX] = properties.getProperty(startName + DATABASE_PROPERTIES_USER_NAME, "");
                        settings[DATABASE_PROPERTIES_PASSWORD_INDEX] = properties.getProperty(startName + DATABASE_PROPERTIES_PASSWORD_NAME, "");
                        aDatabasesSettings.put(name, settings);
                        aDatabasesNames.addItem(name);
                    } else {
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MetadataSynchronizerForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            int itemCount = aDatabasesNames.getItemCount();
            if (itemCount > 0) {
                aDatabasesNames.setSelectedIndex(itemCount - 1);
            }
        }
    }

    private void addDatabaseSetting(String aFileName, Map<String, String[]> aDatabasesSettings, JComboBox aDatabasesNames, String aUrl, String aSchema, String aUser, String aPassword) {
        AddDatabaseSettingsForm dialog = new AddDatabaseSettingsForm(this, true, aFileName, aDatabasesSettings, aDatabasesNames, aUrl, aSchema, aUser, aPassword);
        boolean changed = dialog.isChanged();
        dialog.dispose();
        if (changed) {
            saveDatabaseSetting(aFileName, aDatabasesSettings, aDatabasesNames);
            aDatabasesNames.setSelectedIndex(aDatabasesNames.getItemCount() - 1);
        }
    }

    private void removeDatabaseSetting(String aFileName, Map<String, String[]> aDatabasesSettings, JComboBox aDatabasesNames, String aName) {
        int dialogResult = JOptionPane.showConfirmDialog(null, String.format(removeDbSettingsMessage, aName), removeDbSettingsTitle, JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            aDatabasesNames.removeItem(aName);
            aDatabasesSettings.remove(aName);
            saveDatabaseSetting(aFileName, aDatabasesSettings, aDatabasesNames);
        }
    }

    private void saveDatabaseSetting(String aFileName, Map<String, String[]> aDatabasesSettings, JComboBox aDatabasesNames) {
        Properties props = new Properties();
        int index = 0;
        for (int i = 0; i < aDatabasesNames.getItemCount(); i++) {
            String databaseName = (String) aDatabasesNames.getItemAt(i);
            if (!commandLineArgumentsName.equals(databaseName)) {
                String startName = DATABASE_PROPERTIES_PREFIX_NAME + index + ".";
                index++;
                props.setProperty(startName + DATABASE_PROPERTIES_NAME, databaseName);
                String[] settings = aDatabasesSettings.get(databaseName);
                if (settings != null) {
                    assert settings.length == DATABASE_PROPERTIES_SIZE;
                    props.setProperty(startName + DATABASE_PROPERTIES_URL_NAME, settings[DATABASE_PROPERTIES_URL_INDEX]);
                    props.setProperty(startName + DATABASE_PROPERTIES_SCHEMA_NAME, settings[DATABASE_PROPERTIES_SCHEMA_INDEX]);
                    props.setProperty(startName + DATABASE_PROPERTIES_USER_NAME, settings[DATABASE_PROPERTIES_USER_INDEX]);
                    props.setProperty(startName + DATABASE_PROPERTIES_PASSWORD_NAME, settings[DATABASE_PROPERTIES_PASSWORD_INDEX]);
                }
            }
        }
        try {
            props.store(new FileOutputStream(new File(aFileName)), "Database parameters");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), saveDbSettingsErrorTitle, JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(AddDatabaseSettingsForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initDatabaseSetting(Map<String, String[]> aDatabases, JComboBox aDatabasesList, JTextField aFldUrl, JTextField aFldSchema, JTextField aFldUser, JPasswordField aFldPassword, JButton aBtnRemove) {
        String item = (String) aDatabasesList.getSelectedItem();
        String[] setting = null;
        if (item != null && !item.isEmpty()) {
            setting = aDatabases.get(item);
        }
        aFldUrl.setText(setting != null && setting[DATABASE_PROPERTIES_URL_INDEX] != null ? setting[DATABASE_PROPERTIES_URL_INDEX] : "");
        aFldSchema.setText(setting != null && setting[DATABASE_PROPERTIES_SCHEMA_INDEX] != null ? setting[DATABASE_PROPERTIES_SCHEMA_INDEX] : "");
        aFldUser.setText(setting != null && setting[DATABASE_PROPERTIES_USER_INDEX] != null ? setting[DATABASE_PROPERTIES_USER_INDEX] : "");
        aFldPassword.setText(setting != null && setting[DATABASE_PROPERTIES_PASSWORD_INDEX] != null ? setting[DATABASE_PROPERTIES_PASSWORD_INDEX] : "");
        aBtnRemove.setEnabled(aDatabasesList.getItemCount() > 0);
        aBtnRemove.repaint();
    }

    private boolean validateDatabaseParams() {
        if (rbSrcDest.isSelected() || rbSrcXml.isSelected()) {
            if (fldSrcUrl.getText().isEmpty() || fldSrcSchema.getText().isEmpty() || fldSrcUser.getText().isEmpty() || fldSrcPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(null, noSourceMessage, noSourceTitle, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (rbSrcDest.isSelected() || rbXmlDest.isSelected()) {
            if (fldDestUrl.getText().isEmpty() || fldDestSchema.getText().isEmpty() || fldDestUser.getText().isEmpty() || fldDestPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(null, noDestinationMessage, noDestinationTitle, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (rbSrcXml.isSelected() || rbXmlDest.isSelected()) {
            if (fldXml.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, noXmlMessage, noXmlTitle, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        pnActions = new javax.swing.JPanel();
        pnTypeAction = new javax.swing.JPanel();
        rbSrcDest = new javax.swing.JRadioButton();
        rbSrcXml = new javax.swing.JRadioButton();
        rbXmlDest = new javax.swing.JRadioButton();
        btnSynchronize = new javax.swing.JButton();
        btnCompare = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        pnParameters = new javax.swing.JPanel();
        pnSrc = new javax.swing.JPanel();
        fldSrcDb = new javax.swing.JComboBox();
        lbSrcUrl = new javax.swing.JLabel();
        fldSrcUrl = new javax.swing.JTextField();
        lbSrcSchema = new javax.swing.JLabel();
        fldSrcSchema = new javax.swing.JTextField();
        lbSrcUser = new javax.swing.JLabel();
        fldSrcUser = new javax.swing.JTextField();
        lbSrcPassword = new javax.swing.JLabel();
        btnSrcRemove = new javax.swing.JButton();
        btnSrcAdd = new javax.swing.JButton();
        fldSrcPassword = new javax.swing.JPasswordField();
        pnDest = new javax.swing.JPanel();
        fldDestDb = new javax.swing.JComboBox();
        lbDestUrl = new javax.swing.JLabel();
        fldDestUrl = new javax.swing.JTextField();
        lbDestSchema = new javax.swing.JLabel();
        fldDestSchema = new javax.swing.JTextField();
        lbDestUser = new javax.swing.JLabel();
        fldDestUser = new javax.swing.JTextField();
        lbDestPassword = new javax.swing.JLabel();
        btnDestRemove = new javax.swing.JButton();
        btnDestAdd = new javax.swing.JButton();
        fldDestPassword = new javax.swing.JPasswordField();
        pnSetting = new javax.swing.JPanel();
        lbXml = new javax.swing.JLabel();
        fldXml = new javax.swing.JTextField();
        lbTables = new javax.swing.JLabel();
        fldTables = new javax.swing.JTextField();
        chkNoExecute = new javax.swing.JCheckBox();
        chkNoDrop = new javax.swing.JCheckBox();
        pnLogSetting = new javax.swing.JPanel();
        lbLogLevel = new javax.swing.JLabel();
        fldLogLevel = new javax.swing.JComboBox();
        lbSqlLog = new javax.swing.JLabel();
        fldSqlLog = new javax.swing.JTextField();
        lbErrorLog = new javax.swing.JLabel();
        fldErrorLog = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSqlsLog = new javax.swing.JTextArea();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtErrorsLog = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/metadata/gui/Bundle"); // NOI18N
        setTitle(bundle.getString("MetadataSynchronizerForm.title")); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel4.setLayout(new java.awt.BorderLayout());

        buttonGroup1.add(rbSrcDest);
        rbSrcDest.setSelected(true);
        rbSrcDest.setText(bundle.getString("MetadataSynchronizerForm.rbSrcDest.text")); // NOI18N
        pnTypeAction.add(rbSrcDest);

        buttonGroup1.add(rbSrcXml);
        rbSrcXml.setText(bundle.getString("MetadataSynchronizerForm.rbSrcXml.text")); // NOI18N
        pnTypeAction.add(rbSrcXml);

        buttonGroup1.add(rbXmlDest);
        rbXmlDest.setText(bundle.getString("MetadataSynchronizerForm.rbXmlDest.text")); // NOI18N
        pnTypeAction.add(rbXmlDest);

        pnActions.add(pnTypeAction);

        btnSynchronize.setText(bundle.getString("MetadataSynchronizerForm.btnSynchronize.text")); // NOI18N
        btnSynchronize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSynchronizeActionPerformed(evt);
            }
        });
        pnActions.add(btnSynchronize);

        btnCompare.setText(bundle.getString("MetadataSynchronizerForm.btnCompare.text")); // NOI18N
        btnCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompareActionPerformed(evt);
            }
        });
        pnActions.add(btnCompare);

        jPanel4.add(pnActions, java.awt.BorderLayout.WEST);
        jPanel4.add(jPanel6, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.GridLayout(1, 1));

        jSplitPane1.setDividerLocation(400);
        jSplitPane1.setOneTouchExpandable(true);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnSrc.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.pnSrc.border.title"))); // NOI18N

        fldSrcDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fldSrcDbActionPerformed(evt);
            }
        });

        lbSrcUrl.setText(bundle.getString("MetadataSynchronizerForm.lbSrcUrl.text")); // NOI18N

        lbSrcSchema.setText(bundle.getString("MetadataSynchronizerForm.lbSrcSchema.text")); // NOI18N

        lbSrcUser.setText(bundle.getString("MetadataSynchronizerForm.lbSrcUser.text")); // NOI18N

        lbSrcPassword.setText(bundle.getString("MetadataSynchronizerForm.lbSrcPassword.text")); // NOI18N

        btnSrcRemove.setText(bundle.getString("MetadataSynchronizerForm.btnSrcRemove.text")); // NOI18N
        btnSrcRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSrcRemoveActionPerformed(evt);
            }
        });

        btnSrcAdd.setText(bundle.getString("MetadataSynchronizerForm.btnSrcAdd.text")); // NOI18N
        btnSrcAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSrcAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnSrcLayout = new javax.swing.GroupLayout(pnSrc);
        pnSrc.setLayout(pnSrcLayout);
        pnSrcLayout.setHorizontalGroup(
            pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fldSrcDb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnSrcLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSrcAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSrcRemove))
            .addGroup(pnSrcLayout.createSequentialGroup()
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbSrcSchema)
                    .addComponent(lbSrcUrl)
                    .addComponent(lbSrcUser)
                    .addComponent(lbSrcPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldSrcUser)
                    .addComponent(fldSrcUrl)
                    .addComponent(fldSrcSchema)
                    .addComponent(fldSrcPassword)))
        );
        pnSrcLayout.setVerticalGroup(
            pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSrcLayout.createSequentialGroup()
                .addComponent(fldSrcDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSrcUrl)
                    .addComponent(fldSrcUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSrcSchema)
                    .addComponent(fldSrcSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSrcUser)
                    .addComponent(fldSrcUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSrcPassword)
                    .addComponent(fldSrcPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSrcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSrcRemove)
                    .addComponent(btnSrcAdd)))
        );

        pnDest.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.pnDest.border.title"))); // NOI18N

        fldDestDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fldDestDbActionPerformed(evt);
            }
        });

        lbDestUrl.setText(bundle.getString("MetadataSynchronizerForm.lbDestUrl.text")); // NOI18N

        lbDestSchema.setText(bundle.getString("MetadataSynchronizerForm.lbDestSchema.text")); // NOI18N

        lbDestUser.setText(bundle.getString("MetadataSynchronizerForm.lbDestUser.text")); // NOI18N

        lbDestPassword.setText(bundle.getString("MetadataSynchronizerForm.lbDestPassword.text")); // NOI18N

        btnDestRemove.setText(bundle.getString("MetadataSynchronizerForm.btnDestRemove.text")); // NOI18N
        btnDestRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDestRemoveActionPerformed(evt);
            }
        });

        btnDestAdd.setText(bundle.getString("MetadataSynchronizerForm.btnDestAdd.text")); // NOI18N
        btnDestAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDestAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnDestLayout = new javax.swing.GroupLayout(pnDest);
        pnDest.setLayout(pnDestLayout);
        pnDestLayout.setHorizontalGroup(
            pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fldDestDb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnDestLayout.createSequentialGroup()
                .addGap(0, 247, Short.MAX_VALUE)
                .addComponent(btnDestAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDestRemove))
            .addGroup(pnDestLayout.createSequentialGroup()
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDestUrl)
                    .addComponent(lbDestSchema)
                    .addComponent(lbDestUser)
                    .addComponent(lbDestPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldDestUser)
                    .addComponent(fldDestSchema)
                    .addComponent(fldDestUrl)
                    .addComponent(fldDestPassword)))
        );
        pnDestLayout.setVerticalGroup(
            pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDestLayout.createSequentialGroup()
                .addComponent(fldDestDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDestUrl)
                    .addComponent(fldDestUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDestSchema)
                    .addComponent(fldDestSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDestUser)
                    .addComponent(fldDestUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDestPassword)
                    .addComponent(fldDestPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnDestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDestRemove)
                    .addComponent(btnDestAdd)))
        );

        pnSetting.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.pnSetting.border.title"))); // NOI18N

        lbXml.setText(bundle.getString("MetadataSynchronizerForm.lbXml.text")); // NOI18N

        lbTables.setText(bundle.getString("MetadataSynchronizerForm.lbTables.text")); // NOI18N

        chkNoExecute.setSelected(true);
        chkNoExecute.setText(bundle.getString("MetadataSynchronizerForm.chkNoExecute.text")); // NOI18N

        chkNoDrop.setSelected(true);
        chkNoDrop.setText(bundle.getString("MetadataSynchronizerForm.chkNoDrop.text")); // NOI18N

        javax.swing.GroupLayout pnSettingLayout = new javax.swing.GroupLayout(pnSetting);
        pnSetting.setLayout(pnSettingLayout);
        pnSettingLayout.setHorizontalGroup(
            pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSettingLayout.createSequentialGroup()
                .addGroup(pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbXml)
                    .addComponent(lbTables))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldXml)
                    .addComponent(fldTables)))
            .addComponent(chkNoExecute, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(chkNoDrop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnSettingLayout.setVerticalGroup(
            pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSettingLayout.createSequentialGroup()
                .addGroup(pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbXml)
                    .addComponent(fldXml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTables)
                    .addComponent(fldTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkNoExecute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkNoDrop))
        );

        pnLogSetting.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.pnLogSetting.border.title"))); // NOI18N

        lbLogLevel.setText(bundle.getString("MetadataSynchronizerForm.lbLogLevel.text")); // NOI18N

        lbSqlLog.setText(bundle.getString("MetadataSynchronizerForm.lbSqlLog.text")); // NOI18N

        fldSqlLog.setText(bundle.getString("MetadataSynchronizerForm.fldSqlLog.text")); // NOI18N

        lbErrorLog.setText(bundle.getString("MetadataSynchronizerForm.lbErrorLog.text")); // NOI18N

        fldErrorLog.setText(bundle.getString("MetadataSynchronizerForm.fldErrorLog.text")); // NOI18N

        javax.swing.GroupLayout pnLogSettingLayout = new javax.swing.GroupLayout(pnLogSetting);
        pnLogSetting.setLayout(pnLogSettingLayout);
        pnLogSettingLayout.setHorizontalGroup(
            pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLogSettingLayout.createSequentialGroup()
                .addComponent(lbLogLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fldLogLevel, 0, 308, Short.MAX_VALUE))
            .addGroup(pnLogSettingLayout.createSequentialGroup()
                .addGroup(pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbSqlLog)
                    .addComponent(lbErrorLog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fldErrorLog)
                    .addComponent(fldSqlLog)))
        );
        pnLogSettingLayout.setVerticalGroup(
            pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLogSettingLayout.createSequentialGroup()
                .addGroup(pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbLogLevel)
                    .addComponent(fldLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSqlLog)
                    .addComponent(fldSqlLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLogSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbErrorLog)
                    .addComponent(fldErrorLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout pnParametersLayout = new javax.swing.GroupLayout(pnParameters);
        pnParameters.setLayout(pnParametersLayout);
        pnParametersLayout.setHorizontalGroup(
            pnParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnParametersLayout.createSequentialGroup()
                .addGroup(pnParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnLogSetting, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnDest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnSrc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnParametersLayout.setVerticalGroup(
            pnParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnParametersLayout.createSequentialGroup()
                .addComponent(pnSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnDest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnLogSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane5.setViewportView(pnParameters);

        jSplitPane1.setLeftComponent(jScrollPane5);

        jPanel9.setLayout(new java.awt.GridLayout(1, 1));

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        txtSqlsLog.setColumns(20);
        txtSqlsLog.setRows(5);
        jScrollPane1.setViewportView(txtSqlsLog);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(jPanel2);

        jSplitPane3.setDividerLocation(200);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setOneTouchExpandable(true);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        txtErrorsLog.setColumns(20);
        txtErrorsLog.setRows(5);
        jScrollPane2.setViewportView(txtErrorsLog);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane3.setLeftComponent(jPanel3);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataSynchronizerForm.jPanel5.border.title"))); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout());

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane3.setViewportView(txtLog);

        jPanel5.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jSplitPane3.setRightComponent(jPanel5);

        jSplitPane2.setRightComponent(jSplitPane3);

        jPanel9.add(jSplitPane2);

        jSplitPane1.setRightComponent(jPanel9);

        jPanel1.add(jSplitPane1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initLocalizedNames() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/metadata/gui/Bundle");
        commandLineArgumentsName = bundle.getString("MetadataSynchronizerForm.CommandLineArguments");
        noXmlMessage = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoXml.message");
        noXmlTitle = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoXml.title");
        noSourceMessage = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoDbSource.message");
        noSourceTitle = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoDbSource.title");
        noDestinationMessage = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoDbDestination.message");
        noDestinationTitle = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.NoDbDestination.title");
        saveDbSettingsErrorTitle = bundle.getString("MetadataSynchronizerForm.ErrorMessageDialog.SaveSettings.title");
        removeDbSettingsMessage = bundle.getString("MetadataSynchronizerForm.MessageDialog.RemoveDbSettings.message");
        removeDbSettingsTitle = bundle.getString("MetadataSynchronizerForm.MessageDialog.RemoveDbSettings.title");
    }

    private void btnSrcAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSrcAddActionPerformed
        addDatabaseSetting(SOURCE_DATABASE_PROPERTIES_FILE, srcDatabases, fldSrcDb, fldSrcUrl.getText(), fldSrcSchema.getText(), fldSrcUser.getText(), new String(fldSrcPassword.getPassword()));
    }//GEN-LAST:event_btnSrcAddActionPerformed

    private void fldSrcDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fldSrcDbActionPerformed
        initDatabaseSetting(srcDatabases, fldSrcDb, fldSrcUrl, fldSrcSchema, fldSrcUser, fldSrcPassword, btnSrcRemove);
    }//GEN-LAST:event_fldSrcDbActionPerformed

    private void btnSrcRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSrcRemoveActionPerformed
        removeDatabaseSetting(SOURCE_DATABASE_PROPERTIES_FILE, srcDatabases, fldSrcDb, (String) fldSrcDb.getSelectedItem());
        initDatabaseSetting(srcDatabases, fldSrcDb, fldSrcUrl, fldSrcSchema, fldSrcUser, fldSrcPassword, btnSrcRemove);
    }//GEN-LAST:event_btnSrcRemoveActionPerformed

    private void btnDestAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestAddActionPerformed
        addDatabaseSetting(DESTINATION_DATABASE_PROPERTIES_FILE, destDatabases, fldDestDb, fldDestUrl.getText(), fldDestSchema.getText(), fldDestUser.getText(), new String(fldDestPassword.getPassword()));
    }//GEN-LAST:event_btnDestAddActionPerformed

    private void fldDestDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fldDestDbActionPerformed
        initDatabaseSetting(destDatabases, fldDestDb, fldDestUrl, fldDestSchema, fldDestUser, fldDestPassword, btnDestRemove);
    }//GEN-LAST:event_fldDestDbActionPerformed

    private void btnDestRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestRemoveActionPerformed
        removeDatabaseSetting(DESTINATION_DATABASE_PROPERTIES_FILE, destDatabases, fldDestDb, (String) fldDestDb.getSelectedItem());
        initDatabaseSetting(destDatabases, fldDestDb, fldDestUrl, fldDestSchema, fldDestUser, fldDestPassword, btnDestRemove);
    }//GEN-LAST:event_btnDestRemoveActionPerformed

    private void btnSynchronizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynchronizeActionPerformed
        if (validateDatabaseParams()) {
            txtSqlsLog.setText("");
            txtErrorsLog.setText("");
            txtLog.setText("");
            btnSynchronize.setEnabled(false);
            btnCompare.setEnabled(false);
            final Level level = (Level) fldLogLevel.getSelectedItem();
            final boolean selectedSrcDest = rbSrcDest.isSelected();
            final boolean selectedSrcXml = rbSrcXml.isSelected();
            final String srcUrl = fldSrcUrl.getText();
            final String srcSchema = fldSrcSchema.getText();
            final String srcUser = fldSrcUser.getText();
            final String srcPassword = new String(fldSrcPassword.getPassword());
            final String destUrl = fldDestUrl.getText();
            final String destSchema = fldDestSchema.getText();
            final String destUser = fldDestUser.getText();
            final String destPassword = new String(fldDestPassword.getPassword());
            final String xml = fldXml.getText();
            final boolean selectedNoExecute = chkNoExecute.isSelected();
            final boolean selectedNoDrop = chkNoDrop.isSelected();
            final String tables = fldTables.getText();
            final String sqlLogFile = fldSqlLog.getText();
            final String errorLogFile = fldErrorLog.getText();
            final String logEncoding = "UTF-8";
            new Thread() {
                @Override
                public void run() {
                    String loggerName = MetadataSynchronizerForm.class.getName() + "_" + System.currentTimeMillis();
                    Logger sysLog = MetadataSynchronizer.initLogger(loggerName + "_system", level, true);
                    Logger sqlLog = MetadataSynchronizer.initLogger(loggerName + "_sql", level, false);
                    Logger errorLog = MetadataSynchronizer.initLogger(loggerName + "_error", level, false);

                    try {
                        sysLog.addHandler(new TextAreaHandler(txtLog));
                        sqlLog.addHandler(new TextAreaHandler(txtSqlsLog));
                        errorLog.addHandler(new TextAreaHandler(txtErrorsLog));
                        if (!sqlLogFile.isEmpty()) {
                            sqlLog.addHandler(MetadataSynchronizer.createFileHandler(sqlLogFile, logEncoding, new LineLogFormatter()));
                        }
                        if (!errorLogFile.isEmpty()) {
                            errorLog.addHandler(MetadataSynchronizer.createFileHandler(errorLogFile, logEncoding, new LineLogFormatter()));
                        }
                        MetadataSynchronizer mds = new MetadataSynchronizer(sysLog, sqlLog, errorLog, null);
                        if (selectedSrcDest) {
                            mds.setSourceDatabase(srcUrl, srcSchema, srcUser, srcPassword);
                            mds.setDestinationDatabase(destUrl, destSchema, destUser, destPassword);
                        } else if (selectedSrcXml) {
                            mds.setSourceDatabase(srcUrl, srcSchema, srcUser, srcPassword);
                            mds.setFileXml(xml);
                        } else {
                            mds.setFileXml(xml);
                            mds.setDestinationDatabase(destUrl, destSchema, destUser, destPassword);
                        }
                        mds.setNoExecute(selectedNoExecute);
                        mds.setNoDropTables(selectedNoDrop);
                        mds.parseTablesList(tables, ",");
                        mds.run();
                    } catch (Exception ex) {
                        Logger.getLogger(MetadataSynchronizerForm.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        MetadataSynchronizer.closeLogHandlers(sqlLog);
                        MetadataSynchronizer.closeLogHandlers(errorLog);
                        MetadataSynchronizer.closeLogHandlers(sysLog);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                btnSynchronize.setEnabled(true);
                                btnCompare.setEnabled(true);
                            }
                        });
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_btnSynchronizeActionPerformed

    private void btnCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompareActionPerformed
        if (validateDatabaseParams()) {
            final boolean selectedSrcDest = rbSrcDest.isSelected();
            final boolean selectedSrcXml = rbSrcXml.isSelected();
            final String srcUrl = fldSrcUrl.getText();
            final String srcSchema = fldSrcSchema.getText();
            final String srcUser = fldSrcUser.getText();
            final String srcPassword = new String(fldSrcPassword.getPassword());
            final String destUrl = fldDestUrl.getText();
            final String destSchema = fldDestSchema.getText();
            final String destUser = fldDestUser.getText();
            final String destPassword = new String(fldDestPassword.getPassword());
            final String xml = fldXml.getText();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MetadataCompareForm compareForm = new MetadataCompareForm();
                    if (selectedSrcDest) {
                        compareForm.setSourceDatabase(srcUrl, srcSchema, srcUser, srcPassword);
                        compareForm.setDestinationDatabase(destUrl, destSchema, destUser, destPassword);
                    } else if (selectedSrcXml) {
                        compareForm.setSourceDatabase(srcUrl, srcSchema, srcUser, srcPassword);
                        compareForm.setXmlFile(xml);
                    } else {
                        compareForm.setXmlFile(xml);
                        compareForm.setDestinationDatabase(destUrl, destSchema, destUser, destPassword);
                    }
                    compareForm.initStructure();
                    compareForm.setVisible(true);
                }
            });
        }
    }//GEN-LAST:event_btnCompareActionPerformed

    public void setSourceDatabase(String aUrl, String aSchema, String aUser, String aPassword) {
        if ((aUrl != null && !aUrl.isEmpty()) || (aSchema != null && !aSchema.isEmpty()) || (aUser != null && !aUser.isEmpty()) || (aPassword != null && !aPassword.isEmpty())) {
            argsSrcDbSetting = new String[]{aUrl, aSchema, aUser, aPassword};
            srcDatabases.put(commandLineArgumentsName, argsSrcDbSetting);
            fldSrcDb.addItem(commandLineArgumentsName);
            fldSrcDb.setSelectedItem(commandLineArgumentsName);
        }
    }

    public void setDestinationDatabase(String aUrl, String aSchema, String aUser, String aPassword) {
        if ((aUrl != null && !aUrl.isEmpty()) || (aSchema != null && !aSchema.isEmpty()) || (aUser != null && !aUser.isEmpty()) || (aPassword != null && !aPassword.isEmpty())) {
            argsDestDbSetting = new String[]{aUrl, aSchema, aUser, aPassword};
            destDatabases.put(commandLineArgumentsName, argsDestDbSetting);
            fldDestDb.addItem(commandLineArgumentsName);
            fldDestDb.setSelectedItem(commandLineArgumentsName);
        }
    }

    public void setFileXml(String aFileXml) {
        if (aFileXml != null) {
            fldXml.setText(aFileXml);
        }
    }

    public void setTablesList(String aTablesList) {
        if (aTablesList != null) {
            fldTables.setText(aTablesList);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) throws SQLException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MetadataSynchronizerForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        JFrame frame = new MetadataSynchronizerForm();
        frame.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCompare;
    private javax.swing.JButton btnDestAdd;
    private javax.swing.JButton btnDestRemove;
    private javax.swing.JButton btnSrcAdd;
    private javax.swing.JButton btnSrcRemove;
    private javax.swing.JButton btnSynchronize;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkNoDrop;
    private javax.swing.JCheckBox chkNoExecute;
    private javax.swing.JComboBox fldDestDb;
    private javax.swing.JPasswordField fldDestPassword;
    private javax.swing.JTextField fldDestSchema;
    private javax.swing.JTextField fldDestUrl;
    private javax.swing.JTextField fldDestUser;
    private javax.swing.JTextField fldErrorLog;
    private javax.swing.JComboBox fldLogLevel;
    private javax.swing.JTextField fldSqlLog;
    private javax.swing.JComboBox fldSrcDb;
    private javax.swing.JPasswordField fldSrcPassword;
    private javax.swing.JTextField fldSrcSchema;
    private javax.swing.JTextField fldSrcUrl;
    private javax.swing.JTextField fldSrcUser;
    private javax.swing.JTextField fldTables;
    private javax.swing.JTextField fldXml;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JLabel lbDestPassword;
    private javax.swing.JLabel lbDestSchema;
    private javax.swing.JLabel lbDestUrl;
    private javax.swing.JLabel lbDestUser;
    private javax.swing.JLabel lbErrorLog;
    private javax.swing.JLabel lbLogLevel;
    private javax.swing.JLabel lbSqlLog;
    private javax.swing.JLabel lbSrcPassword;
    private javax.swing.JLabel lbSrcSchema;
    private javax.swing.JLabel lbSrcUrl;
    private javax.swing.JLabel lbSrcUser;
    private javax.swing.JLabel lbTables;
    private javax.swing.JLabel lbXml;
    private javax.swing.JPanel pnActions;
    private javax.swing.JPanel pnDest;
    private javax.swing.JPanel pnLogSetting;
    private javax.swing.JPanel pnParameters;
    private javax.swing.JPanel pnSetting;
    private javax.swing.JPanel pnSrc;
    private javax.swing.JPanel pnTypeAction;
    private javax.swing.JRadioButton rbSrcDest;
    private javax.swing.JRadioButton rbSrcXml;
    private javax.swing.JRadioButton rbXmlDest;
    private javax.swing.JTextArea txtErrorsLog;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextArea txtSqlsLog;
    // End of variables declaration//GEN-END:variables
}
