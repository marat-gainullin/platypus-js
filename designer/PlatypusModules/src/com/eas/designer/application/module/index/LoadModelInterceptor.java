/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.index;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.csl.api.OffsetRange;
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
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

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
            TokenHierarchy<?> th = snapshot.getTokenHierarchy();
            TokenSequence<? extends JsTokenId> ts = (TokenSequence<? extends JsTokenId>) th.tokenSequence();
            FunctionArgument moduleNameArg = args.iterator().next();
            ts.move(moduleNameArg.getOffset());
            if (ts.moveNext()) {
                Token<? extends JsTokenId> loadModelToken = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON));
                int loadModelOffset = loadModelToken.offset(th);
                if (loadModelToken != null && loadModelToken.id() == JsTokenId.IDENTIFIER && "loadModel".equals(loadModelToken.text().toString())) {
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
                                    DataObject dataObject = DataObject.find(fo);
                                    if (dataObject instanceof PlatypusModuleDataObject) {
                                        PlatypusModuleDataObject modelContainer = (PlatypusModuleDataObject) dataObject;
                                        ApplicationDbModel model = modelContainer.getModel();
                                        for (ApplicationDbEntity modelEntity : model.getEntities().values()) {
                                            if (modelEntity.getName() != null && !modelEntity.getName().isEmpty()) {
                                                JsObject jsEntity = factory.newObject(jsModel, modelEntity.getName(), new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                TypeUsage tu = factory.newType(modelEntity.getQueryName(), loadModelOffset, true);
                                                jsEntity.addAssignment(tu, loadModelOffset);
                                                jsModel.addProperty(jsEntity.getName(), jsEntity);
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    Logger.getLogger(LoadModelInterceptor.class.getName()).log(Level.SEVERE, null, ex);
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
