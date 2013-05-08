/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tomcat 7 web application's context.
 *
 * @author vv
 */
public class Context {
    
    public static final String CONTEXT_TAG_NAME = "Context";//NOI18N
    public static final String DOC_BASE_ATTR_NAME = "docBase";//NOI18N
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    private Realm realm;
    private List<Resource> resources = new ArrayList<>();
    private String docBase;
    
    public Context() throws ParserConfigurationException {
        builder = factory.newDocumentBuilder();
    }
    
    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(true);
        Element contextTag = doc.createElement(CONTEXT_TAG_NAME);
        if (docBase != null) {
            contextTag.setAttribute(DOC_BASE_ATTR_NAME, docBase);
        }
        if (realm != null) {
            contextTag.appendChild(Realm.getElement(realm));
        }
        doc.appendChild(contextTag);
        return doc;
    }
    
    public static Context valueOf(Document aDoc) throws Exception {
        Context context = new Context();
        NodeList projectNl = aDoc.getElementsByTagName(CONTEXT_TAG_NAME);
        if (projectNl != null && projectNl.getLength() == 1 && projectNl.item(0) instanceof Element) {
            Element contextTag = (Element) projectNl.item(0);
            context.docBase = contextTag.getAttribute(DOC_BASE_ATTR_NAME);
            Element realmTag = getElementByName(contextTag, Realm.TAG_NAME);
            context.realm = Realm.valueOf(realmTag);
            for (Element resourceTag : getElementsByName(contextTag, Resource.TAG_NAME)) {
                Resource res = Resource.valueOf(resourceTag);
                if (res != null) {
                    context.resources.add(res);
                }
            }
            return context;
        }
        return null;
    }

    /**
     * Gets the Document Base value, representing directory (also known as the
     * Context Root) for this web application.
     *
     * @return docBase attribute value
     */
    public String getDocBase() {
        return docBase;
    }

    /**
     * Sets the Document Base, representing directory (also known as the Context
     * Root) for this web application.
     *
     * @param aDocBase a docBase attribute value
     */
    public void setDocBase(String aDocBase) {
        docBase = aDocBase;
    }

    /**
     * Gets the Realm for web application.
     *
     * @return realm value
     */
    public Realm getRealm() {
        return realm;
    }

    /**
     * Sets the Realm for web application.
     *
     * @param aRealm
     */
    public void setRealm(Realm aRealm) {
        realm = aRealm;
    }

    /**
     * Gets JNDI resources.
     *
     * @return resources list
     */
    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    /**
     * Adds a Resource to web application's context.
     *
     * @param aResource resource
     */
    public synchronized void addResource(Resource aResource) {
        resources.add(aResource);
    }

    /**
     * Deletes a Resource from web application's context.
     *
     * @param aResource resource
     */
    public synchronized void deleteResource(Resource aResource) {
        resources.remove(aResource);
    }
    
    private static Element getElementByName(Element aParent, String aName) {
        NodeList nl = aParent.getElementsByTagName(aName);
        if (nl != null && nl.getLength() == 1 && nl.item(0) instanceof Element) {
            return (Element) nl.item(0);
        } else {
            return null;
        }
    }
    
    private static List<Element> getElementsByName(Element aParent, String aName) {
        NodeList nl = aParent.getElementsByTagName(aName);
        if (nl != null && nl.getLength() > 0) {
            List<Element> elements = new ArrayList<>();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i) instanceof Element) {
                    elements.add((Element) nl.item(i));
                }
            }
            return elements;
        } else {
            return null;
        }
    }

    /**
     * A Realm element represents a "database" of usernames, passwords, and
     * roles (similar to Unix groups) assigned to those users.
     */
    public static class Realm {

        public static final String TAG_NAME = "Realm";//NOI18N

        private static Realm valueOf(Element realmTag) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        private static Node getElement(Realm realm) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    /**
     * JNDI resource.
     */
    public static class Resource {
        public static final String TAG_NAME = "Resource";//NOI18N

        private static Resource valueOf(Element resourceTag) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
