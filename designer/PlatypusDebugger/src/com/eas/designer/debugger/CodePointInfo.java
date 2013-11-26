/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.application.indexer.IndexerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class CodePointInfo {

    public String url;
    public FileObject fo;
    public int lineNo;
    public String functionName;
    public String threadName;
    public int frameIndex;

    private CodePointInfo() {
    }
    
    public static CodePointInfo valueOf(Project project, String[] tags) throws Exception {
        CodePointInfo cpInfo = new CodePointInfo();
        cpInfo.url = null;
        cpInfo.lineNo = -1;
        cpInfo.threadName = null;
        for (int i = 0; i < tags.length; i += 2) {
            String tagName = tags[i];
            String tagValue = tags[i + 1];
            switch (tagName) {
                case DebuggerMBean.URL_TAG_NAME:
                    cpInfo.url = tagValue;
                    break;
                case DebuggerMBean.LINE_TAG_NAME:
                    cpInfo.lineNo = Integer.valueOf(tagValue);
                    break;
                case DebuggerMBean.THREAD_NAME_TAG_NAME:
                    cpInfo.threadName = tagValue;
                    break;
                case DebuggerMBean.FUNCTION_NAME_TAG_NAME:
                    cpInfo.functionName = tagValue;
                    break;
            }
        }        
        cpInfo.fo = IndexerQuery.appElementId2File(project, cpInfo.url);
        if (cpInfo.fo == null) {
            cpInfo.fo = DebuggerUtils.getFileObjectByUrl(project, cpInfo.url);
        }
        return cpInfo;
    }

    public void show() throws Exception {
        if (fo != null) {
            DataObject dataObject = DataObject.find(fo);
            if (dataObject != null) {
                EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
                if (ec != null) {
                    if (ec.getLineSet() != null) {
                        final Line lineObject = ec.getLineSet().getCurrent(lineNo);
                        Runnable showRunnable = new Runnable() {

                            @Override
                            public void run() {
                                lineObject.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FRONT);
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
}
