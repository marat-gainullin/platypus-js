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

import com.bearsoft.org.netbeans.modules.form.RADProperty.FakePropertyDescriptor;
import com.bearsoft.org.netbeans.modules.form.editors.EntityJSObjectEditor;
import com.bearsoft.org.netbeans.modules.form.editors.EnumEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.eas.client.forms.HorizontalPosition;
import com.eas.client.forms.Orientation;
import com.eas.client.forms.VerticalPosition;
import com.eas.client.forms.components.FormattedField;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.rt.HasGroup;
import com.eas.client.forms.components.rt.VFormattedField;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.containers.SplitPane;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.ScriptFunction;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;
import jdk.nashorn.api.scripting.JSObject;
import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.datatransfer.NewType;

/**
 *
 * @author Ian Formanek
 * @param <C>
 */
public abstract class RADComponent<C> {

    // -----------------------------------------------------------------------------
    // Static variables
    public static final String COMPONENT_NAME_PROP_NAME = "name"; // NOI18N
    static final NewType[] NO_NEW_TYPES = {};
    static final RADProperty<?>[] NO_PROPERTIES = {};
    // -----------------------------------------------------------------------------
    // Private variables
    private Class<?> beanClass;
    private C beanInstance;
    private BeanInfo beanInfo;
    private BeanInfo fakeBeanInfo;
    private String missingClassName;
    protected Node.PropertySet[] propertySets;
    private Map<String, RADProperty<?>[]> propsByCategories;
    protected Map<String, RADProperty<?>> nameToProperty;
    protected final PropertyChangeListener propertyListener = new PropertyChangesPropagator();
    private ComponentContainer parent;
    public FormModel formModel;
    private boolean inModel;
    private RADComponentNode componentNode;
    protected String storedName; // component name preserved e.g. for remove undo
    private boolean valid = true;
    // -----------------------------------------------------------------------------
    // Constructors & Initialization

    public RADComponent() {
        super();
    }

    /**
     * Called to initialize the component with specified FormModel.
     *
     * @param aFormModel the FormModel of the form into which this component
     * will be added
     * @return <code>true</code> if the model was initialized,
     * <code>false</code> otherwise.
     */
    public boolean initialize(FormModel aFormModel) {
        if (formModel == null) {
            formModel = aFormModel;
            // properties and events will be created on first request
            clearProperties();
            return true;
        } else if (formModel != aFormModel) {
            throw new IllegalStateException(
                    "Cannot initialize radcomponent with another form model"); // NOI18N
        }
        return false;
    }

    public void setParent(ComponentContainer aParent) {
        parent = aParent;
    }

    /**
     * Initializes the bean instance represented by this meta component. A
     * default instance is created for the given bean class. The meta component
     * is fully initialized after this method returns.
     *
     * @param aClass the bean class to be represented by this meta component
     * @return initialized instance.
     * @throws java.lang.Exception when the instance cannot be initialized.
     */
    public C initInstance(Class<?> aClass) throws Exception {
        if (aClass == null) {
            throw new NullPointerException();
        }
        if (beanClass != aClass && beanClass != null) {
            beanInfo = null;
            fakeBeanInfo = null;
            clearProperties();
        }
        beanClass = aClass;
        C bean = createBeanInstance();
        getBeanInfo(); // force BeanInfo creation here - will be needed, may fail
        setBeanInstance(bean);
        return beanInstance;
    }

    /**
     * Sets the bean instance represented by this meta component. The meta
     * component is fully initialized after this method returns.
     *
     * @param beanInstance the bean to be represented by this meta component
     */
    public void setInstance(C beanInstance) {
        if (beanClass != beanInstance.getClass()) {
            beanInfo = null;
            fakeBeanInfo = null;
        }
        clearProperties();
        beanClass = (Class<? extends C>) beanInstance.getClass();
        getBeanInfo(); // force BeanInfo creation here - will be needed, may fail
        setBeanInstance(beanInstance);
    }

