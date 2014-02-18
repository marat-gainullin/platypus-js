/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.dbscheme;

import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbClient;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.DbSchemeModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.Types;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class DbSchemeModel extends Model<FieldsEntity, FieldsEntity, DbClient, SqlQuery> {

    protected String dbId;
    protected String schema;

    public DbSchemeModel() {
        super();
        assert parametersEntity == null;
    }

    public DbSchemeModel(DbClient aClient) {
        super(aClient);
        assert parametersEntity == null;
    }

    public DbSchemeModel(DbClient aClient, String aDbId) {
        super(aClient);
        dbId = aDbId;
        assert parametersEntity == null;
    }

    @Override
    public void accept(ModelVisitor<FieldsEntity> visitor) {
        if (visitor instanceof DbSchemeModelVisitor) {
            ((DbSchemeModelVisitor) visitor).visit(this);
        }
    }

    @Override
    public FieldsEntity newGenericEntity() {
        return new FieldsEntity(this);
    }

    @Override
    public void addEntity(FieldsEntity aEntity) {
        aEntity.setModel(this);
        super.addEntity(aEntity);
    }

    @Override
    public Document toXML() {
        return DbSchemeModel2XmlDom.transform(this);
    }

    @Override
    public Parameters getParameters() {
        return null;
    }

    @Override
    public FieldsEntity getParametersEntity() {
        return null;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String aValue) {
        dbId = aValue;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String aValue) {
        schema = aValue;
        if (schema != null && schema.isEmpty()) {
            schema = null;
        }
    }

    public void addEntityIndex(FieldsEntity aEntity, DbTableIndexSpec aIndexSpec, int aIndexPosition) {
        List<DbTableIndexSpec> indexes = aEntity.getIndexes();
        if (indexes != null) {
            if (aIndexPosition >= 0 && aIndexPosition <= indexes.size()) {
                indexes.add(aIndexPosition, aIndexSpec);
            } else {
                indexes.add(aIndexSpec);
            }
            fireIndexesChanged(aEntity);
        }
    }

    public void removeEntityIndex(FieldsEntity aEntity, DbTableIndexSpec aIndexSpec) {
        List<DbTableIndexSpec> indexes = aEntity.getIndexes();
        if (indexes != null) {
            for (int i = indexes.size() - 1; i >= 0; i--) {
                DbTableIndexSpec lIndex = indexes.get(i);
                if (lIndex == aIndexSpec) {
                    indexes.remove(i);
                    fireIndexesChanged(aEntity);
                    break;
                }
            }
        }
    }

    protected void fireIndexesChanged(FieldsEntity aEntity) {
        editingSupport.fireIndexesChanged(aEntity);
    }

    /**
     * Method checks if the type is supported for datamodel's internal usage.
     * The types are fields or parameters types. If the type is reported as
     * unsupported by this method, it doesn't mean that the type is unsupported
     * in our platypus system at all. It means only that platypus application
     * designer will not be able to add fields or parameters of such types.
     *
     * @param type - the type to check.
     * @return true if the type is supported for datamodel's internal usage.
     */
    @Override
    public boolean isTypeSupported(int type) throws Exception {
        SqlDriver driver = client.getDbMetadataCache(dbId).getConnectionDriver();
        Set<Integer> supportedTypes = driver.getSupportedJdbcDataTypes();
        if (SQLUtils.isTypeSupported(type)) {
            if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.NUMBERS) // numbers
            {
                return (type == Types.NUMERIC && supportedTypes.contains(Types.NUMERIC))
                        || (type == Types.DECIMAL && supportedTypes.contains(Types.DECIMAL));
            } else if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.STRINGS) // strings
            {
                return type == Types.VARCHAR && supportedTypes.contains(Types.VARCHAR);
            } else if (SQLUtils.getTypeGroup(type) == SQLUtils.TypesGroup.DATES) // dates
            {
                return (type == Types.DATE && supportedTypes.contains(Types.DATE))
                        || (type == Types.TIMESTAMP && supportedTypes.contains(Types.TIMESTAMP));
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.BLOB)) // large objects
            {
                return (type == Types.BLOB && supportedTypes.contains(Types.BLOB))
                        || (type == Types.CLOB && supportedTypes.contains(Types.CLOB));
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.BIT)) // logical
            {
                return type == Types.BOOLEAN && supportedTypes.contains(Types.BOOLEAN);
            } else if (SQLUtils.isSimpleTypesCompatible(type, Types.STRUCT)) // aggregating type
            {
                return type == Types.STRUCT && supportedTypes.contains(Types.STRUCT);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
