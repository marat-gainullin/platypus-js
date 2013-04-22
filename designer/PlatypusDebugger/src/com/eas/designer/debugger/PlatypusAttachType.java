/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.SettingsMBean;
import com.eas.designer.debugger.ui.AttachToProcessCustomizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JComponent;
import org.netbeans.spi.debugger.ui.AttachType;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
@AttachType.Registration(displayName = "#attachType")
public class PlatypusAttachType extends AttachType {

    protected static String achieveDbSettingsData(String aHost, int aPort) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + aHost + ":" + String.valueOf(aPort) + "/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection jmxConnection = jmxc.getMBeanServerConnection();
        try {
            ObjectName mBeanSettingsName = new ObjectName(SettingsMBean.SETTINGS_MBEAN_NAME);
            SettingsMBean settings = JMX.newMBeanProxy(jmxConnection, mBeanSettingsName, SettingsMBean.class);
            return settings.getSettingsData();
        } finally {
            jmxc.close();
        }
    }

    protected class AttachTypeController implements Controller, PropertyChangeListener {

        protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

        @Override
        public boolean ok() {
            if (isValid()) {
                try {
                    DebuggerEnvironment env = new DebuggerEnvironment();
                    env.host = settings.getHost();
                    env.port = settings.getPort();
                    DebuggerUtils.attachDebugger(env);
                    return true;
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean cancel() {
            return true;
        }

        @Override
        public boolean isValid() {
            return settings.getHost() != null && !settings.getHost().isEmpty() && settings.getPort() > 0;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            changeSupport.addPropertyChangeListener(l);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            changeSupport.removePropertyChangeListener(l);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            changeSupport.firePropertyChange(Controller.PROP_VALID, !isValid(), isValid());
        }
    }
    protected AttachSettings settings = new AttachSettings();
    protected AttachToProcessCustomizer customizer = new AttachToProcessCustomizer(settings);
    protected AttachTypeController controller = new AttachTypeController();

    public PlatypusAttachType() {
        super();
        settings.getChangeSupport().addPropertyChangeListener(controller);
    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
