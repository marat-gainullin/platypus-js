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
import java.awt.*;
import java.beans.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import org.openide.awt.Mnemonics;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.PropertyEditorRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Pavel Buzek
 */
@PropertyEditorRegistration(targetType = Cursor.class)
public class CursorEditor extends PropertyEditorSupport implements ExPropertyEditor, NamedPropertyEditor {

    private static final Map<String, Integer> CURSOR_TYPES = new HashMap<>();
    private static final Map<Integer, String> CURSOR_CONSTANTS = new HashMap<>();

    static {
        CURSOR_TYPES.put(new Cursor(Cursor.CROSSHAIR_CURSOR).getName(), Cursor.CROSSHAIR_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.DEFAULT_CURSOR).getName(), Cursor.DEFAULT_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.E_RESIZE_CURSOR).getName(), Cursor.E_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.HAND_CURSOR).getName(), Cursor.HAND_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.MOVE_CURSOR).getName(), Cursor.MOVE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.N_RESIZE_CURSOR).getName(), Cursor.N_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.NE_RESIZE_CURSOR).getName(), Cursor.NE_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.NW_RESIZE_CURSOR).getName(), Cursor.NW_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.S_RESIZE_CURSOR).getName(), Cursor.S_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.SE_RESIZE_CURSOR).getName(), Cursor.SE_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.SW_RESIZE_CURSOR).getName(), Cursor.SW_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.TEXT_CURSOR).getName(), Cursor.TEXT_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.W_RESIZE_CURSOR).getName(), Cursor.W_RESIZE_CURSOR);
        CURSOR_TYPES.put(new Cursor(Cursor.WAIT_CURSOR).getName(), Cursor.WAIT_CURSOR);

        CURSOR_CONSTANTS.put(Cursor.CROSSHAIR_CURSOR, "java.awt.Cursor.CROSSHAIR_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.DEFAULT_CURSOR, "java.awt.Cursor.DEFAULT_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.E_RESIZE_CURSOR, "java.awt.Cursor.E_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.HAND_CURSOR, "java.awt.Cursor.HAND_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.MOVE_CURSOR, "java.awt.Cursor.MOVE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.N_RESIZE_CURSOR, "java.awt.Cursor.N_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.NE_RESIZE_CURSOR, "java.awt.Cursor.NE_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.NW_RESIZE_CURSOR, "java.awt.Cursor.NW_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.S_RESIZE_CURSOR, "java.awt.Cursor.S_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.SE_RESIZE_CURSOR, "java.awt.Cursor.SE_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.SW_RESIZE_CURSOR, "java.awt.Cursor.SW_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.TEXT_CURSOR, "java.awt.Cursor.TEXT_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.W_RESIZE_CURSOR, "java.awt.Cursor.W_RESIZE_CURSOR"); // NOI18N
        CURSOR_CONSTANTS.put(Cursor.WAIT_CURSOR, "java.awt.Cursor.WAIT_CURSOR"); // NOI18N
    }
    private Cursor current;
    private PropertyEnv env;

    /**
     * Creates new CursorEditor
     */
    public CursorEditor() {
        current = new Cursor(Cursor.DEFAULT_CURSOR);
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        env = aEnv;
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
    }

    @Override
    public Object getValue() {
        return current;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Cursor) {
            current = (Cursor) value;
            firePropertyChange();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getAsText() {
        if (current == null) {
            return "null"; // NOI18N
        } else {
            return current.getName();
        }
    }

    @Override
    public void setAsText(String string) {
        Object o = CURSOR_TYPES.get(string);
        if (o != null) {
            int type = (Integer) o;
            setValue(new Cursor(type));
        }
    }

    @Override
    public String[] getTags() {
        return CURSOR_TYPES.keySet().toArray(new String[]{});
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        return new CursorPanel();
    }

    @Override
    public String getJavaInitializationString() {
        if (current == null) {
            return null; // no code to generate
        }
        String cursorName = CURSOR_CONSTANTS.get(new Integer(current.getType()));
        if (cursorName != null) {
            return "new java.awt.Cursor(" + cursorName + ")"; // NOI18N
        }
        return "new java.awt.Cursor(" + current.getType() + ")"; // NOI18N
    }

    class CursorPanel extends JPanel implements PropertyChangeListener {

        private final JList<String> list;

        CursorPanel() {
            setLayout(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gridBagConstraints1;
            list = new JList<>(CURSOR_TYPES.keySet().toArray(new String[]{}));
            list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            if (current != null) {
                list.setSelectedValue(current.getName(), true);
            }
            env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
            env.addPropertyChangeListener(this);

            ResourceBundle bundle = org.openide.util.NbBundle.getBundle(CursorEditor.class);
            JLabel cursorListLabel = new JLabel();
            Mnemonics.setLocalizedText(cursorListLabel, bundle.getString("CTL_SelectCursorName")); // NOI18N
            cursorListLabel.setLabelFor(list);

            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.insets = new java.awt.Insets(8, 8, 8, 8);
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.weighty = 1.0;
            JScrollPane scrollPane = new JScrollPane(list);
            add(scrollPane, gridBagConstraints1);

            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.insets = new java.awt.Insets(8, 8, 0, 8);
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;

            add(cursorListLabel, gridBagConstraints1);

            list.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CTL_SelectCursorName"));
            scrollPane.getVerticalScrollBar().getAccessibleContext().setAccessibleName(bundle.getString("ACSD_CTL_SelectCursorName")); // NOI18N
            scrollPane.getVerticalScrollBar().getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CTL_SelectCursorName")); // NOI18N
            getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CursorCustomEditor"));
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName())
                    && evt.getNewValue() == PropertyEnv.STATE_VALID) {
                Cursor cursor;
                if (list.getSelectedValue() == null) {
                    cursor = null;
                } else {
                    int type = CURSOR_TYPES.get(list.getSelectedValue());
                    cursor = new Cursor(type);
                }
                setValue(cursor);
            }
        }
    }

    // ------------------------------------------
    // NamedPropertyEditor implementation
    /**
     * @return display name of the property editor
     */
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(CursorEditor.class, "CTL_CursorEditor_DisplayName");
    }
}
