/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.util.FileUtils;
import java.io.File;

/**
 *
 * @author mg
 */
public class ReportsConfigs extends ActualCache<ReportConfig> {

    public ReportsConfigs() {
        super();
    }

    @Override
    protected ReportConfig parse(String aName, File aFile) throws Exception {
        return new ReportConfig(aName, FileUtils.getFileExtension(aFile), FileUtils.readBytes(aFile));
    }

}
