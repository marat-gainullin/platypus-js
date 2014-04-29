package com.eas.server.filter;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.server.PlatypusServerCore;
import com.eas.xml.dom.XmlDom2String;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.*;

/**
 * Application elements filter for browsers.
 *
 * @author mg
 */
public class AppElementsFilter {

    public class FilteredXml {

        public Set<String> rolesAllowed;// Only module roles are expected to be in filteredXml application elements
        public String content;

        public FilteredXml(String aContent, Set<String> aRolesAllowed) {
            super();
            content = aContent;
            rolesAllowed = aRolesAllowed;
        }
    }
    public static final String SECURITY_VIOLATION_TEMPLATE = "function %s(){ throw 'Access to %s for %s denied! Contact your system administrator please.' }";

    public static final String DEPENDENCY_TAG_NAME = "dependency";
    public static final String QUERY_DEPENDENCY_TAG_NAME = "entityDependency";
    public static final String SERVER_DEPENDENCY_TAG_NAME = "serverDependency";
    protected PlatypusServerCore serverCore;
    protected final Map<String, ActualCacheEntry<FilteredXml>> filteredXml = new HashMap<>();

    public AppElementsFilter(PlatypusServerCore aServerCore) {
        serverCore = aServerCore;
    }

    public synchronized void removeXml(String aAppElement) {
        filteredXml.remove(aAppElement);
    }

    public synchronized void clearXml() {
        filteredXml.clear();
    }

    public synchronized FilteredXml get(String aAppElementId) throws Exception {
        AppCache cache = serverCore.getDatabasesClient().getAppCache();
        ActualCacheEntry<FilteredXml> filteredEntry = filteredXml.get(aAppElementId);
        FilteredXml filteredAppElement = filteredEntry != null ? filteredEntry.getValue() : null;
        if (filteredEntry != null && filteredAppElement != null && !cache.isActual(aAppElementId, filteredEntry.getTxtContentSize(), filteredEntry.getTxtContentCrc32())) {
            filteredAppElement = null;
            filteredXml.remove(aAppElementId);
            cache.remove(aAppElementId);
        }
        if (filteredAppElement == null) {
            ApplicationElement appElement = cache.get(aAppElementId);
            if (appElement != null) {
                filteredAppElement = filter(appElement);
                if (filteredAppElement != null) {
                    filteredXml.put(aAppElementId, new ActualCacheEntry<>(filteredAppElement, appElement.getTxtContentLength(), appElement.getTxtCrc32()));
                }
            }
        }
        return filteredAppElement;
    }

    public static String checkModuleName(String aTypedPrefix, String aModuleId) {
        if (Character.isDigit(aModuleId.charAt(0))) {
            return aTypedPrefix + aModuleId;
        } else {
            return aModuleId;
        }
    }

