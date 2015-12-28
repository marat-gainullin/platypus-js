/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.designer.application.project.PlatypusProject;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.*;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Gala
 */
public class PlatypusProjectView implements LogicalViewProvider {

    protected PlatypusProjectImpl project;

    public PlatypusProjectView(PlatypusProjectImpl aProject) {
        super();
        project = aProject;
    }

    @Override
    public Node createLogicalView() {
        return new PlatypusProjectViewRootNode(project);
    }

    @Override
    public Node findPath(Node root, Object target) {
        Project p = root.getLookup().lookup(Project.class);
        if (p == null) {
            return null;
        }
        // Check each child node in turn.
        Node[] children = root.getChildren().getNodes(true);
        for (Node node : children) {
            if (target instanceof DataObject || target instanceof FileObject) {
                FileObject kidFO = node.getLookup().lookup(FileObject.class);
                if (kidFO == null) {
                    continue;
                }
                // Copied from org.netbeans.spi.java.project.support.ui.TreeRootNode.PathFinder.findPath:
                FileObject targetFO = null;
                if (target instanceof DataObject) {
                    targetFO = ((DataObject) target).getPrimaryFile();
                } else {
                    targetFO = (FileObject) target;
                }
                Project owner = FileOwnerQuery.getOwner(targetFO);
                if (!p.equals(owner)) {
                    return null; // Don't waste time if project does not own the fileobject
                }
                if (kidFO == targetFO) {
                    return node;
                } else if (FileUtil.isParentOf(kidFO, targetFO)) {
                    String relPath = FileUtil.getRelativePath(kidFO, targetFO);

                    // first path without extension (more common case)
                    String[] path = relPath.split("/"); // NOI18N
                    path[path.length - 1] = targetFO.getName();

                    // first try to find the file without extension (more common case)
                    Node found = findNode(node, path);
                    if (found == null) {
                        // file not found, try to search for the name with the extension
                        path[path.length - 1] = targetFO.getNameExt();
                        found = findNode(node, path);
                    }
                    if (found == null) {
                        // can happen for tests that are underneath sources directory
                        continue;
                    }
                    if (hasObject(found, target)) {
                        return found;
                    }
                    Node parent = found.getParentNode();
                    Children kids = parent.getChildren();
                    children = kids.getNodes();
                    for (Node child : children) {
                        if (hasObject(child, target)) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Node findNode(Node start, String[] path) {
        Node found = null;
        try {
            found = NodeOp.findPath(start, path);
        } catch (NodeNotFoundException ex) {
            // ignored
        }
        return found;
    }

    private boolean hasObject(Node node, Object obj) {
        if (obj == null) {
            return false;
        }
        FileObject fileObject = node.getLookup().lookup(FileObject.class);
        if (fileObject == null) {
            return false;
        }
        if (obj instanceof DataObject) {
            DataObject dataObject = node.getLookup().lookup(DataObject.class);
            if (dataObject == null) {
                return false;
            }
            if (dataObject.equals(obj)) {
                return true;
            }
            return hasObject(node, ((DataObject) obj).getPrimaryFile());
        } else if (obj instanceof FileObject) {
            return obj.equals(fileObject);
        }
        return false;
    }
    
    private static final class PlatypusProjectViewRootNode extends AbstractNode implements PropertyChangeListener {

        private final PlatypusProject project;
        private final ProjectInformation info;
        private static final RequestProcessor RP = new RequestProcessor(PlatypusProjectViewRootNode.class);
        private final List<Action> actions = new ArrayList<>();

        @SuppressWarnings("LeakingThisInConstructor")
        public PlatypusProjectViewRootNode(PlatypusProject aProject) {
            super(NodeFactorySupport.createCompositeChildren(aProject, "Projects/org-netbeans-modules-platypus/Nodes"), Lookups.fixed(aProject, aProject.getProjectDirectory(), aProject.getSubTreeSearchOptions()));
            project = aProject;
            info = ProjectUtils.getInformation(aProject);
            info.addPropertyChangeListener(WeakListeners.propertyChange(this, info));
            fillActions();
        }

        @Override
        public String getName() {
            return info.getDisplayName();
        }

        @Override
        public String getShortDescription() {
            return info.getDisplayName();
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.icon2Image(info.getIcon());
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            return actions.toArray(new Action[0]);
        }

        @Override
        public boolean canRename() {
            return true;
        }

        @Override
        public boolean canDestroy() {
            return true;
        }

        @Override
        public boolean canCut() {
            return false;
        }

        @Override
        public void setName(String name) {
            DefaultProjectOperations.performDefaultRenameOperation(project, name);
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            RP.post(new Runnable() {

                @Override
                public void run() {
                    fireNameChange(null, null);
                    fireDisplayNameChange(null, null);
                    fireIconChange();
                    fireOpenedIconChange();
                }
            });
        }

        private void fillActions() {
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_RUN,
                    NbBundle.getMessage(PlatypusProjectView.class, "LBL_RunAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_DEBUG,
                    NbBundle.getMessage(PlatypusProjectView.class, "LBL_DebugAction_Name"), null)); // NOI18N
            actions.add(null);
            actions.add(ProjectSensitiveActions.projectCommandAction(PlatypusProjectActions.COMMAND_CONNECT,
                    NbBundle.getMessage(PlatypusProjectView.class, "LBL_ConnectToDbAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(PlatypusProjectActions.COMMAND_DISCONNECT,
                    NbBundle.getMessage(PlatypusProjectView.class, "LBL_DisconnectFromDbAction_Name"), null)); // NOI18N
            actions.add(null);
            Action[] commonActions = CommonProjectActions.forType("org-netbeans-modules-platypus"); // NOI18N
            actions.addAll(Arrays.asList(commonActions));
        }
    }
}
