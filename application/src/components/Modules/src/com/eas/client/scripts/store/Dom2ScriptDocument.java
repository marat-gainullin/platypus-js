package com.eas.client.scripts.store;

import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.ScriptDocument;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author pk, mg refactoring
 */
public class Dom2ScriptDocument {

    public static final String BAD_ROOT_TAGS_MSG = "In platypus documents should be only one root tag";

    public static ScriptDocument transform(Document aDom) throws Exception {
        Dom2ScriptDocument domReader = new Dom2ScriptDocument();
        ScriptDocument doc = domReader.findAndParseTag(aDom);
        doc.readScriptAnnotations();
        return doc;
    }

    protected Dom2ScriptDocument() throws ParserConfigurationException {
        super();
    }

    protected ScriptDocument findAndParseTag(Document aDocument) throws Exception {
        String scriptSource = null;
        Element root = aDocument.getDocumentElement();
        NodeList tags = root.getChildNodes();
        for (int j = 0; j < tags.getLength(); j++) {
            Node node = tags.item(j);
            if (node instanceof Element && ApplicationElement.SCRIPT_SOURCE_TAG_NAME.equals(((Element) node).getTagName())) {
                scriptSource = node.getTextContent();
            }
        }
        return new ScriptDocument(scriptSource);
    }
}
