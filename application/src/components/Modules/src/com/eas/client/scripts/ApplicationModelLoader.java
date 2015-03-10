/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Application;
import com.eas.client.cache.ServerDataStorage;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationPlatypusEntity;
import com.eas.client.model.application.ApplicationPlatypusModel;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.RemoteQueriesProxy;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ApplicationModelLoader {

    public static ApplicationModel<?, ?> load(Document aDoc, Application<?> aApp) {
        if (aApp.getQueries() instanceof LocalQueriesProxy) {
            LocalQueriesProxy localQueries = (LocalQueriesProxy) aApp.getQueries();
            ApplicationDbModel model = new ApplicationDbModel(localQueries.getCore(), localQueries);
            XmlDom2ApplicationModel<ApplicationDbEntity, ApplicationDbModel> parser = new XmlDom2ApplicationModel<>(aDoc);
            parser.visit(model);
            return model;
        } else {
            RemoteQueriesProxy remoteQueries = (RemoteQueriesProxy) aApp.getQueries();
            assert aApp instanceof ServerDataStorage;
            ApplicationPlatypusModel model = new ApplicationPlatypusModel((ServerDataStorage) aApp, remoteQueries);
            XmlDom2ApplicationModel<ApplicationPlatypusEntity, ApplicationPlatypusModel> parser = new XmlDom2ApplicationModel<>(aDoc);
            parser.visit(model);
            return model;
        }
    }

}
