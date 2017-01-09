/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.selection;

import com.bearsoft.gui.grid.events.insets.InsetAfterLastChangedEvent;
import com.bearsoft.gui.grid.events.insets.InsetChangeListener;
import com.bearsoft.gui.grid.events.insets.InsetPreFirstChangedEvent;
import com.bearsoft.gui.grid.insets.InsetContent;
import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Gala
 */
public class InsettedListSelectionModel extends DefaultListSelectionModel {

    protected LinearInset inset;
    protected InsetContent informer;
    protected ListSelectionModel delegate;

    protected class InsetListener implements InsetChangeListener
    {

        public void insetPreFirstChanged(InsetPreFirstChangedEvent anEvent) {
            fireValueChanged(false);
        }

        public void insetAfterLastChanged(InsetAfterLastChangedEvent anEvent) {
            fireValueChanged(false);
        }

    }

    protected class DelegateListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent delegateEvent) {
            int firstRow = inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, delegateEvent.getFirstIndex()), LinearInset.EMPTY_CONTENT);
            int lastRow = inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, delegateEvent.getLastIndex()), LinearInset.EMPTY_CONTENT);
            fireValueChanged(firstRow, lastRow, delegateEvent.getValueIsAdjusting());
        }
    }

    public InsettedListSelectionModel(ListSelectionModel aDelegate, LinearInset aInset, InsetContent aInformer) {
        super();
        delegate = aDelegate;
        inset = aInset;
        informer = aInformer;
        delegate.addListSelectionListener(new DelegateListSelectionListener());
        inset.addInsetChangeListener(new InsetListener());
        setSelectionMode(delegate.getSelectionMode());
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(index0, index1);
        int contentSize = informer.getContentSize();
        InsetPart part0 = inset.toInnerSpace(index0, contentSize);
        InsetPart part1 = inset.toInnerSpace(index1, contentSize);
        if (part0.getKind() == InsetPart.PartKind.CONTENT
                || part1.getKind() == InsetPart.PartKind.CONTENT
                || (part0.getKind() == InsetPart.PartKind.BEFORE && part1.getKind() == InsetPart.PartKind.AFTER)) {
            if (part0.getKind() == InsetPart.PartKind.BEFORE) {
                index0 = 0;
            } else {
                index0 = part0.getValue();
            }
            if (part1.getKind() == InsetPart.PartKind.AFTER) {
                index1 = contentSize - 1;
            } else {
                index1 = part1.getValue();
            }
            delegate.setSelectionInterval(index0, index1);
        } else {
            delegate.clearSelection();
        }
    }

    @Override
    public void addSelectionInterval(int index0, int index1) {
        super.addSelectionInterval(index0, index1);
        int contentSize = informer.getContentSize();
        InsetPart part0 = inset.toInnerSpace(index0, contentSize);
        InsetPart part1 = inset.toInnerSpace(index1, contentSize);
        if (part0.getKind() == InsetPart.PartKind.CONTENT
                || part1.getKind() == InsetPart.PartKind.CONTENT
                || (part0.getKind() == InsetPart.PartKind.BEFORE && part1.getKind() == InsetPart.PartKind.AFTER)) {
            if (part0.getKind() == InsetPart.PartKind.BEFORE) {
                index0 = 0;
            } else {
                index0 = part0.getValue();
            }
            if (part1.getKind() == InsetPart.PartKind.AFTER) {
                index1 = contentSize - 1;
            } else {
                index1 = part1.getValue();
            }
            delegate.addSelectionInterval(index0, index1);
        }//else ...
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
        super.removeSelectionInterval(index0, index1);
        int contentSize = informer.getContentSize();
        InsetPart part0 = inset.toInnerSpace(index0, contentSize);
        InsetPart part1 = inset.toInnerSpace(index1, contentSize);
        if (part0.getKind() == InsetPart.PartKind.CONTENT
                || part1.getKind() == InsetPart.PartKind.CONTENT
                || (part0.getKind() == InsetPart.PartKind.BEFORE && part1.getKind() == InsetPart.PartKind.AFTER)) {
            if (part0.getKind() == InsetPart.PartKind.BEFORE) {
                index0 = 0;
            } else {
                index0 = part0.getValue();
            }
            if (part1.getKind() == InsetPart.PartKind.AFTER) {
                index1 = contentSize - 1;
            } else {
                index1 = part1.getValue();
            }
            delegate.removeSelectionInterval(index0, index1);
        }//else ...
    }

    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
        super.insertIndexInterval(index, length, before);
        /* The first new index will appear at insMinIndex and the last
         * one will appear at insMaxIndex
         */
        int index0 = before ? index : index + 1;
        int index1 = (index0 + length) - 1;

        int contentSize = informer.getContentSize();
        InsetPart part0 = inset.toInnerSpace(index0, contentSize);
        InsetPart part1 = inset.toInnerSpace(index1, contentSize);
        if (part0.getKind() == InsetPart.PartKind.CONTENT
                || part1.getKind() == InsetPart.PartKind.CONTENT
                || (part0.getKind() == InsetPart.PartKind.BEFORE && part1.getKind() == InsetPart.PartKind.AFTER)) {
            if (part0.getKind() == InsetPart.PartKind.BEFORE) {
                index0 = 0;
            } else {
                index0 = part0.getValue();
            }
            if (part1.getKind() == InsetPart.PartKind.AFTER) {
                index1 = contentSize - 1;
            } else {
                index1 = part1.getValue();
            }
            length = index1 - index0 + 1;

            delegate.insertIndexInterval(before ? index0 : index0 - 1, length, before);
        }//else ...
    }

    @Override
    public void removeIndexInterval(int index0, int index1) {
        super.removeIndexInterval(index0, index1);
        int contentSize = informer.getContentSize();
        InsetPart part0 = inset.toInnerSpace(index0, contentSize);
        InsetPart part1 = inset.toInnerSpace(index1, contentSize);
        if (part0.getKind() == InsetPart.PartKind.CONTENT
                || part1.getKind() == InsetPart.PartKind.CONTENT
                || (part0.getKind() == InsetPart.PartKind.BEFORE && part1.getKind() == InsetPart.PartKind.AFTER)) {
            if (part0.getKind() == InsetPart.PartKind.BEFORE) {
                index0 = 0;
            } else {
                index0 = part0.getValue();
            }
            if (part1.getKind() == InsetPart.PartKind.AFTER) {
                index1 = contentSize - 1;
            } else {
                index1 = part1.getValue();
            }
            delegate.removeIndexInterval(index0, index1);
        }//else ...
    }

    @Override
    public void setLeadSelectionIndex(int index) {
        super.setLeadSelectionIndex(index);
    }

    @Override
    public void setAnchorSelectionIndex(int index) {
        super.setAnchorSelectionIndex(index);
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        delegate.clearSelection();
    }

    @Override
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        super.setValueIsAdjusting(valueIsAdjusting);
        delegate.setValueIsAdjusting(valueIsAdjusting);
    }

    @Override
    public void setSelectionMode(int selectionMode) {
        super.setSelectionMode(selectionMode);
        delegate.setSelectionMode(selectionMode);
    }

    @Override
    public int getMinSelectionIndex() {
        int minFormDelegate = delegate.getMinSelectionIndex();
        if(minFormDelegate != -1)
        {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, minFormDelegate), informer.getContentSize());
        }
        return super.getMinSelectionIndex();
    }

    @Override
    public int getMaxSelectionIndex() {
        int maxFormDelegate = delegate.getMaxSelectionIndex();
        if(maxFormDelegate != -1)
        {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, maxFormDelegate), informer.getContentSize());
        }
        return super.getMaxSelectionIndex();
    }

    @Override
    public boolean isSelectedIndex(int index) {
        InsetPart part = inset.toInnerSpace(index, informer.getContentSize());
        if(part.getKind() == InsetPart.PartKind.CONTENT)
        {
            return delegate.isSelectedIndex(part.getValue());
        }
        return super.isSelectedIndex(index);
    }

    @Override
    public int getAnchorSelectionIndex() {
        /*
        int anchorFormDelegate = delegate.getAnchorSelectionIndex();
        if(anchorFormDelegate != -1)
        {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, anchorFormDelegate), informer.getContentSize());
        }
        */
        return super.getAnchorSelectionIndex();
    }

    @Override
    public int getLeadSelectionIndex() {
        /*
        int leadFormDelegate = delegate.getLeadSelectionIndex();
        if(leadFormDelegate != -1)
        {
            return inset.toOuterSpace(new InsetPart(InsetPart.PartKind.CONTENT, leadFormDelegate), informer.getContentSize());
        }
         */
        return super.getLeadSelectionIndex();
    }

    @Override
    public boolean isSelectionEmpty() {
        return delegate.isSelectionEmpty() && super.isSelectionEmpty();
    }

    @Override
    public boolean getValueIsAdjusting() {
        return super.getValueIsAdjusting();
    }

    @Override
    public int getSelectionMode() {
        return super.getSelectionMode();
    }
}
