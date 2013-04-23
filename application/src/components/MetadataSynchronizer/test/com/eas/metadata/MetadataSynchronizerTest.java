/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.eas.metadata.testdefine.PostgreTestDefine;
import com.eas.metadata.testdefine.DbTestDefine;
import com.eas.metadata.dbdefines.TableDefine;
import com.eas.metadata.dbdefines.IndexDefine;
import com.eas.metadata.dbdefines.IndexColumnDefine;
import com.eas.metadata.dbdefines.FKeyDefine;
import com.eas.metadata.dbdefines.SourceDbSetting;
import com.eas.metadata.dbdefines.DestinationDbSetting;
import com.eas.metadata.dbdefines.DbConnection;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.ClientFactory;
import com.eas.client.DbClient;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.metadata.testdefine.DbTestDefine.Database;
import com.eas.metadata.testdefine.H2TestDefine;
import com.eas.metadata.testdefine.MySqlTestDefine;
import com.eas.metadata.testdefine.OracleTestDefine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
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
public class MetadataSynchronizerTest {
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    
static final Logger logger = Logger.getLogger(MetadataSynchronizerTest.class.getName());    
{
    
}
//private FileHandler fileHandler;
private String sqlLogName = null;
private String mdsLogName = null;
    private int cntTabs = 0;
    private final String XML_NAME = "test.xml";

    private SourceDbSetting[] sourceDbSetting = {
//        new SourceDbSetting(new DbConnection("jdbc:oracle:thin:@asvr:1521/adb", "test1", "test1", "test1"), Database.ORACLE, new OracleTestDefine())
//        new SourceDbSetting(new DbConnection("jdbc:postgresql://asvr:5432/Trans", "test1", "test1", "test1"), Database.POSTGRESQL, new PostgreTestDefine())
        new SourceDbSetting(new DbConnection("jdbc:h2:tcp://localhost/~/test", "test1", "test1", "test1"), Database.H2, new H2TestDefine()),
//        new SourceDbSetting(new DbConnection("jdbc:mysql://192.168.10.205:3306/test1", "test1", "test1", "test1"), Database.MYSQL, new MySqlTestDefine())
            
    };
    private DestinationDbSetting[] destinationDbSetting = {
//        new DestinationDbSetting(new DbConnection("jdbc:oracle:thin:@asvr:1521/adb", "test2", "test2", "test2"), Database.ORACLE),
//        new DestinationDbSetting(new DbConnection("jdbc:postgresql://asvr:5432/Trans", "test2", "test2", "test2"), Database.POSTGRESQL),
//        new DestinationDbSetting(new DbConnection("jdbc:h2:tcp://localhost/~/test", "test2", "test2", "test2"), Database.H2),
        new DestinationDbSetting(new DbConnection("jdbc:mysql://192.168.10.205:3306/test2", "test2", "test2", "test2"), Database.MYSQL)
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
    
    public MetadataSynchronizerTest() {
    }
    
    @Test
    public void work() throws Exception {
        for (SourceDbSetting srcSetting : sourceDbSetting) {
            for (DestinationDbSetting destSetting : destinationDbSetting) {
                runAllTestsDb(srcSetting, destSetting);
            }
        }
    }

    private void runAllTestsDb(SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
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
        mdsLogName = srcDatabase+"_"+destDatabase;
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(mdsLogName+".log");
            fileHandler.setFormatter(new LogFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(MetadataSynchronizerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            printText(cntTabs++, "*** runAllTestsDb ***");
            printText(cntTabs, "source: \t(url=",srcDbConnection.getUrl()," \tschema=",srcDbConnection.getSchema()," \tuser=",srcDbConnection.getUser(),")");
            printText(cntTabs, "destination: \t(url=",destDbConnection.getUrl()," \tschema=",destDbConnection.getSchema()," \tuser=",destDbConnection.getUser(),")");

            clearSchema(aDestinationSetting.getDbConnection());
//            runAllTestTables(aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
            runAllTestsFields(aSourceSetting,aDestinationSetting);
            runAllTestIndexes(aSourceSetting, aDestinationSetting);
            runAllTestKeys(aSourceSetting, aDestinationSetting);
            printText(--cntTabs, "*** end runAllTestsDb ***");
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
                logger.removeHandler(fileHandler);
            }
        }
    }

    private void runAllTestTables(DbConnection aSourceConnection, DbConnection aDestinationConnection) throws Exception {
        printText(cntTabs++, "*** runAllTestTables ***");
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);
        TableDefine[] stateA = {
            new TableDefine("tt1", "id1", null),
            new TableDefine("tt2", "id2", ""),
            new TableDefine("tt3", "id3", "Comment for table")
        };
        TableDefine[] stateB = {
            new TableDefine("tt2", "id2", "Comment"),
            new TableDefine("tt4", "id4", "Comment for table tT4"),
            new TableDefine("tt5", "id5", null)
        };
        TableDefine[] stateAB1 = {
            new TableDefine("tt1", "id1", null),
            new TableDefine("tt2", "id2", ""),
            new TableDefine("tt3", "id3", "Comment for table"),
            new TableDefine("tt5", "id5", null)
        };


        TableDefine[] stateAB2 = {
            new TableDefine("tt1", "id1", null),
            new TableDefine("tt3", "id3", "Comment for table"),
            new TableDefine("tt2", "id2", "Comment"),
            new TableDefine("tt5", "id5", null)
        };

        TableDefine[] stateC = {
            new TableDefine("tt6", "id6", null),
            new TableDefine("tt7", "id7", "c7")
        };

        TableDefine[] stateAB2C = {
            new TableDefine("tt1", "id1", null),
            new TableDefine("tt3", "id3", "Comment for table"),
            new TableDefine("tt2", "id2", "Comment"),
            new TableDefine("tt5", "id5", null),
            new TableDefine("tt7", "id7", "c7")
        };
        runTestTables("test1",aSourceConnection, aDestinationConnection, stateA, stateA, false, null);
        runTestTables("test2",aSourceConnection, aDestinationConnection, stateB, stateAB1, true, "tT1,Tt5");
        runTestTables("test3",aSourceConnection, aDestinationConnection, stateA, stateA, false, null);
        runTestTables("test4",aSourceConnection, aDestinationConnection, stateB, stateAB2, true, "tT1,tt2,Tt5");
        runTestTables("test5",aSourceConnection, aDestinationConnection, stateC, stateAB2C, false, "tT7");
        printText(--cntTabs, "*** end runAllTestTables ***");
    }

