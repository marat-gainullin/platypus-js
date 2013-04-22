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
package com.jeta.forms.store.memento;

import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.gui.common.FormUtils;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.JavaExternalizableObjectOutput;
import com.jeta.forms.store.support.Matrix;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;

/**
 * This class represents the state of a FormComponent including all of its Java
 * bean components and nested forms. A FormMemento can be safely serialized to
 * an i/o stream. You can get the state of a FormComponent by calling:
 * {@link com.jeta.forms.gui.form.FormComponent#getState} Likewise, you can set
 * the state of a FormComponent by calling:
 * {@link com.jeta.forms.gui.form.FormComponent#setState} Once you have a
 * FormMemento object, you can create and initialize multiple FormComponents
 * from the memento.
 * 
 * <pre>
 * 
 *     // assume you have a given form component
 *     FormComponent fc1 = ...;
 * 
 *     FormMemento memento = fc1.getState();
 *     FormComponent fc2 = new FormComponent();
 *     fc2.setState( memento );
 * 
 *     FormComponent fc3 = new FormComponent();
 *     fc3.setState( memento );
 *    
 *     //  fc2 and fc3 are copies of fc1 
 *  
 * </pre>
 * 
 * @author Jeff Tassin
 */
public class FormMemento extends ComponentMemento {

    static final long serialVersionUID = -7808404997780438089L;
    /**
     * The version of this class.
     */
    public static final int VERSION = 9;
    public static final FormMemento EMPTY_FORM_MEMENTO = new FormMemento();
    /**
     * The encoded row specifications used in the FormLayout for this form.
     */
    private String m_row_specs;
    /**
     * The encoded column specifications used in the FormLayout for this form.
     */
    private String m_column_specs;
    /**
     * A list of child component states (ComponentMemento)
     */
    private LinkedList<ComponentMemento> m_components = new LinkedList<ComponentMemento>();
    /**
     * The bean properties for the form such as background color, opaque, fill,
     * etc. This variable will be null for class versions less than 5.
     */
    private PropertiesMemento m_properties_memento;
    /**
     * The relative path that contains this form if it is linked form.
     */
    private String m_path;
    /**
     * A unique id for this form.
     */
    private String m_id;
    /**
     * The cell painters for this form. These are responsible for fill effects
     * for an individual cell.
     */
    private Matrix m_cell_painters;
    /**
     * The row group assignments for the form.
     *
     * @see com.jgoodies.forms.layout.FormLayout#setRowGroups(int[][])
     */
    private FormGroupSet m_row_groups;
    /**
     * The column group assignments for the form.
     *
     * @see com.jgoodies.forms.layout.FormLayout#setColumnGroups(int[][])
     */
    private FormGroupSet m_column_groups;
    /**
     * The form script (since 6 version)
     */
    private String m_script = "";
    private Script m_compiledScript = null;
    private String m_scriptBreakPoints = "";
    private String m_scriptBookmarks = "";
    /**
     * Properties for the form. These are properties such as background color and
     * border that are applied to the form as a whole. m_properties<String,Object>
     * where: String: property name Object: property value (must be serializable)
     *
     * @deprecated As of version 5 of this class. PropertiesMemento is used
     *             instead.
     */
    private HashMap m_properties = new HashMap();

    /**
     * Adds a child component's state to the list of states owned by this
     * memento.
     *
     * @param memento
     *           the state of a Java Bean (BeanComponent) or nested form that is
     *           contained by this form.
     */
    public void addComponent(ComponentMemento memento) {
        m_components.add(memento);
    }

