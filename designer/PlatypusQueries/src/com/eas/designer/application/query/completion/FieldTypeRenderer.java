/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.ErrorManager;
import org.openide.util.ImageUtilities;

/**
 *
 * @author mg
 */
public class FieldTypeRenderer extends DefaultTableCellRenderer {

    protected PlatypusQueryDataObject dataObject;
    protected JLabel testLabel = new JLabel("some text");

    public FieldTypeRenderer(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            Component rComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String fieldType = (String) value;
            Color defaultColor = Color.lightGray;
            Color editedColor = testLabel.getForeground();
            Fields fields = dataObject.getOutputFields();
            if (fields != null && row >= 0 && row < fields.getFieldsCount()) {
                Field field = fields.get(row + 1);
                StoredFieldMetadata addition = getCorrespondingAddition(field);
                if (addition != null) {
                    if (addition.getType() != null && !addition.getType().equals(field.getType())) {
                        defaultColor = editedColor;
                        fieldType = addition.getType();
                    }
                }
                rComp.setForeground(defaultColor);
                if (rComp instanceof JLabel) {
                    Icon icon = calcFieldIcon(fieldType, field);
                    ((JLabel) rComp).setIcon(icon);
                    String typeDisplayName = fieldType;
                    if (typeDisplayName != null) {
                        ((JLabel) rComp).setText(typeDisplayName);
                    }
                }
            }
            return rComp;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    public static Icon calcFieldIcon(String aFieldType, Field field) {
        Icon icon = FieldsTypeIconsCache.getIcon16(aFieldType);
        if (field.isPk() || field.isFk()) {
            Image res = ImageUtilities.icon2Image(icon);
            if (field.isPk()) {
                res = ImageUtilities.mergeImages(res, ImageUtilities.icon2Image(FieldsTypeIconsCache.getPkIcon16()), 0, 0);
            }
            if (field.isFk()) {
                res = ImageUtilities.mergeImages(res, ImageUtilities.icon2Image(FieldsTypeIconsCache.getFkIcon16()), 0, 0);
            }
            icon = ImageUtilities.image2Icon(res);
        }
        return icon;
    }

    private StoredFieldMetadata getCorrespondingAddition(Field aField) throws Exception {
        for (StoredFieldMetadata addition : dataObject.getOutputFieldsHints()) {
            if (addition.getBindedColumn().equals(aField.getName())) {
                return addition;
            }
        }
        return null;
    }
}
