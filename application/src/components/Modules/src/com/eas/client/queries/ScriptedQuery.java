/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ScriptedQuery extends SqlQuery {

    public static final String JAVASCRIPT_QUERY_CONTENTS = "JavaScript query";

    public ScriptedQuery(DatabasesClient aCore, String aModuleName) {
        super(aCore);
        publicAccess = true;
        sqlText = JAVASCRIPT_QUERY_CONTENTS;
        datasourceName = aModuleName;
        entityId = aModuleName;
        readRoles = Collections.<String>emptySet();
        writeRoles = Collections.<String>emptySet();
        procedure = false;
    }

    @Override
    public SqlCompiledQuery compile() throws Exception {
        SqlCompiledQuery compiled = new SqlCompiledQuery(basesProxy, datasourceName, entityId, sqlText, params, fields, readRoles, writeRoles);
        return compiled;
    }

    @Override
    public void setSqlText(String aValue) {
    }

    @Override
    public void setPublicAccess(boolean aValue) {
    }

    @Override
    public void setReadRoles(Set<String> aRoles) {
    }

    @Override
    public void setWriteRoles(Set<String> aRoles) {
    }

    @Override
    public void setProcedure(boolean aValue) {
    }

}
