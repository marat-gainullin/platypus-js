/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.files.wizard;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.util.StringUtils;
import java.awt.Component;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class NewApplicationElementWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    public static final String PLATYPUS_APP_ELEMENT_NAME_PARAM_NAME = "appElementName";

    public NewApplicationElementWizardIterator() {
        super();
    }

    public static NewApplicationElementWizardIterator createIterator() {
        return new NewApplicationElementWizardIterator();
    }
    protected int index;
    protected WizardDescriptor.Panel<WizardDescriptor>[] panels;
    protected WizardDescriptor wiz;

    @Override
    public Set instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wiz);
        String targetName = Templates.getTargetName(wiz);

        DataFolder df = DataFolder.findFolder(dir);
        FileObject template = Templates.getTemplate(wiz);

        DataObject dTemplate = DataObject.find(template);
        Project project = FileOwnerQuery.getOwner(dir);
        DataObject dobj = dTemplate.createFromTemplate(df, targetName, achieveParameters(project, wiz));
        FileObject createdFile = dobj.getPrimaryFile();

        return Collections.singleton(createdFile);
    }

    /**
     * Generates new application element's name.
     * New name will be generated using the provided initial name, unsupported symbols will be replaced 
     * and it will be ensured that new name is unique.
     * @param project Application's project
     * @param aBaseName Initial name
     * @return New name
     */
    public static String getNewValidAppElementName(Project project, String aBaseName) {
        assert aBaseName != null;
        assert !aBaseName.isEmpty();
        String appElementName = StringUtils.replaceUnsupportedSymbols(aBaseName.trim());
        String s = appElementName;
        int i = 0;
        while (IndexerQuery.appElementId2File(project, s) != null) {
            s = String.format("%s_%d", appElementName, ++i); // NOI18N
        }
        return s;
    }

    @Override
    public void initialize(WizardDescriptor aWiz) {
        wiz = aWiz;
        index = 0;
        panels = createPanels(wiz);
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
                // Step №.
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i));
                // Step name (actually the whole list for reference)
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                // Turn on subtitle creation on each step
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                // Show steps on the left side with the image on the background
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                // Turn on numbering of all steps
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wd) {
        Templates.setTargetName(wiz, null);
        panels = null;
        wiz = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
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
    
    protected Map<String, String> achieveParameters(Project project, WizardDescriptor aWiz) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(
                PLATYPUS_APP_ELEMENT_NAME_PARAM_NAME,
                getNewValidAppElementName(project, Templates.getTargetName(wiz)));
        return parameters;
    }

    protected WizardDescriptor.Panel[] createPanels(WizardDescriptor wiz) {
        return new WizardDescriptor.Panel[]{
                    new NewApplicationElementWizardNamePanel()
                };
    }

    protected String[] createSteps() {
        return new String[]{
                    NbBundle.getMessage(NewApplicationElementWizardIterator.class, "LBL_ChooseFileTypeStep"), NbBundle.getMessage(NewApplicationElementWizardIterator.class, "LBL_CreateApplicationElementStep")
                };
    }
}
