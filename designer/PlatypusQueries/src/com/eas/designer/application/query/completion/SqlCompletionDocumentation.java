/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author mg
 */
public class SqlCompletionDocumentation implements CompletionDocumentation {

    private SqlCompletionItem item;

    public SqlCompletionDocumentation(SqlCompletionItem aItem) {
        item = aItem;
    }

    @Override
    public String getText() {
        return item.getInfomationText();
    }

    @Override
    public URL getURL() {
        return null;
    }

    @Override
    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    @Override
    public Action getGotoSourceAction() {
        return null;
    }
}
