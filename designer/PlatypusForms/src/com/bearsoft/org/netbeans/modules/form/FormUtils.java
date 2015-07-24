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

import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.eas.client.forms.Forms;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.Widget;
import com.eas.client.forms.containers.AnchorsPane;
import com.eas.client.forms.containers.BorderPane;
import com.eas.client.forms.containers.BoxPane;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.CardPane;
import com.eas.client.forms.containers.FlowPane;
import com.eas.client.forms.containers.GridPane;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.events.MouseEvent;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.layouts.CardLayout;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Document;
import jdk.nashorn.api.scripting.JSObject;
import org.netbeans.api.editor.DialogBinding;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * A class that contains utility methods for the form editor.
 *
 * @author Ian Formanek
 */
public class FormUtils {

    public static class Panel extends JPanel implements Widget, HasPublished, HasChildren {

        public Panel() {
            super(new MarginLayout());
        }

        public Panel(LayoutManager aLayout) {
            super(aLayout);
        }

        private boolean paintingChildren;

        @Override
        public Component getComponent(int n) {
            if (paintingChildren) {
                int count = super.getComponentCount() - 1;
                return super.getComponent(count - n);
            } else {
                return super.getComponent(n);
            }
        }

        @Override
        protected void paintChildren(Graphics g) {
            paintingChildren = true;
            try {
                super.paintChildren(g);
            } finally {
                paintingChildren = false;
            }
        }

        @Override
        public Widget getParentWidget() {
            return Forms.lookupPublishedParent(this);
        }

        @ScriptFunction(jsDoc = "Js name of the widget")
        @Override
        public String getName() {
            return super.getName();
        }

        @ScriptFunction(jsDoc = GET_NEXT_FOCUSABLE_COMPONENT_JSDOC)
        @Override
        public JComponent getNextFocusableComponent() {
            return (JComponent) super.getNextFocusableComponent();
        }

        @ScriptFunction
        @Override
        public void setNextFocusableComponent(JComponent aValue) {
            super.setNextFocusableComponent(aValue);
        }

        protected String errorMessage;

        @ScriptFunction(jsDoc = ERROR_JSDOC)
        @Override
        public String getError() {
            return errorMessage;
        }

        @ScriptFunction
        @Override
        public void setError(String aValue) {
            errorMessage = aValue;
        }

        @ScriptFunction(jsDoc = BACKGROUND_JSDOC)
        @Override
        public Color getBackground() {
            return super.getBackground();
        }

        @ScriptFunction
        @Override
        public void setBackground(Color aValue) {
            super.setBackground(aValue);
        }

        @ScriptFunction(jsDoc = FOREGROUND_JSDOC)
        @Override
        public Color getForeground() {
            return super.getForeground();
        }

        @ScriptFunction
        @Override
        public void setForeground(Color aValue) {
            super.setForeground(aValue);
        }

        @ScriptFunction(jsDoc = VISIBLE_JSDOC)
        @Override
        public boolean getVisible() {
            return super.isVisible();
        }

        @ScriptFunction
        @Override
        public void setVisible(boolean aValue) {
            super.setVisible(aValue);
        }

        @ScriptFunction(jsDoc = FOCUSABLE_JSDOC)
        @Override
        public boolean getFocusable() {
            return super.isFocusable();
        }

        @ScriptFunction
        @Override
        public void setFocusable(boolean aValue) {
            super.setFocusable(aValue);
        }

        @ScriptFunction(jsDoc = ENABLED_JSDOC)
        @Override
        public boolean getEnabled() {
            return super.isEnabled();
        }

        @ScriptFunction
        @Override
        public void setEnabled(boolean aValue) {
            super.setEnabled(aValue);
        }

        @ScriptFunction(jsDoc = TOOLTIP_TEXT_JSDOC)
        @Override
        public String getToolTipText() {
            return super.getToolTipText();
        }

        @ScriptFunction
        @Override
        public void setToolTipText(String aValue) {
            super.setToolTipText(aValue);
        }

        @ScriptFunction(jsDoc = OPAQUE_TEXT_JSDOC)
        @Override
        public boolean getOpaque() {
            return super.isOpaque();
        }

        @ScriptFunction
        @Override
        public void setOpaque(boolean aValue) {
            super.setOpaque(aValue);
        }

        @ScriptFunction(jsDoc = COMPONENT_POPUP_MENU_JSDOC)
        @Override
        public JPopupMenu getComponentPopupMenu() {
            return super.getComponentPopupMenu();
        }

        @ScriptFunction
        @Override
        public void setComponentPopupMenu(JPopupMenu aMenu) {
            super.setComponentPopupMenu(aMenu);
        }

        @ScriptFunction(jsDoc = FONT_JSDOC)
        @Override
        public Font getFont() {
            return super.getFont();
        }

        @ScriptFunction
        @Override
        public void setFont(Font aFont) {
            super.setFont(aFont);
        }