    /**
     * Updates the bean instance - e.g. when setting a property requires to
     * create new instance of the bean.
     *
     * @param beanInstance bean instance.
     */
    public void updateInstance(C beanInstance) {
        if (beanInstance != null && beanClass == beanInstance.getClass()) {
            setBeanInstance(beanInstance);
        } // should properties also be reinstated?
        // formModel.fireFormChanged() ?
        else {
            setInstance(beanInstance);
        }
    }

    /**
     * Called to create the instance of the bean. This method is called if the
     * initInstance method is used; using the setInstance method, the bean
     * instance is set directly.
     *
     * @return the instance of the bean that will be used during design time
     * @throws java.lang.Exception when the instance cannot be created.
     */
    protected C createBeanInstance() throws Exception {
        return CreationFactory.<C>createDefaultInstance(beanClass);
    }

    /**
     * Sets directly the bean instance. Can be overriden.
     *
     * @param aBeanInstance bean instance.
     */
    protected void setBeanInstance(C aBeanInstance) {
        if (beanClass == null) { // bean class not set yet
            beanClass = (Class<? extends C>) aBeanInstance.getClass();
        }
        beanInstance = aBeanInstance;
    }

    public void setInModel(boolean aValue) {
        if (inModel != aValue) {
            inModel = aValue;
            if (aValue) {
                formModel.updateMapping(this, true);
            } else {
                formModel.updateMapping(this, false);
                setNodeReference(null);
            }
        }
    }

    void setNodeReference(RADComponentNode node) {
        componentNode = node;
    }

    public final boolean isReadOnly() {
        return formModel.isReadOnly();
    }

    /**
     * Provides access to the Class of the bean represented by this RADComponent
     *
     * @return the Class of the bean represented by this RADComponent
     */
    public final Class<?> getBeanClass() {
        return beanClass;
    }

    public final String getMissingClassName() {
        return missingClassName;
    }

    public final void setMissingClassName(String className) {
        missingClassName = className;
    }

    /**
     * Provides access to the real instance of the bean represented by this
     * RADComponent
     *
     * @return the instance of the bean represented by this RADComponent
     */
    public final C getBeanInstance() {
        return beanInstance;
    }

    public final RADVisualContainer<?> getParentComponent() {
        return parent instanceof RADVisualContainer<?> ? (RADVisualContainer<?>) parent : null;
    }

    public final ComponentContainer getParent() {
        return parent;
    }

    public final boolean isParentComponent(RADComponent<?> comp) {
        ComponentContainer lparent = comp.getParentComponent();
        while (lparent != this && lparent instanceof RADComponent<?>) {
            lparent = ((RADComponent<?>) lparent).getParentComponent();
        }
        return lparent == this;
    }

    public C cloneBeanInstance(Collection<RADProperty<?>> relativeProperties) {
        C clone;
        try {
            clone = createBeanInstance();
        } catch (Exception ex) { // ignore, this should not fail
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
            return null;
        }
        FormUtils.copyPropertiesToBean(getBeanProperties(),
                clone,
                relativeProperties);
        return clone;
    }

