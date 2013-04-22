/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jeta.forms.gui.form;

import com.eas.client.scripts.ScriptRunner;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Iterator;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.store.bean.BeanSerializer;
import com.jeta.forms.store.bean.BeanDeserializer;
import com.jeta.forms.store.bean.BeanSerializerFactory;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.CellConstraintsMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.properties.IDGenerator;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.DefaultComponentFinder;
import com.jeta.open.support.IteratedAction;
import com.jeta.open.support.script.VirtualPropsJavaObject;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.mozilla.javascript.ClassCache;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.Debugger;

/**
 * A <code>FormComponent</code> is a type of GridComponent that contains a nested form 
 * in a GridView. A FormComponent represents a top-level form or nested form whereas 
 * StandardComponents represent Swing components (Java beans).
 *
 * Forms come in two types:  Embedded and Linked.  An embedded form is a nested form
 * that is fully encapsulated and stored in the parent. A linked form refers to a
 * form file on disk, but it can exist as a child in another form.
 *
 * All GridComponents have a single JETABean has a child.  The FormComponent has a JETABean
 * child which is a wrapper around a GridView bean.
 *
 * Containment Hierarchy:
 * <pre>
 *   FormComponent
 *       |
 *        -- JETABean -maintains the properties for the form.
 *             |
 *              -- GridView -a GridView has N child components that occupy the cells in the view
 *                    |                           
 *                    //-- GridView.FormContainer ---------------------> FormLayout
 *                            |                         (layoutmgr)
 *                             -- GridComponent1
 *                            |
 *                             -- GridComponent2
 *                            |
 *                             -- GridComponentN
 *                                 -each GridComponent is either a StandardComponent(Swing component)
 *                                  or a nested form (FormComponent)
 * </pre>
 *
 * @author Jeff Tassin
 */
public class FormComponent extends GridComponent {

    public static final String ID_EMBEDDED_PREFIX = "embedded.";
    public static final String ID_LINKED_PREFIX = "linked.";
    public static final String ID_TOP_PARENT_PREFIX = "top.parent.";
    /**
     * A unique id for this form.  If the form is linked, then this value will
     * be the absolute file path.
     */
    private String m_id;
    /**
     * The path for this form on the local file system. Note that we don't
     * store the full path in the FormMemento (only the relative package).
     */
    private String m_abspath = null;
    /**
     * This is a flag used to indicate is this is the top-most form when saving this form.
     * This reason for the flag is if this form is linked,we only store the path to the
     * linked form and not the contents.  However, a top-level form can be linked, and in this case
     * we need to store everything.
     */
    private boolean m_top_level_form = false;
    protected String m_script = "";
    protected Script m_compiledScript = null;
    protected Scriptable m_scriptScope = null;
    protected ContextFactory cf = ContextFactory.getGlobal();
    protected HashSet<Integer> m_breakPoints = new HashSet<Integer>();
    protected ArrayList<Integer> m_bookmarks = new ArrayList<Integer>();
    protected boolean bpModified = false;
    protected boolean runtime = false;

    /**
     * Creates a <code>FormComponent</code> instance.
     */
    protected FormComponent() {
    }

    /**
     * Creates a <code>FormComponent</code> instance with the specified id, child bean, and parent view.
     * @param id the unique id for this form.
     * @param jbean the underlying GridView
     * @param parentView the parent for this form.
     * @param embedded flag that indicates if this form is embedded
     */
    protected FormComponent(String id, JETABean jbean, GridView parentView, boolean embedded) throws FormException {
        super(jbean, parentView);
        m_id = id;
        FormUtils.safeAssert(jbean.getDelegate() instanceof GridView);
        setBean(getBean());
        setEmbedded(embedded);
    }

    public void SetRuntime(boolean aValue) {
        boolean oldValue = runtime;
        runtime = aValue;
        firePropertyChange("runtime", oldValue, runtime);
    }

    public String getTitle() {
        //pk: 2009-11-09 Используется только из скрипта.
        Container window = getWindow();
        if (window != null) {
            if (window instanceof java.awt.Frame) {
                return ((java.awt.Frame) window).getTitle();
            } else if (window instanceof java.awt.Dialog) {
                return ((java.awt.Dialog) window).getTitle();
            } else if (window instanceof JInternalFrame) {
                return ((JInternalFrame) window).getTitle();
            }
        }
        return null;
    }

