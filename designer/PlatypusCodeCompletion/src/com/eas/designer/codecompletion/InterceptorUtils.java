/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion;

import com.eas.designer.explorer.project.RequireJsSupportIndexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mg
 */
public class InterceptorUtils {

    public static Collection<TypeUsage> getModuleExposedTypes(FileObject aSource, int aOffset, ModelElementFactory aFactory, String aModuleName) throws IOException {
        QuerySupport querySupport = getQuerySupport(aSource);
        if (querySupport != null) {
            Collection<? extends IndexResult> result = querySupport.query(RequireJsSupportIndexer.FIELD_MODULE_NAME, aModuleName, QuerySupport.Kind.PREFIX, RequireJsSupportIndexer.FIELD_MODULE_NAME, RequireJsSupportIndexer.FIELD_EXPOSED_TYPES);
            if (result != null && !result.isEmpty()) {
                Collection<TypeUsage> types = new ArrayList<>();
                for (IndexResult indexResult : result) {
                    String[] values = indexResult.getValues(RequireJsSupportIndexer.FIELD_EXPOSED_TYPES);
                    String mn = indexResult.getValue(RequireJsSupportIndexer.FIELD_MODULE_NAME);
                    if (mn.equals(aModuleName)) {
                        for (String stype : values) {
                            String[] typeParts = stype.split(":");
                            String typeName = typeParts[0];
                            int offset = Integer.parseInt(typeParts[1]);
                            boolean resolved = "1".equals(typeParts[2]);
                            types.add(aFactory.newType(typeName, aOffset > -1 ? aOffset : offset, resolved));
                        }
                    }
                }
                return types;
            }
        }
        return Collections.emptySet();
    }

    public static QuerySupport getQuerySupport(FileObject aSource) throws IOException {
        Project project = FileOwnerQuery.getOwner(aSource);
        if (project != null) {
            final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots(project, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
            QuerySupport querySupport = QuerySupport.forRoots(RequireJsSupportIndexer.Factory.NAME, RequireJsSupportIndexer.Factory.VERSION, roots.toArray(new FileObject[]{}));
            return querySupport;
        } else {
            return null;
        }
    }

}
