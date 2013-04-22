/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gala
 */
public class PlatypusProjectInformation implements ProjectInformation {

    protected PropertyChangeSupport propertyChangeSupport;
    protected PlatypusProject project;

    public PlatypusProjectInformation(PlatypusProject aProject) {
        super();
        project = aProject;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public String getName() {
        return project.getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
        return project.getDisplayName();
    }

    public void setDisplayName(String aName) {
        String oldValue = getDisplayName();
        project.getSettings().setDisplayName(aName);
        propertyChangeSupport.firePropertyChange("displayName", oldValue, aName);
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("com/eas/designer/explorer/project/pencil-ruler.png", true);
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propertyChangeSupport.addPropertyChangeListener(pl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propertyChangeSupport.removePropertyChangeListener(pl);
    }
}
