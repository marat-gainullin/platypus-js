/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class DbTableChangedRequest extends Request {

    protected String databaseId;
    protected String schema;
    protected String table;

    public DbTableChangedRequest() {
        super(Requests.rqDbTableChanged);
    }

    public DbTableChangedRequest(String aDatabaseId, String aSchemaName, String aTableName) {
        this();
        databaseId = aDatabaseId;
        schema = aSchemaName;
        table = aTableName;
        assert table != null;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public void setDatabaseId(String aValue) {
        databaseId = aValue;
    }

    public void setSchema(String aValue) {
        schema = aValue;
    }

    public void setTable(String aValue) {
        table = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        public Response() {
            super();
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
