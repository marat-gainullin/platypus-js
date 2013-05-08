/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.DbClient;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class QueryModel extends Model<QueryEntity, QueryParametersEntity, DbClient, SqlQuery> {

    protected String dbId = null;
    private Set<Integer> supportedTypes = null;

    public QueryModel() {
        super();
        parametersEntity = new QueryParametersEntity();
        parametersEntity.setModel(this);
    }

    public QueryModel(DbClient aClient) {
        this();
        setClient(aClient);
    }

    public QueryModel(DbClient aClient, String aDbId) {
        this(aClient);
        dbId = aDbId;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String aValue) {
        dbId = aValue;
    }

    @Override
    public void accept(ModelVisitor<QueryEntity> visitor) {
        if (visitor instanceof QueryModelVisitor) {
            ((QueryModelVisitor) visitor).visit(this);
        }
    }

    @Override
    public QueryEntity newGenericEntity() {
        return new QueryEntity(this);
    }

    @Override
    public void addEntity(QueryEntity aEntity) {
        aEntity.setModel(this);
        super.addEntity(aEntity);
    }

    @Override
    public Document toXML() {
        return QueryModel2XmlDom.transform(this);
    }

    @Override
    public boolean isTypeSupported(int type) {
        checkSupportedTypes();
        return supportedTypes != null ? supportedTypes.contains(type) : true;
    }

    protected void checkSupportedTypes() {
        if (supportedTypes == null && client != null) {
            try {
                SqlDriver driver = ((DbClient) client).getDbMetadataCache(dbId).getConnectionDriver();
                assert driver != null;
                supportedTypes = driver.getSupportedJdbcDataTypes();
            } catch (Exception ex) {
                Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
