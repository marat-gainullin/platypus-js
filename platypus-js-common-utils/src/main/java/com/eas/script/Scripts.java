package com.eas.script;

import com.eas.concurrent.PlatypusThreadFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.api.scripting.URLReader;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Lexer;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.parser.Token;
import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.Undefined;
import jdk.nashorn.internal.runtime.options.Options;

/**
 *
 * @author vv, mg
 */
public class Scripts {

    private static final NashornScriptEngineFactory SCRIPT_FACTORY = new NashornScriptEngineFactory();
    private static final NashornScriptEngine SCRIPT_ENGINE = (NashornScriptEngine) SCRIPT_FACTORY.getScriptEngine();
    protected static final String INTERNALS_MODULENAME = "internals";
    public static final String INTERNALS_JS_FILENAME = INTERNALS_MODULENAME + ".js";
    public static final String STRING_TYPE_NAME = "String";//NOI18N
    public static final String NUMBER_TYPE_NAME = "Number";//NOI18N
    public static final String DATE_TYPE_NAME = "Date";//NOI18N
    public static final String BOOLEAN_TYPE_NAME = "Boolean";//NOI18N
    public static final String GEOMETRY_TYPE_NAME = "Geometry";//NOI18N
    public static final String THIS_KEYWORD = "this";//NOI18N
    public static /*final*/ URL internalsUrl;
    public static /*final*/ boolean globalAPI;
    public static volatile Path absoluteApiPath;

    public static NashornScriptEngine getEngine() {
        return SCRIPT_ENGINE;
    }

    private static final ThreadLocal<LocalContext> contextRef = new ThreadLocal<>();
    private static final ThreadLocal<Space> spaceRef = new ThreadLocal<>();
    private static Space onlySpace; // for single threaded environment

    public static boolean isGlobalAPI() {
        return globalAPI;
    }

    public static Space getSpace() {
        return onlySpace != null ? onlySpace : spaceRef.get();
    }

    public static void setSpace(Space aSpace) {
        if (aSpace != null) {
            spaceRef.set(aSpace);
        } else {
            spaceRef.remove();
        }
    }

    /**
     * Warning! Use it only once and only in single threaded environment
     *
     * @param aSpace
     */
    public static void setOnlySpace(Space aSpace) {
        onlySpace = aSpace;
    }

    public static LocalContext getContext() {
        return contextRef.get();
    }

    public static void setContext(LocalContext aContext) {
        if (aContext != null) {
            contextRef.set(aContext);
        } else {
            contextRef.remove();
        }
    }

    public static class LocalContext {

        protected final Object request;
        protected final Object response;
        protected final Object principal;
        protected final Object session;

        protected Integer asyncsCount;

        public LocalContext(Object aPrincipal, Object aSession) {
            this(null, null, aPrincipal, aSession);
        }

        public LocalContext(Object aRequest, Object aResponse, Object aPrincipal, Object aSession) {
            super();
            request = aRequest;
            response = aResponse;
            principal = aPrincipal;
            session = aSession;
        }

        public Object getSession() {
            return session;
        }

        public Object getRequest() {
            return request;
        }

        public Object getResponse() {
            return response;
        }

        public Object getPrincipal() {
            return principal;
        }

        public int getAsyncsCount() {
            return asyncsCount != null ? asyncsCount : 0;
        }

        public void incAsyncsCount() {
            if (asyncsCount != null) {
                asyncsCount++;
            }
        }

        public void initAsyncs(Integer aSeed) {
            asyncsCount = aSeed;
        }
    }

    public static class Pending {

        protected Consumer<Void> onLoad;
        protected Consumer<Exception> onError;
        protected LocalContext context = getContext();

        public Pending(Consumer<Void> aOnLoad, Consumer<Exception> aOnError) {
            super();
            onLoad = aOnLoad;
            onError = aOnError;
        }

