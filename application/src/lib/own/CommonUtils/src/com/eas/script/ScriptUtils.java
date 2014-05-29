package com.eas.script;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.URLReader;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.objects.NativeDate;
import jdk.nashorn.internal.parser.Lexer;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.parser.Token;
import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.Undefined;
import jdk.nashorn.internal.runtime.options.Options;

/**
 *
 * @author vv, mg
 */
public class ScriptUtils {

    public static final String THIS_KEYWORD = "this";//NOI18N
    protected static JSObject toPrimitiveFunc;
    protected static JSObject lookupInGlobalFunc;
    protected static JSObject putInGlobalFunc;
    protected static JSObject getModuleFunc;
    protected static JSObject toDateFunc;
    protected static JSObject parseJsonFunc;
    protected static JSObject parseDatesFunc;
    protected static JSObject writeJsonFunc;
    protected static JSObject extendFunc;
    protected static JSObject scalarDefFunc;
    protected static JSObject collectionDefFunc;
    protected static ScriptEngine engine;

    public static void init() {
        if (engine == null) {
            engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                engine.eval("load('classpath:platypus.js');");
            } catch (ScriptException ex) {
                Logger.getLogger(ScriptUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean isValidJsIdentifier(final String aName) {
        if (aName != null && !aName.trim().isEmpty()) {
            try {
                FunctionNode astRoot = parseJs(String.format("function %s() {}", aName));
                return astRoot != null && !astRoot.getBody().getStatements().isEmpty();
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    public static FunctionNode parseJs(String aJsContent) {
        Source source = new Source("", aJsContent);//NOI18N
        Options options = new Options(null);
        ScriptEnvironment env = new ScriptEnvironment(options, null, null);
        ErrorManager errors = new ErrorManager();
        Parser p = new Parser(env, source, errors);
        return p.parse();
    }

    public static Object exec(URL aSource) throws ScriptException {
        return engine.eval(new URLReader(aSource), engine.getContext());
    }

    public static Object exec(String aSource) throws ScriptException {
        return engine.eval(aSource, engine.getContext());
    }

    public static void setToPrimitiveFunc(JSObject aValue) {
        assert toPrimitiveFunc == null;
        toPrimitiveFunc = aValue;
    }

    public static void setLookupInGlobalFunc(JSObject aValue) {
        assert lookupInGlobalFunc == null;
        lookupInGlobalFunc = aValue;
    }

    public static void setPutInGlobalFunc(JSObject aValue) {
        assert putInGlobalFunc == null;
        putInGlobalFunc = aValue;
    }

    public static void setGetModuleFunc(JSObject aValue) {
        assert getModuleFunc == null;
        getModuleFunc = aValue;
    }

    public static void setToDateFunc(JSObject aValue) {
        assert toDateFunc == null;
        toDateFunc = aValue;
    }

    public static void setParseJsonFunc(JSObject aValue) {
        assert parseJsonFunc == null;
        parseJsonFunc = aValue;
    }

    public static void setParseDatesFunc(JSObject aValue) {
        assert parseDatesFunc == null;
        parseDatesFunc = aValue;
    }

    public static void setWriteJsonFunc(JSObject aValue) {
        assert writeJsonFunc == null;
        writeJsonFunc = aValue;
    }

    public static void setExtendFunc(JSObject aValue) {
        assert extendFunc == null;
        extendFunc = aValue;
    }

    public static void setScalarDefFunc(JSObject aValue) {
        assert scalarDefFunc == null;
        scalarDefFunc = aValue;
    }

    public static void setCollectionDefFunc(JSObject aValue) {
        assert collectionDefFunc == null;
        collectionDefFunc = aValue;
    }

    public static Object toJava(Object aValue) {
        if (aValue instanceof JSObject) {
            assert toPrimitiveFunc != null : SCRIPT_NOT_INITIALIZED;
            aValue = toPrimitiveFunc.call(null, new Object[]{aValue});
        }
        return aValue;
    }

    public static Object toJs(Object aValue) {
        if (aValue instanceof Date) {// force js boxing of date, because of absence js literal of date value
            assert toDateFunc != null : SCRIPT_NOT_INITIALIZED;
            return toDateFunc.call(null, aValue);
        } else if (aValue instanceof HasPublished) {
            return ((HasPublished) aValue).getPublished();
        } else {
            return aValue;
        }
    }

    public static Object[] toJs(Object[] aArray) {
        Object[] publishedArgs = new Object[aArray.length];
        for (int i = 0; i < aArray.length; i++) {
            publishedArgs[i] = ScriptUtils.toJs(aArray[i]);
        }
        return publishedArgs;
    }

    /**
     * Extracts the comments tokens from a JavaScript source.
     *
     * @param aSource a source
     * @return a list of comment tokens
     */
    public static List<Long> getCommentsTokens(String aSource) {
        TokenStream tokens = new TokenStream();
        Lexer lexer = new Lexer(new Source("", aSource), tokens);//NOI18N
        long t;
        TokenType tt = TokenType.EOL;
        int i = 0;
        List<Long> commentsTokens = new ArrayList<>();
        while (tt != TokenType.EOF) {
            // Get next token in nashorn's parser way
            while (i > tokens.last()) {
                if (tokens.isFull()) {
                    tokens.grow();
                }
                lexer.lexify();
            }
            t = tokens.get(i++);
            tt = Token.descType(t);
            if (tt == TokenType.COMMENT) {
                commentsTokens.add(t);
            }
        }
        return commentsTokens;
    }

    /**
     * Removes all commentaries from some JavaScript code.
     *
     * @param text a source
     * @return comments-free JavaScript code
     */
    public static String removeComments(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Long t : getCommentsTokens(text)) {
            int offset = Token.descPosition(t);
            int lenght = Token.descLength(t);
            sb.append(text.substring(i, offset));
            for (int j = 0; j < lenght; j++) {
                sb.append(" ");//NOI18N
            }
            i = offset + lenght;
        }
        sb.append(text.substring(i));
        return sb.toString();
    }

    /**
     * Searches for all <code>this</code> aliases in a constructor.
     * @param moduleConstructor a constructor to search in
     * @return a set of aliases including <code>this</code> itself
     */
    public static Set<String> getThisAliases(final FunctionNode moduleConstructor) {
        final Set<String> aliases = new HashSet<>();
        if (moduleConstructor != null && moduleConstructor.getBody() != null) {
            aliases.add(THIS_KEYWORD);
            LexicalContext lc = new LexicalContext();
            moduleConstructor.accept(new NodeOperatorVisitor<LexicalContext>(lc) {

                @Override
                public boolean enterVarNode(VarNode varNode) {
                    if (lc.getCurrentFunction() == moduleConstructor) {
                        if (varNode.getAssignmentSource() instanceof IdentNode) {
                            IdentNode in = (IdentNode) varNode.getAssignmentSource();
                            if (THIS_KEYWORD.equals(in.getName())) {
                                aliases.add(varNode.getAssignmentDest().getName());
                            }
                        }
                    }
                    return super.enterVarNode(varNode);
                }
            });
        }
        return aliases;
    }

    public static Object parseJson(String json) {
        assert parseJsonFunc != null : SCRIPT_NOT_INITIALIZED;
        return parseJsonFunc.call(null, new Object[]{json});
    }

    public static Object parseDates(Object aObject) {
        assert parseDatesFunc != null : SCRIPT_NOT_INITIALIZED;
        return parseDatesFunc.call(null, new Object[]{aObject});
    }

    protected static final String SCRIPT_NOT_INITIALIZED = "Platypus script function are not initialized.";

    public static String toJson(Object aObj) {
        if (aObj instanceof Undefined) {//nashorn JSON parser could not work with undefind.
            aObj = null;
        }
        if (aObj instanceof JSObject || aObj instanceof String 
                || aObj instanceof Number || aObj instanceof Boolean || aObj instanceof ScriptObject || aObj == null) {
            return JSType.toString(writeJsonFunc.call(null, new Object[]{aObj}));
        } else {
            throw new IllegalArgumentException("Could not convert to JSON Java object!");
        }    
    }
    public static void extend(JSObject aChild, JSObject aParent) {
        assert extendFunc != null : SCRIPT_NOT_INITIALIZED;
        extendFunc.call(null, new Object[]{aChild, aParent});
    }

    public static JSObject scalarPropertyDefinition(JSObject targetEntity, String targetFieldName, String sourceFieldName) {
        assert scalarDefFunc != null : SCRIPT_NOT_INITIALIZED;
        return (JSObject) scalarDefFunc.newObject(new Object[]{targetEntity, targetFieldName, sourceFieldName});
    }

    public static JSObject collectionPropertyDefinition(JSObject sourceEntity, String targetFieldName, String sourceFieldName) {
        assert collectionDefFunc != null : SCRIPT_NOT_INITIALIZED;
        return (JSObject) collectionDefFunc.newObject(new Object[]{sourceEntity, targetFieldName, sourceFieldName});
    }
    
    public static JSObject createModule(String aModuleName) {
        assert lookupInGlobalFunc != null;
        Object oConstructor = lookupInGlobalFunc.call(null, new Object[]{aModuleName});
        if (oConstructor instanceof JSObject && ((JSObject) oConstructor).isFunction()) {
            JSObject jsConstructor = (JSObject) oConstructor;
            return (JSObject) jsConstructor.newObject(new Object[]{});
        } else {
            return null;
        }
    }

    public static JSObject lookupInGlobal(String aName) {
        assert lookupInGlobalFunc != null;
        return (JSObject) lookupInGlobalFunc.call(null, new Object[]{aName});
    }

    public static void putInGlobal(String aName, JSObject aValue) {
        assert putInGlobalFunc != null;
        putInGlobalFunc.call(null, new Object[]{aName, aValue});
    }

    public static JSObject getCachedModule(String aModuleName) {
        assert getModuleFunc != null;
        return (JSObject) getModuleFunc.call(null, new Object[]{aModuleName});
    }

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