    public void setTitle(String title) {
        //pk: 2009-11-09 Используется только из скрипта.
        if (getParentForm() == null) {
            Container window = getWindow();
            if (window != null) {
                if (window instanceof java.awt.Frame || window instanceof java.awt.Dialog || window instanceof JInternalFrame) {
                    Logger.getLogger(FormComponent.class.getName()).log(Level.FINEST, "Setting title {0}", getChildView().getTitle());
                }
                if (window instanceof java.awt.Frame) {
                    ((java.awt.Frame) window).setTitle(title);
                } else if (window instanceof java.awt.Dialog) {
                    ((java.awt.Dialog) window).setTitle(title);
                } else if (window instanceof JInternalFrame) {
                    ((JInternalFrame) window).setTitle(title);
                } else {
                    Logger.getLogger(FormComponent.class.getName()).log(Level.INFO, "Unknown type of the top-level window (not a java.awt.Frame, java.awt.Dialog, javax.swing.JInternalFrame), unable to set the title: {0}", window.getClass().getName());
                }
            } else {
                Logger.getLogger(FormComponent.class.getName()).info("Unable to determine the top-level window showing the FormComponent while trying to set the title.");
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("This FormComponent is not the toplevel one. The hierarchy follows:\n");
            FormComponent comp = this;
            do {
                sb.append(comp.toString()).append('\n');
                comp = getParentForm(comp);
            } while (comp != null);
            Logger.getLogger(FormComponent.class.getName()).finest(sb.toString());
        }
    }

    public Integer getDefaultCloseOperation() {
        Container window = getWindow();
        if (window != null) {
            if (window instanceof javax.swing.JFrame) {
                return ((javax.swing.JFrame) window).getDefaultCloseOperation();
            } else if (window instanceof javax.swing.JDialog) {
                return ((javax.swing.JDialog) window).getDefaultCloseOperation();
            } else if (window instanceof JInternalFrame) {
                return ((JInternalFrame) window).getDefaultCloseOperation();
            }
        }
        return null;
    }

    public void setDefaultCloseOperation(Integer defaultCloseOperation) {
        if (getParentForm() == null) {
            if (defaultCloseOperation != null) {
                Container window = getWindow();
                if (window != null) {
                    if (window instanceof javax.swing.JFrame || window instanceof javax.swing.JDialog || window instanceof JInternalFrame) {
                        Logger.getLogger(FormComponent.class.getName()).log(Level.FINEST, "Setting default close operation {0}", defaultCloseOperation);
                    }
                    if (window instanceof javax.swing.JFrame) {
                        ((javax.swing.JFrame) window).setDefaultCloseOperation(defaultCloseOperation);
                    } else if (window instanceof javax.swing.JDialog) {
                        ((javax.swing.JDialog) window).setDefaultCloseOperation(defaultCloseOperation);
                    } else if (window instanceof JInternalFrame) {
                        ((javax.swing.JInternalFrame) window).setDefaultCloseOperation(defaultCloseOperation);
                    } else {
                        Logger.getLogger(FormComponent.class.getName()).log(Level.INFO, "Unknown type of the top-level window (not a javax.swing.JFrame, javax.swing.JDialog, javax.swing.JInternalFrame), unable to set the title: {0}", window.getClass().getName());
                    }
                } else {
                    Logger.getLogger(FormComponent.class.getName()).info("Unable to determine the top-level window showing the FormComponent while trying to set the default close operation.");
                }
            } else {
                Logger.getLogger(FormComponent.class.getName()).info("Unable to set the default close operation, null have been passed.");
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("This FormComponent is not the toplevel one. The hierarchy follows:\n");
            FormComponent comp = this;
            do {
                sb.append(comp.toString()).append('\n');
                comp = getParentForm(comp);
            } while (comp != null);
            Logger.getLogger(FormComponent.class.getName()).finest(sb.toString());
        }
    }

    /*
    public Icon getIcon()
    {
    Container window = getWindow();
    if (window != null)
    {
    if (window instanceof java.awt.Frame)
    {
    List<Image> imgs = ((java.awt.Frame) window).getIconImages();
    if (imgs != null && !imgs.isEmpty() && imgs.get(0) instanceof Icon)
    {
    return (Icon) imgs.get(0);
    }
    }
    else if (window instanceof java.awt.Dialog)
    {
    List<Image> imgs = ((java.awt.Dialog) window).getIconImages();
    if (imgs != null && !imgs.isEmpty() && imgs.get(0) instanceof Icon)
    {
    return (Icon) imgs.get(0);
    }
    }
    else if (window instanceof JInternalFrame)
    {
    return ((JInternalFrame) window).getFrameIcon();
    }
    }
    return null;
    }
     */
    public void setIcon(Icon icon) {
        if (getParentForm() == null) {
            Container window = getWindow();
            if (window != null) {
                if (icon instanceof IconProperty) {
                    IconProperty pIcon = (IconProperty) icon;
                    icon = pIcon.imageIcon();
                }
                if (window instanceof java.awt.Frame) {
                    if (icon instanceof Image) {
                        ((java.awt.Frame) window).setIconImage((Image) icon);
                    } else if (icon instanceof ImageIcon) {
                        ((java.awt.Frame) window).setIconImage(((ImageIcon) icon).getImage());
                    }
                } else if (window instanceof java.awt.Dialog) {
                    if (icon instanceof Image) {
                        ((java.awt.Dialog) window).setIconImage((Image) icon);
                    } else if (icon instanceof ImageIcon) {
                        ((java.awt.Dialog) window).setIconImage(((ImageIcon) icon).getImage());
                    }
                } else if (window instanceof JInternalFrame) {
                    ((JInternalFrame) window).setFrameIcon(icon);
                }
            }
        }
    }

    public void close() {
//        if (getParentForm() == null)
//        {
        Container window = getWindow();
        if (window != null) {
            if (window instanceof JFrame) {
                JFrame fr = (JFrame) window;
                WindowListener[] wls = fr.getWindowListeners();
                if (wls != null) {
                    WindowEvent event = new WindowEvent(fr, WindowEvent.WINDOW_CLOSING);
                    for (int i = 0; i < wls.length; i++) {
                        try {
                            wls[i].windowClosing(event);
                        } catch (Exception ex) {
                            return;
                        }
                    }
                }
                int operation = fr.getDefaultCloseOperation();
                switch (operation) {
                    case WindowConstants.DISPOSE_ON_CLOSE:
                        fr.dispose();
                        break;
                    case WindowConstants.HIDE_ON_CLOSE:
                        fr.setVisible(false);
                        break;
                    case WindowConstants.EXIT_ON_CLOSE:
                        System.exit(0);
                        break;
                }
            } else if (window instanceof JInternalFrame) {
                JInternalFrame fr = (JInternalFrame) window;
                InternalFrameListener[] wls = fr.getInternalFrameListeners();
                if (wls != null) {
                    InternalFrameEvent event = new InternalFrameEvent(fr, InternalFrameEvent.INTERNAL_FRAME_CLOSING);
                    for (int i = 0; i < wls.length; i++) {
                        try {
                            wls[i].internalFrameClosing(event);
                        } catch (Exception ex) {
                            return;
                        }
                    }
                }
                int operation = fr.getDefaultCloseOperation();
                switch (operation) {
                    case WindowConstants.DISPOSE_ON_CLOSE:
                        fr.dispose();
                        break;
                    case WindowConstants.HIDE_ON_CLOSE:
                        fr.setVisible(false);
                        break;
                    /*
                    case WindowConstants.EXIT_ON_CLOSE:
                    System.exit(0);
                    break;
                     */
                }
            } else if (window instanceof JDialog) {
                JDialog fr = (JDialog) window;
                WindowListener[] wls = fr.getWindowListeners();
                if (wls != null) {
                    WindowEvent event = new WindowEvent(fr, WindowEvent.WINDOW_CLOSING);
                    for (int i = 0; i < wls.length; i++) {
                        try {
                            wls[i].windowClosing(event);
                        } catch (Exception ex) {
                            return;
                        }
                    }
                }
                int operation = fr.getDefaultCloseOperation();
                switch (operation) {
                    case WindowConstants.DISPOSE_ON_CLOSE:
                        fr.dispose();
                        break;
                    case WindowConstants.HIDE_ON_CLOSE:
                        fr.setVisible(false);
                        break;
                    /*
                    case WindowConstants.EXIT_ON_CLOSE:
                    System.exit(0);
                    break;
                     */
                }
            }
        }
//        }
    }

    public boolean isTopParent() {
        return (m_id != null && m_id.startsWith(ID_TOP_PARENT_PREFIX));
    }

    public boolean isRuntime() {
        return true;
    }
    protected static Scriptable guiLibRunner = null;

    public void executeFormScript() throws FormException {
        String formModule = getScript();
        if (formModule != null && !formModule.isEmpty() && isLinked()) { // We don't need to check if it's runtime. DesignFromComponent overrides this method and no op in it
            try {
                Context cx = cf.enterContext();
                try {
                    if (guiLibRunner == null) {
                        Debugger ldbg = cx.getDebugger();
                        Object ldbgCtxData = cx.getDebuggerContextData();
                        if (ldbg != null) {
                            cx.setDebugger(null, null);
                        }
                        try {
                            assert cx.getDebugger() == null;
                            if (ldbg == null) {
                                cx.setOptimizationLevel(9);
                            }
                            final Scriptable platypusStdLibScope = ScriptRunner.initializePlatypusStandardLibScope();
                            guiLibRunner = cx.newObject(platypusStdLibScope);
                            guiLibRunner.setPrototype(platypusStdLibScope);
                            guiLibRunner.setParentScope(null);
                            InputStream is = null;
                            InputStreamReader isr = null;
                            final Script compiledScript;
                            try {
                                is = FormPanel.class.getResourceAsStream("/com/jeta/open/support/script/gui.js");
                                isr = new InputStreamReader(is);
                                compiledScript = cx.compileReader(isr, "gui", 0, null);
                            } finally {
                                if (isr != null) {
                                    isr.close();
                                }
                                if (is != null) {
                                    is.close();
                                }
                            }
                            compiledScript.exec(cx, guiLibRunner);
                            assert cx.getDebugger() == null;
                            if (ldbg == null) {
                                cx.setOptimizationLevel(0);
                            }
                        } finally {
                            if (ldbg != null) {
                                cx.setDebugger(ldbg, ldbgCtxData);
                            }
                        }
                    }
                    Scriptable sco = cx.newObject(guiLibRunner);
                    sco.setPrototype(guiLibRunner);
                    sco.setParentScope(null);
                    (new ClassCache()).associate((ScriptableObject) sco);
                    VirtualPropsJavaObject sf = new VirtualPropsJavaObject(sco, getChildView(), sco);
                    sco.put("view", sco, sf);
                    sco.put("form", sco, this);
                    //
                    setScriptScope(sco);
                    Script sc = m_compiledScript != null ? m_compiledScript : cx.compileString(formModule, (getAbsolutePath() != null && !getAbsolutePath().isEmpty()) ? getAbsolutePath() : getId(), 0, null);
                    postCompileScript(cx.getDebugger());
                    sc.exec(cx, sco);
                } finally {
                    Context.exit();
                }
            } catch (Exception ex) {
                if (!(ex instanceof IllegalStateException) || ex.getMessage() == null || !ex.getMessage().equals("break")) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, ex.getMessage() != null ? ex.getMessage() : ex.toString(), I18N.getLocalizedMessage("Error"), JOptionPane.ERROR_MESSAGE);
                    throw new FormException(ex);
                }
            }
        }
    }

