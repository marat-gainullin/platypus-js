/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 *
 * @author pk
 */
public class ProtocolConstants {

    public static final String PLATYPUS_NS_PREFIX = "platypus";
    public static final String PLATYPUS_NS_URL = "http://xml.netbeans.org/schema/platypus";
    public static final String SOAP_RPC_NS_PREFIX = "rpc";
    public static final String SOAP_RPC_RESULT_TAG_NAME = "result";
    public static final String SOAP_RPC_NS_URL = "http://www.w3.org/2003/05/soap-rpc";
    public static final String SESSION_HEADER_NAME = "X-Platypus-Session";
    // temporary objects
    private static Name checkObjectTagName;
    private static Name createObjectTagName;
    private static Name deleteObjectTagName;
    private static Name objectTagName;
    private static Name objectUrlAttrName;
    private static Name versionTagName;
    private static Name objectCapacityAttrName;
    private static Name objectSizeAttrName;
    private static Name okTagName;
    private static Name rpcResultTagName;

    //authentication
    private static Name sessionTagName;
    private static Name helloPasswordAttrName;
    private static Name helloUserAttrName;
    private static Name helloTagName;

    // SQL queries
    private static Name executeQueryTagName;
    private static Name executeUpdateTagName;
    private static Name databaseIdAttrName;
    private static Name sqlQueryTagName;
    private static Name parameterTagName;
    private static Name sqlTypeAttrName;
    private static Name parameterNameAttrName;
    private static Name modifiedRowsTagName;
    private static Name updateRowsetTagName;

    //metadata
    private static Name primaryKeysTagName;
    private static Name foreignKeysTagName;
    private static Name indexKeysTagName;
    private static Name achiveDbMetadataTagName;
    private static Name tableCommentsTagName;
    private static Name schemaTablesCommentsTagName;
    private static Name catalogAttrName;
    private static Name schemaAttrName;
    private static Name tableAttrName;
    private static Name metadataTypeAttrName;

    //exceptions
    private static Name exceptionDetailTagName;
    private static Name exceptionClassAttrName;

    public static void initialize(SOAPFactory factory) throws SOAPException {
        // temporary objects
        checkObjectTagName = createPlatypusName(factory, "checkObject");
        createObjectTagName = createPlatypusName(factory, "createObject");
        deleteObjectTagName = createPlatypusName(factory, "deleteObject");
        objectTagName = createPlatypusName(factory, "object");
        objectUrlAttrName = createPlatypusName(factory, "url");
        objectSizeAttrName = createPlatypusName(factory, "size");
        objectCapacityAttrName = createPlatypusName(factory, "capacity");

        versionTagName = createPlatypusName(factory, "version");
        okTagName = createPlatypusName(factory, "ok");

        //authentication
        helloTagName = createPlatypusName(factory, "hello");
        helloUserAttrName = createPlatypusName(factory, "user");
        helloPasswordAttrName = createPlatypusName(factory, "password");
        sessionTagName = createPlatypusName(factory, "session");

        // SQL queries
        executeQueryTagName = createPlatypusName(factory, "executeQuery");
        executeUpdateTagName = createPlatypusName(factory, "executeUpdate");
        databaseIdAttrName = createPlatypusName(factory, "databaseId");
        sqlQueryTagName = createPlatypusName(factory, "sqlQuery");
        parameterTagName = createPlatypusName(factory, "parameter");
        sqlTypeAttrName = createPlatypusName(factory, "sqlType");
        parameterNameAttrName = createPlatypusName(factory, "name");
        modifiedRowsTagName = createPlatypusName(factory, "modified");
        updateRowsetTagName = createPlatypusName(factory, "updateRowset");

        //metadata
        primaryKeysTagName = createPlatypusName(factory, "primaryKeys");
        foreignKeysTagName = createPlatypusName(factory, "foreignKeys");
        indexKeysTagName = createPlatypusName(factory, "indexKeys");
        achiveDbMetadataTagName = createPlatypusName(factory, "achiveDbMetadata");
        tableCommentsTagName = createPlatypusName(factory, "tableComments");
        schemaTablesCommentsTagName = createPlatypusName(factory, "schemaTablesComments");
        catalogAttrName = createPlatypusName(factory, "catalog");
        schemaAttrName = createPlatypusName(factory, "schema");
        tableAttrName = createPlatypusName(factory, "table");
        metadataTypeAttrName = createPlatypusName(factory, "metadataType");

        //exceptions
        exceptionClassAttrName = createPlatypusName(factory, "exceptionClass");
        exceptionDetailTagName = createPlatypusName(factory, "exceptionDetails");

        rpcResultTagName = factory.createName(SOAP_RPC_RESULT_TAG_NAME, SOAP_RPC_NS_PREFIX, SOAP_RPC_NS_URL);
    }

    private static Name createPlatypusName(SOAPFactory factory, String localName) throws SOAPException {
        return factory.createName(localName, PLATYPUS_NS_PREFIX, PLATYPUS_NS_URL);
    }

