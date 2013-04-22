/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.label.DbLabel;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a standard form editor wrapper for model-aware
 * non-visual component - DbGridColumn.
 *
 * @author mg
 * @see RADModelMapLayer
 */
public class RADModelGridColumn extends RADComponent<DbGridColumn> implements ComponentContainer {

    public interface ValueHostListener extends ModelControlListener {

        public Object onSelect(Object aField);

        public Object onRender(Object evt);
    }
    /*
    public interface ValueHostListener extends ModelControlListener {

        public Object onSelect(Object aField);

        public Object onRender(Object aRowId, Object aColumnId, Object aCell, Object aRow);
    }
    */ 
    protected RADColumnView<? super DbControlPanel> viewControl;
    protected List<RADModelGridColumn> columns = new ArrayList<>();

    public RADModelGridColumn() {
        super();
    }

    @Override
    public boolean initialize(FormModel aFormModel) {
        viewControl = new RADColumnView<>();
        viewControl.initialize(aFormModel);
        viewControl.setInstance(new DbLabel());
        return super.initialize(aFormModel);
    }

    public RADColumnView<? super DbControlPanel> getViewControl() {
        return viewControl;
    }

    public void setViewControl(RADColumnView<? super DbControlPanel> aValue) {
        viewControl = aValue;
    }

    public RADModelGrid lookupGrid() {
        ComponentContainer parent = getParent();
        while (parent != null && parent instanceof RADComponent<?>) {
            if (parent instanceof RADModelGrid) {
                return (RADModelGrid) parent;
            }
            parent = ((RADComponent<?>) parent).getParent();
        }
        return null;
    }

    public void fireRawColumnsChanged() {
        RADModelGrid grid = lookupGrid();
        if (grid != null) {
            grid.fireRawColumnsChanged();
        }
    }

    @Override
    protected EventSetDescriptor[] getEventSetDescriptors() {
        try {
            List<EventSetDescriptor> descs = new ArrayList<>();
            descs.addAll(Arrays.asList(super.getEventSetDescriptors()));
            descs.add(new EventSetDescriptor("value", ValueHostListener.class, ValueHostListener.class.getMethods(), null, null));
            return descs.toArray(new EventSetDescriptor[]{});
        } catch (IntrospectionException ex) {
            Logger.getLogger(RADModelGridColumn.class.getName()).log(Level.SEVERE, null, ex);
            return super.getEventSetDescriptors();
        }
    }

    @Override
    public void setStoredName(String name) {
        super.setStoredName(name);
        if (getBeanInstance() != null) {
            getBeanInstance().setName(name);
        }
    }

    @Override
    protected void setBeanInstance(DbGridColumn aBeanInstance) {
        //DbGridColumn oldColumn = getBeanInstance();
        super.setBeanInstance(aBeanInstance);
        if (getBeanInstance() != null) {
            getBeanInstance().setName(getName());
        }
        //DbGridColumn newColumn = getBeanInstance();
    }

    @Override
    public RADComponent<?>[] getSubBeans() {
        return columns.toArray(new RADComponent<?>[]{});
    }

    @Override
    public void initSubComponents(RADComponent<?>[] initComponents) {
        columns.clear();
        getBeanInstance().getChildren().clear();
        for (int i = 0; i < initComponents.length; i++) {
            if (initComponents[i] instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) initComponents[i];
                radColumn.setParent(this);
                columns.add(radColumn);
                getBeanInstance().getChildren().add(radColumn.getBeanInstance());
                radColumn.getBeanInstance().setParent(getBeanInstance());
            }
        }
        fireRawColumnsChanged();
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        RADModelGridColumn[] oldColumns = columns.toArray(new RADModelGridColumn[]{});
        DbGridColumn[] oldRawColumns = getBeanInstance().getChildren().toArray(new DbGridColumn[]{});
        assert perm.length == oldColumns.length;
        assert perm.length == oldRawColumns.length;
        for (int i = 0; i < columns.size(); i++) {
            columns.set(perm[i], oldColumns[i]);
        }
        for (int i = 0; i < getBeanInstance().getChildren().size(); i++) {
            getBeanInstance().getChildren().set(perm[i], oldRawColumns[i]);
        }
        fireRawColumnsChanged();
    }

    @Override
    public void add(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            RADModelGridColumn radColumn = (RADModelGridColumn) comp;
            // TODO: check self-addition
            columns.add(radColumn);
            if (radColumn.isInModel()) {
                getBeanInstance().getChildren().add(radColumn.getBeanInstance());
                radColumn.getBeanInstance().setParent(getBeanInstance());
            }
            fireRawColumnsChanged();
        }
    }

    @Override
    public void remove(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            RADModelGridColumn radColumn = (RADModelGridColumn) comp;
            columns.remove(radColumn);
            getBeanInstance().getChildren().remove(radColumn.getBeanInstance());
            radColumn.getBeanInstance().setParent(null);
            fireRawColumnsChanged();
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return columns.indexOf(comp);
    }
}