    /**
     * Creates a form component.
     */
    public static FormComponent create() {
        FormComponentFactory factory = (FormComponentFactory) JETARegistry.lookup(FormComponentFactory.COMPONENT_ID);
        if (factory == null) {
            FormUtils.safeAssert(!FormUtils.isDesignMode());
            return new FormComponent();
        } else {
            return factory.createFormComponent();
        }
    }

    /**
     * Returns the absolute path to this form. If the form is embedded, this value will
     * be null.
     * @return the absolute path to this form if the form is a linked form.
     */
    public String getAbsolutePath() {
        return m_abspath;
    }

    public void setEmbedded(boolean embedded) {
        if (m_id == null || m_id.isEmpty()) {
            m_id = getId();
        }
        if (!m_id.startsWith(ID_EMBEDDED_PREFIX)
                && !m_id.startsWith(ID_LINKED_PREFIX)
                && !m_id.startsWith(ID_TOP_PARENT_PREFIX)) {
            m_id = ID_EMBEDDED_PREFIX + m_id;
        }
        if (embedded) {
            m_abspath = null;
            if (m_id != null && !m_id.isEmpty()) {
                m_id = m_id.replace(ID_LINKED_PREFIX, ID_EMBEDDED_PREFIX);
            }
        } else {
            if (m_id != null && !m_id.isEmpty()) {
                m_id = m_id.replace(ID_EMBEDDED_PREFIX, ID_LINKED_PREFIX);
            }
        }
    }

