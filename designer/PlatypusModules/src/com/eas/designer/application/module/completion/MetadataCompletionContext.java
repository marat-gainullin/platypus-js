/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * A data model field data schema completion context. 
 * @author vv
 */
public class MetadataCompletionContext extends CompletionContext {

    private static final String FIELD_METADATA_JS_DOC = "/**\n"
            + "* The field's metadata information.\n"
            + "*/";
    private final Fields fields;

    public MetadataCompletionContext(Fields aFields) {
        super(null);
        fields = aFields;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFields(fields, point, resultSet);
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return new CompletionContext(Field.class);
    }

    protected static void fillFields(Fields aFields, CompletionPoint point, CompletionResultSet resultSet) {
        if (aFields != null) {
            for (Field field : aFields.toCollection()) {
                addItem(resultSet, point.getFilter(), new BeanCompletionItem(field.getClass(), field.getName(), FIELD_METADATA_JS_DOC, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        } else {
            throw new IllegalStateException("Fields are unavailable");
        }
    }

}
