package com.eas.application;

import com.eas.client.TestConstants;
import com.eas.client.application.PlatypusClientApplication;
import org.junit.BeforeClass;

/**
 *
 * @author mg
 */
public class PlatypusServerTest extends ThreeTierTests {

    public static final String PLATYPUS_SERVER_URL = "platypus-server.url";
    public static final String PLATYPUS_SERVER_USER = "platypus-server.user";
    public static final String PLATYPUS_SERVER_PASSWORD = "platypus-server.password";
    public static final String NO_PLATYPUS_SERVER_URL_MSG = PLATYPUS_SERVER_URL + " property is not specified.";
    public static final String NO_PLATYPUS_SERVER_USER = PLATYPUS_SERVER_USER + " property is not specified.";
    public static final String NO_PLATYPUS_SERVER_PASSWORD = PLATYPUS_SERVER_PASSWORD + " property is not specified.";
    
    @BeforeClass
    public static void init() throws Exception {

        String url = System.getProperty(PLATYPUS_SERVER_URL);
        if (url == null) {
            throw new IllegalStateException(NO_PLATYPUS_SERVER_URL_MSG);
        }
        String user = System.getProperty(PLATYPUS_SERVER_USER);
        if (user == null) {
            throw new IllegalStateException(NO_PLATYPUS_SERVER_USER);
        }
        String passwd = System.getProperty(PLATYPUS_SERVER_PASSWORD);
        if (passwd == null) {
            throw new IllegalStateException(NO_PLATYPUS_SERVER_PASSWORD);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            throw new IllegalStateException(TestConstants.APP_SOURCE_PATH);
        }

        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-url", url,
            "-source-path", sourcePath,
            "-user", user,
            "-password", passwd
        }));
    }
}
