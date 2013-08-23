/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.selectors;

import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.FileChooser;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mg
 */
public abstract class AppElementSelector implements AppElementSelectorCallback {

    protected FileObject appRoot;

    public AppElementSelector(FileObject anAppRoot) {
        super();
        appRoot = anAppRoot;
    }

    @Override
    public String select(String oldValue) throws Exception {
        FileObject oldFo =  null;
        if (oldValue != null) {
            oldFo = FileUtil.toFileObject(new File(oldValue));
        }
        Set<String> allowedMimeTypes = new HashSet<>();
        fillAllowedMimeTypes(allowedMimeTypes);
        return IndexerQuery.file2AppElementId(FileChooser.selectAppElement(appRoot, oldFo, allowedMimeTypes));
    }

    public abstract void fillAllowedMimeTypes(Set<String> allowedMimeTypes);
}
