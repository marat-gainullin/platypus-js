/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import com.eas.client.AppCache;
import com.eas.client.DbClient;
import com.eas.deploy.Deployer;
import com.eas.util.ListenerRegistration;
import java.awt.Component;
import java.util.concurrent.ExecutionException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.search.SubTreeSearchOptions;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;

/**
 *
 * @author vv
 */
public interface PlatypusProject extends Project {

    public interface ClientChangeListener {

        public void connected(String aDatasourceName);

        public void disconnected(String aDatasourceName);

        public void defaultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName);
    }

    boolean isDbConnected(String aDatasourceId);

    DbClient getClient();

    AppCache getAppCache() throws Exception;

    void startConnecting2db(String aDatasourceId);

    void disconnectFormDb(String aDatasourceId) throws InterruptedException, ExecutionException;

    Deployer getDeployer();

    InputOutput getOutputWindowIO();

    Component generateDbPlaceholder(String aDatasourceId) throws Exception;

    Component generateDbValidatePlaceholder() throws Exception;

    ListenerRegistration addClientChangeListener(final ClientChangeListener onChange);

    FileObject getLocalProjectFile();

    PlatypusProjectSettings getSettings();

    void save() throws Exception;

    boolean isAutoDeployEnabled();

    void setAutoDeployEnabled(boolean isEnabled);

    String getDisplayName();

    ProjectState getState();

    FileObject getSrcRoot() throws Exception;

    FileObject getDbMigrationsRoot() throws Exception;

    PlatypusProjectInformation getProjectInfo();

    SubTreeSearchOptions getSubTreeSearchOptions();

    RequestProcessor getRequestProcessor();
}
