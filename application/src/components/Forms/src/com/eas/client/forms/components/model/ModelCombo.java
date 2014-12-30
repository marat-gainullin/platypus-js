/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VComboBox;
import com.eas.design.Designable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelCombo extends ModelComponentDecorator<VComboBox, Object> implements HasPublished, HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that combines a button or editable field and a drop-down list.\n"
            + " */";

    protected JSObject displayList;
    protected JSObject values;
    protected String displayField;

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelCombo() {
        super();
        setDecorated(new VComboBox());
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

    private static final String DISPLAY_LIST_JSDOC = ""
            + "/**\n"
            + "* List of displayed options in a dropdown list of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = DISPLAY_LIST_JSDOC)
    @Designable(category = "model")
    public JSObject getDisplayList() {
        return displayList;
    }

    @ScriptFunction
    public void setDisplayList(JSObject aValue) throws Exception {
        if (displayList != aValue) {
            displayList = aValue;
            decorated.revalidate();
            decorated.repaint();
        }
    }

    private static final String DISPLAY_FIELD_JSDOC = ""
            + "/**\n"
            + "* Display field of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = DISPLAY_FIELD_JSDOC)
    @Designable(category = "model")
    public String getDisplayField() {
        return displayField;
    }

    @ScriptFunction
    public void setDisplayField(String aField) throws Exception {
        if (displayField == null ? aField != null : !displayField.equals(aField)) {
            displayField = aField;
            decorated.revalidate();
            decorated.repaint();
        }
    }

    private static final String LIST_JSDOC = ""
            + "/**\n"
            + "* Determines if component shown as a list.\n"
            + "*/";

    protected boolean list = true;

    @ScriptFunction(jsDoc = LIST_JSDOC)
    public boolean getList() throws Exception {
        return list;
    }

    @ScriptFunction
    public void setList(boolean aValue) throws Exception {
        if (list != aValue) {
            list = aValue;
            decorated.revalidate();
            decorated.repaint();
        }
    }

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    @Override
    public boolean getEditable() {
        return decorated.isEditable();
    }

    @ScriptFunction
    @Override
    public void setEditable(boolean aValue) {
        decorated.setEditable(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        decorated.setText(aValue);
    }

    @ScriptFunction
    @Override
    public String getEmptyText() {
        return decorated.getEmptyText();
    }

    @ScriptFunction
    @Override
    public void setEmptyText(String aValue) {
        decorated.setEmptyText(aValue);
    }

    private String renderValue(Object aValue) {
        return "[fix me]";
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        remove(decorated);
        String rendered = renderValue(getValue());
        JLabel rendererLine = new JLabel(rendered);
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
