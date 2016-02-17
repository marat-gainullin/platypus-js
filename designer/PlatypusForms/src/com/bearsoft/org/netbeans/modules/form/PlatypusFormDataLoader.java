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
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.cache.PlatypusFiles;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 * Loader for Forms. Recognizes file with extension .layout and .js and with
 * extension class if there is their source and form file.
 *
 * @author Ian Formanek
 */
@DataObject.Registrations(value = {
    @DataObject.Registration(position = 100, displayName = "com.bearsoft.org.netbeans.modules.form.resources.Bundle#Loaders/text/javascript/Factories/com-bearsoft-org-netbeans-modules-form-PlatypusFormDataLoader.instance", mimeType = "text/javascript"),
    @DataObject.Registration(position = 200, displayName = "com.bearsoft.org.netbeans.modules.form.resources.Bundle#Loaders/text/layout+xml/Factories/com-bearsoft-org-netbeans-modules-form-PlatypusFormDataLoader.instance", mimeType = "text/layout+xml"),
    @DataObject.Registration(position = 300, displayName = "com.bearsoft.org.netbeans.modules.form.resources.Bundle#Loaders/text/model+xml/Factories/com-bearsoft-org-netbeans-modules-form-PlatypusFormDataLoader.instance", mimeType = "text/model+xml")})
public class PlatypusFormDataLoader extends MultiFileLoader {

    /**
     * The standard extensions of the recognized files
     */
    public static final String FORM_EXTENSION = PlatypusFiles.FORM_EXTENSION;
    /**
     * The standard extension for Java source files.
     */
    public static final String JS_EXTENSION = PlatypusFiles.JAVASCRIPT_EXTENSION;
    /**
     * The standard extension for model source files.
     */
    public static final String MODEL_EXTENSION = PlatypusFiles.MODEL_EXTENSION;
    static final long serialVersionUID = 7259146057404524013L;

    /**
     * Constructs a new PlatypusFormDataLoader
     */
    public PlatypusFormDataLoader() {
        super(PlatypusFormDataObject.class.getName()); // NOI18N
    }

    @Override
    protected String actionsContext() {
        return "Loaders/text/javascript/Actions/"; // NOI18N
    }

    /**
     * For a given file finds a primary file.
     *
     * @param fo the file to find primary file for
     *
     * @return the primary file for the file or null if the file is not
     * recognized by this loader
     */
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        return findPrimaryFileImpl(fo);
    }

    private static FileObject findPrimaryFileImpl(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            FileObject jsBrother = FileUtil.findBrother(fo, JS_EXTENSION);
            FileObject layoutBrother = FileUtil.findBrother(fo, FORM_EXTENSION);
            FileObject modelBrother = FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION);
            String foMimeType = fo.getMIMEType();
            if ("text/model+xml".equals(foMimeType) && layoutBrother != null && "text/layout+xml".equals(layoutBrother.getMIMEType())) {
                return jsBrother;
            } else if ("text/layout+xml".equals(foMimeType) && modelBrother != null && "text/model+xml".equals(modelBrother.getMIMEType())) {
                return jsBrother;
            } else if ("text/javascript".equals(foMimeType)
                    && modelBrother != null && "text/model+xml".equals(modelBrother.getMIMEType())
                    && layoutBrother != null && "text/layout+xml".equals(layoutBrother.getMIMEType())) {
                return fo;
            }
        }
        return null;
    }
    
    /**
     * Creates the right data object for given primary file. It is guaranteed
     * that the provided file is realy primary file returned from the method
     * findPrimaryFile.
     *
     * @param primaryFile the primary file
     * @return the data object for this file
     * @exception DataObjectExistsException if the primary file already has data
     * object
     */
    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile)
            throws DataObjectExistsException {
        try {
            return new PlatypusFormDataObject(primaryFile, this);
        } catch (Exception ex) {
            if (ex instanceof DataObjectExistsException) {
                throw (DataObjectExistsException) ex;
            } else {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }
    }

    // from JavaDataLoader
    // [?] Probably needed in case PlatypusFormDataObject is deserialized, then the
    // secondary entry is created additionally.
    @Override
    protected MultiDataObject.Entry createSecondaryEntry(MultiDataObject obj,
            FileObject secondaryFile) {
        assert MODEL_EXTENSION.equals(secondaryFile.getExt())
                || FORM_EXTENSION.equals(secondaryFile.getExt());
        return new FileEntry(obj, secondaryFile);
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }
}
