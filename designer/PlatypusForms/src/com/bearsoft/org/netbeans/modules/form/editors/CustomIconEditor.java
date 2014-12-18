/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon;
import com.eas.designer.explorer.FileChooser;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * UI for choosing an icon. Custom editor for icon property editor (IconEditor).
 *
 * @author Tomas Pavek
 */
public class CustomIconEditor extends javax.swing.JPanel {

    private final IconEditor propertyEditor;
    private final FileObject rootFolder;
    private FileObject selectedFolder;
    private FileObject selectedFile;
    private String selectedURL;
    private boolean ignoreSetValue;
    private boolean ignoreNull;
    private boolean ignoreCombo;

    public CustomIconEditor(IconEditor prEd) throws Exception {
        super();
        propertyEditor = prEd;
        rootFolder = propertyEditor.getProjectSrcFolder();
        initComponents();
        setSelectedFolder(rootFolder);
        NbImageIcon val = (NbImageIcon) prEd.getValue();
        setupGui(val);
        scrollPane.setBorder((Border) UIManager.get("Nb.ScrollPane.border")); // NOI18N
        setupBrowseButton(btnBrowseImage);
        cbFile.setPrototypeDisplayValue(NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor_FileCombo_Select")); // NOI18N
        cbFile.setRenderer(new IconComboRenderer());
    }

    private static void setupBrowseButton(JButton button) {
        Insets margin = button.getMargin();
        if (margin.left > 4) {
            margin.left = 4;
            margin.right = 4;
            button.setMargin(margin);
        }
    }

    /**
     * Receives the property value (icon) from the property editor. Sets up the
     * GUI accordingly.
     */
    private void setupGui(NbImageIcon nbIcon) {
        if (ignoreSetValue || (nbIcon == null && ignoreNull)) {
            return;
        }

        selectedFile = null;
        selectedURL = null;
        ignoreCombo = true;
        if (cbFile.getItemCount() > 0) {
            cbFile.setSelectedIndex(0);
        }
        ignoreCombo = false;

        if (nbIcon == null) {
            rbProjectImages.setSelected(true);
            previewLabel.setIcon(null);
            return;
        }

        switch (nbIcon.getType()) {
            case IconEditor.TYPE_FILE:
                FileObject iconFile = rootFolder.getFileObject(nbIcon.getName());
                setSelectedFolder(iconFile.getParent());
                setSelectedFile(iconFile);
                rbProjectImages.setSelected(true);
                enableUrlChoose(false);
                break;
            case IconEditor.TYPE_URL:
                setSelectedUrl(nbIcon.getName());
                rbExternalImages.setSelected(true);
                enableFileChoose(false);
                break;
        }
        previewLabel.setIcon(nbIcon);
    }

    private void enableFileChoose(boolean enable) {
        cbFile.setEnabled(enable);
        btnBrowseImage.setEnabled(enable);
    }
    
    private void enableUrlChoose(boolean enable) {
        txtUrl.setEnabled(enable);
    }
    
    private void setSelectedFolder(FileObject folder) {
        if (folder != selectedFolder) {
            selectedFolder = folder;
            ignoreCombo = true;
            ignoreCombo = false;
            cbFile.setModel(createFileComboModel(folder));
        }
    }

    private void setSelectedFile(FileObject fo) {
        selectedFile = null;
        for (int i = 1, n = cbFile.getModel().getSize(); i < n; i++) {
            IconFileItem item = (IconFileItem) cbFile.getModel().getElementAt(i);
            if (item.file.equals(fo)) {
                selectedFile = fo;
                ignoreCombo = true;
                cbFile.setSelectedIndex(i);
                ignoreCombo = false;
                break;
            }
        }
    }

    private void setSelectedUrl(String url) {
        selectedURL = url;
        txtUrl.setText(url);
    }

    /**
     * Constructs the value (NbImageIcon) according to the current state of the
     * GUI and sets it to the property editor.
     */
    private void updateValue() {
        int type = -1;
        String name = null;
        Icon icon = null;
        if (rbProjectImages.isSelected()) {
            if (selectedFile != null) {
                name = FileUtil.getRelativePath(rootFolder, selectedFile);
                try {
                    try {
                        Image image = ImageIO.read(new File(selectedFile.getPath()));
                        if (image != null) {
                            icon = new ImageIcon(image);
                            type = IconEditor.TYPE_FILE;
                        } // no NbImageIcon will be created for invalid file
                    } catch (IllegalArgumentException iaex) { // Issue 178906
                        Logger.getLogger(CustomIconEditor.class.getName()).log(Level.INFO, null, iaex);
                        icon = new ImageIcon(name);
                        type = IconEditor.TYPE_FILE;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CustomIconEditor.class.getName()).log(Level.WARNING, null, ex);
                }
            }
        } else if (rbExternalImages.isSelected()) {
            if (selectedURL != null && !"".equals(selectedURL)) { // NOI18N
                type = IconEditor.TYPE_URL;
                name = selectedURL;
                try {
                    try {
                        Image image = ImageIO.read(new URL(selectedURL));
                        if (image != null) {
                            icon = new ImageIcon(image);
                            type = IconEditor.TYPE_URL;
                        }
                    } catch (IllegalArgumentException iaex) { // Issue 178906
                        Logger.getLogger(CustomIconEditor.class.getName()).log(Level.INFO, null, iaex);
                        icon = new ImageIcon(new URL(selectedURL));
                    }
                    // for URL-based icon create NbImageIcon even if no icon can be loaded from the URL
                } catch (IOException ex) {
                    Logger.getLogger(CustomIconEditor.class.getName()).log(Level.WARNING, null, ex);
                }
            }
        }

        ignoreSetValue = true;
        try {
            propertyEditor.setAsText(name);
        } finally {
            ignoreSetValue = false;
        }

        previewLabel.setIcon(icon);

    }

    private static ComboBoxModel createFileComboModel(FileObject folder) {
        TreeSet<IconFileItem> data = new TreeSet<>();
        int maxIconW = 0;
        int maxIconH = 0;
        for (FileObject fo : folder.getChildren()) {
            if (IconEditor.isImageFile(fo)) {
                IconFileItem ifi = new IconFileItem(fo);
                data.add(ifi);

                Dimension iconSize = ifi.getScaledSize();
                if (iconSize.width > maxIconW) {
                    maxIconW = iconSize.width;
                }
                if (iconSize.height > maxIconH) {
                    maxIconH = iconSize.height;
                }
            }
        }
        for (IconFileItem ifi : data) {
            ifi.setEffectiveSize(maxIconW, maxIconH);
        }

        Vector<Object> v = new Vector<Object>(data.size() + 1);
        v.add(NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor_FileCombo_Select")); // NOI18N
        v.addAll(data);
        return new DefaultComboBoxModel(v);
    }

    private static class IconFileItem implements Comparable, Icon {

        private FileObject file;
        private ImageIcon icon;
        private boolean scaled;
        private int maxW;
        private int maxH;
        private static final int MAX_W = 32;
        private static final int MAX_H = 32;
        private static final long SIZE_LIMIT = 50000;

        IconFileItem(FileObject file) {
            this.file = file;
            try {
                try {
                    Image image = (file.getSize() < SIZE_LIMIT) ? ImageIO.read(file.getURL()) : null;
                    icon = (image != null) ? new ImageIcon(image) : null;
                } catch (IllegalArgumentException iaex) { // Issue 178906
                    Logger.getLogger(CustomIconEditor.class.getName()).log(Level.INFO, null, iaex);
                    icon = new ImageIcon(file.getURL());
                }
            } catch (IOException ex) {
                Logger.getLogger(CustomIconEditor.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        @Override
        public String toString() {
            return file.getNameExt();
        }

        @Override
        public int compareTo(Object obj) {
            return toString().compareTo(obj.toString());
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (icon == null) {
                return;
            }
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            if (!scaled) {
                if (w > maxW || h > maxH) {
                    Dimension size = getScaledSize();
                    w = size.width;
                    h = size.height;
                    icon.setImage(icon.getImage().getScaledInstance(w, h, Image.SCALE_FAST));
                }
                scaled = true;
            }
            icon.paintIcon(c, g, x + ((maxW - w) / 2), y + ((maxH - h) / 2));
        }

        @Override
        public int getIconWidth() {
            return maxW;
        }

        @Override
        public int getIconHeight() {
            return maxH;
        }

        Dimension getScaledSize() {
            if (icon == null) {
                return new Dimension(0, 0);
            }
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            if (w > MAX_W || h > MAX_H) {
                float ratio = ((float) w) / ((float) h);
                if (w > h) {
                    w = MAX_W;
                    h = Math.max(1, Math.round(((float) MAX_W) / ratio));
                } else {
                    h = MAX_H;
                    w = Math.max(1, Math.round(((float) MAX_H) * ratio));
                }
            }
            return new Dimension(w, h);
        }

        void setEffectiveSize(int w, int h) {
            maxW = w > 0 ? w : MAX_W;
            maxH = h > 0 ? h : MAX_H;
        }
    }

    private class IconComboRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list,
                    value != null ? value.toString() : value,
                    index, isSelected, cellHasFocus);
            setIcon(value instanceof IconFileItem ? (IconFileItem) value : null);
            return this;
        }

        @Override
        public void paintComponent(Graphics g) {
            Icon icon = getIcon();
            if (icon != null && !cbFile.isPopupVisible()) {
                // try not to paint the icon in the combo box itself (only in popup list)
                setIcon(null);
            } else {
                icon = null;
            }
            super.paintComponent(g);
            if (icon != null) {
                setIcon(icon);
            }
        }
    }

    private void selectClassPathFile() throws Exception {
        FileObject selectedFile = null;// TODO Rework app element selector
        Set<String> allowedTypes = new HashSet<>();
        allowedTypes.add("image/gif");//NOI18N
        allowedTypes.add("image/jpeg");//NOI18N
        allowedTypes.add("image/png");//NOI18N    
        FileObject newSelectedFile = FileChooser.selectFile(propertyEditor.getProjectSrcFolder(), selectedFile, allowedTypes, allowedTypes);

        if (newSelectedFile != null) {
            FileObject fo = newSelectedFile;
            setSelectedFolder(fo.getParent());
            setSelectedFile(fo);
            rbProjectImages.setSelected(true);
            updateValue();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        scrollPane = new javax.swing.JScrollPane();
        previewLabel = new javax.swing.JLabel();
        rbProjectImages = new javax.swing.JRadioButton();
        rbExternalImages = new javax.swing.JRadioButton();
        lblFile = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        cbFile = new javax.swing.JComboBox();
        rbNoImage = new javax.swing.JRadioButton();
        txtUrl = new javax.swing.JTextField();
        btnBrowseImage = new javax.swing.JButton();

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scrollPane.setViewportView(previewLabel);

        buttonGroup1.add(rbProjectImages);
        rbProjectImages.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbProjectImages.text")); // NOI18N
        rbProjectImages.setToolTipText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbProjectImages.toolTipText")); // NOI18N
        rbProjectImages.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbProjectImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbProjectImagesActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbExternalImages);
        rbExternalImages.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbExternalImages.text")); // NOI18N
        rbExternalImages.setToolTipText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbExternalImages.toolTipText")); // NOI18N
        rbExternalImages.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbExternalImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbExternalImagesActionPerformed(evt);
            }
        });

