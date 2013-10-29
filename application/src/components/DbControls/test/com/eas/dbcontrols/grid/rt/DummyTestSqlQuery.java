/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.queries.SqlQuery;

/**
 *
 * @author Gala
 */
public class DummyTestSqlQuery extends SqlQuery {

    public DummyTestSqlQuery() {
        super();
    }

    @Override
    public Fields getFields() {
        return null;
    }
}
