/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.CompletionSupportService;
import com.eas.designer.application.module.completion.JsCompletionItem;
import com.eas.designer.application.module.completion.SystemConstructorCompletionItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service = CompletionSupportService.class)
public class FormCompletionSupportService implements CompletionSupportService {

    private static final String FORM_CONSTRUCTOR_NAME = "Form";//NOI18N
    private static final String FORM_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Creates new Platypus Form application element instance.\n"//NOI18N
            + "* @param name Form application element name\n"//NOI18N
            + "*/";//NOI18N

    @Override
    public Class getClassByName(String name) {
        return FormUtils.getPlatypusApiClassByName(name);
    }

    @Override
    public Collection<JsCompletionItem> getSystemConstructors(CompletionPoint point) {
        List<JsCompletionItem> constructors = new ArrayList<>();
        constructors.add(new SystemConstructorCompletionItem(FORM_CONSTRUCTOR_NAME,
                "",//NOI18N
                new ArrayList<String>() {
                    {
                        add("name");//NOI18N
                    }
                },
                FORM_CONSTRUCTOR_JSDOC,
                point.getCaretBeginWordOffset(),
                point.getCaretEndWordOffset()));
        return constructors;
    }
}
