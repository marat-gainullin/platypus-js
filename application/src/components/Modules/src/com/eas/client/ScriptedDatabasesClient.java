package com.eas.client;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.changes.Change;
import com.eas.client.metadata.Fields;
import com.eas.client.queries.ScriptedQuery;
import com.eas.client.queries.ScriptedFlowProvider;
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
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Multi data source client. It allows to use js modules as datasources,
 * validators and appliers.
 *
 * @author mg
 */
public class ScriptedDatabasesClient extends DatabasesClient {

    protected PlatypusIndexer indexer;
    // key - validator name, value datasources list
    protected Map<String, Collection<String>> validators = new HashMap<>();

    /**
     * 
     * @param aDefaultDatasourceName
     * @param aIndexer
     * @param aAutoFillMetadata
     * @param aValidators
     * @param aMaxJdbcThreads
     * @throws Exception 
     */
    public ScriptedDatabasesClient(String aDefaultDatasourceName, PlatypusIndexer aIndexer, boolean aAutoFillMetadata, Map<String, Collection<String>> aValidators, int aMaxJdbcThreads) throws Exception {
        this(aDefaultDatasourceName, aIndexer, aAutoFillMetadata, aMaxJdbcThreads);
        validators.putAll(aValidators);
    }

    /**
     * 
     * @param aDefaultDatasourceName
     * @param aIndexer
     * @param aAutoFillMetadata
     * @param aMaxJdbcThreads
     * @throws Exception 
     */
    public ScriptedDatabasesClient(String aDefaultDatasourceName, PlatypusIndexer aIndexer, boolean aAutoFillMetadata, int aMaxJdbcThreads) throws Exception {
        super(aDefaultDatasourceName, aAutoFillMetadata, aMaxJdbcThreads);
        indexer = aIndexer;
    }

    protected JSObject createModule(String aModuleName) throws Exception {
        ScriptedResource.require(new String[]{aModuleName});
        return ScriptUtils.createModule(aModuleName);
    }

    /**
     * Adds transaction validator module. Validator modules are used in commit
     * to verify transaction changes log. They mey consume particuled changes
     * and optionally send them to custom data store or a service. If validator
     * module detects an errorneous data changes, than it should thor ab
     * exception.
     *
     * @param aModuleName
     * @param aDatasources
     */
    public void addValidator(String aModuleName, Collection<String> aDatasources) {
        validators.put(aModuleName, aDatasources);
    }

    @Override
    public synchronized DatabaseMdCache getDbMetadataCache(String aDatasourceName) throws Exception {
        AppElementFiles files = indexer.nameToFiles(aDatasourceName);
        if (files == null || !files.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
            return super.getDbMetadataCache(aDatasourceName);
        } else {
            return null;
        }
    }

    @Override
    public PlatypusJdbcFlowProvider createFlowProvider(String aDatasourceName, final String aEntityName, String aSqlClause, final Fields aExpectedFields) throws Exception {
        if (ScriptedQuery.JAVASCRIPT_QUERY_CONTENTS.equals(aSqlClause)) {
            JSObject dataFeeder = createModule(aDatasourceName);
            if (dataFeeder != null) {
                return new ScriptedFlowProvider(ScriptedDatabasesClient.this, aExpectedFields, dataFeeder);
            } else {
                throw new IllegalStateException("Datasource module: " + aDatasourceName + " is not found");
            }
        } else {
            return super.createFlowProvider(aDatasourceName, aEntityName, aSqlClause, aExpectedFields);
        }
    }

