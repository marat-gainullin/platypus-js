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

import com.bearsoft.org.netbeans.modules.form.NamedPropertyEditor;
import java.beans.*;
import org.openide.util.NbBundle;

/**
 * A property editor class handling enumeration values provided for some
 * properties of Swing components.
 *
 * @author Tran Duc Trung, Tomas Pavek
 */
public class EnumEditor extends PropertyEditorSupport implements NamedPropertyEditor {

    public static final String ENUMERATION_VALUES_KEY = "enumerationValues";

    /**
     * Array of object triplets describing the enumeration 0 - displayed label 1 - value 2 - code string.
     */
    private final Object[] enumerationValues;

    public EnumEditor(Object[] aValues) {
        super();
        enumerationValues = aValues;
    }

    protected Object[] getEnumerationValues() {
        return enumerationValues;
    }

    // --------
    @Override
    public String[] getTags() {
        int n = enumerationValues.length / 3;
        String[] tags = new String[n];
        for (int i = 0; i < n; i++) {
            tags[i] = (String) enumerationValues[i * 3];
        }

        return tags;
    }

    @Override
    public void setAsText(String str) {
        int n = enumerationValues.length / 3;
        for (int i = 0; i < n; i++) {
            if (enumerationValues[i * 3].toString().equals(str)) {
                setValue(enumerationValues[i * 3 + 1]);
                break;
            }
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        int n = enumerationValues.length / 3;
        for (int i = 0; i < n; i++) {
            Object eVal = enumerationValues[i * 3 + 1];
            if ((eVal == null && value == null) || (eVal != null && eVal.equals(value))) {
                return enumerationValues[i * 3].toString();
            }
        }
        return enumerationValues.length > 0
                ? enumerationValues[0].toString() : null;
    }

    @Override
    public String getJavaInitializationString() {
        String initString = null;

        Object value = getValue();
        int n = enumerationValues.length / 3;
        for (int i = 0; i < n; i++) {
            Object eVal = enumerationValues[i * 3 + 1];
            if ((eVal == null && value == null) || (eVal != null && eVal.equals(value))) {
                initString = (String) enumerationValues[i * 3 + 2];
                break;
            }
        }

        if (initString == null) {
            initString = enumerationValues.length > 2
                    ? (String) enumerationValues[2] : null;
        }
        if (initString == null) {
            return null;
        }
        return initString;
    }

    // -------
    // NamedPropertyEditor
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(EnumEditor.class, "CTL_EnumEditorName"); // NOI18N
    }
}
