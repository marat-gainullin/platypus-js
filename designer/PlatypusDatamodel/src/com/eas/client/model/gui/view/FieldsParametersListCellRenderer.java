package com.eas.client.model.gui.view;

import com.eas.client.SQLUtils;
import com.eas.client.StoredQueryFactory;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.JdbcField;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.DatamodelDesignUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.MatteBorder;

public class FieldsParametersListCellRenderer<E extends Entity<?, ?, E>> implements ListCellRenderer<Field> {

    public static final Color interFieldsColor = new Color(245, 245, 245);
    protected Font fieldFont;
    protected Font bindedFieldFont;
    protected E entity;
    IconsListCellRenderer iconsRenderer;

    public FieldsParametersListCellRenderer(Font aFieldFont, Font aBindedFieldFont, E aEntity) {
        super();
        fieldFont = aFieldFont;
        bindedFieldFont = aBindedFieldFont;
        entity = aEntity;
    }

    public FieldsParametersListCellRenderer() {
        super();
        fieldFont = DatamodelDesignUtils.getFieldsFont();
        bindedFieldFont = DatamodelDesignUtils.getBindedFieldsFont();
        entity = null;
    }

    public E getEntity() {
        return entity;
    }

    public void setEntity(E aEntity) {
        entity = aEntity;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Field> list, Field value, int index, boolean isSelected, boolean cellHasFocus) {
        if (entity != null && value != null) {
            Field field = value;
            Font fieldsFont = fieldFont;
            Set<Relation<E>> lrelations = entity.getInOutRelations();
            if (entity.getModel().isFieldInRelations(entity, lrelations, field)) {
                fieldsFont = bindedFieldFont;
            }
            String fieldDescription = field.getDescription();
            if (StoredQueryFactory.ABSENT_QUERY_MSG.equals(fieldDescription)) {
                fieldDescription = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.ABSENT_QUERY_MSG), entity.getQueryName());
            }
            if (StoredQueryFactory.CONTENT_EMPTY_MSG.equals(fieldDescription)) {
                fieldDescription = String.format(DatamodelDesignUtils.localizeString(StoredQueryFactory.CONTENT_EMPTY_MSG), entity.getQueryName());
            }
            String typeName = field.getType();
            Icon pkIcon = null;
            Icon fkIcon = null;
            Icon typeIcon = calcIcon(field instanceof JdbcField ? ((JdbcField) field).getJdbcType() : 0, typeName);
            boolean lisPk = field.isPk();
            boolean lisFk = field.isFk();
            int iconTextGap = 4;
            if (lisPk) {
                typeName = typeName + "." + SQLUtils.getLocalizedPkName();
                pkIcon = FieldsTypeIconsCache.getPkIcon16();
            }
            if (lisFk) {
                typeName = typeName + "." + SQLUtils.getLocalizedFkName();
                fkIcon = FieldsTypeIconsCache.getFkIcon16();
            }
            iconsRenderer = new IconsListCellRenderer();
            iconsRenderer.getListCellRendererComponent(list, "fff", index, isSelected, cellHasFocus);
            String fieldName = calcFieldName(field);
            prepareIconsRenderer(pkIcon, fkIcon, typeIcon, fieldDescription, fieldName, typeName, iconTextGap, fieldsFont);//, list);
            return iconsRenderer;
        }
        return null;
    }

    protected String calcFieldName(Field field) {
        String fieldName = field.getName();
        if (fieldName == null) {
            fieldName = "";
        }
        return fieldName;
    }

    protected Icon calcIcon(int aJdbcType, String typeName) {
        return FieldsTypeIconsCache.getIcon16(typeName);
    }

    protected void prepareIconsRenderer(Icon aPkIcon, Icon aFkIcon, Icon aTypeIcon, String aDescription, String aName, String aTypeName, int aIconTextGap, Font aFont) {
        if (aPkIcon != null && aFkIcon == null) {
            iconsRenderer.setIcon(aTypeIcon);
            iconsRenderer.addIcon(aPkIcon);
        } else if (aPkIcon == null && aFkIcon != null) {
            iconsRenderer.setIcon(aTypeIcon);
            iconsRenderer.addIcon(aFkIcon);
        } else if (aPkIcon != null && aFkIcon != null) {
            iconsRenderer.setIcon(aTypeIcon);
            iconsRenderer.addIcon(aPkIcon);
            iconsRenderer.addIcon(aFkIcon);
        } else {
            iconsRenderer.setIcon(aTypeIcon);
        }
        iconsRenderer.setText(aName + (aDescription != null && !aDescription.isEmpty() ? ("  [ " + aDescription + " ]") : "") + (aTypeName != null && !aTypeName.isEmpty() ? " : " + aTypeName : ""));
        iconsRenderer.setIconTextGap(aIconTextGap);
        iconsRenderer.setFont(aFont);
        iconsRenderer.setBorder(new MatteBorder(0, 0, 1, 0, interFieldsColor));
    }
}
