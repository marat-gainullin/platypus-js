/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.client.ClientConstants;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
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
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
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
public class TomcatWebAppManager {

    public static final String TOMCAT_SERVER_ID = "Tomcat"; //NOI18N
    public static final String TOMCAT_LIB_DIR = "lib"; //NOI18N
    public static final String CONTEXT_FILE_NAME = "context.xml"; //NOI18N
    public static final String DATASOURCE_REALM_CLASS_NAME = "org.apache.catalina.realm.DataSourceRealm"; //NOI18N
    protected final PlatypusProjectImpl project;
    private final ServerInstance tomcatInstance;

    public TomcatWebAppManager(PlatypusProjectImpl aProject, String serverInstanceId) throws InstanceRemovedException {
        project = aProject;
        tomcatInstance = Deployment.getDefault().getServerInstance(serverInstanceId);
    }

    public void deployJdbcDrivers() {
        try {
            List<File> driverCP = Arrays.asList(tomcatInstance.getJ2eePlatform().getClasspathEntries());
            FileObject tomcatLibDirectory = FileUtil.toFileObject(tomcatInstance.getJ2eePlatform().getServerHome()).getFileObject(TOMCAT_LIB_DIR);
            for (DataSourceResource res : getDataSources()) {
                if (!containsClass(driverCP, res.getDriverClassName())) {
                    deployJdbcDriver(tomcatLibDirectory, res.getDriverClassName());
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    public void configure() {
        try {
            FileObject contextXml = getWebMobdule().getMetaInfDir().getFileObject(CONTEXT_FILE_NAME);
            if (contextXml == null || project.getSettings().isAutoApplyWebSettings()) {
                if (contextXml == null) {
                    contextXml = getWebMobdule().getMetaInfDir().createData(CONTEXT_FILE_NAME);
                }
                Context ctx = getContext();
                FileUtils.writeString(FileUtil.toFile(contextXml), XmlDom2String.transform(ctx.toDocument()), PlatypusUtils.COMMON_ENCODING_NAME);
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting configuring an application for Tomcat.");
            }
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    private static void deployJdbcDriver(FileObject targetDirectory, String className) throws PlatformHomePathException, IOException {
        File jarFile = PlatypusPlatform.findThirdpartyJar(className);
        if (jarFile != null) {
            FileObject jdbcDriverFo = FileUtil.toFileObject(jarFile);
            Enumeration<? extends FileObject> jdbcDriversEnumeration = jdbcDriverFo.getParent().getChildren(false);
            while (jdbcDriversEnumeration.hasMoreElements()) {
                FileObject fo = jdbcDriversEnumeration.nextElement();
                if (PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())) {
                    fo.copy(targetDirectory, fo.getName(), fo.getExt());
                }
            }
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
                try (JarFile jf = new JarFile(file)) {
                    Enumeration entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) entries.nextElement();
                        if (classFilePath.equals(entry.getName())) {
                            return true;
                        }
                    }
                }
            } else if (new File(file, classFilePath).exists()) {
                return true;
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
            String path = getWebMobdule().getUrl();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            ctx.setPath(path); //NOI18N
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
        DatabaseConnection[] dataSources = ConnectionManager.getDefault().getConnections();
        for (DatabaseConnection connection : dataSources) {
            DataSourceResource dataSourceResource = new DataSourceResource();
            resources.add(dataSourceResource);
            dataSourceResource.setType(DataSourceResource.DATA_SOURCE_RESOURCE_TYPE_NAME);
            dataSourceResource.setName(connection.getDisplayName());
            dataSourceResource.setUrl(connection.getDatabaseURL());
            dataSourceResource.setDriverClassName(connection.getDriverClass());
            dataSourceResource.setUsername(connection.getUser());
            dataSourceResource.setPassword(connection.getPassword());
            dataSourceResource.setSchema(connection.getSchema());
        }
        return resources;
    }

    private Realm getRealm() {
        DataSourceRealm realm = new DataSourceRealm();
        realm.setClassName(DATASOURCE_REALM_CLASS_NAME);
        realm.setDataSourceName(project.getSettings().getDefaultDataSourceName());
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
