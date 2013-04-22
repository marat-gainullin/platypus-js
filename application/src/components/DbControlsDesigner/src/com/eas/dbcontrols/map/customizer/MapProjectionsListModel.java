/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationMethod;

/**
 *
 * @author pk
 */
public class MapProjectionsListModel implements ComboBoxModel
{
    private final MathTransformFactory mtFactory;
    private final List<ListDataListener> listeners = new ArrayList<>();
    private List<OperationMethod> projections = new ArrayList<>();
    private OperationMethod selectedProjection;

    public MapProjectionsListModel(MathTransformFactory mathTransformFactory)
    {
        this.mtFactory = mathTransformFactory;
        loadProjections();
    }

    public void setSelectedItem(Object anItem)
    {
        if (anItem == null)
            selectedProjection = null;
        else
        {
            if (!(anItem instanceof OperationMethod))
                throw new IllegalArgumentException("!instanceof OperationMethod: " + anItem);
            if (!projections.contains((OperationMethod) anItem))
                throw new IllegalArgumentException("Not in items list: " + anItem);
            this.selectedProjection = (OperationMethod) anItem;
        }
    }

    public Object getProjection(String name)
    {
        if (name == null)
            return null;
        for (OperationMethod method : projections)
            if (name.equals(method.getName().getCode()))
                return method;
        return name;
    }

    public OperationMethod getSelectedItem()
    {
        return selectedProjection;
    }

    public int getSize()
    {
        return projections.size();
    }

    public OperationMethod getElementAt(int index)
    {
        return projections.get(index);
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

    private void loadProjections()
    {
        final Set<OperationMethod> availableMethods = mtFactory.getAvailableMethods(null);
        projections = new ArrayList<>(availableMethods.size());
        for (OperationMethod method : availableMethods)
        {
            if (method.toString().startsWith("PROJECTION"))
            {
                projections.add(method);
            }
        }
        Collections.sort(projections, new Comparator<OperationMethod>()
        {
            public int compare(OperationMethod o1, OperationMethod o2)
            {
                return o1.getName().getCode().compareTo(o2.getName().getCode());
            }
        });
    }
}
