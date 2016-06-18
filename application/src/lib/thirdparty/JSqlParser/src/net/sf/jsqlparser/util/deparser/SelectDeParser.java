package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Top;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.select.UnionTypes;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.select.Select}
 */
public class SelectDeParser implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;

    public SelectDeParser() {
    }

    /**
     * @param aBuilder the buffer that will be filled with the select
     */
    public SelectDeParser(StringBuilder aBuilder) {
        buffer = aBuilder;
    }

    /**
     * @param aExpressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param aBuilder the buffer that will be filled with the select
     */
    public SelectDeParser(ExpressionVisitor aExpressionVisitor, StringBuilder aBuilder) {
        buffer = aBuilder;
        expressionVisitor = aExpressionVisitor;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        buffer.append(plainSelect.getComment() != null ? plainSelect.getComment()+" "+ExpressionDeParser.EOL : "").append("Select ");
        Top top = plainSelect.getTop();
        if (top != null) {
            top.toString();
        }
        if (plainSelect.getDistinct() != null) {
            buffer.append(plainSelect.getDistinct().getComment() != null ? plainSelect.getDistinct().getComment()+" "+ExpressionDeParser.EOL : "").append("Distinct ");
            if (plainSelect.getDistinct().getOnSelectItems() != null) {
                buffer.append(plainSelect.getDistinct().getCommentOn() != null ? plainSelect.getDistinct().getCommentOn()+" "+ExpressionDeParser.EOL : "").append("on")
                      .append(plainSelect.getDistinct().getCommentBeginBracket() != null ? " "+plainSelect.getDistinct().getCommentBeginBracket()+ExpressionDeParser.EOL : "").append(" (");
                for (int i = 0; i < plainSelect.getDistinct().getOnSelectItems().size(); i++) {
                    SelectItem selectItem = (SelectItem) plainSelect.getDistinct().getOnSelectItems().get(i);
                    selectItem.accept(this);
                    if (i < plainSelect.getDistinct().getOnSelectItems().size() - 1) {
                        buffer.append(!"".equals(plainSelect.getDistinct().getCommentsComma().get(i)) ? " " + plainSelect.getDistinct().getCommentsComma().get(i)+ExpressionDeParser.EOL : "")
                              .append(", ");
                    }
                }
                buffer.append(plainSelect.getDistinct().getCommentEndBracket() != null ? plainSelect.getDistinct().getCommentEndBracket()+" "+ExpressionDeParser.EOL : "").append(") ");
            }

        }

        int selectItemsCounter = 0;
        for (int i = 0, s = plainSelect.getSelectItems().size(); i < s; i++) {
           SelectItem selectItem = (SelectItem) plainSelect.getSelectItems().get(i);
           selectItem.accept(this);
           if (i < plainSelect.getSelectItems().size() - 1) {
                buffer.append(!plainSelect.getCommentCommaItems().get(i).toString().isEmpty()?" "+plainSelect.getCommentCommaItems().get(i)+" ":"");
                if (selectItemsCounter++ == 2) {
                    selectItemsCounter = 0;
                    buffer.append(ExpressionDeParser.EOL).append(", ");
                } else {
                    buffer.append(", ");
                }
            }
        }
        buffer.append(" ");

        if (plainSelect.getFromItem() != null) {
            buffer.append(plainSelect.getCommentFrom() != null ? plainSelect.getCommentFrom()+" " : "")
                  .append(ExpressionDeParser.EOL).append("From ");
            plainSelect.getFromItem().accept(this);
        }

        if (plainSelect.getJoins() != null) {
            for (Iterator iter = plainSelect.getJoins().iterator(); iter.hasNext();) {
                Join join = (Join) iter.next();
                buffer.append(ExpressionDeParser.EOL);
                deparseJoin(join);
            }
        }

        if (plainSelect.getWhere() != null) {
            buffer.append(plainSelect.getCommentWhere() != null ? " "+plainSelect.getCommentWhere() : "")
                  .append(ExpressionDeParser.EOL).append(" Where ");
            plainSelect.getWhere().accept(expressionVisitor);
        }

        if (plainSelect.getConnect() != null) {
            plainSelect.getConnect().accept(expressionVisitor);
        }

        if (plainSelect.getGroupByColumnReferences() != null) {
            buffer.append(plainSelect.getCommentGroup() != null ? " "+plainSelect.getCommentGroup() : "")
                  .append(ExpressionDeParser.EOL).append(" Group")
                  .append(plainSelect.getCommentGroupBy() != null ? " "+plainSelect.getCommentGroupBy()+ExpressionDeParser.EOL : "").append(" by ");
            for (int i = 0; i < plainSelect.getGroupByColumnReferences().size(); i++) {
                Expression columnReference = (Expression) plainSelect.getGroupByColumnReferences().get(i);
                columnReference.accept(expressionVisitor);
                if (i < plainSelect.getGroupByColumnReferences().size() - 1) {
                    buffer.append(!"".equals(plainSelect.getCommentCommaGroupBy().get(i)) ? " " + plainSelect.getCommentCommaGroupBy().get(i)+ExpressionDeParser.EOL : "")
                          .append(", ");
                }
            }

        }

        if (plainSelect.getHaving() != null) {
            buffer.append(plainSelect.getCommentHaving() != null ? " "+plainSelect.getCommentHaving() : "")
                  .append(ExpressionDeParser.EOL).append(" Having ");
            plainSelect.getHaving().accept(expressionVisitor);
        }

        if (plainSelect.getOrderByElements() != null) {
            buffer.append(plainSelect.getCommentOrder() != null ? " "+plainSelect.getCommentOrder() : "")
                  .append(ExpressionDeParser.EOL).append(" Order")
                  .append(plainSelect.getCommentOrderBy() != null ? " "+plainSelect.getCommentOrderBy()+ExpressionDeParser.EOL : "").append(" by ");
            for (int i = 0; i < plainSelect.getOrderByElements().size(); i++) {
                OrderByElement orderByElement = (OrderByElement) plainSelect.getOrderByElements().get(i);
                orderByElement.accept(this);
                if (i < plainSelect.getOrderByElements().size() - 1) {
                    buffer.append(!"".equals(plainSelect.getCommentCommaOrderBy().get(i)) ? " " + plainSelect.getCommentCommaOrderBy().get(i)+ExpressionDeParser.EOL : "")
                          .append(", ");
                }
            }
        }

        if (plainSelect.getLimit() != null) {
            deparseLimit(plainSelect.getLimit());
        }
    }

    public void visit(Union union) {
        for (int i = 0; i<union.getPlainSelects().size(); i++) {
            assert union.getTypeOperations().size() == union.getPlainSelects().size()-1; 
            if (union.getCommentsBeginBracket() != null) {
              buffer.append(!union.getCommentsBeginBracket().get(i).toString().isEmpty()?union.getCommentsBeginBracket().get(i)+" "+ExpressionDeParser.EOL:"").append("(");
            }
            PlainSelect plainSelect = (PlainSelect) union.getPlainSelects().get(i);
            plainSelect.accept(this);
            if (union.getCommentsEndBracket() != null) {
              buffer.append(!union.getCommentsEndBracket().get(i).toString().isEmpty()?union.getCommentsEndBracket().get(i)+" "+ExpressionDeParser.EOL:"").append(")");
            }
            if (i<union.getPlainSelects().size()-1) {
                buffer.append(ExpressionDeParser.EOL).append((UnionTypes) union.getTypeOperations().get(i)).append(ExpressionDeParser.EOL);
            } else {
                buffer.append(ExpressionDeParser.EOL);
            }
        }

        if (union.getOrderByElements() != null) {
            buffer.append(union.getCommentOrder() != null ? " "+union.getCommentOrder() : "")
                  .append(ExpressionDeParser.EOL).append(" Order")
                  .append(union.getCommentOrderBy() != null ? " "+union.getCommentOrderBy()+ExpressionDeParser.EOL : "").append(" by ");
            for (int i = 0; i < union.getOrderByElements().size(); i++) {
                OrderByElement orderByElement = (OrderByElement) union.getOrderByElements().get(i);
                orderByElement.accept(this);
                if (i < union.getOrderByElements().size() - 1) {
                    buffer.append(!"".equals(union.getCommentCommaOrderBy().get(i)) ? " " + union.getCommentCommaOrderBy().get(i)+ExpressionDeParser.EOL : "")
                          .append(", ");
                }
            }
        }

        if (union.getLimit() != null) {
            deparseLimit(union.getLimit());
        }
    }

    public void visit(OrderByElement orderBy) {
        orderBy.getExpression().accept(expressionVisitor);
        if (orderBy.isAsc()) {
            buffer.append(orderBy.getComment() != null ? " "+orderBy.getComment()+ExpressionDeParser.EOL : "").append(" asc");
        } 
        if (orderBy.isDesc()) {
            buffer.append(orderBy.getComment() != null ? " "+orderBy.getComment()+ExpressionDeParser.EOL : "").append(" desc");
        }
    }

    public void visit(Column column) {
        buffer.append(column.getComment() != null ? column.getComment()+" "+ExpressionDeParser.EOL : "").append(column.getWholeColumnName());
    }

    public void visit(AllColumns allColumns) {
        buffer.append(allColumns.getComment() != null ? allColumns.getComment()+" "+ExpressionDeParser.EOL : "").append("*");
    }

    public void visit(AllTableColumns allTableColumns) {
        buffer.append(allTableColumns.getTable().getWholeTableName()).append(".*");
    }

    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(expressionVisitor);
        if (selectExpressionItem.getAlias() != null) {
            buffer.append(" ").append(selectExpressionItem.getAlias().toString());
        }

    }

    public void visit(SubSelect subSelect) {
        buffer.append(subSelect.getCommentBeginBracket() != null ? subSelect.getCommentBeginBracket()+" "+ExpressionDeParser.EOL : "").append("(");
        subSelect.getSelectBody().accept(this);
        buffer.append(subSelect.getCommentEndBracket() != null ? subSelect.getCommentEndBracket()+" "+ExpressionDeParser.EOL : "").append(")");
        String alias = subSelect.getAlias() != null ? subSelect.getAlias().toString() : "";
        if (alias != null && !alias.isEmpty()) {
            buffer.append(" ").append(alias);// it's very strange, but in fact oracle doesn't permit as key word if form clause
//            buffer.append(" as ").append(alias);
        }
    }

    public void visit(Table aTable) {
        buffer.append(aTable.getComment() != null ? aTable.getComment()+" "+ExpressionDeParser.EOL : "").append(aTable.getWholeTableName());
        String alias =aTable.getAlias() != null ? aTable.getAlias().toString() : "";
        if (alias != null && !alias.isEmpty()) {
            buffer.append(" ").append(alias);// it's very strange, but in fact oracle doesn't permit as key word if form clause
//            buffer.append(" as ").append(alias);
        }
    }

    public void deparseOrderBy(List orderByElements) {
        buffer.append(ExpressionDeParser.EOL).append(" Order by ");
        for (Iterator iter = orderByElements.iterator(); iter.hasNext();) {
            OrderByElement orderByElement = (OrderByElement) iter.next();
            orderByElement.accept(this);
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }
    }

    public void deparseLimit(Limit limit) {
        // LIMIT n OFFSET skip
        buffer.append(ExpressionDeParser.EOL)
              .append(limit.getCommentLimit() != null ? " "+limit.getCommentLimit()+ExpressionDeParser.EOL : "").append(" Limit");
        if (limit.isLimitAll()) {
           buffer.append(limit.getCommentAll() != null ? " "+limit.getCommentAll()+ExpressionDeParser.EOL : "").append(" All ");
        }
        buffer.append(limit.getCommentLimitValue() != null ? limit.getCommentLimitValue()+" "+ExpressionDeParser.EOL : "");
   
        if (limit.isComma()){
            if (limit.isOffsetJdbcParameter()) {
                buffer.append("?");
            } else if (limit.getOffset() != 0) {
                buffer.append(limit.getOffset());
            }
            
            buffer.append(limit.getCommentComma() != null ? limit.getCommentComma()+" "+ExpressionDeParser.EOL : "").append(", ")
                  .append(limit.getCommentAfterCommaValue() != null ? limit.getCommentAfterCommaValue()+" "+ExpressionDeParser.EOL : "");
            if (limit.isRowCountJdbcParameter()) {
                buffer.append("?");
            } else if (limit.getRowCount() != 0) {
                buffer.append(limit.getRowCount());
            }
        } else {
            if (limit.isRowCountJdbcParameter()) {
                buffer.append("?");
            } else {
                if (limit.getRowCount() != 0) {
                    buffer.append(limit.getRowCount());
                 } else {
                    /*
                    from mysql docs:
                    For compatibility with PostgreSQL, MySQL also supports the LIMIT row_count OFFSET offset syntax.
                    To retrieve all rows from a certain offset up to the end of the result set, you can use some large number
                    for the second parameter.
                     */
                    buffer.append("18446744073709551615");
                }
            }    
        }

        if (limit.isOffsetJdbcParameter()) {
            buffer.append(limit.getCommentOffset() != null ? " "+limit.getCommentOffset()+ExpressionDeParser.EOL : "").append(" Offset")
                  .append(limit.getCommentOffsetValue() != null ? " "+limit.getCommentOffsetValue()+ExpressionDeParser.EOL : "").append(" ?");
        } else if (limit.getOffset() != 0) {
            buffer.append(limit.getCommentOffset() != null ? " "+limit.getCommentOffset()+ExpressionDeParser.EOL : "").append(" Offset ")
                  .append(limit.getCommentOffsetValue() != null ? " "+limit.getCommentOffsetValue()+" "+ExpressionDeParser.EOL : "").append(limit.getOffset());
        }

    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public void visit(SubJoin subjoin) {
        buffer.append(subjoin.getCommentBeginBracket() != null ? subjoin.getCommentBeginBracket()+" "+ExpressionDeParser.EOL : "").append("(");
        subjoin.getLeft().accept(this);
        buffer.append(" ");
        deparseJoin(subjoin.getJoin());
        buffer.append(subjoin.getCommentEndBracket() != null ? subjoin.getCommentEndBracket()+" "+ExpressionDeParser.EOL : "").append(")");
    }

    public void deparseJoin(Join join) {
        if (join.isSimple()) {
            buffer.append(join.getComment() != null ? join.getComment()+" "+ExpressionDeParser.EOL : "").append(", ");
        } else {

            buffer.append(" ");
            if (join.isRight()) {
                buffer.append(join.getCommentRight() != null ? join.getCommentRight()+" "+ExpressionDeParser.EOL : "").append("Right ");
            } else if (join.isNatural()) {
                buffer.append(join.getCommentNatural() != null ? join.getCommentNatural()+" "+ExpressionDeParser.EOL : "").append("Natural ");
            } else if (join.isFull()) {
                buffer.append(join.getCommentFull() != null ? join.getCommentFull()+" "+ExpressionDeParser.EOL : "").append("Full ");
            } else if (join.isLeft()) {
                buffer.append(join.getCommentLeft() != null ? join.getCommentLeft()+" "+ExpressionDeParser.EOL : "").append("Left ");
            }

            if (join.isOuter()) {
                buffer.append(join.getCommentOuter() != null ? join.getCommentOuter()+" "+ExpressionDeParser.EOL : "").append("Outer ");
            } else if (join.isInner()) {
                buffer.append(join.getCommentInner() != null ? join.getCommentInner()+" "+ExpressionDeParser.EOL : "").append("Inner ");
            }

            buffer.append(join.getCommentJoin() != null ? join.getCommentJoin()+" "+ExpressionDeParser.EOL : "").append("Join ");

        }

        FromItem fromItem = join.getRightItem();
        fromItem.accept(this);
        if (join.getOnExpression() != null) {
            buffer.append(join.getCommentOn() != null ? " "+join.getCommentOn()+ExpressionDeParser.EOL : "").append(" on ");
            join.getOnExpression().accept(expressionVisitor);
        }
        if (join.getUsingColumns() != null) {
            buffer.append(join.getCommentUsing() != null ? " "+join.getCommentUsing()+ExpressionDeParser.EOL : "").append(" Using")
                  .append(join.getCommentBeginBracket() != null ? " "+join.getCommentBeginBracket()+ExpressionDeParser.EOL : "").append(" ( ");
            for (int i = 0, s = join.getUsingColumns().size(); i < s; i++) {
                Column column = (Column) join.getUsingColumns().get(i);
                buffer.append(column.getComment() != null ? column.getComment()+" "+ExpressionDeParser.EOL : "").append(column.getWholeColumnName());
                if (i < join.getUsingColumns().size() - 1) {
                    buffer.append(!join.getCommentComma().get(i).toString().isEmpty()?" "+join.getCommentComma().get(i)+" "+ExpressionDeParser.EOL:"");
                    buffer.append(", ");
                }
            }
            buffer.append(join.getCommentEndBracket() != null ? join.getCommentEndBracket()+" "+ExpressionDeParser.EOL : "").append(")");
        }
    }
}
