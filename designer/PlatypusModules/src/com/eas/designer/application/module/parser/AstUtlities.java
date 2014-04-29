/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.parser;

import jdk.nashorn.internal.ir.Node;

/**
 *
 * @author vv
 */
public class AstUtlities {

    public static boolean isInNode(Node node, int offset) {
        int startPosition = node.getStart();
        int endPosition = node.getFinish();
        return startPosition <= offset
                && offset <= endPosition;
    }

    public static Node getOffsetNode(Node node, final int offset) {
        GetOffsetNodeVisitorSupport vs = new GetOffsetNodeVisitorSupport(node, offset);
        Node offsetNode = vs.getOffsetNode();
        return offsetNode != null ? offsetNode : node;
    }

    private static class GetOffsetNodeVisitorSupport {
        
        private Node root;
        private int offset;
        private Node offsetNode;

        public GetOffsetNodeVisitorSupport(Node root, int offset) {
            this.root = root;
            this.offset = offset;
        }

        public Node getOffsetNode() {
            assert false : "Refactoring is needed";
            return null;
            /*
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
                    */
        }
    }
}
