/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.eas.client.ClientFactory;
import com.eas.client.DbClient;
import com.eas.deploy.project.PlatypusSettings;
import com.eas.xml.dom.Source2XmlDom;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author vv
 */
public class BaseDeployer {
   
    public static final String PLATYPUS_SETTINGS_FILE = "platypus.xml";// NOI18N
    public static final String COMMON_ENCODING_NAME = "utf-8";// NOI18N
    
    protected static final String LOCKED_MSG = "Deployer is locked.";
    
    protected File projectDir;
    protected DbClient client;
    protected File projectSettingsFile;
    protected boolean silentMode;
    protected PlatypusSettings settings;
    protected PrintWriter out = new PrintWriter(System.out, true);
    protected PrintWriter err = new PrintWriter(System.err, true);
    protected boolean busy;
    
    public BaseDeployer() {
    }
    
    public BaseDeployer(File aProjectDir, DbClient aClient) {
        if (!aProjectDir.isDirectory()) {
            throw new IllegalArgumentException("Project path is not for directory: " + aProjectDir.getAbsolutePath()); // NOI18N 
        }
        projectDir = aProjectDir;
        client = aClient;
        projectSettingsFile = new File(projectDir, PLATYPUS_SETTINGS_FILE);
    }
    
    public BaseDeployer(String aProjectPath) {
        if (aProjectPath == null) {
            throw new NullPointerException("Project path is null"); // NOI18N
        }
        if (aProjectPath.isEmpty()) {
            throw new IllegalArgumentException("Project path is empty"); // NOI18N
        }
        projectDir = new File(aProjectPath);
        if (!projectDir.isDirectory()) {
            throw new IllegalArgumentException("Project path is not for directory: " + projectDir.getAbsolutePath()); // NOI18N 
        }
        projectSettingsFile = new File(projectDir, PLATYPUS_SETTINGS_FILE);
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
    
   protected void checkDbClient() throws DeployException {
        if (client == null) {
            client = createDbClient();
        }
        //Check database connection
        if (client == null) {
            throw new DeployException("Can't connect to database - check connection settings."); // NOI18N 
        }
    }

    private DbClient createDbClient() {
        try {
            checkSettings();
            return (DbClient) ClientFactory.getInstance(settings.getDbSettings());
        } catch (Exception ex) {
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected void checkSettings() throws Exception {
        if (settings == null) {
            //Read project configuration
            FileInputStream fstream = new FileInputStream(projectSettingsFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, COMMON_ENCODING_NAME));
            Document doc = Source2XmlDom.transform(br);
            settings = PlatypusSettings.valueOf(doc);
        }
    }
}
