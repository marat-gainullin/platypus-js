/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.dbscheme;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.DbSchemeModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.SqlQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class FieldsEntity extends Entity<DbSchemeModel, SqlQuery, FieldsEntity> {

    public static final String INDEXES_PROPERTY = "indexes"; //NOI18N
    public static final String FIELDS_PROPERTY = "fields"; //NOI18N
    protected List<DbTableIndexSpec> grabedIndexes;

    public FieldsEntity() {
        super();
    }

    public FieldsEntity(DbSchemeModel dm) {
        super(dm);
    }

    public FieldsEntity(String aSqlId) {
        super(aSqlId);
    }

    @Override
    public void accept(ModelVisitor<FieldsEntity> visitor) {
        if (visitor instanceof DbSchemeModelVisitor) {
            ((DbSchemeModelVisitor) visitor).visit(this);
        }
    }

    @Override
    public String getTableDbId() {
        return model != null ? model.getDbId() : null;
    }

    @Override
    public void setTableDbId(String tableDbId) {
    }

    @Override
    public String getTableSchemaName() {
        return model != null ? model.getSchema() : null;
    }

    @Override
    public void setTableSchemaName(String tableSchemaName) {
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String getFullTableNameEntityForDescription() {
        return tableName;
    }

    public List<DbTableIndexSpec> getIndexes() {
        return grabedIndexes;
    }

    public void achiveIndexes() {
        DbClient lclient = getModel().getClient();
        if (lclient != null) {
            try {
                DbMetadataCache mdCache = lclient.getDbMetadataCache(getTableDbId());
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
        return aTagName != null && !aTagName.equals(Model.DATASOURCE_TITLE_TAG_NAME);
    }

    public static <RE extends Entity<?, ?, RE>> Set<Relation<RE>> getInOutRelationsByEntityField(RE aEntity, Field aField) {
        Set<Relation<RE>> result = new HashSet<>();
        Set<Relation<RE>> rels = aEntity.getInRelations();
        if (rels != null) {
            for (Relation<RE> rel : rels) {
                if (rel.getRightField() == aField) {
                    result.add(rel);
                }
            }
        }
        rels = aEntity.getOutRelations();
        if (rels != null) {
            for (Relation<RE> rel : rels) {
                if (rel.getLeftField() == aField) {
                    result.add(rel);
                }
            }
        }
        return result;
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null && tableName != null) {
            query = SQLUtils.validateTableSqlQuery(getTableDbId(), getTableName(), getTableSchemaName(), model.getClient());
        }
    }
}
