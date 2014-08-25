/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.cache.AppElementsCache;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class DatabaseAppCache extends AppElementsCache {

    public static final String ACTUALITY_QUERY_TEXT = "select %s, %s, %s from %s where %s = :%s";
    public static final String APP_URL_PREFIX = "jndi://";

    protected String appDatabaseJndiUrl;
    protected String datasourceName;
    protected DbClient client;

    public DatabaseAppCache(String aAppDatabaseJndiUrl) throws Exception {
        this(aAppDatabaseJndiUrl, null);
    }

    public DatabaseAppCache(String aAppDatabaseJndiUrl, String aAppCacheBasePath) throws Exception {
        super("app-" + String.valueOf(aAppDatabaseJndiUrl.hashCode()), aAppCacheBasePath);
        if (!aAppDatabaseJndiUrl.startsWith(APP_URL_PREFIX)) {
            throw new IllegalArgumentException("Application jndi url must start with " + APP_URL_PREFIX + " prefix.");
        }
        appDatabaseJndiUrl = aAppDatabaseJndiUrl;
        datasourceName = appDatabaseJndiUrl.substring(APP_URL_PREFIX.length(), appDatabaseJndiUrl.length());
        client = new DatabasesClient(null, datasourceName, false);
    }

    @Override
    public String getApplicationPath() {
        return appDatabaseJndiUrl;
    }

    @Override
    public boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        SqlQuery query = new SqlQuery(client, String.format(ACTUALITY_QUERY_TEXT, ClientConstants.F_MDENT_ID, ClientConstants.F_MDENT_CONTENT_TXT_SIZE, ClientConstants.F_MDENT_CONTENT_TXT_CRC32, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_ID, ClientConstants.APP_ELEMENT_SQL_PARAM_NAME));
        query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, aId);
        SqlCompiledQuery compiledQuery = query.compile();
        if (onSuccess != null) {
            compiledQuery.executeQuery((Rowset rowset) -> {
                if (!rowset.isEmpty()) {
                    try {
                        rowset.first();
                        Long dbSize = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_SIZE));
                        Long dbCrc32 = rowset.getLong(rowset.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_CRC32));
                        dbSize = dbSize == null ? 0 : dbSize;
                        dbCrc32 = dbCrc32 == null ? 0 : dbCrc32;
                        long localSize = aTxtContentLength;
                        long localCrc32 = aTxtCrc32;
                        onSuccess.accept(localSize == dbSize && localCrc32 == dbCrc32);
                    } catch (InvalidCursorPositionException | InvalidColIndexException ex) {
                        if (onFailure != null) {
                            onFailure.accept(ex);
                        }
                    }
                } else {
                    onSuccess.accept(false);
                }
            }, onFailure);
        } else {
            Rowset rowset = compiledQuery.executeQuery(null, null);
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
        }
        return false;
    }

    @Override
    protected ApplicationElement achieveAppElement(String aId, Consumer<ApplicationElement> onSuccess, Consumer<Exception> onFailure) throws Exception {
        SqlQuery query = new SqlQuery(client, ClientConstants.SQL_SELECT_MD);
        query.putParameter(ClientConstants.APP_ELEMENT_SQL_PARAM_NAME, DataTypeInfo.VARCHAR, aId);
        SqlCompiledQuery compiledQuery = query.compile();
        if (onSuccess != null) {
            compiledQuery.executeQuery((Rowset rowset) -> {
                if (rowset != null && !rowset.isEmpty()) {
                    try {
                        rowset.first();
                        onSuccess.accept(ApplicationElement.read(rowset));
                    } catch (Exception ex) {
                        if(onFailure != null)
                            onFailure.accept(ex);
                    }
                } else {
                    onSuccess.accept(null);
                }
            }, onFailure);
            return null;
        } else {
            Rowset rowset = compiledQuery.executeQuery(null, null);
            if (rowset != null && !rowset.isEmpty()) {
                rowset.first();
                return ApplicationElement.read(rowset);
            } else {
                return null;
            }
        }
    }
}
