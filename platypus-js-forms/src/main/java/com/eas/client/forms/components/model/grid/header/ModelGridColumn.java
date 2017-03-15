package com.eas.client.forms.components.model.grid.header;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.Color;
import java.awt.Font;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Марат
 */
public class ModelGridColumn extends GridColumnsNode implements HasPublished {

    protected JSObject published;

    protected String field;
    protected String sortField;

    @ScriptFunction
    public ModelGridColumn() {
        super();
        setTableColumn(new ModelColumn());
    }

    @Override
    public GridColumnsNode lightCopy() {
        GridColumnsNode copied = new ModelGridColumn();
        copied.lightAssign(this);
        return copied;
    }

    @Override
    public GridColumnsNode copy() throws Exception {
        GridColumnsNode copied = new ModelGridColumn();
        copied.assign(this);
        return copied;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Returns script handler, used for calculate cell's data, display value and style attributes.\n"
            + " */")
    public JSObject getOnRender() {
        return ((ModelColumn) getTableColumn()).getOnRender();
    }

    @ScriptFunction
    public void setOnRender(JSObject aValue) {
        ((ModelColumn) getTableColumn()).setOnRender(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Returns script handler, used for select a value of the cell.\n"
            + " */")
    public JSObject getOnSelect() {
        return ((ModelColumn) getTableColumn()).getOnSelect();
    }

    @ScriptFunction
    public void setOnSelect(JSObject aValue) throws Exception {
        ((ModelColumn) getTableColumn()).setOnSelect(aValue);
    }

    @Undesignable
    @ScriptFunction
    public ModelWidget getEditor() {
        return ((ModelColumn) getTableColumn()).getEditor();
    }

    @ScriptFunction
    public void setEditor(ModelWidget aEditor) {
        ((ModelColumn) getTableColumn()).setEditor(aEditor);
    }

    @Undesignable
    @ScriptFunction
    public ModelWidget getView() {
        return ((ModelColumn) getTableColumn()).getView();
    }

    @ScriptFunction
    public void setView(ModelWidget aEditor) {
        ((ModelColumn) getTableColumn()).setView(aEditor);
    }

    @ScriptFunction(params = {"node"})
    @Override
    public void removeColumnNode(GridColumnsNode aNode) {
        super.removeColumnNode(aNode);
    }

    @ScriptFunction(params = {"node"})
    @Override
    public void addColumnNode(GridColumnsNode aNode) {
        super.addColumnNode(aNode);
    }

    @ScriptFunction(params = {"position", "node"})
    @Override
    public void insertColumnNode(int atIndex, GridColumnsNode aNode) {
        super.insertColumnNode(atIndex, aNode);
    }

    @ScriptFunction
    public GridColumnsNode[] columnNodes() {
        return children.toArray(new GridColumnsNode[]{});
    }

    @ScriptFunction
    @Designable(category = "model")
    public String getField() {
        return field;
    }

    @ScriptFunction
    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            field = aValue;
            ((ModelColumn) tableColumn).setField(field);
        }
    }

    @ScriptFunction
    @Designable(category = "model")
    public String getSortField() {
        return sortField;
    }

    @ScriptFunction
    public void setSortField(String aValue) {
        if (sortField == null ? aValue != null : !sortField.equals(aValue)) {
            sortField = aValue;
            ((ModelColumn) tableColumn).setSortField(sortField);
        }
    }

    @ScriptFunction
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public void setWidth(int aValue) {
        super.setWidth(aValue);
    }

    @ScriptFunction
    @Override
    public int getPreferredWidth() {
        return super.getPreferredWidth();
    }

    @ScriptFunction
    @Override
    public void setPreferredWidth(int aValue) {
        super.setPreferredWidth(aValue);
    }

    @ScriptFunction
    @Override
    public int getMinWidth() {
        return super.getMinWidth();
    }

    @ScriptFunction
    @Override
    public void setMinWidth(int aValue) {
        super.setMinWidth(aValue);
    }

    @ScriptFunction
    @Override
    public int getMaxWidth() {
        return super.getMaxWidth();
    }

    @ScriptFunction
    @Override
    public void setMaxWidth(int aValue) {
        super.setMaxWidth(aValue);
    }

    @ScriptFunction
    @Override
    public boolean isReadonly() {
        return super.isReadonly();
    }

    @ScriptFunction
    @Override
    public void setReadonly(boolean aValue) {
        super.setReadonly(aValue);
    }

    @ScriptFunction
    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @ScriptFunction
    @Override
    public void setVisible(boolean aValue) {
        super.setVisible(aValue);
    }

    @ScriptFunction
    @Override
    public boolean isMovable() {
        return super.isMovable();
    }

    @ScriptFunction
    @Override
    public void setMovable(boolean aValue) {
        super.setMovable(aValue);
    }

    @ScriptFunction
    @Override
    public boolean isResizable() {
        return super.isResizable();
    }

    @ScriptFunction
    @Override
    public void setResizable(boolean aValue) {
        super.setResizable(aValue);
    }

    @ScriptFunction
    @Override
    public boolean isSortable() {
        return super.isSortable();
    }

    @ScriptFunction
    @Override
    public void setSortable(boolean aValue) {
        super.setSortable(aValue);
    }

    @ScriptFunction
    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @ScriptFunction
    @Override
    public void setTitle(String aTitle) {
        super.setTitle(aTitle);
    }

    @ScriptFunction
    @Override
    public Color getBackground() {
        return super.getBackground();
    }

    @ScriptFunction
    @Override
    public void setBackground(Color aValue) {
        super.setBackground(aValue);
    }

    @ScriptFunction
    @Override
    public Color getForeground() {
        return super.getForeground();
    }

    @ScriptFunction
    @Override
    public void setForeground(Color aValue) {
        super.setForeground(aValue);
    }

    @ScriptFunction
    @Override
    public Font getFont() {
        return super.getFont();
    }

    @ScriptFunction
    @Override
    public void setFont(Font aValue) {
        super.setFont(aValue);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject jsColumn) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = jsColumn;
        ((ModelColumn) getTableColumn()).setEventsSource(jsColumn);
        /*
         if(view != null)
         view.injectPublished(published);
         if(editor != null)
         editor.injectPublished(published);
         */
    }

    private static final String SORT = ""
            + "/**\n"
            + " * Column sort, works only in HTML5\n"
            + " */";

    @ScriptFunction(jsDoc = SORT)
    public void sort() {

    }
    private static final String SORT_DESC = ""
            + "/**\n"
            + " * Descending column sort, works only in HTML5\n"
            + " */";

    @ScriptFunction(jsDoc = SORT_DESC)
    public void sortDesc() {

    }
    private static final String UNSORT = ""
            + "/**\n"
            + " * Clears sort column, works only in HTML5\n"
            + " */";

    @ScriptFunction(jsDoc = UNSORT)
    public void unsort() {

    }
}
