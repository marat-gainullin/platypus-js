/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.selection;

import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.events.constraints.ConstraintChangeListener;
import com.bearsoft.gui.grid.events.constraints.ConstraintMaximumChangedEvent;
import com.bearsoft.gui.grid.events.constraints.ConstraintMinimumChangedEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Gala
 */
public class ConstrainedListSelectionModel implements ListSelectionModel {

    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    protected LinearConstraint constraint = null;
    protected ListSelectionModel delegate = null;
    protected Set<ListSelectionListener> listeners = new HashSet<>();

    protected class ConstraintListener implements ConstraintChangeListener {

        public void constraintMinimumChanged(ConstraintMinimumChangedEvent anEvent) {
            fireValueChanged(false);
        }

        public void constraintMaximumChanged(ConstraintMaximumChangedEvent anEvent) {
            fireValueChanged(false);
        }
    }

    protected class DelegateListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent delegateEvent) {
            if ((delegateEvent.getFirstIndex() >= constraint.getMin() && delegateEvent.getFirstIndex() <= constraint.getMax())
                    || (delegateEvent.getLastIndex() >= constraint.getMin() && delegateEvent.getLastIndex() <= constraint.getMax())) {
                int firstIndex = constraint.constraint(delegateEvent.getFirstIndex());
                int lastIndex = constraint.constraint(delegateEvent.getLastIndex());
                fireValueChanged(firstIndex, lastIndex, delegate.getValueIsAdjusting());
                /*
                ListSelectionEvent e = new ListSelectionEvent(ConstrainedListSelectionModel.this, firstRow, lastRow, delegate.getValueIsAdjusting());
                for (ListSelectionListener l : listeners) {
                l.valueChanged(e);
                }
                 */
            }
        }
    }

    public ConstrainedListSelectionModel(ListSelectionModel aDelegate, LinearConstraint aConstraint) {
        super();
        constraint = aConstraint;
        delegate = aDelegate;
        delegate.addListSelectionListener(new DelegateListSelectionListener());
        constraint.addConstraintChangeListener(new ConstraintListener());
        setSelectionMode(delegate.getSelectionMode());
    }

    public ListSelectionModel getDelegate() {
        return delegate;
    }

    public void setSelectionInterval(int index0, int index1) {
        int uindex0 = constraint.unconstraint(index0);
        int uindex1 = constraint.unconstraint(index1);
        delegate.setSelectionInterval(uindex0, uindex1);
    }

    public void addSelectionInterval(int index0, int index1) {
        int uindex0 = constraint.unconstraint(index0);
        int uindex1 = constraint.unconstraint(index1);
        delegate.addSelectionInterval(uindex0, uindex1);
    }

    public void removeSelectionInterval(int index0, int index1) {
        int uindex0 = constraint.unconstraint(index0);
        int uindex1 = constraint.unconstraint(index1);
        delegate.removeSelectionInterval(uindex0, uindex1);
    }

    public void setAnchorSelectionIndex(int index) {
        int uindex = constraint.unconstraint(index);
        delegate.setAnchorSelectionIndex(uindex);
    }

    public void setLeadSelectionIndex(int index) {
        int uindex = constraint.unconstraint(index);
        delegate.setLeadSelectionIndex(uindex);
    }

    public void insertIndexInterval(int index, int length, boolean before) {
        int uindex = constraint.unconstraint(index);
        /* The first new index will appear at insMinIndex and the last
         * one will appear at insMaxIndex
         */
        int index0 = before ? uindex : uindex + 1;
        int index1 = (index0 + length) - 1;

        index0 = constraint.unconstraint(index0);
        index1 = constraint.unconstraint(index1);

        length = index1 - index0 + 1;
        delegate.insertIndexInterval(before ? index0 : index0 - 1, length, before);
    }

    public void removeIndexInterval(int index0, int index1) {
        int uindex0 = constraint.unconstraint(index0);
        int uindex1 = constraint.unconstraint(index1);
        delegate.removeIndexInterval(uindex0, uindex1);
    }

    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        delegate.setValueIsAdjusting(valueIsAdjusting);
    }

    public boolean getValueIsAdjusting() {
        return delegate.getValueIsAdjusting();
    }

    public void setSelectionMode(int selectionMode) {
        delegate.setSelectionMode(selectionMode);
    }

    public int getSelectionMode() {
        return delegate.getSelectionMode();
    }

    public int getMinSelectionIndex() {
        if (!delegate.isSelectionEmpty()) {
            int unMinIndex = delegate.getMinSelectionIndex();
            int unMaxIndex = delegate.getMaxSelectionIndex();
            if ((unMinIndex >= constraint.getMin() && unMinIndex <= constraint.getMax())
                    || (unMaxIndex >= constraint.getMin() && unMaxIndex <= constraint.getMax())) {
                return constraint.constraint(unMinIndex);
            } else {
                if (unMinIndex < constraint.getMin() && unMaxIndex > constraint.getMax()) {
                    return constraint.constraint(unMinIndex);
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
    }

    public int getMaxSelectionIndex() {
        if (!delegate.isSelectionEmpty()) {
            int unMinIndex = delegate.getMinSelectionIndex();
            int unMaxIndex = delegate.getMaxSelectionIndex();
            if ((unMinIndex >= constraint.getMin() && unMinIndex <= constraint.getMax())
                    || (unMaxIndex >= constraint.getMin() && unMaxIndex <= constraint.getMax())) {
                return constraint.constraint(unMaxIndex);
            } else {
                if (unMinIndex < constraint.getMin() && unMaxIndex > constraint.getMax()) {
                    return constraint.constraint(unMaxIndex);
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
    }

    public boolean isSelectedIndex(int index) {
        int uindex = constraint.unconstraint(index);
        return delegate.isSelectedIndex(uindex);
    }

    public int getAnchorSelectionIndex() {
        int unAnchor = delegate.getAnchorSelectionIndex();
        int unLead = delegate.getLeadSelectionIndex();
        if ((unAnchor >= constraint.getMin() && unAnchor <= constraint.getMax())
                || (unLead >= constraint.getMin() && unLead <= constraint.getMax())) {
            return constraint.constraint(unAnchor);
        } else {
            if (unAnchor < constraint.getMin() && unLead > constraint.getMax()) {
                return constraint.constraint(unAnchor);
            } else {
                return -1;
            }
        }
    }

    public int getLeadSelectionIndex() {
        int unAnchor = delegate.getAnchorSelectionIndex();
        int unLead = delegate.getLeadSelectionIndex();
        if ((unAnchor >= constraint.getMin() && unAnchor <= constraint.getMax())
                || (unLead >= constraint.getMin() && unLead <= constraint.getMax())) {
            return constraint.constraint(unLead);
        } else {
            if (unAnchor < constraint.getMin() && unLead > constraint.getMax()) {
                return constraint.constraint(unLead);
            } else {
                return -1;
            }
        }
    }

    public void clearSelection() {
        int lMin = constraint.getMin();
        int lMax = Math.min(constraint.getMax(), delegate.getMaxSelectionIndex());
        if(lMin <= lMax)
            delegate.removeSelectionInterval(lMin, lMax);
    }

    public boolean isSelectionEmpty() {
        if (!delegate.isSelectionEmpty()) {
            int unMinIndex = delegate.getMinSelectionIndex();
            int unMaxIndex = delegate.getMaxSelectionIndex();
            if ((unMinIndex >= constraint.getMin() && unMinIndex <= constraint.getMax())
                    || (unMaxIndex >= constraint.getMin() && unMaxIndex <= constraint.getMax())) {
                return false;
            } else {
                if ((unMinIndex < constraint.getMin() && unMaxIndex > constraint.getMax())) {
                    for (int i = constraint.getMin(); i <= Math.min(constraint.getMax(), delegate.getMaxSelectionIndex()); i++) {
                        if (delegate.isSelectedIndex(i)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    public void addListSelectionListener(ListSelectionListener l) {
        listeners.add(l);
    }

    public void removeListSelectionListener(ListSelectionListener l) {
        listeners.remove(l);
    }

    public void fireValueChanged(boolean aAdjusting) {
        ListSelectionEvent event = new ListSelectionEvent(this, MIN, MAX, aAdjusting);
        for (ListSelectionListener l : listeners) {
            l.valueChanged(event);
        }
    }

    protected void fireValueChanged(int firstIndex, int lastIndex) {
        fireValueChanged(firstIndex, lastIndex, false);
    }

    protected void fireValueChanged(int firstIndex, int lastIndex, boolean aAdjusting) {
        ListSelectionEvent event = new ListSelectionEvent(this, firstIndex, lastIndex, aAdjusting);
        for (ListSelectionListener l : listeners) {
            l.valueChanged(event);
        }
    }
}
