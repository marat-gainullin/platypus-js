/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.metadata.Field;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 * The class represents a data model field item in a completion pop-up list.
 *
 * @author mg
 */
public class FieldCompletionItem extends JsCompletionItem {

    private static final int SORT_PRIORITY = 10;

    public FieldCompletionItem(Field aField, int aStartOffset, int aEndOffset) {
        super(aField.getName(), aField.getDescription(), aStartOffset, aEndOffset);
        Icon fIcon = calcFieldIcon(aField.getType(), aField);
        if (fIcon != null) {
            icon = new ImageIcon(ImageUtilities.icon2Image(fIcon));
        }
        rightText = aField.getType();
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }

    @Override
    public String getInfomationText() {
        return informationText == null ? "" : informationText;//NOI18N
    }

    public static Icon calcFieldIcon(String aType, Field field) {
        Icon icon = FieldsTypeIconsCache.getIcon16(aType);
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
}
