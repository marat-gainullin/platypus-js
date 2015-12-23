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
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * A file chooser allowing to choose a file or folder from the netbeans
 * filesystem. Can be used as a standalone panel, or as a dialog.
 *
 * @author Tomas Pavek
 */
public class FileChooser extends JPanel implements ExplorerManager.Provider {

    public static final Filter SELECT_FOLDERS_FILTER = new FolderFilter();
    private boolean applicationElementMode;
    private DataFolder rootFolder;
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
    private Filter selectFilter;
    public static final String PROP_SELECTED_FILE = "selectedFile"; // NOI18N

    public interface Filter {

        boolean accept(FileObject file);
    }

    public static class FolderFilter implements Filter {

        @Override
        public boolean accept(FileObject file) {
            return file.isFolder();
        }
    }

    /**
     * Creates a new FileChooser. Can be used directly as a panel, or getDialog
     * can be called to get it wrapped in a Dialog.
     *
     * @param aRootFile
     * @param aDisplayFilter
     * @param anSelectFilter
     * @param okCancelButtons defines whether the controls buttons should be
     * shown (typically true if using as a dialog and false if using as a panel)
     */
    public FileChooser(FileObject aRootFile, final Filter aDisplayFilter, Filter anSelectFilter, boolean okCancelButtons) {
        super();
        selectFilter = anSelectFilter;

        rootFolder = DataFolder.findFolder(aRootFile);
        explorerManager = new ExplorerManager();
        explorerManager.setRootContext(new FilterNode(rootFolder.getNodeDelegate(), rootFolder.createNodeChildren(new DataFilter() {
            @Override
            public boolean acceptDataObject(DataObject obj) {
                return aDisplayFilter.accept(obj.getPrimaryFile());
            }
        })));
        init(okCancelButtons);
    }

    /**
     * Creates a new FileChooser for application elements. Can be used directly
     * as a panel, or getDialog can be called to get it wrapped in a Dialog.
     *
     * @param aRootFile
     * @param anSelectFilter
     * @param okCancelButtons defines whether the controls buttons should be
     * shown (typically true if using as a dialog and false if using as a panel)
     */
    public FileChooser(FileObject aRootFile, Filter anSelectFilter, boolean okCancelButtons) {
        super();
        applicationElementMode = true;
        selectFilter = anSelectFilter;

        rootFolder = DataFolder.findFolder(aRootFile);
        explorerManager = new ExplorerManager();
        explorerManager.setRootContext(new FilterNode(rootFolder.getNodeDelegate(), rootFolder.createNodeChildren(PlatypusProjectNodesList.APPLICATION_TYPES_FILTER)));
        init(okCancelButtons);
    }

    private boolean isChooseFolders() {
        return selectFilter == SELECT_FOLDERS_FILTER;
    }

