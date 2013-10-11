/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.*;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.cache.FreqCache;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.store.XmlDom2QueryDocument;
import com.eas.client.queries.SqlQuery;
import com.eas.script.JsDoc;
import java.io.StringReader;
import java.sql.Types;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jsqlparser.ResultsFinder;
import net.sf.jsqlparser.SourcesFinder;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.w3c.dom.Document;

/**
 *
 * @author pk begining. mg full refactoring inside second version.
 */
public class StoredQueryFactory {

    private Fields processSubQuery(SqlQuery aQuery, SubSelect aSubSelect) throws Exception {
        SqlQuery subQuery = new SqlQuery(aQuery.getClient(), aQuery.getDbId(), "");
        subQuery.setEntityId(aSubSelect.getAliasName());
        resolveOutputFieldsFromTables(subQuery, aSubSelect.getSelectBody());
        Fields subFields = subQuery.getFields();
        return subFields;
    }

    protected class StoredQueryCache extends FreqCache<String, ActualCacheEntry<SqlQuery>> {

        public StoredQueryCache() {
            super();
        }

        @Override
        public ActualCacheEntry<SqlQuery> get(String aId) throws Exception {
            // Ordinary queries will be checked against underlying application element.
            // Dynamic queries will be treated as unactual and will be re-cached.
            // It's not harmful as long as factory maintains dynamic queries cache by itself.
            // It will be simple projection one cache on another.
            ActualCacheEntry<SqlQuery> res = super.get(aId);
            if (!client.getAppCache().isActual(aId, res.getTxtContentSize(), res.getTxtContentCrc32())) {
                remove(aId);
                client.getAppCache().remove(aId);
                res = super.get(aId);
            }
            return res;
        }

        @Override
        protected ActualCacheEntry<SqlQuery> getNewEntry(String aId) throws Exception {
            return loadQuery(aId);
        }
    }
    public static final String INNER_JOIN_CONSTRUCTION = "select %s from %s %s inner join %s on (%s.%s = %s.%s)";
    public static final String ABSENT_QUERY_MSG = "Query %s is not found";
    public static final String CANT_LOAD_NULL_MSG = "Query ID is null.";
    public static final String COLON = ":";
    public static final String CONTENT_EMPTY_MSG = "Content of %s is empty";
    public static final String DUMMY_FIELD_NAME = "dummy";
    public static final String INEER_JOIN_CONSTRUCTING_MSG = "Constructing query with left Query %s and right table %s";
    public static final String LOADING_QUERY_MSG = "Loading stored query %s";
    public static final Pattern SUBQUERY_LINK_PATTERN = Pattern.compile(SQLUtils.SUBQUERY_TABLE_NAME_REGEXP, Pattern.CASE_INSENSITIVE);
    private DbClient client;
    private DbMetadataCache dbMdCache;
    private StoredQueryCache queriesCache;
    private boolean preserveDatasources;

    public void addTableFieldsToSelectResults(SqlQuery aQuery, Table table) throws Exception {
        Fields fields = getTableFields(aQuery.getDbId(), table);
        if (fields != null) {
            for (Field field : fields.toCollection()) {
                Field copied = field.copy();
                /*
                 * if (copied.isPk()) { checkPrimaryKey(aQuery, copied); }
                 */
                /**
                 * Заменим имя оригинальной таблицы на алиас если это возможно,
                 * чтобы в редакторе запросов было хорошо видно откуда взялось
                 * поле.
                 */
                if (preserveDatasources) {
                    boolean aliasPresent = table.getAlias() != null && !table.getAlias().getName().isEmpty();
                    if (aliasPresent) {
                        copied.setTableName(table.getAlias().getName());
                        copied.setSchemaName(null);
                    } else {
                        copied.setTableName(table.getName());
                        copied.setSchemaName(table.getSchemaName());
                    }
                    /**
                     * Заменять имя оригинальной таблицы нельзя, особенно если
                     * это поле ключевое т.к. при установления связи по этим
                     * полям будут проблемы. ORM-у придется "разматывать"
                     * источник поля до таблицы чтобы проверить совместимость
                     * ключей. } else {
                     * copied.setTableName(ClientConstants.QUERY_ID_PREFIX +
                     * aQuery.getEntityId().toString());
                     */
                }
                aQuery.getFields().add(copied);
            }
        }
    }

