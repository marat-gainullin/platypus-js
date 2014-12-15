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
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mg
 */
public class RADModelGrid extends RADVisualComponent<ModelGrid> implements ComponentContainer {

    protected List<RADModelGridColumn> columns = new ArrayList<>();
    protected boolean fireRawColumnsChanges = true;

    private static void scanLeaves(List<ModelColumn> beanColumns, RADComponent<?>[] aColumns) {
        for (RADComponent<?> designColumn : aColumns) {
            if (!((RADModelGridColumn)designColumn).getBeanInstance().hasChildren()) {
                beanColumns.add((ModelColumn) ((RADModelGridColumn)designColumn).getBeanInstance().getTableColumn());
            } else {
                scanLeaves(beanColumns, ((RADModelGridColumn)designColumn).getSubBeans());
            }
        }
    }

    @Override
    protected void setBeanInstance(ModelGrid aBeanInstance) {
        ModelGrid bean = super.getBeanInstance();
        if(bean != null)
            bean.setData(null);
        super.setBeanInstance(aBeanInstance);
        bean = super.getBeanInstance();
        if(bean != null)
            bean.setData(new GridDataArray());
    }

    public void resetBeanColumnsAndHeader() {
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
    }

    @Override
    public RADComponent<?>[] getSubBeans() {
        return columns.toArray(new RADComponent<?>[]{});
    }

    @Override
    public void initSubComponents(RADComponent<?>[] initComponents) {
        columns.clear();
        getBeanInstance().getHeader().clear();
        for (int i = 0; i < initComponents.length; i++) {
            if (initComponents[i] instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) initComponents[i];
                radColumn.setParent(this);
                columns.add(radColumn);
            }
        }
        resetBeanColumnsAndHeader();
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        ModelGrid modelGrid = getBeanInstance();
        RADModelGridColumn[] oldColumns = columns.toArray(new RADModelGridColumn[]{});
        GridColumnsNode[] oldRawColumns = modelGrid.getHeader().toArray(new GridColumnsNode[]{});
        assert perm.length == oldColumns.length;
        assert perm.length == oldRawColumns.length;
        for (int i = 0; i < columns.size(); i++) {
            columns.set(perm[i], oldColumns[i]);
        }
        resetBeanColumnsAndHeader();
    }

    @Override
    public void add(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            RADModelGridColumn radColumn = (RADModelGridColumn) comp;
            columns.add(radColumn);
        }
        resetBeanColumnsAndHeader();
    }

    @Override
    public void remove(RADComponent<?> comp) {
        if (comp instanceof RADModelGridColumn) {
            RADModelGridColumn radColumn = (RADModelGridColumn) comp;
            columns.remove(radColumn);
        }
        resetBeanColumnsAndHeader();
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

    public void fillColumns() throws Exception {
        /*
         ApplicationDbModel model = (ApplicationDbModel)getBeanInstance().getModel();
         if (model != null) {
         if (getBeanInstance().getRowsDatasource() != null
         && getBeanInstance().getRowsDatasource().getEntityId() != null) {
         Long rowsEntityId = getBeanInstance().getRowsDatasource().getEntityId();
         ApplicationDbEntity rowsEntity = model.getEntityById(rowsEntityId);
         if (rowsEntity != null && rowsEntity.getFields() != null && rowsEntity.getFields().getFieldsCount() > 0) {
         Fields fields = rowsEntity.getFields();
         int rowsetColumnsCount = fields.getFieldsCount();
         fireRawColumnsChanges = false;
         try {
         for (int i = 1; i <= rowsetColumnsCount; i++) {
         Field columnField = fields.get(i);
         RADModelGridColumn radColumn = new RADModelGridColumn();
         radColumn.initialize(getFormModel());
         radColumn.initInstance(DbGridColumn.class);
         String colBaseName = (columnField.getName() != null && !columnField.getName().isEmpty()) ? columnField.getName() : "Column";
         colBaseName = "col"+StringUtils.capitalize(colBaseName);
         radColumn.setStoredName(getFormModel().findFreeComponentName(colBaseName));
         DbGridColumn column = radColumn.getBeanInstance();

         int lwidth = 50;
         if (lwidth >= column.getWidth()) {
         column.setWidth(lwidth);
         }
         String description = columnField.getDescription();
         if (description != null && !description.isEmpty()) {
         column.setTitle(description);
         }
         ModelElementRef fieldRef = new ModelElementRef();
         fieldRef.setEntityId(rowsEntity.getEntityId());
         fieldRef.setFieldName(columnField.getName());
         column.setDatamodelElement(fieldRef);
         Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(columnField.getTypeInfo().getSqlType());
         if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
         Class<?> lControlClass = compatibleControlsClasses[0];
         if (lControlClass != null) {
         Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
         if (infoClass != null) {
         try {
         DbControlDesignInfo cdi = (DbControlDesignInfo) infoClass.newInstance();
         cdi.setDatamodelElement(fieldRef);
         if (cdi instanceof DbDateDesignInfo) {
         DbDateDesignInfo dateDi = (DbDateDesignInfo) cdi;
         dateDi.setDateFormat(DbDate.DD_MM_YYYY_HH_MM_SS);
         }
         DbSwingFactory factory = new DbSwingFactory();
         cdi.accept(factory);
         assert factory.getComp() instanceof DbControlPanel;
         radColumn.getViewControl().setInstance((DbControlPanel) factory.getComp());
         } catch (InstantiationException | IllegalAccessException ex) {
         Logger.getLogger(RADModelGrid.class.getName()).log(Level.SEVERE, null, ex);
         }
         }
         }
         }
         getFormModel().addComponent(radColumn, this, true);
         }
         } finally {
         fireRawColumnsChanges = true;
         resetBeanColumnsAndHeader();
         }
         }
         }
         }
         */
    }
}
