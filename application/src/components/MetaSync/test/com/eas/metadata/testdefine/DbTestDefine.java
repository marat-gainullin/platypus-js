/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.testdefine;

import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import java.util.Map;

/**
 *
 * @author vy
 */
public abstract class DbTestDefine {

    private Object fieldsSizes;

    public enum Database {

//---            assertEquals(field.getPrecision(),aPrecision);
//---            assertEquals(field.isReadonly(),aReadonly);
//---            assertEquals(field.isSigned(),aSigned);
//???????????????????????????? bitmap ??????
//    private static final boolean checkHashed = false;
        ORACLE(1, false, false, false),
        POSTGRESQL(2, false, false, false),
        MYSQL(3, false, false, false),
        DB2(4, false, false, false),
        H2(5, false, false, false),
        MSSQL(6, false, false, false);
        private int code;
        private boolean checkIndexClustered;
        private boolean checkIndexHashed;
        private boolean checkIndexAscending;

        private Database(int aDatabaseIndex, boolean aIndexClustered, boolean aIndexHashed, boolean aIndexAscending) {
            code = aDatabaseIndex;
            checkIndexClustered = aIndexClustered;
            checkIndexHashed = aIndexHashed;
        }

        public int getCode() {
            return code;
        }

        /**
         * @return the checkIndexClustered
         */
        public boolean isCheckIndexClustered() {
            return checkIndexClustered;
        }

        /**
         * @return the checkIndexHashed
         */
        public boolean isCheckIndexHashed() {
            return checkIndexHashed;
        }

        /**
         * @return the checkIndexAscending
         */
        public boolean isCheckIndexAscending() {
            return checkIndexAscending;
        }
    }
    static public final int ORIGINALVALUE = 0;

    abstract public String[][] getFieldsTypes();

    abstract public Map<String, int[]> getFieldsSizes();

    abstract public Map<String, int[]> getFieldsScales();

    abstract public ForeignKeyRule[][] getFKeyDeleteRules();

    abstract public ForeignKeyRule[][] getFKeyUpdateRules();

    abstract public boolean[][] getFKeyDeferrables();

    abstract public boolean enabledSetNull(String aFieldName);

    public String[] getDBTypes(int aDatabase) {
        String[][] fieldsTypes = getFieldsTypes();
        assert fieldsTypes != null;
        String[] arrTypes = new String[fieldsTypes.length];
        for (int i = 0; i < fieldsTypes.length; i++) {
            String[] types = fieldsTypes[i];
            assert types != null;
            assert types.length > aDatabase;
            arrTypes[i] = types[aDatabase];
        }
        return arrTypes;
    }

    public int getDBSize(String aOriginalDataBaseType, int aDatabase) {
        Map<String, int[]> fldSizes = getFieldsSizes();
        assert fldSizes != null;
        if (fldSizes.containsKey(aOriginalDataBaseType)) {
            int[] sizes = fldSizes.get(aOriginalDataBaseType);
            assert sizes != null;
            assert sizes.length > aDatabase;
            return sizes[aDatabase];
        }
        return -1;
    }

    public int getDBScale(String aOriginalDataBaseType, int aDatabase) {
        Map<String, int[]> fieldsScales = getFieldsScales();
        assert fieldsScales != null;
        if (fieldsScales.containsKey(aOriginalDataBaseType)) {
            int[] scales = fieldsScales.get(aOriginalDataBaseType);
            assert scales != null;
            assert scales.length > aDatabase;
            return scales[aDatabase];
        }
        return -1;
    }

    public String[] getOriginalTypes() {
        return getDBTypes(ORIGINALVALUE);
    }

    public int getOriginalSize(String aOriginalDataBaseType) {
        return getDBSize(aOriginalDataBaseType, ORIGINALVALUE);
    }

    public int getOriginalScale(String aOriginalDataBaseType) {
        return getDBScale(aOriginalDataBaseType, ORIGINALVALUE);
    }

    public String getDBType(String aOriginalDataBaseType, int aDatabase) {
        String[][] fieldsTypes = getFieldsTypes();
        assert fieldsTypes != null;
        String[] arrTypes = new String[fieldsTypes.length];
        for (int i = 0; i < fieldsTypes.length; i++) {
            String[] types = fieldsTypes[i];
            assert types != null;
            assert types.length > aDatabase;
            if (types[ORIGINALVALUE].equals(aOriginalDataBaseType)) {
                return types[aDatabase];
            }
        }
        return null;
    }

    public ForeignKeyRule getFKeyDeleteRule(ForeignKeyRule aOriginalRule, int aDatabase) {
        ForeignKeyRule[][] arrRules = getFKeyDeleteRules();
        assert arrRules != null;
        for (ForeignKeyRule[] rules : arrRules) {
            assert rules != null;
            assert rules.length > aDatabase;
            if (rules[ORIGINALVALUE].equals(aOriginalRule)) {
                return rules[aDatabase];
            }
        }
        return null;
    }

    public ForeignKeyRule getFKeyUpdateRule(ForeignKeyRule aOriginalRule, int aDatabase) {
        ForeignKeyRule[][] arrRules = getFKeyUpdateRules();
        assert arrRules != null;
        for (ForeignKeyRule[] rules : arrRules) {
            assert rules != null;
            assert rules.length > aDatabase;
            if (rules[ORIGINALVALUE].equals(aOriginalRule)) {
                return rules[aDatabase];
            }
        }
        return null;
    }

    public Boolean getFKeyDeferrable(boolean aOriginalValue, int aDatabase) {
        boolean[][] arrDeferrables = getFKeyDeferrables();
        assert arrDeferrables != null;
        for (boolean[] deferrables : arrDeferrables) {
            assert deferrables != null;
            assert deferrables.length > aDatabase;
            if (deferrables[ORIGINALVALUE] == (aOriginalValue)) {
                return deferrables[aDatabase];
            }
        }
        return null;
    }
}
