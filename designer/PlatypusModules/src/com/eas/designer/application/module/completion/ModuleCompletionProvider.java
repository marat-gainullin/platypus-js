/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class ModuleCompletionProvider implements CompletionProvider {

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
                        CompletionPoint completionPoint = CompletionPoint.createInstance((NbEditorDocument) doc, caretOffset);
                        fillCompletionPoint(dataObject, completionPoint, resultSet, caretOffset);
                    }
                    resultSet.finish();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }, component);
    }

    protected void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, int caretOffset) throws Exception {
        dataObject.setAst(point.getAstRoot());
        CompletionContext initialCompltionContext = dataObject.getCompletionContext();
        CompletionContext completionContext = getCompletionContext(initialCompltionContext, point.getCompletionTokens(), caretOffset);
        if (completionContext != null) {
            completionContext.applyCompletionItems(point, caretOffset, resultSet);
        }
    }

    protected static CompletionContext getCompletionContext(CompletionContext initialContext, List<CompletionPoint.CompletionToken> completionTokens, int caretOffset) throws Exception {
        if (initialContext == null) {
            throw new NullPointerException("Initial context is null.");
        }
        CompletionContext completionContext = initialContext;
        if (completionTokens != null) {
            Iterator<CompletionToken> i = completionTokens.iterator();
            while (i.hasNext()) {
                completionContext = completionContext.getChildContext(i.next(), caretOffset);
                if (completionContext == null) {
                    break;
                }
                caretOffset = 0;
            }
        }
        return completionContext;
    }
}