    private void runAllTestsFields(SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
        printText(cntTabs++, "*** runAllTestsFields ***");
        runTestFields("test1",aSourceSetting, aDestinationSetting, "Tbl", "Fld", true, true, true, 0, null, false, false);
        runTestFields("test2",aSourceSetting, aDestinationSetting, "Tbl", "Fld", true, true, true, 0, null, true, false);
//!!!!!!!!!!!!!!!!!!!!SEVERE: ORA-01451: column to be modified to NULL cannot be modified to NULL
//!!!   runTestFields("test3",aSourceSetting, aDestinationSetting, "Tbl", "Fld",false,true,true,0,null,false,false);
        runTestFields("test4",aSourceSetting, aDestinationSetting, "Tbl", "Fld", true, true, true, 0, "Comment", false, false);
        runTestFields("test5",aSourceSetting, aDestinationSetting, "Tbl", "Fld", true, true, true, 0, "", false, false);
        runTestFields("test6",aSourceSetting, aDestinationSetting, "Tbl", "Fld", true, true, true, 0, null, false, false);
        runTestFields("test7",aSourceSetting, aDestinationSetting, "Tbl", "Fld",true,true,true,0,null,false,true);
        printText(--cntTabs, "*** end runAllTestsFields ***");
    }

    private void runAllTestIndexes(SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {

//???????????????????????????????????????
//SEVERE: ORA-01408: such column list already indexed
//???????????????????????????????????????
        
        
        
        IndexDefine[] state1 = {
            //indexName, isClustered, isHashed, isUnique,arrayColumns            
            new IndexDefine("Ind1", true, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
            new IndexDefine("Ind2", true, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true)}),
            new IndexDefine("Ind3", true, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", true), new IndexColumnDefine("f3", true)})
        };
        
        
        
//?????????????????????? asc/desc !!!!!!!!!!!!!!!!!!
        
        IndexDefine[] state2 = {
            new IndexDefine("Ind1", true, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", false)}),
            new IndexDefine("Ind2", true, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", false), new IndexColumnDefine("f3", false)}),
            new IndexDefine("Ind3", true, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f4", true), new IndexColumnDefine("f3", false)})
        };
        
        IndexDefine[] state3 = {
            new IndexDefine("Ind1", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
            new IndexDefine("Ind2", true, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true)}),
            new IndexDefine("Ind3", true, false, false, new IndexColumnDefine[]{new IndexColumnDefine("f4", true), new IndexColumnDefine("f3", true)})
        };

        IndexDefine[] state4 = {
            new IndexDefine("Ind1", true, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f4", true), new IndexColumnDefine("f3", true)}),
            new IndexDefine("Ind2", false, true, false, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
            new IndexDefine("Ind3", true, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true)})
        };
        
        IndexDefine[] state5 = {
            new IndexDefine("Ind2", false, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f1", true)}),
            new IndexDefine("Ind3", true, false, true, new IndexColumnDefine[]{new IndexColumnDefine("f2", true), new IndexColumnDefine("f3", true), new IndexColumnDefine("f1", true)})
        };
        IndexDefine[] state6 = {
        };
        runTestIndexes("test1",aSourceSetting, aDestinationSetting, state1);
        //!!!!!!runTestIndexes("<<test2>>",aSourceSetting, aDestinationSetting, state2);
        runTestIndexes("test3",aSourceSetting, aDestinationSetting, state3);
        //???SEVERE: ORA-01408: such column list already indexed
        //???runTestIndexes("<<test4>>",aSourceSetting, aDestinationSetting, state4);
        
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//для Н2 после test3        (несколько полей в индексе)
//alter table test2.TBL drop column F4;
//Exception=На поле может ссылаться "TEST2.IND3"
//Column may be referenced by "TEST2.IND3"; SQL statement:
//alter table test2.TBL drop column F4 [90083-167]
//        runTestIndexes("test5",aSourceSetting, aDestinationSetting, state5);
//        runTestIndexes("test6",aSourceSetting, aDestinationSetting, state6);
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private void runAllTestKeys(SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting) throws Exception {
        Map <String,String[]> pkeys1 = new HashMap();
        pkeys1.put("Tbl1",new String[]{});
        pkeys1.put("Tbl2",new String[]{"f2_1"});
        pkeys1.put("Tbl3",new String[]{"f3_1","f3_2"});
        pkeys1.put("Tbl4",new String[]{"f4_1"});
        pkeys1.put("Tbl5",new String[]{"f5_1"});
        pkeys1.put("Tbl6",new String[]{"f6_1"});
        pkeys1.put("Tbl7",new String[]{"f7_1","f7_2"});
        pkeys1.put("Tbl8",new String[]{"f8_1","f8_2"});
        pkeys1.put("Tbl9",new String[]{"f9_1","f9_2"});
        
        Map <String,String[]> pkeys1_reverse = new HashMap();
        pkeys1_reverse.put("Tbl3",new String[]{"f3_2","f3_1"});
        pkeys1_reverse.put("Tbl7",new String[]{"f7_2","f7_1"});
        pkeys1_reverse.put("Tbl8",new String[]{"f8_2","f8_1"});
        pkeys1_reverse.put("Tbl9",new String[]{"f9_2","f9_1"});
        
        Map <String,String[]> pkeys2 = new HashMap();
        pkeys2.put("Tbl1",new String[]{});
        pkeys2.put("Tbl2",new String[]{"f2_1","f2_2"});
        pkeys2.put("Tbl3",new String[]{"f3_1"});
        pkeys2.put("Tbl4",new String[]{"f4_1","F4_2"});
        pkeys2.put("Tbl5",new String[]{"f5_1","f5_1d"});
        pkeys2.put("Tbl6",new String[]{"f6_1d"});
        pkeys2.put("Tbl7",new String[]{"f7_1"});
        pkeys2.put("Tbl8",new String[]{"f8_1"});
        pkeys2.put("Tbl9",new String[]{"f9_1","f9_2d","f9_3"});

        FKeyDefine [] fkeys0 = {};
        FKeyDefine [] fkeys1 = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl4","Tbl4_Fk1","Tbl2",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f4_1"}),    
            new FKeyDefine("Tbl4","Tbl4_Fk2","Tbl4",ForeignKeyRule.NOACTION,ForeignKeyRule.SETNULL, true, new String[] {"f4_2"}),    
            new FKeyDefine("Tbl5","Tbl5_Fk1","Tbl2",ForeignKeyRule.SETNULL,ForeignKeyRule.SETDEFAULT, true, new String[] {"f5_11"}),    
            new FKeyDefine("Tbl6","Tbl6_Fk1","Tbl6",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.CASCADE, true, new String[] {"f6_2"}),    
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f7_11","f7_12"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f8_1","f8_2"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f9_3","f9_4"})    
        };
        FKeyDefine [] fkeys1_reverse = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f7_12","f7_11"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f8_2","f8_1"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f9_4","f9_3"})    
        };
        FKeyDefine [] fkeys2 = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl4","Tbl4_Fk1","Tbl2",ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL, true, new String[] {"f4_11"}),    
            new FKeyDefine("Tbl4","Tbl4_Fk2","Tbl4",ForeignKeyRule.SETNULL,ForeignKeyRule.SETDEFAULT, true, new String[] {"f4_2"}),    
            new FKeyDefine("Tbl5","Tbl5_Fk1","Tbl2",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.CASCADE, true, new String[] {"f5_11"}),    
            new FKeyDefine("Tbl6","Tbl6_Fk1","Tbl6",ForeignKeyRule.CASCADE,ForeignKeyRule.NOACTION, true, new String[] {"f6_2"}),    
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.SETNULL,ForeignKeyRule.NOACTION, true, new String[] {"f7_11","f7_12"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL, true, new String[] {"f8_11","f8_12"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL, true, new String[] {"f9_3","f9_4"})    
        };
        FKeyDefine [] fkeys3 = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl4","Tbl4_Fk1","Tbl2",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.SETDEFAULT, true, new String[] {"f4_11"}),    
            new FKeyDefine("Tbl4","Tbl4_Fk2","Tbl4",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.CASCADE, true, new String[] {"f4_2"}),    
            new FKeyDefine("Tbl5","Tbl5_Fk1","Tbl2",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.NOACTION, true, new String[] {"f5_11"}),    
            new FKeyDefine("Tbl6","Tbl6_Fk1","Tbl6",ForeignKeyRule.NOACTION,ForeignKeyRule.SETNULL, true, new String[] {"f6_2"}),    
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.SETNULL, true, new String[] {"f7_11","f7_12"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.SETDEFAULT, true, new String[] {"f8_11","f8_12"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.SETDEFAULT, true, new String[] {"f9_3","f9_4"})    
        };
        FKeyDefine [] fkeys4 = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl4","Tbl4_Fk1","Tbl2",ForeignKeyRule.CASCADE,ForeignKeyRule.CASCADE, true, new String[] {"f4_11"}),    
            new FKeyDefine("Tbl4","Tbl4_Fk2","Tbl4",ForeignKeyRule.CASCADE,ForeignKeyRule.NOACTION, true, new String[] {"f4_2"}),    
            new FKeyDefine("Tbl5","Tbl5_Fk1","Tbl2",ForeignKeyRule.CASCADE,ForeignKeyRule.SETNULL, true, new String[] {"f5_11"}),    
            new FKeyDefine("Tbl6","Tbl6_Fk1","Tbl6",ForeignKeyRule.SETNULL,ForeignKeyRule.SETDEFAULT, true, new String[] {"f6_2"}),    
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.CASCADE,ForeignKeyRule.SETDEFAULT, true, new String[] {"f7_11","f7_12"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.CASCADE,ForeignKeyRule.CASCADE, true, new String[] {"f8_11","f8_12"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.CASCADE,ForeignKeyRule.CASCADE, true, new String[] {"f9_3","f9_4"})    
        };
        FKeyDefine [] fkeys5 = {  //NOACTION, SETNULL, SETDEFAULT, CASCADE;
            new FKeyDefine("Tbl4","Tbl4_Fk1","Tbl2",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f4_11","f4_2"}),    
            new FKeyDefine("Tbl4","Tbl4_Fk2","Tbl4",ForeignKeyRule.NOACTION,ForeignKeyRule.SETNULL, true, new String[] {"f4_3","f4_4"}),    
            new FKeyDefine("Tbl5","Tbl5_Fk1","Tbl2",ForeignKeyRule.NOACTION,ForeignKeyRule.SETDEFAULT, true, new String[] {"f5_11","f5_1d"}),    
            new FKeyDefine("Tbl6","Tbl6_Fk1","Tbl6",ForeignKeyRule.SETDEFAULT,ForeignKeyRule.CASCADE, true, new String[] {"f6_2d"}),    
            new FKeyDefine("Tbl7","Tbl7_Fk1","Tbl3",ForeignKeyRule.SETNULL,ForeignKeyRule.NOACTION, true, new String[] {"f7_11"}),    
            new FKeyDefine("Tbl8","Tbl8_Fk1","Tbl3",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f8_11"}),    
            new FKeyDefine("Tbl9","Tbl9_Fk1","Tbl9",ForeignKeyRule.NOACTION,ForeignKeyRule.NOACTION, true, new String[] {"f9_3d","f9_4","f9_5"})    
        };
        runTestKeys("test1", aSourceSetting, aDestinationSetting, pkeys1, fkeys0);
        runTestKeys("test2", aSourceSetting, aDestinationSetting, pkeys1, fkeys1);
        runTestKeys("test2_reverse", aSourceSetting, aDestinationSetting, pkeys1_reverse, fkeys1_reverse);
        runTestKeys("test3", aSourceSetting, aDestinationSetting, pkeys1, fkeys2);
        runTestKeys("test4", aSourceSetting, aDestinationSetting, pkeys1, fkeys3);
        runTestKeys("test5", aSourceSetting, aDestinationSetting, pkeys1, fkeys4);
        runTestKeys("test6", aSourceSetting, aDestinationSetting, pkeys2, fkeys5);
    
    }

    private void runTestTables(String aTestName,DbConnection aSourceConnection, DbConnection aDestinationConnection, TableDefine[] aSourceTableDefine, TableDefine[] aDestinationTableDefine, boolean aNoDropTables, String aTablesList) throws Exception {
        printText(cntTabs++, "*** ","runTestTables ",aTestName," ***");
sqlLogName = "Tables_"+aTestName;        
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);
        clearSchema(aSourceConnection);
        createTables(aSourceConnection,aSourceTableDefine);
        assertNotNull(aSourceConnection);
        assertNotNull(aSourceTableDefine);
        checkTables(aSourceConnection, aSourceTableDefine);
        synchronizeDb(aSourceConnection, aDestinationConnection, aNoDropTables, aTablesList);
        assertNotNull(aDestinationConnection);
        assertNotNull(aDestinationTableDefine);
        checkTables(aDestinationConnection, aDestinationTableDefine);
        assertNotNull(XML_NAME);
        assertNotNull(aSourceTableDefine);
        checkTables(XML_NAME, aSourceTableDefine);
        printText(--cntTabs, "*** ","end runTestTables ",aTestName," ***");
    }

    private void runTestFields(String aTestName,SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, String aTableName, String aFieldName, boolean aNullable, boolean aReadonly, boolean aSigned, int aPrecision, String aDescription, boolean changeSize, boolean changeType) throws Exception {
        printText(cntTabs++, "*** ","runTestFields ",aTestName," ***");
sqlLogName = "Fields_"+aTestName;        
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        assertNotNull(aSourceSetting.getDbConnection());
        assertNotNull(aDestinationSetting.getDbConnection());
        assertNotNull(aSourceSetting.getDatabase());
        assertNotNull(aDestinationSetting.getDatabase());
        assertNotNull(aSourceSetting.getDbTestDefine());
        clearSchema(aSourceSetting.getDbConnection());
        createFields(aSourceSetting, aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
//!!!!!!!!!!!!!!!!!!
//if (aTestName.equalsIgnoreCase("test2"))        
//    return;
        checkFields(aSourceSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
        synchronizeDb(aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
        checkFields(aDestinationSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aDestinationSetting.getDatabase(), aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
        assertNotNull(XML_NAME);
        checkFields(XML_NAME, aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(), aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
        printText(--cntTabs, "*** ","end runTestFields ",aTestName," ***");
    }

    private void runTestIndexes(String aTestName,SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, IndexDefine[] aIndexesDefine) throws Exception {
        printText(cntTabs++, "*** ","runTestIndexes ",aTestName," ***");
sqlLogName = "Indexes_"+aTestName;        
        assertNotNull(aSourceSetting);
        assertNotNull(aDestinationSetting);
        assertNotNull(aIndexesDefine);
        assertNotNull(aSourceSetting.getDbConnection());
        assertNotNull(aDestinationSetting.getDbConnection());
        assertNotNull(aSourceSetting.getDatabase());
        assertNotNull(aDestinationSetting.getDatabase());
        assertNotNull(aSourceSetting.getDbTestDefine());
        clearSchema(aSourceSetting.getDbConnection());
        String tableName = "Tbl";
        createIndexes(aSourceSetting.getDbConnection(), tableName, aIndexesDefine);
        checkIndexes(aSourceSetting.getDbConnection(), tableName, aIndexesDefine, aSourceSetting.getDatabase().isCheckIndexClustered(), aSourceSetting.getDatabase().isCheckIndexHashed());
        synchronizeDb(aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
        checkIndexes(aDestinationSetting.getDbConnection(), tableName, aIndexesDefine, aDestinationSetting.getDatabase().isCheckIndexClustered(), aDestinationSetting.getDatabase().isCheckIndexHashed());
        assertNotNull(XML_NAME);
        checkIndexes(XML_NAME, tableName, aIndexesDefine, aSourceSetting.getDatabase().isCheckIndexClustered(), aSourceSetting.getDatabase().isCheckIndexHashed());
        printText(--cntTabs, "*** ","end runTestIndexes ",aTestName," ***");
    }
    
    private void runTestKeys(String aTestName, SourceDbSetting aSourceSetting, DestinationDbSetting aDestinationSetting, Map <String,String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs++, "*** ","runTestKeys ",aTestName," ***");
sqlLogName = "Keys_"+aTestName;        
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
        checkKeys(aSourceSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(),aPKeysDefine, aFKeysDefine);
//if ("test2_reverse".equalsIgnoreCase(aTestName) ) {
//    return;//aTestName = "test2";
//}
        synchronizeDb(aSourceSetting.getDbConnection(), aDestinationSetting.getDbConnection());
    
        checkKeys(aDestinationSetting.getDbConnection(), aSourceSetting.getDbTestDefine(), aDestinationSetting.getDatabase(),aPKeysDefine, aFKeysDefine);
        assertNotNull(XML_NAME);
        checkKeys(XML_NAME, aSourceSetting.getDbTestDefine(), aSourceSetting.getDatabase(),aPKeysDefine, aFKeysDefine);
        printText(--cntTabs, "*** ","end runTestKeys ",aTestName," ***");
    }

    private void createTables(DbConnection aDbConnection, TableDefine[] aTableDefine) throws Exception {
        printText(cntTabs, "createTables \turl=",aDbConnection.getUrl()," \tschema=",aDbConnection.getSchema()," \tuser=",aDbConnection.getUser());
        DbClient client = createClient(aDbConnection);
        assertNotNull(client);
        SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
        assertNotNull(driver);
        for (TableDefine tableDefine : aTableDefine) {
            String sql = driver.getSql4EmptyTableCreation(aDbConnection.getSchema(), tableDefine.getTableName(), tableDefine.getPkFieldName());
            executeSql(client, sql);
            sql = driver.getSql4CreateTableComment(aDbConnection.getSchema(), tableDefine.getTableName(), tableDefine.getDescription());
            executeSql(client, sql);
        }
        executeSql(client, "commit");
    }
    private void createFields(SourceDbSetting aSourceSetting, String aTableName, String aFieldName, boolean aNullable, boolean aReadonly, boolean aSigned, int aPrecision, String aDescription, boolean changeSize, boolean changeType) throws Exception {
        printText(cntTabs, "createFields \turl=",aSourceSetting.getDbConnection().getUrl()," \tschema=",aSourceSetting.getDbConnection().getSchema()," \tuser=",aSourceSetting.getDbConnection().getUser());
//        printText(cntTabs++, "*** createFields ***");
//        assertNotNull(aSourceSetting);
        DbConnection dbConnection = aSourceSetting.getDbConnection();
//        assertNotNull(dbConnection);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", dbConnection.getUrl(), dbConnection.getSchema(), dbConnection.getUser()));

        DbClient client = createClient(dbConnection);
        assertNotNull(client);
        SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
        assertNotNull(driver);
        DbTestDefine dbTestDefine = aSourceSetting.getDbTestDefine();
        assertNotNull(dbTestDefine);
        String pkFieldName = "id";
        String[] dbTypes = dbTestDefine.getOriginalTypes();
        for (int i = 0; i < dbTypes.length; i++) {
            String tableName = aTableName + i;
            String fieldName = aFieldName + i;
            String description = aDescription;
            if (description != null) {
                description += " -for " + tableName.toUpperCase();
            }
            int index = i;
            if (changeType) {
                index = i + dbTypes.length / 2;
                if (index >= dbTypes.length) {
                    index -= dbTypes.length;
                }
            }
            String dbType = dbTypes[index];
//            logText(cntTabs, String.format("table=%s\ttype=%s", tableName, dbType));

            executeSql(client, driver.getSql4EmptyTableCreation(dbConnection.getSchema(), tableName, pkFieldName));
            Field field = new Field();
            field.setSchemaName(dbConnection.getSchema());
            field.setName(fieldName);
            field.setTableName(tableName);
            field.setNullable(aNullable);
            field.setReadonly(aReadonly);
            field.setSigned(aSigned);
            field.setPrecision(aPrecision);
            //field.setDescription(description);

            DataTypeInfo typeInfo = new DataTypeInfo();
            typeInfo.setSqlType(driver.getJdbcTypeByRDBMSTypename(dbType));
            field.setTypeInfo(typeInfo);
            int dbSize = dbTestDefine.getOriginalSize(dbType);
            int dbScale = dbTestDefine.getOriginalScale(dbType);
            if (changeSize) {
                dbSize = dbSize * 2;
                dbScale = dbScale * 2;
            }
            if (dbSize >= 0) {
                field.setSize(dbSize);
            }
            if (dbScale >= 0) {
                field.setScale(dbScale);
            }

            String sql = String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, tableName) + driver.getSql4FieldDefinition(field);
            executeSql(client, sql);
            String[] sqls = driver.getSql4CreateColumnComment(dbConnection.getSchema(), tableName, fieldName, description);
            executeSql(client, sqls);
        }
        executeSql(client, "commit");
//        printText(--cntTabs, "*** end createFields ***");
    }
    
    private void createIndexes(DbConnection aDbConnection,  String aTableName, IndexDefine[] aIndexesDefine) throws Exception {
        printText(cntTabs, "createIndexes \turl=",aDbConnection.getUrl()," \tschema=",aDbConnection.getSchema()," \tuser=",aDbConnection.getUser());
//        printText(cntTabs++, "*** createIndexes ***");
//        assertNotNull(dbConnection);
        assertNotNull(aIndexesDefine);
        clearSchema(aDbConnection);
        Set<String> fields = new HashSet<>();
        DbClient client = createClient(aDbConnection);
        assertNotNull(client);
        SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
        assertNotNull(driver);
        String pkField = "id";
        // create table
        String sql = driver.getSql4EmptyTableCreation(aDbConnection.getSchema(), aTableName, pkField);
        executeSql(client, sql);
        // drop pkey
        String defaultPlatypusPkName = aTableName+"_pk"; 
        sql = driver.getSql4DropPkConstraint(aDbConnection.getSchema(), new PrimaryKeySpec(aDbConnection.getSchema(), aTableName, pkField, defaultPlatypusPkName));
        executeSql(client, sql);
        for (IndexDefine indexDefine : aIndexesDefine) {
            IndexColumnDefine[] columnsDefines = indexDefine.getColumns();
            assertNotNull(columnsDefines);
            DbTableIndexSpec indexSpec = new DbTableIndexSpec();
            indexSpec.setName(indexDefine.getIndexName());
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
                    field.setName(fieldName);
                    field.setSchemaName(aDbConnection.getSchema());
                    field.setTypeInfo(DataTypeInfo.VARCHAR);
                    field.setSize(10);
                    sql = String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, aTableName) + driver.getSql4FieldDefinition(field);
                    executeSql(client, sql);
                    fields.add(fieldName.toUpperCase());
                }
                indexSpec.addColumn(new DbTableIndexColumnSpec(fieldName, columnDefine.isAscending()));
            }
            // create index
            sql = driver.getSql4CreateIndex(aDbConnection.getSchema(), aTableName, indexSpec);
            executeSql(client, sql);
        }
        executeSql(client, "commit");
//        printText(--cntTabs, "*** end createIndexes ***");
    }
    
    private void createKeys(DbConnection aDbConnection, Map <String,String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "createKeys \turl=",aDbConnection.getUrl()," \tschema=",aDbConnection.getSchema()," \tuser=",aDbConnection.getUser());
//        printText(cntTabs++, "*** createKeys ***");
//        assertNotNull(dbConnection);
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
        
        clearSchema(aDbConnection);
        Map<String,Set<String>> fieldsNames = new HashMap<>();
        Map<String,List<PrimaryKeySpec>> pkeys = new HashMap<>();
        DbClient client = createClient(aDbConnection);
        assertNotNull(client);
        SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();
        assertNotNull(driver);
        String pkField = "id";

        logText(cntTabs, "start create primaryKeys");
        for (String tableName: aPKeysDefine.keySet()) {
            String upperTableName = tableName.toUpperCase();
            //create table
            String sql = driver.getSql4EmptyTableCreation(aDbConnection.getSchema(), tableName, pkField);
            executeSql(client, sql);
            // drop pkey
            String defaultPlatypusPkName = tableName+"_pk"; 
            sql = driver.getSql4DropPkConstraint(aDbConnection.getSchema(), new PrimaryKeySpec(aDbConnection.getSchema(), tableName, pkField, defaultPlatypusPkName));
            executeSql(client, sql);
            String[] fieldsDefine = aPKeysDefine.get(tableName);
            Set<String> tableFields = new HashSet();
            List<PrimaryKeySpec> pkSpecs = new ArrayList();
            if (fieldsDefine != null) {
                for (String fieldName : fieldsDefine) {
                    // create field
                    assertNotNull(fieldName);
                    Field field = new Field();
                    field.setName(fieldName);
                    field.setSchemaName(aDbConnection.getSchema());
                    field.setTypeInfo(DataTypeInfo.VARCHAR);
                    field.setSize(10);
                    //field.setNullable(true);
                    field.setNullable(false);
                    sql = String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, tableName) + driver.getSql4FieldDefinition(field);
                    executeSql(client, sql);
                    tableFields.add(fieldName.toUpperCase());
                    // create spec
                    pkSpecs.add(new PrimaryKeySpec(aDbConnection.getSchema(), tableName, fieldName, defaultPlatypusPkName));
                }
            }
            fieldsNames.put(upperTableName, tableFields);
            pkeys.put(upperTableName, pkSpecs);
            // create pk
            if (pkSpecs.size() > 0) {
                sql = driver.getSql4CreatePkConstraint(aDbConnection.getSchema(), pkSpecs);
                executeSql(client, sql);
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
                    fkSpec.setSchema(aDbConnection.getSchema());
                    fkSpec.setTable(tableName);
                    fkSpec.setCName(fkeyDefine.getName());
                    fkSpec.setField(fieldName);
                    fkSpec.setReferee(pkSpecs.get(i));
//????????                    
                    fkSpec.setFkDeleteRule(fkeyDefine.getDeleteRule());
                    fkSpec.setFkUpdateRule(fkeyDefine.getUpdateRule());
                    fkSpec.setFkDeferrable(fkeyDefine.isDeferrable());
                    // create field
                    Set<String> tableFields = fieldsNames.get(tableName.toUpperCase());
                    if (!tableFields.contains(fieldName.toUpperCase())) {
                        Field field = new Field();
                        field.setName(fieldName);
                        field.setSchemaName(aDbConnection.getSchema());
                        field.setTypeInfo(DataTypeInfo.VARCHAR);
                        field.setSize(10);
                        String sql = String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, tableName) + driver.getSql4FieldDefinition(field);
                        executeSql(client, sql);
                        tableFields.add(fieldName.toUpperCase());
                        fieldsNames.put(tableName.toUpperCase(), tableFields);
                    }
                    fkSpecs.add(fkSpec);
                }
                if (fkSpecs.size() > 0) {
                    // для теста разных методов
                    if (fkSpecs.size() == 1) {
                        String sql = driver.getSql4CreateFkConstraint(aDbConnection.getSchema(), fkSpecs.get(0));
                        executeSql(client,sql);
                    } else {
                        String sql = driver.getSql4CreateFkConstraint(aDbConnection.getSchema(), fkSpecs);
                        executeSql(client,sql);
                    }
                }
            }
        }
        executeSql(client, "commit");
//        printText(--cntTabs, "*** end createKeys ***");
    }
    
    
    private void checkTables(DbConnection aDbConnection, TableDefine[] aTablesDefine) throws Exception {
        printText(cntTabs, "checkTables: "," \t", "url=",aDbConnection.getUrl()," \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
//        printText(cntTabs++, "*** checkTables ***");
//        assertNotNull(aDbConnection);
//        assertNotNull(aTablesDefine);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkTables(databaseStructure, aTablesDefine);
//        printText(--cntTabs, "*** end checkTables ***");
    }
    private void checkTables(String aXmlFileName, TableDefine[] aTablesDefine) throws Exception {
        printText(cntTabs, "checkTables: "," \t", "xml=",aXmlFileName);
//              printText(cntTabs, "checkTables");
//        printText(cntTabs++, "*** checkTables ***");
//        assertNotNull(aXmlFileName);
//        assertNotNull(aTablesDefine);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkTables(databaseStructure, aTablesDefine);
//        printText(--cntTabs, "*** end checkTables ***");
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

    private void checkFields(DbConnection aDbConnection, DbTestDefine aTestDefine, Database aDatabase, String aTableName, String aFieldName, boolean aNullable, boolean aReadonly, boolean aSigned, int aPrecision, String aDescription, boolean changeSize, boolean changeType) throws Exception {
        printText(cntTabs, "checkFields: "," \t", "url=",aDbConnection.getUrl()," \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
  //      printText(cntTabs++, "*** checkFields ***");
        assertNotNull(aDbConnection);
        assertNotNull(aTestDefine);
        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkFields(databaseStructure, aTestDefine, aDatabase, aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
//        printText(--cntTabs, "*** end checkFields ***");
    }
    private void checkFields(String aXmlFileName, DbTestDefine aTestDefine, Database aDatabase, String aTableName, String aFieldName, boolean aNullable, boolean aReadonly, boolean aSigned, int aPrecision, String aDescription, boolean changeSize, boolean changeType) throws Exception {
        printText(cntTabs, "checkFields: "," \t", "xml=",aXmlFileName);
  //      printText(cntTabs++, "*** checkFields ***");
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkFields(databaseStructure, aTestDefine, aDatabase, aTableName, aFieldName, aNullable, aReadonly, aSigned, aPrecision, aDescription, changeSize, changeType);
//        printText(--cntTabs, "*** end checkFields ***");
    }
    private void checkFields(DBStructure aDatabaseStructure, DbTestDefine aTestDefine, Database aDatabase, String aTableName, String aFieldName, boolean aNullable, boolean aReadonly, boolean aSigned, int aPrecision, String aDescription, boolean changeSize, boolean changeType) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);

        String tableNameUpper = aTableName.toUpperCase();
        String fieldNameUpper = aFieldName.toUpperCase();
        String[] originalTypes = aTestDefine.getOriginalTypes();
        for (int i = 0; i < originalTypes.length; i++) {
            int index = i;
            if (changeType) {
                index = i + originalTypes.length / 2;
                if (index >= originalTypes.length) {
                    index -= originalTypes.length;
                }
            }
            String originalType = originalTypes[index];
            String tableName = tableNameUpper + i;
            String fieldName = fieldNameUpper + i;
            String description = aDescription;
            if (description != null) {
                description += " -for " + tableName;
            }
            TableStructure tblStructure = dbStructure.get(tableName);
            assertNotNull(tblStructure);
            assertEquals(tableName, tblStructure.getTableName().toUpperCase());
            Fields fields = tblStructure.getTableFields();
            assertNotNull(fields);
            Field field = fields.get(fieldName);
            assertNotNull(field);
            assertEquals(field.getName().toUpperCase(), fieldName);
            //??????
            if (description == null) {
                description = "";
            }
            if (field.getDescription() == null) {
                assertEquals("", description);
            } else {
                assertEquals(field.getDescription(), description);
            }
//---            assertEquals(field.getSchemaName().toUpperCase(),aSchemaName.toUpperCase());
            DataTypeInfo typeInfo = field.getTypeInfo();
            String sqlTypeName = typeInfo.getSqlTypeName();
            String dbType = aTestDefine.getDBType(originalType, aDatabase.getCode());
            
            logText(cntTabs, String.format("table=%s \toriginalType=%s \tplatypus type=%s", tableName, originalType, sqlTypeName));
            assertNotNull(dbType);
            assertEquals(sqlTypeName.toUpperCase(), dbType.toUpperCase());
            int dbSize = aTestDefine.getDBSize(originalType, aDatabase.getCode());
            int dbScale = aTestDefine.getDBScale(originalType, aDatabase.getCode());
            if (changeSize) {
                dbSize = dbSize * 2;
                dbScale = dbScale * 2;
            }
            if (dbSize >= 0) {
                assertEquals(field.getSize(), dbSize);
            }
            if (dbScale >= 0) {
                assertEquals(field.getScale(), dbScale);
            }

//---            assertEquals(field.getPrecision(),aPrecision);
            assertEquals(field.isNullable(), aNullable);
//---            assertEquals(field.isReadonly(),aReadonly);
//---            assertEquals(field.isSigned(),aSigned);
            assertEquals(field.isPk(), false);
            assertEquals(field.isFk(), false);
        }
    }

    private void checkIndexes(DbConnection aDbConnection, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed) throws Exception {
        printText(cntTabs, "checkIndexes: "," \t", "url=",aDbConnection.getUrl()," \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
//        printText(cntTabs++, "*** checkIndexes ***");
//        assertNotNull(aDbConnection);
        assertNotNull(aTableName);
        assertNotNull(aIndexesDefine);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        checkIndexes(databaseStructure, aTableName, aIndexesDefine, checkClustered, checkHashed);
//        printText(--cntTabs, "*** end checkIndexes ***");
    }
    
    private void checkIndexes(String aXmlFileName, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed) throws Exception {
        printText(cntTabs, "checkIndexes: "," \t", "xml=",aXmlFileName);
//        printText(cntTabs++, "*** checkIndexes ***");
//        assertNotNull(aDbConnection);
        assertNotNull(aTableName);
        assertNotNull(aIndexesDefine);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        checkIndexes(databaseStructure, aTableName, aIndexesDefine, checkClustered, checkHashed);
//        printText(--cntTabs, "*** end checkIndexes ***");
    }
    private void checkIndexes(DBStructure aDatabaseStructure, String aTableName, IndexDefine[] aIndexesDefine, boolean checkClustered, boolean checkHashed) throws Exception {
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
                assertEquals(originalIndexName.toUpperCase(),indexNameDefine.toUpperCase());
                DbTableIndexSpec indexSpec = indexSpecs.get(originalIndexName);
                assertNotNull(indexSpec);
                assertEquals(indexSpec.getName().toUpperCase(),indexNameDefine.toUpperCase());
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
                assertEquals(columnsSpec.size(),define.getColumns().length);
                for (int i = 0; i < define.getColumns().length; i++) {
                    DbTableIndexColumnSpec columnSpec = columnsSpec.get(i);
                    IndexColumnDefine columnDefine = define.getColumns()[i];
                    assertNotNull(columnSpec);
                    assertNotNull(columnDefine);
                    assertEquals(columnSpec.getColumnName().toUpperCase(),columnDefine.getColumnName().toUpperCase());
                    assertEquals(columnSpec.isAscending(), columnDefine.isAscending());
                }
            }
        } else {
            assertTrue(indexSpecs == null || indexSpecs.isEmpty());
        }
    }
    
    private void checkKeys(DbConnection aDbConnection, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "checkKeys: "," \t", "url=",aDbConnection.getUrl()," \tschema=", aDbConnection.getSchema(), " \tuser=", aDbConnection.getUser());
//        printText(cntTabs++, "*** checkKeys ***");
//        assertNotNull(aDbConnection);
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
//        printText(cntTabs, String.format("database:(url=%s\tschema=%s\tuser=%s)", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
        assertNotNull(databaseStructure);
        checkKeys(databaseStructure, aTestDefine, aDatabase, aPKeysDefine, aFKeysDefine);
//        printText(--cntTabs, "*** end checkKeys ***");
    }
    private void checkKeys(String aXmlFileName, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        printText(cntTabs, "checkKeys: "," \t", "xml=",aXmlFileName);
//        printText(cntTabs++, "*** checkKeys ***");
        assertNotNull(aPKeysDefine);
        assertNotNull(aFKeysDefine);
        MetadataSynchronizer mds = new MetadataSynchronizer();
        mds.initDefaultLoggers(null, Level.ALL, false);
        DBStructure databaseStructure = mds.readDBStructureFromFile(aXmlFileName);
        assertNotNull(databaseStructure);
        checkKeys(databaseStructure, aTestDefine, aDatabase, aPKeysDefine, aFKeysDefine);
//        printText(--cntTabs, "*** end checkKeys ***");
    }
    private void checkKeys(DBStructure aDatabaseStructure, DbTestDefine aTestDefine, Database aDatabase, Map<String, String[]> aPKeysDefine, FKeyDefine[] aFKeysDefine) throws Exception {
        assertNotNull(aDatabaseStructure);
        Map<String, TableStructure> dbStructure = aDatabaseStructure.getTablesStructure();
        assertNotNull(dbStructure);
        logText(cntTabs, "start check primary keys");
        assertEquals(dbStructure.keySet().size(), aPKeysDefine.size());
        for (String tableName : aPKeysDefine.keySet()) {
            assertNotNull(tableName);
            logText(cntTabs, "table=",tableName);
            String[] pkFields = aPKeysDefine.get(tableName);
            TableStructure tblStructure = dbStructure.get(tableName.toUpperCase());
            assertNotNull(tblStructure);
            List<PrimaryKeySpec> tablePKeySpecs = tblStructure.getTablePKeySpecs();
            if (pkFields != null && pkFields.length > 0) {
                assertNotNull(tablePKeySpecs);
                assertEquals(pkFields.length, tablePKeySpecs.size());
                for (int i = 0 ; i < pkFields.length; i++) {
                    String fieldDefine = pkFields[i];
                    assertNotNull(fieldDefine);
                    PrimaryKeySpec pkSpec = tablePKeySpecs.get(i);
                    assertNotNull(pkSpec);
                    String fieldSpec = pkSpec.getField();
                    assertNotNull(fieldSpec);
                    assertEquals(fieldSpec.toUpperCase(),fieldDefine.toUpperCase());
//                    String schemaSpec = pkSpec.getSchema();
//                    assertNotNull(schemaSpec);
//                    assertEquals(schemaSpec.toUpperCase(),aDbConnection.getSchema().toUpperCase());
                    String tableSpec = pkSpec.getTable();
                    assertNotNull(tableSpec);
                    assertEquals(tableSpec.toUpperCase(), tableName.toUpperCase());
                }
            } else {
                assertTrue(tablePKeySpecs == null || tablePKeySpecs.isEmpty() );
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
            logText(cntTabs, "table=",tableName," \t","fkName=",cNameDefine);
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
                    //                    assertNotNull(fkSpec.getSchema());
                    //                    assertEquals(fkSpec.getSchema().toUpperCase(), aDbConnection.getSchema().toUpperCase());
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
                    assertEquals(fieldNameSpec.toUpperCase(),fieldNameDefine.toUpperCase());
                    assertNotNull(cNameSpec);
                    assertEquals(cNameSpec.toUpperCase(), cNameDefine.toUpperCase());
                    
                    ForeignKeyRule fkDeleteRuleDefine = aTestDefine.getFKeyDeleteRule(fkDeleteRuleOriginal,aDatabase.getCode());
                    ForeignKeyRule fkUpdateRuleDefine = aTestDefine.getFKeyUpdateRule(fkUpdateRuleOriginal,aDatabase.getCode());
//??????                    Boolean fKeyDeferrableDefine = aTestDefine.getFKeyDeferrable(fkDeferrableOriginal,aDatabase.getCode());
                    assertEquals(fkDeleteRuleDefine, fkDeleteRuleSpec);
                    assertEquals(fkUpdateRuleDefine, fkUpdateRuleSpec);
                    //            printText(cntTabs, String.format("table=%s \toriginalType=%s \tplatypus type=%s", tableName, originalType, sqlTypeName));
                    //                    //?????????
                    //                    assertNotNull(referee.getSchema());
                    //                    assertEquals(referee.getSchema().toUpperCase(), aDbConnection.getSchema().toUpperCase());
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
//        printText(--cntTabs, "*** end checkKeys ***");
    }

    private DbClient createClient(DbConnection aDbConnection) throws Exception {
//+++        printText(cntTabs, "createClient \turl=",aDbConnection.getUrl()," \tschema=",aDbConnection.getSchema()," \tuser=",aDbConnection.getUser());
//        printText(cntTabs++, "*** createClient ***");
//        assertNotNull(aDbConnection);
//        printText(cntTabs, String.format("url=%s \tschema=%s \tuser=%s", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        EasSettings settings = EasSettings.createInstance(aDbConnection.getUrl());
        if (settings instanceof DbConnectionSettings) {
            settings.getInfo().put(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, aDbConnection.getSchema());
            settings.getInfo().put(ClientConstants.DB_CONNECTION_USER_PROP_NAME, aDbConnection.getUser());
            settings.getInfo().put(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME, aDbConnection.getPassword());
            ((DbConnectionSettings) settings).setInitSchema(false);
        }
        settings.setUrl(aDbConnection.getUrl());
        Client lclient = ClientFactory.getInstance(settings);
        assert lclient instanceof DbClient;
        DbClient client = (DbClient) lclient;
//        printText(--cntTabs, "*** end createClient ***");
        return client;
    }

    private void clearSchema(DbConnection aDbConnection) throws Exception {
//        printText(cntTabs++, "*** clearSchema ***");
//+++        printText(cntTabs, "clearSchema \turl=",aDbConnection.getUrl()," \tschema=",aDbConnection.getSchema()," \tuser=",aDbConnection.getUser());
//        assertNotNull(aDbConnection);
//        printText(cntTabs, String.format("url=%s \tschema=%s \tuser=%s", aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser()));
        MetadataSynchronizer mds = null;
        DbClient client = null;
        try {
            mds = new MetadataSynchronizer();
            mds.initDefaultLoggers(null, Level.ALL, false);
            DBStructure databaseStructure = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
            assertNotNull(databaseStructure);
            Map<String, TableStructure> dbStructure = databaseStructure.getTablesStructure();
            assertNotNull(dbStructure);
            client = createClient(aDbConnection);
            SqlDriver driver = client.getDbMetadataCache(null).getConnectionDriver();

            for (String upperTblName : dbStructure.keySet()) {
                TableStructure tblStructure = dbStructure.get(upperTblName);
                Map<String, List<ForeignKeySpec>> fkeySpecs = tblStructure.getTableFKeySpecs();
                if (fkeySpecs != null) {
                    for (String fkName : fkeySpecs.keySet()) {
                        List<ForeignKeySpec> fkeys = fkeySpecs.get(fkName);
                        assertNotNull(fkeys);
                        assert fkeys.size() > 0;
                        String sql = driver.getSql4DropFkConstraint(aDbConnection.getSchema(), fkeys.get(0));
                        assertNotNull(sql);
                        executeSql(client, sql);
                    }
                }
            }
            for (String upperTblName : dbStructure.keySet()) {
                TableStructure tblStructure = dbStructure.get(upperTblName);
                String sql = driver.getSql4DropTable(aDbConnection.getSchema(), tblStructure.getTableName());
                assertNotNull(sql);
                executeSql(client, sql);
            }
            DBStructure databaseStructure2 = mds.readDBStructure(aDbConnection.getUrl(), aDbConnection.getSchema(), aDbConnection.getUser(), aDbConnection.getPassword());
            assertNotNull(databaseStructure2);
            Map<String, TableStructure> dbStructure2 = databaseStructure2.getTablesStructure();
            assertNotNull(dbStructure2);
            assertEquals(dbStructure2.size(), 0);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
//        printText(--cntTabs, "*** end clearSchema ***");
    }

    private void synchronizeDb(DbConnection aSourceConnection, DbConnection aDestinationConnection, boolean aNoDropTables, String aTablesList) throws Exception {
//        printText(cntTabs++, "*** synchronizeDb ***");
        printText(cntTabs, "synchronizeDb \tnoDropTables=",String.valueOf(aNoDropTables).toUpperCase()," \ttables=",aTablesList);
        assertNotNull(aSourceConnection);
        assertNotNull(aDestinationConnection);

//        printText(cntTabs, String.format("source: \t(url=%s\tschema=%s\tuser=%s)", aSourceConnection.getUrl(), aSourceConnection.getSchema(), aSourceConnection.getUser()));
//        printText(cntTabs, String.format("destination: \t(url=%s\tschema=%s\tuser=%s)", aDestinationConnection.getUrl(), aDestinationConnection.getSchema(), aDestinationConnection.getUser()));
//        printText(cntTabs, String.format("no drop tables=%s \ttables for syncronize=%s", aNoDropTables, aTablesList));

        MetadataSynchronizer mds = null;
        try {
            mds = new MetadataSynchronizer();
            mds.initDefaultLoggers(null, Level.ALL, false);
            mds.setUrlFrom(aSourceConnection.getUrl());
            mds.setSchemaFrom(aSourceConnection.getSchema());
            mds.setUserFrom(aSourceConnection.getUser());
            mds.setPasswordFrom(aSourceConnection.getPassword());

            mds.setUrlTo(aDestinationConnection.getUrl());
            mds.setSchemaTo(aDestinationConnection.getSchema());
            mds.setUserTo(aDestinationConnection.getUser());
            mds.setPasswordTo(aDestinationConnection.getPassword());
            mds.setNoDropTables(aNoDropTables);
            mds.parseTablesList(aTablesList, ",");
            mds.setFileXml(XML_NAME);
//!!!!!!!!!!!!   
if (mdsLogName != null && sqlLogName != null) {       
    mds.initSqlLogger(mdsLogName + "_" + sqlLogName+".log", null, Level.ALL, false, new LogFormatter());
    mds.initErrorLogger(mdsLogName + "_" + sqlLogName+"_err.log", null,Level.ALL, false, new LogFormatter());
}    

            mds.run();
        } finally {
            if (mds != null && mdsLogName != null && sqlLogName != null) {       
                mds.clearSqlLogger();
                mds.clearErrorLogger();
            }
            
        }
//        printText(--cntTabs, "*** end synchronizeDb ***");
    }

    private void synchronizeDb(DbConnection aSourceConnection, DbConnection aDestinationConnection) throws Exception {
        synchronizeDb(aSourceConnection, aDestinationConnection, false, null);
    }

    private void executeSql(DbClient aClient, String aSql) throws Exception {
//        System.out.println("sql="+aSql);
        logText(cntTabs+1, "sql="+aSql);

        assertNotNull(aClient);
        assertNotNull(aSql);
        assertFalse(aSql.isEmpty());
        SqlCompiledQuery compiledSql = new SqlCompiledQuery(aClient, null, aSql);
        compiledSql.enqueueUpdate();
        aClient.commit(null);
    }

    private void executeSql(DbClient aClient, String[] aSqls) throws Exception {
        for (String sql : aSqls) {
            executeSql(aClient, sql);
        }
    }
    
    private void printText(int countTabs, String ... texts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countTabs; i++) {
            sb.append("\t");
        }
        if (texts != null) {
            for (String text: texts) {
                sb.append(text);
            }
        }
        System.out.println(sb.toString());
        if (logger != null) {
            Logger.getLogger(MetadataSynchronizerTest.class.getName()).log(Level.INFO, sb.toString());
        }
    }

    private void logText(int countTabs, String ... texts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < countTabs; i++) {
            sb.append("\t");
        }
        if (texts != null) {
            for (String text: texts) {
                sb.append(text);
            }
        }
        if (logger != null) {
            Logger.getLogger(MetadataSynchronizerTest.class.getName()).log(Level.INFO, sb.toString());
        }
    }
    
    
}