    public static Map<String, FromItem> prepareUniqueTables(Map<String, FromItem> tables) {
        Map<String, FromItem> uniqueTables = new HashMap<>();
        for (FromItem fromItem : tables.values()) {
            if (fromItem.getAlias() != null && !fromItem.getAlias().getName().isEmpty()) {
                uniqueTables.put(fromItem.getAlias().getName().toLowerCase(), fromItem);
            } else if (fromItem instanceof Table) {
                uniqueTables.put(((Table) fromItem).getWholeTableName().toLowerCase(), fromItem);
            }
        }
        return uniqueTables;
    }

    protected ActualCacheEntry<SqlQuery> loadQuery(String aAppElementId) throws ParserConfigurationException, Exception {
        if (aAppElementId == null) {
            throw new NullPointerException(CANT_LOAD_NULL_MSG);
        }
        Logger.getLogger(this.getClass().getName()).finer(String.format(LOADING_QUERY_MSG, aAppElementId));
        ApplicationElement appElement = client.getAppCache().get(aAppElementId);
        if (appElement == null && SUBQUERY_LINK_PATTERN.matcher(aAppElementId).matches()) {
            aAppElementId = aAppElementId.substring(1);
            appElement = client.getAppCache().get(aAppElementId);
        }
        if (appElement != null) {// Ordinary queries, stored in application database
            Document queryDom = appElement.getContent();
            if (queryDom != null) {
                QueryDocument queryDoc = XmlDom2QueryDocument.transform(client, aAppElementId, queryDom);

                QueryModel model = queryDoc.getModel();
                SqlQuery query = queryDoc.getQuery();
                putRolesMutatables(query);
                List<StoredFieldMetadata> additionalFieldsMetadata = queryDoc.getAdditionalFieldsMetadata();
                String sqlText = query.getSqlText();
                if (sqlText != null && !sqlText.isEmpty()) {
                    if (query.getFullSqlText() != null && !query.getFullSqlText().isEmpty()) {
                        sqlText = query.getFullSqlText();
                    }
                    try {
                        String compiledSqlText = compileSubqueries(sqlText, model);
                        try {
                            putParametersMetadata(query, model);
                            if (putTableFieldsMetadata(query)) {
                                putStoredTableFieldsMetadata(query, additionalFieldsMetadata);
                            }
                        } finally {
                            query.setSqlText(compiledSqlText);
                        }
                    } finally {
                        query.setTitle(appElement.getName());
                        query.getFields().setTableDescription(query.getTitle());
                    }
                }
                return new ActualCacheEntry<>(query, appElement.getTxtContentLength(), appElement.getTxtCrc32());
            } else {
                throw new IllegalArgumentException(String.format(CONTENT_EMPTY_MSG, aAppElementId));
            }
        } else {
            throw new IllegalArgumentException(String.format(ABSENT_QUERY_MSG, aAppElementId));
        }
    }
    /**
     * WARNING! Operating with dynamicQueries map is synchronized in
     * innerJoinQueryTable() and loadQuery() methods. If semantics is to be
     * changed, than you should review the synchronization scheme!!!
     */
    protected Map<String, SqlQuery> dynamicQueries = new HashMap<>();

