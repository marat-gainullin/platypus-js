package com.eas.script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Lexer;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.parser.Token;
import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

/**
 * Base class for annotations mining. Important! The visitor's
 * <code>accept</code> method must be invoked on an AST root.
 *
 * @author mg
 */
public abstract class AnnotationsMiner extends NodeVisitor<LexicalContext> {

    protected static final int TOP_SCOPE_LEVEL = 1;
    protected static final int TOP_CONSTRUCTORS_SCOPE_LEVEL = 2;
    protected static final int AMD_CONSTRUCTORS_SCOPE_LEVEL = 3;
    protected Map<Long, Long> prevComments = new TreeMap<>();
    protected Source source;

    public AnnotationsMiner(Source aSource) {
        super(new LexicalContext());
        source = aSource;
        mineComments();
    }

    private void mineComments() {
        Options options = new Options(null);
        ScriptEnvironment env = new ScriptEnvironment(options, null, null);
        ErrorManager errors = new ErrorManager();
        Parser p = new Parser(env, source, errors) {
            @Override
            public FunctionNode parse(String scriptName, int startPos, int len, boolean allowPropertyFunction) {
                stream = new TokenStream(){
                    protected long prevToken;
                    
                    @Override
                    public void put(long token) {
                        if(Token.descType(token) != TokenType.EOL){
                            if(Token.descType(prevToken) == TokenType.COMMENT){
                                prevComments.put(token, prevToken);
                            }
                            prevToken = token;
                        }
                        super.put(token);
                    }
                
                };
                lexer = new Lexer(source, stream, false);

                // Set up first token (skips opening EOL.)
                k = -1;
                next();
                // Begin parse.
                try {
                    Method program = Parser.class.getDeclaredMethod("program", new Class[]{String.class, boolean.class});
                    program.setAccessible(true);
                    return (FunctionNode)program.invoke(this, new Object[]{scriptName, true});
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(AnnotationsMiner.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

        };
        p.parse();
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
