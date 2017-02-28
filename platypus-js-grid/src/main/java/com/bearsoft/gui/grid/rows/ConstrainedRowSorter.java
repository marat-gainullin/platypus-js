/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.events.constraints.ConstraintChangeListener;
import com.bearsoft.gui.grid.events.constraints.ConstraintMaximumChangedEvent;
import com.bearsoft.gui.grid.events.constraints.ConstraintMinimumChangedEvent;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public class ConstrainedRowSorter<M extends TableModel> extends DelegatingRowSorter<M> {

    protected class DelegateListener implements RowSorterListener {

        @Override
        public void sorterChanged(RowSorterEvent e) {
            fireRowSorterChanged(null);
        }
    }

    protected class ConstraintListener implements ConstraintChangeListener {

        @Override
        public void constraintMinimumChanged(ConstraintMinimumChangedEvent anEvent) {
            fireRowSorterChanged(null);
        }

        @Override
        public void constraintMaximumChanged(ConstraintMaximumChangedEvent anEvent) {
            fireRowSorterChanged(null);
        }
    }
    protected LinearConstraint constraint;

    /**
     * Universal constructor of constrainting row sorter.
     * @param aDelegate Delegate row sorter, all significant work to be delegated to. It may be null.
     * @param aModel Table model to be used while <code>aDelegate</code> is null.
     * @param aConstraint <code>LinearConstraint</code> instance, used to constraint delegate row sorter or table model.
     */
    protected ConstrainedRowSorter(RowSorter<M> aDelegate, M aModel, LinearConstraint aConstraint) {
        super(aDelegate, aModel);
        constraint = aConstraint;
        if (delegate != null) {
            delegate.addRowSorterListener(new DelegateListener());
        }
        assert constraint != null : "Constraint is required for constructing ConstraintedRowSorter.";
        constraint.addConstraintChangeListener(new ConstraintListener());
    }

    /**
     * Constructor with the row sorter delegate.
     * @param aDelegate Delegate row sorter, all significant work to be delegated to. It may be null.
     * @param aConstraint <code>LinearConstraint</code> instance, used to constraint delegate row sorter or table model.
     */
    public ConstrainedRowSorter(RowSorter<M> aDelegate, LinearConstraint aConstraint) {
        this(aDelegate, null, aConstraint);
    }

    /**
     * Constructor with the table model to be constrainted.
     * @param aModel Table model to be used while <code>aDelegate</code> is null.
     * @param aConstraint <code>LinearConstraint</code> instance, used to constraint delegate row sorter or table model.
     */
    public ConstrainedRowSorter(M aModel, LinearConstraint aConstraint) {
        this(null, aModel, aConstraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int convertRowIndexToModel(int vIndex) {
        if (delegate != null) {
            return delegate.convertRowIndexToModel(constraint.unconstraint(vIndex));
        } else {
            return constraint.unconstraint(vIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int convertRowIndexToView(int mIndex) {
        if (delegate != null) {
            return constraint.constraint(delegate.convertRowIndexToView(mIndex));
        } else {
            return constraint.constraint(mIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getViewRowCount() {
        int vMin = Math.max(0, constraint.getMin());
        if (delegate != null) {
            int vMax = Math.min(delegate.getViewRowCount() - 1, constraint.getMax());
            return vMax - vMin + 1;
        } else {
            int vMax = Math.min(model.getRowCount() - 1, constraint.getMax());
            return vMax - vMin + 1;
        }
    }
}
