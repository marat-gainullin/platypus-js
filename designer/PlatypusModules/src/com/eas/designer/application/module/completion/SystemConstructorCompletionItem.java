/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author vv
 */
public class SystemConstructorCompletionItem extends JsFunctionCompletionItem {

    protected static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(SystemConstructorCompletionItem.class.getResource("class_16.png")); //NOI18N
    private static final int SORT_PRIORITY = 20;
    
    public SystemConstructorCompletionItem(String name, String rightText, List<String> params, String jsDoc, int aStartOffset, int aEndOffset) {
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
