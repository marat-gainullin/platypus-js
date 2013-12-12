/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.REPORT_MODULE_NAME;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.explorer.utils.StringUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
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
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    public static final String THIS_KEYWORD = "this";//NOI18N
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
        }
    }

    protected void fillSystemConstructors(CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<SystemConstructorCompletionItem> items = scp.getSystemConstructors(point);
            if (items != null) {
                for (SystemConstructorCompletionItem item : items) {
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
                    ModuleCompletionContext.FindModuleElementSupport visitor =
                            new ModuleCompletionContext.FindModuleElementSupport(currentNode,
                            parentScope == PlatypusFilesSupport.extractModuleConstructor(astRoot),
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

        private final AstNode lookupScope;
        private final boolean moduleConstructorScope;
        private final String fieldName;
        private final ModuleCompletionContext parentContext;
        private CompletionContext ctx;

        public FindModuleElementSupport(AstNode aLookupScope, boolean aModuleConstructorScope, String aFieldName, ModuleCompletionContext aParentContext) {
            lookupScope = aLookupScope;
            moduleConstructorScope = aModuleConstructorScope;
            fieldName = aFieldName;
            parentContext = aParentContext;
        }

        public CompletionContext findContext() {
            lookupScope.visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode an) {
                    if (an == lookupScope) {
                        if (an instanceof FunctionNode) {// Completion support for Array iteration functions parameters on an entity
                            FunctionNode fn = (FunctionNode) an;
                            if (fn.getParams() != null && fn.getParams().size() > 0 && fieldName.equals(fn.getParams().get(0).toSource())) {
                                if (fn.getParent() instanceof FunctionCall) {
                                    FunctionCall fc = (FunctionCall) fn.getParent();
                                    List<CompletionToken> tokens = CompletionPoint.getContextTokens(fc);
                                    if (tokens != null && tokens.size() > 1) {
                                        CompletionToken funcitonCallToken = tokens.get(tokens.size() - 1);
                                        if (fn == fc.getArguments().get(0) && ARRAY_ITERATION_FUNCTIONS_NAMES.contains(funcitonCallToken.name)) {
                                            try {
                                                CompletionContext c = ModuleCompletionProvider.getCompletionContext(parentContext, tokens.subList(0, tokens.size() - 1), fc.getAbsolutePosition());
                                                if (c instanceof EntityCompletionContext) {
                                                    ctx = ((EntityCompletionContext)c).getElementCompletionContext();
                                                }
                                            } catch (Exception ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return true;
                    }
                    if (an instanceof PropertyGet && moduleConstructorScope) {
                        PropertyGet pg = (PropertyGet) an;
                        if (pg.getTarget() instanceof KeywordLiteral && Token.THIS == pg.getTarget().getType()) { // things like this.prop1
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
