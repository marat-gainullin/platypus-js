/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.ResultingDialog;
import com.eas.designer.application.project.PlatypusProject;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author mg
 */
public class TableNameSelector {

    public static final ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/model/gui/messages");

    public static Window getFirstParentWindow(Component aChild) {
        Component lParent = aChild;
        while (lParent != null && !(lParent instanceof Window)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof Window) {
            return (Window) lParent;
        }
        return null;
    }

    public static TableRef[] selectTableName(PlatypusProject aProject, TableRef aOldValue, Component aParentComponent, String aTitle) throws Exception {
        return selectTableName(aProject, aOldValue, false, aParentComponent, aTitle);
    }

    public static TableRef[] selectTableName(PlatypusProject aProject, TableRef aOldValue, boolean allowDBChange, Component aParentComponent, String aTitle) throws Exception {
        return selectTableName(aProject, aOldValue, allowDBChange, true, aParentComponent, aTitle);
    }

    public static TableRef[] selectTableName(PlatypusProject aProject, TableRef aOldValue, boolean allowDBChange, boolean allowSchemaChange, Component aParentComponent, String aTitle) throws Exception {
        TableRef[] selected = null;
        Window parentWindow = getFirstParentWindow(aParentComponent);

        JScrollPane scroll = new JScrollPane();
        final DbTablesView tablesView = new DbTablesView(aProject, aOldValue.datasourceName, aOldValue.schema, aTitle, allowSchemaChange, allowDBChange);
        if (tablesView.txtConnection.getModel().getSize() != 0) {
            scroll.setViewportView(tablesView);
            final ResultingDialog dlg = new ResultingDialog(parentWindow, aTitle, scroll);

            tablesView.lstTables.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1 && dlg.getOkAction().isEnabled()) {
                        dlg.getOkAction().actionPerformed(null);
                    } else {
                        super.mouseClicked(e);
                    }
                }
            });
            tablesView.lstTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    dlg.getOkAction().setEnabled(!tablesView.lstTables.isSelectionEmpty());
                }
            });

            dlg.pack();
            Dimension packedSize = dlg.getSize();
            dlg.setSize(packedSize.width, Math.round(packedSize.width * 1.2f));
            dlg.setVisible(true);
            if (dlg.isOk()) {
                List<TableRef> aSelected = new ArrayList<>();
                List<String> sSelectedList = tablesView.lstTables.getSelectedValuesList();
                for (String sSelected : sSelectedList) {
                    int indexOfDot = sSelected.indexOf(".");
                    String schemaName;
                    String tableName;
                    if (indexOfDot != -1) {
                        schemaName = sSelected.substring(0, indexOfDot);
                        tableName = sSelected.substring(indexOfDot + 1);
                    } else {
                        schemaName = null;
                        tableName = sSelected;
                    }
                    TableRef lselected = new TableRef();
                    lselected.datasourceName = tablesView.datasourceName;
                    lselected.schema = schemaName;
                    lselected.tableName = tableName;
                    aSelected.add(lselected);
                }
                selected = aSelected.toArray(new TableRef[aSelected.size()]);
                checkTableRefDefaultDatasourceAndSchema(aProject, selected);
            }
        } else {
            NotifyDescriptor d = new NotifyDescriptor.Message(DatamodelDesignUtils.getLocalizedString("noConnections"), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
        return selected;
    }

    protected static void checkTableRefDefaultDatasourceAndSchema(PlatypusProject aProject, TableRef[] aRefs) {
        if (aRefs != null) {
            for (TableRef tRef : aRefs) {
                if (tRef != null && tRef.schema != null && !tRef.schema.isEmpty()) {
                    try {
                        if (aProject.getBasesProxy() != null) {
                            String schema = aProject.getBasesProxy().getConnectionSchema(tRef.datasourceName);
                            if (schema != null && !schema.isEmpty()
                                    && schema.equalsIgnoreCase(tRef.schema)) {
                                tRef.schema = null;
                            }
                        }
                        String defDatasource = aProject.getSettings().getDefaultDataSourceName();
                        if (tRef.datasourceName == null ? defDatasource == null : tRef.datasourceName.equals(defDatasource)) {
                            tRef.datasourceName = null;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(TableNameSelector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
