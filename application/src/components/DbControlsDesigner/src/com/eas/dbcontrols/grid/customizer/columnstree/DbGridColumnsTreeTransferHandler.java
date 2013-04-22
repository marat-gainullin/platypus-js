/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer.columnstree;

import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;
import com.eas.dbcontrols.grid.customizer.actions.DbGridColumnsStructureSnapshotAction;
import com.eas.dbcontrols.grid.edits.DbGridHeaderStructureEdit;
import java.awt.Component;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author mg
 */
public class DbGridColumnsTreeTransferHandler extends TransferHandler {

    protected DataFlavor columnFlavor = null;
    protected DbGridColumnMoveAction actionMove = null;
    protected DbGridColumnCopyAction actionCopy = null;
    protected DbGridCustomizer customizer;

    public DbGridColumnsTreeTransferHandler(DbGridCustomizer aCustomizer) {
        super();
        customizer = aCustomizer;
        actionMove = new DbGridColumnMoveAction(customizer);
        actionCopy = new DbGridColumnCopyAction(customizer);
        try {
            columnFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Object.class.getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbGridColumnsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected class ColumnTransferable implements Transferable {

        protected DataFlavor[] supportedFlavors = new DataFlavor[1];
        protected DbGridColumn column = null;

        public ColumnTransferable(DbGridColumn aColumn) {
            super();
            supportedFlavors[0] = columnFlavor;
            column = aColumn;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == columnFlavor;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return column;
        }
    }

    protected class DbGridColumnMoveAction extends DbGridColumnsStructureSnapshotAction {

        protected DbGridColumn source = null;
        protected DbGridColumn dest = null;
        protected DbGridColumn col = null;
        protected int inDestIndex = -1;

        public DbGridColumnMoveAction(DbGridCustomizer aCustomiozer) {
            super(aCustomiozer);
            putValue(Action.NAME, null);
            putValue(Action.ACCELERATOR_KEY, null);
        }

        public void setupAction(DbGridColumn aSource, DbGridColumn aDest, DbGridColumn aCol, int aInDestIndex) {
            source = aSource;
            dest = aDest;
            col = aCol;
            inDestIndex = aInDestIndex;
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            /*If the customizer in embedded, than this action is disabled*/
            if (!customizer.isEmbedded() && source != null && dest != null && col != null) {
                int lOldIndex = source.getChildren().indexOf(col);
                col.setParent(dest);

                source.removeChild(col);
                if (inDestIndex != -1) {
                    if (source == dest && inDestIndex > lOldIndex) {
                        dest.addChild(inDestIndex - 1, col);
                    } else {
                        dest.addChild(inDestIndex, col);
                    }
                } else {
                    dest.addChild(col);
                }
                return true;
            }
            return false;
        }

        @Override
        protected DbGridColumn getProcessedColumn() {
            return col;
        }
    }

    protected class DbGridColumnCopyAction extends DbGridColumnMoveAction {

        public DbGridColumnCopyAction(DbGridCustomizer aCustomizer) {
            super(aCustomizer);
        }

        @Override
        protected boolean changeStructure(DbGridHeaderStructureEdit aEdit) {
            if (source != null && dest != null && col != null) {
                DbGridColumn copyCol = col.copy();
                copyCol.setParent(dest);

                if (inDestIndex != -1) {
                    dest.addChild(inDestIndex, copyCol);
                } else {
                    dest.addChild(copyCol);
                }
                col = copyCol;
                return true;
            }
            return false;
        }

        @Override
        protected DbGridColumn getProcessedColumn() {
            return col;
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        super.exportToClipboard(comp, clip, action);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        DbGridColumn column = null;
        if (c != null && c instanceof JTree) {
            JTree tree = (JTree) c;
            column = DbGridColumnsStructureSnapshotAction.getSingleSelectedColumn(tree);
            if (column != null) {
                return new ColumnTransferable(column);
            }
        }
        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (support.isDrop()) {
            support.setShowDropLocation(true);
            Component cTree = support.getComponent();
            if (cTree != null && cTree instanceof JTree) {
                JTree treeDragTarget = (JTree) cTree;
                TreeModel iTargetTreeModel = treeDragTarget.getModel();
                if (iTargetTreeModel != null && iTargetTreeModel instanceof DbGridColumnsTreeModel) {
                    DbGridColumnsTreeModel targetTreeModel = (DbGridColumnsTreeModel) iTargetTreeModel;
                    DropLocation dl = support.getDropLocation();
                    if (dl != null && dl instanceof JTree.DropLocation) {
                        JTree.DropLocation tdl = (JTree.DropLocation) dl;
                        TreePath tp = tdl.getPath();
                        if (tp != null) {
                            Object oCol = tp.getLastPathComponent();
                            if (oCol != null && oCol instanceof DbGridColumn) {
                                try {
                                    DbGridColumn targetParent = (DbGridColumn) oCol;
                                    int targetIndex = tdl.getChildIndex();
                                    if (support.getTransferable() != null && support.getTransferable().getTransferData(columnFlavor) != null) {
                                        Object oDraggedCol = support.getTransferable().getTransferData(columnFlavor);
                                        if (oDraggedCol != null && oDraggedCol instanceof DbGridColumn) {
                                            DbGridColumn sourceColumn = (DbGridColumn) oDraggedCol;
                                            DbGridColumn sourceParent = sourceColumn.getParent();
                                            if (sourceParent == null) {
                                                sourceParent = targetTreeModel.getDummyRoot();
                                            }
                                            int sourceIndex = targetTreeModel.getIndexOfChild(sourceParent, sourceColumn);
                                            if (support.getDropAction() == MOVE) {
                                                if (targetParent == sourceParent && (sourceIndex == targetIndex || targetIndex - sourceIndex == 1)) {
                                                    return false;
                                                }
                                                if (isInParents(targetParent, sourceColumn)) {
                                                    return false;
                                                }
                                            } else if (support.getDropAction() == COPY) {
                                                // all is accepted
                                            }
                                        }
                                        return true;
                                    }
                                } catch (UnsupportedFlavorException ex) {
                                    Logger.getLogger(DbGridColumnsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(DbGridColumnsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.canImport(support);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (canImport(support)) {
            if (support.isDrop()) {
                Component cTree = support.getComponent();
                if (cTree != null && cTree instanceof JTree) {
                    JTree treeDragTarget = (JTree) cTree;
                    TreeModel iTargetTreeModel = treeDragTarget.getModel();
                    if (iTargetTreeModel != null && iTargetTreeModel instanceof DbGridColumnsTreeModel) {
                        DbGridColumnsTreeModel targetTreeModel = (DbGridColumnsTreeModel) iTargetTreeModel;
                        DropLocation dl = support.getDropLocation();
                        if (dl != null && dl instanceof JTree.DropLocation) {
                            JTree.DropLocation tdl = (JTree.DropLocation) dl;
                            TreePath tp = tdl.getPath();
                            if (tp != null) {
                                Object oCol = tp.getLastPathComponent();
                                if (oCol != null && oCol instanceof DbGridColumn) {
                                    try {
                                        DbGridColumn sourceColumn = null;
                                        DbGridColumn sourceParent = null;
                                        DbGridColumn targetParent = (DbGridColumn) oCol;
                                        int targetIndex = tdl.getChildIndex();
                                        if (support.getTransferable() != null && support.getTransferable().getTransferData(columnFlavor) != null) {
                                            Object oDraggedCol = support.getTransferable().getTransferData(columnFlavor);
                                            if (oDraggedCol != null && oDraggedCol instanceof DbGridColumn) {
                                                sourceColumn = (DbGridColumn) oDraggedCol;
                                                sourceParent = sourceColumn.getParent();
                                                if (sourceParent == null) {
                                                    sourceParent = targetTreeModel.getDummyRoot();
                                                }
                                                int sourceIndex = targetTreeModel.getIndexOfChild(sourceParent, sourceColumn);
                                                if (support.getDropAction() == MOVE) {
                                                    if (targetParent == sourceParent && (sourceIndex == targetIndex || targetIndex - sourceIndex == 1)) {
                                                        return false;
                                                    }
                                                    if (isInParents(targetParent, sourceColumn)) {
                                                        return false;
                                                    }
                                                }
                                            }
                                            if (targetParent == null) {
                                                targetParent = targetTreeModel.getDummyRoot();
                                            }
                                            actionMove.setupAction(sourceParent, targetParent, sourceColumn, targetIndex);
                                            actionCopy.setupAction(sourceParent, targetParent, sourceColumn, targetIndex);
                                            if (support.getDropAction() == MOVE) {
                                                actionMove.actionPerformed(null);
                                            } else if (support.getDropAction() == COPY) {
                                                actionCopy.actionPerformed(null);
                                            }
                                            return true;
                                        }
                                    } catch (UnsupportedFlavorException ex) {
                                        Logger.getLogger(DbGridColumnsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(DbGridColumnsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    protected boolean isInParents(DbGridColumn col2Test, DbGridColumn col2TestWith) {
        DbGridColumn lParent = col2Test;
        while (lParent != null) {
            if (lParent == col2TestWith) {
                return true;
            }
            lParent = lParent.getParent();
        }
        return false;
    }
}
