/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.AppClient;
import com.eas.client.Client;
import com.eas.client.DbClient;

/**
 *
 * @author mg
 */
public class ApplicationModelFactory {
    /*
    public static <E extends ApplicationEntity<?, ?, E>> ApplicationModel<E, ?, ?, ?> newModel(Client aClient) {
        if (aClient instanceof DbClient) {
            return (ApplicationModel<E, ?, ?, ?>)new ApplicationDbModel((DbClient) aClient);
        } else if (aClient instanceof AppClient) {
            return (ApplicationModel<E, ?, ?, ?>)new ApplicationPlatypusModel((AppClient) aClient);
        } else if (aClient == null) {
            throw new IllegalStateException("Null client is invalid for model creation");
        } else {
            throw new IllegalStateException(String.format("Client of unknown type [ %s ] detected!", aClient.getClass().getName()));
        }
    }
    */ 
}
