/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.QueryDocument;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.Relation;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.JsDoc;
import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.ResultsFinder;
import net.sf.jsqlparser.SourcesFinder;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

/**
 *
 * @author mg.
 */
public class StoredQueryFactory {

    public static final String _Q = "\\" + ClientConstants.STORED_QUERY_REF_PREFIX + "?";

    private Fields processSubQuery(SqlQuery aQuery, SubSelect aSubSelect) throws Exception {
        SqlQuery subQuery = new SqlQuery(aQuery.getBasesProxy(), aQuery.getDatasourceName(), "");
        subQuery.setEntityName(aSubSelect.getAliasName());
        resolveOutputFieldsFromTables(subQuery, aSubSelect.getSelectBody());
        Fields subFields = subQuery.getFields();
        return subFields;
    }

    public static final String INNER_JOIN_CONSTRUCTION = "select %s from %s %s inner join %s on (%s.%s = %s.%s)";
    public static final String ABSENT_QUERY_MSG = "Query %s is not found";
    public static final String CANT_LOAD_NULL_MSG = "Query name is null.";
    public static final String COLON = ":";
    public static final String CONTENT_EMPTY_MSG = "Content of %s is empty";
    public static final String DUMMY_FIELD_NAME = "dummy";
    public static final String INEER_JOIN_CONSTRUCTING_MSG = "Constructing query with left Query %s and right table %s";
    public static final String LOADING_QUERY_MSG = "Loading stored query %s";
    protected QueriesProxy<SqlQuery> subQueriesProxy;
    private final DatabasesClient basesProxy;
    private final PlatypusIndexer indexer;
    private boolean aliasesToTableNames;

    public boolean isAliasesToTableNames() {
        return aliasesToTableNames;
    }

    public void setAliasesToTableNames(boolean aValue) {
        aliasesToTableNames = aValue;
    }

    protected void addTableFieldsToSelectResults(SqlQuery aQuery, Table aFieldsSource) throws Exception {
        FieldsResult fieldsRes = getTablyFields(aQuery.getDatasourceName(), aFieldsSource.getWholeTableName());
        if (fieldsRes != null && fieldsRes.fields != null) {
            MetadataCache mdCache = basesProxy.getMetadataCache(aQuery.getDatasourceName());
            if (mdCache != null) {
                TypesResolver resolver = mdCache.getDatasourceSqlDriver().getTypesResolver();
                fieldsRes.fields.toCollection().stream().forEach((Field field) -> {
                    Field copied = new Field();
                    copied.assignFrom(field);
                    if (fieldsRes.fromRealTable) {
                        JdbcField jField = (JdbcField) field;
                        copied.setType(resolver.toApplicationType(jField.getJdbcType(), jField.getType()));
                        if (jField.getSchemaName() != null && !jField.getSchemaName().isEmpty()) {
                            copied.setTableName(jField.getSchemaName() + "." + copied.getTableName());
                        }
                    }
                    /**
                     * Заменять имя оригинальной таблицы нельзя, особенно если
                     * это поле ключевое т.к. при установлении связи по этим
                     * полям будут проблемы. ORM-у придется "разматывать"
                     * источник поля до таблицы чтобы восстановиит связи по
                     * ключам. Здесь это делается исключительно ради очень
                     * специального использования фабрики в дизайнере запросов.
                     */
                    if (aliasesToTableNames
                            && aFieldsSource.getAlias() != null && !aFieldsSource.getAlias().getName().isEmpty()) {
                        copied.setTableName(aFieldsSource.getAlias().getName());
                    }
                    aQuery.getFields().add(copied);
                });
            }
        }
    }

    public static Map<String, FromItem> prepareUniqueTables(Map<String, FromItem> tables) {
        Map<String, FromItem> uniqueTables = new HashMap<>();
        tables.values().stream().forEach((fromItem) -> {
            if (fromItem.getAlias() != null && !fromItem.getAlias().getName().isEmpty()) {
                uniqueTables.put(fromItem.getAlias().getName().toLowerCase(), fromItem);
            } else if (fromItem instanceof Table) {
                uniqueTables.put(((Table) fromItem).getWholeTableName().toLowerCase(), fromItem);
            }
        });
        return uniqueTables;
    }

    public SqlQuery loadQuery(String aAppElementName) throws Exception {
        if (aAppElementName == null) {
            throw new NullPointerException(CANT_LOAD_NULL_MSG);
        }
        Logger.getLogger(this.getClass().getName()).finer(String.format(LOADING_QUERY_MSG, aAppElementName));
        File mainQueryFile = indexer.nameToFile(aAppElementName);
        return mainQueryFile != null ? fileToSqlQuery(aAppElementName, mainQueryFile) : null;
    }

