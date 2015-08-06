/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class TransferableRelation<E extends Entity<?, ?, E>> implements Transferable {

    protected E entity = null;
    protected Field field = null;
    protected DataFlavor[] flavors = new DataFlavor[1];
    protected static DataFlavor defaultDataFlavor = null;

    static {
        try {
            defaultDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + TransferableRelation.class.getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TransferableRelation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TransferableRelation(E aEntity, Field aField) {
        super();
        entity = aEntity;
        field = aField;
        flavors[0] = defaultDataFlavor;
    }

    public static DataFlavor getDefaultDataFlavor() {
        return defaultDataFlavor;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.getRepresentationClass() == TransferableRelation.class;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this;
    }

    public E getEntity() {
        return entity;
    }

    public Field getField() {
        return field;
    }
}
