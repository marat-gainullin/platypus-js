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
public class JsFieldCompletionItem extends JsCompletionItem {
    
    protected static final ImageIcon methodIcon = new ImageIcon(JsFieldCompletionItem.class.getResource("fieldPublic.png")); // NOI18N
    private static final int SORT_PRIORITY = 0;
    
    public JsFieldCompletionItem(String name, String rightText, String jsDoc, int aStartOffset, int aEndOffset) {
        super(name, jsDoc, aStartOffset, aEndOffset);
        this.rightText = rightText;
        this.icon = methodIcon;
    }
    
    @Override
    public String getInfomationText() {
        JsCommentFormatter formatter = new JsCommentFormatter(CompletionUtils.getComments(informationText));
        return formatter.toHtml();
    }
    
    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
