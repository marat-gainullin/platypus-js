package com.eas.client.threetier;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.util.JSONUtils;
import java.sql.Types;
import java.text.SimpleDateFormat;

/**
 *
 * @author mg
 */
public class RowsetJsonWriter {

    private final Rowset rowset;

    public RowsetJsonWriter(Rowset aRowset) {
        super();
        rowset = aRowset;
    }

    public String write() throws Exception {
        return writeRows().toString();
    }

    private StringBuilder writeRows() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int rowIndex = 1; rowIndex <= rowset.size(); rowIndex++) {
            if (rowIndex > 1) {
                sb.append(", ");
            }
            sb.append(writeRow(rowset.getRow(rowIndex)));
        }
        sb.append("]");
        return sb;
    }

    private StringBuilder writeRow(Row aRow) throws Exception {
        StringBuilder sb = new StringBuilder();
        Object[] values = aRow.getCurrentValues();
        Fields fields = rowset.getFields();
        sb.append("{");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(writeValue(values[i], fields.get(i + 1)));
        }
        sb.append("}");
        return sb;
    }

    private StringBuilder writeValue(Object aValue, Field aField) throws Exception {
        String sValue = "null";
        if (aValue != null) {
            sValue = String.valueOf(rowset.getConverter().convert2RowsetCompatible(aValue, DataTypeInfo.VARCHAR));
            switch (aField.getTypeInfo().getSqlType()) {
                case Types.TIME:
                case Types.DATE:
                case Types.TIMESTAMP:
                    sValue = JSONUtils.s((new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT)).format(aValue)).toString();
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
                    sValue = JSONUtils.s(sValue).toString();
                    break;
                case Types.BIT:
                case Types.BOOLEAN:
                    sValue = String.valueOf(aValue);
                    break;
                default:
                    break;
            }
        }
        return JSONUtils.p(aField.getName(), sValue);
    }
}
