/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.dataflow.FlowProvider;
import com.eas.client.exceptions.UnboundSqlParameterException;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.Query;
import com.eas.script.Scripts;
import java.sql.ParameterMetaData;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.nashorn.api.scripting.JSObject;

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
    protected String datasourceName;
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
     *
     * @param aBasesProxy
     */
    public SqlQuery(DatabasesClient aBasesProxy) {
        super();
        basesProxy = aBasesProxy;
    }

    /**
     * Creates an instance of Query with given SQL query text. Leaves the
     * parameters map empty.
     *
     * @param aBasesProxy
     * @param aSqlText the SQL query text.
     */
    public SqlQuery(DatabasesClient aBasesProxy, String aSqlText) {
        this(aBasesProxy);
        sqlText = aSqlText;
    }

    /**
     * Creates an instance of Query with given SQL query text. Leaves the
     * parameters map empty.
     *
     * @param aBasesProxy
     * @param aDatasourceName A database identifier.
     * @param aSqlText the SQL query text.
     */
    public SqlQuery(DatabasesClient aBasesProxy, String aDatasourceName, String aSqlText) {
        this(aBasesProxy, aSqlText);
        datasourceName = aDatasourceName;
    }

    public SqlQuery(SqlQuery aSource) {
        super(aSource);
        String sourceDatasourceName = aSource.getDatasourceName();
        if (sourceDatasourceName != null) {
            datasourceName = sourceDatasourceName;
        }
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
        basesProxy = aSource.getBasesProxy();
    }

    @Override
    public SqlQuery copy() {
        return new SqlQuery(this);
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    /**
     * @return the datasourceName
     */
    public String getDatasourceName() {
        return datasourceName;
    }

    /**
     * @param aValue A datasourceName to set to the squery.
     */
    public void setDatasourceName(String aValue) {
        String oldValue = datasourceName;
        datasourceName = aValue;
        changeSupport.firePropertyChange("datasourceName", oldValue, datasourceName);
    }

    /**
     * Checks if this query's metadata is accessible from its datasource.
     *
     * @return True if metadata is accessible and false otherwise.
     */
    @Override
    public boolean isMetadataAccessible() {
        try {
            return basesProxy != null && basesProxy.obtainDataSource(datasourceName) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isCommand() {
        return command;
    }

    public void setCommand(boolean aValue) {
        command = aValue;
    }

    /**
     * Clears all roles assigned to this query. Used by two-tier datamodel for
     * security context inheritance. WARNING!!! Don't use it if you have no
     * clear mind about your use case.
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
                        Parameter spmdi = new Parameter(paramName, "", Scripts.STRING_TYPE_NAME);
                        params.add(spmdi);
                    }
                }
            }
        }
    }

    public SqlCompiledQuery compile() throws Exception {
        return compile(null);
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
     * @param aSpace Scripts space for JavaScript to Java conversion. If null, no
     * conversion is performed.
     * @return Compiled Sql query.
     * @throws UnboundSqlParameterException
     * @throws Exception
     */
    public SqlCompiledQuery compile(Scripts.Space aSpace) throws Exception {
        String dialect = basesProxy.getConnectionDialect(datasourceName);
        boolean postgreSQL = ClientConstants.SERVER_PROPERTY_POSTGRE_DIALECT.equals(dialect);
        Parameters compiledParams = new Parameters();
        if (sqlText == null || sqlText.isEmpty()) {
            throw new Exception("Empty sql query strings are not supported");
        }
        StringBuilder compiledSb = new StringBuilder();
        Matcher sm = STRINGS_PATTERN.matcher(sqlText);
        String[] withoutStrings = sqlText.split("('[^']*')");
        for (int i = 0; i < withoutStrings.length; i++) {
            Matcher m = PARAMETER_NAME_PATTERN.matcher(withoutStrings[i]);
            StringBuffer withoutStringsSegment = new StringBuffer();
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
                Parameter copied = p.copy();
                if (aSpace != null) {
                    copied.setValue(aSpace.toJava(copied.getValue()));
                }
                compiledParams.add(copied);
                m.appendReplacement(withoutStringsSegment, postgreSQL && Scripts.DATE_TYPE_NAME.equals(p.getType()) ? "?::timestamp" : "?");
            }
            m.appendTail(withoutStringsSegment);
            withoutStrings[i] = withoutStringsSegment.toString();
            compiledSb.append(withoutStrings[i]);
            if (sm.find()) {
                compiledSb.append(sm.group(0));
            }
        }
        SqlCompiledQuery compiled = new SqlCompiledQuery(basesProxy, datasourceName, compiledSb.toString(), compiledParams, fields);
        compiled.setEntityName(entityName);
        compiled.setProcedure(procedure);
        compiled.setPageSize(pageSize);
        return compiled;
    }

    @Override
    public JSObject execute(Scripts.Space aSpace, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        SqlCompiledQuery compiled = compile();
        Runnable paramsRetriever = () -> {
            for (int i = 1; i <= compiled.getParameters().getParametersCount(); i++) {
                Parameter param = compiled.getParameters().get(i);
                if (param.getMode() == ParameterMetaData.parameterModeOut
                        || param.getMode() == ParameterMetaData.parameterModeInOut) {
                    Parameter innerParam = params.get(param.getName());
                    if (innerParam != null) {
                        innerParam.setValue(param.getValue());
                    }
                }
            }
        };
        JSObject jsData = compiled.executeQuery(onSuccess != null ? (JSObject aData) -> {
            if (compiled.isProcedure()) {
                paramsRetriever.run();
            }
            onSuccess.accept(aData);
        } : null, onFailure, aSpace);
        if (onSuccess == null && compiled.isProcedure()) {
            paramsRetriever.run();
        }
        return jsData;
    }
}
