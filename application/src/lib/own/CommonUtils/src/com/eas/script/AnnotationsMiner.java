/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.Map;
import java.util.TreeMap;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Lexer;
import jdk.nashorn.internal.parser.Token;
import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.Source;

/**
 *
 * @author mg
 */
public abstract class AnnotationsMiner extends NodeVisitor<LexicalContext> {

    protected Map<Long, Long> prevComments = new TreeMap<>();
    protected Source source;

    public AnnotationsMiner(Source aSource) {
        super(new LexicalContext());
        source = aSource;
        mineComments();
    }

    private void mineComments() {
        TokenStream tokens = new TokenStream();
        Lexer lexer = new Lexer(source, tokens);
        long prevT = 0;
        long t;
        TokenType tt = TokenType.EOL;
        int i = 0;
        while (tt != TokenType.EOF) {
            // Get next token in nashorn's parser way
            while (i > tokens.last()) {
                if (tokens.isFull()) {
                    tokens.grow();
                }
                lexer.lexify();
            }
            t = tokens.get(i++);
            // next token has been obtained.
            tt = Token.descType(t);
            if (tt == TokenType.EOL) {
                continue;
            }
            TokenType prevTt = Token.descType(prevT);
            if (prevTt == TokenType.COMMENT) {
                prevComments.put(t, prevT);
            }
            prevT = t;
        }
    }

    protected int scopeLevel;

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        scopeLevel++;
        long ft = functionNode.getFirstToken();
        if (prevComments.containsKey(ft)) {
            long prevComment = prevComments.get(ft);
            commentedFunction(functionNode, source.getString(prevComment));
        }
        return super.enterFunctionNode(functionNode);
    }

    @Override
    public Node leaveFunctionNode(FunctionNode functionNode) {
        scopeLevel--;
        return super.leaveFunctionNode(functionNode);
    }

    protected abstract void commentedFunction(FunctionNode aFunction, String aComment);

    @Override
    public boolean enterBinaryNode(BinaryNode binaryNode) {
        if (scopeLevel == 1 && binaryNode.isAssignment() && !binaryNode.isSelfModifying()) {
            if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                AccessNode left = (AccessNode) binaryNode.getAssignmentDest();
                if (left.getBase().getSymbol().isThis()) {
                    long ft = left.getBase().getToken();
                    if (prevComments.containsKey(ft)) {
                        long prevComment = prevComments.get(ft);
                        commentedProperty(left.getProperty(), source.getString(prevComment));
                    }
                }
            }
        }
        return super.enterBinaryNode(binaryNode);
    }

    protected abstract void commentedProperty(IdentNode aProperty, String aComment);

}
