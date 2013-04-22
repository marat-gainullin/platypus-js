package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class CreateTableDeParser {

    protected StringBuilder buffer;

    /**
     * @param aBuffer the buffer that will be filled with the select
     */
    public CreateTableDeParser(StringBuilder aBuffer) {
        buffer = aBuffer;
    }

    public void deParse(CreateTable aCreateTable) {
        buffer.append(aCreateTable.getComment() != null ? aCreateTable.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Create");
        for (int i = 0; i < aCreateTable.getCreateOptions().size(); i++) {
            buffer.append(!"".equals(aCreateTable.getCommentCreateOptions().get(i)) ? " " + aCreateTable.getCommentCreateOptions().get(i) + " " + ExpressionDeParser.LINE_SEPARATOR : "");
            buffer.append(aCreateTable.getCreateOptions().get(i));
        }
        buffer.append(aCreateTable.getCommentTable() != null ? " " + aCreateTable.getCommentTable() + ExpressionDeParser.LINE_SEPARATOR : "").append(" table ").append(aCreateTable.getComment() != null ? aCreateTable.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(aCreateTable.getTable().getWholeTableName());
        if (aCreateTable.getColumnDefinitions() != null) {
            buffer.append(aCreateTable.getCommentBeginBracket() != null ? " " + aCreateTable.getCommentBeginBracket() + ExpressionDeParser.LINE_SEPARATOR : "").append(" ( ");

            for (int i = 0; i < aCreateTable.getColumnDefinitions().size(); i++) {
                ColumnDefinition columnDefinition = (ColumnDefinition) aCreateTable.getColumnDefinitions().get(i);
                buffer.append(columnDefinition.getCommentName() != null ? columnDefinition.getCommentName() + " " + ExpressionDeParser.LINE_SEPARATOR : "");
                buffer.append(columnDefinition.getColumnName());
                buffer.append(" ");
                buffer.append(columnDefinition.getColDataType().toString());

                if (columnDefinition.getColumnSpecStrings() != null) {
                    for (int j = 0; j < columnDefinition.getColumnSpecStrings().size(); j++) {
                        buffer.append(" ");
                        buffer.append(!"".equals(columnDefinition.getCommentsSpec().get(i)) ? columnDefinition.getCommentsSpec().get(i) + " " + ExpressionDeParser.LINE_SEPARATOR : "");
                        buffer.append((String) columnDefinition.getColumnSpecStrings().get(i));
                    }
                }

                if (i < aCreateTable.getColumnDefinitions().size() - 1) {
                    buffer.append(!"".equals(aCreateTable.getCommentCommaIndexes().get(i)) ? " " + aCreateTable.getCommentCommaIndexes().get(i) + ExpressionDeParser.LINE_SEPARATOR : "").append(", ");
                }
            }
            int shift = aCreateTable.getColumnDefinitions().size() - 1;
            for (int i = 0; i < aCreateTable.getIndexes().size(); i++) {
                buffer.append(!"".equals(aCreateTable.getCommentCommaIndexes().get(i + shift)) ? aCreateTable.getCommentCommaIndexes().get(i + shift) + " " + ExpressionDeParser.LINE_SEPARATOR : "");
                buffer.append(",").append(ExpressionDeParser.LINE_SEPARATOR);
                Index index = (Index) aCreateTable.getIndexes().get(i);
                buffer.append(index.toString());
            }
            buffer.append(" ").append(aCreateTable.getCommentEndBracket() != null ? aCreateTable.getCommentEndBracket() + " " : "").append(ExpressionDeParser.LINE_SEPARATOR).append(") ");
        }

        for (int i = 0; i < aCreateTable.getTableOptionsStrings().size(); i++) {
            buffer.append(!"".equals(aCreateTable.getCommentTableOptions().get(i)) ? aCreateTable.getCommentTableOptions().get(i) + " " + ExpressionDeParser.LINE_SEPARATOR : "");
            buffer.append(aCreateTable.getTableOptionsStrings().get(i));
            buffer.append(" ");
        }
        buffer.append(!"".equals(aCreateTable.getEndComment()) ? " " + aCreateTable.getEndComment() : "");
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder aBuffer) {
        buffer = aBuffer;
    }
}