    protected SqlQuery fileToSqlQuery(String aName, File aFile) throws Exception {
        QueryDocument queryDoc = QueryDocument.parse(aName, aFile, basesProxy, subQueriesProxy);
        QueryModel model = queryDoc.getModel();
        SqlQuery query = queryDoc.getQuery();
        putRolesMutatables(query);
        List<StoredFieldMetadata> additionalFieldsMetadata = queryDoc.getAdditionalFieldsMetadata();
        String sqlText = query.getSqlText();
        if (sqlText != null && !sqlText.isEmpty()) {
            if (query.getFullSqlText() != null && !query.getFullSqlText().isEmpty() && !query.getFullSqlText().replaceAll("\\s", "").isEmpty()) {
                sqlText = query.getFullSqlText();
            }
            try {
                String compiledSqlText = compileSubqueries(sqlText, model);
                try {
                    putParametersMetadata(query, model);
                    if (putTableFieldsMetadata(query)) {
                        putStoredTableFieldsMetadata(query, additionalFieldsMetadata);
                    } else {
                        query.setCommand(true);
                    }
                } finally {
                    query.setSqlText(compiledSqlText);
                }
            } finally {
                query.setTitle(aName);
                query.getFields().setTableDescription(query.getTitle());
            }
        }
        return query;
    }

    /**
     * Constructs factory for stored in appliction database queries;
     *
     * @param aBasesProxy ClientIntf instance, responsible for interacting with
     * appliction database.
     * @param aSubQueriesProxy
     * @param aIndexer
     * @throws java.lang.Exception
     */
    public StoredQueryFactory(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aSubQueriesProxy, PlatypusIndexer aIndexer) throws Exception {
        super();
        basesProxy = aBasesProxy;
        subQueriesProxy = aSubQueriesProxy;
        indexer = aIndexer;
    }

