/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.tools;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.ClientConstants;
import com.eas.client.DatabaseMdCache;
import com.eas.client.DatabasesClient;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.sqldrivers.Db2SqlDriver;
import com.eas.client.sqldrivers.H2SqlDriver;
import com.eas.client.sqldrivers.MsSqlSqlDriver;
import com.eas.client.sqldrivers.MySqlSqlDriver;
import com.eas.client.sqldrivers.OracleSqlDriver;
import com.eas.client.sqldrivers.PostgreSqlDriver;
import com.eas.client.sqldrivers.SqlDriver;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author vy
 */
public class SqlDriversTester extends JFrame {

    //--- общие переменные ---
    private Connection connectJDBC;   // для jdbc connection
    private SqlDriver platypusDriver; // для jdbc connection
    private DatabasesClient client;           // для platypus connection
    private SqlDriver driver = null;         // для platypus connection
    private Map<String, Object[]> jdbcSets = new HashMap<>();
    //--- переменные для SELECT ---
    private String[] sqls_select;
    //--- переменные для TABLE ---
    private String[] sqls_table;
    //--- переменные для FIELD ---
    private String[] sqls_field;
    private Fields fields;
    private Field oldField_field;
    //--- переменные для  commentsDS---
    private String[] sqls_commentsDS;
    //--- переменные для INDEX ---
    private String[] sqls_index;
    //--- переменные для  PK ---
    private String[] sqls_pk;
    //--- переменные для  FK ---
    private String[] sqls_fk;
    //--- компоненты для панели CONNECT к БД ---
    private JComboBox comboDB_connect = new JComboBox();
    private JTextField fldDriver_connect = new JTextField("", 50);
    private JTextField fldUrl_connect = new JTextField("", 50);
    private JTextField fldUser_connect = new JTextField("", 50);
    private JPasswordField fldPassword_connect = new JPasswordField("", 50);
    private JTextArea textLog_connect = new JTextArea("", 10, 80);
    private JButton btnConnection_connect = new JButton("JDBC Connection");
    private JTextField fldSchema_connect = new JTextField("", 50);
    private JCheckBox chkCreatePlatypusTables_connect = new JCheckBox("Создать системные таблицы Platypus?", false);
    private JButton btnPlatypusConnection_connect = new JButton("Platypus Connection");
    //--- компоненты для тестирования SET SCHEMA ---
    private JTextArea textLog_setschema = new JTextArea("", 3, 50);
    private JTextField fldSchema_setschema = new JTextField("", 20);
    private JButton btnRun_setschema = new JButton("executeQuery: applyContextToConnection(String aSchema)");
    private JButton btnRun_getschema = new JButton("executeQuery: getConnectionContext()");
    //--- компоненты для тестирования SELECT ---
    private JTextArea textSql_select = new JTextArea("", 3, 50);
    private JTextArea textLog_select = new JTextArea("", 3, 50);
    private JTextField fldSchema_select = new JTextField("", 20);
    private JTextField fldTable1_select = new JTextField("", 20);
    private JTextField fldTable2_select = new JTextField("", 20);
    private JTable table_select = new JTable();
    private JRadioButton rb_getSql4SchemasEnumeration = new JRadioButton("getSql4SchemasEnumeration()");
    private JRadioButton rb_getSql4TableColumns = new JRadioButton("getSql4TableColumns(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb_getSql4TablesEnumeration = new JRadioButton("getSql4TablesEnumeration(String schema4Sql)");
    private JRadioButton rb_getSql4ColumnsComments = new JRadioButton("getSql4ColumnsComments(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb_getSql4TableComments = new JRadioButton("getSql4TableComments(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb_getSql4Indexes = new JRadioButton("getSql4Indexes(String aOwnerName, Set<String> aTableNames");
    private JRadioButton rb_getSql4TableForeignKeys = new JRadioButton("getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb_getSql4TablePrimaryKeys = new JRadioButton("getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames)");
    private JButton btnRun_select = new JButton("executeQuery");
    //--- компоненты тестирования sql для TABLE ---
    private JTextArea textSql_table = new JTextArea("", 3, 50);
    private JTextArea textLog_table = new JTextArea("", 3, 50);
    private JTextField fldSchema_table = new JTextField("", 20);
    private JTextField fldTable_table = new JTextField("", 20);
    private JTextField fldPkField_table = new JTextField("", 20);
    private JTextField fldComment_table = new JTextField("", 20);
    private JRadioButton rb_getSql4EmptyTableCreation = new JRadioButton("getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName)");
    private JRadioButton rb_getSql4DropTable = new JRadioButton("getSql4DropTable(String aSchemaName, String aTableName)");
    private JRadioButton rb_getSql4CreateTableComment = new JRadioButton("getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription)");
    private JButton btnRun_table = new JButton("Execute");
    //--- компоненты тестирования sql для FIELD ---
    private JTextArea textSql_field = new JTextArea("", 3, 50);
    private JTextArea textLog_field = new JTextArea("", 3, 50);
    private JTextField fldSchema_field = new JTextField("");
    JComboBox comboTable_field = new JComboBox();
    JComboBox comboField_field = new JComboBox();
    private JTextField lbName_field = new JTextField("", 10);
    private JTextField lbType_field = new JTextField("", 10);
    private JTextField lbSize_field = new JTextField("", 10);
    private JTextField lbPrecision_field = new JTextField("", 10);
    private JCheckBox lbNullable_field = new JCheckBox("", false);
    private JCheckBox lbReadOnly_field = new JCheckBox("", false);
    private JCheckBox lbSigned_field = new JCheckBox("", false);
    private JCheckBox lbStrong4Insert_field = new JCheckBox("", false);
    private JTextField lbScale_field = new JTextField("", 10);
    private JTextField lbDescription_field = new JTextField("", 10);
    private JCheckBox lbPk_field = new JCheckBox("", false);
    private JTextField lbFKName_field = new JTextField("", 10);
    private JCheckBox lbFKDeferrable_field = new JCheckBox("", false);
    private JTextField lbFKUpdateRule_field = new JTextField("", 10);
    private JTextField lbFKDeleteRule_field = new JTextField("", 10);
    private JTextField lbFKRefereeCName_field = new JTextField("", 10);
    private JTextField lbFKRefereeFieldName_field = new JTextField("", 10);
    private JTextField lbFKRefereeTableName_field = new JTextField("", 10);
    private JTextField lbFKRefereeSchemaName_field = new JTextField("", 10);
    private JTextField fldName_field = new JTextField("", 10);
    private JTextField fldType_field = new JTextField("", 10);
    private JTextField fldSize_field = new JTextField("", 10);
    private JTextField fldPrecision_field = new JTextField("", 10);
    private JCheckBox fldNullable_field = new JCheckBox("", false);
    private JCheckBox fldReadOnly_field = new JCheckBox("", false);
    private JCheckBox fldSigned_field = new JCheckBox("", false);
    private JCheckBox fldStrong4Insert_field = new JCheckBox("", false);
    private JTextField fldScale_field = new JTextField("", 10);
    private JTextField fldDescription_field = new JTextField("", 10);
    private JCheckBox fldPk_field = new JCheckBox("", false);
    private JRadioButton rb_getSql4FieldDefinition = new JRadioButton("getSql4FieldDefinition(Field aField)");
    private JRadioButton rb_getSqls4ModifyingField = new JRadioButton("getSqls4ModifyingField(String aTableName, Field aOldFieldMd, Field aNewFieldMd)");
    private JRadioButton rb_getSqls4RenamingField = new JRadioButton("getSqls4RenamingField(String aTableName, String aOldFieldName, Field aNewFieldMd)");
    private JRadioButton rb_getSql4DroppingField = new JRadioButton("getSql4DroppingField(String aTableName, String aFieldName)");
    private JRadioButton rb_getSql4CreateColumnComment = new JRadioButton("getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription)");
    private JRadioButton rb_getSql4CreateField = new JRadioButton("for AddFieldAction(String aTableName, Field aFieldMd))");
    private JButton btnRun_field = new JButton("Execute");
    //--- компоненты тестирования sql для  INDEX ---
    private JTextArea textSql_index = new JTextArea("", 3, 50);
    private JTextArea textLog_index = new JTextArea("", 3, 50);
    private JTextField fldSchema_index = new JTextField("", 10);
    private JTextField fldTable_index = new JTextField("", 10);
    private JTextField fldIndex_index = new JTextField("", 10);
    private JCheckBox fldClustered_index = new JCheckBox("", false);
    private JCheckBox fldHashed_index = new JCheckBox("", false);
    private JCheckBox fldUnique_index = new JCheckBox("", false);
    private JTextField fldColumn1_index = new JTextField("", 10);
    private JTextField fldColumn2_index = new JTextField("", 10);
    private JCheckBox fldDesc1_index = new JCheckBox("Descending", false);
    private JCheckBox fldDesc2_index = new JCheckBox("Descending", false);
    private JRadioButton rb_getSql4CreateIndex = new JRadioButton("getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex)");
    private JRadioButton rb_getSql4DropIndex = new JRadioButton("getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName)");
    private JButton btnRun_index = new JButton("Execute");
    //--- компоненты тестирования sql для PKEY ---
    private JTextArea textSql_pk = new JTextArea("", 3, 50);
    private JTextArea textLog_pk = new JTextArea("", 3, 50);
    private JTextField fldSchema_pk = new JTextField("", 10);
    private JTextField fldTable_pk = new JTextField("", 10);
    private JTextField fldCName_pk = new JTextField("", 10);
    private JTextField fldColumn1_pk = new JTextField("", 10);
    private JTextField fldColumn2_pk = new JTextField("", 10);
    private JRadioButton rb_getSql4CreatePkConstraint = new JRadioButton("getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> list aPk)");
    private JRadioButton rb_getSql4DropPkConstraint = new JRadioButton("getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk)");
    private JButton btnRun_pk = new JButton("Execute");
    //--- компоненты тестирования sql для FKEY ---
    private JTextArea textSql_fk = new JTextArea("", 3, 50);
    private JTextArea textLog_fk = new JTextArea("", 3, 50);
    private JTextField fldSchema_fk = new JTextField("", 10);
    private JTextField fldTable_fk = new JTextField("", 10);
    private JTextField fldCName_fk = new JTextField("", 10);
    private JTextField fldColumn1_fk = new JTextField("", 10);
    private JTextField fldColumn2_fk = new JTextField("", 10);
    private JCheckBox fldDeferrable_fk = new JCheckBox("", false);
    JComboBox<ForeignKeyRule> comboDeleteRule_fk = new JComboBox<>();
    JComboBox<ForeignKeyRule> comboUpdateRule_fk = new JComboBox<>();
    private JTextField fldRefereeSchema_fk = new JTextField("", 10);
    private JTextField fldRefereeTable_fk = new JTextField("", 10);
    private JTextField fldRefereeCName_fk = new JTextField("", 10);
    private JTextField fldRefereeColumn1_fk = new JTextField("", 10);
    private JTextField fldRefereeColumn2_fk = new JTextField("", 10);
    private JRadioButton rb_getSql4CreateFkConstraint = new JRadioButton("getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk)");
    private JRadioButton rb_getSql4CreateFkConstraint2 = new JRadioButton("getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk)");
    private JRadioButton rb_getSql4DropFkConstraint = new JRadioButton("getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk)");
    private JButton btnRun_fk = new JButton("Execute");
    //--- компоненты тестирования sql для COMMENTDS  ---
    private JTextArea textSql_commentDS = new JTextArea("", 3, 50);
    private JTextArea textLog_commentDS = new JTextArea("", 3, 50);
    private JTextField fldSchema_commentDS = new JTextField("", 20);
    private JTextField fldTable1_commentDS = new JTextField("", 20);
    private JTextField fldTable2_commentDS = new JTextField("", 20);
    private JRadioButton rb2_getSql4SchemasEnumeration = new JRadioButton("getSql4SchemasEnumeration()");
    private JRadioButton rb2_getSql4TableColumns = new JRadioButton("getSql4TableColumns(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb2_getSql4TablesEnumeration = new JRadioButton("getSql4TablesEnumeration(String schema4Sql)");
    private JRadioButton rb2_getSql4ColumnsComments = new JRadioButton("getSql4ColumnsComments(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb2_getSql4TableComments = new JRadioButton("getSql4TableComments(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb2_getSql4Indexes = new JRadioButton("getSql4Indexes(String aOwnerName, Set<String> aTableNames");
    private JRadioButton rb2_getSql4TableForeignKeys = new JRadioButton("getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames)");
    private JRadioButton rb2_getSql4TablePrimaryKeys = new JRadioButton("getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames)");
    private JButton btnGetColumnNameFromCommentsDs_commentDS = new JButton("getColumnNameFromCommentsDs(Rowset rs)");
    private JButton btnGetColumnCommentFromCommentsDs_commentDS = new JButton("getColumnCommentFromCommentsDs(Rowset rs)");
    private JButton btnGetTableNameFromCommentsDs_commentDS = new JButton("getTableNameFromCommentsDs(Rowset rs)");
    private JButton btnGetTableCommentFromCommentsDs_commentDS = new JButton("getTableCommentFromCommentsDs(Rowset rs)");
    //--- компоненты тестирования UserSql ******
    private JTextArea textSql_user = new JTextArea("", 3, 50);
    private JTextArea textLog_user = new JTextArea("", 3, 50);
    private JTable table_user = new JTable();
    private JButton btnRun_user = new JButton("Execute");
    private JButton btnSelect_user = new JButton("Select");
    //--- компоненты тестирования typeInfo ******
    private JTable table_typeinfo = new JTable();
    private JButton btnMD_typeinfo = new JButton("getMetaData()");
    //--- компоненты тестирования select Info ******
    private JTable table_selectinfo = new JTable();
    private JButton btnMD_selectinfo = new JButton("getMetaData() from select");
    private JTextArea textSql_selectinfo = new JTextArea("select * from ", 10, 50);
    //--- общая часть
    JTabbedPane tabbedPane = new JTabbedPane();

    // инициализация списка настроек для баз данных
    {
        // n-имя типа базы
        // 0-имя jdbc драйвера
        // 1-строка соединения
        // 2-имя схемы
        // 3-пользователь БД
        // 4-пароль БД
        // 5-драйвер platypus для данной БД
        //            n       ,              0                               ,1                                 ,2        ,3       ,4         ,5
        jdbcSets.put("oracle", new Object[]{"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@research.office.altsoft.biz:1521/DBALT", "test1", "test1", "test1", new OracleSqlDriver()});
        jdbcSets.put("postgre", new Object[]{"org.postgresql.Driver", "jdbc:postgresql://192.168.10.1:5432/Trans", "test1", "test1", "test1", new PostgreSqlDriver()});
        jdbcSets.put("db2", new Object[]{"com.ibm.db2.jcc.DB2Driver", "jdbc:db2://192.168.10.154:50000/test", "test1", "dba", "masterkey", new Db2SqlDriver()});
        jdbcSets.put("mssql", new Object[]{"net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://192.168.10.154:1433/test1", "dbo", "test1", "1test1", new MsSqlSqlDriver()});
        jdbcSets.put("mysql", new Object[]{"com.mysql.jdbc.Driver", "jdbc:mysql://192.168.10.205:3306/test1", "test1", "test1", "test1", new MySqlSqlDriver()});
        jdbcSets.put("h2", new Object[]{"org.h2.Driver", "jdbc:h2:tcp://localhost/~/test", "test1", "test1", "test1", new H2SqlDriver()});
        for (String dbName : jdbcSets.keySet()) {
            comboDB_connect.addItem(dbName);
        }
        dbChoice();
    }

    /**
     * конструктор
     *
     */
    public SqlDriversTester() {
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                logout();
            }
        });
        try {
            jbInit();
        } catch (Exception ex) {
            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(true);
    }

    /**
     * инициализация swing-компонентов
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        //--- панель jdbc-соединения ---
        JPanel pnMain_connect = new JPanel(new BorderLayout());

        JPanel pnWest_connect = new JPanel(new VerticalFlowLayout(0, 1, 0, true, false));
        JPanel pnWest1_connect = new JPanel(new GridLayout(1, 2));
        JPanel pnWest2_connect = new JPanel(new GridLayout(1, 2));
        JPanel pnWest3_connect = new JPanel(new GridLayout(1, 2));
        JPanel pnWest4_connect = new JPanel(new GridLayout(1, 2));
        JPanel pnWest5_connect = new JPanel(new GridLayout(1, 2));
        JPanel pnWest6_connect = new JPanel(new GridLayout(1, 2));

        pnWest1_connect.add(new JLabel("тип БД"));
        pnWest1_connect.add(comboDB_connect);
        pnWest2_connect.add(new JLabel("имя драйвера"));
        pnWest2_connect.add(fldDriver_connect);
        pnWest3_connect.add(new JLabel("строка соединения"));
        pnWest3_connect.add(fldUrl_connect);
        pnWest4_connect.add(new JLabel("пользователь"));
        pnWest4_connect.add(fldUser_connect);
        pnWest5_connect.add(new JLabel("пароль"));
        pnWest5_connect.add(fldPassword_connect);
        pnWest6_connect.add(new JLabel("схема для Platypus-клиента"));
        pnWest6_connect.add(fldSchema_connect);

        pnWest_connect.add(new JLabel("Настройки соединения"));
        pnWest_connect.add(pnWest1_connect);
        pnWest_connect.add(pnWest2_connect);
        pnWest_connect.add(pnWest3_connect);
        pnWest_connect.add(pnWest4_connect);
        pnWest_connect.add(pnWest5_connect);
        pnWest_connect.add(pnWest6_connect);
        pnWest_connect.add(chkCreatePlatypusTables_connect);

        JPanel pnSouth_connect = new JPanel();
        pnSouth_connect.add(btnConnection_connect);
        pnSouth_connect.add(btnPlatypusConnection_connect);

        JPanel pnCenter_connect = new JPanel(new GridLayout(1, 1));
        pnCenter_connect.add(new JScrollPane(textLog_connect));

        pnMain_connect.add(pnCenter_connect, BorderLayout.CENTER);
        pnWest_connect.setPreferredSize(new Dimension(350, 100));
        pnMain_connect.add(pnWest_connect, BorderLayout.WEST);
        pnMain_connect.add(pnSouth_connect, BorderLayout.SOUTH);

        //--- панель тестирования SET SCHEMA ---
        JPanel pnMain_setschema = new JPanel();
        pnMain_setschema.setLayout(new BorderLayout());

        JPanel pnWest_setschema = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_setschema = new JPanel(new GridLayout(1, 2));

        pnWest1_setschema.add(new JLabel("схема"));
        pnWest1_setschema.add(fldSchema_setschema);
        pnWest_setschema.add(pnWest1_setschema);

        JPanel pnSouth_setschema = new JPanel();
        pnSouth_setschema.add(btnRun_setschema);
        pnSouth_setschema.add(btnRun_getschema);

        JSplitPane spCenterH_setschema = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_setschema), new JScrollPane(textLog_setschema));
        spCenterH_setschema.setDividerLocation(370);

        pnMain_setschema.add(spCenterH_setschema, BorderLayout.CENTER);
        pnMain_setschema.add(pnSouth_setschema, BorderLayout.SOUTH);

        //--- панель тестирования всех select ---
        JPanel pnMain_select = new JPanel();
        pnMain_select.setLayout(new BorderLayout());

        JPanel pnWest_select = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));

        JPanel pnWest1_select = new JPanel(new GridLayout(3, 2));

        pnWest1_select.add(new JLabel("схема"));
        pnWest1_select.add(fldSchema_select);
        pnWest1_select.add(new JLabel("таблица1"));
        pnWest1_select.add(fldTable1_select);
        pnWest1_select.add(new JLabel("таблица2"));
        pnWest1_select.add(fldTable2_select);

        pnWest_select.add(new JLabel("Параметры sql:"));
        pnWest_select.add(pnWest1_select);
        pnWest_select.add(pnWest1_select);
        pnWest_select.add(pnWest1_select);

        pnWest_select.add(new JLabel("Методы драйвера:"));
        pnWest_select.add(rb_getSql4SchemasEnumeration);
        pnWest_select.add(rb_getSql4TablesEnumeration);
        pnWest_select.add(rb_getSql4TableColumns);
        pnWest_select.add(rb_getSql4TableComments);
        pnWest_select.add(rb_getSql4ColumnsComments);
        pnWest_select.add(rb_getSql4Indexes);
        pnWest_select.add(rb_getSql4TablePrimaryKeys);
        pnWest_select.add(rb_getSql4TableForeignKeys);

        ButtonGroup buttonGroup_select = new ButtonGroup();
        buttonGroup_select.add(rb_getSql4SchemasEnumeration);
        buttonGroup_select.add(rb_getSql4TableColumns);
        buttonGroup_select.add(rb_getSql4TablesEnumeration);
        buttonGroup_select.add(rb_getSql4ColumnsComments);
        buttonGroup_select.add(rb_getSql4TableComments);
        buttonGroup_select.add(rb_getSql4Indexes);
        buttonGroup_select.add(rb_getSql4TableForeignKeys);
        buttonGroup_select.add(rb_getSql4TablePrimaryKeys);

        JPanel pnSouth_select = new JPanel();
        pnSouth_select.add(btnRun_select);

        JSplitPane spCenter1_select = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textLog_select), new JScrollPane(table_select));
        JSplitPane spCenter2_select = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_select), spCenter1_select);
        spCenter1_select.setDividerLocation(100);
        spCenter2_select.setDividerLocation(100);
        table_select.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_select.setColumnSelectionAllowed(true);

        JSplitPane spCenterH_select = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_select), spCenter2_select);
        spCenterH_select.setDividerLocation(370);

        pnMain_select.add(spCenterH_select, BorderLayout.CENTER);
        pnMain_select.add(pnSouth_select, BorderLayout.SOUTH);

        //--- панель тестирования sql для table ---
        JPanel pnMain_table = new JPanel(new BorderLayout());

        JPanel pnWest_table = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));

        JPanel pnWest1_table = new JPanel(new GridLayout(4, 3));

        pnWest1_table.add(new JLabel("схема"));
        pnWest1_table.add(fldSchema_table);
        pnWest1_table.add(new JLabel(""));

        pnWest1_table.add(new JLabel("таблица"));
        pnWest1_table.add(fldTable_table);
        pnWest1_table.add(new JLabel(""));

        pnWest1_table.add(new JLabel("поле для PK"));
        pnWest1_table.add(fldPkField_table);
        pnWest1_table.add(new JLabel(""));

        pnWest1_table.add(new JLabel("комментарий к таблице"));
        pnWest1_table.add(fldComment_table);
        pnWest1_table.add(new JLabel(""));

        pnWest_table.add(new JLabel("Параметры sql для table:"));
        pnWest_table.add(pnWest1_table);

        pnWest_table.add(new JLabel("Методы драйвера:"));
        pnWest_table.add(rb_getSql4EmptyTableCreation);
        pnWest_table.add(rb_getSql4CreateTableComment);
        pnWest_table.add(rb_getSql4DropTable);

        ButtonGroup buttonGroup_table = new ButtonGroup();
        buttonGroup_table.add(rb_getSql4EmptyTableCreation);
        buttonGroup_table.add(rb_getSql4CreateTableComment);
        buttonGroup_table.add(rb_getSql4DropTable);

        JPanel pnSouth_table = new JPanel();
        pnSouth_table.add(btnRun_table);

        JSplitPane spCenterV_table = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_table), new JScrollPane(textLog_table));
        JSplitPane spCenterH_table = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_table), spCenterV_table);

        spCenterV_table.setDividerLocation(170);
        spCenterH_table.setDividerLocation(370);

        pnMain_table.add(spCenterH_table, BorderLayout.CENTER);
        pnMain_table.add(pnSouth_table, BorderLayout.SOUTH);

        //--- панель тестирования sql для field ---
        JPanel pnMain_field = new JPanel(new BorderLayout());

        JPanel pnWest_field = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_field = new JPanel(new GridLayout(3, 3, 10, 0));

        pnWest1_field.add(new JLabel("схема"));
        pnWest1_field.add(fldSchema_field);
        pnWest1_field.add(new JLabel(""));

        pnWest1_field.add(new JLabel("таблица"));
        pnWest1_field.add(comboTable_field);
        pnWest1_field.add(new JLabel(""));

        pnWest1_field.add(new JLabel("колонка"));
        pnWest1_field.add(comboField_field);
        pnWest1_field.add(new JLabel(""));

        JPanel pnWest2_field = new JPanel(new GridLayout(20, 4, 10, 0));

        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel("значение БД"));
        pnWest2_field.add(new JLabel("новое значение"));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Имя колонки"));
        pnWest2_field.add(lbName_field);
        pnWest2_field.add(fldName_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Type"));
        pnWest2_field.add(lbType_field);
        pnWest2_field.add(fldType_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Size"));
        pnWest2_field.add(lbSize_field);
        pnWest2_field.add(fldSize_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Scale"));
        pnWest2_field.add(lbScale_field);
        pnWest2_field.add(fldScale_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Precision"));
        pnWest2_field.add(lbPrecision_field);
        pnWest2_field.add(fldPrecision_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("isNullable"));
        pnWest2_field.add(lbNullable_field);
        pnWest2_field.add(fldNullable_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("isReadOnly"));
        pnWest2_field.add(lbReadOnly_field);
        pnWest2_field.add(fldReadOnly_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("isSigned"));
        pnWest2_field.add(lbSigned_field);
        pnWest2_field.add(fldSigned_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("isStrong4Insert"));
        pnWest2_field.add(lbStrong4Insert_field);
        pnWest2_field.add(fldStrong4Insert_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("Description"));
        pnWest2_field.add(lbDescription_field);
        pnWest2_field.add(fldDescription_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("isPk"));
        pnWest2_field.add(lbPk_field);
        pnWest2_field.add(fldPk_field);
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.CName"));
        pnWest2_field.add(lbFKName_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.Deferrable"));
        pnWest2_field.add(lbFKDeferrable_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.DeleteRule"));
        pnWest2_field.add(lbFKDeleteRule_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.UpdateRule"));
        pnWest2_field.add(lbFKUpdateRule_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.referee.CName"));
        pnWest2_field.add(lbFKRefereeCName_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.referee.FieldName"));
        pnWest2_field.add(lbFKRefereeFieldName_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.referee.TableName"));
        pnWest2_field.add(lbFKRefereeTableName_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest2_field.add(new JLabel("FK.referee.SchemaName"));
        pnWest2_field.add(lbFKRefereeSchemaName_field);
        pnWest2_field.add(new JLabel(""));
        pnWest2_field.add(new JLabel(""));

        pnWest_field.add(new JLabel("Параметры sql для field:"));
        pnWest_field.add(pnWest1_field);
        pnWest_field.add(pnWest2_field);

        pnWest_field.add(new JLabel("Методы драйвера:"));
        pnWest_field.add(rb_getSql4FieldDefinition);
        pnWest_field.add(rb_getSqls4ModifyingField);
        pnWest_field.add(rb_getSqls4RenamingField);
        pnWest_field.add(rb_getSql4DroppingField);
        pnWest_field.add(rb_getSql4CreateColumnComment);
        pnWest_field.add(rb_getSql4CreateField);

        ButtonGroup buttonGroup_field = new ButtonGroup();
        buttonGroup_field.add(rb_getSql4FieldDefinition);
        buttonGroup_field.add(rb_getSqls4ModifyingField);
        buttonGroup_field.add(rb_getSqls4RenamingField);
        buttonGroup_field.add(rb_getSql4DroppingField);
        buttonGroup_field.add(rb_getSql4CreateColumnComment);
        buttonGroup_field.add(rb_getSql4CreateField);

        JPanel pnSouth_field = new JPanel();
        pnSouth_field.add(btnRun_field);

        JSplitPane spCenterV_field = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_field), new JScrollPane(textLog_field));
        JSplitPane spCenterH_field = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_field), spCenterV_field);

        spCenterV_field.setDividerLocation(170);
        spCenterH_field.setDividerLocation(470);

        pnMain_field.add(spCenterH_field, BorderLayout.CENTER);
        pnMain_field.add(pnSouth_field, BorderLayout.SOUTH);

        //--- панель тестирования  commentDS *************
        JPanel pnMain_commentDS = new JPanel(new BorderLayout());
        JPanel pnWest_commentDS = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_commentDS = new JPanel(new GridLayout(3, 2));

        pnWest1_commentDS.add(new JLabel("схема"));
        pnWest1_commentDS.add(fldSchema_commentDS);
        pnWest1_commentDS.add(new JLabel("таблица1"));
        pnWest1_commentDS.add(fldTable1_commentDS);
        pnWest1_commentDS.add(new JLabel("таблица2"));
        pnWest1_commentDS.add(fldTable2_commentDS);

        pnWest_commentDS.add(new JLabel("Параметры sql:"));
        pnWest_commentDS.add(pnWest1_commentDS);

        pnWest_commentDS.add(new JLabel("Методы драйвера:"));
        pnWest_commentDS.add(rb2_getSql4SchemasEnumeration);
        pnWest_commentDS.add(rb2_getSql4TablesEnumeration);
        pnWest_commentDS.add(rb2_getSql4TableColumns);
        pnWest_commentDS.add(rb2_getSql4TableComments);
        pnWest_commentDS.add(rb2_getSql4ColumnsComments);
        pnWest_commentDS.add(rb2_getSql4Indexes);
        pnWest_commentDS.add(rb2_getSql4TablePrimaryKeys);
        pnWest_commentDS.add(rb2_getSql4TableForeignKeys);

        ButtonGroup buttonGroup_commentDS = new ButtonGroup();
        buttonGroup_commentDS.add(rb2_getSql4SchemasEnumeration);
        buttonGroup_commentDS.add(rb2_getSql4TableColumns);
        buttonGroup_commentDS.add(rb2_getSql4TablesEnumeration);
        buttonGroup_commentDS.add(rb2_getSql4ColumnsComments);
        buttonGroup_commentDS.add(rb2_getSql4TableComments);
        buttonGroup_commentDS.add(rb2_getSql4Indexes);
        buttonGroup_commentDS.add(rb2_getSql4TableForeignKeys);
        buttonGroup_commentDS.add(rb2_getSql4TablePrimaryKeys);

        JPanel pnSouth_commentDS = new JPanel(new GridLayout(2, 2));
        pnSouth_commentDS.add(btnGetTableNameFromCommentsDs_commentDS);
        pnSouth_commentDS.add(btnGetTableCommentFromCommentsDs_commentDS);
        pnSouth_commentDS.add(btnGetColumnNameFromCommentsDs_commentDS);
        pnSouth_commentDS.add(btnGetColumnCommentFromCommentsDs_commentDS);

        JSplitPane spCenter2_commentDS = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_commentDS), textLog_commentDS);
        spCenter2_commentDS.setDividerLocation(100);

        JSplitPane spCenterH_commentDS = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_commentDS), spCenter2_commentDS);
        spCenterH_commentDS.setDividerLocation(370);

        pnMain_commentDS.add(spCenterH_commentDS, BorderLayout.CENTER);
        pnMain_commentDS.add(pnSouth_commentDS, BorderLayout.SOUTH);

        //--- панель тестирования  INDEX *************
        JPanel pnMain_index = new JPanel(new BorderLayout());
        JPanel pnWest_index = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_index = new JPanel(new GridLayout(8, 4));

        pnWest1_index.add(new JLabel("схема"));
        pnWest1_index.add(fldSchema_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("таблица"));
        pnWest1_index.add(fldTable_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("индекс"));
        pnWest1_index.add(fldIndex_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("Clustered"));
        pnWest1_index.add(fldClustered_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("Hashed"));
        pnWest1_index.add(fldHashed_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("Unique"));
        pnWest1_index.add(fldUnique_index);
        pnWest1_index.add(new JLabel(""));
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("колонка 1"));
        pnWest1_index.add(fldColumn1_index);
        pnWest1_index.add(fldDesc1_index);
        pnWest1_index.add(new JLabel(""));

        pnWest1_index.add(new JLabel("колонка 2"));
        pnWest1_index.add(fldColumn2_index);
        pnWest1_index.add(fldDesc2_index);
        pnWest1_index.add(new JLabel(""));

        pnWest_index.add(new JLabel("Параметры sql для index:"));
        pnWest_index.add(pnWest1_index);

        pnWest_index.add(new JLabel("Методы драйвера:"));
        pnWest_index.add(rb_getSql4CreateIndex);
        pnWest_index.add(rb_getSql4DropIndex);

        ButtonGroup buttonGroup_index = new ButtonGroup();
        buttonGroup_index.add(rb_getSql4CreateIndex);
        buttonGroup_index.add(rb_getSql4DropIndex);

        JSplitPane spCenterV_index = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_index), new JScrollPane(textLog_index));
        JSplitPane spCenterH_index = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_index), spCenterV_index);

        spCenterV_index.setDividerLocation(170);
        spCenterH_index.setDividerLocation(370);

        pnMain_index.add(spCenterH_index, BorderLayout.CENTER);

        JPanel pnSouth_index = new JPanel();
        pnSouth_index.add(btnRun_index);
        pnMain_index.add(pnSouth_index, BorderLayout.SOUTH);

        //--- панель тестирования  PKey *************
        JPanel pnMain_pk = new JPanel(new BorderLayout());
        JPanel pnWest_pk = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_pk = new JPanel(new GridLayout(5, 4));

        pnWest1_pk.add(new JLabel("схема"));
        pnWest1_pk.add(fldSchema_pk);
        pnWest1_pk.add(new JLabel(""));
        pnWest1_pk.add(new JLabel(""));

        pnWest1_pk.add(new JLabel("таблица"));
        pnWest1_pk.add(fldTable_pk);
        pnWest1_pk.add(new JLabel(""));
        pnWest1_pk.add(new JLabel(""));

        pnWest1_pk.add(new JLabel("constraint"));
        pnWest1_pk.add(fldCName_pk);
        pnWest1_pk.add(new JLabel(""));
        pnWest1_pk.add(new JLabel(""));

        pnWest1_pk.add(new JLabel("колонка 1"));
        pnWest1_pk.add(fldColumn1_pk);
        pnWest1_pk.add(new JLabel(""));
        pnWest1_pk.add(new JLabel(""));

        pnWest1_pk.add(new JLabel("колонка 2"));
        pnWest1_pk.add(fldColumn2_pk);
        pnWest1_pk.add(new JLabel(""));
        pnWest1_pk.add(new JLabel(""));

        pnWest_pk.add(new JLabel("Параметры sql для PKey:"));
        pnWest_pk.add(pnWest1_pk);

        pnWest_pk.add(new JLabel("Методы драйвера:"));
        pnWest_pk.add(rb_getSql4CreatePkConstraint);
        pnWest_pk.add(rb_getSql4DropPkConstraint);

        ButtonGroup buttonGroup_pk = new ButtonGroup();
        buttonGroup_pk.add(rb_getSql4CreatePkConstraint);
        buttonGroup_pk.add(rb_getSql4DropPkConstraint);

        JSplitPane spCenterV_pk = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_pk), new JScrollPane(textLog_pk));
        JSplitPane spCenterH_pk = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_pk), spCenterV_pk);

        spCenterV_pk.setDividerLocation(170);
        spCenterH_pk.setDividerLocation(370);

        pnMain_pk.add(spCenterH_pk, BorderLayout.CENTER);

        JPanel pnSouth_pk = new JPanel();
        pnSouth_pk.add(btnRun_pk);
        pnMain_pk.add(pnSouth_pk, BorderLayout.SOUTH);

        //--- панель тестирования  FKey *************
        JPanel pnMain_fk = new JPanel(new BorderLayout());
        JPanel pnWest_fk = new JPanel(new VerticalFlowLayout(0, 0, 0, true, false));
        JPanel pnWest1_fk = new JPanel(new GridLayout(9, 4));

        pnWest1_fk.add(new JLabel(""));
        pnWest1_fk.add(new JLabel("FK"));
        pnWest1_fk.add(new JLabel("Referee"));
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("схема"));
        pnWest1_fk.add(fldSchema_fk);
        pnWest1_fk.add(fldRefereeSchema_fk);
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("таблица"));
        pnWest1_fk.add(fldTable_fk);
        pnWest1_fk.add(fldRefereeTable_fk);
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("constraint"));
        pnWest1_fk.add(fldCName_fk);
        pnWest1_fk.add(fldRefereeCName_fk);
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("DeleteRule"));
        comboDeleteRule_fk.addItem(ForeignKeyRule.NOACTION);
        comboDeleteRule_fk.addItem(ForeignKeyRule.CASCADE);
        comboDeleteRule_fk.addItem(ForeignKeyRule.SETNULL);
        comboDeleteRule_fk.addItem(ForeignKeyRule.SETDEFAULT);
        pnWest1_fk.add(comboDeleteRule_fk);
        pnWest1_fk.add(new JLabel(""));
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("UpdateRule"));
        comboUpdateRule_fk.addItem(ForeignKeyRule.NOACTION);
        comboUpdateRule_fk.addItem(ForeignKeyRule.CASCADE);
        comboUpdateRule_fk.addItem(ForeignKeyRule.SETNULL);
        comboUpdateRule_fk.addItem(ForeignKeyRule.SETDEFAULT);
        pnWest1_fk.add(comboUpdateRule_fk);
        pnWest1_fk.add(new JLabel(""));
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("Deferrable"));
        pnWest1_fk.add(fldDeferrable_fk);
        pnWest1_fk.add(new JLabel(""));
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("колонка 1"));
        pnWest1_fk.add(fldColumn1_fk);
        pnWest1_fk.add(fldRefereeColumn1_fk);
        pnWest1_fk.add(new JLabel(""));

        pnWest1_fk.add(new JLabel("колонка 2"));
        pnWest1_fk.add(fldColumn2_fk);
        pnWest1_fk.add(fldRefereeColumn2_fk);
        pnWest1_fk.add(new JLabel(""));

        pnWest_fk.add(new JLabel("Параметры sql для FKey:"));
        pnWest_fk.add(pnWest1_fk);

        pnWest_fk.add(new JLabel("Методы драйвера:"));
        pnWest_fk.add(rb_getSql4CreateFkConstraint);
        pnWest_fk.add(rb_getSql4CreateFkConstraint2);
        pnWest_fk.add(rb_getSql4DropFkConstraint);

        ButtonGroup buttonGroup_fk = new ButtonGroup();
        buttonGroup_fk.add(rb_getSql4CreateFkConstraint);
        buttonGroup_fk.add(rb_getSql4CreateFkConstraint2);
        buttonGroup_fk.add(rb_getSql4DropFkConstraint);

        JSplitPane spCenterV_fk = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_fk), new JScrollPane(textLog_fk));
        JSplitPane spCenterH_fk = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(pnWest_fk), spCenterV_fk);

        spCenterV_fk.setDividerLocation(170);
        spCenterH_fk.setDividerLocation(370);

        pnMain_fk.add(spCenterH_fk, BorderLayout.CENTER);

        JPanel pnSouth_fk = new JPanel();
        pnSouth_fk.add(btnRun_fk);
        pnMain_fk.add(pnSouth_fk, BorderLayout.SOUTH);

        //--- панель тестирования userssql *************
        JPanel pnMain_user = new JPanel(new BorderLayout());

        JSplitPane spCenter1_user = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textLog_user), new JScrollPane(table_user));
        JSplitPane spCenter2_user = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textSql_user), spCenter1_user);
        spCenter1_user.setDividerLocation(100);
        spCenter2_user.setDividerLocation(100);
        table_user.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_user.setColumnSelectionAllowed(true);
        pnMain_user.add(spCenter2_user, BorderLayout.CENTER);

        JPanel pnSouth_user = new JPanel();
        pnSouth_user.add(btnRun_user);
        pnSouth_user.add(btnSelect_user);

        pnMain_user.add(pnSouth_user, BorderLayout.SOUTH);

        //--- панель getTypeInfo *************
        JPanel pnMain_typeinfo = new JPanel(new BorderLayout());

        table_typeinfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_typeinfo.setColumnSelectionAllowed(true);
        pnMain_typeinfo.add(new JScrollPane(table_typeinfo), BorderLayout.CENTER);
        JPanel pnSouth_typeinfo = new JPanel();
        pnSouth_typeinfo.add(btnMD_typeinfo);
        pnMain_typeinfo.add(pnSouth_typeinfo, BorderLayout.SOUTH);

        //--- панель getSelectInfo *************
        JPanel pnMain_selectinfo = new JPanel(new BorderLayout());

        table_selectinfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table_selectinfo.setColumnSelectionAllowed(true);
        pnMain_selectinfo.add(new JScrollPane(table_selectinfo), BorderLayout.CENTER);
        JPanel pnSouth_selectinfo = new JPanel();
        pnSouth_selectinfo.add(btnMD_selectinfo);
        pnMain_selectinfo.add(pnSouth_selectinfo, BorderLayout.SOUTH);
        pnMain_selectinfo.add(new JScrollPane(textSql_selectinfo), BorderLayout.NORTH);

        //--- общая часть ---
        tabbedPane.addTab("connection", pnMain_connect);
        tabbedPane.addTab("jdbc-setschema", pnMain_setschema);
        tabbedPane.addTab("jdbc-select", pnMain_select);
        tabbedPane.addTab("jdbc-table", pnMain_table);
        tabbedPane.addTab("platypus-field", pnMain_field);

        tabbedPane.addTab("jdbc-index", pnMain_index);
        tabbedPane.addTab("jdbc-pkey", pnMain_pk);
        tabbedPane.addTab("jdbc-fkey", pnMain_fk);
        tabbedPane.addTab("platypus-commentDs", pnMain_commentDS);
        tabbedPane.addTab("jdbc-userScript", pnMain_user);
        tabbedPane.addTab("jdbc-getTypeInfo", pnMain_typeinfo);
        tabbedPane.addTab("jdbc-getSelectInfo", pnMain_selectinfo);
        getContentPane().add(tabbedPane, null);

        // задать отработку событий у всех компонентов
        comboDB_connect.addActionListener(clickAction);
        btnConnection_connect.addActionListener(clickAction);
        btnPlatypusConnection_connect.addActionListener(clickAction);

        btnRun_setschema.addActionListener(clickAction);
        btnRun_getschema.addActionListener(clickAction);

        rb_getSql4SchemasEnumeration.addActionListener(clickAction);
        rb_getSql4TableColumns.addActionListener(clickAction);
        rb_getSql4TablesEnumeration.addActionListener(clickAction);
        rb_getSql4ColumnsComments.addActionListener(clickAction);
        rb_getSql4TableComments.addActionListener(clickAction);
        rb_getSql4Indexes.addActionListener(clickAction);
        rb_getSql4TableForeignKeys.addActionListener(clickAction);
        rb_getSql4TablePrimaryKeys.addActionListener(clickAction);

        btnRun_select.addActionListener(clickAction);

        rb_getSql4EmptyTableCreation.addActionListener(clickAction);
        rb_getSql4DropTable.addActionListener(clickAction);
        rb_getSql4CreateTableComment.addActionListener(clickAction);
        btnRun_table.addActionListener(clickAction);

        rb_getSql4FieldDefinition.addActionListener(clickAction);
        rb_getSqls4ModifyingField.addActionListener(clickAction);
        rb_getSqls4RenamingField.addActionListener(clickAction);
        rb_getSql4DroppingField.addActionListener(clickAction);
        rb_getSql4CreateColumnComment.addActionListener(clickAction);
        rb_getSql4CreateField.addActionListener(clickAction);
        btnRun_field.addActionListener(clickAction);

        comboTable_field.addPopupMenuListener(popupMenuListener);
        comboField_field.addPopupMenuListener(popupMenuListener);
        comboField_field.addActionListener(clickAction);

        rb_getSql4CreateIndex.addActionListener(clickAction);
        rb_getSql4DropIndex.addActionListener(clickAction);
        btnRun_index.addActionListener(clickAction);

        rb_getSql4CreatePkConstraint.addActionListener(clickAction);
        rb_getSql4DropPkConstraint.addActionListener(clickAction);
        btnRun_pk.addActionListener(clickAction);

        rb_getSql4CreateFkConstraint.addActionListener(clickAction);
        rb_getSql4CreateFkConstraint2.addActionListener(clickAction);
        rb_getSql4DropFkConstraint.addActionListener(clickAction);
        btnRun_fk.addActionListener(clickAction);

        btnRun_user.addActionListener(clickAction);
        btnSelect_user.addActionListener(clickAction);

        rb2_getSql4SchemasEnumeration.addActionListener(clickAction);
        rb2_getSql4TableColumns.addActionListener(clickAction);
        rb2_getSql4TablesEnumeration.addActionListener(clickAction);
        rb2_getSql4ColumnsComments.addActionListener(clickAction);
        rb2_getSql4TableComments.addActionListener(clickAction);
        rb2_getSql4Indexes.addActionListener(clickAction);
        rb2_getSql4TableForeignKeys.addActionListener(clickAction);
        rb2_getSql4TablePrimaryKeys.addActionListener(clickAction);

        btnGetColumnCommentFromCommentsDs_commentDS.addActionListener(clickAction);
        btnGetColumnNameFromCommentsDs_commentDS.addActionListener(clickAction);
        btnGetTableCommentFromCommentsDs_commentDS.addActionListener(clickAction);
        btnGetTableNameFromCommentsDs_commentDS.addActionListener(clickAction);

        btnMD_typeinfo.addActionListener(clickAction);

        btnMD_selectinfo.addActionListener(clickAction);

    }

    /**
     * Выполнить sql-скрипт c Select и вывести таблицу результатов
     *
     * @param tabIndex номер панели в JTabPanel
     */
    private void runSelect(int tabIndex) {
        String sqls[] = null;

        JTextArea textLog = null;
        JTextArea textSql = null;
        JTable table = null;
        switch (tabIndex) {
            case 2:
                sqls = sqls_select;
                textSql = textSql_select;
                textLog = textLog_select;
                table = table_select;
                break;
            case 9:
                sqls = new String[]{textSql_user.getText()};
                textSql = textSql_user;
                textLog = textLog_user;
                table = table_user;
                break;
            case 10:
                try {
                    table_typeinfo.setModel(new JDBCModel(connectJDBC.getMetaData().getTypeInfo()));
                } catch (SQLException ex) {
                    Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 11:
                Statement st = null;
                try {
                    table_selectinfo.setModel(new SqlFieldModel(null));
                    st = connectJDBC.createStatement();
                    ResultSet executeQuery = st.executeQuery(textSql_selectinfo.getText());
                    ResultSetMetaData metaData = executeQuery.getMetaData();
                    table_selectinfo.setModel(new SqlFieldModel(metaData));
                } catch (SQLException ex) {
                    Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (st != null) {
                        try {
                            st.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                break;
        }
        assert textLog != null;
        assert table != null;

        if (sqls != null && sqls.length > 0) {
            Statement statementJDBC = null;
            ResultSet rsJDBC = null;
            try {
                textLog.append(sqls[0] + "\nexecuteQuery: ");
                long time = System.currentTimeMillis();
                statementJDBC = connectJDBC.createStatement();
                rsJDBC = statementJDBC.executeQuery(sqls[0]);
                table.setModel(new JDBCModel(rsJDBC));
                textLog.append("Ok!!!");
                textLog.append("  Time: " + ((double) (System.currentTimeMillis() - time)) / 1000 + " s  Rows: " + table.getRowCount() + "\n");
            } catch (SQLException ex) {
                textLog.append("Error !!!\nException: " + ex + "\n\n");
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (rsJDBC != null) {
                    try {
                        rsJDBC.close();
                    } catch (SQLException ex) {
                        textLog.append("Error !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (statementJDBC != null) {
                    try {
                        statementJDBC.close();
                    } catch (SQLException ex) {
                        textLog.append("Error !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
    }

    /**
     * Выполнить sql-скрипт c DDL
     *
     * @param tabIndex номер панели в JTabPanel
     */
    private void runExecute(int tabIndex) {

        String sqls[] = null;

        JTextArea textLog = null;
        JTextArea textSql = null;
        switch (tabIndex) {
            case 3:
                sqls = sqls_table;
                textSql = textSql_table;
                textLog = textLog_table;
                break;
            case 4:
                sqls = sqls_field;
                textSql = textSql_field;
                textLog = textLog_field;
                break;
            case 5:
                sqls = sqls_index;
                textSql = textSql_index;
                textLog = textLog_index;
                break;
            case 6:
                sqls = sqls_pk;
                textSql = textSql_pk;
                textLog = textLog_pk;
                break;
            case 7:
                sqls = sqls_fk;
                textSql = textSql_fk;
                textLog = textLog_fk;
                break;

            case 9:
                sqls = new String[]{textSql_user.getText()};
                textSql = textSql_user;
                textLog = textLog_user;
                break;

        }
        if (sqls != null && sqls.length > 0) {
            assert textLog != null;
            try {
                for (String s : sqls) {
                    textLog.append(s);
                    textLog.append("\nExecute: ");
                    long time = System.currentTimeMillis();
                    if (tabIndex == 4) {
                        SqlCompiledQuery q = new SqlCompiledQuery(client, null, s);
                        q.enqueueUpdate();
                        Map<String, List<Change>> changeLogs = new HashMap<>();
                        changeLogs.put(null, q.getFlow().getChangeLog());
                        client.commit(changeLogs, null, null);
                    } else {
                        try (Statement statementJDBC = connectJDBC.createStatement()) {
                            statementJDBC.execute(s);
                        }
                    }
                    textLog.append("Ok!!!");
                    textLog.append("  Time: " + ((double) (System.currentTimeMillis() - time)) / 1000 + " s\n");
                }
            } catch (Exception ex) {
                textLog.append("Error !!!\nException: " + ex + "\n\n");
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Обработка выбора всех JRadioButton для формирования и вывода sql-скриптов
     *
     * @param source текущий объект JRadioButton
     * @param tabIndex номер панели в JTabPanel
     */
    private void clickRadioButton(Object source, int tabIndex) {
        JTextArea textLog = textLog_connect;
        JTextArea textSql = textLog_connect;
        String sqls[] = null;
        String schemaName = "";
        String tableName = "";
        String indexName = "";
        String pkFieldName = "";
        String commentTable = "";

        Set<String> tablesSet = new HashSet();

        switch (tabIndex) {
            case 2:
                textSql = textSql_select;
                textLog = textLog_select;

                schemaName = fldSchema_select.getText();
                tablesSet.clear();
                tableName = fldTable1_select.getText();
                if (tableName != null && !tableName.isEmpty()) {
                    tablesSet.add(tableName);
                }
                tableName = fldTable2_select.getText();
                if (tableName != null && !tableName.isEmpty()) {
                    tablesSet.add(tableName);
                }

                break;
            case 3:
                textSql = textSql_table;
                textLog = textLog_table;

                schemaName = fldSchema_table.getText();
                tableName = fldTable_table.getText();
                pkFieldName = fldPkField_table.getText();
                commentTable = fldComment_table.getText();

                break;
            case 4:
                textSql = textSql_field;
                textLog = textLog_field;
                break;
            case 5:
                textSql = textSql_index;
                textLog = textLog_index;

                schemaName = fldSchema_index.getText();
                tableName = fldTable_index.getText();
                indexName = fldIndex_index.getText();

                break;

            case 6:
                textSql = textSql_pk;
                textLog = textLog_pk;

                schemaName = fldSchema_pk.getText();
                break;

            case 7:
                textSql = textSql_fk;
                textLog = textLog_fk;

                schemaName = fldSchema_fk.getText();
                break;
            case 8:
                textSql = textSql_commentDS;
                textLog = textLog_commentDS;

                schemaName = fldSchema_commentDS.getText();
                tablesSet.clear();
                tableName = fldTable1_commentDS.getText();
                if (tableName != null && !tableName.isEmpty()) {
                    tablesSet.add(tableName);
                }
                tableName = fldTable2_commentDS.getText();
                if (tableName != null && !tableName.isEmpty()) {
                    tablesSet.add(tableName);
                }

                break;

        }
        if (connectJDBC != null && platypusDriver != null) {
            if (source == rb_getSql4SchemasEnumeration) {
                sqls = new String[]{platypusDriver.getSql4SchemasEnumeration()};
            }
            if (source == rb_getSql4TableColumns) {
                sqls = new String[]{platypusDriver.getSql4TableColumns(schemaName, tablesSet)};
            }
            if (source == rb_getSql4TablesEnumeration) {
                sqls = new String[]{platypusDriver.getSql4TablesEnumeration(schemaName)};
            }
            if (source == rb_getSql4ColumnsComments) {
                sqls = new String[]{platypusDriver.getSql4ColumnsComments(schemaName, tablesSet)};
            }
            if (source == rb_getSql4TableComments) {
                sqls = new String[]{platypusDriver.getSql4TableComments(schemaName, tablesSet)};
            }
            if (source == rb_getSql4Indexes) {
                sqls = new String[]{platypusDriver.getSql4Indexes(schemaName, tablesSet)};
            }
            if (source == rb_getSql4TableForeignKeys) {
                sqls = new String[]{platypusDriver.getSql4TableForeignKeys(schemaName, tablesSet)};
            }
            if (source == rb_getSql4TablePrimaryKeys) {
                sqls = new String[]{platypusDriver.getSql4TablePrimaryKeys(schemaName, tablesSet)};
            }

            if (source == rb_getSql4EmptyTableCreation) {
                sqls = new String[]{platypusDriver.getSql4EmptyTableCreation(schemaName, tableName, pkFieldName)};
            }
            if (source == rb_getSql4CreateTableComment) {
                sqls = new String[]{platypusDriver.getSql4CreateTableComment(schemaName, tableName, commentTable)};
            }
            if (source == rb_getSql4DropTable) {
                sqls = new String[]{platypusDriver.getSql4DropTable(schemaName, tableName)};
            }

            if (source == rb_getSql4CreateIndex) {
                DbTableIndexSpec indexSpec = makeIndexSpec();
                sqls = new String[]{platypusDriver.getSql4CreateIndex(schemaName, tableName, indexSpec)};
            }

            if (source == rb_getSql4DropIndex) {
                sqls = new String[]{platypusDriver.getSql4DropIndex(schemaName, tableName, indexName)};
            }

            if (source == rb_getSql4CreateFkConstraint) {
                ForeignKeySpec fkSpec = makeFkSpec(fldColumn1_fk.getText(), fldRefereeColumn1_fk.getText());
                sqls = new String[]{platypusDriver.getSql4CreateFkConstraint(schemaName, fkSpec)};
            }
            if (source == rb_getSql4CreateFkConstraint2) {
                ForeignKeySpec fkSpec1 = makeFkSpec(fldColumn1_fk.getText(), fldRefereeColumn1_fk.getText());
                ForeignKeySpec fkSpec2 = makeFkSpec(fldColumn2_fk.getText(), fldRefereeColumn2_fk.getText());
                List<ForeignKeySpec> fkSpecs = new ArrayList<>();
                fkSpecs.add(fkSpec1);
                fkSpecs.add(fkSpec2);

                sqls = new String[]{platypusDriver.getSql4CreateFkConstraint(schemaName, fkSpecs)};
            }

            if (source == rb_getSql4DropFkConstraint) {
                ForeignKeySpec fkSpec = makeFkSpec(fldColumn1_fk.getText(), fldRefereeColumn1_fk.getText());
                sqls = new String[]{platypusDriver.getSql4DropFkConstraint(schemaName, fkSpec)};
            }
            if (source == rb_getSql4CreatePkConstraint) {
                PrimaryKeySpec pkSpec1 = makePkSpec(fldColumn1_pk.getText());
                List<PrimaryKeySpec> pkSpecs = new ArrayList<>();
                pkSpecs.add(pkSpec1);
                String column2 = fldColumn2_pk.getText();
                if (!column2.isEmpty()) {
                    PrimaryKeySpec pkSpec2 = makePkSpec(fldColumn2_pk.getText());
                    pkSpecs.add(pkSpec2);
                }
                sqls = platypusDriver.getSql4CreatePkConstraint(schemaName, pkSpecs);
            }
            if (source == rb_getSql4DropPkConstraint) {
                PrimaryKeySpec pkSpec = makePkSpec(fldColumn1_pk.getText());
                sqls = new String[]{platypusDriver.getSql4DropPkConstraint(schemaName, pkSpec)};
            }

        }
        // --- Platypus ---
        if (driver != null) {
            Field newField_field = makeField();
            if (source == rb_getSql4FieldDefinition) {

                sqls = new String[]{driver.getSql4FieldDefinition(newField_field)};
            }

            if (source == rb_getSqls4ModifyingField) {
                tableName = (String) comboTable_field.getSelectedItem();
                makeField();
                sqls = driver.getSqls4ModifyingField(schemaName, tableName, oldField_field, newField_field);
            }

            if (source == rb_getSqls4RenamingField) {
                tableName = (String) comboTable_field.getSelectedItem();
                makeField();
                if (oldField_field != null) {
                    sqls = driver.getSqls4RenamingField(schemaName, tableName, oldField_field.getName(), newField_field);
                }
            }
            if (source == rb_getSql4DroppingField) {
                tableName = (String) comboTable_field.getSelectedItem();
                sqls = driver.getSql4DroppingField(schemaName, tableName, fldName_field.getText());
            }

            if (source == rb_getSql4CreateColumnComment) {
                tableName = (String) comboTable_field.getSelectedItem();
                sqls = driver.getSql4CreateColumnComment(fldSchema_field.getText(), tableName, fldName_field.getText(), fldDescription_field.getText());

            }
            if (source == rb_getSql4CreateField) {
                tableName = (String) comboTable_field.getSelectedItem();
                String sName = fldSchema_field.getText();
                if (!sName.isEmpty()) {
                    tableName = driver.wrapName(sName) + "." + driver.wrapName(tableName);
                }
                sqls = new String[]{String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, tableName) + driver.getSql4FieldDefinition(newField_field)};
            }

            if (source == rb2_getSql4SchemasEnumeration) {
                sqls = new String[]{driver.getSql4SchemasEnumeration()};
            }
            if (source == rb2_getSql4TableColumns) {
                sqls = new String[]{driver.getSql4TableColumns(schemaName, tablesSet)};
            }
            if (source == rb2_getSql4TablesEnumeration) {
                sqls = new String[]{driver.getSql4TablesEnumeration(schemaName)};
            }
            if (source == rb2_getSql4ColumnsComments) {
                sqls = new String[]{driver.getSql4ColumnsComments(schemaName, tablesSet)};
            }
            if (source == rb2_getSql4TableComments) {
                sqls = new String[]{driver.getSql4TableComments(schemaName, tablesSet)};
            }
            if (source == rb2_getSql4Indexes) {
                sqls = new String[]{driver.getSql4Indexes(schemaName, tablesSet)};
            }
            if (source == rb2_getSql4TableForeignKeys) {
                sqls = new String[]{driver.getSql4TableForeignKeys(schemaName, tablesSet)};
            }
            if (source == rb2_getSql4TablePrimaryKeys) {
                sqls = new String[]{driver.getSql4TablePrimaryKeys(schemaName, tablesSet)};
            }

        }

        String all_sql = "";
        if (sqls != null) {
            for (String s : sqls) {
                all_sql = all_sql + s + ";\n";
            }
            textSql.setText(all_sql);
            switch (tabIndex) {
                case 2:
                    sqls_select = sqls;
                    break;
                case 3:
                    sqls_table = sqls;
                    break;
                case 4:
                    sqls_field = sqls;
                    break;
                case 5:
                    sqls_index = sqls;
                    break;
                case 6:
                    sqls_pk = sqls;
                    break;
                case 7:
                    sqls_fk = sqls;
                    break;
                case 8:
                    sqls_commentsDS = sqls;
                    break;

            }
        }
    }

    /**
     * создать соединение с базой данных для Platypus-клиента
     *
     * @param aUrl строка соединения
     * @param aSchema имя схемы
     * @param aUser имя пользователя
     * @param aPassword пароль пользователя
     * @param createSysTables необходимость проверки для выполнения скриптов
     * инициализации при подключении к БД
     * @return
     * @throws Exception
     */
    private DatabasesClient createPlatypusClient(String aUrl, String aSchema, String aUser, String aPassword, boolean createSysTables) throws Exception {
        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.INFO, "Start creating connection to schema {0}", aSchema);
        try {
            DbConnectionSettings settings = new DbConnectionSettings(aUrl, aUser, aPassword);
            DatabasesClient dbClient = new DatabasesClientWithResource(settings, null).getClient();
            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.INFO, "Connect to schema {0} created", dbClient.getConnectionSchema(null));
            return dbClient;
        } catch (Exception ex) {
            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.INFO, "Connect to schema {0} not created", aSchema);
            throw ex;
        }
    }
    /**
     * обработка событий
     */
    ActionListener clickAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            int tabIndex = tabbedPane.getSelectedIndex();

            Object source = e.getSource();
            if (source == comboDB_connect) {
                dbChoice();
            }
            if (source == btnConnection_connect) {
                dbConnect();
            }
            if (source == btnPlatypusConnection_connect) {
                platypusConnect();
            }
            if (source == btnRun_select) {
                runSelect(tabIndex);
            }
            if (source instanceof JRadioButton) {
                clickRadioButton(source, tabIndex);
            }

            if (source == btnRun_setschema) {
                JTextArea textLog = textLog_setschema;
                String schemaName = fldSchema_setschema.getText();
                if (connectJDBC != null) {
                    try {
                        textLog.append("задать текущую схему =  \"" + schemaName + "\": ");

                        platypusDriver.applyContextToConnection(connectJDBC, schemaName);
                        textLog.append("Ok !!!\n");
                    } catch (Exception ex) {
                        textLog.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (source == btnRun_getschema) {
                JTextArea textLog = textLog_setschema;
                if (connectJDBC != null) {
                    try {
                        String currentSchema = platypusDriver.getConnectionContext(connectJDBC);
                        textLog.setText(String.format("Current schema: %s\n", currentSchema));
                    } catch (Exception ex) {
                        textLog.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (source == btnRun_table) {
                runExecute(tabIndex);
            }
            if (source == btnRun_field) {
                runExecute(tabIndex);
            }
            if (source == comboField_field) {
                choiceField();
            }

            if (source == btnRun_index) {
                runExecute(tabIndex);
            }
            if (source == btnRun_pk) {
                runExecute(tabIndex);
            }
            if (source == btnRun_fk) {
                runExecute(tabIndex);
            }
            if (source == btnRun_user) {
                runExecute(tabIndex);
            }
            if (source == btnSelect_user) {
                runSelect(tabIndex);
            }
            if (source == btnMD_typeinfo) {
                runSelect(tabIndex);
            }
            if (source == btnMD_selectinfo) {
                runSelect(tabIndex);
            }

            // --- Platypus ---
            if (driver != null) {

                if (source == btnGetColumnCommentFromCommentsDs_commentDS) {
                    try {
                        SqlCompiledQuery q = new SqlCompiledQuery(client, null, sqls_commentsDS[0]);
                        Rowset rs = q.executeQuery(null, null);
                        textLog_commentDS.append("count records=" + rs.size() + "\n");
                        rs.next();
                        String res = driver.getColumnCommentFromCommentsDs(rs);
                        res = (res != null ? "'" + res + "'" : res);
                        textLog_commentDS.append("first result=" + res + "\n");

                    } catch (Exception ex) {
                        textLog_commentDS.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (source == btnGetColumnNameFromCommentsDs_commentDS) {
                    try {
                        SqlCompiledQuery q = new SqlCompiledQuery(client, null, sqls_commentsDS[0]);
                        Rowset rs = q.executeQuery(null, null);
                        textLog_commentDS.append("count records=" + rs.size() + "\n");
                        rs.next();
                        String res = driver.getColumnNameFromCommentsDs(rs);
                        res = (res != null ? "'" + res + "'" : res);
                        textLog_commentDS.append("first result=" + res + "\n");

                    } catch (Exception ex) {
                        textLog_commentDS.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (source == btnGetTableCommentFromCommentsDs_commentDS) {
                    try {
                        SqlCompiledQuery q = new SqlCompiledQuery(client, null, sqls_commentsDS[0]);
                        Rowset rs = q.executeQuery(null, null);
                        textLog_commentDS.append("count records=" + rs.size() + "\n");
                        rs.next();
                        String res = driver.getTableCommentFromCommentsDs(rs);
                        res = (res != null ? "'" + res + "'" : res);
                        textLog_commentDS.append("first result=" + res + "\n");

                    } catch (Exception ex) {
                        textLog_commentDS.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (source == btnGetTableNameFromCommentsDs_commentDS) {
                    try {
                        SqlCompiledQuery q = new SqlCompiledQuery(client, null, sqls_commentsDS[0]);
                        Rowset rs = q.executeQuery(null, null);
                        textLog_commentDS.append("count records=" + rs.size() + "\n");
                        rs.next();
                        String res = driver.getTableNameFromCommentsDs(rs);
                        res = (res != null ? "'" + res + "'" : res);
                        textLog_commentDS.append("first result=" + res + "\n");

                    } catch (Exception ex) {
                        textLog_commentDS.append("ERROR !!!\nException: " + ex + "\n\n");
                        Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };
    /**
     * обработка событий
     */
    PopupMenuListener popupMenuListener = new PopupMenuListener() {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            Object source = e.getSource();
            if (source == comboTable_field) {
                getTables_field();
            }
            if (source == comboField_field) {
                getFields_field();
            }
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
    };

    /**
     * установка переменных при выборе БД
     */
    private void dbChoice() {
        String dbName = (String) comboDB_connect.getSelectedItem();
        Object[] dbSets = null;
        if (dbName != null) {
            dbSets = jdbcSets.get(dbName);
        }
        if (dbSets != null && dbSets.length > 5) {
            fldDriver_connect.setText((String) dbSets[0]);
            fldUrl_connect.setText((String) dbSets[1]);
            fldSchema_connect.setText((String) dbSets[2]);
            fldUser_connect.setText((String) dbSets[3]);
            fldPassword_connect.setText((String) dbSets[4]);
            platypusDriver = (SqlDriver) dbSets[5];
        }
    }

    private void setSchemasNames() {
        String schemaName = fldSchema_connect.getText();
        {
            fldSchema_setschema.setText(schemaName);
            fldSchema_select.setText(schemaName);
            fldSchema_table.setText(schemaName);
            fldSchema_index.setText(schemaName);
            fldSchema_pk.setText(schemaName);
            fldSchema_fk.setText(schemaName);
            fldRefereeSchema_fk.setText(schemaName);
            fldSchema_commentDS.setText(schemaName);
        }
    }

    /**
     * установка jdbc-соединения с базой данных
     */
    private void dbConnect() {
        try {
            String driverName = fldDriver_connect.getText();
            textLog_connect.append("<< JDBC клиент >>\n");
            textLog_connect.append("регистрация \"" + driverName + "\": ");
            Class.forName(driverName);
            textLog_connect.append("Ok !!!\n");
            try {
                textLog_connect.append("подключение: ");
                connectJDBC = DriverManager.getConnection(fldUrl_connect.getText(), fldUser_connect.getText(), fldPassword_connect.getText());
                connectJDBC.setAutoCommit(false);
                textLog_connect.append("Ok !!!\n");
                setSchemasNames();
            } catch (SQLException ex) {
                textLog_connect.append("ERROR !!!\nException: " + ex + "\n\n");
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            textLog_connect.append("ERROR !!!\nException: " + ex + "\n\n");
            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * установка platypus-соединения с базой данных
     */
    private void platypusConnect() {
        String text = fldSchema_connect.getText();
        try {
            textLog_connect.append("<< Platypus клиент >>\n Create connection: ");
            client = createPlatypusClient(fldUrl_connect.getText(), fldSchema_connect.getText(), fldUser_connect.getText(), new String(fldPassword_connect.getPassword()), chkCreatePlatypusTables_connect.isSelected());
            textLog_connect.append("Ok !!!\n");

            DatabaseMdCache mdCache = client.getDbMetadataCache(null);
            driver = mdCache.getConnectionDriver();
            fldSchema_field.setText(fldSchema_connect.getText());

        } catch (Exception ex) {
            Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            textLog_connect.append("ERROR !!!\nException: " + ex + "\n\n");
        }

    }

    /**
     * сформировать список имен таблиц текущей схемы для тестирования DDL
     * скриптов работы с колонками таблицы (необходимо platypus-соединение с БД)
     *
     */
    private void getTables_field() {
        String schemaName = fldSchema_field.getText();

        comboTable_field.removeAllItems();
        JTextArea textLog = textLog_field;
        textLog.append("\n<< Установить список таблиц >>");
        if (driver != null) {

            try {
                String sql = driver.getSql4TablesEnumeration(schemaName);
                textLog.append(sql + "\nexecuteQuery: ");
                SqlCompiledQuery query = new SqlCompiledQuery(client, null, sql);
                Rowset rowsetTablesList = query.executeQuery(null, null);
                Fields fieldsTable = rowsetTablesList.getFields();

                if (rowsetTablesList.first()) {
                    int tableColIndex = fieldsTable.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                    int tableTypeColIndex = fieldsTable.find(ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME);
                    int cnt = 0;
                    do {
                        // each table
                        String tableType = null;
                        if (tableTypeColIndex > 0) {
                            tableType = rowsetTablesList.getString(tableTypeColIndex);
                        }
                        if (tableType == null || tableType.equalsIgnoreCase(ClientConstants.JDBCPKS_TABLE_TYPE_TABLE)) {
                            String tableName = rowsetTablesList.getString(tableColIndex);
                            comboTable_field.addItem(tableName);
                            cnt++;
                        }
                    } while (rowsetTablesList.next());
                    textLog.append("Ok!!!     Rows count: " + cnt + "\n");
                }

            } catch (Exception ex) {
                textLog.append("Error !!!\nException: " + ex + "\n\n");
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            textLog.append("\nError !!! Нет Platypus-клиента\n\n");
        }

    }

    /**
     * сформировать список имен колонок текущей таблицы для тестирования DDL
     * скриптов работы с колонками таблицы (необходимо platypus-соединение с БД)
     *
     */
    private void getFields_field() {
        JTextArea textLog = textLog_field;

        comboField_field.removeAllItems();
        textLog.append("\n<< Установить список полей DbMetadataCache >>");
        if (client != null) {
            try {
                DatabaseMdCache mdCache = client.getDbMetadataCache(null);
                fields = mdCache.getTableMetadata((String) comboTable_field.getSelectedItem());
                for (Field f : fields.toCollection()) {
                    comboField_field.addItem(f.getName());
                }
                textLog.append("Ok!!!     Fields count: " + fields.getFieldsCount() + "\n");
            } catch (Exception ex) {
                textLog.append("Error !!!\nException: " + ex + "\n\n");
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            textLog.append("\nError !!! Нет Platypus-клиента\n\n");
        }
    }

    /**
     * выбор имени поля в текущей таблице для тестирования DDL скриптов работы с
     * колонками таблицы (необходимо platypus-соединение с БД)
     *
     */
    private void choiceField() {

        String fieldName = (String) comboField_field.getSelectedItem();
        oldField_field = fields.get(fieldName);

        if (oldField_field != null) {
            String name = oldField_field.getName();
            lbName_field.setText((name == null ? "" : oldField_field.getName()));
            DataTypeInfo typeInfo = oldField_field.getTypeInfo();
            String sqlType = "";
            if (typeInfo != null) {
                sqlType = typeInfo.getSqlTypeName();
            }
            lbType_field.setText((sqlType == null ? "" : sqlType));
            lbSize_field.setText("" + oldField_field.getSize());
            lbPrecision_field.setText("" + oldField_field.getPrecision());
            lbNullable_field.setSelected(oldField_field.isNullable());
            lbReadOnly_field.setSelected(oldField_field.isReadonly());
            lbSigned_field.setSelected(oldField_field.isSigned());
//            lbStrong4Insert_field.setSelected(oldField_field.isStrong4Insert());
            lbScale_field.setText("" + oldField_field.getScale());
            String description = oldField_field.getDescription();
            lbDescription_field.setText((description == null ? "" : description));
            lbPk_field.setSelected(oldField_field.isPk());

            String fkName = "";
            ForeignKeyRule fkDeleteRule = null;
            ForeignKeyRule fkUpdateRule = null;
            PrimaryKeySpec pk;
            String pkName = "";
            String pkField = "";
            String pkTable = "";
            String pkSchema = "";

            ForeignKeySpec fk = oldField_field.getFk();
            if (fk != null) {
                fkName = fk.getCName();
                fkDeleteRule = fk.getFkDeleteRule();
                fkUpdateRule = fk.getFkUpdateRule();
                pk = fk.getReferee();
                if (pk != null) {
                    pkName = pk.getCName();
                    pkField = pk.getField();
                    pkTable = pk.getTable();
                    pkSchema = pk.getSchema();
                }
            }

            lbFKName_field.setText((fkName == null ? "" : fkName));
            lbFKDeferrable_field.setSelected((fk == null ? false : fk.getFkDeferrable()));
            lbFKUpdateRule_field.setText((fkUpdateRule == null ? "" : fkUpdateRule.toString()));
            lbFKDeleteRule_field.setText((fkDeleteRule == null ? "" : fkDeleteRule.toString()));

            lbFKRefereeCName_field.setText((pkName == null ? "" : pkName));
            lbFKRefereeFieldName_field.setText((pkField == null ? "" : pkField));
            lbFKRefereeTableName_field.setText((pkTable == null ? "" : pkTable));
            lbFKRefereeSchemaName_field.setText((pkSchema == null ? "" : pkSchema));
        }
    }

    /**
     * сформировать описание колонки таблицы на основе заданных значений
     *
     * @return
     */
    private Field makeField() {
        JTextArea textLog = textLog_field;
        try {
            Field newField_field = new Field();
            newField_field.setSchemaName(fldSchema_field.getText());
            newField_field.setTableName((String) comboTable_field.getSelectedItem());
            newField_field.setName(fldName_field.getText());
            newField_field.setPk(fldPk_field.isSelected());
            newField_field.setNullable(fldNullable_field.isSelected());
            newField_field.setReadonly(fldReadOnly_field.isSelected());
//            newField_field.setStrong4Insert(fldStrong4Insert_field.isSelected());
            newField_field.setSigned(fldSigned_field.isSelected());
            String txtSize = fldSize_field.getText().trim();
            newField_field.setSize(txtSize.isEmpty() ? 0 : Integer.valueOf(txtSize));
            String txtScale = fldScale_field.getText().trim();
            newField_field.setScale(txtScale.isEmpty() ? 0 : Integer.valueOf(txtScale));
            String txtPrecision = fldScale_field.getText().trim();
            newField_field.setPrecision(txtPrecision.isEmpty() ? 0 : Integer.valueOf(txtPrecision));
            newField_field.setDescription(fldDescription_field.getText());

            DataTypeInfo typeInfo = new DataTypeInfo();
            String txtType = fldType_field.getText();
            typeInfo.setSqlType(driver.getJdbcTypeByRDBMSTypename(txtType));
            newField_field.setTypeInfo(typeInfo);
            return newField_field;
        } catch (Exception e) {
            textLog.append("\nОшибка при создании описания новой колонки!!!\nException:" + e + "\n\n");
        }
        return null;

    }

    /**
     * сформировать описание индекса на основе заданных значений
     *
     * @return
     */
    private DbTableIndexSpec makeIndexSpec() {
        JTextArea textLog = textLog_index;
        try {
            DbTableIndexSpec index = new DbTableIndexSpec();
            index.setName(fldIndex_index.getText());
            index.setClustered(fldClustered_index.isSelected());
            index.setHashed(fldHashed_index.isSelected());
            index.setUnique(fldUnique_index.isSelected());
            String columnName = fldColumn1_index.getText().trim();
            if (!columnName.isEmpty()) {
                index.addColumn(new DbTableIndexColumnSpec(columnName, !fldDesc1_index.isSelected()));
            }
            columnName = fldColumn2_index.getText().trim();
            if (!columnName.isEmpty()) {
                index.addColumn(new DbTableIndexColumnSpec(columnName, !fldDesc2_index.isSelected()));
            }
            return index;
        } catch (Exception e) {
            textLog.append("\nОшибка при создании спецификации индекса!!!\nException:" + e + "\n\n");
        }
        return null;
    }

    /**
     * сформировать описание первичного ключа на основе заданных значений
     *
     * @return
     */
    private PrimaryKeySpec makePkSpec(String aColumnName) {
        JTextArea textLog = textLog_pk;
        PrimaryKeySpec pk = new PrimaryKeySpec();
        try {
            pk.setSchema(fldSchema_pk.getText());
            pk.setTable(fldTable_pk.getText());
            pk.setCName(fldCName_pk.getText());
            pk.setField(aColumnName);
            return pk;
        } catch (Exception e) {
            textLog.append("\nОшибка при создании спецификации первичного ключа!!!\nException:" + e + "\n\n");
        }
        return null;
    }

    /**
     * сформировать описание внешнего ключа на основе заданных значений
     *
     * @return
     */
    private ForeignKeySpec makeFkSpec(String aColumnName, String aRefereeColumnName) {
        JTextArea textLog = textLog_fk;
        ForeignKeySpec fk = new ForeignKeySpec();
        try {
            fk.setSchema(fldSchema_fk.getText());
            fk.setTable(fldTable_fk.getText());
            fk.setCName(fldCName_fk.getText());
            fk.setField(aColumnName);
            fk.setFkDeferrable(fldDeferrable_fk.isSelected());
            fk.setFkDeleteRule((ForeignKeyRule) comboDeleteRule_fk.getSelectedItem());
            fk.setFkUpdateRule((ForeignKeyRule) comboUpdateRule_fk.getSelectedItem());
            PrimaryKeySpec pk = new PrimaryKeySpec();
            pk.setSchema(fldRefereeSchema_fk.getText());
            pk.setTable(fldRefereeTable_fk.getText());
            pk.setField(aRefereeColumnName);
            pk.setCName(fldRefereeCName_fk.getText());
            fk.setReferee(pk);
            return fk;
        } catch (Exception e) {
            textLog.append("\nОшибка при создании спецификации внешнего ключа!!!\nException:" + e + "\n\n");
        }
        return null;
    }

    private void logout() {
        if (connectJDBC != null) {
            try {
                connectJDBC.close();
            } catch (SQLException ex) {
                Logger.getLogger(SqlDriversTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SqlDriversTester sqlDriversTester = new SqlDriversTester();

    }
}
