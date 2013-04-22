/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.QueryDocument;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class QueryDocument2XmlDom {

    protected DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder documentBuilder;

    protected QueryDocument2XmlDom() throws Exception {
        super();
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    public static Document transform(QueryDocument document) throws Exception {
        QueryDocument2XmlDom transformer = new QueryDocument2XmlDom();
        return transformer.query2XmlDom(document);
    }

    public static Document transformModel(QueryDocument document) throws Exception {
        QueryDocument2XmlDom transformer = new QueryDocument2XmlDom();
        return transformer.queryModel2XmlDom(document);
    }

    public static Document transformOutHints(List<StoredFieldMetadata> outFieldsHints, Fields aHintedFields) throws Exception {
        QueryDocument2XmlDom transformer = new QueryDocument2XmlDom();
        return transformer.queryOutHints2XmlDom(outFieldsHints, aHintedFields);
    }

    protected Document query2XmlDom(QueryDocument document) throws Exception {
        Document dmDom = document.getModel().toXML();
        Document queryDom = documentBuilder.newDocument();
	queryDom.setXmlStandalone(true);
        Node queryRoot = queryDom.createElement(ApplicationElement.QUERY_ROOT_TAG_NAME),
                sqlTextNode = queryDom.createElement(ApplicationElement.SQL_TAG_NAME),
                fullSqlNode = queryDom.createElement(ApplicationElement.FULL_SQL_TAG_NAME),
                outputFieldsNode = queryDom.createElement(ApplicationElement.OUTPUT_FIELDS_TAG_NAME);
        queryRoot.appendChild(sqlTextNode);
        sqlTextNode.setTextContent(document.getQuery().getSqlText());
        if (document.getQuery().getFullSqlText() != null && !document.getQuery().getFullSqlText().isEmpty()) {
            fullSqlNode.setTextContent(document.getQuery().getFullSqlText());
            queryRoot.appendChild(fullSqlNode);
        }
        if (saveOutputFields(document.getAdditionalFieldsMetadata(), document.getQuery().getFields(), queryDom, outputFieldsNode)) {
            queryRoot.appendChild(outputFieldsNode);
        }
        NodeList dmRoots = dmDom.getElementsByTagName(Model2XmlDom.DATAMODEL_TAG_NAME);
        if (dmRoots.getLength() > 0) {
            queryRoot.appendChild(queryDom.adoptNode(dmRoots.item(0)));
        }
        queryDom.appendChild(queryRoot);
        queryDom.normalizeDocument();
        return queryDom;
    }

    protected Document queryModel2XmlDom(QueryDocument document) throws Exception {
        Document dmDom = document.getModel().toXML();
        Document queryDom = documentBuilder.newDocument();
	queryDom.setXmlStandalone(true);
        Node queryRoot = queryDom.createElement(ApplicationElement.QUERY_ROOT_TAG_NAME);
        NodeList dmRoots = dmDom.getElementsByTagName(Model2XmlDom.DATAMODEL_TAG_NAME);
        if (dmRoots.getLength() > 0) {
            queryRoot.appendChild(queryDom.adoptNode(dmRoots.item(0)));
        }
        queryDom.appendChild(queryRoot);
        queryDom.normalizeDocument();
        return queryDom;
    }

    protected Document queryOutHints2XmlDom(List<StoredFieldMetadata> outFieldsHints, Fields aHintedFields) throws Exception {
        Document queryDom = documentBuilder.newDocument();
	queryDom.setXmlStandalone(true);
        Node outputFieldsNode = queryDom.createElement(ApplicationElement.OUTPUT_FIELDS_TAG_NAME);
        saveOutputFields(outFieldsHints, aHintedFields, queryDom, outputFieldsNode);
        queryDom.appendChild(outputFieldsNode);
        queryDom.normalizeDocument();
        return queryDom;
    }

    private static boolean saveOutputFields(List<StoredFieldMetadata> outFieldsHints, Fields aHintedFields, Document queryDom, Node outputFields) {
        boolean processed = false;
        for (StoredFieldMetadata additionalField : outFieldsHints) {
            if (needToStoreOutputFieldAddition(aHintedFields, additionalField)) {
                Node outputFieldNode = queryDom.createElement(XmlDom2QueryDocument.OUTPUT_FIELD_TAG_NAME);
                NamedNodeMap attrs = outputFieldNode.getAttributes();
                Node bindedColAttr = queryDom.createAttribute(XmlDom2QueryDocument.FIELD_NAME_ATTRIBUTE_NAME),
                        descAttr = queryDom.createAttribute(XmlDom2QueryDocument.FIELD_DESCRIPTION_ATTRIBUTE_NAME),
                        typeAttr = queryDom.createAttribute(XmlDom2QueryDocument.FIELD_TYPE_ATTRIBUTE_NAME);
                bindedColAttr.setTextContent(additionalField.getBindedColumn());
                attrs.setNamedItem(bindedColAttr);

                Field queryField = aHintedFields != null ? aHintedFields.get(additionalField.getBindedColumn()) : null;
                if (additionalField.getDescription() != null && (queryField == null || !additionalField.getDescription().equals(queryField.getDescription()))) {
                    descAttr.setTextContent(additionalField.getDescription());
                    attrs.setNamedItem(descAttr);
                }
                if (additionalField.getTypeInfo() != null && (queryField == null || !additionalField.getTypeInfo().equals(queryField.getTypeInfo()))) {
                    typeAttr.setTextContent(String.valueOf(additionalField.getTypeInfo().getSqlType()));
                    attrs.setNamedItem(typeAttr);
                }
                outputFields.appendChild(outputFieldNode);
                processed = true;
            }
        }
        return processed;
    }

    private static boolean needToStoreOutputFieldAddition(Fields hintedFields, StoredFieldMetadata aAdditionalField) {
        if (hintedFields != null) {
            Field queryField = hintedFields.get(aAdditionalField.getBindedColumn());
            if (queryField != null) {
                if (aAdditionalField.getDescription() != null && !aAdditionalField.getDescription().equals(queryField.getDescription())) {
                    return true;
                }
                if (aAdditionalField.getTypeInfo() != null && !aAdditionalField.getTypeInfo().equals(queryField.getTypeInfo())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
