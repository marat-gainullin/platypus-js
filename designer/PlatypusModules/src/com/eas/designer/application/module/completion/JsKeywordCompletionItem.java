/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import javax.swing.ImageIcon;


/**
 *
 * @author vv
 */
public class JsKeywordCompletionItem extends JsCompletionItem {
    protected static final ImageIcon jsKeywordIcon = new ImageIcon(JsKeywordCompletionItem.class.getResource("javascript.png")); // NOI18N
    private static final int SORT_PRIORITY = 100;
    
    public JsKeywordCompletionItem(String name, int aStartOffset, int aEndOffset) {
        super(name, null, aStartOffset, aEndOffset);
        this.icon = jsKeywordIcon;
    }
        
    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
