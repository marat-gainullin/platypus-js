/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.util.PropertiesUtils.PropBox;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
public class PropertyCompletionItem extends JsCompletionItem {

    protected static final ImageIcon propertyIcon = new ImageIcon(PropertyCompletionItem.class.getResource("property.png"));
    protected static final ImageIcon propertyRoIcon = new ImageIcon(PropertyCompletionItem.class.getResource("property_ro.png"));
    protected static final ImageIcon propertyWoIcon = new ImageIcon(PropertyCompletionItem.class.getResource("property_wo.png"));
    protected PropBox propBox;
    private static final int SORT_PRIORITY = 20;
    
    public PropertyCompletionItem(PropBox aPropBox, int aStartOffset, int aEndOffset) {
        super(aPropBox.name, (aPropBox.jsDoc != null && !aPropBox.jsDoc.isEmpty()) ? aPropBox.jsDoc : null, aStartOffset, aEndOffset);
        propBox = aPropBox;
        if (propBox.typeName != null) {
            rightText = propBox.typeName;
        }
        icon = propertyIcon;
        if (propBox.writeable && !propBox.readable) {
            icon = propertyWoIcon;
        }
        if (!propBox.writeable && propBox.readable) {
            icon = propertyRoIcon;
        }
    }
    
    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
