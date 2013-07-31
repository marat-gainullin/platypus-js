/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.explorer.project.ui;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.ErrorManager;

/**
 *
 * @author Gala
 */
@NodeFactory.Registration(projectType="org-netbeans-modules-platypus")
public class PlatypusProjectNodesFactory implements NodeFactory{

    @Override
    public NodeList<?> createNodes(Project aProject){
        try {
            assert aProject instanceof PlatypusProjectImpl;
            PlatypusProjectImpl project = (PlatypusProjectImpl)aProject;
            return new PlatypusProjectNodesList(project);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
