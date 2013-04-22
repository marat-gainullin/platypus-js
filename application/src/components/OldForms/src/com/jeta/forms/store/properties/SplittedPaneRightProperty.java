/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.registry.JETARegistry;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSplitPane;

/**
 *
 * @author Mg
 */
public class SplittedPaneRightProperty extends JETAProperty
{

    static final long serialVersionUID = 2375434426561274626L;
    public static final int VERSION = 1;
    private String m_GridViewPropName = "form";
    private JSplitPane m_Pane = null;
    private FormMemento m_memento = null;
    private FormComponent m_form = null;
    public static final String PROPERTY_ID = "rightSplittedPane";

    /**
     * The current version number of this class
     */
    public SplittedPaneRightProperty()
    {
        super(PROPERTY_ID);
    }

    @Override
    public boolean isPreferred()
    {
        return false;
    }

    public FormComponent getForm() throws FormException
    {
        if (m_form == null)
        {
            ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
            FormUtils.safeAssert(factory != null);
            m_form = factory.createContainedForm(JSplitPane.class, m_memento);
        }
        return m_form;
    }

    /**
     * Returns a memento object that completely defines the state of the form
     * contained by this tab.
     *
     * @return the memento for the form contained by this tab.
     */
    public FormMemento getFormMemento() throws FormException
    {
        if (m_form != null)
        {
            return m_form.getExternalState(StateRequest.DEEP_COPY);
        }
        else
        {
            return m_memento;
        }
    }

    @Override
    public void setValue(Object obj)
    {
        FormUtils.safeAssert(obj instanceof SplittedPaneRightProperty);
        SplittedPaneRightProperty sourceprop = (SplittedPaneRightProperty) obj;
        m_memento = sourceprop.m_memento;
        m_form = sourceprop.m_form;
    }

    @Override
    public void updateBean(JETABean jbean)
    {
        FormUtils.safeAssert(jbean.getDelegate() instanceof JSplitPane);
        m_Pane = (JSplitPane) jbean.getDelegate();
        if (FormUtils.isDesignMode())
        {
            try
            {
                ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
                ComponentSource compsrc = (ComponentSource) JETARegistry.lookup(ComponentSource.COMPONENT_ID);
                FormUtils.safeAssert(compsrc != null);
                FormUtils.safeAssert(factory != null);
                FormComponent righttopparent = factory.createTopParent(m_Pane, compsrc, getForm());
                m_Pane.setRightComponent(righttopparent);
            }
            catch (FormException ex)
            {
                Logger.getLogger(SplittedPaneRightProperty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try
            {
                FormComponent righttopparent = FormComponent.create();
                righttopparent.setState(getFormMemento());
                righttopparent.setTopLevelForm(true);
                m_Pane.setRightComponent(righttopparent);
                m_memento = null;
            }
            catch (FormException e)
            {
                FormsLogger.severe(e);
            }
        }
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException
    {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        m_memento = (FormMemento) in.readObject(m_GridViewPropName, FormMemento.EMPTY_FORM_MEMENTO);
        m_form = null;
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException
    {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        /**
         * This is a hack to obtain the state request which is set by the
         * caller. There is no way to obtain this value other than getting
         * it from the registry. The caller should have set this value. This
         * is needed during preview when in design mode; otherwise we won't
         * get the most current state for linked forms if they are opened in
         * the designer
         */
        StateRequest state_req = StateRequest.SHALLOW_COPY;
        Object obj = com.jeta.open.registry.JETARegistry.lookup(StateRequest.COMPONENT_ID);
        if (obj instanceof StateRequest)
        {
            state_req = (StateRequest) obj;
        }

        try
        {
            getForm();
            if (m_form != null)
            {
                out.writeObject(m_GridViewPropName, m_form.getExternalState(state_req));
            }
            else
            {
                out.writeObject(m_GridViewPropName, null);
            }
        }
        catch (Exception e)
        {
            FormsLogger.severe(e);
            out.writeObject(m_GridViewPropName, null);
        }
    }
}
