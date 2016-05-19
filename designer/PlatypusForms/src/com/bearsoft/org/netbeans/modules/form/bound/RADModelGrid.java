/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelDate;
import com.eas.client.forms.components.model.ModelSpin;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.header.ModelGridColumn;
import com.eas.client.forms.components.model.grid.header.ServiceGridColumn;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.module.EntityJSObject;
import com.eas.script.Scripts;
import com.eas.util.StringUtils;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class RADModelGrid extends RADVisualComponent<ModelGrid> implements ComponentContainer {

    protected Map<String, RADModelGridColumn> deepColumns = new HashMap<>();
    protected List<RADModelGridColumn> columns = new ArrayList<>();
    protected boolean fireRawColumnsChanges = true;

    private static void scanLeaves(List<ModelColumn> beanColumns, RADComponent<?>[] aColumns) {
        for (RADComponent<?> designColumn : aColumns) {
            if (!((RADModelGridColumn) designColumn).getBeanInstance().hasChildren()) {
                beanColumns.add((ModelColumn) ((RADModelGridColumn) designColumn).getBeanInstance().getTableColumn());
            } else {
                scanLeaves(beanColumns, ((RADModelGridColumn) designColumn).getSubBeans());
            }
        }
    }

    @Override
    protected void setBeanInstance(ModelGrid aBeanInstance) {
        ModelGrid bean = super.getBeanInstance();
        if (bean != null) {
            bean.setData(null);
        }
        super.setBeanInstance(aBeanInstance);
    }

    public void resetBeanColumnsAndHeader() throws Exception {
        if (fireRawColumnsChanges) {
            List<ModelColumn> beanColumns = new ArrayList<>();
            scanLeaves(beanColumns, columns.toArray(new RADModelGridColumn[]{}));
            getBeanInstance().setColumns(beanColumns.toArray(new ModelColumn[]{}));
            List<GridColumnsNode> headerRoots = new ArrayList<>();
            columns.stream().forEach((RADModelGridColumn aDesignColumn) -> {
                headerRoots.add(aDesignColumn.getBeanInstance());
            });
            getBeanInstance().setHeader(headerRoots);
        }
    }

    @Override
    public ModelGrid cloneBeanInstance(Collection<RADProperty<?>> relativeProperties) {
        try {
            ModelGrid clonedGrid = super.cloneBeanInstance(relativeProperties);
            ModelColumn[] targetColumns = new ModelColumn[getBeanInstance().getColumnModel().getColumnCount()];
            for (int i = 0; i < targetColumns.length; i++) {
                ModelColumn sourceCol = (ModelColumn) getBeanInstance().getColumnModel().getColumn(i);
                targetColumns[i] = sourceCol;
            }
            clonedGrid.setColumns(targetColumns);
            clonedGrid.setHeader(getBeanInstance().getHeader());
            clonedGrid.setData(getBeanInstance().getData());
            return clonedGrid;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
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
            columns.forEach((RADModelGridColumn col) -> {
                unregister(col);
            });
            columns.clear();
            getBeanInstance().getHeader().clear();
            for (int i = 0; i < initComponents.length; i++) {
                if (initComponents[i] instanceof RADModelGridColumn) {
                    RADModelGridColumn radColumn = (RADModelGridColumn) initComponents[i];
                    radColumn.setParent(this);
                    columns.add(radColumn);
                    register(radColumn);
                }
            }
            resetBeanColumnsAndHeader();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        try {
            ModelGrid modelGrid = getBeanInstance();
            RADModelGridColumn[] oldColumns = columns.toArray(new RADModelGridColumn[]{});
            GridColumnsNode[] oldRawColumns = modelGrid.getHeader().toArray(new GridColumnsNode[]{});
            assert perm.length == oldColumns.length;
            assert perm.length == oldRawColumns.length;
            for (int i = 0; i < columns.size(); i++) {
                columns.set(perm[i], oldColumns[i]);
            }
            resetBeanColumnsAndHeader();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void add(RADComponent<?> comp) {
        try {
            if (comp instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) comp;
                columns.add(radColumn);
                register(radColumn);
            }
            resetBeanColumnsAndHeader();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void remove(RADComponent<?> comp) {
        try {
            if (comp instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) comp;
                columns.remove(radColumn);
                unregister(radColumn);
            }
            resetBeanColumnsAndHeader();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return columns.indexOf(comp);
    }

    public boolean isFireRawColumnsChanges() {
        return fireRawColumnsChanges;
    }

    public void setFireRawColumnsChanges(boolean aValue) {
        fireRawColumnsChanges = aValue;
    }

    @Override
    protected RADProperty<?> createCheckedBeanProperty(PropertyDescriptor desc) throws InvocationTargetException, IllegalAccessException {
        if ("parentField".equals(desc.getName()) || "childrenField".equals(desc.getName())) {
            return new EntityJSObjectFieldProperty(this, desc, "");
        } else {
            return super.createCheckedBeanProperty(desc);
        }
    }

    public void fillColumns() throws Exception {
        EntityJSObject jsEntity = (EntityJSObject) getBeanInstance().getData();
        if (jsEntity != null) {
            ApplicationDbModel model = jsEntity.getEntity().getModel();
            if (model != null) {
                ApplicationDbEntity rowsEntity = jsEntity.getEntity();
                if (rowsEntity != null && rowsEntity.getFields() != null && rowsEntity.getFields().getFieldsCount() > 0) {
                    Fields fields = rowsEntity.getFields();
                    int rowsetColumnsCount = fields.getFieldsCount();
                    fireRawColumnsChanges = false;
                    try {
                        RADModelGridColumn serviceRadColumn = new RADModelGridColumn();
                        serviceRadColumn.initialize(getFormModel());
                        ServiceGridColumn serviceColumn = new ServiceGridColumn();
                        serviceRadColumn.setBeanInstance(serviceColumn);
                        serviceRadColumn.setStoredName(findFreeColumnName("colService"));
                        getFormModel().addComponent(serviceRadColumn, this, true);
                        for (int i = 1; i <= rowsetColumnsCount; i++) {
                            Field columnField = fields.get(i);
                            if (columnField.getType() != null) {
                                RADModelGridColumn radColumn = new RADModelGridColumn();
                                radColumn.initialize(getFormModel());
                                ModelGridColumn column = new ModelGridColumn();
                                radColumn.setBeanInstance(column);
                                String colBaseName = (columnField.getName() != null && !columnField.getName().isEmpty()) ? columnField.getName() : "Column";
                                colBaseName = "col" + StringUtils.capitalize(colBaseName);
                                radColumn.setStoredName(findFreeColumnName(colBaseName));

                                int lwidth = 50;
                                if (lwidth >= column.getWidth()) {
                                    column.setWidth(lwidth);
                                }
                                String description = columnField.getDescription();
                                if (description != null && !description.isEmpty()) {
                                    column.setTitle(description);
                                }
                                column.setField(columnField.getName());
                                switch (columnField.getType()) {
                                    case Scripts.NUMBER_TYPE_NAME: {
                                        ModelSpin editor = new ModelSpin();
                                        editor.setMin(-Double.MAX_VALUE);
                                        editor.setMax(Double.MAX_VALUE);
                                        radColumn.getViewControl().setInstance(editor);
                                    }
                                    break;
                                    case Scripts.BOOLEAN_TYPE_NAME:
                                        radColumn.getViewControl().setInstance(new ModelCheckBox());
                                        break;
                                    case Scripts.DATE_TYPE_NAME: {
                                        ModelDate editor = new ModelDate();
                                        editor.setFormat("dd.MM.yyyy HH:mm:ss.SSS");
                                        radColumn.getViewControl().setInstance(editor);
                                    }
                                    break;
                                    default:
                                        // ModelFormattedField already installed in the column's view
                                        break;
                                }
                                getFormModel().addComponent(radColumn, this, true);
                            }
                        }
                    } finally {
                        fireRawColumnsChanges = true;
                        resetBeanColumnsAndHeader();
                    }
                }
            }
        }
    }

    public void register(RADModelGridColumn aColumn) {
        if (isInModel() && deepColumns.containsKey(aColumn.getName())) {
            String newName = findFreeColumnName(aColumn.getName());
            aColumn.setStoredName(newName);
        }
        deepColumns.put(aColumn.getName(), aColumn);
        aColumn.columns.forEach((RADModelGridColumn col) -> {
            register(col);
        });
    }

    public void unregister(RADModelGridColumn aColumn) {
        deepColumns.remove(aColumn.getName());
        aColumn.columns.forEach((RADModelGridColumn col) -> {
            unregister(col);
        });
    }

    public RADModelGridColumn findColumn(String aColumnName) {
        return deepColumns.get(aColumnName);
    }

    public String findFreeColumnName(String baseName) {
        baseName = Introspector.decapitalize(baseName);
        RADModelGridColumn column = deepColumns.get(baseName);
        int counter = 0;
        String generatedName = baseName;
        while (column != null) {
            counter++;
            generatedName = baseName + counter;
            column = deepColumns.get(generatedName);
        }
        return generatedName;
    }
}
