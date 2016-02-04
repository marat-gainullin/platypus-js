/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.index;

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
public abstract class ModelInterceptor implements FunctionInterceptor {
    
    private static final String MODEL_MODULE_NAME = "application-platypus-model"/*Crazy RequireJsIndexer! It should be datamodel/application-platypus-entity */;
    private final String interceptedName;
    
    public ModelInterceptor(String aInterceptedName) {
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
                Token<? extends JsTokenId> loadModelToken = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON));
                if (loadModelToken != null && loadModelToken.id() == JsTokenId.IDENTIFIER && interceptedName.equals(loadModelToken.text().toString())) {
                    int loadModelOffset = loadModelToken.offset(th);
                    Token<? extends JsTokenId> token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.OPERATOR_ASSIGNMENT, JsTokenId.OPERATOR_SEMICOLON));
                    if (token != null && token.id() == JsTokenId.OPERATOR_ASSIGNMENT) {
                        token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON,
                                JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_LEFT_PAREN));
                        if (token != null && token.id() == JsTokenId.IDENTIFIER) {
                            String objectName = token.text().toString();
                            JsObject jsModel = ((JsObject) scope).getProperty(objectName);
                            if (jsModel != null) {
                                try {
                                    FileObject fo = jsModel.getFileObject();
                                    Collection<TypeUsage> apiModelTypes = TemplateInterceptor.getModuleExposedTypes(fo, loadModelOffset, factory, MODEL_MODULE_NAME);
                                    apiModelTypes.stream().forEach((apiModelType) -> {
                                        jsModel.addAssignment(apiModelType, apiModelType.getOffset());
                                    });
                                    fillJsModel(fo, factory, loadModelOffset, jsModel);
                                } catch (Exception ex) {
                                    Logger.getLogger(ModelInterceptor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }

    protected abstract void fillJsModel(FileObject fo, ModelElementFactory factory, int loadModelOffset, JsObject jsModel) throws Exception;
}
