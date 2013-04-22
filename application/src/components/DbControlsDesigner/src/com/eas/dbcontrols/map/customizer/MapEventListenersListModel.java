/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.undo.UndoManager;

/**
 *
 * @author pk
 */
public class MapEventListenersListModel implements ComboBoxModel
{
    private final static List<String> elements = Arrays.asList(new String[]
            {
                "Item 1", "Item 2", "Item 3", "Item 4"
            });
    private final List<ListDataListener> listeners = new ArrayList<>();
    private final UndoManager undo;
    private final DbMapDesignInfo designInfo;

    public MapEventListenersListModel(UndoManager undo, DbMapDesignInfo designInfo)
    {
        this.undo = undo;
        this.designInfo = designInfo;
    }

    public void setSelectedItem(Object anItem)
    {
        if (anItem == null || elements.contains((String) anItem))
        {
            try
            {
                final String newValue = (String) anItem;
                final String oldValue = designInfo.getMapEventListener();
                if (newValue == null && oldValue != null || newValue != null && !newValue.equals(oldValue))
                {
                    final ModifyBeanPropertyEdit<String> edit = new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_MAP_EVENT_LISTENER, oldValue, newValue);
                    Logger.getLogger(DbMapCustomizer.class.getName()).finest(String.format("Setting map event listener to %s", newValue));
                    designInfo.setMapEventListener(newValue);
                    undo.addEdit(edit);
                }
            } catch (NoSuchMethodException ex)
            {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            throw new IllegalArgumentException("Unknown element " + anItem);
    }

    public Object getSelectedItem()
    {
        return designInfo.getMapEventListener();
    }

    public int getSize()
    {
        return elements.size();
    }

    public Object getElementAt(int index)
    {
        return elements.get(index);
    }

    public void addListDataListener(ListDataListener l)
    {
        synchronized (listeners)
        {
            listeners.add(l);
        }
    }

    public void removeListDataListener(ListDataListener l)
    {
        synchronized (listeners)
        {
            listeners.remove(l);
        }
    }

    protected void fireContentsChanged(int index0, int index1)
    {
        ListDataListener[] ls;
        synchronized (listeners)
        {
            ls = new ListDataListener[listeners.size()];
            ls = listeners.toArray(ls);
        }
        final ListDataEvent ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
        for (ListDataListener l : ls)
            l.contentsChanged(ev);
    }

    protected void fireIntervalAdded(int index0, int index1)
    {
        ListDataListener[] ls;
        synchronized (listeners)
        {
            ls = new ListDataListener[listeners.size()];
            ls = listeners.toArray(ls);
        }
        final ListDataEvent ev = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
        for (ListDataListener l : ls)
            l.intervalAdded(ev);
    }

    protected void fireIntervalRemoved(int index0, int index1)
    {
        ListDataListener[] ls;
        synchronized (listeners)
        {
            ls = new ListDataListener[listeners.size()];
            ls = listeners.toArray(ls);
        }
        final ListDataEvent ev = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
        for (ListDataListener l : ls)
            l.intervalRemoved(ev);
    }
}
