/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.dbmigrations;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author vv
 */
public class DbMigrationsNode extends FilterNode {

    protected DataFolder dataFolder;
    protected FileObject folder;
    protected Image icon;
    protected Image openIcon;
    protected String name;
    protected String displayName;
    protected PlatypusProjectImpl project;
    protected static final Action ADD_DB_METADATA_MIGRATION_ACTION = new AddDdMetadataMigrationAction();
    protected static final Action ADD_SQL_MIGRATION_ACTION = new AddSqlMigrationAction();
    protected static final Action APPLY_MIGRATIONS_ACTION = new ApplyMigrationsAction();
    protected static final Action CLEANUP_MIGRATIONS_ACTION = new CleanupMigrationsAction();
    protected static final Action SET_DB_VERSION_ACTION = new SetDbVersionAction();
    
    public DbMigrationsNode(PlatypusProjectImpl aProject, Node aDelegate, org.openide.nodes.Children aChildren, Icon aIcon, Icon aOpenIcon, String aName, String aDisplayName) {
        super(aDelegate, aChildren, Lookups.singleton(aProject));
        project = aProject;
        name = aName;
        icon = ImageUtilities.icon2Image(aIcon);
        openIcon = ImageUtilities.icon2Image(aOpenIcon);
        displayName = aDisplayName;
    }

    public PlatypusProjectImpl getProject() {
        return project;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        return super.getDropType(t, action, index);
    }

    @Override
    public Action[] getActions(boolean context) {
        return getDbMigrationsActions();
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public Image getIcon(int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return openIcon;
    }

    private Action[] getDbMigrationsActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(ADD_DB_METADATA_MIGRATION_ACTION);
        actions.add(ADD_SQL_MIGRATION_ACTION);
        actions.add(CLEANUP_MIGRATIONS_ACTION);
        actions.add(null);
        actions.add(APPLY_MIGRATIONS_ACTION);
        actions.add(SET_DB_VERSION_ACTION);
        return actions.toArray(new Action[actions.size()]);
    }
}
