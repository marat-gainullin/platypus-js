/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a standard form editor wrapper for model-aware
 * non-visual component - GridColumnsGroup.
 *
 * @author mg
 */
public class RADModelGridColumn extends RADComponent<GridColumnsGroup> implements ComponentContainer {

    protected RADColumnView<? super ModelComponentDecorator> viewControl;
    protected List<RADModelGridColumn> columns = new ArrayList<>();

    public RADModelGridColumn() {
        super();
    }

    @Override
    public boolean initialize(FormModel aFormModel) {
        viewControl = new RADColumnView<>();
        viewControl.initialize(aFormModel);
        viewControl.setInstance(new ModelFormattedField());
        viewControl.setInModel(false);
        return super.initialize(aFormModel);
    }

    public RADColumnView<? super ModelComponentDecorator> getViewControl() {
        return viewControl;
    }

    public void setViewControl(RADColumnView<? super ModelComponentDecorator> aValue) {
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

    public void resetGridColumnsAndHeader() {
        RADModelGrid grid = lookupGrid();
        if (grid != null) {
            grid.resetBeanColumnsAndHeader();
        }
    }

    @Override
    public void setStoredName(String name) {
        super.setStoredName(name);
        if (getBeanInstance() != null) {
            ((ModelColumn) getBeanInstance().getTableColumn()).setName(name);
            if (getBeanInstance().getTitle() == null) {
                getBeanInstance().setTitle(getName());
            }
        }
    }

    @Override
    protected void setBeanInstance(GridColumnsGroup aBeanInstance) {
        super.setBeanInstance(aBeanInstance);
        if (getBeanInstance() != null) {
            if (getBeanInstance().getTableColumn() == null) {
                getBeanInstance().setTableColumn(new ModelColumn());
            }
            ((ModelColumn) getBeanInstance().getTableColumn()).setName(getName());
            getBeanInstance().setTitle(getName());
        }
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
        resetGridColumnsAndHeader();
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        RADModelGridColumn[] oldColumns = columns.toArray(new RADModelGridColumn[]{});
        GridColumnsGroup[] oldRawColumns = getBeanInstance().getChildren().toArray(new GridColumnsGroup[]{});
        assert perm.length == oldColumns.length;
        assert perm.length == oldRawColumns.length;
        for (int i = 0; i < columns.size(); i++) {
            columns.set(perm[i], oldColumns[i]);
        }
        for (int i = 0; i < getBeanInstance().getChildren().size(); i++) {
            getBeanInstance().getChildren().set(perm[i], oldRawColumns[i]);
        }
        resetGridColumnsAndHeader();
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
            resetGridColumnsAndHeader();
        }
    }

    @Override
    public void remove(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            RADModelGridColumn radColumn = (RADModelGridColumn) comp;
            columns.remove(radColumn);
            getBeanInstance().getChildren().remove(radColumn.getBeanInstance());
            radColumn.getBeanInstance().setParent(null);
            resetGridColumnsAndHeader();
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return columns.indexOf(comp);
    }
}
