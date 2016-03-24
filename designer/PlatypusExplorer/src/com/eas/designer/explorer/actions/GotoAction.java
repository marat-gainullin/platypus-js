/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.actions;

import com.eas.client.cache.PlatypusFiles;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.Visualizer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "File",
id = "com.eas.designer.explorer.actions.GotoAction")
@ActionRegistration(displayName = "#CTL_GotoAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 618),
    @ActionReference(path = "Shortcuts", name = "DS-G")
})
public final class GotoAction implements ActionListener {

    public static final String PROJECT_TAB_TC_ID = "projectTabLogical_tc";

    @Override
    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor.InputLine inputLine = new NotifyDescriptor.InputLine(
                NbBundle.getMessage(GotoAction.class, "ENTER_AppElementID"), NbBundle.getMessage(GotoAction.class, "CTL_GotoAction"));
        DialogDisplayer.getDefault().notify(inputLine);
        try {
            String appElementId = inputLine.getInputText();
            if (appElementId != null && !appElementId.isEmpty()) {
                Project currentProject = Utilities.actionsGlobalContext().lookup(Project.class);
                if (currentProject instanceof PlatypusProjectImpl) {
                    if (selectNode((PlatypusProjectImpl) currentProject, appElementId)) {
                        return;
                    }
                }
                for (Project p : OpenProjects.getDefault().getOpenProjects()) {
                    if (p instanceof PlatypusProjectImpl && selectNode((PlatypusProjectImpl)p, appElementId)) {
                        break;
                    }
                }
//                for (Project project : OpenProjects.getDefault().getOpenProjects()) {
//                    FileObject fo = IndexerQuery.appElementId2File(project, appElementId);
//                    if (fo != null) {
//                        if (project instanceof PlatypusProject) {
//                            PlatypusProject pp = (PlatypusProject) project;
//                            TopComponent tc = WindowManager.getDefault().findTopComponent(PROJECT_TAB_TC_ID);
//                            if (tc instanceof ExplorerManager.Provider) {
//                                ExplorerManager.Provider expl = (ExplorerManager.Provider) tc;
//                                String foPath = FileUtil.getRelativePath(pp.getProjectDirectory(), fo).substring(PlatypusUtils.PLATYPUS_PROJECT_SOURCES_ROOT.length());
//                                String[] spath = (pp.getDisplayName() + "/" + PlatypusUtils.ELEMENTS_SOURCES_GROUP + foPath).split("/");
//                                Node nToSelect = null;
//                                try {
//                                    nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
//                                } catch (NodeNotFoundException ex) {
//                                    if (spath.length > 0) {
//                                        String lastPathEl = spath[spath.length - 1];
//                                        if (lastPathEl.endsWith("." + PlatypusFiles.JAVASCRIPT_EXTENSION)) {
//                                            lastPathEl = lastPathEl.substring(0, lastPathEl.length() - (PlatypusFiles.JAVASCRIPT_EXTENSION.length() + 1));
//                                            spath[spath.length - 1] = lastPathEl;
//                                            nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
//                                        } else if (lastPathEl.endsWith("." + fo.getExt())) {
//                                            lastPathEl = lastPathEl.substring(0, lastPathEl.length() - (fo.getExt().length() + 1));
//                                            spath[spath.length - 1] = lastPathEl;
//                                            nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
//                                        }
//                                    }
//                                }
//                                if (nToSelect != null) {
//                                    BeanTreeView btv = findProjectTreeView(tc);
//                                    assert btv != null : "Hack failed (1)! ProjectTreeView can't be found on projectTab";
//                                    Component treeComp = btv.getViewport().getView();
//                                    assert treeComp instanceof JTree : "Hack failed (2)! ProjectTreeView doesn't consist of JTree";
//                                    JTree tree = (JTree) treeComp;
//                                    TreeNode tn = Visualizer.findVisualizer(nToSelect);//doToSelect.getNodeDelegate());
//                                    if (tn == null) {
//                                        return;
//                                    }
//                                    TreeModel model = tree.getModel();
//                                    if (model instanceof DefaultTreeModel) {
//                                        TreePath path = new TreePath(((DefaultTreeModel) model).getPathToRoot(tn));
//                                        tree.expandPath(path);
//                                        tree.setSelectionPath(path);
//                                        Rectangle r = tree.getPathBounds(path);
//                                        if (r != null) {
//                                            r.grow(0, 25);
//                                            tree.scrollRectToVisible(r);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                }
            }
        } catch (Exception ex) {
            // no op
        }
    }

    private boolean selectNode(PlatypusProjectImpl project, String appElementName) throws NodeNotFoundException {
        FileObject fo = IndexerQuery.appElementId2File(project, appElementName);
        if (fo != null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent(PROJECT_TAB_TC_ID);
            if (tc instanceof ExplorerManager.Provider) {
                ExplorerManager.Provider expl = (ExplorerManager.Provider) tc;
                String foPath = FileUtil.getRelativePath(project.getSrcRoot(), fo);
                String[] spath = (project.getDisplayName() + "/" + PlatypusUtils.ELEMENTS_SOURCES_GROUP + "/" + foPath).split("/");
                Node nToSelect = null;
                try {
                    nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
                } catch (NodeNotFoundException ex) {
                    if (spath.length > 0) {
                        String lastPathEl = spath[spath.length - 1];
                        if (lastPathEl.endsWith("." + PlatypusFiles.JAVASCRIPT_EXTENSION)) {
                            lastPathEl = lastPathEl.substring(0, lastPathEl.length() - (PlatypusFiles.JAVASCRIPT_EXTENSION.length() + 1));
                            spath[spath.length - 1] = lastPathEl;
                            nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
                        } else if (lastPathEl.endsWith("." + fo.getExt())) {
                            lastPathEl = lastPathEl.substring(0, lastPathEl.length() - (fo.getExt().length() + 1));
                            spath[spath.length - 1] = lastPathEl;
                            nToSelect = NodeOp.findPath(expl.getExplorerManager().getRootContext(), spath);
                        }
                    }
                }
                if (nToSelect != null) {
                    BeanTreeView btv = findProjectTreeView(tc);
                    assert btv != null : "Hack failed (1)! ProjectTreeView can't be found on projectTab";
                    Component treeComp = btv.getViewport().getView();
                    assert treeComp instanceof JTree : "Hack failed (2)! ProjectTreeView doesn't consist of JTree";
                    JTree tree = (JTree) treeComp;
                    TreeNode tn = Visualizer.findVisualizer(nToSelect);//doToSelect.getNodeDelegate());
                    if (tn == null) {
                        return false;
                    }
                    TreeModel model = tree.getModel();
                    if (model instanceof DefaultTreeModel) {
                        TreePath path = new TreePath(((DefaultTreeModel) model).getPathToRoot(tn));
                        tree.expandPath(path);
                        tree.setSelectionPath(path);
                        Rectangle r = tree.getPathBounds(path);
                        if (r != null) {
                            r.grow(0, 25);
                            tree.scrollRectToVisible(r);
                        }
                    }
                }
                return nToSelect != null;
            }
        }
        return false;
    }

    /**
     * Dirty hack! Finds project tree view by class in given top component.
     *
     * @param tc
     * @return
     */
    private BeanTreeView findProjectTreeView(TopComponent tc) {
        for (Component comp : tc.getComponents()) {
            if (comp instanceof BeanTreeView) {
                return (BeanTreeView) comp;
            }
        }
        return null;
    }
}
