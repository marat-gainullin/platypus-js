/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion.report;

import com.eas.designer.codecompletion.TemplateInterceptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.api.lexer.LexUtilities;
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
public class GenerateReportInterceptor implements FunctionInterceptor {

    private static final String REPORT_MODULE_NAME = "report";
    private static final String GENERATE_REPORT_NAME = "generateReport";
    private static final Pattern PATTERN = Pattern.compile(".+\\." + GENERATE_REPORT_NAME);

    public GenerateReportInterceptor() {
        super();
    }

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    public Collection<TypeUsage> intercept(Snapshot snapshot, String name, JsObject globalObject,
            DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        TokenHierarchy<?> th = snapshot.getTokenHierarchy();
        TokenSequence<? extends JsTokenId> ts = (TokenSequence<? extends JsTokenId>) th.tokenSequence();
        ts.moveStart();
        while (ts.moveNext()) {
            if (ts.offsetToken().id() == JsTokenId.IDENTIFIER && GENERATE_REPORT_NAME.equals(ts.offsetToken().text().toString())) {
                int offset = ts.offset();
                try {
                    interceptGenerateReport(th, ts, name, globalObject, scope, factory, args);
                } finally {
                    ts.move(offset);
                    ts.moveNext();
                }
            }
        }
        return Collections.emptySet();
    }

    public Collection<TypeUsage> interceptGenerateReport(TokenHierarchy<?> th, TokenSequence<? extends JsTokenId> ts, String name, JsObject globalObject,
            DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        Token<? extends JsTokenId> generateReportToken = ts.offsetToken();
        if (generateReportToken != null && generateReportToken.id() == JsTokenId.IDENTIFIER && GENERATE_REPORT_NAME.equals(generateReportToken.text().toString())) {
            int generateReportOffset = generateReportToken.offset(th);
            Token<? extends JsTokenId> token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.OPERATOR_ASSIGNMENT, JsTokenId.OPERATOR_SEMICOLON));
            if (token != null && token.id() == JsTokenId.OPERATOR_ASSIGNMENT) {
                token = LexUtilities.findPreviousIncluding(ts, Arrays.asList(JsTokenId.IDENTIFIER, JsTokenId.OPERATOR_SEMICOLON,
                        JsTokenId.BRACKET_LEFT_BRACKET, JsTokenId.BRACKET_LEFT_CURLY, JsTokenId.BRACKET_LEFT_PAREN));
                if (token != null && token.id() == JsTokenId.IDENTIFIER) {
                    String objectName = token.text().toString();
                    JsObject jsReport = ((JsObject) scope).getProperty(objectName);
                    if (jsReport != null) {
                        try {
                            FileObject fo = jsReport.getFileObject();
                            Collection<TypeUsage> apiReportTypes = TemplateInterceptor.getModuleExposedTypes(fo, generateReportOffset, factory, REPORT_MODULE_NAME);
                            apiReportTypes.stream().forEach((apiReportType) -> {
                                jsReport.addAssignment(apiReportType, apiReportType.getOffset());
                            });
                        } catch (Exception ex) {
                            Logger.getLogger(GenerateReportInterceptor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }
}
