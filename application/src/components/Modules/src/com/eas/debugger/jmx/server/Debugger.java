/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.util.ArrayList;
import java.util.List;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.Dim.ContextData;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;
import org.mozilla.javascript.tools.debugger.Dim.StackFrame;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.ScopeProvider;
import org.mozilla.javascript.tools.shell.Global;

/**
 *
 * @author mg
 */
public class Debugger extends NotificationBroadcasterSupport implements DebuggerMBean, GuiCallback {

    private static Debugger instance;

    public static Debugger initialize(boolean needInitialBreak) {
        if (instance == null) {
            instance = new Debugger(needInitialBreak);
        }
        return instance;
    }

    public static Debugger getInstance() {
        assert instance != null : "Debugger instance must initialized while call getInstacne(). May be ScriptRunner is executed before debugger has been initialized?";
        return instance;
    }
    private long interruptSequence = 1;

    public String[] encodeFrame(StackFrame lastFrame, String threadTitle) {
        List<String> frameTags = new ArrayList<>();
        frameTags.add(URL_TAG_NAME);
        frameTags.add(lastFrame.getUrl());
        if (lastFrame.getFunctionName() != null) {
            frameTags.add(FUNCTION_NAME_TAG_NAME);
            frameTags.add(lastFrame.getFunctionName());
        }
        frameTags.add(LINE_TAG_NAME);
        frameTags.add(String.valueOf(lastFrame.getLineNumber()));
        frameTags.add(THREAD_NAME_TAG_NAME);
        frameTags.add(threadTitle);
        return frameTags.toArray(new String[0]);
    }

    @Override
    public void updateSourceText(SourceInfo sourceInfo) {
    }

    @Override
    public void enterInterrupt(StackFrame lastFrame, String threadTitle, String alertMessage) {
        lastFrame.scope();
        String[] ecodedFrame = encodeFrame(lastFrame, threadTitle);
        AttributeChangeNotification notification = new AttributeChangeNotification(this, interruptSequence++, System.currentTimeMillis(), "Platypus debugger encoutered a break in user program", BREAK_ATTRIBUTE_NAME, "String[]", null, ecodedFrame);
        sendNotification(notification);
    }

    @Override
    public boolean isGuiEventThread() {
        return false;
    }

    @Override
    public void dispatchNextGuiEvent() throws InterruptedException {
    }

    @Override
    public void pause() throws Exception {
        jsDebugger.setBreak();
    }

    @Override
    public void continueRun() throws Exception {
        jsDebugger.go();
    }

