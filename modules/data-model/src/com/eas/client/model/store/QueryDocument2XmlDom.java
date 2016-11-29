/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.QueryDocument;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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

    public static Document transformOutHints(List<QueryDocument.StoredFieldMetadata> outFieldsHints, Fields aHintedFields) throws Exception {
        QueryDocument2XmlDom transformer = new QueryDocument2XmlDom();
        return transformer.queryOutHints2XmlDom(outFieldsHints, aHintedFields);
    }

    protected Document queryOutHints2XmlDom(List<QueryDocument.StoredFieldMetadata> outFieldsHints, Fields aHintedFields) throws Exception {
        Document queryDom = documentBuilder.newDocument();
	queryDom.setXmlStandalone(true);
        Node outputFieldsNode = queryDom.createElement(QueryDocument.OUTPUT_FIELDS_TAG_NAME);
        saveOutputFields(outFieldsHints, aHintedFields, queryDom, outputFieldsNode);
        queryDom.appendChild(outputFieldsNode);
        queryDom.normalizeDocument();
        return queryDom;
    }

    private static boolean saveOutputFields(List<QueryDocument.StoredFieldMetadata> outFieldsHints, Fields aHintedFields, Document queryDom, Node outputFields) {
        boolean processed = false;
        for (QueryDocument.StoredFieldMetadata additionalField : outFieldsHints) {
            if (needToStoreOutputFieldAddition(aHintedFields, additionalField)) {
                Node outputFieldNode = queryDom.createElement(QueryDocument.OUTPUT_FIELD_TAG_NAME);
                NamedNodeMap attrs = outputFieldNode.getAttributes();
                Node bindedColAttr = queryDom.createAttribute(QueryDocument.FIELD_NAME_ATTRIBUTE_NAME),
                        descAttr = queryDom.createAttribute(QueryDocument.FIELD_DESCRIPTION_ATTRIBUTE_NAME),
                        typeAttr = queryDom.createAttribute(QueryDocument.FIELD_TYPE_ATTRIBUTE_NAME);
                bindedColAttr.setTextContent(additionalField.getBindedColumn());
                attrs.setNamedItem(bindedColAttr);

                Field queryField = aHintedFields != null ? aHintedFields.get(additionalField.getBindedColumn()) : null;
                if (additionalField.getDescription() != null && (queryField == null || !additionalField.getDescription().equals(queryField.getDescription()))) {
                    descAttr.setTextContent(additionalField.getDescription());
                    attrs.setNamedItem(descAttr);
                }
                if (additionalField.getType() != null && (queryField == null || !additionalField.getType().equals(queryField.getType()))) {
                    typeAttr.setTextContent(additionalField.getType());
                    attrs.setNamedItem(typeAttr);
                }
                outputFields.appendChild(outputFieldNode);
                processed = true;
            }
        }
        return processed;
    }

    private static boolean needToStoreOutputFieldAddition(Fields hintedFields, QueryDocument.StoredFieldMetadata aAdditionalField) {
        if (hintedFields != null) {
            Field queryField = hintedFields.get(aAdditionalField.getBindedColumn());
            if (queryField != null) {
                if (aAdditionalField.getDescription() != null && !aAdditionalField.getDescription().equals(queryField.getDescription())) {
                    return true;
                }
                if (aAdditionalField.getType() != null && !aAdditionalField.getType().equals(queryField.getType())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
