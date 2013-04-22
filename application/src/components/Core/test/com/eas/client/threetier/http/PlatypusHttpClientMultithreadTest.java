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
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author kl
 */
public class PlatypusHttpClientMultithreadTest {

    private void executeAllClientTest(PlatypusHttpClientTest test) throws Exception {
        test.beforeTest();
        try {
             test.testAppElementChangedRequest();
             test.testAppElementRequestAndIsAppElementActual();
             test.testAppQueryRequest();
             test.testCommitRequest();
             test.testDbTableChangedRequest();
             test.testExecuteQueryRequest();
             test.testExecuteServerModuleMethodRequest();
             test.testHelloRequest();
             test.testIsUserInRole();
             test.testKeepAliveRequest();
             test.testStartAppElementRequest();
        } finally {
            test.afterTest();
        }
    }

    private Thread createThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        PlatypusHttpClientTest test = new PlatypusHttpClientTest();
                        executeAllClientTest(test);

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
    public void mainTest() throws InterruptedException {
        List<Thread> thList = new ArrayList<>();
        for (int i = 0; i < PlatypusHttpConnectionTest.TEST_THREAD_COUNT; i++) {
            Thread th = createThread();
            thList.add(th);
        }

        for (Thread thread : thList) {
            thread.start();
        }

        Thread.sleep(PlatypusHttpConnectionTest.TEST_THREADS_WORK * 1000);

        int demonCount = 0;

        for (Thread thread : thList) {
            thread.interrupt();
            thread.join(300);
            if (thread.isAlive()) {
                demonCount++;
            }
        }

        if (demonCount > 0) {
            Logger.getLogger(PlatypusHttpConnectionTest.class.getName()).log(Level.SEVERE, "{0} of {1} threads won't die", new String[]{String.valueOf(demonCount), String.valueOf(PlatypusHttpConnectionTest.TEST_THREAD_COUNT)});
        }
    }
}
