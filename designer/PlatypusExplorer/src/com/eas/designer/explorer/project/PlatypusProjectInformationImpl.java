/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectInformation;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Gala
 */
public class PlatypusProjectInformationImpl implements PlatypusProjectInformation {

    protected PropertyChangeSupport propertyChangeSupport;
    protected PlatypusProject project;

    public PlatypusProjectInformationImpl(PlatypusProjectImpl aProject) {
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

    @Override
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
