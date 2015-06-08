/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppElementFiles;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import java.io.File;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ScriptsConfigs extends ActualCache<ScriptDocument> {

    public ScriptsConfigs() {
        super();
    }

    @Override
    public ScriptDocument get(String aName, AppElementFiles aFiles) throws Exception {
        AppElementFiles files = new AppElementFiles();
        if (aFiles.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
            files.addFile(aFiles.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION));
        } else {
            throw new IllegalStateException("Application element " + aName + " has no JavaScript source file (*." + PlatypusFiles.JAVASCRIPT_EXTENSION + ")");
        }
        return super.get(aName, files);
    }

    @Override
    protected ScriptDocument parse(String aName, AppElementFiles aFiles) throws Exception {
        Set<File> files = aFiles.getFiles();
        assert files.size() == 1;
        File sourceFile = files.iterator().next();
        String source = FileUtils.readString(sourceFile, SettingsConstants.COMMON_ENCODING);
        return ScriptDocument.parse(source, sourceFile.getPath());
    }

}
