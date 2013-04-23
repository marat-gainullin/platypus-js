/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.settings.DbConnectionSettings;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ml
 */
public class ServerConfigParserTest {

    public final static String DB_URL = "jdbc:oracle:thin:@server:1521/workdb";
    public final static String DB_SCHEMA = "";
    public static final String DB_USER = "Chack Norris";
    public static final String DB_PASSWORD = "egyptskaya sila";
    public final static String DB_DIALECT = "oracle";
    public final static String[] rightConfigKeys = {
        "modules.m3.module-id",
        "modules.m1.load-on-startup",
        "modules.m1.acceptor",
        ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME,
        "modules.m1.accept-protocol",
        "modules.m1.module-id",
        "modules.m2.load-on-startup",
        "modules.m2.stateless",
        ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME,
        "modules.m2.acceptor",
        "modules.m2.accept-protocol",
        "modules.m1.stateless",
        "modules.m2.module-id",
        "modules.m3.load-on-startup",
        "modules.m3.stateless",
        ClientConstants.DB_CONNECTION_USER_PROP_NAME,
        "modules.m3.acceptor",
        "modules.m3.accept-protocol",
        ClientConstants.DB_CONNECTION_URL_PROP_NAME,
        ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME,};
    public final static String[] rightConfigValues = {
        "3333333333",
        "true",
        "false",
        DB_PASSWORD,
        "",
        "1111111111",
        "false",
        "false",
        DB_SCHEMA,
        "false",
        "",
        "false",
        "2222222222",
        "false",
        "false",
        DB_USER,
        "false",
        "",
        DB_URL,
        DB_DIALECT
    };
    /**
     * Ошибки в плохой конфигурации: Отсутствует параметр moduleId для модуля m3
     * Для модуля m2 отсутствует значение параметра moduleId
     */
    public final static String[] badConfigKeys = {
        "modules.m1.load-on-startup",
        "modules.m1.acceptor",
        ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME,
        "modules.m1.accept-protocol",
        "modules.m1.module-id",
        "modules.m2.load-on-startup",
        "modules.m2.stateless",
        ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME,
        "modules.m2.acceptor",
        "modules.m2.accept-protocol",
        "modules.m1.stateless",
        "modules.m2.module-id",
        "modules.m3.load-on-startup",
        "modules.m3.stateless",
        ClientConstants.DB_CONNECTION_USER_PROP_NAME,
        "modules.m3.acceptor",
        "modules.m3.accept-protocol",
        ClientConstants.DB_CONNECTION_URL_PROP_NAME,
        ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME
    };
    public final static String[] badConfigValues = {
        "true",
        "false",
        DB_PASSWORD,
        "",
        "1111111111",
        "false",
        "false",
        DB_SCHEMA,
        "false",
        "",
        "false",
        "",
        "false",
        "false",
        DB_USER,
        "false",
        "",
        DB_URL,
        DB_DIALECT
    };

    public ServerConfigParserTest() {
        super();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDbSettings method, of class ServletConfigParser.
     *
     * @throws Exception
     */
    @Test
    public void testGetDbSettings() throws Exception {
        System.out.println("getDbSettings");
        TestServerConfig rightConfig = new TestServerConfig(rightConfigKeys, rightConfigValues);
        ServerConfig parser = ServerConfig.parse(rightConfig);
        DbConnectionSettings dbSettings = parser.getDbSettings();
        assertEquals(DB_URL, dbSettings.getUrl());
        assertEquals(DB_SCHEMA, dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME));
        assertEquals(DB_USER, dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
        assertEquals(DB_PASSWORD, dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));
        assertEquals(DB_DIALECT, dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME));
    }

    /**
     * Test of getModuleConfigs method, of class ServletConfigParser.
     */
    @Test
    public void testGetModuleConfigs() throws Exception {
        System.out.println("getModuleConfigs");
        System.out.println("\tright configuration test");
        TestServerConfig rightConfig = new TestServerConfig(rightConfigKeys, rightConfigValues);
        ServerConfig rightConfigParser = ServerConfig.parse(rightConfig);
        Set<ModuleConfig> rmc = rightConfigParser.getModuleConfigs();
        // check configs amount
        assertEquals(3, rmc.size());
        Iterator<ModuleConfig> it = rmc.iterator();
        // check "module id" values
        while (it.hasNext()) {
            ModuleConfig mc = it.next();
            assertNotNull(mc.getModuleId());
        }
        System.out.println("\tbad configuration test");
        TestServerConfig badConfig = new TestServerConfig(badConfigKeys, badConfigValues);
        ServerConfig badConfigParser = ServerConfig.parse(badConfig);
        Set<ModuleConfig> bmc = badConfigParser.getModuleConfigs();
        // check configs amount
        assertEquals(1, bmc.size());
        // check "module id" values
        for (ModuleConfig mc : bmc) {
            assertNotNull(mc.getModuleId());
        }
    }

    private static class TestServerConfig implements ServletConfig {

        Map<String, String> config = new HashMap<>();

        public TestServerConfig(String[] keys, String[] values) {
            super();
            for (int li = 0; li < keys.length; li++) {
                config.put(keys[li], values[li]);
            }
        }

        @Override
        public String getServletName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ServletContext getServletContext() {
            return new ServletContext() {
                @Override
                public String getContextPath() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ServletContext getContext(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getMajorVersion() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getMinorVersion() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getEffectiveMajorVersion() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int getEffectiveMinorVersion() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getMimeType(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Set<String> getResourcePaths(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public URL getResource(String arg0) throws MalformedURLException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public InputStream getResourceAsStream(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public RequestDispatcher getRequestDispatcher(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public RequestDispatcher getNamedDispatcher(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Servlet getServlet(String arg0) throws ServletException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Enumeration<Servlet> getServlets() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Enumeration<String> getServletNames() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void log(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void log(Exception arg0, String arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void log(String arg0, Throwable arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getRealPath(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getServerInfo() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getInitParameter(String arg0) {
                    return TestServerConfig.this.getInitParameter(arg0);
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return TestServerConfig.this.getInitParameterNames();
                }

                @Override
                public boolean setInitParameter(String arg0, String arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Object getAttribute(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Enumeration<String> getAttributeNames() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setAttribute(String arg0, Object arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void removeAttribute(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public String getServletContextName() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Dynamic addServlet(String arg0, String arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Dynamic addServlet(String arg0, Servlet arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Dynamic addServlet(String arg0, Class<? extends Servlet> arg1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public <T extends Servlet> T createServlet(Class<T> arg0) throws ServletException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ServletRegistration getServletRegistration(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Map<String, ? extends ServletRegistration> getServletRegistrations() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FilterRegistration.Dynamic addFilter(String filterName, String className) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public <T extends Filter> T createFilter(Class<T> arg0) throws ServletException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public FilterRegistration getFilterRegistration(String arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public SessionCookieConfig getSessionCookieConfig() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addListener(String className) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public <T extends EventListener> void addListener(T arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void addListener(Class<? extends EventListener> listenerClass) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public <T extends EventListener> T createListener(Class<T> arg0) throws ServletException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public JspConfigDescriptor getJspConfigDescriptor() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public ClassLoader getClassLoader() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void declareRoles(String... arg0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        @Override
        public String getInitParameter(String name) {
            return config.get(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.<String>enumeration(config.keySet());
        }
    }
}
