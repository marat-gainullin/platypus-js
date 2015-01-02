/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasValue;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Interface for scalar data-aware widget. Scalar widget views and edits a value
 * from particular object and particular field of a js object.
 *
 * @param <V>
 * @author mg
 */
public interface ModelWidget<V> extends TableCellRenderer, TableCellEditor, HasValue<V> {

    public static Object getPathData(JSObject anElement, String aPath) {
        if (aPath != null && !aPath.isEmpty()) {
            JSObject target = anElement;
            String[] path = aPath.split(".");
            String propName = path[0];
            for (int i = 1; i < path.length; i++) {
                Object oTarget = anElement.getMember(propName);
                propName = path[i];
                if (!(oTarget instanceof JSObject)) {
                    propName = null;
                    break;
                } else {
                    target = (JSObject) oTarget;
                }
            }
            Object value = null;
            if (propName != null) {
                value = ScriptUtils.toJava(target.getMember(propName));
            } else {
                Logger.getLogger(ModelWidget.class.getName()).log(Level.SEVERE, PROPERTY_PATH_MISSING_MSG, aPath);
            }
            return value;
        } else {
            return null;
        }
    }

    public static void setPathData(JSObject anElement, String aPath, Object aValue) {
        if (aPath != null && !aPath.isEmpty()) {
            JSObject target = anElement;
            String[] path = aPath.split(".");
            String propName = path[0];
            for (int i = 1; i < path.length; i++) {
                Object oTarget = anElement.getMember(propName);
                propName = path[i];
                if (!(oTarget instanceof JSObject)) {
                    propName = null;
                    break;
                } else {
                    target = (JSObject) oTarget;
                }
            }
            if (propName != null) {
                target.setMember(propName, ScriptUtils.toJs(aValue));
            } else {
                Logger.getLogger(ModelWidget.class.getName()).log(Level.SEVERE, PROPERTY_PATH_MISSING_MSG, aPath);
            }
        } else {
            Logger.getLogger(ModelWidget.class.getName()).log(Level.SEVERE, "Property path missing");
        }
    }
    static final String PROPERTY_PATH_MISSING_MSG = "Property path: {0} doesn't exist.";

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