        @ScriptFunction(jsDoc = CURSOR_JSDOC)
        @Override
        public Cursor getCursor() {
            return super.getCursor();
        }

        @ScriptFunction
        @Override
        public void setCursor(Cursor aCursor) {
            super.setCursor(aCursor);
        }

        @ScriptFunction(jsDoc = LEFT_JSDOC)
        @Override
        public int getLeft() {
            return super.getLocation().x;
        }

        @ScriptFunction
        @Override
        public void setLeft(int aValue) {
            if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
                MarginLayout.ajustLeft(this, aValue);
            }
            super.setLocation(aValue, getTop());
        }

        @ScriptFunction(jsDoc = TOP_JSDOC)
        @Override
        public int getTop() {
            return super.getLocation().y;
        }

        @ScriptFunction
        @Override
        public void setTop(int aValue) {
            if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
                MarginLayout.ajustTop(this, aValue);
            }
            super.setLocation(getLeft(), aValue);
        }

        @ScriptFunction(jsDoc = WIDTH_JSDOC)
        @Override
        public int getWidth() {
            return super.getWidth();
        }

        @ScriptFunction
        @Override
        public void setWidth(int aValue) {
            Widget.setWidth(this, aValue);
        }

        @ScriptFunction(jsDoc = HEIGHT_JSDOC)
        @Override
        public int getHeight() {
            return super.getHeight();
        }

        @ScriptFunction
        @Override
        public void setHeight(int aValue) {
            Widget.setHeight(this, aValue);
        }

        @ScriptFunction(jsDoc = FOCUS_JSDOC)
        @Override
        public void focus() {
            super.requestFocus();
        }

        @Override
        public Object getElement() {
            return null;
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        @Override
        public JSObject getPublished() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setPublished(JSObject jso) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public JComponent child(int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public JComponent[] children() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void remove(JComponent jc) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static final Logger LOGGER = Logger.getLogger("com.bearsoft.org.netbeans.modules.form"); // NOI18N
    private static final RequestProcessor RP = new RequestProcessor("GUI Builder", 10, false); // NOI18N
    // constants for CopyProperties method
    public static final int CHANGED_ONLY = 1;
    public static final int DISABLE_CHANGE_FIRING = 2;
    public static final int PASS_DESIGN_VALUES = 4;
    public static final int DONT_CLONE_VALUES = 8;
    static final String PROP_REQUIRES_PARENT = "thisPropertyRequiresParent"; // NOI18N
    static final String PROP_REQUIRES_CHILDREN = "thisPropertyRequiresChildren"; // NOI18N
    static final String ACTION_PERFORMED_EVENT_HANDLER_NAME = "onActionPerformed";//NOI18N
    static final String ON_MOUSE_CLICKED_EVENT_HANDLER_NAME = "onMouseClicked";//NOI18N
    private static final Map<String, Class<?>> eventsNames2scriptEventsClasses = new HashMap<>();

    private static Map<Class<?>, Map<String, DefaultValueDeviation>> defaultValueDeviations;

    private static final Map<Class<?>, Class<?>> layoutClasses2PlatypusContainerClasses = new HashMap<>();
    private static final Map<Class<?>, String> componentClasses2DefaultEventHandlers = new HashMap<>();

    static {
        initLayoutClasses2PlatypusContainerClasses();
        initComponentClasses2DefaultEventHandlers();
        initEventsNames2ScriptEventClasses();
    }

    private static void initLayoutClasses2PlatypusContainerClasses() {
        layoutClasses2PlatypusContainerClasses.put(MarginLayout.class, AnchorsPane.class);
        layoutClasses2PlatypusContainerClasses.put(BorderLayout.class, BorderPane.class);
        layoutClasses2PlatypusContainerClasses.put(BoxLayout.class, BoxPane.class);
        layoutClasses2PlatypusContainerClasses.put(java.awt.FlowLayout.class, FlowPane.class);
        layoutClasses2PlatypusContainerClasses.put(CardLayout.class, CardPane.class);
        layoutClasses2PlatypusContainerClasses.put(java.awt.GridLayout.class, GridPane.class);
    }

    private static void initEventsNames2ScriptEventClasses() {
        eventsNames2scriptEventsClasses.put(ON_MOUSE_CLICKED_EVENT_HANDLER_NAME, MouseEvent.class);
    }

    private static void initComponentClasses2DefaultEventHandlers() {
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.Button.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.ToggleButton.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.DropDownButton.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.CheckBox.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.RadioButton.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.TextField.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.menu.MenuItem.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.menu.CheckMenuItem.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.menu.RadioMenuItem.class, ACTION_PERFORMED_EVENT_HANDLER_NAME);
        componentClasses2DefaultEventHandlers.put(com.eas.client.forms.components.Label.class, ON_MOUSE_CLICKED_EVENT_HANDLER_NAME);

    }

    // -----------------------------------------------------------------------------
    // Utility methods
    public static ResourceBundle getBundle() {
        return NbBundle.getBundle(FormUtils.class);
    }

    public static String getBundleString(String key) {
        return NbBundle.getMessage(FormUtils.class, key);
    }

    public static String getFormattedBundleString(String key,
            Object[] arguments) {
        return NbBundle.getMessage(FormUtils.class, key, arguments);
    }

    public static String getDefaultEventPropertyName(Class<?> componentClass) {
        return componentClasses2DefaultEventHandlers.get(componentClass);
    }

    public static boolean addComponentToEndOfContainer(RADComponent<?> targetContainer, PaletteItem paletteItem) throws Exception {
        FormModel model = targetContainer.getFormModel();
        RADComponentCreator creator = model.getComponentCreator();
        creator.precreateVisualComponent(paletteItem.getComponentClassSource());
        return creator.addPrecreatedComponent(targetContainer, -1, null);
    }

    /**
     * Utility method that tries to clone an object. Objects of explicitly
     * specified types are constructed directly, other are serialized and
     * deserialized (if not serializable exception is thrown).
     *
     * @param o object to clone.
     * @return cloned of the given object.
     * @throws java.lang.CloneNotSupportedException when cloning was
     * unsuccessful.
     */
    public static Object cloneObject(Object o) throws CloneNotSupportedException {
        if (o == null) {
            return null;
        }
        if ((o instanceof Byte)
                || (o instanceof Short)
                || (o instanceof Integer)
                || (o instanceof Long)
                || (o instanceof Float)
                || (o instanceof Double)
                || (o instanceof Boolean)
                || (o instanceof Character)
                || (o instanceof String)) {
            return o; // no need to change reference
        }

        if (o instanceof Font) {
            return o;
        }
        if (o instanceof Dimension) {
            return new Dimension((Dimension) o);
        }
        if (o instanceof Point) {
            return new Point((Point) o);
        }
        if (o instanceof Rectangle) {
            return new Rectangle((Rectangle) o);
        }
        if (o instanceof Insets) {
            return ((Insets) o).clone();
        }
        if (o instanceof Cursor) {
            return new com.eas.gui.Cursor(((Cursor) o).getType());
        }
        if (o instanceof Paint) {
            return o;
        }
        if (o instanceof JSObject) {
            return o;
        }
        if (o instanceof IconEditor.NbImageIcon) {
            return o;
        }
        throw new CloneNotSupportedException();
    }

    /**
     * Utility method that tries to clone an object as a bean. First - if it is
     * serializable, then it is copied using serialization. If not serializable,
     * then all properties (taken from BeanInfo) are copied (property values
     * cloned recursively).
     *
     * @param bean bean to clone.
     * @param bInfo bean info.
     * @param formModel form model.
     * @return clone of the given bean.
     * @throws java.lang.CloneNotSupportedException when cloning was
     * unsuccessful.
     */
    public static Object cloneBeanInstance(Object bean, BeanInfo bInfo, FormModel formModel)
            throws CloneNotSupportedException {
        if (bean == null) {
            return null;
        }

        if (bean instanceof Serializable) {
            OOS oos = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new OOS(baos);
                oos.writeObject(bean);
                oos.close();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                return new OIS(bais, bean.getClass().getClassLoader(), formModel).readObject();
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Cannot clone " + bean.getClass().getName(), ex); // NOI18N
                throw new CloneNotSupportedException();
            } finally {
                if (oos != null) {
                    oos.checkJComponentSerialization();
                }
            }
        }

        // object is not Serializable
        Object clone;
        try {
            clone = CreationFactory.createDefaultInstance(bean.getClass());
            if (clone == null) {
                throw new CloneNotSupportedException();
            }

            if (bInfo == null) {
                bInfo = Utilities.getBeanInfo(bean.getClass());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Cannot clone " + bean.getClass().getName(), ex); // NOI18N
            throw new CloneNotSupportedException();
        }

        // default instance successfully created, now copy properties
        PropertyDescriptor[] pds = bInfo.getPropertyDescriptors();
        for (int i = 0; i < pds.length; i++) {
            Method getter = pds[i].getReadMethod();
            Method setter = pds[i].getWriteMethod();
            if (getter != null && setter != null) {
                Object propertyValue;
                try {
                    propertyValue = getter.invoke(bean, new Object[0]);
                } catch (Exception e1) { // ignore - do not copy this property
                    continue;
                }
                try {
                    propertyValue = cloneObject(propertyValue);
                } catch (Exception e2) { // ignore - do not clone property value
                }
                try {
                    setter.invoke(clone, new Object[]{propertyValue});
                } catch (Exception e3) { // ignore - do not copy this property
                }
            }
        }

        return clone;
    }

    /**
     * Special ObjectOutputStream subclass that takes care of possible failure
     * in serialization of JComponent which may leave the component with
     * uninstalled ComponentUI. This may happen when serializing property values
     * like DefaultComboBoxModel which reference the component they are set to.
     * See issue 72802. [In future it would be nice to have a better way of
     * copying the property values, minimizing the use of serialization.]
     */
    private static class OOS extends ObjectOutputStream {

        private Set<SerializationMarker> placedMarkers = new HashSet<>();

        private OOS(OutputStream os) throws IOException {
            super(os);
            enableReplaceObject(true);
        }

        /**
         * This method allows us to monitor objects going into the stream. If
         * the object is a JComponent, we add our special marker object to its
         * client properties. The marker keeps track of whether it was
         * serialized or not.
         */
        @Override
        protected Object replaceObject(Object obj) throws IOException {
            if (obj instanceof JComponent) {
                JComponent comp = (JComponent) obj;
                SerializationMarker sm = new SerializationMarker(comp);
                comp.putClientProperty(SerializationMarker.KEY, sm);
                placedMarkers.add(sm);
            }
            return obj;
        }

        /**
         * Go through all markers added to components during serialization. If a
         * marker was serialized, it means the component's client properties
         * serialization was at least started - which is done after installing
         * the ComponentUI back after serializing the component itself (see
         * JComponent.writeObject). If the marker was not serialized, it is
         * likely that the ComponentUI was left uninstalled (from
         * JComponent.compWriteObjectNotify).
         */
        private void checkJComponentSerialization() {
            for (SerializationMarker sm : placedMarkers) {
                JComponent comp = sm.component;
                if (!sm.serialized) {
                    fixUnserializedJComponent(comp);
                }
                comp.putClientProperty(SerializationMarker.KEY, null);
            }
        }

        /**
         * Hack: Mimics the code of JComponent.writeObject() to install back
         * ComponentUI of the component if it was not done due to interrupted
         * serialization. Calling private methods and accessing private field
         * via reflection, yuck...
         */
        private static void fixUnserializedJComponent(JComponent comp) {
            try {
                Method getWriteObjCounter_Method = JComponent.class.getDeclaredMethod("getWriteObjCounter", JComponent.class); // NOI18N
                getWriteObjCounter_Method.setAccessible(true);
                Method setWriteObjCounter_Method = JComponent.class.getDeclaredMethod("setWriteObjCounter", JComponent.class, Byte.TYPE); // NOI18N
                setWriteObjCounter_Method.setAccessible(true);
                Field ui_Field = JComponent.class.getDeclaredField("ui"); // NOI18N
                ui_Field.setAccessible(true);

                byte count = ((Byte) getWriteObjCounter_Method.invoke(null, comp)).byteValue();
                if (count > 0) { // counter not 0, serialization has not finished
                    count = 0;
                    setWriteObjCounter_Method.invoke(null, comp, count);
                    // reinstall ComponentUI
                    LOGGER.log(Level.INFO, "Reinstalling ComponentUI after interrupted serialization of component: {0}", comp); // NOI18N
                    ComponentUI ui = (ComponentUI) ui_Field.get(comp);
                    ui.installUI(comp);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Reinstalling ComponentUI after interrupted serialization of JComponent failed", ex); // NOI18N
            }
        }
    }

    /**
     * Special object added to JComponent's client properties to track whether
     * the client properties were sucessfully serialized (or at least started).
     */
    private static class SerializationMarker implements Serializable {

        private static final Object KEY = new Object();
        transient boolean serialized;
        transient JComponent component;

        private SerializationMarker(JComponent comp) {
            component = comp;
        }

        public Object writeReplace() {
            serialized = true;
            return new Object();
        }
    }

    /**
     * This method provides copying of property values from one array of
     * properties to another. The arrays need not be equally sorted. It is
     * recommended to use arrays of FormProperty, for which the mode parameter
     * can be used to specify some options (using bit flags): CHANGED_ONLY (to
     * copy only values of changed properties), DISABLE_CHANGE_FIRING (to
     * disable firing of changes in target properties), PASS_DESIGN_VALUES (to
     * pass the same FormDesignValue instances if they cannot or should not be
     * copied),
     *
     * @param sourceProperties properties to copy values from.
     * @param targetProperties properties to copy values to.
     * @param mode see the description above.
     */
    public static void copyProperties(FormProperty<?>[] sourceProperties,
            FormProperty<?>[] targetProperties,
            int mode) {
        for (int i = 0; i < sourceProperties.length; i++) {
            FormProperty<?> snProp = sourceProperties[i];
            FormProperty<?> sfProp = snProp instanceof FormProperty
                    ? (FormProperty<?>) snProp : null;

            if (sfProp != null
                    && (mode & CHANGED_ONLY) != 0
                    && sfProp.isDefaultValue()) {
                continue; // copy only changed properties
            }
            // find target property
            FormProperty<Object> tnProp = (FormProperty<Object>) targetProperties[i];
            if (!tnProp.getName().equals(snProp.getName())) {
                int j;
                for (j = 0; j < targetProperties.length; j++) {
                    tnProp = (FormProperty<Object>) targetProperties[i];
                    if (tnProp.getName().equals(snProp.getName())) {
                        break;
                    }
                }
                if (j == targetProperties.length) {
                    continue; // not found
                }
            }
            try {
                // get and clone property value
                Object propertyValue = snProp.getValue();
                Object copiedValue = propertyValue;
                if ((mode & DONT_CLONE_VALUES) == 0) {
                    try { // clone common property value                        
                        copiedValue = FormUtils.cloneObject(propertyValue);
                    } catch (CloneNotSupportedException ex) {
                    } // ignore, don't report
                }
                // set property value
                boolean firing = tnProp.isChangeFiring();
                tnProp.setChangeFiring((mode & DISABLE_CHANGE_FIRING) == 0);
                tnProp.setValue(copiedValue);
                tnProp.setChangeFiring(firing);
            } catch (Exception ex) { // ignore
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
    }

    public static void copyPropertiesToBean(RADProperty<?>[] props,
            Object targetBean,
            Collection<RADProperty<?>> relativeProperties) {
        copyPropertiesToBean(Arrays.asList(props), targetBean, relativeProperties);
    }

    public static void copyPropertiesToBean(List<RADProperty<?>> props,
            Object targetBean,
            Collection<RADProperty<?>> relativeProperties) {
        for (RADProperty<?> prop : props) {
            if (prop.isDefaultValue()) {
                continue;
            }
            try {
                if (relativeProperties != null) {
                    Object value = prop.getValue();
                    if (value instanceof RADComponent<?> || value instanceof ComponentReference) {
                        relativeProperties.add(prop);
                        continue;
                    }
                }
                Method writeMethod = getPropertyWriteMethod(prop, targetBean.getClass()); //prop.getPropertyDescriptor().getWriteMethod();
                if (writeMethod != null) {
                    // There are cases when properties values are not applicable to native swing components properties
                    // So we have to get value to clone directly from Swing Component
                    Object realValue = prop.getPropertyDescriptor().getReadMethod().invoke(prop.getComponent().getBeanInstance(), new Object[]{});
                    realValue = FormUtils.cloneObject(realValue);
                    writeMethod.invoke(targetBean, new Object[]{realValue});
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, null, ex); // NOI18N
            }
        }
    }

    public static Method getPropertyWriteMethod(RADProperty<?> property, Class<?> targetClass) {
        Method method = property.getPropertyDescriptor().getWriteMethod();
        if (method != null
                && targetClass != null
                && !method.getDeclaringClass().isAssignableFrom(targetClass)) {
            // try to use find the same method in the target class
            try {
                method = targetClass.getMethod(method.getName(),
                        method.getParameterTypes());
            } catch (Exception ex) { // ignore
                method = null;
            }
        }
        return method;
    }

    public static void setupEditorPane(javax.swing.JEditorPane editor, FileObject srcFile, int ccPosition) {
        DataObject dob = null;
        try {
            dob = DataObject.find(srcFile);
        } catch (DataObjectNotFoundException dnfex) {
            LOGGER.log(Level.INFO, dnfex.getMessage(), dnfex);
        }
        if (!(dob instanceof PlatypusFormDataObject)) {
            LOGGER.log(Level.INFO, "Unable to find FormDataObject for {0}", srcFile); // NOI18N
            return;
        }
        PlatypusFormDataObject formDob = (PlatypusFormDataObject) dob;
        Document document = formDob.getLookup().lookup(PlatypusFormSupport.class).getDocument();
        DialogBinding.bindComponentToDocument(document, ccPosition, 0, editor);

        // do not highlight current row
        editor.putClientProperty(
                "HighlightsLayerExcludes", //NOI18N
                "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" //NOI18N
        );
    }

    public static boolean isContainer(Class<?> beanClass) {
        return HasChildren.class.isAssignableFrom(beanClass) || Panel.class.isAssignableFrom(beanClass);
    }

    public static boolean isVisualizableClass(Class<?> cls) {
        if (java.awt.Component.class.isAssignableFrom(cls) && !ButtonGroup.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public static RADVisualContainer<?> getSameParent(List<RADVisualComponent<?>> aComps) {
        RADVisualContainer<?> prevParent;
        RADVisualContainer<?> parent = null;
        if (aComps != null && !aComps.isEmpty()) {
            parent = aComps.get(0).getParentComponent();
            for (RADVisualComponent<?> comp : aComps) {
                prevParent = parent;
                parent = comp.getParentComponent();
                if (parent != prevParent) {
                    return null;
                }
            }
        }
        return parent;
    }
    // ---------

    public static Class<?> getPlatypusConainerClass(Class<?> aLayoutClass) {
        return layoutClasses2PlatypusContainerClasses.get(aLayoutClass);
    }

    static boolean isMarkedParentDependentProperty(FormProperty<?> prop) {
        return Boolean.TRUE.equals(prop.getValue(PROP_REQUIRES_PARENT));
    }

    static boolean isMarkedChildrenDependentProperty(FormProperty<?> prop) {
        return Boolean.TRUE.equals(prop.getValue(PROP_REQUIRES_CHILDREN));
    }

    // -----
    private static abstract class DefaultValueDeviation {

        protected Object[] values;

        DefaultValueDeviation(Object[] values) {
            this.values = values;
        }

        abstract Object getValue(Object beanInstance);
    }

    private static Map<String, DefaultValueDeviation> getDefaultValueDeviations(Object bean) {
        if (defaultValueDeviations == null) {
            defaultValueDeviations = new HashMap<>();
        }
        Map<String, DefaultValueDeviation> deviationMap = defaultValueDeviations.get(bean.getClass());
        if (deviationMap == null) {
            if (bean instanceof javax.swing.JTextField) {
                // text field has different default background when not editable
                Object[] values = new Color[2];
                javax.swing.JTextField jtf = new javax.swing.JTextField();
                values[0] = jtf.getBackground();
                jtf.setEditable(false);
                values[1] = jtf.getBackground();
                deviationMap = new HashMap<>();
                deviationMap.put("background", // NOI18N
                        new DefaultValueDeviation(values) {
                            @Override
                            Object getValue(Object beanInstance) {
                                return ((javax.swing.JTextField) beanInstance).isEditable()
                                        ? this.values[0] : this.values[1];
                            }
                        });
                defaultValueDeviations.put(bean.getClass(), deviationMap);
            }
        }
        return deviationMap;
    }

    static Object getSpecialDefaultPropertyValue(Object bean, String propName) {
        Map<String, DefaultValueDeviation> deviationMap = getDefaultValueDeviations(bean);
        if (deviationMap != null) {
            DefaultValueDeviation deviation = deviationMap.get(propName);
            if (deviation != null) {
                return deviation.getValue(bean);
            }
        }
        return BeanSupport.NO_VALUE;
    }

    // -----
    /**
     * Utility method that returns name of the method.
     *
     * @param desc descriptor of the method.
     * @return a formatted name of specified method
     */
    public static String getMethodName(MethodDescriptor desc) {
        return getMethodName(desc.getName(), desc.getMethod().getParameterTypes());
    }

    public static String getMethodName(String name, Class<?>[] params) {
        StringBuilder sb = new StringBuilder(name);
        if ((params == null) || (params.length == 0)) {
            sb.append("()"); // NOI18N
        } else {
            for (int i = 0; i < params.length; i++) {
                if (i == 0) {
                    sb.append("("); // NOI18N
                } else {
                    sb.append(", "); // NOI18N
                }
                sb.append(Utilities.getShortClassName(params[i]));
            }
            sb.append(")"); // NOI18N
        }

        return sb.toString();
    }

    static void sortProperties(FormProperty<?>[] properties) {
        Arrays.sort(properties, new Comparator<FormProperty<?>>() {
            @Override
            public int compare(FormProperty<?> o1, FormProperty<?> o2) {
                String n1 = o1.getDisplayName();
                String n2 = o2.getDisplayName();
                return n1.compareTo(n2);
            }
        });
    }

    private static int findPropertyIndex(RADProperty<?>[] properties, String property) {
        int index = -1;
        for (int i = 0; i < properties.length; i++) {
            if (property.equals(properties[i].getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Loads a class using IDE system class loader. Usable for form module
     * support classes, property editors, etc.
     *
     * @param name name of the class to load.
     * @return loaded class.
     * @throws java.lang.ClassNotFoundException if there is a problem with class
     * loading.
     */
    public static Class<?> loadSystemClass(String name) throws ClassNotFoundException {
        ClassLoader loader = Lookup.getDefault().lookup(ClassLoader.class);
        if (loader == null) {
            throw new ClassNotFoundException();
        }

        return Class.forName(name, true, loader);
    }

    // ---------
    private static class OIS extends ObjectInputStream {

        private ClassLoader classLoader;
        private FormModel formModel;

        public OIS(InputStream is, ClassLoader loader, FormModel formModel) throws IOException {
            super(is);
            this.formModel = formModel;
            classLoader = loader;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass streamCls)
                throws IOException, ClassNotFoundException {
            String name = streamCls.getName();
            return loadClass(name);
        }

        private Class<?> loadClass(String name) throws ClassNotFoundException {
            if (classLoader != null) {
                try {
                    return Class.forName(name, true, classLoader);
                } catch (ClassNotFoundException ex) {
                }
            }
            return loadSystemClass(name);//loadClass(name, formModel);
        }
    }

    public static List<RADVisualComponent<?>> getSelectedLayoutComponents(Node[] nodes) {
        if (nodes != null && nodes.length > 0) {
            List<RADVisualComponent<?>> components = new ArrayList<>();
            for (int i = 0; i < nodes.length; i++) {
                RADComponentCookie radCookie = nodes[i].getLookup().lookup(RADComponentCookie.class);
                if (radCookie != null) {
                    RADComponent<?> radComp = radCookie.getRADComponent();
                    if ((radComp instanceof RADVisualComponent<?>)) {
                        RADVisualComponent<?> visComp = (RADVisualComponent<?>) radComp;
                        RADVisualContainer<?> visCont = visComp.getParentComponent();
                        if ((visCont != null) && ScrollPane.class.isAssignableFrom(visCont.getBeanInstance().getClass())) {
                            visComp = visCont;
                            visCont = visCont.getParentComponent();
                        }
                        if (visCont != null
                                && (visCont.getLayoutSupport() == null || (visCont.getLayoutSupport() != null && visCont.getLayoutSupport().getLayoutDelegate() instanceof MarginLayoutSupport))
                                && !visComp.isMenuComponent()) {
                            components.add(visComp);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
            return components;
        } else {
            return null;
        }
    }

    private static Set<String> superClasses(Class<?> beanClass) {
        Set<String> superClasses = new HashSet<>();
        Class<?>[] infaces = beanClass.getInterfaces();
        for (int i = 0; i < infaces.length; i++) {
            superClasses.add(infaces[i].getName());
        }
        Class<?> superClass = beanClass;
        do {
            superClasses.add(superClass.getName());
        } while ((superClass = superClass.getSuperclass()) != null);
        return superClasses;
    }

    /**
     * "Un-generifies" the given type.
     *
     * @param type type to "un-generify".
     * @return "un-generified" type.
     */
    public static Class<?> typeToClass(TypeHelper type) {
        Class<?> clazz = Object.class;
        if (type == null) {
            return clazz;
        }
        Type t = type.getType();
        if (t instanceof Class<?>) {
            clazz = (Class<?>) t;
        } else if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            clazz = (Class) pt.getRawType();
        } else if (t instanceof WildcardType) {
            WildcardType wt = (WildcardType) t;
            for (Type bound : wt.getUpperBounds()) {
                clazz = typeToClass(new TypeHelper(bound, type.getActualTypeArgs()));
                if (!Object.class.equals(clazz) && !clazz.isInterface()) {
                    break;
                }
            }
        } else if (t instanceof TypeVariable<?>) {
            TypeVariable<?> tv = (TypeVariable<?>) t;
            Map<String, TypeHelper> actualTypeArgs = type.getActualTypeArgs();
            if (actualTypeArgs != null) {
                TypeHelper tt = actualTypeArgs.get(tv.getName());
                if (tt != null) {
                    Type typ = typeToClass(tt);
                    clazz = typeToClass(new TypeHelper(typ, actualTypeArgs));
                }
            }
        }
        return clazz;
    }

    /**
     * Represents generified type with (possibly) some type parameters set.
     */
    public static class TypeHelper {

        /**
         * The type.
         */
        private Type type;
        /**
         * Fully qualified name of the type.
         */
        private String name;
        /**
         * Type parameters that has been set.
         */
        private Map<String, TypeHelper> actualTypeArgs;

        /**
         * Creates <code>TypeHelper</code> that represents <code>Object</code>.
         */
        public TypeHelper() {
            this(Object.class, null);
        }

        public TypeHelper(String name) {
            this(name, null);
        }

        public TypeHelper(String name, Map<String, TypeHelper> actualTypeArgs) {
            this.name = name;
            this.actualTypeArgs = actualTypeArgs;
        }

        /**
         * Creates <code>TypeHelper</code> that represents given type with no
         * type arguments set.
         *
         * @param type type.
         */
        public TypeHelper(Type type) {
            this(type, null);
        }

        /**
         * Creates <code>TypeHelper</code> that represents given type with some
         * type arguments set.
         *
         * @param type type.
         * @param actualTypeArgs type parameters that has been set.
         */
        public TypeHelper(Type type, Map<String, TypeHelper> actualTypeArgs) {
            this.type = type;
            this.actualTypeArgs = actualTypeArgs;
        }

        /**
         * Returns generified type represented by this instance.
         *
         * @return generified type represented by this instance.
         */
        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        /**
         * Returns map of type parameters that has been set.
         *
         * @return map or <code>null</code> if the type is not generified or
         * none of its type parameters has been set.
         */
        public Map<String, TypeHelper> getActualTypeArgs() {
            return actualTypeArgs;
        }

        /**
         * Returns (undefined ;-)) normalized form of this type.
         */
        TypeHelper normalize() {
            TypeHelper t = this;
            if (type instanceof TypeVariable<?>) {
                if (actualTypeArgs != null) {
                    TypeVariable<?> tv = (TypeVariable<?>) type;
                    t = actualTypeArgs.get(tv.getName());
                }
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                Class<?> clazz = (Class<?>) pt.getRawType();
                Map<String, TypeHelper> newMap = new HashMap<>();
                Type[] args = pt.getActualTypeArguments();
                TypeVariable<?>[] tvar = clazz.getTypeParameters();
                for (int i = 0; i < tvar.length; i++) {
                    Type arg = args[i];
                    TypeHelper sub = new TypeHelper(arg, actualTypeArgs).normalize();
                    newMap.put(tvar[i].getName(), new TypeHelper(sub.getType()));
                }
                t = new TypeHelper(clazz, newMap);
            } else if (type instanceof WildcardType) {
                WildcardType wt = (WildcardType) type;
                // PENDING more upper bounds
                TypeHelper sub = new TypeHelper(wt.getUpperBounds()[0], actualTypeArgs).normalize();
                t = new TypeHelper(sub.getType());
            }
            return t;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TypeHelper) {
                TypeHelper t = (TypeHelper) o;
                return ((name == null) ? (t.name == null) : name.equals(t.name))
                        && ((type == null) ? (t.type == null) : type.equals(t.type));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
    }

    public static String autobox(String className) {
        switch (className) {
            case "byte":
            case "short":
            case "long":
            case "float":
            case "double":
            case "boolean":
                // NOI18N
                className = "java.lang." + Character.toUpperCase(className.charAt(0)) + className.substring(1); // NOI18N
                break;
            case "int":
                // NOI18N
                className = "java.lang.Integer"; // NOI18N
                break;
            case "char":
                // NOI18N
                className = "java.lang.Character"; // NOI18N
                break;
        }
        return className;
    }

    public static String escapeCharactersInString(String str) {
        StringBuilder buf = new StringBuilder(str.length() * 6); // x -> \u1234
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case '\b':
                    buf.append("\\b");
                    break; // NOI18N
                case '\t':
                    buf.append("\\t");
                    break; // NOI18N
                case '\n':
                    buf.append("\\n");
                    break; // NOI18N
                case '\f':
                    buf.append("\\f");
                    break; // NOI18N
                case '\r':
                    buf.append("\\r");
                    break; // NOI18N
                case '\"':
                    buf.append("\\\"");
                    break; // NOI18N
                case '\\':
                    buf.append("\\\\");
                    break; // NOI18N
                default:
                    if (c >= 0x0020/* && c <= 0x007f*/) {
                        buf.append(c);
                    } else {
                        buf.append("\\u"); // NOI18N
                        String hex = Integer.toHexString(c);
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            buf.append('0');
                        }
                        buf.append(hex);
                    }
            }
        }
        return buf.toString();
    }

    /*
     * Calls Introspector.getBeanInfo() more safely to handle 3rd party BeanInfos
     * that may be broken or malformed. This is a replacement for Introspector.getBeanInfo().
     * @see java.beans.Introspector.getBeanInfo(Class)
     */
    public static BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz, java.beans.Introspector.IGNORE_ALL_BEANINFO);
        /*
         try {
         return Introspector.getBeanInfo(clazz, java.beans.Introspector.IGNORE_ALL_BEANINFO);
         } catch (Exception | Error ex) {
         org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
         return getBeanInfo(clazz, Introspector.IGNORE_IMMEDIATE_BEANINFO);
         }
         */
    }

    // helper method for getBeanInfo(Class)
    /*
     static BeanInfo getBeanInfo(Class<?> clazz, int mode) throws IntrospectionException {
     if (mode == Introspector.IGNORE_IMMEDIATE_BEANINFO) {
     try {
     return Introspector.getBeanInfo(clazz, Introspector.IGNORE_IMMEDIATE_BEANINFO);
     } catch (Exception | Error ex) {
     org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
     return getBeanInfo(clazz, Introspector.IGNORE_ALL_BEANINFO);
     }
     } else {
     assert mode == Introspector.IGNORE_ALL_BEANINFO;
     return Introspector.getBeanInfo(clazz, Introspector.IGNORE_ALL_BEANINFO);
     }
     }
     */
    public static Class<?> getScriptEventClassByName(String anEventName) {
        return eventsNames2scriptEventsClasses.get(anEventName);
    }

    public static RequestProcessor getRequestProcessor() {
        return RP;
    }

    public static Font getDefaultAWTFont() {
        if (defaultFont == null) {
            defaultFont = org.openide.windows.WindowManager.getDefault()
                    .getMainWindow().getFont();
            if (defaultFont == null) {
                defaultFont = new Font("Dialog", Font.PLAIN, 12); // NOI18N
            }
        }
        return defaultFont;
    }
    private static Font defaultFont;
}
