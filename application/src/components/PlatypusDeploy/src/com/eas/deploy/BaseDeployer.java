/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.eas.client.DatabasesClient;
import java.io.*;

/**
 *
 * @author vv
 */
public class BaseDeployer {

    public static final String COMMON_ENCODING_NAME = "utf-8";// NOI18N

    protected static final String LOCKED_MSG = "Deployer is locked.";

    protected DatabasesClient client;
    protected boolean silentMode;
    protected PrintWriter out = new PrintWriter(System.out, true);
    protected PrintWriter err = new PrintWriter(System.err, true);
    protected boolean busy;

    public BaseDeployer(DatabasesClient aClient) {
        client = aClient;
    }

    /**
     * Sets default messages output
     *
     * @param anOut Print destination
     */
    public void setOut(PrintWriter anOut) {
        out = anOut;
    }

    /**
     * Sets default error messages output
     *
     * @param anErr Print destination
     */
    public void setErr(PrintWriter anErr) {
        err = anErr;
    }

    public void setSilentMode(boolean silent) {
        silentMode = silent;
    }

    public boolean isSilentMode(boolean silent) {
        return silentMode;
    }

    public boolean isBusy() {
        return busy;
    }

    /*
    protected void checkSettings() throws Exception {
        if (settings == null) {
            //Read project configuration
            try (FileInputStream fstream = new FileInputStream(projectSettingsFile)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream, COMMON_ENCODING_NAME));
                Document doc = Source2XmlDom.transform(br);
                settings = PlatypusSettings.valueOf(doc);
            }
        }
    }
    */
}
