/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasValue;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Interface for scalar data-aware widget. Scalar widget views and edits a
 * value from particular object and particular field of a js object.
 *
 * @param <V>
 * @author mg
 */
public interface ModelWidget<V> extends TableCellRenderer, TableCellEditor, HasValue<V> {

    public boolean isFieldContentModified();

    public void injectPublished(JSObject aPublished);

    public JSObject getOnRender();

    public void setOnRender(JSObject aValue);

    public JSObject getOnSelect();

    public void setOnSelect(JSObject aValue);

    public boolean isSelectOnly();

    public void setSelectOnly(boolean aValue);
    
    public JSObject getData();
    
    public void setData(JSObject aData);
    
    public String getField();
    
    public void setField(String aFieldPath) throws Exception;
    
}
