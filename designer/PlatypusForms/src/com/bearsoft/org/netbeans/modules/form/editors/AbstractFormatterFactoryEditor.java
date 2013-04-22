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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

import com.bearsoft.org.netbeans.modules.form.FormAwareEditor;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.NamedPropertyEditor;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import org.openide.util.NbBundle;

/**
 * Property editor for JFormattedTextField's format (formatter factory).
 *
 * @author Jan Stola
 */
public class AbstractFormatterFactoryEditor extends PropertyEditorSupport
        implements NamedPropertyEditor, PropertyChangeListener, FormAwareEditor {

    /**
     * Format selector used by the editor.
     */
    private FormatSelector selector;
    /**
     * Property being edited.
     */
    private FormProperty<?> property;

    /**
     * Returns custom property editor (form selector).
     *
     * @return custom property editor.
     */
    @Override
    public Component getCustomEditor() {
        if (selector == null) {
            selector = new FormatSelector();
            selector.addPropertyChangeListener(this);
        }
        Object value = getValue();
        if (value instanceof FormFormatter) {
            FormFormatter formatter = (FormFormatter) value;
            selector.setFormat(formatter.getFormat());
        } else {
            selector.setFormat(new FormatSelector.FormatInfo(FormatSelector.FormatInfo.NUMBER, FormatSelector.FormatInfo.DEFAULT, null));
            propertyChange(null);
        }
        return selector.getSelectorPanel();
    }

    /**
     * Returns Java code that corresponds to the selected formatter factory.
     *
     * @return Java code that corresponds to the selected formatter factory.
     */
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        if (!(value instanceof FormFormatter)) {
            return super.getJavaInitializationString();
        }
        FormFormatter formatter = (FormFormatter) value;
        FormatSelector.FormatInfo formatInfo = formatter.getFormat();
        int type = formatInfo.getType();
        int subtype = formatInfo.getSubtype();
        String format = formatInfo.getFormat();
        if (format != null) {
            format = "\"" + format.replace("\"", "\\\"") + "\""; // NOI18N
        }
        String code = null;
        if (type == FormatSelector.FormatInfo.MASK) {
            code = "new javax.swing.text.MaskFormatter(" + format + ")"; // NOI18N
        } else if (type == FormatSelector.FormatInfo.DATE) {
            switch (subtype) {
                case FormatSelector.FormatInfo.NONE:
                    code = "new java.text.SimpleDateFormat(" + format + ")"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.DEFAULT:
                    code = ""; // NOI18N
                    break;
                case FormatSelector.FormatInfo.SHORT:
                    code = "java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.MEDIUM:
                    code = "java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.LONG:
                    code = "java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.FULL:
                    code = "java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL)"; // NOI18N
                    break;
                default:
                    assert false;
                    break;
            }
            code = "new javax.swing.text.DateFormatter(" + code + ")"; // NOI18N
        } else if (type == FormatSelector.FormatInfo.TIME) {
            switch (subtype) {
                case FormatSelector.FormatInfo.NONE:
                    code = "new java.text.SimpleDateFormat(" + format + ")"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.DEFAULT:
                    code = "java.text.DateFormat.getTimeInstance()"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.SHORT:
                    code = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.MEDIUM:
                    code = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.LONG:
                    code = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.LONG)"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.FULL:
                    code = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.FULL)"; // NOI18N
                    break;
                default:
                    assert false;
                    break;
            }
            code = "new javax.swing.text.DateFormatter(" + code + ")"; // NOI18N
        } else if (type == FormatSelector.FormatInfo.NUMBER) {
            switch (subtype) {
                case FormatSelector.FormatInfo.NONE:
                    code = "new java.text.DecimalFormat(" + format + ")"; // NOI18N
                    break;
                case FormatSelector.FormatInfo.DEFAULT:
                    code = ""; // NOI18N
                    break;
                case FormatSelector.FormatInfo.INTEGER:
                    code = "java.text.NumberFormat.getIntegerInstance()"; // NOI18N
                    break;
                default:
                    assert false;
                    break;
            }
            code = "new javax.swing.text.NumberFormatter(" + code + ")"; // NOI18N
        } else if (type == FormatSelector.FormatInfo.PERCENT) {
            if (subtype == FormatSelector.FormatInfo.DEFAULT) {
                code = "java.text.NumberFormat.getPercentInstance()"; // NOI18N
            } else {
                code = "new java.text.DecimalFormat(" + format + ")"; // NOI18N
            }
            code = "new javax.swing.text.NumberFormatter(" + code + ")"; // NOI18N
        } else if (type == FormatSelector.FormatInfo.CURRENCY) {
            if (subtype == FormatSelector.FormatInfo.DEFAULT) {
                code = "java.text.NumberFormat.getCurrencyInstance()"; // NOI18N
            } else {
                code = "new java.text.DecimalFormat(" + format + ")"; // NOI18N
            }
            code = "new javax.swing.text.NumberFormatter(" + code + ")"; // NOI18N            
        }
        return "new javax.swing.text.DefaultFormatterFactory(" + code + ")"; // NOI18N
    }

    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics g, Rectangle rectangle) {
        String msg = NbBundle.getMessage(AbstractFormatterFactoryEditor.class, "MSG_AbstractFormatterFactory"); // NOI18N
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, rectangle.x, rectangle.y + (rectangle.height - fm.getHeight()) / 2 + fm.getAscent());
    }

    /**
     * Implementation of property change listener that listens on changes in
     * selected format.
     *
     * @param evt property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setValue(new FormFormatter(selector.getFormat()));
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    /**
     * Determines whether custom property editor is supported.
     *
     * @return <code>true</code>.
     */
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * Returns display name of the property editor.
     *
     * @return display name of the property editor.
     */
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "CTL_AbstractFormatterFactoryEditor_DisplayName"); // NOI18N
    }

    @Override
    public void setContext(FormModel formModel, FormProperty<?> aProperty) {
        property = aProperty;
    }

    /**
     * Form wrapper around formatter.
     */
    public static class FormFormatter {

        /**
         * Information about wrapped format.
         */
        private FormatSelector.FormatInfo format;

        /**
         * Creates new
         * <code>FormFormatter</code>.
         *
         * @param aFormat information about wrapped format.
         */
        FormFormatter(FormatSelector.FormatInfo aFormat) {
            format = aFormat;
        }

        public static FormFormatter valueOf(String aFormat, int aType) {            
            return new FormFormatter(new FormatSelector.FormatInfo(aType, FormatSelector.FormatInfo.NONE, aFormat));
        }

        /**
         * Returns information about wrapped format.
         *
         * @return information about wrapped format.
         */
        public FormatSelector.FormatInfo getFormat() {
            return format;
        }

        /**
         * Returns design value corresponding to this formatter.
         *
         * @return design value corresponding to this formatter.
         */
        public AbstractFormatterFactory constructFormatterFactory() {
            JFormattedTextField.AbstractFormatter value = null;
            int type = format.getType();
            if (type == FormatSelector.FormatInfo.MASK) {
                try {
                    value = new MaskFormatter(format.getFormat());
                } catch (ParseException pex) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, pex.getMessage(), pex);
                    value = new MaskFormatter();
                }
            } else if (type == FormatSelector.FormatInfo.DATE) {
                DateFormat dateFormat = null;
                switch (format.getSubtype()) {
                    case FormatSelector.FormatInfo.NONE:
                        dateFormat = new SimpleDateFormat(format.getFormat());
                        break;
                    case FormatSelector.FormatInfo.DEFAULT:
                        dateFormat = DateFormat.getDateInstance();
                        break;
                    case FormatSelector.FormatInfo.SHORT:
                        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                        break;
                    case FormatSelector.FormatInfo.MEDIUM:
                        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                        break;
                    case FormatSelector.FormatInfo.LONG:
                        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
                        break;
                    case FormatSelector.FormatInfo.FULL:
                        dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
                        break;
                    default:
                        assert false;
                        break;
                }
                value = new DateFormatter(dateFormat);
            } else if (type == FormatSelector.FormatInfo.TIME) {
                DateFormat timeFormat = null;
                switch (format.getSubtype()) {
                    case FormatSelector.FormatInfo.NONE:
                        timeFormat = new SimpleDateFormat(format.getFormat());
                        break;
                    case FormatSelector.FormatInfo.DEFAULT:
                        timeFormat = DateFormat.getTimeInstance();
                        break;
                    case FormatSelector.FormatInfo.SHORT:
                        timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
                        break;
                    case FormatSelector.FormatInfo.MEDIUM:
                        timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
                        break;
                    case FormatSelector.FormatInfo.LONG:
                        timeFormat = DateFormat.getTimeInstance(DateFormat.LONG);
                        break;
                    case FormatSelector.FormatInfo.FULL:
                        timeFormat = DateFormat.getTimeInstance(DateFormat.FULL);
                        break;
                    default:
                        assert false;
                        break;
                }
                value = new DateFormatter(timeFormat);
            } else if (type == FormatSelector.FormatInfo.NUMBER) {
                NumberFormat numberFormat = null;
                switch (format.getSubtype()) {
                    case FormatSelector.FormatInfo.NONE:
                        numberFormat = new DecimalFormat(format.getFormat());
                        break;
                    case FormatSelector.FormatInfo.DEFAULT:
                        numberFormat = NumberFormat.getInstance();
                        break;
                    case FormatSelector.FormatInfo.INTEGER:
                        numberFormat = NumberFormat.getIntegerInstance();
                        break;
                    default:
                        assert false;
                        break;
                }
                value = new NumberFormatter(numberFormat);
            } else if (type == FormatSelector.FormatInfo.PERCENT) {
                NumberFormat percentFormat;
                if (format.getSubtype() == FormatSelector.FormatInfo.DEFAULT) {
                    percentFormat = NumberFormat.getPercentInstance();
                } else {
                    percentFormat = new DecimalFormat(format.getFormat());
                }
                value = new NumberFormatter(percentFormat);
            } else if (type == FormatSelector.FormatInfo.CURRENCY) {
                NumberFormat currencyFormat;
                if (format.getSubtype() == FormatSelector.FormatInfo.DEFAULT) {
                    currencyFormat = NumberFormat.getCurrencyInstance();
                } else {
                    currencyFormat = new DecimalFormat(format.getFormat());
                }
                value = new NumberFormatter(currencyFormat);
            } else {
                assert false;
            }
            return new DefaultFormatterFactory(value);
        }
    }
}
