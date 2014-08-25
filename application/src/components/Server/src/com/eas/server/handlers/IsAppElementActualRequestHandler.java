/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.ClientConstants;
import com.eas.client.DatabaseAppCache;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.server.PlatypusServerCore;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class IsAppElementActualRequestHandler extends CommonRequestHandler<IsAppElementActualRequest, IsAppElementActualRequest.Response> {

    public IsAppElementActualRequestHandler(PlatypusServerCore aServerCore, IsAppElementActualRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<IsAppElementActualRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            if (getServerCore().getDatabasesClient().getAppCache() instanceof FilesAppCache) {
                getServerCore().getDatabasesClient().getAppCache().isActual(getRequest().getAppElementId(), getRequest().getTxtContentSize(), getRequest().getTxtContentCrc32(),
                        (Boolean actuality) -> {
                            if (onSuccess != null) {
                                onSuccess.accept(new IsAppElementActualRequest.Response(actuality));
                            }
                        }, onFailure);
            } else {
                SqlQuery query = new SqlQuery(getServerCore().getDatabasesClient(), String.format(DatabaseAppCache.ACTUALITY_QUERY_TEXT, ClientConstants.F_MDENT_ID, ClientConstants.F_MDENT_CONTENT_TXT_SIZE, ClientConstants.F_MDENT_CONTENT_TXT_CRC32, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_ID, ClientConstants.APP_ELEMENT_SQL_PARAM_NAME));
                query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, getRequest().getAppElementId());
                query.compile().executeQuery((Rowset rowset) -> {
                    if (!rowset.isEmpty()) {
                        try {
                            rowset.first();
                            Long dbSize = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_SIZE));
                            Long dbCrc32 = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_CRC32));
                            dbSize = dbSize == null ? 0 : dbSize;
                            dbCrc32 = dbCrc32 == null ? 0 : dbCrc32;
                            long remoteSize = getRequest().getTxtContentSize();
                            long remoteCrc32 = getRequest().getTxtContentCrc32();
                            if (onSuccess != null) {
                                onSuccess.accept(new IsAppElementActualRequest.Response(remoteSize == dbSize && remoteCrc32 == dbCrc32));
                            }
                        } catch (InvalidCursorPositionException | InvalidColIndexException ex) {
                            if (onFailure != null) {
                                onFailure.accept(ex);
                            }
                        }
                    } else {
                        if (onSuccess != null) {
                            onSuccess.accept(new IsAppElementActualRequest.Response(false));
                        }
                    }
                }, onFailure);
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
}
