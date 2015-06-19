/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.queries;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.StoredQueryFactory;
import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.model.Relation;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mg
 */
public class ScriptedQueryFactory extends StoredQueryFactory{

    public ScriptedQueryFactory(DatabasesClient aBasesProxy, LocalQueriesProxy aQueriesProxy, PlatypusIndexer aIndexer, boolean aPreserveDatasources) throws Exception {
        super(aBasesProxy, aQueriesProxy, aIndexer, aPreserveDatasources);
    }

    public ScriptedQueryFactory(DatabasesClient aBasesProxy, LocalQueriesProxy aQueriesProxy, PlatypusIndexer aIndexer) throws Exception {
        super(aBasesProxy, aQueriesProxy, aIndexer);
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
    @Override
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

}
