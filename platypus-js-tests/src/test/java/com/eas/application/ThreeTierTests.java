package com.eas.application;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author mg
 */
public abstract class ThreeTierTests extends ScriptedTests {

    @Test
    public void select_stateless_test() throws InterruptedException {
        start("select_stateless_test", 20000L);
    }

    @Test
    public void easHRValidatorTest() throws InterruptedException {
        start("EasHRValidatorTest", 20000L);
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
        start("ambigous_changes", 20000L);
    }

    @Test
    public void extra_fields_insert_update() throws InterruptedException {
        start("extra_fields_insert_update", 20000L);
    }

    @Test
    public void modelModyfiedTestClient() throws InterruptedException {
        start("ModelModyfiedTestClient", 20000L);
    }

    @Test
    public void sqlUpdateTestClient() throws InterruptedException {
        start("SqlUpdateTestClient", 20000L);
    }

    @Test
    public void sqlExecuteUpdateTestClient() throws InterruptedException {
        start("SqlExecuteUpdateTestClient", 20000L);
    }

    @Test
    public void sqlUpdateTest() throws InterruptedException {
        start("SqlUpdateTest", 20000L);
    }

    @Test
    public void sqlEnqueueUpdateTest() throws InterruptedException {
        start("SqlEnqueueUpdateTest", 20000L);
    }

    @Test
    public void dependenciesTest() throws InterruptedException {
        start("DependenciesTest", 20000L);
    }

    @Test
    public void parallelRequireTest() throws InterruptedException {
        start("ParallelRequireTest", 20000L);
    }

    @Test
    public void createEntityTestClient() throws InterruptedException {
        start("CreateEntityTestClient", 20000L);
    }

    @Test
    public void loadEntityTestClient() throws InterruptedException {
        start("LoadEntityTestClient", 20000L);
    }

    @Test
    public void modelAPI() throws InterruptedException {
        start("ModelAPI", 50000L);
    }

    @Test
    public void multiSourceTest() throws InterruptedException {
        start("MultiSourceTest", 20000L);
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
    public void testReportClient() throws InterruptedException {
        start("TestReportClient", 20000L);
    }

    @Test
    public void iconLoadTest() throws InterruptedException {
        start("IconLoadTest", 20000L);
    }

    @Test
    public void resourceLoadTest() throws InterruptedException {
        start("ResourceLoadTest", 20000L);
    }

    @Test
    public void storedProcedureTestClient() throws InterruptedException {
        start("StoredProcedureTestClient", 10000L);
    }
    
    @Test
    public void secureServerModulesClient() throws InterruptedException {
        start("SecureServerModulesClient", 20000L);
    }
  
    @Test
    public void secureDataSourcesTest() throws InterruptedException {
        start("SecureDataSourcesTest", 20000L);
    }
    
    @Test
    public void syncServerModulesTest() throws InterruptedException {
        start("SyncServerModulesTest", 20000L);
    }

    @Test
    public void asyncServerModulesTest() throws InterruptedException {
        start("AsyncServerModulesTest", 20000L);
    }


    @Test
    public void accounterClient() throws InterruptedException {
        start("AccounterClient", 20000L);
    }

    @Test
    public void principalTestClient() throws InterruptedException {
        start("PrincipalTestClient", 20000L);
    }

    @Test
    public void invokeLaterDelayedClient() throws InterruptedException {
        start("InvokeLaterDelayedClient", 20000L);
    }

    @Test
    public void invokeLaterDelayedTest() throws InterruptedException {
        start("InvokeLaterDelayedTest", 20000L);
    }

    @Test
    public void errorsTestClient() throws InterruptedException {
        start("ErrorsTestClient", 20000L);
    }

    @Test
    public void AMDSelfTest() throws InterruptedException {
        start("AMDSelfTest", 20000L);
    }

    @Test
    public void LPCCallbacksTest() throws InterruptedException {
        start("LPCCallbacksTest", 20000L);
    }
    
    @Test
    public void loadWidgetsWithoutWindow() throws InterruptedException {
        start("load-widgets-without-window", 10000L);
    }
}
