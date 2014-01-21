/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.DatabaseFlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.settings.DbConnectionSettings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class DbClientTest {

    protected static final int TEST_THREADS_COUNT = 64;
    protected static int ORDER_ID = 280513973;
    protected static int AMOUNT = 45;
    protected static int GOOD = 2;
    protected static int CUSTOMER = 2;
    protected static int GOOD_ID = 2;
    protected static String GOOD_NAME = "Sample good. MUST NOT to appear in a database";
    //
    protected String ORIGINAL_GOOD_NAME = null;
    private Exception failedException;
    protected DbConnectionSettings settings;

    public DbClientTest() throws IOException {
        super();
    }

    @Before
    public void setupConnectionSettings() throws Exception {
        settings = new DbConnectionSettings();
        settings.setMaxConnections(2);
        settings.setMaxStatements(4);
        settings.setUrl("jdbc:oracle:thin:@asvr:1521:adb");
        settings.setSchema("eas");
        settings.setUser("eas");
        settings.setPassword("eas");
        settings.setName("Test connection");
    }

    @Test
    public void multiThreadedUpdatingRowsetTest() throws Exception {
        System.out.println("multiThreadedUpdatingRowsetTest");
        // initialize client in single thread mode as at the startup of the program
        final DbClient dbClient = new DatabasesClient((DbConnectionSettings) settings);
        Runnable clientRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {

                        Random rnd = new Random();
                        rnd.setSeed(System.currentTimeMillis());
                        int due = rnd.nextInt(10);
                        try {
                            Thread.sleep(due);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        SqlCompiledQuery query00 = new SqlCompiledQuery(dbClient, "select TABLE1.ID, TABLE1.F1, TABLE1.F2, TABLE1.F3 from TABLE1 order by TABLE1.F1");
                        Rowset rowset00 = query00.executeQuery();
                        rowset00.refresh();
                        SqlCompiledQuery query0 = new SqlCompiledQuery(dbClient, "select TABLE1.ID, TABLE1.F1, TABLE1.F2, TABLE1.F3 from TABLE1 order by TABLE1.F2");
                        Rowset rowset0 = query0.executeQuery();
                        rowset0.refresh();
                        SqlCompiledQuery query = new SqlCompiledQuery(dbClient, "select TABLE1.ID, TABLE1.F1, TABLE1.F2, TABLE1.F3 from TABLE1 order by TABLE1.ID");
                        query.setEntityId("TABLE1");
                        Rowset rowset = query.executeQuery();
                        rowset.getFields().get("ID").setPk(true);
                        assertNotNull(rowset.getFlowProvider());
                        assertTrue(rowset.getFlowProvider() instanceof DatabaseFlowProvider);
                        rowset.getFields().get(1).setPk(true);
                        assertTrue(rowset.size() > 0);
                        rowset.beforeFirst();
                        boolean rowMet = false;
                        int newValue = (new Random()).nextInt();
                        while (rowset.next()) {
                            Integer id = rowset.getInt(rowset.getFields().find("id"));
                            assertNotNull(id);
                            if (id == 2) {
                                rowMet = true;
                                rowset.updateObject(rowset.getFields().find("f3"), newValue);
                            }
                        }
                        assertTrue(rowMet);
                        dbClient.commit(null);
                        dbClient.dbTableChanged(null, "eAs", "test_fieldsAdding");
                    }

                } catch (Exception ex) {
                    boolean sleepIterrupted = (ex instanceof InterruptedException)
                            || ((ex instanceof SQLException) && ((SQLException) ex).getCause() instanceof InterruptedException);
                    if (!sleepIterrupted) {
                        failedException = ex;
                        Logger.getLogger(DbClientTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < TEST_THREADS_COUNT; i++) {
            Thread thread = new Thread(clientRunnable);
            threads.add(thread);
        }
        try {
            // start the set of threads as in server's thread pool

            for (int i = 0; i < threads.size(); i++) {
                threads.get(i).start();
            }
            Thread.sleep(1000 * 120);// 2 minutes
            int deadlessThreads = 0;
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                thread.interrupt();
            }
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                thread.join(1000 * 20);
                if (thread.isAlive()) {
                    deadlessThreads++;
                    System.out.println("Thread " + String.valueOf(i) + " won't to die.");
                }
            }
            if (failedException != null) {
                throw failedException;
            }
            if (deadlessThreads > 0) {
                throw new Exception(String.valueOf(deadlessThreads) + " threads are stay alive");
            }
        } finally {
            dbClient.shutdown();
        }
    }

    /**
     * Tests insertion in rowset with fields from main table and some fields
     * from other tables. The trick is that some fields are absent in target
     * table and data flow system must apply changes only to fields, that
     * present in main table.
     *
     * @throws Exception
     */
    @Test
    public void extraFieldsInsertTest() throws Exception {
        System.out.println("extraFieldsInsertTest");
        // initialize client in single thread mode as at the startup of the program
        Client lfclient = new DatabasesClient(settings);
        assertTrue(lfclient instanceof DbClient);
        DbClient lclient = (DbClient) lfclient;
        try {
            Parameters params = new Parameters();
            Rowset goodOrderRowset = makeGoodOrderRowsetWith1Record(lclient, params);
            goodOrderRowset.refresh(params);
            assertFalse(goodOrderRowset.isEmpty());
            goodOrderRowset.first();
            goodOrderRowset.delete();
            lclient.commit(null);
        } finally {
            lclient.shutdown();
        }
    }

    protected Rowset makeGoodOrderRowsetWith1Record(DbClient aClient, Parameters aParams) throws Exception {
        Parameter p = new Parameter("p1", "p1 desc", DataTypeInfo.DECIMAL);
        p.setValue(ORDER_ID);
        aParams.add(p);
        SqlCompiledQuery goodOrderQuery = new SqlCompiledQuery(aClient, null, "Select * From GOODORDER t2 Inner Join GOOD t1 on t2.GOOD = t1.GOOD_ID where t2.ORDER_ID = ?", aParams, (Fields) null, (Set<String>) null, (Set<String>) null);
        goodOrderQuery.setEntityId("GOODORDER");
        Rowset goodOrderRowset = goodOrderQuery.executeQuery();
        goodOrderRowset.deleteAll();
        aClient.commit(null);
        assertTrue(goodOrderRowset.getOriginal().isEmpty());
        assertTrue(aClient.getChangeLog(null, null).isEmpty());
        assertTrue(goodOrderRowset.isEmpty());
        Fields fields = goodOrderRowset.getFields();
        fields.get("ORDER_ID").setPk(true);
        goodOrderRowset.insert();
        goodOrderRowset.updateObject(fields.find("ORDER_ID"), ORDER_ID);
        goodOrderRowset.updateObject(fields.find("AMOUNT"), AMOUNT);
        goodOrderRowset.updateObject(fields.find("GOOD"), GOOD);
        goodOrderRowset.updateObject(fields.find("CUSTOMER"), CUSTOMER);
        goodOrderRowset.updateObject(fields.find("GOOD_ID"), GOOD_ID);
        goodOrderRowset.updateObject(fields.find("GOOD_NAME"), GOOD_NAME);
        aClient.commit(null);
        assertTrue(aClient.getChangeLog(null, null).isEmpty());
        goodOrderRowset.refresh(aParams);
        assertEquals(1, goodOrderRowset.size());
        goodOrderRowset.first();
        assertEquals(goodOrderRowset.getInt(fields.find("ORDER_ID")), Integer.valueOf(ORDER_ID));
        assertEquals(goodOrderRowset.getInt(fields.find("AMOUNT")), Integer.valueOf(AMOUNT));
        assertEquals(goodOrderRowset.getInt(fields.find("GOOD")), Integer.valueOf(GOOD));
        assertEquals(goodOrderRowset.getInt(fields.find("CUSTOMER")), Integer.valueOf(CUSTOMER));
        ORIGINAL_GOOD_NAME = goodOrderRowset.getString(fields.find("GOOD_NAME"));
        return goodOrderRowset;
    }

    /**
     * Tests updating in dataset with fields from main table and some fields
     * from other tables. The trick is that some fields are absent in target
     * table and data flow system must apply changes only to fields, that
     * present in main table.
     *
     * @throws Exception
     */
    @Test
    public void extraFieldsUpdateTest() throws Exception {
        System.out.println("extraFieldsUpdateTest");

        // initialize client in single thread mode as at the startup of the program
        Client lfclient = new DatabasesClient(settings);
        assertTrue(lfclient instanceof DbClient);
        DbClient lclient = (DbClient) lfclient;
        try {
            Parameters params = new Parameters();
            Rowset goodOrderRowset = makeGoodOrderRowsetWith1Record(lclient, params);
            goodOrderRowset.refresh(params);
            assertFalse(goodOrderRowset.isEmpty());
            goodOrderRowset.first();
            Fields fields = goodOrderRowset.getFields();
            goodOrderRowset.updateObject(fields.find("AMOUNT"), AMOUNT + 10);
            goodOrderRowset.updateObject(fields.find("GOOD_NAME"), GOOD_NAME);
            lclient.commit(null);
            goodOrderRowset.refresh(params);
            assertFalse(goodOrderRowset.isEmpty());
            goodOrderRowset.first();
            assertEquals(goodOrderRowset.getInt(fields.find("AMOUNT")), Integer.valueOf(AMOUNT + 10));
            assertEquals(goodOrderRowset.getString(fields.find("GOOD_NAME")), ORIGINAL_GOOD_NAME);
            goodOrderRowset.delete();
            lclient.commit(null);
            goodOrderRowset.refresh(params);
            assertTrue(goodOrderRowset.isEmpty());
        } finally {
            lclient.shutdown();
        }
    }
}