        public void loaded() {
            LocalContext oldContext = getContext();
            setContext(context);
            try {
                onLoad.accept(null);
            } finally {
                setContext(oldContext);
            }
        }

        public void failed(Exception ex) {
            LocalContext oldContext = getContext();
            setContext(context);
            try {
                onError.accept(ex);
            } finally {
                setContext(oldContext);
            }
        }
    }

    public static class AmdDefine {

        protected String moduleName;
        protected String[] amdDependencies;
        protected JSObject moduleDefiner;

        public AmdDefine(String aModuleName, String[] aAmdDependencies, JSObject aModuleDefiner) {
            super();
            moduleName = aModuleName;
            amdDependencies = aAmdDependencies;
            moduleDefiner = aModuleDefiner;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String[] getAmdDependencies() {
            return amdDependencies;
        }

        public JSObject getModuleDefiner() {
            return moduleDefiner;
        }
    }

    public static class Space {

        protected ScriptContext scriptContext;
        protected Object global;
        protected Map<String, JSObject> publishers = new HashMap<>();
        protected Collection<AmdDefine> amdDefines = new ArrayList<>();
        // script files alredy executed within this script space
        protected Map<URL, Set<String>> executed = new HashMap<>();
        protected Map<String, List<Pending>> pending = new HashMap<>();
        protected Map<String, JSObject> defined = new HashMap<>();

        protected Space() {
            this(null);
            global = new Object();
        }

        public Space(ScriptContext aScriptContext) {
            super();
            scriptContext = aScriptContext;
        }

        /**
         * This method is used by crazy designer only
         *
         * @return
         */
        public String getFileNameFromContext() {
            return (String) scriptContext.getAttribute(ScriptEngine.FILENAME);
        }

        public void pendOn(String aModuleName, Scripts.Pending aPending) {
            List<Scripts.Pending> pends = pending.get(aModuleName);
            if (pends == null) {
                pends = new ArrayList<>();
                pending.put(aModuleName, pends);
            }
            pends.add(aPending);
        }

        public void notifyLoaded(String aModuleName) {
            List<Scripts.Pending> pendings = pending.remove(aModuleName);
            if (pendings != null) {
                Scripts.Pending[] pend = pendings.toArray(new Scripts.Pending[]{});
                pendings.clear();
                for (Scripts.Pending p : pend) {
                    p.loaded();
                }
            }
        }

        public void notifyFailed(String aModuleName, Exception ex) {
            List<Scripts.Pending> pendings = pending.remove(aModuleName);
            if (pendings != null) {
                Scripts.Pending[] pend = pendings.toArray(new Scripts.Pending[]{});
                pendings.clear();
                for (Scripts.Pending p : pend) {
                    p.failed(ex);
                }
            }
        }

        public Map<URL, Set<String>> getExecuted() {
            return Collections.unmodifiableMap(executed);
        }

        public Map<String, List<Pending>> getPending() {
            return pending;
        }

        public Map<String, JSObject> getDefined() {
            return defined;
        }

        public void addAmdDefine(String aModuleName, String[] aAmdDependencies, JSObject aModuleDefiner) {
            amdDefines.add(new AmdDefine(aModuleName, aAmdDependencies, aModuleDefiner));
        }

        public Collection<AmdDefine> consumeAmdDefines() {
            Collection<AmdDefine> res = amdDefines;
            amdDefines = new ArrayList<>();
            return res;
        }

        protected JSObject loadFunc;
        protected JSObject toPrimitiveFunc;
        protected JSObject lookupInGlobalFunc;
        protected JSObject putInGlobalFunc;
        protected JSObject toDateFunc;
        protected JSObject parseJsonFunc;
        protected JSObject parseJsonWithDatesFunc;
        protected JSObject writeJsonFunc;
        protected JSObject extendFunc;
        protected JSObject scalarDefFunc;
        protected JSObject collectionDefFunc;
        protected JSObject isArrayFunc;
        protected JSObject makeObjFunc;
        protected JSObject makeArrayFunc;
        protected JSObject listenFunc;
        protected JSObject listenElementsFunc;
        protected JSObject copyObjectFunc;
        protected JSObject restoreObjectFunc;

