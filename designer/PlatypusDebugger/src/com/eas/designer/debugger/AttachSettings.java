/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author mg
 */
public class AttachSettings {

    protected int port = 8900;
    protected String host = "localhost";
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public AttachSettings() {
        super();
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String aValue) {
        if (host == null ? aValue != null : !host.equals(aValue)) {
            String oldValue = host;
            host = aValue;
            changeSupport.firePropertyChange("host", oldValue, host);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int aValue) {
        if (port != aValue) {
            int oldValue = port;
            port = aValue;
            changeSupport.firePropertyChange("port", oldValue, port);
        }
    }
}
