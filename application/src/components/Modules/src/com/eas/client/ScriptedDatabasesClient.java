package com.eas.client;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.cache.FreqCache;
import com.eas.client.exceptions.UnboundSqlParameterException;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.queries.PlatypusScriptedFlowProvider;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.client.scripts.store.Dom2ModelDocument;
import com.eas.script.ScriptUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Multi data source client. It allows to use js modules as datasources,
 * validators and appliers.
 *
 * @author mg
 */
public class ScriptedDatabasesClient extends DatabasesClient {

    protected static final String JAVASCRIPT_QUERY_CONTENTS = "javascript query";
    // key - validator name, value datasources list
    protected Map<String, Collection<String>> validators = new HashMap<>();
    //
    protected FreqCache<String, SqlQuery> scriptedQueries = new FreqCache<String, SqlQuery>() {

        private void readScriptFields(String aQueryId, JSObject sSchema, Fields fields) {
            Object oLength = sSchema.getMember("length");
            if (oLength instanceof Number) {
                int length = ((Number) oLength).intValue();
                for (int i = 0; i < length; i++) {
                    Object oElement = sSchema.getSlot(i);
                    if (oElement instanceof JSObject) {
                        JSObject sElement = (JSObject) oElement;
                        Object oFieldName = ScriptUtils.toJava(sElement.hasMember("name") ? sElement.getMember("name") : null);
                        if (oFieldName instanceof String && !((String) oFieldName).isEmpty()) {
                            String sFieldName = (String) oFieldName;
                            Field field = fields instanceof Parameters ? new Parameter() : new Field();
                            field.setTypeInfo(DataTypeInfo.OTHER);
                            fields.add(field);
                            field.setName(sFieldName);
                            field.setOriginalName(sFieldName);
                            Object oEntity = ScriptUtils.toJava(sElement.hasMember("entity") ? sElement.getMember("entity") : null);
                            if (oEntity instanceof String && !((String) oEntity).isEmpty()) {
                                field.setTableName((String) oEntity);
                            } else {
                                field.setTableName(aQueryId);
                            }
                            Object oDescription = ScriptUtils.toJava(sElement.hasMember("description") ? sElement.getMember("description") : null);
                            if (oDescription instanceof String && !((String) oDescription).isEmpty()) {
                                field.setDescription((String) oDescription);
                            }
                            Object oType = sElement.getMember("type");
                            if (oType instanceof JSObject && ((JSObject) oType).isFunction()) {
                                Object ofName = ScriptUtils.toJava(((JSObject) oType).getMember("name"));
                                if (ofName instanceof String) {
                                    String fName = (String) ofName;
                                    if (String.class.getSimpleName().equals(fName)) {
                                        field.setTypeInfo(DataTypeInfo.VARCHAR.copy());
                                    } else if (Number.class.getSimpleName().equals(fName)) {
                                        field.setTypeInfo(DataTypeInfo.DECIMAL.copy());
                                    } else if (Boolean.class.getSimpleName().equals(fName)) {
                                        field.setTypeInfo(DataTypeInfo.BOOLEAN.copy());
                                    } else if (Date.class.getSimpleName().equals(fName)) {
                                        field.setTypeInfo(DataTypeInfo.TIMESTAMP.copy());
                                    }
                                }
                            }
                            Object oRequired = ScriptUtils.toJava(sElement.hasMember("required") ? sElement.getMember("required") : null);
                            if (oRequired instanceof Boolean) {
                                boolean bRequired = (Boolean) oRequired;
                                field.setNullable(!bRequired);
                            }
                            Object oKey = ScriptUtils.toJava(sElement.hasMember("key") ? sElement.getMember("key") : null);
                            if (oKey instanceof Boolean) {
                                boolean bKey = (Boolean) oKey;
                                field.setPk(bKey);
                                field.setNullable(false);
                            }
                            Object oRef = sElement.hasMember("ref") ? sElement.getMember("ref") : null;
                            if (oRef instanceof JSObject) {
                                JSObject sRef = (JSObject) oRef;
                                Object oProperty = ScriptUtils.toJava(sRef.hasMember("property") ? sRef.getMember("property") : null);
                                if (oProperty instanceof String) {
                                    String sProperty = (String) oProperty;
                                    if (!sProperty.isEmpty()) {
                                        Object oRefEntity = sRef.hasMember("entity") ? sRef.getMember("entity") : null;
                                        String sRefEntity;
                                        if (oRefEntity instanceof String && !((String) oRefEntity).isEmpty()) {
                                            sRefEntity = (String) oRefEntity;
                                        } else {
                                            sRefEntity = aQueryId;
                                        }
                                        field.setFk(new ForeignKeySpec(null, aQueryId, field.getName(), null, ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, null, sRefEntity, sProperty, null));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected SqlQuery getNewEntry(final String aQueryId) throws Exception {
            ApplicationElement ae = appCache.get(aQueryId);
            if (ae != null && ae.getType() == ClientConstants.ET_COMPONENT) {
                final String aDatasourceId = aQueryId;
                SqlQuery query = new SqlQuery(ScriptedDatabasesClient.this) {
                    @Override
                    public SqlCompiledQuery compile() throws UnboundSqlParameterException, Exception {
                        SqlCompiledQuery compiled = new SqlCompiledQuery(ScriptedDatabasesClient.this, aDatasourceId, aQueryId, JAVASCRIPT_QUERY_CONTENTS, getParameters(), getFields(), Collections.<String>emptySet(), Collections.<String>emptySet());
                        return compiled;
                    }

                    @Override
                    public boolean isPublicAccess() {
                        return true;
                    }
                };
                JSObject schemaContainer = createModule(aQueryId);
                if (schemaContainer != null) {
                    Fields fields = new Fields();
                    query.setFields(fields);
                    query.setEntityId(aQueryId);
                    query.setDbId(aDatasourceId);
                    Object oSchema = schemaContainer.hasMember("schema") ? schemaContainer.getMember("schema") : null;
                    if (oSchema instanceof JSObject) {
                        readScriptFields(aQueryId, (JSObject) oSchema, fields);
                        Parameters params;
                        Object oParams = schemaContainer.hasMember("params") ? schemaContainer.getMember("params") : null;
                        if (oParams instanceof JSObject) {
                            params = new Parameters();
                            readScriptFields(aQueryId, (JSObject) oParams, params);
                        } else {
                            ApplicationElement moduleQuery = getAppCache().get(aQueryId);
                            ApplicationModel<?, ?, ?, ?> model = Dom2ModelDocument.transform(ScriptedDatabasesClient.this, moduleQuery.getContent());
                            params = model != null ? model.getParameters() : new Parameters();
                        }
                        params.toCollection().stream().forEach((p) -> {
                            query.putParameter(p.getName(), p.getTypeInfo(), null);
                        });
                        return query;
                    } else {
                        throw new IllegalStateException(" datasource module: " + aQueryId + " doesn't contain schema");
                    }
                } else {
                    throw new IllegalStateException(" datasource module: " + aQueryId + " not found");
                }
            } else {
                return null;
            }
        }
    };

    protected JSObject createModule(String aModuleId) throws Exception {
        PlatypusScriptedResource.executeScriptResource(aModuleId);
        return ScriptUtils.createModule(aModuleId);
    }

    /**
     * @inheritDoc
     */
    public ScriptedDatabasesClient(AppCache anAppCache, String aDefaultDatasourceName, boolean aAutoFillMetadata) throws Exception {
        super(anAppCache, aDefaultDatasourceName, aAutoFillMetadata);
    }

    /**
     * Adds transaction validator module. Validator modules are used in commit
     * to verify transaction changes log. They mey consume particuled changes
     * and optionally send them to custom data store or a service. If validator
     * module detects an errorneous data changes, than it should thor ab
     * exception.
     *
     * @param aModuleId
     */
    public void addValidator(String aModuleId, Collection<String> aDatasources) {
        validators.put(aModuleId, aDatasources);
    }

    @Override
    public void clearQueries() throws Exception {
        super.clearQueries();
        scriptedQueries.clear();
    }

    @Override
    public void appEntityChanged(String aEntityId) throws Exception {
        super.appEntityChanged(aEntityId);
        if (scriptedQueries.containsKey(aEntityId)) {
            clearQueries();
        }
    }

    @Override
    public SqlQuery getAppQuery(final String aQueryId, boolean aCopy) throws Exception {
        SqlQuery query = scriptedQueries.get(aQueryId);
        if (query == null) {
            query = super.getAppQuery(aQueryId, aCopy);
        }
        return query;
    }

    @Override
    public synchronized DbMetadataCache getDbMetadataCache(String aDatasourceId) throws Exception {
        ApplicationElement appElement = aDatasourceId != null ? getAppCache().get(aDatasourceId) : null;
        if (appElement == null || appElement.getType() != ClientConstants.ET_COMPONENT) {
            return super.getDbMetadataCache(aDatasourceId);
        } else {
            return null;
        }
    }

    @Override
    public FlowProvider createFlowProvider(String aDbId, final String aEntityId, String aSqlClause, final Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        if (JAVASCRIPT_QUERY_CONTENTS.equals(aSqlClause)) {
            JSObject dataFeeder = createModule(aEntityId);
            if (dataFeeder != null) {
                return new PlatypusScriptedFlowProvider(ScriptedDatabasesClient.this, aExpectedFields, dataFeeder);
            } else {
                throw new IllegalStateException(" datasource module: " + aEntityId + " not found");
            }
        } else {
            return super.createFlowProvider(aDbId, aEntityId, aSqlClause, aExpectedFields, aReadRoles, aWriteRoles);
        }
    }

    @Override
    protected int commit(final String aDatasourceId, final List<Change> aLog) throws Exception {
        for (String validatorName : validators.keySet()) {
            Collection<String> datasourcesUnderControl = validators.get(validatorName);
            // aDatasourceId must be null or it must be contained in datasourcesUnderControl to be validated by script validator
            if (((datasourcesUnderControl == null || datasourcesUnderControl.isEmpty()) && aDatasourceId == null) || (datasourcesUnderControl != null && datasourcesUnderControl.contains(aDatasourceId))) {
                JSObject validator = ScriptUtils.createModule(validatorName);
                if (validator != null) {
                    Object oValidate = validator.getMember("validate");
                    if (oValidate instanceof JSObject) {
                        JSObject fValidate = (JSObject) oValidate;
                        Object oResult = ScriptUtils.toJava(fValidate.call(validator, new Object[]{ScriptUtils.toJs(aLog.toArray()), aDatasourceId}));
                        if (oResult != null && Boolean.FALSE.equals(ScriptUtils.toJava(oResult))) {
                            break;
                        }
                    } else {
                        Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.WARNING, "\"validate\" method couldn''t be found in {0} module.", validatorName);
                    }
                } else {
                    Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.WARNING, "{0} constructor couldn''t be found", validatorName);
                }
            }
        }
        if (aDatasourceId != null) {
            ApplicationElement appElement = getAppCache().get(aDatasourceId);
            if (appElement != null && appElement.getType() == ClientConstants.ET_COMPONENT) {
                JSObject dataSourceApplier = createModule(aDatasourceId);
                if (dataSourceApplier != null) {
                    Object oApply = dataSourceApplier.getMember("apply");
                    if (oApply instanceof JSObject && ((JSObject) oApply).isFunction()) {
                        JSObject fApply = (JSObject) oApply;
                        fApply.call(dataSourceApplier, new Object[]{ScriptUtils.toJs(aLog.toArray())});
                    }
                }
            }
        }
        boolean consumed = true;
        for (Change change : aLog) {
            if (!change.consumed) {
                consumed = false;
            }
        }
        if (!consumed) {
            return super.commit(aDatasourceId, aLog);
        } else {
            aLog.clear();
            return 0;
        }
    }
}
