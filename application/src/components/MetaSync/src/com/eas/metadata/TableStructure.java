/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * structure for metadata one table
 *
 * @author vy
 */
public class TableStructure {

    private String tableName;
    private String tableDescription;
    private Fields tableFields;
    private Map<String, DbTableIndexSpec> tableIndexSpecs;
    private String pKeyCName;
    private List<PrimaryKeySpec> tablePKeySpecs;
    private Map<String, List<ForeignKeySpec>> tableFKeySpecs;
    // maps upper name -> name
    private Map<String, String> fieldsNameUpper;
    private Map<String, String> indexesNameUpper;
    private Map<String, String> fkeysNameUpper;

    /**
     * get table fields
     *
     * @return the tableFields
     */
    public Fields getTableFields() {
        return tableFields;
    }

    /**
     * set table fields
     *
     * @param aTableFields the tableFields to set
     */
    public void setTableFields(Fields aTableFields) {
        tableFields = aTableFields;
    }

    /**
     * get table comment
     *
     * @return the tableDescription
     */
    public String getTableDescription() {
        return tableDescription;
    }

    /**
     * set table comment
     *
     * @param aTableDescription the tableDescription to set
     */
    public void setTableDescription(String aTableDescription) {
        tableDescription = aTableDescription;
    }

    /**
     * get table name
     *
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * set table name
     *
     * @param aTableName the tableName to set
     */
    public void setTableName(String aTableName) {
        tableName = aTableName;
    }

    /**
     * get primary key name
     *
     * @return the pKeyCName
     */
    public String getPKeyCName() {
        return pKeyCName;
    }

    /**
     * set primary key name
     *
     * @param aPKeyCName the pKeyCName to set
     */
    public void setPKeyCName(String aPKeyCName) {
        pKeyCName = aPKeyCName;
    }

    /**
     * get primary key name in uppercase
     *
     * @return the pKeyCName
     */
    public String getPKeyCNameUpper() {
        return pKeyCName.toUpperCase();
    }

    /**
     * get primary key columns specifications
     *
     * @return the tablePKeySpecs
     */
    public List<PrimaryKeySpec> getTablePKeySpecs() {
        return tablePKeySpecs;
    }

    /**
     * set primary key columns specifications
     *
     * @param aTablePKeySpecs the tablePKeySpecs to set
     */
    public void setTablePKeySpecs(List<PrimaryKeySpec> aTablePKeySpecs) {
        tablePKeySpecs = aTablePKeySpecs;
    }

    /**
     * get foreign keys specifications
     *
     * @return the tableFKeySpecs
     */
    public Map<String, List<ForeignKeySpec>> getTableFKeySpecs() {
        return tableFKeySpecs;
    }

    /**
     * set foreign keys specifications
     *
     * @param aTableFKeySpecs the tableFKeySpecs to set
     */
    public void setTableFKeySpecs(Map<String, List<ForeignKeySpec>> aTableFKeySpecs) {
        tableFKeySpecs = aTableFKeySpecs;
    }

    /**
     * get indexes specifications
     *
     * @return the tableIndexSpecs
     */
    public Map<String, DbTableIndexSpec> getTableIndexSpecs() {
        return tableIndexSpecs;
    }

    /**
     * set indexes specifications
     *
     * @param aTableIndexSpecs the tableIndexSpecs to set
     */
    public void setTableIndexSpecs(Map<String, DbTableIndexSpec> aTableIndexSpecs) {
        tableIndexSpecs = aTableIndexSpecs;
    }

    /**
     * create decode (uppername -> name) for all names in maps: fields,indexes, foreign keys
     */
    public void makeMapNamesToUpper() {

        // fields names
        if (tableFields != null) {
            fieldsNameUpper = new HashMap();
            for (int i = 1; i <= tableFields.getFieldsCount(); i++) {
                Field field = tableFields.get(i);
                String name = field.getName();
                fieldsNameUpper.put(name.toUpperCase(), name);
            }
        }
        // indexes names
        if (tableIndexSpecs != null) {
            indexesNameUpper = new HashMap();
            for (String name : tableIndexSpecs.keySet()) {
                indexesNameUpper.put(name.toUpperCase(), name);
            }
        }
        // foreign keys
        if (tableFKeySpecs != null) {
            fkeysNameUpper = new HashMap();
            for (String name : tableFKeySpecs.keySet()) {
                fkeysNameUpper.put(name.toUpperCase(), name);
            }
        }
    }

    /**
     * get original field name
     *
     * @param upperName name field convert to UpperCase
     * @return field name
     */
    public String getOriginalFieldName(String upperName) {
        return fieldsNameUpper.get(upperName);
    }

    /**
     * get original index name
     *
     * @param upperName name index convert to UpperCase
     * @return index name
     */
    public String getOriginalIndexName(String upperName) {
        return indexesNameUpper.get(upperName);
    }

    /**
     * get original foreign key name
     *
     * @param upperName name foreign key convert to UpperCase
     * @return foreign key name
     */
    public String getOriginalFKeyName(String upperName) {
        return fkeysNameUpper.get(upperName);
    }
}
