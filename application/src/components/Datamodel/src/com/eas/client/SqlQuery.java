/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.exceptions.UnboundSqlParameterException;
import com.eas.client.queries.Query;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Sql query with name-bound parameters.
 *
 * <p>
 * This class represents a SQL query which text contains named parameters, and
 * their values with type information. Provides a method compile() to transform
 * it to a SqlCompiledQuery replacing parameters names in the query text to "?"
 * placeholders accepted by JDBC, along with a vector of parameters values in
 * the right order. Method <code>compile()</code> recursively resolves the
 * queries reusing and parameters bindings.</p>
 *
 * @author mg
 */
public class SqlQuery extends Query {

    private final static Pattern PARAMETER_NAME_PATTERN = Pattern.compile(SQLUtils.PARAMETER_NAME_REGEXP, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private final static Pattern STRINGS_PATTERN = Pattern.compile("'[^']*'", Pattern.MULTILINE);
    // For single and multi-table queries
    // Joins, conditions, parametersList, groups, havings etc.
    protected String sqlText;
    // the same as sqlText, but it is used when very custom sql is needed.
    protected String fullSqlText;
    protected Set<String> writable;
    protected int pageSize = FlowProvider.NO_PAGING_PAGE_SIZE;
    protected boolean publicAccess;
    protected boolean command;
    protected DatabasesClient basesProxy;

    /**
     * Creates an instance of Query with empty SQL query text and parameters
     * map.
     */
    public SqlQuery(DatabasesClient aBasesProxy) {
        super();
        basesProxy = aBasesProxy;
    }

    /**
     * Creates an instance of Query with given SQL query text. Leaves the
     * parameters map empty.
     *
     * @param aSqlText the SQL query text.
     */
    public SqlQuery(DatabasesClient aBase, String aSqlText) {
        this(aBase);
        sqlText = aSqlText;
    }

    /**
     * Creates an instance of Query with given SQL query text. Leaves the
     * parameters map empty.
     *
     * @param aDatasourceName A database identifier.
     * @param aSqlText the SQL query text.
     */
    public SqlQuery(DatabasesClient aBase, String aDatasourceName, String aSqlText) {
        this(aBase, aSqlText);
        datasourceName = aDatasourceName;
    }

    public SqlQuery(SqlQuery aSource) {
        super(aSource);
        String asqlText = aSource.getSqlText();
        if (asqlText != null) {
            sqlText = asqlText;
        }
        String aFullSqlText = aSource.getFullSqlText();
        if (aFullSqlText != null) {
            fullSqlText = aFullSqlText;
        }
        if (aSource.getWritable() != null) {
            writable = new HashSet<>();
            writable.addAll(aSource.getWritable());
        }
        publicAccess = aSource.isPublicAccess();
        command = aSource.isCommand();
    }

    @Override
    public SqlQuery copy() {
        return new SqlQuery(this);
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    public boolean isCommand() {
        return command;
    }

    public void setCommand(boolean aValue) {
        command = aValue;
    }

    /**
     * Clears all roles assigned to this query.
     * Used by two-tier datamodel for security context inheritance.
     * WARNING!!! Don't use it if you have no clear mind about your use case.
     */
    public void clearRoles() {
        if (readRoles != null) {
            readRoles.clear();
        }
        if (writeRoles != null) {
            writeRoles.clear();
        }
    }

    /**
     * Returns the SQL query text.
     *
     * @return SQL query text.
     */
    public String getSqlText() {
        return sqlText;
    }

    /**
     * Sets the SQL query text.
     *
     * @param aValue SQL query text.
     */
    public void setSqlText(String aValue) {
        String oldValue = sqlText;
        sqlText = aValue;
        changeSupport.firePropertyChange("sqlText", oldValue, sqlText);
    }

    public String getFullSqlText() {
        return fullSqlText;
    }

    public void setFullSqlText(String aValue) {
        String oldValue = fullSqlText;
        fullSqlText = aValue;
        changeSupport.firePropertyChange("fullSqlText", oldValue, fullSqlText);
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean aValue) {
        boolean oldValue = publicAccess;
        publicAccess = aValue;
        changeSupport.firePropertyChange("publicAccess", oldValue, fullSqlText);
    }

    public Set<String> getWritable() {
        return writable;
    }

    public void setWritable(Set<String> aValue) {
        writable = aValue;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int aPageSize) {
        pageSize = aPageSize;
    }

    /**
     * Builds internal representation of all named parameters based on query
     * text
     */
    protected void extractParameters() {
        if (sqlText != null && !sqlText.isEmpty()) {
            Pattern ptrn = Pattern.compile(SQLUtils.PARAMETER_NAME_REGEXP, Pattern.CASE_INSENSITIVE);
            Matcher mtch = ptrn.matcher(sqlText);
            params = new Parameters();
            Set<String> forUniqueness = new HashSet<>();
            while (mtch.find()) {
                String paramName = sqlText.substring(mtch.start() + 1, mtch.end());
                if (paramName != null) {
                    if (!forUniqueness.contains(paramName)) {
                        forUniqueness.add(paramName);
                        Parameter spmdi = new Parameter(paramName, "", DataTypeInfo.VARCHAR);
                        params.add(spmdi);
                    }
                }
            }
        }
    }

    /**
     * Compiles the SQL query.
     *
     * <p>
     * The compilation process includes replacing named parameters binding like
     * ":param1" in SQL query text with JDBC "?" placeholders and filling the
     * vector of parameters values according to each parameter occurance in the
     * query.</p>
     *
     * <p>
     * The returned object is able to assign parameters values stored in it to
     * any PreparedStatement object.</p>
     *
     * @return compiled SQL query object.
     * @throws UnboundSqlParameterException
     * @throws Exception
     */
    public SqlCompiledQuery compile() throws UnboundSqlParameterException, Exception {
        Parameters ps = new Parameters();
        if (sqlText == null || sqlText.isEmpty()) {
            throw new Exception("Empty sql-query strings are not supported");
        }
        StringBuilder compiledSb = new StringBuilder();
        Matcher sm = STRINGS_PATTERN.matcher(sqlText);
        String[] withoutStrings = sqlText.split("('[^']*')");
        for (int i = 0; i < withoutStrings.length; i++) {
            Matcher m = PARAMETER_NAME_PATTERN.matcher(withoutStrings[i]);
            while (m.find()) {
                String paramName = m.group(1);
                if (params == null) {
                    throw new UnboundSqlParameterException(paramName, sqlText);
                }
                Parameter p = params.get(paramName);
                if (p == null) {
                    // Несвязанные параметры заменяем null-ами.
                    p = new Parameter(paramName);
                    p.setValue(null);
                }
                ps.add(p.copy());
            }
            withoutStrings[i] = m.replaceAll("?");
            compiledSb.append(withoutStrings[i]);
            if (sm.find()) {
                compiledSb.append(sm.group(0));
            }
        }
        SqlCompiledQuery compiled = new SqlCompiledQuery(basesProxy, datasourceName, compiledSb.toString(), ps, fields, readRoles, writeRoles);
        compiled.setEntityId(entityId);
        compiled.setProcedure(procedure);
        compiled.setPageSize(pageSize);
        return compiled;
    }

    /**
     * Compiles the SQL query for achiving metadata.
     *
     * <p>
     * This method achives simple metadata, returned from query execution with
     * sql text metadata translation. </p>
     * <p>
     * The compilation process includes replacing named parameters binding like
     * ":param1" in SQL query text with JDBC "?" placeholders and filling the
     * vector of parameters values according to each parameter occurence in the
     * query.</p>
     *
     * <p>
     * The returned object is able to assign parameters values stored in it to
     * any PreparedStatement object.</p>
     *
     * @return compiled metadata SQL query object.
     * @throws UnboundSqlParameterException
     * @throws Exception
     */
    public SqlCompiledQuery compileMetadataQuery() throws UnboundSqlParameterException, Exception {
        Parameters ps = new Parameters();
        Matcher m = PARAMETER_NAME_PATTERN.matcher(sqlText);
        while (m.find()) {
            String paramName = m.group(1);
            if (params == null) {
                throw new UnboundSqlParameterException(paramName, sqlText);
            }
            Parameter p = params.get(paramName);
            if (p == null) {
                throw new UnboundSqlParameterException(paramName, sqlText);
            }
            Parameter nulledUndefined = p.copy();
            nulledUndefined.setValue(null);
            ps.add(nulledUndefined);
        }
        String sqlCompiledText = m.replaceAll("?");
        sqlCompiledText = RowsetUtils.makeQueryMetadataQuery(sqlCompiledText);
        SqlCompiledQuery compiled = new SqlCompiledQuery(basesProxy, datasourceName, sqlCompiledText, ps);
        compiled.setPageSize(pageSize);
        return compiled;
    }

    @Override
    public Rowset execute(Consumer<Rowset> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return compile().executeQuery(onSuccess, onFailure);
    }

    /*
    @Override
    public void enqueueUpdate() throws Exception {
        compile().enqueueUpdate();
    }
    */
}
