/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.metadata;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.settings.SettingsConstants;
import com.eas.xml.dom.Source2XmlDom;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import org.w3c.dom.Document;
import sun.misc.UUDecoder;

/**
 *
 * @author mg
 */
public class ApplicationElement {

    // modules
    public static final String SCRIPT_ROOT_TAG_NAME = "script";
    // modules, forms, reports
    public static final String SCRIPT_SOURCE_TAG_NAME = "source";
    // forms
    public static final String FORM_ROOT_TAG_NAME = "form";
    public static final String LAYOUT_TAG_NAME = "layout";
    // reports
    public static final String XLS_LAYOUT_TAG_NAME = "xlsLayout";
    public static final String EXT_TAG_ATTRIBUTE_NAME = "ext";
    // queries
    public final static String QUERY_ROOT_TAG_NAME = "query";
    public final static String SQL_TAG_NAME = "sql";
    public final static String FULL_SQL_TAG_NAME = "fullSql";
    public final static String OUTPUT_FIELDS_TAG_NAME = "outputFields";
    protected String id;
    protected String parentId;
    protected String name;
    protected double order;
    protected int type;
    protected Document content;
    protected byte[] binaryContent;
    protected Long txtCrc32;
    protected Long txtContentLength;

    public ApplicationElement() {
        super();
    }

    private ApplicationElement(ApplicationElement aSource) {
        this();
        id = aSource.getId();
        String sName = aSource.getName();
        name = sName != null ? new String(sName.toCharArray()) : "";
        order = aSource.getOrder();
        type = aSource.getType();
        if (aSource.getContent() != null) {
            content = (Document) aSource.getContent().cloneNode(true);
        } else {
            content = null;
        }
        txtContentLength = aSource.getTxtContentLength();
        txtCrc32 = aSource.getTxtCrc32();
    }

    public String getId() {
        return id;
    }

    public void setId(String aId) {
        id = aId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String aValue) {
        parentId = aValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        name = aValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int aValue) {
        type = aValue;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double aValue) {
        order = aValue;
    }

    public long getTxtCrc32() {
        return txtCrc32 != null ? txtCrc32 : 0;
    }

    public void setTxtCrc32(Long aValue) {
        txtCrc32 = aValue;
    }

    public static long calcTxtCrc32(String aValue) {
        if (aValue != null) {
            try {
                CRC32 crc = new CRC32();
                byte[] bytes = aValue.getBytes(SettingsConstants.COMMON_ENCODING);
                crc.update(bytes);
                return crc.getValue();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ApplicationElement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public void setTxtContent(String aValue) {
        if (aValue != null && !aValue.isEmpty()) {
            content = Source2XmlDom.transform(aValue);
            txtContentLength = Integer.valueOf(aValue.length()).longValue();
            txtCrc32 = calcTxtCrc32(aValue);
        } else {
            content = null;
            txtContentLength = txtCrc32 = (aValue != null) ? 0l : null;
        }
    }

    public Document getContent() {
        return content;
    }

    public void setContent(Document aValue) {
        content = aValue;
    }

    public byte[] getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(byte[] aValue) {
        binaryContent = aValue;
    }

    public long getTxtContentLength() {
        return txtContentLength != null ? txtContentLength : 0;
    }

    public void setTxtContentLength(Long aValue) {
        txtContentLength = aValue;
    }

    public ApplicationElement copy() {
        ApplicationElement appElement = new ApplicationElement(this);
        return appElement;
    }

    public Object copyPropValue(String aPropName) {
        if (ClientConstants.F_MDENT_ID.equalsIgnoreCase(aPropName)) {
            return getId();
        } else if (ClientConstants.F_MDENT_NAME.equalsIgnoreCase(aPropName)) {
            if (name != null) {
                return new String(name.toCharArray());
            } else {
                return null;
            }
        } else if (ClientConstants.F_MDENT_ORDER.equalsIgnoreCase(aPropName)) {
            return getOrder();
        } else if (ClientConstants.F_MDENT_TYPE.equalsIgnoreCase(aPropName)) {
            return getType();
        } else if (ClientConstants.F_MDENT_PARENT_ID.equalsIgnoreCase(aPropName)) {
            return getParentId();
        } else if (ClientConstants.F_MDENT_CONTENT_TXT.equalsIgnoreCase(aPropName)) {
            if (content != null) {
                return content.cloneNode(true);
            } else {
                return null;
            }
        } else if (ClientConstants.F_MDENT_CONTENT_TXT_SIZE.equalsIgnoreCase(aPropName)) {
            if (txtContentLength != null) {
                return txtContentLength;
            } else {
                return 0;
            }
        } else if (ClientConstants.F_MDENT_CONTENT_TXT_CRC32.equalsIgnoreCase(aPropName)) {
            return getTxtCrc32();
        }
        return null;
    }

    public static ApplicationElement read(Rowset aRowset) throws Exception {
        return read(aRowset, null);
    }

    public static ApplicationElement read(Rowset aRowset, ApplicationElement aElement) throws Exception {
        if (aElement == null) {
            aElement = new ApplicationElement();
        }
        Fields lmd = aRowset.getFields();
        if (lmd != null) {
            String txtContent = null;
            byte[] binaryContent = null;
            for (int f = 1; f <= lmd.getFieldsCount(); f++) {
                String colName = lmd.get(f).getName();
                Object colValue = aRowset.getObject(f);
                if (ClientConstants.F_MDENT_ID.equalsIgnoreCase(colName)) {
                    aElement.setId(aRowset.getString(f));
                } else if (ClientConstants.F_MDENT_NAME.equalsIgnoreCase(colName)) {
                    aElement.setName((String) colValue);
                } else if (ClientConstants.F_MDENT_TYPE.equalsIgnoreCase(colName)) {
                    aElement.setType(SQLUtils.extractIntegerFromJDBCObject(colValue));
                } else if (ClientConstants.F_MDENT_CONTENT_TXT_CRC32.equalsIgnoreCase(colName)) {
                    Long dbCrc32 = SQLUtils.extractLongFromJDBCObject(colValue);
                    aElement.setTxtCrc32(dbCrc32 == null ? 0 : dbCrc32);
                } else if (ClientConstants.F_MDENT_PARENT_ID.equalsIgnoreCase(colName)) {
                    aElement.setParentId(aRowset.getString(f));
                } else if (ClientConstants.F_MDENT_CONTENT_TXT.equalsIgnoreCase(colName)) {
                    if (colValue instanceof String) {
                        txtContent = (String) colValue;
                    } else if (colValue instanceof Clob) {
                        Clob clob = (Clob) colValue;
                        txtContent = clob.getSubString(1, (int) clob.length());
                    } else if (colValue instanceof CompactClob) {
                        CompactClob clob = (CompactClob) colValue;
                        txtContent = clob.getData();
                    }
                } else if (ClientConstants.F_MDENT_CONTENT_TXT_SIZE.equalsIgnoreCase(colName)) {
                    aElement.setTxtContentLength(aRowset.getLong(f));
                } else if (ClientConstants.F_MDENT_CONTENT_TXT_CRC32.equalsIgnoreCase(colName)) {
                    aElement.setTxtCrc32(aRowset.getLong(f));
                }
            }
            if (aElement.getType() == ClientConstants.ET_RESOURCE) {
                UUDecoder decoder = new UUDecoder();
                aElement.setBinaryContent(decoder.decodeBuffer(txtContent));
            } else {
                aElement.setTxtContent(txtContent);
            }
        }
        return aElement;
    }
}
