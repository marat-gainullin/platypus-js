/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
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
        try {
            PlatypusServerCore serverCore = PlatypusServerCore.getInstance();
            if (serverCore != null && serverCore.getSessionManager() != null) {
                Session session = serverCore.getSessionManager().create(se.getSession().getId());
                Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.INFO, "Platypus session opened. Session id: {0}", session.getId());
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            PlatypusServerCore serverCore = PlatypusServerCore.getInstance();
            if (serverCore != null && serverCore.getSessionManager() != null) {
                Session removed = serverCore.getSessionManager().remove(se.getSession().getId());
                Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.INFO, "Platypus session closed. Session id: {0}", removed.getId());
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
