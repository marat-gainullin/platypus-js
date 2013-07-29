/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.store.Model2XmlDom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class TableSchemeDbValidator extends DefaultMtdSelectionValidator {

    // flag, indicating that dbId verification is needed
    protected boolean isOnlyDb = false;
    protected String onlyDbId = null;
    // flag, indicating that schema verification is needed
    protected boolean isOnlySchema = false;
    protected String onlySchema = null;
    protected DbMetadataCache dbCache = null;
    protected AppCache appCache = null;

    public TableSchemeDbValidator(boolean aIsOnlyDb, String aOnlyDbId, boolean aIsOnlySchema, String aOnlySchema, DbClient aClient) throws Exception {
        super(null);
        isOnlyDb = aIsOnlyDb;
        onlyDbId = aOnlyDbId;
        isOnlySchema = aIsOnlySchema;
        onlySchema = aOnlySchema;
        assert !isOnlySchema || (isOnlySchema && isOnlyDb) : "Fixed schema validation is impossible without fixed dbId validation.";
        if (aClient != null) {
            dbCache = aClient.getDbMetadataCache(null);
            appCache = aClient.getAppCache();
            if (onlySchema != null && !onlySchema.isEmpty() && onlySchema.equalsIgnoreCase(dbCache.getConnectionSchema())) {
                onlySchema = "";
            }
        }
        allowedTypes = new ArrayList<>();
        allowedTypes.add(ClientConstants.ET_DB_SCHEME);
        allowedTypes.add(ClientConstants.ET_CONNECTION);
    }

    @Override
    public boolean isEntityValid(ApplicationElement entity) {
        return super.isEntityValid(entity) && isDbIdAndSchemaValid(entity);
    }

    private boolean isDbIdAndSchemaValid(ApplicationElement entity) {
        if (entity != null) {
            if (entity.getType() == ClientConstants.ET_CONNECTION) {
                if (isOnlyDb) {
                    if (onlyDbId == null) {
                        // user have selected some db connection, but we need a table from
                        // application db, so return false immidiatly.
                        return false;
                    }
                    return entity.getId().equals(onlyDbId);
                } else // db doesn't matter
                {
                    return true;
                }
            } else if (entity.getType() == ClientConstants.ET_DB_SCHEME) {
                if (isOnlyDb) {
                    if (dbCache != null) {
                        try {
                            ApplicationElement appElement = appCache.get(entity.getId());
                            if (appElement != null) {
                                Document content = appElement.getContent();
                                if (content != null) {
                                    Element datamodelElement = content.getDocumentElement();
                                    String dbIdAttribute = datamodelElement.getAttribute(Model2XmlDom.DATAMODEL_DB_ID);
                                    if (String.valueOf(onlyDbId).equalsIgnoreCase(dbIdAttribute) || (dbIdAttribute == null && onlyDbId == null)) {
                                        // db id is verificated
                                        if (isOnlySchema) {
                                            String schemaAttribute = datamodelElement.getAttribute(Model2XmlDom.DATAMODEL_DB_SCHEMA_NAME);
                                            if (String.valueOf(onlySchema).equalsIgnoreCase(schemaAttribute) || (schemaAttribute == null && (onlySchema == null || onlySchema.isEmpty()))) {
                                                // schema is verificated
                                                return true;
                                            } else {
                                                if (onlySchema == null || onlySchema.isEmpty()) {
                                                    // schema might be valid if it contains explicit application schema name
                                                    return String.valueOf(dbCache.getConnectionSchema()).equalsIgnoreCase(schemaAttribute);
                                                } else {
                                                    // schema is invalid
                                                    return false;
                                                }
                                            }
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(TableSchemeDbValidator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
