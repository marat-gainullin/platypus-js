/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.server.httpservlet.HttpScriptContext;

/**
 *
 * @author vv
 */
public class HttpCompletionContext extends CompletionContext {

    public HttpCompletionContext() {
        super(HttpScriptContext.class);
    }

    @Override
    public CompletionContext getChildContext(CompletionPoint.CompletionToken token, int offset) throws Exception {
        if (HttpScriptContext.REQUEST_PROP_NAME.equals(token.name)) {
            return new CompletionContext(HttpScriptContext.Request.class);
        } else if ((HttpScriptContext.RESPONSE_PROP_NAME.equals(token.name))) {
            return new CompletionContext(HttpScriptContext.Response.class);
        }
        return null;
    }
}
