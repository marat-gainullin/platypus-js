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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.awt.Mnemonics;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;

/**
 * Implementation of one panel in Options Dialog.
 *
 * @author Jan Stola, Jan Jancura
 */
public final class FormDesignerOptionsCustomizer extends JPanel implements ActionListener, ChangeListener {

    private JCheckBox cbFold = new JCheckBox();
    private JCheckBox cbAssistant = new JCheckBox();
    private JComboBox<String> cbAutoI18n = new JComboBox<>();
    private PropertyPanel guideLineColEditor = new PropertyPanel();
    private PropertyPanel selectionBorderColEditor = new PropertyPanel();
    private JSpinner spGridSizeX = new JSpinner(new SpinnerNumberModel(10, 2, 100, 1));
    private JSpinner spGridSizeY = new JSpinner(new SpinnerNumberModel(10, 2, 100, 1));
    private boolean changed = false;
    private boolean listen = false;

    public FormDesignerOptionsCustomizer() {
        loc(cbFold, "Fold"); // NOI18N
        loc(cbAssistant, "Assistant"); // NOI18N
        cbAutoI18n.addItem(loc("CTL_AUTO_RESOURCE_DEFAULT")); // NOI18N
        cbAutoI18n.addItem(loc("CTL_AUTO_RESOURCE_ON")); // NOI18N
        cbAutoI18n.addItem(loc("CTL_AUTO_RESOURCE_OFF")); // NOI18N

        JLabel selectionBorderColLabel = new JLabel();
        JLabel guideLineColLabel = new JLabel();
        JLabel autoI18nLabel = new JLabel();
        JLabel gridSizeXLabel = new JLabel();
        JLabel gridSizeYLabel = new JLabel();
        loc(autoI18nLabel, "Auto_I18n"); // NOI18N
        loc(gridSizeXLabel, "Grid_SizeX"); // NOI18N
        loc(gridSizeYLabel, "Grid_SizeY"); // NOI18N
        loc(selectionBorderColLabel, "Selection_Border_Color"); // NOI18N
        loc(guideLineColLabel, "Guiding_Line_Color"); // NOI18N
        autoI18nLabel.setToolTipText(loc("HINT_AUTO_RESOURCE_GLOBAL")); // NOI18N
        guideLineColLabel.setToolTipText(loc("HINT_GUIDING_LINE_COLOR")); // NOI18N
        selectionBorderColLabel.setToolTipText(loc("HINT_SELECTION_BORDER_COLOR")); // NOI18N
        gridSizeXLabel.setToolTipText(loc("HINT_GRID_SIZE_X")); // NOI18N
        gridSizeYLabel.setToolTipText(loc("HINT_GRID_SIZE_Y")); // NOI18N
        cbFold.setToolTipText(loc("HINT_FOLD_GENERATED_CODE")); // NOI18N
        cbAssistant.setToolTipText(loc("HINT_ASSISTANT_SHOWN")); // NOI18N
        autoI18nLabel.setLabelFor(cbAutoI18n);
        guideLineColLabel.setLabelFor(guideLineColEditor);
        selectionBorderColLabel.setLabelFor(selectionBorderColEditor);
        gridSizeXLabel.setLabelFor(spGridSizeX);
        gridSizeYLabel.setLabelFor(spGridSizeY);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(autoI18nLabel)
                .addComponent(guideLineColLabel)
                .addComponent(selectionBorderColLabel)
                .addComponent(gridSizeXLabel)
                .addComponent(gridSizeYLabel)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(cbFold)
                .addComponent(cbAssistant)
                .addComponent(cbAutoI18n, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guideLineColEditor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(selectionBorderColEditor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spGridSizeX, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spGridSizeY, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addContainerGap()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(autoI18nLabel)
                .addComponent(cbAutoI18n))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbFold)
                .addComponent(cbAssistant)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(guideLineColLabel, GroupLayout.Alignment.CENTER)
                .addComponent(guideLineColEditor, GroupLayout.Alignment.CENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(selectionBorderColLabel, GroupLayout.Alignment.CENTER)
                .addComponent(selectionBorderColEditor, GroupLayout.Alignment.CENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(gridSizeXLabel, GroupLayout.Alignment.CENTER)
                .addComponent(spGridSizeX, GroupLayout.Alignment.CENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(gridSizeYLabel, GroupLayout.Alignment.CENTER)
                .addComponent(spGridSizeY, GroupLayout.Alignment.CENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addContainerGap());
        setBorder(new TitledBorder(loc("Code_Generation"))); // NOI18N

        cbFold.addActionListener(this);
        cbAssistant.addActionListener(this);
        cbAutoI18n.addActionListener(this);
        spGridSizeX.addChangeListener(this);
        spGridSizeY.addChangeListener(this);
    }

    private static String loc(String key) {
        return NbBundle.getMessage(FormDesignerOptionsCustomizer.class, key);
    }

    private static void loc(Component c, String key) {
        if (c instanceof AbstractButton) {
            Mnemonics.setLocalizedText((AbstractButton) c, loc(key));
        } else {
            Mnemonics.setLocalizedText((JLabel) c, loc(key));
        }
    }

    // other methods ...........................................................
    void update() {
        listen = false;
        FormLoaderSettings options = FormLoaderSettings.getInstance();
        try {
            selectionBorderColEditor.setProperty(
                    new PropertySupport.Reflection<>(
                    options,
                    java.awt.Color.class,
                    "selectionBorderColor")); // NOI18N
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        try {
            guideLineColEditor.setProperty(
                    new PropertySupport.Reflection<>(
                    options,
                    java.awt.Color.class,
                    "guidingLineColor")); // NOI18N
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        cbFold.setSelected(options.getFoldGeneratedCode());
        cbAssistant.setSelected(options.getAssistantShown());
        cbAutoI18n.setSelectedIndex(options.getI18nAutoMode());
        spGridSizeX.setValue(options.getGridX());
        spGridSizeY.setValue(options.getGridY());
        listen = true;
        changed = false;
    }

    void applyChanges() {
        FormLoaderSettings options = FormLoaderSettings.getInstance();
        options.setFoldGeneratedCode(cbFold.isSelected());
        options.setAssistantShown(cbAssistant.isSelected());
        options.setI18nAutoMode(cbAutoI18n.getSelectedIndex());
        options.setGridX((Integer) spGridSizeX.getValue());
        options.setGridY((Integer) spGridSizeY.getValue());
        changed = false;
    }

    void cancel() {
        changed = false;
    }

    boolean dataValid() {
        return true;
    }

    boolean isChanged() {
        return changed;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listen) {
            changed = true;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (listen) {
            changed = true;
        }
    }
}