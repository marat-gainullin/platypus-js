/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.client.ClientConstants;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.explorer.j2ee.WebAppConfigurator;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.util.FileUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation2;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Configures web application for Tomcat 7.
 *
 * @author vv
 */
public class TomcatWebAppConfigurator implements WebAppConfigurator {

    public final String MAIN_DATASOURCE_NAME = "jdbc/main"; //NOI18N 
    public final String CONTEXT_FILE_NAME = "context.xml"; //NOI18N
    public final String DATASOURCE_REALM_CLASS_NAME = "org.apache.catalina.realm.DataSourceRealm";
    protected final PlatypusProject project;

    public TomcatWebAppConfigurator(PlatypusProject aProject) {
        project = aProject;
    }

    @Override
    public void configure() {
        try {
            FileObject contexFileObject = getWebMobdule().getMetaInfDir().getFileObject(CONTEXT_FILE_NAME);
            if (contexFileObject == null) {
                contexFileObject = getWebMobdule().getMetaInfDir().createData(CONTEXT_FILE_NAME);
            }
            Context ctx = getContext();
            FileUtils.writeString(FileUtil.toFile(contexFileObject), XmlDom2String.transform(ctx.toDocument()), PlatypusUtils.COMMON_ENCODING_NAME);
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting configuring an application for Tomcat.");
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    protected PlatypusWebModule getWebMobdule() {
        return project.getLookup().lookup(PlatypusWebModule.class);
    }

    protected J2eeModuleImplementation2 getWebMobduleImpl() {
        return project.getLookup().lookup(J2eeModuleImplementation2.class);
    }

    private Context getContext() {
        Context ctx = null;
        try {
            ctx = new Context();
            ctx.setPath("/" + project.getSettings().getServerContext());//NOI18N
            ctx.addResource(getMainDbConnectionResource());
            if (project.getSettings().isSecurityRealmEnabled()) {
                ctx.setRealm(getRealm());
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return ctx;
    }

    private Resource getMainDbConnectionResource() throws Exception {
        DataSourceResource dataSourceResource = new DataSourceResource();
        dataSourceResource.setName(MAIN_DATASOURCE_NAME);
        dataSourceResource.setType(DataSourceResource.DATA_SOURCE_RESOURCE_TYPE_NAME);
        DbConnectionSettings dbSettings = project.getSettings().getAppSettings().getDbSettings();
        dataSourceResource.setUrl(dbSettings.getUrl());
        String dialect = GeneralResourceProvider.constructPropertiesByDbConnectionSettings(dbSettings).getProperty(ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME);
        if (dialect != null) {
            String driverClassName = DbConnectionSettings.readDrivers().get(dialect);
            dataSourceResource.setDriverClassName(driverClassName);
        } else {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unsupported JDBC driver or incorrect URL: {0}", dbSettings.getUrl());
        }
        dataSourceResource.setUsername(dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
        dataSourceResource.setPassword(dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));
        return dataSourceResource;
    }
    
    
    private Realm getRealm() {
        DataSourceRealm realm = new DataSourceRealm();
        realm.setClassName(DATASOURCE_REALM_CLASS_NAME);
        realm.setDataSourceName(MAIN_DATASOURCE_NAME);
        realm.setUserTable(ClientConstants.T_MTD_USERS);
        realm.setUserNameCol(ClientConstants.F_USR_NAME);
        realm.setUserCredCol(ClientConstants.F_USR_PASSWD);
        realm.setUserRoleTable(ClientConstants.T_MTD_GROUPS);
        realm.setRoleNameCol(ClientConstants.F_GROUP_NAME);
        realm.setLocalDataSource(true);
        realm.setDigest(DataSourceRealm.MD5_DIGEST);
        return realm;
    }
}
