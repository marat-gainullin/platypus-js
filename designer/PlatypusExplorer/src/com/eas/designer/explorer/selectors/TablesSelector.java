/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.selectors;

import com.eas.client.DbClient;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.selectors.TableNameSelector;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.designer.application.project.PlatypusProject;
import java.awt.Component;

/**
 *
 * @author mg
 */
public class TablesSelector implements TablesSelectorCallback {

    protected DbClient client;
    protected String title;
    protected Component parent;
    protected PlatypusProject project;
    protected boolean allowDatabaseChange;
    protected boolean allowSchemaChange;

    public TablesSelector(PlatypusProject aProject, String aTitle, Component aParent) {
        super();
        client = aProject.getClient();
        project = aProject;
        title = aTitle;
        parent = aParent;
    }

    public TablesSelector(PlatypusProject aProject, boolean aAllowDatabaseChange, boolean aAllowSchemaChange, String aTitle, Component aParent) {
        this(aProject, aTitle, aParent);
        allowDatabaseChange = aAllowDatabaseChange;
        allowSchemaChange = aAllowSchemaChange;
    }

    @Override
    public TableRef[] selectTableRef(TableRef oldRef) throws Exception {
        return TableNameSelector.selectTableName(project, oldRef, allowDatabaseChange, allowSchemaChange, parent, title);
    }

    public void setClient(DbClient aDbClient) {
        client = aDbClient;
    }
}
