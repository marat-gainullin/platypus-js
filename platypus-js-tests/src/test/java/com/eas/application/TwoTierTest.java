package com.eas.application;

import com.eas.client.TestConstants;
import com.eas.client.application.PlatypusClientApplication;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class TwoTierTest extends ScriptedTests {

    @BeforeClass
    public static void init() throws Exception {
        String source1 = System.getProperty(TestConstants.DATASOURCE_NAME_1);
        if (source1 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_NAME_1 + TestConstants.PROPERTY_ERROR);
        }
        String url1 = System.getProperty(TestConstants.DATASOURCE_URL_1);
        if (url1 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_URL_1 + TestConstants.PROPERTY_ERROR);
        }
        String user1 = System.getProperty(TestConstants.DATASOURCE_USER_1);
        if (user1 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_USER_1 + TestConstants.PROPERTY_ERROR);
        }
        String passwd1 = System.getProperty(TestConstants.DATASOURCE_PASSWORD_1);
        if (passwd1 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_PASSWORD_1 + TestConstants.PROPERTY_ERROR);
        }
        String schema1 = System.getProperty(TestConstants.DATASOURCE_SCHEMA_1);
        if (schema1 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_SCHEMA_1 + TestConstants.PROPERTY_ERROR);
        }
        String source2 = System.getProperty(TestConstants.DATASOURCE_NAME_2);
        if (source2 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_NAME_2 + TestConstants.PROPERTY_ERROR);
        }
        String url2 = System.getProperty(TestConstants.DATASOURCE_URL_2);
        if (url2 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_URL_2 + TestConstants.PROPERTY_ERROR);
        }
        String user2 = System.getProperty(TestConstants.DATASOURCE_USER_2);
        if (user2 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_USER_2 + TestConstants.PROPERTY_ERROR);
        }
        String passwd2 = System.getProperty(TestConstants.DATASOURCE_PASSWORD_2);
        if (passwd2 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_PASSWORD_2 + TestConstants.PROPERTY_ERROR);
        }
        String schema2 = System.getProperty(TestConstants.DATASOURCE_SCHEMA_2);
        if (schema2 == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_SCHEMA_2 + TestConstants.PROPERTY_ERROR);
        }
        String defaultSchema = System.getProperty(TestConstants.DATASOURCE_DEFAULT);
        if (defaultSchema == null) {
            throw new IllegalStateException(TestConstants.DATASOURCE_DEFAULT + TestConstants.PROPERTY_ERROR);
        }
        String sourceURL = System.getProperty(TestConstants.TEST_SOURCE_URL);
        if (sourceURL == null) {
            throw new IllegalStateException(TestConstants.TEST_SOURCE_URL + TestConstants.PROPERTY_ERROR);
        }
        String sourcePath = System.getProperty(TestConstants.APP_SOURCE_PATH);
        if (sourcePath == null) {
            throw new IllegalStateException(TestConstants.NO_APP_SOURCE_PATH);
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
            "-url", sourceURL,
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
        start("append_test", 40000L);
    }

    @Test
    public void ambigous_changes_semi_writable() throws InterruptedException {
        start("ambigous_changes_semi_writable", 20000L);
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
        start("Load_Entity_Test", 20000L);
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
        start("MultiSourceWithErrorTest", 20000L);
    }

    @Test
    public void orm_Relations_Test() throws InterruptedException {
        start("ORM_Relations_Test", 20000L);
    }

    @Test
    public void orm_properties_names_calc() throws InterruptedException {
        start("ORM_properties_names_calc", 20000L);
    }

    @Test
    public void testReportCore() throws InterruptedException {
        start("TestReportCore", 20000L);
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

    @Test
    public void loadWidgetsWithoutWindow() throws InterruptedException {
        start("load-widgets-without-window", 10000L);
    }
}