    private void init(boolean okCancelButtons) {
        Listener listener = new Listener();
        try {
            explorerManager.setSelectedNodes(new Node[]{rootFolder.getNodeDelegate()});
        } catch (PropertyVetoException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        explorerManager.addPropertyChangeListener(listener);

        if (isChooseFolders()) { // add a button allowing to create a new folder
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
                isChooseFolders() ? "LBL_FolderName" : applicationElementMode ? "LBL_AppName" : "LBL_FileName")); // NOI18N
        fileNameTextField = new JTextField();
        fileNameTextField.setEditable(false);
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
     * Creates a modal dialog containing the file chooser with given title. Use
     * ActionListener to be informed about pressing OK button. Otherwise call
     * isConfirmed which returns true if OK button was pressed.
     *
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
     *
     * @return true if OK button has been pressed by the user since last call of
     * getDialog
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the selected application element name.
     *
     * @return application element name
     */
    public String getSelectedAppElementName() {
        return selectedAppElementName;
    }

    /**
     * Returns the file selected by the user (or set via setSelectedFile
     * method).
     *
     * @return the FileObject selected in the chooser
     */
    public FileObject getSelectedFile() {
        return selectedFile;
    }

    /**
     * Sets the selected file in the chooser. The tree view is expanded as
     * needed and the corresponding node selected.
     *
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
                if (isChooseFolders() && selectedFile != null) {
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
                        okButton.setEnabled(selectedFile != null && selectFilter.accept(selectedFile));
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
                        if (applicationElementMode && !fo.isFolder()) {
                            selectedAppElementName = IndexerQuery.file2AppElementId(fo);
                            fileNameTextField.setText(selectedAppElementName != null ? selectedAppElementName : ""); // NOI18N
                        } else if (!applicationElementMode && !fo.isFolder()) {
                            fileNameTextField.setText(fo.getNameExt());
                        } else {
                            fileNameTextField.setText(""); // NOI18N
                            selectedAppElementName = null;
                        }
                        selectedFile = fo;
                        selectedFolder = fo.getParent();
                    }
                }
                if (okButton != null) {
                    okButton.setEnabled(selectedFile != null && (!selectedFile.isFolder() || isChooseFolders()) && selectFilter.accept(selectedFile));
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
                okButton.setEnabled(selectedFile != null && (!selectedFile.isFolder() || isChooseFolders()));
            }
            if (newButton != null) {
                newButton.setEnabled(selectedFile == null && isChooseFolders()
                        && Utilities.isJavaIdentifier(fileName));
            }
        }
    }

    /**
     * Implementation of ExplorerManager.Provider. Needed for the tree view to
     * work.
     */
    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public static FileChooser createInstance(FileObject aRoot, FileObject aSelectedFile, Filter aDisplayFilter, Filter aSelectFilter) {
        FileChooser chooser = new FileChooser(aRoot, aDisplayFilter, aSelectFilter, true);
        try {
            chooser.setSelectedFile(aSelectedFile);
        } catch (IllegalArgumentException iaex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, iaex);
        }
        return chooser;
    }

    public static FileChooser createInstance(FileObject aRoot, FileObject aSelectedFile, final Set<String> allowedMimeTypes) {
        FileChooser chooser = new FileChooser(aRoot, getMimeTypeFilter(allowedMimeTypes), true);
        try {
            chooser.setSelectedFile(aSelectedFile);
        } catch (IllegalArgumentException iaex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, iaex);
        }
        return chooser;
    }

    private static Filter getMimeTypeFilter(final Set<String> allowedMimeTypes) {
        return new Filter() {
            @Override
            public boolean accept(FileObject fo) {
                return allowedMimeTypes == null || allowedMimeTypes.isEmpty() || allowedMimeTypes.contains(fo.getMIMEType()) || fo.isFolder();
            }
        };
    }

    public static FileObject selectFile(FileObject aRoot, FileObject aSelectedFile, Set<String> aDisplayMimeTypes, Set<String> aSelectMimeTypes) {
        FileChooser chooser = createInstance(aRoot, aSelectedFile, getMimeTypeFilter(aDisplayMimeTypes), getMimeTypeFilter(aSelectMimeTypes));
        chooser.getDialog(chooser.getTitle(), null)
                .setVisible(true);
        if (chooser.isConfirmed()) {
            return chooser.getSelectedFile();
        } else {
            return aSelectedFile;
        }
    }

    public static FileObject selectAppElement(FileObject aRoot, FileObject aSelectedFile, Set<String> allowedMimeTypes) {
        FileChooser chooser = createInstance(aRoot, aSelectedFile, allowedMimeTypes);
        chooser.getDialog(chooser.getTitle(), null)
                .setVisible(true);
        if (chooser.isConfirmed()) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private String getTitle() {
        return NbBundle.getMessage(FileChooser.class,
                applicationElementMode
                ? "CTL_OpenAppDialogName" //NOI18N
                : isChooseFolders()
                ? "CTL_OpenFolderDialogName" : "CTL_OpenFileDialogName"); //NOI18N
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
     * A mutualy recursive children that ensure propagation of the filter to
     * deeper levels of hiearachy. That is, it creates FilteredNodes filtered by
     * the same filter.
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
