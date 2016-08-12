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

    public static final String WEB_SERVER_URL = "webSever.url";
    public static final String NO_WEB_SERVER_URL_MSG = WEB_SERVER_URL + " property is not specified.";
    public static final String WEB_SERVER_USER = "webSever.user";
    public static final String WEB_SERVER_PASSWORD = "webSever.password";
    public static final String NO_WEB_SERVER_USER = WEB_SERVER_USER + " property is not specified.";
    public static final String NO_WEB_SERVER_PASSWORD = WEB_SERVER_PASSWORD + " property is not specified.";


//    "-url", "http://localhost:8080/p-tests",
//    "-source-path", "app",
//    "-user", "testuser2",
//    "-password", "test"
    @BeforeClass
    public static void init() throws Exception {

        String url = System.getProperty(WEB_SERVER_URL);
        if (url == null) {
            System.err.println(NO_WEB_SERVER_URL_MSG);
            System.exit(1);
        }
        String user = System.getProperty(WEB_SERVER_USER);
        if (user == null) {
            System.err.println(NO_WEB_SERVER_USER);
            System.exit(1);
        }
        String passwd = System.getProperty(WEB_SERVER_PASSWORD);
        if (passwd == null) {
            System.err.println(NO_WEB_SERVER_PASSWORD);
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
