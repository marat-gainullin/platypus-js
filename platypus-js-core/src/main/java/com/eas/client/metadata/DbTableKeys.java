package com.eas.client.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DbTableKeys {

    protected Map<String, PrimaryKeySpec> pks = new HashMap<>();
    protected Map<String, ForeignKeySpec> fks = new HashMap<>();
    protected String tableName;

    private DbTableKeys(DbTableKeys aSource) {
        this();
        if (aSource != null) {
            String otherTableName = aSource.getTableName();
            if (otherTableName != null) {
                tableName = new String(otherTableName.toCharArray());
            }
            fks.clear();
            Map<String, ForeignKeySpec> lfks = aSource.getFks();
            Set<Entry<String, ForeignKeySpec>> lfkSet = lfks.entrySet();
            if (lfkSet != null) {
                Iterator<Entry<String, ForeignKeySpec>> lfkIt = lfkSet.iterator();
                while (lfkIt.hasNext()) {
                    Entry<String, ForeignKeySpec> lfkEntry = lfkIt.next();
                    String lfkFieldName = lfkEntry.getKey();
                    ForeignKeySpec lfkSpec = lfkEntry.getValue();
                    fks.put(new String(lfkFieldName.toCharArray()), (ForeignKeySpec) lfkSpec.copy());
                }
            }
            pks.clear();
            Map<String, PrimaryKeySpec> lpks = aSource.getPks();
            Set<Entry<String, PrimaryKeySpec>> lpkSet = lpks.entrySet();
            if (lpkSet != null) {
                Iterator<Entry<String, PrimaryKeySpec>> lpkIt = lpkSet.iterator();
                while (lpkIt.hasNext()) {
                    Entry<String, PrimaryKeySpec> lpkEntry = lpkIt.next();
                    String lpkFieldName = lpkEntry.getKey();
                    PrimaryKeySpec lpkSpec = lpkEntry.getValue();
                    pks.put(new String(lpkFieldName.toCharArray()), lpkSpec.copy());
                }
            }
        }
    }

    public boolean isPrimaryKey(String fieldName) {
        return (pks != null && pks.containsKey(fieldName));
    }

    public boolean isForeignKey(String fieldName) {
        return (fks != null && fks.containsKey(fieldName));
    }

    public DbTableKeys() {
        super();
    }

    /**
     * Constructor with table name
     * @param aTableName Table name without any schema name.
     */
    public DbTableKeys(String aTableName) {
        super();
        tableName = aTableName;
    }

    /**
     * Returns table name without any schema name.
     * @return Table name without any schema name.
     */
    public String getTableName() {
        return tableName;
    }

    public void addForeignKey(String aFkSchema, String aFkTable, String aFkField, String aFkName, ForeignKeySpec.ForeignKeyRule afkUpdateRule, ForeignKeySpec.ForeignKeyRule afkDeleteRule, boolean afkDeferrable, String aPkSchema, String aPkTable, String aPkField, String aPkName) {
        fks.put(aFkField, new ForeignKeySpec(aFkSchema, aFkTable, aFkField, aFkName, afkUpdateRule, afkDeleteRule, afkDeferrable, aPkSchema, aPkTable, aPkField, aPkName));
    }

    public void addPrimaryKey(String aPkSchema, String aPkTable, String aPkField, String aPkName) {
        pks.put(aPkField, new PrimaryKeySpec(aPkSchema, aPkTable, aPkField, aPkName));
    }

    public void clear() {
        fks.clear();
        pks.clear();
    }

    public boolean isEmpty() {
        return (pks == null || pks.isEmpty()) && (fks == null || fks.isEmpty());
    }

    public Map<String, ForeignKeySpec> getFks() {
        return fks;
    }

    public Map<String, PrimaryKeySpec> getPks() {
        return pks;
    }

    public DbTableKeys copy() {
        DbTableKeys dbTblFks = new DbTableKeys(this);
        return dbTblFks;
    }

    public static boolean isKeysCompatible(DbTableKeys table1Keys, String field1Name, DbTableKeys table2Keys, String field2Name) {
        if (table1Keys != null && field1Name != null
                && table2Keys != null && field2Name != null) {
            PrimaryKeySpec[] lKeys = new PrimaryKeySpec[2];
            int lKeysCount = 0;
            Map<String, PrimaryKeySpec> lPks = table1Keys.getPks();
            Map<String, ForeignKeySpec> lFks = table1Keys.getFks();
            if (lPks.containsKey(field1Name)) {
                PrimaryKeySpec lfoundPk = lPks.get(field1Name);
                lKeys[lKeysCount] = new PrimaryKeySpec(lfoundPk.getSchema(), lfoundPk.getTable(), lfoundPk.getField(), lfoundPk.getCName());
                lKeysCount++;
            }
            if (lFks.containsKey(field1Name)) {
                ForeignKeySpec lfoundFk = lFks.get(field1Name);
                lKeys[lKeysCount] = new PrimaryKeySpec(lfoundFk.getReferee().getSchema(), lfoundFk.getReferee().getTable(), lfoundFk.getReferee().getField(), lfoundFk.getReferee().getCName());
                lKeysCount++;
            }
            PrimaryKeySpec[] rKeys = new PrimaryKeySpec[2];
            int rKeysCount = 0;
            Map<String, ForeignKeySpec> rFks = table2Keys.getFks();
            Map<String, PrimaryKeySpec> rPks = table2Keys.getPks();
            if (rPks.containsKey(field2Name)) {
                PrimaryKeySpec lfoundPk = rPks.get(field2Name);
                rKeys[rKeysCount] = new PrimaryKeySpec(lfoundPk.getSchema(), lfoundPk.getTable(), lfoundPk.getField(), lfoundPk.getCName());
                rKeysCount++;
            }
            if (rFks.containsKey(field2Name)) {
                ForeignKeySpec lfoundFk = rFks.get(field2Name);
                rKeys[rKeysCount] = new PrimaryKeySpec(lfoundFk.getReferee().getSchema(), lfoundFk.getReferee().getTable(), lfoundFk.getReferee().getField(), lfoundFk.getReferee().getCName());
                rKeysCount++;
            }
            for (int i = 0; i < lKeysCount; i++) {
                for (int j = 0; j < rKeysCount; j++) {
                    String lSchema = lKeys[i].getSchema();
                    if (lSchema == null) {
                        lSchema = "";
                    }
                    lSchema = lSchema.toUpperCase();
                    String lTable = lKeys[i].getTable();
                    if (lTable == null) {
                        lTable = "";
                    }
                    lTable = lTable.toUpperCase();
                    String lField = lKeys[i].getField();
                    if (lField == null) {
                        lField = "";
                    }
                    lField = lField.toUpperCase();

                    String rSchema = rKeys[j].getSchema();
                    if (rSchema == null) {
                        rSchema = "";
                    }
                    rSchema = rSchema.toUpperCase();
                    String rTable = rKeys[j].getTable();
                    if (rTable == null) {
                        rTable = "";
                    }
                    rTable = rTable.toUpperCase();
                    String rField = rKeys[j].getField();
                    if (rField == null) {
                        rField = "";
                    }
                    rField = rField.toUpperCase();

                    if (lSchema.equals(rSchema)
                            && lTable.equals(rTable)
                            && lField.equals(rField)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
