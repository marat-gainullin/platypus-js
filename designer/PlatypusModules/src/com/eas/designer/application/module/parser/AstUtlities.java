/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.parser;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

/**
 *
 * @author vv
 */
public class AstUtlities {

    public static boolean isInNode(AstNode node, int offset) {
        int startPosition = node.getAbsolutePosition();
        int endPosition = node.getAbsolutePosition() + node.getLength();
        return startPosition <= offset
                && offset <= endPosition;
    }

    public static AstNode getOffsetNode(AstNode node, final int offset) {
        GetOffsetNodeVisitorSupport vs = new GetOffsetNodeVisitorSupport(node, offset);
        AstNode offsetNode = vs.getOffsetNode();
        return offsetNode != null ? offsetNode : node;
    }

    private static class GetOffsetNodeVisitorSupport {
        
        private AstNode root;
        private int offset;
        private AstNode offsetNode;

        public GetOffsetNodeVisitorSupport(AstNode root, int offset) {
            this.root = root;
            this.offset = offset;
        }

        public AstNode getOffsetNode() {
            root.visit(new NodeVisitor() {
                
                @Override
                public boolean visit(AstNode an) {
                    if (isInNode(an, offset)) {
                        offsetNode = an;
                        return true;
                    }
                    return false;
                }
            });
            return offsetNode;
        }
    }
}
