/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.Map;
import jdk.nashorn.internal.ir.FunctionNode;

/**
 *
 * @author mg
 */
public class ParsedJs {

    protected FunctionNode ast;
    protected Map<Long, Long> prevComments;

    public ParsedJs(FunctionNode aAst, Map<Long, Long> aPrevComments) {
        super();
        ast = aAst;
        prevComments = aPrevComments;
    }

    public FunctionNode getAst() {
        return ast;
    }

    public Map<Long, Long> getPrevComments() {
        return prevComments;
    }

}
