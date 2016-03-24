/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.ui;

import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
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
public class PlatypusProjectNodesList implements NodeList<FileObject> {

    private static final String JAVASCRIPT_FILE_EXTENSION = "js";
    private static final String PACKAGE_PREFIX = "com/eas/designer/explorer/project/ui/";
    public static final ImageIcon sourceIcon = ImageUtilities.loadImageIcon(PACKAGE_PREFIX + "elements.png", true);
    public static final DataFilter APPLICATION_TYPES_FILTER = new ApplicationTypesFilter();
    protected PlatypusProjectImpl project;
    protected Set<ChangeListener> listeners = new HashSet<>();

    public PlatypusProjectNodesList(PlatypusProjectImpl aProject) throws Exception {
        super();
        project = aProject;
        project.getSettings().getChangeSupport().addPropertyChangeListener(PlatypusProjectSettingsImpl.SOURCE_PATH_KEY, (PropertyChangeEvent evt) -> {
            for (ChangeListener cl : listeners.toArray(new ChangeListener[]{})) {
                cl.stateChanged(new ChangeEvent(PlatypusProjectNodesList.this));
            }
        });
    }

    @Override
    public List<FileObject> keys() {
        return Collections.singletonList(project.getSrcRoot());
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
    public Node node(FileObject aFilePoint) {
        return new CategoryNode(project,
                DataFolder.findFolder(aFilePoint),
                sourceIcon,
                sourceIcon,
                PlatypusUtils.ELEMENTS_SOURCES_GROUP,
                NbBundle.getMessage(PlatypusProjectImpl.class, PlatypusUtils.ELEMENTS_SOURCES_GROUP));
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
}
