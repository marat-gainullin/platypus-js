/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.components.panel.JFramePanel;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.gui.formmgr.FormManagerUtils;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.registry.JETARegistry;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 *
 * @author mg
 */
public class FramePanelLinkProperty extends JETAProperty
{

    static final long serialVersionUID = 8375432266561274626L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public static final String PROPERTY_ID = "formLink";
    protected static String PATH_PROP_NAME = "framePath";
    protected static String ID_PROP_NAME = "frameId";
    protected String m_Path = "";
    protected long m_id = -1L;
    protected JFramePanel m_FramePanel = null;

    public FramePanelLinkProperty()
    {
        super(PROPERTY_ID);
    }

    public FramePanelLinkProperty(String aPath, long id)
    {
        this();
        m_Path = aPath;
        m_id = id;
    }

    public String getM_Path()
    {
        return m_Path;
    }

    public long getM_id()
    {
        return m_id;
    }

    @Override
    public void setValue(Object obj)
    {
        if (obj == null)
        {
            m_id = -1L;
            m_Path = "";
        }
        else if (obj instanceof FramePanelLinkProperty)
        {
            FramePanelLinkProperty flprop = (FramePanelLinkProperty) obj;
            m_id = flprop.getM_id();
            m_Path = flprop.getM_Path();
        }
    }

    @Override
    public void updateBean(JETABean jbean)
    {
        if (jbean != null)
        {
            m_FramePanel = (JFramePanel) jbean.getDelegate();
            if (m_FramePanel != null)
            {
                if ((m_Path != null && !m_Path.isEmpty()) || m_id != -1L)
                {
                    FormUtils.safeAssert(jbean.getDelegate() instanceof JFramePanel);
                    LayoutManager lm = m_FramePanel.getLayout();
                    if (lm == null || !(lm instanceof BorderLayout))
                    {
                        m_FramePanel.setLayout(new BorderLayout());
                    }
                    if (FormUtils.isDesignMode())
                    {
                        try
                        {
                            ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
                            ComponentSource compsrc = (ComponentSource) JETARegistry.lookup(ComponentSource.COMPONENT_ID);
                            FormUtils.safeAssert(compsrc != null);
                            FormUtils.safeAssert(factory != null);

                            FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
                            FormComponent fc = null;
                            /*
                            if (Environment.getDocumentsSource() != null)
                            {
                                DocumentsSource ds = Environment.getDocumentsSource();
                                String lContent = ds.getDocumentContent(m_id);
                                if (lContent != null && !lContent.isEmpty())
                                {
                                    fc = fmgr.openLinkedFormByXmlContent(lContent, FormComponent.ID_LINKED_PREFIX + String.valueOf(m_id));
                                }
                            }
                            else
                            {
                                fc = fmgr.openLinkedForm(m_Path);
                            }
                            */
                            if (fc != null)
                            {
                                FormComponent fctopparent = factory.createTopParent(m_FramePanel, compsrc, fc);
                                m_FramePanel.removeAll();
                                m_FramePanel.add(fctopparent, BorderLayout.CENTER);
                                fmgr.activateForm(fctopparent.getId());
                                fc.resolveComponentReferences();
                            }
                        }
                        catch (FormException ex)
                        {
                            Logger.getLogger(SplittedPaneLeftProperty.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else
                    {
                        try
                        {
                            FormComponent fc = null;
                            if (m_Path != null && !m_Path.isEmpty())
                            {
                                fc = FormManagerUtils.openForm(m_Path);
                                fc.setTopLevelForm(true);
                                FormPanel fp = new FormPanel(fc);
                                m_FramePanel.removeAll();
                                m_FramePanel.add(fp, BorderLayout.CENTER);
                            }
                            /*
                            else if (Environment.getDocumentsSource() != null)
                            {
                                FormRunner fr = new FormRunner(m_id);
                                try {
                                    m_FramePanel.setFormRunner(fr);
                                    fr.showOnPanel(m_FramePanel);
                                    
                                    DocumentsSource ds = Environment.getDocumentsSource();
                                    String lContent = ds.getDocumentContent(m_id);
                                    if (lContent != null && !lContent.isEmpty())
                                    {
                                    fc = FormManagerUtils.openForm(lContent, FormComponent.ID_LINKED_PREFIX + String.valueOf(m_id));
                                    }
                                     
                                } catch (SQLException ex) {
                                    Logger.getLogger(FramePanelLinkProperty.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                DocumentsSource ds = Environment.getDocumentsSource();
                                String lContent = ds.getDocumentContent(m_id);
                                if (lContent != null && !lContent.isEmpty())
                                {
                                    fc = FormManagerUtils.openForm(lContent, FormComponent.ID_LINKED_PREFIX + String.valueOf(m_id));
                                }
                                 
                            }
                            
                            if (fc != null)
                            {
                                fc.setTopLevelForm(true);
                                m_FramePanel.removeAll();
                                m_FramePanel.add(fc, BorderLayout.CENTER);
                                fc.resolveComponentReferences();
                            }
                             */
                        }
                        catch (FormException e)
                        {
                            FormsLogger.severe(e);
                        }
                    }
                }
                else
                {
                    m_FramePanel.removeAll();
                }
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

        m_Path = in.readString(PATH_PROP_NAME);
        String sid = in.readString(ID_PROP_NAME);
        try
        {
            m_id = Long.valueOf(sid);
        }
        catch (NumberFormatException e)
        {
            m_id = -1L;
        }
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException
    {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);

        out.writeString(PATH_PROP_NAME, m_Path);
        out.writeString(ID_PROP_NAME, String.valueOf(m_id));
    }
}
