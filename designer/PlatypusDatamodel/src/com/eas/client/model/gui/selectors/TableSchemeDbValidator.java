/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.MetadataCache;
import com.eas.client.DatabasesClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.xml.dom.Source2XmlDom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.loaders.DataObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class TableSchemeDbValidator extends DefaultMtdSelectionValidator {

    // flag, indicating that dbId verification is needed
    protected boolean isOnlyDb;
    protected String onlyDatasource;
    // flag, indicating that schema verification is needed
    protected boolean isOnlySchema;
    protected String onlySchema;
    protected MetadataCache dbCache;

    public TableSchemeDbValidator(boolean aIsOnlyDatasource, String aOnlyDatasource, boolean aIsOnlySchema, String aOnlySchema, DatabasesClient aBasesProxy) throws Exception {
        super(null);
        isOnlyDb = aIsOnlyDatasource;
        onlyDatasource = aOnlyDatasource;
        isOnlySchema = aIsOnlySchema;
        onlySchema = aOnlySchema;
        assert !isOnlySchema || (isOnlySchema && isOnlyDb) : "Fixed schema validation is impossible without fixed dbId validation.";
        if (aBasesProxy != null) {
            dbCache = aBasesProxy.getMetadataCache(null);
            if (onlySchema != null && !onlySchema.isEmpty() && onlySchema.equalsIgnoreCase(dbCache.getDatasourceSchema())) {
                onlySchema = "";
            }
        }
        allowedTypes = new ArrayList<>();
        allowedTypes.add(PlatypusFiles.DB_SCHEME_EXTENSION);
    }

    @Override
    public boolean isEntityValid(DataObject entity) {
        return super.isEntityValid(entity) && isDbIdAndSchemaValid(entity);
    }

    private boolean isDbIdAndSchemaValid(DataObject entity) {
        if (PlatypusFiles.DB_SCHEME_EXTENSION.equalsIgnoreCase(entity.getPrimaryFile().getExt())) {
            if (isOnlyDb) {
                if (dbCache != null) {
                    try {
                        Document content = Source2XmlDom.transform(entity.getPrimaryFile().asText());
                        if (content != null) {
                            Element datamodelElement = content.getDocumentElement();
                            String dbIdAttribute = datamodelElement.getAttribute(Model2XmlDom.DATAMODEL_DB_ID);
                            if (String.valueOf(onlyDatasource).equalsIgnoreCase(dbIdAttribute) || (dbIdAttribute == null && onlyDatasource == null)) {
                                // db id is verificated
                                if (isOnlySchema) {
                                    String schemaAttribute = datamodelElement.getAttribute(Model2XmlDom.DATAMODEL_DB_SCHEMA_NAME);
                                    if (String.valueOf(onlySchema).equalsIgnoreCase(schemaAttribute) || (schemaAttribute == null && (onlySchema == null || onlySchema.isEmpty()))) {
                                        // schema is verificated
                                        return true;
                                    } else {
                                        if (onlySchema == null || onlySchema.isEmpty()) {
                                            // schema might be valid if it contains explicit application schema name
                                            return String.valueOf(dbCache.getDatasourceSchema()).equalsIgnoreCase(schemaAttribute);
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
                    } catch (Exception ex) {
                        Logger.getLogger(TableSchemeDbValidator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
