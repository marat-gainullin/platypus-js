/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.eas.metadata.testdefine.DbTestDefine;
import com.eas.metadata.testdefine.DbTestDefine.Database;
import com.eas.metadata.dbdefines.TableDefine;
import com.eas.metadata.dbdefines.IndexDefine;
import com.eas.metadata.dbdefines.IndexColumnDefine;
import com.eas.metadata.dbdefines.FKeyDefine;
import com.eas.metadata.dbdefines.SourceDbSetting;
import com.eas.metadata.dbdefines.DestinationDbSetting;
import com.eas.metadata.dbdefines.DbConnection;
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
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.metadata.testdefine.Db2TestDefine;
import com.eas.metadata.testdefine.PostgreTestDefine;
import com.eas.metadata.testdefine.H2TestDefine;
import com.eas.metadata.testdefine.MsSqlTestDefine;
import com.eas.metadata.testdefine.MySqlTestDefine;
import com.eas.metadata.testdefine.OracleTestDefine;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vy
 */
public class MetadataSynchronizerTest2 {

    private int cntTabs = 0;
    private final String XML_NAME = "test.xml";

    private final SourceDbSetting[] sourceDbSetting = {
        new SourceDbSetting(new DbConnection("jdbc:oracle:thin:@research.office.altsoft.biz:1521/DBALT", "\"testUser\"", "test1", "ptest1"), Database.ORACLE, new OracleTestDefine()),
        new SourceDbSetting(new DbConnection("jdbc:postgresql://192.168.10.52:5432/tst_db", "\"testSchema\"", "user1", "puser1"), Database.POSTGRESQL, new PostgreTestDefine()),
        new SourceDbSetting(new DbConnection("jdbc:mysql://192.168.10.205:3306/test1", "`testSchema`", "test1", "test1"), Database.MYSQL, new MySqlTestDefine()),
        new SourceDbSetting(new DbConnection("jdbc:db2://192.168.10.52:50000/tst_db", "\"testSchema\"", "test1", "ptest1"), Database.DB2, new Db2TestDefine()),
        new SourceDbSetting(new DbConnection("jdbc:h2:tcp://localhost/~/test", "\"testSchema\"", "test1", "ptest1"), Database.H2, new H2TestDefine()), 
        new SourceDbSetting(new DbConnection("jdbc:jtds:sqlserver://192.168.10.52:1433/tst2_db", "[testSchema]", "test1", "ptest1"), Database.MSSQL, new MsSqlTestDefine()),
        new SourceDbSetting(new DbConnection("jdbc:mysql://192.168.10.52:3306/tst_db", "`testSchema`", "test1", "ptest1"), Database.MYSQL, new MySqlTestDefine())
    };
    private final DestinationDbSetting[] destinationDbSetting = {
        new DestinationDbSetting(new DbConnection("jdbc:oracle:thin:@research.office.altsoft.biz:1521/DBALT", "\"test user\"", "test1", "ptest1"), Database.ORACLE),
        new DestinationDbSetting(new DbConnection("jdbc:postgresql://192.168.10.52:5432/tst_db", "\"test schema\"", "user1", "puser1"), Database.POSTGRESQL),
        new DestinationDbSetting(new DbConnection("jdbc:mysql://192.168.10.205:3306/test2", "`test schema`", "test2", "test2"), Database.MYSQL),
        new DestinationDbSetting(new DbConnection("jdbc:db2://192.168.10.52:50000/tst_db", "\"test schema\"", "test1", "ptest1"), Database.DB2),
        new DestinationDbSetting(new DbConnection("jdbc:h2:tcp://localhost/~/test", "\"test schema\"", "test1", "ptest1"), Database.H2), 
        new DestinationDbSetting(new DbConnection("jdbc:jtds:sqlserver://192.168.10.52:1433/tst2_db", "[test schema]", "test1", "ptest1"), Database.MSSQL),
        new DestinationDbSetting(new DbConnection("jdbc:mysql://192.168.10.52:3306/tst_db", "`test schema`", "test1", "ptest1"), Database.MYSQL)
    };

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public MetadataSynchronizerTest2() {
    }

    @Test
    public void work() throws Exception {
        int i = 0;
        for (SourceDbSetting srcSetting : sourceDbSetting) {
            i++;
            int j = 0;
            for (DestinationDbSetting destSetting : destinationDbSetting) {
                j++;
                runAllTestsDb(srcSetting, destSetting,i+"_",j+"_");
            }
        }
    }

    private void runAllTestsDb(SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, String aPreffixSource, String aPreffixDestination) throws Exception {
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        DbConnection srcDbConnection = aSourceSetting.getDbConnection();
        DbConnection destDbConnection = aDestinationSetting.getDbConnection();
        Database srcDatabase = aSourceSetting.getDatabase();
        Database destDatabase = aDestinationSetting.getDatabase();
        assertNotNull(srcDbConnection);
        assertNotNull(destDbConnection);
        assertNotNull(srcDatabase);
        assertNotNull(destDatabase);
        String logName = aPreffixSource+srcDatabase + "_" + aPreffixDestination+destDatabase;
        Logger logger = MetadataSynchronizer.initLogger(MetadataSynchronizerTest.class.getName(), Level.ALL, false);
        try {
            logger.addHandler(MetadataSynchronizer.createFileHandler(logName + ".log", "UTF-8", new LineLogFormatter()));
            Date d1 = new Date();
            printText(cntTabs++, "*** runAllTestsDb ***    " + d1);
            printText(cntTabs, "source: \t(url=", srcDbConnection.getUrl(), " \tschema=", srcDbConnection.getSchema(), " \tuser=", srcDbConnection.getUser(), ")");
            printText(cntTabs, "destination: \t(url=", destDbConnection.getUrl(), " \tschema=", destDbConnection.getSchema(), " \tuser=", destDbConnection.getUser(), ")");

            clearSchema(aDestinationSetting.getDbConnection());
            runAllTestTables(logName, aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
            runAllTestsFields(logName, aSourceSetting, aDestinationSetting);
            runAllTestIndexes(logName, aSourceSetting, aDestinationSetting);
            runAllTestKeys(logName, aSourceSetting, aDestinationSetting);
            long s = (new Date().getTime() - d1.getTime()) / 1000;
            printText(--cntTabs, "*** end runAllTestsDb ***    (" + s/60 + " min " + s%60 + " sec)");
        } finally {
            MetadataSynchronizer.closeLogHandlers(logger);
        }
    }

    private void runAllTestTables(String aLogName, DbConnection aSourceConnection, DbConnection aDestinationConnection) throws Exception {
        printText(cntTabs++, "*** runAllTestTables ***");
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);
        TableDefine[] stateA = {
            new TableDefine("tt 1", "id 1", null),
            new TableDefine("ttTwo", "idTwo", ""),
            new TableDefine("tt 3", "id 3", "Comment for table")
        };
        TableDefine[] stateB = {
            new TableDefine("ttTwo", "idTwo", "Comment"),
            new TableDefine("tt 4", "id 4", "Comment for table tT4"),
            new TableDefine("tt 5", "id 5", null)
        };
        TableDefine[] stateAB1 = {
            new TableDefine("tt 1", "id 1", null),
            new TableDefine("ttTwo", "idTwo", ""),
            new TableDefine("tt 3", "id 3", "Comment for table"),
            new TableDefine("tt 5", "id 5", null)
        };

        runTestTables(aLogName, "test1", aSourceConnection, aDestinationConnection, stateA, stateA, false, null);
        runTestTables(aLogName, "test2", aSourceConnection, aDestinationConnection, stateB, stateAB1, true, "tT 1,Tt 5");
        runTestTables(aLogName, "test3", aSourceConnection, aDestinationConnection, stateA, stateA, false, "");

        printText(--cntTabs, "*** end runAllTestTables ***");
    }

