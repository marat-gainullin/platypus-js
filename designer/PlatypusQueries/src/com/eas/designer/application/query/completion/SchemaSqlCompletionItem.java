/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

/**
 *
 * @author mg
 */
public class SchemaSqlCompletionItem extends SqlCompletionItem {

    public SchemaSqlCompletionItem(String aSchema, int aStartOffset, int aEndOffset) {
        super(aSchema, null, aStartOffset, aEndOffset);
    }
}
