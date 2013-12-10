/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.designer.application.query.lexer.SqlTokenId;
import com.eas.util.StringUtils;
import org.netbeans.spi.editor.completion.CompletionTask;

/**
 *
 * @author mg
 */
public class KeywordSqlCompletionItem extends SqlCompletionItem {

    public KeywordSqlCompletionItem(SqlTokenId token, int aStartOffset, int aEndOffset) {
        super(null, null, aStartOffset, aEndOffset);
        if (token.name().startsWith("K_")) {
            text = token.name().substring(2);
        } else {
            text = token.name();
        }
        text = StringUtils.capitalize(text.toLowerCase());
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

}
