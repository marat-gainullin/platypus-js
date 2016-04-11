package com.eas.samples;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class PlatypusSamplesWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    protected transient WizardDescriptor wiz;
    
    public PlatypusSamplesWizardIterator() {}
    
    public static PlatypusSamplesWizardIterator createIterator() {
        return new PlatypusSamplesWizardIterator();
    }
    
    protected WizardDescriptor.Panel[] createPanels() {
        boolean specifyPrjName = "web".equals(Templates.getTemplate(wiz).getAttribute("prjType")); // NOI18N
        return new WizardDescriptor.Panel[] {
            new PlatypusSamplesWizardPanel(false, specifyPrjName)
        };
    }
    
    protected String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(PlatypusSamplesWizardIterator.class, "LBL_CreateProjectStep") // NOI18N
        };
    }
    
    @Override
    public Set<FileObject> instantiate() throws IOException {
        assert false : "This method cannot be called if the class implements WizardDescriptor.ProgressInstantiatingIterator."; // NOI18N
        return null;
    }

    @Override
    public Set instantiate(ProgressHandle handle) throws IOException {
        handle.start(2);
        handle.progress(NbBundle.getMessage(PlatypusSamplesWizardIterator.class, "LBL_NewSampleProjectWizardIterator_WizardProgress_CreatingProject"), 1); // NOI18N

        Set resultSet = new LinkedHashSet();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty(PlatypusSamples.PROJ_DIR));
        String name = (String)wiz.getProperty(PlatypusSamples.NAME);
        FileObject template = Templates.getTemplate(wiz);

        FileObject dir = null;
        /*
        if ("web".equals(template.getAttribute("prjType"))) { // NOI18N
            // Use generator from web.examples to create project with specified name
            dir = WebSampleProjectGenerator.createProjectFromTemplate(template, dirF, name);
        }
        else {
            // Unzip prepared project only (no way to change name of the project)
            // FIXME: should be modified to create projects with specified name (project.xml files in sub-projects should be modified too)
            // FIXME: web.examples and j2ee.samples modules may be merged into one module
            createFolder(dirF);
            dir = FileUtil.toFileObject(dirF);
            unZipFile(template.getInputStream(), dir);
            WebSampleProjectGenerator.configureServer(dir);
            for (FileObject child : dir.getChildren()) {
                WebSampleProjectGenerator.configureServer(child);
            }
        }
        */

        ProjectManager.getDefault().clearNonProjectCache();
        handle.progress(NbBundle.getMessage(PlatypusSamplesWizardIterator.class, "LBL_NewSampleProjectWizardIterator_WizardProgress_PreparingToOpen"), 2); // NOI18N

        // Always open top dir as a project:
        resultSet.add(dir);
        // Look for nested projects to open as well:
        Enumeration e = dir.getFolders(true);
        while (e.hasMoreElements()) {
            FileObject subfolder = (FileObject) e.nextElement();
            if (ProjectManager.getDefault().isProject(subfolder)) {
                resultSet.add(subfolder);
            }
        }

        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        handle.finish();
        return resultSet;
    }
    
    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty(PlatypusSamples.SELECTED_INDEX, new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty(PlatypusSamples.CONTENT_DATA, steps);
            }
        }
        
        FileObject template = Templates.getTemplate(wiz);
        
        wiz.putProperty(PlatypusSamples.NAME, template.getName());
    }
    
    @Override
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty(PlatypusSamples.PROJ_DIR, null);
        this.wiz.putProperty(PlatypusSamples.NAME, null);
        this.wiz = null;
        panels = null;
    }
    
    @Override
    public String name() {
        return NbBundle.getMessage(PlatypusSamplesWizardIterator.class, "LBL_Order", 
                new Object[] {index + 1, panels.length});
    }
    
    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    
    @Override
    public boolean hasPrevious() {
        return index > 0;
    }
    
    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    @Override
    public WizardDescriptor.Panel current() {
        // Not sure how but issue 217645 proves that it might happen
        if (panels == null) {
            panels = createPanels();
        }
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {}
    @Override
    public final void removeChangeListener(ChangeListener l) {}
    
    private static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
                    FileLock lock = fo.lock();
                    try {
                        OutputStream out = fo.getOutputStream(lock);
                        try {
                            FileUtil.copy(str, out);
                        } finally {
                            out.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            }
        } finally {
            source.close();
        }
    }
    
    /** TODO: replace with FileUtil.createFolder(File) in trunk. */
    private static FileObject createFolder(File dir) throws IOException {
        Stack stack = new Stack();
        while (!dir.exists()) {
            stack.push(dir.getName());
            dir = dir.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(dir);
        if (dirFO == null) {
            refreshFileSystem(dir);
            dirFO = FileUtil.toFileObject(dir);
        }
        assert dirFO != null;
        while (!stack.isEmpty()) {
            dirFO = dirFO.createFolder((String)stack.pop());
        }
        return dirFO;
    }
    
    private static void refreshFileSystem(final File dir) throws FileStateInvalidException {
        File rootF = dir;
        while (rootF.getParentFile() != null) {
            rootF = rootF.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(rootF);
        assert dirFO != null : "At least disk roots must be mounted! " + rootF; // NOI18N
        dirFO.getFileSystem().refresh(false);
    }

}