    public synchronized void innerJoinQueryTable(String newQueryId, String leftQueryId, String rightTableName, String leftFieldName, String rightFieldName) throws Exception {
        if (!dynamicQueries.containsKey(newQueryId)) {
            ActualCacheEntry<SqlQuery> leftSqlQueryEntry = loadQuery(leftQueryId);
            SqlQuery leftSqlQuery = leftSqlQueryEntry != null ? leftSqlQueryEntry.getValue() : null;
            Fields rightTableFields = dbMdCache.getTableMetadata(rightTableName);

            if (leftSqlQuery != null && rightTableFields != null) {
                Logger.getLogger(this.getClass().getName()).finer(String.format(INEER_JOIN_CONSTRUCTING_MSG, leftQueryId, rightTableName));
                SqlQuery result = new SqlQuery(client);
                result.setEntityId(IDGenerator.genID().toString());
                result.setDbId(leftSqlQuery.getDbId());
                String leftQueryName = ClientConstants.QUERY_ID_PREFIX + leftQueryId.toString();
                String leftQueryAlias = ClientConstants.QUERY_ID_PREFIX + "_" + leftQueryId.toString();
                // Text for sql query is generated
                String selectClause = "*";
                if (!rightTableFields.isEmpty()) {
                    selectClause = "";
                    for (int i = 1; i <= rightTableFields.getFieldsCount(); i++) {
                        Field f = rightTableFields.get(i);
                        if (!selectClause.isEmpty()) {
                            selectClause += ", ";
                        }
                        selectClause += rightTableName + "." + f.getName();
                    }
                }
                String sqlText = String.format(INNER_JOIN_CONSTRUCTION, selectClause, leftQueryName, leftQueryAlias, rightTableName, leftQueryAlias, leftFieldName, rightTableName, rightFieldName);
                // model for sql query join and parameters is generated
                QueryModel rootModel = new QueryModel(client);
                rootModel.setDbId(result.getDbId());
                QueryEntity leftQueryEntity = rootModel.newGenericEntity();
                leftQueryEntity.setQueryId(leftQueryId);
                leftQueryEntity.setAlias(leftQueryAlias);

                rootModel.addEntity(leftQueryEntity);
                QueryEntity rightTableEntity = rootModel.newGenericEntity();
                rightTableEntity.setTableName(rightTableName);
                rootModel.addEntity(rightTableEntity);
                Relation<QueryEntity> joinRel = new Relation<>(leftQueryEntity, leftQueryEntity.getFields().get(leftFieldName), rightTableEntity, rightTableEntity.getFields().get(rightFieldName));
                rootModel.addRelation(joinRel);

                Parameters rootParams = rootModel.getParameters();
                Parameters leftQueryParams = leftSqlQuery.getParameters();
                for (int i = 1; i <= leftQueryParams.getParametersCount(); i++) {
                    Parameter rootParam = new Parameter(leftQueryParams.get(i));
                    assert rootParam.getName().equals(leftQueryParams.get(i).getName());
                    rootParams.add(rootParam);
                    Relation<QueryEntity> rel = new Relation<>(rootModel.getParametersEntity(), rootParam, leftQueryEntity, leftQueryEntity.getQuery().getParameters().get(rootParam.getName()));
                    rootModel.addRelation(rel);
                }
                result.setSqlText(compileSubqueries(sqlText, rootModel));
                //result.setMainTable(rightTableName);
                result.setFields(rightTableEntity.getFields());
                putParametersMetadata(result, rootModel);
                result.setEntityId(newQueryId);
                dynamicQueries.put(newQueryId, result);
            }
        }
    }

    /**
     * Returns a copy of cached SqlQuery instance. Takes into account, that
     * underlying application element might be changed since last request. In
     * such case it will be removed from cached and re-built and re-cached.
     *
     * @param aAppElementId Underlying application element identiifer.
     * @return Cached instance of SqlQuery.
     * @throws Exception
     */
    public SqlQuery getQuery(String aAppElementId) throws Exception {
        return getQuery(aAppElementId, true);
    }

