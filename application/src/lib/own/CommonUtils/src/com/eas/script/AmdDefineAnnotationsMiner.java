/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.runtime.Source;

/**
 *
 * @author mg
 */
public abstract class AmdDefineAnnotationsMiner extends BaseAnnotationsMiner {

    public AmdDefineAnnotationsMiner(Source aSource) {
        super(aSource);
    }

    private void amdNameByToken(CallNode callNode, long aToken) {
        if (prevComments.containsKey(aToken)) {
            long prevComment = prevComments.get(aToken);
            String defineCallComment = source.getString(prevComment);
            JsDoc jsDoc = new JsDoc(defineCallComment);
            jsDoc.parseAnnotations();
            commentedDefineCall(callNode, jsDoc);
        }
    }

    @Override
    public boolean enterCallNode(CallNode callNode) {
        Expression func = callNode.getFunction();
        if (func instanceof AccessNode) {
            AccessNode an = (AccessNode) func;
            if ("define".equals(an.getProperty())) {
                amdNameByToken(callNode, an.getBase().getToken());
            }
        } else if (func instanceof IdentNode) {
            IdentNode in = (IdentNode) func;
            if ("define".equals(in.getName())) {
                amdNameByToken(callNode, in.getToken());
            }
        }
        return super.enterCallNode(callNode);
    }

    @Override
    protected void commentedFunction(FunctionNode aFunction, String aComment) {
        // no op
    }

    protected abstract void commentedDefineCall(CallNode aCallNode, JsDoc aJsDoc);
}
