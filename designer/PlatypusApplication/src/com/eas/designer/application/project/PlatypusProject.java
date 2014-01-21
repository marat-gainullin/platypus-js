/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.project;

import com.eas.client.AppCache;
import com.eas.client.DbClient;
import com.eas.deploy.DbMigrator;
import com.eas.deploy.Deployer;
import com.eas.designer.application.HandlerRegistration;
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
    
    boolean isDbConnected();

    DbClient getClient();

    AppCache getAppCache() throws Exception;

    void startConnecting2db();
    
    void disconnectFormDb() throws InterruptedException, ExecutionException;
    
    Deployer getDeployer();
    
    DbMigrator getDbMigrator();
    
    InputOutput getOutputWindowIO();
    
    Component generateDbPlaceholder() throws Exception;
    
    Component generateDbValidatePlaceholder() throws Exception;
    
    HandlerRegistration addClientChangeListener(final Runnable onChange);
    
    FileObject getLocalProjectFile();
    
    PlatypusProjectSettings getSettings();
    
    void save() throws Exception;
    
    boolean isAutoDeployEnabled();
    
    void setAutoDeployEnabled(boolean isEnabled);
    
    String getDisplayName();
    
    ProjectState getState();
    
    FileObject getSrcRoot() throws Exception;
    
    PlatypusProjectInformation getProjectInfo();
    
    SubTreeSearchOptions getSubTreeSearchOptions();
    
    RequestProcessor getRequestProcessor();
}
