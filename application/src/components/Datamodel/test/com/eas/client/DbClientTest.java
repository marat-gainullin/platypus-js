/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.eas.client.settings.DbConnectionSettings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    protected String ORIGINAL_GOOD_NAME;
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
        try (DatabasesClientWithResource resource = new DatabasesClientWithResource(settings)) {
            final List<Change> commonLog = new ArrayList<>();
            final Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, commonLog);

            final DatabasesClient dbClient = resource.getClient();
            Runnable clientRunnable = () -> {
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
                        Rowset rowset00 = query00.executeQuery(null, null);
                        rowset00.refresh(null, null);
                        SqlCompiledQuery query0 = new SqlCompiledQuery(dbClient, "select TABLE1.ID, TABLE1.F1, TABLE1.F2, TABLE1.F3 from TABLE1 order by TABLE1.F2");
                        Rowset rowset0 = query0.executeQuery(null, null);
                        rowset0.refresh(null, null);
                        SqlCompiledQuery query = new SqlCompiledQuery(dbClient, "select TABLE1.ID, TABLE1.F1, TABLE1.F2, TABLE1.F3 from TABLE1 order by TABLE1.ID");
                        query.setEntityId("TABLE1");
                        Rowset rowset = query.executeQuery(null, null);
                        rowset.setLog(commonLog);
                        rowset.getFields().get("ID").setPk(true);
                        assertNotNull(rowset.getFlowProvider());
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
                                Row updated = rowset.getCurrentRow();
                                updated.setLog(commonLog);
                                updated.setEntityName(rowset.getFlowProvider().getEntityId());
                                updated.setColumnObject(rowset.getFields().find("f3"), newValue);
                            }
                        }
                        assertTrue(rowMet);
                        dbClient.commit(changeLogs, null, null);
                        assertTrue(commonLog.isEmpty());
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
            };
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < TEST_THREADS_COUNT; i++) {
                Thread thread = new Thread(clientRunnable);
                threads.add(thread);
            }
            threads.stream().forEach((thread) -> {
                thread.start();
            });
            Thread.sleep(1000 * 120);// 2 minutes
            int deadlessThreads = 0;
            threads.stream().forEach((thread) -> {
                thread.interrupt();
            });
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
        }
    }
}
