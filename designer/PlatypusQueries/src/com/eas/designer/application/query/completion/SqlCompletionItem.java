/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class SqlCompletionItem implements CompletionItem {

    protected ImageIcon icon;
    protected String text;
    protected String informationText;
    protected String rightText;
    protected int startOffset;
    protected int endOffset;

    public SqlCompletionItem(String aText, String aInformationText, int aStartOffset, int aEndOffset) {
        super();
        text = aText;
        informationText = aInformationText;
        startOffset = aStartOffset;
        endOffset = aEndOffset;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        try {
            StyledDocument doc = (StyledDocument) component.getDocument();
            doc.remove(startOffset, endOffset - startOffset);
            doc.insertString(startOffset, getTestToInsert(), null);
            Completion.get().hideAll();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    protected String getTestToInsert() throws Exception {
        return text;
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {
    }

    @Override
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(text + (rightText != null ? (rightText + "  ") : ""), null, g, defaultFont);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(icon, text, rightText, g, defaultFont,
                (selected ? Color.white : defaultColor), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        if (informationText != null) {
            return new AsyncCompletionTask(new AsyncCompletionQuery() {
                @Override
                protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                    completionResultSet.setDocumentation(new SqlCompletionDocumentation(SqlCompletionItem.this));
                    completionResultSet.finish();
                }
            });
        } else {
            return null;
        }
    }

    @Override
    public CompletionTask createToolTipTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText(NbBundle.getMessage(SqlCompletionItem.class, "Press_Enter_to_insert") + " \"" + text + "\"");
                completionResultSet.setToolTip(toolTip);
                completionResultSet.finish();
            }
        });
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return null;
    }

    public String getText() {
        return text;
    }

    public String getInfomationText() {
        return informationText;
    }
}
