package com.eas.client;

import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.changes.Change;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.ECMAException;

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
        ScriptedResource.require(new String[]{aModuleName}, null);
        return Scripts.getSpace().createModule(aModuleName);
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

    protected static final String CANT_CREATE_MODULE_MSG = "Can't create module %s";

    private static class CallPoint {

        public JSObject module;
        public JSObject function;

        public CallPoint(JSObject aModule, JSObject aFunction) {
            super();
            module = aModule;
            function = aFunction;
        }
    }

    private static class CollectionAsyncProcess<T> {

        private final Collection<T> processed;
        private final Scripts.Space space;
        private final Consumer<Void> onSuccess;
        private final Consumer<Exception> onFailure;
        private int completed;
        private final Set<Exception> exceptions = new HashSet<>();

        public CollectionAsyncProcess(Collection<T> aProcessed, Scripts.Space aSpace, Consumer<Void> aOnSuccess, Consumer<Exception> aOnFailure) {
            processed = aProcessed;
            space = aSpace;
            onSuccess = aOnSuccess;
            onFailure = aOnFailure;
        }

        public void complete() {
            complete(null);
        }

        public void complete(Exception aFailureCause) {
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == processed.size()) {
                if (exceptions.isEmpty()) {
                    if (onSuccess != null) {
                        space.process(() -> {
                            onSuccess.accept(null);
                        });
                    }
                } else if (onFailure != null) {
                    StringBuilder eMessagesSum = new StringBuilder();
                    exceptions.stream().forEach((ex) -> {
                        if (eMessagesSum.length() > 0) {
                            eMessagesSum.append("\n");
                        }
                        eMessagesSum.append(ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.toString());
                    });
                    space.process(() -> {
                        onFailure.accept(new IllegalStateException(eMessagesSum.toString()));
                    });
                }
            }
        }

        public void perform(Consumer<T> action) {
            if (processed.isEmpty()) {
                space.process(() -> {
                    onSuccess.accept(null);
                });
            } else {
                processed.stream().forEach(action);
            }
        }
    }

    @Override
    public int commit(Map<String, List<Change>> aChangeLogs, Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        if (onSuccess != null) {
            CollectionAsyncProcess<Map.Entry<String, List<Change>>> logsProcess = new CollectionAsyncProcess<>(aChangeLogs.entrySet(), space, v -> {
                try {
                    super.commit(aChangeLogs, onSuccess, onFailure);
                } catch (Exception ex) {
                    Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }, onFailure);
            logsProcess.perform(dsEntry -> {
                String dsName = dsEntry.getKey();
                List<Change> changeLog = dsEntry.getValue();
                validate(dsName, changeLog, v -> {
                    logsProcess.complete(null);
                }, ex -> {
                    logsProcess.complete(ex);
                }, space);
            });
            return 0;
        } else {
            aChangeLogs.entrySet().stream().forEach(dsEntry -> {
                String dataSourceName = dsEntry.getKey();
                List<Change> log = dsEntry.getValue();
                validate(dataSourceName, log, null, null, space);
            });
            return super.commit(aChangeLogs, null, null);
        }
    }

    private void validate(final String aDatasourceName, final List<Change> aLog, Consumer<Void> onSuccess, Consumer<Exception> onFailure, Scripts.Space aSpace) {
        List<CallPoint> validatorsPoints = new ArrayList<>();
        validators.keySet().stream().forEach((validatorName) -> {
            Collection<String> datasourcesUnderControl = validators.get(validatorName);
            if (((datasourcesUnderControl == null || datasourcesUnderControl.isEmpty()) && (aDatasourceName == null || Objects.equals(aDatasourceName, defaultDatasourceName)))
                    || (datasourcesUnderControl != null && datasourcesUnderControl.contains(aDatasourceName))) {
                try {
                    JSObject module = createModule(validatorName);
                    if (module != null) {
                        Object oValidate = module.getMember("validate");
                        if (oValidate instanceof JSObject) {
                            JSObject validateFunction = (JSObject) oValidate;
                            validatorsPoints.add(new CallPoint(module, validateFunction));
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
            CollectionAsyncProcess<CallPoint> logProcess = new CollectionAsyncProcess<>(validatorsPoints, aSpace, onSuccess, onFailure);
            logProcess.perform(callPoint -> {
                try {
                    callPoint.function.call(callPoint.module, new Object[]{aSpace.toJs(aLog.toArray()), aDatasourceName,
                        new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                logProcess.complete(null);
                                return null;
                            }
                        },
                        new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                if (args.length > 0) {
                                    if (args[0] instanceof Exception) {
                                        logProcess.complete((Exception) args[0]);
                                    } else {
                                        logProcess.complete(new Exception(String.valueOf(aSpace.toJava(args[0]))));
                                    }
                                } else {
                                    logProcess.complete(new Exception("No error information from validate method"));
                                }
                                return null;
                            }
                        }
                    });
                } catch (ECMAException ex) {
                    logProcess.complete(ex);
                }
            });
        } else {
            validatorsPoints.stream().forEach((v) -> {
                v.function.call(v.module, new Object[]{Scripts.getSpace().toJs(aLog.toArray()), aDatasourceName});
            });
        }
    }
}
