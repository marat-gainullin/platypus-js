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
public class WithSeServerTest extends WithServerTests {

    public static final String NATIVE_SERVER_URL = "nativeSever.url";
    public static final String NO_NATIVE_SERVER_URL_MSG = NATIVE_SERVER_URL + " property is not specified.";
    public static final String NATIVE_SERVER_USER = "nativeSever.user";
    public static final String NATIVE_SERVER_PASSWORD = "nativeSever.password";
    public static final String NO_NATIVE_SERVER_USER = NATIVE_SERVER_USER + " property is not specified.";
    public static final String NO_NATIVE_SERVER_PASSWORD = NATIVE_SERVER_PASSWORD + " property is not specified.";
    

//    "-url", "platypus://localhost:8500",
//    "-source-path", "app",
//    "-user", "testuser2",
//    "-password", "test"
    @BeforeClass
    public static void init() throws Exception {

        String url = System.getProperty(NATIVE_SERVER_URL);
        if (url == null) {
            System.err.println(NO_NATIVE_SERVER_URL_MSG);
            System.exit(1);
        }
        String user = System.getProperty(NATIVE_SERVER_USER);
        if (user == null) {
            System.err.println(NO_NATIVE_SERVER_USER);
            System.exit(1);
        }
        String passwd = System.getProperty(NATIVE_SERVER_PASSWORD);
        if (passwd == null) {
            System.err.println(NO_NATIVE_SERVER_PASSWORD);
            System.exit(1);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            System.err.println(TestConstants.APP_SOURCE_PATH);
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
