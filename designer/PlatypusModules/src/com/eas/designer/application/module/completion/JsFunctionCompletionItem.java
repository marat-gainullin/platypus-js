/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;

/**
 * The class represents a JavaScript function item in a completion pop-up list.
 *
 * @author vv
 */
public class JsFunctionCompletionItem extends JsCompletionItem {

    protected static final ImageIcon PLATYPUS_METHOD_ICON = new ImageIcon(JsFunctionCompletionItem.class.getResource("method-platypus.png"));
    protected static final ImageIcon METHOD_ICON = new ImageIcon(JsFunctionCompletionItem.class.getResource("method.png")); //NOI18N

    private static final String PARAMETER_NAME_COLOR = "<font color=#a06001>"; //NOI18N
    private static final String END_COLOR = "</font>"; // NOI18N
    private static final int SORT_PRIORITY = 30;

    private List<String> params;

    public List<String> getParams() {
        return Collections.unmodifiableList(params);
    }

    public JsFunctionCompletionItem(String aText, String aInformationText, int aStartOffset, int aEndOffset) {
        this(aText, aInformationText, null, null, aStartOffset, aEndOffset);
    }

    public JsFunctionCompletionItem(String name, String rightText, List<String> params, String jsDoc, int aStartOffset, int aEndOffset) {
        this(name, rightText, params, jsDoc, aStartOffset, aEndOffset, false);
    }

    public JsFunctionCompletionItem(String aName, String aRightText, List<String> aParams, String aJsDoc, int aStartOffset, int aEndOffset, boolean isPlatypusJsMethod) {
        super(aName, aJsDoc, aStartOffset, aEndOffset);
        params = aParams;
        rightText = aRightText;
        icon = isPlatypusJsMethod ? PLATYPUS_METHOD_ICON : METHOD_ICON;
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(false), rightText, g, defaultFont,
                (selected ? Color.white : defaultColor), width, height, selected);
    }

    @Override
    public void defaultAction(JTextComponent component) {
        try {
            StyledDocument doc = (StyledDocument) component.getDocument();
            doc.remove(startOffset, endOffset - startOffset);
            doc.insertString(startOffset, getLeftHtmlText(true), null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }

    @Override
    public String getInfomationText() {
        JsCommentFormatter formatter = new JsCommentFormatter(CompletionUtils.getComments(informationText));
        return formatter.toHtml();
    }

    @Override
    protected String getLeftHtmlText(boolean plainText) {
        return text + "(" + getParamsHtmlText(plainText) + ")"; //NOI18N
    }

    private String getParamsHtmlText(boolean plainText) {
        if (params == null || params.isEmpty()) {
            return ""; //NOI18N
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (plainText) {
                sb.append(params.get(i));
            } else {
                sb.append(PARAMETER_NAME_COLOR).append(params.get(i)).append(END_COLOR);
            }
            if (i < params.size() - 1) {
                sb.append(", ");  //NOI18N
            }
        }
        return sb.toString();
    }
}
