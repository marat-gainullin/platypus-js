package com.eas.application;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    private static final int SCRIPT_TIMEOUT = 60;

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

    private static void perform(String aTestName) {
        Object requireError = browser.executeAsyncScript(""
                + "var complete = arguments[arguments.length - 1];\n"
                + "require(['" + aTestName + "'], function(aRequired){\n"
                + "window['"+aTestName+"'] = aRequired;\n" // Hack for AMD modules. Since our tests are global modules, we need to make required module global
                + "    complete();\n"
                + "},\n"
                + "function(aError){\n"
                + "    complete('' + aError);\n"
                + "})");
        assertNull("" + requireError, requireError);
        Object testError = browser.executeAsyncScript(""
                + "var complete = arguments[arguments.length - 1];\n"
                + "var testInstance = new window['" + aTestName + "']();\n"
                + "testInstance.execute(function(){\n"
                + "    complete();"
                + "},\n"
                + "function(aError){\n"
                + "    complete('' + aError);"
                + "});");
        assertNull("" + testError, testError);
    }

    @Test
    public void select_stateless_test() {
        perform("select_stateless_test");
    }

    @Test
    public void easHRValidatorTest() {
        perform("EasHRValidatorTest");
    }

    @Test
    public void append_test() {
        perform("append_test");
    }

    @Test
    public void ambigous_changes_semi_writable() {
        perform("ambigous_changes_semi_writable");
    }

    @Test
    public void ambigous_changes() {
        perform("ambigous_changes");
    }

    @Test
    public void extra_fields_insert_update() {
        perform("extra_fields_insert_update");
    }

    @Test
    public void modelModyfiedTestClient() {
        perform("ModelModyfiedTestClient");
    }

    @Test
    public void sqlUpdateTestClient() {
        perform("SqlUpdateTestClient");
    }

    @Test
    public void sqlExecuteUpdateTestClient() {
        perform("SqlExecuteUpdateTestClient");
    }

    @Test
    public void sqlUpdateTest() {
        perform("SqlUpdateTest");
    }

    @Test
    public void sqlEnqueueUpdateTest() {
        perform("SqlEnqueueUpdateTest");
    }

    @Test
    public void dependenciesTest() {
        perform("DependenciesTest");
    }

    @Test
    public void parallelRequireTest() {
        perform("ParallelRequireTest");
    }

    @Test
    public void createEntityTestClient() {
        perform("CreateEntityTestClient");
    }

    @Test
    public void loadEntityTestClient() {
        perform("LoadEntityTestClient");
    }

    @Test
    public void modelAPI() {
        perform("ModelAPI");
    }

    @Test
    public void multiSourceTest() {
        perform("MultiSourceTest");
    }

    @Test
    public void multiSourceWithErrorTest() {
        perform("MultiSourceWithErrorTest");
    }

    @Test
    public void orm_Relations_Test() {
        perform("ORM_Relations_Test");
    }

    @Test
    public void orm_properties_names_calc() {
        perform("ORM_properties_names_calc");
    }

    @Test
    public void testReportClient() {
        perform("TestReportClient");
    }

    @Test
    public void iconLoadTest() {
        perform("IconLoadTest");
    }

    @Test
    public void resourceLoadTest() {
        perform("ResourceLoadTest");
    }

    @Test
    public void storedProcedureTestClient() {
        perform("StoredProcedureTestClient");
    }

    @Test
    public void secureServerModulesClient() {
        perform("SecureServerModulesClient");
    }

    @Test
    public void secureDataSourcesTest() {
        perform("SecureDataSourcesTest");
    }

    @Test
    public void syncServerModulesTest() {
        perform("SyncServerModulesTest");
    }

    @Test
    public void asyncServerModulesTest() {
        perform("AsyncServerModulesTest");
    }

    @Test
    public void accounterClient() {
        perform("AccounterClient");
    }

    @Test
    public void principalTestClient() {
        perform("PrincipalTestClient");
    }

    @Test
    public void invokeLaterDelayedClient() {
        perform("InvokeLaterDelayedClient");
    }

    @Test
    public void invokeLaterDelayedTest() {
        perform("InvokeLaterDelayedTest");
    }

    @Test
    public void errorsTestClient() {
        perform("ErrorsTestClient");
    }

    @Test
    public void AMDSelfTest() {
        perform("AMDSelfTest");
    }

    @Test
    public void LPCCallbacksTest() {
        perform("LPCCallbacksTest");
    }
    
    @Test
    public void loadWidgetsWithoutWindow() {
        perform("load-widgets-without-window");
    }
}
