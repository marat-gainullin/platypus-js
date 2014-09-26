package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class StatementRiddler implements StatementVisitor {

    protected RiddleTask riddleTask;

    public StatementRiddler(RiddleTask aTask) {
        riddleTask = aTask;
    }

    @Override
    public void visit(CreateTable createTable) {
        CreateTableRiddler createTableDeParser = new CreateTableRiddler(riddleTask);
        createTableDeParser.riddle(createTable);
    }

    @Override
    public void visit(Delete delete) {
        SelectRiddler selectRiddler = new SelectRiddler(riddleTask);
        ExpressionRiddler expressionRiddler = new ExpressionRiddler(selectRiddler, riddleTask);
        selectRiddler.setExpressionVisitor(expressionRiddler);
        DeleteRiddler deleteRiddler = new DeleteRiddler(expressionRiddler);
        deleteRiddler.riddle(delete);
    }

    @Override
    public void visit(Drop drop) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Insert insert) {
        SelectRiddler selectRiddler = new SelectRiddler(riddleTask);
        ExpressionRiddler expressionRiddler = new ExpressionRiddler(selectRiddler, riddleTask);
        selectRiddler.setExpressionVisitor(expressionRiddler);
        InsertRiddler insertDeParser = new InsertRiddler(expressionRiddler, selectRiddler);
        insertDeParser.riddle(insert);
    }

    @Override
    public void visit(Replace replace) {
        SelectRiddler selectRiddler = new SelectRiddler(riddleTask);
        ExpressionRiddler expressionRiddler = new ExpressionRiddler(selectRiddler, riddleTask);
        selectRiddler.setExpressionVisitor(expressionRiddler);
        ReplaceRiddler replaceDeParser = new ReplaceRiddler(expressionRiddler, selectRiddler);
        replaceDeParser.riddle(replace);
    }

    @Override
    public void visit(Select select) {
        SelectRiddler selectRiddler = new SelectRiddler(riddleTask);
        ExpressionRiddler expressionRiddler = new ExpressionRiddler(selectRiddler, riddleTask);
        selectRiddler.setExpressionVisitor(expressionRiddler);
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            for (WithItem withItem : select.getWithItemsList()) {
                //
            }
        }
        select.getSelectBody().accept(selectRiddler);
        if (select.getSelectBody() instanceof Union) {
            Union union = (Union) select.getSelectBody();
            if (union.getPlainSelects().size() == 1) {
                select.setSelectBody(union.getPlainSelects().get(0));
            }
        }
    }

    @Override
    public void visit(Truncate truncate) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Update update) {
        SelectRiddler selectRiddler = new SelectRiddler(riddleTask);
        ExpressionRiddler expressionRiddler = new ExpressionRiddler(selectRiddler, riddleTask);
        UpdateRiddler updateDeParser = new UpdateRiddler(expressionRiddler);
        selectRiddler.setExpressionVisitor(expressionRiddler);
        updateDeParser.riddle(update);
    }
}
