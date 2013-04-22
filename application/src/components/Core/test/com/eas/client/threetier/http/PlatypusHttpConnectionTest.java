/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class PlatypusHttpConnectionTest {

    public static final int TEST_THREAD_COUNT = 8;//100;
    public static final int TEST_THREADS_WORK = 30; // seconds

    @Test
    public void testConnectDisconnect() throws Exception {
        PlatypusHttpConnection platConn = new PlatypusHttpConnection(PlatypusHttpTestConstants.HTTP_REQUEST_URL);
        platConn.connect();
        try {
            int respCode = platConn.getHttpConnection().getResponseCode();
            assertTrue(respCode >= 200 && respCode <= 505);
        } finally {
            platConn.disconnect();
        }
    }

    private Thread createThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        testConnectDisconnect();
                        Random rnd = new Random();
                        rnd.setSeed(System.currentTimeMillis());
                        int due = rnd.nextInt(10000);
                        try {
                            Thread.sleep(due);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusHttpConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }
        };
        Thread th = new Thread(runnable);
        th.setDaemon(true);
        return th;
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        List<Thread> thList = new ArrayList<>();
        for (int i = 0; i < TEST_THREAD_COUNT; i++) {
            Thread th = createThread();
            thList.add(th);
        }
        for (Thread thread : thList) {
            thread.start();
        }
        Thread.sleep(TEST_THREADS_WORK * 1000); // 2 minutes
        int demonCount = 0;
        for (Thread thread : thList) {
            thread.interrupt();
            thread.join(300);
            if (thread.isAlive()) {
                demonCount++;
            }
        }
        if (demonCount > 0) {
            Logger.getLogger(PlatypusHttpConnectionTest.class.getName()).log(Level.SEVERE, "{0} of {1} threads won't die", new String[]{String.valueOf(demonCount), String.valueOf(TEST_THREAD_COUNT)});
        }
    }
}
