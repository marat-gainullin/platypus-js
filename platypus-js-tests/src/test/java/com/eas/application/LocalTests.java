package com.eas.application;

import com.eas.client.TestConstants;
import com.eas.client.application.PlatypusClientApplication;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mgainullin
 */
public class LocalTests extends ScriptedTests {

    @BeforeClass
    public static void init() throws Exception {
        String sourceURL = System.getProperty(TestConstants.TEST_SOURCE_URL);
        if (sourceURL == null) {
            throw new IllegalStateException(TestConstants.TEST_SOURCE_URL + TestConstants.PROPERTY_ERROR);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            throw new IllegalStateException(TestConstants.NO_APP_SOURCE_PATH);
        }
        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-url", sourceURL,
            "-source-path", sourcePath
        }));
    }

    @Test
    public void pathTest() throws InterruptedException {
        start("files-nio/path-test", 1000L);
    }

    @Test
    public void transferTest() throws InterruptedException, IOException {
        start("files-nio/transfer-test", 40000L);
    }

    @Test
    public void writeAppendReadAllTest() throws InterruptedException {
        start("files-nio/write-append-read-all-test", 10000L);
    }

    @Test
    public void writeWriteReadAllTest() throws InterruptedException {
        start("files-nio/write-write-read-all-test", 10000L);
    }

    @Test
    public void writeReadLineByLineTest() throws InterruptedException {
        start("files-nio/write-read-line-by-line-test", 10000L);
    }

    @Test
    public void writeReadLineByLineAbortTest() throws InterruptedException {
        start("files-nio/write-read-line-by-line-abort-test", 10000L);
    }

    @Test
    public void writeTuncateReadAllTest() throws InterruptedException {
        start("files-nio/write-truncate-read-all-test", 10000L);
    }

}
