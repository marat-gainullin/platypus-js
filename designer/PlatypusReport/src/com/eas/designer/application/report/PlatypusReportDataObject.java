/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.cache.PlatypusFiles;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.report.completion.ReportModuleCompletionContext;
import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

@MIMEResolver.ExtensionRegistration(displayName = "#LBL_PlatypusReport_layout_file", extension = "xlsx", mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
public class PlatypusReportDataObject extends PlatypusModuleDataObject {

    protected Entry layoutEntry;

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

    @Override
    public ModuleCompletionContext getCompletionContext() {
        return new ReportModuleCompletionContext(this);
    }
    
    public boolean isTemplateValid() {
        File templateFile = FileUtil.toFile(getLayoutFile());
        String path = templateFile.getPath();
        String fileName = templateFile.getName();
        path = path.substring(0, path.length() - fileName.length());
        File fCandidate1 = new File(path + ".~lock." + fileName + "#");// open office
        File fCandidate2 = new File(path + "~$" + fileName); // microsoft office
        return !fCandidate1.exists() && !fCandidate2.exists();
    }

}
