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

import com.eas.util.IDGenerator;
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
                String newValue = casted;
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

    public static boolean isTypeCompatible2JavaClass(int jdbcType, Class<?> aClass) {
        return true;
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

    /*
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
    */
}
