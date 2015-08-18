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

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.eas.designer.explorer.PlatypusDataObject;
import java.awt.Component;
import java.awt.Image;
import java.beans.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import org.openide.ErrorManager;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertyEditorRegistration;

/**
 * PropertyEditor for Icons. Depends on existing DataObject for images. Images
 * must be represented by some DataObject which returns itself as cookie, and
 * has image file as a primary file. File extensions for images is specified in
 * isImage method.
 *
 * @author Jan Jancura, Jan Stola, Tomas Pavek
 */
@PropertyEditorRegistration(targetType = {javax.swing.Icon.class, java.awt.Image.class, javax.swing.ImageIcon.class})
public class IconEditor extends PropertyEditorSupport implements ExPropertyEditor {

    /**
     * Type constant for icons from URL.
     */
    public static final int TYPE_URL = 1;
    /**
     * Type constant for icons from file.
     */
    public static final int TYPE_FILE = 2;

    public static final String RESOURCES_IMAGES_ANCHOR = "thumbs.cp";
    //
    private static String[] currentFiles;
    private PlatypusDataObject dataObject;

    protected static class ImageNode extends AbstractNode {

        protected FileObject imageFile;
        protected ImageIcon icon;

        public ImageNode(FileObject aImageFile) {
            super(Children.LEAF);
            imageFile = aImageFile;
        }

        @Override
        public Image getIcon(int type) {
            try {
                if (icon == null) {
                    icon = new ImageIcon(imageFile.asBytes());
                }
                return icon.getImage();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public String getName() {
            return imageFile.getNameExt();
        }
    }

    protected class FileChildren extends Children.Keys<String> {

        public FileChildren() throws Exception {
            super();
            setKeys(getCurrentFileNames());
        }

        @Override
        protected Node[] createNodes(String key) {
            try {
                return new Node[]{new ImageNode(getProjectSrcFolder().getFileObject(key))};
            } catch (Exception ex) {
                return new Node[]{};
            }
        }
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
        Object bean = aEnv.getBeans()[0];
        if (bean instanceof Node) {
            Node node = (Node) bean;
            FormCookie formCookie = node.getLookup().lookup(FormCookie.class);
            if (formCookie != null && aEnv.getFeatureDescriptor() instanceof FormProperty<?>) {
                dataObject = formCookie.getFormModel().getDataObject();
            }
        }
    }

    @Override
    public void setValue(Object value) {
        if (!sameValue(value, getValue())) {
            super.setValue(value);
        }
    }

    private static boolean sameValue(Object val1, Object val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        if (val1 instanceof NbImageIcon && val2 instanceof NbImageIcon) {
            return sameIcon((NbImageIcon) val1, (NbImageIcon) val2);
        }
        return false;
    }

    private static boolean sameIcon(NbImageIcon nbIcon1, NbImageIcon nbIcon2) {
        if ((nbIcon1.getName() == null) ? (nbIcon2.getName() != null) : !nbIcon1.getName().equals(nbIcon2.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public String getAsText() {
        Object val = getValue();
        if (val instanceof NbImageIcon) {
            NbImageIcon nbIcon = (NbImageIcon) val;
            return nbIcon.getName();
        }
        return ""; // NOI18N
    }

    @Override
    public void setAsText(String aValue) throws IllegalArgumentException {
        try {
            setValue(createIconFromText(aValue));
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public String getJavaInitializationString() {
        if (getValue() instanceof NbImageIcon) {
            NbImageIcon ii = (NbImageIcon) getValue();
            return ii.getName();
        }
        return "null"; // NOI18N
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        try {
            return new CustomIconEditor(this);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    public FileObject getProjectSrcFolder() throws Exception {
        return dataObject.getProject().getSrcRoot();
    }
    /*    
     private Node getCurrentRootNode() throws Exception {
     if (rootNode == null) {
     rootNode = new AbstractNode(new FileChildren());
     }
     return rootNode;
     }
     */

    /**
     * @return names of files (without path) available in current folder
     */
    public String[] getCurrentFileNames() throws Exception {
        if (currentFiles == null) {
            FileObject folder = getProjectSrcFolder();
            assert folder != null;
            List<String> list = new ArrayList<>();
            for (FileObject fo : folder.getChildren()) {
                if (isImageFile(fo)) {
                    list.add(fo.getNameExt());
                }
            }
            currentFiles = new String[list.size()];
            list.toArray(currentFiles);
            Arrays.sort(currentFiles);
        }
        return currentFiles;
    }

    static boolean isImageFile(FileObject fo) {
        return fo.isFolder() ? false : isImageFileName(fo.getNameExt());
    }

    static boolean isImageFileName(String name) {
        name = name.toLowerCase();
        return name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") // NOI18N
                || name.endsWith(".jpeg") || name.endsWith(".jpe"); // NOI18N
    }

    private NbImageIcon createIconFromText(String aIconName) throws Exception {
        if (aIconName == null || "".equals(aIconName.trim())) {
            return null;
        }
        NbImageIcon nbIcon = iconFromResourceName(dataObject, aIconName);
        if (nbIcon != null) {
            return nbIcon;
        }
        return null;
    }
    private static final Pattern pattern = Pattern.compile("https?://.*");

    public static NbImageIcon iconFromResourceName(PlatypusDataObject dataObject, String resName) throws Exception {
        if (resName != null && !resName.isEmpty()) {
            Matcher htppMatcher = pattern.matcher(resName);
            if (htppMatcher.matches()) {
                return new NbImageIcon(dataObject, new URL(resName), TYPE_URL, resName);
            } else {
                FileObject fo = dataObject.getProject().getSrcRoot().getFileObject(resName);
                if (fo != null) {
                    return new NbImageIcon(dataObject, fo.toURL(), TYPE_FILE, resName);
                }else{
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static class NbImageIcon extends ImageIcon {

        private final int type;

        /**
         * Name of the icon in icon library.
         */
        private final String name;
        protected PlatypusDataObject dataObject;

        public NbImageIcon(PlatypusDataObject aDataObject, URL aURL, int aType, String aName) {
            super(aURL);
            dataObject = aDataObject;
            type = aType;
            name = aName;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        @Override
        public Image getImage() {
            return super.getImage();
        }

        public NbImageIcon copy() throws Exception {
            return iconFromResourceName(dataObject, name);
        }
    }
}
