/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Gala
 */
public class CategoryNode extends FilterNode {

    public static final DataFilter PROJECT_FILES_DATA_FILTER = new ProjectFilesDataFilter();
    protected DataFolder dataFolder;
    protected FileObject folder;
    protected Image icon;
    protected Image openIcon;
    protected String name;
    protected String displayName;
    protected PlatypusProjectImpl project;

    public CategoryNode(PlatypusProjectImpl aProject, DataFolder aDataFolder, Icon aIcon, Icon aOpenIcon, String aName, String aDisplayName) {
        super(aDataFolder.getNodeDelegate(),
                aDataFolder.createNodeChildren(PROJECT_FILES_DATA_FILTER),
                new ProxyLookup(aDataFolder.getLookup(), Lookups.fixed(aProject.getSubTreeSearchOptions())));
        project = aProject;
        name = aName;
        icon = ImageUtilities.icon2Image(aIcon);
        openIcon = ImageUtilities.icon2Image(aOpenIcon);
        displayName = aDisplayName;
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
        return super.getActions(context);
    }

    public PlatypusProjectImpl getProject() {
        return project;
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

    public FileObject getFolder() {
        return folder;
    }
    
    protected static class ProjectFilesDataFilter implements DataFilter {

        @Override
        public boolean acceptDataObject(DataObject obj) {
            return !FileUtil.toFile(obj.getPrimaryFile()).isHidden();
        }
        
    }
}
