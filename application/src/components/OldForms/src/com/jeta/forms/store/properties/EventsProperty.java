/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Marat
 */
public class EventsProperty extends JETAProperty{

    static final long serialVersionUID = -7709719637183198546L;

    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;


    public static final String EVENTS_PROPERTY_NAME = "events";
    /**
     * The name for this property
     */
    private String         m_name = EVENTS_PROPERTY_NAME;

    
    private HashMap<String, String> m_events = new HashMap<String, String>();

    public String getEventHandler(String event) {
        return m_events.get(event);
    }

    public void setEventHandler(String event, String handler) {
        m_events.put(event, handler);
    }

    @Override
    public void setValue(Object obj) {
        if(obj instanceof EventsProperty)
            m_events.putAll(((EventsProperty)obj).getEvents());
    }

    @Override
    public void updateBean(JETABean jbean) {
        if(jbean != null)
            jbean.setEvents(this);
    }

    public HashMap<String, String> getEvents()
    {
        return m_events; 
    }
    
    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public boolean isPreferred() {
        return false;
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read( in.getSuperClassInput() );
        int version = in.readVersion();
        m_events = (HashMap<String, String>)in.readObject(m_name, FormUtils.EMPTY_STRING_HASH_MAP);
    }

    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        out.writeObject(m_name, m_events);
    }
}
