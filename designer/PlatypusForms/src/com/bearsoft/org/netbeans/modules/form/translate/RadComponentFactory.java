/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.RADButtonGroup;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelMap;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelMapLayer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelScalarComponent;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.containers.*;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import com.eas.dbcontrols.visitors.DbControlClassFinder;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;

/**
 * Factory class, intended to create appropriate
 * <code>RADComponent</code> instance for each
 * <code>DbControlDesignInfo</code>. Factory also responsible for initializing
 * <code>RADComponent</code>'s properties and events.
 *
 * @author mg
 */
public class RadComponentFactory implements DbControlsDesignInfoVisitor {

    private static void turnNonDefaultProperties(RADComponent<?> aComponent) {
        for (RADProperty<?> prop : aComponent.getAllBeanProperties()) {
            if (!prop.isDefaultValue() && !prop.isHidden()) {
                prop.setChanged(true);
            }
        }
    }
    protected FormModel model;
    protected RADComponent<?> result;
    protected List<Throwable> errors;

    public RadComponentFactory(FormModel aModel) {
        super();
        model = aModel;
    }

    public RADComponent<?> getResult() {
        return result;
    }

    protected void initRADComponent(ControlDesignInfo dcdi) {
        try {
            result.initialize(model);
            result.setStoredName(dcdi.getName());
            DbControlClassFinder clsFinder = new DbControlClassFinder();
            dcdi.accept(clsFinder);
            result.initInstance(clsFinder.getResult());
            result.setInModel(true);
        } catch (Exception ex) {
            errors.add(ex);
        }
    }

    protected void visitVisualComponent(ControlDesignInfo dcdi) {
        result = new RADVisualComponent<>();
        initRADComponent(dcdi);
    }

    protected void visitScalarModelComponent(ControlDesignInfo dcdi) {
        result = new RADModelScalarComponent<>();
        initRADComponent(dcdi);
    }

    protected void visitNonVisualComponent(ControlDesignInfo dcdi) {
        result = new RADVisualComponent<>();
        initRADComponent(dcdi);
    }

    protected void visitVisualContainer(ContainerDesignInfo dcdi) {
        result = new RADVisualContainer<>();
        initRADComponent(dcdi);
    }

    protected void visitVisualMenuComponent(MenuItemDesignInfo dcdi) {
        result = new RADVisualContainer<>();
        initRADComponent(dcdi);
    }

    @Override
    public void visit(DbCheckDesignInfo dcdi) {
        visitScalarModelComponent(dcdi);
    }

    @Override
    public void visit(DbComboDesignInfo dcdi) {
        visitScalarModelComponent(dcdi);
    }

    @Override
    public void visit(DbDateDesignInfo dddi) {
        visitScalarModelComponent(dddi);
    }

    @Override
    public void visit(DbImageDesignInfo didi) {
        visitScalarModelComponent(didi);
    }

    @Override
    public void visit(DbLabelDesignInfo dldi) {
        visitScalarModelComponent(dldi);
    }

    @Override
    public void visit(DbSchemeDesignInfo dsdi) {
        visitScalarModelComponent(dsdi);
    }

    @Override
    public void visit(DbSpinDesignInfo dsdi) {
        visitScalarModelComponent(dsdi);
    }

    @Override
    public void visit(DbTextDesignInfo dtdi) {
        visitScalarModelComponent(dtdi);
    }