    /**
     * Provides access to BeanInfo of the bean represented by this RADComponent
     *
     * @return the BeanInfo of the bean represented by this RADComponent
     */
    public BeanInfo getBeanInfo() {
        if (beanInfo == null) {
            try {
                beanInfo = FormUtils.getBeanInfo(beanClass);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, ex);
                beanInfo = new FakeBeanInfo();
            }
        }
        if (isValid()) {
            return beanInfo;
        } else {
            if (fakeBeanInfo == null) {
                fakeBeanInfo = new FakeBeanInfo();
            }
            return fakeBeanInfo;
        }
    }

    /**
     * This method can be used to check whether the bean represented by this
     * RADComponent has hidden-state.
     *
     * @return true if the component has hidden state, false otherwise
     */
    public boolean hasHiddenState() {
        String name = beanClass.getName();
        if (name.startsWith("javax.") // NOI18N
                || name.startsWith("java.") // NOI18N
                || name.startsWith("org.openide.")) // NOI18N
        {
            return false;
        }

        return getBeanInfo().getBeanDescriptor().getValue("hidden-state") != null; // NOI18N
    }

    /**
     * Getter for the name of the radcomponent - it maps to variable name
     * declared for the instance of the component in the generated java code. It
     * is a unique identification of the component within a form, but it may
     * change (currently editable as "Variable Name" in code gen. properties).
     *
     * @return current value of the Name property
     */
    public String getName() {
        return storedName;
    }

    /**
     * Setter for the name of the component - it is the name of the component's
     * node and the name of the variable declared for the component in the
     * generated code.
     *
     * @param aValue new name of the component
     */
    public void setName(String aValue) {
        if (formModel.getRADComponent(aValue) == null) {
            String oldName = storedName;
            formModel.updateMapping(this, false);
            storedName = aValue;
            if (beanInstance instanceof Component) {
                ((Component) beanInstance).setName(storedName);
            }
            formModel.updateMapping(this, true);
            formModel.fireSyntheticPropertyChanged(this, COMPONENT_NAME_PROP_NAME,
                    oldName, aValue);
            if (getNodeReference() != null) {
                getNodeReference().updateName();
            }
        }
    }

    /**
     * Name setter for internal use. E.g. without any events, handlers renaming
     * etc. Typically applied while copy of a component.
     *
     * @param name
     */
    public void setStoredName(String name) {
        storedName = name;
    }

    /**
     * Provides access to the FormModel class which manages the form in which
     * this component has been added.
     *
     * @return the FormModel which manages the form into which this component
     * has been added
     */
    public final FormModel getFormModel() {
        return formModel;
    }

    public final boolean isInModel() {
        return inModel;
    }

    /**
     * Support for new types that can be created in this node.
     *
     * @return array of new type operations that are allowed
     */
    public NewType[] getNewTypes() {
        return NO_NEW_TYPES;
    }

    public Node.PropertySet[] getProperties() {
        if (propertySets == null) {
            List<Node.PropertySet> propSets = new ArrayList<>(5);
            createPropertySets(propSets);
            propertySets = new Node.PropertySet[propSets.size()];
            propSets.toArray(propertySets);
        }
        return propertySets;
    }

    /**
     * Provides access to the Node which represents this RADComponent
     *
     * @return the RADComponentNode which represents this RADComponent
     */
    public RADComponentNode getNodeReference() {
        return componentNode;
    }

    /**
     * Returns property of given name corresponding to a property or event.
     * Forces creation of all property objects.
     *
     * @param <P>
     * @param name name of the property.
     * @return bean or event property
     */
    public <P extends FormProperty<?>> P getProperty(String name) {
        if (nameToProperty == null) {
            createBeanProperties();
        }
        return (P) nameToProperty.get(name);
    }

    public RADProperty<?>[] getFakeBeanProperties(String[] propNames, Class<?>[] propertyTypes) {
        FakeBeanInfo fbi = (FakeBeanInfo) getBeanInfo();
        fbi.removePropertyDescriptors();
        for (int i = 0; i < propNames.length; i++) {
            fbi.addPropertyDescriptor(propNames[i], propertyTypes[i]);
        }
        return getBeanProperties(propNames);
    }

    /**
     * Returns bean properties of given names. Creates the properties if not
     * created yet, but does not force creation of all bean properties.
     *
     * @param propNames property names.
     * @return array of properties corresponding to the names; may contain null
     * if there is no property of given name
     */
    public RADProperty<?>[] getBeanProperties(String[] propNames) {
        if (nameToProperty == null) {
            createBeanProperties();
        }
        final Set<String> names = new HashSet<>(Arrays.asList(propNames));
        return nameToProperty.values().stream().filter((RADProperty<?> aProp) -> {
            return names.contains(aProp.getName());
        }).toArray((aLength) -> {
            return new RADProperty<?>[aLength];
        });
    }

    // -----------------------------------------------------------------------------
    // Properties
    protected void clearProperties() {
        propsByCategories = null;
        nameToProperty = null;
        propertySets = null;
    }

    protected void createPropertySets(List<Node.PropertySet> propSets) {
        if (nameToProperty == null) {
            createBeanProperties();
        }
        ResourceBundle bundle = FormUtils.getBundle();
        if (isValid()) {
            for (Map.Entry<String, RADProperty<?>[]> entry : propsByCategories.entrySet()) {
                final String category = entry.getKey();
                final RADProperty<?>[] props = propsByCategories.get(category);
                if (props.length > 0) {
                    propSets.add(new Node.PropertySet(category, bundle.getString("CTL_" + category), bundle.getString("CTL_" + category + "Hint")) {
                        @Override
                        public FormProperty<?>[] getProperties() {
                            return props;
                        }
                    });
                }
            }
        }
    }

    protected void createBeanProperties() {
        nameToProperty = new HashMap<>();
        Map<String, List<RADProperty<?>>> _propsByCategories = new TreeMap<>();
        // Let's create RADProperties by PropertyDescriptors
        // and fill nameToProperty map.
        PropertyDescriptor[] descriptors = getBeanInfo().getPropertyDescriptors();
        //props = FormUtils.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO).getPropertyDescriptors();
        //Class<?> _beanClass = getBeanInfo().getBeanDescriptor().getBeanClass();
        for (int i = 0; i < descriptors.length; i++) {
            PropertyDescriptor descriptor = descriptors[i];
            if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                Designable designable = descriptor.getReadMethod().getAnnotation(Designable.class);
                ScriptFunction scriptFunction = descriptor.getReadMethod().getAnnotation(ScriptFunction.class);
                if (designable == null) {
                    designable = descriptor.getWriteMethod().getAnnotation(Designable.class);
                }
                if (scriptFunction == null) {
                    scriptFunction = descriptor.getWriteMethod().getAnnotation(ScriptFunction.class);
                }
                if (designable != null || scriptFunction != null || LayoutManager.class.isAssignableFrom(beanClass)) {
                    String category = "general"; // NOI18N
                    if (designable != null && designable.category() != null && !designable.category().isEmpty()) {
                        category = designable.category();
                    }
                    List<RADProperty<?>> listToAdd = _propsByCategories.get(category);
                    if (listToAdd == null) {
                        listToAdd = new ArrayList<>();
                        _propsByCategories.put(category, listToAdd);
                    }
                    RADProperty<?> prop = createBeanProperty(descriptor);
                    if (prop != null) {
                        listToAdd.add(prop);
                    }
                }
            }
        }
        propsByCategories = new TreeMap<>();
        _propsByCategories.entrySet().stream().forEach((entry) -> {
            propsByCategories.put(entry.getKey(), entry.getValue().toArray(new RADProperty<?>[]{}));
        });
        addMagicProperties();
    }

    protected RADProperty<?> createBeanProperty(PropertyDescriptor desc) {
        if (desc.getPropertyType() != null && desc.getReadMethod() != null && desc.getWriteMethod() != null
                && !desc.getReadMethod().isAnnotationPresent(Undesignable.class)
                && !desc.getWriteMethod().isAnnotationPresent(Undesignable.class)) {
            try {
                RADProperty<?> prop = createCheckedBeanProperty(desc);
                Designable ann = null;
                if (desc.getReadMethod().isAnnotationPresent(Designable.class)) {
                    ann = desc.getReadMethod().getAnnotation(Designable.class);
                } else if (desc.getWriteMethod().isAnnotationPresent(Designable.class)) {
                    ann = desc.getWriteMethod().getAnnotation(Designable.class);
                }
                if (ann != null) {
                    if (ann.displayName() != null && !ann.displayName().isEmpty()) {
                        prop.setDisplayName(ann.displayName());
                    }
                    if (ann.description() != null && !ann.description().isEmpty()) {
                        prop.setShortDescription(ann.description());
                    }
                }
                prop.addPropertyChangeListener(propertyListener);
                nameToProperty.put(desc.getName(), prop);
                return prop;
            } catch (InvocationTargetException | IllegalAccessException ex) { // should not happen
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
                return null;
            }
        } else {
            return null;
        }
    }

    protected RADProperty<?> createCheckedBeanProperty(PropertyDescriptor desc) throws InvocationTargetException, IllegalAccessException {
        if (desc instanceof FakePropertyDescriptor) {
            return new FakeRADProperty<>(this, (FakePropertyDescriptor) desc);
        } else {
            if (java.awt.Component.class.isAssignableFrom(desc.getPropertyType())
                    && !ButtonGroup.class.isAssignableFrom(desc.getPropertyType())) {
                return new ComponentProperty(this, desc);
            } else if (javax.swing.Icon.class.isAssignableFrom(desc.getPropertyType()) || java.awt.Image.class.isAssignableFrom(desc.getPropertyType())) {
                return new IconProperty(this, desc);
            } else if ("valueType".equals(desc.getName()) && (FormattedField.class.isAssignableFrom(beanClass) || ModelFormattedField.class.isAssignableFrom(beanClass))) {
                return new ValueTypeProperty(this, desc);
            } else if ("orientation".equals(desc.getName()) && SplitPane.class.isAssignableFrom(beanClass)) {
                return new OrientationProperty(this, desc);
            } else if ("horizontalScrollBarPolicy".equals(desc.getName()) && ScrollPane.class.isAssignableFrom(beanClass)) {
                return new HorizontalScrollPolicyProperty(this, desc);
            } else if ("verticalScrollBarPolicy".equals(desc.getName()) && ScrollPane.class.isAssignableFrom(beanClass)) {
                return new HorizontalScrollPolicyProperty(this, desc);
            } else if ("horizontalAlignment".equals(desc.getName())) {
                return new HorizontalPositionProperty(this, desc);
            } else if ("verticalAlignment".equals(desc.getName())) {
                return new VerticalPositionProperty(this, desc);
            } else if ("horizontalTextPosition".equals(desc.getName())) {
                return new HorizontalPositionProperty(this, desc);
            } else if ("verticalTextPosition".equals(desc.getName())) {
                return new VerticalPositionProperty(this, desc);
            } else if(JSObject.class.isAssignableFrom(desc.getPropertyType())){
                return new EntityJsObjectProperty(this, desc);
            } else {
                return new RADProperty<>(this, desc);
            }
        }
    }

    /**
     * Called to modify original bean properties obtained from BeanInfo.
     * Properties may be added, removed etc. - due to specific needs.
     */
    protected void addMagicProperties() {
        Map<String, RADProperty<?>> magicProps = new HashMap<>();
        // Issue 171445 - missing cursor property
        if ((getBeanInstance() instanceof java.awt.Component) && (nameToProperty.get("cursor") == null)) { // NOI18N
            try {
                PropertyDescriptor pd = new PropertyDescriptor("cursor", java.awt.Component.class); // NOI18N
                RADProperty<?> prop = createBeanProperty(pd);
                if (prop != null) {
                    nameToProperty.put("cursor", prop); // NOI18N
                    magicProps.put(prop.getName(), prop);
                }
            } catch (IntrospectionException ex) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
            }
        }
        // hack for buttons - add fake property for ButtonGroup
        if ((getBeanInstance() instanceof javax.swing.JToggleButton && !(getBeanInstance() instanceof javax.swing.JCheckBox))
                || getBeanInstance() instanceof javax.swing.JRadioButton
                || getBeanInstance() instanceof javax.swing.JRadioButtonMenuItem) {
            try {
                ButtonGroupProperty prop = new ButtonGroupProperty(this);
                prop.addPropertyChangeListener(propertyListener);
                nameToProperty.put(prop.getName(), prop);
                magicProps.put(prop.getName(), prop);
            } catch (InvocationTargetException | IllegalAccessException | IntrospectionException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }

        if (getBeanInstance() instanceof javax.swing.text.JTextComponent) {
            try {
                PropertyDescriptor pd = new FakePropertyDescriptor("emptyText", String.class); // NOI18N
                RADProperty<?> prop = createBeanProperty(pd);
                if (prop != null) {
                    nameToProperty.put("emptyText", prop); // NOI18N
                    magicProps.put(prop.getName(), prop);
                }
            } catch (IntrospectionException ex) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
            }
        }
        propsByCategories.entrySet().stream().forEach((Map.Entry<String, RADProperty<?>[]> aEntry) -> {
            for (int i = 0; i < aEntry.getValue().length; i++) {
                if (magicProps.containsKey(aEntry.getValue()[i].getName())) {
                    aEntry.getValue()[i] = magicProps.get(aEntry.getValue()[i].getName());
                }
            }
        });
    }

    public RADProperty<?>[] getBeanProperties() {
        if (nameToProperty == null) {
            createBeanProperties();
        }
        return nameToProperty.values().toArray(new RADProperty<?>[]{});
    }

    /**
     * Listener class for listening to changes in component's properties.
     */
    private class PropertyChangesPropagator implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Object source = evt.getSource();
            if (source instanceof FormProperty<?>) {
                FormProperty<?> property = (FormProperty<?>) source;
                String propName = property.getName();
                String eventName = evt.getPropertyName();
                switch (eventName) {
                    case FormProperty.PROP_VALUE:
                        // property value has settedToDefault (or value and editor together)
                        Object oldValue = evt.getOldValue();
                        Object newValue = evt.getNewValue();
                        formModel.fireComponentPropertyChanged(
                                RADComponent.this, propName, oldValue, newValue);
                        if (getNodeReference() != null) { // propagate the change to node
                            getNodeReference().firePropertyChangeHelper(propName, oldValue, newValue);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", name: " + getName() + ", class: " + getBeanClass() + ", beaninfo: " + getBeanInfo() + ", instance: " + getBeanInstance(); // NOI18N
    }

    static class EntityJsObjectProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EntityJSObjectEditor();
            
        
        EntityJsObjectProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    static class ValueTypeProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EnumEditor(new Object[]{
            FormUtils.getBundleString("CTL_RegExp"), VFormattedField.REGEXP, "REGEXP", FormUtils.getBundleString("CTL_Mask"), VFormattedField.MASK, "MASK", FormUtils.getBundleString("CTL_Number"), VFormattedField.NUMBER, "NUMBER", FormUtils.getBundleString("CTL_Percent"), VFormattedField.PERCENT, "PERCENT", FormUtils.getBundleString("CTL_DateTime"), VFormattedField.DATE, "DATE", FormUtils.getBundleString("CTL_Currency"), VFormattedField.CURRENCY, "CURRENCY"
        });

        ValueTypeProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    public static class OrientationProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EnumEditor(new Object[]{
            FormUtils.getBundleString("CTL_Horizontal"), Orientation.HORIZONTAL, "HORIZONTAL", FormUtils.getBundleString("CTL_Vertical"), Orientation.VERTICAL, "VERTICAL"
        });

        OrientationProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    static class HorizontalPositionProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EnumEditor(new Object[]{
            FormUtils.getBundleString("CTL_Left"), HorizontalPosition.LEFT, "LEFT", FormUtils.getBundleString("CTL_Center"), HorizontalPosition.CENTER, "CENTER", FormUtils.getBundleString("CTL_Right"), HorizontalPosition.RIGHT, "RIGHT"
        });

        HorizontalPositionProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    static class VerticalPositionProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EnumEditor(new Object[]{
            FormUtils.getBundleString("CTL_Top"), VerticalPosition.TOP, "TOP", FormUtils.getBundleString("CTL_Center"), VerticalPosition.CENTER, "CENTER", FormUtils.getBundleString("CTL_Bottom"), VerticalPosition.BOTTOM, "BOTTOM"
        });

        VerticalPositionProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    static class HorizontalScrollPolicyProperty extends RADProperty<Integer> {

        private PropertyEditor editor = new EnumEditor(new Object[]{
            FormUtils.getBundleString("CTL_Always"), JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS, "HORIZONTAL_SCROLLBAR_ALWAYS", FormUtils.getBundleString("CTL_Never"), JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, "HORIZONTAL_SCROLLBAR_NEVER", FormUtils.getBundleString("CTL_Auto"), JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED, "HORIZONTAL_SCROLLBAR_AS_NEEDED"
        });

        HorizontalScrollPolicyProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

    }

    static class IconProperty extends RADProperty<IconEditor.NbImageIcon> {

//        protected IconEditor.NbImageIcon value;

        IconProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public IconEditor.NbImageIcon getDefaultValue() {
            return null;
        }
/*
        @Override
        public IconEditor.NbImageIcon getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(IconEditor.NbImageIcon aValue) throws IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException {
            IconEditor.NbImageIcon oldValue = getValue();
            value = aValue;
            getPropertyDescriptor().getWriteMethod().invoke(getComponent().getBeanInstance(), new Object[]{value});
            propertyValueChanged(oldValue, value);
        }
        */
    }

    public static class ComponentRef<C> implements ComponentReference<C> {

        private final FormModel formModel;
        private String componentName;
        private RADComponent<C> component;

        public ComponentRef(String name, FormModel aFormModel) {
            super();
            componentName = name;
            formModel = aFormModel;
        }

        public ComponentRef(RADComponent<C> radComp, FormModel aFormModel) {
            super();
            componentName = radComp.getName();
            component = radComp;
            formModel = aFormModel;
        }

        @Override
        public boolean equals(Object obj) {
            boolean equal;

            if (obj instanceof ComponentRef<?>) {
                ComponentRef<C> ref = (ComponentRef<C>) obj;

                equal = (ref.component == component);
                if (componentName == null) {
                    equal = equal && (ref.componentName == null);
                } else {
                    equal = equal && componentName.equals(ref.componentName);
                }
            } else {
                equal = (obj instanceof RADComponent<?> && obj == component);
            }

            return equal;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + (this.componentName != null ? this.componentName.hashCode() : 0);
            hash = 89 * hash + (this.component != null ? this.component.hashCode() : 0);
            return hash;
        }

        public String getJavaInitString() {
            checkComponent();
            if (component != null) {
                if (component == component.getFormModel().getTopRADComponent()) {
                    return "this"; // NOI18N
                }
            }
            return componentName;
        }

        @Override
        public RADComponent<C> getComponent() {
            checkComponent();
            return component;
        }

        private void checkComponent() {
            if (component == null) {
                component = (RADComponent<C>) formModel.getRADComponent(componentName);
            } else {
                if (!component.isInModel()) {
                    component = null;
                } else {
                    componentName = component.getName();
                }
            }
        }
    }

    static class ComponentProperty extends RADProperty<ComponentReference<Component>> {

        protected ComponentReference<Component> value;

        ComponentProperty(RADComponent<?> aRadComp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(aRadComp, aDesc);
            Object nativeValue = aDesc.getReadMethod().invoke(aRadComp.getBeanInstance());
            if (nativeValue instanceof Component) {
                value = new ComponentRef(((Component) nativeValue).getName(), aRadComp.getFormModel());
            }
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public ComponentReference<Component> getDefaultValue() {
            return null;
        }

        @Override
        public ComponentReference<Component> getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(ComponentReference<Component> aValue) throws IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException {
            ComponentReference<Component> oldValue = getValue();
            value = aValue;
            if (oldValue != value) {
                Object innerValue = null;
                if (value != null && value.getComponent() != null && canWrite()) {
                    innerValue = value.getComponent().getBeanInstance();
                }
                getPropertyDescriptor().getWriteMethod().invoke(getComponent().getBeanInstance(), new Object[]{innerValue});
                propertyValueChanged(oldValue, value);
            }
        }
    }
    // Some hacks for ButtonGroup "component" ...
    // pseudo-property for buttons - holds ButtonGroup in which button
    // is placed; kind of "reversed" property

    static class ButtonGroupProperty extends RADProperty<ComponentReference<ButtonGroup>> {

        protected ComponentReference<ButtonGroup> value;

        ButtonGroupProperty(RADComponent<?> aRadComp) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
            super(aRadComp,
                    new FakePropertyDescriptor("buttonGroup", // NOI18N
                            ButtonGroup.class));
            setAccessType(DETACHED_READ | DETACHED_WRITE);
            setShortDescription(FormUtils.getBundleString("HINT_ButtonGroup")); // NOI18N
            ButtonGroup nativeValue = ((HasGroup) aRadComp.getBeanInstance()).getButtonGroup();
            value = nativeValue != null ? new ComponentRef(((Component) nativeValue).getName(), aRadComp.getFormModel()) : null;
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public ComponentReference<ButtonGroup> getDefaultValue() {
            return null;
        }

        // add/removes AbtractButton components to/from ButtonGroup component now or later
        private void synchronizeButtonGroup(final AbstractButton button,
                final ButtonGroup originalGroup,
                final ButtonGroup newGroup) {
            synchronizeButtonGroupInAWT(button,
                    originalGroup,
                    newGroup);
        }

        // add/removes AbtractButton components to/from RadioGroup component
        private void synchronizeButtonGroupInAWT(AbstractButton button,
                ButtonGroup originalGroup,
                ButtonGroup newGroup) {
            if (originalGroup != null) {
                //remove button from original buttongroup
                originalGroup.remove(button);
            }
            if (newGroup != null) {
                // add button to new buttongroup
                newGroup.add(button);
            }
        }

        @Override
        public ComponentReference<ButtonGroup> getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(ComponentReference<ButtonGroup> aValue) throws IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException {
            ComponentReference<ButtonGroup> oldValue = getValue();
            value = aValue;
            if (oldValue != value) {
                ButtonGroup oldGroup = null;
                if (oldValue != null && oldValue.getComponent() != null) {
                    oldGroup = oldValue.getComponent().getBeanInstance();
                }
                ButtonGroup newGroup = null;
                if (value != null && value.getComponent() != null) {
                    newGroup = value.getComponent().getBeanInstance();
                }
                if (oldGroup != newGroup) {
                    // get swing abstractbutton component
                    AbstractButton button = (AbstractButton) getRADComponent().getBeanInstance();
                    // add/remove button from buttongroup
                    // note: using "getValue()" instead of "value", because setValue()
                    //       handles more different types (eg.FormProperty.ValueWithEditor)
                    synchronizeButtonGroup(button, oldGroup, newGroup);
                }
                propertyValueChanged(oldValue, value);
            }
        }
    }
    /*
     // property editor for selecting ButtonGroup (for ButtonGroupProperty)
     public static class ButtonGroupPropertyEditor extends ComponentChooserEditor {

     public ButtonGroupPropertyEditor() {
     super();
     setBeanTypes(new Class<?>[]{ButtonGroup.class});
     setComponentCategory(NONVISUAL_COMPONENTS);
     }

     @Override
     public String getDisplayName() {
     return NbBundle.getMessage(getClass(), "CTL_ButtonGroupPropertyEditor_DisplayName"); // NOI18N
     }
     }
     */

    public void setValid(boolean aValue) {
        valid = aValue;
    }

    protected boolean isValid() {
        return valid;
    }

    private class FakeBeanInfo extends SimpleBeanInfo {

        private final List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();

        @Override
        public BeanDescriptor getBeanDescriptor() {
            return (beanInfo == this) ? new BeanDescriptor(beanClass) : beanInfo.getBeanDescriptor();
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
        }

        @Override
        public EventSetDescriptor[] getEventSetDescriptors() {
            return new EventSetDescriptor[0];
        }

        void addPropertyDescriptor(String propertyName, Class<?> propertyClass) {
            try {
                propertyDescriptors.add(new FakePropertyDescriptor(propertyName, propertyClass));
            } catch (IntrospectionException ex) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex); // should not happen
            }
        }

        void addPropertyDescriptor(PropertyDescriptor pd) {
            propertyDescriptors.add(pd);
        }

        void removePropertyDescriptors() {
            propertyDescriptors.clear();
        }
    }
}
