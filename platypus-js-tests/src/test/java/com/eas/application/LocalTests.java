package com.eas.application;

import com.eas.client.TestConstants;
import com.eas.client.application.PlatypusClientApplication;
import java.io.IOException;
import java.nio.file.Paths;
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
        start("files-nio/path-test", 10000L);
    }

    @Test
    public void fileInputChannelTest() throws InterruptedException, IOException {
        java.nio.channels.AsynchronousFileChannel ch;
        ch.re
        start("files-nio/file-input-channel-test", 10000L);
    }

    @Test
    public void fileOutputChannelTest() throws InterruptedException {
        start("files-nio/file-output-channel-test", 10000L);
    }

}