        lblFile.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.lblFile.text")); // NOI18N

        lblUrl.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.lblUrl.text")); // NOI18N

        cbFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFileActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbNoImage);
        rbNoImage.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbNoImage.text")); // NOI18N
        rbNoImage.setToolTipText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.rbNoImage.toolTipText")); // NOI18N
        rbNoImage.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbNoImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbNoImageActionPerformed(evt);
            }
        });

        txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });

        btnBrowseImage.setText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.btnBrowseImage.text")); // NOI18N
        btnBrowseImage.setToolTipText(org.openide.util.NbBundle.getMessage(CustomIconEditor.class, "CustomIconEditor.btnBrowseImage.toolTipText")); // NOI18N
        btnBrowseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(rbProjectImages, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbExternalImages, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFile)
                            .addComponent(lblUrl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                .addGap(46, 46, 46))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbFile, 0, 357, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowseImage))))
                    .addComponent(rbNoImage, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbProjectImages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFile)
                    .addComponent(cbFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowseImage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbExternalImages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUrl)
                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbNoImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUrlActionPerformed
        String text = txtUrl.getText();
//        if (selectedPackage != null) { // use current package to resolve short file names
//            propertyEditor.setCurrentFolder(selectedPackage);
//        }
        ignoreNull = true; // do not set no icon from text field
        try {
            propertyEditor.setAsText(text);
        } finally {
            ignoreNull = false;
        }
        if (propertyEditor.getValue() instanceof NbImageIcon) {
            setupGui((NbImageIcon) propertyEditor.getValue());
        } else if (!"".equals(text.trim())) { // not a valid text
            txtUrl.setText(text);
            txtUrl.setSelectionStart(0);
            txtUrl.setSelectionEnd(text.length());
            Toolkit.getDefaultToolkit().beep();
        }
        rbExternalImages.setSelected(true);
    }//GEN-LAST:event_txtUrlActionPerformed

    private void rbProjectImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbProjectImagesActionPerformed
        enableFileChoose(true);
        enableUrlChoose(false);
        updateValue();
    }//GEN-LAST:event_rbProjectImagesActionPerformed

    private void rbExternalImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbExternalImagesActionPerformed
        enableFileChoose(false);
        enableUrlChoose(true);
        updateValue();
    }//GEN-LAST:event_rbExternalImagesActionPerformed

    private void rbNoImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNoImageActionPerformed
        enableFileChoose(false);
        enableUrlChoose(false);
        updateValue();
    }//GEN-LAST:event_rbNoImageActionPerformed

    private void cbFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFileActionPerformed
        if (!ignoreCombo) { // only if triggered directly by user
            Object item = cbFile.getSelectedItem();
            if (item instanceof IconFileItem) {
                selectedFile = ((IconFileItem) item).file;
                rbProjectImages.setSelected(true);
            } else {
                selectedFile = null;
            }
            updateValue();
        }
    }//GEN-LAST:event_cbFileActionPerformed

    private void btnBrowseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseImageActionPerformed
        try {
            selectClassPathFile();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnBrowseImageActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseImage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbFile;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JRadioButton rbExternalImages;
    private javax.swing.JRadioButton rbNoImage;
    private javax.swing.JRadioButton rbProjectImages;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}
