/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import com.eas.client.DatabasesClient;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.changes.Change;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.metadata.DBStructure;
import com.eas.metadata.MetadataSynchronizer;
import com.eas.metadata.MetadataUtils;
import com.eas.metadata.TableStructure;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author vy
 */
public class MetadataCompareForm extends javax.swing.JFrame {

    private String srcUrl;
    private String srcUser;
    private String srcSchema;
    private String srcPassword;
    private String destUrl;
    private String destUser;
    private String destSchema;
    private String destPassword;
    private DBStructure srcDBStructure;
    private String xml;
    private String sourceLegend = "";
    private String destinationLegend = "";
    private static final String TD_SPACE = "<td style=\"width: 10\"></td>";
    private static final String FONT_BOLD = " style=\"font-weight: bold\"";
    private static final String FONT_ITALIC = " style=\"font-style: italic\"";
    private static final String FONT_BOLDITALIC = " style=\"font-weight: bold; font-style: italic\"";
    private static final String LEGEND_DATABASE_FORMAT = "<td>%s</td>" + TD_SPACE + "<td>url=</td><td" + FONT_BOLD + ">%s</td>" + TD_SPACE + "<td>schema=</td><td" + FONT_BOLD + ">%s</td>";
    private static final String LEGEND_FILE_FORMAT = "<td>%s</td>" + TD_SPACE + "<td>file=</td><td" + FONT_BOLD + ">%s</td>";
    private static final String LEGEND_FORMAT = "<html><table style=\"cellspacing=\"0\" cellpadding=\"0\"><tr>%s</tr><tr>%s</tr></table></html>";
    private static final String COLOR_FORMAT = "<font color=RED>%s</font>";
    private static final String NODE_FORMAT = "<html><table cellspacing=\"0\" cellpadding=\"0\"><tr>%s</tr><tr>%s</tr></table></html>";
    private static final String TABLE_FORMAT = "<td>%s</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>";
    private static final String EMPTYTABLE_FORMAT = "<td>%s</td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>";
    private static final String FIELD_FORMAT = "<td>%s</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>";
    private static final String EMPTYFIELD_FORMAT = "<td>%s</td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>";
    private static final String PKEY_FORMAT = "<td>%s</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>";
    private static final String EMPTYPKEY_FORMAT = "<td>%s</td><td></td>"
            + TD_SPACE + "<td></td><td></td>";
    private static final String FKEY_FORMAT = "<td>%s</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>";
    private static final String EMPTYFKEY_FORMAT = "<td>%s</td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>";
    private static final String INDEX_FORMAT = "<td>%s</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            //            + TD_SPACE + "<td>" + CLUSTERED_TITLE + ":</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>"
            + TD_SPACE + "<td>%s:</td><td>%s</td>";
    private static final String EMPTYINDEX_FORMAT = "<td>%s</td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            //            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>"
            + TD_SPACE + "<td></td><td></td>";
    private static final String ELEMENTS_FORMAT = "<html><p" + FONT_BOLDITALIC + ">%s</p></html>";
    private static final String ELEMENTNAME_FORMAT = "<html><p" + FONT_BOLD + ">%s</p></html>";
    // localized vars
    private String sourceTitle;
    private String destinationTitle;
    private String fieldsTitle;
    private String indexesTitle;
    private String pKeyTitle;
    private String fKeysTitle;
    private String descriptionTitle;
    private String nullableTitle;
    private String signedTitle;
    private String typeTitle;
    private String sizeTitle;
    private String scaleTitle;
    private String precisionTitle;
    private String refereeTitle;
    private String refereeFieldsTitle;
    private String refereeTableTitle;
    private String refereePKeyTitle;
    private String deferableTitle;
    private String deleteRuleTitle;
    private String updateRuleTitle;
    private String uniqueTitle;
    private String hashedTitle;
    private String clusteredTitle;
    private String findDialogMessage;
    private String findDialogTitle;
    private String errorConnectionTitle;
    private String sqlTableColumn1;
    private String sqlTableColumn2;
    private String sqlTableColumn3;

