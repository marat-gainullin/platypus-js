/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;

/**
 *
 * @author vv
 */
public class SystemConstructorCompletionItem extends JsFunctionCompletionItem {
    
    private static final String DOUBLE_QUOTES = "\"\"";//NOI18N 
    private static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(SystemConstructorCompletionItem.class.getResource("class_16.png")); //NOI18N
    private static final int SORT_PRIORITY = 40;
    private static final int QUOTES_CARET_POSITION_OFFSET = 2;
    public static final List<String> DOUBLE_QUOTES_PARAMS = Arrays.<String>asList(new String[]{DOUBLE_QUOTES});
    
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
    
    @Override
    public void defaultAction(JTextComponent component) {
        super.defaultAction(component);
        if (getParams() != null && getParams().size() == 1 && getParams().get(0).equals(DOUBLE_QUOTES)) {
            component.setCaretPosition(getNewCaretPositon(component) > 0 ? getNewCaretPositon(component) : 0);
        }
    }
    
    private int getNewCaretPositon(JTextComponent component) {
        return component.getCaretPosition() - QUOTES_CARET_POSITION_OFFSET;
    }
}
