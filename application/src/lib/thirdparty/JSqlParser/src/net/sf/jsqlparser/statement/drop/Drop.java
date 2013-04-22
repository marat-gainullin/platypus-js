package net.sf.jsqlparser.statement.drop;

import java.util.List;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class Drop implements Statement {

    private String type;
    private String name;
    private List<String> parameters;
    private List<String> parametrsComment;
    private String comment;
    private String endComment = new String();
    private String typeComment;
    private String nameComment;

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getType() {
        return type;
    }

    public void setName(String string) {
        name = string;
    }

    public void setParameters(List<String> list) {
        parameters = list;
    }

    public void setType(String string) {
        type = string;
    }

    @Override
    public String toString() {
        String sql = getComment() != null ? getComment() + " " : "";
        sql += "DROP " + (getTypeComment() != null ? getTypeComment() + " " : "") + type + " "
                + (getNameComment() != null ? getNameComment() + " " : "") + name;

        if (parameters != null && parameters.size() > 0) {
            sql += " " + PlainSelect.getStringListWithComments(parameters, parametrsComment, false, false);
        }
        sql += !"".equals(getEndComment()) ? " " + getEndComment() : "";
        return sql;
    }

    @Override
    public void setEndComment(String endComment) {
        this.endComment = endComment;
    }

    @Override
    public String getEndComment() {
        return endComment;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the typeComment
     */
    public String getTypeComment() {
        return typeComment;
    }

    /**
     * @param typeComment the typeComment to set
     */
    public void setTypeComment(String typeComment) {
        this.typeComment = typeComment;
    }

    /**
     * @return the nameComment
     */
    public String getNameComment() {
        return nameComment;
    }

    /**
     * @param nameComment the nameComment to set
     */
    public void setNameComment(String nameComment) {
        this.nameComment = nameComment;
    }

    /**
     * @return the parametrsComment
     */
    public List<String> getParametrsComment() {
        return parametrsComment;
    }

    /**
     * @param parametrsComment the parametrsComment to set
     */
    public void setParametrsComment(List<String> parametrsComment) {
        this.parametrsComment = parametrsComment;
    }
}
