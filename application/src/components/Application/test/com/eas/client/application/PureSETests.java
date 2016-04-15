/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PureSETests extends ScriptedTests {

    @BeforeClass
    public static void initPureSEApplication() throws Exception {
        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-datasource", "eas",
            "-dburl", "jdbc:oracle:thin:@asvr:1521:adb",
            "-dbuser", "eas",
            "-dbpassword", "eas",
            "-dbschema", "EAS",
            "-datasource", "easHR",
            "-dburl", "jdbc:oracle:thin:@asvr:1521:adb",
            "-dbuser", "hr",
            "-dbpassword", "hr",
            "-dbschema", "HR",
            "-default-datasource", "eas",
            "-url", "file:/C:/projects/PlatypusTests/",
            "-source-path", "app"
        }));
    }

    @Test
    public void select_stateless_test() throws InterruptedException {
        start("select_stateless_test", 10000L);
    }

    @Test
    public void EasHRValidatorTest() throws InterruptedException {
        start("EasHRValidatorTest", 10000L);
    }

}
