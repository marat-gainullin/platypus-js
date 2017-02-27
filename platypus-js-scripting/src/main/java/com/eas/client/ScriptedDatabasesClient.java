package com.eas.client;

import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.changes.Change;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
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
import java.util.stream.Collectors;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.ECMAException;

/**
 * Multi datasource client. It allows to use js modules as datasources,
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

    private static class CallPoint {

        private final JSObject module;
        private final JSObject function;

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

    protected JSObject createModule(String aModuleName) {
        return Scripts.getSpace().createModule(aModuleName);
    }

    private void validate(final String aDatasourceName, final List<Change> aLog, Consumer<Void> onSuccess, Consumer<Exception> onFailure, Scripts.Space aSpace) {
        Collection<String> requiredModules = validators.entrySet().stream()
                .filter(vEntry -> {
                    Collection<String> datasourcesUnderControl = vEntry.getValue();
                    return (((datasourcesUnderControl == null || datasourcesUnderControl.isEmpty()) && (aDatasourceName == null || Objects.equals(aDatasourceName, defaultDatasourceName)))
                            || (datasourcesUnderControl != null && datasourcesUnderControl.contains(aDatasourceName)));
                })
                .map(vEntry -> vEntry.getKey())
                .collect(Collectors.toSet());
        if (onSuccess != null) {
            try {
                ScriptedResource._require(requiredModules.stream().toArray(size -> new String[size]), null, Scripts.getSpace(), new HashSet<>(), v -> {
                    Collection<CallPoint> validatorsPoints = toCallPoints(requiredModules);
                    JSObject jsLog = validatorsPoints.isEmpty() ? aSpace.makeArray() : aSpace.toJsArray(aLog);
                    CollectionAsyncProcess<CallPoint> logProcess = new CollectionAsyncProcess<>(validatorsPoints, aSpace, onSuccess, onFailure);
                    logProcess.perform(validatorPoint -> {
                        try {
                            validatorPoint.function.call(validatorPoint.module, new Object[]{jsLog, aDatasourceName,
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
                }, onFailure);
            } catch (Exception ex) {
                Logger.getLogger(ScriptedDatabasesClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ScriptedResource._require(requiredModules.stream().toArray(size -> new String[size]), null, Scripts.getSpace(), new HashSet<>());
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
            Collection<CallPoint> validatorsPoints = toCallPoints(requiredModules);
            JSObject jsLog = validatorsPoints.isEmpty() ? aSpace.makeArray() : aSpace.toJsArray(aLog);
            validatorsPoints.stream().forEach((v) -> {
                v.function.call(v.module, new Object[]{jsLog, aDatasourceName});
            });
        }
    }

    private Collection<CallPoint> toCallPoints(Collection<String> requiredModules) {
        return requiredModules.stream()
                .map(validatorName -> createModule(validatorName))
                .filter(module -> module != null)
                .filter(module -> module.getMember("validate") instanceof JSObject)
                .map(module -> new CallPoint(module, (JSObject) module.getMember("validate")))
                .collect(Collectors.toList());
    }
}
