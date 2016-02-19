/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion.report;

import com.eas.designer.codecompletion.TemplateInterceptor;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.netbeans.modules.javascript2.editor.model.DeclarationScope;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionArgument;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class LoadTemplateInterceptor implements FunctionInterceptor {

    private static final String REPORT_TEMPLATE_MODULE_NAME = "report-template";
    private static final String LOAD_TEMPLATE_NAME = "loadTemplate";
    private static final Pattern PATTERN = Pattern.compile(LOAD_TEMPLATE_NAME);

    public LoadTemplateInterceptor() {
        super();
    }

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    public Collection<TypeUsage> intercept(Snapshot snapshot, String name, JsObject globalObject,
            DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        try {
            FileObject fo = globalObject.getFileObject();
            return TemplateInterceptor.getModuleExposedTypes(fo, -1, factory, REPORT_TEMPLATE_MODULE_NAME);
        } catch (IOException ex) {
            Logger.getLogger(GenerateReportInterceptor.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptySet();
        }
    }

}
