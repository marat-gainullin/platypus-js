package net.sf.jsqlparser.statement.select;

/**
 * A limit clause in the form [LIMIT {[offset,] row_count) | (row_count | ALL) OFFSET offset}]
 */
public class Limit {

    private long offset;
    private long rowCount;
    private boolean rowCountJdbcParameter = false;
    private boolean offsetJdbcParameter = false;
    private boolean limitAll;
    private boolean comma = false;
    private String commentLimit;
    private String commentOffset;
    private String commentOffsetValue;
    private String commentLimitValue;
    private String commentAll;
    private String commentComma;
    private String commentAfterCommaValue;

    public long getOffset() {
        return offset;
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setOffset(long l) {
        offset = l;
    }

    public void setRowCount(long l) {
        rowCount = l;
    }

    public boolean isOffsetJdbcParameter() {
        return offsetJdbcParameter;
    }

    public boolean isRowCountJdbcParameter() {
        return rowCountJdbcParameter;
    }

    public void setOffsetJdbcParameter(boolean b) {
        offsetJdbcParameter = b;
    }

    public void setRowCountJdbcParameter(boolean b) {
        rowCountJdbcParameter = b;
    }

    /**
     * @return true if the limit is "LIMIT ALL [OFFSET ...])
     */
    public boolean isLimitAll() {
        return limitAll;
    }

    public void setLimitAll(boolean b) {
        limitAll = b;
    }

    public String toString() {
        String retVal = "";
        if (rowCount > 0 || rowCountJdbcParameter || limitAll) {
            if (limitAll) {
                retVal += (getCommentLimit() != null ? " "+getCommentLimit() : "") + " LIMIT " + 
                          (getCommentAll() != null ? getCommentAll()+" " : "") + "ALL";
            } else {
                if (isComma()) {
                    retVal +=(getCommentLimit() != null ? " "+getCommentLimit() : "") + " LIMIT " +  
                             (getCommentLimitValue() != null ? getCommentLimitValue()+" " : "") + (offsetJdbcParameter ? "?" : offset + "");
                    retVal +=(getCommentComma() != null ? " "+getCommentComma()+" " : "") + ", " + 
                             (getCommentAfterCommaValue() != null ? getCommentAfterCommaValue()+" " : "") + (rowCountJdbcParameter ? "?" : rowCount + "");
                    return retVal;
                } else {
                    retVal += (getCommentLimit() != null ? " "+getCommentLimit() : "") + " LIMIT " + 
                              (getCommentLimitValue() != null ? getCommentLimitValue()+" " : "") + (rowCountJdbcParameter ? "?" : rowCount + "");
                }
            }
        }
        if (offset > 0 || offsetJdbcParameter) {
            retVal += (getCommentOffset() != null ? " "+getCommentOffset() : "") + " OFFSET " + 
                      (getCommentOffsetValue() != null ? getCommentOffsetValue()+" " : "") + (offsetJdbcParameter ? "?" : offset + "");
        }
        return retVal;
    }

    /**
     * @return the commentLimit
     */
    public String getCommentLimit() {
        return commentLimit;
    }

    /**
     * @param commentLimit the commentLimit to set
     */
    public void setCommentLimit(String commentLimit) {
        this.commentLimit = commentLimit;
    }

    /**
     * @return the commentOffset
     */
    public String getCommentOffset() {
        return commentOffset;
    }

    /**
     * @param commentOffset the commentOffset to set
     */
    public void setCommentOffset(String commentOffset) {
        this.commentOffset = commentOffset;
    }

    /**
     * @return the commentOffsetValue
     */
    public String getCommentOffsetValue() {
        return commentOffsetValue;
    }

    /**
     * @param commentOffsetValue the commentOffsetValue to set
     */
    public void setCommentOffsetValue(String commentOffsetValue) {
        this.commentOffsetValue = commentOffsetValue;
    }

    /**
     * @return the commentLimitValue
     */
    public String getCommentLimitValue() {
        return commentLimitValue;
    }

    /**
     * @param commentLimitValue the commentLimitValue to set
     */
    public void setCommentLimitValue(String commentLimitValue) {
        this.commentLimitValue = commentLimitValue;
    }

    /**
     * @return the comma
     */
    public boolean isComma() {
        return comma;
    }

    /**
     * @param comma the comma to set
     */
    public void setComma(boolean comma) {
        this.comma = comma;
    }

    /**
     * @return the commentAll
     */
    public String getCommentAll() {
        return commentAll;
    }

    /**
     * @param commentAll the commentAll to set
     */
    public void setCommentAll(String commentAll) {
        this.commentAll = commentAll;
    }

    /**
     * @return the commentComma
     */
    public String getCommentComma() {
        return commentComma;
    }

    /**
     * @param commentComma the commentComma to set
     */
    public void setCommentComma(String commentComma) {
        this.commentComma = commentComma;
    }

    /**
     * @return the commentAfterCommaValue
     */
    public String getCommentAfterCommaValue() {
        return commentAfterCommaValue;
    }

    /**
     * @param commentAfterCommaValue the commentAfterCommaValue to set
     */
    public void setCommentAfterCommaValue(String commentAfterCommaValue) {
        this.commentAfterCommaValue = commentAfterCommaValue;
    }
}
