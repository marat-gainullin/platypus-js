/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer;

import com.eas.dbcontrols.DbControlsUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public class EmbeddedControlComboModel implements ComboBoxModel
{
    protected Set<ListDataListener> listeners = new HashSet<>();
    protected Class<?> selected = null;
    protected Class<?>[] classes = null;

    public EmbeddedControlComboModel()
    {
        super();
    }

    public void setFieldType(int type)
    {
        classes = DbControlsUtils.getCompatibleControls(type);
        fireDataChanaged();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(anItem == null || anItem instanceof Class<?>)
            selected = (Class<?>)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize()
    {
        return classes != null?classes.length:0;
    }

    @Override
    public Object getElementAt(int index)
    {
        if(classes != null && index >= 0 && index < classes.length)
            return classes[index];
        return null;
    }

    protected void fireDataChanaged()
    {
        Iterator<ListDataListener> lIt = listeners.iterator();
        if(lIt != null)
        {
            while(lIt.hasNext())
            {
                ListDataListener l = lIt.next();
                if(l != null)
                    l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()-1));
            }
        }
    }

    @Override
    public void addListDataListener(ListDataListener l)
    {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l)
    {
        listeners.remove(l);
    }

}
