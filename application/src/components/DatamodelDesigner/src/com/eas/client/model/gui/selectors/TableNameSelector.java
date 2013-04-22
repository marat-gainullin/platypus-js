/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.DbClient;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.gui.ResultingDialog;
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

    public static TableRef[] selectTableName(DbClient aClient, AppElementSelectorCallback aAppElementSelector, TableRef aOldValue, Component aParentComponent, String aTitle) throws Exception {
        return selectTableName(aClient, aAppElementSelector, aOldValue, false, aParentComponent, aTitle);
    }

    public static TableRef[] selectTableName(DbClient aClient, AppElementSelectorCallback aAppElementSelector, TableRef aOldValue, boolean allowDBChange, Component aParentComponent, String aTitle) throws Exception {
        return selectTableName(aClient, aAppElementSelector, aOldValue, allowDBChange, true, aParentComponent, aTitle);
    }

    public static TableRef[] selectTableName(DbClient aClient, AppElementSelectorCallback aAppElementSelector, TableRef aOldValue, boolean allowDBChange, boolean allowSchemaChange, Component aParentComponent, String aTitle) throws Exception {
        TableRef[] selected = null;
        Window parentWindow = getFirstParentWindow(aParentComponent);

        JScrollPane scroll = new JScrollPane();
        final DbTablesView tablesView = new DbTablesView(aClient, aOldValue.dbId, aOldValue.schema, aTitle, allowSchemaChange, allowDBChange, aAppElementSelector);
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
        dlg.setSize(packedSize.width, Math.round(packedSize.width*1.2f));
        dlg.setVisible(true);
        if (dlg.isOk()) {
            List<TableRef> aSelected = new ArrayList<>();
            Object[] osSelected = tablesView.lstTables.getSelectedValues();
            for (Object oSelected : osSelected) {
                if (oSelected != null && oSelected instanceof String) {
                    String sSelected = (String) oSelected;
                    int indexOfDot = sSelected.indexOf(".");
                    if (indexOfDot != -1) {
                        String schemaName = sSelected.substring(0, indexOfDot);
                        String tableName = sSelected.substring(indexOfDot + 1);
                        TableRef lselected = new TableRef();
                        lselected.dbId = aOldValue.dbId;
                        lselected.schema = schemaName;
                        lselected.tableName = tableName;
                        aSelected.add(lselected);
                    }
                }
            }
            selected = aSelected.toArray(new TableRef[aSelected.size()]);
            checkTableRefDefaultSchema(aClient, selected);
        }
        return selected;
    }

    protected static void checkTableRefDefaultSchema(DbClient aClient, TableRef[] aRefs) {
        if (aRefs != null) {
            for (TableRef tRef : aRefs) {
                if (tRef != null && tRef.schema != null && !tRef.schema.isEmpty()) {
                    try {
                        if (aClient != null) {
                            String schema = aClient.getDbMetadataCache(tRef.dbId).getConnectionSchema();
                            if (schema != null && !schema.isEmpty()
                                    && schema.equalsIgnoreCase(tRef.schema)) {
                                tRef.schema = null;
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(TableNameSelector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
