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
public class DummySqlCompletionItem extends SqlCompletionItem {

    public DummySqlCompletionItem() {
        super(NbBundle.getMessage(DummySqlCompletionItem.class, "NoSuggessions"), NbBundle.getMessage(SqlCompletionDocumentation.class, "ReferToSqlPane"), -1, -1);
    }

    @Override
    public void defaultAction(JTextComponent component) {
        Completion.get().hideAll();
    }

}
