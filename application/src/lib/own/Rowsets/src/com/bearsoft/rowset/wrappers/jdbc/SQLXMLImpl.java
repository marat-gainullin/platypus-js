/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactSqlXml;
import com.bearsoft.rowset.compacts.CompactSqlXmlOutputStream;
import com.bearsoft.rowset.compacts.CompactSqlXmlWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * JDBC wrapper for <code>CompactSqlXml</code>, implementing SQLXML interface.
 * @author mg
 * @see SQLXML
 */
public class SQLXMLImpl implements SQLXML {

    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected CompactSqlXml delegate = null;

    /**
     * Simple constructor
     * @param aCompactSqlXml A <code>CompactSqlXml</code> instance to wrapped.
     */
    public SQLXMLImpl(CompactSqlXml aCompactSqlXml) {
        super();
        delegate = aCompactSqlXml;
    }

    /**
     * @inheritDoc
     */
    public void free() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream() throws SQLException {
        try {
            return new ByteArrayInputStream(delegate.getData().getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public OutputStream setBinaryStream() throws SQLException {
        return new CompactSqlXmlOutputStream(delegate);
    }

    /**
     * @inheritDoc
     */
    public Reader getCharacterStream() throws SQLException {
        return new StringReader(delegate.getData());
    }

    /**
     * @inheritDoc
     */
    public Writer setCharacterStream() throws SQLException {
        return new CompactSqlXmlWriter(delegate);
    }

    /**
     * @inheritDoc
     */
    public String getString() throws SQLException {
        return delegate.getData();
    }

    /**
     * @inheritDoc
     */
    public void setString(String value) throws SQLException {
        delegate.setData(value);
    }

    /**
     * @inheritDoc
     */
    public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
        try {
            if (DOMSource.class.isAssignableFrom(sourceClass)) {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(delegate.getData())));
                DOMSource source = new DOMSource(doc.getDocumentElement());
                return (T) source;
            } else if (SAXSource.class.isAssignableFrom(sourceClass)) {
                SAXSource source = new SAXSource(new InputSource(new StringReader(delegate.getData())));
                return (T) source;
            } else if (StreamSource.class.isAssignableFrom(sourceClass)) {
                StreamSource source = new StreamSource(new StringReader(delegate.getData()));
                return (T) source;
            } else {
                SAXSource source = new SAXSource(new InputSource(new StringReader(delegate.getData())));
                return (T) source;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
        try {
            if (SAXResult.class.isAssignableFrom(resultClass)) {
                Result res = new SAXResult();
                return (T) res;
            } else if (DOMResult.class.isAssignableFrom(resultClass)) {
                Result res = new DOMResult();
                return (T) res;
            } else if (StreamResult.class.isAssignableFrom(resultClass)) {
                Result res = new StreamResult();
                return (T) res;
            } else {
                Result res = new StreamResult();
                return (T) res;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }
}
