/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.model.query.QueryModel;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.queries.SqlQuery;
import java.util.List;

/**
 *
 * @author mg
 */
public class QueryDocument {

    public static class StoredFieldMetadata {

        public String bindedColumn;
        public String description;
        public DataTypeInfo typeInfo;

        public StoredFieldMetadata()
        {
            super();
        }

        public StoredFieldMetadata(String aBindedColumn)
        {
            super();
            bindedColumn = aBindedColumn;
        }

        public DataTypeInfo getTypeInfo() {
            return typeInfo;
        }

        public void setTypeInfo(DataTypeInfo aValue) {
            typeInfo = aValue;
        }

        public String getBindedColumn() {
            return bindedColumn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String aValue) {
            description = aValue;
        }
    }

    protected SqlQuery query;
    protected QueryModel model;
    protected List<StoredFieldMetadata> additionalFieldsMetadata;

    public QueryDocument(SqlQuery aQuery, QueryModel aModel, List<StoredFieldMetadata> aAdditionalFieldsMetadata)
    {
        super();
        query = aQuery;
        model = aModel;
        additionalFieldsMetadata = aAdditionalFieldsMetadata;
        query.setDbId(model.getDbId());
        assert query.getEntityId() != null : "SqlQuery should be constructured with non-null entity id!";
    }

    public List<StoredFieldMetadata> getAdditionalFieldsMetadata() {
        return additionalFieldsMetadata;
    }

    public SqlQuery getQuery() {
        return query;
    }

    public QueryModel getModel() {
        return model;
    }

}
