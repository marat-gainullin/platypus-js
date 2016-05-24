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
public class WithoutServerTest extends ScriptedTests {

    @BeforeClass
    public static void init() throws Exception {
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
    public void easHRValidatorTest() throws InterruptedException {
        start("EasHRValidatorTest", 10000L);
    }

    @Test
    public void append_test() throws InterruptedException {
        start("append_test", 20000L);
    }

    @Test
    public void ambigous_changes_semi_writable() throws InterruptedException {
        start("ambigous_changes_semi_writable", 10000L);
    }

    @Test
    public void ambigous_changes() throws InterruptedException {
        start("ambigous_changes", 10000L);
    }

    @Test
    public void extra_fields_insert_update() throws InterruptedException {
        start("extra_fields_insert_update", 10000L);
    }

    @Test
    public void modelModyfiedTest() throws InterruptedException {
        start("ModelModyfiedTest", 10000L);
    }

    @Test
    public void sqlUpdateTest() throws InterruptedException {
        start("SqlUpdateTest", 10000L);
    }

    @Test
    public void sqlExecuteUpdateTest() throws InterruptedException {
        start("SqlExecuteUpdateTest", 10000L);
    }

    @Test
    public void dependenciesTest() throws InterruptedException {
        start("DependenciesTest", 10000L);
    }

    @Test
    public void parallelRequireTest() throws InterruptedException {
        start("ParallelRequireTest", 10000L);
    }

    @Test
    public void create_Entity_Test() throws InterruptedException {
        start("Create_Entity_Test", 10000L);
    }

    @Test
    public void load_Entity_Test() throws InterruptedException {
        start("Load_Entity_Test", 10000L);
    }

    @Test
    public void modelAPI() throws InterruptedException {
        start("ModelAPI", 50000L);
    }

    @Test
    public void multiSourceTest() throws InterruptedException {
        start("MultiSourceTest", 10000L);
    }

    @Test
    public void multiSourceWithErrorTest() throws InterruptedException {
        start("MultiSourceWithErrorTest", 10000L);
    }

    @Test
    public void orm_Relations_Test() throws InterruptedException {
        start("ORM_Relations_Test", 10000L);
    }

    @Test
    public void orm_properties_names_calc() throws InterruptedException {
        start("ORM_properties_names_calc", 10000L);
    }

    @Test
    public void testReportCore() throws InterruptedException {
        start("TestReportCore", 10000L);
    }

    @Test
    public void iconLoadTest() throws InterruptedException {
        start("IconLoadTest", 10000L);
    }

    @Test
    public void resourceLoadTest() throws InterruptedException {
        start("ResourceLoadTest", 10000L);
    }

    @Test
    public void storedProcedureCallerTest() throws InterruptedException {
        start("StoredProcedureCallerTest", 10000L);
    }

    @Test
    public void invokeLaterDelayedTest() throws InterruptedException {
        start("InvokeLaterDelayedTest", 10000L);
    }

    @Test
    public void AMDSelfTest() throws InterruptedException {
        start("AMDSelfTest", 10000L);
    }

}