    public ArrayList<Integer> decodeBookmarks() {
        ArrayList<Integer> bookmarks = new ArrayList<Integer>();
        String[] sBookmarks = m_scriptBookmarks.split(";");
        if (sBookmarks != null) {
            for (int i = 0; i < sBookmarks.length; i++) {
                if (sBookmarks[i] != null && !sBookmarks[i].isEmpty()) {
                    try {
                        Integer lbookmark = Integer.valueOf(sBookmarks[i]);
                        bookmarks.add(lbookmark);
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        }
        return bookmarks;
    }

    public HashSet<Integer> decodeBreakPoints() {
        HashSet<Integer> brks = new HashSet<Integer>();
        String[] sBrks = m_scriptBreakPoints.split(";");
        if (sBrks != null) {
            for (int i = 0; i < sBrks.length; i++) {
                if (sBrks[i] != null && !sBrks[i].isEmpty()) {
                    try {
                        Integer lbrk = Integer.valueOf(sBrks[i]);
                        brks.add(lbrk);
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        }
        return brks;
    }

    public static String encodeBreakPoints(HashSet<Integer> aBreakPoints) {
        String lBrkValue = "";
        if (aBreakPoints != null && !aBreakPoints.isEmpty()) {
            Iterator<Integer> bIt = aBreakPoints.iterator();
            if (bIt != null) {
                while (bIt.hasNext()) {
                    Integer bp = bIt.next();
                    if (bp != null) {
                        if (!lBrkValue.isEmpty()) {
                            lBrkValue += ";";
                        }
                        lBrkValue += String.valueOf(bp.intValue());
                    }
                }
            }
        }
        return lBrkValue;
    }

    public static String encodeBookmarks(ArrayList<Integer> aBookmarks) {
        String lBookmarkValue = "";
        if (aBookmarks != null && !aBookmarks.isEmpty()) {
            Iterator<Integer> bIt = aBookmarks.iterator();
            if (bIt != null) {
                while (bIt.hasNext()) {
                    Integer bp = bIt.next();
                    if (bp != null) {
                        if (!lBookmarkValue.isEmpty()) {
                            lBookmarkValue += ";";
                        }
                        lBookmarkValue += String.valueOf(bp.intValue());
                    }
                }
            }
        }
        return lBookmarkValue;
    }

    /**
     * Returns a matrix that defines the painters for individual cells in the
     * form.
     *
     * @return A matrix of cell painters
     */
    public Matrix getCellPainters() {
        return m_cell_painters;
    }

    /**
     * Return the column groups for the form.
     *
     * @return the column groups for the form
     */
    public FormGroupSet getColumnGroups() {
        return m_column_groups;
    }

    /**
     * Return an encoded string of column specs for this form. Each column spec
     * is separated by a comma.
     *
     * @return the encoded ColumnSpecs used by the FormLayout for this form.
     */
    public String getColumnSpecs() {
        return m_column_specs;
    }

    /**
     * Returns a unique id for this form. The id is used primarily in the
     * designer and has no effect during runtime.
     *
     * @return the unique id for this form
     */
    public String getId() {
        return m_id;
    }

    /**
     * Returns properties memento for this form. The PropertiesMemento stores the
     * Java Bean properties and custom properties for a component.
     *
     * @return the properties memento to associated with this form.
     */
    public PropertiesMemento getPropertiesMemento() {
        return m_properties_memento;
    }

    /**
     * @return the form properties
     * @deprecated replaced by getPropertiesMemento. see #getPropertiesMemento().
     */
    public HashMap getProperties() {
        return m_properties;
    }

    /**
     * Returns the relative path where this form is located. This is the same as
     * the package that contains the form.
     *
     * @return the relative path where this form is located.
     */
    public String getRelativePath() {
        return m_path;
    }

    /**
     * Return the row groups for this form.
     *
     * @return the row groups for the form
     */
    public FormGroupSet getRowGroups() {
        return m_row_groups;
    }

    /**
     * Return an encoded string of row specs for this form. Each row spec is
     * separated by a comma.
     *
     * @return the encoded RowSpecs used by the FormLayout for this form.
     */
    public String getRowSpecs() {
        return m_row_specs;
    }

    public String getScript() {
        return m_script;
    }

    public Script getCompiledScript() {
        return m_compiledScript;
    }

    public String getScriptBreakPoints() {
        return m_scriptBreakPoints;
    }

    public String getScriptBookmarks() {
        return m_scriptBookmarks;
    }

    public boolean isEmbedded() {
        return (m_id != null && m_id.startsWith(FormComponent.ID_EMBEDDED_PREFIX));
    }

    public void setScript(String aScript) {
        m_script = aScript;
    }

    public void setScriptBreakPoints(String aScriptBreakPoints) {
        m_scriptBreakPoints = aScriptBreakPoints;
    }

    public void setScriptBookmarks(String aScriptBookmarks) {
        m_scriptBookmarks = aScriptBookmarks;
    }

    /**
     * Returns an iterator to a set of ComponentMemento objects which are
     * contained by this form.
     *
     * @returns an iterator that can be used to iterate over all child
     *          ComponentMemento objects currently contained in this mememnto
     */
    public Iterator<ComponentMemento> iterator() {
        return m_components.iterator();
    }

    public List<ComponentMemento> componentMementos() {
        return m_components;
    }
    /**
     * Used for testing
     */
    @Override
    public void print() {
        System.out.println(" >>>>>>>>>>>>>>>>>  FormMemento state >>>>>>>>>>>>>> ");
        System.out.println("rowSpecs: " + m_row_specs);
        System.out.println("colSpecs: " + m_column_specs);
        System.out.println(" ----------- comp mementos --------- ");
        Iterator iter = m_components.iterator();
        while (iter.hasNext()) {
            ComponentMemento cm = (ComponentMemento) iter.next();
            cm.print();
        }
    }

    /**
     * Sets the cell painters defined in the form.
     *
     * @param painters
     *           A matrix of cell painters
     */
    public void setCellPainters(Matrix painters) {
        m_cell_painters = painters;
    }

    /**
     * Set the column groups for the form
     *
     * @param colgrps
     *           the column groups
     * @see com.jgoodies.forms.layout.FormLayout#setColumnGroups(int[][])
     */
    public void setColumnGroups(FormGroupSet colgrps) {
        m_column_groups = colgrps;
    }

    /**
     * Sets the encoded ColumnSpecs used by the FormLayout for this form.
     *
     * @param colSpecs
     *           a comma separated list of column specs.
     */
    public void setColumnSpecs(String colSpecs) {
        m_column_specs = colSpecs;
    }

    /**
     * Sets the unique id for this form. Ids are only used by the designer and
     * have no effect during runtime.
     *
     * @param id
     *           the id to assign to the form.
     */
    public void setId(String id) {
        m_id = id;
    }

    /**
     * Sets the relative path where this form is located. This is the same as the
     * package that contains the form.
     *
     * @param path
     *           the relative path to set for the form.
     */
    public void setRelativePath(String path) {
        m_path = path;
    }

    /**
     * Set the row groups for the form
     *
     * @param rowgrps
     *           the row groups to set
     * @see com.jgoodies.forms.layout.FormLayout#setRowGroups(int[][])
     */
    public void setRowGroups(FormGroupSet rowgrps) {
        m_row_groups = rowgrps;
    }

    /**
     * Sets the form properties
     *
     * @deprecated As of version 5 of this class.
     * @see #setPropertiesMemento(PropertiesMemento)
     */
    public void setProperties(HashMap props) {
        m_properties = props;
    }

    /**
     * Sets the properties memento for this form. The PropertiesMemento stores
     * standard Java bean properties and custom properties.
     *
     * @param pm
     *           the properties memento to associated with this form.
     */
    public void setPropertiesMemento(PropertiesMemento pm) {
        m_properties_memento = pm;
    }

    /**
     * Sets the RowSpecs used by the FormLayout for this form.
     *
     * @param rowSpecs
     *           a comma separated list of row specs for the form.
     */
    public void setRowSpecs(String rowSpecs) {
        m_row_specs = rowSpecs;
    }

    public void compileScript() {
        ContextFactory cf = ContextFactory.getGlobal();
        Context cx = cf.enterContext();
        try {
            m_compiledScript = cx.compileString(m_script, (m_path != null && !m_path.isEmpty()) ? m_path : m_id, 0, null);
        } finally {
            Context.exit();
        }
    }

    /**
     * Returns the number of child components in this form.
     */
    public int size() {
        return m_components.size();
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        m_id = (String) in.readObject("id", "");
        m_path = (String) in.readObject("path", "");
        if (FormUtils.isDesignMode()) {
            m_path = FormUtils.fixPath(m_path);
        }
        m_row_specs = (String) in.readObject("rowspecs", "");
        m_column_specs = (String) in.readObject("colspecs", "");
        m_components = (LinkedList<ComponentMemento>) in.readObject("components", FormUtils.EMPTY_LIST);

        if (version >= 5) {
            m_properties_memento = (PropertiesMemento) in.readObject("properties", PropertiesMemento.EMPTY_PROPERTIES_MEMENTO);
        } else {
            m_properties = (HashMap) in.readObject("properties", FormUtils.EMPTY_HASH_MAP);
        }

        if (version >= 2) {
            m_cell_painters = (Matrix) in.readObject("cellpainters", Matrix.EMTY_MATRIX);
        }

        if (version >= 3 && version < 7) {
            Object ldummy_focus_policy = in.readObject("focuspolicy", null);
            // not doing focus anymore
        }

        if (version >= 4) {
            m_row_groups = (FormGroupSet) in.readObject("rowgroups", FormGroupSet.EMPTY_FORM_GROUP_SET);
            m_column_groups = (FormGroupSet) in.readObject("colgroups", FormGroupSet.EMPTY_FORM_GROUP_SET);
        }
        if (version >= 6) {
            m_script = (String) in.readObject("script", "");
        }
        if (version >= 8) {
            m_scriptBreakPoints = (String) in.readObject("scriptBreakPoints", "");
        }
        if (version >= 9) {
            m_scriptBookmarks = (String) in.readObject("scriptBookmarks", "");
        }
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(ComponentMemento.class));
        out.writeVersion(VERSION);
        out.writeObject("id", m_id);
        out.writeObject("path", m_path);

        out.writeObject("rowspecs", m_row_specs);
        out.writeObject("colspecs", m_column_specs);
        if (!m_components.isEmpty() || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("components", m_components);
        }
        out.writeObject("properties", m_properties_memento);
        out.writeObject("cellpainters", m_cell_painters);
        if (!m_row_groups.isEmpty() || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("rowgroups", m_row_groups);
        }
        if (!m_column_groups.isEmpty() || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("colgroups", m_column_groups);
        }

        if ((m_script != null && !m_script.isEmpty()) || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("script", m_script);
        }
        if ((m_scriptBreakPoints != null && !m_scriptBreakPoints.isEmpty()) || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("scriptBreakPoints", m_scriptBreakPoints);
        }
        if ((m_scriptBookmarks != null && !m_scriptBookmarks.isEmpty()) || out instanceof JavaExternalizableObjectOutput) {
            out.writeObject("scriptBookmarks", m_scriptBookmarks);
        }
    }
}
