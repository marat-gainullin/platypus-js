/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.selectors;

import com.eas.client.DbClient;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.selectors.TableNameSelector;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import java.awt.Component;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public class TablesSelector implements TablesSelectorCallback {

    protected FileObject appRoot;
    protected DbClient client;
    protected String title;
    protected Component parent;
    protected boolean allowDatabaseChange;
    protected boolean allowSchemaChange;

    public TablesSelector(FileObject anAppRoot, DbClient aDbClient, String aTitle, Component aParent) {
        super();
        appRoot = anAppRoot;
        client = aDbClient;
        title = aTitle;
        parent = aParent;
    }

    public TablesSelector(FileObject anAppRoot, DbClient aDbClient, boolean aAllowDatabaseChange, boolean aAllowSchemaChange, String aTitle, Component aParent) {
        this(anAppRoot, aDbClient, aTitle, aParent);
        allowDatabaseChange = aAllowDatabaseChange;
        allowSchemaChange = aAllowSchemaChange;
    }

    @Override
    public TableRef[] selectTableRef(TableRef oldRef) throws Exception {
        return TableNameSelector.selectTableName(client, new ConnectionSelector(appRoot), oldRef, allowDatabaseChange, allowSchemaChange, parent, title);
    }

    public void setClient(DbClient aDbClient) {
        client = aDbClient;
    }
}
