package com.eas.designer.explorer.project;

import java.io.File;
import org.netbeans.spi.search.SearchFilterDefinition;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Platypus project's search filter.
 *
 * @author vv
 */
public final class SearchFilter extends SearchFilterDefinition {

    private static final List<String> ignoredPaths = Arrays.asList(new String[] {"web/pwc", "web/pub", "web/META-INF", "web/WEB-INF/lib"});//NOI18N
    private final PlatypusProject project;
    private final Path projectPath;

    public SearchFilter(PlatypusProject aProject) {
        project = aProject;
        projectPath = FileUtil.toFile(project.getProjectDirectory()).toPath();
    }

    @Override
    public boolean searchFile(FileObject fo)
            throws IllegalArgumentException {
        if (fo.isFolder()) {
            throw new java.lang.IllegalArgumentException(
                    "file (not folder) expected");//NOI18N
        } else {
            return !FileUtil.toFile(fo).isHidden();
        }
    }
    
    @Override
    public FolderResult traverseFolder(FileObject fo)
            throws IllegalArgumentException {
        if (!fo.isFolder()) {
            throw new java.lang.IllegalArgumentException(
                    "folder expected");//NOI18N
        }
        File file = FileUtil.toFile(fo);
        String relPath = projectPath.relativize(file.toPath()).toString().replace(File.pathSeparator, "/");
        return !file.isHidden() && !ignoredPaths.contains(relPath) ? FolderResult.TRAVERSE : FolderResult.DO_NOT_TRAVERSE;
    }
}
