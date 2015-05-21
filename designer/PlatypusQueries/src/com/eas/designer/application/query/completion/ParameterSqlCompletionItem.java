/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.metadata.Parameter;


/**
 *
 * @author mg
 */
public class ParameterSqlCompletionItem extends FieldSqlCompletionItem {

    public ParameterSqlCompletionItem(Parameter aParam, int aStartOffset, int aEndOffset) {
        super(aParam, aStartOffset, aEndOffset);
        text = ":" + aParam.getName();
    }
}
