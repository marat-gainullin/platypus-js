package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vv
 */
public class ModuleCompletionProvider extends JsCompletionProvider {

    @Override
    protected void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, int caretOffset) throws Exception {
        if (dataObject != null && dataObject.getProject().isDbConnected()) {
            dataObject.setAst(point.getAstRoot());
//            CompletionContext completionContext = dataObject.getCompletionContext();
//            if (point.getContext() != null && point.getContext().length > 0) {
//                for (int i = 0; i < point.getContext().length; i++) {
//                    completionContext = completionContext.getChildContext(point.getContext()[i], caretOffset);
//                    if (completionContext == null) {
//                        return;
//                    }
//                    caretOffset = 0;
//                }
//            }
            CompletionContext initialCompltionContext = dataObject.getCompletionContext();
            initialCompltionContext.setCompltionPoint(point);
            CompletionContext completionContext = getCompletionContext(initialCompltionContext, caretOffset);
            if (completionContext != null) {
                completionContext.applyCompletionItems(point, caretOffset, resultSet);
            }
        }
    }

    protected static CompletionContext getCompletionContext(CompletionContext initialContext, int caretOffset) throws Exception {
        if (initialContext == null) {
            throw new NullPointerException("Initial context is null.");
        }
        CompletionContext completionContext = initialContext;
        CompletionPoint point = initialContext.getCompletionPoint();
        assert point != null;
        if (point.getCompletionTokens() != null && point.getCompletionTokens().length > 0) {
            for (int i = 0; i < point.getCompletionTokens().length; i++) {
                completionContext = completionContext.getChildContext(point.getCompletionTokens()[i], caretOffset);
                if (completionContext == null) {
                    break;
                }
                caretOffset = 0;
            }
        }
        return completionContext;
    }
}