    @Override
    public void step() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_OVER);
    }

    @Override
    public void stepInto() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_INTO);
    }

    @Override
    public void stepOut() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_OUT);
    }

    @Override
    public void stop() throws Exception {
        try {
            pause();
        } finally {
            System.exit(255);// Exit has been forced by remote debugger.
        }
    }

    @Override
    public String evaluate(String aExpression) throws Exception {
        jsDebugger.contextSwitch(0);
        return jsDebugger.eval(aExpression);
    }

    @Override
    public String[] locals() throws Exception {
        jsDebugger.contextSwitch(0);
        ContextData ctxData = jsDebugger.currentContextData();
        if (ctxData != null) {
            Object oScope = ctxData.getFrame(0).scope();
            if (oScope instanceof Scriptable) {
                Object[] oIds = jsDebugger.getObjectIds(oScope);
                return idsToStrings(oIds);
            }
        }
        return null;
    }

    @Override
    public String[] props(String aExpression) throws Exception {
        jsDebugger.contextSwitch(0);
        Object oResult = eval(aExpression);
        return oResult != null ? idsToStrings(jsDebugger.getObjectIds(oResult)) : new String[]{};
    }

    @Override
    public String[][] getCallStack() throws Exception {
        ContextData ctxData = jsDebugger.currentContextData();
        if (ctxData != null) {
            String[][] stack = new String[ctxData.frameCount()][];
            for (int i = 0; i < stack.length; i++) {
                String[] ecodedFrame = encodeFrame(ctxData.getFrame(i), Thread.currentThread().getName());
                stack[i] = ecodedFrame;
            }
            return stack;
        }
        return new String[0][0];
    }

    private String[] idsToStrings(Object[] oIds) {
        List<String> sIds = new ArrayList<>();
        for (int i = 0; i < oIds.length; i++) {
            if (!"__parent__".equals(oIds[i]) && !"__proto__".equals(oIds[i])) {
                sIds.add(String.valueOf(oIds[i]));
            }
        }
        return sIds.toArray(new String[]{});
    }

    /**
     * Class to consolidate all internal implementations of interfaces to avoid
     * class generation bloat.
     */
    private static class IProxy implements Runnable, ScopeProvider {

        // Constants for 'type'.
        public static final int EXIT_ACTION = 1;
        public static final int SCOPE_PROVIDER = 2;
        /**
         * The type of interface.
         */
        private final int type;
        /**
         * The scope object to expose when {@link #type} =
         * {@link #SCOPE_PROVIDER}.
         */
        private Scriptable scope;

        /**
         * Creates a new IProxy.
         */
        public IProxy(int type) {
            this.type = type;
        }

        /**
         * Creates a new IProxy that acts as a {@link ScopeProvider}.
         */
        public static ScopeProvider newScopeProvider(Scriptable scope) {
            IProxy scopeProvider = new IProxy(SCOPE_PROVIDER);
            scopeProvider.scope = scope;
            return scopeProvider;
        }

        // ContextAction
        /**
         * Exit action.
         */
        @Override
        public void run() {
            if (type != EXIT_ACTION) {
                Kit.codeBug();
            }
            System.exit(0);
        }

        // ScopeProvider
        /**
         * Returns the scope for script evaluations.
         */
        @Override
        public Scriptable getScope() {
            if (type != SCOPE_PROVIDER) {
                Kit.codeBug();
            }
            if (scope == null) {
                Kit.codeBug();
            }
            return scope;
        }
    }
    protected Dim jsDebugger;

    protected Debugger(boolean needInitialBreak) {
        super();
        ScriptUtils.getScope();// force Rhino ininializations. Some initialization are related to ContextFactory.
        ContextFactory factory = ContextFactory.getGlobal();
        Global global = new Global();
        global.init(factory);

        jsDebugger = new Dim();
        jsDebugger.setGuiCallback(this);
        if (needInitialBreak) {
            jsDebugger.setBreak();
        }
        jsDebugger.attachTo(factory);

        if (global instanceof ScopeProvider) {
            jsDebugger.setScopeProvider((ScopeProvider) global);
        } else {
            Scriptable scope = (Scriptable) global;
            jsDebugger.setScopeProvider(IProxy.newScopeProvider(scope));
        }
    }

    public Dim getJsDebugger() {
        return jsDebugger;
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[]{
            AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        String description = "A executed line and/or source have changed";
        MBeanNotificationInfo info =
                new MBeanNotificationInfo(types, AttributeChangeNotification.class.getName(), description);
        return new MBeanNotificationInfo[]{info};
    }

    public Object eval(final String expr) throws Exception {
        String result = "undefined";
        if (expr == null) {
            return result;
        }
        int frameIndex = 0;
        ContextData contextData = jsDebugger.currentContextData();
        if (contextData == null || frameIndex >= contextData.frameCount()) {
            return result;
        }
        final StackFrame frame = contextData.getFrame(frameIndex);
        return ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                return do_eval(cx, frame, expr);
            }
        });
    }

    /**
     * Evaluates script in the given stack frame.
     */
    private static Object do_eval(Context cx, StackFrame frame, String expr) {
        String resultString;
        org.mozilla.javascript.debug.Debugger saved_debugger = cx.getDebugger();
        Object saved_data = cx.getDebuggerContextData();
        int saved_level = cx.getOptimizationLevel();

        cx.setDebugger(null, null);
        cx.setOptimizationLevel(-1);
        cx.setGeneratingDebug(false);
        try {
            Callable script = (Callable) cx.compileString(expr, "", 0, null);
            Object result = script.call(cx, (Scriptable) frame.scope(), (Scriptable) frame.thisObj(),
                    ScriptRuntime.emptyArgs);
            if (result == Undefined.instance) {
                return "";
            } else {
                return result;
            }
        } catch (Exception exc) {
            resultString = exc.getMessage();
        } finally {
            cx.setGeneratingDebug(true);
            cx.setOptimizationLevel(saved_level);
            cx.setDebugger(saved_debugger, saved_data);
        }
        if (resultString == null) {
            resultString = "null";
        }
        return resultString;
    }
}
