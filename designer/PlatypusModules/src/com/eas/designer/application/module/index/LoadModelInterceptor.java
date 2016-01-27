/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.index;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.model.DeclarationScope;
import org.netbeans.modules.javascript2.editor.model.JsFunction;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionArgument;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class LoadModelInterceptor implements FunctionInterceptor {

    private static final Pattern PATTERN = Pattern.compile(".+\\.loadModel");

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    public Collection<TypeUsage> intercept(Snapshot snapshot, String name, JsObject globalObject,
            DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        if (!args.isEmpty()) {
            FunctionArgument moduleName = args.iterator().next();
            TokenSequence<?> sequence = snapshot.getTokenHierarchy().tokenSequence();
            int oldOffset = sequence.move(moduleName.getOffset());
            if (sequence.movePrevious()) {
                Token lParentToken = sequence.offsetToken();
                if (JsTokenId.BRACKET_LEFT_PAREN == lParentToken.id()) {
                    if (sequence.movePrevious()) {
                        Token loadModelToken = sequence.offsetToken();
                        if (JsTokenId.IDENTIFIER == loadModelToken.id() && "loadModel".equals(loadModelToken.text())) {
                            if (sequence.movePrevious()) {
                                Token dotToken = sequence.offsetToken();
                                if (JsTokenId.OPERATOR_DOT == dotToken.id()) {
                                    if (sequence.movePrevious()) {
                                        Token pOrOrmToken = sequence.offsetToken();
                                        if (JsTokenId.IDENTIFIER == pOrOrmToken.id()) {
                                            while (scope != globalObject) {
                                                if (scope instanceof JsFunction) {
                                                    JsFunction funcScope = (JsFunction) scope;
                                                    JsObject pOrOrmVar = funcScope.getProperty(pOrOrmToken.text().toString());
                                                    JsObject pOrOrmArg = funcScope.getParameter(pOrOrmToken.text().toString());
                                                }
                                                scope = scope.getParentScope();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }

}
