/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 *
 * @author vv
 */
public class FileIndexer extends CustomIndexer {

    public static final String INDEXER_NAME = "platypusCustomIndexer"; //NOI18N
    public static final int INDEXER_VERSION = 1;
    public static final String FIELD_NAME = "file-name"; //NOI18N
    public static final String PUBLIC_FIELD_NAME = "is-public"; //NOI18N
    public static final String FULL_PATH = "full-path"; //NOI18N
    public static final String APP_ELEMENT_NAME = "app-element-name"; //NOI18N

    @Override
    protected void index(Iterable<? extends Indexable> files, Context context) {
        try {
            IndexingSupport is = IndexingSupport.getInstance(context);
            for (Indexable i : files) {
                if (context.isCancelled()) {
                    break;
                }
                String nameExt = getNameExt(i);
                if (nameExt.length() > 0 && isIndexableFile(nameExt)) {
                    URL indexableURL = i.getURL();
                    if (indexableURL == null) {
                        continue;
                    }
                    FileObject fo = URLMapper.findFileObject(indexableURL);
                    if (fo == null) {
                        continue;
                    }
                    IndexDocument d = is.createDocument(i);
                    // By default look for application element name in annotation
                    File f = FileUtil.toFile(fo);
                    if (f != null) {
                        String appElementName = null;
                        boolean isPublic = false;
                        if (nameExt.endsWith("." + PlatypusFiles.CONNECTION_EXTENSION)) {
                            appElementName = PlatypusFilesSupport.getAppElementIdForConnectionAppElement(f);
                        } else {
                            String fileContent = FileUtils.readString(f, PlatypusFiles.DEFAULT_ENCODING);
                            if (fo.existsExt(PlatypusFiles.MODEL_EXTENSION) && nameExt.endsWith("." + PlatypusFiles.JAVASCRIPT_EXTENSION)) {
                                appElementName = PlatypusFilesSupport.extractModuleConstructorName(fileContent);
                            } else {
                                appElementName = PlatypusFilesSupport.getAnnotationValue(fileContent, JsDoc.Tag.NAME_TAG);
                            }
                            isPublic = PlatypusFilesSupport.getAnnotationValue(fileContent, JsDoc.Tag.PUBLIC_TAG) != null;
                        }
                        if (appElementName != null) {
                            d.addPair(APP_ELEMENT_NAME, appElementName, true, true);
                        }
                        d.addPair(PUBLIC_FIELD_NAME, Boolean.toString(isPublic), true, true);
                        d.addPair(FIELD_NAME, nameExt, true, true);
                        d.addPair(FULL_PATH, fo.getPath(), true, true);
                        is.addDocument(d);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileIndexer.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    private static String getNameExt(Indexable i) {
        String path = i.getRelativePath();
        int lastSlash = path.lastIndexOf('/'); //NOI18N
        if (lastSlash != -1) {
            return path.substring(lastSlash + 1);
        } else {
            return i.getRelativePath();
        }
    }

    private static boolean isIndexableFile(String nameExt) {
        //For now indexing only js, sql and connection files
        return nameExt.endsWith(PlatypusFiles.JAVASCRIPT_EXTENSION)
                || nameExt.endsWith(PlatypusFiles.SQL_EXTENSION)
                || nameExt.endsWith(PlatypusFiles.CONNECTION_EXTENSION);
    }
}
