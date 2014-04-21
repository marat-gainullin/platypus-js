/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.util.PropertiesUtils.PropBox;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

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
    private static final int DEFAULT_NUMBER_OF_SPACES_PER_INDENT = 4;
    private static final String FUNCTION_HEADER = " = function(event) {\n";//NOI18N

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
            String tabs = getLineTabs(doc, startOffset);
            doc.remove(startOffset, endOffset - startOffset);
            boolean insertEventAssignmentTemplate = propBox.eventClass != null && isLineEndClear(doc, endOffset);
            doc.insertString(startOffset, insertEventAssignmentTemplate ? getEventHandler(tabs) : text, null);
            Completion.get().hideAll();
            if (insertEventAssignmentTemplate) {
                component.setCaretPosition(getEventTemplateCaretPosition(tabs));
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }

    private String getEventHandler(String tabs) {
        return text
                + FUNCTION_HEADER
                + tabs + getIndent() + getHandlerBody() + "\n"//NOI18N
                + tabs + "};\n";//NOI18N
    }

    private static String getHandlerBody() {
        return NbBundle.getMessage(PropertyCompletionItem.class, "MSG_EventHandlerBody");//NOI18N
    }
    
    
    private int getEventTemplateCaretPosition(String tabs) {
        return startOffset + text.length() + FUNCTION_HEADER.length() + tabs.length() + getNumberOfSpacesPerIndent() + getHandlerBody().length();
    }

    private static String getLineTabs(StyledDocument doc, int startOffset) {
        int i = startOffset;
        String s;
        try {
            do {

                s = doc.getText(i, 1);
                if ((i > 0 && !"\n".equals(s))) {//NOI18N
                    i--;
                } else {
                    break;
                }

            } while (true);
            StringBuilder tabs = new StringBuilder();
            i++;
            do {
                s = doc.getText(i, 1);
                if (" ".equals(s) || "\t".equals(s)) {//NOI18N
                    tabs.append(s);
                } else if (Character.isJavaIdentifierPart(s.charAt(0))) {
                    break;
                }
                i++;

            } while (i < doc.getLength());
            return tabs.toString();
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);//should never happen
        }
    }

    private static boolean isLineEndClear(StyledDocument doc, int pos) {
        String s;
        int i = pos;
        try {
            do {
                s = doc.getText(i, 1);
                if ("\n".equals(s)) {//NOI18N
                    return true;
                } else if (Character.isJavaIdentifierPart(s.charAt(0))) {
                    return false;
                }
                i++;
            } while (i < doc.getLength());
            return false;
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumberOfSpacesPerIndent();i++) {
            sb.append(" ");//NOI18N
        }
        return sb.toString();
    }
    
    
    private static int getNumberOfSpacesPerIndent() {
       return DEFAULT_NUMBER_OF_SPACES_PER_INDENT; //TODO read the NB editor's formating Number of Spaces per Indent value.
    }
}
