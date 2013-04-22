/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;

/**
 *
 * @author mg
 */
public class DatabaseAppCache extends AppElementsCache<DbClient> {

    public static final String ACTUALITY_QUERY_TEXT = "select %s, %s, %s from %s where %s = :%s";

    public DatabaseAppCache(DbClient aClient) throws Exception {
        super(aClient);
    }

    @Override
    public boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32) throws Exception {
        SqlQuery query = new SqlQuery(client, String.format(ACTUALITY_QUERY_TEXT, ClientConstants.F_MDENT_ID, ClientConstants.F_MDENT_CONTENT_TXT_SIZE, ClientConstants.F_MDENT_CONTENT_TXT_CRC32, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_ID, ClientConstants.APP_ELEMENT_SQL_PARAM_NAME));
        query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, aId);
        Rowset rowset = query.compile().executeQuery();
        if (!rowset.isEmpty()) {
            rowset.first();
            Long dbSize = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_SIZE));
            Long dbCrc32 = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_CRC32));
            dbSize = dbSize == null ? 0 : dbSize;
            dbCrc32 = dbCrc32 == null ? 0 : dbCrc32;
            long localSize = aTxtContentLength;
            long localCrc32 = aTxtCrc32;
            return localSize == dbSize && localCrc32 == dbCrc32;
        }
        return false;
    }

    @Override
    protected ApplicationElement achieveAppElement(String aId) throws Exception {
        SqlQuery query = new SqlQuery(client, ClientConstants.SQL_SELECT_MD);
        query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, aId);
        SqlCompiledQuery compiledQuery = query.compile();
        Rowset rowset = compiledQuery.executeQuery();
        if (rowset != null && !rowset.isEmpty()) {
            rowset.first();
            return ApplicationElement.read(rowset);
        } else {
            return null;
        }
    }
}
