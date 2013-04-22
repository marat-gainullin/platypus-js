/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.query.completion;

import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;

/**
 *
 * @author mg
 */
public class ExceptionSqlCompletionItem extends SqlCompletionItem{

    protected Exception ex;

    public ExceptionSqlCompletionItem(Exception aException)
    {
        super(aException.getMessage(), null, -1, -1);
        ex = aException;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        Completion.get().hideAll();
    }
}