    /**
     * Returns cached instance of SqlQuery. Takes into account, that underlying
     * application element might be changed since last request. In such case it
     * will be removed from cached and rebuilded and recached.
     *
     * @param aAppElementId Underlying application element identiifer.
     * @param aCopy True if client code needs a copy of the query, false
     * otherwise. Default is true.
     * @return Cached instance of SqlQuery.
     * @throws Exception
     */
    public SqlQuery getQuery(String aAppElementId, boolean aCopy) throws Exception {
        SqlQuery query = queriesCache.get(aAppElementId).getValue();
        // It's significant to copy the query.
        return query != null ? (aCopy ? query.copy() : query) : null;
        /**
         * Otherwise we will get situation with same query instance across
         * multiple entities or across multiple calls to execute query request
         * handler. Such situations are VERY HARMFUL for parameters behaviour
         * and etc.
         */
    }

    public void clearCache(String aAppElementId) throws Exception {
        queriesCache.remove(aAppElementId);
    }

    public void clearCache() throws Exception {
        queriesCache.clear();
    }

    /**
     * Constructs factory for stored in appliction database queries;
     *
     * @param aClient ClientIntf instance, responsible for interacting with
     * appliction database.
     * @throws java.lang.Exception
     * @see DbClientIntf
     */
    public StoredQueryFactory(DbClient aClient) throws Exception {
        client = aClient;
        dbMdCache = client.getDbMetadataCache(null);
        queriesCache = new StoredQueryCache();
    }

    /**
     * Constructs factory for stored in appliction database queries;
     *
     * @param aClient ClientIntf instance, responsible for interacting with
     * appliction database.
     * @param aPreserveDatasources If true, aliased names of tables
     * (datasources) are setted to resulting fields in query compilation
     * process. If false, query's virtual table name (e.g. q76067e72752) is
     * setted to resulting fields.
     * @throws java.lang.Exception
     * @see ClientIntf
     */
    public StoredQueryFactory(DbClient aClient, boolean aPreserveDatasources) throws Exception {
        this(aClient);
        preserveDatasources = aPreserveDatasources;
    }

    /**
     * Заменяет в запросе ссылки на подзапросы на их реальный контент.
     * Подставляет параметры запроса в соответствии со связями в параметры
     * подзапросов.
     *
     * @return Запрос без ссылок на подзапросы.
     */
    public String compileSubqueries(String sql, QueryModel aModel) throws Exception {
        /**
         * Старая реализация заменяла текст всех подзапросов с одним и тем же
         * идентификатором, не обращая внимания на алиасы. Поэтому запросы
         * содержащие в себе один и тот же подзапрос несколько раз,
         * компилировались неправильно. Неправильно подставлялись и параметры.
         */
        assert aModel != null;
        if (aModel.getEntities() != null) {
            String processedSql = sql;
            for (QueryEntity entity : aModel.getEntities().values()) {
                assert entity != null;
                if (entity.getQueryId() != null) {
                    String queryTablyName = entity.getQueryId();
                    if (queryTablyName.matches("\\d+")) {
                        queryTablyName = ClientConstants.QUERY_ID_PREFIX + queryTablyName;
                    }
                    Pattern subQueryPattern = Pattern.compile(queryTablyName, Pattern.CASE_INSENSITIVE);
                    String tAlias = entity.getAlias();
                    if (tAlias != null && !tAlias.isEmpty()) {
                        subQueryPattern = Pattern.compile(queryTablyName + "[\\s]+" + tAlias, Pattern.CASE_INSENSITIVE);
                        if (tAlias.equalsIgnoreCase(queryTablyName)
                                && !subQueryPattern.matcher(processedSql).find()) {
                            /**
                             * Эта проверка с финтом ушами нужна, т.к. даже в
                             * отсутствии алиаса, он все равно есть и равен
                             * queryTablyName. А так как алиас может в SQL
                             * совпадать с именем таблицы, то эти ситуации никак
                             * не различить, кроме как явной проверкой на
                             * нахождение такого алиаса и имени таблицы
                             * (подзапроса).
                             */
                            subQueryPattern = Pattern.compile(queryTablyName, Pattern.CASE_INSENSITIVE);
                        }
                    }
                    Matcher subQueryMatcher = subQueryPattern.matcher(processedSql);
                    if (subQueryMatcher.find()) {
                        SqlQuery subQuery = queriesCache.get(entity.getQueryId()).getValue();
                        if (subQuery != null && subQuery.getSqlText() != null) {
                            String subQueryText = subQuery.getSqlText();
                            subQueryText = replaceLinkedParameters(subQueryText, entity.getInRelations());

                            String sqlBegin = processedSql.substring(0, subQueryMatcher.start());
                            String sqlToInsert = " (" + subQueryText + ") ";
                            String sqlTail = processedSql.substring(subQueryMatcher.end());
                            if (tAlias != null && !tAlias.isEmpty()) {
                                processedSql = sqlBegin + sqlToInsert + " " + tAlias + " " + sqlTail;
                            } else {
                                processedSql = sqlBegin + sqlToInsert + " " + queryTablyName + " " + sqlTail;
                            }
                        }
                    }
                }
            }
            return processedSql;
        }
        return sql;
    }

