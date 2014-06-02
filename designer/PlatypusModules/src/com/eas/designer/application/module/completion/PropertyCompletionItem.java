/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.ModuleUtils;
import com.eas.util.PropertiesUtils.PropBox;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.openide.ErrorManager;

/**
 * The class represents a JavaScript property item in 
 * a completion pop-up list.
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
    public void defaultAction(JTextComponent component) {
        try {
            StyledDocument doc = (StyledDocument) component.getDocument();
            String tabs = ModuleUtils.getLineTabs(doc, startOffset);
            doc.remove(startOffset, endOffset - startOffset);
            boolean insertEventAssignmentTemplate = propBox.eventClass != null && ModuleUtils.isLineEndClear(doc, startOffset);
            doc.insertString(startOffset, insertEventAssignmentTemplate ? ModuleUtils.getEventHandlerJs(text, tabs) : text, null);
            Completion.get().hideAll();
            if (insertEventAssignmentTemplate) {
                component.setCaretPosition(getEventTemplateCaretPosition(startOffset, text, tabs));
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
    
    private static int getEventTemplateCaretPosition(int startOffset, String handlerName, String tabs) {
        return startOffset 
                + handlerName.length() 
                + ModuleUtils.FUNCTION_HEADER.length() 
                + tabs.length() 
                + ModuleUtils.getNumberOfSpacesPerIndent();
    }
}
