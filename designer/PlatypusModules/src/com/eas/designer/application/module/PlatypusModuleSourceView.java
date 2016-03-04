/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.spi.debugger.ui.EditorContextDispatcher;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
public final class PlatypusModuleSourceView extends CloneableEditor implements MultiViewElement {

    static final long serialVersionUID = 53142032923497728L;
    protected PlatypusModuleDataObject dataObject;
    protected transient MultiViewElementCallback callback;
    protected transient JPanel toolsWrapper = new JPanel(new BorderLayout());
    protected transient JToolBar tools;

    public PlatypusModuleSourceView() {
        super();
        setLayout(new java.awt.BorderLayout());
    }

    public PlatypusModuleSourceView(PlatypusModuleDataObject aDataObject) {
        super(aDataObject.getLookup().lookup(PlatypusModuleSupport.class));
        setLayout(new java.awt.BorderLayout());
        dataObject = aDataObject;
        setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        // WARNING!!!
        // Don't refactor this code as getEditorPane().getDocument() | ((NbDocument.CustomToolbar) doc).createToolbar(lpane)
        // in getToolbarRepresentation() method.
        // NetBeans contains deadlock :(
        // Document initializing is performed in another thread and EDT thread wait untils it happens.
        // Initializing thread can perform some stuff like a new DebugJSAction() while document initialization.
        // Thus initializing thread wait for AWT treeLock already auqired by EDT and EDT waits for initializing thread.
        // HACK :(
        if (tools == null && comp instanceof JToolBar) {
            tools = (JToolBar) comp;
            toolsWrapper.add(comp, BorderLayout.CENTER);
        } else {
            super.add(comp, constraints, index);
        }
    }

    @Override
    public void add(Component comp, Object constraints) {
        add(comp, constraints, -1);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws java.io.IOException {
        super.writeExternal(out);
        out.writeObject(dataObject);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        dataObject = (PlatypusModuleDataObject) in.readObject();
        if (dataObject.isValid()) {
            setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
            super.componentActivated();
        }
    }

    @Override
    public void requestActive() {
        if (callback != null) {
            callback.requestActive();
        } else {
            super.requestActive();
        }
    }

    @Override
    public void requestVisible() {
        if (callback != null) {
            callback.requestVisible();
        } else {
            super.requestVisible();
        }
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        updateName();
        // If uncomment the following line, netbeans ocasionally hangs
//        DebuggerManager.getDebuggerManager().getBreakpoints();// initialize breakpoints' storing listeners of the debugger manager
    }

    @Override
    public void componentActivated() {
        if (dataObject.isValid()) {
            EditorContextDispatcher.getDefault(); //initialize EditorContextDispatcher's global lookup's listeners
        }
        callback.getTopComponent().requestActive();
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        dataObject.getLookup().lookup(PlatypusModuleSupport.class).shrink();
    }

    @Override
    public void updateName() {
        super.updateName();
        if (callback != null) {
            callback.getTopComponent().setHtmlDisplayName(getHtmlDisplayName());
            callback.updateTitle(getDisplayName());
        }
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        // See add() method hack.
        return toolsWrapper;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback aCallback) {
        callback = aCallback;
    }

    protected boolean isLastView() {
        List<CloneableTopComponent> views = dataObject.getLookup().lookup(PlatypusModuleSupport.class).getAllViews();
        return views.size() <= 1;
    }

    @Override
    protected boolean closeLast() {
        return true;
    }

    @Override
    public CloseOperationState canCloseElement() {
        if (dataObject.isModified() && isLastView()) {
            return MultiViewFactory.createUnsafeCloseState(PlatypusModuleSourceDescription.MODULE_SOURCE_VIEW_NAME, MultiViewFactory.NOOP_CLOSE_ACTION, MultiViewFactory.NOOP_CLOSE_ACTION);
        } else {
            return CloseOperationState.STATE_OK;
        }
    }

    @Override
    protected String preferredID() {
        return PlatypusModuleSourceDescription.MODULE_SOURCE_VIEW_NAME;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }
}
