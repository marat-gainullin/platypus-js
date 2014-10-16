/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.server.PlatypusServerCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author mg
 */
public class PlatypusSessionsSynchronizer implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //NO OP
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String platypusSessionId = (String) se.getSession().getAttribute(PlatypusHttpServlet.PLATYPUS_SESSION_ATTR_NAME);
        if (platypusSessionId != null) {
            PlatypusServerCore serverCore = (PlatypusServerCore) se.getSession().getAttribute(PlatypusHttpServlet.PLATYPUS_SERVER_CORE_ATTR_NAME);
            if (serverCore != null && serverCore.getSessionManager() != null) {
                serverCore.getSessionManager().remove(platypusSessionId);
                Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.INFO, "Platypus session closed id: {0}", platypusSessionId);
            }
        }
        se.getSession().removeAttribute(PlatypusHttpServlet.PLATYPUS_SESSION_ATTR_NAME);
        se.getSession().removeAttribute(PlatypusHttpServlet.PLATYPUS_SERVER_CORE_ATTR_NAME);
    }
}
