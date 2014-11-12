/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

import org.openide.LifecycleManager;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jskonst
 */
@ServiceProvider(service = LifecycleManager.class, position = 1)
public class ShutdownLifecycleManager extends LifecycleManager {

    @Override
    public void saveAll() {
        for (LifecycleManager manager : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
            if (manager != this) {
                manager.saveAll();
            }
        }
    }

    @Override
    public void exit() {
        Process updater = PlatypusPlatform.getUpdaterProcess();
            if (updater != null) {
                updater.destroy();
            }
//        DataObject[] modifs = DataObject.getRegistry().getModified();
//        if (modifs.length == 0) {
//            
//        }
        for (LifecycleManager manager : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
            if (manager != this) {
                manager.exit();
            }
        }
    }
    

}
