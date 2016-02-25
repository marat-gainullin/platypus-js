/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.openide.modules.ModuleInfo;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Modules;

/**
 *
 * @author mg
 */
public class Installer extends ModuleInstall {

    private static final Collection<String> FRIEND_OF_WHOM = Collections.singleton("org.netbeans.modules.javascript2.editor");

    @Override
    public void validate() throws IllegalStateException {
        try {
            ModuleInfo selfModuleInfo = Modules.getDefault().ownerOf(getClass());
            if (selfModuleInfo != null) {
                Object manager = selfModuleInfo.getClass().getMethod("getManager").invoke(selfModuleInfo);
                for (String targetModuleName : FRIEND_OF_WHOM) {
                    Object moduleData = lookupModuleData(manager, targetModuleName);
                    addToFriends(moduleData, selfModuleInfo);
                }
            } else {
                throw new IllegalStateException("Failed to obtain self module with class: " + getClass().getName());
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void addToFriends(Object toBeFriendOf, ModuleInfo aNewFriend) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, SecurityException {
        Field friendsField = Class.forName("org.netbeans.ModuleData", true, toBeFriendOf.getClass().getClassLoader()).getDeclaredField("friendNames");
        friendsField.setAccessible(true);
        Set<?> oldFriends = (Set<?>) friendsField.get(toBeFriendOf);
        Set<Object> newFriends = new HashSet<>(oldFriends);
        newFriends.add(aNewFriend.getCodeNameBase());
        friendsField.set(toBeFriendOf, newFriends);
    }

    private Object lookupModuleData(Object aModulesManager, String aTargetModuleName) throws Exception {
        Object nbModule = aModulesManager.getClass().getMethod("get", String.class).invoke(aModulesManager, aTargetModuleName);
        if (nbModule != null) {
            Method nbModuleDataMethod = Class.forName("org.netbeans.Module", true, nbModule.getClass().getClassLoader()).getDeclaredMethod("data");
            nbModuleDataMethod.setAccessible(true);
            return nbModuleDataMethod.invoke(nbModule);
        } else {
            throw new IllegalStateException("Module " + aTargetModuleName + " is not found");
        }
    }

}
