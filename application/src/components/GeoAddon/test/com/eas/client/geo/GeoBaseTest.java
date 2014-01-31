/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.eas.client.DatabasesClient;
import com.eas.client.DbClient;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class GeoBaseTest {

    private static final String TEST_DATASOURCE_NAME = "testGeoAddOnDatasource";
    protected static DbClient dbClient;

    @BeforeClass
    public static void setUpClass() throws Exception {
        String url = "jdbc:oracle:thin:@asvr:1521/adb";
        String login = "eas";
        String passwd = "eas";
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.setUser(login);
        settings.setPassword(passwd);

        GeneralResourceProvider.getInstance().registerDatasource(TEST_DATASOURCE_NAME, settings);
        dbClient = new DatabasesClient(null, TEST_DATASOURCE_NAME, false);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        dbClient.shutdown();
        GeneralResourceProvider.getInstance().unregisterDatasource(TEST_DATASOURCE_NAME);
    }

    @Test
    public void dummyTest() {
    }
}
