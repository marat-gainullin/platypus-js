package com.eas.designer.explorer;

import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.project.ui.PlatypusProjectNodesList;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * A file chooser allowing to choose a file or folder from the netbeans filesystem.
 * Can be used as a standalone panel, or as a dialog.
 * 
 * @author Tomas Pavek
 */
public class FileChooser extends JPanel implements ExplorerManager.Provider {

    private boolean choosingFolder;
    private ExplorerManager explorerManager;
    private String selectedAppElementName;
    private FileObject selectedFile;
    private FileObject selectedFolder;
    private boolean confirmed;
    private BeanTreeView treeView;
    private JButton newButton;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField fileNameTextField;
    private Filter filter;
    public static final String PROP_SELECTED_FILE = "selectedFile"; // NOI18N

    public interface Filter {

        boolean accept(FileObject file);
    }

// [TODO: multiselection, separate type of classpath (all vs. project's sources only)
//  - not needed for now]
    /**
     * Creates a new FileChooser. Can be used directly as a panel,
     * or getDialog can be called to get it wrapped in a Dialog.
     * @param fileInProject a source file from project sources (determines the
     *        project's classpath)
     * @param aFilter a filter for files to be displayed
     * @param aChoosingFolder if true, the chooser only allows to select a folder,
     *        and only source classpath is shown (i.e. not JARs on execution CP)
     * @param okCancelButtons defines whether the controls buttons should be shown
     *        (typically true if using as a dialog and false if using as a panel)
     */
    public FileChooser(FileObject aRootFile, Filter aFilter, boolean aChoosingFolder, boolean okCancelButtons) {
        super();
        choosingFolder = aChoosingFolder;
        filter = aFilter;

        Listener listener = new Listener();

        DataFolder rootFolder = DataFolder.findFolder(aRootFile);
        explorerManager = new ExplorerManager();
        explorerManager.setRootContext(new FilterNode(rootFolder.getNodeDelegate(), rootFolder.createNodeChildren(PlatypusProjectNodesList.APPLICATION_TYPES_FILTER)));
        try {
            explorerManager.setSelectedNodes(new Node[]{ rootFolder.getNodeDelegate() });
        } catch (PropertyVetoException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        explorerManager.addPropertyChangeListener(listener);

        if (aChoosingFolder) { // add a button allowing to create a new folder
            newButton = new JButton();
            Mnemonics.setLocalizedText(newButton, NbBundle.getMessage(FileChooser.class, "CTL_CreateNewButton")); // NOI18N
            newButton.addActionListener(listener);
            newButton.setEnabled(false);
            newButton.setToolTipText(NbBundle.getMessage(FileChooser.class, "CTL_CreateNewButtonHint")); // NOI18N
        }
        if (okCancelButtons) {
            okButton = new JButton(NbBundle.getMessage(FileChooser.class, "CTL_OKButton")); // NOI18N
            okButton.addActionListener(listener);
            okButton.setEnabled(false);
            cancelButton = new JButton(NbBundle.getMessage(FileChooser.class, "CTL_CancelButton")); // NOI18N
        }

        treeView = new BeanTreeView();
        treeView.setPopupAllowed(false);
        treeView.setDefaultActionAllowed(false);
        treeView.setBorder((Border) UIManager.get("Nb.ScrollPane.border")); // NOI18N
        treeView.getAccessibleContext().setAccessibleName(NbBundle.getMessage(FileChooser.class, "ACSN_FileSelectorTreeView")); // NOI18N
        treeView.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FileChooser.class, "ACSD_FileSelectorTreeView")); // NOI18N
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FileChooser.class, "ACSD_FileSelectorPanel")); // NOI18N

        // label and text field with mnemonic
        JLabel label = new JLabel();
        Mnemonics.setLocalizedText(label, NbBundle.getMessage(FileChooser.class,
                aChoosingFolder ? "LBL_FolderName" : "LBL_FileName")); // NOI18N
        fileNameTextField = new JTextField();
        fileNameTextField.setEditable(false);
        //fileNameTextField.getDocument().addDocumentListener(listener);
        //fileNameTextField.addActionListener(listener);
        label.setLabelFor(fileNameTextField);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);

        GroupLayout.SequentialGroup sq = layout.createSequentialGroup().addComponent(label).addComponent(fileNameTextField);
        if (!okCancelButtons && newButton != null) // add newButton next to the text field
        {
            sq.addComponent(newButton);
        }
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(treeView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(sq));

        GroupLayout.ParallelGroup pq = layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label).addComponent(fileNameTextField);
        if (!okCancelButtons && newButton != null) // add newButton next to the text field
        {
            pq.addComponent(newButton);
        }
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(treeView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(pq));
    }

    /**
     * Creates a modal dialog containing the file chooser with given title.
     * Use ActionListener to be informed about pressing OK button. Otherwise
     * call isConfirmed which returns true if OK button was pressed.
     * @param title the title of the dialog
     * @param listener ActionListener attached to the OK button (if not null)
     */
    public Dialog getDialog(String title, ActionListener listener) {
        if (okButton == null) {
            throw new IllegalStateException("Can't create dialog for a chooser without OK and Cancel buttons."); // NOI18N
        }
        ((GroupLayout) getLayout()).setAutoCreateContainerGaps(true);

        DialogDescriptor dd = new DialogDescriptor(
                this, title, true,
                newButton != null
                ? new JButton[]{newButton, okButton, cancelButton}
                : new JButton[]{okButton, cancelButton},
                okButton,
                DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP,
                null);
        dd.setClosingOptions(new JButton[]{okButton, cancelButton});
        if (listener != null) {
            okButton.addActionListener(listener);
        }
        return DialogDisplayer.getDefault().createDialog(dd);
    }

    @Override
    public void addNotify() {
        confirmed = false;
        super.addNotify();
        treeView.requestFocusInWindow();
    }

    /**
     * Returns if the user selected some file and confirmed by OK button.
     * @return true if OK button has been pressed by the user since last call of
     *         getDialog
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the selected application element name.
     * @return application element name
     */
    public String getSelectedAppElementName() {
        return selectedAppElementName;
    }
    
    /**
     * Returns the file selected by the user (or set via setSelectedFile method).
     * @return the FileObject selected in the chooser
     */
    public FileObject getSelectedFile() {
        return selectedFile;
    }

    /**
     * Sets the selected file in the chooser. The tree view is expanded as
     * needed and the corresponding node selected.
     * @param file the FileObject to be selected in the chooser
     */
    public void setSelectedFile(FileObject file) {
        if (file != null) {
            selectFileNode(file);
        }
        selectedFile = file;
    }

    private class Listener implements PropertyChangeListener, ActionListener, DocumentListener {
        // called when Create New or OK button pressed

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
                confirmed = true;
            } else if (e.getSource() == newButton) {
                if (selectedFolder == null || selectedFile != null) {
                    return;
                }

                String fileName = fileNameTextField.getText();
                try { // create a new dir
                    selectedFile = selectedFolder.createFolder(fileName);
                    selectFileNode(selectedFile);
                } catch (Exception ex) { // report failure (name should be OK from checkFileName)
                    ErrorManager.getDefault().notify(ex);
                }
                if (choosingFolder && selectedFile != null) {
                    firePropertyChange(PROP_SELECTED_FILE, null, selectedFile);
                }
            } else if (e.getSource() == fileNameTextField) { // enter pressed in the text field
                if (selectedFile == null) { // nothing set from checkNameField
                    String fileName = fileNameTextField.getText();
                    if (fileName.startsWith("/")) // NOI18N
                    {
                        fileName = fileName.substring(1);
                    }
                }
                if (selectedFile != null) {
                    Node[] nodes = explorerManager.getSelectedNodes();
                    if (nodes.length != 1 || fileFromNode(nodes[0]) != selectedFile) {
                        selectFileNode(selectedFile);
                        treeView.requestFocus();
                    } else if (okButton != null) {
                        okButton.doClick();
                        return;
                    }
                    if (okButton != null) {
                        okButton.setEnabled(selectedFile != null && (!selectedFile.isFolder() || choosingFolder) && filter.accept(selectedFile));
                    }
                    if (newButton != null) {
                        newButton.setEnabled(false);
                    }
                }
            }
        }

        // called from ExplorerManager when node selection changes
        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            if (ev.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                Node[] nodes = explorerManager.getSelectedNodes();
                FileObject oldSelected = selectedFile;
                selectedFile = null;
                selectedFolder = null;
                if (nodes.length == 1) {
                    FileObject fo = fileFromNode(nodes[0]);
                    if (fo != null) {
                        if (!fo.isFolder() && !choosingFolder) {
                           selectedAppElementName = IndexerQuery.file2AppElementId(fo); 
                           fileNameTextField.setText(selectedAppElementName != null ? selectedAppElementName : ""); // NOI18N
                        } else {
                            fileNameTextField.setText(""); // NOI18N
                            selectedAppElementName = null;
                        }            
                        selectedFile = fo;
                        selectedFolder = fo.getParent();
                    }
                }
                if (okButton != null) {
                    okButton.setEnabled(selectedFile != null && (!selectedFile.isFolder() || choosingFolder) && filter.accept(selectedFile));
                }
                if (newButton != null) {
                    newButton.setEnabled(false);
                }

                firePropertyChange(PROP_SELECTED_FILE, oldSelected, selectedFile);
            }
        }

        // called when a the user types in the text field (DocumentListener)
        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        // called when a the user types in the text field (DocumentListener)
        @Override
        public void insertUpdate(DocumentEvent e) {
            checkNameField();
        }

        // called when a the user types in the text field (DocumentListener)
        @Override
        public void removeUpdate(DocumentEvent e) {
            checkNameField();
        }
    }

    private void checkNameField() {
        if (selectedFolder != null) {
            selectedFile = null;
            String fileName = fileNameTextField.getText();
            Node[] nodes = explorerManager.getSelectedNodes();
            if (nodes.length == 1) {
                FileObject fo = fileFromNode(nodes[0]);
                if (fo != null) {
                    if (!fo.isFolder()) {
                        fo = fo.getParent();
                    }
                    selectedFile = fo.getFileObject(fileName);
                    selectedFolder = fo;
                }
            }
            if (okButton != null) {
                okButton.setEnabled(selectedFile != null && (!selectedFile.isFolder() || choosingFolder));
            }
            if (newButton != null) {
                newButton.setEnabled(selectedFile == null && choosingFolder
                        && Utilities.isJavaIdentifier(fileName));
            }
        }
    }

    /**
     * Implementation of ExplorerManager.Provider. Needed for the tree view to work.
     */
    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
    
    public static FileChooser createInstance(FileObject aRoot, FileObject aSelectedFile, final Set<String> allowedMimeTypes) {
        FileChooser chooser = new FileChooser(
                aRoot,
                new FileChooser.Filter() {

                    @Override
                    public boolean accept(FileObject fo) {
                        return allowedMimeTypes.isEmpty() || allowedMimeTypes.contains(fo.getMIMEType());
                    }
                },
                false, true);
        try {
            chooser.setSelectedFile(aSelectedFile);
        } catch (IllegalArgumentException iaex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, iaex);
        }
        return chooser;
    }
    
    public static FileObject selectFile(FileObject aRoot, FileObject aSelectedFile, Set<String> allowedMimeTypes) {
        FileChooser chooser = createInstance(aRoot, aSelectedFile, allowedMimeTypes);    
        chooser.getDialog(NbBundle.getMessage(FileChooser.class, "CTL_OpenDialogName"), null)// NOI18N
                .setVisible(true);
        if (chooser.isConfirmed()) {
            return chooser.getSelectedFile();
        } else {
            return aSelectedFile;
        }
    }
    
    public static String selectAppElement(FileObject aRoot, FileObject aSelectedFile, Set<String> allowedMimeTypes) {
        FileChooser chooser = createInstance(aRoot, aSelectedFile, allowedMimeTypes);    
        chooser.getDialog(NbBundle.getMessage(FileChooser.class, "CTL_OpenDialogName"), null)// NOI18N
                .setVisible(true);
        if (chooser.isConfirmed()) {
            return chooser.getSelectedAppElementName();
        } else {
            return null;
        }
    }
    
    private static FileObject fileFromNode(Node n) {
        DataObject dobj = n.getLookup().lookup(DataObject.class);
        return dobj != null ? dobj.getPrimaryFile() : null;
    }

    private void selectFileNode(FileObject fo) {
        selectNode(explorerManager.getRootContext(), fo);
    }

    private void selectNode(Node parent, FileObject fo) {
        for (Node n : parent.getChildren().getNodes(true)) {
            FileObject nodeFO = fileFromNode(n);
            if (nodeFO == fo) {
                try {
                    explorerManager.setSelectedNodes(new Node[]{n});
                } catch (PropertyVetoException ex) { // should not happen
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                }
                break;
            } else if (FileUtil.isParentOf(nodeFO, fo)) {
                selectNode(n, fo);
                break;
            }
        }
    }

    private static class FilteredNode extends FilterNode {

        FilteredNode(Node original, String displayName, Filter filter) {
            super(original, new FilteredChildren(original, filter));
            if (displayName != null) {
                disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME);
                setDisplayName(displayName);
            }
        }
    }

    /**
     * A mutualy recursive children that ensure propagation of the
     * filter to deeper levels of hiearachy. That is, it creates
     * FilteredNodes filtered by the same filter.
     */
    public static class FilteredChildren extends FilterNode.Children {

        private Filter filter;

        public FilteredChildren(Node original, Filter filter) {
            super(original);
            this.filter = filter;
        }

        @Override
        protected Node copyNode(Node node) {
            return filter != null ? new FilteredNode(node, null, filter)
                    : super.copyNode(node);
        }

        @Override
        protected Node[] createNodes(Node key) {
            if (filter != null) {
                FileObject fo = fileFromNode(key);
                if (fo == null || !filter.accept(fo)) {
                    return new Node[0];
                }
            }
            return super.createNodes(key);
        }
    }
}
