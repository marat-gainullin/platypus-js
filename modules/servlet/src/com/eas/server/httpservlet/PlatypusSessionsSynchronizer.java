/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.server.Session;
import com.eas.server.SessionManager;
import static com.eas.server.httpservlet.PlatypusHttpServlet.PLATYPUS_SESSION_ID_ATTR_NAME;
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
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            String pSessionId = (String) se.getSession().getAttribute(PLATYPUS_SESSION_ID_ATTR_NAME);
            if (pSessionId != null) {
                Session removed = SessionManager.Singleton.instance.remove(pSessionId);
                if (removed != null) {
                    Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.INFO, "Platypus session closed. Session id: {0}", removed.getId());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusSessionsSynchronizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // no op. Scripts.Space is appended to session by servlet code, due to parallel and sessions replication problems.
    }
}
