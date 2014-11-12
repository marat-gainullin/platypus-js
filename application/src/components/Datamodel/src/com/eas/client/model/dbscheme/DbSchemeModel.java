/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.dbscheme;

import com.eas.client.DatabasesClient;
import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.Types;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DbSchemeModel extends Model<FieldsEntity, SqlQuery> {

    protected String datasourceName;
    protected String schema;
    protected DatabasesClient basesProxy;

    public DbSchemeModel() {
        super();
    }

    public DbSchemeModel(DatabasesClient aBasesProxy) {
        super();
        basesProxy = aBasesProxy;
    }

    public DbSchemeModel(DatabasesClient aBasesProxy, String aDatasourceName) {
        super();
        basesProxy = aBasesProxy;
        datasourceName = aDatasourceName;
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    @Override
    public <M extends Model<FieldsEntity, ?>> void accept(ModelVisitor<FieldsEntity, M> visitor) {
        visitor.visit((M)this);
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

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String aValue) {
        datasourceName = aValue;
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
     * @throws java.lang.Exception
     */
    @Override
    public boolean isTypeSupported(int type) throws Exception {
        SqlDriver driver = basesProxy.getDbMetadataCache(datasourceName).getConnectionDriver();
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
