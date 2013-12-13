/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

/**
 *
 * @author vv
 */
public class JsParser {

    public static AstRoot parse(String source) throws EvaluatorException {
        CompilerEnvirons compilerEnv = CompilerEnvirons.ideEnvirons();
        compilerEnv.setRecordingLocalJsDocComments(true);
        compilerEnv.setRecoverFromErrors(true);
        Parser p = new Parser(compilerEnv, compilerEnv.getErrorReporter());
        AstRoot root = p.parse(source, "", 0); //NOI18N 
        return root;
    }   
}
