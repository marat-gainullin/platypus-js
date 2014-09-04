package com.eas.client;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.queries.JsQuery;
import com.eas.client.queries.PlatypusScriptedFlowProvider;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.SqlQuery;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.ScriptUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
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

    protected ApplicationSourceIndexer indexer;
    // key - validator name, value datasources list
    protected Map<String, Collection<String>> validators = new HashMap<>();

    /**
     * @inheritDoc
     */
    public ScriptedDatabasesClient(String aDefaultDatasourceName, ApplicationSourceIndexer aIndexer, boolean aAutoFillMetadata) throws Exception {
        super(aDefaultDatasourceName, aAutoFillMetadata);
        indexer = aIndexer;
    }

    protected JSObject createModule(String aModuleName) throws Exception {
        ScriptedResource.executeScriptResource(aModuleName);
        return ScriptUtils.createModule(aModuleName);
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
    public synchronized DbMetadataCache getDbMetadataCache(String aDatasourceName) throws Exception {
        AppElementFiles files = indexer.nameToFiles(aDatasourceName);
        if (files == null || !files.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
            return super.getDbMetadataCache(aDatasourceName);
        } else {
            return null;
        }
    }

    @Override
    public FlowProvider createFlowProvider(String aDbId, final String aEntityId, String aSqlClause, final Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        if (JsQuery.JAVASCRIPT_QUERY_CONTENTS.equals(aSqlClause)) {
            JSObject dataFeeder = createModule(aEntityId);
            if (dataFeeder != null) {
                return new PlatypusScriptedFlowProvider(ScriptedDatabasesClient.this, aExpectedFields, dataFeeder);
            } else {
                throw new IllegalStateException(" datasource module: " + aEntityId + " is not found");
            }
        } else {
            return super.createFlowProvider(aDbId, aEntityId, aSqlClause, aExpectedFields, aReadRoles, aWriteRoles);
        }
    }

    @Override
    protected int commit(final String aDatasourceName, final List<Change> aLog, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            validate(aDatasourceName, aLog, (Void v) -> {
                try {
                    scriptApply(aDatasourceName, aLog, (Integer affectedInModules) -> {
                        for (int i = aLog.size() - 1; i >= 0; i--) {
                            if (aLog.get(i).consumed) {
                                aLog.remove(i);
                            }
                        }
                        try {
                            super.commit(aDatasourceName, aLog, (Integer affectedInBase) -> {
                                onSuccess.accept((affectedInBase != null ? affectedInBase : 0) + (affectedInModules != null ? affectedInModules : 0));
                            }, onFailure);
                        } catch (Exception ex) {
                            if (onFailure != null) {
                                onFailure.accept(ex);
                            }
                        }
                    }, onFailure);
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
            return 0;
        } else {
            validate(aDatasourceName, aLog, null, null);
            int affectedInModules = scriptApply(aDatasourceName, aLog, null, null);
            for (int i = aLog.size() - 1; i >= 0; i--) {
                if (aLog.get(i).consumed) {
                    aLog.remove(i);
                }
            }
            int affectedInBase = super.commit(aDatasourceName, aLog, null, null);
            return affectedInModules + affectedInBase;
        }
    }

    private int scriptApply(final String aDatasourceName, final List<Change> aLog, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        List<CallPoint> toBeCalled = new ArrayList<>();
        if (aDatasourceName != null) {
            AppElementFiles files = indexer.nameToFiles(aDatasourceName);
            if (files.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
                JSObject module = createModule(aDatasourceName);
                if (module != null) {
                    Object oApply = module.getMember("apply");
                    if (oApply instanceof JSObject && ((JSObject) oApply).isFunction()) {
                        JSObject applyFunction = (JSObject) oApply;
                        toBeCalled.add(new CallPoint(module, applyFunction));
                    }
                }
            }
        }
        if (onSuccess != null) {
            if (toBeCalled.isEmpty()) {
                onSuccess.accept(0);
            } else {
                CommitProcess process = new CommitProcess(toBeCalled.size(), onSuccess, onFailure);
                toBeCalled.stream().forEach((v) -> {
                    v.function.call(v.module, new Object[]{ScriptUtils.toJs(aLog.toArray()),
                        new Consumer<Integer>() {

                            @Override
                            public void accept(Integer t) {
                                process.complete(t != null ? t : 0, null);
                            }
                        },
                        new Consumer<Exception>() {

                            @Override
                            public void accept(Exception ex) {
                                process.complete(0, ex);
                            }
                        }
                    });
                });
            }
            return 0;
        } else {
            int affected = 0;
            for (CallPoint v : toBeCalled) {
                Object oAffected = ScriptUtils.toJava(v.function.call(v.module, new Object[]{ScriptUtils.toJs(aLog.toArray())}));
                if (oAffected instanceof Number) {
                    affected += ((Number) oAffected).intValue();
                }
            }
            return affected;
        }
    }

    private static class CallPoint {

        public JSObject module;
        public JSObject function;

        public CallPoint(JSObject aModule, JSObject aFunction) {
            super();
            module = aModule;
            function = aFunction;
        }
    }

    private static class ValidateProcess {

        public int expected;
        public int completed;
        public int rowsAffected;
        public Set<Exception> exceptions = new HashSet<>();
        public Consumer<Void> onSuccess;
        public Consumer<Exception> onFailure;

        public ValidateProcess(int aExpected, Consumer<Void> aOnSuccess, Consumer<Exception> aOnFailure) {
            expected = aExpected;
            onSuccess = aOnSuccess;
            onFailure = aOnFailure;
        }

        public synchronized void complete(Exception aFailureCause) {
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == expected) {
                if (exceptions.isEmpty()) {
                    if (onSuccess != null) {
                        onSuccess.accept(null);
                    }
                } else {
                    if (onFailure != null) {
                        StringBuilder eMessagesSum = new StringBuilder();
                        exceptions.stream().forEach((ex) -> {
                            if (eMessagesSum.length() > 0) {
                                eMessagesSum.append("\n");
                            }
                            eMessagesSum.append(ex.getMessage());
                        });
                        onFailure.accept(new IllegalStateException(eMessagesSum.toString()));
                    }
                }
            }
        }
    }

    private void validate(final String aDatasourceName, final List<Change> aLog, Consumer<Void> onSuccess, Consumer<Exception> onFailure) {
        List<CallPoint> toBeCalled = new ArrayList<>();
        validators.keySet().stream().forEach((validatorName) -> {
            Collection<String> datasourcesUnderControl = validators.get(validatorName);
            if (((datasourcesUnderControl == null || datasourcesUnderControl.isEmpty()) && aDatasourceName == null) || (datasourcesUnderControl != null && datasourcesUnderControl.contains(aDatasourceName))) {
                JSObject module = ScriptUtils.createModule(validatorName);
                if (module != null) {
                    Object oValidate = module.getMember("validate");
                    if (oValidate instanceof JSObject) {
                        JSObject validateFunction = (JSObject) oValidate;
                        toBeCalled.add(new CallPoint(module, validateFunction));
                    } else {
                        Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.WARNING, "\"validate\" method couldn''t be found in {0} module.", validatorName);
                    }
                } else {
                    Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.WARNING, "{0} constructor couldn''t be found", validatorName);
                }
            }
        });
        if (onSuccess != null) {
            if (toBeCalled.isEmpty()) {
                onSuccess.accept(null);
            } else {
                ValidateProcess process = new ValidateProcess(toBeCalled.size(), onSuccess, onFailure);
                toBeCalled.stream().forEach((v) -> {
                    v.function.call(v.module, new Object[]{ScriptUtils.toJs(aLog.toArray()), aDatasourceName,
                        new Consumer<Void>() {

                            @Override
                            public void accept(Void t) {
                                process.complete(null);
                            }
                        },
                        new Consumer<Exception>() {

                            @Override
                            public void accept(Exception ex) {
                                process.complete(ex);
                            }
                        }
                    });
                });
            }
        } else {
            toBeCalled.stream().forEach((v) -> {
                v.function.call(v.module, new Object[]{ScriptUtils.toJs(aLog.toArray()), aDatasourceName});
            });
        }
    }
}
