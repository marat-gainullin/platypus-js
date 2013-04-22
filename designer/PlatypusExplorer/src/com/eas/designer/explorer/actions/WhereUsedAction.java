/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.actions;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.DataObjectProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.api.search.SearchControl;
import org.netbeans.api.search.SearchHistory;
import org.netbeans.api.search.SearchPattern;
import org.netbeans.api.search.SearchScopeOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "File", id = "WhereUsedAction")
@ActionRegistration(displayName = "#CTL_WhereUsedAction")
public final class WhereUsedAction implements ActionListener {

    private final DataObjectProvider context;

    public WhereUsedAction(DataObjectProvider aContext) {
        super();
        context = aContext;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String appElementId = IndexerQuery.file2AppElementId(context.getDataObject().getPrimaryFile());
        if (appElementId != null) {
            SearchPattern sp = SearchPattern.create(appElementId, false, true, false);
            SearchHistory.getDefault().add(sp);
            SearchControl.startBasicSearch(sp, SearchScopeOptions.create(), null);
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(WhereUsedAction.class, "MSG_AppElementNameNotSet"), NotifyDescriptor.WARNING_MESSAGE); // NOI18N
            DialogDisplayer.getDefault().notify(nd);
        }
    }
}
