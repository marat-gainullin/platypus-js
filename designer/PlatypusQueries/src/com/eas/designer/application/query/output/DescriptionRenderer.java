/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.output;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public class DescriptionRenderer extends DefaultTableCellRenderer {

    protected PlatypusQueryDataObject dataObject;
    protected JLabel testLabel = new JLabel("some text");

    public DescriptionRenderer(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            Component rComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color defaultColor = Color.lightGray;
            Color editedColor = testLabel.getForeground();
            Fields fields = dataObject.getOutputFields();
            if (fields != null && row >= 0 && row < fields.getFieldsCount()) {
                Field field = fields.get(row + 1);
                StoredFieldMetadata addition = getCorrespondingAddition(field);
                if (addition != null) {
                    if (addition.getDescription() != null && !addition.getDescription().equals(field.getDescription())) {
                        defaultColor = editedColor;
                    }
                }
            }
            rComp.setForeground(defaultColor);
            return rComp;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
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
