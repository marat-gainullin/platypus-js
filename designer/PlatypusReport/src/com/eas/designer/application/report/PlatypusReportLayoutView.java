/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 *
 * @author mg
 */
@TopComponent.Description(preferredID = PlatypusReportLayoutDescription.REPORT_LAYOUT_VIEW_NAME, persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
public class PlatypusReportLayoutView extends TopComponent implements MultiViewElement {

    static final long serialVersionUID = 23142032223494428L;
    protected transient MultiViewElementCallback callback;
    protected PlatypusReportDataObject dataObject;
    protected transient JPanel toolsPnl = new JPanel();

    public PlatypusReportLayoutView() {
        super();
    }

    public PlatypusReportLayoutView(PlatypusReportDataObject aDataObject) throws Exception {
        this();
        dataObject = aDataObject;
        initEditorView();
    }

    protected void initEditorView() throws Exception {
        setLayout(new BorderLayout());
        ReportDesignerPanel panel = new ReportDesignerPanel(dataObject, () -> {
            dataObject.getLookup().lookup(PlatypusReportSupport.class).notifyModified();
        });
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws java.io.IOException {
        super.writeExternal(out);
        out.writeObject(dataObject);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        dataObject = (PlatypusReportDataObject) in.readObject();
        try {
            initEditorView();
        } catch (Exception ex) {
            throw new IOException(ex);
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
    public void requestActive() {
        if (callback != null) {
            callback.requestActive();
        } else {
            super.requestActive();
        }
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        updateName();
    }

    public void updateName() {
        setHtmlDisplayName(getHtmlDisplayName());
        setDisplayName(getDisplayName());
        if (callback != null) {
            callback.getTopComponent().setHtmlDisplayName(getHtmlDisplayName());
            callback.updateTitle(dataObject.getName());
        }
    }

    @Override
    public String getHtmlDisplayName() {
        String ldisplayName = "<html>" + dataObject.getName();
        if (dataObject.isModified()) {
            ldisplayName = "<html><b>" + dataObject.getName() + "</b>";
        }
        return ldisplayName;
    }

    @Override
    public Action[] getActions() {
        List<Action> actions = new ArrayList<>(Arrays.asList(super.getActions()));
        // XXX nicer to use MimeLookup for type-specific actions, but not easy; see org.netbeans.modules.editor.impl.EditorActionsProvider
        actions.add(null);
        actions.addAll(Utilities.actionsForPath("Editors/TabActions")); //NOI18N
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        if (dataObject.isValid()) {
            setActivatedNodes(new Node[0]);
            setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
        }
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
        dataObject.getLookup().lookup(PlatypusReportSupport.class).shrink();
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolsPnl;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback aCallback) {
        callback = aCallback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
}
