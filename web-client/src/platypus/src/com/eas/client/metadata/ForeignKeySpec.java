package com.eas.client.metadata;

/**
 * A class intended to hold information about foreign key constraint in database.
 */
public class ForeignKeySpec extends PrimaryKeySpec {

    public enum ForeignKeyRule {

        NOACTION,
        SETNULL,
        SETDEFAULT,
        CASCADE;

        public static ForeignKeyRule valueOf(short aValue) {
            if (/*DatabaseMetaData.importedKeyCascade*/0 == aValue) {
                return CASCADE;
            } else if (/*DatabaseMetaData.importedKeyNoAction*/3 == aValue
                    || /*DatabaseMetaData.importedKeyRestrict*/1 == aValue) {
                return NOACTION;
            } else if (/*DatabaseMetaData.importedKeySetDefault*/4 == aValue) {
                return SETDEFAULT;
            } else if (/*DatabaseMetaData.importedKeySetNull*/2 == aValue) {
                return SETNULL;
            } else {
                return null;
            }
        }

        public short toShort() {
            if (this == CASCADE) {
                return 0;//DatabaseMetaData.importedKeyCascade;
            } else if (this == NOACTION) {
                return 3;//DatabaseMetaData.importedKeyNoAction;
            } else if (this == SETDEFAULT) {
                return 4;//DatabaseMetaData.importedKeySetDefault;
            } else // if(this == SETNULL)
            {
                return 2;//DatabaseMetaData.importedKeySetNull;
            }
        }
    }
    protected ForeignKeyRule fkUpdateRule = ForeignKeyRule.NOACTION;
    protected ForeignKeyRule fkDeleteRule = ForeignKeyRule.NOACTION;
    protected boolean fkDeferrable = true;
    protected PrimaryKeySpec referee = null;

    /**
     * Default constructor.
     */
    public ForeignKeySpec() {
        super();
    }

    /**
     * Constructor with all information specified as the parameters.
     * @param aSchema Database schema. Null means application schema in application database.
     * @param aTable Table name. Null and empty string are not allowed.
     * @param aField Field name. Null and empty string are not allowed.
     * @param aFkName Constraint name. Null and empty string are not allowed.
     * @param afkUpdateRule Update rule for foreign key been constructed.
     * @param afkDeleteRule Delete rule for foreign key been constructed.
     * @param afkDeferrable Deferrable rule for foreign key check.
     * @param aPkDbId Connection identifier for referent primary key. Null means application database.
     * @param aPkSchema Database schema for referent primary key. Null means application schema in application database.
     * @param aPkTable Table name of referent primary key. Null and empty string are not allowed.
     * @param aPkField Field name of referent primary key. Null and empty string are not allowed.
     * @param aPkName Referent primary key constraint name. Null and empty string are not allowed.
     */
    public ForeignKeySpec(String aSchema, String aTable, String aField, String aFkName, ForeignKeyRule afkUpdateRule, ForeignKeyRule afkDeleteRule, boolean afkDeferrable, String aPkSchema, String aPkTable, String aPkField, String aPkName) {
        this();
        copyFkFromValues(aSchema, aTable, aField, aFkName, afkUpdateRule, afkDeleteRule, afkDeferrable, aPkSchema, aPkTable, aPkField, aPkName);
    }

    /**
     * Copy contructor.
     * @param aSource <code>ForeignKeySpec</code> instance to be used as information source.
     */
    protected ForeignKeySpec(ForeignKeySpec aSource) {
        this();
        if (aSource != null && aSource.getReferee() != null) {
            PrimaryKeySpec lpk = aSource.getReferee();
            copyFkFromValues(aSource.getSchema(), aSource.getTable(), aSource.getField(), aSource.getCName(), aSource.getFkUpdateRule(), aSource.getFkDeleteRule(), aSource.getFkDeferrable(), lpk.getSchema(), lpk.getTable(), lpk.getField(), lpk.getCName());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.fkUpdateRule != null ? this.fkUpdateRule.hashCode() : 0);
        hash = 59 * hash + (this.fkDeleteRule != null ? this.fkDeleteRule.hashCode() : 0);
        hash = 59 * hash + (this.fkDeferrable ? 1 : 0);
        hash = 59 * hash + (this.referee != null ? this.referee.hashCode() : 0);
        return hash;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ForeignKeySpec other = (ForeignKeySpec) obj;
        if (this.fkUpdateRule != other.fkUpdateRule) {
            return false;
        }
        if (this.fkDeleteRule != other.fkDeleteRule) {
            return false;
        }
        if (this.fkDeferrable != other.fkDeferrable) {
            return false;
        }
        if (this.referee != other.referee && (this.referee == null || !this.referee.equals(other.referee))) {
            return false;
        }
        return true;
    }

    /**
     * Copies this instance to a new instance using copy constructor.
     * @return Copied <code>ForeignKeySpec</code> instance.
     * @see #ForeignKeySpec(ForeignKeySpec aSource)
     */
    @Override
    public PrimaryKeySpec copy() {
        return new ForeignKeySpec(this);
    }

    /**
     * Returns deferrable state of this foreign key.
     * @return Deferrable state of this foreign key.
     */
    public boolean getFkDeferrable() {
        return fkDeferrable;
    }

    /**
     * Sets deferrable state to this foreign key.
     * @param aValue Deferrable state to set.
     */
    public void setFkDeferrable(boolean aValue) {
        fkDeferrable = aValue;
    }

    /**
     * Returns delete rule of this foreign key.
     * @return Delete rule of this foreign key.
     */
    public ForeignKeyRule getFkDeleteRule() {
        return fkDeleteRule;
    }

    /**
     * Sets delete rule to this foreign key.
     * @param aValue Delete rule to set.
     */
    public void setFkDeleteRule(ForeignKeyRule aValue) {
        fkDeleteRule = aValue;
    }

    /**
     * Returns update rule of this foreign key.
     * @return Update rule of this foreign key.
     */
    public ForeignKeyRule getFkUpdateRule() {
        return fkUpdateRule;
    }

    /**
     * Sets update rule to this foreign key.
     * @param aValue Update rule to set.
     */
    public void setFkUpdateRule(ForeignKeyRule aValue) {
        fkUpdateRule = aValue;
    }

    /**
     * Returns the referent primary key.
     * @return Referent primary key.
     */
    public PrimaryKeySpec getReferee() {
        return referee;
    }

    public void setReferee(PrimaryKeySpec aValue) {
        referee = aValue;

    }

    private void copyFkFromValues(String aSchema, String aTable, String aField, String aFkName, ForeignKeyRule afkUpdateRule, ForeignKeyRule afkDeleteRule, boolean afkDeferrable, String aPkSchema, String aPkTable, String aPkField, String aPkName) {
        copyPkFromValues(aSchema, aTable, aField, aFkName);
        setFkUpdateRule(afkUpdateRule);
        setFkDeleteRule(afkDeleteRule);
        setFkDeferrable(afkDeferrable);
        setReferee(new PrimaryKeySpec(aPkSchema, aPkTable, aPkField, aPkName));
    }
}
