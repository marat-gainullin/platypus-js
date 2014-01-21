/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

import com.eas.designer.explorer.project.PlatypusProjectSettingsImpl;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class PlatypusApplicationWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator<WizardDescriptor> {
    
    private int index;
    private WizardDescriptor.Panel<WizardDescriptor>[] panels;
    private WizardDescriptor wiz;
    
    public PlatypusApplicationWizardIterator() {
        super();
    }
    
    @TemplateRegistration(folder = "Project/Platypus", position = 100, content = "/com/eas/designer/explorer/PlatypusApplicationProject", displayName = "com.eas.designer.explorer.Bundle#Templates/Project/Platypus/PlatypusApplicationProject", description = "PlatypusApplicationDescription.html")
    public static PlatypusApplicationWizardIterator createIterator() {
        return new PlatypusApplicationWizardIterator();
    }
    
    private WizardDescriptor.Panel<WizardDescriptor>[] createPanels() {
        return new WizardDescriptor.Panel[]{
                    new NameAndLocationWizardPanel()
                };
    }
    
    private String[] createSteps() {
        return new String[]{
                    NbBundle.getMessage(PlatypusApplicationWizardIterator.class, "LBL_CreateProjectStep"),
                    NbBundle.getMessage(PlatypusApplicationWizardIterator.class, "LBL_SetupProjectStep")
                };
    }
    
    @Override
    public Set<FileObject> instantiate(/*ProgressHandle handle*/) throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<>();
        String projDirName = (String) wiz.getProperty("projDir");
        String projName = (String) wiz.getProperty("projName");
        String projTitle = (String) wiz.getProperty("projTitle");
        File projDir = FileUtil.normalizeFile(new File(projDirName).getAbsoluteFile());
        File projSpecDir = new File(projDir, projName);
        if (projSpecDir.mkdirs()) {
            try {
                PlatypusProjectSettingsImpl settings = new PlatypusProjectSettingsImpl(FileUtil.toFileObject(projSpecDir));
                settings.setDisplayName(projTitle);
                settings.save();
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
            // Always open file as a project:
            resultSet.add(FileUtil.toFileObject(projSpecDir));
            ProjectChooser.setProjectsFolder(projSpecDir);
            return resultSet;
        } else {
            throw new IOException("Can't crate project folder");
        }
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
                // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }
    
    @Override
    public void uninitialize(WizardDescriptor wd) {
        wd.putProperty("projDir", null);
        wd.putProperty("projName", null);
        wd.putProperty("projTitle", null);
        wd = null;
        panels = null;
    }
    
    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{new Integer(index + 1), new Integer(panels.length)});
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
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {
    }
    
    @Override
    public final void removeChangeListener(ChangeListener l) {
    }
}
