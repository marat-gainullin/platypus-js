/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.actions;

import com.eas.designer.application.query.OpenedPaneEditorCookie;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.result.QueryResultsView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@ActionID(category = "File",
id = "com.eas.designer.application.query.actions.QueryResultsSelectedTextAction")
@ActionRegistration(displayName = "#CTL_QueryResultsAction")
@ActionReference(path = "Editors/text/x-platypus-sql/Popup", position = 500)
public final class QueryResultsSelectedTextAction implements ActionListener {

    private final PlatypusQueryDataObject context;

    public QueryResultsSelectedTextAction(PlatypusQueryDataObject aContext) {
        super();
        context = aContext;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            if (context.getBasesProxy() != null) {
                OpenedPaneEditorCookie ec = context.getLookup().lookup(OpenedPaneEditorCookie.class);
                if (ec != null && ec.getOpenedPane() != null) {
                    String selectedText = ec.getOpenedPane().getSelectedText();
                    if (selectedText != null && !selectedText.isEmpty()) {
                        QueryResultsAction.openQueryResultsView(new QueryResultsView(context.getBasesProxy(), context.getDatasourceName(), selectedText));
                        return;
                    }
                }
                QueryResultsAction.openQueryResultsView(new QueryResultsView(context));
            } else {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(QueryResultsSelectedTextAction.class, "LBL_CantQueryWithoutClient"), NotifyDescriptor.WARNING_MESSAGE));
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
