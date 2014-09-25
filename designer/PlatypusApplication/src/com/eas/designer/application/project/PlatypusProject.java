/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import com.eas.client.DatabasesClient;
import com.eas.client.queries.LocalQueriesProxy;
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

    public interface QueriesChangeListener {

        public void changed();
    }

    DatabasesClient getBasesProxy();

    LocalQueriesProxy getQueries();

    boolean isDbConnected(String aDatasourceId);

    void startConnecting2db(String aDatasourceId);

    void disconnectFormDb(String aDatasourceId) throws InterruptedException, ExecutionException;

    InputOutput getOutputWindowIO();

    Component generateDbPlaceholder(String aDatasourceId) throws Exception;

    Component generateDbValidatePlaceholder() throws Exception;

    ListenerRegistration addClientChangeListener(final ClientChangeListener onChange);
    
    ListenerRegistration addQueriesChangeListener(final QueriesChangeListener onChange);

    public void fireQueriesChanged();
        
    public void fireQueryChanged(String aQueryName);
    
    PlatypusProjectSettings getSettings();

    void save() throws Exception;

    boolean isAutoDeployEnabled();

    void setAutoDeployEnabled(boolean isEnabled);

    String getDisplayName();

    ProjectState getState();

    FileObject getSrcRoot();

    PlatypusProjectInformation getProjectInfo();

    SubTreeSearchOptions getSubTreeSearchOptions();

    RequestProcessor getRequestProcessor();
}
