/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import static com.eas.server.handlers.PositioningPacketReciever.RECIEVER_METHOD_NAME;
import static com.eas.server.httpservlet.PlatypusHttpServlet.PLATYPUS_SERVER_CORE_ATTR_NAME;
import static com.eas.server.httpservlet.PlatypusHttpServlet.PLATYPUS_STARTUP_MODULE_ATTR_NAME;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author mg
 */
public class PlatypusStartUpListener implements ServletContextListener {

    public static final String RUN_ON_STARTUP_METHOD_NAME = "execute";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Object moduleObject = sce.getServletContext().getAttribute(PLATYPUS_STARTUP_MODULE_ATTR_NAME);
        if (moduleObject != null && moduleObject instanceof String) {
            String moduleName = (String)moduleObject;
            Object coreObject = sce.getServletContext().getAttribute(PLATYPUS_SERVER_CORE_ATTR_NAME);
            if (coreObject != null && coreObject instanceof PlatypusServerCore) {
                PlatypusServerCore serverCore = (PlatypusServerCore) coreObject;
                Session session = serverCore.getSessionManager().getSystemSession();
                Scripts.LocalContext context = new Scripts.LocalContext(session.getPrincipal(), session);
                session.getSpace().process(context, () -> {
                    serverCore.executeMethod(moduleName, RUN_ON_STARTUP_METHOD_NAME, new Object[]{}, true, (Object result) -> {
                        Logger.getLogger(PlatypusStartUpListener.class.getName()).log(Level.INFO
                                , "StartUp method " + RECIEVER_METHOD_NAME + " in module {0} called successfully."
                                , moduleName);
                    }, (Exception ex) -> {
                        Logger.getLogger(PlatypusStartUpListener.class.getName()).log(Level.WARNING, null, ex);
                    });
                });
            } else {
                Logger.getLogger(PlatypusStartUpListener.class.getName()).log(Level.WARNING, "Can't get PlatypusServerCore instance from context.");
            }
        } else {
            Logger.getLogger(PlatypusStartUpListener.class.getName()).log(Level.INFO, "No module to run on startup.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
