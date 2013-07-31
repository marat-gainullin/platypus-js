/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.dbmigrations.DbMetadataMigrationDataObject;
import com.eas.designer.explorer.dbmigrations.DbMigrationsNode;
import com.eas.designer.explorer.dbmigrations.SqlMigrationDataObject;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Nodes list, identified by names as keys.
 *
 * @author Gala
 */
public class PlatypusProjectNodesList implements NodeList<String> {

    private static final String JAVASCRIPT_FILE_EXTENSION = "js";
    private static final String PACKAGE_PREFIX = "com/eas/designer/explorer/project/ui/";
    public static final ImageIcon sourceIcon = ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "elements.png", true);
    public static final ImageIcon migrationsIcon = ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "db.png", true);
    public static final DataFilter APPLICATION_TYPES_FILTER = new ApplicationTypesFilter();
    public static final DataFilter DB_MIGRATIONS_TYPES_FILTER = new DbMigrationsTypesFilter();
    protected List<String> keys = new ArrayList<>();
    protected List<Node> nodes = new ArrayList<>();
    protected PlatypusProjectImpl project;
    protected Set<ChangeListener> listeners = new HashSet<>();

    public PlatypusProjectNodesList(PlatypusProjectImpl aProject) throws Exception {
        super();
        project = aProject;
        keys.add(PlatypusUtils.ELEMENTS_SOURCES_GROUP);
        DataFolder appRootDataFolder = DataFolder.findFolder(project.getSrcRoot());
        nodes.add(new CategoryNode(project,
                appRootDataFolder,
                sourceIcon,
                sourceIcon,
                PlatypusUtils.ELEMENTS_SOURCES_GROUP,
                NbBundle.getMessage(PlatypusProjectImpl.class, PlatypusUtils.ELEMENTS_SOURCES_GROUP)));
        keys.add(PlatypusUtils.DB_MIGRATIONS_SOURCES_GROUP);
        DataFolder dbMigrationsDataFolder = DataFolder.findFolder(project.getDbMigrationsRoot());
        nodes.add(new DbMigrationsNode(project, dbMigrationsDataFolder.getNodeDelegate(),
                dbMigrationsDataFolder.createNodeChildren(DB_MIGRATIONS_TYPES_FILTER), migrationsIcon, migrationsIcon, PlatypusUtils.DB_MIGRATIONS_SOURCES_GROUP, NbBundle.getMessage(PlatypusProjectImpl.class, PlatypusUtils.DB_MIGRATIONS_SOURCES_GROUP)));

    }

    @Override
    public List<String> keys() {
        return keys;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        listeners.add(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        listeners.remove(cl);
    }

    @Override
    public Node node(String aName) {
        int nodeIndex = keys.indexOf(aName);
        if (nodeIndex != -1) {
            return nodes.get(nodeIndex);
        }
        return null;
    }

    @Override
    public void addNotify() {
    }

    @Override
    public void removeNotify() {
    }

    public static class ApplicationTypesFilter implements DataFilter {

        @Override
        public boolean acceptDataObject(DataObject d) {
            return d.getPrimaryFile().isFolder() || JAVASCRIPT_FILE_EXTENSION.equals(d.getPrimaryFile().getExt()) || d instanceof PlatypusDataObject;
        }
    }
    
    public static class DbMigrationsTypesFilter implements DataFilter {

        @Override
        public boolean acceptDataObject(DataObject d) {
            return d instanceof DbMetadataMigrationDataObject || d instanceof SqlMigrationDataObject;
        }
    }
}
