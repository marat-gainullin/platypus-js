/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.index;

import com.eas.client.SqlQuery;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.explorer.project.RequireJsSupportIndexer;
import java.io.IOException;
import java.util.ArrayList;
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
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class LoadModelInterceptor implements FunctionInterceptor {

    private static final Pattern PATTERN = Pattern.compile(".+\\.loadModel");
    private static final String ENTITY_MODULE_NAME = "application-platypus-entity"/*Crazy RequireJsIndexer! It should be datamodel/application-platypus-entity */;
    private static final String FIELD_MODULE_NAME = "field"/*Crazy RequireJsIndexer! It should be core/field */;
    private static final String PARAMETER_MODULE_NAME = "parameter"/*Crazy RequireJsIndexer! It should be core/parameter */;
    private static final String SCHEMA_PROP_NAME = "schema";
    private static final String LENGTH_PROP_NAME = "length";
    private static final String PARAMS_PROP_NAME = "params";

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    protected Collection<TypeUsage> getModuleExposedTypes(FileObject aSource, int aOffset, ModelElementFactory aFactory, String aModuleName) throws IOException {
        QuerySupport querySupport = getQuerySupport(aSource);
        if (querySupport != null) {
            Collection<? extends IndexResult> result = querySupport.query(RequireJsSupportIndexer.FIELD_MODULE_NAME, aModuleName, QuerySupport.Kind.PREFIX, RequireJsSupportIndexer.FIELD_MODULE_NAME, RequireJsSupportIndexer.FIELD_EXPOSED_TYPES);
            if (result != null && !result.isEmpty()) {
                Collection<TypeUsage> types = new ArrayList<>();
                for (IndexResult indexResult : result) {
                    String[] values = indexResult.getValues(RequireJsSupportIndexer.FIELD_EXPOSED_TYPES);
                    String mn = indexResult.getValue(RequireJsSupportIndexer.FIELD_MODULE_NAME);
                    if (mn.equals(aModuleName)) {
                        for (String stype : values) {
                            String[] typeParts = stype.split(":");
                            String typeName = typeParts[0];
                            int offset = Integer.parseInt(typeParts[1]);
                            boolean resolved = "1".equals(typeParts[2]);
                            types.add(aFactory.newType(typeName, aOffset/*offset*/, resolved));
                        }
                    }
                }
                return types;
            }
        }
        return Collections.emptySet();
    }

    protected QuerySupport getQuerySupport(FileObject aSource) throws IOException {
        Project project = FileOwnerQuery.getOwner(aSource);
        if (project != null) {
            final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots(project, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
            QuerySupport querySupport = QuerySupport.forRoots(RequireJsSupportIndexer.Factory.NAME, RequireJsSupportIndexer.Factory.VERSION, roots.toArray(new FileObject[]{}));
            return querySupport;
        } else {
            return null;
        }
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
                if (loadModelToken != null && loadModelToken.id() == JsTokenId.IDENTIFIER && "loadModel".equals(loadModelToken.text().toString())) {
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
                                    DataObject dataObject = DataObject.find(fo);
                                    if (dataObject instanceof PlatypusModuleDataObject) {
                                        PlatypusModuleDataObject modelContainer = (PlatypusModuleDataObject) dataObject;
                                        ApplicationDbModel model = modelContainer.getModel();
                                        TypeUsage numberType = factory.newType("Number", loadModelOffset, true);
                                        TypeUsage arrayType = factory.newType("Array", loadModelOffset, true);
                                        TypeUsage objectType = factory.newType("Object", loadModelOffset, true);
                                        for (ApplicationDbEntity modelEntity : model.getEntities().values()) {
                                            if (modelEntity.getName() != null && !modelEntity.getName().isEmpty()) {
                                                JsObject jsEntity = factory.newObject(jsModel, modelEntity.getName(), new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                TypeUsage entityQueryType = factory.newType(modelEntity.getQueryName(), loadModelOffset, false);
                                                TypeUsage localEntityType = factory.newType(jsModel.getFullyQualifiedName() + "." + jsEntity.getName(), loadModelOffset, true);
                                                //
                                                Collection<TypeUsage> apiEntityTypes = getModuleExposedTypes(fo, loadModelOffset, factory, ENTITY_MODULE_NAME);
                                                for (TypeUsage apiEntityType : apiEntityTypes) {
                                                    jsEntity.addAssignment(apiEntityType, apiEntityType.getOffset());
                                                }
                                                //
                                                jsEntity.addAssignment(arrayType, loadModelOffset);
                                                jsEntity.addAssignment(entityQueryType, loadModelOffset);
                                                jsEntity.addAssignment(localEntityType, loadModelOffset);
                                                //
                                                SqlQuery sqlQuery = modelEntity.getQuery();
                                                if (sqlQuery != null) {
                                                    Fields fields = sqlQuery.getFields();
                                                    JsObject jsSchema = factory.newObject(jsEntity, SCHEMA_PROP_NAME, new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                    jsSchema.addAssignment(factory.newType(jsEntity.getFullyQualifiedName() + "." + SCHEMA_PROP_NAME, loadModelOffset, true), loadModelOffset);
                                                    jsSchema.addAssignment(objectType, loadModelOffset);
                                                    JsObject jsSchemaLength = factory.newObject(jsSchema, LENGTH_PROP_NAME, new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                    jsSchemaLength.addAssignment(numberType, -1);
                                                    jsSchema.addProperty(jsSchemaLength.getName(), jsSchemaLength);
                                                    Collection<TypeUsage> apiFieldTypes = getModuleExposedTypes(fo, loadModelOffset, factory, FIELD_MODULE_NAME);
                                                    for (int i = 1; i < fields.getFieldsCount(); i++) {
                                                        Field field = fields.get(i);
                                                        JsObject jsField = factory.newObject(jsSchema, field.getName(), new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                        apiFieldTypes.stream().forEach((apiFieldType) -> {
                                                            jsField.addAssignment(apiFieldType, apiFieldType.getOffset());
                                                        });
                                                        jsSchema.addProperty(jsField.getName(), jsField);
                                                    }
                                                    jsEntity.addProperty(jsSchema.getName(), jsSchema);
                                                    //
                                                    Parameters params = sqlQuery.getParameters();
                                                    JsObject jsParams = factory.newObject(jsEntity, PARAMS_PROP_NAME, new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                    jsParams.addAssignment(factory.newType(jsEntity.getFullyQualifiedName() + "." + PARAMS_PROP_NAME, loadModelOffset, true), loadModelOffset);
                                                    jsParams.addAssignment(objectType, loadModelOffset);
                                                    JsObject jsParamsLength = factory.newObject(jsParams, LENGTH_PROP_NAME, new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                    jsParamsLength.addAssignment(numberType, loadModelOffset);
                                                    jsParams.addProperty(jsParamsLength.getName(), jsParamsLength);
                                                    Collection<TypeUsage> apiParameterTypes = getModuleExposedTypes(fo, loadModelOffset, factory, PARAMETER_MODULE_NAME);
                                                    for (int i = 1; i < params.getParametersCount(); i++) {
                                                        Parameter parameter = params.get(i);
                                                        JsObject jsParameter = factory.newObject(jsParams, parameter.getName(), new OffsetRange(loadModelOffset, loadModelOffset), false);
                                                        apiParameterTypes.stream().forEach((apiParameterType) -> {
                                                            jsParameter.addAssignment(apiParameterType, apiParameterType.getOffset());
                                                        });
                                                        jsParams.addProperty(jsParameter.getName(), jsParameter);
                                                    }
                                                    jsEntity.addProperty(jsParams.getName(), jsParams);
                                                    //
                                                    jsModel.addProperty(jsEntity.getName(), jsEntity);
                                                }
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
