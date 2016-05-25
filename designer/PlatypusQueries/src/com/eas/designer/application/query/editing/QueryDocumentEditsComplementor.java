/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.edits.NewRelationEdit;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.edits.fields.NewFieldEdit;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.editing.riddle.RiddleTask;
import com.eas.designer.application.query.editing.riddle.StatementRiddler;
import com.eas.designer.application.query.nodes.QueryEntityNode;
import com.eas.designer.datamodel.nodes.NodePropertyUndoableEdit;
import com.eas.script.JsDoc;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

/**
 *
 * @author mg
 */
public class QueryDocumentEditsComplementor {

    public static final String QUERY_ALIAS_PREFIX = "q";
    public static final String TABLE_ALIAS_PREFIX = "t";
    private static final String NEW_ENTITY_STATEMENT_SQL = "select * from ";

    public CompoundEdit complementEditWithStatement(Statement statement, UndoableEdit edit) throws Exception, BadLocationException {
        CompoundEdit compound = new CompoundEdit();
        StringBuilder sb = new StringBuilder();
        deparseStatement(sb, statement);
        String newSqlText = sb.toString();
        Document doc = dataObject.getSqlTextDocument();
        compound.addEdit(edit);
        UndoableEditListener undoListener = new UndoableEditsAdder(compound);
        doc.addUndoableEditListener(undoListener);
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, newSqlText, null);
        } finally {
            doc.removeUndoableEditListener(undoListener);
        }
        compound.end();
        return compound;
    }

    private String constructTablyName(QueryEntity qEntity) {
        if (qEntity.getQueryName() != null) {
            return ClientConstants.STORED_QUERY_REF_PREFIX + qEntity.getQueryName();
        } else {
            return qEntity.getFullTableName();
        }
    }

    public void deparseStatement(StringBuilder sb, Statement statement) {
        StatementDeParser deparser = new StatementDeParser(sb);
        if (statement instanceof Select) {
            deparser.visit((Select) statement);
        } else if (statement instanceof Update) {
            deparser.visit((Update) statement);
        } else if (statement instanceof Insert) {
            deparser.visit((Insert) statement);
        } else if (statement instanceof Delete) {
            deparser.visit((Delete) statement);
        } else if (statement instanceof Replace) {
            deparser.visit((Replace) statement);
        }
    }

    private void riddleStatement(Statement statement, RiddleTask task) {
        StatementRiddler riddler = new StatementRiddler(task);
        if (statement instanceof Select) {
            riddler.visit((Select) statement);
        } else if (statement instanceof Update) {
            riddler.visit((Update) statement);
        } else if (statement instanceof Insert) {
            riddler.visit((Insert) statement);
        } else if (statement instanceof Delete) {
            riddler.visit((Delete) statement);
        } else if (statement instanceof Replace) {
            riddler.visit((Replace) statement);
        }
    }

    public static String findFreeAliasName(Map<String, Table> aTables, String baseName) {
        Set<String> names = new HashSet<>();
        names.addAll(aTables.keySet());
        String name = baseName;
        int n = 0;
        while (names.contains(name)) {
            name = baseName + (++n);
        }
        return name;
    }

    private UndoableEdit complementNewEntity(UndoableEdit anEdit) throws Exception {
        NewEntityEdit<QueryEntity, QueryModel> edit = (NewEntityEdit<QueryEntity, QueryModel>) anEdit;
        Statement statement = dataObject.getCommitedStatement();
        if (statement == null) {
            String factText = dataObject.getSqlTextDocument().getText(0, dataObject.getSqlTextDocument().getLength());
            factText = normalizeFactQueryText(factText);
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            String aliasName = (edit.getEntity().getQueryName() != null ? QUERY_ALIAS_PREFIX : TABLE_ALIAS_PREFIX) + "1"; //NOI18N
            assert edit.getEntity() instanceof QueryEntity;
            edit.getEntity().setAlias(aliasName);
            statement = parserManager.parse(new StringReader(factText + NEW_ENTITY_STATEMENT_SQL + constructTablyName(edit.getEntity()) + " " + aliasName)); //NOI18N
            anEdit = complementEditWithStatement(statement, edit);
        } else {
            Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
            // Take care of pretty queries alias names.
            String aliasName = findFreeAliasName(tables, edit.getEntity().getQueryName() != null ? QUERY_ALIAS_PREFIX : TABLE_ALIAS_PREFIX);
            QueryEntity qEntity = edit.getEntity();
            qEntity.setAlias(aliasName);
            Table tbl = new Table(qEntity.getQueryName() == null ? qEntity.getTableSchemaName() : null, constructTablyName(qEntity));
            if (tbl.getAlias() != null) {
                tbl.getAlias().setName(qEntity.getAlias());
            } else {
                tbl.setAlias(new Alias());
                tbl.getAlias().setName(qEntity.getAlias());
            }
            if (statement instanceof Select) {
                Select select = (Select) statement;
                if (select.getSelectBody() instanceof PlainSelect) {
                    PlainSelect pSelect = (PlainSelect) select.getSelectBody();
                    if (pSelect.getFromItem() == null) {
                        pSelect.setFromItem(tbl);
                    } else {
                        Join join = new Join();
                        join.setSimple(true);// Cross join called "simple" in parser.
                        join.setRightItem(tbl);
                        join.setOnExpression(null);// Cross join without any on condition.
                        if (pSelect.getJoins() == null) {
                            pSelect.setJoins(new ArrayList<>());
                        }
                        pSelect.getJoins().add(join);
                    }
                    anEdit = complementEditWithStatement(statement, edit);
                }// union is out of scope
            }
        }
        return anEdit;
    }

    private UndoableEdit complementDeleteEntity(UndoableEdit anEdit) throws Exception {
        assert anEdit instanceof DeleteEntityEdit;
        DeleteEntityEdit<QueryEntity, QueryModel> edit = (DeleteEntityEdit<QueryEntity, QueryModel>) anEdit;
        QueryEntity entity = edit.getEntity();
        Statement statement = dataObject.getCommitedStatement();
        if (statement != null) {
            Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
            riddleStatement(statement, new DeleteEntityColumnsRiddleTask(entity, tables));
            riddleStatement(statement, new DeleteEntityRiddleTask(entity, tables));
            anEdit = complementEditWithStatement(statement, edit);
        }
        return anEdit;
    }

    private UndoableEdit complementDeleteField(UndoableEdit anEdit) throws Exception {
        assert anEdit instanceof DeleteFieldEdit<?>;
        DeleteFieldEdit<QueryEntity> edit = (DeleteFieldEdit<QueryEntity>) anEdit;
        assert edit.getField() instanceof Parameter;
        Statement statement = dataObject.getCommitedStatement();
        if (statement != null) {
            //Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement);
            riddleStatement(statement, new DeleteParameterRiddleTask((Parameter) edit.getField()));
            anEdit = complementEditWithStatement(statement, edit);
        }
        return anEdit;
    }

    private UndoableEdit complementAliasChange(NodePropertyUndoableEdit anEdit) throws Exception {
        assert anEdit.getSubject() instanceof QueryEntity;
        QueryEntity entity = (QueryEntity) anEdit.getSubject();
        Statement statement = dataObject.getCommitedStatement();
        if (statement != null) {
            Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
            String oldAlias = (String) anEdit.getOldValue();
            String newAlias = (String) anEdit.getNewValue();
            if (newAlias != null && newAlias.isEmpty()) {
                newAlias = null;
            }
            entity.setAlias(oldAlias);
            riddleStatement(statement, new ChangeAliasRiddleTask(entity, oldAlias, newAlias, tables));
            entity.setAlias(newAlias != null ? newAlias : "");
            return complementEditWithStatement(statement, anEdit);
        } else {
            return anEdit;
        }
    }

    private UndoableEdit complementFieldNameChange(ChangeFieldEdit<QueryEntity> anEdit) throws Exception {
        Statement statement = dataObject.getCommitedStatement();
        if (statement == null) {
            return anEdit;
        } else {
            riddleStatement(statement, new ChangeParametersNameRiddleTask(anEdit.getBeforeContent().getName(), anEdit.getAfterContent().getName()));
            return complementEditWithStatement(statement, anEdit);
        }
    }

    private boolean isParameterToParameterBinding(Relation<QueryEntity> relation) {
        return !(relation.getLeftEntity() instanceof QueryParametersEntity) && relation.isLeftParameter()
                && !(relation.getRightEntity() instanceof QueryParametersEntity) && relation.isRightParameter();
    }

    private String normalizeFactQueryText(String factText) {
        if (factText != null) {
            String authorAnnotationValue = PlatypusFilesSupport.getAnnotationValue(factText, JsDoc.Tag.AUTHOR_TAG);
            String nameAnnotationValue = PlatypusFilesSupport.getAnnotationValue(factText, JsDoc.Tag.NAME_TAG);
            String publicAnnotationValue = PlatypusFilesSupport.getAnnotationValue(factText, JsDoc.Tag.PUBLIC_TAG);
            String readonlyAnnotationValue = PlatypusFilesSupport.getAnnotationValue(factText, JsDoc.Tag.READONLY_TAG);
            String procedureAnnotationValue = PlatypusFilesSupport.getAnnotationValue(factText, JsDoc.Tag.PROCEDURE_TAG);
            StringBuilder factTextBuilder = new StringBuilder();
            factTextBuilder.append("/**\n");
            factTextBuilder.append(" *\n");
            if (authorAnnotationValue != null) {
                factTextBuilder.append(" * ").append("@author").append(" ").append(authorAnnotationValue).append("\n");
            }
            if (nameAnnotationValue != null) {
                factTextBuilder.append(" * ").append(JsDoc.Tag.NAME_TAG).append(" ").append(nameAnnotationValue).append("\n");
            }
            if (publicAnnotationValue != null) {
                factTextBuilder.append(" * ").append(JsDoc.Tag.PUBLIC_TAG).append(" ").append(publicAnnotationValue).append("\n");
            }
            if (readonlyAnnotationValue != null) {
                factTextBuilder.append(" * ").append(JsDoc.Tag.PUBLIC_TAG).append(" ").append(readonlyAnnotationValue).append("\n");
            }
            if (procedureAnnotationValue != null) {
                factTextBuilder.append(" * ").append(JsDoc.Tag.PROCEDURE_TAG).append(" ").append(procedureAnnotationValue).append("\n");
            }
            factTextBuilder.append(" */\n");
            factText = factTextBuilder.toString();
        } else {
            factText = "";
        }
        return factText;
    }

    protected class UndoableEditsAdder implements UndoableEditListener {

        protected CompoundEdit compound;

        public UndoableEditsAdder(CompoundEdit aCompound) {
            super();
            compound = aCompound;
        }

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            compound.addEdit(e.getEdit());
        }
    }
    protected PlatypusQueryDataObject dataObject;
    protected boolean complementing;

    public QueryDocumentEditsComplementor(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    public UndoableEdit complementEdit(UndoableEdit anEdit) throws Exception {
        if (!complementing) {
            complementing = true;
            try {
                anEdit = complementKnownEdits(anEdit);
            } finally {
                complementing = false;
            }
            return anEdit;
        } else {
            return null;
        }
    }

    public boolean isComplementing() {
        return complementing;
    }

    public UndoableEdit complementKnownEdits(UndoableEdit anEdit) throws Exception {
        if (anEdit instanceof AccessibleCompoundEdit) {
            CompoundEdit newCompound = new CompoundEdit();
            AccessibleCompoundEdit compound = (AccessibleCompoundEdit) anEdit;
            for (UndoableEdit lEdit : compound.getEdits()) {
                lEdit = complementKnownEdits(lEdit);
                newCompound.addEdit(lEdit);
            }
            newCompound.end();
            anEdit = newCompound;
        } else if (anEdit instanceof NewRelationEdit) {
            anEdit = complementNewRelation(anEdit);
        } else if (anEdit instanceof DeleteRelationEdit) {
            anEdit = complementDeleteRelation(anEdit);
        } else if (anEdit instanceof NewEntityEdit) {
            anEdit = complementNewEntity(anEdit);
        } else if (anEdit instanceof DeleteEntityEdit) {
            anEdit = complementDeleteEntity(anEdit);
        } else if (!(anEdit instanceof NewFieldEdit) && anEdit instanceof DeleteFieldEdit) {
            anEdit = complementDeleteField(anEdit);
        } else if (anEdit instanceof NodePropertyUndoableEdit) {
            NodePropertyUndoableEdit npEdit = (NodePropertyUndoableEdit) anEdit;
            if (npEdit.getSubject() instanceof QueryEntity && QueryEntityNode.ALIAS_PROP_NAME.equals(npEdit.getPropertyName())) {
                anEdit = complementAliasChange(npEdit);
            }
        } else if (anEdit instanceof ChangeFieldEdit<?>) {
            ChangeFieldEdit<QueryEntity> chEdit = (ChangeFieldEdit<QueryEntity>) anEdit;
            if (!chEdit.getBeforeContent().getName().equals(chEdit.getAfterContent().getName())) {
                anEdit = complementFieldNameChange(chEdit);
            }
        }
        return anEdit;
    }

    public UndoableEdit complementNewRelation(UndoableEdit anEdit) throws Exception {
        // Adding a relation by a user, leads to modifying where or from clauses only.
        // other edits must be rejected by model's validator and inspired by sql-source
        // editing and following model complementation.
        NewRelationEdit<QueryEntity> edit = (NewRelationEdit<QueryEntity>) anEdit;
        Relation<QueryEntity> relation = edit.getRelation();
        // Sql grammar has no any place for subquery parameters binding
        if (!isParameterToParameterBinding(relation)) {
            Statement statement = dataObject.getCommitedStatement();
            if (statement != null) {
                Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
                // Where clause
                if (relation.getLeftEntity() instanceof QueryParametersEntity && relation.isRightField()) {
                    QueryEntity qEntity = relation.getRightEntity();
                    if (statement instanceof Select) {
                        Select select = (Select) statement;
                        if (select.getSelectBody() instanceof PlainSelect) {
                            PlainSelect ps = (PlainSelect) select.getSelectBody();
                            Expression oldWhere = ps.getWhere();
                            try {
                                EqualsTo condition = new EqualsTo();
                                // Таблица из списка from
                                Table fTable = tables.get(SqlTextEditsComplementor.generateSyntaxicId(qEntity).toLowerCase());
                                // Для парсера, столбцы во where, не связаны с таблицами из from
                                condition.setLeftExpression(new NamedParameter(relation.getLeftField().getName()));
                                condition.setRightExpression(new Column(fTable.getAlias() != null ? new Table(null, fTable.getAlias().getName()) : fTable, relation.getRightField().getName()));
                                ps.setWhere(ps.getWhere() != null ? new AndExpression(ps.getWhere(), condition) : condition);

                                anEdit = complementEditWithStatement(statement, edit);
                            } finally {
                                ps.setWhere(oldWhere);
                            }
                        } else if (select.getSelectBody() instanceof Union) {
                            Union union = (Union) select.getSelectBody();
                            EqualsTo condition = new EqualsTo();
                            String rightTabbleSyntaxicId = SqlTextEditsComplementor.generateSyntaxicId(qEntity).toLowerCase();
                            // Таблица из списка from
                            Table fTable = tables.get(rightTabbleSyntaxicId);
                            // Для парсера, столбцы во where, не связаны с таблицами из from
                            condition.setLeftExpression(new NamedParameter(relation.getLeftField().getName()));
                            condition.setRightExpression(new Column(fTable.getAlias() != null ? new Table(null, fTable.getAlias().getName()) : fTable, relation.getRightField().getName()));
                            PlainSelect targetPs = null;
                            // determine what plain select right table from.
                            for (Object oPs : union.getPlainSelects()) {
                                PlainSelect ps = (PlainSelect) oPs;
                                Select dummySelect = new Select();
                                dummySelect.setSelectBody(ps);
                                Map<String, Table> subTables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, dummySelect, true);
                                if (subTables.containsKey(rightTabbleSyntaxicId)) {
                                    targetPs = ps;
                                    break;
                                }
                            }
                            if (targetPs != null) {
                                Expression oldWhere = targetPs.getWhere();
                                try {
                                    targetPs.setWhere(targetPs.getWhere() != null ? new AndExpression(targetPs.getWhere(), condition) : condition);

                                    anEdit = complementEditWithStatement(statement, edit);
                                } finally {
                                    targetPs.setWhere(oldWhere);
                                }
                            }
                        }
                    } else if (statement instanceof Delete) {
                        Delete delete = (Delete) statement;
                        Expression oldWhere = delete.getWhere();
                        try {
                            EqualsTo condition = new EqualsTo();
                            // Таблица из списка from
                            Table fTable = tables.get(SqlTextEditsComplementor.generateSyntaxicId(qEntity).toLowerCase());
                            // Для парсера, столбцы во where, не связаны с таблицами из from
                            condition.setLeftExpression(new NamedParameter(relation.getLeftField().getName()));
                            condition.setRightExpression(new Column(fTable.getAlias() != null ? new Table(null, fTable.getAlias().getName()) : fTable, relation.getRightField().getName()));
                            delete.setWhere(delete.getWhere() != null ? new AndExpression(delete.getWhere(), condition) : condition);
                            anEdit = complementEditWithStatement(statement, edit);
                        } finally {
                            delete.setWhere(oldWhere);
                        }
                    } else if (statement instanceof Update) {
                        Update update = (Update) statement;
                        Expression oldWhere = update.getWhere();
                        try {
                            EqualsTo condition = new EqualsTo();
                            // Таблица из списка from
                            Table fTable = tables.get(SqlTextEditsComplementor.generateSyntaxicId(qEntity).toLowerCase());
                            // Для парсера, столбцы во where, не связаны с таблицами из from
                            condition.setLeftExpression(new NamedParameter(relation.getLeftField().getName()));
                            condition.setRightExpression(new Column(fTable.getAlias() != null ? new Table(null, fTable.getAlias().getName()) : fTable, relation.getRightField().getName()));
                            update.setWhere(update.getWhere() != null ? new AndExpression(update.getWhere(), condition) : condition);
                            anEdit = complementEditWithStatement(statement, edit);
                        } finally {
                            update.setWhere(oldWhere);
                        }
                    }
                } else if (relation.isLeftField() && relation.isRightField()) {
                    // From clause
                    QueryEntity lEntity = relation.getLeftEntity();
                    QueryEntity rEntity = relation.getRightEntity();
                    Table lTable = tables.get(SqlTextEditsComplementor.generateSyntaxicId(lEntity).toLowerCase());
                    Table rTable = tables.get(SqlTextEditsComplementor.generateSyntaxicId(rEntity).toLowerCase());

                    if (lTable != null && rTable != null) {
                        if (statement instanceof Select) {
                            Select select = (Select) statement;
                            if (select.getSelectBody() instanceof PlainSelect) {
                                PlainSelect ps = (PlainSelect) select.getSelectBody();
                                if (ps.getJoins() != null && !ps.getJoins().isEmpty()) {
                                    Join join = null;
                                    Table leftTable = null;
                                    Table rightTable = null;
                                    String leftField = null;
                                    String rightField = null;
                                    boolean inverse = false;
                                    // We need to iterate on joins from most right to most left
                                    // to edit join condition of right most involed table
                                    for (int i = ps.getJoins().size() - 1; i >= 0; i--) {
                                        join = ps.getJoins().get(i);
                                        FromItem joinItem = join.getRightItem();
                                        if (joinItem == lTable || joinItem == rTable) {
                                            if (join.getRightItem() == lTable) {
                                                inverse = true;
                                                leftTable = rTable;
                                                leftField = relation.getRightField().getName();
                                                rightTable = lTable;
                                                rightField = relation.getLeftField().getName();
                                            } else {
                                                leftTable = lTable;
                                                leftField = relation.getLeftField().getName();
                                                rightTable = rTable;
                                                rightField = relation.getRightField().getName();
                                            }
                                            break;
                                        }
                                    }
                                    if (leftTable != null && rightTable != null) {
                                        EqualsTo condition = new EqualsTo();
                                        condition.setLeftExpression(new Column(leftTable.getAlias() != null ? new Table(null, leftTable.getAlias().getName()) : leftTable, leftField));
                                        condition.setRightExpression(new Column(rightTable.getAlias() != null ? new Table(null, rightTable.getAlias().getName()) : rightTable, rightField));
                                        if (inverse) {
                                            Expression tmpExpression = condition.getLeftExpression();
                                            condition.setLeftExpression(condition.getRightExpression());
                                            condition.setRightExpression(tmpExpression);
                                        }
                                        if (join.isSimple() || join.getOnExpression() == null) {
                                            join.setSimple(false);
                                            join.setInner(true);
                                            join.setOnExpression(condition);
                                        } else {
                                            join.setOnExpression(new AndExpression(join.getOnExpression(), condition));
                                        }
                                        anEdit = complementEditWithStatement(statement, edit);
                                    }
                                }
                            }
                        }// case of union clause is out of the scope
                    }
                }
            }
        }
        return anEdit;
    }

    private UndoableEdit complementDeleteRelation(UndoableEdit anEdit) throws Exception {
        assert anEdit instanceof DeleteRelationEdit<?>;
        DeleteRelationEdit<QueryEntity> edit = (DeleteRelationEdit<QueryEntity>) anEdit;
        Relation<QueryEntity> relation = edit.getRelation();
        // Sql grammar has no any place for subquery parameters binding
        if (relation.isLeftField() || relation.isRightField()) {
            Statement statement = dataObject.getCommitedStatement();
            if (statement != null) {
                Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
                riddleStatement(statement, new DeleteRelationRiddleTask(relation, tables));
                anEdit = complementEditWithStatement(statement, edit);
            }
        }
        return anEdit;
    }
}
