/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.client.geo.RowsetFeatureDescriptor;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author pk
 */
class GeometryBindingComboModel implements ComboBoxModel
{
    protected final static Class[] AVAILABLE_BINDINGS =
    {
        com.vividsolutions.jts.geom.Polygon.class,
        com.vividsolutions.jts.geom.Point.class,
        com.vividsolutions.jts.geom.MultiPoint.class,
        com.vividsolutions.jts.geom.MultiPolygon.class,
        com.vividsolutions.jts.geom.LineString.class,
        com.vividsolutions.jts.geom.MultiLineString.class,
    };
    private Object selectedItem;

    public GeometryBindingComboModel() throws ClassNotFoundException
    {
        selectedItem = Class.forName(new RowsetFeatureDescriptor().getGeometryBinding());
    }

    @Override
    public void setSelectedItem(Object anItem)
    {
        checkAvailable(anItem);
        selectedItem = anItem;
    }

    @Override
    public Object getSelectedItem()
    {
        return selectedItem;
    }

    @Override
    public int getSize()
    {
        return AVAILABLE_BINDINGS.length;
    }

    @Override
    public Object getElementAt(int index)
    {
        return AVAILABLE_BINDINGS[index];
    }

    @Override
    public void addListDataListener(ListDataListener l)
    {
    }

    @Override
    public void removeListDataListener(ListDataListener l)
    {
    }

    private void checkAvailable(Object anItem)
    {
        for (Class c : AVAILABLE_BINDINGS)
        {
            if (c.equals(anItem))
                return;
        }
        throw new IllegalArgumentException(String.format("Unknown geometry binding %s", String.valueOf(anItem)));
    }
}
