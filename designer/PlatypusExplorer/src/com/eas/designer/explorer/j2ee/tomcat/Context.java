/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import com.eas.xml.dom.XmlDomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tomcat 7 web application's context.
 *
 * @author vv
 */
public class Context {

    public static final String ROOT_TAG_NAME = "Context";//NOI18N
    public static final String DOC_BASE_ATTR_NAME = "docBase";//NOI18N
    public static final String PATH_ATTR_NAME = "path";//NOI18N
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    private final List<Parameter> params = new ArrayList<>();
    private Realm realm;
    private final List<Resource> resources = new ArrayList<>();
    private String docBase;
    private String path;

    public Context() throws ParserConfigurationException {
        builder = factory.newDocumentBuilder();
    }

    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(true);
        Element contextTag = doc.createElement(ROOT_TAG_NAME);
        if (docBase != null) {
            contextTag.setAttribute(DOC_BASE_ATTR_NAME, docBase);
        }
        if (path != null) {
            contextTag.setAttribute(PATH_ATTR_NAME, path);
        }
        for (Parameter param : params) {
            contextTag.appendChild(param.getElement(doc));
        }
        if (realm != null) {
            contextTag.appendChild(realm.getElement(doc));
        }
        for (Resource resource : resources) {
            contextTag.appendChild(resource.getElement(doc));
        }
        doc.appendChild(contextTag);
        return doc;
    }

    public static Context valueOf(Document aDoc) throws Exception {
        Context context = new Context();
        NodeList projectNl = aDoc.getElementsByTagName(ROOT_TAG_NAME);
        if (projectNl != null && projectNl.getLength() == 1 && projectNl.item(0) instanceof Element) {
            Element contextTag = (Element) projectNl.item(0);
            context.docBase = contextTag.getAttribute(DOC_BASE_ATTR_NAME);
            context.path = contextTag.getAttribute(PATH_ATTR_NAME);
            Element realmTag = XmlDomUtils.getElementByTagName(contextTag, Realm.TAG_NAME, Realm.TAG_NAME);
            context.realm = RealmFactory.getRealm(realmTag);
            for (Element paramTag : XmlDomUtils.elementsByTagName(contextTag, Parameter.TAG_NAME, Parameter.TAG_NAME)) {
                Parameter param = new Parameter(paramTag.getAttribute(Parameter.NAME_ATTR_NAME), paramTag.getAttribute(Parameter.VALUE_ATTR_NAME));
                context.params.add(param);     
            }
            for (Element resourceTag : XmlDomUtils.elementsByTagName(contextTag, Resource.TAG_NAME, Resource.TAG_NAME)) {
                Resource res = ResourceFactory.getRealm(resourceTag);
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
     * Gets the context path of this web application, which is matched against the
     * beginning of each request URI to select the appropriate web application
     * for processing.

     * @return path 
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the context path of this web application, which is matched against the
     * beginning of each request URI to select the appropriate web application
     * for processing.
     * 
     * @param aPath path
     */
    public void setPath(String aPath) {
        path = aPath;
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
}
