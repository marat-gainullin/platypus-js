/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.metadata.Field;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author mg
 */
public class FieldSqlCompletionItem extends SqlCompletionItem {

    public FieldSqlCompletionItem(Field aField, int aStartOffset, int aEndOffset) {
        super(aField.getName(), (aField.getDescription() != null && aField.getDescription().isEmpty()) ? aField.getDescription() : null, aStartOffset, aEndOffset);
        Icon fIcon = FieldTypeRenderer.calcFieldIcon(aField.getType(), aField);
        if (fIcon != null) {
            icon = new ImageIcon(ImageUtilities.icon2Image(fIcon));
        }
        rightText = aField.getType();
    }
}
