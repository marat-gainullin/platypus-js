/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.DbClient;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.QueryDocument;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.query.QueryModel;
import com.eas.client.queries.SqlQuery;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class XmlDom2QueryDocument {

    public final static String NAME_TAG_NAME = "name";
    public final static String SCHEMA_TAG_NAME = "schema";
    public final static String OUTPUT_FIELD_TAG_NAME = "field";
    public final static String FIELD_NAME_ATTRIBUTE_NAME = "bindedColumn";
    public final static String FIELD_DESCRIPTION_ATTRIBUTE_NAME = "description";
    public final static String FIELD_TYPE_ATTRIBUTE_NAME = "sqlType";
    protected DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder documentBuilder;

    public XmlDom2QueryDocument() throws Exception {
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    public static QueryDocument transform(DbClient aClient, String aEntityId, Document aDom) throws Exception {
        XmlDom2QueryDocument dom2Query = new XmlDom2QueryDocument();
        return dom2Query.dom2QueryDocument(aClient, aEntityId, aDom);
    }

    private QueryDocument dom2QueryDocument(DbClient aClient, String aEntityId, Document aDom) throws Exception {
        QueryModel model = null;
        SqlQuery query = new SqlQuery(aClient);
        query.setEntityId(aEntityId);
        List<StoredFieldMetadata> additionalFields = new ArrayList<>();
        synchronized (aDom) {// query's dom is caching subject, and so multiple threads can arrive and try to parse same query's dom
            NodeList roots = aDom.getChildNodes();
            for (int i = 0; i < roots.getLength(); i++) {
                Node root = roots.item(i);
                if (ApplicationElement.QUERY_ROOT_TAG_NAME.equals(root.getNodeName())) {
                    NodeList tags = root.getChildNodes();
                    for (int j = 0; j < tags.getLength(); j++) {
                        Node node = tags.item(j);
                        switch (node.getNodeName()) {
                            case Model2XmlDom.DATAMODEL_TAG_NAME:
                                Document modelDom = documentBuilder.newDocument();
				modelDom.setXmlStandalone(true);
                                modelDom.appendChild(modelDom.importNode(node, true));
                                model = XmlDom2QueryModel.transform(aClient, modelDom);
                                break;
                            case ApplicationElement.SQL_TAG_NAME:
                                query.setSqlText(node.getTextContent());
                                break;
                            case ApplicationElement.FULL_SQL_TAG_NAME:
                                query.setFullSqlText(node.getTextContent());
                                break;
                            case ApplicationElement.OUTPUT_FIELDS_TAG_NAME:
                                assert node instanceof Element;
                                additionalFields = parseFieldsHintsTag((Element) node);
                                break;
                        }
                    }
                }
            }
        }
        return new QueryDocument(query, model, additionalFields);
    }

    public static List<StoredFieldMetadata> parseFieldsHintsTag(Element aTag) {
        List<StoredFieldMetadata> additionalFields = new ArrayList<>();
        NodeList fieldsNodes = aTag.getChildNodes();
        for (int k = 0; k < fieldsNodes.getLength(); k++) {
            Node fn = fieldsNodes.item(k);
            if (OUTPUT_FIELD_TAG_NAME.equals(fn.getNodeName())) {
                NamedNodeMap attrs = fn.getAttributes();
                Node bindedColAttr = attrs.getNamedItem(FIELD_NAME_ATTRIBUTE_NAME),
                        descAttr = attrs.getNamedItem(FIELD_DESCRIPTION_ATTRIBUTE_NAME),
                        typeAttr = attrs.getNamedItem(FIELD_TYPE_ATTRIBUTE_NAME);
                if (bindedColAttr != null && (descAttr != null || typeAttr != null)) {
                    StoredFieldMetadata additionalField = new StoredFieldMetadata();
                    additionalField.bindedColumn = bindedColAttr.getNodeValue();
                    if (descAttr != null) {
                        additionalField.description = descAttr.getNodeValue();
                    }
                    if (typeAttr != null) {
                        additionalField.typeInfo = DataTypeInfo.valueOf(Integer.valueOf(typeAttr.getNodeValue()));
                    }
                    additionalFields.add(additionalField);
                }
            }
        }
        return additionalFields;
    }
}
