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
import com.eas.designer.explorer.j2ee.WebAppManager;
import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.util.FileUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerInstance;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation2;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Parameters;

/**
 * Configures web application for Tomcat 7.
 *
 * @author vv
 */
public class TomcatWebAppManager implements WebAppManager {

    public static final String TOMCAT_SERVER_ID = "Tomcat"; //NOI18N
    public static final String TOMCAT_LIB_DIR = "lib"; //NOI18N
    public final String CONTEXT_FILE_NAME = "context.xml"; //NOI18N
    public final String DATASOURCE_REALM_CLASS_NAME = "org.apache.catalina.realm.DataSourceRealm"; //NOI18N
    protected final PlatypusProject project;
    private ServerInstance si;

    public TomcatWebAppManager(PlatypusProject aProject, String serverInstanceID) throws InstanceRemovedException {
        project = aProject;
        si = Deployment.getDefault().getServerInstance(serverInstanceID);
    }

    @Override
    public void deployJdbcDrivers() {
        try {
            List<File> driverCP = Arrays.asList(si.getJ2eePlatform().getClasspathEntries());
            FileObject tomcatLibDirectory = FileUtil.toFileObject(si.getJ2eePlatform().getServerHome()).getFileObject(TOMCAT_LIB_DIR);
            for (DataSourceResource res : getDataSources()) {
                if (!containsClass(driverCP, res.getDriverClassName())) {
                    deployJdbcDriver(tomcatLibDirectory, res.getDriverClassName());
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
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

    private static void deployJdbcDriver(FileObject targetDirectory, String className) throws EmptyPlatformHomePathException, IOException {
        File jarFile = PlatypusPlatform.findThirdpartyJar(className);
        if (jarFile != null) {
            FileObject jarFo = FileUtil.toFileObject(jarFile);
            jarFo.copy(targetDirectory, jarFo.getName(), jarFo.getExt());
        } else {
            throw new FileNotFoundException("JDBC driver for " + className + " isn't found.");
        }
    }
    
    /**
     * Returns true if the specified classpath contains a class of the given
     * name, false otherwise.
     *
     * @param classpath consists of jar files and folders containing classes
     * @param className the name of the class
     *
     * @return true if the specified classpath contains a class of the given
     * name, false otherwise.
     *
     * @throws IOException if an I/O error has occurred
     *
     * @since 1.15
     */
    public static boolean containsClass(Collection<File> classpath, String className) throws IOException {
        Parameters.notNull("classpath", classpath); // NOI18N
        Parameters.notNull("driverClassName", className); // NOI18N
        String classFilePath = className.replace('.', '/') + ".class"; // NOI18N
        for (File file : classpath) {
            if (file.isFile()) {
                JarFile jf = new JarFile(file);
                try {
                    Enumeration entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) entries.nextElement();
                        if (classFilePath.equals(entry.getName())) {
                            return true;
                        }
                    }
                } finally {
                    jf.close();
                }
            } else {
                if (new File(file, classFilePath).exists()) {
                    return true;
                }
            }
        }
        return false;
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
            ctx.setPath("/" + project.getSettings().getServerContext()); //NOI18N
            for (Resource res : getDataSources()) {
                ctx.addResource(res);
            }
            ctx.setRealm(getRealm()); 
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return ctx;
    }

    private List<DataSourceResource> getDataSources() throws Exception {
        List<DataSourceResource> resources = new ArrayList<>();
        resources.add(getMainDbConnectionResource());
        return resources;
    }

    private DataSourceResource getMainDbConnectionResource() throws Exception {
        DataSourceResource dataSourceResource = new DataSourceResource();
        dataSourceResource.setName(PlatypusWebModule.MAIN_DATASOURCE_NAME);
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
        realm.setDataSourceName(PlatypusWebModule.MAIN_DATASOURCE_NAME);
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
