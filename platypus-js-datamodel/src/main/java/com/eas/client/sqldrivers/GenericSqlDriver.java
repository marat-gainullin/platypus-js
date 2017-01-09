/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers;

import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.resolvers.GenericTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.List;

/**
 *
 * @author mg
 */
public class GenericSqlDriver extends SqlDriver {

    @Override
    public boolean is(String aDialect) {
        return true;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    @Override
    public TypesResolver getTypesResolver() {
        return new GenericTypesResolver();
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return null;
    }

    @Override
    public String getVersionInitResourceName() {
        return null;
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
    }

    @Override
    public String getSql4GetConnectionContext() {
        return null;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        return null;
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        return null;
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        return null;
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return null;
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return null;
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        return null;
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {
        return null;
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        return null;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        return null;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        return null;
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        return null;
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        return null;
    }

    @Override
    public String parseException(Exception ex) {
        String res = ex.getLocalizedMessage();
        if (res == null) {
            res = ex.getMessage();
        }
        if (res == null) {
            res = ex.toString();
        }
        return res;
    }

    @Override
    public String getSql4FieldDefinition(JdbcField aField) {
        return null;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, JdbcField aField) {
        return null;
    }

    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        return null;
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, JdbcField aNewFieldMd) {
        return null;
    }

    @Override
    public JdbcChangeValue convertGeometry(String aValue, Connection aConnection) throws SQLException {
        return null;
    }

    @Override
    public String readGeometry(Wrapper aRs, int aColumnIndex, Connection aConnection) throws SQLException {
        return null;
    }

    @Override
    public TwinString[] getCharsForWrap() {
        return null;
    }

    @Override
    public char[] getRestrictedChars() {
        return null;
    }

    @Override
    public boolean isHadWrapped(String aName) {
        return true;
    }

}
