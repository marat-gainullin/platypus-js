/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.ModuleUtils;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.events.ApplicationEntityEventProperty;
import com.eas.designer.application.module.nodes.ApplicationEntityNode;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.utils.StringUtils;
import com.eas.script.ScriptObj;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    public static final String THIS_KEYWORD = "this";//NOI18N
    public static final String PARAMS_SCRIPT_NAME = "params";// NOI18N
    protected static final String METADATA_SCRIPT_NAME = ApplicationDbModel.DATASOURCE_METADATA_SCRIPT_NAME;
    protected static final String MODULE_NAME = "Module";// NOI18N
    protected static final String SERVER_MODULE_NAME = "ServerModule";// NOI18N
    protected static final String FORM_MODULE_NAME = "Form";// NOI18N
    protected static final String REPORT_MODULE_NAME = "Report";// NOI18N
    protected static final String SERVER_REPORT_MODULE_NAME = "ServerReport";// NOI18N
    protected static final String MODULES_OBJECT_NAME = "Modules";// NOI18N
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

    public ModuleCompletionContext(PlatypusModuleDataObject aDataObject, Class<?> aClass) {
        super(aClass);
        dataObject = aDataObject;
    }

    public PlatypusModuleDataObject getDataObject() {
        return dataObject;
    }

    public ModuleThisCompletionContext createThisContext(boolean enableJsElementsCompletion) {
        return new ModuleThisCompletionContext(this, enableJsElementsCompletion);
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        JsCodeCompletionScopeInfo completionScopeInfo = getCompletionScopeInfo(dataObject, offset, point.getFilter());
        if (completionScopeInfo.mode == CompletionMode.CONSTRUCTORS) {
            fillSystemConstructors(point, resultSet);
        } else if (completionScopeInfo.mode == CompletionMode.VARIABLES_AND_FUNCTIONS) {
            fillSystemObjects(point, resultSet);
        }
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

    protected Class<?> getEventHandlerFunctionParameterClass(String functionName) {
        if (functionName == null) {
            throw new NullPointerException("Function name is null.");
        } else {
            try {
                ModelNode<ApplicationDbEntity, ApplicationDbModel> modelNode = getDataObject().getModelNode();
                Children modelChildren = modelNode.getChildren();
                for (Node node : modelChildren.getNodes()) {
                    if (node instanceof ApplicationEntityNode) {
                        PropertySet[] propertySets = node.getPropertySets();
                        for (PropertySet ps : propertySets) {
                            if (ApplicationEntityNode.EVENTS_PROPERTY_SET_NAME.equals(ps.getName())) {
                                for (Property p : ps.getProperties()) {
                                    if (p instanceof ApplicationEntityEventProperty) {
                                        ApplicationEntityEventProperty eventProperty = (ApplicationEntityEventProperty) p;
                                        if (eventProperty.hasEventHandler() && functionName.equals(eventProperty.getEventHandler())) {
                                            return ModuleUtils.getScriptEventClass(eventProperty.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
            return null;
        }
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return findCompletionContext(token.name, offset, this);
    }

    public static JsCodeCompletionScopeInfo getCompletionScopeInfo(PlatypusModuleDataObject aDataObject, int offset, String text) {
        AstRoot tree = aDataObject.getAst();
        AstNode offsetNode = AstUtlities.getOffsetNode(tree, offset);
        CompletionMode codeCompletionInfo = isInNewExpression(offsetNode, text) ? CompletionMode.CONSTRUCTORS : CompletionMode.VARIABLES_AND_FUNCTIONS;
        return new JsCodeCompletionScopeInfo(offsetNode, codeCompletionInfo);
    }

    private static boolean isInNewExpression(AstNode aNode, String txt) {
        return (aNode != null) && ((aNode instanceof NewExpression && (txt == null || txt.isEmpty()))
                || (aNode instanceof Name && aNode.getParent() instanceof NewExpression));
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

    public static CompletionContext findCompletionContext(String fieldName, int offset, ModuleCompletionContext parentModuleContext) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Class clazz = scp.getClassByName(fieldName);
            if (clazz !=null && clazz.isAnnotationPresent(ScriptObj.class)) {
                return new CompletionContext(clazz);
            }
        }
        AstRoot astRoot = parentModuleContext.dataObject.getAst();
        if (astRoot != null) {
            AstNode offsetNode = AstUtlities.getOffsetNode(astRoot, offset);
            AstNode currentNode = offsetNode;
            AstNode parentScope = null;
            for (;;) {//up to the root node  
                if (currentNode instanceof ScriptNode) {
                    if (parentScope == null) {
                        parentScope = currentNode;
                    }
                    ModuleCompletionContext.FindModuleElementSupport visitor
                            = new ModuleCompletionContext.FindModuleElementSupport(PlatypusFilesSupport.extractModuleConstructor(astRoot),
                                    parentScope,
                                    currentNode,
                                    fieldName,
                                    parentModuleContext);
                    CompletionContext ctx = visitor.findContext();
                    if (ctx != null) {
                        return ctx;
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode == null) {
                    break;
                }
            }
        }
        return null;
    }

    public static boolean isModuleInitializerName(String name) {
        return name.equals(MODULE_NAME)
                || name.equals(SERVER_MODULE_NAME)
                || name.equals(FORM_MODULE_NAME)
                || name.equals(REPORT_MODULE_NAME)
                || name.equals(SERVER_REPORT_MODULE_NAME);
    }

    public enum CompletionMode {

        VARIABLES_AND_FUNCTIONS, CONSTRUCTORS
    }

    public static class JsCodeCompletionScopeInfo {

        public final AstNode scope;
        public final CompletionMode mode;

        public JsCodeCompletionScopeInfo(AstNode aScope, CompletionMode aMode) {
            scope = aScope;
            mode = aMode;
        }
    }

    private static class FindModuleElementSupport {

        private final AstNode moduleConstructorScope;
        private final AstNode parentScope;
        private final AstNode lookupScope;

        private final String fieldName;
        private final ModuleCompletionContext parentContext;
        private CompletionContext ctx;

        public FindModuleElementSupport(AstNode aModuleConstructor, AstNode aParentNode, AstNode aLookupScope, String aFieldName, ModuleCompletionContext aParentContext) {
            moduleConstructorScope = aModuleConstructor;
            parentScope = aParentNode;
            lookupScope = aLookupScope;
            fieldName = aFieldName;
            parentContext = aParentContext;
        }

        public CompletionContext findContext() {
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
                                } else if (fn.getName() != null && fn.getParent() instanceof Block && fn.getParent().getParent() == moduleConstructorScope) {
                                    //event handler function parameter
                                    Class<?> eventClass = parentContext.getEventHandlerFunctionParameterClass(fn.getName());
                                    if (eventClass != null) {
                                        ctx = new CompletionContext(eventClass);
                                        return false;
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
                                    }
                                }
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
            return ctx;
        }

        private static String stripElementId(String str) {
            return StringUtils.strip(StringUtils.strip(StringUtils.strip(str, "\""), "'"));//NOI18N
        }
    }
}
