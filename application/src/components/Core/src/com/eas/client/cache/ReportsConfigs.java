/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppElementFiles;
import com.eas.util.FileUtils;
import java.io.File;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ReportsConfigs extends ActualCache<ReportConfig> {

    public ReportsConfigs() {
        super();
    }

    @Override
    public ReportConfig get(String aName, AppElementFiles aFiles) throws Exception {
        AppElementFiles files = new AppElementFiles();
        if (aFiles.hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION)) {
            files.addFile(aFiles.findFileByExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION));
        } else if (aFiles.hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X)) {
            files.addFile(aFiles.findFileByExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X));
        } else {
            throw new IllegalStateException("Application element " + aName + " has no report template file (*." + PlatypusFiles.REPORT_LAYOUT_EXTENSION + " | *." + PlatypusFiles.REPORT_LAYOUT_EXTENSION_X + ")");
        }
        return super.get(aName, files);
    }

    @Override
    protected ReportConfig parse(String aName, AppElementFiles aFiles) throws Exception {
        Set<File> files = aFiles.getFiles();
        assert files.size() == 1;
        File templateFile = files.iterator().next();
        return new ReportConfig(templateFile.getName(), FileUtils.getFileExtension(templateFile), FileUtils.readBytes(templateFile));
    }

}
