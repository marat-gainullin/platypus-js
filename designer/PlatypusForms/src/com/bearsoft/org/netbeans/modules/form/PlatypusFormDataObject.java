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
import com.eas.designer.application.module.PlatypusModuleDataObject;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

/**
 * The DataObject for forms.
 *
 * @author mg
 */
public class PlatypusFormDataObject extends PlatypusModuleDataObject {

    /**
     * The entry for the .layout file
     */
    protected FileEntry formEntry;

    public PlatypusFormDataObject(FileObject aJsFile, MultiFileLoader loader) throws Exception {
        super(aJsFile, loader);
        FileObject aFormFile = FileUtil.findBrother(aJsFile, PlatypusFiles.FORM_EXTENSION);
        formEntry = (FileEntry) registerEntry(aFormFile);
    }

    public FileObject getFormFile() {
        return formEntry.getFile();
    }

    @Override
    protected Cookie[] createServices() {
        return new Cookie[]{new PlatypusFormSupport(this)};
    }

    public boolean isReadOnly() {
        FileObject javaFO = getPrimaryFile();
        FileObject formFO = formEntry.getFile();
        return !javaFO.canWrite() || !formFO.canWrite();
    }

    public FileEntry getFormEntry() {
        return formEntry;
    }

    /**
     * Provides node that should represent this data object. When a node for
     * representation in a parent is requested by a call to getNode(parent) it
     * is the exact copy of this node with only parent changed. This
     * implementation creates instance <CODE>DataNode</CODE>.
     * <P>
     * This method is called only once.
     *
     * @return the node representation for this data object
     * @see DataNode
     */
    @Override
    protected Node createNodeDelegate() {
        FormDataNode node = new FormDataNode(this);
        return node;
    }

    // Serialization
    static final long serialVersionUID = -975322113627854168L;
    
    private void readObject(java.io.ObjectInputStream is)
            throws java.io.IOException, ClassNotFoundException {
        is.defaultReadObject();
    }
}