    private void putParametersMetadata(SqlQuery aQuery, QueryModel aModel) {
        for (int i = 1; i <= aModel.getParameters().getParametersCount(); i++) {
            Parameter p = aModel.getParameters().get(i);
            if (p.getTypeInfo().getSqlTypeName() == null || p.getTypeInfo().getSqlTypeName().isEmpty()) {
                DataTypeInfo dt = DataTypeInfo.valueOf(p.getTypeInfo().getSqlType());
                if (dt != null) {
                    p.getTypeInfo().setJavaClassName(dt.getJavaClassName());
                    p.getTypeInfo().setSqlTypeName(dt.getSqlTypeName());
                }
            }
            aQuery.getParameters().add(p);
        }
    }

    private void putStoredTableFieldsMetadata(SqlQuery aQuery, List<StoredFieldMetadata> storedMetadata) {
        Fields fields = aQuery.getFields();
        if (fields != null) {
            for (StoredFieldMetadata addition : storedMetadata) {
                Field queryField = fields.get(addition.getBindedColumn());
                if (queryField != null) {
                    if (addition.description != null && !addition.description.isEmpty()) {
                        queryField.setDescription(addition.description);
                    }
                    if (addition.getTypeInfo() != null && !addition.getTypeInfo().equals(queryField.getTypeInfo())) {
                        queryField.setTypeInfo(addition.getTypeInfo());
                    }
                }
            }
        }
    }

    public static void putRolesMutatables(SqlQuery aQuery) throws Exception {
        // Let's extract all comments
        Set<String> comments = new HashSet<>();
        CCJSqlParserTokenManager tokenManager = new CCJSqlParserTokenManager(new SimpleCharStream(new StringReader(aQuery.getSqlText())));
        Token token = tokenManager.getNextToken();
        while (token != null && token.kind != CCJSqlParserConstants.EOF) {
            if (token.specialToken != null) {
                comments.add(token.specialToken.toString());
            }
            token = tokenManager.getNextToken();
        }
        if (token != null && token.specialToken != null) {
            comments.add(token.specialToken.toString());
        }
        boolean readonly = false;
        for (String comment : comments) {
            JsDoc jsDoc = new JsDoc(comment);
            jsDoc.parseAnnotations();
            for (JsDoc.Tag tag : jsDoc.getAnnotations()) {
                switch (tag.getName()) {
                    case JsDoc.Tag.ROLES_ALLOWED_TAG:
                        aQuery.getReadRoles().addAll(tag.getParams());
                        aQuery.getWriteRoles().addAll(tag.getParams());
                        break;
                    case JsDoc.Tag.ROLES_ALLOWED_READ_TAG:
                        aQuery.getReadRoles().addAll(tag.getParams());
                        break;
                    case JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG:
                        aQuery.getWriteRoles().addAll(tag.getParams());
                        break;
                    case JsDoc.Tag.READONLY_TAG:
                        readonly = true;
                        break;
                    case JsDoc.Tag.WRITABLE_TAG:
                        Set<String> writables = new HashSet<>();
                        if (tag.getParams() != null) {
                            for (String writable : tag.getParams()) {
                                if (writable != null) {
                                    writables.add(writable.toLowerCase());
                                }
                            }
                        }
                        aQuery.setWritable(writables);
                        break;
                    case JsDoc.Tag.MANUAL_TAG:
                        aQuery.setManual(true);
                        break;
                    case JsDoc.Tag.PROCEDURE_TAG:
                        aQuery.setProcedure(true);
                        break;
                    case JsDoc.Tag.PUBLIC_TAG:
                        aQuery.setPublicAccess(true);
                        break;
                }
            }
        }
        if (readonly) {
            aQuery.setWritable(Collections.<String>emptySet());
        }
    }

