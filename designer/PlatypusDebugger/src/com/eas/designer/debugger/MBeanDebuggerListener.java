/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.debugger.annotations.PlatypusRunpointAnnotation;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class MBeanDebuggerListener implements NotificationListener {

    protected boolean debuggingStarted;
    protected boolean running;
    protected PlatypusRunpointAnnotation annotation = new PlatypusRunpointAnnotation();
    protected Project project;

    MBeanDebuggerListener(Project aProject) {
        project = aProject;
    }

    public void die() {
        annotation.detach();
        annotation = null;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (notification instanceof AttributeChangeNotification) {
            AttributeChangeNotification event = (AttributeChangeNotification) notification;
            if (DebuggerMBean.BREAK_ATTRIBUTE_NAME.equals(event.getAttributeName())) {
                annotation.detach();
                Object oNewValue = event.getNewValue();
                if (oNewValue instanceof String[]) {
                    try {
                        CodePointInfo cpInfo = CodePointInfo.valueOf(project, (String[]) oNewValue);
                        breakChangeRecieved(cpInfo);
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        }
    }

    private void breakChangeRecieved(CodePointInfo cpInfo) throws Exception {
        // Let's find our engine;
        running = false;
        DebuggerEngine ourEngine = null;
        DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
        for (DebuggerEngine engine : engines) {
            if (this == engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class).mDebuggerListener) {
                ourEngine = engine;
            }
        }
        assert ourEngine != null : "Debugging engine missing";

        if (cpInfo.fo != null) {// can't open libraries
            if (cpInfo.fo != null) {
                DataObject dataObject = DataObject.find(cpInfo.fo);
                if (dataObject != null) {
                    EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
                    if (ec != null) {
                        if (ec.getLineSet() != null) {
                            final Line lineObject = ec.getLineSet().getCurrent(cpInfo.lineNo);
                            annotation.attach(lineObject);
                            final Annotation addedAnnotation = annotation;
                            Runnable showRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    lineObject.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FRONT);
                                    if (addedAnnotation.getAttachedAnnotatable() != null) {
                                        addedAnnotation.moveToFront();
                                    }
                                }
                            };
                            if (java.awt.EventQueue.isDispatchThread()) {
                                showRunnable.run();
                            } else {
                                java.awt.EventQueue.invokeLater(showRunnable);
                            }
                        }
                    }
                }
            }
        }
        if (!debuggingStarted) {
            debuggingStarted = true;
            ourEngine.getActionsManager().doAction(ActionsManager.ACTION_START);
        }
        ourEngine.getActionsManager().doAction(DebuggerConstants.ACTION_ENABLED_CHANGED);
    }

    public void cancelStoppedAnnotation() {
        running = true;
        if (annotation != null && annotation.getAttachedAnnotatable() != null) {
            annotation.detach();
        }
    }

    public boolean positionedOnSource() {
        return annotation.getAttachedAnnotatable() != null;
    }

    public boolean isHaveBeenRun() {
        return debuggingStarted;
    }

    public int getCurrentLineNumber() {
        if (annotation != null && annotation.getAttachedAnnotatable() != null
                && annotation.getAttachedAnnotatable() instanceof Line) {
            Line line = (Line) annotation.getAttachedAnnotatable();
            return line.getLineNumber();
        } else {
            return -1;
        }
    }

    public FileObject getCurrentAppFile() {
        if (annotation != null && annotation.getAttachedAnnotatable() != null
                && annotation.getAttachedAnnotatable() instanceof Line) {
            Line line = (Line) annotation.getAttachedAnnotatable();
            if (line != null) {
                FileObject file = line.getLookup().lookup(FileObject.class);
                return file;
            }
        }
        return null;
    }

    public boolean isRunning() {
        return running;
    }
}
