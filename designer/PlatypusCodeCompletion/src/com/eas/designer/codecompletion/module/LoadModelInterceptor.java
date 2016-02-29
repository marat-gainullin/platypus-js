/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion.module;

import com.eas.client.SqlQuery;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.codecompletion.InterceptorUtils;
import java.util.Collection;
import java.util.regex.Pattern;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class LoadModelInterceptor extends ModelInterceptor {

    private static final String LOAD_MODEL_NAME = "loadModel";
    private static final Pattern PATTERN = Pattern.compile(".+\\." + LOAD_MODEL_NAME);
    private static final String ENTITY_MODULE_NAME = "application-platypus-entity"/*Crazy RequireJsIndexer! It should be datamodel/application-platypus-entity */;
    private static final String FIELD_MODULE_NAME = "field"/*Crazy RequireJsIndexer! It should be core/field */;
    private static final String PARAMETER_MODULE_NAME = "parameter"/*Crazy RequireJsIndexer! It should be core/parameter */;
    private static final String SCHEMA_PROP_NAME = "schema";
    private static final String LENGTH_PROP_NAME = "length";
    private static final String PARAMS_PROP_NAME = "params";

    public LoadModelInterceptor() {
        super(LOAD_MODEL_NAME);
    }

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    protected void fillJsModel(FileObject fo, ModelElementFactory factory, int loadModelOffset, JsObject jsModel) throws DataObjectNotFoundException, Exception {
        DataObject dataObject = DataObject.find(fo);
        if (dataObject instanceof PlatypusModuleDataObject) {
            PlatypusModuleDataObject modelContainer = (PlatypusModuleDataObject) dataObject;
            if (modelContainer.isModelRead()) {
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
                        Collection<TypeUsage> apiEntityTypes = InterceptorUtils.getModuleExposedTypes(fo, loadModelOffset, factory, ENTITY_MODULE_NAME);
                        apiEntityTypes.stream().forEach((apiEntityType) -> {
                            jsEntity.addAssignment(apiEntityType, apiEntityType.getOffset());
                        });
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
                            Collection<TypeUsage> apiFieldTypes = InterceptorUtils.getModuleExposedTypes(fo, loadModelOffset, factory, FIELD_MODULE_NAME);
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
                            Collection<TypeUsage> apiParameterTypes = InterceptorUtils.getModuleExposedTypes(fo, loadModelOffset, factory, PARAMETER_MODULE_NAME);
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
        }
    }
}