        public void setGlobal(Object aValue) {
            if (global == null) {
                global = aValue;
            } else {
                throw new IllegalStateException("Scripts space should be initialized only once.");
            }
        }

        public Object getGlobal() {
            return global;
        }

        public Object getUndefined() {
            return Undefined.getUndefined();
        }

        public void putPublisher(String aClassName, JSObject aPublisher) {
            publishers.put(aClassName, aPublisher);
        }

        public JSObject getPublisher(String aClassName) {
            return publishers.get(aClassName);
        }

        public JSObject getLoadFunc() {
            assert loadFunc != null : SCRIPT_NOT_INITIALIZED;
            return loadFunc;
        }

        public void setLoadFunc(JSObject aValue) {
            assert loadFunc == null;
            loadFunc = aValue;
        }

        public JSObject getToPrimitiveFunc() {
            assert toPrimitiveFunc != null : SCRIPT_NOT_INITIALIZED;
            return toPrimitiveFunc;
        }

        public void setToPrimitiveFunc(JSObject aValue) {
            assert toPrimitiveFunc == null;
            toPrimitiveFunc = aValue;
        }

        public void setLookupInGlobalFunc(JSObject aValue) {
            assert lookupInGlobalFunc == null;
            lookupInGlobalFunc = aValue;
        }

        public void setPutInGlobalFunc(JSObject aValue) {
            assert putInGlobalFunc == null;
            putInGlobalFunc = aValue;
        }

        public JSObject getToDateFunc() {
            assert toDateFunc != null;
            return toDateFunc;
        }

        public void setToDateFunc(JSObject aValue) {
            assert toDateFunc == null;
            toDateFunc = aValue;
        }

        public void setParseJsonFunc(JSObject aValue) {
            assert parseJsonFunc == null;
            parseJsonFunc = aValue;
        }

        public void setParseJsonWithDatesFunc(JSObject aValue) {
            assert parseJsonWithDatesFunc == null;
            parseJsonWithDatesFunc = aValue;
        }

        public void setWriteJsonFunc(JSObject aValue) {
            assert writeJsonFunc == null;
            writeJsonFunc = aValue;
        }

        public void setExtendFunc(JSObject aValue) {
            assert extendFunc == null;
            extendFunc = aValue;
        }

        public void setScalarDefFunc(JSObject aValue) {
            assert scalarDefFunc == null;
            scalarDefFunc = aValue;
        }

        public void setCollectionDefFunc(JSObject aValue) {
            assert collectionDefFunc == null;
            collectionDefFunc = aValue;
        }

        public void setIsArrayFunc(JSObject aValue) {
            assert isArrayFunc == null;
            isArrayFunc = aValue;
        }

        public void setMakeObjFunc(JSObject aValue) {
            assert makeObjFunc == null;
            makeObjFunc = aValue;
        }

        public void setMakeArrayFunc(JSObject aValue) {
            assert makeArrayFunc == null;
            makeArrayFunc = aValue;
        }

        public void setListenFunc(JSObject aValue) {
            assert listenFunc == null;
            listenFunc = aValue;
        }

        public void setListenElementsFunc(JSObject aValue) {
            assert listenElementsFunc == null;
            listenElementsFunc = aValue;
        }

        public void setCopyObjectFunc(JSObject aValue) {
            assert copyObjectFunc == null;
            copyObjectFunc = aValue;
        }

        public JSObject getCopyObjectFunc() {
            return copyObjectFunc;
        }

        public void setRestoreObjectFunc(JSObject aValue) {
            assert restoreObjectFunc == null;
            restoreObjectFunc = aValue;
        }

