/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import org.junit.BeforeClass;

/**
 *
 * @author mg
 */
public class WithSeServerTest extends WithServerTests {

    @BeforeClass
    public static void init() throws Exception {
        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-url", "platypus://localhost:8500",
            "-source-path", "app",
            "-user", "testuser2",
            "-password", "test"
        }));
    }

}
