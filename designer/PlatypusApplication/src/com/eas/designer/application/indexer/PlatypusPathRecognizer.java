/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service=PathRecognizer.class, position=100)
public class PlatypusPathRecognizer extends PathRecognizer {
    
    public static final String BOOT_CP = "platypus/classpath/boot"; // NOI18N
    public static final String SOURCE_CP = "platypus/classpath/source"; // NOI18N
    public static final String JAVASRIPT_MIME_TYPE = "text/javascript";  //NOI18N
    public static final String QUERY_MIME_TYPE = "text/x-platypus-sql";  //NOI18N
    public static final String CONNECTION_MIME_TYPE = "text/connection+xml";  //NOI18N
    
    private final Set<String> MIME_TYPES = new HashSet<String>() {{
        add(JAVASRIPT_MIME_TYPE);
        add(QUERY_MIME_TYPE);
        add(CONNECTION_MIME_TYPE);
    }};
    
    private final Set<String> SOURCES_CP_IDS = Collections.singleton(SOURCE_CP);
    private final Set<String> BOOT_CP_IDS = Collections.singleton(BOOT_CP);
    
    @Override
    public Set<String> getSourcePathIds() {
        return SOURCES_CP_IDS;
    }

    @Override
    public Set<String> getBinaryLibraryPathIds() {
        return null;
    }

    @Override
    public Set<String> getLibraryPathIds() {
        return BOOT_CP_IDS;
    }

    @Override
    public Set<String> getMimeTypes() {
        return MIME_TYPES;
    }

}
