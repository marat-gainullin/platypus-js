/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.query.completion;

import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class EmptySqlCompletionItem extends SqlCompletionItem{

    public EmptySqlCompletionItem()
    {
        super(NbBundle.getMessage(DummySqlCompletionItem.class, "NoSuggessions"), null, -1, -1);
    }

    public EmptySqlCompletionItem(String aText)
    {
        this();
        text = aText;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        Completion.get().hideAll();
    }
}
