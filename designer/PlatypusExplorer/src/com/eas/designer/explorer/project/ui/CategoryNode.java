/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.explorer.project.PlatypusProject;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.datatransfer.PasteType;
/**
 *
 * @author Gala
 */
public class CategoryNode extends FilterNode {

    protected DataFolder dataFolder;
    protected FileObject folder;
    protected Image icon;
    protected Image openIcon;
    protected String name;
    protected String displayName;
    protected PlatypusProject project;

    public CategoryNode(PlatypusProject aProject, Node aDelegate, org.openide.nodes.Children aChildren, Icon aIcon, Icon aOpenIcon, String aName, String aDisplayName) {
        super(aDelegate, aChildren);
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

    public PlatypusProject getProject() {
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
}
