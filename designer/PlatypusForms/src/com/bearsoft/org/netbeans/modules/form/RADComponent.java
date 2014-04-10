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

import static com.bearsoft.org.netbeans.modules.form.FormProperty.DETACHED_READ;
import static com.bearsoft.org.netbeans.modules.form.FormProperty.DETACHED_WRITE;
import com.bearsoft.org.netbeans.modules.form.RADProperty.FakePropertyDescriptor;
import com.bearsoft.org.netbeans.modules.form.bound.ModelControlListener;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.editors.NbBorder;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.datatransfer.NewType;

/**
 *
 * @author Ian Formanek
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
    private RADProperty<?>[] beanProperties1;
    private RADProperty<?>[] beanProperties2;
    private Map<String, RADProperty<?>[]> otherProperties;
    private List<RADProperty<?>> actionProperties;
    private RADProperty<?>[] knownBeanProperties;
    private PropertyChangeListener propertyListener;
    protected Map<String, FormProperty<?>> nameToProperty;
    private ComponentContainer parent;
    private FormModel formModel;
    private boolean inModel;
    private RADComponentNode componentNode;
    private String storedName; // component name preserved e.g. for remove undo
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

    public void setInModel(boolean in) {
        if (inModel != in) {
            inModel = in;
            if (in) {
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
        FormUtils.copyPropertiesToBean(getKnownBeanProperties(),
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

    public RADProperty<?>[] getAllBeanProperties() {
        if (knownBeanProperties == null) {
            createBeanProperties();
        }
        return knownBeanProperties;
    }

    public RADProperty<?>[] getKnownBeanProperties() {
        return knownBeanProperties != null ? knownBeanProperties : NO_PROPERTIES;
    }

    public List<RADProperty<?>> getBeanProperties(FormProperty.Filter filter, boolean fromAll) {
        List<RADProperty<?>> filtered = new ArrayList<>();
        RADProperty<?>[] toFilter = fromAll ? getAllBeanProperties() : getKnownBeanProperties();
        for (RADProperty<?> subject : toFilter) {
            if (filter.accept(subject)) {
                filtered.add(subject);
            }
        }
        return filtered;
    }

    /**
     * Provides access to the Node which represents this RADComponent
     *
     * @return the RADComponentNode which represents this RADComponent
     */
    public RADComponentNode getNodeReference() {
        return componentNode;
    }

    RADProperty<?>[] getBeanProperties1() {
        if (beanProperties1 == null) {
            createBeanProperties();
        }
        return beanProperties1;
    }

    RADProperty<?>[] getBeanProperties2() {
        if (beanProperties2 == null) {
            createBeanProperties();
        }
        return beanProperties2;
    }

    List<RADProperty<?>> getActionProperties() {
        if (actionProperties == null) {
            createBeanProperties();
        }
        return actionProperties;
    }

    protected <T extends FormProperty<?>> T getPropertyByName(String name, Class<? extends T> propertyType, boolean fromAll) {
        FormProperty<?> prop = nameToProperty.get(name);
        if (prop == null && fromAll) {
            if (beanProperties1 == null && !name.startsWith("$")) // NOI18N
            {
                createBeanProperties();
            }
            prop = nameToProperty.get(name);
        }
        return prop != null
                && (propertyType == null
                || propertyType.isAssignableFrom(prop.getClass()))
                ? (T) prop : null;
    }

    /**
     * Returns property of given name corresponding to a property or event.
     * Forces creation of all property objects.
     *
     * @param name name of the property.
     * @return bean or event property
     */
    public <P> P getProperty(String name) {
        return (P) getPropertyByName(name, null, true);
    }

    public final <P> P getRADProperty(String name) {
        return (P) getPropertyByName(name, RADProperty.class, true);
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
        RADProperty<?>[] properties = new RADProperty<?>[propNames.length];
        PropertyDescriptor[] descriptors = null;

        boolean empty = knownBeanProperties == null;
        int validCount = 0;
        List<RADProperty<?>> newProps = null;
        Object[] propAccessClsf = null;
        Object[] propParentChildDepClsf = null;

        int descIndex = 0;
        for (int i = 0; i < propNames.length; i++) {
            String name = propNames[i];
            if (name == null) {
                continue;
            }

            RADProperty<?> prop;
            if (!empty) {
                Object obj = nameToProperty.get(name);
                prop = obj instanceof RADProperty<?> ? (RADProperty<?>) obj : null;
            } else {
                prop = null;
            }

            if (prop == null) {
                if (descriptors == null) {
                    descriptors = getBeanInfo().getPropertyDescriptors();
                    if (descriptors.length == 0) {
                        break;
                    }
                }
                int j = descIndex;
                do {
                    if (descriptors[j].getName().equals(name)) {
                        if (propAccessClsf == null) {
                            propAccessClsf = FormUtils.getPropertiesAccessClsf(beanClass);
                            propParentChildDepClsf = FormUtils.getPropertiesParentChildDepsClsf(beanClass);
                        }

                        prop = createBeanProperty(descriptors[j], propAccessClsf, propParentChildDepClsf);

                        if (!empty) {
                            if (newProps == null) {
                                newProps = new ArrayList<>();
                            }
                            newProps.add(prop);
                        }
                        descIndex = j + 1;
                        if (descIndex == descriptors.length
                                && i + 1 < propNames.length) {
                            descIndex = 0; // go again from beginning
                        }
                        break;
                    }
                    j++;
                    if (j == descriptors.length) {
                        j = 0;
                    }
                } while (j != descIndex);
            }
            if (prop != null) {
                properties[i] = prop;
                validCount++;
            } else { // force all properties creation, there might be more
                // properties than from BeanInfo [currently just ButtonGroup]
                properties[i] = getPropertyByName(name, RADProperty.class, true);
                empty = false;
                newProps = null;
            }
        }

        if (empty) {
            if (validCount == properties.length) {
                knownBeanProperties = properties;
            } else if (validCount > 0) {
                knownBeanProperties = new RADProperty<?>[validCount];
                for (int i = 0, j = 0; i < properties.length; i++) {
                    if (properties[i] != null) {
                        knownBeanProperties[j++] = properties[i];
                    }
                }
            }
        } else if (newProps != null) { // just for consistency, should not occur
            RADProperty<?>[] knownProps =
                    new RADProperty<?>[knownBeanProperties.length + newProps.size()];
            System.arraycopy(knownBeanProperties, 0,
                    knownProps, 0,
                    knownBeanProperties.length);
            for (int i = 0; i < newProps.size(); i++) {
                knownProps[i + knownBeanProperties.length] = newProps.get(i);
            }

            knownBeanProperties = knownProps;
        }

        return properties;
    }
    
    // -----------------------------------------------------------------------------
    // Properties
    protected void clearProperties() {
        if (nameToProperty != null) {
            nameToProperty.clear();
        } else {
            nameToProperty = new HashMap<>();
        }
        propertySets = null;
        beanProperties1 = null;
        beanProperties2 = null;
        knownBeanProperties = null;
    }
    static final boolean SUPPRESS_PROPERTY_TABS = Boolean.getBoolean(
            "nb.form.suppressTabs");

    protected void createPropertySets(List<Node.PropertySet> propSets) {
        if (beanProperties1 == null) {
            createBeanProperties();
        }

        ResourceBundle bundle = FormUtils.getBundle();

        Node.PropertySet ps;
        propSets.add(new Node.PropertySet(
                "properties", // NOI18N
                bundle.getString("CTL_general"), // NOI18N
                bundle.getString("CTL_generalHint")) // NOI18N
        {
            @Override
            public FormProperty<?>[] getProperties() {
                return getBeanProperties1();
            }
        });

        if (!SUPPRESS_PROPERTY_TABS) {
            if (isValid()) {
                for (Map.Entry<String, RADProperty<?>[]> entry : otherProperties.entrySet()) {
                    final String category = entry.getKey();
                    ps = new Node.PropertySet(category, bundle.getString("CTL_" + category), bundle.getString("CTL_" + category + "Hint")) {
                        @Override
                        public FormProperty<?>[] getProperties() {
                            if (otherProperties == null) {
                                createBeanProperties();
                            }
                            return otherProperties.get(category);
                        }
                    };
                    propSets.add(ps);
                }

                if (beanProperties2.length > 0) {
                    propSets.add(new Node.PropertySet(
                            "properties2", // NOI18N
                            bundle.getString("CTL_misc"), // NOI18N
                            bundle.getString("CTL_miscHint")) // NOI18N
                    {
                        @Override
                        public FormProperty<?>[] getProperties() {
                            return getBeanProperties2();
                        }
                    });
                }
            }
        }
    }

    protected void createBeanProperties() {
        List<FormProperty<?>> prefProps = new ArrayList<>();
        List<FormProperty<?>> normalProps = new ArrayList<>();
        List<FormProperty<?>> expertProps = new ArrayList<>();
        Map<String, List<FormProperty<?>>> otherProps = new TreeMap<>();
        List<RADProperty<?>> actionProps = new ArrayList<>();

        Object[] propsCats = FormUtils.getPropertiesCategoryClsf(beanClass, getBeanInfo().getBeanDescriptor());
        PropertyDescriptor[] props = getBeanInfo().getPropertyDescriptors();
        if (propsCats != null && Utilities.isMac() && beanClass.getClassLoader() == null) {
            try {
                Object[] newPropsCats = new Object[propsCats.length + 2 * props.length];
                Map<String, PropertyDescriptor> oldProps = new HashMap<>();
                for (int i = 0; i < props.length; i++) {
                    PropertyDescriptor pd = props[i];
                    String name = pd.getName();
                    oldProps.put(name, pd);
                    newPropsCats[2 * i] = name;
                    Object cat = FormUtils.PROP_NORMAL;
                    if (pd.isPreferred()) {
                        cat = FormUtils.PROP_PREFERRED;
                    }
                    if (pd.isExpert()) {
                        cat = FormUtils.PROP_EXPERT;
                    }
                    if (pd.isHidden()) {
                        cat = FormUtils.PROP_HIDDEN;
                    }
                    newPropsCats[2 * i + 1] = cat;
                }
                System.arraycopy(propsCats, 0, newPropsCats, 2 * props.length, propsCats.length);
                propsCats = newPropsCats;
                props = FormUtils.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO).getPropertyDescriptors();
                for (PropertyDescriptor pd : props) {
                    PropertyDescriptor oldPD = oldProps.get(pd.getName());
                    if (oldPD != null) {
                        Enumeration<String> enumeration = oldPD.attributeNames();
                        while (enumeration.hasMoreElements()) {
                            String attr = enumeration.nextElement();
                            pd.setValue(attr, oldPD.getValue(attr));
                        }
                    }
                }
            } catch (IntrospectionException iex) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, iex.getMessage(), iex);
            }
        }

        Object[] propsAccess = FormUtils.getPropertiesAccessClsf(beanClass);
        Object[] propParentChildDepClsf = FormUtils.getPropertiesParentChildDepsClsf(beanClass);

        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor pd = props[i];
            if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                continue;
            }
            boolean action = (pd.getValue("action") != null); // NOI18N
            String category = (String) pd.getValue("category"); // NOI18N
            Designable ann = null;
            if (pd.getReadMethod() != null && pd.getReadMethod().getAnnotation(Designable.class) != null) {
                ann = pd.getReadMethod().getAnnotation(Designable.class);
            }
            if (pd.getWriteMethod() != null && pd.getWriteMethod().getAnnotation(Designable.class) != null) {
                ann = pd.getWriteMethod().getAnnotation(Designable.class);
            }
            if (ann != null && ann.category() != null && !ann.category().isEmpty()) {
                category = ann.category();
            }

            List<FormProperty<?>> listToAdd;

            if (category == null || !(category instanceof String)) {
                Object propCat = FormUtils.getPropertyCategory(pd, propsCats);
                if (propCat == FormUtils.PROP_PREFERRED) {
                    listToAdd = prefProps;
                } else if (propCat == FormUtils.PROP_NORMAL) {
                    listToAdd = normalProps;
                } else if (propCat == FormUtils.PROP_EXPERT) {
                    listToAdd = expertProps;
                } else {
                    continue; // PROP_HIDDEN
                }
            } else {
                listToAdd = otherProps.get(category);
                if (listToAdd == null) {
                    listToAdd = new ArrayList<>();
                    otherProps.put(category, listToAdd);
                }
            }

            RADProperty<?> prop = getPropertyByName(pd.getName(), RADProperty.class, false);
            if (prop == null) {
                prop = createBeanProperty(pd, propsAccess, propParentChildDepClsf);
            }

            if (prop != null) {
                listToAdd.add(prop);
                if ("action".equals(pd.getName()) && (listToAdd == prefProps) && javax.swing.Action.class.isAssignableFrom(pd.getPropertyType())) { // NOI18N
                    action = true;
                    prop.setValue("actionName", FormUtils.getBundleString("CTL_SetAction")); // NOI18N
                }
                if (action) {
                    Object actionName = pd.getValue("actionName"); // NOI18N
                    if (actionName instanceof String) {
                        prop.setValue("actionName", actionName); // NOI18N
                    }
                    actionProps.add(prop);
                }
            }
        }

        changePropertiesExplicitly(prefProps, normalProps, expertProps);

        int prefCount = prefProps.size();
        int normalCount = normalProps.size();
        int expertCount = expertProps.size();
        int otherCount = 0;

        if (prefCount > 0) {
            beanProperties1 = new RADProperty<?>[prefCount];
            prefProps.toArray(beanProperties1);
            if (normalCount + expertCount > 0) {
                normalProps.addAll(expertProps);
                beanProperties2 = new RADProperty<?>[normalCount + expertCount];
                normalProps.toArray(beanProperties2);
            } else {
                beanProperties2 = new RADProperty<?>[]{};
            }
        } else {
            beanProperties1 = new RADProperty<?>[normalCount];
            normalProps.toArray(beanProperties1);
            if (expertCount > 0) {
                beanProperties2 = new RADProperty<?>[expertCount];
                expertProps.toArray(beanProperties2);
            } else {
                beanProperties2 = new RADProperty<?>[]{};
            }
        }

        Map<String, RADProperty<?>[]> otherProps2 = new TreeMap<>();
        RADProperty<?>[] array = new RADProperty<?>[]{};
        for (Map.Entry<String, List<FormProperty<?>>> entry : otherProps.entrySet()) {
            List<FormProperty<?>> catProps = entry.getValue();
            otherCount += catProps.size();
            otherProps2.put(entry.getKey(), catProps.toArray(array));
        }
        otherProperties = otherProps2;

        FormUtils.reorderProperties(beanClass, beanProperties1);
        FormUtils.reorderProperties(beanClass, beanProperties2);

        knownBeanProperties = new RADProperty<?>[beanProperties1.length
                + beanProperties2.length + otherCount];
        System.arraycopy(beanProperties1, 0,
                knownBeanProperties, 0,
                beanProperties1.length);
        System.arraycopy(beanProperties2, 0,
                knownBeanProperties, beanProperties1.length,
                beanProperties2.length);

        int where = beanProperties1.length + beanProperties2.length;

        for (Map.Entry<String, RADProperty<?>[]> entry : otherProperties.entrySet()) {
            RADProperty<?>[] catProps = entry.getValue();
            System.arraycopy(catProps, 0,
                    knownBeanProperties, where,
                    catProps.length);
            where += catProps.length;
        }

        actionProperties = actionProps;
    }

    protected EventSetDescriptor[] getEventSetDescriptors() {
        return getBeanInfo().getEventSetDescriptors();
    }

    protected RADProperty<?> createBeanProperty(PropertyDescriptor desc,
            Object[] propAccessClsf,
            Object[] propParentChildDepClsf) {
        if (desc.getPropertyType() == null || (desc.getReadMethod() != null && desc.getReadMethod().getAnnotation(Undesignable.class) != null)
                || (desc.getWriteMethod() != null && desc.getWriteMethod().getAnnotation(Undesignable.class) != null)) {
            return null;
        }

        RADProperty<?> prop = null;
        try {
            if (desc instanceof FakePropertyDescriptor) {
                prop = new FakeRADProperty<>(this, (FakePropertyDescriptor) desc);
            } else {
                if (java.awt.Component.class.isAssignableFrom(desc.getPropertyType())
                        && !(ButtonGroup.class.isAssignableFrom(desc.getPropertyType()))) {
                    prop = new ComponentProperty(this, desc);
                } else if (javax.swing.border.Border.class.isAssignableFrom(desc.getPropertyType())) {
                    prop = new BorderProperty(this, desc);
                } else if (javax.swing.Icon.class.isAssignableFrom(desc.getPropertyType()) || java.awt.Image.class.isAssignableFrom(desc.getPropertyType())) {
                    prop = new IconProperty(this, desc);
                } else if (javax.swing.JFormattedTextField.AbstractFormatterFactory.class.isAssignableFrom(desc.getPropertyType())) {
                    prop = new FormatterFactoryProperty(this, desc);
                } else {
                    prop = new RADProperty<>(this, desc);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException ex) { // should not happen
            Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
            return null;
        }

        int access = FormUtils.getPropertyAccess(desc, propAccessClsf);
        if (access != 0) {
            prop.setAccessType(access);
        }

        String parentChildDependency = FormUtils.getPropertyParentChildDependency(
                desc, propParentChildDepClsf);
        if (parentChildDependency != null) {
            prop.setValue(parentChildDependency, Boolean.TRUE);
        }
        Designable ann = null;
        if (desc.getReadMethod() != null && desc.getReadMethod().getAnnotation(Designable.class) != null) {
            ann = desc.getReadMethod().getAnnotation(Designable.class);
        } else if (desc.getWriteMethod() != null && desc.getWriteMethod().getAnnotation(Designable.class) != null) {
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
        setPropertyListener(prop);
        nameToProperty.put(desc.getName(), prop);
        return prop;
    }

    /**
     * Called to modify original bean properties obtained from BeanInfo.
     * Properties may be added, removed etc. - due to specific needs.
     *
     * @param prefProps preferred properties.
     * @param normalProps normal properties.
     * @param expertProps expert properties.
     */
    protected void changePropertiesExplicitly(List<FormProperty<?>> prefProps,
            List<FormProperty<?>> normalProps,
            List<FormProperty<?>> expertProps) {
        // Issue 171445 - missing cursor property
        if ((getBeanInstance() instanceof java.awt.Component) && (nameToProperty.get("cursor") == null)) { // NOI18N
            try {
                PropertyDescriptor pd = new PropertyDescriptor("cursor", java.awt.Component.class); // NOI18N
                RADProperty<?> prop = createBeanProperty(pd, null, null);
                if (prop != null) {
                    nameToProperty.put("cursor", prop); // NOI18N
                    normalProps.add(prop);
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
                setPropertyListener(prop);
                nameToProperty.put(prop.getName(), prop);

                Object propCategory = FormUtils.getPropertyCategory(
                        prop.getPropertyDescriptor(),
                        FormUtils.getPropertiesCategoryClsf(
                        beanClass, getBeanInfo().getBeanDescriptor()));

                if (propCategory == FormUtils.PROP_PREFERRED) {
                    prefProps.add(prop);
                } else {
                    normalProps.add(prop);
                }
            } catch (InvocationTargetException | IllegalAccessException | IntrospectionException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        
        if(getBeanInstance() instanceof javax.swing.text.JTextComponent){
            try {
                PropertyDescriptor pd = new FakePropertyDescriptor("emptyText", String.class); // NOI18N
                RADProperty<?> prop = createBeanProperty(pd, null, null);
                if (prop != null) {
                    nameToProperty.put("emptyText", prop); // NOI18N
                    normalProps.add(prop);
                }
            } catch (IntrospectionException ex) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, ex.getMessage(), ex);
            }
        }

        // PENDING improve performance - keep lookup result, listen on it etc.
        boolean modified = false;
        Lookup.Template<PropertyModifier> template = new Lookup.Template<>(PropertyModifier.class);
        Collection<? extends PropertyModifier> modifiers = Lookup.getDefault().lookup(template).allInstances();
        for (PropertyModifier modifier : modifiers) {
            modified |= modifier.modifyProperties(this, prefProps, normalProps, expertProps);
        }

        if (modified) {
            checkForAddedProperties(prefProps);
            checkForAddedProperties(normalProps);
            checkForAddedProperties(expertProps);
        }
    }

    private void checkForAddedProperties(List<FormProperty<?>> props) {
        for (FormProperty<?> prop : props) {
            String propName = prop.getName();
            if (!nameToProperty.containsKey(propName)) {
                nameToProperty.put(propName, prop);
                setPropertyListener(prop);
            }
        }
    }

    protected PropertyChangeListener createPropertyListener() {
        return new PropertyChangesPropagator();
    }

    public void setPropertyListener(FormProperty<?> property) {
        if (propertyListener == null) {
            propertyListener = createPropertyListener();
        }
        if (propertyListener != null) {
            property.addPropertyChangeListener(propertyListener);
        }
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
                        // property value has changed (or value and editor together)
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

    static class BorderProperty extends RADProperty<NbBorder> {

        protected NbBorder value;

        BorderProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public NbBorder getDefaultValue() {
            return null;
        }

        @Override
        public NbBorder getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(NbBorder aValue) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            NbBorder oldValue = getValue();
            value = aValue;
            if (oldValue != value) {
                setChanged(value != getDefaultValue());
                Object innerValue = null;
                if (value != null && canWrite()) {
                    innerValue = value.toBorder();
                }
                getPropertyDescriptor().getWriteMethod().invoke(getComponent().getBeanInstance(), new Object[]{innerValue});
                propertyValueChanged(oldValue, value);
            }
        }
    }

    public static class FormatterFactoryProperty extends RADProperty<AbstractFormatterFactoryEditor.FormFormatter> {

        public static final String FORMATTER_FACTORY_PROP_NAME = "formatterFactory";
        protected AbstractFormatterFactoryEditor.FormFormatter value;

        FormatterFactoryProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
            setDisplayName("format");
        }

        @Override
        public boolean supportsDefaultValue() {
            return true;
        }

        @Override
        public AbstractFormatterFactoryEditor.FormFormatter getDefaultValue() {
            return null;
        }

        @Override
        public AbstractFormatterFactoryEditor.FormFormatter getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(AbstractFormatterFactoryEditor.FormFormatter aValue) throws IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException {
            AbstractFormatterFactoryEditor.FormFormatter oldValue = getValue();
            value = aValue;
            if (oldValue != value) {
                setChanged(value != getDefaultValue());
                AbstractFormatterFactory innerValue = null;
                if (value != null && canWrite()) {
                    innerValue = value.constructFormatterFactory();
                }
                getPropertyDescriptor().getWriteMethod().invoke(getComponent().getBeanInstance(), new Object[]{innerValue});
                propertyValueChanged(oldValue, value);
            }
        }
    }

    static class IconProperty extends RADProperty<IconEditor.NbImageIcon> {

        protected IconEditor.NbImageIcon value;

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
            if (oldValue != value) {
                setChanged(value != getDefaultValue());
                Object innerValue = null;
                if (value != null && (value.getIcon() != null || value.getImage() != null) && canWrite()) {
                    if (java.awt.Image.class.isAssignableFrom(getPropertyDescriptor().getPropertyType())) {
                        innerValue = value.getImage();
                    } else {
                        innerValue = value.getIcon();
                    }
                }
                getPropertyDescriptor().getWriteMethod().invoke(getComponent().getBeanInstance(), new Object[]{innerValue});
                propertyValueChanged(oldValue, value);
            }
        }
    }

    static class ComponentProperty extends RADProperty<ComponentReference<Component>> {

        protected ComponentReference<Component> value;

        ComponentProperty(RADComponent<?> comp, PropertyDescriptor aDesc) throws IllegalAccessException, InvocationTargetException {
            super(comp, aDesc);
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
                setChanged(value != getDefaultValue());
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

        ButtonGroupProperty(RADComponent<?> comp) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
            super(comp,
                    new FakePropertyDescriptor("buttonGroup", // NOI18N
                    ButtonGroup.class));
            setAccessType(DETACHED_READ | DETACHED_WRITE);
            setShortDescription(FormUtils.getBundleString("HINT_ButtonGroup")); // NOI18N
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
                // try to find button inside buttongroup
                boolean add = true;
                for (Enumeration<AbstractButton> e = newGroup.getElements(); e.hasMoreElements();) {
                    if (button.equals(e.nextElement())) {
                        add = false;
                        break;
                    }
                }
                // button not found inside group, add it
                if (add) {
                    newGroup.add(button);
                }
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
                setChanged(value != getDefaultValue());
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

    // property editor for selecting ButtonGroup (for ButtonGroupProperty)
    public static class ButtonGroupPropertyEditor extends ComponentChooserEditor {

        public ButtonGroupPropertyEditor() {
            super();
            setBeanTypes(new Class<?>[]{javax.swing.ButtonGroup.class});
            setComponentCategory(NONVISUAL_COMPONENTS);
        }

        @Override
        public String getDisplayName() {
            return NbBundle.getMessage(getClass(), "CTL_ButtonGroupPropertyEditor_DisplayName"); // NOI18N
        }
    }

    public void setValid(boolean aValue) {
        valid = aValue;
    }

    protected boolean isValid() {
        return valid;
    }

    private class FakeBeanInfo extends SimpleBeanInfo {

        private List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();

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
