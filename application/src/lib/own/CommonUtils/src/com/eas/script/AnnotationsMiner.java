package com.eas.script;

import java.util.Map;
import java.util.TreeMap;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Lexer;
import jdk.nashorn.internal.parser.Token;
import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.Source;

/**
 * Base class for annotations mining. Important! The visitor's
 * <code>accept</code> method must be invoked on an AST root.
 *
 * @author mg
 */
public abstract class AnnotationsMiner extends NodeVisitor<LexicalContext> {

    protected static class OpenLexer extends Lexer {

        protected LineInfoReceiver lineInfos = (int line1, int linePosition1) -> {
        };

        public OpenLexer(final Source source, final TokenStream stream) {
            super(source, stream);
        }

        public void resetPosition(int aPosition) {
            super.reset(aPosition);
        }

        @Override
        public boolean scanLiteral(long token, TokenType startTokenType, LineInfoReceiver lir) {
            return super.scanLiteral(token, startTokenType, lineInfos);
        }
    }

    protected final int TOP_SCOPE_LEVEL = 1;
    protected final int TOP_CONSTRUCTORS_SCOPE_LEVEL = 2;
    protected final int AMD_CONSTRUCTORS_SCOPE_LEVEL = 3;
    protected Map<Long, Long> prevComments = new TreeMap<>();
    protected Source source;

    public AnnotationsMiner(Source aSource) {
        super(new LexicalContext());
        source = aSource;
        mineComments();
    }

    private void mineComments() {
        TokenStream tokens = new TokenStream();
        OpenLexer lexer = new OpenLexer(source, tokens);
        long previousToken = 0;
        TokenType tokenType = TokenType.EOL;
        int i = 0;
        while (tokenType != TokenType.EOF) {
            long token = nextToken(i, tokens, lexer);
            tokenType = Token.descType(token);
            // Literals care
            boolean canStartLiteral = lexer.canStartLiteral(tokenType);
            boolean literalScanned = canStartLiteral && lexer.scanLiteral(token, tokenType, null);
            if (canStartLiteral && !literalScanned) {// scanLiteral() calls reset() and so, we have to revert it to repair plain token stream
                int tokenPosition = Token.descPosition(token);
                int tokenLength = Token.descLength(token);
                lexer.resetPosition(tokenPosition + tokenLength);
            }
            // Main logic
            if (tokenType != TokenType.EOL && !literalScanned) {// If a literal was scanned, it will be added as next separate token to the stream and we will see it twice...
                if (Token.descType(previousToken) == TokenType.COMMENT) {
                    prevComments.put(token, previousToken);
                }
                previousToken = token;
            }
            // Memory sanitizing
            if (i % 1024 == 0) {
                tokens.commit(i);
            }
            // !Unconditionally! increase token number
            i++;
        }
    }

    private long nextToken(int k, TokenStream tokens, Lexer lexer) {
        // Get next token in nashorn's parser way
        while (k > tokens.last()) {
            if (tokens.isFull()) {
                tokens.grow();
            }
            lexer.lexify();
        }
        long t = tokens.get(k);
        return t;
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

}