    /**
     * Creates new form MetadataCompareForm
     */
    public MetadataCompareForm() {
        initComponents();
        initLocalizedNames();
        pnFilterTables.setVisible(false);
        pnFindTables.setVisible(false);
        pnGroupActions.setVisible(false);
        pnTreeTools.setVisible(false);
        btnExpandMarked.setVisible(false);

        Rectangle screen = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setSize(screen.width, screen.height);

        tblSqls.setModel(new SqlsTableModel(new String[]{sqlTableColumn1, sqlTableColumn2, sqlTableColumn3}));
        tblSqls.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSqls.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = tblSqls.getSelectedRow();
                if (row >= 0 && row < tblSqls.getRowCount()) {
                    TableModel model = tblSqls.getModel();
                    if (model != null && model instanceof SqlsTableModel) {
                        SqlsTableModel sqlsModel = (SqlsTableModel) model;
                        txtSql.setText(sqlsModel.getSql(row));
                    }
                    btnSaveSql.setEnabled(false);
                }
            }
        });

        txtSql.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                btnSaveSql.setEnabled(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                btnSaveSql.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                btnSaveSql.setEnabled(true);
            }
        });
        ImageIcon equalsIcon = new ImageIcon(MetadataCompareForm.class.getResource("/icons/equals.png"));
        ImageIcon sameIcon = new ImageIcon(MetadataCompareForm.class.getResource("/icons/same.png"));
        ImageIcon notSameIcon = new ImageIcon(MetadataCompareForm.class.getResource("/icons/notsame.png"));
        ImageIcon notExistsIcon = new ImageIcon(MetadataCompareForm.class.getResource("/icons/notexists.png"));

        btnExpandEquals.setIcon(equalsIcon);
        btnExpandSame.setIcon(sameIcon);
        btnExpandNotSame.setIcon(notSameIcon);
        btnExpandNotExists.setIcon(notExistsIcon);

        MetadataTreeCellRenderer renderer = new MetadataTreeCellRenderer();
        renderer.setEqualsIcon(equalsIcon);
        renderer.setSameIcon(sameIcon);
        renderer.setNotSameIcon(notSameIcon);
        renderer.setNotExistsIcon(notExistsIcon);
        tree.setCellRenderer(renderer);
        tree.setCellEditor(new ChoicedTreeCellEditor());
        tree.setEditable(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpTablesType = new javax.swing.ButtonGroup();
        grpGroupActions = new javax.swing.ButtonGroup();
        menuTree = new javax.swing.JPopupMenu();
        itemExpand = new javax.swing.JMenuItem();
        itemCollapse = new javax.swing.JMenuItem();
        grpFindType = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pnGroupActions = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnExpandAll = new javax.swing.JButton();
        btnExpandEquals = new javax.swing.JButton();
        btnExpandSame = new javax.swing.JButton();
        btnExpandNotSame = new javax.swing.JButton();
        btnExpandNotExists = new javax.swing.JButton();
        btnExpandMarked = new javax.swing.JButton();
        pnFilterTables = new javax.swing.JPanel();
        rbShowAllTables = new javax.swing.JRadioButton();
        rbShowSourceTables = new javax.swing.JRadioButton();
        rbShowDestinationTables = new javax.swing.JRadioButton();
        rbShowSorceDestinationTables = new javax.swing.JRadioButton();
        pnFindTables = new javax.swing.JPanel();
        pnFindType = new javax.swing.JPanel();
        rbFindEqual = new javax.swing.JRadioButton();
        rbFindBeginsWith = new javax.swing.JRadioButton();
        rbFindContains = new javax.swing.JRadioButton();
        fldFindTable = new javax.swing.JTextField();
        btnFindNext = new javax.swing.JButton();
        pnButtons = new javax.swing.JPanel();
        btnFindTables = new javax.swing.JToggleButton();
        btnFilterTables = new javax.swing.JToggleButton();
        btnExpandTree = new javax.swing.JToggleButton();
        btnCollapseAll = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        lbDbLegend = new javax.swing.JLabel();
        splitTree = new javax.swing.JSplitPane();
        pnTree = new javax.swing.JPanel();
        scrollTblStructure = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        pnTreeTools = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        pnMarkTables = new javax.swing.JPanel();
        btnMarkAllTables = new javax.swing.JButton();
        btnUnmarkAllTables = new javax.swing.JButton();
        btnInvertMarkTables = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        btnSynchronyze = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        pnInfo = new javax.swing.JPanel();
        pnSqlsTable = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSqls = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        btnAllChoiceSqls = new javax.swing.JButton();
        btnAllClearSqls = new javax.swing.JButton();
        btnInvertChoiceSqls = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnExecuteSqls = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSql = new javax.swing.JTextArea();
        jPanel12 = new javax.swing.JPanel();
        btnSaveSql = new javax.swing.JButton();
        pnLogs = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/metadata/gui/Bundle"); // NOI18N
        itemExpand.setText(bundle.getString("MetadataCompareForm.itemExpand.text")); // NOI18N
        itemExpand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExpandActionPerformed(evt);
            }
        });
        menuTree.add(itemExpand);

        itemCollapse.setText(bundle.getString("MetadataCompareForm.itemCollapse.text")); // NOI18N
        itemCollapse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCollapseActionPerformed(evt);
            }
        });
        menuTree.add(itemCollapse);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("MetadataCompareForm.title")); // NOI18N

        jPanel2.setBorder(null);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(null);
        jPanel3.setLayout(new java.awt.BorderLayout());

        pnGroupActions.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.pnGroupActions.border.title"))); // NOI18N
        pnGroupActions.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnExpandAll.setText(bundle.getString("MetadataCompareForm.btnExpandAll.text")); // NOI18N
        btnExpandAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandAllActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandAll);

        btnExpandEquals.setText(bundle.getString("MetadataCompareForm.btnExpandEquals.text")); // NOI18N
        btnExpandEquals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandEqualsActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandEquals);

        btnExpandSame.setText(bundle.getString("MetadataCompareForm.btnExpandSame.text")); // NOI18N
        btnExpandSame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandSameActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandSame);

        btnExpandNotSame.setText(bundle.getString("MetadataCompareForm.btnExpandNotSame.text")); // NOI18N
        btnExpandNotSame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandNotSameActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandNotSame);

        btnExpandNotExists.setText(bundle.getString("MetadataCompareForm.btnExpandNotExists.text")); // NOI18N
        btnExpandNotExists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandNotExistsActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandNotExists);

        btnExpandMarked.setText(bundle.getString("MetadataCompareForm.btnExpandMarked.text")); // NOI18N
        btnExpandMarked.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandMarkedActionPerformed(evt);
            }
        });
        jPanel5.add(btnExpandMarked);

        pnGroupActions.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jPanel3.add(pnGroupActions, java.awt.BorderLayout.PAGE_END);

        pnFilterTables.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.pnFilterTables.border.title"))); // NOI18N
        pnFilterTables.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        grpTablesType.add(rbShowAllTables);
        rbShowAllTables.setSelected(true);
        rbShowAllTables.setText(bundle.getString("MetadataCompareForm.rbShowAllTables.text")); // NOI18N
        rbShowAllTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbShowAllTablesActionPerformed(evt);
            }
        });
        pnFilterTables.add(rbShowAllTables);

        grpTablesType.add(rbShowSourceTables);
        rbShowSourceTables.setText(bundle.getString("MetadataCompareForm.rbShowSourceTables.text")); // NOI18N
        rbShowSourceTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbShowSourceTablesActionPerformed(evt);
            }
        });
        pnFilterTables.add(rbShowSourceTables);

        grpTablesType.add(rbShowDestinationTables);
        rbShowDestinationTables.setText(bundle.getString("MetadataCompareForm.rbShowDestinationTables.text")); // NOI18N
        rbShowDestinationTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbShowDestinationTablesActionPerformed(evt);
            }
        });
        pnFilterTables.add(rbShowDestinationTables);

        grpTablesType.add(rbShowSorceDestinationTables);
        rbShowSorceDestinationTables.setText(bundle.getString("MetadataCompareForm.rbShowSorceDestinationTables.text")); // NOI18N
        rbShowSorceDestinationTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbShowSorceDestinationTablesActionPerformed(evt);
            }
        });
        pnFilterTables.add(rbShowSorceDestinationTables);

        jPanel3.add(pnFilterTables, java.awt.BorderLayout.CENTER);

        pnFindTables.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.pnFindTables.border.title"))); // NOI18N
        pnFindTables.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        grpFindType.add(rbFindEqual);
        rbFindEqual.setSelected(true);
        rbFindEqual.setText(bundle.getString("MetadataCompareForm.rbFindEqual.text")); // NOI18N
        pnFindType.add(rbFindEqual);

        grpFindType.add(rbFindBeginsWith);
        rbFindBeginsWith.setText(bundle.getString("MetadataCompareForm.rbFindBeginsWith.text")); // NOI18N
        pnFindType.add(rbFindBeginsWith);

        grpFindType.add(rbFindContains);
        rbFindContains.setText(bundle.getString("MetadataCompareForm.rbFindContains.text")); // NOI18N
        pnFindType.add(rbFindContains);

        pnFindTables.add(pnFindType);

        fldFindTable.setPreferredSize(new java.awt.Dimension(200, 23));
        pnFindTables.add(fldFindTable);

        btnFindNext.setText(bundle.getString("MetadataCompareForm.btnFindNext.text")); // NOI18N
        btnFindNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindNextActionPerformed(evt);
            }
        });
        pnFindTables.add(btnFindNext);

        jPanel3.add(pnFindTables, java.awt.BorderLayout.PAGE_START);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        pnButtons.setBorder(null);
        pnButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnFindTables.setText(bundle.getString("MetadataCompareForm.btnFindTables.text")); // NOI18N
        btnFindTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindTablesActionPerformed(evt);
            }
        });
        pnButtons.add(btnFindTables);

        btnFilterTables.setText(bundle.getString("MetadataCompareForm.btnFilterTables.text")); // NOI18N
        btnFilterTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterTablesActionPerformed(evt);
            }
        });
        pnButtons.add(btnFilterTables);

        btnExpandTree.setText(bundle.getString("MetadataCompareForm.btnExpandTree.text")); // NOI18N
        btnExpandTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpandTreeActionPerformed(evt);
            }
        });
        pnButtons.add(btnExpandTree);

        btnCollapseAll.setText(bundle.getString("MetadataCompareForm.btnCollapseAll.text")); // NOI18N
        btnCollapseAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCollapseAllActionPerformed(evt);
            }
        });
        pnButtons.add(btnCollapseAll);

        jPanel2.add(pnButtons, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBorder(null);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel8.add(lbDbLegend);

        jPanel1.add(jPanel8, java.awt.BorderLayout.NORTH);

        splitTree.setDividerLocation(600);
        splitTree.setOneTouchExpandable(true);

        pnTree.setBorder(null);
        pnTree.setLayout(new java.awt.BorderLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode(".");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setComponentPopupMenu(menuTree);
        tree.setRootVisible(false);
        scrollTblStructure.setViewportView(tree);

        pnTree.add(scrollTblStructure, java.awt.BorderLayout.CENTER);

        pnTreeTools.setLayout(new java.awt.BorderLayout());

        jPanel17.setLayout(new java.awt.BorderLayout());

        pnMarkTables.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        btnMarkAllTables.setText(bundle.getString("MetadataCompareForm.btnMarkAllTables.text")); // NOI18N
        btnMarkAllTables.setToolTipText(bundle.getString("MetadataCompareForm.btnMarkAllTables.toolTipText")); // NOI18N
        btnMarkAllTables.setFocusable(false);
        btnMarkAllTables.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMarkAllTables.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMarkAllTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkAllTablesActionPerformed(evt);
            }
        });
        pnMarkTables.add(btnMarkAllTables);

        btnUnmarkAllTables.setText(bundle.getString("MetadataCompareForm.btnUnmarkAllTables.text")); // NOI18N
        btnUnmarkAllTables.setToolTipText(bundle.getString("MetadataCompareForm.btnUnmarkAllTables.toolTipText")); // NOI18N
        btnUnmarkAllTables.setFocusable(false);
        btnUnmarkAllTables.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUnmarkAllTables.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUnmarkAllTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnmarkAllTablesActionPerformed(evt);
            }
        });
        pnMarkTables.add(btnUnmarkAllTables);

        btnInvertMarkTables.setText(bundle.getString("MetadataCompareForm.btnInvertMarkTables.text")); // NOI18N
        btnInvertMarkTables.setToolTipText(bundle.getString("MetadataCompareForm.btnInvertMarkTables.toolTipText")); // NOI18N
        btnInvertMarkTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvertMarkTablesActionPerformed(evt);
            }
        });
        pnMarkTables.add(btnInvertMarkTables);

        jPanel17.add(pnMarkTables, java.awt.BorderLayout.SOUTH);

        pnTreeTools.add(jPanel17, java.awt.BorderLayout.WEST);

        jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnSynchronyze.setText(bundle.getString("MetadataCompareForm.btnSynchronyze.text")); // NOI18N
        btnSynchronyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSynchronyzeActionPerformed(evt);
            }
        });
        jPanel16.add(btnSynchronyze);

        btnRefresh.setText(bundle.getString("MetadataCompareForm.btnRefresh.text")); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel16.add(btnRefresh);

        pnTreeTools.add(jPanel16, java.awt.BorderLayout.EAST);

        pnTree.add(pnTreeTools, java.awt.BorderLayout.NORTH);

        splitTree.setLeftComponent(pnTree);

        pnInfo.setBorder(null);
        pnInfo.setLayout(new java.awt.CardLayout());

        pnSqlsTable.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.pnSqlsTable.border.title"))); // NOI18N
        pnSqlsTable.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setOneTouchExpandable(true);

        jPanel9.setBorder(null);
        jPanel9.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(152, 102));

        tblSqls.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblSqls);

        jPanel9.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel11.setBorder(null);
        jPanel11.setLayout(new java.awt.BorderLayout());

        jPanel14.setLayout(new java.awt.BorderLayout());

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0);
        flowLayout1.setAlignOnBaseline(true);
        jPanel13.setLayout(flowLayout1);

        btnAllChoiceSqls.setText(bundle.getString("MetadataCompareForm.btnAllChoiceSqls.text")); // NOI18N
        btnAllChoiceSqls.setToolTipText(bundle.getString("MetadataCompareForm.btnAllChoiceSqls.toolTipText")); // NOI18N
        btnAllChoiceSqls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllChoiceSqlsActionPerformed(evt);
            }
        });
        jPanel13.add(btnAllChoiceSqls);

        btnAllClearSqls.setText(bundle.getString("MetadataCompareForm.btnAllClearSqls.text")); // NOI18N
        btnAllClearSqls.setToolTipText(bundle.getString("MetadataCompareForm.btnAllClearSqls.toolTipText")); // NOI18N
        btnAllClearSqls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllClearSqlsActionPerformed(evt);
            }
        });
        jPanel13.add(btnAllClearSqls);

        btnInvertChoiceSqls.setText(bundle.getString("MetadataCompareForm.btnInvertChoiceSqls.text")); // NOI18N
        btnInvertChoiceSqls.setToolTipText(bundle.getString("MetadataCompareForm.btnInvertChoiceSqls.toolTipText")); // NOI18N
        btnInvertChoiceSqls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvertChoiceSqlsActionPerformed(evt);
            }
        });
        jPanel13.add(btnInvertChoiceSqls);

        jPanel14.add(jPanel13, java.awt.BorderLayout.SOUTH);

        jPanel11.add(jPanel14, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnExecuteSqls.setText(bundle.getString("MetadataCompareForm.btnExecuteSqls.text")); // NOI18N
        btnExecuteSqls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecuteSqlsActionPerformed(evt);
            }
        });
        jPanel4.add(btnExecuteSqls);

        jPanel11.add(jPanel4, java.awt.BorderLayout.EAST);

        jPanel9.add(jPanel11, java.awt.BorderLayout.NORTH);

        jSplitPane2.setLeftComponent(jPanel9);

        jPanel10.setBorder(null);
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.jPanel6.border.title"))); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout());

        txtSql.setColumns(20);
        txtSql.setRows(5);
        jScrollPane3.setViewportView(txtSql);

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        btnSaveSql.setText(bundle.getString("MetadataCompareForm.btnSaveSql.text")); // NOI18N
        btnSaveSql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveSqlActionPerformed(evt);
            }
        });
        jPanel12.add(btnSaveSql);

        jPanel6.add(jPanel12, java.awt.BorderLayout.NORTH);

        jPanel10.add(jPanel6, java.awt.BorderLayout.CENTER);

        jSplitPane2.setRightComponent(jPanel10);

        jPanel7.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        pnSqlsTable.add(jPanel7, java.awt.BorderLayout.CENTER);

        pnInfo.add(pnSqlsTable, "sqlsCard");

        pnLogs.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MetadataCompareForm.pnLogs.border.title"))); // NOI18N
        pnLogs.setLayout(new java.awt.BorderLayout());

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane2.setViewportView(txtLog);

        pnLogs.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pnInfo.add(pnLogs, "logsCard");

        splitTree.setRightComponent(pnInfo);

        jPanel1.add(splitTree, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initLocalizedNames() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/metadata/gui/Bundle");
        sourceTitle = bundle.getString("MetadataCompareForm.DbTitles.sourceTitle");

        destinationTitle = bundle.getString("MetadataCompareForm.DbTitles.destinationTitle");
        fieldsTitle = bundle.getString("MetadataCompareForm.DbTitles.fieldsTitle");
        indexesTitle = bundle.getString("MetadataCompareForm.DbTitles.indexesTitle");
        pKeyTitle = bundle.getString("MetadataCompareForm.DbTitles.pKeyTitle");
        fKeysTitle = bundle.getString("MetadataCompareForm.DbTitles.fKeysTitle");
        descriptionTitle = bundle.getString("MetadataCompareForm.DbTitles.descriptionTitle");
        nullableTitle = bundle.getString("MetadataCompareForm.DbTitles.nullableTitle");
        signedTitle = bundle.getString("MetadataCompareForm.DbTitles.signedTitle");
        typeTitle = bundle.getString("MetadataCompareForm.DbTitles.typeTitle");
        sizeTitle = bundle.getString("MetadataCompareForm.DbTitles.sizeTitle");
        scaleTitle = bundle.getString("MetadataCompareForm.DbTitles.scaleTitle");
        precisionTitle = bundle.getString("MetadataCompareForm.DbTitles.precisionTitle");
        refereeTitle = bundle.getString("MetadataCompareForm.DbTitles.refereeTitle");
        refereeFieldsTitle = bundle.getString("MetadataCompareForm.DbTitles.refereeFieldsTitle");
        refereeTableTitle = bundle.getString("MetadataCompareForm.DbTitles.refereeTableTitle");
        refereePKeyTitle = bundle.getString("MetadataCompareForm.DbTitles.refereePKeyTitle");
        deferableTitle = bundle.getString("MetadataCompareForm.DbTitles.deferableTitle");
        deleteRuleTitle = bundle.getString("MetadataCompareForm.DbTitles.deleteRuleTitle");
        updateRuleTitle = bundle.getString("MetadataCompareForm.DbTitles.updateRuleTitle");
        uniqueTitle = bundle.getString("MetadataCompareForm.DbTitles.uniqueTitle");
        hashedTitle = bundle.getString("MetadataCompareForm.DbTitles.hashedTitle");
        clusteredTitle = bundle.getString("MetadataCompareForm.DbTitles.clusteredTitle");
        findDialogMessage = bundle.getString("MetadataCompareForm.FindDialog.message");
        findDialogTitle = bundle.getString("MetadataCompareForm.FindDialog.title");
        errorConnectionTitle = bundle.getString("MetadataCompareForm.ConnectionErrorDialog.title");
        sqlTableColumn1 = bundle.getString("MetadataCompareForm.SqlTable.columnName1");
        sqlTableColumn2 = bundle.getString("MetadataCompareForm.SqlTable.columnName2");
        sqlTableColumn3 = bundle.getString("MetadataCompareForm.SqlTable.columnName3");
    }

    private void btnFindTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindTablesActionPerformed
        pnFindTables.setVisible(btnFindTables.isSelected());
    }//GEN-LAST:event_btnFindTablesActionPerformed

    private void btnExpandTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandTreeActionPerformed
        pnGroupActions.setVisible(btnExpandTree.isSelected());
    }//GEN-LAST:event_btnExpandTreeActionPerformed

    private void btnFilterTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterTablesActionPerformed
        pnFilterTables.setVisible(btnFilterTables.isSelected());
    }//GEN-LAST:event_btnFilterTablesActionPerformed

    private void btnSynchronyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynchronyzeActionPerformed
        showCard(pnInfo, "logsCard");
        txtLog.setText("");
        txtSql.setText("");
        Set<DefaultMutableTreeNode> tablesNodes = getTablesNodes(true);
        final Set<String> tablesNames = new HashSet<>();
        for (DefaultMutableTreeNode node : tablesNodes) {
            Object userObject = node.getUserObject();
            if (userObject instanceof DbTableInfo) {
                String name = ((DbTableInfo) userObject).getName();
                tablesNames.add(name.toUpperCase());
            }
        }
        if (!tablesNames.isEmpty()) {
            new Thread() {
                @Override
                public void run() {
                    String loggerName = MetadataCompareForm.class.getName() + "_" + System.currentTimeMillis();
                    Logger sysLog = MetadataSynchronizer.initLogger(loggerName + "_system", Level.INFO, true);
                    try {
                        sysLog.addHandler(new TextAreaHandler(txtLog));
                        final MetadataSynchronizer mds = new MetadataSynchronizer(true, sysLog, null, null, null);
                        assert destUrl != null;
                        assert !destUrl.isEmpty();
                        mds.setDestinationDatabase(destUrl, destSchema, destUser, destPassword);
                        if (srcUrl != null && !srcUrl.isEmpty()) {
                            mds.setSourceDatabase(srcUrl, srcSchema, srcUser, srcPassword);
                        } else {
                            mds.setFileXml(xml);
                        }

                        mds.setNoDropTables(true);
                        mds.setNoExecute(true);
                        mds.setTablesList(tablesNames);
                        mds.run();
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                TableModel model = tblSqls.getModel();
                                if (model != null && model instanceof SqlsTableModel) {
                                    SqlsTableModel sqlsModel = (SqlsTableModel) model;
                                    sqlsModel.setSqls(mds.getSqlsList());
                                }
                                btnSaveSql.setEnabled(false);
                                tblSqls.getColumnModel().getColumn(0).setMaxWidth(100);
                                tblSqls.getColumnModel().getColumn(0).setWidth(30);
                                tblSqls.getColumnModel().getColumn(0).setPreferredWidth(30);
                                showCard(pnInfo, "sqlsCard");
                            }
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(MetadataCompareForm.class.getName()).log(Level.SEVERE, null, ex);
                        txtLog.append("\nError:" + ex.getMessage());
                    } finally {
                        MetadataSynchronizer.closeLogHandlers(sysLog);
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_btnSynchronyzeActionPerformed

    private void changeTreeFilter(FilteredTablesNode.STRUCTURE_TYPE aType) {
        assert tree != null;
        tree.clearSelection();
        TreeModel treeModel = tree.getModel();
        assert treeModel != null;
        assert treeModel instanceof FilteredTablesModel;
        FilteredTablesModel model = (FilteredTablesModel) treeModel;
        model.setShowedType(aType);
        model.reload();

    }
    private void rbShowAllTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbShowAllTablesActionPerformed
        changeTreeFilter(null);
    }//GEN-LAST:event_rbShowAllTablesActionPerformed

    private void rbShowSourceTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbShowSourceTablesActionPerformed
        changeTreeFilter(FilteredTablesNode.STRUCTURE_TYPE.SOURCE);
    }//GEN-LAST:event_rbShowSourceTablesActionPerformed

    private void rbShowDestinationTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbShowDestinationTablesActionPerformed
        changeTreeFilter(FilteredTablesNode.STRUCTURE_TYPE.DESTINATION);
    }//GEN-LAST:event_rbShowDestinationTablesActionPerformed

    private void rbShowSorceDestinationTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbShowSorceDestinationTablesActionPerformed
        changeTreeFilter(FilteredTablesNode.STRUCTURE_TYPE.BOTH);
    }//GEN-LAST:event_rbShowSorceDestinationTablesActionPerformed

    private void btnFindNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindNextActionPerformed
        TreeModel treeModel = tree.getModel();
        assert treeModel != null;
        assert treeModel instanceof FilteredTablesModel;
        FilteredTablesModel model = (FilteredTablesModel) treeModel;
        Object rootNode = model.getRoot();
        assert rootNode != null;
        assert rootNode instanceof FilteredTablesNode;
        FilteredTablesNode root = (FilteredTablesNode) rootNode;

        DefaultMutableTreeNode node;
        int childCount = root.getChildCount();
        if (childCount == 0) {
            return;
        }
        int index = -1;
        while (true) {
            TreePath treePath = tree.getSelectionPath();
            if (treePath != null && treePath.getPathCount() > 1) {
                Object[] path = treePath.getPath();
                TreePath tp = new TreePath(new Object[]{path[0], path[1]});
                node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                tree.clearSelection();
                index = root.getIndex(node);
            }
            index++;
            if (index >= childCount) {
                if (JOptionPane.showConfirmDialog(null, findDialogMessage, findDialogTitle, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    index = -1;
                    continue;
                }
                return;
            }
            node = (DefaultMutableTreeNode) root.getChildAt(index);
            Object userObject = node.getUserObject();
            if (userObject instanceof DbTableInfo) {
                DbTableInfo info = (DbTableInfo) userObject;
                String text = fldFindTable.getText().toUpperCase();
                String tableName = info.getName();
                if ((rbFindEqual.isSelected() && tableName.equalsIgnoreCase(text))
                        || (rbFindBeginsWith.isSelected() && tableName.startsWith(text))
                        || (rbFindContains.isSelected() && tableName.contains(text))) {
                    treePath = new TreePath(node.getPath());
                    tree.setSelectionPath(treePath);
                    tree.scrollPathToVisible(treePath);
                    return;
                }
            }
        }
    }//GEN-LAST:event_btnFindNextActionPerformed

    private Set<DefaultMutableTreeNode> getTablesNodes(boolean onlyChoiced) {
        assert tree != null;
        TreeModel model = tree.getModel();
        assert model != null;
        Object rootNode = model.getRoot();
        assert (rootNode != null && rootNode instanceof DefaultMutableTreeNode);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) rootNode;

        Set<DefaultMutableTreeNode> tables = new HashSet<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            TreeNode treeNode = root.getChildAt(i);
            if (treeNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
                Object userObject = node.getUserObject();
                if (userObject instanceof DbTableInfo) {
                    if (!onlyChoiced || ((DbTableInfo) userObject).isChoice()) {
                        tables.add(node);
                    }
                }
            }
        }
        return tables;
    }

    private Set<DefaultMutableTreeNode> getSelectedNodes() {
        assert tree != null;
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath treePath : selectionPaths) {
                nodes.add((DefaultMutableTreeNode) treePath.getLastPathComponent());
            }
        }
        return nodes;
    }

    private void expandTree(Set<DefaultMutableTreeNode> aNodes, DbStructureInfo.COMPARE_TYPE aType) {
        if (aNodes != null && !aNodes.isEmpty()) {
            for (DefaultMutableTreeNode node : aNodes) {
                Enumeration enumeration = ((DefaultMutableTreeNode) node).depthFirstEnumeration();
                while (enumeration.hasMoreElements()) {
                    Object nextElement = enumeration.nextElement();
                    if (nextElement != null && nextElement instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nextElement;
                        Object userInfo = nextNode.getUserObject();
                        if (userInfo != null && userInfo instanceof DbStructureInfo) {
                            DbStructureInfo info = (DbStructureInfo) userInfo;
                            if (aType == null || aType.equals(info.getCompareType())) {
                                tree.expandPath(new TreePath(nextNode.getPath()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void collapseTree(Set<DefaultMutableTreeNode> aNodes, DbStructureInfo.COMPARE_TYPE aType) {
        if (aNodes != null && !aNodes.isEmpty()) {
            for (DefaultMutableTreeNode node : aNodes) {
                Enumeration enumeration = ((DefaultMutableTreeNode) node).depthFirstEnumeration();
                while (enumeration.hasMoreElements()) {
                    Object nextElement = enumeration.nextElement();
                    if (nextElement != null && nextElement instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nextElement;
                        Object userInfo = nextNode.getUserObject();
                        if (userInfo != null && userInfo instanceof DbStructureInfo) {
                            DbStructureInfo info = (DbStructureInfo) userInfo;
                            if (aType == null || aType.equals(info.getCompareType())) {
                                tree.collapsePath(new TreePath(nextNode.getPath()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void setChoiceTables(boolean aChoice) {
        TreeModel treeModel = tree.getModel();
        assert treeModel instanceof DefaultTreeModel;
        DefaultTreeModel model = (DefaultTreeModel) treeModel;
        Set<DefaultMutableTreeNode> nodes = getTablesNodes(false);
        TreePath[] selectionPaths = tree.getSelectionPaths();
        tree.clearSelection();
        for (DefaultMutableTreeNode node : nodes) {
            Object userObject = node.getUserObject();
            if (userObject instanceof DbTableInfo) {
                DbTableInfo info = (DbTableInfo) userObject;
                info.setChoice(aChoice);
                model.nodeChanged(node);
            }
        }
        tree.setSelectionPaths(selectionPaths);
    }

    private void btnSaveSqlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveSqlActionPerformed
        assert tblSqls != null;
        TableModel model = tblSqls.getModel();
        assert model != null;
        assert model instanceof SqlsTableModel;
        SqlsTableModel sqlModel = (SqlsTableModel) model;
        List<String> sqls = sqlModel.getSqls();
        if (sqls != null) {
            sqls.set(tblSqls.getSelectedRow(), txtSql.getText().trim());
            tblSqls.repaint();
            btnSaveSql.setEnabled(false);
        }
    }//GEN-LAST:event_btnSaveSqlActionPerformed

    private void markAllSqls(boolean aChoice) {
        assert tblSqls != null;
        TableModel model = tblSqls.getModel();
        assert model != null;
        assert model instanceof SqlsTableModel;
        SqlsTableModel sqlModel = (SqlsTableModel) model;
        sqlModel.setAllChoices(aChoice);
        sqlModel.setAllResults("");
        tblSqls.repaint();
    }

    private void btnAllChoiceSqlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllChoiceSqlsActionPerformed
        markAllSqls(true);
    }//GEN-LAST:event_btnAllChoiceSqlsActionPerformed

    private void btnAllClearSqlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllClearSqlsActionPerformed
        markAllSqls(false);
    }//GEN-LAST:event_btnAllClearSqlsActionPerformed

    private void btnExecuteSqlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecuteSqlsActionPerformed
        assert tblSqls != null;
        TableModel model = tblSqls.getModel();
        assert model != null;
        assert model instanceof SqlsTableModel;
        final SqlsTableModel sqlModel = (SqlsTableModel) model;
        final int size = sqlModel.getRowCount();
        btnExecuteSqls.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                try (DatabasesClientWithResource dbResource = new DatabasesClientWithResource(new DbConnectionSettings(destUrl, destUser, destPassword))) {
                    DatabasesClient client = dbResource.getClient();
                    for (int i = 0; i < size; i++) {
                        if (sqlModel.isChoiced(i)) {
                            try {
                                SqlCompiledQuery query = new SqlCompiledQuery(client, null, sqlModel.getSql(i));
                                client.commit(Collections.singletonMap((String)null, Collections.singletonList((Change)query.prepareCommand())), null, null);
                                sqlModel.setChoice(i, false);
                                sqlModel.setResult(i, "Ok");
                                final int row = i;
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        tblSqls.setRowSelectionInterval(row, row);
                                        tblSqls.scrollRectToVisible(new Rectangle(tblSqls.getCellRect(row, 0, true)));
                                        tblSqls.repaint();
                                    }
                                });
                            } catch (Exception ex) {
                                sqlModel.setResult(i, "Error: " + ex.getMessage());
                                tblSqls.repaint();
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MetadataCompareForm.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            btnExecuteSqls.setEnabled(true);
                        }
                    });
                }
            }
        }.start();
    }//GEN-LAST:event_btnExecuteSqlsActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        initStructure();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnMarkAllTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkAllTablesActionPerformed
        setChoiceTables(true);
    }//GEN-LAST:event_btnMarkAllTablesActionPerformed

    private void btnUnmarkAllTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnmarkAllTablesActionPerformed
        setChoiceTables(false);
    }//GEN-LAST:event_btnUnmarkAllTablesActionPerformed

    private void btnExpandAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandAllActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        expandTree(nodes, null);
    }//GEN-LAST:event_btnExpandAllActionPerformed

    private void btnExpandEqualsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandEqualsActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        collapseTree(nodes, DbStructureInfo.COMPARE_TYPE.EQUAL);
        expandTree(nodes, DbStructureInfo.COMPARE_TYPE.EQUAL);
    }//GEN-LAST:event_btnExpandEqualsActionPerformed

    private void btnExpandSameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandSameActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        collapseTree(nodes, DbStructureInfo.COMPARE_TYPE.NOT_EQUAL);
        expandTree(nodes, DbStructureInfo.COMPARE_TYPE.NOT_EQUAL);
    }//GEN-LAST:event_btnExpandSameActionPerformed

    private void btnExpandNotSameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandNotSameActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        collapseTree(nodes, DbStructureInfo.COMPARE_TYPE.NOT_SAME);
        expandTree(nodes, DbStructureInfo.COMPARE_TYPE.NOT_SAME);
    }//GEN-LAST:event_btnExpandNotSameActionPerformed

    private void btnExpandNotExistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandNotExistsActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        expandTree(nodes, DbStructureInfo.COMPARE_TYPE.NOT_EXIST);
    }//GEN-LAST:event_btnExpandNotExistsActionPerformed

    private void btnCollapseAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCollapseAllActionPerformed
        Set<DefaultMutableTreeNode> nodes = new HashSet<>();
        nodes.add((DefaultMutableTreeNode) tree.getModel().getRoot());
        collapseTree(nodes, null);
    }//GEN-LAST:event_btnCollapseAllActionPerformed

    private void btnExpandMarkedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpandMarkedActionPerformed
        expandTree(getTablesNodes(true), null);
    }//GEN-LAST:event_btnExpandMarkedActionPerformed

    private void itemExpandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExpandActionPerformed
        expandTree(getSelectedNodes(), null);
    }//GEN-LAST:event_itemExpandActionPerformed

    private void itemCollapseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCollapseActionPerformed
        collapseTree(getSelectedNodes(), null);
    }//GEN-LAST:event_itemCollapseActionPerformed

    private void btnInvertMarkTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvertMarkTablesActionPerformed
        TreeModel treeModel = tree.getModel();
        assert treeModel instanceof DefaultTreeModel;
        DefaultTreeModel model = (DefaultTreeModel) treeModel;
        Set<DefaultMutableTreeNode> nodes = getTablesNodes(false);
        TreePath[] selectionPaths = tree.getSelectionPaths();
        tree.clearSelection();
        for (DefaultMutableTreeNode node : nodes) {
            Object userObject = node.getUserObject();
            if (userObject instanceof DbTableInfo) {
                DbTableInfo info = (DbTableInfo) userObject;
                info.setChoice(!info.isChoice());
                model.nodeChanged(node);
            }
        }
        tree.setSelectionPaths(selectionPaths);
    }//GEN-LAST:event_btnInvertMarkTablesActionPerformed

    private void btnInvertChoiceSqlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvertChoiceSqlsActionPerformed
        assert tblSqls != null;
        TableModel model = tblSqls.getModel();
        assert model != null;
        assert model instanceof SqlsTableModel;
        SqlsTableModel sqlModel = (SqlsTableModel) model;
        for (int i = 0; i < sqlModel.getRowCount(); i++) {
            sqlModel.setChoice(i, !sqlModel.isChoiced(i));
        }
        sqlModel.setAllResults("");
        tblSqls.repaint();
    }//GEN-LAST:event_btnInvertChoiceSqlsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAllChoiceSqls;
    private javax.swing.JButton btnAllClearSqls;
    private javax.swing.JButton btnCollapseAll;
    private javax.swing.JButton btnExecuteSqls;
    private javax.swing.JButton btnExpandAll;
    private javax.swing.JButton btnExpandEquals;
    private javax.swing.JButton btnExpandMarked;
    private javax.swing.JButton btnExpandNotExists;
    private javax.swing.JButton btnExpandNotSame;
    private javax.swing.JButton btnExpandSame;
    private javax.swing.JToggleButton btnExpandTree;
    private javax.swing.JToggleButton btnFilterTables;
    private javax.swing.JButton btnFindNext;
    private javax.swing.JToggleButton btnFindTables;
    private javax.swing.JButton btnInvertChoiceSqls;
    private javax.swing.JButton btnInvertMarkTables;
    private javax.swing.JButton btnMarkAllTables;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSaveSql;
    private javax.swing.JButton btnSynchronyze;
    private javax.swing.JButton btnUnmarkAllTables;
    private javax.swing.JTextField fldFindTable;
    private javax.swing.ButtonGroup grpFindType;
    private javax.swing.ButtonGroup grpGroupActions;
    private javax.swing.ButtonGroup grpTablesType;
    private javax.swing.JMenuItem itemCollapse;
    private javax.swing.JMenuItem itemExpand;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lbDbLegend;
    private javax.swing.JPopupMenu menuTree;
    private javax.swing.JPanel pnButtons;
    private javax.swing.JPanel pnFilterTables;
    private javax.swing.JPanel pnFindTables;
    private javax.swing.JPanel pnFindType;
    private javax.swing.JPanel pnGroupActions;
    private javax.swing.JPanel pnInfo;
    private javax.swing.JPanel pnLogs;
    private javax.swing.JPanel pnMarkTables;
    private javax.swing.JPanel pnSqlsTable;
    private javax.swing.JPanel pnTree;
    private javax.swing.JPanel pnTreeTools;
    private javax.swing.JRadioButton rbFindBeginsWith;
    private javax.swing.JRadioButton rbFindContains;
    private javax.swing.JRadioButton rbFindEqual;
    private javax.swing.JRadioButton rbShowAllTables;
    private javax.swing.JRadioButton rbShowDestinationTables;
    private javax.swing.JRadioButton rbShowSorceDestinationTables;
    private javax.swing.JRadioButton rbShowSourceTables;
    private javax.swing.JScrollPane scrollTblStructure;
    private javax.swing.JSplitPane splitTree;
    private javax.swing.JTable tblSqls;
    private javax.swing.JTree tree;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextArea txtSql;
    // End of variables declaration//GEN-END:variables

    public void setSourceDatabase(String aUrl, String aSchema, String aUser, String aPassword) {
        srcUrl = aUrl;
        srcSchema = aSchema;
        srcUser = aUser;
        srcPassword = aPassword;
    }

    public void setDestinationDatabase(String aUrl, String aSchema, String aUser, String aPassword) {
        destUrl = aUrl;
        destSchema = aSchema;
        destUser = aUser;
        destPassword = aPassword;
    }

    public void setXmlFile(String aFileName) {
        xml = aFileName;
    }

    public void initStructure() {
        txtLog.setText("");
        showCard(pnInfo, "logsCard");
        btnRefresh.setEnabled(false);
        tree.clearSelection();
        tree.setVisible(false);
        new Thread() {
            @Override
            public void run() {
                final FilteredTablesNode root = new FilteredTablesNode();
                String loggerName = MetadataCompareForm.class.getName() + "_" + System.currentTimeMillis();
                Logger sysLog = MetadataSynchronizer.initLogger(loggerName + "_system", Level.INFO, true);
                try {
                    sysLog.addHandler(new TextAreaHandler(txtLog));
                    final MetadataSynchronizer mds = new MetadataSynchronizer(true, sysLog, null, null, null);
                    if (srcDBStructure == null) {
                        if (srcUrl != null) {
                            srcDBStructure = mds.readDBStructure(srcUrl, srcSchema, srcUser, srcPassword);
                            sourceLegend = String.format(LEGEND_DATABASE_FORMAT, sourceTitle, srcUrl, srcSchema);
                        } else {
                            assert xml != null && !xml.isEmpty();
                            srcDBStructure = mds.readDBStructureFromFile(xml);
                            sourceLegend = String.format(LEGEND_FILE_FORMAT, sourceTitle, xml);
                        }
                    }
                    DBStructure destDBStructure;
                    if (destUrl != null) {
                        destDBStructure = mds.readDBStructure(destUrl, destSchema, destUser, destPassword);
                        destinationLegend = String.format(LEGEND_DATABASE_FORMAT, destinationTitle, destUrl, destSchema);
                        pnTreeTools.setVisible(true);
                        btnExpandMarked.setVisible(true);
                    } else {
                        assert xml != null && !xml.isEmpty();
                        destDBStructure = mds.readDBStructureFromFile(xml);
                        destinationLegend = String.format(LEGEND_FILE_FORMAT, destinationTitle, xml);
                    }
                    assert srcDBStructure != null;
                    assert destDBStructure != null;

                    Map<String, TableStructure> srcTables = srcDBStructure.getTablesStructure();
                    Map<String, TableStructure> destTables = destDBStructure.getTablesStructure();
                    String srcDialect = srcDBStructure.getDatabaseDialect();
                    String destDialect = destDBStructure.getDatabaseDialect();
                    boolean oneDialect = (srcDialect != null && srcDialect.equalsIgnoreCase(destDialect));
                    SortedSet<String> tablesNames = new TreeSet<>();
                    fillUpperKeys(tablesNames, srcTables);
                    fillUpperKeys(tablesNames, destTables);
                    for (String tableName : tablesNames) {
                        TableStructure srcTable = srcTables.get(tableName);
                        TableStructure destTable = destTables.get(tableName);
                        DefaultMutableTreeNode tableNode = createTableStructureNode(tableName, srcTable, destTable, oneDialect);

                        root.add(tableNode);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MetadataCompareForm.class.getName()).log(Level.SEVERE, null, ex);
                    txtLog.append("\nError:" + ex.getMessage());
                } finally {
                    MetadataSynchronizer.closeLogHandlers(sysLog);

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lbDbLegend.setText(String.format(LEGEND_FORMAT, sourceLegend, destinationLegend));
                            lbDbLegend.repaint();
                            FilteredTablesModel treeModel = new FilteredTablesModel(root, false, null);
                            tree.setModel(treeModel);
                            treeModel.nodeChanged(root);
                            tree.setVisible(true);
                            btnRefresh.setEnabled(true);
                            rbShowAllTables.setSelected(true);
                        }
                    });
                }
            }
        }.start();
    }

    private void fillUpperKeys(Set<String> aNames, Map<String, ?> aMap) {
        assert aNames != null;
        if (aMap != null) {
            for (String name : aMap.keySet()) {
                aNames.add(name.toUpperCase());
            }
        }
    }

    private DefaultMutableTreeNode createTableStructureNode(String tableName, TableStructure srcStructure, TableStructure destStructure, boolean oneDialect) {
        String srcTableName = null;
        Fields srcFields;
        Map<String, DbTableIndexSpec> srcIndexes;
        List<PrimaryKeySpec> srcPKey;
        Map<String, List<ForeignKeySpec>> srcFKeys;
        String srcDescription = null;
        int srcFldCount = 0;
        int srcIndCount = 0;
        int srcPKCount = 0;
        int srcFKCount = 0;

        if (srcStructure != null) {
            srcTableName = srcStructure.getTableName();
            srcFields = srcStructure.getTableFields();
            srcIndexes = srcStructure.getTableIndexSpecs();
            srcPKey = srcStructure.getTablePKeySpecs();
            srcFKeys = srcStructure.getTableFKeySpecs();
            srcDescription = srcStructure.getTableDescription();
            srcFldCount = srcFields.getFieldsCount();
            if (srcIndexes != null) {
                srcIndCount = srcIndexes.size();
            }
            if (srcPKey != null) {
                srcPKCount = srcPKey.size();
            }
            if (srcFKeys != null) {
                srcFKCount = srcFKeys.size();
            }
        }

        String destTableName = null;
        Fields destFields;
        Map<String, DbTableIndexSpec> destIndexes;
        List<PrimaryKeySpec> destPKey;
        Map<String, List<ForeignKeySpec>> destFKeys;
        String destDescription = "";
        int destFldCount = 0;
        int destIndCount = 0;
        int destPKCount = 0;
        int destFKCount = 0;

        if (destStructure != null) {
            destTableName = destStructure.getTableName();
            destFields = destStructure.getTableFields();
            destIndexes = destStructure.getTableIndexSpecs();
            destPKey = destStructure.getTablePKeySpecs();
            destFKeys = destStructure.getTableFKeySpecs();
            destDescription = destStructure.getTableDescription();
            destFldCount = destFields.getFieldsCount();
            if (destIndexes != null) {
                destIndCount = destIndexes.size();
            }
            if (destPKey != null) {
                destPKCount = destPKey.size();
            }
            if (destFKeys != null) {
                destFKCount = destFKeys.size();
            }
        }
        if (srcTableName == null) {
            srcTableName = "";
        }
        if (destTableName == null) {
            destTableName = "";
        }
        if (srcDescription == null) {
            srcDescription = "";
        }
        if (destDescription == null) {
            destDescription = "";
        }
        boolean equalsTableName = srcTableName.equalsIgnoreCase(destTableName);
        boolean equalsDescription = srcDescription.equals(destDescription);
        String srcRow;
        String destRow;
        if (srcStructure != null) {
            srcRow = String.format(TABLE_FORMAT, sourceTitle,
                    (destStructure == null || equalsTableName ? srcTableName : String.format(COLOR_FORMAT, srcTableName)),
                    fieldsTitle, (destStructure == null || srcFldCount == destFldCount ? "" + srcFldCount : String.format(COLOR_FORMAT, "" + srcFldCount)),
                    indexesTitle, (destStructure == null || srcIndCount == destIndCount ? "" + srcIndCount : String.format(COLOR_FORMAT, "" + srcIndCount)),
                    pKeyTitle + "(" + fieldsTitle + ")", (destStructure == null || srcPKCount == destPKCount ? "" + srcPKCount : String.format(COLOR_FORMAT, "" + srcPKCount)),
                    fKeysTitle, (destStructure == null || srcFKCount == destFKCount ? "" + srcFKCount : String.format(COLOR_FORMAT, "" + srcFKCount)),
                    descriptionTitle, (destStructure == null || equalsDescription ? srcDescription : String.format(COLOR_FORMAT, srcDescription)));
        } else {
            srcRow = String.format(EMPTYTABLE_FORMAT, sourceTitle);
        }
        if (destStructure != null) {
            destRow = String.format(TABLE_FORMAT, destinationTitle,
                    (srcStructure == null || equalsTableName ? destTableName : String.format(COLOR_FORMAT, destTableName)),
                    fieldsTitle, (srcStructure == null || srcFldCount == destFldCount ? "" + destFldCount : String.format(COLOR_FORMAT, "" + destFldCount)),
                    indexesTitle, (srcStructure == null || srcIndCount == destIndCount ? "" + destIndCount : String.format(COLOR_FORMAT, "" + destIndCount)),
                    pKeyTitle + "(" + fieldsTitle + ")", (srcStructure == null || srcPKCount == destPKCount ? "" + destPKCount : String.format(COLOR_FORMAT, "" + destPKCount)),
                    fKeysTitle, (srcStructure == null || srcFKCount == destFKCount ? "" + destFKCount : String.format(COLOR_FORMAT, "" + destFKCount)),
                    descriptionTitle, (srcStructure == null || equalsDescription ? destDescription : String.format(COLOR_FORMAT, destDescription)));
        } else {
            destRow = String.format(EMPTYTABLE_FORMAT, destinationTitle);
        }

        FilteredTablesNode.STRUCTURE_TYPE type = null;
        if (srcStructure != null) {
            if (destStructure != null) {
                type = FilteredTablesNode.STRUCTURE_TYPE.BOTH;
            } else {
                type = FilteredTablesNode.STRUCTURE_TYPE.SOURCE;
            }
        } else if (destStructure != null) {
            type = FilteredTablesNode.STRUCTURE_TYPE.DESTINATION;

        }
        FilteredTablesNode tableNode = new FilteredTablesNode(new DbTableInfo(tableName, String.format(ELEMENTNAME_FORMAT, tableName), (destUrl != null && !destUrl.isEmpty())), type);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new DbStructureInfo(String.format(NODE_FORMAT, srcRow, destRow)));

        tableNode.add(node);
        addFieldsNode(tableNode, srcStructure, destStructure, oneDialect);
        addPKeysNode(tableNode, srcStructure, destStructure, oneDialect);
        addFKeysNode(tableNode, srcStructure, destStructure, oneDialect);
        addIndexesNode(tableNode, srcStructure, destStructure, oneDialect);
        return tableNode;
    }

    private DbStructureInfo.COMPARE_TYPE addFieldsNode(DefaultMutableTreeNode parentNode, TableStructure srcStructure, TableStructure destStructure, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;

        Fields srcFields = null;
        Fields destFields = null;
        if (srcStructure != null) {
            srcFields = srcStructure.getTableFields();
        }
        if (destStructure != null) {
            destFields = destStructure.getTableFields();
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTS_FORMAT, fieldsTitle), DbStructureInfo.COMPARE_TYPE.EQUAL));

        // for all fields source
        if (srcFields != null) {
            for (int i = 1; i <= srcFields.getFieldsCount(); i++) {
                Field srcField = srcFields.get(i);
                Field destField = null;
                String upperName = srcField.getName().toUpperCase();
                if (destFields != null) {
                    assert destStructure != null;
                    destField = destFields.get(destStructure.getOriginalFieldName(upperName));
                }

                DbStructureInfo.COMPARE_TYPE fieldType = addFieldNode(node, srcField, destField, oneDialect);

                if (nodeType.ordinal() < fieldType.ordinal()) {
                    nodeType = fieldType;
                }
            }
        }
        // for all fields destination
        if (destFields != null) {
            for (int i = 1; i <= destFields.getFieldsCount(); i++) {
                Field destField = destFields.get(i);
                Field srcField = null;
                String upperName = destField.getName().toUpperCase();
                if (srcFields != null) {
                    assert srcStructure != null;
                    srcField = srcFields.get(srcStructure.getOriginalFieldName(upperName));
                }
                // only if not exists in source
                if (srcField == null) {
                    DbStructureInfo.COMPARE_TYPE fieldType = addFieldNode(node, srcField, destField, oneDialect);

                    if (nodeType.ordinal() < fieldType.ordinal()) {
                        nodeType = fieldType;
                    }
                }
            }
        }

        if (nodeType.ordinal() > DbStructureInfo.COMPARE_TYPE.EQUAL.ordinal()) {
            Object object = node.getUserObject();
            if (object instanceof DbStructureInfo) {
                ((DbStructureInfo) object).setCompareType(nodeType);
            }
        }
        if (!DbStructureInfo.COMPARE_TYPE.EQUAL.equals(nodeType)) {
            Object userObject = parentNode.getUserObject();
            if (userObject instanceof DbStructureInfo) {
                ((DbStructureInfo) userObject).setCompareType(nodeType);
            }
        }
        parentNode.add(node);
        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addFieldNode(DefaultMutableTreeNode parentNode, Field srcField, Field destField, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;
        String srcName = "";
        boolean srcPk = false;
        boolean srcFk = false;
        boolean srcNullable = false;
        DataTypeInfo srcTypeInfo = null;
        int srcSqlType = 0;
        String srcSqlTypeName = "";
        //String srcClassName = "";
        boolean srcSigned = false;
        int srcSize = 0;
        int srcScale = 0;
        int srcPrecision = 0;
        String srcDescription = "";

        if (srcField != null) {
            srcName = srcField.getName();
            srcPk = srcField.isPk();
            srcFk = srcField.isFk();
            srcNullable = srcField.isNullable();
            srcTypeInfo = srcField.getTypeInfo();
            srcSqlType = srcTypeInfo.getSqlType();
            srcSqlTypeName = srcTypeInfo.getSqlTypeName();
            //srcClassName = srcTypeInfo.getJavaClassName();
            srcSigned = srcField.isSigned();
            srcSize = srcField.getSize();
            srcScale = srcField.getScale();
            srcPrecision = srcField.getPrecision();
            srcDescription = srcField.getDescription();
            if (srcName == null) {
                srcName = "";
            }
            if (srcSqlTypeName == null) {
                srcSqlTypeName = "";
            }
            //if (srcClassName == null) {
            //    srcClassName = "";
            //}
            if (srcDescription == null) {
                srcDescription = "";
            }
        }

        String destName = "";
        boolean destPk = false;
        boolean destFk = false;
        boolean destNullable = false;
        DataTypeInfo destTypeInfo = null;
        int destSqlType = 0;
        String destSqlTypeName = "";
        //String destClassName = "";
        boolean destSigned = false;
        int destSize = 0;
        int destScale = 0;
        int destPrecision = 0;
        String destDescription = "";

        if (destField != null) {
            destName = destField.getName();
            destPk = destField.isPk();
            destFk = destField.isFk();
            destNullable = destField.isNullable();
            destTypeInfo = destField.getTypeInfo();
            destSqlType = destTypeInfo.getSqlType();
            destSqlTypeName = destTypeInfo.getSqlTypeName();
            //destClassName = destTypeInfo.getJavaClassName();
            destSigned = destField.isSigned();
            destSize = destField.getSize();
            destScale = destField.getScale();
            destPrecision = destField.getPrecision();
            destDescription = destField.getDescription();
            if (destName == null) {
                destName = "";
            }
            if (destSqlTypeName == null) {
                destSqlTypeName = "";
            }
            //if (destClassName == null) {
            //    destClassName = "";
            //}
            if (destDescription == null) {
                destDescription = "";
            }
        }
        String srcRow;
        String destRow;

        if (srcField != null) {
            if (destField != null) {

                boolean equalsName = srcName.equalsIgnoreCase(destName);
                boolean equalsPk = srcPk == destPk;
                boolean equalsFk = srcFk == destFk;
                boolean equalsNullable = srcNullable == destNullable;
                boolean equalsSqlType = srcSqlType == destSqlType;
                boolean equalsSqlTypeName = srcSqlTypeName.equals(destSqlTypeName);
                //boolean equalsClassName = srcClassName.equals(destClassName);
                boolean equalsSigned = srcSigned == destSigned;
                boolean equalsSize = srcSize == destSize;
                boolean equalsScale = srcScale == destScale;
                boolean equalsPrecision = srcPrecision == destPrecision;
                boolean equalsDescription = srcDescription.equals(destDescription);

                srcRow = String.format(FIELD_FORMAT, sourceTitle,
                        (equalsName ? srcName : String.format(COLOR_FORMAT, srcName)),
                        pKeyTitle, (equalsPk ? "" + srcPk : String.format(COLOR_FORMAT, "" + srcPk)),
                        fKeysTitle, (equalsFk ? "" + srcFk : String.format(COLOR_FORMAT, "" + srcFk)),
                        nullableTitle, (equalsNullable ? "" + srcNullable : String.format(COLOR_FORMAT, "" + srcNullable)),
                        typeTitle, (srcTypeInfo == null ? "" : "("
                        + (equalsSqlType ? "" + srcSqlType : String.format(COLOR_FORMAT, "" + srcSqlType)) + ","
                        + (equalsSqlTypeName ? srcSqlTypeName : String.format(COLOR_FORMAT, srcSqlTypeName)) + ")"),
                        //                        + (equalsClassName ? srcClassName : String.format(COLOR_FORMAT, srcClassName)) + ")"),
                        signedTitle, (equalsSigned ? "" + srcSigned : String.format(COLOR_FORMAT, "" + srcSigned)),
                        sizeTitle, (equalsSize ? "" + srcSize : String.format(COLOR_FORMAT, "" + srcSize)),
                        scaleTitle, (equalsScale ? "" + srcScale : String.format(COLOR_FORMAT, "" + srcScale)),
                        precisionTitle, (equalsPrecision ? "" + srcPrecision : String.format(COLOR_FORMAT, "" + srcPrecision)),
                        descriptionTitle, (equalsDescription ? srcDescription : String.format(COLOR_FORMAT, srcDescription)));

                destRow = String.format(FIELD_FORMAT, destinationTitle,
                        (equalsName ? destName : String.format(COLOR_FORMAT, destName)),
                        pKeyTitle, (equalsPk ? "" + destPk : String.format(COLOR_FORMAT, "" + destPk)),
                        fKeysTitle, (equalsFk ? "" + destFk : String.format(COLOR_FORMAT, "" + destFk)),
                        nullableTitle, (equalsNullable ? "" + destNullable : String.format(COLOR_FORMAT, "" + destNullable)),
                        typeTitle, (srcTypeInfo == null ? "" : "("
                        + (equalsSqlType ? "" + destSqlType : String.format(COLOR_FORMAT, "" + destSqlType)) + ","
                        + (equalsSqlTypeName ? destSqlTypeName : String.format(COLOR_FORMAT, destSqlTypeName)) + ")"),
                        //                        + (equalsClassName ? destClassName : String.format(COLOR_FORMAT, destClassName)) + ")"),
                        signedTitle, (equalsSigned ? "" + destSigned : String.format(COLOR_FORMAT, "" + destSigned)),
                        sizeTitle, (equalsSize ? "" + destSize : String.format(COLOR_FORMAT, "" + destSize)),
                        scaleTitle, (equalsScale ? "" + destScale : String.format(COLOR_FORMAT, "" + destScale)),
                        precisionTitle, (equalsPrecision ? "" + destPrecision : String.format(COLOR_FORMAT, "" + destPrecision)),
                        descriptionTitle, (equalsDescription ? destDescription : String.format(COLOR_FORMAT, destDescription)));

                if (!MetadataUtils.isSameField(srcField, destField, null, null, oneDialect)) {
                    nodeType = DbStructureInfo.COMPARE_TYPE.NOT_SAME;
                } else {
                    if (!(equalsName && equalsPk && equalsFk && equalsNullable && equalsSqlType && equalsSqlTypeName && equalsSigned && equalsSize && equalsScale && equalsPrecision && equalsDescription)) {
                        nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                    }
                }
            } else {
                srcRow = String.format(FIELD_FORMAT, sourceTitle,
                        srcName,
                        pKeyTitle, "" + srcPk,
                        fKeysTitle, "" + srcFk,
                        nullableTitle, "" + srcNullable,
                        typeTitle, (srcTypeInfo == null ? "" : "(" + srcSqlType + "," + srcSqlTypeName + ")"),
                        signedTitle, "" + srcSigned,
                        sizeTitle, "" + srcSize,
                        scaleTitle, "" + srcScale,
                        precisionTitle, "" + srcPrecision,
                        descriptionTitle, srcDescription);
                destRow = String.format(EMPTYFIELD_FORMAT, destinationTitle);

                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EXIST;
            }
        } else {
            assert destField != null;
            srcRow = String.format(EMPTYFIELD_FORMAT, sourceTitle);
            destRow = String.format(FIELD_FORMAT, destinationTitle,
                    destName,
                    pKeyTitle, "" + destPk,
                    fKeysTitle, "" + destFk,
                    nullableTitle, "" + destNullable,
                    typeTitle, (destTypeInfo == null ? "" : "(" + destSqlType + "," + destSqlTypeName + ")"),
                    signedTitle, "" + destSigned,
                    sizeTitle, "" + destSize,
                    scaleTitle, "" + destScale,
                    precisionTitle, "" + destPrecision,
                    descriptionTitle, destDescription);

            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EXIST;
        }
        DefaultMutableTreeNode fieldNameNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTNAME_FORMAT, (srcName.isEmpty() ? destName.toUpperCase() : srcName.toUpperCase())), nodeType));
        DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(NODE_FORMAT, srcRow, destRow)));
        fieldNameNode.add(fieldNode);
        parentNode.add(fieldNameNode);
        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addPKeysNode(DefaultMutableTreeNode parentNode, TableStructure srcStructure, TableStructure destStructure, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;

        List<PrimaryKeySpec> srcPKeys = null;
        List<PrimaryKeySpec> destPKeys = null;
        int srcPKeysSize = 0;
        int destPKeysSize = 0;
        String srcPkeyName = null;
        String destPkeyName = null;
        if (srcStructure != null) {
            srcPKeys = srcStructure.getTablePKeySpecs();
            if (srcPKeys != null) {
                srcPKeysSize = srcPKeys.size();
            }
            srcPkeyName = srcStructure.getPKeyCName();
        }
        if (destStructure != null) {
            destPKeys = destStructure.getTablePKeySpecs();
            if (destPKeys != null) {
                destPKeysSize = destPKeys.size();
            }
            destPkeyName = destStructure.getPKeyCName();
        }
        if (srcPKeysSize > 0 || destPKeysSize > 0) {
            if (srcPkeyName == null) {
                srcPkeyName = "";
            }
            if (destPkeyName == null) {
                destPkeyName = "";
            }
            String srcFields = "";
            String destFields = "";
            String dlm = "";
            if (srcPKeys != null) {
                for (int i = 0; i < srcPKeysSize; i++) {
                    PrimaryKeySpec srcPKey = srcPKeys.get(i);
                    assert srcPKey != null;
                    String srcField = srcPKey.getField();
                    String destField = "";
                    if (destPKeysSize > i) {
                        assert destPKeys != null;
                        PrimaryKeySpec destPKey = destPKeys.get(i);
                        assert destPKey != null;
                        destField = destPKey.getField();
                    }
                    assert srcField != null;
                    assert destField != null;
                    boolean equals = (srcPKeysSize == 0 || destPKeysSize == 0 || srcField.equalsIgnoreCase(destField));
                    srcFields += dlm + (equals ? srcField : String.format(COLOR_FORMAT, srcField));
                    destFields += (destField.isEmpty() ? "" : dlm) + (equals ? destField : String.format(COLOR_FORMAT, destField));
                    if (!equals) {
                        nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                    }
                    dlm = ", ";
                }
            }
            if (destPKeys != null) {
                for (int i = srcPKeysSize; i < destPKeysSize; i++) {
                    PrimaryKeySpec destPKey = destPKeys.get(i);
                    assert destPKey != null;
                    String destField = destPKey.getField();
                    assert destField != null;
                    destFields += dlm + (srcPKeysSize == 0 ? destField : String.format(COLOR_FORMAT, destField));
                    nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                }
            }
            String srcRow;
            String destRow;
            boolean equals = (srcPKeysSize == 0 || destPKeysSize == 0 || srcPkeyName.equalsIgnoreCase(destPkeyName));
            if (!equals) {
                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
            }
            if (srcPKeysSize > 0) {
                srcRow = String.format(PKEY_FORMAT, sourceTitle,
                        (equals ? srcPkeyName : String.format(COLOR_FORMAT, srcPkeyName)),
                        fieldsTitle, srcFields);
            } else {
                srcRow = String.format(EMPTYPKEY_FORMAT, sourceTitle);
            }
            if (destPKeysSize > 0) {
                destRow = String.format(PKEY_FORMAT, destinationTitle,
                        (equals ? destPkeyName : String.format(COLOR_FORMAT, destPkeyName)),
                        fieldsTitle, destFields);
            } else {
                destRow = String.format(EMPTYPKEY_FORMAT, destinationTitle);
            }
            if (srcPKeysSize > 0 && destPKeysSize > 0) {
                if (!MetadataUtils.isSamePKeys(srcPKeys, destPKeys, oneDialect)) {
                    nodeType = DbStructureInfo.COMPARE_TYPE.NOT_SAME;
                }
            } else {
                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EXIST;
            }
            DefaultMutableTreeNode pKeysNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTS_FORMAT, pKeyTitle), nodeType));
            DefaultMutableTreeNode pKeyNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(NODE_FORMAT, srcRow, destRow)));
            pKeysNode.add(pKeyNode);
            parentNode.add(pKeysNode);
        }
        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addFKeysNode(DefaultMutableTreeNode parentNode, TableStructure srcStructure, TableStructure destStructure, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;

        Map<String, List<ForeignKeySpec>> srcFKeys = null;
        Map<String, List<ForeignKeySpec>> destFKeys = null;
        SortedSet<String> fKeyNames = new TreeSet<>();
        if (srcStructure != null) {
            srcFKeys = srcStructure.getTableFKeySpecs();
            fillUpperKeys(fKeyNames, srcFKeys);
        }
        if (destStructure != null) {
            destFKeys = destStructure.getTableFKeySpecs();
            fillUpperKeys(fKeyNames, destFKeys);
        }
        if (!fKeyNames.isEmpty()) {
            DefaultMutableTreeNode fKeysNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTS_FORMAT, fKeysTitle), DbStructureInfo.COMPARE_TYPE.EQUAL));
            for (String fKeyName : fKeyNames) {
                List<ForeignKeySpec> srcFKey = null;
                List<ForeignKeySpec> destFKey = null;
                String srcFKeyName = "";
                String destFKeyName = "";
                if (srcFKeys != null) {
                    assert srcStructure != null;
                    srcFKeyName = srcStructure.getOriginalFKeyName(fKeyName);
                    srcFKey = srcFKeys.get(srcFKeyName);
                }
                if (destFKeys != null) {
                    assert destStructure != null;
                    destFKeyName = destStructure.getOriginalFKeyName(fKeyName);
                    destFKey = destFKeys.get(destFKeyName);
                }
                DbStructureInfo.COMPARE_TYPE fKeyType = addFKeyNode(fKeysNode, srcFKeyName, destFKeyName, srcFKey, destFKey, oneDialect);
                if (nodeType.ordinal() < fKeyType.ordinal()) {
                    nodeType = fKeyType;
                }
            }

            if (nodeType.ordinal() > DbStructureInfo.COMPARE_TYPE.EQUAL.ordinal()) {
                Object object = fKeysNode.getUserObject();
                if (object instanceof DbStructureInfo) {
                    ((DbStructureInfo) object).setCompareType(nodeType);
                }
            }
            if (!DbStructureInfo.COMPARE_TYPE.EQUAL.equals(nodeType)) {
                Object userObject = parentNode.getUserObject();
                if (userObject instanceof DbStructureInfo) {
                    ((DbStructureInfo) userObject).setCompareType(nodeType);
                }
            }
            parentNode.add(fKeysNode);
        }

        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addFKeyNode(DefaultMutableTreeNode parentNode, String srcFKeyName, String destFKeyName, List<ForeignKeySpec> srcFKey, List<ForeignKeySpec> destFKey, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;

        int srcSize = 0;
        int destSize = 0;
        if (srcFKey != null && !srcFKey.isEmpty()) {
            srcSize = srcFKey.size();
        }
        if (destFKey != null && !destFKey.isEmpty()) {
            destSize = destFKey.size();
        }

        boolean srcDeferrable = false;
        ForeignKeySpec.ForeignKeyRule srcDeleteRule = null;
        ForeignKeySpec.ForeignKeyRule srcUpdateRule = null;
        String srcFields = "";
        String srcRefereeTable = "";
        String srcRefereeCName = "";

        boolean destDeferrable = false;
        ForeignKeySpec.ForeignKeyRule destDeleteRule = null;
        ForeignKeySpec.ForeignKeyRule destUpdateRule = null;
        String destFields = "";
        String destRefereeTable = "";
        String destRefereeCName = "";
        String dlm = "";

        if (srcFKey != null) {
            for (int i = 0; i < srcSize; i++) {
                ForeignKeySpec srcKey = srcFKey.get(i);
                assert srcKey != null;
                String srcField = srcKey.getField();
                PrimaryKeySpec srcReferee = srcKey.getReferee();
                assert srcReferee != null;
                String srcRefereeField = srcReferee.getField();

                ForeignKeySpec destKey = null;
                String destField = "";
                PrimaryKeySpec destReferee = null;
                String destRefereeField = "";

                if (destSize > i) {
                    assert destFKey != null;
                    destKey = destFKey.get(i);
                    assert destKey != null;
                    destReferee = destKey.getReferee();
                    assert destReferee != null;
                    destField = destKey.getField();
                    destRefereeField = destReferee.getField();
                }
                if (i == 0) {
                    srcRefereeTable = srcReferee.getTable();
                    srcRefereeCName = srcReferee.getCName();
                    srcDeferrable = srcKey.getFkDeferrable();
                    srcDeleteRule = srcKey.getFkDeleteRule();
                    srcUpdateRule = srcKey.getFkUpdateRule();
                    if (destKey != null) {
                        assert destReferee != null;
                        destRefereeTable = destReferee.getTable();
                        destRefereeCName = destReferee.getCName();
                        destDeferrable = destKey.getFkDeferrable();
                        destDeleteRule = destKey.getFkDeleteRule();
                        destUpdateRule = destKey.getFkUpdateRule();
                    }
                }

                if (srcField == null) {
                    srcField = "";
                }
                if (srcRefereeField == null) {
                    srcRefereeField = "";
                }
                if (destField == null) {
                    destField = "";
                }
                if (destRefereeField == null) {
                    destRefereeField = "";
                }

                boolean equalsField = (srcSize == 0 || destSize == 0 || srcField.equalsIgnoreCase(destField));
                boolean equalsRefereeField = (srcSize == 0 || destSize == 0 || srcRefereeField.equalsIgnoreCase(destRefereeField));

                srcFields += dlm + (equalsField ? srcField : String.format(COLOR_FORMAT, srcField));
                srcFields += refereeTitle + (equalsRefereeField ? srcRefereeField : String.format(COLOR_FORMAT, srcRefereeField));
                destFields += (destField.isEmpty() ? "" : dlm) + (equalsField ? destField : String.format(COLOR_FORMAT, destField));
                destFields += (destRefereeField.isEmpty() ? "" : refereeTitle) + (equalsRefereeField ? destRefereeField : String.format(COLOR_FORMAT, destRefereeField));
                if (!equalsField || !equalsRefereeField) {
                    nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                }
                dlm = ", ";
            }
        }

        if (destFKey != null) {
            for (int i = srcSize; i < destSize; i++) {
                ForeignKeySpec destKey = destFKey.get(i);
                assert destKey != null;
                String destField = destKey.getField();
                PrimaryKeySpec destReferee = destKey.getReferee();
                assert destReferee != null;
                String destRefereeField = destReferee.getField();

                if (i == 0) {
                    destRefereeTable = destReferee.getTable();
                    destRefereeCName = destReferee.getCName();
                    destDeferrable = destKey.getFkDeferrable();
                    destDeleteRule = destKey.getFkDeleteRule();
                    destUpdateRule = destKey.getFkUpdateRule();
                }

                destFields += (destField.isEmpty() ? "" : dlm + (srcSize == 0 ? destField : String.format(COLOR_FORMAT, destField)));
                destFields += (destRefereeField.isEmpty() ? "" : refereeTitle + (srcSize == 0 ? destRefereeField : String.format(COLOR_FORMAT, destRefereeField)));
                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                dlm = ", ";
            }
        }

        if (srcFKeyName == null) {
            srcFKeyName = "";
        }
        if (srcRefereeTable == null) {
            srcRefereeTable = "";
        }
        if (srcRefereeCName == null) {
            srcRefereeCName = "";
        }
        String srcDelete = (srcDeleteRule == null ? "" : "" + srcDeleteRule);
        String srcUpdate = (srcUpdateRule == null ? "" : "" + srcUpdateRule);

        if (destFKeyName == null) {
            destFKeyName = "";
        }
        if (destRefereeTable == null) {
            destRefereeTable = "";
        }
        if (destRefereeCName == null) {
            destRefereeCName = "";
        }
        String destDelete = (destDeleteRule == null ? "" : "" + destDeleteRule);
        String destUpdate = (destUpdateRule == null ? "" : "" + destUpdateRule);

        boolean equalsName = (srcSize == 0 || destSize == 0 || srcFKeyName.equalsIgnoreCase(destFKeyName));
        boolean equalsRefereeTable = (srcSize == 0 || destSize == 0 || srcRefereeTable.equalsIgnoreCase(destRefereeTable));
        boolean equalsRefereeCName = (srcSize == 0 || destSize == 0 || srcRefereeCName.equalsIgnoreCase(destRefereeCName));
        boolean equalsDeferrable = (srcSize == 0 || destSize == 0 || srcDeferrable == destDeferrable);
        boolean equalsDelete = (srcSize == 0 || destSize == 0 || srcDelete.equals(destDelete));
        boolean equalsUpdate = (srcSize == 0 || destSize == 0 || srcUpdate.equals(destUpdate));

        String srcRow;
        String destRow;

        if (srcSize > 0) {
            srcRow = String.format(FKEY_FORMAT, sourceTitle,
                    (equalsName ? srcFKeyName : String.format(COLOR_FORMAT, srcFKeyName)),
                    refereeTableTitle, (equalsRefereeTable ? srcRefereeTable : String.format(COLOR_FORMAT, srcRefereeTable)),
                    fieldsTitle + refereeTitle + refereeFieldsTitle, srcFields,
                    refereePKeyTitle, (equalsRefereeCName ? srcRefereeCName : String.format(COLOR_FORMAT, srcRefereeCName)),
                    deferableTitle, (equalsDeferrable ? srcDeferrable : String.format(COLOR_FORMAT, srcDeferrable)),
                    deleteRuleTitle, (equalsDelete ? srcDelete : String.format(COLOR_FORMAT, srcDelete)),
                    updateRuleTitle, (equalsUpdate ? srcUpdate : String.format(COLOR_FORMAT, srcUpdate)));
        } else {
            srcRow = String.format(EMPTYFKEY_FORMAT, sourceTitle);
        }
        if (destSize > 0) {
            destRow = String.format(FKEY_FORMAT, destinationTitle,
                    (equalsName ? destFKeyName : String.format(COLOR_FORMAT, destFKeyName)),
                    refereeTableTitle, (equalsRefereeTable ? destRefereeTable : String.format(COLOR_FORMAT, destRefereeTable)),
                    fieldsTitle + refereeTitle + refereeFieldsTitle, destFields,
                    refereePKeyTitle, (equalsRefereeCName ? destRefereeCName : String.format(COLOR_FORMAT, destRefereeCName)),
                    deferableTitle, (equalsDeferrable ? destDeferrable : String.format(COLOR_FORMAT, destDeferrable)),
                    deleteRuleTitle, (equalsDelete ? destDelete : String.format(COLOR_FORMAT, destDelete)),
                    updateRuleTitle, (equalsUpdate ? destUpdate : String.format(COLOR_FORMAT, destUpdate)));
        } else {
            destRow = String.format(EMPTYFKEY_FORMAT, destinationTitle);
        }

        if (!equalsName || !equalsRefereeCName || !equalsRefereeTable || !equalsDeferrable || !equalsDelete || !equalsUpdate) {
            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
        }

        if (srcSize > 0 && destSize > 0) {
            if (!MetadataUtils.isSameFKeys(srcFKey, destFKey, oneDialect)) {
                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_SAME;
            }
        } else {
            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EXIST;
        }

        DefaultMutableTreeNode fKeyNameNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTNAME_FORMAT, (srcFKeyName.isEmpty() ? destFKeyName.toUpperCase() : srcFKeyName.toUpperCase())), nodeType));
        DefaultMutableTreeNode fKeyNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(NODE_FORMAT, srcRow, destRow)));
        fKeyNameNode.add(fKeyNode);
        parentNode.add(fKeyNameNode);
        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addIndexesNode(DefaultMutableTreeNode parentNode, TableStructure srcStructure, TableStructure destStructure, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;
        Map<String, DbTableIndexSpec> srcIndexes = null;
        Map<String, DbTableIndexSpec> destIndexes = null;
        SortedSet<String> indexesNames = new TreeSet<>();
        if (srcStructure != null) {
            srcIndexes = srcStructure.getTableIndexSpecs();
            fillUpperKeys(indexesNames, srcIndexes);
        }
        if (destStructure != null) {
            destIndexes = destStructure.getTableIndexSpecs();
            fillUpperKeys(indexesNames, destIndexes);
        }
        if (!indexesNames.isEmpty()) {
            DefaultMutableTreeNode indexesNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTS_FORMAT, indexesTitle), DbStructureInfo.COMPARE_TYPE.EQUAL));
            for (String indexName : indexesNames) {
                indexName = indexName.toUpperCase();
                DbTableIndexSpec srcIndex = null;
                DbTableIndexSpec destIndex = null;
                String srcIndexName = "";
                String destIndexName = "";
                if (srcIndexes != null) {
                    assert srcStructure != null;
                    srcIndexName = srcStructure.getOriginalIndexName(indexName);
                    srcIndex = srcIndexes.get(srcIndexName);
                }
                if (destIndexes != null) {
                    assert destStructure != null;
                    destIndexName = destStructure.getOriginalIndexName(indexName);
                    destIndex = destIndexes.get(destIndexName);
                }
                DbStructureInfo.COMPARE_TYPE indexType = addIndexNode(indexesNode, srcIndexName, destIndexName, srcIndex, destIndex, oneDialect);
                if (nodeType.ordinal() < indexType.ordinal()) {
                    nodeType = indexType;
                }
            }
            if (nodeType.ordinal() > DbStructureInfo.COMPARE_TYPE.EQUAL.ordinal()) {
                Object object = indexesNode.getUserObject();
                if (object instanceof DbStructureInfo) {
                    ((DbStructureInfo) object).setCompareType(nodeType);
                }
            }
            if (!DbStructureInfo.COMPARE_TYPE.EQUAL.equals(nodeType)) {
                Object userObject = parentNode.getUserObject();
                if (userObject instanceof DbStructureInfo) {
                    ((DbStructureInfo) userObject).setCompareType(nodeType);
                }
            }
            parentNode.add(indexesNode);
        }
        return nodeType;
    }

    private DbStructureInfo.COMPARE_TYPE addIndexNode(DefaultMutableTreeNode parentNode, String srcIndexName, String destIndexName, DbTableIndexSpec srcIndex, DbTableIndexSpec destIndex, boolean oneDialect) {
        DbStructureInfo.COMPARE_TYPE nodeType = DbStructureInfo.COMPARE_TYPE.EQUAL;
        String srcFKeyName = "";
        List<DbTableIndexColumnSpec> srcColumns = null;
        boolean srcPKey = false;
        boolean srcUnique = false;
        boolean srcHashed = false;
        boolean srcClustered = false;
        int srcColumnCount = 0;
        String srcFields = "";
        String destFKeyName = "";
        List<DbTableIndexColumnSpec> destColumns = null;
        boolean destPKey = false;
        boolean destUnique = false;
        boolean destHashed = false;
        boolean destClustered = false;
        int destColumnCount = 0;
        String destFields = "";
        if (srcIndex != null) {
            srcFKeyName = srcIndex.getFKeyName();
            srcColumns = srcIndex.getColumns();
            srcPKey = srcIndex.isPKey();
            srcUnique = srcIndex.isUnique();
            srcHashed = srcIndex.isHashed();
            srcClustered = srcIndex.isClustered();
            assert srcColumns != null;
            srcColumnCount = srcColumns.size();
        }
        if (destIndex != null) {
            destFKeyName = destIndex.getFKeyName();
            destColumns = destIndex.getColumns();
            destPKey = destIndex.isPKey();
            destUnique = destIndex.isUnique();
            destHashed = destIndex.isHashed();
            destClustered = destIndex.isClustered();
            assert destColumns != null;
            destColumnCount = destColumns.size();
        }
        String dlm = "";
        String desc = " desc";
        if (srcColumns != null) {
            for (int i = 0; i < srcColumnCount; i++) {
                DbTableIndexColumnSpec srcColumn = srcColumns.get(i);
                assert srcColumn != null;
                int srcPos = srcColumn.getOrdinalPosition();
                String srcField = srcColumn.getColumnName();
                boolean srcAsc = srcColumn.isAscending();

                String destField = "";
                int destPos = 0;
                boolean destAsc = true;
                if (destColumnCount > i) {
                    assert destColumns != null;
                    DbTableIndexColumnSpec destColumn = destColumns.get(i);
                    assert destColumn != null;
                    destField = destColumn.getColumnName();
                    destPos = destColumn.getOrdinalPosition();
                    destAsc = destColumn.isAscending();
                }
                if (srcField == null) {
                    srcField = "";
                }
                if (destField == null) {
                    destField = "";
                }

                boolean equalsPos = (srcColumnCount == 0 || destColumnCount == 0 || srcPos == destPos);
                boolean equalsField = (srcColumnCount == 0 || destColumnCount == 0 || srcField.equalsIgnoreCase(destField));
                boolean equalsAsc = (srcColumnCount == 0 || destColumnCount == 0 || srcAsc == destAsc);

                srcFields += dlm + (equalsPos ? "" + srcPos : String.format(COLOR_FORMAT, "" + srcPos));
                srcFields += "-" + (equalsField ? srcField : String.format(COLOR_FORMAT, srcField));
                if (!srcAsc) {
                    srcFields += (equalsAsc ? desc : String.format(COLOR_FORMAT, desc));
                }
                destFields += (destField.isEmpty() ? "" : dlm) + (equalsPos ? "" + destPos : String.format(COLOR_FORMAT, "" + destPos));
                destFields += (destField.isEmpty() ? "" : "-") + (equalsField ? destField : String.format(COLOR_FORMAT, destField));
                if (!destAsc) {
                    destFields += (equalsAsc ? desc : String.format(COLOR_FORMAT, desc));
                }
                if (!equalsField || !equalsAsc) {
                    nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                }
                dlm = ", ";
            }
        }
        if (destColumns != null) {
            for (int i = srcColumnCount; i < destColumnCount; i++) {
                DbTableIndexColumnSpec destColumn = destColumns.get(i);
                assert destColumn != null;
                String destField = destColumn.getColumnName();
                int destPos = destColumn.getOrdinalPosition();
                boolean destAsc = destColumn.isAscending();
                if (destField == null) {
                    destField = "";
                }

                destFields += (destField.isEmpty() ? "" : dlm + destPos + "-" + (srcColumnCount == 0 ? destField : String.format(COLOR_FORMAT, destField)));
                if (!destAsc) {
                    destFields += (srcColumnCount == 0 ? desc : String.format(COLOR_FORMAT, desc));
                }
                nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
                dlm = ", ";
            }
        }

        if (srcFKeyName == null) {
            srcFKeyName = "";
        }
        if (srcIndexName == null) {
            srcIndexName = "";
        }

        if (destFKeyName == null) {
            destFKeyName = "";
        }
        if (destIndexName == null) {
            destIndexName = "";
        }

        boolean equalsName = (srcIndex == null || destIndex == null || srcIndexName.equalsIgnoreCase(destIndexName));
        boolean equalsFKeyName = (srcIndex == null || destIndex == null || srcFKeyName.equalsIgnoreCase(destFKeyName));
        boolean equalsPKey = (srcIndex == null || destIndex == null || srcPKey == destPKey);
        boolean equalsUnique = (srcIndex == null || destIndex == null || srcUnique == destUnique);
        boolean equalsHashed = (srcIndex == null || destIndex == null || srcHashed == destHashed);
        //boolean equalsClustered = (srcClustered = destClustered);

        if (!equalsName || !equalsFKeyName || !equalsPKey || !equalsUnique || !equalsHashed) {
            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EQUAL;
        }

        String srcRow;
        String destRow;
        if (srcIndex != null) {
            srcRow = String.format(INDEX_FORMAT, sourceTitle,
                    (equalsName ? srcIndexName : String.format(COLOR_FORMAT, srcIndexName)),
                    fieldsTitle, srcFields,
                    uniqueTitle, (equalsUnique ? "" + srcUnique : String.format(COLOR_FORMAT, "" + srcUnique)),
                    hashedTitle, (equalsHashed ? "" + srcHashed : String.format(COLOR_FORMAT, "" + srcHashed)),
                    //                    (equalsClustered ? "" + srcClustered : String.format(COLOR_FORMAT, "" + srcClustered)),
                    pKeyTitle, (equalsPKey ? "" + srcPKey : String.format(COLOR_FORMAT, "" + srcPKey)),
                    fKeysTitle, (equalsFKeyName ? srcFKeyName : String.format(COLOR_FORMAT, srcFKeyName)));
        } else {
            srcRow = String.format(EMPTYINDEX_FORMAT, sourceTitle);
        }
        if (destIndex != null) {
            destRow = String.format(INDEX_FORMAT, destinationTitle,
                    (equalsName ? destIndexName : String.format(COLOR_FORMAT, destIndexName)),
                    fieldsTitle, destFields,
                    uniqueTitle, (equalsUnique ? "" + destUnique : String.format(COLOR_FORMAT, "" + destUnique)),
                    hashedTitle, (equalsHashed ? "" + destHashed : String.format(COLOR_FORMAT, "" + destHashed)),
                    //                    (equalsClustered ? "" + destClustered : String.format(COLOR_FORMAT, "" + destClustered)),
                    pKeyTitle, (equalsPKey ? "" + destPKey : String.format(COLOR_FORMAT, "" + destPKey)),
                    fKeysTitle, (equalsFKeyName ? destFKeyName : String.format(COLOR_FORMAT, destFKeyName)));
        } else {
            destRow = String.format(EMPTYINDEX_FORMAT, destinationTitle);
        }
        if (srcIndex == null || destIndex == null) {
            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_EXIST;
        } else if (!MetadataUtils.isSameIndex(srcIndex, destIndex, oneDialect)) {
            nodeType = DbStructureInfo.COMPARE_TYPE.NOT_SAME;
        }
        DefaultMutableTreeNode indexNameNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(ELEMENTNAME_FORMAT, (srcIndexName.isEmpty() ? destIndexName.toUpperCase() : srcIndexName.toUpperCase())), nodeType));
        DefaultMutableTreeNode indexNode = new DefaultMutableTreeNode(new DbStructureInfo(String.format(NODE_FORMAT, srcRow, destRow)));
        indexNameNode.add(indexNode);
        parentNode.add(indexNameNode);
        return nodeType;
    }

    private void showCard(JPanel aPanel, final String aCardName) {
        assert aPanel != null;
        LayoutManager layout = aPanel.getLayout();
        assert layout != null;
        assert layout instanceof CardLayout;
        CardLayout cardLayout = (CardLayout) layout;
        cardLayout.show(pnInfo, aCardName);
    }

}
