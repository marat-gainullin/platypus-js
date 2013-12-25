/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.org.netbeans.modules.form.completion;

import com.eas.designer.application.module.completion.JsFieldCompletionItem;
import javax.swing.ImageIcon;

/**
 *
 * @author vv
 */
public class FormObjectCompletionItem extends JsFieldCompletionItem {

    protected static final ImageIcon ICON = new ImageIcon(FormObjectCompletionItem.class.getResource("map_16.png")); // NOI18N
    
    public FormObjectCompletionItem(String name, String rightText, String jsDoc, int aStartOffset, int aEndOffset) {
        super(name, rightText, jsDoc, aStartOffset, aEndOffset);
    }

    @Override
    public ImageIcon getIcon() {
        return ICON;
    }
    
    
}
