package com.eas.server.httpservlet.serial.rowset;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.server.httpservlet.serial.JsonWriter;
import com.eas.server.httpservlet.serial.query.QueryJsonWriter;
import com.eas.util.JSONUtils;
import java.sql.Types;
import java.text.SimpleDateFormat;

/**
 *
 * @author kl, mg refactoring
 */
public class RowsetJsonWriter extends JsonWriter {

    private Rowset rowset;

    public RowsetJsonWriter(Rowset aRowset) {
        rowset = aRowset;
    }

    public String write() throws Exception {
        return writeRows();
    }

    private String writeRows() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int rowIndex = 1; rowIndex <= rowset.size(); rowIndex++) {
            if (rowIndex > 1) {
                sb.append(", ");
            }
            writeRow(sb, rowset.getRow(rowIndex));
        }
        sb.append("]");
        return sb.toString();
    }

    private void writeRow(StringBuilder sb, Row aRow) throws Exception {
        Object[] values = aRow.getCurrentValues();
        Fields fields = rowset.getFields();
        sb.append("{");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            writeValue(sb, values[i], fields.get(i + 1));
        }
        sb.append("}");
    }

    private void writeValue(StringBuilder sb, Object aValue, Field aField) throws Exception {
        String sValue = "null";
        if (aValue != null) {
            sValue = String.valueOf(rowset.getConverter().convert2RowsetCompatible(aValue, DataTypeInfo.VARCHAR));
            switch (aField.getTypeInfo().getSqlType()) {
                case Types.TIME:
                case Types.DATE:
                case Types.TIMESTAMP:
                    SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
                    sValue = JSONUtils.s(sdf.format(aValue));
                    break;
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                case Types.CLOB:
                case Types.NCLOB:
                case Types.OTHER:
                case Types.STRUCT:
                    sValue = JSONUtils.s(sValue);
                default:
                    break;
            }
        }
        JSONUtils.p(sb, aField.getName(), sValue);
    }
}
