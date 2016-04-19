/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vv
 */
public class PlatypusFiles {

    public static final String JAVASCRIPT_EXTENSION = "js"; // NOI18N
    public static final String JAVASCRIPT_FILE_END = "." + JAVASCRIPT_EXTENSION; // NOI18N
    public static final String FORM_EXTENSION = "layout"; // NOI18N
    public static final String MODEL_EXTENSION = "model"; // NOI18N
    public static final String OUT_EXTENSION = "out"; // NOI18N
    public static final String OUT_EMPTY_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<outputFields>\n"
            + "</outputFields>"; // NOI18N
    public static final String SQL_EXTENSION = "sql"; // NOI18N
    public static final String SQL_FILE_END = "." + SQL_EXTENSION; // NOI18N
    public static final String DIALECT_EXTENSION = "dialect"; // NOI18N
    public static final String REPORT_LAYOUT_EXTENSION = "xls"; // NOI18N
    public static final String REPORT_LAYOUT_EXTENSION_X = "xlsx"; // NOI18N
    public static final String DB_SCHEME_EXTENSION = "pd"; // NOI18N
    private static final List<String> PLATYPUS_FILE_EXTENSIONS = new ArrayList<String>() {
        {
            add(PlatypusFiles.JAVASCRIPT_EXTENSION);
            add(PlatypusFiles.FORM_EXTENSION);
            add(PlatypusFiles.MODEL_EXTENSION);
            add(PlatypusFiles.SQL_EXTENSION);
            add(PlatypusFiles.DIALECT_EXTENSION);
            add(PlatypusFiles.OUT_EXTENSION);
            add(PlatypusFiles.REPORT_LAYOUT_EXTENSION);
            add(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X);
            add(PlatypusFiles.DB_SCHEME_EXTENSION);
        }
    };
    public static final String DEFAULT_ENCODING = SettingsConstants.COMMON_ENCODING; // NOI18N

    public static boolean isPlatypusProjectFile(File file) {
        return PLATYPUS_FILE_EXTENSIONS.contains(FileUtils.getFileExtension(file));
    }

    public static boolean isPlatypusProjectFileExt(String ext) {
        return PLATYPUS_FILE_EXTENSIONS.contains(ext);
    }
}
