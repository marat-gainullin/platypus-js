/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class RowDragTransferable extends Object implements Transferable {

    public static DataFlavor flavor;
    protected JSObject row;

    static {
        try {
            flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Object.class.getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RowDragTransferable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RowDragTransferable(JSObject aRow) {
        super();
        row = aRow;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{flavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor aFlavor) {
        return flavor == aFlavor;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return row;
        }
        return null;
    }
}