    @Override
    public void visit(DbGridDesignInfo dgdi) {
        result = new RADModelGrid();
        initRADComponent(dgdi);
        RADModelGrid radGrid = (RADModelGrid) result;
        radGrid.setFireRawColumnsChanges(false);
        try {
            for (DbGridColumn column : dgdi.getHeader()) {
                try {
                    initGridColumn(model, column, null, radGrid);
                } catch (Exception ex) {
                    Logger.getLogger(RadComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            radGrid.setFireRawColumnsChanges(true);
        }
        radGrid.fireRawColumnsChanged();
    }

    public static void initGridColumn(FormModel aFormModel, DbGridColumn aColumn, RADModelGridColumn aRADParentColumn, RADModelGrid aRADParentComponent) throws Exception {
        if (aColumn.getName() == null || aColumn.getName().isEmpty()) {
            aColumn.setName(aFormModel.findFreeComponentName("column"));
        }
        if (aColumn.getName() != null && !aColumn.getName().isEmpty()) {
            RADModelGridColumn column = new RADModelGridColumn();
            column.initialize(aFormModel);
            column.initInstance(DbGridColumn.class);
            turnNonDefaultProperties(column);
            column.setStoredName(aColumn.getName());
            column.setInstance(aColumn);

            if (aColumn.getControlInfo() != null || aColumn.getCellDesignInfo().getCellControlInfo() != null) {
                DbSwingFactory factory = new DbSwingFactory();
                DbControlDesignInfo dbDesignInfo;
                // column view
                if (aColumn.isVeer()) {
                    dbDesignInfo = aColumn.getCellDesignInfo().getCellControlInfo();
                } else {
                    dbDesignInfo = aColumn.getControlInfo();
                }
                dbDesignInfo.accept(factory);
                assert factory.getComp() instanceof DbControlPanel;
                column.getViewControl().setInstance((DbControlPanel) factory.getComp());
                if (dbDesignInfo instanceof DbLabelDesignInfo) {
                    DbLabelDesignInfo ldi = (DbLabelDesignInfo) dbDesignInfo;
                    if (ldi.getFormat() != null) {
                        RADProperty<Object> radProp = column.getViewControl().getProperty(RADComponent.FormatterFactoryProperty.FORMATTER_FACTORY_PROP_NAME);
                        assert radProp != null;
                        radProp.setValue(AbstractFormatterFactoryEditor.FormFormatter.valueOf(ldi.getFormat(), ldi.getValueType()));
                    }
                }

            }
            for (DbGridColumn childColumn : aColumn.getChildren()) {
                initGridColumn(aFormModel, childColumn, column, null);
            }

            if (aRADParentColumn != null) {
                column.setParent(aRADParentColumn);
                aRADParentColumn.add(column);
                column.setInModel(true);
            } else if (aRADParentComponent != null) {
                column.setParent(aRADParentComponent);
                column.setInModel(true);
                aRADParentComponent.add(column);
            } else {
                aFormModel.addComponent(column, null, false);
            }
        }
    }

    @Override
    public void visit(DbMapDesignInfo dmdi) {
        result = new RADModelMap();
        initRADComponent(dmdi);
        RADModelMap radMap = (RADModelMap) result;
        for (RowsetFeatureDescriptor layer : dmdi.getFeatures()) {
            try {
                initMapLayer(model, layer, radMap);
            } catch (Exception ex) {
                Logger.getLogger(RadComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void initMapLayer(FormModel aFormModel, RowsetFeatureDescriptor aLayer, RADModelMap aParentComponent) throws Exception {
        if (aLayer.getTypeName() != null && !aLayer.getTypeName().isEmpty()) {
            RADModelMapLayer layer = new RADModelMapLayer();
            layer.initialize(aFormModel);
            layer.initInstance(RowsetFeatureDescriptor.class);
            layer.setStoredName(aLayer.getTypeName());
            layer.setInstance(aLayer);
            turnNonDefaultProperties(layer);
            if (aParentComponent != null) {
                layer.setParent(aParentComponent);
                layer.setInModel(true);
                aParentComponent.add(layer);
            } else {
                aFormModel.addComponent(layer, null, false);
            }
        }
    }

    @Override
    public void visit(LabelDesignInfo ldi) {
        visitVisualComponent(ldi);
    }

    @Override
    public void visit(ButtonDesignInfo bdi) {
        visitVisualComponent(bdi);
    }

    @Override
    public void visit(DropDownButtonDesignInfo bdi) {
        visitVisualComponent(bdi);
    }

    @Override
    public void visit(ButtonGroupDesignInfo bgdi) {
        try {
            result = new RADButtonGroup();
            result.initialize(model);
            result.setStoredName(bgdi.getName());
            ((RADButtonGroup) result).initInstance(ButtonGroup.class);
            result.setInModel(true);
        } catch (Exception ex) {
            errors.add(ex);
        }
    }

    @Override
    public void visit(CheckDesignInfo cdi) {
        visitVisualComponent(cdi);
    }

    @Override
    public void visit(TextPaneDesignInfo epdi) {
        visitVisualComponent(epdi);
    }

    @Override
    public void visit(EditorPaneDesignInfo epdi) {
        visitVisualComponent(epdi);
    }

    @Override
    public void visit(FormattedFieldDesignInfo ffdi) {
        visitVisualComponent(ffdi);
    }

    @Override
    public void visit(PasswordFieldDesignInfo pfdi) {
        visitVisualComponent(pfdi);
    }

    @Override
    public void visit(ProgressBarDesignInfo pbdi) {
        visitVisualComponent(pbdi);
    }

    @Override
    public void visit(RadioDesignInfo rdi) {
        visitVisualComponent(rdi);
    }

    @Override
    public void visit(SliderDesignInfo sdi) {
        visitVisualComponent(sdi);
    }

    @Override
    public void visit(TextFieldDesignInfo tfdi) {
        visitVisualComponent(tfdi);
    }

    @Override
    public void visit(ToggleButtonDesignInfo tbdi) {
        visitVisualComponent(tbdi);
    }

    @Override
    public void visit(FormDesignInfo fdi) {
        throw new UnsupportedOperationException("Not supported ever. Form top-level containers are created outside this visitor");
    }

    @Override
    public void visit(DesktopDesignInfo ddi) {
        visitVisualContainer(ddi);
    }

    @Override
    public void visit(LayersDesignInfo ldi) {
        visitVisualContainer(ldi);
    }

    @Override
    public void visit(PanelDesignInfo pdi) {
        visitVisualContainer(pdi);
    }

    @Override
    public void visit(ScrollDesignInfo sdi) {
        visitVisualContainer(sdi);
    }

    @Override
    public void visit(SplitDesignInfo sdi) {
        visitVisualContainer(sdi);
    }

    @Override
    public void visit(TabsDesignInfo tdi) {
        visitVisualContainer(tdi);
    }

    @Override
    public void visit(ToolbarDesignInfo tdi) {
        visitVisualContainer(tdi);
    }

    @Override
    public void visit(MenubarDesignInfo mdi) {
        visitVisualContainer(mdi);
    }

    @Override
    public void visit(MenuDesignInfo mdi) {
        result = new RADVisualContainer<>();
        initRADComponent(mdi);
    }

    @Override
    public void visit(MenuCheckItemDesignInfo mcidi) {
        visitVisualComponent(mcidi);
    }

    @Override
    public void visit(MenuItemDesignInfo midi) {
        visitVisualComponent(midi);
    }

    @Override
    public void visit(MenuRadioItemDesignInfo mridi) {
        visitVisualComponent(mridi);
    }

    @Override
    public void visit(MenuSeparatorDesignInfo msdi) {
        visitVisualComponent(msdi);
    }

    @Override
    public void visit(PopupDesignInfo pdi) {
        try {
            result = new RADVisualContainer<>();
            result.initialize(model);
            result.setStoredName(pdi.getName());
            result.initInstance(JPopupMenu.class);
            //((RADVisualContainer<JPopupMenu>) result).initInstance(JPopupMenu.class);
            result.setInModel(true);
        } catch (Exception ex) {
            errors.add(ex);
        }
    }
}
