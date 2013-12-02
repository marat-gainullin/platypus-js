/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.debugger.jmx.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.Dim.SourceInfo;

/**
 * Process wide, javascript debugger JMX server implementation class.
 * @author mg
 * @see Dim
 */
public class Breakpoints implements BreakpointsMBean {

    protected Debugger debugger;
    protected Map<String, Set<Integer>> pending = new HashMap<>();
    private static Breakpoints instance;

    public static Breakpoints getInstance() {
        if (instance == null) {
            instance = new Breakpoints(Debugger.getInstance());
        }
        return instance;
    }

    /**
     * Simple constructor. It has to be called only once.
     * @param aJsDebugger
     */
    protected Breakpoints(Debugger aDebugger) {
        super();
        debugger = aDebugger;
    }

    @Override
    public boolean isBreakable(String sourceModuleId, int aLineNumber) {
        SourceInfo si = debugger.getJsDebugger().sourceInfo(sourceModuleId);
        if (si != null) {
            return si.breakableLine(aLineNumber);
        } else {
            return sourceModuleId != null && !sourceModuleId.isEmpty();// give pending breakpoints a chance
        }
    }

    @Override
    public boolean hasBreakpoint(String sourceModuleId, int aLineNumber) {
        SourceInfo si = debugger.getJsDebugger().sourceInfo(sourceModuleId);
        if (si != null) {
            try {
                return si.breakpoint(aLineNumber);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        } else {
            return isPending(sourceModuleId, aLineNumber);
        }
    }

    private boolean isPending(String sourceModuleId, int aLineNumber) {
        return pending.containsKey(sourceModuleId) && pending.get(sourceModuleId) != null && pending.get(sourceModuleId).contains(aLineNumber);
    }

    @Override
    public boolean addBreakpoint(String sourceModuleId, int aLineNumber) {
        if (isBreakable(sourceModuleId, aLineNumber)) {
            SourceInfo si = debugger.getJsDebugger().sourceInfo(sourceModuleId);
            if (si != null && si.breakableLine(aLineNumber)) {
                if (si.breakpoint(aLineNumber, true)) {
                    if (isPending(sourceModuleId, aLineNumber)) {
                        removePending(sourceModuleId, aLineNumber);
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!pending.containsKey(sourceModuleId)) {
                    pending.put(sourceModuleId, new HashSet<Integer>());
                }
                Set<Integer> pendingBreaks = pending.get(sourceModuleId);
                assert pendingBreaks != null;
                pendingBreaks.add(aLineNumber);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean removeBreakpoint(String sourceModuleId, int aLineNumber) {
        removePending(sourceModuleId, aLineNumber);
        SourceInfo si = debugger.getJsDebugger().sourceInfo(sourceModuleId);
        if (si != null && si.breakableLine(aLineNumber)) {
            return si.breakpoint(aLineNumber, false);
        } else {
            return true;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean toggleBreakpoint(String sourceModuleId, int aLineNumber) {
        if (hasBreakpoint(sourceModuleId, aLineNumber)) {
            removeBreakpoint(sourceModuleId, aLineNumber);
        } else {
            addBreakpoint(sourceModuleId, aLineNumber);
        }
        return hasBreakpoint(sourceModuleId, aLineNumber);
    }

    public void checkPendingBreakpoints() {
        Set<String> toDel = new HashSet<>();
        for (Entry<String, Set<Integer>> pSet : pending.entrySet()) {
            String moduleId = pSet.getKey();
            Set<Integer> breaks = pSet.getValue();
            for (Integer lineNumber : breaks.toArray(new Integer[0])) {
                if (!hasBreakpoint(moduleId, lineNumber)) {
                    SourceInfo si = debugger.getJsDebugger().sourceInfo(moduleId);
                    if (si != null && si.breakableLine(lineNumber)) {
                        si.breakpoint(lineNumber, true);
                    }
                }
                if (hasBreakpoint(moduleId, lineNumber) && !isPending(moduleId, lineNumber)) {
                    breaks.remove(lineNumber);
                }
            }
            if (breaks.isEmpty()) {
                toDel.add(moduleId);
            }
        }
        // cleanup
        for (String toDelKey : toDel) {
            pending.remove(toDelKey);
            //Logger.getLogger(Breakpoints.class.getName()).severe("checkPendingBreakpoints(). removed from pending: " + toDelKey);
        }
    }

    private void removePending(String sourceModuleId, int aLineNumber) {
        Set<Integer> breaks = pending.get(sourceModuleId);
        if (breaks != null) {
            breaks.remove(aLineNumber);
            //Logger.getLogger(Breakpoints.class.getName()).severe("removePending(). removed from pending. sourceModuleId:" + sourceModuleId + ", aLineNumber:" + aLineNumber);
            if (breaks.isEmpty()) {
                pending.remove(sourceModuleId);
                //Logger.getLogger(Breakpoints.class.getName()).severe("removePending(). removed from pending. sourceModuleId:" + sourceModuleId);
            }
        }
    }
}
