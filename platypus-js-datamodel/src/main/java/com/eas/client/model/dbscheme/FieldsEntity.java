/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.dbscheme;

import com.eas.client.MetadataCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.model.Entity;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.model.visitors.ModelVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class FieldsEntity extends Entity<DbSchemeModel, SqlQuery, FieldsEntity> {

    public static final String INDEXES_PROPERTY = "indexes"; //NOI18N
    public static final String FIELDS_PROPERTY = "fields"; //NOI18N
    protected List<DbTableIndexSpec> grabedIndexes = new ArrayList<>();

    public FieldsEntity() {
        super();
    }

    public FieldsEntity(DbSchemeModel aModel) {
        super(aModel);
    }

    public FieldsEntity(String aSqlId) {
        super(aSqlId);
    }

    @Override
    public void accept(ModelVisitor<FieldsEntity, DbSchemeModel> visitor) {
        visitor.visit(this);
    }

    @Override
    public String getTableDatasourceName() {
        return model != null ? model.getDatasourceName() : null;
    }

    @Override
    public void setTableDatasourceName(String tableDbId) {
    }

    @Override
    public String getTableSchemaName() {
        return model != null ? model.getSchema() : null;
    }

    @Override
    public void setTableSchemaName(String tableSchemaName) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableNameForDescription() {
        return tableName;
    }

    public String getFullTableName() {
        String fullTableName = tableName;
        if (getTableSchemaName() != null && !getTableSchemaName().isEmpty()) {
            fullTableName = getTableSchemaName() + "." + fullTableName;
        }
        return fullTableName;
    }

    public List<DbTableIndexSpec> getIndexes() {
        return grabedIndexes;
    }

    public void achiveIndexes() {
        DatabasesClient basesProxy = getModel().getBasesProxy();
        if (basesProxy != null) {
            try {
                MetadataCache mdCache = basesProxy.getMetadataCache(getTableDatasourceName());
                assert getTableName() != null;
                String ltblName = getTableName();
                if (getTableSchemaName() != null && !getTableSchemaName().isEmpty()) {
                    ltblName = getTableSchemaName() + "." + ltblName;
                }
                DbTableIndexes indexes = mdCache.getTableIndexes(ltblName);
                if (indexes != null && indexes.getIndexes() != null && !indexes.getIndexes().isEmpty()) {
                    List<DbTableIndexSpec> indexesVector = new ArrayList<>();
                    for (DbTableIndexSpec index : indexes.getIndexes().values()) {
                        indexesVector.add(index);
                    }
                    setIndexes(indexesVector);
                }
            } catch (Exception ex) {
                Logger.getLogger(FieldsEntity.class.getName()).log(Level.SEVERE, null, ex);
                List<DbTableIndexSpec> indexesVector = new ArrayList<>();
                DbTableIndexSpec errorIndex = new DbTableIndexSpec();
                errorIndex.setName(ex.getMessage());
                indexesVector.add(errorIndex);
                setIndexes(indexesVector);
            }
        }
    }

    public void setIndexes(List<DbTableIndexSpec> aValue) {
        List<DbTableIndexSpec> oldValue = grabedIndexes;
        grabedIndexes = aValue;
        changeSupport.firePropertyChange(INDEXES_PROPERTY, oldValue, aValue);
    }

    @Override
    protected boolean isTagValid(String aTagName) {
        return aTagName != null && !aTagName.equals(Model2XmlDom.DATASOURCE_TITLE_ATTR_NAME);
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null && tableName != null) {
            query = SQLUtils.validateTableSqlQuery(getTableDatasourceName(), getTableName(), getTableSchemaName(), model.getBasesProxy(), true);
        }
    }
}
