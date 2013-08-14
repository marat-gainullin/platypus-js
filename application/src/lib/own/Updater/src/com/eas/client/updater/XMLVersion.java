/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author AB
 */
public class XMLVersion {

    public static Document getDocumentFile(String fname) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            String fn = FileUpdater.fixFileSeparatorChar(fname);
            DocumentBuilder builder = f.newDocumentBuilder();
            File fXml = new File(fn);
            if (!fXml.exists()) {
                return null;
            }
            return builder.parse(fXml);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static Document getDocumentStream(InputStream in) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(in);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 
     * @param to document with files release on local user machine
     * @param from document with files release on server
     * @return NodeList list of different nodes
     */
    public static int compareDocumentsNodeEx(Document to, Document from) {
        try {
            if ((to == null) || (from == null)) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, Updater.res.getString("nullNode"));
                return UpdaterConstants.FATAL_NOT_EQUALS;
            }
            Node node = from.getFirstChild();
            if (!UpdaterConstants.ROOT_NAME.equalsIgnoreCase(node.getNodeName())) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, Updater.res.getString("xmlDocError"));
                return UpdaterConstants.FATAL_NOT_EQUALS;
            }
            Node nd;
            NodeList toNds;
            Node toNd;
            String nName;
            nd = node.getFirstChild();

            while (nd.getNodeType() == Node.TEXT_NODE) {
                nd = nd.getNextSibling();
            }
            nName = nd.getNodeName();
            toNds = to.getElementsByTagName(nName);
            if (toNds.getLength() <= 0) {
                return UpdaterConstants.FATAL_NOT_EQUALS;
            }
            toNd = toNds.item(0);
            return compareNodesByVersion(toNd, nd);
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, e.getLocalizedMessage(), e);
            return UpdaterConstants.FATAL_NOT_EQUALS;
        }
    }

    /**
     * 
     * @param nd 
     * @return Version object contains a file version information
     */
    public static Version getNodeVersion(Node nd) {
        if (nd == null) {
            return null;
        }
        NamedNodeMap nnm = nd.getAttributes();
        Node n = nnm.getNamedItem(UpdaterConstants.VERSION_NAME);
        if (n == null) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, Updater.res.getString("couldNotGetVersion"));
            return null;
        }
        return new Version(n.getNodeValue());
    }

    /**
     * 
     * @param to node files release on local user machine
     * @param from node with files release on server
     * @return int value 
     */
    public static int compareNodesByVersion(Node to, Node from) {
        try {
            if ((to == null) || (from == null)) {
                return UpdaterConstants.FATAL_NOT_EQUALS;
            }
            Version verTo = getNodeVersion(to);
            return getNodeVersion(from).compareTo(verTo);
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, e.getStackTrace()[0].toString());
            return UpdaterConstants.FATAL_NOT_EQUALS;
        }
    }

    public static Version getDocVersion(Document doc) {
        try {
            if (doc != null)  {
                Node node = doc.getFirstChild();
                if (UpdaterConstants.ROOT_NAME.equalsIgnoreCase(node.getNodeName())) { 
                    Node nd = node.getFirstChild();
                    while (nd.getNodeType() == Node.TEXT_NODE) {
                        nd = nd.getNextSibling();
                    }
                    return getNodeVersion(nd);
                } else {    
                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, Updater.res.getString("xmlDocError"));
                    return null;
                }
            } else {    
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, Updater.res.getString("nullNode"));
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.ALL, e.getStackTrace()[0].toString());
            return null;
        }
    }
}