        public JSObject getRestoreObjectFunc() {
            return restoreObjectFunc;
        }

        public Object toJava(Object aValue) {
            if (aValue instanceof ScriptObject) {
                aValue = ScriptUtils.wrap((ScriptObject) aValue);
            }
            if (aValue instanceof JSObject) {
                assert toPrimitiveFunc != null : SCRIPT_NOT_INITIALIZED;
                aValue = toPrimitiveFunc.call(null, new Object[]{aValue});
            } else if (aValue == ScriptRuntime.UNDEFINED) {
                return null;
            }
            return aValue;
        }

        public Object toJs(Object aValue) {
            if (aValue instanceof Date) {// force js boxing of date, because of absence js literal of date value
                assert toDateFunc != null : SCRIPT_NOT_INITIALIZED;
                return toDateFunc.call(null, aValue);
            } else if (aValue instanceof Number) {
                return ((Number) aValue).doubleValue();
            } else if (aValue instanceof HasPublished) {
                return ((HasPublished) aValue).getPublished();
            } else {
                return aValue;
            }
        }

        public JSObject toJsArray(List aArray) {
            JSObject published = makeArray();
            JSObject push = (JSObject) published.getMember("push");
            for (int i = 0; i < aArray.size(); i++) {
                push.call(published, toJs(aArray.get(i)));
            }
            return published;
        }

        public Object parseJson(String json) {
            assert parseJsonFunc != null : SCRIPT_NOT_INITIALIZED;
            return parseJsonFunc.call(null, new Object[]{json});
        }

        public Object parseJsonWithDates(String json) {
            assert parseJsonWithDatesFunc != null : SCRIPT_NOT_INITIALIZED;
            return parseJsonWithDatesFunc.call(null, new Object[]{json});
        }

        public String toJson(Object aObj) {
            assert writeJsonFunc != null : SCRIPT_NOT_INITIALIZED;
            if (aObj instanceof Undefined) {//nashorn JSON parser could not work with undefined.
                aObj = null;
            }
            if (aObj instanceof JSObject || aObj instanceof CharSequence
                    || aObj instanceof Number || aObj instanceof Boolean || aObj instanceof ScriptObject || aObj == null) {
                return JSType.toString(writeJsonFunc.call(null, new Object[]{aObj}));
            } else {
                throw new IllegalArgumentException("Java object couldn't be converted to JSON!");
            }
        }

        public void extend(JSObject aChild, JSObject aParent) {
            assert extendFunc != null : SCRIPT_NOT_INITIALIZED;
            extendFunc.call(null, new Object[]{aChild, aParent});
        }

        public JSObject scalarPropertyDefinition(JSObject targetEntity, String targetFieldName, String sourceFieldName) {
            assert scalarDefFunc != null : SCRIPT_NOT_INITIALIZED;
            return (JSObject) scalarDefFunc.newObject(new Object[]{targetEntity, targetFieldName, sourceFieldName});
        }

        public JSObject collectionPropertyDefinition(JSObject sourceEntity, String targetFieldName, String sourceFieldName) {
            assert collectionDefFunc != null : SCRIPT_NOT_INITIALIZED;
            return (JSObject) collectionDefFunc.newObject(new Object[]{sourceEntity, targetFieldName, sourceFieldName});
        }

        public boolean isArrayDeep(JSObject aInstance) {
            assert isArrayFunc != null : SCRIPT_NOT_INITIALIZED;
            Object oResult = isArrayFunc.call(null, new Object[]{aInstance});
            return Boolean.TRUE.equals(oResult);
        }

        public JSObject makeObj() {
            assert makeObjFunc != null : SCRIPT_NOT_INITIALIZED;
            Object oResult = makeObjFunc.call(null, new Object[]{});
            return (JSObject) oResult;
        }

        public JSObject makeArray() {
            assert makeArrayFunc != null : SCRIPT_NOT_INITIALIZED;
            Object oResult = makeArrayFunc.call(null, new Object[]{});
            return (JSObject) oResult;
        }

