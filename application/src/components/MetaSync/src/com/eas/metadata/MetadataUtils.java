/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.metadata.PrimaryKeySpec;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for merge and print metadata
 *
 * @author vy
 */
public class MetadataUtils {

    private static final String DLM = "\t";
    private static final String ATTRDLM = ", ";
    private static final String SPACE = "   ";
    private static final String EQUAL = " + ";
    private static final String NOEQUAL = " ?? ";
    private static final String NOFOUND = " -- ";
    private static final String TITLE = " src" + DLM + " dest" + DLM + DLM + DLM + "Object";
    private static final Set<Integer> CLOB_SAME = new HashSet<>();
    private static final Set<Integer> DECIMAL_NUMERIC_SAME = new HashSet<>();

    static {
        // like CLOB
        CLOB_SAME.add(Types.CLOB);
        CLOB_SAME.add(Types.LONGVARCHAR);
        // ????
        DECIMAL_NUMERIC_SAME.add(Types.DECIMAL);
        DECIMAL_NUMERIC_SAME.add(Types.NUMERIC);
    }

    /**
     * Check on identical sql types
     *
     * @param leftSqlType first sql type
     * @param rightSqlType second sql type
     * @return
     */
    public static boolean isSameSqlType(int leftSqlType, int rightSqlType, boolean oneDialect) {
        if (oneDialect) {
            return (leftSqlType == rightSqlType);
        }
        if (CLOB_SAME.contains(leftSqlType) && CLOB_SAME.contains(rightSqlType)) {
            return true;
        }
        if (DECIMAL_NUMERIC_SAME.contains(leftSqlType) && DECIMAL_NUMERIC_SAME.contains(rightSqlType)) {
            return true;
        }
//???????????        
        return (leftSqlType == rightSqlType);
//        return SQLUtils.isSameTypeGroup(leftSqlType, rightSqlType);
    }

