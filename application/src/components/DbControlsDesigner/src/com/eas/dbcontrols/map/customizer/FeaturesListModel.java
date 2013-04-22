/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractListModel;

/**
 *
 * @author pk
 */
public class FeaturesListModel extends AbstractListModel implements PropertyChangeListener
{
    private DbMapDesignInfo designInfo;

    public DbMapDesignInfo getDesignInfo()
    {
        return designInfo;
    }

    public void setDesignInfo(DbMapDesignInfo aValue)
    {
        if (designInfo != aValue)
        {
            if (designInfo != null)
            {
                designInfo.removePropertyChangeListener(this);
                fireIntervalRemoved(this, 0, this.designInfo.getFeatures().size());
            }
            designInfo = aValue;
            if (designInfo != null)
            {
                designInfo.addPropertyChangeListener(this);
                fireIntervalAdded(this, 0, designInfo.getFeatures().size());
            }
        }
    }

    @Override
    public int getSize()
    {
        return designInfo == null ? 0 : designInfo.getFeatures().size();
    }

    @Override
    public Object getElementAt(int index)
    {
        return designInfo == null ? null : designInfo.getFeatures().get(index);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (DbMapDesignInfo.PROP_FEATURES.equals(evt.getPropertyName()))
        {
            fireContentsChanged(this, 0, getSize()-1);
        }
    }
}
