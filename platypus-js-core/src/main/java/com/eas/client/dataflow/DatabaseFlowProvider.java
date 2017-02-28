/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.Parameters;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This flow provider intended to support the flow process from and to jdbc data sources.
 * @author mg
 * @param <JKT> Jdbc source key type. It may be long number or string identifier.
 * @see FlowProvider
 */
public abstract class DatabaseFlowProvider<JKT> implements FlowProvider {

    protected static final String CONVERTER_MISSING_MSG = "Refreshing of a rowset without converter is impossible";
    protected static final String STATEMENT_MISSING_MSG = "Refreshing of a rowset without statement is impossible";
    protected static final String BAD_PARAMETERS_MSG = "Bad parameters. Parameter count from an sql clause and in parameters collection must be th same!";
    protected static final String BAD_REFRESH_NEXTPAGE_CHAIN_MSG = "The call of refresh() method is allowed only for non paged flow providers or as the first call in the refresh() -> nextPage() -> nextPage() -> ... calls chain";
    protected static final String BAD_NEXTPAGE_REFRESH_CHAIN_MSG = "The call of nextPage() method is allowed only for paged flow providers as the subsequent calls in the refresh() -> nextPage() -> nextPage() -> ... calls chain";
    protected JKT jdbcSourceTag;
    protected String clause;
    protected int pageSize = NO_PAGING_PAGE_SIZE;

    /**
     * A flow provider, intended to support jdbc data sources.
     * @param aClause A sql clause, provider should use to achieve PreparedStatement instance to use it in the
     * result set querying process.
     * @param aJdbcSourceTag Jdbc source key value. It may be long number or string identifier.
     */
    public DatabaseFlowProvider(JKT aJdbcSourceTag, String aClause) {
        super();
        jdbcSourceTag = aJdbcSourceTag;
        clause = aClause;
        assert clause != null : "Flow provider cant't exist without a selecting sql clause";
    }

    public JKT getJdbcSourceTag() {
        return jdbcSourceTag;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int aPageSize) {
        pageSize = aPageSize;
    }

    protected boolean isPaged() {
        return pageSize > 0;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String aValue) {
        clause = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Collection<Map<String, Object>> nextPage(Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Collection<Map<String, Object>> refresh(Parameters aParams, Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception;

}
