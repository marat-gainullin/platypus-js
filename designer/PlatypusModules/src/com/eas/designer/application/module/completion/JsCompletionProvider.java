/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionTokenType;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.script.JsParser;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public abstract class JsCompletionProvider implements CompletionProvider {

    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return CompletionProvider.COMPLETION_QUERY_TYPE;
    }

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component) {
        return createCompletionTask(component);
    }

    public CompletionTask createCompletionTask(JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                try {
                    PlatypusModuleDataObject dataObject = (PlatypusModuleDataObject) doc.getProperty(PlatypusModuleDataObject.DATAOBJECT_DOC_PROPERTY);
                    if (doc instanceof NbEditorDocument) {
                        CompletionPoint completionPoint = CompletionPoint.createInstance((NbEditorDocument)doc, caretOffset);
                        fillCompletionPoint(dataObject, completionPoint, resultSet, caretOffset);
                    }
                    resultSet.finish();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }, component);
    }

    protected abstract void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, int caretOffset) throws Exception;
}