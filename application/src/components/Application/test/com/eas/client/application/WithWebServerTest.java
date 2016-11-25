/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.TestConstants;
import org.junit.BeforeClass;

/**
 *
 * @author mg
 */
public class WithWebServerTest extends WithServerTests {

    public static final String SERVLET_CONTAINER_URL = "servlet-container.url";
    public static final String NO_SERVLET_CONTAINER_URL_MSG = SERVLET_CONTAINER_URL + " property is not specified.";
    public static final String SERVLET_CONTAINER_USER = "servlet-container.user";
    public static final String SERVLET_CONTAINER_PASSWORD = "servlet-container.password";
    public static final String NO_SERVLET_CONTAINER_USER = SERVLET_CONTAINER_USER + " property is not specified.";
    public static final String NO_SERVLET_CONTAINER_PASSWORD = SERVLET_CONTAINER_PASSWORD + " property is not specified.";


//    "-url", "http://localhost:8080/p-tests",
//    "-source-path", "app",
//    "-user", "testuser2",
//    "-password", "test"
    @BeforeClass
    public static void init() throws Exception {

        String url = System.getProperty(SERVLET_CONTAINER_URL);
        if (url == null) {
            System.err.println(NO_SERVLET_CONTAINER_URL_MSG);
            System.exit(1);
        }
        String user = System.getProperty(SERVLET_CONTAINER_USER);
        if (user == null) {
            System.err.println(NO_SERVLET_CONTAINER_USER);
            System.exit(1);
        }
        String passwd = System.getProperty(SERVLET_CONTAINER_PASSWORD);
        if (passwd == null) {
            System.err.println(NO_SERVLET_CONTAINER_PASSWORD);
            System.exit(1);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            System.err.println(TestConstants.NO_APP_SOURCE_PATH);
            System.exit(1);
        }

        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-url", url,
            "-source-path", sourcePath,
            "-user", user,
            "-password", passwd
        }));
    }

}