        public Object makeCopy(Object aSource) {
            assert copyObjectFunc != null : SCRIPT_NOT_INITIALIZED;
            return copyObjectFunc.call(null, new Object[]{aSource});
        }

        public Object restoreCopy(Object aSource) {
            assert restoreObjectFunc != null : SCRIPT_NOT_INITIALIZED;
            return restoreObjectFunc.call(null, new Object[]{aSource});
        }

        public JSObject listen(JSObject aTarget, String aPath, JSObject aCallback) {
            assert listenFunc != null : SCRIPT_NOT_INITIALIZED;
            Object oResult = listenFunc.call(null, new Object[]{aTarget, aPath, aCallback});
            return (JSObject) oResult;
        }

        public JSObject listenElements(JSObject aTarget, JSObject aCallback) {
            assert listenElementsFunc != null : SCRIPT_NOT_INITIALIZED;
            Object oResult = listenElementsFunc.call(null, new Object[]{aTarget, aCallback});
            return (JSObject) oResult;
        }

        public JSObject createModule(String aModuleName) {
            assert lookupInGlobalFunc != null : SCRIPT_NOT_INITIALIZED;
            JSObject jsConstructor = lookup(aModuleName);
            if (jsConstructor != null && jsConstructor.isFunction()) {
                return (JSObject) jsConstructor.newObject(new Object[]{});
            } else {
                return null;
            }
        }

        public JSObject lookup(String aName) {
            JSObject amd = defined.get(aName);
            if (amd != null) {
                return amd;
            } else {
                return lookupInGlobal(aName);
            }
        }

        public JSObject lookupInGlobal(String aName) {
            assert lookupInGlobalFunc != null : SCRIPT_NOT_INITIALIZED;
            Object res = aName != null && !aName.isEmpty() ? lookupInGlobalFunc.call(null, new Object[]{aName}) : null;
            return res instanceof JSObject ? (JSObject) res : null;
        }

        public void putInGlobal(String aName, JSObject aValue) {
            assert putInGlobalFunc != null : SCRIPT_NOT_INITIALIZED;
            putInGlobalFunc.call(null, new Object[]{aName, aValue});
        }

        public Object exec(String aSourceName, URL aSourcePlace) throws ScriptException, URISyntaxException {
            scriptContext.setAttribute(ScriptEngine.FILENAME, aSourceName.toLowerCase().endsWith(".js") ? aSourceName.substring(0, aSourceName.length() - 3) : aSourceName, ScriptContext.ENGINE_SCOPE);
            Object result = SCRIPT_ENGINE.eval(new URLReader(aSourcePlace), scriptContext);
            executed.put(aSourcePlace, new HashSet<>());
            return result;
        }

        public Object exec(String aSource) throws ScriptException, URISyntaxException {
            assert scriptContext != null : SCRIPT_NOT_INITIALIZED;
            return SCRIPT_ENGINE.eval(aSource, scriptContext);
        }

        public void schedule(JSObject aJsTask, long aTimeout) {
            Scripts.LocalContext context = Scripts.getContext();
            scheduler.schedule(() -> {
                Scripts.setContext(context);
                try {
                    process(() -> {
                        aJsTask.call(null, new Object[]{});
                    });
                } finally {
                    Scripts.setContext(null);
                }
            }, aTimeout, TimeUnit.MILLISECONDS);
        }

        public void enqueue(JSObject aJsTask) {
            process(() -> {
                aJsTask.call(null, new Object[]{});
            });
        }

        protected Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
        protected AtomicInteger queueVersion = new AtomicInteger();
        protected AtomicReference worker = new AtomicReference(null);

        public void process(Runnable aTask) {
            Scripts.LocalContext context = Scripts.getContext();
            process(context, aTask);
        }

