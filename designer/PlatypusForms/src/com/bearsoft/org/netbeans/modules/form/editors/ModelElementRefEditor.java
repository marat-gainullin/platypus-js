/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormAwareEditor;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelMapLayer;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.SQLUtils;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.StoredQueryFactory;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.client.model.gui.view.IconsListCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ModelElementRefEditor extends PropertyEditorSupport implements FormAwareEditor {

    protected int selectionSubject = ModelElementSelector.FIELD_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(ModelElementRefEditor.class, "CTL_SelectField");
    protected FormModel formModel;
    protected FormProperty<Object> property;
    protected IconsListCellRenderer renderer;

    public ModelElementRefEditor() {
        super();
    }

    @Override
    public String getAsText() {
        assert getValue() == null || getValue() instanceof ModelElementRef;
        ModelElementRef element = (ModelElementRef) getValue();
        return element != null ? element.getFieldName() : super.getAsText();
    }

    // Combo editing section
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            assert getValue() == null || getValue() instanceof ModelElementRef;
            ModelElementRef element = (ModelElementRef) getValue();
            element = lookupGridOrMapLayerRowsSource(element);
            ApplicationDbModel model = formModel.getDataObject().getClient() != null ? formModel.getDataObject().getModel() : null;
            if (element != null && model != null) {
                ApplicationDbEntity elementEntity = model.getEntityById(element.getEntityId());
                if (elementEntity != null && elementEntity.isQuery()) {
                    Fields fields = elementEntity.getFields();
                    if (fields.contains(text)) {
                        ModelElementRef copied = element.copy();
                        copied.setField(true);
                        Field field = fields.get(text);
                        copied.setField(field);
                        setValue(copied);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String[] getTags() {
        try {
            assert getValue() == null || getValue() instanceof ModelElementRef;
            ModelElementRef element = (ModelElementRef) getValue();
            element = lookupGridOrMapLayerRowsSource(element);
            ApplicationDbModel model = formModel.getDataObject().getClient() != null ? formModel.getDataObject().getModel() : null;
            if (element != null && model != null) {
                ApplicationDbEntity elementEntity = model.getEntityById(element.getEntityId());
                if (elementEntity != null && elementEntity.isQuery()) {
                    Fields fields = elementEntity.getFields();
                    List<String> tags = new ArrayList<>();
                    for (int i = 1; i <= fields.getFieldsCount(); i++) {
                        Field field = fields.get(i);
                        tags.add(field.getName());
                    }
                    return tags.toArray(new String[]{});
                } else {
                    return super.getTags();
                }
            } else {
                return super.getTags();
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
            return super.getTags();
        }
    }

    @Override
    public void setValue(Object value) {
        try {
            assert value == null || value instanceof ModelElementRef;
            ModelElementRef element = (ModelElementRef) value;
            ApplicationDbModel model = formModel.getDataObject().getClient() != null ? formModel.getDataObject().getModel() : null;
            if (model != null) {
                if (element != null) {
                    ApplicationDbEntity elementEntity = model.getEntityById(element.getEntityId());
                    if (elementEntity != null && elementEntity.isQuery()) {
                        if (element.getFieldName() != null && !element.getFieldName().isEmpty()) {
                            Field field;
                            if (element.isField()) {
                                field = elementEntity.getFields() != null ? elementEntity.getFields().get(element.getFieldName()) : null;
                            } else {
                                field = elementEntity.getQuery() != null ? elementEntity.getQuery().getParameters().get(element.getFieldName()) : null;
                            }
                            element.setField(field);
                        }
                    }
                }
                super.setValue(value);
                property.setValue(value);
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // In-Place painting section
    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        try {
            assert getValue() == null || getValue() instanceof ModelElementRef;
            renderer = new IconsListCellRenderer();
            renderer.setOpaque(false);
            ModelElementRef element = (ModelElementRef) getValue();
            ApplicationDbModel model = formModel.getDataObject().getClient() != null ? formModel.getDataObject().getModel() : null;
            if (model != null) {
                if (element != null) {
                    ApplicationDbEntity entity = model.getEntityById(element.getEntityId());
                    if (entity != null && (entity.isQuery() || entity instanceof ApplicationParametersEntity)) {
                        Font fieldsFont = DatamodelDesignUtils.getFieldsFont();
                        int iconTextGap = 4;
                        String entityText = entity.getName() != null && !entity.getName().isEmpty() ? entity.getName() : entity.getTitle();
                        Field field = element.getField();
                        if (field != null) {
                            field = entity.getFields().get(field.getName());
                            if (field != null) {
                                element.setField(field);
                            }
                        }
                        if (entity instanceof ApplicationParametersEntity) {
                            entityText = ApplicationDbModel.PARAMETERS_SCRIPT_NAME;// since we prefer names instead of titles
                            //entityText = DatamodelDesignUtils.getLocalizedString("Parameters");
                        }
                        if (field != null) {
                            String fieldDescription = field.getDescription();
                            String fieldText = field.getName() != null && !field.getName().isEmpty() ? field.getName() : fieldDescription;

                            if (StoredQueryFactory.ABSENT_QUERY_MSG.equals(fieldDescription)) {
                                entityText = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.ABSENT_QUERY_MSG), entity.getQueryId());
                                fieldText = "";
                            }
                            if (StoredQueryFactory.CONTENT_EMPTY_MSG.equals(fieldDescription)) {
                                entityText = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.CONTENT_EMPTY_MSG), entity.getQueryId());
                                fieldText = "";
                            }
                            String typeName = SQLUtils.getLocalizedTypeName(field.getTypeInfo().getSqlType());
                            if (typeName == null) {
                                typeName = field.getTypeInfo().getSqlTypeName();
                            }
                            Icon pkIcon = null;
                            Icon fkIcon = null;
                            Icon typeIcon = FieldsTypeIconsCache.getIcon16(field.getTypeInfo().getSqlType());
                            if (field.getTypeInfo().getSqlType() == Types.STRUCT || field.getTypeInfo().getSqlType() == Types.OTHER) {
                                String ltext = SQLUtils.getLocalizedTypeName(field.getTypeInfo().getSqlTypeName());
                                if (ltext != null && !ltext.isEmpty()) {
                                    typeName = ltext;
                                }
                                String fTypeName = field.getTypeInfo().getSqlTypeName();
                                if (fTypeName != null) {
                                    fTypeName = fTypeName.toUpperCase();
                                }
                                Icon licon = FieldsTypeIconsCache.getIcon16(fTypeName);
                                if (licon != null) {
                                    typeIcon = licon;
                                }
                            }
                            boolean lisPk = field.isPk();
                            boolean lisFk = field.isFk();
                            if (lisPk) {
                                typeName = SQLUtils.getLocalizedPkName() + "." + typeName;
                                pkIcon = FieldsTypeIconsCache.getPkIcon16();
                                //iconTextGap += pkIcon.getIconWidth() + 2;
                            }
                            if (lisFk) {
                                typeName = SQLUtils.getLocalizedFkName() + "." + typeName;
                                fkIcon = FieldsTypeIconsCache.getFkIcon16();
                                //iconTextGap += fkIcon.getIconWidth() + 2;
                            }
                            prepareIconsRenderer(pkIcon, fkIcon, typeIcon, entityText + "." + fieldText + ":" + typeName, iconTextGap, fieldsFont);
                        } else {
                            prepareIconsRenderer(null, null, null, entityText, iconTextGap, fieldsFont);
                        }
                    } else {
                        renderer.setText("N/A");
                    }
                } else {
                    renderer.setText("<>");
                }
            } else {
                renderer.setText(NbBundle.getMessage(ModelElementRefEditor.class, "CTL_ModelUnavailable"));
            }
            renderer.setSize(box.getSize());
            renderer.paint(gfx);
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void prepareIconsRenderer(Icon aPkIcon, Icon aFkIcon, Icon aTypeIcon, String aText, int aIconTextGap, Font aFont) {//, JList aList) {
        if (aPkIcon != null && aFkIcon == null) {
            renderer.setIcon(aTypeIcon);
            renderer.addIcon(aPkIcon);
        } else if (aPkIcon == null && aFkIcon != null) {
            renderer.setIcon(aTypeIcon);
            renderer.addIcon(aFkIcon);
        } else if (aPkIcon != null && aFkIcon != null) {
            renderer.setIcon(aTypeIcon);
            renderer.addIcon(aPkIcon);
            renderer.addIcon(aFkIcon);
        } else {
            renderer.setIcon(aTypeIcon);
        }
        renderer.setText(aText);
        renderer.setIconTextGap(aIconTextGap);
        renderer.setFont(aFont);
    }

    // Elipsis button section
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        try {
            ApplicationDbModel model = formModel.getDataObject().getClient() != null ? formModel.getDataObject().getModel() : null;
            if (model != null) {
                final ModelElementRef selected = new ModelElementRef();
                return ModelElementSelector.prepareDialog(
                        model,
                        dialogTitle,
                        selected,
                        selectionSubject,
                        null,
                        (ModelElementRef) getValue(),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setValue(selected);
                            }
                        });
            } else {
                return super.getCustomEditor();
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
            return super.getCustomEditor();
        }
    }

    @Override
    public void setContext(FormModel aFormModel, FormProperty<?> aProperty) {
        formModel = aFormModel;
        property = (FormProperty<Object>) aProperty;
    }

    protected ModelElementRef lookupGridOrMapLayerRowsSource(ModelElementRef element) {
        if ((element == null || (element.getEntityId() == null && element.getField() == null)) && property instanceof RADProperty<?>) {
            RADProperty<?> radProperty = (RADProperty<?>) property;
            if (radProperty.getComponent() instanceof RADModelGrid) {
                RADModelGrid radGrid = (RADModelGrid) ((RADProperty<?>) property).getComponent();
                element = radGrid.getBeanInstance().getRowsDatasource();
            } else if (radProperty.getComponent() instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) radProperty.getComponent();
                RADModelGrid radGrid = radColumn.lookupGrid();
                element = radGrid.getBeanInstance().getRowsDatasource();
            } else if (radProperty.getComponent() instanceof RADModelMapLayer) {
                RADModelMapLayer radMapLayer = (RADModelMapLayer) radProperty.getComponent();
                element = radMapLayer.getBeanInstance().getRef();
            }
        }
        return element;
    }
}
