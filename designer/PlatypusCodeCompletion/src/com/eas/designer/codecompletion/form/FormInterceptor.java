/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion.form;

import com.eas.designer.codecompletion.InterceptorUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.api.lexer.LexUtilities;
import org.netbeans.modules.javascript2.editor.model.DeclarationScope;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionArgument;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public abstract class FormInterceptor implements FunctionInterceptor {
    
    private static final String FORM_MODULE_NAME = "form"/*Crazy RequireJsIndexer! It should be forms/form */;
    private final String interceptedName;
    
    public FormInterceptor(String aInterceptedName) {
        super();
        interceptedName = aInterceptedName;
    }

    @Override
    public Collection<TypeUsage> intercept(Snapshot snapshot, String name, JsObject globalObject,
            DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        if (!args.isEmpty()) {
            TokenHierarchy<?> th = snapshot.getTokenHierarchy();
            TokenSequence<? extends JsTokenId> ts = (TokenSequence<? extends JsTokenId>) th.tokenSequence();
            FunctionArgument moduleNameArg = args.iterator().next();
            ts.move(moduleNameArg.getOffset());
            if (ts.moveNext()) {
                Token<? extends JsTokenId> loadFormToken = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON));
                if (loadFormToken != null && loadFormToken.id() == JsTokenId.IDENTIFIER && interceptedName.equals(loadFormToken.text().toString())) {
                    int loadFormOffset = loadFormToken.offset(th);
                    Token<? extends JsTokenId> token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.OPERATOR_ASSIGNMENT, JsTokenId.OPERATOR_SEMICOLON));
                    if (token != null && token.id() == JsTokenId.OPERATOR_ASSIGNMENT) {
                        token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON,
                                JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_LEFT_PAREN));
                        if (token != null && token.id() == JsTokenId.IDENTIFIER) {
                            String objectName = token.text().toString();
                            JsObject jsForm = ((JsObject) scope).getProperty(objectName);
                            if (jsForm != null) {
                                try {
                                    FileObject fo = jsForm.getFileObject();
                                    Collection<TypeUsage> apiFormTypes = InterceptorUtils.getModuleExposedTypes(fo, loadFormOffset, factory, FORM_MODULE_NAME);
                                    apiFormTypes.stream().forEach((apiFormType) -> {
                                        jsForm.addAssignment(apiFormType, apiFormType.getOffset());
                                    });
                                    fillJsForm(fo, factory, loadFormOffset, jsForm);
                                } catch (Exception ex) {
                                    Logger.getLogger(FormInterceptor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }

    protected abstract void fillJsForm(FileObject fo, ModelElementFactory factory, int loadFormOffset, JsObject jsForm) throws Exception;
}
