/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mycompany.installer.wizard.components.panels;

import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;

/**
 *
 * @author jskonst
 */
public class Utils {

    public static boolean isJavaVersion(String aJavaRequired, String aJavaActual) {

//        String[] javaRequired = (ResourceUtils.getString(WelcomePanel.class,
//                    "WP.required.java.text"));
//        String[] javaActual = System.getProperty("java.runtime.version").split("\\.|_|-b");
        String[] javaRequired = aJavaRequired.split("\\.|_|-b");
        int majorRequired = Integer.parseInt(javaRequired[1]);
        int minorRequired = Integer.parseInt(javaRequired[2]);
        int updateRequired = Integer.parseInt(javaRequired[3]);

        String[] javaActual = aJavaActual.split("\\.|_|-b");
        int majorActual = Integer.parseInt(javaActual[1]);
        int minorActual = Integer.parseInt(javaActual[2]);
        int updateActual = Integer.parseInt(javaActual[3]);

        if (majorActual >= majorRequired) {
            if (majorActual == majorRequired) {
                if (minorActual >= minorRequired) {
                    if (minorActual == minorRequired) {
                        if (updateActual >= updateRequired) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
