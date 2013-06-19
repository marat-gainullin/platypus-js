/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.NewRelationEdit;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.edits.fields.NewFieldEdit;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.editing.riddle.ExpressionRiddler;
import com.eas.designer.application.query.editing.riddle.SelectRiddler;
import com.eas.designer.application.query.editing.riddle.StatementRiddler;
import java.awt.Rectangle;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.expression.Relation;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

/**
 *
 * @author mg
 */
public class SqlTextEditsComplementor {

    private final static String PARAMETERS_NAME = "!parameters!";
    private final static Pattern SUBQUERY_LINK_PATTERN = Pattern.compile(SQLUtils.SUBQUERY_TABLE_NAME_REGEXP, Pattern.CASE_INSENSITIVE);
    protected PlatypusQueryDataObject dataObject;

    public SqlTextEditsComplementor(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    public void complementStatement(CompoundEdit edit) throws Exception {
        Statement statement = dataObject.getCommitedStatement();
        if (statement == null) {
            synchronizeParameters(edit, dataObject.getModel(), new HashSet<NamedParameter>());
            synchronizeTables(edit, dataObject.getModel(), new HashMap<String, Table>());
            synchronizeRelations(edit, dataObject.getModel(), new HashMap<String, com.eas.client.model.Relation<QueryEntity>>());
        } else {
            Set<NamedParameter> parameters = extractParameters(statement);
            synchronizeParameters(edit, dataObject.getModel(), parameters);
            Map<String, Table> tables = TablesFinder.getTablesMap(null, statement, true);
            synchronizeTables(edit, dataObject.getModel(), tables);
            Map<String, QueryEntity> modelTables = prepareModelTables(dataObject.getModel());
            Map<String, com.eas.client.model.Relation<QueryEntity>> statementRelations = prepareStatementRelations(dataObject.getModel(), statement, modelTables);
            synchronizeRelations(edit, dataObject.getModel(), statementRelations);
        }
        dataObject.procedureChanged(!dataObject.isProcedure(), dataObject.isProcedure());
        dataObject.manualChanged(!dataObject.isManual(), dataObject.isManual());
    }

    public String generateSideId(GatherRelationsSubjectsRiddleTask sideSubjectsTask) {
        StringBuilder sb = new StringBuilder();
        if (!sideSubjectsTask.getParameters().isEmpty()) {
            NamedParameter np = sideSubjectsTask.getParameters().iterator().next();
            sb.append(PARAMETERS_NAME).append(".").append(np.getName());
        } else if (!sideSubjectsTask.getColumns().isEmpty()) {
            Column col = sideSubjectsTask.getColumns().iterator().next();
            sb.append(col.getTable().getWholeTableName()).append(".").append(col.getColumnName());
        }
        return sb.toString();
    }

    public void synchronizeRelations(CompoundEdit section, QueryModel model, Map<String, com.eas.client.model.Relation<QueryEntity>> statmentRelations) throws Exception {
        List<UndoableEdit> edits = new ArrayList<>();
        // Let's delete relations absent in statement's relations map
        Map<String, com.eas.client.model.Relation<QueryEntity>> modelRelations = prepareModelRelations(model);
        for (com.eas.client.model.Relation<QueryEntity> rel : modelRelations.values()) {
            String left2RightId = generateRelationId(rel, true);
            String right2LeftId = generateRelationId(rel, false);
            if (!statmentRelations.containsKey(left2RightId)
                    && !statmentRelations.containsKey(right2LeftId)) {
                DeleteRelationEdit<QueryEntity> rEdit = new DeleteRelationEdit<>(rel);
                edits.add(rEdit);
                section.addEdit(rEdit);
            }
        }
        for (UndoableEdit edit : edits) {
            edit.redo();
        }

        // Let's add relations absent in model's relations map
        modelRelations = prepareModelRelations(dataObject.getModel());
        for (com.eas.client.model.Relation<QueryEntity> rel : statmentRelations.values()) {
            String left2RightId = generateRelationId(rel, true);
            String right2LeftId = generateRelationId(rel, false);
            if (!modelRelations.containsKey(left2RightId)
                    && !modelRelations.containsKey(right2LeftId)) {
                NewRelationEdit<QueryEntity> rEdit = new NewRelationEdit<>(rel);
                edits.add(rEdit);
                section.addEdit(rEdit);
            }
        }
        for (UndoableEdit edit : edits) {
            edit.redo();
        }
    }

    public Map<String, com.eas.client.model.Relation<QueryEntity>> prepareStatementRelations(QueryModel model, Statement statement, Map<String, QueryEntity> tables) throws Exception {
        Map<String, com.eas.client.model.Relation<QueryEntity>> statmentRelations = new HashMap<>();
        // Relation here is only syntaxic relational operator
        Set<Relation> relations = extractRelations(statement);
        for (Relation rel : relations) {
            GatherRelationsSubjectsRiddleTask leftSubjectsTask = extractRelationsSubjects(((BinaryExpression) rel).getLeftExpression());
            GatherRelationsSubjectsRiddleTask rightSubjectsTask = extractRelationsSubjects(((BinaryExpression) rel).getRightExpression());

            String leftSideId = generateSideId(leftSubjectsTask);
            String rightSideId = generateSideId(rightSubjectsTask);
            String relId = leftSideId + ">>>" + rightSideId;
            com.eas.client.model.Relation<QueryEntity> modelRel = new com.eas.client.model.Relation<>();
            // Relation's left side
            if (leftSideId != null && !leftSideId.isEmpty()) {
                if (leftSideId.startsWith(PARAMETERS_NAME + ".")) {
                    modelRel.setLeftEntity(model.getParametersEntity());
                    modelRel.setLeftField(model.getParametersEntity().getFields().get(leftSideId.substring(PARAMETERS_NAME.length() + 1)));
                } else {
                    int dotIdx = leftSideId.lastIndexOf('.');
                    String tblId = leftSideId.substring(0, dotIdx);
                    QueryEntity lEntity = tables.get(tblId.toLowerCase());
                    if (lEntity != null) {
                        modelRel.setLeftEntity(lEntity);
                        modelRel.setLeftField(lEntity.getFields().get(leftSideId.substring(dotIdx + 1)));
                    }
                }
            }
            // Relation's right side
            if (rightSideId != null && !rightSideId.isEmpty()) {
                if (rightSideId.startsWith(PARAMETERS_NAME + ".")) {
                    modelRel.setRightEntity(model.getParametersEntity());
                    modelRel.setRightField(model.getParametersEntity().getFields().get(rightSideId.substring(PARAMETERS_NAME.length() + 1)));
                } else {
                    int dotIdx = rightSideId.lastIndexOf('.');
                    String tblId = rightSideId.substring(0, dotIdx);
                    QueryEntity rEntity = tables.get(tblId.toLowerCase());
                    if (rEntity != null) {
                        modelRel.setRightEntity(rEntity);
                        modelRel.setRightField(rEntity.getFields().get(rightSideId.substring(dotIdx + 1)));
                    }
                }
            }
            if (validStatementRelation(modelRel)) {
                statmentRelations.put(relId, modelRel);
            }
        }
        return statmentRelations;
    }

    public static Set<NamedParameter> extractParameters(Statement statement) {
        GatherRelationsSubjectsRiddleTask relsTask = new GatherRelationsSubjectsRiddleTask();
        StatementRiddler stRiddler = new StatementRiddler(relsTask);
        statement.accept(stRiddler);
        return relsTask.getParameters();
    }

    public static Set<Relation> extractRelations(Statement statement) {
        GatherRelationsRiddleTask relsTask = new GatherRelationsRiddleTask();
        StatementRiddler stRiddler = new StatementRiddler(relsTask);
        statement.accept(stRiddler);
        return relsTask.getRelations();
    }

    public static GatherRelationsSubjectsRiddleTask extractRelationsSubjects(Expression aExpression) {
        GatherRelationsSubjectsRiddleTask subjectsTask = new GatherRelationsSubjectsRiddleTask();
        SelectRiddler selRiddler = new SelectRiddler(subjectsTask);
        ExpressionRiddler er = new ExpressionRiddler(selRiddler, subjectsTask);
        selRiddler.setExpressionVisitor(er);
        aExpression.accept(er);
        return subjectsTask;
    }

    public static String generateSyntaxicId(QueryEntity entity) {
        String tablyName = null;
        if (entity instanceof QueryParametersEntity) {
            tablyName = PARAMETERS_NAME;
        } else {
            if (entity.getAlias() != null && !entity.getAlias().isEmpty()) {
                tablyName = entity.getAlias();
            } else {
                if (entity.getQueryId() != null) {
                    String inQueryName = entity.getQueryId();
                    if (inQueryName.matches("\\d+")) {
                        inQueryName = ClientConstants.QUERY_ID_PREFIX + entity.getQueryId();
                    }
                    tablyName = inQueryName;
                } else {
                    if (entity.getTableSchemaName() != null && !entity.getTableSchemaName().isEmpty()) {
                        tablyName = entity.getTableSchemaName() + "." + entity.getTableName();
                    } else {
                        tablyName = entity.getTableName();
                    }
                }
            }
        }
        return tablyName.toLowerCase();
    }

    public Map<String, com.eas.client.model.Relation<QueryEntity>> prepareModelRelations(QueryModel model) {
        Map<String, com.eas.client.model.Relation<QueryEntity>> relsMap = new HashMap<>();
        for (com.eas.client.model.Relation<QueryEntity> relation : model.getRelations()) {
            if (relation.isLeftField() || relation.isRightField()) {
                // Parameter to parameter relations is out of scope
                // Note! Edited query parameters are treated as fields of 
                // parameters entity.
                relsMap.put(generateRelationId(relation, true), relation);
            }
        }
        return relsMap;
    }

    public Map<String, QueryEntity> prepareModelTables(QueryModel model) {
        // Prepare model's tables map
        Map<String, QueryEntity> modelTables = new HashMap<>();
        for (QueryEntity entity : model.getEntities().values()) {
            String tablyName = null;
            tablyName = generateSyntaxicId(entity);
            modelTables.put(tablyName, entity);
        }
        return modelTables;
    }

    private void synchronizeParameters(CompoundEdit section, QueryModel model, Set<NamedParameter> parsedParameters) {
        List<UndoableEdit> edits = new ArrayList<>();

        // Let's delete unactual parameters from model
        Set<String> paramNames = new HashSet<>();
        for (NamedParameter np : parsedParameters) {
            paramNames.add(np.getName().toLowerCase());
        }
        for (Field param : model.getParameters().toCollection()) {
            if (param instanceof GeneratedParameter // We must preserve parameters, created by the user
                    && !paramNames.contains(param.getName().toLowerCase())) {
                // need to delete
                Set<com.eas.client.model.Relation<QueryEntity>> toDel = FieldsEntity.<QueryEntity>getInOutRelationsByEntityField(model.getParametersEntity(), param);

                for (com.eas.client.model.Relation<QueryEntity> rel : toDel) {
                    DeleteRelationEdit<QueryEntity> drEdit = new DeleteRelationEdit<>(rel);
                    edits.add(drEdit);
                    section.addEdit(drEdit);
                }
                DeleteFieldEdit<QueryEntity> edit = new DeleteFieldEdit<QueryEntity>(model.getParametersEntity(), param);
                edits.add(edit);
                section.addEdit(edit);

            }
        }
        paramNames.clear();
        for (Field modelParam : model.getParameters().toCollection()) {
            paramNames.add(modelParam.getName().toLowerCase());
        }
        // Let's add yet absent parameters
        for (NamedParameter np : parsedParameters) {
            if (!paramNames.contains(np.getName().toLowerCase())) {
                GeneratedParameter param = new GeneratedParameter(np.getName(), null, DataTypeInfo.VARCHAR);
                NewFieldEdit<QueryEntity> edit = new NewFieldEdit<QueryEntity>(model.getParametersEntity(), param);
                edits.add(edit);
                section.addEdit(edit);
            }
        }
        for (UndoableEdit edit : edits) {
            edit.redo();
        }
    }

    private void synchronizeTables(CompoundEdit section, QueryModel model, Map<String, Table> aTables) {
        // Let's prepare tables map.
        // In order to achieve correct runtime query processing,
        // we had to add tables with schema in their's names two times in the map.
        // And we must correct this here.
        Map<String, Table> tables = new HashMap<>();
        for (Table table : aTables.values()) {
            if (table.getAlias() != null && !table.getAlias().getName().isEmpty()) {
                tables.put(table.getAlias().getName().toLowerCase(), table);
            } else {
                tables.put(table.getWholeTableName().toLowerCase(), table);
            }
        }
        // Tables map has been prepared.
        List<UndoableEdit> edits = new ArrayList<>();
        Map<String, QueryEntity> modelTables = prepareModelTables(model);
        // Let's delete unactual tables from model
        List<Rectangle> recentBounds = new ArrayList<>();
        for (String tablyName : modelTables.keySet()) {
            if (!tables.containsKey(tablyName) || !DeleteRelationRiddleTask.isDerivedFromIgnoreAlias(tables.get(tablyName), modelTables.get(tablyName))) {
                // Need to delete entity...
                QueryEntity entity = modelTables.get(tablyName);
                Set<com.eas.client.model.Relation<QueryEntity>> toDel = entity.getInOutRelations();
                // Let's delete all binded relations.
                for (com.eas.client.model.Relation<QueryEntity> rel : toDel) {
                    DeleteRelationEdit<QueryEntity> drEdit = new DeleteRelationEdit<>(rel);
                    edits.add(drEdit);
                    section.addEdit(drEdit);
                }
                // Than entity itself.
                recentBounds.add(new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight()));
                DeleteEntityEdit<QueryEntity, QueryModel> deEdit = new DeleteEntityEdit<>(model, entity);
                edits.add(deEdit);
                section.addEdit(deEdit);
            }
        }
        for (UndoableEdit edit : edits) {
            edit.redo();
        }
        edits.clear();
        modelTables = prepareModelTables(model);
        // Let's add absent tables to model
        int newViewX = 10;
        int newViewY = 10;
        for (String inQueryName : tables.keySet()) {
            if (!modelTables.containsKey(inQueryName)) {
                Table table = tables.get(inQueryName);
                QueryEntity toAdd = model.newGenericEntity();
                toAdd.setX(Integer.MAX_VALUE);
                toAdd.setY(Integer.MAX_VALUE);
                newViewX += EntityView.ENTITY_VIEW_DEFAULT_WIDTH + 10;
                newViewY += EntityView.ENTITY_VIEW_DEFAULT_HEIGHT / 2 + 10;
                Rectangle rect;
                if (!recentBounds.isEmpty()) {
                    rect = recentBounds.remove(0);
                } else {
                    rect = new Rectangle(newViewX, newViewY, EntityView.ENTITY_VIEW_DEFAULT_WIDTH, EntityView.ENTITY_VIEW_DEFAULT_HEIGHT);
                }
                assert rect != null : "Some opened PlatypusQueryTopComponent must present while sql text changes are processed.";
                toAdd.setX(rect.x);
                toAdd.setY(rect.y);
                toAdd.setWidth(rect.width);
                toAdd.setHeight(rect.height);
                toAdd.setAlias(table.getAlias() != null ? table.getAlias().getName() : "");
                if (dataObject.existsAppQuery(table.getName())) {
                    toAdd.setQueryId(table.getName());
                } else if (SUBQUERY_LINK_PATTERN.matcher(table.getName()).matches()) {
                    toAdd.setQueryId(table.getName().substring(1));
                } else {
                    toAdd.setTableSchemaName(table.getSchemaName());
                    toAdd.setTableName(table.getName());
                }
                NewEntityEdit<QueryEntity, QueryModel> edit = new NewEntityEdit<>(model, toAdd);
                edits.add(edit);
                section.addEdit(edit);
            }
        }
        for (UndoableEdit edit : edits) {
            edit.redo();
        }
        edits.clear();
    }

    public static String generateRelationId(com.eas.client.model.Relation<QueryEntity> relation, boolean left2Right) {
        StringBuilder sb = new StringBuilder();
        if (left2Right) {
            sb.append(generateSyntaxicId(relation.getLeftEntity()));
            sb.append(".").append(relation.getLeftField());
            sb.append(">>>");
            sb.append(generateSyntaxicId(relation.getRightEntity()));
            sb.append(".").append(relation.getRightField());
        } else {
            sb.append(generateSyntaxicId(relation.getRightEntity()));
            sb.append(".").append(relation.getRightField());
            sb.append(">>>");
            sb.append(generateSyntaxicId(relation.getLeftEntity()));
            sb.append(".").append(relation.getLeftField());
        }
        return sb.toString();
    }

    private boolean validStatementRelation(com.eas.client.model.Relation<QueryEntity> modelRel) {
        return modelRel.getLeftEntity() != null && modelRel.getRightEntity() != null;
    }
}
