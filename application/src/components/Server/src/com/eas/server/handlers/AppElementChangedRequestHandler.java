/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.AppElementChangedRequest.Response;
import com.eas.server.ModuleConfig;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.util.Map.Entry;

/**
 *
 * @author mg
 */
public class AppElementChangedRequestHandler extends SessionRequestHandler<AppElementChangedRequest> {

    public AppElementChangedRequestHandler(PlatypusServerCore server, Session session, AppElementChangedRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        if (getRequest().getEntityId() != null) {
            handleApplicationElementChanged();
        } else {
            redeployWholeApplication();
        }
        return new AppElementChangedRequest.Response(getRequest().getID());
    }

    public void redeployWholeApplication() throws Exception {
        // Выгрузим все загруженные серверные модули
        unregisterAllModules();
        // Очистим кэш компиляции скриптовых модулей
        getServerCore().getDocuments().clearCompiledScriptDocuments();
        // Очистим все закэшированные элементы приложения
        // Очистим информацию об элементах приложения и метаданных базы данных
        // Очистим так же все скомпилированные запросы
        // Очистим так же все prepared statements
        getServerCore().getDatabasesClient().appEntityChanged(null);
        // Поднимем фоновые серверные модули заново
        registerBackgroundModules();
    }

    public void unregisterAllModules() {
        for (Entry<String, Session> sEntry : getSessionManager().entrySet()) {
            sEntry.getValue().unregisterModules();
        }
    }

    public void handleApplicationElementChanged() throws Exception {
        // Загруженные серверные модули надо выгрузить, если изменения касаются их
        for (Entry<String, Session> sEntry : getSessionManager().entrySet()) {
            sEntry.getValue().unregisterModule(getRequest().getEntityId());
        }
        // Кэш компиляции скриптовых модулей
        getServerCore().getDocuments().removeCompiledScriptDocument(getRequest().getEntityId());
        // Элементы приложения, которые закэшировались из-за серверных модулей
        getServerCore().getDatabasesClient().appEntityChanged(getRequest().getEntityId());
        // Поднимем фоновые серверные модули заново
        registerBackgroundModules();
    }

    public void registerBackgroundModules() {
        for (ModuleConfig config : getServerCore().getModuleConfigs()) {
            if (config.isLoadOnStartup() && getSessionManager().getSystemSession().getModule(config.getModuleId()) == null) {
                getServerCore().startBackgroundTask(config, null);
            }
        }
    }
}
