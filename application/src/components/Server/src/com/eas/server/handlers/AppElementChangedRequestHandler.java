/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.server.PlatypusServerCore;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class AppElementChangedRequestHandler extends CommonRequestHandler<AppElementChangedRequest, AppElementChangedRequest.Response> {

    public AppElementChangedRequestHandler(PlatypusServerCore aServerCore, AppElementChangedRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<AppElementChangedRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            if (getRequest().getEntityId() != null) {
                handleApplicationElementChanged();
            } else {
                redeployWholeApplication();
            }
            if (onSuccess != null) {
                onSuccess.accept(new AppElementChangedRequest.Response());
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }

    public void redeployWholeApplication() throws Exception {
        // Выгрузим все загруженные серверные модули
        unregisterAllModules();
        // Очистим все закэшированные элементы приложения
        // Очистим информацию об элементах приложения и метаданных базы данных
        // Очистим так же все скомпилированные запросы
        // Очистим так же все prepared statements
        getServerCore().getDatabasesClient().appEntityChanged(null);
        // Поднимем фоновые серверные модули заново
        registerBackgroundModules();
    }

    public void unregisterAllModules() {
        serverCore.getSessionManager().entrySet().stream().forEach((sEntry) -> {
            sEntry.getValue().unregisterModules();
        });
    }

    public void handleApplicationElementChanged() throws Exception {
        serverCore.getSessionManager().entrySet().stream().forEach((sEntry) -> {
            sEntry.getValue().unregisterModule(getRequest().getEntityId());
        });
        // Элементы приложения, которые закэшировались из-за серверных модулей
        getServerCore().getDatabasesClient().appEntityChanged(getRequest().getEntityId());
        // Поднимем фоновые серверные модули заново
        registerBackgroundModules();
    }

    public void registerBackgroundModules() throws Exception {
        for (String moduleId : getServerCore().getTasks()) {
            if (serverCore.getSessionManager().getSystemSession().getModule(moduleId) == null) {
                getServerCore().startServerTask(moduleId);
            }
        }
    }
}