        public void process(Scripts.LocalContext context, Runnable aTask) {
            Runnable taskWrapper = () -> {
                setContext(context);
                try {
                    setSpace(Space.this);
                    try {
                        aTask.run();
                    } finally {
                        setSpace(null);
                    }
                } finally {
                    setContext(null);
                }
            };
            queue.offer(taskWrapper);
            offerTask(() -> {
                //Runnable processedTask = taskWrapper;
                int version;
                int newVersion;
                Thread thisThread = Thread.currentThread();
                do {
                    version = queueVersion.get();
                    // Zombie counter ...
                    newVersion = version + 1;
                    if (newVersion == Integer.MAX_VALUE) {
                        newVersion = 0;
                    }
                    /* moved to top of body
                     if (processedTask != null) {//Single attempt to offer aTask.
                     queue.offer(processedTask); 
                     processedTask = null;
                     }
                     */
                    if (worker.compareAndSet(null, thisThread)) {// Worker electing.
                        try {
                            // already single threaded environment
                            if (global == null) {
                                Space.this.initSpaceGlobal();
                            }
                            // Zombie processing ...
                            Runnable task = queue.poll();
                            while (task != null) {
                                task.run();
                                task = queue.poll();
                            }
                        } catch (Throwable t) {
                            Logger.getLogger(Scripts.class.getName()).log(Level.SEVERE, null, t);
                        } finally {
                            boolean setted = worker.compareAndSet(thisThread, null);
                            assert setted : "Worker electing assumption failed";// Always successfull CAS.
                        }
                    }
                } while (!queueVersion.compareAndSet(version, newVersion));
            });
        }

