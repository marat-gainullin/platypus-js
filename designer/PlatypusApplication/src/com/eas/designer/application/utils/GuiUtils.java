/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import javax.swing.SwingUtilities;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;

/**
 *
 * @author vv
 */
public class GuiUtils {
        
    public static boolean openDocument(final DataObject dataObject) {
        assert dataObject != null;
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                public @Override
                void run() {
                    doOpenDocument(dataObject);
                }
            });
            return true; // not exactly accurate, but....
        }
        return doOpenDocument(dataObject);
    }

    private static boolean doOpenDocument(DataObject dataObject) {
        if (dataObject != null) {
            OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
            if (oc != null) {
                oc.open();
                return true;
            }
        }
        return false;
    }
}
