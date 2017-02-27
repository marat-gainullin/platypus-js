/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

import com.bearsoft.gui.grid.data.TreedModel;
import com.bearsoft.gui.grid.events.data.ElementsAddedEvent;
import com.bearsoft.gui.grid.events.data.ElementsDataChangedEvent;
import com.bearsoft.gui.grid.events.data.ElementsRemovedEvent;
import com.bearsoft.gui.grid.events.data.TreedModelListener;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ArrayTreedModel extends ArrayModel implements TreedModel<JSObject> {

    protected Set<TreedModelListener<JSObject>> listeners = new HashSet<>();
    protected String parentField;
    protected String childrenField;

    public ArrayTreedModel(TableColumnModel aColumns, JSObject aData, String aParentField, String aChildrenField, JSObject aOnRender) {
        super(aColumns, aData, aOnRender);
        parentField = aParentField;
        childrenField = aChildrenField;
    }

    public String getParentField() {
        return parentField;
    }

    @Override
    public JSObject getParentOf(JSObject anElement) {
        try {
            Object oChildren = ModelWidget.getPathData(anElement, parentField);
            if (oChildren instanceof JSObject) {
                return (JSObject) oChildren;
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(ArrayTreedModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<JSObject> getChildrenOf(JSObject anElement) {
        List<JSObject> children = new ArrayList<>();
        if (data != null) {
            if (anElement != null) {
                try {
                    Object oChildren = ModelWidget.getPathData(anElement, childrenField);
                    if (oChildren instanceof JSObject) {
                        JSObject jsChildren = (JSObject) oChildren;
                        int length = JSType.toInteger(jsChildren.getMember("length"));
                        if (length > 0 && length != Integer.MAX_VALUE) {
                            for (int i = 0; i < length; i++) {
                                Object oChild = jsChildren.getSlot(i);
                                if (oChild instanceof JSObject) {
                                    children.add((JSObject) oChild);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ArrayTreedModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                int length = JSType.toInteger(data.getMember("length"));
                for (int i = 0; i < length; i++) {
                    Object oItem = data.getSlot(i);
                    if (oItem instanceof JSObject) {
                        Object oParent = ModelWidget.getPathData((JSObject) oItem, parentField);
                        if (!(oParent instanceof JSObject)) {
                            children.add((JSObject) oItem);
                        }
                    }
                }
            }
        }
        return children;
    }

    @Override
    public boolean isLeaf(JSObject anElement) {
        List<JSObject> children = getChildrenOf(anElement);
        return children.isEmpty();
    }

    @Override
    public void addTreedModelListener(TreedModelListener<JSObject> aListener) {
        listeners.add(aListener);
    }

    @Override
    public void removeTreedModelListener(TreedModelListener<JSObject> aListener) {
        listeners.remove(aListener);
    }

    public void fireElementsAdded(List<JSObject> aRemoved) {
        ElementsAddedEvent<JSObject> ev = new ElementsAddedEvent<>(aRemoved);
        listeners.stream().forEach((l) -> {
            l.elementsAdded(ev);
        });
    }

    public void fireElementsRemoved(List<JSObject> aRemoved) {
        ElementsRemovedEvent<JSObject> ev = new ElementsRemovedEvent<>(aRemoved);
        listeners.stream().forEach((l) -> {
            l.elementsRemoved(ev);
        });
    }

    @Override
    public void fireColumnFieldChanged(ModelColumn aColumn) {
        ElementsDataChangedEvent<JSObject> ev = new ElementsDataChangedEvent<>(null, aColumn.getModelIndex());
        listeners.stream().forEach((l) -> {
            l.elementsDataChanged(ev);
        });
    }

    @Override
    public void fireElementsChanged() {
        ElementsDataChangedEvent<JSObject> ev = new ElementsDataChangedEvent<>();
        listeners.stream().forEach((l) -> {
            l.elementsDataChanged(ev);
        });
    }

    @Override
    public void fireElementsDataChanged() {
        ElementsDataChangedEvent<JSObject> ev = new ElementsDataChangedEvent<>();
        listeners.stream().forEach((l) -> {
            l.elementsDataChanged(ev);
        });
    }

    public void fireElementsChanged(JSObject anElement, String aElementsField, boolean aAjusting) {
        if (aElementsField == null ? parentField == null : aElementsField.equals(parentField)) {
            listeners.stream().forEach((l) -> {
                l.elementsStructureChanged();
            });
        } else {
            for (int i = 0; i < columns.getColumnCount(); i++) {
                ModelColumn col = (ModelColumn) columns.getColumn(i);
                if (aElementsField.equals(col.getField())) {
                    ElementsDataChangedEvent<JSObject> ev = new ElementsDataChangedEvent<>(Collections.singletonList(anElement), col.getModelIndex(), aAjusting);
                    listeners.stream().forEach((l) -> {
                        l.elementsDataChanged(ev);
                    });
                }
            }
        }
    }
}
