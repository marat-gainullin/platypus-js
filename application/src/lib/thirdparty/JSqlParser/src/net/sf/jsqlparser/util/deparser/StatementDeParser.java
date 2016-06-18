package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class StatementDeParser implements StatementVisitor {

    protected StringBuilder buffer;
    protected SelectDeParser selectDeParser;
    protected ExpressionDeParser expressionDeParser;

    public StatementDeParser(StringBuilder aBuffer) {
        super();
        buffer = aBuffer;
        selectDeParser = new SelectDeParser(aBuffer);
        expressionDeParser = new ExpressionDeParser(selectDeParser, aBuffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
    }

    public StatementDeParser(StringBuilder aBuffer, SelectDeParser aSelectDeParser, ExpressionDeParser aExpressionDeParser) {
        super();
        buffer = aBuffer;
        selectDeParser = aSelectDeParser;
        expressionDeParser = aExpressionDeParser;
    }

    @Override
    public void visit(CreateTable createTable) {
        CreateTableDeParser createTableDeParser = new CreateTableDeParser(buffer);
        createTableDeParser.deParse(createTable);
    }

    @Override
    public void visit(Delete delete) {
        DeleteDeParser deleteDeParser = new DeleteDeParser(expressionDeParser, buffer);
        deleteDeParser.deParse(delete);
    }

    @Override
    public void visit(Drop drop) {
        DropDeParser dropDeParser = new DropDeParser(expressionDeParser, buffer);
        dropDeParser.deParse(drop);
    }

    @Override
    public void visit(Insert insert) {
        InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, buffer);
        insertDeParser.deParse(insert);

    }

    @Override
    public void visit(Replace replace) {
        ReplaceDeParser replaceDeParser = new ReplaceDeParser(expressionDeParser, selectDeParser, buffer);
        replaceDeParser.deParse(replace);
    }

    @Override
    public void visit(Select select) {
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            buffer.append(select.getCommentWith() != null ? select.getCommentWith() + " " : "").append(ExpressionDeParser.EOL).append("With ");

            for (int i = 0; i < select.getWithItemsList().size(); i++) {
                WithItem withItem = (WithItem) select.getWithItemsList().get(i);
                buffer.append(withItem);
                buffer.append((i < select.getWithItemsList().size() - 1) ? (!"".equals(select.getCommentsComma().get(i)) ? " " + select.getCommentsComma().get(i) + ExpressionDeParser.EOL : "") + "," : "")
                        .append(ExpressionDeParser.EOL).append(" ");
            }
        }
        select.getSelectBody().accept(selectDeParser);
        buffer.append(!"".equals(select.getEndComment()) ? " " + select.getEndComment() : "");
    }

    @Override
    public void visit(Truncate truncate) {
        TruncateDeParser truncateDeParser = new TruncateDeParser(expressionDeParser, buffer);
        truncateDeParser.deParse(truncate);
    }

    @Override
    public void visit(Update update) {
        UpdateDeParser updateDeParser = new UpdateDeParser(expressionDeParser, buffer);
        updateDeParser.deParse(update);
    }

    public StringBuilder getBuffer() {
        return buffer;
    }
}