    /**
     * @param aQuery
     * @return True if query is select query.
     * @throws Exception
     */
    public boolean putTableFieldsMetadata(SqlQuery aQuery) throws Exception {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement parsedQuery = parserManager.parse(new StringReader(aQuery.getSqlText()));
        if (parsedQuery instanceof Select) {
            Select select = (Select) parsedQuery;
            resolveOutputFieldsFromTables(aQuery, select.getSelectBody());
            return true;
        }
        return false;
    }

    private String replaceLinkedParameters(String aSqlText, Set<Relation<QueryEntity>> parametersRelations) {
        for (Relation<QueryEntity> rel : parametersRelations) {
            if (rel.getLeftEntity() instanceof QueryParametersEntity && rel.getLeftField() != null && rel.getRightParameter() != null) {
                aSqlText = Pattern.compile(COLON + rel.getRightParameter().getName() + "\\b", Pattern.CASE_INSENSITIVE).matcher(aSqlText).replaceAll(COLON + rel.getLeftField().getName());
            }
        }
        return aSqlText;
    }

    private void resolveOutputFieldsFromTables(SqlQuery aQuery, SelectBody aSelectBody) throws Exception {
        Map<String, FromItem> sources = SourcesFinder.getSourcesMap(SourcesFinder.TO_CASE.LOWER, aSelectBody);
        for (SelectItem sItem : ResultsFinder.getResults(aSelectBody)) {
            if (sItem instanceof AllColumns) {// *
                Map<String, FromItem> uniqueTables = prepareUniqueTables(sources);
                for (FromItem source : uniqueTables.values()) {
                    if (source instanceof Table) {
                        addTableFieldsToSelectResults(aQuery, (Table) source);
                    } else if (source instanceof SubSelect) {
                        Fields subFields = processSubQuery(aQuery, (SubSelect) source);
                        Fields destFields = aQuery.getFields();
                        for (Field field : subFields.toCollection()) {
                            destFields.add(field);
                        }
                    }
                }
            } else if (sItem instanceof AllTableColumns) {// t.*
                AllTableColumns cols = (AllTableColumns) sItem;
                assert cols.getTable() != null : "<table>.* syntax must lead to .getTable() != null";
                FromItem source = sources.get(cols.getTable().getWholeTableName().toLowerCase());
                if (source instanceof Table) {
                    addTableFieldsToSelectResults(aQuery, (Table) source);
                } else if (source instanceof SubSelect) {
                    Fields subFields = processSubQuery(aQuery, (SubSelect) source);
                    Fields destFields = aQuery.getFields();
                    for (Field field : subFields.toCollection()) {
                        destFields.add(field);
                    }
                }
            } else {
                assert sItem instanceof SelectExpressionItem;
                SelectExpressionItem col = (SelectExpressionItem) sItem;
                Field field = null;
                /* Если пользоваться этим приемом, то будет введение разработчика в заблуждение
                 * т.к. в дизайнере и автозавершении кода поле результата будет поименовано
                 * так же как и поле-агрумент функции, а из скрипта оно будет недоступно.
                 if (col.getExpression() instanceof Function) {
                 Function func = (Function) col.getExpression();
                 if (func.getParameters() != null && func.getParameters().getExpressions() != null
                 && func.getParameters().getExpressions().size() == 1) {
                 Expression firstArg = (Expression) func.getParameters().getExpressions().get(0);
                 if (firstArg instanceof Column) {
                 field = resolveFieldByColumn(aQuery, (Column) firstArg, col, tables);
                 }
                 }
                 } else */
                if (col.getExpression() instanceof Column) {
                    field = resolveFieldByColumn(aQuery, (Column) col.getExpression(), col, sources);
                } else // free expression like a ...,'text' as txt,...
                {
                    field = null;
                    /*
                     * // Absent alias generation is parser's work. field = new
                     * Field(col.getAlias()); // Such field is absent in
                     * database tables and so, field's table is the processed
                     * query. field.setTableName(ClientConstants.QUERY_ID_PREFIX
                     * + aQuery.getEntityId().toString()); /** There is an
                     * unsolved problem about type of the expression. This might
                     * be solved using manually setted up field's type and
                     * description information during
                     * "putStoredTableFieldsMetadata(...)" call.
                     */
                    //field.getTypeInfo().setSqlType(Types.OTHER);
                }
                if (field == null) {
                    // Absent alias generation is parser's work.
                    //Безымянные поля, получающиеся когда нет алиаса, будут
                    //замещены полями полученными из базы во время исполнения запроса.

                    field = new Field(col.getAlias() != null ? col.getAlias().getName()
                            : (col.getExpression() instanceof Column ? ((Column) col.getExpression()).getColumnName() : ""));

                    // Such field is absent in database tables and so, field's table is the processed query.
                    if (Character.isDigit(aQuery.getEntityId().charAt(0))) {
                        field.setTableName(ClientConstants.QUERY_ID_PREFIX + aQuery.getEntityId());
                    } else {
                        field.setTableName(aQuery.getEntityId());
                    }
                    /**
                     * There is an unsolved problem about type of the
                     * expression. This might be solved using manually setted up
                     * field's type and description information during
                     * "putStoredTableFieldsMetadata(...)" call.
                     */
                    field.getTypeInfo().setSqlType(Types.OTHER);
                }
                aQuery.getFields().add(field);
            }
        }
    }

