/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.actions;

import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.PlatypusQuerySupport;
import com.eas.designer.application.query.result.QueryResultTopComponent;
import com.eas.designer.application.query.result.QueryResultsView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

@ActionID(category = "File",
id = "com.eas.designer.application.query.actions.QueryResultsAction")
@ActionRegistration(displayName = "#CTL_QueryResultsAction")
@ActionReference(path = "Loaders/text/x-platypus-sql/Actions", position = 140)
public final class QueryResultsAction implements ActionListener {

    private final PlatypusQuerySupport context;
    private static final String QUERY_RESULT_TOPCOMPONENT_PREFFERED_ID = "QueryResultTopComponent";

    public QueryResultsAction(PlatypusQuerySupport aContext) {
        super();
        context = aContext;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        runQuery(context.getDataObject());
    }

    public static void runQuery(PlatypusQueryDataObject dataObject) {
        try {
            if (dataObject.getBasesProxy() != null) {
                openQueryResultsView(new QueryResultsView(dataObject));
            } else {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(QueryResultsAction.class, "LBL_CantQueryWithoutClient"), NotifyDescriptor.WARNING_MESSAGE));
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    public static void openQueryResultsView(QueryResultsView view) throws Exception {
        QueryResultTopComponent window = (QueryResultTopComponent) WindowManager.getDefault().findTopComponent(QUERY_RESULT_TOPCOMPONENT_PREFFERED_ID);
        window.openAtTabPosition(0);
        window.addResultsView(view);
        window.requestActive();
    }
}
