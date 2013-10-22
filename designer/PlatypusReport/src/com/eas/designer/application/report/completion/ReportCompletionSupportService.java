/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.client.cache.PlatypusFiles;
import com.eas.designer.application.indexer.AppElementInfo;
import com.eas.designer.application.module.completion.AppElementConstructorCompletionItem;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.JsCompletionProvider;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import com.eas.designer.application.report.PlatypusReportDataLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service = CompletionSupportService.class)
public class ReportCompletionSupportService implements CompletionSupportService {

    private static final String REPORT_CONSTRUCTOR_NAME = "Report";//NOI18N
    private static final String REPORT_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new Platypus Report application element instance.\n"//NOI18N
            + "* @param name Report application element name\n"//NOI18N
            + "*/";//NOI18N

    @Override
    public Class getClassByName(String name) {
        return null;
    }

    @Override
    public Collection<SystemConstructorCompletionItem> getSystemConstructors(JsCompletionProvider.CompletionPoint point) {
        List<SystemConstructorCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(REPORT_CONSTRUCTOR_NAME, "", SystemConstructorCompletionItem.DOUBLE_QUOTES_PARAMS, REPORT_CONSTRUCTOR_JSDOC, point.caretBeginWordOffset, point.caretEndWordOffset));
        return constructors;
    }

    @Override
    public Collection<AppElementConstructorCompletionItem> getAppElementsConstructors(Collection<AppElementInfo> appElements, JsCompletionProvider.CompletionPoint point) {
        List<AppElementConstructorCompletionItem> constructors = new ArrayList<>();
        for (AppElementInfo appElementInfo : appElements) {
            if (PlatypusFiles.JAVASCRIPT_EXTENSION.equals(appElementInfo.primaryFileObject.getExt())
                    && appElementInfo.primaryFileObject.equals(PlatypusReportDataLoader.findPrimaryFileImpl(appElementInfo.primaryFileObject))) {
                constructors.add(new ReportConstructorCompletionItem(appElementInfo.appElementId, "", Collections.<String>emptyList(), appElementInfo.primaryFileObject, point.caretBeginWordOffset, point.caretEndWordOffset));
            }
        }
        return constructors;
    }
}