    /**
     * @return the checkObjectTagName
     */
    public static Name getCheckObjectTagName() {
        return checkObjectTagName;
    }

    /**
     * @return the createObjectTagName
     */
    public static Name getCreateObjectTagName() {
        return createObjectTagName;
    }

    /**
     * @return the deleteObjectTagName
     */
    public static Name getDeleteObjectTagName() {
        return deleteObjectTagName;
    }

    /**
     * @return the objectTagName
     */
    public static Name getObjectTagName() {
        return objectTagName;
    }

    /**
     * @return the objectUrlAttrName
     */
    public static Name getObjectUrlAttrName() {
        return objectUrlAttrName;
    }

    /**
     * @return the versionTagName
     */
    public static Name getVersionTagName() {
        return versionTagName;
    }

    /**
     * @return the objectCapacityAttrName
     */
    public static Name getObjectCapacityAttrName() {
        return objectCapacityAttrName;
    }

    /**
     * @return the objectSizeAttrName
     */
    public static Name getObjectSizeAttrName() {
        return objectSizeAttrName;
    }

    /**
     * @return the okTagName
     */
    public static Name getOkTagName() {
        return okTagName;
    }

    /**
     * @return the rpcResultTagName
     */
    public static Name getRpcResultTagName() {
        return rpcResultTagName;
    }

    /**
     * @return the sessionTagName
     */
    public static Name getSessionTagName() {
        return sessionTagName;
    }

    /**
     * @return the helloPasswordAttrName
     */
    public static Name getHelloPasswordAttrName() {
        return helloPasswordAttrName;
    }

    /**
     * @return the helloUserAttrName
     */
    public static Name getHelloUserAttrName() {
        return helloUserAttrName;
    }

    /**
     * @return the helloTagName
     */
    public static Name getHelloTagName() {
        return helloTagName;
    }

    /**
     * @return the executeQueryTagName
     */
    public static Name getExecuteQueryTagName() {
        return executeQueryTagName;
    }

    /**
     * @return the databaseIdAttrName
     */
    public static Name getDatabaseIdAttrName() {
        return databaseIdAttrName;
    }

    /**
     * @return the sqlQueryTagName
     */
    public static Name getSqlQueryTagName() {
        return sqlQueryTagName;
    }

    /**
     * @return the parameterTagName
     */
    public static Name getParameterTagName() {
        return parameterTagName;
    }

    /**
     * @return the sqlTypeAttrName
     */
    public static Name getSqlTypeAttrName() {
        return sqlTypeAttrName;
    }

    /**
     * @return the parameterNameAttrName
     */
    public static Name getParameterNameAttrName() {
        return parameterNameAttrName;
    }

    /**
     * @return the executeUpdateTagName
     */
    public static Name getExecuteUpdateTagName() {
        return executeUpdateTagName;
    }

    /**
     * @return the modifiedRowsTagName
     */
    public static Name getModifiedRowsTagName() {
        return modifiedRowsTagName;
    }

    /**
     * @return the updateRowsetTagName
     */
    public static Name getUpdateRowsetTagName() {
        return updateRowsetTagName;
    }

    /**
     * @return the primaryKeysTagName
     */
    public static Name getPrimaryKeysTagName() {
        return primaryKeysTagName;
    }

    /**
     * @return the catalogAttrName
     */
    public static Name getCatalogAttrName() {
        return catalogAttrName;
    }

    /**
     * @return the schemaAttrName
     */
    public static Name getSchemaAttrName() {
        return schemaAttrName;
    }

    /**
     * @return the tableAttrName
     */
    public static Name getTableAttrName() {
        return tableAttrName;
    }

    /**
     * @return the foreignKeysTagName
     */
    public static Name getForeignKeysTagName() {
        return foreignKeysTagName;
    }

    /**
     * @return the indexKeysTagName
     */
    public static Name getIndexKeysTagName() {
        return indexKeysTagName;
    }

    /**
     * @return the achiveDbMetadataTagName
     */
    public static Name getAchiveDbMetadataTagName() {
        return achiveDbMetadataTagName;
    }

    /**
     * @return the metadataTypeAttrName
     */
    public static Name getMetadataTypeAttrName() {
        return metadataTypeAttrName;
    }

    /**
     * @return the tableCommentsTagName
     */
    public static Name getTableCommentsTagName() {
        return tableCommentsTagName;
    }

    /**
     * @return the schemaTablesCommentsTagName
     */
    public static Name getSchemaTablesCommentsTagName() {
        return schemaTablesCommentsTagName;
    }

    /**
     * @return the exceptionDetailTagName
     */
    public static Name getExceptionDetailTagName() {
        return exceptionDetailTagName;
    }

    /**
     * @return the exceptionClassAttrName
     */
    public static Name getExceptionClassAttrName() {
        return exceptionClassAttrName;
    }
}
