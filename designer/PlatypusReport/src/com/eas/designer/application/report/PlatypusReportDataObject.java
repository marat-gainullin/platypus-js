/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.reports.ExcelReport;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.events.ApplicationModuleEvents;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

@MIMEResolver.ExtensionRegistration(displayName="#LBL_PlatypusReport_layout_file", extension="xls", mimeType="application/vnd.ms-excel")
public class PlatypusReportDataObject extends PlatypusModuleDataObject {

    protected Entry layoutEntry;
    protected ExcelReport xlReport;

    public PlatypusReportDataObject(FileObject aJsFile, MultiFileLoader aLoader) throws Exception {
        super(aJsFile, aLoader);
        FileObject aLayoutFile = FileUtil.findBrother(aJsFile, PlatypusFiles.REPORT_LAYOUT_EXTENSION);
        layoutEntry = registerEntry(aLayoutFile);
    }

    public ExcelReport getLayoutData() throws Exception {
        checkLayoutData();
        return xlReport;
    }

    public void checkLayoutData() throws IOException {
        if (xlReport == null) {
            xlReport = new ExcelReport();
            xlReport.setTemplate(new CompactBlob(layoutEntry.getFile().asBytes()));
        }
    }

    @Override
    public void shrink() {
        super.shrink();
        xlReport = null;
    }

    @Override
    protected Cookie[] createServices() {
        return new Cookie[]{new PlatypusReportSupport(this), new ApplicationModuleEvents(this)};
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

    public void saveLayout() throws Exception {
        if (xlReport != null) {
            try (OutputStream out = layoutEntry.getFile().getOutputStream()) {
                out.write(xlReport.getTemplate().getData());
                out.flush();
            }
        }
    }
}
