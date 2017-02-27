package com.eas.application;

import com.eas.client.TestConstants;
import com.eas.client.application.PlatypusClientApplication;
import org.junit.BeforeClass;

/**
 *
 * @author mg
 */
public class ServletContainerTest extends ThreeTierTests {

    public static final String SERVLET_CONTAINER_URL = "servlet-container.url";
    public static final String SERVLET_CONTAINER_USER = "servlet-container.user";
    public static final String SERVLET_CONTAINER_PASSWORD = "servlet-container.password";
    public static final String NO_SERVLET_CONTAINER_URL_MSG = SERVLET_CONTAINER_URL + " property is not specified.";
    public static final String NO_SERVLET_CONTAINER_USER = SERVLET_CONTAINER_USER + " property is not specified.";
    public static final String NO_SERVLET_CONTAINER_PASSWORD = SERVLET_CONTAINER_PASSWORD + " property is not specified.";

    @BeforeClass
    public static void init() throws Exception {
        String url = System.getProperty(SERVLET_CONTAINER_URL);
        if (url == null) {
            throw new IllegalStateException(NO_SERVLET_CONTAINER_URL_MSG);
        }
        String user = System.getProperty(SERVLET_CONTAINER_USER);
        if (user == null) {
            throw new IllegalStateException(NO_SERVLET_CONTAINER_USER);
        }
        String passwd = System.getProperty(SERVLET_CONTAINER_PASSWORD);
        if (passwd == null) {
            throw new IllegalStateException(NO_SERVLET_CONTAINER_PASSWORD);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            throw new IllegalStateException(TestConstants.NO_APP_SOURCE_PATH);
        }

        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-url", url,
            "-source-path", sourcePath,
            "-user", user,
            "-password", passwd
        }));
    }

}
