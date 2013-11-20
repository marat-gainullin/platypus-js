/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer;

import com.eas.client.ClientFactory;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.Client;
import com.eas.client.DbClient;

/**
 *
 * @author mg
 */
public class PlatypusNetBeansTestUtilities {

    public static DbClient initDevelopTestClientInstance() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr:1521:adb");
        settings.setUser("eas");
        settings.setPassword("eas");
        settings.setName("Develop test client two-tier connection settings");
        Client client = ClientFactory.getInstance(settings);
        assert client instanceof DbClient;
        return (DbClient) client;
    }
}