    private void runAllTestsFields(String aLogName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
        printText(cntTabs++, "*** runAllTestsFields ***");
        
        
        String [] state1 = {"fieldOne","field two","FieldForDelete"};
        String [] state2 = {"fieldOne","field two"};
        
        runTestFields(aLogName, "test1", aSourceSetting, aDestinationSetting, "Tbl", state1, 10);
        runTestFields(aLogName, "test2", aSourceSetting, aDestinationSetting, "Tbl", state2, 10);
        runTestFields(aLogName, "test3", aSourceSetting, aDestinationSetting, "Tbl", state2, 20);

        printText(--cntTabs, "*** end runAllTestsFields ***");
    }

    private void runAllTestIndexes(String aLogName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
        printText(cntTabs++, "*** runAllTestIndexes ***");
        IndexDefine[] state1 = {
            //indexName, isClustered(=false !!!), isHashed, isUnique,arrayColumns
            new IndexDefine("Ind 1", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f 1", true)}),
            new IndexDefine("IndTwo", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f 2", true), new IndexColumnDefine("f 3", true)}),
            new IndexDefine("Ind 3", false, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f 1", true), new IndexColumnDefine("f 3", true)})
        };

        //?????????????????????? asc/desc !!!!!!!!!!!!!!!!!!
        IndexDefine[] state2 = {
            new IndexDefine("Ind 1", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f 1", false)}),
            new IndexDefine("IndTwo", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f 2", false), new IndexColumnDefine("f 3", false)}),
            new IndexDefine("Ind 3", false, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f 4", true), new IndexColumnDefine("f 3", false)})
        };

        IndexDefine[] state3 = {
            new IndexDefine("Ind 1", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f 1", true)}),
            new IndexDefine("IndTwo", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f 2", true), new IndexColumnDefine("f 3", true)}),
            new IndexDefine("Ind 3", false, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f 4", true), new IndexColumnDefine("f 3", true)})
        };

//        IndexDefine[] state4 = {
//            new IndexDefine("Ind1", false, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f4", true), new IndexColumnDefine("f3", true)}),
//            new IndexDefine("Ind2", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
//            new IndexDefine("Ind3", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true)})
//        };
//
//        IndexDefine[] state5 = {
//            new IndexDefine("Ind2", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
//            new IndexDefine("Ind3", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true), new IndexColumnDefine("f1", true)})
//        };
        IndexDefine[] state6 = {};
        runTestIndexes(aLogName, "test1", aSourceSetting, aDestinationSetting, state1);
        runTestIndexes(aLogName, "test2", aSourceSetting, aDestinationSetting, state2);
        runTestIndexes(aLogName, "test3", aSourceSetting, aDestinationSetting, state3);
//        runTestIndexes(aLogName, "test4", aSourceSetting, aDestinationSetting, state4);
//        runTestIndexes(aLogName, "test5", aSourceSetting, aDestinationSetting, state5);
        runTestIndexes(aLogName, "test4", aSourceSetting, aDestinationSetting, state6);
        printText(--cntTabs, "*** end runAllTestIndexes ***");
    }

    private void runAllTestKeys(String aLogName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
        printText(cntTabs++, "*** runAllTestKeys ***");
        Map<String, String[]> pkeys1 = new HashMap();
        pkeys1.put("Tbl 1", new String[]{});
        pkeys1.put("TblTwo", new String[]{"fTwo_1"});
        pkeys1.put("Tbl 3", new String[]{"f 3_1", "f 3_2"});
        pkeys1.put("Tbl 4", new String[]{"f 4_1"});
//        pkeys1.put("Tbl 5", new String[]{"f 5_1"});
//        pkeys1.put("Tbl 6", new String[]{"f 6_1"});
        pkeys1.put("Tbl 7", new String[]{"f 7_1", "f 7_2"});
//        pkeys1.put("Tbl 8", new String[]{"f 8_1", "f 8_2"});
//        pkeys1.put("Tbl 9", new String[]{"f 9_1", "f 9_2"});

//        Map<String, String[]> pkeys1_reverse = new HashMap();
//        pkeys1_reverse.put("Tbl 3", new String[]{"f 3_2", "f 3_1"});
//        pkeys1_reverse.put("Tbl 7", new String[]{"f 7_2", "f 7_1"});
//        pkeys1_reverse.put("Tbl 8", new String[]{"f 8_2", "f 8_1"});
//        pkeys1_reverse.put("Tbl 9", new String[]{"f 9_2", "f 9_1"});

        Map<String, String[]> pkeys2 = new HashMap();
        pkeys2.put("Tbl 1", new String[]{});
        pkeys2.put("TblTwo", new String[]{"fTwo_1", "f 2_2"});
        pkeys2.put("Tbl 3", new String[]{"f 3_1"});
        pkeys2.put("Tbl 4", new String[]{"f 4_1", "f 4_2"});
//        pkeys2.put("Tbl5", new String[]{"f 5_1", "f 5_1d"});
//        pkeys2.put("Tbl6", new String[]{"f 6_1d"});
        pkeys2.put("Tbl 7", new String[]{"f 7_1"});
//        pkeys2.put("Tbl8", new String[]{"f 8_1"});
//        pkeys2.put("Tbl9", new String[]{"f 9_1", "f 9_2d", "f 9_3"});

        FKeyDefine[] fkeys0 = {};
        FKeyDefine[] fkeys1 = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl 4", "Tbl4 Fk1", "TblTwo", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f 4_1"}),
            new FKeyDefine("Tbl 4", "Tbl4_Fk2", "Tbl 4", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f 4_2"}),
//            new FKeyDefine("Tbl5", "Tbl5_Fk1", "Tbl2", ForeignKeyRule.SETNULL, ForeignKeyRule.SETDEFAULT, true, new String[]{"f5_11"}),
//            new FKeyDefine("Tbl6", "Tbl6_Fk1", "Tbl6", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f6_2"}),
            new FKeyDefine("Tbl 7", "Tbl7_Fk 1", "Tbl 3", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f 7_11", "f 7_12"})
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f8_1", "f8_2"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_3", "f9_4"})
        };
//        FKeyDefine[] fkeys1_reverse = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
//            new FKeyDefine("Tbl7", "Tbl7_Fk1", "Tbl3", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f7_12", "f7_11"}),
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f8_2", "f8_1"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_4", "f9_3"})
//        };
        FKeyDefine[] fkeys2 = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl 4", "Tbl4 Fk1", "TblTwo", ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, true, new String[]{"f 4_11"}),
            new FKeyDefine("Tbl 4", "Tbl4_Fk2", "Tbl 4", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f 4_2"}),
//            new FKeyDefine("Tbl5", "Tbl5_Fk1", "Tbl2", ForeignKeyRule.SETDEFAULT, ForeignKeyRule.CASCADE, true, new String[]{"f5_11"}),
//            new FKeyDefine("Tbl6", "Tbl6_Fk1", "Tbl6", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f6_2"}),
            new FKeyDefine("Tbl 7", "Tbl7_Fk 1", "Tbl 3", ForeignKeyRule.SETNULL, ForeignKeyRule.NOACTION, true, new String[]{"f 7_11", "f 7_12"}),
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, true, new String[]{"f8_11", "f8_12"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_3", "f9_4"})
        };
//        FKeyDefine[] fkeys3 = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
//            new FKeyDefine("Tbl4", "Tbl4_Fk1", "Tbl2", ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, true, new String[]{"f4_11"}),
//            new FKeyDefine("Tbl4", "Tbl4_Fk2", "Tbl4", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f4_2"}),
//            new FKeyDefine("Tbl5", "Tbl5_Fk1", "Tbl2", ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, true, new String[]{"f5_11"}),
//            new FKeyDefine("Tbl6", "Tbl6_Fk1", "Tbl6", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f6_2"}),
//            new FKeyDefine("Tbl7", "Tbl7_Fk1", "Tbl3", ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETNULL, true, new String[]{"f7_11", "f7_12"}),
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, true, new String[]{"f8_11", "f8_12"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_3", "f9_4"})
//        };
//        FKeyDefine[] fkeys4 = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
//            new FKeyDefine("Tbl4", "Tbl4_Fk1", "Tbl2", ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, true, new String[]{"f4_11"}),
//            new FKeyDefine("Tbl4", "Tbl4_Fk2", "Tbl4", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f4_2"}),
//            new FKeyDefine("Tbl5", "Tbl5_Fk1", "Tbl2", ForeignKeyRule.CASCADE, ForeignKeyRule.SETNULL, true, new String[]{"f5_11"}),
//            new FKeyDefine("Tbl6", "Tbl6_Fk1", "Tbl6", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f6_2"}),
//            new FKeyDefine("Tbl7", "Tbl7_Fk1", "Tbl3", ForeignKeyRule.CASCADE, ForeignKeyRule.SETDEFAULT, true, new String[]{"f7_11", "f7_12"}),
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, true, new String[]{"f8_11", "f8_12"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_3", "f9_4"})
//        };
//        FKeyDefine[] fkeys5 = { //NOACTION, SETNULL, SETDEFAULT, CASCADE;
//            new FKeyDefine("Tbl4", "Tbl4_Fk1", "Tbl2", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f4_11", "f4_2"}),
//            new FKeyDefine("Tbl4", "Tbl4_Fk2", "Tbl4", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f4_3", "f4_4"}),
//            new FKeyDefine("Tbl5", "Tbl5_Fk1", "Tbl2", ForeignKeyRule.NOACTION, ForeignKeyRule.CASCADE, true, new String[]{"f5_11", "f5_1d"}),
//            new FKeyDefine("Tbl6", "Tbl6_Fk1", "Tbl6", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f6_2d"}),
//            new FKeyDefine("Tbl7", "Tbl7_Fk1", "Tbl3", ForeignKeyRule.SETNULL, ForeignKeyRule.CASCADE, true, new String[]{"f7_11"}),
//            new FKeyDefine("Tbl8", "Tbl8_Fk1", "Tbl3", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f8_11"}),
//            new FKeyDefine("Tbl9", "Tbl9_Fk1", "Tbl9", ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, true, new String[]{"f9_3d", "f9_4", "f9_5"})
//        };
        runTestKeys(aLogName, "test1", aSourceSetting, aDestinationSetting, pkeys1, fkeys0);
        runTestKeys(aLogName, "test2", aSourceSetting, aDestinationSetting, pkeys1, fkeys1);
//        runTestKeys(aLogName, "test2_reverse", aSourceSetting, aDestinationSetting, pkeys1_reverse, fkeys1_reverse);
        runTestKeys(aLogName, "test3", aSourceSetting, aDestinationSetting, pkeys1, fkeys2);
//        runTestKeys(aLogName, "test4", aSourceSetting, aDestinationSetting, pkeys1, fkeys3);
//        runTestKeys(aLogName, "test5", aSourceSetting, aDestinationSetting, pkeys1, fkeys4);
//        runTestKeys(aLogName, "test6", aSourceSetting, aDestinationSetting, pkeys2, fkeys5);
        printText(--cntTabs, "*** end runAllTestKeys ***");
    }

    private void runTestTables(String aLogName, String aTestName, DbConnection aSourceConnection, DbConnection aDestinationConnection, TableDefine[] aSourceTableDefine, TableDefine[] aDestinationTableDefine, boolean aNoDropTables, String aTablesList) throws Exception {
        printText(cntTabs++, "*** ", "runTestTables ", aTestName, " ***");
        String logName = aLogName + "_Tables_" + aTestName;
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);
        clearSchema(aSourceConnection);
        createTables(aSourceConnection, aSourceTableDefine);
        assertNotNull(aSourceConnection);
        assertNotNull(aSourceTableDefine);
        checkTables(aSourceConnection, aSourceTableDefine);
        synchronizeDb(logName, aSourceConnection, aDestinationConnection, aNoDropTables, aTablesList);
        assertNotNull(aDestinationConnection);
        assertNotNull(aDestinationTableDefine);
        checkTables(aDestinationConnection, aDestinationTableDefine);
        assertNotNull(XML_NAME);
        assertNotNull(aSourceTableDefine);
        checkTables(XML_NAME, aSourceTableDefine);
        printText(--cntTabs, "*** ", "end runTestTables ", aTestName, " ***");
    }

    private void runTestFields(String aLogName, String aTestName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, String aTableName, String[] aFieldNames, int aSize) throws Exception {
        printText(cntTabs++, "*** ", "runTestFields ", aTestName, " ***");
        String logName = aLogName + "_Fields_" + aTestName;
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        assertNotNull(aSourceSetting.getDbConnection());
        assertNotNull(aDestinationSetting.getDbConnection());
        assertNotNull(aSourceSetting.getDatabase());
        assertNotNull(aDestinationSetting.getDatabase());
        assertNotNull(aSourceSetting.getDbTestDefine());
        clearSchema(aSourceSetting.getDbConnection());
        createFields(aSourceSetting, aTableName, aFieldNames, aSize);
        checkFields(aSourceSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aTableName, aFieldNames, aSize);
        synchronizeDb(logName, aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
        checkFields(aDestinationSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aDestinationSetting.getDatabase(), aTableName, aFieldNames, aSize);
        assertNotNull(XML_NAME);
        checkFields(XML_NAME, aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aTableName, aFieldNames, aSize);
        printText(--cntTabs, "*** ", "end runTestFields ", aTestName, " ***");
    }

    private void runTestIndexes(String aLogName, String aTestName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, IndexDefine[] aIndexesDefine) throws Exception {
        printText(cntTabs++, "*** ", "runTestIndexes ", aTestName, " ***");
        String logName = aLogName + "_Indexes_" + aTestName;
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        assertNotNull(aIndexesDefine);
        assertNotNull(aSourceSetting.getDbConnection());
        assertNotNull(aDestinationSetting.getDbConnection());
        Database srcDatabase = aSourceSetting.getDatabase();
        assertNotNull(srcDatabase);
        Database destDatabase = aDestinationSetting.getDatabase();
        assertNotNull(destDatabase);
        assertNotNull(aSourceSetting.getDbTestDefine());
        clearSchema(aSourceSetting.getDbConnection());
        String tableName = "Tbl";
        createIndexes(aSourceSetting.getDbConnection(), tableName, aIndexesDefine);
        checkIndexes(aSourceSetting.getDbConnection(), tableName, aIndexesDefine, srcDatabase.isCheckIndexClustered(), srcDatabase.isCheckIndexHashed(), srcDatabase.isCheckIndexAscending());
        synchronizeDb(logName, aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
        checkIndexes(aDestinationSetting.getDbConnection(), tableName, aIndexesDefine, destDatabase.isCheckIndexClustered(), destDatabase.isCheckIndexHashed(), destDatabase.isCheckIndexAscending());
        assertNotNull(XML_NAME);
        checkIndexes(XML_NAME, tableName, aIndexesDefine, srcDatabase.isCheckIndexClustered(), srcDatabase.isCheckIndexHashed(), srcDatabase.isCheckIndexAscending());
        printText(--cntTabs, "*** ", "end runTestIndexes ", aTestName, " ***");
    }

    private void runTestKeys(String aLogName, String aTestName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs++, "*** ", "runTestKeys ", aTestName, " ***");
        String logName = aLogName + "_Keys_" + aTestName;
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        assertNotNull(aSourceSetting.getDbConnection());
        assertNotNull(aDestinationSetting.getDbConnection());
        clearSchema(aSourceSetting.getDbConnection());
        assertNotNull(aSourceSetting.getDbTestDefine());
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
        assertNotNull(aSourceSetting.getDatabase());
        assertNotNull(aDestinationSetting.getDatabase());

        createKeys(aSourceSetting.getDbConnection(), aPKeysDefine, aFKeysDefine);
        checkKeys(aSourceSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aPKeysDefine, aFKeysDefine);
        synchronizeDb(logName, aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());

        checkKeys(aDestinationSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aDestinationSetting.getDatabase(), aPKeysDefine, aFKeysDefine);
        assertNotNull(XML_NAME);
        checkKeys(XML_NAME, aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aPKeysDefine, aFKeysDefine);
        printText(--cntTabs, "*** ", "end runTestKeys ", aTestName, " ***");
    }

    private void createTables(DbConnection aDbConnection, TableDefine[] aTableDefine) throws Exception {
        printText(cntTabs, "createTables \turl=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
//        DatabasesClient client = createClient(aDbConnection).getClient();
//        assertNotNull(client);
        try (DatabasesClientWithResource dbResource = createClient(aDbConnection)) {
            DatabasesClient client = dbResource.getClient();
            assertNotNull(client);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
            assertNotNull(driver);
            for (TableDefine tableDefine : aTableDefine) {
                String tableName = driver.wrapName(tableDefine.getTableName(),true);
                String pkFieldName = driver.wrapName(tableDefine.getPkFieldName(),true);
                String sql = driver.getSql4EmptyTableCreation(aDbConnection.getSchema(), tableName, pkFieldName);
                executeSql(client, sql);
                sql = driver.getSql4CreateTableComment(aDbConnection.getSchema(), tableName, tableDefine.getDescription());
                executeSql(client, sql);
            }
        }
    }

    private void createFields(SourceDbSetting aSourceSetting, String aTableName,  String[] aFieldNames, int aSize) throws Exception {
        printText(cntTabs, "createFields \turl=", aSourceSetting.getDbConnection().getUrl(), " \tschema=", aSourceSetting.getDbConnection().getSchema(), " \tuser=", aSourceSetting.getDbConnection().getUser());
        DbConnection dbConnection = aSourceSetting.getDbConnection();

        try (DatabasesClientWithResource dbResource = createClient(dbConnection)) {
            DatabasesClient client = dbResource.getClient();
            assertNotNull(client);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
            assertNotNull(driver);
            String pkFieldName = "id";
            for (int i = 0; i < aFieldNames.length; i++) {
                String tableName = aTableName + i;
                String fieldName = aFieldNames[i];
                String wFieldName = driver.wrapName(fieldName, true);
                String description = "Comment for " + tableName.toUpperCase();

                String schema = dbConnection.getSchema();
                executeSql(client, driver.getSql4EmptyTableCreation(schema, tableName, pkFieldName));

                executeSql(client, driver.getSql4DropPkConstraint(schema, new PrimaryKeySpec(schema, tableName, pkFieldName, tableName + SqlDriver.PKEY_NAME_SUFFIX)));

                Field field = new Field();
                field.setSchemaName(schema);
                field.setName(wFieldName);
                field.setTableName(tableName);
                field.setDescription(description);
                
                field.setTypeInfo(DataTypeInfo.VARCHAR);
                field.setSize(aSize);
                String[] sqls = driver.getSqls4AddingField(schema, tableName, field);
                executeSql(client, sqls);
                
                String[] sqls2 = driver.getSql4CreateColumnComment(schema, tableName, wFieldName, description);
                executeSql(client, sqls2);
                
            }
        }
    }

    private void createIndexes(DbConnection aDbConnection, String aTableName, IndexDefine[] aIndexesDefine) throws Exception {
        printText(cntTabs, "createIndexes \turl=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        assertNotNull(aIndexesDefine);
        clearSchema(aDbConnection);
        Set<String> fields = new HashSet<>();
//        DbClient client = createClient(aDbConnection);
//        DatabasesClient client = createClient(aDbConnection).getClient();
//        assertNotNull(client);
//        try {
        try (DatabasesClientWithResource dbResource = createClient(aDbConnection)) {
            DatabasesClient client = dbResource.getClient();
            assertNotNull(client);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
            assertNotNull(driver);
            String tableName = driver.wrapName(aTableName, true);
            String pkField = "id";
            String schema = aDbConnection.getSchema();
            // create table
            String sql = driver.getSql4EmptyTableCreation(schema, tableName, pkField);
            executeSql(client, sql);
            // drop pkey
            String defaultPlatypusPkName = aTableName + "_pk";
            sql = driver.getSql4DropPkConstraint(schema, new PrimaryKeySpec(schema, tableName, pkField, driver.wrapName(defaultPlatypusPkName,true)));
            executeSql(client, sql);
            for (IndexDefine indexDefine : aIndexesDefine) {
                IndexColumnDefine[] columnsDefines = indexDefine.getColumns();
                assertNotNull(columnsDefines);
                DbTableIndexSpec indexSpec = new DbTableIndexSpec();
                indexSpec.setName(driver.wrapName(indexDefine.getIndexName(),true));
                indexSpec.setClustered(indexDefine.isClustered());
                indexSpec.setHashed(indexDefine.isHashed());
                indexSpec.setUnique(indexDefine.isUnique());
                for (IndexColumnDefine columnDefine : columnsDefines) {
                    assertNotNull(columnDefine);
                    String fieldName = columnDefine.getColumnName();
                    assertNotNull(fieldName);
                    // create fields
                    if (!fields.contains(fieldName.toUpperCase())) {
                        Field field = new Field();
                        field.setName(driver.wrapName(fieldName,true));
                        field.setSchemaName(driver.wrapName(schema,true));
                        field.setTypeInfo(DataTypeInfo.VARCHAR);
                        field.setSize(10);
                        String[] sqls = driver.getSqls4AddingField(schema, tableName, field);
                        executeSql(client, sqls);
                        fields.add(fieldName.toUpperCase());
                    }
                    indexSpec.addColumn(new DbTableIndexColumnSpec(fieldName, columnDefine.isAscending()));
                }
                // create index
                sql = driver.getSql4CreateIndex(schema, tableName, indexSpec);
                executeSql(client, sql);
            }
        }
    }

    private void createKeys(DbConnection aDbConnection, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "createKeys \turl=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);

        clearSchema(aDbConnection);
        Map<String, Set<String>> fieldsNames = new HashMap<>();
        Map<String, List<PrimaryKeySpec>> pkeys = new HashMap<>();
//        DbClient client = createClient(aDbConnection);
//        DatabasesClient client = createClient(aDbConnection).getClient();
//        assertNotNull(client);
//        try {
        try (DatabasesClientWithResource dbResource = createClient(aDbConnection)) {
            DatabasesClient client = dbResource.getClient();
            assertNotNull(client);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
            assertNotNull(driver);
            String pkField = "id";
            String schema = aDbConnection.getSchema();

            logText(cntTabs, "start create primaryKeys");
            for (String tblName : aPKeysDefine.keySet()) {
                String upperTableName = tblName.toUpperCase();
                //create table
                String tableName = driver.wrapName(tblName,true);
                String sql = driver.getSql4EmptyTableCreation(schema, tableName, pkField);
                executeSql(client, sql);
                // drop pkey
                String defaultPlatypusPkName = tblName + "_pk";
                sql = driver.getSql4DropPkConstraint(schema, new PrimaryKeySpec(schema, tableName, pkField, driver.wrapName(defaultPlatypusPkName,true)));
                executeSql(client, sql);
                String[] fieldsDefine = aPKeysDefine.get(tblName);
                Set<String> tableFields = new HashSet();
                List<PrimaryKeySpec> pkSpecs = new ArrayList();
                if (fieldsDefine != null) {
                    for (String fldName : fieldsDefine) {
                        // create field
                        assertNotNull(fldName);
                        String fieldName = driver.wrapName(fldName, true);
                        Field field = new Field();
                        field.setName(fieldName);
                        field.setSchemaName(schema);
                        field.setTypeInfo(DataTypeInfo.VARCHAR);
                        field.setSize(10);
                        //field.setNullable(true);
                        field.setNullable(false);
                        String[] sqls = driver.getSqls4AddingField(schema, tableName, field);
                        executeSql(client, sqls);
                        tableFields.add(fldName.toUpperCase());
                        // create spec
                        pkSpecs.add(new PrimaryKeySpec(schema, tableName, fieldName, defaultPlatypusPkName));
                    }
                }
                fieldsNames.put(upperTableName, tableFields);
                pkeys.put(upperTableName, pkSpecs);
                // create pk
                if (pkSpecs.size() > 0) {
                    String[] sqls = driver.getSql4CreatePkConstraint(schema, pkSpecs);
                    executeSql(client, sqls);
                }
            }
            logText(cntTabs, "start create foreignKeys");
            for (FKeyDefine fkeyDefine : aFKeysDefine) {
                assertNotNull(fkeyDefine);
                if (fkeyDefine.getFieldsNames() != null) {
                    String referTableName = fkeyDefine.getReferTableName();
                    assertNotNull(referTableName);
                    List<PrimaryKeySpec> pkSpecs = pkeys.get(referTableName.toUpperCase());
                    assertNotNull(pkSpecs);
                    assertEquals(pkSpecs.size(), fkeyDefine.getFieldsNames().length);
                    List<ForeignKeySpec> fkSpecs = new ArrayList<>();
                    for (int i = 0; i < fkeyDefine.getFieldsNames().length; i++) {
                        String fieldName = fkeyDefine.getFieldsNames()[i];
                        String tableName = fkeyDefine.getTableName();

                        ForeignKeySpec fkSpec = new ForeignKeySpec();
                        fkSpec.setSchema(schema);
                        fkSpec.setTable(driver.wrapName(tableName,true));
                        fkSpec.setCName(driver.wrapName(fkeyDefine.getName(), true));
                        fkSpec.setField(driver.wrapName(fieldName, true));
                        PrimaryKeySpec pk = pkSpecs.get(i);
                        pk.setCName(driver.wrapName(pk.getCName(),true));
                        pk.setField(driver.wrapName(pk.getField(),true));
                        pk.setTable(driver.wrapName(pk.getTable(),true));
                        pk.setSchema(schema);
                        fkSpec.setReferee(pk);

                        fkSpec.setFkDeleteRule(fkeyDefine.getDeleteRule());
                        fkSpec.setFkUpdateRule(fkeyDefine.getUpdateRule());
                        fkSpec.setFkDeferrable(fkeyDefine.isDeferrable());
                        // create field
                        Set<String> tableFields = fieldsNames.get(tableName.toUpperCase());
                        if (!tableFields.contains(fieldName.toUpperCase())) {
                            Field field = new Field();
                            field.setName(fieldName);
                            field.setSchemaName(schema);
                            field.setTypeInfo(DataTypeInfo.VARCHAR);
                            field.setSize(10);
                            String[] sqls = driver.getSqls4AddingField(schema, tableName, field);
                            executeSql(client, sqls);
                            tableFields.add(fieldName.toUpperCase());
                            fieldsNames.put(tableName.toUpperCase(), tableFields);
                        }
                        fkSpecs.add(fkSpec);
                    }
                    if (fkSpecs.size() > 0) {
                        // для теста разных методов
                        if (fkSpecs.size() == 1) {
                            String sql = driver.getSql4CreateFkConstraint(schema, fkSpecs.get(0));
                            executeSql(client, sql);
                        } else {
                            String sql = driver.getSql4CreateFkConstraint(schema, fkSpecs);
                            executeSql(client, sql);
                        }
                    }
                }
            }
        }
    }

    private void checkTables(DbConnection aDbConnection, TableDefine[] aTablesDefine) throws Exception {
        printText(cntTabs, "checkTables: ", " \t", "url=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkTables(databaseStructure, aTablesDefine);
    }

    private void checkTables(String aXmlFileName, TableDefine[] aTablesDefine) throws Exception {
        printText(cntTabs, "checkTables: ", " \t", "xml=", aXmlFileName);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkTables(databaseStructure, aTablesDefine);
    }

    private void checkTables(DBStructure aDatabaseStructure, TableDefine[] aTablesDefine) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);
        assertEquals(dbStructure.size(), aTablesDefine.length);
        for (TableDefine tableDefine : aTablesDefine) {
            String tableNameDefine = tableDefine.getTableName();
            String pkFieldNameDefine = tableDefine.getPkFieldName();
            String descriptionDefine = tableDefine.getDescription();
            assertNotNull(tableNameDefine);
            assertNotNull(pkFieldNameDefine);
            String upperTableName = tableNameDefine.toUpperCase();
            assertTrue(dbStructure.containsKey(upperTableName));
            TableStructure tblStructure = dbStructure.get(upperTableName);
            assertTrue(upperTableName.equalsIgnoreCase(tblStructure.getTableName()));
            Fields fields = tblStructure.getTableFields();
            assertEquals(fields.getFieldsCount(), 1);
            Field field = fields.get(pkFieldNameDefine);
            assertNotNull(field);
            assertTrue(pkFieldNameDefine.equalsIgnoreCase(field.getName()));
            assertTrue(field.isPk());
            String description = tblStructure.getTableDescription();
            //??????
            if (description == null) {
                description = "";
            }
            if (descriptionDefine == null) {
                assertEquals("", description);
            } else {
                assertEquals(descriptionDefine, description);
            }
        }
    }

    private void checkFields(DbConnection aDbConnection, DbTestDefine aTestDefine, Database aDatabase, String aTableName,  String[] aFieldNames, int aSize) throws Exception {
        printText(cntTabs, "checkFields: ", " \t", "url=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        assertNotNull(aDbConnection);
        assertNotNull(aTestDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkFields(databaseStructure, aTestDefine, aDatabase, aTableName, aFieldNames, aSize);
    }

    private void checkFields(String aXmlFileName, DbTestDefine aTestDefine, Database aDatabase, String aTableName,  String[] aFieldNames, int aSize) throws Exception {
        printText(cntTabs, "checkFields: ", " \t", "xml=", aXmlFileName);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkFields(databaseStructure, aTestDefine, aDatabase, aTableName, aFieldNames, aSize);
    }

    private void checkFields(DBStructure aDatabaseStructure, DbTestDefine aTestDefine, Database aDatabase, String aTableName,  String[] aFieldNames, int aSize) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);
        
        
        
        
        
        for (int i = 0; i < aFieldNames.length; i++) {
            String tableName = aTableName + i;
            String fieldName = aFieldNames[i];
            String description = "Comment for " + tableName.toUpperCase();
            
            TableStructure tblStructure = dbStructure.get(tableName.toUpperCase());
            assertNotNull(tblStructure);
            assertEquals(tableName.toUpperCase(), tblStructure.getTableName().toUpperCase());
            Fields fields = tblStructure.getTableFields();
            assertNotNull(fields);
            Field field = fields.get(fieldName);
            assertNotNull(field);
            assertEquals(field.getName().toUpperCase(), fieldName.toUpperCase());
            //??????
            if (description == null) {
                description = "";
            }
            if (field.getDescription() == null) {
                assertEquals("", description);
            } else {
                assertEquals(field.getDescription(), description);
            }
            assertEquals(field.getSize() , aSize);
            assertEquals(field.getTypeInfo().getSqlType(), Types.VARCHAR);
        }    
    }

    private void checkIndexes(DbConnection aDbConnection, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed, boolean checkAscending) throws Exception {
        printText(cntTabs, "checkIndexes: ", " \t", "url=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        assertNotNull(aTableName);
        assertNotNull(aIndexesDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkIndexes(databaseStructure, aTableName, aIndexesDefine, checkClustered, checkHashed, checkAscending);
    }

    private void checkIndexes(String aXmlFileName, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed, boolean checkAscending) throws Exception {
        printText(cntTabs, "checkIndexes: ", " \t", "xml=", aXmlFileName);
        assertNotNull(aTableName);
        assertNotNull(aIndexesDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkIndexes(databaseStructure, aTableName, aIndexesDefine, checkClustered, checkHashed, checkAscending);
    }

    private void checkIndexes(DBStructure aDatabaseStructure, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed, boolean checkAscending) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);
        TableStructure tblStructure = dbStructure.get(aTableName.toUpperCase());
        assertNotNull(tblStructure);
        Map<String, DbTableIndexSpec> indexSpecs = tblStructure.getTableIndexSpecs();
        if (aIndexesDefine.length > 0) {
            assertNotNull(indexSpecs);
            assertEquals(indexSpecs.size(), aIndexesDefine.length);
            for (IndexDefine define : aIndexesDefine) {
                String indexNameDefine = define.getIndexName();
                assertNotNull(indexNameDefine);
                String originalIndexName = tblStructure.getOriginalIndexName(indexNameDefine.toUpperCase());
                assertNotNull(originalIndexName);
                assertEquals(originalIndexName.toUpperCase(), indexNameDefine.toUpperCase());
                DbTableIndexSpec indexSpec = indexSpecs.get(originalIndexName);
                assertNotNull(indexSpec);
                assertEquals(indexSpec.getName().toUpperCase(), indexNameDefine.toUpperCase());
                assertEquals(indexSpec.isUnique(), define.isUnique());
                if (checkClustered) {
                    assertEquals(indexSpec.isClustered(), define.isClustered());
                }
                if (checkHashed) {
                    assertEquals(indexSpec.isHashed(), define.isHashed());
                }
                List<DbTableIndexColumnSpec> columnsSpec = indexSpec.getColumns();
                assertNotNull(columnsSpec);
                assertNotNull(define.getColumns());
                assertEquals(columnsSpec.size(), define.getColumns().length);
                for (int i = 0; i < define.getColumns().length; i++) {
                    DbTableIndexColumnSpec columnSpec = columnsSpec.get(i);
                    IndexColumnDefine columnDefine = define.getColumns()[i];
                    assertNotNull(columnSpec);
                    assertNotNull(columnDefine);
                    assertEquals(columnSpec.getColumnName().toUpperCase(), columnDefine.getColumnName().toUpperCase());
                    if (checkAscending) {
                        assertEquals(columnSpec.isAscending(), columnDefine.isAscending());
                    }
                }
            }
        } else {
            assertTrue(indexSpecs == null || indexSpecs.isEmpty());
        }
    }

    private void checkKeys(DbConnection aDbConnection, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "checkKeys: ", " \t", "url=", aDbConnection.getUrl(), " \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        assertNotNull(databaseStructure);
        checkKeys(databaseStructure, aTestDefine, aDatabase, aPKeysDefine, aFKeysDefine);
    }

    private void checkKeys(String aXmlFileName, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "checkKeys: ", " \t", "xml=", aXmlFileName);
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        assertNotNull(databaseStructure);
        checkKeys(databaseStructure, aTestDefine, aDatabase, aPKeysDefine, aFKeysDefine);
    }

    private void checkKeys(DBStructure aDatabaseStructure, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);
        logText(cntTabs, "start check primary keys");
        assertEquals(dbStructure.keySet().size(), aPKeysDefine.size());
        for (String tableName : aPKeysDefine.keySet()) {
            assertNotNull(tableName);
            logText(cntTabs, "table=", tableName);
            String[] pkFields = aPKeysDefine.get(tableName);
            TableStructure tblStructure = dbStructure.get(tableName.toUpperCase());
            assertNotNull(tblStructure);
            List<PrimaryKeySpec> tablePKeySpecs = tblStructure.getTablePKeySpecs();
            if (pkFields != null && pkFields.length > 0) {
                assertNotNull(tablePKeySpecs);
                assertEquals(pkFields.length, tablePKeySpecs.size());
                for (int i = 0; i < pkFields.length; i++) {
                    String fieldDefine = pkFields[i];
                    assertNotNull(fieldDefine);
                    PrimaryKeySpec pkSpec = tablePKeySpecs.get(i);
                    assertNotNull(pkSpec);
                    String fieldSpec = pkSpec.getField();
                    assertNotNull(fieldSpec);
                    assertEquals(fieldSpec.toUpperCase(), fieldDefine.toUpperCase());
                    String tableSpec = pkSpec.getTable();
                    assertNotNull(tableSpec);
                    assertEquals(tableSpec.toUpperCase(), tableName.toUpperCase());
                }
            } else {
                assertTrue(tablePKeySpecs == null || tablePKeySpecs.isEmpty());
            }
        }
        logText(cntTabs, "start check foreign keys");
        for (FKeyDefine fkeyDefine : aFKeysDefine) {
            String tableName = fkeyDefine.getTableName();
            String cNameDefine = fkeyDefine.getName();
            String[] fieldsDefine = fkeyDefine.getFieldsNames();
            boolean fkDeferrableOriginal = fkeyDefine.isDeferrable();
            ForeignKeyRule fkDeleteRuleOriginal = fkeyDefine.getDeleteRule();
            ForeignKeyRule fkUpdateRuleOriginal = fkeyDefine.getUpdateRule();
            String referTableNameDefine = fkeyDefine.getReferTableName();
            logText(cntTabs, "table=", tableName, " \t", "fkName=", cNameDefine);
            if (fieldsDefine != null) {
                assertNotNull(tableName);
//????
//                assertNotNull(deleteRuleDefine);
//                assertNotNull(updateRuleDefine);
//                assertNotNull(referTableNameDefine);
                TableStructure tblStructure = dbStructure.get(tableName.toUpperCase());
                assertNotNull(tblStructure);
                Map<String, List<ForeignKeySpec>> tableFKeySpecs = tblStructure.getTableFKeySpecs();
                assertNotNull(tableFKeySpecs);
                String originalFKeyName = tblStructure.getOriginalFKeyName(cNameDefine.toUpperCase());
                assertNotNull(originalFKeyName);
                List<ForeignKeySpec> fkSpecs = tableFKeySpecs.get(originalFKeyName);
                assertEquals(originalFKeyName.toUpperCase(), cNameDefine.toUpperCase());
                assertEquals(fkSpecs.size(), fieldsDefine.length);
                for (int i = 0; i < fkSpecs.size(); i++) {
                    ForeignKeySpec fkSpec = fkSpecs.get(i);
                    assertNotNull(fkSpec);
                    String tableNameSpec = fkSpec.getTable();
                    String fieldNameSpec = fkSpec.getField();
                    String cNameSpec = fkSpec.getCName();
                    ForeignKeyRule fkDeleteRuleSpec = fkSpec.getFkDeleteRule();
                    ForeignKeyRule fkUpdateRuleSpec = fkSpec.getFkUpdateRule();
                    //                    //?????????
                    boolean fkDeferrable = fkSpec.getFkDeferrable();
                    PrimaryKeySpec refereeSpec = fkSpec.getReferee();

                    String fieldNameDefine = fieldsDefine[i];

                    assertNotNull(tableNameSpec);
                    assertEquals(tableNameSpec.toUpperCase(), tableName.toUpperCase());
                    assertNotNull(fieldNameSpec);
                    assertNotNull(fieldNameDefine);
                    assertEquals(fieldNameSpec.toUpperCase(), fieldNameDefine.toUpperCase());
                    assertNotNull(cNameSpec);
                    assertEquals(cNameSpec.toUpperCase(), cNameDefine.toUpperCase());

                    ForeignKeyRule fkDeleteRuleDefine = aTestDefine.getFKeyDeleteRule(fkDeleteRuleOriginal, aDatabase.getCode());
                    ForeignKeyRule fkUpdateRuleDefine = aTestDefine.getFKeyUpdateRule(fkUpdateRuleOriginal, aDatabase.getCode());
                    //??????                    Boolean fKeyDeferrableDefine = aTestDefine.getFKeyDeferrable(fkDeferrableOriginal,aDatabase.getCode());
                    assertEquals(fkDeleteRuleDefine, fkDeleteRuleSpec);
                    assertEquals(fkUpdateRuleDefine, fkUpdateRuleSpec);
                    String refereeTableNameSpec = refereeSpec.getTable();
                    assertNotNull(refereeTableNameSpec);
                    assertEquals(refereeTableNameSpec.toUpperCase(), referTableNameDefine.toUpperCase());

                    TableStructure referStructure = dbStructure.get(referTableNameDefine.toUpperCase());
                    assertNotNull(referStructure);
                    List<PrimaryKeySpec> referPKeySpecs = referStructure.getTablePKeySpecs();
                    assertNotNull(referPKeySpecs);
                    assertEquals(referPKeySpecs.size(), fkSpecs.size());
                    PrimaryKeySpec referPKey = referPKeySpecs.get(i);
                    assertNotNull(referPKey);
                    String referPKFieldName = referPKey.getField();
                    assertNotNull(referPKFieldName);
                    String refereeFieldNameSpec = refereeSpec.getField();
                    assertNotNull(refereeFieldNameSpec);
                    assertEquals(referPKFieldName.toUpperCase(), refereeFieldNameSpec.toUpperCase());
                }
            }
        }
    }

    private DatabasesClientWithResource createClient(DbConnection aDbConnection) throws Exception {
//        DbConnectionSettings settings = new DbConnectionSettings(aDbConnection.getUrl(), aDbConnection.getUser(), aDbConnection.getPassword(), aDbConnection.getSchema(), null);
        DbConnectionSettings settings = new DbConnectionSettings(aDbConnection.getUrl(), aDbConnection.getUser(), aDbConnection.getPassword());
        return new DatabasesClientWithResource(settings);
    }

    private void clearSchema(DbConnection aDbConnection) throws Exception {
        MetadataSynchronizer mds = new MetadataSynchronizer();
        try (DatabasesClientWithResource resource = createClient(aDbConnection)) {
            DatabasesClient client = resource.getClient();
            DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
            assertNotNull(databaseStructure);
            Map<String, TableStructure> dbStructure = databaseStructure.getTablesStructure();
            assertNotNull(dbStructure);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();

            for (String upperTblName : dbStructure.keySet()) {
                TableStructure tblStructure = dbStructure.get(upperTblName);
                Map<String, List<ForeignKeySpec>> fkeySpecs = tblStructure.getTableFKeySpecs();
                if (fkeySpecs != null) {
                    for (String fkName : fkeySpecs.keySet()) {
                        List<ForeignKeySpec> fkeys = fkeySpecs.get(fkName);
                        assertNotNull(fkeys);
                        assert fkeys.size() > 0;
                        ForeignKeySpec fKey = fkeys.get(0);
                        String cName = fKey.getCName();
                        fKey.setCName(driver.wrapName(cName,driver.isHadWrapped(cName)));
                        String field = fKey.getField();
                        fKey.setField(driver.wrapName(field,driver.isHadWrapped(field)));
                        String table = fKey.getTable();
                        fKey.setTable(driver.wrapName(table,driver.isHadWrapped(table)));
                        String sql = driver.getSql4DropFkConstraint(aDbConnection.getSchema(), fKey);
                        assertNotNull(sql);
                        executeSql(client, sql);
                    }
                }
            }
            for (String upperTblName : dbStructure.keySet()) {
                TableStructure tblStructure = dbStructure.get(upperTblName);
                String tableName = tblStructure.getTableName();
                String sql = driver.getSql4DropTable(aDbConnection.getSchema(), driver.wrapName(tableName, driver.isHadWrapped(tableName)));
                assertNotNull(sql);
                executeSql(client, sql);
            }
            DBStructure databaseStructure2 = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
            assertNotNull(databaseStructure2);
            Map<String, TableStructure> dbStructure2 = databaseStructure2.getTablesStructure();
            assertNotNull(dbStructure2);
            assertEquals(dbStructure2.size(), 0);
        }
    }

    private void synchronizeDb(String aLogName, DbConnection aSourceConnection, DbConnection aDestinationConnection, boolean aNoDropTables, String aTablesList) throws Exception {
        printText(cntTabs, "synchronizeDb \tnoDropTables=", String.valueOf(aNoDropTables).toUpperCase(), " \ttables=", aTablesList);
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);

        String loggerName = MetadataSynchronizerTest.class.getName() + "_" + System.currentTimeMillis();
        Logger sqlLog = MetadataSynchronizer.initLogger(loggerName + "_sql", Level.ALL, false);
        Logger errorLog = MetadataSynchronizer.initLogger(loggerName + "_error", Level.ALL, false);
        String logEncoding = "UTF-8";
        try {
            if (aLogName != null) {
                sqlLog.addHandler(MetadataSynchronizer.createFileHandler(aLogName + ".log", logEncoding, new LineLogFormatter()));
                errorLog.addHandler(MetadataSynchronizer.createFileHandler(aLogName + "_err.log", logEncoding, new LineLogFormatter()));
            }
            MetadataSynchronizer mds = new MetadataSynchronizer(null, sqlLog, errorLog, null);

            mds.setSourceDatabase(aSourceConnection.getUrl(), aSourceConnection.getSchema(), aSourceConnection.getUser(), aSourceConnection.getPassword());
            mds.setDestinationDatabase(aDestinationConnection.getUrl(), aDestinationConnection.getSchema(), aDestinationConnection.getUser(), aDestinationConnection.getPassword());
            mds.setNoDropTables(aNoDropTables);
            mds.parseTablesList(aTablesList, ",");
            mds.setFileXml(XML_NAME);
            mds.run();
        } finally {
            MetadataSynchronizer.closeLogHandlers(sqlLog);
            MetadataSynchronizer.closeLogHandlers(errorLog);
        }
    }

    private void synchronizeDb(String aLogName, DbConnection aSourceConnection, DbConnection aDestinationConnection) throws Exception {
        synchronizeDb(aLogName, aSourceConnection, aDestinationConnection, false, null);
    }

    private void executeSql(DatabasesClient aClient, String aSql) throws Exception {
        logText(cntTabs + 1, "sql=" + aSql);

        assertNotNull(aClient);
        assertNotNull(aSql);
        assertFalse(aSql.isEmpty());
        SqlCompiledQuery compiledSql = new SqlCompiledQuery(aClient, null, aSql);
        aClient.commit(Collections.singletonMap((String)null, Collections.singletonList((Change)compiledSql.prepareCommand())), null, null);
    }

    private void executeSql(DatabasesClient aClient, String[] aSqls) throws Exception {
        for (String sql : aSqls) {
            executeSql(aClient, sql);
        }
    }

    private void printText(int countTabs, String... texts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countTabs; i++) {
            sb.append("\t");
        }
        if (texts != null) {
            for (String text : texts) {
                sb.append(text);
            }
        }
        System.out.println(sb.toString());
        Logger.getLogger(MetadataSynchronizerTest.class.getName()).log(Level.INFO, sb.toString());
    }

    private void logText(int countTabs, String... texts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countTabs; i++) {
            sb.append("\t");
        }
        if (texts != null) {
            for (String text : texts) {
                sb.append(text);
            }
        }
        Logger.getLogger(MetadataSynchronizerTest.class.getName()).log(Level.INFO, sb.toString());
    }
}
