/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
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
    private static final String REPORT_CONSTRUCTOR_JSDOC = "/**\n"//NOI18N
            + "* Creates new Platypus Report application element instance.\n"//NOI18N
            + "* @param name Report application element name\n"//NOI18N
            + "*/";//NOI18N

    @Override
    public Class getClassByName(String name) {
        return null;
    }

    @Override
    public Collection<SystemConstructorCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<SystemConstructorCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(REPORT_CONSTRUCTOR_NAME, "", Collections.EMPTY_LIST, REPORT_CONSTRUCTOR_JSDOC, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        return constructors;
    }
}
