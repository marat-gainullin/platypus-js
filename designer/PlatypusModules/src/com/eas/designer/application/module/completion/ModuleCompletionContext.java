/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.explorer.utils.StringUtils;
import java.util.Collection;
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
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    public static final String THIS_KEYWORD = "this"; //NOI18N
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
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        JsCodeCompletionScopeInfo completionScopeInfo = getCompletionScopeInfo(dataObject, offset, point.filter);
        if (completionScopeInfo.mode == CompletionMode.CONSTRUCTORS) {
            fillSystemConstructors(point, resultSet);
        }
    }

    protected void fillSystemConstructors(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<SystemConstructorCompletionItem> items = scp.getSystemConstructors(point);
            if (items != null) {
                for (SystemConstructorCompletionItem item : items) {
                    addItem(resultSet, point.filter, item);
                }
            }
        }
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        return findCompletionContext(fieldName, offset, this);
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
            FindModuleConstructorBlockSupport helper = new FindModuleConstructorBlockSupport();
            Block moduleConstructorBlock = helper.findModuleConstuctorBlock(astRoot);
            if (offsetNode != null && offsetNode.equals(moduleConstructorBlock) && THIS_KEYWORD.equals(fieldName)) {
                return parentModuleContext.createThisContext(false);
            }
            AstNode currentNode = offsetNode;
            for (;;) {//up to the root node  
                if (currentNode instanceof ScriptNode) {
                    ScriptNode scriptNode = (ScriptNode) currentNode;
                    ModuleCompletionContext.FindModuleElementSupport visitor =
                            new ModuleCompletionContext.FindModuleElementSupport(scriptNode, fieldName, parentModuleContext);
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

    private static boolean isModuleInitializerName(String name) {
        return name.equals(MODULE_NAME)
                || name.equals(SERVER_MODULE_NAME)
                || name.equals(FORM_MODULE_NAME)
                || name.equals(REPORT_MODULE_NAME);
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

    public static class FindModuleConstructorBlockSupport {

        Block constructorBlock;

        public Block findModuleConstuctorBlock(final AstRoot astRoot) {

            astRoot.visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode an) {
                    if (astRoot.equals(an)) {
                        return true;
                    }
                    if (an instanceof FunctionNode) {
                        FunctionNode fn = (FunctionNode) an;
                        constructorBlock = (Block) fn.getBody();
                    }
                    return false;
                }
            });
            return constructorBlock;
        }
    }

    private static class FindModuleElementSupport {

        private final AstNode node;
        private final String fieldName;
        private final ModuleCompletionContext parentContext;
        private CompletionContext ctx;

        public FindModuleElementSupport(AstNode aNode, String aFieldName, ModuleCompletionContext aParentContext) {
            node = aNode;
            fieldName = aFieldName;
            parentContext = aParentContext;
        }

        public CompletionContext findContext() {
            node.visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode an) {
                    if (an == node) {
                        return true;
                    }
                    if (an instanceof FunctionNode) {
                        return false;
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
                                            CompletionContext cc = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(ne.getTarget().getString())).createThisContext(false);
                                            if (cc != null) {
                                                ctx = cc;
                                                return false;
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
                                    } else if (variableInitializer.getInitializer() instanceof KeywordLiteral && Token.THIS == variableInitializer.getInitializer().getType()) {
                                        ctx = parentContext.createThisContext(false);
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
