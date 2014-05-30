/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import javax.swing.ImageIcon;

/**
 * The class represents a special kind of property item in a completion
 * pop-up list.
 * 
 * @author mg
 */
public class BeanCompletionItem extends JsCompletionItem {

    protected static final ImageIcon compIcon = new ImageIcon(BeanCompletionItem.class.getResource("bean.png"));
    protected Class<?> beanClass;
    private static final int SORT_PRIORITY = 50;

    public BeanCompletionItem(Class<?> aBean, String aText, String aInformationText, int aStartOffset, int aEndOffset) {
        super(aText, (aInformationText != null && !aInformationText.isEmpty()) ? aInformationText : null, aStartOffset, aEndOffset);
        beanClass = aBean;
        rightText = beanClass.getSimpleName();
        icon = compIcon;
    }
    
    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
