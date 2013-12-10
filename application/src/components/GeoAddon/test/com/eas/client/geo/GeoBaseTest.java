/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.eas.client.DatabasesClient;
import com.eas.client.DbClient;
import com.eas.client.settings.DbConnectionSettings;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class GeoBaseTest {

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

        dbClient = new DatabasesClient(settings);
    }

    @Test
    public void dummyTest() {
    }
}