    /**
     * Returns the total width in pixels of the cells occupied by this component.
     * @return the total width in pixels of the cells occupied by this component
     */
    @Override
    public int getCellWidth() {
        if (getParentView() == null) {
            return getWidth();
        } else {
            return super.getCellWidth();
        }
    }

    /**
     * Returns the total height in pixels of the cells occupied by this component.
     * @return the total height in pixels of the cells occupied by this component
     */
    @Override
    public int getCellHeight() {
        if (getParentView() == null) {
            return getHeight();
        } else {
            return super.getCellHeight();
        }
    }

    /**
     * Returns the left location of this component's cell in the parent coordinates.
     * @return the left location of this component's cell in the parent coordinates.
     */
    @Override
    public int getCellX() {
        if (getParentView() == null) {
            return getX();
        } else {
            return super.getCellX();
        }
    }

    /**
     * Returns the top location of this component's cell in the parent coordinates.
     * @return the top location of this component's cell in the parent coordinates.
     */
    @Override
    public int getCellY() {
        if (getParentView() == null) {
            return getY();
        } else {
            return super.getCellY();
        }
    }

    /**
     * Returns the GridView that is associated with this form.
     * @return the child view associated with this component
     */
    public GridView getChildView() {
        JETABean bean = getBean();
        if (bean != null) {
            return (GridView) bean.getDelegate();
        }
        return null;
    }

    /**
     * Returns the number of columns in this form.
     * @return the number of columns in this form
     */
    public int getColumnCount() {
        GridView view = getChildView();
        return (view == null ? 0 : view.getColumnCount());
    }

