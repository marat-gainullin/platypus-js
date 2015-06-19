/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbModel;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.script.EventMethod;
import com.eas.script.Scripts;
import com.eas.util.PropertiesUtils;
import java.lang.reflect.Method;
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
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.UnaryNode;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.TokenType;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * This class represents a JavaScript module completion context.
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    public static final String PARAMS_SCRIPT_NAME = "params";// NOI18N
    protected static final String METADATA_SCRIPT_NAME = ApplicationDbModel.DATASOURCE_METADATA_SCRIPT_NAME;
    protected static final String SERVER_MODULE_NAME = "ServerModule";// NOI18N
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

    public CompletionContext getInstanceCompletionContext() {
        return new ModuleInstanceCompletionContext(this);
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
    }

    @Override
    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return findCompletionContext(token.name, offset, this);
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

    protected static CompletionContext findCompletionContext(final String fieldName, final int offset, final ModuleCompletionContext parentModuleContext) {
        FunctionNode astRoot = parentModuleContext.dataObject.getAstRoot();
        if (astRoot != null) {
            //Collect a <code>CompletionContext</code> for all system objects like model and others.
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
                    if (Scripts.isInNode(lc.getCurrentFunction(), offset)) {
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
                    if (Scripts.isInNode(binaryNode, offset)
                            && TokenType.ASSIGN.equals(binaryNode.tokenType())) {
                        if (binaryNode.getAssignmentDest() instanceof AccessNode) {
                            List<CompletionToken> tokens = CompletionPoint.getContextTokens(parentModuleContext.dataObject.getAstRoot(), binaryNode.getAssignmentDest().getFinish());
                            if (tokens != null && tokens.size() > 2) {
                                try {
                                    CompletionContext systemCompletionContext = slc.systemCompletionContexts.get(tokens.get(0).name);
                                    CompletionToken handlerToken = tokens.get(tokens.size() - 2);
                                    CompletionContext propsCtx = ModuleCompletionProvider.getCompletionContext(systemCompletionContext, tokens.subList(1, tokens.size() - 2), 0);
                                    if (propsCtx != null && propsCtx.getScriptClass() != null) {
                                        Class<?> eventClass = getEventClass(propsCtx.getScriptClass(), handlerToken.name);
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
                    if (Scripts.isInNode(callNode, offset)
                            && callNode.getArgs() != null
                            && callNode.getArgs().size() > 0
                            && callNode.getArgs().get(0) instanceof FunctionNode) {
                        FunctionNode parameterFunction = (FunctionNode) callNode.getArgs().get(0);
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
        } else {
            return null;
        }
    }

    public void injectVarContext(Map<String, CompletionContext> contexts, VarNode varNode) {
        if (isSystemObjectMethod(varNode.getAssignmentSource(), LOAD_MODEL_METHOD_NAME)) {
            contexts.put(varNode.getName().getName(), new ModelCompletionContext(getDataObject()));
        } else {
            String elementId = tryGetModuleElementId(varNode.getAssignmentSource());
            if (elementId != null) {
                ModuleCompletionContext mcc = getModuleCompletionContext(getDataObject().getProject(), elementId);
                if (mcc != null) {
                    contexts.put(varNode.getName().getName(), mcc.getInstanceCompletionContext());
                }
            }

        }
    }

    private String tryGetModuleElementId(Expression assignmentSource) {
        if (assignmentSource instanceof UnaryNode) {
            UnaryNode un = (UnaryNode) assignmentSource;
            if (un.isTokenType(TokenType.NEW)) {
                if (un.getExpression() instanceof CallNode) {
                    CallNode call = (CallNode) un.getExpression();
                    if (call.getFunction() instanceof IdentNode) {
                        IdentNode in = (IdentNode) call.getFunction();
                        if (SERVER_MODULE_NAME.equals(in.getName()) && call.getArgs().size() > 0) {
                            if (call.getArgs().get(0) instanceof LiteralNode) {
                                return ((LiteralNode) call.getArgs().get(0)).getPropertyName();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isSystemObjectMethod(Expression assignmentSource, String methodName) {
        if (assignmentSource instanceof CallNode) {
            CallNode cn = (CallNode) assignmentSource;
            if (cn.getFunction() instanceof AccessNode) {
                AccessNode an = (AccessNode) cn.getFunction();
                return methodName.equals(an.getProperty())
                        && an.getBase() instanceof IdentNode
                        && SYSTEM_OBJECT_NAME.equals(((IdentNode) an.getBase()).getName());
            }
        }
        return false;
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
}
