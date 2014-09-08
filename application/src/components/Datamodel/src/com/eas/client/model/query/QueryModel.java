/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.model.Model;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class QueryModel extends Model<QueryEntity, QueryParametersEntity, SqlQuery> {

    protected DatabasesClient basesProxy;
    protected String datasourceName;
    private Set<Integer> supportedTypes;

    public QueryModel() {
        super();
        parametersEntity = new QueryParametersEntity();
        parametersEntity.setModel(this);
    }

    public QueryModel(DatabasesClient aBasesProxy) {
        this();
        basesProxy = aBasesProxy;
    }

    public QueryModel(DatabasesClient aaBasesProxy, String aDatasourceName) {
        this(aaBasesProxy);
        datasourceName = aDatasourceName;
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDbId(String aValue) {
        datasourceName = aValue;
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
    public void setParametersEntity(QueryParametersEntity aParamsEntity) {
        super.setParametersEntity(aParamsEntity);
        parametersEntity.setModel(this);
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
        if (supportedTypes == null && basesProxy != null) {
            try {
                SqlDriver driver = basesProxy.getDbMetadataCache(datasourceName).getConnectionDriver();
                assert driver != null;
                supportedTypes = driver.getSupportedJdbcDataTypes();
            } catch (Exception ex) {
                Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
