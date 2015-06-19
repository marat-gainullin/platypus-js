/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.Set;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.runtime.Source;

/**
 * An annotation miner for the properties of the instances of the objects spawned by
 * top level constructors.
 * Usually this is module's properties.
 * Important! The visitor's <code>accept</code> method must be invoked on an AST root.
 * @author vv
 */
public abstract class PropertiesAnnotationsMiner extends BaseAnnotationsMiner {

    protected final Set<String> thisAliases;

    public PropertiesAnnotationsMiner(Source aSource, Set<String> aThisAliases) {
        super(aSource);
        thisAliases = aThisAliases;
    }

    @Override
    public boolean enterBinaryNode(BinaryNode binaryNode) {
        if (scopeLevel == TOP_CONSTRUCTORS_SCOPE_LEVEL && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
            if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                if (left.getBase() instanceof IdentNode && thisAliases.contains(((IdentNode) left.getBase()).getName())) {
                    long ft = left.getBase().getToken();
                    if (prevComments.containsKey(ft)) {
                        long prevComment = prevComments.get(ft);
                        commentedProperty(left.getProperty(), source.getString(prevComment));
                    }                    
                    property(left.getProperty(), binaryNode.getAssignmentSource());
                }
            }
        }
        return super.enterBinaryNode(binaryNode);
    }

    protected abstract void commentedProperty(String aPropertyName, String aComment);

    protected abstract void property(String aPropertyName, Expression aValueExpression);
}
