/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import com.bearsoft.gui.grid.events.insets.InsetAfterLastChangedEvent;
import com.bearsoft.gui.grid.events.insets.InsetChangeListener;
import com.bearsoft.gui.grid.events.insets.InsetPreFirstChangedEvent;
import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 * @param <M>
 */
public class InsettedRowSorter<M extends TableModel> extends DelegatingRowSorter<M> {

    protected class DelegateListener implements RowSorterListener {

        @Override
        public void sorterChanged(RowSorterEvent e) {
            fireRowSorterChanged(null);
        }
    }

    protected class InsetListener implements InsetChangeListener {

        @Override
        public void insetPreFirstChanged(InsetPreFirstChangedEvent anEvent) {
            fireRowSorterChanged(null);
        }

        @Override
        public void insetAfterLastChanged(InsetAfterLastChangedEvent anEvent) {
            fireRowSorterChanged(null);
        }
    }
    protected LinearInset inset;

    protected InsettedRowSorter(M aModel, RowSorter<M> aDelegate, LinearInset aInset) {
        super(aDelegate, aModel);
        inset = aInset;
        if (delegate != null) {
            delegate.addRowSorterListener(new DelegateListener());
        }
        assert inset != null : "Inset is required for constructing InsettedRowSorter.";
        inset.addInsetChangeListener(new InsetListener());
    }

    public InsettedRowSorter(RowSorter<M> aDelegate, LinearInset aInset) {
        this(null, aDelegate, aInset);
    }

    public InsettedRowSorter(M aModel, LinearInset aInset) {
        this(aModel, null, aInset);
    }

    /**
     * Returns the location of <code>index</code> in terms of the
     * underlying model.  That is, for the row <code>index</code> in
     * the coordinates of the view this returns the row index in terms
     * of the underlying model.
     * Inset within this row sorter works like this:
     * 1. <code>index</code> is in the InsetPart.PartKind.BEFORE section of the inset space. In this case index is reverted. For example inset is 3 and view index is 2, model index will be -1.
     * 2. <code>index</code> is in the InsetPart.PartKind.CONTENT section of the inset space. In this case index will be converted in standard way.
     * 3. <code>index</code> is in the InsetPart.PartKind.AFTER section of the inset space. In this case index is biased with very big value (<code>InsetPart.AFTER_INSET_BIAS</code>).
     * @param aViewIndex the row index in terms of the underlying view
     * @return row index in terms of the view
     * @throws IndexOutOfBoundsException if <code>index</code> is outside the
     *         range of the view
     * @see InsetPart
     * @see LinearInset#toInnerSpace(int, int)
     */
    @Override
    public int convertRowIndexToModel(int aViewIndex) {
        if (delegate != null) {
            InsetPart insetPart = inset.toInnerSpace(aViewIndex, delegate.getViewRowCount());
            if (insetPart.getKind() == InsetPart.PartKind.CONTENT) {
                return delegate.convertRowIndexToModel(insetPart.getValue());
            } else if (insetPart.getKind() == InsetPart.PartKind.BEFORE) {
                return insetPart.getValue()-inset.getPreFirst();
            } else {
                assert insetPart.getKind() == InsetPart.PartKind.AFTER;
                return insetPart.getValue() + InsetPart.AFTER_INSET_BIAS;
            }
        } else {
            assert model != null;
            InsetPart insetPart = inset.toInnerSpace(aViewIndex, model.getRowCount());
            if (insetPart.getKind() == InsetPart.PartKind.CONTENT) {
                return insetPart.getValue();
            } else if (insetPart.getKind() == InsetPart.PartKind.BEFORE) {
                return insetPart.getValue()-inset.getPreFirst();
            } else {
                assert insetPart.getKind() == InsetPart.PartKind.AFTER;
                return insetPart.getValue() + InsetPart.AFTER_INSET_BIAS;
            }
        }
    }

    @Override
    public int convertRowIndexToView(int aModelIndex) {
        if (delegate != null) {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, delegate.convertRowIndexToView(aModelIndex)), LinearInset.EMPTY_CONTENT);
        } else {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, aModelIndex), LinearInset.EMPTY_CONTENT);
        }
    }

    @Override
    public int getViewRowCount() {
        int viewRowCount = inset.getPreFirst() + inset.getAfterLast();
        if (delegate != null) {
            viewRowCount += delegate.getViewRowCount();
        } else {
            assert model != null;
            viewRowCount += model.getRowCount();
        }
        return viewRowCount;
    }
}
