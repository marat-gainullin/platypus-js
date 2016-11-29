/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import java.io.File;

/**
 * caches ScriptDocument by default module name for a file, i.e. app/folder/a.js will be parsed and
 * stored under key "folder/a"
 * @author mg
 */
public class ScriptsConfigs extends ActualCache<ScriptDocument> {

    public ScriptsConfigs() {
        super();
    }

    @Override
    public ScriptDocument get(String aDefaultModuleName, File aFile) throws Exception {
        return super.get(aDefaultModuleName, aFile);
    }

    public ScriptDocument getCachedConfig(String aDefaultModuleName) {
        ActualCacheEntry<ScriptDocument> docEntry = entries.get(aDefaultModuleName);
        return docEntry != null ? docEntry.getValue() : null;
    }

    @Override
    protected ScriptDocument parse(String aDefaultModuleName, File aFile) throws Exception {
        String source = FileUtils.readString(aFile, SettingsConstants.COMMON_ENCODING);
        return ScriptDocument.parse(source, aDefaultModuleName);
    }

}
