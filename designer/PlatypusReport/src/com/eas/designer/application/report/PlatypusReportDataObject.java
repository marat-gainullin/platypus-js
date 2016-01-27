/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.cache.PlatypusFiles;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

public class PlatypusReportDataObject extends PlatypusModuleDataObject {

    protected Entry layoutEntry;

    @MIMEResolver.ExtensionRegistration(displayName = "#LBL_PlatypusReport_layout_file", extension = "xlsx", mimeType = "application/ms-excel-x")
    public PlatypusReportDataObject(FileObject aJsFile, MultiFileLoader aLoader) throws Exception {
        super(aJsFile, aLoader);
        FileObject aLayoutFile = FileUtil.findBrother(aJsFile, PlatypusFiles.REPORT_LAYOUT_EXTENSION_X);
        if (aLayoutFile == null) {
            aLayoutFile = FileUtil.findBrother(aJsFile, PlatypusFiles.REPORT_LAYOUT_EXTENSION);
        }
        layoutEntry = registerEntry(aLayoutFile);
    }

    @Override
    protected Cookie[] createServices() {
        return new Cookie[]{new PlatypusReportSupport(this)};
    }

    public FileObject getLayoutFile() {
        return layoutEntry.getFile();
    }

    @Override
    protected Node createNodeDelegate() {
        Node node = super.createNodeDelegate();
        if (node instanceof AbstractNode) {
            ((AbstractNode) node).setIconBaseWithExtension(PlatypusReportDataObject.class.getPackage().getName().replace('.', '/') + "/report.png");
        }
        return node;
    }
}
