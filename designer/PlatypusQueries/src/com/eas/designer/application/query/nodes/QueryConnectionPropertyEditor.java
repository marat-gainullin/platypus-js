/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.explorer.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.util.Collections;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public class QueryConnectionPropertyEditor extends PropertyEditorSupport {

    protected PlatypusQueryDataObject dataObject;

    public QueryConnectionPropertyEditor(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setValue(text);
    }

    @Override
    public Component getCustomEditor() {
        try {
            String oldConnectionId = getAsText();
            FileObject oldFile = oldConnectionId != null ? IndexerQuery.appElementId2File(dataObject.getProject(), oldConnectionId) : null;
            final FileChooser chooser = FileChooser.createInstance(dataObject.getAppRoot(), oldFile, Collections.singleton("text/connection+xml"));
            return chooser.getDialog(dataObject.getName() + " - dbId", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setValue(chooser.getSelectedAppElementName());
                }
            });// NOI18N
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}
