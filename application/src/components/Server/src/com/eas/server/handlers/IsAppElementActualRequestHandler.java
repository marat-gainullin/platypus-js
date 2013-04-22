/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.ClientConstants;
import com.eas.client.cache.DatabaseAppCache;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;

/**
 *
 * @author mg
 */
public class IsAppElementActualRequestHandler extends SessionRequestHandler<IsAppElementActualRequest> {

    public IsAppElementActualRequestHandler(PlatypusServerCore aServerCore, Session aSession, IsAppElementActualRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    protected Response handle2() throws Exception {
        IsAppElementActualRequest.Response response = new IsAppElementActualRequest.Response(getRequest().getID(), checkActuality());
        return response;
    }

    protected boolean checkActuality() throws Exception {
        if (getServerCore().getDatabasesClient().getAppCache() instanceof FilesAppCache) {
            return getServerCore().getDatabasesClient().getAppCache().isActual(getRequest().getAppElementId(), getRequest().getTxtContentSize(), getRequest().getTxtContentCrc32());
        } else {
            SqlQuery query = new SqlQuery(getServerCore().getDatabasesClient(), String.format(DatabaseAppCache.ACTUALITY_QUERY_TEXT, ClientConstants.F_MDENT_ID, ClientConstants.F_MDENT_CONTENT_TXT_SIZE, ClientConstants.F_MDENT_CONTENT_TXT_CRC32, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_ID, ClientConstants.APP_ELEMENT_SQL_PARAM_NAME));
            query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, getRequest().getAppElementId());
            Rowset rowset = query.compile().executeQuery();
            if (!rowset.isEmpty()) {
                rowset.first();
                Long dbSize = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_SIZE));
                Long dbCrc32 = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_CRC32));
                dbSize = dbSize == null ? 0 : dbSize;
                dbCrc32 = dbCrc32 == null ? 0 : dbCrc32;
                long remoteSize = getRequest().getTxtContentSize();
                long remoteCrc32 = getRequest().getTxtContentCrc32();
                return remoteSize == dbSize && remoteCrc32 == dbCrc32;
            }
        }
        return false;
    }
}
