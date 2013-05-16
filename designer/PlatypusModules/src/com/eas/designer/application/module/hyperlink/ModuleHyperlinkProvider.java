/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.hyperlink;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.parser.AstUtlities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class ModuleHyperlinkProvider implements HyperlinkProviderExt {

    private int startOffset;
    private int endOffset;
    private List<String> identifiersPath;

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return EnumSet.of(HyperlinkType.GO_TO_DECLARATION);
    }

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset, HyperlinkType type) {
        if (doc == null) {
            return false;
        }
        AstRoot tree = getAst(doc);
        if (tree == null) {
            return false;
        }
        AstNode node = AstUtlities.getOffsetNode(tree, offset);
        if (node == null || !(node instanceof Name)) {
            return false;
        }
        startOffset = node.getAbsolutePosition();
        endOffset = node.getAbsolutePosition() + node.getLength();
        return true;
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset, HyperlinkType type) {
        if (doc == null) {
            return null;
        }
        return new int[]{startOffset, endOffset};
    }

    @Override
    public void performClickAction(final Document doc, final int offset, HyperlinkType type) {
        final AtomicBoolean cancel = new AtomicBoolean();
        String name = NbBundle.getMessage(ModuleHyperlinkProvider.class, "NM_GoToDeclaration");
        ProgressUtils.runOffEventDispatchThread(new Runnable() {
            @Override
            public void run() {
                perform(doc, offset, cancel);
            }
        }, name, cancel, false);
    }

    @Override
    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        return NbBundle.getMessage(ModuleHyperlinkProvider.class, "NM_GoToDeclaration");
    }

    private AstRoot getAst(Document doc) {
        FileObject fo = NbEditorUtilities.getFileObject(doc);
        PlatypusModuleDataObject dataObject = null;
        try {
            DataObject dObject = DataObject.find(fo);
            if (dObject instanceof PlatypusModuleDataObject) {
                dataObject = (PlatypusModuleDataObject) dObject;
                return dataObject.getAst();
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private void perform(final Document doc, final int offset, final AtomicBoolean cancel) {
        try {
            openLocation(findDeclarationLocation(doc, offset, cancel));
        } catch (DataObjectNotFoundException | FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private DeclarationLocation findDeclarationLocation(Document doc, int offset, AtomicBoolean cancel) throws DataObjectNotFoundException, FileStateInvalidException {
        AstRoot tree = getAst(doc);
        if (tree == null) {
            return DeclarationLocation.NONE;
        }
        AstNode node = AstUtlities.getOffsetNode(tree, offset);
        if (node == null || !(node instanceof Name)) {
            return DeclarationLocation.NONE;
        }

        makePath(node);
        if (identifiersPath == null || identifiersPath.isEmpty()) {
            return DeclarationLocation.NONE;
        }

        FileObject fo = NbEditorUtilities.getFileObject(doc);
        PlatypusModuleDataObject appElementDataObject = (PlatypusModuleDataObject) DataObject.find(fo);
        for (int i = 0; i < identifiersPath.size() - 1; i++) {
            String fieldName = identifiersPath.get(i);
            CompletionContext typeCompletionContext = ModuleCompletionContext.findModuleCompletionContext(fieldName, offset, appElementDataObject);
            if (typeCompletionContext == null || typeCompletionContext == null || !(typeCompletionContext instanceof ModuleCompletionContext)) {
                return DeclarationLocation.NONE;
            }
            appElementDataObject = ((ModuleCompletionContext) typeCompletionContext).getDataObject();
            if (appElementDataObject == null) {
                return DeclarationLocation.NONE;
            }
            offset = 0;
        }
        String name = identifiersPath.get(identifiersPath.size() - 1);
        AstNode declarationNode = findDeclaration(identifiersPath.size() == 1 ? node : appElementDataObject.getAst(), name);
        if (declarationNode != null) {
            return new DeclarationLocation(appElementDataObject, declarationNode.getAbsolutePosition());
        }
        return DeclarationLocation.NONE;
    }

    private void makePath(AstNode node) {
        identifiersPath = new ArrayList<>();
        if (node.getParent() instanceof PropertyGet) {
            if (((PropertyGet) node.getParent()).getLeft() == node) {
                identifiersPath.add(((Name) node).getIdentifier());
            } else {
                node.getParent().visit(new NodeVisitor() {
                    @Override
                    public boolean visit(AstNode an) {
                        if (an instanceof Name) {
                            identifiersPath.add(((Name) an).getIdentifier());
                        }
                        return true;
                    }
                });
            }
        } else {
            identifiersPath.add(((Name) node).getIdentifier());
        }
    }

    private AstNode findDeclaration(AstNode node, String declarationName) {
        AstNode currentNode = node;
        for (;;) {//up to the root node
            if (currentNode instanceof ScriptNode) {
                ScriptNode scriptNode = (ScriptNode) currentNode;
                if (scriptNode instanceof FunctionNode) {
                    AstNode declaration = scanLevel(((FunctionNode) currentNode).getBody(), declarationName);
                    if (declaration != null) {
                        return declaration;
                    }
                } else {
                    AstNode declaration = scanLevel(currentNode, declarationName);
                    if (declaration != null) {
                        return declaration;
                    }
                }
            }
            currentNode = currentNode.getParent();
            if (currentNode == null) {
                break;
            }
        }
        return null;
    }

    private AstNode scanLevel(AstNode currentNode, String declarationName) {
        Node n = currentNode.getFirstChild();
        while (n != null) {
            if (n instanceof FunctionNode) {
                FunctionNode functionNode = (FunctionNode) n;
                if (functionNode.getFunctionName().getIdentifier().equals(declarationName)) {
                    return functionNode;
                }
            }
            if (n instanceof VariableDeclaration) {
                VariableDeclaration variableDeclarationNode = (VariableDeclaration) n;
                List<VariableInitializer> variables = variableDeclarationNode.getVariables();
                if (variables != null) {
                    for (VariableInitializer variable : variables) {
                        if (variable.getTarget() instanceof Name
                                && ((Name) variable.getTarget()).getIdentifier().equals(declarationName)) {
                            return variableDeclarationNode;
                        }
                    }
                }
            }
            n = n.getNext();
        }
        return null;
    }

    private static boolean openLocation(DeclarationLocation location) {
        if (location != DeclarationLocation.NONE && location.dataObject != null) {
            return open(location.dataObject, location.offset);
        }
        return false;
    }

    public static boolean open(final DataObject dataObject, final int offset) {
        assert dataObject != null;
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public @Override
                void run() {
                    doOpen(dataObject, offset);
                }
            });
            return true; // not exactly accurate, but....
        }
        return doOpen(dataObject, offset);
    }

    private static boolean doOpen(DataObject dataObject, int offset) {
        if (dataObject != null) {
            EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
            LineCookie lc = dataObject.getLookup().lookup(LineCookie.class);

            if ((ec != null) && (lc != null) && (offset != -1)) {
                try {
                    StyledDocument doc = ec.openDocument();

                    if (doc != null) {
                        int line = NbDocument.findLineNumber(doc, offset);
                        int lineOffset = NbDocument.findLineOffset(doc, line);
                        int column = offset - lineOffset;

                        if (line != -1) {
                            Line l = lc.getLineSet().getCurrent(line);

                            if (l != null) {
                                l.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS, column);
                                return true;
                            }
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
            if (oc != null) {
                oc.open();
                return true;
            }
        }
        return false;
    }

    private static class DeclarationLocation {

        public static final DeclarationLocation NONE = new DeclarationLocation(null, -1);
        public final DataObject dataObject;
        public final int offset;

        public DeclarationLocation(DataObject dataObject, int offset) {
            this.dataObject = dataObject;
            this.offset = offset;
        }
    }
}
