package com.eas.client.forms.components.model.grid.header;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.eas.client.forms.components.model.grid.columns.ServiceColumn;
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
public class ServiceGridColumn extends GridColumnsNode implements HasPublished {

    protected JSObject published;

    @ScriptFunction
    public ServiceGridColumn() {
        super();
        setTableColumn(new ServiceColumn());
        resizable = false;
        setTitle("\\");
    }

    @Override
    public GridColumnsNode lightCopy() {
        GridColumnsNode copied = new ServiceGridColumn();
        copied.lightAssign(this);
        return copied;
    }

    @Override
    public GridColumnsNode copy() throws Exception {
        GridColumnsNode copied = new ServiceGridColumn();
        copied.assign(this);
        return copied;
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
    }
}
