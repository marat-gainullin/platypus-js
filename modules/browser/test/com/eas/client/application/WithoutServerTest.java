/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.TestConstants;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class WithoutServerTest extends ScriptedTests {

//            "-datasource", "eas",
//            "-dburl", "jdbc:oracle:thin:@asvr:1521:adb",
//            "-dbuser", "eas",
//            "-dbpassword", "eas",
//            "-dbschema", "EAS",
//            "-datasource", "easHR",
//            "-dburl", "jdbc:oracle:thin:@asvr:1521:adb",
//            "-dbuser", "hr",
//            "-dbpassword", "hr",
//            "-dbschema", "HR",
//            "-default-datasource", "eas",
//            "-url", "file:/C:/projects/PlatypusTests/",
//              file:///home/jskonst/workspace/Altsoft/PlatypusTests/
//            "-source-path", "app"
    @BeforeClass
    public static void init() throws Exception {

        String source1 = System.getProperty(TestConstants.DATASOURCE_NAME_1);
        if (source1 == null) {
            System.err.println(TestConstants.DATASOURCE_NAME_1 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String url1 = System.getProperty(TestConstants.DATASOURCE_URL_1);
        if (url1 == null) {
            System.err.println(TestConstants.DATASOURCE_URL_1 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String user1 = System.getProperty(TestConstants.DATASOURCE_USER_1);
        if (user1 == null) {
            System.err.println(TestConstants.DATASOURCE_USER_1 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String passwd1 = System.getProperty(TestConstants.DATASOURCE_PASSWORD_1);
        if (passwd1 == null) {
            System.err.println(TestConstants.DATASOURCE_PASSWORD_1 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String schema1 = System.getProperty(TestConstants.DATASOURCE_SCHEMA_1);
        if (schema1 == null) {
            System.err.println(TestConstants.DATASOURCE_SCHEMA_1 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String source2 = System.getProperty(TestConstants.DATASOURCE_NAME_2);
        if (source2 == null) {
            System.err.println(TestConstants.DATASOURCE_NAME_2 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String url2 = System.getProperty(TestConstants.DATASOURCE_URL_2);
        if (url2 == null) {
            System.err.println(TestConstants.DATASOURCE_URL_2 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String user2 = System.getProperty(TestConstants.DATASOURCE_USER_2);
        if (user2 == null) {
            System.err.println(TestConstants.DATASOURCE_USER_2 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String passwd2 = System.getProperty(TestConstants.DATASOURCE_PASSWORD_2);
        if (passwd2 == null) {
            System.err.println(TestConstants.DATASOURCE_PASSWORD_2 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String schema2 = System.getProperty(TestConstants.DATASOURCE_SCHEMA_2);
        if (schema2 == null) {
            System.err.println(TestConstants.DATASOURCE_SCHEMA_2 + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String defaultSchema = System.getProperty(TestConstants.DATASOURCE_DEFAULT);
        if (defaultSchema == null) {
            System.err.println(TestConstants.DATASOURCE_DEFAULT + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String sourceURL = System.getProperty(TestConstants.TEST_SOURCE_URL);
        if (sourceURL == null) {
            System.err.println(TestConstants.TEST_SOURCE_URL + TestConstants.PROPERTY_ERROR);
            System.exit(1);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            System.err.println(TestConstants.NO_APP_SOURCE_PATH);
            System.exit(1);
        }

        PlatypusClientApplication.init(PlatypusClientApplication.Config.parse(new String[]{
            "-datasource", source1,
            "-dburl", url1,
            "-dbuser", user1,
            "-dbpassword", passwd1,
            "-dbschema", schema1,
            "-datasource", source2,
            "-dburl", url2,
            "-dbuser", user2,
            "-dbpassword", passwd2,
            "-dbschema", schema1,
            "-default-datasource", defaultSchema,
            "-url", (sourceURL),
            "-source-path", sourcePath

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
    public void sqlEnqueueUpdateTest() throws InterruptedException {
        start("SqlEnqueueUpdateTest", 20000L);
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
        start("ORM_Relations_Test", 20000L);
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
        start("ResourceLoadTest", 15000L);
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
