/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.actions;

import com.eas.designer.application.indexer.IndexerQuery;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

@ActionID(id = "com.eas.designer.explorer.actions.InfoAction", category = "Project")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_InfoAction")
@ActionReferences(value = {
    @ActionReference(path = "Shortcuts", name = "DS-I"),
    @ActionReference(path = "Menu/File", position = 632)})
public final class InfoAction implements ActionListener {

    private final DataObject context;

    public InfoAction(DataObject aContext) {
        super();
        context = aContext;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context != null) {
            NotifyDescriptor.InputLine il = new NotifyDescriptor.InputLine(NbBundle.getMessage(InfoAction.class, "AppElementID"),
                    context.getPrimaryFile().getName());
            il.setInputText(String.valueOf(IndexerQuery.file2AppElementId(context.getPrimaryFile())));
            DialogDisplayer.getDefault().notify(il);
        }
    }
}
