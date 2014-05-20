/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.explorer.utils.StringUtils;
import com.eas.script.EventMethod;
import com.eas.script.ScriptObj;
import com.eas.util.PropertiesUtils;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.TokenType;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    protected static final String GET_METHOD_NAME = "get";// NOI18N
    public static final String THIS_KEYWORD = "this";//NOI18N
    public static final String PARAMS_SCRIPT_NAME = "params";// NOI18N
    protected static final String METADATA_SCRIPT_NAME = ApplicationDbModel.DATASOURCE_METADATA_SCRIPT_NAME;
    protected static final String MODULE_NAME = "Module";// NOI18N
    protected static final String SERVER_MODULE_NAME = "ServerModule";// NOI18N
    protected static final String FORM_MODULE_NAME = "Form";// NOI18N
    protected static final String REPORT_MODULE_NAME = "Report";// NOI18N
    protected static final String SERVER_REPORT_MODULE_NAME = "ServerReport";// NOI18N
    protected static final String MODULES_OBJECT_NAME = "Modules";// NOI18N
    protected static final String SYSTEM_OBJECT_NAME = "P";//NOI18N
    protected static final String LOAD_MODEL_METHOD_NAME = "loadModel";// NOI18N
    private static final Set<String> ARRAY_ITERATION_FUNCTIONS_NAMES = new HashSet<String>() {
        {
            add("forEach");//NOI18N
            add("filter");//NOI18N
            add("every");//NOI18N
            add("map");//NOI18N
            add("some");//NOI18N
            add("reduce");//NOI18N
            add("reduceRight");//NOI18N
        }
    };
    protected PlatypusModuleDataObject dataObject;

    public ModuleCompletionContext(PlatypusModuleDataObject aDataObject) {
        super(null);
        dataObject = aDataObject;
    }

    public PlatypusModuleDataObject getDataObject() {
        return dataObject;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
//        JsCodeCompletionScopeInfo completionScopeInfo = getCompletionScopeInfo(dataObject, offset, point.getFilter());
//        if (completionScopeInfo.mode == CompletionMode.CONSTRUCTORS) {
//            fillSystemConstructors(point, resultSet);
//        } else if (completionScopeInfo.mode == CompletionMode.VARIABLES_AND_FUNCTIONS) {
//            fillSystemObjects(point, resultSet);
//        }
    }

    protected void fillSystemConstructors(CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<JsCompletionItem> items = scp.getSystemConstructors(point);
            if (items != null) {
                for (JsCompletionItem item : items) {
                    addItem(resultSet, point.getFilter(), item);
                }
            }
        }
    }

    protected void fillSystemObjects(CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<JsCompletionItem> items = scp.getSystemObjects(point);
            if (items != null) {
                for (JsCompletionItem item : items) {
                    addItem(resultSet, point.getFilter(), item);
                }
            }
        }
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return findCompletionContext(token.name, offset, this);
    }

    public static JsCodeCompletionScopeInfo getCompletionScopeInfo(PlatypusModuleDataObject aDataObject, int offset, String text) {
        FunctionNode tree = aDataObject.getAstRoot();
        Node offsetNode = AstUtlities.getOffsetNode(tree, offset);
        CompletionMode codeCompletionInfo = isInNewExpression(offsetNode, text) ? CompletionMode.CONSTRUCTORS : CompletionMode.VARIABLES_AND_FUNCTIONS;
        return new JsCodeCompletionScopeInfo(offsetNode, codeCompletionInfo);
    }

    private static boolean isInNewExpression(jdk.nashorn.internal.ir.Node aNode, String txt) {
        assert false : "Refactoring is needed";
        return false;
        /*
         return (aNode != null) && ((aNode instanceof NewExpression && (txt == null || txt.isEmpty()))
         || (aNode instanceof Name && aNode.getParent() instanceof NewExpression));
         */
    }

    public static ModuleCompletionContext getModuleCompletionContext(Project project, String appElementId) {
        FileObject appElementFileObject = IndexerQuery.appElementId2File(project, appElementId);
        if (appElementFileObject == null) {
            return null;
        }
        try {
            DataObject referencedDataObject = DataObject.find(appElementFileObject);
            if (referencedDataObject instanceof PlatypusModuleDataObject) {
                return ((PlatypusModuleDataObject) DataObject.find(appElementFileObject)).getCompletionContext();
            }
        } catch (DataObjectNotFoundException ex) {
            //no-op
        }
        return null;
    }

    public static CompletionContext findCompletionContext(final String fieldName, final int offset, final ModuleCompletionContext parentModuleContext) {
        /*
         for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
         Class clazz = scp.getClassByName(fieldName);
         if (clazz != null && clazz.isAnnotationPresent(ScriptObj.class)) {
         return new CompletionContext(clazz);
         }
         }
         */

        //Collect a <code>CompletionContext</code> for all system objects like model and others.
        FunctionNode astRoot = parentModuleContext.dataObject.getAstRoot();
        class SystemLexicalContext extends LexicalContext {

            public Map<String, CompletionContext> systemCompletionContexts = new HashMap<>();

        }
        final SystemLexicalContext slc = new SystemLexicalContext();
        astRoot.accept(new NodeVisitor<SystemLexicalContext>(slc) {

            @Override
            protected boolean enterDefault(jdk.nashorn.internal.ir.Node node) {
                return super.enterDefault(node);
            }

            @Override
            public boolean enterVarNode(VarNode varNode) {
                if (AstUtlities.isInNode(lc.getCurrentFunction(), offset)) {
                    parentModuleContext.injectVarContext(lc.systemCompletionContexts, varNode);
                }
                return super.enterVarNode(varNode);
            }
        });
        if (slc.systemCompletionContexts.get(fieldName) != null) {
            return slc.systemCompletionContexts.get(fieldName);
        }

        //Lookup for an event handler function parameter.
        class CompletionLexicalContext extends LexicalContext {

            public CompletionContext completionContext;

        }
        final CompletionLexicalContext clc = new CompletionLexicalContext();
        astRoot.accept(new NodeVisitor<CompletionLexicalContext>(clc) {

            @Override
            public boolean enterBinaryNode(BinaryNode binaryNode) {
                if (AstUtlities.isInNode(binaryNode, offset)
                        && TokenType.ASSIGN.equals(binaryNode.tokenType())) {
                    if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                        List<CompletionToken> tokens = CompletionPoint.getContextTokens(parentModuleContext.dataObject.getAstRoot(), binaryNode.getAssignmentDest().getFinish());
                        if (tokens != null && tokens.size() > 1) {
                            try {
                                CompletionContext systemCompletionContext = slc.systemCompletionContexts.get(tokens.get(0).name);
                                CompletionContext propsCtx = ModuleCompletionProvider.getCompletionContext(systemCompletionContext, tokens.subList(1, tokens.size() - 1), 0);
                                if (propsCtx != null && propsCtx.getScriptClass() != null) {
                                    Class<?> eventClass = getEventClass(propsCtx.getScriptClass(), tokens.get(tokens.size() - 1).name);
                                    if (eventClass != null) {
                                        lc.completionContext = new CompletionContext(eventClass);
                                        return false;
                                    }
                                }
                            } catch (Exception ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    }
                }
                return super.enterBinaryNode(binaryNode);
            }
        });
        if (clc.completionContext != null) {
            return clc.completionContext;
        }
        //Lookup for an entity's row iteration methods handler's parameter.
        astRoot.accept(new NodeVisitor<CompletionLexicalContext>(clc) {

            @Override
            public boolean enterCallNode(CallNode callNode) {
                if (AstUtlities.isInNode(callNode, offset)
                    && callNode.getArgs() != null
                        && callNode.getArgs().size() > 0
                        && callNode.getArgs().get(0) instanceof FunctionNode) {
                        FunctionNode parameterFunction = (FunctionNode)callNode.getArgs().get(0);
                        if (parameterFunction.getParameters() != null 
                            && parameterFunction.getParameters().size() > 0
                            && fieldName.equals(parameterFunction.getParameters().get(0).getName())) {                 
                        List<CompletionToken> tokens = CompletionPoint.getContextTokens(parentModuleContext.dataObject.getAstRoot(), callNode.getFunction().getStart());
                        if (tokens != null && tokens.size() > 1) {
                            CompletionToken functionCallToken = tokens.get(tokens.size() - 1);
                            if (ARRAY_ITERATION_FUNCTIONS_NAMES.contains(functionCallToken.name)) {
                                try {
                                    CompletionContext systemCompletionContext = slc.systemCompletionContexts.get(tokens.get(0).name);
                                    CompletionContext c = ModuleCompletionProvider.getCompletionContext(systemCompletionContext, tokens.subList(1, tokens.size() - 1), 0);
                                    if (c instanceof EntityCompletionContext) {
                                        lc.completionContext = ((EntityCompletionContext) c).getElementCompletionContext();
                                        return false;
                                    }
                                } catch (Exception ex) {
                                    ErrorManager.getDefault().notify(ex);
                                }
                            }
                        }
                    }
                }
                return super.enterCallNode(callNode);
            }
        });

        return clc.completionContext;
    }

    public void injectVarContext(Map<String, CompletionContext> contexts, VarNode varNode) {
        if (isSystemObjectMethod(varNode.getAssignmentSource(), LOAD_MODEL_METHOD_NAME)) {
            contexts.put(varNode.getName().getName(), new ModelCompletionContext(getDataObject()));
        }
    }

    protected static boolean isSystemObjectMethod(Expression assignmentSource, String methodName) {
        if (assignmentSource instanceof CallNode) {
            CallNode cn = (CallNode) assignmentSource;
            if (cn.getFunction() instanceof AccessNode) {
                AccessNode an = (AccessNode) cn.getFunction();
                return methodName.equals(an.getProperty().getName())
                        && an.getBase() instanceof IdentNode
                        && SYSTEM_OBJECT_NAME.equals(((IdentNode) an.getBase()).getName());
            }
        }
        return false;
    }

    public static boolean isModuleInitializerName(String name) {
        return name.equals(MODULE_NAME)
                || name.equals(SERVER_MODULE_NAME)
                || name.equals(FORM_MODULE_NAME)
                || name.equals(REPORT_MODULE_NAME)
                || name.equals(SERVER_REPORT_MODULE_NAME);
    }

    private static Class<?> getEventClass(Class<?> scriptClass, String name) {
        for (Method method : scriptClass.getMethods()) {
            if (PropertiesUtils.isBeanPatternMethod(method)
                    && name.equals(PropertiesUtils.getPropertyName(method.getName()))
                    && method.isAnnotationPresent(EventMethod.class)) {
                return method.getAnnotation(EventMethod.class).eventClass();
            }
        }
        return null;
    }

    public enum CompletionMode {

        VARIABLES_AND_FUNCTIONS, CONSTRUCTORS
    }

    public static class JsCodeCompletionScopeInfo {

        public final jdk.nashorn.internal.ir.Node scope;
        public final CompletionMode mode;

        public JsCodeCompletionScopeInfo(jdk.nashorn.internal.ir.Node aScope, CompletionMode aMode) {
            scope = aScope;
            mode = aMode;
        }
    }

    private static class FindModuleElementSupport {

        private final jdk.nashorn.internal.ir.Node moduleConstructorScope;
        private final jdk.nashorn.internal.ir.Node parentScope;
        private final jdk.nashorn.internal.ir.Node lookupScope;

        private final String fieldName;
        private final ModuleCompletionContext parentContext;
        private CompletionContext ctx;

        public FindModuleElementSupport(jdk.nashorn.internal.ir.Node aModuleConstructor, jdk.nashorn.internal.ir.Node aParentNode, jdk.nashorn.internal.ir.Node aLookupScope, String aFieldName, ModuleCompletionContext aParentContext) {
            moduleConstructorScope = aModuleConstructor;
            parentScope = aParentNode;
            lookupScope = aLookupScope;
            fieldName = aFieldName;
            parentContext = aParentContext;
        }

        public CompletionContext findContext() {
            assert false : "Refactoring is needed";
            /*
             lookupScope.visit(new NodeVisitor() {
             @Override
             public boolean visit(AstNode an) {
             if (an == lookupScope) {
             if (an instanceof FunctionNode) {
             FunctionNode fn = (FunctionNode) an;
             if (fn.getParams() != null && fn.getParams().size() > 0
             && fieldName.equals(fn.getParams().get(0).toSource())) {// function parameter completion
             if (fn.getParent() instanceof FunctionCall) { // array iteration methods parameters on an entity with anonymous function an the fist parameter
             FunctionCall fc = (FunctionCall) fn.getParent();
             List<CompletionToken> tokens = CompletionPoint.getContextTokens(fc);
             if (tokens != null && tokens.size() > 1) {
             CompletionToken funcitonCallToken = tokens.get(tokens.size() - 1);
             if (fn == fc.getArguments().get(0) && ARRAY_ITERATION_FUNCTIONS_NAMES.contains(funcitonCallToken.name)) {
             try {
             CompletionContext c = ModuleCompletionProvider.getCompletionContext(parentContext, tokens.subList(0, tokens.size() - 1), fc.getAbsolutePosition());
             if (c instanceof EntityCompletionContext) {
             ctx = ((EntityCompletionContext) c).getElementCompletionContext();
             return false;
             }
             } catch (Exception ex) {
             ErrorManager.getDefault().notify(ex);
             }
             }
             }
             } else if (fn.getParent() instanceof Assignment) { // event handler function pameter with event assignment
             Assignment assignment = (Assignment) fn.getParent();
             if (assignment.getLeft() instanceof PropertyGet) {
             PropertyGet leftPg = (PropertyGet) assignment.getLeft();
             List<CompletionToken> tokens = CompletionPoint.getContextTokens(leftPg);
             if (tokens != null && tokens.size() > 1) {
             try {
             CompletionContext propsCtx = ModuleCompletionProvider.getCompletionContext(parentContext, tokens.subList(0, tokens.size() - 1), leftPg.getAbsolutePosition());
             if (propsCtx != null && propsCtx.getScriptClass() != null) {
             Class<?> eventClass = getEventClass(propsCtx.getScriptClass(), tokens.get(tokens.size() - 1).name);
             if (eventClass != null) {
             ctx = new CompletionContext(eventClass);
             return false;
             }
             }
             } catch (Exception ex) {
             ErrorManager.getDefault().notify(ex);
             }
             }
             }
             }
             }
             }
             return true;
             }
             if (parentScope == moduleConstructorScope) {
             if (an instanceof PropertyGet) {
             PropertyGet pg = (PropertyGet) an;
             if (THIS_KEYWORD.equals(fieldName)
             && pg.getTarget() instanceof KeywordLiteral
             && Token.THIS == pg.getTarget().getType()) { // this.prop1
             ctx = parentContext.createThisContext(false);
             return false;
             }
             } else if (an instanceof ExpressionStatement) {
             ExpressionStatement es = (ExpressionStatement) an;
             if (THIS_KEYWORD.equals(fieldName)
             && es.getExpression() instanceof KeywordLiteral
             && Token.THIS == es.getExpression().getType()) { // this.
             ctx = parentContext.createThisContext(false);
             return false;
             }
             } else if (Token.THIS == an.getType() && THIS_KEYWORD.equals(fieldName)) {
             ctx = parentContext.createThisContext(false);
             return false;
             }
             }
             if (an instanceof VariableDeclaration) {
             VariableDeclaration variableDeclaration = (VariableDeclaration) an;
             if (variableDeclaration.getVariables() != null) {
             for (VariableInitializer variableInitializer : variableDeclaration.getVariables()) {
             if (variableInitializer.getTarget() != null
             && variableInitializer.getTarget().getString().equals(fieldName)
             && variableInitializer.getInitializer() != null) {
             if (variableInitializer.getInitializer() instanceof NewExpression) {
             NewExpression ne = (NewExpression) variableInitializer.getInitializer();
             if (ne.getTarget() != null && ne.getTarget() instanceof Name) {
             //checks for new Module(moduleName) like expression 
             if (isModuleInitializerName(ne.getTarget().getString())
             && ne.getArguments() != null
             && ne.getArguments().size() > 0) {
             ModuleCompletionContext mctx = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(ne.getArguments().get(0).toSource()));
             if (mctx != null) {
             ctx = mctx.createThisContext(true);
             }
             return false;
             }
             //checks for Platypus API classes
             for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
             Class clazz = scp.getClassByName(ne.getTarget().getString());
             if (clazz != null) {
             ctx = new CompletionContext(clazz);
             return false;
             }
             }
             //checks for new ModuleName() expression
             ModuleCompletionContext mcc = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(ne.getTarget().getString()));
             if (mcc != null) {
             CompletionContext cc = mcc.createThisContext(true);
             if (cc != null) {
             ctx = cc;
             return false;
             }
             }
             }
             //checks for Modules.get(moduleName) expression
             } else if (variableInitializer.getInitializer() instanceof FunctionCall) {
             FunctionCall fc = (FunctionCall) variableInitializer.getInitializer();
             if (fc.getTarget() instanceof PropertyGet) {
             PropertyGet pg = (PropertyGet) fc.getTarget();
             if (pg.getLeft().getString().equals(MODULES_OBJECT_NAME)
             && pg.getRight().getString().equals(GET_METHOD_NAME)) {
             if (fc.getArguments() != null && fc.getArguments().size() > 0) {
             ctx = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(fc.getArguments().get(0).toSource())).createThisContext(true);
             return false;
             }
             }
             }
             } else if (variableInitializer.getInitializer() instanceof KeywordLiteral
             && Token.THIS == variableInitializer.getInitializer().getType()) {// var self = this;
             ctx = parentContext.createThisContext(false);
             return false;
             } else {
             List<CompletionToken> tokens = CompletionPoint.getContextTokens(variableInitializer);
             if (tokens != null && tokens.size() > 1) {
             try {
             ctx = ModuleCompletionProvider.getCompletionContext(parentContext, tokens, variableInitializer.getAbsolutePosition());
             return false;
             } catch (Exception ex) {
             ErrorManager.getDefault().notify(ex);
             }
             }
             }
             }
             }
             }
             }
             return true;
             }
             });
             */
            return ctx;
        }

        private static String stripElementId(String str) {
            return StringUtils.strip(StringUtils.strip(StringUtils.strip(str, "\""), "'"));//NOI18N
        }
    }
}
