/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class QueryModel extends Model<QueryEntity, SqlQuery> {

    public static final long PARAMETERS_ENTITY_ID = -1L;
    public static final String PARAMETERS_SCRIPT_NAME = "params";
    protected QueryParametersEntity parametersEntity;
    //
    protected Parameters parameters = new Parameters();
    protected QueriesProxy<SqlQuery> queries;
    protected DatabasesClient basesProxy;
    protected String datasourceName;
    private Set<String> supportedTypes;

    public QueryModel(QueriesProxy<SqlQuery> aQueries) {
        super();
        queries = aQueries;
        parametersEntity = new QueryParametersEntity();
        parametersEntity.setModel(this);
    }

    public QueryModel(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries) {
        this(aQueries);
        basesProxy = aBasesProxy;
    }

    public QueryModel(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries, String aDatasourceName) {
        this(aBasesProxy, aQueries);
        datasourceName = aDatasourceName;
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    public QueriesProxy<SqlQuery> getQueries() {
        return queries;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String aValue) {
        datasourceName = aValue;
    }

    @Override
    public Model<QueryEntity, SqlQuery> copy() throws Exception {
        Model<QueryEntity, SqlQuery> copied = super.copy();
        if (parameters != null) {
            ((QueryModel) copied).setParameters(parameters.copy());
        }
        if (getParametersEntity() != null) {
            ((QueryModel) copied).setParametersEntity(getParametersEntity().copy());
        }
        return copied;
    }

    public QueryParametersEntity getParametersEntity() {
        return parametersEntity;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters aParams) {
        parameters = aParams;
    }

    public void setParametersEntity(QueryParametersEntity aParamsEntity) {
        if (parametersEntity != null) {
            parametersEntity.setModel(null);
        }
        parametersEntity = aParamsEntity;
        if (parametersEntity != null) {
            parametersEntity.setModel(this);
        }
    }

    @Override
    public <M extends Model<QueryEntity, ?>> void accept(ModelVisitor<QueryEntity, M> visitor) {
        visitor.visit((M) this);
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
    protected void resolveRelationEntities(Relation<QueryEntity> aRelation) {
        if (aRelation.getLeftEntity() != null) {
            if (aRelation.getLeftEntity() instanceof QueryParametersEntity) {
                aRelation.setLeftEntity(getParametersEntity());
            } else {
                aRelation.setLeftEntity(getEntityById(aRelation.getLeftEntity().getEntityId()));
            }
        }
        if (aRelation.getRightEntity() != null) {
            if (aRelation.getRightEntity() instanceof QueryParametersEntity) {
                aRelation.setRightEntity(getParametersEntity());
            } else {
                aRelation.setRightEntity(getEntityById(aRelation.getRightEntity().getEntityId()));
            }
        }
    }

    protected void checkSupportedTypes() {
        if (supportedTypes == null && basesProxy != null) {
            try {
                SqlDriver driver = basesProxy.getMetadataCache(datasourceName).getDatasourceSqlDriver();
                assert driver != null;
                supportedTypes = driver.getTypesResolver().getSupportedTypes();
            } catch (Exception ex) {
                Logger.getLogger(QueryModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Validates queries in force way. Such case is used in designer ONLY!
     *
     * @return
     * @throws Exception
     */
    @Override
    protected boolean validateEntities() throws Exception {
        for (QueryEntity e : entities.values()) {
            queries.getQuery(e.getQueryName(), null, null, null);
        }
        return super.validateEntities();
    }

}
