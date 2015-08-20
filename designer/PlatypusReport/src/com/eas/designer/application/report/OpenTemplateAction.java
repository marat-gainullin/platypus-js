/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.settings.SettingsConstants;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;

@ActionID(
        category = "File",
        id = "com.eas.designer.application.report.OpenTemplateAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenTemplateAction"
)
@ActionReference(path = "Loaders/application/ms-excel-x/Actions", position = 0)
public final class OpenTemplateAction extends AbstractAction {

    private final DataObject dataObject;

    public OpenTemplateAction(DataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            File templateFile = FileUtil.toFile(dataObject.getPrimaryFile());
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop desk = Desktop.getDesktop();
                desk.open(templateFile);
            } else {
                final String pathReport = Preferences.userRoot().node(SettingsConstants.CLIENT_SETTINGS_NODE).get(SettingsConstants.REPORT_RUN_COMMAND, "");
                Runtime.getRuntime().exec(String.format(pathReport, templateFile.getPath()));
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
