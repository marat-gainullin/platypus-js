/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

import java.util.ArrayList;
import java.util.List;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;
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

    public void updateSourceText(SourceInfo sourceInfo) {
    }

    public void enterInterrupt(StackFrame lastFrame, String threadTitle, String alertMessage) {
        lastFrame.scope();
        String[] ecodedFrame = encodeFrame(lastFrame, threadTitle);
        AttributeChangeNotification notification = new AttributeChangeNotification(this, interruptSequence++, System.currentTimeMillis(), "Platypus debugger encoutered a break in user program", BREAK_ATTRIBUTE_NAME, "String[]", null, ecodedFrame);
        sendNotification(notification);
    }

    public boolean isGuiEventThread() {
        return false;
    }

    public void dispatchNextGuiEvent() throws InterruptedException {
    }

    public void pause() throws Exception {
        jsDebugger.setBreak();
    }

    public void continueRun() throws Exception {
        jsDebugger.go();
    }

    public void step() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_OVER);
    }

    public void stepInto() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_INTO);
    }

    public void stepOut() throws Exception {
        jsDebugger.setReturnValue(Dim.STEP_OUT);
    }

    public void stop() throws Exception {
        try {
            pause();
        } finally {
            System.exit(255);// Exit has been forced by remote debugger.
        }
    }

    public String evaluate(String aExpression) throws Exception {
        jsDebugger.contextSwitch(0);
        return jsDebugger.eval(aExpression);
    }

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

    /**
     * Class to consolidate all internal implementations of interfaces
     * to avoid class generation bloat.
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
}