    /**
     * Заменяет в запросе ссылки на подзапросы на их содержимое. Подставляет
     * параметры запроса в соответствии со связями в параметры подзапросов.
     *
     * @param aSqlText
     * @param aModel
     * @return Запрос без ссылок на подзапросы.
     * @throws java.lang.Exception
     */
    public String compileSubqueries(String aSqlText, QueryModel aModel) throws Exception {
        /**
         * Старая реализация заменяла текст всех подзапросов с одним и тем же
         * идентификатором, не обращая внимания на алиасы. Поэтому запросы
         * содержащие в себе один и тот же подзапрос несколько раз,
         * компилировались неправильно. Неправильно подставлялись и параметры.
         */
        assert aModel != null;
        if (aModel.getEntities() != null) {
            String processedSql = aSqlText;
            for (QueryEntity entity : aModel.getEntities().values()) {
                assert entity != null;
                if (entity.getQueryName() != null) {
                    String queryTablyName = entity.getQueryName();
                    Pattern subQueryPattern = Pattern.compile(_Q + queryTablyName, Pattern.CASE_INSENSITIVE);
                    String tAlias = entity.getAlias();
                    if (tAlias != null && !tAlias.isEmpty()) {
                        subQueryPattern = Pattern.compile(_Q + queryTablyName + "[\\s]+" + tAlias, Pattern.CASE_INSENSITIVE);
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
                            subQueryPattern = Pattern.compile(_Q + queryTablyName, Pattern.CASE_INSENSITIVE);
                        }
                    }
                    Matcher subQueryMatcher = subQueryPattern.matcher(processedSql);
                    if (subQueryMatcher.find()) {
                        SqlQuery subQuery = subQueriesProxy.getQuery(entity.getQueryName(), null, null, null);
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
        return aSqlText;
    }

    private String replaceLinkedParameters(String aSqlText, Set<Relation<QueryEntity>> parametersRelations) {
        for (Relation<QueryEntity> rel : parametersRelations) {
            if (rel.getLeftEntity() instanceof QueryParametersEntity && rel.getLeftField() != null && rel.getRightParameter() != null) {
                aSqlText = Pattern.compile(COLON + rel.getRightParameter().getName() + "\\b", Pattern.CASE_INSENSITIVE).matcher(aSqlText).replaceAll(COLON + rel.getLeftField().getName());
            }
        }
        return aSqlText;
    }

    private void putParametersMetadata(SqlQuery aQuery, QueryModel aModel) {
        for (int i = 1; i <= aModel.getParameters().getParametersCount(); i++) {
            Parameter p = aModel.getParameters().get(i);
            aQuery.getParameters().add(p);
        }
    }

    private void putStoredTableFieldsMetadata(SqlQuery aQuery, List<StoredFieldMetadata> storedMetadata) {
        Fields fields = aQuery.getFields();
        if (fields != null) {
            storedMetadata.stream().forEach((addition) -> {
                Field queryField = fields.get(addition.getBindedColumn());
                if (queryField != null) {
                    if (addition.description != null && !addition.description.isEmpty()) {
                        queryField.setDescription(addition.description);
                    }
                    if (addition.getType() != null && !addition.getType().equals(queryField.getType())) {
                        queryField.setType(addition.getType());
                    }
                }
            });
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
                switch (tag.getName().toLowerCase()) {
                    case JsDoc.Tag.ROLES_ALLOWED_TAG:
                        aQuery.getReadRoles().addAll(tag.getParams());
                        if (aQuery.getWriteRoles().isEmpty()) {
                            aQuery.getWriteRoles().addAll(tag.getParams());
                        }
                        break;
                    case JsDoc.Tag.ROLES_ALLOWED_READ_TAG:
                        aQuery.getReadRoles().addAll(tag.getParams());
                        break;
                    case JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG:
                        if (!aQuery.getWriteRoles().isEmpty()) {
                            aQuery.getWriteRoles().clear();
                        }
                        aQuery.getWriteRoles().addAll(tag.getParams());
                        break;
                    case JsDoc.Tag.READONLY_TAG:
                        readonly = true;
                        break;
                    case JsDoc.Tag.WRITABLE_TAG:
                        Set<String> writables = new HashSet<>();
                        if (tag.getParams() != null) {
                            tag.getParams().stream().forEach((writable) -> {
                                if (writable != null) {
                                    writables.add(writable.toLowerCase());
                                }
                            });
                        }
                        aQuery.setWritable(writables);
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
        try {
            Statement parsedQuery = parserManager.parse(new StringReader(aQuery.getSqlText()));
            if (parsedQuery instanceof Select) {
                Select select = (Select) parsedQuery;
                resolveOutputFieldsFromTables(aQuery, select.getSelectBody());
                return true;
            }
        } catch (JSQLParserException ex) {
            if (aQuery.isProcedure()) {
                Logger.getLogger(StoredQueryFactory.class.getName()).log(Level.WARNING, ex.getMessage());
            } else {
                throw ex;
            }
        }
        return false;
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
                        subFields.toCollection().stream().forEach((field) -> {
                            destFields.add(field);
                        });
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
                    subFields.toCollection().stream().forEach((field) -> {
                        destFields.add(field);
                    });
                }
            } else {
                assert sItem instanceof SelectExpressionItem;
                SelectExpressionItem col = (SelectExpressionItem) sItem;
                Field field = null;
                /* Если пользоваться этим приемом, то будет введение разработчика в заблуждение
                 * т.к. в дизайнере и автозавершении кода, поле результата будет поименовано
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
                    // Безымянные поля, получающиеся когда нет алиаса, будут
                    // замещены полями полученными из базы во время исполнения запроса.

                    field = new Field(col.getAlias() != null ? col.getAlias().getName()
                            : (col.getExpression() instanceof Column ? ((Column) col.getExpression()).getColumnName() : ""));

                    field.setTableName(aQuery.getEntityName());
                    /**
                     * There is an unsolved problem about type of the
                     * expression. This might be solved using manually setted up
                     * field's type and description information during
                     * "putStoredTableFieldsMetadata(...)" call.
                     */
                    field.setType(null);
                }
                aQuery.getFields().add(field);
            }
        }
    }

    protected class FieldsResult {

        public Fields fields;
        public boolean fromRealTable;

        public FieldsResult(Fields aResult, boolean aFromRealTable) {
            super();
            fields = aResult;
            fromRealTable = aFromRealTable;
        }
    }

    /**
     * Returns cached table fields if <code>aTablyName</code> is a table name or
     * query fields if <code>aTablyName</code> is query tably name in format:
     * #&lt;id&gt;.
     *
     * @param aDatasourceName Database identifier, the query belongs to. That
     * database is query-inner table metadata source, but query is stored in
     * application.
     * @param aTablyName Table or query tably name.
     * @return Fields instance.
     * @throws Exception
     */
    protected FieldsResult getTablyFields(String aDatasourceName, String aTablyName) throws Exception {
        Fields tableFields;
        if (aTablyName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {// strong reference to stored subquery
            tableFields = null;
            aTablyName = aTablyName.substring(ClientConstants.STORED_QUERY_REF_PREFIX.length());
        } else {// soft reference to table or a stored subquery.
            try {
                tableFields = basesProxy.getMetadataCache(aDatasourceName).getTableMetadata(aTablyName);
            } catch (Exception ex) {
                tableFields = null;
            }
        }
        if (tableFields != null) {// Tables have a higher priority in soft reference case
            return new FieldsResult(tableFields, true);
        } else {
            SqlQuery query = subQueriesProxy.getQuery(aTablyName, null, null, null);
            if (query != null) {
                return new FieldsResult(query.getFields(), false);
            } else {
                return null;
            }
        }
    }

    private Field resolveFieldByColumn(SqlQuery aQuery, Column column, SelectExpressionItem selectItem, Map<String, FromItem> aSources) throws Exception {
        Field field = null;
        FromItem fieldSource = null;// Это таблица парсера - источник данных в составе запроса.
        boolean fieldFromRealTable = false;
        if (column.getTable() != null && column.getTable().getWholeTableName() != null) {
            FromItem namedSource = aSources.get(column.getTable().getWholeTableName().toLowerCase());
            if (namedSource != null) {
                if (namedSource instanceof Table) {
                    /**
                     * Таблица поля, предоставляемая парсером никак не связана с
                     * таблицей из списка from. Поэтому мы должны связать их
                     * самостоятельно. Такая вот особенность парсера.
                     */
                    FieldsResult tableFieldsResult = getTablyFields(aQuery.getDatasourceName(), ((Table) namedSource).getWholeTableName());
                    if (tableFieldsResult != null && tableFieldsResult.fields != null && tableFieldsResult.fields.contains(column.getColumnName())) {
                        field = tableFieldsResult.fields.get(column.getColumnName());
                        fieldSource = namedSource;
                        fieldFromRealTable = tableFieldsResult.fromRealTable;
                    }
                } else if (namedSource instanceof SubSelect) {
                    Fields subFields = processSubQuery(aQuery, (SubSelect) namedSource);
                    if (subFields.contains(column.getColumnName())) {
                        field = subFields.get(column.getColumnName());
                        fieldSource = namedSource;
                        fieldFromRealTable = false;
                    }
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
                if (anySource instanceof Table) {
                    FieldsResult fieldsResult = getTablyFields(aQuery.getDatasourceName(), ((Table) anySource).getWholeTableName());
                    if (fieldsResult != null && fieldsResult.fields != null && fieldsResult.fields.contains(column.getColumnName())) {
                        field = fieldsResult.fields.get(column.getColumnName());
                        fieldSource = anySource;
                        fieldFromRealTable = fieldsResult.fromRealTable;
                        break;
                    }
                } else if (anySource instanceof SubSelect) {
                    Fields fields = processSubQuery(aQuery, (SubSelect) anySource);
                    if (fields != null && fields.contains(column.getColumnName())) {
                        field = fields.get(column.getColumnName());
                        fieldSource = anySource;
                        fieldFromRealTable = false;
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
            Field copied = new Field();
            copied.assignFrom(field);
            if (fieldFromRealTable) {
                TypesResolver resolver = basesProxy.getMetadataCache(aQuery.getDatasourceName()).getDatasourceSqlDriver().getTypesResolver();
                JdbcField jField = (JdbcField) field;
                copied.setType(resolver.toApplicationType(jField.getJdbcType(), jField.getType()));
                if (jField.getSchemaName() != null && !jField.getSchemaName().isEmpty()) {
                    copied.setTableName(jField.getSchemaName() + "." + copied.getTableName());
                }
            }
            /**
             * Заменим отметку о первичном ключе из оригинальной таблицы на
             * отметку о внешнем ключе, указывающем на ту же таблицу. Замена
             * производится с учетом "главной" таблицы. Теперь эта обработка не
             * нужна, т.к. все таблицы "главные", т.е. изменения могут попасть в
             * несколько таблиц одновременно, с учетом их ключей, конечно.
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
             * Заменять имя оригинальной таблицы нельзя, особенно если это поле
             * ключевое т.к. при установлении связи по этим полям будут
             * проблемы. ORM-у и дизайнеру придется "разматывать" источник поля
             * сквозь все запросы до таблицы чтобы восстановить связи по ключам.
             * Здесь это делается исключительно ради очень специального
             * использования фабрики в дизайнере запросов.
             */
            if (aliasesToTableNames
                    && fieldSource != null && fieldSource.getAlias() != null && !fieldSource.getAlias().getName().isEmpty()) {
                copied.setTableName(fieldSource.getAlias().getName());
            }
            return copied;
        } else {
            return null;
        }
    }
}
