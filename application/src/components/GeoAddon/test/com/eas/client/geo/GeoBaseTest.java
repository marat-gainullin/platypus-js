/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.geo;

import com.eas.client.ClientFactory;
import com.eas.client.DatabasesClient;
import com.eas.client.DbClient;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class GeoBaseTest {

    protected static DbClient dbClient;
    
    @BeforeClass
    public static void setUpClass() throws Exception
    {
        String url = "jdbc:oracle:thin:@asvr:1521/adb";
        String login = "eas";
        String passwd = "eas";
        String schema = "eas";
        EasSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.getInfo().setProperty("user", login);
        settings.getInfo().setProperty("password", passwd);
        settings.getInfo().setProperty("schema", schema);

        ClientFactory.setDefaultSettings(settings);
        dbClient = new DatabasesClient((DbConnectionSettings) settings);
    }

    @Test
    public void dummyTest()
    {
    }
}
