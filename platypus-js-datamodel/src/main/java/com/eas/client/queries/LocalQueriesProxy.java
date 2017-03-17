/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.StoredQueryFactory;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.script.Scripts;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class LocalQueriesProxy implements QueriesProxy<SqlQuery> {

    protected Map<String, ActualCacheEntry<SqlQuery>> entries = new ConcurrentHashMap<>();
    protected PlatypusIndexer indexer;
    protected StoredQueryFactory factory;
    protected DatabasesClient core;

    public LocalQueriesProxy(DatabasesClient aBasesProxy, PlatypusIndexer aIndexer) throws Exception {
        super();
        indexer = aIndexer;
        factory = new StoredQueryFactory(aBasesProxy, this, indexer);
        core = aBasesProxy;
    }

    @Override
    public SqlQuery getQuery(String aName, Scripts.Space aSpace, Consumer<SqlQuery> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Callable<SqlQuery> doWork = () -> {
            SqlQuery query;
            if (aName != null) {
                Date cachedTimeStamp = null;
                ActualCacheEntry<SqlQuery> entry = entries.get(aName);
                if (entry != null) {
                    cachedTimeStamp = entry.getTimeStamp();
                }
                File file = indexer.nameToFile(aName);
                if (file != null) {
                    Date filesTimeStamp = new Date(file.lastModified());
                    if (cachedTimeStamp == null || filesTimeStamp.after(cachedTimeStamp)) {
                        if (file.getName().endsWith(PlatypusFiles.SQL_FILE_END)) {
                            query = factory.loadQuery(aName);
                        } else {
                            String nameMsg;
                            if (aName.isEmpty()) {
                                nameMsg = "empty string name";
                            } else {
                                nameMsg = "name '" + aName + "'";
                            }
                            throw new IllegalStateException("Query with " + nameMsg + " can't be constructed. Queries can be constructed with only platypus queries application elements.");
                        }
                        entries.put(aName, new ActualCacheEntry<>(query, filesTimeStamp));
                    } else {
                        assert entry != null : "Neither in memory, nor in files query found";
                        query = entry.getValue();
                    }
                } else// Let's support in memory only (without underlying files) queries. Used in createEntity().
                {
                    if (entry != null) {
                        query = entry.getValue();
                    } else {
                        query = null;
                    }
                }
            } else {
                query = null;
            }
            return query;
        };
        if (onSuccess != null) {
            aSpace.process(() -> {
                try {
                    SqlQuery query = doWork.call();
                    try {
                        onSuccess.accept(query);
                    } catch (Exception ex) {
                        Logger.getLogger(LocalQueriesProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            });
            return null;
        } else {
            return doWork.call();
        }
    }

    /*
    private void readScriptFields(String aQueryName, JSObject sSchema, Fields fields, Scripts.Space aSpace) {
        Object oLength = sSchema.getMember("length");
        if (oLength instanceof Number) {
            int length = ((Number) oLength).intValue();
            for (int i = 0; i < length; i++) {
                Object oElement = sSchema.getSlot(i);
                if (oElement instanceof JSObject) {
                    JSObject sElement = (JSObject) oElement;
                    Object oFieldName = aSpace.toJava(sElement.hasMember("name") ? sElement.getMember("name") : null);
                    if (oFieldName instanceof String && !((String) oFieldName).isEmpty()) {
                        String sFieldName = (String) oFieldName;
                        Field field = fields instanceof Parameters ? new Parameter() : new Field();
                        field.setType(Scripts.STRING_TYPE_NAME);
                        fields.add(field);
                        field.setName(sFieldName);
                        field.setOriginalName(sFieldName);
                        Object oEntity = aSpace.toJava(sElement.hasMember("entity") ? sElement.getMember("entity") : null);
                        if (oEntity instanceof String && !((String) oEntity).isEmpty()) {
                            field.setTableName((String) oEntity);
                        } else {
                            field.setTableName(aQueryName);
                        }
                        Object oDescription = aSpace.toJava(sElement.hasMember("description") ? sElement.getMember("description") : null);
                        if (oDescription instanceof String && !((String) oDescription).isEmpty()) {
                            field.setDescription((String) oDescription);
                        }
                        Object oType = sElement.getMember("type");
                        if (oType instanceof JSObject && ((JSObject) oType).isFunction()) {
                            Object ofName = aSpace.toJava(((JSObject) oType).getMember("name"));
                            if (ofName instanceof String) {
                                String fName = (String) ofName;
                                if (Scripts.STRING_TYPE_NAME.equals(fName)) {
                                    field.setType(Scripts.STRING_TYPE_NAME);
                                } else if (Scripts.NUMBER_TYPE_NAME.equals(fName)) {
                                    field.setType(Scripts.NUMBER_TYPE_NAME);
                                } else if (Scripts.BOOLEAN_TYPE_NAME.equals(fName)) {
                                    field.setType(Scripts.BOOLEAN_TYPE_NAME);
                                } else if (Scripts.DATE_TYPE_NAME.equals(fName)) {
                                    field.setType(Scripts.DATE_TYPE_NAME);
                                } else if (Scripts.GEOMETRY_TYPE_NAME.equals(fName)) {
                                    field.setType(Scripts.GEOMETRY_TYPE_NAME);
                                }
                            }
                        }
                        Object oRequired = aSpace.toJava(sElement.hasMember("required") ? sElement.getMember("required") : null);
                        if (oRequired instanceof Boolean) {
                            boolean bRequired = (Boolean) oRequired;
                            field.setNullable(!bRequired);
                        }
                        Object oKey = aSpace.toJava(sElement.hasMember("key") ? sElement.getMember("key") : null);
                        if (oKey instanceof Boolean) {
                            boolean bKey = (Boolean) oKey;
                            field.setPk(bKey);
                            field.setNullable(false);
                        }
                        Object oRef = sElement.hasMember("ref") ? sElement.getMember("ref") : null;
                        if (oRef instanceof JSObject) {
                            JSObject sRef = (JSObject) oRef;
                            Object oProperty = aSpace.toJava(sRef.hasMember("property") ? sRef.getMember("property") : null);
                            if (oProperty instanceof String) {
                                String sProperty = (String) oProperty;
                                if (!sProperty.isEmpty()) {
                                    Object oRefEntity = sRef.hasMember("entity") ? sRef.getMember("entity") : null;
                                    String sRefEntity;
                                    if (oRefEntity instanceof String && !((String) oRefEntity).isEmpty()) {
                                        sRefEntity = (String) oRefEntity;
                                    } else {
                                        sRefEntity = aQueryName;
                                    }
                                    field.setFk(new ForeignKeySpec(null, aQueryName, field.getName(), null, ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, null, sRefEntity, sProperty, null));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
     */
    @Override
    public SqlQuery getCachedQuery(String aName) {
        if (aName != null) {
            ActualCacheEntry<SqlQuery> entry = entries.get(aName);
            if (entry != null) {
                return entry.getValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void putCachedQuery(String aName, SqlQuery aQuery) {
        entries.put(aName, new ActualCacheEntry<>(aQuery, new Date()));
    }

    public DatabasesClient getCore() {
        return core;
    }

    public void clearCachedQueries() {
        entries.clear();
    }

    public void clearCachedQuery(String aQueryName) {
        if (aQueryName != null) {
            entries.remove(aQueryName);
        }
    }
}
