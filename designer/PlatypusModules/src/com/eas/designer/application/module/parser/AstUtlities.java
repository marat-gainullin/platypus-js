/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.parser;

import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;

/**
 *
 * @author vv
 */
public class AstUtlities {

    public static boolean isInNode(Node node, int offset) {
        return node.getStart() <= offset
                && offset <= node.getFinish() + 1;
    }
    
    public static boolean isInNode(Node outerNode, Node innerNode) {
        return outerNode.getStart() <= innerNode.getStart()
                && innerNode.getFinish() <= outerNode.getFinish();
    }

    public static Node getOffsetNode(Node node, final int offset) {
        GetOffsetNodeVisitorSupport vs = new GetOffsetNodeVisitorSupport(node, offset);
        Node offsetNode = vs.getOffsetNode();
        return offsetNode != null ? offsetNode : node;
    }

    private static class GetOffsetNodeVisitorSupport {

        private final Node root;
        private final int offset;
        private Node offsetNode;

        public GetOffsetNodeVisitorSupport(Node root, int offset) {
            this.root = root;
            this.offset = offset;
        }

        public Node getOffsetNode() {
            final LexicalContext lc = new LexicalContext();
            root.accept(new NodeVisitor<LexicalContext>(lc) {

                @Override
                protected boolean enterDefault(Node node) {
                    if (isInNode(node, offset)) {
                        offsetNode = node;
                        return true;
                    }
                    return false;
                }
            });
            return offsetNode;
        }
    }
}
