/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Marat
 */
public class FocusPolicyProperty extends JETAProperty {

    public FocusPolicyProperty() {
        super(PROP_NAME);
    }

    public FocusTraversalPolicy getDelegate() {
        return m_value;
    }

    public JETAFocusTraversalPolicy getJETADelegate() {
        return m_value;
    }

    public class JETAFocusTraversalPolicy extends FocusTraversalPolicy {

        public static final String LIST_PROP_NAME = "componentsList";
        private LinkedList<ComponentRefProperty> elements = new LinkedList<ComponentRefProperty>();
        private ComponentRefProperty forFind = new ComponentRefProperty();
        private Container container = null;

        public JETAFocusTraversalPolicy() {
            super();
        }

        public Container getContainer() {
            return container;
        }

        public void setContainer(Container aContainer) {
            container = aContainer;
            container.setFocusCycleRoot(true);
        }

        protected boolean isAnyFocusable() {
            for (ComponentRefProperty crp : elements) {
                if (crp != null && crp.getGridComponent() != null) {
                    Component comp = crp.getGridComponent().getBeanDelegate();
                    if (comp != null && comp.isFocusable() && comp.isEnabled()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Component fetchNextComponent(Container aContainer, Component aComponent) {
            GridComponent gc = FormUtils.getFirstGridComponent(aComponent);
            if (gc != null) {
                forFind.setGridComponent(gc);
                int lIndex = elements.indexOf(forFind);
                if (lIndex == elements.size() - 1) {
                    Component comp = getFirstComponent(aContainer);
                    if (comp != null) {
                        if (comp.isFocusable() && comp.isEnabled()) {
                            return comp;
                        } else {
                            return fetchNextComponent(aContainer, comp);
                        }
                    }
                } else if (lIndex > -1 && (lIndex + 1) < elements.size()) {
                    ComponentRefProperty crp = elements.get(lIndex + 1);
                    if (crp != null && crp.getGridComponent() != null) {
                        Component comp = crp.getGridComponent().getBeanDelegate();
                        if (comp != null) {
                            if (comp.isFocusable() && comp.isEnabled()) {
                                return comp;
                            } else {
                                return fetchNextComponent(aContainer, comp);
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getComponentAfter(Container aContainer, Component aComponent) {
            if (isAnyFocusable()) {
                return fetchNextComponent(aContainer, aComponent);
            }
            return null;
        }

        public Component fetchPrevComponent(Container aContainer, Component aComponent) {
            GridComponent gc = FormUtils.getFirstGridComponent(aComponent);
            if (gc != null) {
                forFind.setGridComponent(gc);
                int lIndex = elements.indexOf(forFind);
                if (lIndex == 0) {
                    Component comp = getLastComponent(aContainer);
                    if (comp != null) {
                        if (comp.isFocusable() && comp.isEnabled()) {
                            return comp;
                        } else {
                            return fetchPrevComponent(aContainer, comp);
                        }
                    }
                } else if (lIndex > 0) {
                    ComponentRefProperty crp = elements.get(lIndex - 1);
                    if (crp != null && crp.getGridComponent() != null) {
                        Component comp = crp.getGridComponent().getBeanDelegate();
                        if (comp != null) {
                            if (comp.isFocusable() && comp.isEnabled()) {
                                return comp;
                            } else {
                                return fetchPrevComponent(aContainer, comp);
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getComponentBefore(Container aContainer, Component aComponent) {
            if (isAnyFocusable()) {
                return fetchPrevComponent(aContainer, aComponent);
            }
            return null;
        }

        @Override
        public Component getFirstComponent(Container aContainer) {
            if (isAnyFocusable()) {
                if (!elements.isEmpty() && elements.getFirst() != null
                        && elements.getFirst().getGridComponent() != null) {
                    Component comp = elements.getFirst().getGridComponent().getBeanDelegate();
                    if (comp != null) {
                        if (comp.isFocusable() && comp.isEnabled()) {
                            return comp;
                        } else {
                            return fetchNextComponent(aContainer, comp);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getLastComponent(Container aContainer) {
            if (!elements.isEmpty()) {
                ComponentRefProperty crp = elements.getLast();
                if (crp != null && crp.getGridComponent() != null) {
                    Component comp = crp.getGridComponent().getBeanDelegate();
                    if (comp != null) {
                        if (comp.isFocusable() && comp.isEnabled()) {
                            return comp;
                        } else {
                            return fetchPrevComponent(aContainer, comp);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getDefaultComponent(Container aContainer) {
            return getFirstComponent(aContainer);
        }

        @Override
        public Component getInitialComponent(Window window) {
            return getFirstComponent(window);
        }

        public void assign(FocusTraversalPolicy fp) {
            if (fp instanceof JETAFocusTraversalPolicy) {
                JETAFocusTraversalPolicy jfp = (JETAFocusTraversalPolicy) fp;
                LinkedList<ComponentRefProperty> lelems = jfp.getElements();
                if (lelems != null) {
                    elements = new LinkedList<ComponentRefProperty>();
                    for (int i = 0; i < lelems.size(); i++) {
                        elements.add(new ComponentRefProperty(lelems.get(i)));
                    }
                }
            }
        }

        public LinkedList<ComponentRefProperty> getElements() {
            return elements;
        }

        public void setElements(LinkedList<ComponentRefProperty> aElements) {
            if (aElements != null) {
                elements = aElements;
            } else {
                elements = new LinkedList<ComponentRefProperty>();
            }
        }

        public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
            if (elements != null) {
                for (int i = elements.size() - 1; i >= 0; i--) {
                    ComponentRefProperty crp = elements.get(i);
                    crp.resolveReferences(jbean, aAllComps, propName);
                    if (crp.getGridComponent() == null) {
                        elements.remove(i);
                    }
                }
            }
        }
    }
    
    JETAFocusTraversalPolicy m_value = new JETAFocusTraversalPolicy();
    public static final String PROPERTY_4REPLACE_NAME = "focusTraversalPolicy";
    public static final String PROP_NAME = "JETAFocusPolicy";
    static final long serialVersionUID = -6082801367391562705L;
    /**
     * The version for this class.
     */
    public static final int VERSION = 1;

    @Override
    public void setValue(Object obj) {
        if (obj instanceof FocusPolicyProperty) {
            FocusPolicyProperty lfpp = (FocusPolicyProperty) obj;
            m_value.assign(lfpp.getDelegate());
        } else if (obj instanceof FocusTraversalPolicy) {
            FocusTraversalPolicy fp = (FocusTraversalPolicy) obj;
            m_value.assign(fp);
        } else if (obj == null) {
            m_value = null;
        }
    }

    @Override
    public void updateBean(JETABean jbean) {
        if (jbean != null && jbean.getDelegate() instanceof GridView) {
            GridView gv = (GridView) jbean.getDelegate();
            if (m_value != null && m_value.getElements() != null
                    && m_value.getElements().size() > 0) {
                m_value.setContainer(gv);
                gv.setFocusTraversalPolicy(m_value);
            } else {
                gv.setFocusCycleRoot(false);
                gv.setFocusTraversalPolicy(null);
            }
        }
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        Component comp = jbean.getDelegate();
        if (comp != null && comp instanceof GridView) {
            GridView gv = (GridView) comp;
            m_value.setContainer(gv);
        }
        m_value.resolveReferences(jbean, aAllComps, propName);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isPreferred() {
        return true;
    }

    @Override
    public String getName() {
        return PROP_NAME;
    }

    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        @SuppressWarnings("unchecked")
        LinkedList<ComponentRefProperty> ll = (LinkedList<ComponentRefProperty>) in.readObject(JETAFocusTraversalPolicy.LIST_PROP_NAME, FormUtils.EMPTY_LIST);
        m_value.setElements(ll);
    }

    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        out.writeObject(JETAFocusTraversalPolicy.LIST_PROP_NAME, m_value.getElements());
    }
}
