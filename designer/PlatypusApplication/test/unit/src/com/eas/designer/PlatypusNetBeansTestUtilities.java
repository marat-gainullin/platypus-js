/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer;

import com.eas.client.ClientFactory;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.Client;
import com.eas.client.DbClient;
import java.util.Properties;

/**
 *
 * @author mg
 */
public class PlatypusNetBeansTestUtilities {

    public static DbClient initDevelopTestClientInstance() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr:1521:adb");
        Properties p = new Properties();
        p.put("user", "eas");
        p.put("schema", "eas");
        p.put("password", "eas");
        settings.setInfo(p);
        settings.setName("Develop test client two-tier connection settings");
        Client client = ClientFactory.getInstance(settings);
        assert client instanceof DbClient;
        return (DbClient)client;
    }
}
