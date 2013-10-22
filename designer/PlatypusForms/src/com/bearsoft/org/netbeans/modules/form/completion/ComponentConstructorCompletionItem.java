/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author vv
 */
public class ComponentConstructorCompletionItem  extends SystemConstructorCompletionItem{
    
    private static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(ComponentConstructorCompletionItem.class.getResource("map_16.png")); //NOI18N
    private static final int SORT_PRIORITY = 60;
    
    public ComponentConstructorCompletionItem(String name, String rightText, List<String> params, String jsDoc, int aStartOffset, int aEndOffset) {
        super(name, rightText, params, jsDoc, aStartOffset, aEndOffset);
    }

    @Override
    public ImageIcon getIcon() {
        return CONSTRUCTOR_ICON;
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
