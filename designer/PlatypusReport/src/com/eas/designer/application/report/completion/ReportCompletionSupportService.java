/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.JsCompletionItem;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service = CompletionSupportService.class)
public class ReportCompletionSupportService implements CompletionSupportService {

    private static final String REPORT_CONSTRUCTOR_NAME = "Report";//NOI18N
    private static final String REPORT_CONSTRUCTOR_JSDOC = "/**\n"//NOI18N
            + "* Creates new <code>Report</code> instance.\n"//NOI18N
            + "* @param name Report constructor name\n"//NOI18N
            + "*/";//NOI18N
    private static final String SERVER_REPORT_CONSTRUCTOR_NAME = "ServerReport";//NOI18N
    private static final String SERVER_REPORT_CONSTRUCTOR_JSDOC = "/**\n"//NOI18N
            + "* Creates new <code>Report</code> instance on the application server.\n"//NOI18N
            + "* @param name Report constructor name\n"//NOI18N
            + "*/";//NOI18N
    @Override
    public Class getClassByName(String name) {
        return null;
    }

    @Override
    public Collection<JsCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<JsCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(REPORT_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                }, REPORT_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        constructors.add(new SystemConstructorCompletionItem(SERVER_REPORT_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                }, SERVER_REPORT_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        return constructors;
    }

    @Override
    public Collection<JsCompletionItem> getSystemObjects(CompletionPoint point) {
        List<JsCompletionItem> items = new ArrayList<>();
        return items;
    }
}
