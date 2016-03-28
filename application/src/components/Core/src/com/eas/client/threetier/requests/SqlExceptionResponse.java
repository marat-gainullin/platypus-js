/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import java.sql.SQLException;

/**
 *
 * @author mg
 */
public class SqlExceptionResponse extends ExceptionResponse {

    private Integer sqlErrorCode;
    private String sqlState;

    public SqlExceptionResponse() {
        super();
    }

    public SqlExceptionResponse(SQLException aException) {
        super(aException);
        sqlState = aException.getSQLState();
        sqlErrorCode = aException.getErrorCode();
    }

    public Integer getSqlErrorCode() {
        return sqlErrorCode;
    }

    public void setSqlErrorCode(Integer aValue) {
        sqlErrorCode = aValue;
    }

    public String getSqlState() {
        return sqlState;
    }

    public void setSqlState(String aValue) {
        sqlState = aValue;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

}