    /**
     * Check on identical fields
     *
     * @param leftField first field
     * @param rightField second field
     * @return
     */
    public static boolean isSameField(Field leftField, Field rightField, Boolean sized, Boolean scaled, boolean oneDialect) {
        if (leftField == null && rightField == null) {
            return true;
        }
        if (leftField == null || rightField == null) {
            return false;
        }

        // no compare description, schema name, fkey specification
        DataTypeInfo leftTypeInfo = leftField.getTypeInfo();
        DataTypeInfo rightTypeInfo = rightField.getTypeInfo();
        if (leftTypeInfo != null) {

            if (rightTypeInfo != null) {
                if (!isSameSqlType(leftTypeInfo.getSqlType(), rightTypeInfo.getSqlType(), oneDialect)) {
                    return false;
                }
                int leftSize = leftField.getSize();
                int rightSize = rightField.getSize();
                int leftScale = leftField.getScale();
                int rightScale = rightField.getScale();
                int leftSqlType = leftTypeInfo.getSqlType();

                if (oneDialect) {
                    if (leftSize != rightSize || leftScale != rightScale) {
                        return false;
                    }
                } else {
                    if (sized != null && scaled != null) {
                        if (sized && leftSize != rightSize && leftSize != 0 && rightSize != 0) {
                            return false;
                        }
                        if (scaled && leftScale != rightScale) {
                            return false;
                        }
                    } else {
                        if (leftSqlType != Types.TIMESTAMP) {
                            // if one in default(==0)? then no compare
                            if (leftSize != rightSize && leftSize != 0 && rightSize != 0) {
                                return false;
                            }
                        }
                        // only for number
                        if (SQLUtils.getTypeGroup(leftTypeInfo.getSqlType()) == SQLUtils.TypesGroup.NUMBERS) {
                            if (leftField.getScale() != rightField.getScale()) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        } else if (rightTypeInfo != null) {
            return false;
        }

        String leftName = leftField.getName();
        String leftTableName = leftField.getTableName();

        String rightName = rightField.getName();
        String rightTableName = rightField.getTableName();

        return leftField.isNullable() == rightField.isNullable()
                && leftField.isSigned() == rightField.isSigned()
                && leftField.isPk() == rightField.isPk()
                && leftField.isFk() == rightField.isFk()
                && leftField.isReadonly() == rightField.isReadonly()
                && ((leftTableName == null && rightTableName == null) || (leftTableName != null && leftTableName.equalsIgnoreCase(rightTableName)))
                && ((leftName == null && rightName == null) || (leftName != null && leftName.equalsIgnoreCase(rightName)));
    }

    /**
     * Check on identical foreign keys specifications
     *
     * @param leftFKSpec first foreign key specification
     * @param rightFKSpec second foreign key specification
     * @return
     */
    public static boolean isSameFKey(ForeignKeySpec leftFKSpec, ForeignKeySpec rightFKSpec, boolean oneDialect) {
        if (leftFKSpec == null && rightFKSpec == null) {
            return true;
        }
        if (leftFKSpec == null || rightFKSpec == null) {
            return false;
        }

        // no compare schema name
        String leftCName = leftFKSpec.getCName();
        String leftFieldName = leftFKSpec.getField();
        String leftTableName = leftFKSpec.getTable();

        String rightCName = rightFKSpec.getCName();
        String rightFieldName = rightFKSpec.getField();
        String rightTableName = rightFKSpec.getTable();

        ForeignKeyRule leftFkUpdateRule = leftFKSpec.getFkUpdateRule();
        ForeignKeyRule rightFkUpdateRule = rightFKSpec.getFkUpdateRule();

        return leftFKSpec.getFkDeferrable() == rightFKSpec.getFkDeferrable()
                //                && leftFKSpec.getFkUpdateRule() == rightFKSpec.getFkUpdateRule()
                // !!! in Oracle not exists UpdateRule !!!!!
                && (rightFkUpdateRule == null || leftFkUpdateRule == null || rightFkUpdateRule.equals(leftFkUpdateRule))
                && leftFKSpec.getFkDeleteRule() == rightFKSpec.getFkDeleteRule()
                && ((leftTableName == null && rightTableName == null) || (leftTableName != null && leftTableName.equalsIgnoreCase(rightTableName)))
                && ((leftFieldName == null && rightFieldName == null) || (leftFieldName != null && leftFieldName.equalsIgnoreCase(rightFieldName)))
                && ((leftCName == null && rightCName == null) || (leftCName != null && leftCName.equalsIgnoreCase(rightCName)))
                && isSamePKey(leftFKSpec.getReferee(), rightFKSpec.getReferee(), oneDialect);

    }

    /**
     * Check on identical primary keys specifications
     *
     * @param leftPKSpec first primary key specification
     * @param rightPKSpec second primary key specification
     * @return
     */
    public static boolean isSamePKey(PrimaryKeySpec leftPKSpec, PrimaryKeySpec rightPKSpec, boolean oneDialect) {
        if (leftPKSpec == null && rightPKSpec == null) {
            return true;
        }
        if (leftPKSpec == null || rightPKSpec == null) {
            return false;
        }

        // no compare schema name
        String leftCName = leftPKSpec.getCName();
        String leftFieldName = leftPKSpec.getField();
        String leftTableName = leftPKSpec.getTable();

        String rightCName = rightPKSpec.getCName();
        String rightFieldName = rightPKSpec.getField();
        String rightTableName = rightPKSpec.getTable();

        return ((leftTableName == null && rightTableName == null) || (leftTableName != null && leftTableName.equalsIgnoreCase(rightTableName)))
                && ((leftFieldName == null && rightFieldName == null) || (leftFieldName != null && leftFieldName.equalsIgnoreCase(rightFieldName)));
    }

    /**
     * Check on identical columns primary key specifications
     *
     * @param leftPKSpecs first columns primary key specification
     * @param rightPKSpecs second columns primary key specification
     * @return
     */
    public static boolean isSamePKeys(List<PrimaryKeySpec> leftPKSpecs, List<PrimaryKeySpec> rightPKSpecs, boolean oneDialect) {
        if (leftPKSpecs == null && rightPKSpecs == null) {
            return true;
        }
        if (leftPKSpecs == null || rightPKSpecs == null || leftPKSpecs.size() != rightPKSpecs.size()) {
            return false;
        }

        for (int i = 0; i < leftPKSpecs.size(); i++) {
            if (!isSamePKey(leftPKSpecs.get(i), rightPKSpecs.get(i), oneDialect)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check on identical columns foreign key specifications
     *
     * @param leftFKSpecs first foreign key specification
     * @param rightFKSpecs second foreign key specification
     * @return
     */
    public static boolean isSameFKeys(List<ForeignKeySpec> leftFKSpecs, List<ForeignKeySpec> rightFKSpecs, boolean oneDialect) {
        if (leftFKSpecs == null && rightFKSpecs == null) {
            return true;
        }
        if (leftFKSpecs == null || rightFKSpecs == null || leftFKSpecs.size() != rightFKSpecs.size()) {
            return false;
        }

        for (int i = 0; i < leftFKSpecs.size(); i++) {
            if (!isSameFKey(leftFKSpecs.get(i), rightFKSpecs.get(i), oneDialect)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check on identical index specifications
     *
     * @param leftIndexSpec first index specification
     * @param rightIndexSpec second index specification
     * @return
     */
    public static boolean isSameIndex(DbTableIndexSpec leftIndexSpec, DbTableIndexSpec rightIndexSpec, boolean oneDialect) {
        if (leftIndexSpec == null && rightIndexSpec == null) {
            return true;
        }
        if (leftIndexSpec == null || rightIndexSpec == null) {
            return false;
        }

        String leftName = leftIndexSpec.getName();
        String rightName = rightIndexSpec.getName();

        return (leftIndexSpec.isHashed() == rightIndexSpec.isHashed()
                && leftIndexSpec.isUnique() == rightIndexSpec.isUnique()
                && ((leftName == null && rightName == null) || (leftName != null && leftName.equalsIgnoreCase(rightName)))
                && isSameIndColumns(leftIndexSpec.getColumns(), rightIndexSpec.getColumns(), oneDialect));
    }

    /**
     * Check on identical columns indexes specifications
     *
     * @param leftColumnsIndexSpec first columns index specification
     * @param rightColumnsIndexSpec second columns index specification
     * @return
     */
    public static boolean isSameIndColumns(List<DbTableIndexColumnSpec> leftColumnsIndexSpec, List<DbTableIndexColumnSpec> rightColumnsIndexSpec, boolean oneDialect) {
        if (leftColumnsIndexSpec == null && rightColumnsIndexSpec == null) {
            return true;
        }
        if (leftColumnsIndexSpec == null || rightColumnsIndexSpec == null) {
            return false;
        }

        int leftCountColumns = leftColumnsIndexSpec.size();
        int rightCountColumns = rightColumnsIndexSpec.size();

        if (leftCountColumns != rightCountColumns) {
            return false;
        }

        for (int i = 0; i < leftCountColumns; i++) {
            DbTableIndexColumnSpec leftIndexColumnSpec = leftColumnsIndexSpec.get(i);
            DbTableIndexColumnSpec rightIndexColumnSpec = rightColumnsIndexSpec.get(i);
            String leftColumnName = leftIndexColumnSpec.getColumnName();
            String rightColumnName = rightIndexColumnSpec.getColumnName();

            if (!((leftIndexColumnSpec.getOrdinalPosition() == rightIndexColumnSpec.getOrdinalPosition())
                    && ((leftColumnName == null && rightColumnName == null) || (leftColumnName != null && leftColumnName.equalsIgnoreCase(rightColumnName))))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check on Platypus system tables
     *
     * @param aTableName table name
     * @return
     */
    public static boolean isSystemTable(String aTableName) {
        if (aTableName == null) {
            return false;
        }
        String tableName = aTableName.toUpperCase();
        if (tableName.equalsIgnoreCase(ClientConstants.T_MTD_USERS)) {
            return true;
        }
        if (tableName.equalsIgnoreCase(ClientConstants.T_MTD_GROUPS)) {
            return true;
        }
        if (tableName.equalsIgnoreCase(ClientConstants.T_MTD_VERSION)) {
            return true;
        }
        return false;
    }

    /**
     * generate information line with table properties
     *
     * @param aHeader line header
     * @param aStructure table structure
     * @return
     */
    public static String printTable(String aHeader, TableStructure aStructure) {
        if (aStructure != null) {

            StringBuilder sb = new StringBuilder();
            if (aHeader != null) {
                sb.append(aHeader).append(DLM);
            }
            sb.append(aStructure.getTableName()).append("=[");

            String tableDescription = aStructure.getTableDescription();
            sb.append(tableDescription == null ? "" : tableDescription).append(ATTRDLM);

            sb.append("Fields:").append(aStructure.getTableFields().getFieldsCount()).append(ATTRDLM);

            Map<String, DbTableIndexSpec> indexes = aStructure.getTableIndexSpecs();
            sb.append("Indexes:").append((indexes == null ? 0 : indexes.size())).append(ATTRDLM);

            int cnt = 0;
            int cntF = 0;
            List<PrimaryKeySpec> tablePKeySpecs = aStructure.getTablePKeySpecs();
            if (tablePKeySpecs != null) {
                cnt = 1;
                cntF = cntF + tablePKeySpecs.size();
            }
            sb.append("Primary Key (fields):").append(cnt).append("(").append(cntF).append(")").append(ATTRDLM);

            cnt = 0;
            cntF = 0;

            Map<String, List<ForeignKeySpec>> tableFKeySpecs = aStructure.getTableFKeySpecs();
            if (tableFKeySpecs != null) {
                for (String name : tableFKeySpecs.keySet()) {
                    cnt++;
                    cntF = cntF + tableFKeySpecs.get(name).size();
                }
            }
            sb.append("Foreign Key (fields):").append(cnt).append("(").append(cntF).append(")");
            sb.append("]");
            return (sb.toString());
        } else {
            return aHeader;
        }

    }

    /**
     * generate information line with field properties
     *
     * @param aHeader line header
     * @param aField column from table
     * @return
     */
    public static String printField(String aHeader, Field aField) {
        if (aField != null) {
            StringBuilder sb = new StringBuilder();

            if (aHeader != null) {
                sb.append(aHeader).append(DLM);
            }
            sb.append(aField.getName()).append("=[");

            String description = aField.getDescription();
            sb.append(description == null ? "" : description).append(ATTRDLM);
            sb.append("pk:").append(aField.isPk()).append(ATTRDLM);
            sb.append("fk:").append(aField.isFk()).append(ATTRDLM);
            sb.append("nullable:").append(aField.isNullable()).append(ATTRDLM);
            sb.append("readonly:").append(aField.isReadonly()).append(ATTRDLM);
            sb.append("signed:").append(aField.isSigned()).append(ATTRDLM);

            DataTypeInfo tInfo = aField.getTypeInfo();
            sb.append("type:{").append(tInfo.getSqlType()).append(ATTRDLM);
            sb.append(tInfo.getSqlTypeName()).append(ATTRDLM);
            sb.append("}").append(ATTRDLM);

            sb.append("size:").append(aField.getSize()).append(ATTRDLM);
            sb.append("scale:").append(aField.getScale()).append(ATTRDLM);
            sb.append("precision:").append(aField.getPrecision());
            sb.append("]");

            return sb.toString();
        } else {
            return aHeader;
        }
    }

    /**
     * generate information line with index properties
     *
     * @param aHeader line header
     * @param aIndexSpec specification for index
     * @return
     */
    public static String printIndex(String aHeader, DbTableIndexSpec aIndexSpec) {
        if (aIndexSpec != null) {
            StringBuilder sb = new StringBuilder();

            if (aHeader != null) {
                sb.append(aHeader).append(DLM);
            }
            sb.append(aIndexSpec.getName()).append("=[");
            sb.append("isClustered:").append(aIndexSpec.isClustered()).append(ATTRDLM);
            sb.append("isHashed:").append(aIndexSpec.isHashed()).append(ATTRDLM);
            sb.append("isUnique:").append(aIndexSpec.isUnique()).append(ATTRDLM);
            sb.append("Columns:{");

            List<DbTableIndexColumnSpec> columns = aIndexSpec.getColumns();
            if (columns != null) {
                int size = columns.size();
                for (int i = 0; i < size; i++) {
                    DbTableIndexColumnSpec col = columns.get(i);
                    sb.append(col.getOrdinalPosition()).append("-").append(col.getColumnName()).append(col.isAscending() ? "" : "-desc");
                    if (i + 1 < size) {
                        sb.append(ATTRDLM);
                    }
                }
            }
            sb.append("}]");

            return sb.toString();
        } else {
            return aHeader;
        }
    }

    /**
     * generate information line with primary key properties
     *
     * @param aHeader line header
     * @param aPKSpec specification for primary key (list columns in primary key)
     * @return
     */
    public static String printPKey(String aHeader, List<PrimaryKeySpec> aPKSpec) {
        if (aPKSpec != null && aPKSpec.size() > 0) {
            StringBuilder sb = new StringBuilder();

            if (aHeader != null) {
                sb.append(aHeader).append(DLM);
            }
            // get Name from first fields
            sb.append(aPKSpec.get(0).getCName()).append("=[");
            sb.append("Field:{");
            int size = aPKSpec.size();
            for (int i = 0; i < size; i++) {
                sb.append(aPKSpec.get(i).getField());
                if (i + 1 < size) {
                    sb.append(ATTRDLM);
                }
            }
            sb.append("}]");

            return sb.toString();
        } else {
            return aHeader;
        }
    }

    /**
     * generate information line with foreign key properties
     *
     * @param aHeader line header
     * @param aFKSpec specification for foreign key (list columns in foreign key)
     * @return
     */
    public static String printFKey(String aHeader, List<ForeignKeySpec> aFKSpec) {
        if (aFKSpec != null && aFKSpec.size() > 0) {
            StringBuilder sb = new StringBuilder();

            if (aHeader != null) {
                sb.append(aHeader).append(DLM);
            }
            // get all from first fields
            ForeignKeySpec fkSpec0 = aFKSpec.get(0);
            sb.append(fkSpec0.getCName()).append("=[");
            sb.append("Deferable:").append(fkSpec0.getFkDeferrable()).append(ATTRDLM);
            sb.append("DeleteRule:").append(fkSpec0.getFkDeleteRule()).append(ATTRDLM);
            sb.append("UpdateRule:").append(fkSpec0.getFkUpdateRule()).append(ATTRDLM);

            PrimaryKeySpec referee0 = fkSpec0.getReferee();
            sb.append("RefereeTable:").append((referee0 == null ? "" : referee0.getTable())).append(ATTRDLM);
            sb.append("RefereePKName:").append((referee0 == null ? "" : referee0.getCName())).append(ATTRDLM);
            sb.append("Field -> RefereePKField:{");
            int size = aFKSpec.size();
            for (int i = 0; i < size; i++) {
                ForeignKeySpec fk = aFKSpec.get(i);
                sb.append(fk.getField()).append(" -> ");
                PrimaryKeySpec referee = fk.getReferee();
                sb.append((referee == null ? "" : fk.getReferee().getField()));
                if (i + 1 < size) {
                    sb.append(ATTRDLM);
                }
            }
            sb.append("}]");
            return sb.toString();
        } else {
            return aHeader;
        }
    }

    /**
     * generate information line with check on identical tables
     *
     * @param srcTblStructure first table structure
     * @param destTblStructure second table structure
     * @return
     */
    public static String printCompareTable(TableStructure srcTblStructure, TableStructure destTblStructure, boolean oneDialect) {
        StringBuilder sb = new StringBuilder();
        String srcTable = printTable("Table:", srcTblStructure);
        String destTable = printTable("Table:", destTblStructure);
        if (srcTblStructure != null) {
            sb.append(EQUAL).append(DLM);
            if (destTblStructure != null) {
                sb.append(srcTable.equalsIgnoreCase(destTable) ? EQUAL : NOEQUAL);
            } else {
                sb.append(NOFOUND);
            }

            sb.append(DLM).append("src").append(DLM).append(srcTable).append("\n");
            sb.append(SPACE).append(DLM).append(SPACE);
            sb.append(DLM).append("dest").append(DLM).append(destTable).append("\n");

            if (destTblStructure != null) {
                // each field
                Fields dFields = destTblStructure.getTableFields();
                Fields sFields = srcTblStructure.getTableFields();

                // for all fields source
                for (int i = 1; i <= sFields.getFieldsCount(); i++) {
                    Field sField = sFields.get(i);
                    Field dField = null;
                    if (dFields != null) {
                        dField = dFields.get(destTblStructure.getOriginalFieldName(sField.getName().toUpperCase()));
                    }

                    sb.append(printCompareField(sField, dField, oneDialect));

                }
                // for all fields destination
                if (dFields != null) {
                    for (int i = 1; i <= dFields.getFieldsCount(); i++) {
                        Field dField = dFields.get(i);
                        Field sField = sFields.get(srcTblStructure.getOriginalFieldName(dField.getName().toUpperCase()));
                        // only if not exists in source
                        if (sField == null) {
                            sb.append(printCompareField(null, dField, oneDialect));
                        }
                    }
                }    

                // each indexes
                Map<String, DbTableIndexSpec> sIndexes = srcTblStructure.getTableIndexSpecs();
                Map<String, DbTableIndexSpec> dIndexes = destTblStructure.getTableIndexSpecs();


                if (sIndexes != null) {
                    // for all indexes in source
                    for (String name : sIndexes.keySet()) {
                        DbTableIndexSpec sIndex = sIndexes.get(name);
                        if (dIndexes != null) {
                            DbTableIndexSpec dIndex = dIndexes.get(destTblStructure.getOriginalIndexName(name.toUpperCase()));
                            sb.append(printCompareIndex(sIndex, dIndex, oneDialect));
                        }
                    }
                }
                if (dIndexes != null) {
                    // for all indexes in destination
                    for (String name : dIndexes.keySet()) {
                        DbTableIndexSpec dIndex = dIndexes.get(name);
                        // only if not exists in source
                        if (sIndexes == null || sIndexes.get(srcTblStructure.getOriginalIndexName(name.toUpperCase())) == null) {
                            sb.append(printCompareIndex(null, dIndex, oneDialect));
                        }
                    }

                }
                // pkey
                List<PrimaryKeySpec> sPKeys = srcTblStructure.getTablePKeySpecs();
                List<PrimaryKeySpec> dPKeys = destTblStructure.getTablePKeySpecs();

                if (sPKeys != null || dPKeys != null) {
                    sb.append(printComparePKey(sPKeys, dPKeys, oneDialect));
                }

                // all fkeys
                Map<String, List<ForeignKeySpec>> sFKeySpecs = srcTblStructure.getTableFKeySpecs();
                Map<String, List<ForeignKeySpec>> dFKeySpecs = destTblStructure.getTableFKeySpecs();

                if (sFKeySpecs != null) {
                    // for each fkeysSpec in source
                    for (String name : sFKeySpecs.keySet()) {
                        List<ForeignKeySpec> sFKeys = sFKeySpecs.get(name);
                        List<ForeignKeySpec> dFKeys = null;
                        if (dFKeySpecs != null) {
                            dFKeys = dFKeySpecs.get(destTblStructure.getOriginalFKeyName(name.toUpperCase()));
                        }
                        sb.append(printCompareFKeys(sFKeys, dFKeys, oneDialect));
                    }
                    // for each fkeysSpec in destination
                    if (dFKeySpecs != null) {
                        for (String name : dFKeySpecs.keySet()) {
                            List<ForeignKeySpec> dFKeys = dFKeySpecs.get(name);
                            //List<ForeignKeySpec> sFKeys = null;
                            if (sFKeySpecs.get(srcTblStructure.getOriginalFKeyName(name.toUpperCase())) == null) {
                                sb.append(printCompareFKeys(null, dFKeys, oneDialect));
                            }
                        }
                    }    
                }
            }



        } else {
            sb.append(NOFOUND).append(DLM).append(EQUAL);
            sb.append(DLM).append("dest").append(DLM).append(printTable("Table:", destTblStructure)).append("\n");
        }
        return sb.toString();
    }

    /**
     * generate information line with check on identical indexes
     *
     * @param leftIndexSpec first index specification
     * @param rightIndexSpec second index specification
     * @return
     */
    public static String printCompareIndex(DbTableIndexSpec leftIndexSpec, DbTableIndexSpec rightIndexSpec, boolean oneDialect) {
        StringBuilder sb = new StringBuilder();
        if (leftIndexSpec != null) {
            sb.append(EQUAL).append(DLM);
            if (rightIndexSpec != null) {
                sb.append(isSameIndex(leftIndexSpec, rightIndexSpec, oneDialect) ? EQUAL : NOEQUAL);
            } else {
                sb.append(NOFOUND);
            }

            sb.append(DLM).append("src").append(DLM).append(printIndex("Index:", leftIndexSpec)).append("\n");
            sb.append(SPACE).append(DLM).append(SPACE);
        } else {
            sb.append(NOFOUND).append(DLM).append(EQUAL);
        }
        sb.append(DLM).append("dest").append(DLM).append(printIndex("Index:", rightIndexSpec)).append("\n");
        return sb.toString();
    }

    /**
     * generate information line with check on identical fields
     *
     * @param leftField first field specification
     * @param rightField second field specification
     * @return
     */
    public static String printCompareField(Field leftField, Field rightField, boolean oneDialect) {
        StringBuilder sb = new StringBuilder();
        if (leftField != null) {
            sb.append(EQUAL).append(DLM);
            if (rightField != null) {
                sb.append(isSameField(leftField, rightField, null, null, oneDialect) ? EQUAL : NOEQUAL);
            } else {
                sb.append(NOFOUND);
            }

            sb.append(DLM).append("src").append(DLM).append(printField("Field:", leftField)).append("\n");
            sb.append(SPACE).append(DLM).append(SPACE);
        } else {
            sb.append(NOFOUND).append(DLM).append(EQUAL);
        }
        sb.append(DLM).append("dest").append(DLM).append(printField("Field:", rightField)).append("\n");
        return sb.toString();

    }

    /**
     * generate information line with check on identical primary keys
     *
     * @param leftPKSpec first primary key specification
     * @param rightPKSpec second primary key specification
     * @return
     */
    public static String printComparePKey(List<PrimaryKeySpec> leftPKSpec, List<PrimaryKeySpec> rightPKSpec, boolean oneDialect) {
        StringBuilder sb = new StringBuilder();
        if (leftPKSpec != null) {
            sb.append(EQUAL).append(DLM);
            if (rightPKSpec != null) {
                sb.append(isSamePKeys(leftPKSpec, rightPKSpec, oneDialect) ? EQUAL : NOEQUAL);
            } else {
                sb.append(NOFOUND);
            }

            sb.append(DLM).append("src").append(DLM).append(printPKey("PrimaryKey:", leftPKSpec)).append("\n");
            sb.append(SPACE).append(DLM).append(SPACE);
        } else {
            sb.append(NOFOUND).append(DLM).append(EQUAL);
        }
        sb.append(DLM).append("dest").append(DLM).append(printPKey("PrimaryKey:", rightPKSpec)).append("\n");
        return sb.toString();
    }

    /**
     * generate information line with check on identical foreign keys
     *
     * @param leftFKSpec first foreign key specification
     * @param rightFKSpec second foreign key specification
     * @return
     */
    public static String printCompareFKeys(List<ForeignKeySpec> leftFKSpec, List<ForeignKeySpec> rightFKSpec, boolean oneDialect) {
        StringBuilder sb = new StringBuilder();
        if (leftFKSpec != null) {
            sb.append(EQUAL).append(DLM);
            if (rightFKSpec != null) {
                sb.append(isSameFKeys(leftFKSpec, rightFKSpec, oneDialect) ? EQUAL : NOEQUAL);
            } else {
                sb.append(NOFOUND);
            }

            sb.append(DLM).append("src").append(DLM).append(printFKey("ForeignKey:", leftFKSpec)).append("\n");
            sb.append(SPACE).append(DLM).append(SPACE);
        } else {
            sb.append(NOFOUND).append(DLM).append(EQUAL);
        }
        sb.append(DLM).append("dest").append(DLM).append(printFKey("ForeignKey:", rightFKSpec)).append("\n");
        return sb.toString();
    }

    /**
     * generate title for compare metadates
     *
     * @return the TITLE
     */
    public static String printTitle() {
        return TITLE;
    }

    /**
     * print structure database to log
     *
     * @param aMetadata structure database
     */
    public static void printCompareMetadata(DBStructure aFirstStructure, DBStructure aSecondStructure, Logger aLogger) throws Exception {
        if (aFirstStructure != null && aSecondStructure != null && aLogger != null) {
            Map<String, TableStructure> firstMetadata = aFirstStructure.getTablesStructure();
            Map<String, TableStructure> secondMetadata = aSecondStructure.getTablesStructure();
            String firstDialect = aFirstStructure.getDatabaseDialect();
            String secondDialect = aSecondStructure.getDatabaseDialect();
            boolean oneDialect = false;
            if (firstDialect != null && !firstDialect.isEmpty()) {
                oneDialect = firstDialect.equalsIgnoreCase(secondDialect);
            }
            if (firstMetadata != null && secondMetadata != null) {
                aLogger.log(Level.INFO, MetadataUtils.printTitle());
                for (String tableNameUpper : secondMetadata.keySet()) {
                    TableStructure sTblStructure = secondMetadata.get(tableNameUpper);
                    TableStructure dTblStructure = firstMetadata.get(tableNameUpper);
                    aLogger.log(Level.INFO, MetadataUtils.printCompareTable(sTblStructure, dTblStructure, oneDialect));
                }
                for (String tableNameUpper : firstMetadata.keySet()) {
                    TableStructure sTblStructure = secondMetadata.get(tableNameUpper);
                    TableStructure dTblStructure = firstMetadata.get(tableNameUpper);
                    if (sTblStructure == null) {
                        aLogger.log(Level.INFO, MetadataUtils.printCompareTable(sTblStructure, dTblStructure, oneDialect));
                    }
                }
            }
        }
    }
}
