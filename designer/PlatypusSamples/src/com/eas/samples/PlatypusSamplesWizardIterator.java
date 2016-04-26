package com.eas.samples;

import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
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
import org.openide.filesystems.FileUtil;
import org.openide.util.EditableProperties;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class PlatypusSamplesWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {

    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    protected transient WizardDescriptor wiz;

    public PlatypusSamplesWizardIterator() {
    }

    public static PlatypusSamplesWizardIterator createIterator() {
        return new PlatypusSamplesWizardIterator();
    }

    protected WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new PlatypusSamplesWizardPanel()
        };
    }

    protected String[] createSteps() {
        return new String[]{
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
        File projectDir = FileUtil.normalizeFile((File) wiz.getProperty(PlatypusSamples.PROJ_DIR));
        projectDir.mkdirs();

        FileObject template = Templates.getTemplate(wiz);
        try (InputStream templateStream = template.getInputStream()) {
            unZipFile(templateStream, projectDir);
        }

        Path projectDirPath = Paths.get(Utilities.toURI(projectDir));
        Path generalPropertiesPath = projectDirPath.resolve(PlatypusProjectSettingsImpl.PROJECT_SETTINGS_FILE);
        EditableProperties generalProperties = new EditableProperties(true);
        try (InputStream ppIn = new FileInputStream(generalPropertiesPath.toFile())) {
            generalProperties.load(ppIn);
        }
        Path privatePropertiesPath = projectDirPath.resolve(PlatypusProjectSettingsImpl.PROJECT_PRIVATE_SETTINGS_FILE);
        EditableProperties privateProperties = new EditableProperties(true);
        if (privatePropertiesPath.toFile().exists()) {
            try (InputStream ppIn = new FileInputStream(privatePropertiesPath.toFile())) {
                privateProperties.load(ppIn);
            }
        }

        processProjectProperties(generalProperties, privateProperties);

        try (OutputStream ppOut = new FileOutputStream(generalPropertiesPath.toFile())) {
            generalProperties.store(ppOut);
        }
        try (OutputStream pppOut = new FileOutputStream(privatePropertiesPath.toFile())) {
            privateProperties.store(pppOut);
        }

        ProjectManager.getDefault().clearNonProjectCache();
        handle.progress(NbBundle.getMessage(PlatypusSamplesWizardIterator.class, "LBL_NewSampleProjectWizardIterator_WizardProgress_PreparingToOpen"), 2); // NOI18N

        // Open top folder as a project
        resultSet.add(FileUtil.toFileObject(projectDir));

        File parent = projectDir.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        handle.finish();
        return resultSet;
    }

    protected void processProjectProperties(EditableProperties aGeneralProperties, EditableProperties aPrivateProperties) {
        String projectName = (String) wiz.getProperty(PlatypusSamples.NAME);
        PlatypusSamplesPanelVisual.J2eePlatformAdapter j2eeServer = (PlatypusSamplesPanelVisual.J2eePlatformAdapter) wiz.getProperty(PlatypusSamples.SERVER_ID);
        aGeneralProperties.setProperty(PlatypusProjectSettingsImpl.PROJECT_DISPLAY_NAME_KEY, projectName);
        aGeneralProperties.setProperty(PlatypusProjectSettingsImpl.SERVER_CONTEXT_KEY, projectName);
        aPrivateProperties.setProperty(PlatypusProjectSettingsImpl.J2EE_SERVER_ID_KEY, j2eeServer.getServerInstanceId());
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
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                // Step name (actually the whole list for reference).
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
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
                new Object[]{index + 1, panels.length});
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
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

    private static void unZipFile(InputStream source, File aDestination) throws IOException {
        FileObject destination = FileUtil.toFileObject(aDestination);
        ZipInputStream str = new ZipInputStream(source);
        ZipEntry entry;
        while ((entry = str.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                FileUtil.createFolder(destination, entry.getName());
            } else {
                FileObject fo = FileUtil.createData(destination, entry.getName());
                FileLock lock = fo.lock();
                try {
                    try (OutputStream out = fo.getOutputStream(lock)) {
                        FileUtil.copy(str, out);
                    }
                } finally {
                    lock.releaseLock();
                }
            }
        }
    }
}