    /**
     * Returns cached table fields if
     * <code>aTablyName</code> is a table name or query fields if
     * <code>aTablyName</code> is query tably name in format: q<id>.
     *
     * @param aDbId Databse identifier, the query belongs to. That database is
     * query-inner table metadata source, but query is stored in application
     * databse.
     * @param aTablyName Table or query tably name.
     * @return Fields instance.
     * @throws Exception
     */
    protected Fields getTablyFields(String aDbId, String aTablyName) throws Exception {
        ApplicationElement testAppElement = client.getAppCache().get(aTablyName);
        if (testAppElement == null && SUBQUERY_LINK_PATTERN.matcher(aTablyName).matches()) {
            aTablyName = aTablyName.substring(1);
            testAppElement = client.getAppCache().get(aTablyName);
        }
        if (testAppElement != null && testAppElement.getType() == ClientConstants.ET_QUERY) {
            SqlQuery query = queriesCache.get(aTablyName).getValue();
            return query.getFields();
        } else {
            //                                                     
            return client.getDbMetadataCache(aDbId).getTableMetadata(aTablyName);
        }
    }

    private Fields getTableFields(String aDbId, Table aTable) throws Exception {
        return getTablyFields(aDbId, aTable.getWholeTableName());
    }

    private Field resolveFieldByColumn(SqlQuery aQuery, Column column, SelectExpressionItem selectItem, Map<String, FromItem> aSources) throws Exception {
        FromItem source = null;// Это таблица парсера - источник данных в составе запроса.
        Field field = null;
        if (column.getTable() != null && column.getTable().getWholeTableName() != null) {
            source = aSources.get(column.getTable().getWholeTableName().toLowerCase());
            if (source != null) {
                if (source instanceof Table) {
                    /**
                     * Таблица поля, предоставляемая парсером никак не связана с
                     * таблицей из списка from. Поэтому мы должны связать их
                     * самостоятельно. Такая вот особенность парсера.
                     */
                    Fields tableFields = getTableFields(aQuery.getDbId(), (Table) source);
                    field = tableFields.get(column.getColumnName());
                } else if (source instanceof SubSelect) {
                    Fields subFields = processSubQuery(aQuery, (SubSelect) source);
                    field = subFields.get(column.getColumnName());
                }
            }
        }
        if (field == null) {
            /**
             * Часто бывает, что алиас/имя таблицы из которой берется поле не
             * указаны. Поэтому парсер не предоставляет таблицу. В этом случае
             * как и в первой версии поищем первую таблицу, содержащую поле с
             * таким именем.
             */
            for (FromItem anySource : aSources.values()) {
                Fields fields = null;
                if (anySource instanceof Table) {
                    fields = getTableFields(aQuery.getDbId(), (Table) anySource);
                } else if (anySource instanceof SubSelect) {
                    fields = processSubQuery(aQuery, (SubSelect) anySource);
                }
                if (fields != null) {
                    field = fields.get(column.getColumnName());
                    if (field != null) {
                        source = anySource;
                        break;
                    }
                }
            }
        }
        if (field != null) {
            /**
             * Скопируем поле, чтобы избежать пересечения информации о полях
             * таблицы из-за её участия в разных запросах.
             */
            Field copied = field.copy();
            /**
             * Заменим отметку о первичном ключе из оригинальной таблицы на
             * отметку о внешнем ключе, указывающем на ту же таблицу. Замена
             * производится с учетом "главной" таблицы. Теперь эта обработка не
             * нужна, т.к. все таблицы "главные", т.е. изменения могут попасть в
             * несколько таблиц одновременно, с учетом их ключей, конечно,.
             */
            //checkPrimaryKey(aQuery, copied);
            /**
             * Заменим имя поля из оригинальной таблицы на алиас. Если его нет,
             * то его надо сгенерировать. Генерация алиаса, - это работа
             * парсера. По возможности, парсер должен генерировать алиасы
             * совпадающие с именем поля.
             */
            copied.setName(selectItem.getAlias() != null ? selectItem.getAlias().getName() : column.getColumnName());
            copied.setOriginalName(column.getColumnName() != null ? column.getColumnName() : copied.getName());
            /**
             * Заменим имя оригинальной таблицы на алиас если это возможно,
             * чтобы в редакторе запросов было хорошо видно откуда взялось поле.
             */
            if (preserveDatasources) {
                boolean aliasPresent = source.getAlias() != null && !source.getAlias().getName().isEmpty();
                if (aliasPresent) {
                    copied.setTableName(source.getAlias().getName());
                    copied.setSchemaName(null);
                } else if (source instanceof Table) {
                    copied.setTableName(((Table) source).getName());
                    copied.setSchemaName(((Table) source).getSchemaName());
                }
                /**
                 * Заменять имя оригинальной таблицы нельзя, особенно если это
                 * поле ключевое т.к. при установления связи по этим полям будут
                 * проблемы. Дизайнеру придется "разматывать" источник поля
                 * сквозь все запросы до таблицы чтобы проверить совместимость
                 * ключей. } else {
                 * copied.setTableName(ClientConstants.QUERY_ID_PREFIX +
                 * aQuery.getEntityId().toString());
                 */
            }
            return copied;
        } else {
            return null;
        }
    }
}