        /**
         * Public only for tests
         */
        public void initSpaceGlobal() {
            Bindings bindings = SCRIPT_ENGINE.createBindings();
            scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            try {
                setSpace(Space.this);
                try {
                    scriptContext.setAttribute(ScriptEngine.FILENAME, INTERNALS_MODULENAME, ScriptContext.ENGINE_SCOPE);
                    SCRIPT_ENGINE.eval(new URLReader(internalsUrl), scriptContext);
                } finally {
                    setSpace(null);
                }
            } catch (ScriptException ex) {
                Logger.getLogger(Scripts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public JSObject readJsArray(Collection<Map<String, Object>> aCollection) {
            JSObject result = makeArray();
            JSObject jsPush = (JSObject) result.getMember("push");
            aCollection.forEach((Map<String, Object> aItem) -> {
                JSObject jsItem = makeObj();
                aItem.entrySet().forEach((Map.Entry<String, Object> aItemContent) -> {
                    jsItem.setMember(aItemContent.getKey(), toJs(aItemContent.getValue()));
                });
                jsPush.call(result, new Object[]{jsItem});
            });
            return result;
        }

    }
    protected static Consumer<Runnable> tasks;
    // bio thread pool
    protected static ThreadPoolExecutor bio;
    protected static ScheduledExecutorService scheduler;

    public static void init(Path aAbsoluteApiPath, boolean aGlobalAPI) throws MalformedURLException {
        globalAPI = aGlobalAPI;
        internalsUrl = aAbsoluteApiPath.resolve(INTERNALS_JS_FILENAME).toUri().toURL();
        absoluteApiPath = aAbsoluteApiPath;
    }

    public static Path getAbsoluteApiPath() {
        return absoluteApiPath;
    }

    public static void initTasks(Consumer<Runnable> aTasks) {
        assert tasks == null : "Scripts tasks are already initialized";
        tasks = aTasks;
    }

    public static void initTasks(ExecutorService aExecutor) {
        class TasksExecutor implements Consumer<Runnable>, Function<Void, ExecutorService> {

            @Override
            public void accept(Runnable aTask) {
                aExecutor.submit(aTask);
            }

            @Override
            public ExecutorService apply(Void v) {
                return aExecutor;
            }
        }
        initTasks(new TasksExecutor());
    }

    public static ExecutorService getTasksExecutorIfPresent() {
        assert tasks != null : "Scripts tasks are not initialized";
        if (tasks instanceof Callable<?>) {
            return ((Function<Void, ExecutorService>) tasks).apply(null);
        } else {
            return null;
        }
    }

    public static void offerTask(Runnable aTask) {
        assert tasks != null : "Scripts tasks are not initialized";
        if (Scripts.getContext() != null) {
            Scripts.getContext().incAsyncsCount();
        }
        tasks.accept(aTask);
    }

    public static void initBIO(int aMaxThreads) {
        bio = new ThreadPoolExecutor(aMaxThreads, aMaxThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PlatypusThreadFactory("platypus-abio-", false));
        bio.allowCoreThreadTimeOut(true);
        scheduler = Executors.newSingleThreadScheduledExecutor(
                new PlatypusThreadFactory("platypus-scheduler-", false));
    }

    public static void shutdown() {
        if (bio != null) {
            bio.shutdownNow();
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public static void startBIO(Runnable aBioTask) {
        LocalContext context = getContext();
        if (context != null) {
            context.incAsyncsCount();
        }
        bio.submit(() -> {
            setContext(context);
            try {
                aBioTask.run();
            } finally {
                setContext(null);
            }
        });
    }

    public static Space createSpace() throws ScriptException {
        Space space = new Space(new SimpleScriptContext());
        return space;
    }

    public static Space createQueue() throws ScriptException {
        Space space = new Space();
        return space;
    }

    /**
     * If scripts are initialized, then Platypua.ja system will work in full
     * manner. Otherwise it will not perform some actions, such as data binding.
     *
     * @return True if scripts are fully initialized, false otherwise.
     */
    public static boolean isInitialized() {
        Space space = getSpace();
        return space != null
                && space.copyObjectFunc != null
                && space.restoreObjectFunc != null;
    }

    public static boolean isValidJsIdentifier(final String aName) {
        if (aName != null && !aName.trim().isEmpty()) {
            try {
                FunctionNode astRoot = parseJs(String.format("function %s() {}", aName)).getAst();
                return astRoot != null && !astRoot.getBody().getStatements().isEmpty();
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    public static ParsedJs parseJs(String aJsContent) {
        Source source = Source.sourceFor("", aJsContent);//NOI18N
        Options options = new Options(null);
        ScriptEnvironment env = new ScriptEnvironment(options, null, null);
        ErrorManager errors = new ErrorManager();
        //
        Map<Long, Long> prevComments = new HashMap<>();
        Parser p = new Parser(env, source, errors) {
            @Override
            public FunctionNode parse(String scriptName, int startPos, int len, boolean allowPropertyFunction) {
                prevComments.clear();
                stream = new TokenStream() {
                    protected long prevToken;

                    @Override
                    public void put(long token) {
                        if (Token.descType(token) != TokenType.EOL) {
                            if (Token.descType(prevToken) == TokenType.COMMENT) {
                                prevComments.put(token, prevToken);
                            }
                            prevToken = token;
                        }
                        super.put(token);
                    }

                };
                lexer = new Lexer(source, stream, false);

                // Set up first token (skips opening EOL.)
                k = -1;
                next();
                // Begin parse.
                try {
                    Method program = Parser.class.getDeclaredMethod("program", new Class[]{String.class, boolean.class});
                    program.setAccessible(true);
                    return (FunctionNode) program.invoke(this, new Object[]{scriptName, true});
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Scripts.class.getName()).log(Level.WARNING, null, ex);
                    return null;
                }
            }
        };
        FunctionNode jsAst = p.parse();
        return jsAst != null ? new ParsedJs(jsAst, prevComments) : null;
    }

    /**
     * Extracts the comments tokens from a JavaScript source.
     *
     * @param aSource a source
     * @return a list of comment tokens
     */
    public static List<Long> getCommentsTokens(String aSource) {
        TokenStream tokens = new TokenStream();
        Lexer lexer = new Lexer(Source.sourceFor("", aSource), tokens);//NOI18N
        long t;
        TokenType tt = TokenType.EOL;
        int i = 0;
        List<Long> commentsTokens = new ArrayList<>();
        while (tt != TokenType.EOF) {
            // Get next token in nashorn's parser way
            while (i > tokens.last()) {
                if (tokens.isFull()) {
                    tokens.grow();
                }
                lexer.lexify();
            }
            t = tokens.get(i++);
            tt = Token.descType(t);
            if (tt == TokenType.COMMENT) {
                commentsTokens.add(t);
            }
        }
        return commentsTokens;
    }

    /**
     * Removes all commentaries from some JavaScript code.
     *
     * @param text a source
     * @return comments-free JavaScript code
     */
    public static String removeComments(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long t : getCommentsTokens(text)) {
            int offset = Token.descPosition(t);
            int lenght = Token.descLength(t);
            sb.append(text.substring(i, offset));
            for (int j = 0; j < lenght; j++) {
                sb.append(" ");//NOI18N
            }
            i = offset + lenght;
        }
        sb.append(text.substring(i));
        return sb.toString();
    }

    protected static final String SCRIPT_NOT_INITIALIZED = "Platypus script functions are not initialized.";

    public static void unlisten(JSObject aCookie) {
        if (aCookie != null) {
            JSObject unlisten = (JSObject) aCookie.getMember("unlisten");
            unlisten.call(null, new Object[]{});
        }
    }

    public static CompletionHandler<?, ?> asCompletionHandler(JSObject aOnSuccess, JSObject aOnFailure) {
        final Space callingSpace = getSpace();
        final LocalContext callingContext = getContext();
        return new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                callingSpace.process(callingContext, () -> {
                    if (aOnSuccess != null) {
                        aOnSuccess.call(null, new Object[]{result});
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                callingSpace.process(callingContext, () -> {
                    if (aOnFailure != null) {
                        aOnFailure.call(null, new Object[]{exc.toString()});
                    }
                });
            }
        };
    }

    /**
     * For external API.
     *
     * @param aWrapped
     * @return BiConsumer&lt;Object, Throwable&gt; Object - result instance,
     * Throwable - exception raised while an operation.
     */
    public static BiConsumer<Object, Throwable> inContext(BiConsumer<Object, Throwable> aWrapped) {
        Space callingSpace = getSpace();
        LocalContext callingContext = getContext();
        return (Object aResult, Throwable aReason) -> {
            callingSpace.process(callingContext, () -> {
                aWrapped.accept(aResult, aReason);
            });
        };
    }

    public static boolean isInNode(Node node, int offset) {
        return node.getStart() <= offset
                && offset <= node.getFinish() + 1;
    }

    public static boolean isInNode(Node outerNode, Node innerNode) {
        return outerNode.getStart() <= innerNode.getStart()
                && innerNode.getFinish() <= outerNode.getFinish();
    }

    public static Node getOffsetNode(Node node, final int offset) {
        GetOffsetNodeVisitorSupport vs = new GetOffsetNodeVisitorSupport(node, offset);
        Node offsetNode = vs.getOffsetNode();
        return offsetNode != null ? offsetNode : node;
    }

    private static class GetOffsetNodeVisitorSupport {

        private final Node root;
        private final int offset;
        private Node offsetNode;

        public GetOffsetNodeVisitorSupport(Node root, int offset) {
            this.root = root;
            this.offset = offset;
        }

        public Node getOffsetNode() {
            final LexicalContext lc = new LexicalContext();
            root.accept(new NodeVisitor<LexicalContext>(lc) {

                @Override
                protected boolean enterDefault(Node node) {
                    if (isInNode(node, offset)) {
                        offsetNode = node;
                        return true;
                    }
                    return false;
                }
            });
            return offsetNode;
        }
    }
}
