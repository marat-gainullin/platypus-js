/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.edits.ModifyFeaturesEdit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author pk
 */
public class FeatureDataSourceTransferHandler extends TransferHandler {

    private final DbMapCustomizer customizer;
    private boolean dragging = false;

    public FeatureDataSourceTransferHandler(DbMapCustomizer aCustomizer) {
        customizer = aCustomizer;
    }

    public int calcIndexToInsertAt(TransferSupport support) {
        int index = customizer.getFeaturesList().getSelectedIndex();
        if (index == -1) {
            index = customizer.getFeaturesList().getModel().getSize();
        } else {
            ++index;
        }
        if (support.isDrop()) {
            DropLocation dl = support.getDropLocation();
            assert dl instanceof JList.DropLocation;
            JList.DropLocation dropLocation = (JList.DropLocation) dl;
            index = dropLocation.getIndex();
        }
        if (index == -1) {
            index = customizer.getFeaturesList().getModel().getSize();
        }
        return index;
    }

    public List<RowsetFeatureDescriptor> getSelectedFeatures(final JList list) throws IllegalStateException {
        final Object[] selectedValues = list.getSelectedValues();
        final List<RowsetFeatureDescriptor> selectedFeatures = new ArrayList<>();
        for (int i = 0; i < selectedValues.length; i++) {
            if (!(selectedValues[i] instanceof RowsetFeatureDescriptor)) {
                throw new IllegalStateException(String.format("Selected value %s at %d is not a feature rowset.", selectedValues[i], i));
            } else {
                selectedFeatures.add((RowsetFeatureDescriptor) selectedValues[i]);
            }
        }
        return selectedFeatures;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            final JList list = (JList) c;
            List<RowsetFeatureDescriptor> selectedFeatures = getSelectedFeatures(list);
            return new FeatureDataSourceTransferable(dragging ? selectedFeatures : copyFeaturesList(selectedFeatures));
        } else {
            throw new IllegalArgumentException("Component is not JList");
        }
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        dragging = true;
        super.exportAsDrag(comp, e, action);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
        if (data.isDataFlavorSupported(FeatureDataSourceTransferable.featureDataSourceFlavor)) {
            if (!dragging && action == TransferHandler.MOVE) {
                try {
                    List<RowsetFeatureDescriptor> selectedFeatures = getSelectedFeatures(customizer.getFeaturesList());
                    delete(selectedFeatures); // actual features
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(FeatureDataSourceTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        dragging = false;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (support.isDataFlavorSupported(FeatureDataSourceTransferable.featureDataSourceFlavor)) {
            try {
                Object oTransferData = support.getTransferable().getTransferData(FeatureDataSourceTransferable.featureDataSourceFlavor);
                if (oTransferData instanceof List) {
                    List<RowsetFeatureDescriptor> transferredFeatures = (List<RowsetFeatureDescriptor>) oTransferData;
                    return !transferredFeatures.isEmpty();
                }
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(FeatureDataSourceTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FeatureDataSourceTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (canImport(support)) {
            try {
                Object oTransferData = support.getTransferable().getTransferData(FeatureDataSourceTransferable.featureDataSourceFlavor);
                List<RowsetFeatureDescriptor> transferredFeatures = (List<RowsetFeatureDescriptor>) oTransferData;
                int index = calcIndexToInsertAt(support);
                if (support.isDrop()) {
                    customizer.getUndoSupport().beginUpdate();
                    try {
                        if (support.getUserDropAction() == TransferHandler.MOVE) {
                            index -= delete(transferredFeatures, index); // actual features
                        }
                        paste(transferredFeatures, index); // actual features
                    } finally {
                        customizer.getUndoSupport().endUpdate();
                    }
                } else {
                    paste(transferredFeatures, index); // pre-copied features
                }
                return true;
            } catch (Exception ex) {
                Logger.getLogger(FeatureDataSourceTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Checks seelction before deleting
     *
     * @param aFeatures New features model list.
     */
    private void checkSelection(List<RowsetFeatureDescriptor> aFeatures) {
        int selIndex = Math.max(customizer.getFeaturesList().getMaxSelectionIndex(), customizer.getFeaturesList().getMinSelectionIndex());
        int newModelSize = aFeatures.size();
        if (newModelSize > 0) {
            if (selIndex >= newModelSize) {
                customizer.getFeaturesList().setSelectedIndex(newModelSize - 1);
            }
        } else {
            customizer.getFeaturesList().clearSelection();
        }
    }

    private List<RowsetFeatureDescriptor> copyFeaturesList(List<RowsetFeatureDescriptor> aFeatures) {
        List<RowsetFeatureDescriptor> features = new ArrayList<>();
        for (int i = 0; i < aFeatures.size(); i++) {
            features.add(aFeatures.get(i).copy());
        }
        return features;
    }

    /**
     * Deletes passed features through undable edit.
     *
     * @param aFeaturesToDelete Actual features instances.
     */
    private void delete(List<RowsetFeatureDescriptor> aFeaturesToDelete) throws CloneNotSupportedException {
        delete(aFeaturesToDelete, 0);
    }

    /**
     * Deletes passed features through undable edit.
     *
     * @param aFeaturesToDelete Actual features instances.
     * @param insertIndexProposal Proposal of subsequent insert operation. If
     * item with lesser or equal indicies are removed, than subsequent insert
     * operation must decrease it's insertTo index
     * @return Delta value for insert operation index to be decreased on.
     */
    private int delete(List<RowsetFeatureDescriptor> aFeaturesToDelete, int insertIndexProposal) throws CloneNotSupportedException {
        int indexDelta = 0;
        DbMapDesignInfo originalDesignInfo = (DbMapDesignInfo) customizer.getDesignInfo();
        List<RowsetFeatureDescriptor> remainingFeatures = new ArrayList<>();
        for (int i = originalDesignInfo.getFeatures().size() - 1; i >= 0; i--) {
            RowsetFeatureDescriptor d = originalDesignInfo.getFeatures().get(i);
            if (!aFeaturesToDelete.contains(d)) {
                remainingFeatures.add(0, d.copy());
            } else {
                if (i < insertIndexProposal) {
                    ++indexDelta;
                }
            }
        }
        checkSelection(remainingFeatures);
        final ModifyFeaturesEdit edit = new ModifyFeaturesEdit(originalDesignInfo, originalDesignInfo.getFeatures(), remainingFeatures);
        edit.redo();
        customizer.getUndoSupport().postEdit(edit);
        return indexDelta;
    }

    private void paste(List<RowsetFeatureDescriptor> aFeatures, int aIndex2AddTo) throws CloneNotSupportedException {
        if (!aFeatures.isEmpty()) {
            DbMapDesignInfo originalDesignInfo = (DbMapDesignInfo) customizer.getDesignInfo();
            List<RowsetFeatureDescriptor> newFeatures = new ArrayList<>(originalDesignInfo.getFeatures());
            int minSelectionIndex = aIndex2AddTo;
            int maxSelectionIndex = minSelectionIndex - 1;// to force the cycle to form properly selection bounds
            for (int i = 0; i < aFeatures.size(); i++) {
                RowsetFeatureDescriptor d = aFeatures.get(i);
                RowsetFeatureDescriptor pd = d.copy();
                newFeatures.add(++maxSelectionIndex, pd);
            }
            customizer.getFeaturesList().clearSelection();
            final ModifyFeaturesEdit edit = new ModifyFeaturesEdit(originalDesignInfo, originalDesignInfo.getFeatures(), newFeatures);
            edit.redo();
            customizer.getUndoSupport().postEdit(edit);
            customizer.getFeaturesList().getSelectionModel().setSelectionInterval(minSelectionIndex, maxSelectionIndex);
        }
    }

    public static class FeatureDataSourceTransferable implements Transferable {

        public static final DataFlavor featureDataSourceFlavor;
        public static final DataFlavor[] flavors;
        private final List<RowsetFeatureDescriptor> features;

        static {
            DataFlavor f = null;
            try {
                f = new DataFlavor(String.format("%s;class=%s", DataFlavor.javaJVMLocalObjectMimeType, Object.class.getName()));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FeatureDataSourceTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                f = null;
            }
            featureDataSourceFlavor = f;
            flavors = new DataFlavor[]{featureDataSourceFlavor};
        }

        public FeatureDataSourceTransferable(List<RowsetFeatureDescriptor> aFeatures) {
            features = aFeatures;
        }

        public boolean isEmpty() {
            return features == null || features.isEmpty();
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (DataFlavor f : flavors) {
                if (f.equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (featureDataSourceFlavor.equals(flavor)) {
                return features;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}