    /**
     * Performs some mutations of a xml content dom. Adds dependencies. Stripes
     * put &lt;source&gt; tag and so on.
     *
     * @param anAppElement Appelement, whoose content dom to mutated.
     * @return
     * @throws Exception
     */
    protected FilteredXml filter(ApplicationElement anAppElement) throws Exception {
        if (anAppElement != null && (anAppElement.getType() == ClientConstants.ET_FORM || anAppElement.getType() == ClientConstants.ET_COMPONENT || anAppElement.getType() == ClientConstants.ET_REPORT)) {
            anAppElement = anAppElement.copy();// Because of mutations of xml, made by this code.
            Document doc = anAppElement.getContent();
            if (doc != null) {
                ScriptDocument scriptDoc = Dom2ScriptDocument.dom2ScriptDocument(serverCore.getDatabasesClient(), doc);
                scriptDoc.readScriptAnnotations();
                //String moduleId = aAppElement.getId();
                Set<String> dependencies = null;
                Set<String> queryDependencies = null;
                Set<String> serverDependencies = null;
                assert doc.getChildNodes().getLength() == 1 : "Platypus application elements must contain only one root tag in xml DOM";
                Node rootNode = doc.getChildNodes().item(0);
                NodeList docNodes = rootNode.getChildNodes();
                for (int i = docNodes.getLength() - 1; i >= 0; i--) {
                    Node docNode = docNodes.item(i);
                    switch (docNode.getNodeName()) {
                        case ApplicationElement.SCRIPT_SOURCE_TAG_NAME:
                            if (anAppElement.getType() != ClientConstants.ET_COMPONENT && anAppElement.getType() != ClientConstants.ET_FORM) {
                                throw new Exception("Application element of unexpected type occured. Only Modules and Forms are allowed be requested by browser.");
                            }
                            String appSource = docNode.getTextContent();
                            DependenciesWalker depsWalker = new DependenciesWalker(appSource, serverCore.getDatabasesClient().getAppCache());
                            depsWalker.walk();
                            docNode.getParentNode().removeChild(docNode);

                            dependencies = depsWalker.getDependencies();
                            queryDependencies = depsWalker.getQueryDependencies();
                            serverDependencies = depsWalker.getServerDependencies();
                            break;
                        case Model2XmlDom.DATAMODEL_TAG_NAME:
                            NodeList entitiesNodes = docNode.getChildNodes();
                            for (int l = 0; l < entitiesNodes.getLength(); l++) {
                                Node entityNode = entitiesNodes.item(l);
                                NamedNodeMap attrs = entityNode.getAttributes();
                                if (attrs != null) {
                                    String[] attr2Remove = new String[]{
                                        Model2XmlDom.ENTITY_LOCATION_X, Model2XmlDom.ENTITY_LOCATION_Y,
                                        Model2XmlDom.ENTITY_SIZE_WIDTH, Model2XmlDom.ENTITY_SIZE_HEIGHT,
                                        Model2XmlDom.ENTITY_ICONIFIED,
                                        Model2XmlDom.TABLE_DB_ID_ATTR_NAME, Model2XmlDom.TABLE_NAME_ATTR_NAME, Model2XmlDom.TABLE_SCHEMA_NAME_ATTR_NAME};
                                    for (String a2Remove : attr2Remove) {
                                        if (attrs.getNamedItem(a2Remove) != null) {
                                            attrs.removeNamedItem(a2Remove);
                                        }
                                    }
                                }
                            }
                            break;
                        case "layout": {
                            String title = ((Element) docNode).getAttribute("title");
                            if (title == null || title.isEmpty()) {
                                ((Element) docNode).setAttribute("title", anAppElement.getName());
                            }
                        }
                        break;
                    }
                }
                if (dependencies != null && !dependencies.isEmpty()) {
                    for (String dependency : dependencies) {
                        Node depencyNode = doc.createElement(DEPENDENCY_TAG_NAME);
                        depencyNode.setTextContent(dependency);
                        rootNode.appendChild(depencyNode);
                    }
                }
                if (serverDependencies != null && !serverDependencies.isEmpty()) {
                    for (String serverDependency : serverDependencies) {
                        Node depencyNode = doc.createElement(SERVER_DEPENDENCY_TAG_NAME);
                        depencyNode.setTextContent(serverDependency);
                        rootNode.appendChild(depencyNode);
                    }
                }
                if (queryDependencies != null && !queryDependencies.isEmpty()) {
                    for (String dependency : queryDependencies) {
                        Node depencyNode = doc.createElement(QUERY_DEPENDENCY_TAG_NAME);
                        depencyNode.setTextContent(dependency);
                        rootNode.appendChild(depencyNode);
                    }
                }
                return new FilteredXml(XmlDom2String.transform(doc), checkResourceKindAndRoles(anAppElement, scriptDoc));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected Set<String> checkResourceKindAndRoles(ApplicationElement appElement, ScriptDocument scriptDoc) throws Exception {
        if (ClientConstants.ET_COMPONENT == appElement.getType()
                || ClientConstants.ET_REPORT == appElement.getType()
                || ClientConstants.ET_FORM == appElement.getType()
                || ClientConstants.ET_RESOURCE == appElement.getType()) {
            return readResourceRoles(scriptDoc);
        } else {
            // We disallow access of any three-tier client to application
            // design data like folders, queries content or database diagrams.
            // || ClientConstants.ET_DB_SCHEME == appElement.getType()
            // || ClientConstants.ET_FOLDER == appElement.getType()
            // || ClientConstants.ET_OLD_FORM == appElement.getType()
            // || ClientConstants.ET_RECYCLE == appElement.getType()
            // Also, three-tier client doesn't need any query application element content.
            // || ClientConstants.ET_QUERY == appElement.getType()
        }
        return null;
    }

    private Set<String> readResourceRoles(ScriptDocument scriptDoc) {
        scriptDoc.readScriptAnnotations();
        return scriptDoc.getModuleAllowedRoles();
    }
}
