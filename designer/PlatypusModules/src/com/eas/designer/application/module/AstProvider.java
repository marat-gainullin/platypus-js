/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import jdk.nashorn.internal.ir.FunctionNode;

/**
 *
 * @author vv
 */
public interface AstProvider {
    
    FunctionNode getAst();
    
    FunctionNode getConstructor();
    
    void setAst(FunctionNode anAstRoot);
    
}
