package com.eas.application;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mg
 */
public class BrowserTests {

    private static final int SCRIPT_TIMEOUT = 20;

    private static RemoteWebDriver browser;

    @BeforeClass
    public static void init() {
        String url = System.getProperty(ServletContainerTest.SERVLET_CONTAINER_URL);
        if (url == null) {
            throw new IllegalStateException(ServletContainerTest.NO_SERVLET_CONTAINER_URL_MSG);
        } else {
            url += "/tests-start.html";
        }
        String user = System.getProperty(ServletContainerTest.SERVLET_CONTAINER_USER);
        if (user == null) {
            throw new IllegalStateException(ServletContainerTest.NO_SERVLET_CONTAINER_USER);
        }
        String passwd = System.getProperty(ServletContainerTest.SERVLET_CONTAINER_PASSWORD);
        if (passwd == null) {
            throw new IllegalStateException(ServletContainerTest.NO_SERVLET_CONTAINER_PASSWORD);
        }

        ChromeDriverManager.getInstance().setup();
        browser = new ChromeDriver();
        browser.manage().timeouts().setScriptTimeout(SCRIPT_TIMEOUT, TimeUnit.SECONDS);

        browser.get(url);
        browser.findElement(By.id("name")).sendKeys(user);
        browser.findElement(By.id("password")).sendKeys(passwd);
        browser.findElement(By.id("login")).submit();
        WebDriverWait wait = new WebDriverWait(browser, SCRIPT_TIMEOUT);
        wait.until((WebDriver driver) -> {
            Object requireType = browser.executeScript("return typeof require");
            return "function".equals(requireType);
        });
        Object platypusJsLoaded = browser.executeAsyncScript(""
                + "var complete = arguments[arguments.length - 1];\n"
                + "require(['facade', 'logger'], function(F, Logger){\n"
                + "    var global = window;\n"
                + "    F.cacheBust(true);\n"
                + "    F.export(global);\n"
                + "    complete();\n"
                + "}, function(aError){\n"
                + "    complete('' + aError);\n"
                + "})");
        assertNull(platypusJsLoaded);
    }

    @AfterClass
    public static void tearDown() {
        browser.close();
        browser.quit();
    }

    private static void start(String aTestName) {
        Object requireError = browser.executeAsyncScript(""
                + "var complete = arguments[arguments.length - 1];\n"
                + "require(['" + aTestName + "'], function(){\n"
                + "    complete();\n"
                + "},\n"
                + "function(aError){\n"
                + "    complete('' + aError);\n"
                + "})");
        assertNull(requireError);
        Object testError = browser.executeAsyncScript(""
                + "var complete = arguments[arguments.length - 1];\n"
                + "var testInstance = new " + aTestName + "();\n"
                + "testInstance.execute(function(){\n"
                + "    complete();"
                + "},\n"
                + "function(aError){\n"
                + "    complete('' + aError);"
                + "});");
        assertNull(testError);
    }

    @Test
    public void select_stateless_test() {
        start("select_stateless_test");
    }

    @Test
    public void easHRValidatorTest() {
        start("EasHRValidatorTest");
    }

    @Test
    public void append_test() {
        start("append_test");
    }

    @Test
    public void ambigous_changes_semi_writable() {
        start("ambigous_changes_semi_writable");
    }

    @Test
    public void ambigous_changes() {
        start("ambigous_changes");
    }

    @Test
    public void extra_fields_insert_update() {
        start("extra_fields_insert_update");
    }

    @Test
    public void modelModyfiedTestClient() {
        start("ModelModyfiedTestClient");
    }

    @Test
    public void sqlUpdateTestClient() {
        start("SqlUpdateTestClient");
    }

    @Test
    public void sqlExecuteUpdateTestClient() {
        start("SqlExecuteUpdateTestClient");
    }

    @Test
    public void sqlUpdateTest() {
        start("SqlUpdateTest");
    }

    @Test
    public void sqlEnqueueUpdateTest() {
        start("SqlEnqueueUpdateTest");
    }

    @Test
    public void dependenciesTest() {
        start("DependenciesTest");
    }

    @Test
    public void parallelRequireTest() {
        start("ParallelRequireTest");
    }

    @Test
    public void createEntityTestClient() {
        start("CreateEntityTestClient");
    }

    @Test
    public void loadEntityTestClient() {
        start("LoadEntityTestClient");
    }

    @Test
    public void modelAPI() {
        start("ModelAPI");
    }

    @Test
    public void multiSourceTest() {
        start("MultiSourceTest");
    }

    @Test
    public void multiSourceWithErrorTest() {
        start("MultiSourceWithErrorTest");
    }

    @Test
    public void orm_Relations_Test() {
        start("ORM_Relations_Test");
    }

    @Test
    public void orm_properties_names_calc() {
        start("ORM_properties_names_calc");
    }

    @Test
    public void testReportClient() {
        start("TestReportClient");
    }

    @Test
    public void iconLoadTest() {
        start("IconLoadTest");
    }

    @Test
    public void resourceLoadTest() {
        start("ResourceLoadTest");
    }

    @Test
    public void storedProcedureTestClient() {
        start("StoredProcedureTestClient");
    }

    @Test
    public void secureServerModulesClient() {
        start("SecureServerModulesClient");
    }

    @Test
    public void secureDataSourcesTest() {
        start("SecureDataSourcesTest");
    }

    @Test
    public void syncServerModulesTest() {
        start("SyncServerModulesTest");
    }

    @Test
    public void asyncServerModulesTest() {
        start("AsyncServerModulesTest");
    }

    @Test
    public void accounterClient() {
        start("AccounterClient");
    }

    @Test
    public void principalTestClient() {
        start("PrincipalTestClient");
    }

    @Test
    public void invokeLaterDelayedClient() {
        start("InvokeLaterDelayedClient");
    }

    @Test
    public void invokeLaterDelayedTest() {
        start("InvokeLaterDelayedTest");
    }

    @Test
    public void errorsTestClient() {
        start("ErrorsTestClient");
    }

    @Test
    public void AMDSelfTest() {
        start("AMDSelfTest");
    }

    @Test
    public void LPCCallbacksTest() {
        start("LPCCallbacksTest");
    }
}
