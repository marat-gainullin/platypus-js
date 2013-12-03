/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.AstProvider;
import com.eas.script.JsParser;
import org.mozilla.javascript.ast.AstRoot;

/**
 *
 * @author vv
 */
public class DataObjectMock implements AstProvider {
    
    private String source;
    
    public DataObjectMock(String source) {
        this.source = source;
    }

    @Override
    public AstRoot getAst() {
        return JsParser.parse(source);
    }
    
}