    @Override
    protected ApplyResult apply(final String aDatasourceName, final List<Change> aLog, Consumer<ApplyResult> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            validate(aDatasourceName, aLog, (Void v) -> {
                try {
                    AppElementFiles files = indexer.nameToFiles(aDatasourceName);
                    if (files != null && files.isModule()) {
                        JSObject module = createModule(aDatasourceName);
                        if (module != null) {
                            Object oApply = module.getMember("apply");
                            if (oApply instanceof JSObject && ((JSObject) oApply).isFunction()) {
                                JSObject applyFunction = (JSObject) oApply;
                                ScriptUtils.toJava(applyFunction.call(module, new Object[]{ScriptUtils.toJs(aLog.toArray()),
                                    new AbstractJSObject() {

                                        @Override
                                        public Object call(final Object thiz, final Object... args) {
                                            int affected = 0;
                                            if (args.length > 0) {
                                                Object oAffected = ScriptUtils.toJava(args[0]);
                                                if (oAffected instanceof Number) {
                                                    affected = ((Number) oAffected).intValue();
                                                }
                                            }
                                            onSuccess.accept(new ApplyResult(affected, new DummySqlConnection()));
                                            return null;
                                        }

                                    },
                                    new AbstractJSObject() {

                                        @Override
                                        public Object call(final Object thiz, final Object... args) {
                                            if (onFailure != null) {
                                                if (args.length > 0) {
                                                    if (args[0] instanceof Exception) {
                                                        onFailure.accept((Exception) args[0]);
                                                    } else {
                                                        onFailure.accept(new Exception(String.valueOf(ScriptUtils.toJava(args[0]))));
                                                    }
                                                } else {
                                                    onFailure.accept(new Exception("No error information from apply method"));
                                                }
                                            }
                                            return null;
                                        }
                                    }
                                }));
                            } else {
                                onFailure.accept(new IllegalStateException(String.format(APPLY_MISSING_MSG, aDatasourceName)));
                            }
                        } else {
                            onFailure.accept(new IllegalStateException(String.format(CANT_CREATE_MODULE_MSG, aDatasourceName)));
                        }
                    } else {
                        super.apply(aDatasourceName, aLog, onSuccess, onFailure);
                    }
                } catch (Exception ex) {
                    onFailure.accept(ex);
                }
            }, onFailure);
            return null;
        } else {
            validate(aDatasourceName, aLog, null, null);
            AppElementFiles files = indexer.nameToFiles(aDatasourceName);
            if (files != null && files.isModule()) {
                JSObject module = createModule(aDatasourceName);
                if (module != null) {
                    Object oApply = module.getMember("apply");
                    if (oApply instanceof JSObject && ((JSObject) oApply).isFunction()) {
                        JSObject applyFunction = (JSObject) oApply;
                        int affectedInModules = 0;
                        Object oAffected = ScriptUtils.toJava(applyFunction.call(module, new Object[]{ScriptUtils.toJs(aLog.toArray())}));
                        if (oAffected instanceof Number) {
                            affectedInModules = ((Number) oAffected).intValue();
                        }
                        return new ApplyResult(affectedInModules, new DummySqlConnection());
                    } else {
                        throw new IllegalStateException(String.format(APPLY_MISSING_MSG, aDatasourceName));
                    }
                } else {
                    throw new IllegalStateException(String.format(CANT_CREATE_MODULE_MSG, aDatasourceName));
                }
            } else {
                return super.apply(aDatasourceName, aLog, null, null);
            }
        }
    }
    protected static final String CANT_CREATE_MODULE_MSG = "Can't create module %s";
    protected static final String APPLY_MISSING_MSG = "Can't find apply function in module %s";

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
                            eMessagesSum.append(ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.toString());
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
                try {
                    JSObject module = createModule(validatorName);
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
                } catch (Exception ex) {
                    Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
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
                        new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                process.complete(null);
                                return null;
                            }
                        },
                        new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                if (args.length > 0) {
                                    if (args[0] instanceof Exception) {
                                        process.complete((Exception) args[0]);
                                    } else {
                                        process.complete(new Exception(String.valueOf(ScriptUtils.toJava(args[0]))));
                                    }
                                } else {
                                    process.complete(new Exception("No error information from validate method"));
                                }
                                return null;
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
