/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentNode;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.bound.ModelObjectPropertyProperty;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.forms.components.model.ModelCombo;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import com.eas.designer.application.module.ModelJSObject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ModelObjectPropertyPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {

    protected int selectionSubject = ModelElementSelector.FIELD_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(ModelObjectPropertyPropertyEditor.class, "CTL_SelectField");
    protected FormModel formModel;
    protected ModelObjectPropertyProperty property;
    protected RADComponent<?> comp;
    protected String prefix = "";

    public ModelObjectPropertyPropertyEditor() {
        super();
    }

    public ModelObjectPropertyPropertyEditor(String aPrefix) {
        this();
        prefix = aPrefix;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue("".equals(text) ? null : text);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String[] getTags() {
        ApplicationDbEntity dataEntity = lookupDataEntity();
        if (dataEntity != null) {
            List<String> tags = new ArrayList<>();
            tags.add("");
            Fields fields = dataEntity.getFields();
            if (fields != null) {
                fields.toCollection().stream().forEach((Field aField) -> {
                    tags.add(prefix + aField.getName());
                });
            }
            return tags.toArray(new String[]{});
        } else {
            return super.getTags();
        }
    }

    protected ApplicationDbEntity lookupDataEntity() {
        try {
            if (comp instanceof RADModelGridColumn) {
                RADModelGridColumn radCol = (RADModelGridColumn) comp;
                comp = radCol.lookupGrid();
            }
            RADProperty<ModelJSObject> prop = comp.getProperty(ModelCombo.class.isAssignableFrom(comp.getBeanClass()) && "displayField".equals(property.getName()) ? "displayList" : "data");
            ModelJSObject modelJsObject = prop != null ? prop.getValue() : null;
            ApplicationDbEntity dataEntity = modelJsObject != null ? modelJsObject.getEntity() : null;
            return dataEntity;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        assert value == null || value instanceof String;
        super.setValue(value);
    }
    /*
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
     ApplicationDbModel model = formModel.getDataObject().getBasesProxy() != null ? formModel.getDataObject().getModel() : null;
     if (model != null) {
     if (element != null) {
     ApplicationDbEntity entity = model.getEntityById(element.getEntityId());
     if (entity != null && entity.isQuery()) {
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
     if (field != null) {
     String fieldDescription = field.getDescription();
     String fieldText = field.getName() != null && !field.getName().isEmpty() ? field.getName() : fieldDescription;

     if (StoredQueryFactory.ABSENT_QUERY_MSG.equals(fieldDescription)) {
     entityText = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.ABSENT_QUERY_MSG), entity.getQueryName());
     fieldText = "";
     }
     if (StoredQueryFactory.CONTENT_EMPTY_MSG.equals(fieldDescription)) {
     entityText = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.CONTENT_EMPTY_MSG), entity.getQueryName());
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
     renderer.setText(NbBundle.getMessage(ModelObjectPropertyPropertyEditor.class, "CTL_ModelUnavailable"));
     }
     renderer.setSize(box.getSize());
     renderer.paint(gfx);
     } catch (Exception ex) {
     Logger.getLogger(ModelObjectPropertyPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
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
     */

    // Elipsis button section
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        try {
            ApplicationDbModel model = formModel.getDataObject().getModel();
            if (model != null) {
                ApplicationDbEntity dataEntity = lookupDataEntity();
                String oldValue = (String) getValue();
                Field oldField = oldValue != null && dataEntity != null && dataEntity.getFields() != null ? dataEntity.getFields().get(oldValue.startsWith(prefix) ? oldValue.substring(prefix.length()) : oldValue) : null;
                final ModelElementRef oldRef = oldField != null && dataEntity != null ? new ModelElementRef(oldField, true, dataEntity.getEntityId()) : new ModelElementRef();
                final ModelElementRef selectedRef = new ModelElementRef();
                return ModelElementSelector.prepareDialog(model,
                        dialogTitle,
                        selectedRef,
                        selectionSubject,
                        null,
                        oldRef, (ActionEvent e) -> {
                            if (dataEntity != null && dataEntity.getFields() != null && selectedRef.entityId != null
                            && selectedRef.entityId.equals(dataEntity.getEntityId())) {
                                Field newField = dataEntity.getFields().get(selectedRef.getFieldName());
                                setValue(prefix + newField.getName());
                            }
                        });
            } else {
                return super.getCustomEditor();
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelObjectPropertyPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            return super.getCustomEditor();
        }
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
        Object bean = aEnv.getBeans()[0];
        if (bean instanceof Node) {
            Node node = (Node) bean;
            FormCookie formCookie = node.getLookup().lookup(FormCookie.class);
            if (formCookie != null && aEnv.getFeatureDescriptor() instanceof FormProperty<?>) {
                formModel = formCookie.getFormModel();
                property = (ModelObjectPropertyProperty) aEnv.getFeatureDescriptor();
            }
            if (node instanceof RADComponentNode) {
                comp = ((RADComponentNode) node).getRADComponent();
            }
        }
    }

    public void setFormModel(FormModel aFormModel) {
        formModel = aFormModel;
    }

    public void setProperty(ModelObjectPropertyProperty aProperty) {
        property = aProperty;
    }

    public void setComp(RADComponent<?> aComp) {
        comp = aComp;
    }
}
