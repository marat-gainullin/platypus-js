/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.bearsoft.rowset.utils;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Field;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

/**
 *
 * @author mg
 */
public class RowsetUtils {

    public static Object generatePkValueByType(int colType) {
        switch (colType) {
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
                return IDGenerator.genID();
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
                return String.valueOf(IDGenerator.genID());
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                return new Date(IDGenerator.genID());
            case java.sql.Types.BIT:
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.NULL:
            case java.sql.Types.OTHER:
            case java.sql.Types.JAVA_OBJECT:
            case java.sql.Types.DISTINCT:
            case java.sql.Types.STRUCT:
            case java.sql.Types.ARRAY:
            case java.sql.Types.BLOB:
            case java.sql.Types.CLOB:
            case java.sql.Types.REF:
            case java.sql.Types.DATALINK:
            case java.sql.Types.ROWID:
            case java.sql.Types.SQLXML:
            case java.sql.Types.BOOLEAN:
            case java.sql.Types.NCLOB:
                break;
        }
        assert false;
        return null;
    }

    public static Object cloneFieldValue(Object aValue) {
        if (aValue != null) {
            if (aValue instanceof BigDecimal) {
                BigDecimal casted = (BigDecimal) aValue;
                BigDecimal newValue = casted.add(BigDecimal.ZERO);
                return newValue;
            } else if (aValue instanceof Boolean) {
                Boolean casted = (Boolean) aValue;
                Boolean newValue = casted.booleanValue();
                return newValue;
            } else if (aValue instanceof Byte) {
                Byte casted = (Byte) aValue;
                Byte newValue = new Byte(casted.byteValue());
                return newValue;
            } else if (aValue instanceof Double) {
                Double casted = (Double) aValue;
                Double newValue = new Double(casted.doubleValue());
                return newValue;
            } else if (aValue instanceof Float) {
                Float casted = (Float) aValue;
                Float newValue = new Float(casted.floatValue());
                return newValue;
            } else if (aValue instanceof Integer) {
                Integer casted = (Integer) aValue;
                Integer newValue = new Integer(casted.intValue());
                return newValue;
            } else if (aValue instanceof Long) {
                Long casted = (Long) aValue;
                Long newValue = new Long(casted.longValue());
                return newValue;
            } else if (aValue instanceof Short) {
                Short casted = (Short) aValue;
                Short newValue = new Short(casted.shortValue());
                return newValue;
            } else if (aValue instanceof Time) {
                Time casted = (Time) aValue;
                Time newValue = new Time(casted.getTime());
                return newValue;
            } else if (aValue instanceof Timestamp) {
                Timestamp casted = (Timestamp) aValue;
                Timestamp newValue = new Timestamp(casted.getTime());
                return newValue;
            } else if (aValue instanceof Date) {
                Date casted = (Date) aValue;
                Date newValue = new Date(casted.getTime());
                return newValue;
            } else if (aValue instanceof String) {
                String casted = (String) aValue;
                String newValue = new String(casted.toCharArray());
                return newValue;
            } else if (aValue instanceof URL) {
                try {
                    URL casted = (URL) aValue;
                    URL newValue = new URL(casted.toExternalForm());
                    return newValue;
                } catch (MalformedURLException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (aValue instanceof Array) {
                try {
                    Array casted = (Array) aValue;
                    Array newValue = new SerialArray(casted);
                    return newValue;
                } catch (SerialException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (aValue instanceof Blob) {
                try {
                    Blob casted = (Blob) aValue;
                    Blob newValue = null;
                    if (casted.length() > 0) {
                        newValue = new SerialBlob(casted);
                    }
                    return newValue;
                } catch (SerialException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (aValue instanceof Clob) {
                try {
                    Clob casted = (Clob) aValue;
                    Clob newValue = null;
                    if (casted.length() > 0) {
                        newValue = new SerialClob(casted);
                    }
                    return newValue;
                } catch (SerialException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else //            if(aValue instanceof NClob) NClob - is the Clob!!!
            //            {
            //                NClob casted = (NClob)aValue;
            //                NClob newValue = new SerialNClob();
            //            }else
            if (aValue instanceof SQLXML) {
                try {
                    SQLXML casted = (SQLXML) aValue;
                    String xmlValue = casted.getString();
                    if (xmlValue != null) {
                        Clob newValue = new SerialClob(xmlValue.toCharArray());
                        return newValue;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RowsetUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public static BigDecimal number2BigDecimal(Number aNumber) {
        if (aNumber instanceof Float || aNumber instanceof Double) {
            return new BigDecimal(aNumber.doubleValue());
        } else if (aNumber instanceof BigInteger) {
            return new BigDecimal((BigInteger) aNumber);
        } else if (aNumber instanceof BigDecimal) {
            return (BigDecimal) aNumber;
        } else {
            return new BigDecimal(aNumber.longValue());
        }
    }
    public static final int INOPERABLE_TYPE_MARKER = 0;
    public static final Object UNDEFINED_SQL_VALUE = new Object();
    public static final String SQL_FALSE_CONDITION = " where 1=0";
    public static final String SQL_2_METADTA_TAIL = "t01010101" + SQL_FALSE_CONDITION;
    public static final String SQL_2_METADTA = "select * from ( %s ) " + SQL_2_METADTA_TAIL;
    public static final Map<Integer, String> typesNames = new HashMap<>();

    static {
        typesNames.put(java.sql.Types.ARRAY, "ARRAY");
        typesNames.put(java.sql.Types.BIGINT, "BIGINT");
        typesNames.put(java.sql.Types.BINARY, "BINARY");
        typesNames.put(java.sql.Types.BIT, "BIT");
        typesNames.put(java.sql.Types.BLOB, "BLOB");
        typesNames.put(java.sql.Types.BOOLEAN, "BOOLEAN");
        typesNames.put(java.sql.Types.CHAR, "CHAR");
        typesNames.put(java.sql.Types.CLOB, "CLOB");
        typesNames.put(java.sql.Types.DATALINK, "DATALINK");
        typesNames.put(java.sql.Types.DATE, "DATE");
        typesNames.put(java.sql.Types.DECIMAL, "DECIMAL");
        typesNames.put(java.sql.Types.DISTINCT, "DISTINCT");
        typesNames.put(java.sql.Types.DOUBLE, "DOUBLE");
        typesNames.put(java.sql.Types.FLOAT, "FLOAT");
        typesNames.put(java.sql.Types.INTEGER, "INTEGER");
        typesNames.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
        typesNames.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
        typesNames.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
        typesNames.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
        typesNames.put(java.sql.Types.NCHAR, "NCHAR");
        typesNames.put(java.sql.Types.NCLOB, "NCLOB");
        typesNames.put(java.sql.Types.NULL, "NULL");
        typesNames.put(java.sql.Types.NUMERIC, "NUMERIC");
        typesNames.put(java.sql.Types.NVARCHAR, "NVARCHAR");
        typesNames.put(java.sql.Types.OTHER, "OTHER");
        typesNames.put(java.sql.Types.REAL, "REAL");
        typesNames.put(java.sql.Types.REF, "REF");
        typesNames.put(java.sql.Types.ROWID, "ROWID");
        typesNames.put(java.sql.Types.SMALLINT, "SMALLINT");
        typesNames.put(java.sql.Types.SQLXML, "SQLXML");
        typesNames.put(java.sql.Types.STRUCT, "STRUCT");
        typesNames.put(java.sql.Types.TIME, "TIME");
        typesNames.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
        typesNames.put(java.sql.Types.TINYINT, "TINYINT");
        typesNames.put(java.sql.Types.VARBINARY, "VARBINARY");
        typesNames.put(java.sql.Types.VARCHAR, "VARCHAR");
    }

    public static boolean isTypeCompatible2JavaClass(int jdbcType, Class<?> aClass) {
        return true;
    }

    public static String getTypeName(int type) {
        return typesNames.get(type);
    }

    /**
     * Reads data from an abstract stream up to the length or up to the end of stream.
     * @param is Input stream to read.
     * @param length Length of segment to be read. If length == -1, than reading is performed until the end of the stream.
     * @return Byte array containing data read from stream.
     * @throws IOException
     */
    public static byte[] readStream(InputStream is, int length) throws IOException {
        byte[] buffer = new byte[64];
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        int read = 0;
        while ((read = is.read(buffer)) != -1) {
            if (length < 0 || res.size() + read <= length) {
                res.write(buffer, 0, read);
            } else {
                res.write(buffer, 0, read - (res.size() + read - length));
                break;
            }
        }
        res.flush();
        byte[] bytes = res.toByteArray();
        assert length < 0 || bytes.length == length;
        return bytes;
    }

    /**
     * Reads string data from an abstract reader up to the length or up to the end of the reader.
     * @param aReader Reader to read from.
     * @param length Length of segment to be read. It length == -1, than reading is performed until the end of Reader.
     * @return String, containing data read from Reader.
     * @throws IOException
     */
    public static String readReader(Reader aReader, int length) throws IOException {
        char[] buffer = new char[32];
        StringWriter res = new StringWriter();
        int read = 0;
        int written = 0;
        while ((read = aReader.read(buffer)) != -1) {
            if (length < 0 || written + read <= length) {
                res.write(buffer, 0, read);
                written += read;
            } else {
                res.write(buffer, 0, read - (written + read - length));
                written += length - (written + read);
                break;
            }
        }
        res.flush();
        String str = res.toString();
        assert length < 0 || str.length() == length;
        return str;
    }

    public static String makeQueryMetadataQuery(String sql) {
        if (sql != null && !sql.isEmpty()) {
            if (!sql.endsWith(SQL_2_METADTA_TAIL)) {
                String lsql = sql.toLowerCase().replaceAll("[\n\r]", "");
                if (lsql.matches(".+\\bwhere\\b.+")
                        // complex queries
                        || lsql.matches(".+\\border\\b.+")
                        || lsql.matches(".+\\bgroup\\b.+")
                        || lsql.matches(".+\\bconnect\\b.+")) {
                    return String.format(SQL_2_METADTA, sql);
                } else // simple queries
                {
                    return sql + SQL_FALSE_CONDITION;
                }
            } else {// bypass
                return sql;
            }
        }
        return "";
    }

    public static Locator generatePkLocator(Rowset aRowset) {
        List<Field> pks = aRowset.getFields().getPrimaryKeys();
        Locator res = aRowset.createLocator();
        res.beginConstrainting();
        try {
            for (Field pk : pks) {
                res.addConstraint(aRowset.getFields().find(pk.getName()));
            }
        } finally {
            res.endConstrainting();
        }
        return res;
    }
}