    /**
     * Returns the filename of this form.  If the form is embedded, this value is null.
     * @return the filename portion of the path to this form (only if this form is linked)
     */
    public String getFileName() {
        if (isLinked()) {
            String path = getAbsolutePath();
            if (path != null) {
                int pos = path.lastIndexOf('/');
                if (pos < 0) {
                    pos = path.lastIndexOf('\\');
                }

                if (pos >= 0) {
                    return path.substring(pos + 1, path.length());
                } else {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * Returns the child grid component that occupies the given row and column in this form.
     * @return the GridComponent at the specificed column and row
     */
    public GridComponent getGridComponent(int col, int row) {
        GridView view = getChildView();
        return (view == null ? null : view.getGridComponent(col, row));
    }

    /**
     * Traverses the container hiearchy for the given form starting from its parent and traversing
     * until it encounters a FormComponent or JDesktopPane instance. if JDesktopPane instance is found
     * then null is returned indicating that <b>comp</b> is the top-level form.
     * @return the formcomponent that is the closest ancestor of the given form.  Null is returned
     * if this the topmost parent form.
     */
    public static FormComponent getParentForm(Component comp) {
        if (comp == null) {
            return null;
        }

        comp = comp.getParent();
        while (comp != null) {
            if (comp instanceof JDesktopPane) {
                return null;
            }
            if (comp instanceof FormComponent) {
                return (FormComponent) comp;
            }

            comp = comp.getParent();
        }
        return null;
    }

    /**
     * Traverses the container hiearchy for this form starting from its parent and traversing
     * until it encounters a FormComponent instance or JDesktopPane instance. if JDesktopPane
     * instance is found then null is returned indicating that FormComponent is the top-level form.
     * @return the formcomponent that is the closest ancestor of this form.  Null is returned
     * if this the topmost parent form.
     */
    public FormComponent getParentForm() {
        return getParentForm(this);
    }

    /**
     * Returns the selected child component in this form.  The is the component that
     * is selected by the user in design mode.
     * @returns the first selected component it finds in the component hierarhcy of
     * this container. Null is returned if no component is selected.
     */
    public GridComponent getSelectedComponent() {
        if (isSelected()) {
            return this;
        } else {
            return getChildView().getSelectedComponent();
        }
    }

    /**
     * Always call this method instead of getState when saving a top level form.
     * Saves this form's state to a memento.
     * @return the form state as a memento.
     */
    public FormMemento getExternalState(StateRequest si) throws FormException {
        try {
            JETARegistry.rebind(StateRequest.COMPONENT_ID, si);
            setTopLevelForm(true);
            return (FormMemento) getState(si);
        } finally {
            setTopLevelForm(false);
            JETARegistry.rebind(StateRequest.COMPONENT_ID, null);
        }
    }

    /**
     * Returns the number of rows in this form.
     * @return the number of rows in this form
     */
    public int getRowCount() {
        GridView view = getChildView();
        return (view == null ? 0 : view.getRowCount());
    }

    /**
     * Saves this form's state as a memento object.
     * @param si a state request that has some control over how the form state is
     * stored.  For example, in some cases we want to store full copy of a linked form
     * in the memento as oposed to the link reference.
     * @return the state of this object as a mememento
     */
    @Override
    public ComponentMemento getState(StateRequest si) throws FormException {
        FormMemento state = new FormMemento();
        GridView view = getChildView();

        state.setId(getId());
        state.setComponentClass(FormComponent.class.getName());
        state.setComponentId(getComponentID());
        if (getParentView() != null) {
            state.setCellConstraints(getConstraints().createCellConstraints());
        }

        state.setRowSpecs(view.getRowSpecs());
        state.setColumnSpecs(view.getColumnSpecs());

        if (isLinked() && si.isShallowCopy()) {
            state.setRelativePath(getRelativePath());
            if (!isTopLevelForm()) {
                return state;
            }
        }

        state.setRowGroups(view.getRowGroups());
        state.setColumnGroups(view.getColumnGroups());
        state.setCellPainters(view.getCellPainters());
        Iterator iter = view.gridIterator();
        while (iter.hasNext()) {
            GridComponent gc = (GridComponent) iter.next();
            if (gc.getBean() != null) {
                ComponentMemento ccm = gc.getState(si);
                state.addComponent(ccm);
            }
        }

        /** store the view properties */
        BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
        BeanSerializer bs = fac.createSerializer();
        JETABean jbean = getBean();
        FormUtils.safeAssert(jbean.getDelegate() == view);
        PropertiesMemento pm = bs.writeBean(jbean);
        state.setPropertiesMemento(pm);

        state.setScript(m_script);
        state.setScriptBreakPoints(FormMemento.encodeBreakPoints(m_breakPoints));
        state.setScriptBookmarks(FormMemento.encodeBookmarks(m_bookmarks));

        return state;
    }

    /**
     * Returns a unique id for this form.  If the form is embedded, the id is based
     * on this object's hashCode.  If the form is linked, the id is the absolute
     * path to the form.
     * @return the unique id of this form.
     */
    @Override
    public String getId() {
        if (m_id == null) {
            m_id = ID_EMBEDDED_PREFIX + String.valueOf(IDGenerator.genID());
            return m_id;
        } else {
            return m_id;
        }
    }

    public void setId(String newId) {
        m_id = newId;
    }

    /**
     * Returns the relative path to this form.  The path is determined by the source paths
     * defined in the project settings.  If this form is embedded, null is returned.
     * @return the relative path.
     */
    public String getRelativePath() {
        return m_abspath;
//        ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
//        return pmgr.getRelativePath(m_abspath);
    }

    /**
     * Traverses the container hierarchy for the given component and returns
     * the first parent that is a top-level FormComponent.  Most components
     * only have one top level parent.  However, if a component is in a JTabbedPane,
     * it will have two top-level parents.
     * @param comp the comp that determines where the traversal will start.
     * @return the first FormComponent ancestor found in the container heirarchy.
     */
    public static FormComponent getTopLevelForm(Component comp) {
        if (comp == null) {
            return null;
        }


        while (comp != null && !(comp instanceof java.awt.Window)) {
            if (comp instanceof FormComponent && ((FormComponent) comp).isTopLevelForm()) {
                return (FormComponent) comp;
            }
            comp = comp.getParent();
        }
        return null;
    }

    /** Traversses the container hierarchy for this form component and returns
     * the first parent which is JFrame, JDialog, or JInternalFrame. It may return
     * null if neither of three classes are in the parents hierarchy.
     * @return JFrame, JDialog or JInternalFrame containing this form component.
     */
    public Container getWindow() {
        Container container = getParent(), prevContainer = null;
        while (container != null) {
            if (container instanceof java.awt.Frame || container instanceof java.awt.Dialog || container instanceof javax.swing.JInternalFrame) {
                break;
            }
            prevContainer = container;
            container = container.getParent();
        }
        if (container == null) {
            Logger.getLogger(FormComponent.class.getName()).log(Level.FINE, "Top-level window not found. The topmost container was {0}", String.valueOf(prevContainer));
        }
        return container;
    }

    /**
     * Returns true if this form is an embedded form. An embedded form is stored
     * within the parent form.
     * @return true if this form is an embedded form.
     */
    public boolean isEmbedded() {
        return (m_id != null && m_id.startsWith(ID_EMBEDDED_PREFIX));
    }

    /**
     * Returns true if this form is linked.  A linked form is stored in its own form file
     * on disk.
     * @return true if this form is a linked form.
     */
    public boolean isLinked() {
        return !isEmbedded() && !isTopParent();
    }

    /**
     * Returns an interator that iterates over the grid components in a child view.
     * @return an iterator that iterates over the grid components (GridComponent objects) in the child view
     */
    public Iterator gridIterator() {
        return getChildView().gridIterator();
    }

    /**
     * Return true if this form is the top-most form in the container hierarchy.
     * @return the flag used to indicate is this is the top-most form when saving this form.
     */
    public boolean isTopLevelForm() {
        return m_top_level_form;
    }

    /**
     * Creates and initializes a FormComponent from the given form memento.
     */
    protected FormComponent openLinkedForm(FormMemento fm) throws FormException {
        FormComponent fc = com.jeta.forms.gui.formmgr.FormManagerUtils.openPackagedForm(fm.getRelativePath());
        return fc;
    }

    /**
     * PostInitialize is called once after all components in a FormPanel have been instantiated
     * at runtime (not design time).  This gives each property and component a chance to
     * do some last minute initializations that might depend on the top level parent.
     * FormComponent simply forwards the call to any children.
     * @param panel the top-level form container
     * @param cc a container whose child components are to be (post)intitialized.
     */
    public void _postInitialize(FormPanel panel, Container cc) {
        if (cc == null) {
            return;
        }

        for (int index = 0; index < cc.getComponentCount(); index++) {
            Component comp = cc.getComponent(index);
            if (comp instanceof GridComponent) {
                ((GridComponent) comp).postInitialize(panel);
            } else if (comp instanceof Container) {
                _postInitialize(panel, (Container) comp);
            }
        }
    }

    /**
     * PostInitialize is called once after all components in a form have been re-instantiated
     * at runtime (not design time).  This gives each property and component a chance to
     * do some last minute initializations that might depend on the top level parent.  An
     * example of this is button groups which are global to a form.
     * FormComponent simply forwards the call to any children.
     * @param panel the top-level form container
     */
    @Override
    public void postInitialize(FormPanel panel) {
        _postInitialize(panel, this);
    }

    /**
     * Performs any final initialization of this form component after it's state has been restored.
     * The main operation is to add empty components where needed. When in design mode, every cell
     * in the grid has a GridComponent.  The reason is because we need to enforce a minimum size
     * for the cell when the  user sets the row/col size to preferred.  If not, the cell size would
     * be zero if nothing  were there.  However, in run mode, we don't want to create an empty
     * component for every single cell.  For example, a 20x20 grid would require 400 components.
     * To prevent this, we only add 1 empty component per row and column.  This allows the
     * runtime form to look approximately like the design time forms with rows/cols that have no
     * components.  We use the grid_cache to keep track of which columns and rows
     * have had empty components added.
     */
    protected void postSetState(ComponentMemento cm) {
        boolean srt = FormUtils.isSemiRuntime();
        boolean rt = FormUtils.isRuntime();
        boolean dm = FormUtils.isDesignMode();
        FormUtils.setSemiRuntime(true);
        FormUtils.setRuntime(true);
        FormUtils.setDesignMode(false);
        try {
            GridView view = getChildView();
            for (int col = 1; col <= view.getColumnCount(); col++) {
                for (int row = 1; row <= view.getRowCount(); row++) {
                    GridComponent gc = view.getGridComponent(col, row);
                    if (gc == null) {
                        gc = new StandardComponent(null, view);
                        gc.setPreferredSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
                        gc.setMinimumSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
                        view.addComponent(gc, new ReadOnlyConstraints(col, row));
                        break;
                    }
                }
            }

            for (int row = 2; row <= view.getRowCount(); row++) {
                for (int col = 1; col <= view.getColumnCount(); col++) {
                    GridComponent gc = view.getGridComponent(col, row);
                    if (gc == null) {
                        gc = new StandardComponent(null, view);
                        gc.setPreferredSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
                        gc.setMinimumSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
                        view.addComponent(gc, new ReadOnlyConstraints(col, row));
                        break;
                    }
                }
            }
        } finally {
            FormUtils.setSemiRuntime(srt);
            FormUtils.setRuntime(rt);
            FormUtils.setDesignMode(dm);
        }
    }

    /**
     * Print for debugging
     */
    @Override
    public void print() {
        FormsLogger.debug("FormComponent  name = " + getName() + "   uid = " + getId() + "  path = " + getAbsolutePath() + "  hash: " + hashCode() + "   parentView: " + getParentView());
    }

    /**
     * Override revalidate so we can forward the call to the underlying GridView as well.
     */
    @Override
    public void revalidate() {
        GridView view = getChildView();
        if (view != null) {
            view.revalidate();
        }
        super.revalidate();
    }

    /**
     * Override GridComponent implementation so we can add the child to this container for
     * the design view.
     */
    @Override
    protected void setBean(JETABean jbean) {
        super.setBean(jbean);

        FormUtils.safeAssert(jbean.getDelegate() instanceof GridView);
        setLayout(new BorderLayout());

        FormUtils.safeAssert(getComponentCount() == 0);

        /** remove any existing components */
        removeAll();

        add(jbean, BorderLayout.CENTER);
    }

    /**
     * Sets the absolute path for this form.
     */
    public void setAbsolutePath(String path) {
        m_abspath = path;
        if (path != null && !path.isEmpty()) {
            setEmbedded(false);
        }
    }

    public void setControlButtonsVisible(boolean bVisible) {
        // no op
    }

    /**
     * Override setSelected so we can deselect everything in the child view when being deselected.
     */
    @Override
    public void setSelected(boolean bsel) {
        super.setSelected(bsel);
        if (!bsel) {
            GridView gv = getChildView();
            gv.deselectAll();
        }
    }

    public String getScript() {
        return m_script;
    }

    public Script getCompiledScript() {
        return m_compiledScript;
    }

    public Scriptable getScriptScope() {
        return m_scriptScope;
    }

    public void setScriptScope(Scriptable sc) {
        Scriptable old = m_scriptScope;
        m_scriptScope = sc;
        firePropertyChange("scriptScope", old, m_scriptScope);
    }

    protected boolean isBpModified() {
        return bpModified;
    }

    protected void setBpModified(boolean aBpModified) {
        bpModified = aBpModified;
    }

    protected boolean isBreakPoint(int aLine) {
        return m_breakPoints.contains(aLine);
    }

    protected boolean isBookmark(int aLine) {
        return m_bookmarks.contains(aLine);
    }

    public ArrayList<Integer> getBookmarks() {
        return m_bookmarks;
    }

    protected void toggleBookmark(int aLine) {
        if (isBookmark(aLine)) {
            m_bookmarks.remove(new Integer(aLine));
        } else {
            if (m_bookmarks.size() < 10) {
                m_bookmarks.add(aLine);
            }
        }
        setBpModified(true);
    }

    protected void clearBookmarks() {
        m_bookmarks.clear();
        setBpModified(true);
    }

    protected void clearBreakpoints() {
        m_breakPoints.clear();
        setBpModified(true);
    }

    protected void toggleBreakpoint(int aLine) {
        if (isBreakPoint(aLine)) {
            m_breakPoints.remove(aLine);
        } else {
            m_breakPoints.add(aLine);
        }
        setBpModified(true);
    }

    protected void postCompileScript(Debugger dbg) {
    }

    public HashSet<Integer> getBreakPoints() {
        return m_breakPoints;
    }

    public void setBreakPoints(HashSet<Integer> aBps) {
        m_breakPoints = aBps;
    }

    protected int[] getBreakPointsArray() {
        if (m_breakPoints != null) {
            int[] bps = new int[m_breakPoints.size()];
            Iterator<Integer> bpIt = m_breakPoints.iterator();
            if (bpIt != null) {
                int i = 0;
                while (bpIt.hasNext()) {
                    bps[i++] = bpIt.next();
                }
            }
            return bps;
        } else {
            return new int[0];
        }
    }

    /**
     * Resets this component from a previously saved state.
     * @param memento
     */
    @Override
    public void setState(ComponentMemento memento) throws FormException {
        FormMemento state = (FormMemento) memento;
        setEmbedded(state.isEmbedded());
        setComponentID(state.getComponentId());
        m_id = state.getId();
        FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
        if (FormUtils.isDesignMode()) {
            FormUtils.safeAssert(fmgr != null);
        }
        JETABean viewbean = JETABeanFactory.createBean("com.jeta.forms.gui.form.GridView", "gridview", true, true);
        GridView view = (GridView) viewbean.getDelegate();
        view.initialize(state.getColumnSpecs(), state.getRowSpecs());
        view.setCellPainters(state.getCellPainters());
        setBean(viewbean);
        view.setRowGroups(state.getRowGroups());
        view.setColumnGroups(state.getColumnGroups());
        m_script = state.getScript();
        m_compiledScript = state.getCompiledScript();
        m_breakPoints = state.decodeBreakPoints();
        m_bookmarks = state.decodeBookmarks();
        /** set the view properties */
        PropertiesMemento pm = state.getPropertiesMemento();
        if (pm != null) {
            BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
            BeanDeserializer bds = fac.createDeserializer(pm);
            bds.initializeBean(viewbean);
        }
        Iterator iter = state.iterator();
        while (iter.hasNext()) {
            ComponentMemento cm = (ComponentMemento) iter.next();
            try {
                if (cm instanceof FormMemento) {
                    FormMemento fm = (FormMemento) cm;
                    /** form is linked if the path is valid */
                    if (fm.getRelativePath() != null && !fm.getRelativePath().isEmpty()) {
                        try {
                            FormComponent fc = openLinkedForm(fm);
                            /** @todo check if the linked form has already been opened in this view. need to
                             * handle the case where the user wants to embed two or more of the same linked forms
                             * in a single view.  This is an exceedinly rare case */
                            view.addComponent(fc, fm.getCellConstraintsMemento().createCellConstraints());
                        } catch (Exception e) {
                            javax.swing.JLabel elabel = new javax.swing.JLabel("Error: " + fm.getRelativePath());
                            elabel.setForeground(java.awt.Color.red);
                            JETABean bean = new JETABean(elabel);
                            StandardComponent gc = new StandardComponent(bean, view);
                            view.addComponent(gc, fm.getCellConstraintsMemento().createCellConstraints());
                            /**
                             * Unable to add form.
                             */
                            FormsLogger.severe(e);
                        }
                        continue;
                    }
                }
                CellConstraintsMemento ccm = cm.getCellConstraintsMemento();
                /**
                 * If we are here, then the child component is either a standard Java Bean or an embedded form.
                 */
                if (StandardComponent.class.getName().equals(cm.getComponentClass()) && cm instanceof BeanMemento) {
                    BeanMemento bm = (BeanMemento) cm;
                    if (bm.getBeanClass() == null) {
                        // ignore empty components here.  
                        continue;
                    }
                }
                GridComponent gc = null;
                Class gc_class = Class.forName(cm.getComponentClass());
                if (FormComponent.class.isAssignableFrom(gc_class)) {
                    gc = FormComponent.create();
                } else {
                    gc = (GridComponent) gc_class.newInstance();
                }
                gc.setState(cm);
                if (ccm == null) {
                    /** this should never happen */
                    System.out.println("FormComponent.setState cellconstraints memento is null: ");
                    gc.print();
                    FormUtils.safeAssert(false);
                } else {
                    view.addComponent(gc, ccm.createCellConstraints());
                }
            } catch (Exception e) {
                FormsLogger.severe(e);
            }
        }
        // now traverse the grid cache and add an empty components where needed
        postSetState(memento);
        view.refreshView();
    }

    /**
     * Sets the flag used to indicate is this is the top-most form when saving this form.
     * @param topLevel set to true to make this a top-level form.
     */
    public void setTopLevelForm(boolean topLevel) {
        m_top_level_form = topLevel;
    }

    public void resolveComponentReferences() {
        if (isLinked()) {
            final HashMap<Long, GridComponent> compsHt = new HashMap<Long, GridComponent>();
            int i = 0;
            IteratedAction iaObserve = new IteratedAction() {

                @Override
                public boolean doWork(JComponent comp) {
                    if (comp instanceof GridComponent) {
                        GridComponent gcomp = (GridComponent) comp;
                        compsHt.put(gcomp.getComponentID(), gcomp);
                    }
                    return (!(comp instanceof FormComponent) || !((FormComponent) comp).isLinked());
                }
            };
            Component[] lchildren = getComponents();
            for (i = 0; i < lchildren.length; i++) {
                if (lchildren[i] instanceof JComponent) {
                    DefaultComponentFinder.iterateThroughTree((JComponent) lchildren[i], iaObserve);
                }
            }
            IteratedAction iaResolve = new IteratedAction() {

                @Override
                public boolean doWork(JComponent comp) {
                    if (comp instanceof JETABean) {
                        JETABean jbcomp = (JETABean) comp;
                        jbcomp.resolveReferences(compsHt);
                        if (jbcomp.getDelegate() != null && jbcomp.getDelegate() instanceof PropertyChangeListener) {
                            FormComponent.this.addPropertyChangeListener((PropertyChangeListener) jbcomp.getDelegate());
                        }
                    }
                    return (!(comp instanceof FormComponent) || !((FormComponent) comp).isLinked());
                }
            };
            for (i = 0; i < lchildren.length; i++) {
                if (lchildren[i] instanceof JComponent) {
                    DefaultComponentFinder.iterateThroughTree((JComponent) lchildren[i], iaResolve);
                }
            }
        }
    }
}
