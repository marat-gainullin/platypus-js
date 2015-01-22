/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 * This class represents a standard form editor wrapper for model-aware
 * non-visual component - GridColumnsGroup.
 *
 * @author mg
 */
public class RADModelGridColumn extends RADComponent<GridColumnsNode> implements ComponentContainer {

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

    @Override
    protected RADProperty<?> createCheckedBeanProperty(PropertyDescriptor desc) throws InvocationTargetException, IllegalAccessException {
        if ("field".equals(desc.getName())) {
            return new EntityJSObjectFieldProperty(this, desc, "");
        } else {
            return super.createCheckedBeanProperty(desc);
        }
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

    public void resetGridColumnsAndHeader() throws Exception {
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
    public void setBeanInstance(GridColumnsNode aBeanInstance) {
        super.setBeanInstance(aBeanInstance);
        if (getBeanInstance() != null) {
            ((ModelColumn) getBeanInstance().getTableColumn()).setName(getName());
            if (getBeanInstance().getTitle() == null || getBeanInstance().getTitle().isEmpty()) {
                getBeanInstance().setTitle(getName());
            }
        }
    }

    @Override
    public RADComponent<?>[] getSubBeans() {
        return columns.toArray(new RADComponent<?>[]{});
    }

    @Override
    public int getSubBeansCount() {
        return columns.size();
    }

    @Override
    public void initSubComponents(RADComponent<?>[] initComponents) {
        try {
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
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        try {
            RADModelGridColumn[] oldColumns = columns.toArray(new RADModelGridColumn[]{});
            GridColumnsNode[] oldRawColumns = getBeanInstance().getChildren().toArray(new GridColumnsNode[]{});
            assert perm.length == oldColumns.length;
            assert perm.length == oldRawColumns.length;
            for (int i = 0; i < columns.size(); i++) {
                columns.set(perm[i], oldColumns[i]);
            }
            for (int i = 0; i < getBeanInstance().getChildren().size(); i++) {
                getBeanInstance().getChildren().set(perm[i], oldRawColumns[i]);
            }
            resetGridColumnsAndHeader();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void add(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            try {
                RADModelGridColumn radColumn = (RADModelGridColumn) comp;
                // TODO: check self-addition
                columns.add(radColumn);
                if (radColumn.isInModel()) {
                    getBeanInstance().getChildren().add(radColumn.getBeanInstance());
                    radColumn.getBeanInstance().setParent(getBeanInstance());
                }
                resetGridColumnsAndHeader();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void remove(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            try {
                RADModelGridColumn radColumn = (RADModelGridColumn) comp;
                columns.remove(radColumn);
                getBeanInstance().getChildren().remove(radColumn.getBeanInstance());
                radColumn.getBeanInstance().setParent(null);
                resetGridColumnsAndHeader();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return columns.indexOf(comp);
    }
}
