/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.dbscheme;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import java.util.List;

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
}
