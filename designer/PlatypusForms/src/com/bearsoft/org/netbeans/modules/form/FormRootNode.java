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

import com.bearsoft.org.netbeans.modules.form.actions.*;
import com.eas.client.forms.Form;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.ScriptFunction;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.swing.Action;
import org.openide.actions.PasteAction;
import org.openide.actions.ReorderAction;
import org.openide.nodes.*;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;

/**
 * This class represents the root node of the form (displayed as root in
 * Component Inspector).
 *
 * @author Tomas Pavek
 */
public class FormRootNode extends FormNode {

    private Map<String, FormProperty<?>> propsByName;
    private List<PropertySet> propSets;
    private final PropertyChangeListener propsListener = (PropertyChangeEvent evt) -> {
        if (evt.getSource() instanceof FormRootProperty<?> && FormProperty.PROP_VALUE.equals(evt.getPropertyName())) {
            formModel.fireFormPropertyChanged(FormRootNode.this, ((FormRootProperty<?>)evt.getSource()).getName(), evt.getOldValue(), evt.getNewValue());
        }
    };

    public FormRootNode(FormModel aFormModel) {
        super(new RootChildren(aFormModel), aFormModel);
        setName("Form Root Node"); // NOI18N
        setIconBaseWithExtension("com/bearsoft/org/netbeans/modules/form/resources/formDesigner.gif"); // NOI18N
        updateName(formModel.getName());
    }

    public FormProperty<?> getProperty(String propertyName) {
        return propsByName.get(propertyName);
    }

    public FormProperty[] getFormProperties(){
        checkPropertiesSets();
        return propsByName.values().toArray(new FormProperty[]{});
    }
    
    @Override
    public PropertySet[] getPropertySets() {
        checkPropertiesSets();
        return propSets.toArray(new PropertySet[]{});
    }

    protected void checkPropertiesSets() {
        if (propSets == null) {
            try {
                propSets = new ArrayList<>();
                propsByName = new HashMap<>();
                Map<String, List<FormProperty<?>>> propsByCategory = new HashMap<>();
                BeanInfo bi = Introspector.getBeanInfo(Form.class, java.beans.Introspector.IGNORE_ALL_BEANINFO);
                for (PropertyDescriptor descriptor : bi.getPropertyDescriptors()) {
                    if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
                        Designable designable = descriptor.getReadMethod().getAnnotation(Designable.class);
                        ScriptFunction scriptFunction = descriptor.getReadMethod().getAnnotation(ScriptFunction.class);
                        if (designable == null) {
                            designable = descriptor.getWriteMethod().getAnnotation(Designable.class);
                        }
                        if (scriptFunction == null) {
                            scriptFunction = descriptor.getWriteMethod().getAnnotation(ScriptFunction.class);
                        }
                        if ((designable != null || scriptFunction != null)
                                && !descriptor.getReadMethod().isAnnotationPresent(Undesignable.class) && !descriptor.getWriteMethod().isAnnotationPresent(Undesignable.class)) {
                            String category = "general";
                            if (designable != null && designable.category() != null && !designable.category().isEmpty()) {
                                category = designable.category();
                            }
                            List<FormProperty<?>> catProps = propsByCategory.get(category);
                            if (catProps == null) {
                                catProps = new ArrayList<>();
                                propsByCategory.put(category, catProps);
                            }
                            FormProperty<?> prop = new FormRootProperty<>(formModel, descriptor);
                            prop.addPropertyChangeListener(propsListener);
                            catProps.add(prop);
                            propsByName.put(prop.getName(), prop);
                        }
                    }
                }
                final ResourceBundle bundle = FormUtils.getBundle();
                propsByCategory.entrySet().stream().forEach((Map.Entry<String, List<FormProperty<?>>> aEntry) -> {
                    final String category = aEntry.getKey();
                    final List<FormProperty<?>> props = aEntry.getValue();
                    if (props.size() > 0) {
                        propSets.add(new Node.PropertySet(category, bundle.getString("CTL_" + category), bundle.getString("CTL_" + category + "Hint")) {
                            @Override
                            public Node.Property<?>[] getProperties() {
                                return props.toArray(new Node.Property<?>[]{});
                            }
                        });
                    }
                });
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        if (actions == null) { // from AbstractNode
            List<Action> l = new ArrayList<>();
            if (isModifiableContainer()) {
                l.add(SystemAction.get(AddAction.class));
                l.add(null);
                l.add(SystemAction.get(PasteAction.class));
                l.add(null);
                l.add(SystemAction.get(ReorderAction.class));
                l.add(null);
            }
            l.add(null);
            l.addAll(Arrays.asList(super.getActions(context)));
            actions = l.toArray(new Action[l.size()]);
        }
        return actions;
    }

    void updateName(String name) {
        setDisplayName(FormUtils.getFormattedBundleString("FMT_FormNodeName", // NOI18N
                new Object[]{name}));
    }

    FormOthersNode getOthersNode() {
        return ((RootChildren) getChildren()).othersNode;
    }

    @Override
    protected void createPasteTypes(Transferable t, java.util.List<PasteType> s) {
        if (isModifiableContainer()) {
            CopySupport.createPasteTypes(t, s, formModel, null);
        }
    }

    /**
     * Returns whether "other components" can be added under this node (i.e.
     * there is no Other Components node, the components appear directly under
     * root node).
     */
    private boolean isModifiableContainer() {
        return !formModel.isReadOnly() && !shouldHaveOthersNode(formModel);
    }

    /**
     * Returns true if the Other Components node should be used, or false if all
     * the "other" components should be shown directly under the root node. The
     * latter is the case when the root component either does not exists (the
     * form class extends Object) or if it is not a visual container. Here all
     * the components can be presented on the same level. OTOH if the root
     * component is a visual container (e.g. extends JPanel or JFrame), then it
     * has its hierarchy (the node can be expanded) and it seems better to have
     * the other components presented separately under Other Components node.
     */
    private static boolean shouldHaveOthersNode(FormModel formModel) {
        return formModel.getTopRADComponent() instanceof RADVisualContainer<?>;
    }

    // ----------------
    /**
     * The children nodes of the root node can have 3 variants:
     */
    static class RootChildren extends FormNodeChildren {

        static final RADVisualContainer<?> OTHERS_ROOT = new RADVisualContainer<>();
        private final FormModel formModel;
        private FormOthersNode othersNode;

        protected RootChildren(FormModel aFormModel) {
            super();
            formModel = aFormModel;
            updateKeys();
        }

        // FormNodeChildren implementation
        @Override
        protected void updateKeys() {
            othersNode = null;

            List<RADComponent<?>> keys = new ArrayList<>();
            boolean otherComps = shouldHaveOthersNode(formModel);
            if (otherComps) {
                keys.add(OTHERS_ROOT);
            }
            RADComponent<?> rootComp = formModel.getTopRADComponent();
            if (rootComp != null) {
                keys.add(rootComp);
            }
            if (!otherComps) {
                keys.addAll(formModel.getOtherComponents());
            }
            setKeys(keys.toArray(new RADComponent<?>[]{}));
        }

        @Override
        protected Node[] createNodes(RADComponent<?> key) {
            Node node;
            if (key == OTHERS_ROOT) {
                node = othersNode = new FormOthersNode(formModel);
            } else {
                assert key instanceof RADVisualComponent<?>;
                node = new RADComponentNode((RADVisualComponent<?>) key);
                key.setNodeReference((RADComponentNode) node);
            }
            node.getChildren().getNodes(); // enforce subnodes creation
            return new Node[]{node};
        }

        protected final FormModel getFormModel() {
            return formModel;
        }
    }
}
