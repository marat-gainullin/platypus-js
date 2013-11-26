/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.filter;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.script.ScriptUtils;
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

    public class Filtered {

        public Set<String> rolesAllowed;// Only module roles are expected to be in filtered application elements
        public String content;
        public String script;

        public Filtered(String aContent, String aScript, Set<String> aRolesAllowed) {
            super();
            content = aContent;
            script = aScript;
            rolesAllowed = aRolesAllowed;
        }
    }
    public static final String SECURITY_VIOLATION_TEMPLATE = "function %s(){ throw 'Access to %s for %s denied! Contact your system administrator please.' }";
    /*
    protected static String[] topLevelModulePropertiesAndMethods = new String[]{
        "model", "principal", "applicationElementId"
    };
    protected static String[] topLevelFormPropertiesAndMethods = new String[]{
        "view", "formKey", "defaultCloseOperation",
        "icon", "title",
        "resizable", "minimized", "maximized", "minimizable", "maximizable",
        "minimize", "maximize", "restore", "toFront",
        "undecorated", "opacity", "alwaysOnTop",
        "locationByPlatform", "left", "top", "width", "height",
        "onWindowOpened", "onWindowClosing", "onWindowClosed", "onWindowMinimized",
        "onWindowRestored", "onWindowMaximized", "onWindowActivated", "onWindowDeactivated",
        "show", "showModal", "showOnPanel", "showInternalFrame", "close"
    };
    */ 
    public static final String BROWSER_SELF_TEMPLATE = ""
            + "try{"
//            + "var " + ScriptTransformer.SELF_NAME + " = this;"
            + "Object.defineProperty(this, \"applicationElementId\", {"
            + "    get:function() {"
            + "        return '%s';"
            + "    }"
            + "});"
            + "var ___appElement = window.platypus.getCached(this.applicationElementId);";
    public static final String BROWSER_MODEL_READ_TEMPLATE = "window.platypus.readModel(___appElement, this);";
    public static final String BROWSER_FORM_READ_TEMPLATE = "window.platypus.readForm(___appElement, this);";
    //public static final String BROWSER_REPORT_READ_TEMPLATE = "window.platypus.readReport(___appElement, this);";
    public static final String BROWSER_SOURCE_BODY = ""
            + "%s";
    public static final String BROWSER_SOURCE_TAIL = ""
            + "this.model.runtime = true;"
            + "} catch(e) {"
            + "    if(window.console && e != null){"
            + "        window.console.log(e.toString());"
            + "        if(e.stack)"
            + "            window.console.log(e.stack);"
            + "    }"
            + "}"
            + "}"
            + "(function(){"
            + "    var appElement = '%s';"
            + "    if(!window.platypusModulesConstructors)"
            + "        window.platypusModulesConstructors = {};"
            + "    var constr = %s;"
            + "    window.platypusModulesConstructors[appElement] = constr;"
            + "  var F = function() {"
            + "  };"
            + "  F.prototype = %s.prototype;"
            + "  constr.prototype = new F();"
            + "  constr.prototype.constructor = constr;"
            + "  constr.superclass = %s.prototype;"
            + "})();";
    public static final String BROWSER_MODULE_TEMPLATE = ""
            + "function %s()"// Module name
            + "{"
            + BROWSER_SELF_TEMPLATE
            + BROWSER_MODEL_READ_TEMPLATE
            + BROWSER_SOURCE_BODY
            + BROWSER_SOURCE_TAIL;
    public static final String BROWSER_FORM_TEMPLATE = ""
            + "function %s()"// Module name
            + "{"
            + BROWSER_SELF_TEMPLATE
            + BROWSER_MODEL_READ_TEMPLATE
            + BROWSER_FORM_READ_TEMPLATE
            + BROWSER_SOURCE_BODY
            + "this.handled = true;"
            + BROWSER_SOURCE_TAIL;
    public static final String DEPENDENCY_TAG_NAME = "dependency";
    public static final String QUERY_DEPENDENCY_TAG_NAME = "entityDependency";
    public static final String SERVER_DEPENDENCY_TAG_NAME = "serverDependency";
    protected PlatypusServerCore serverCore;
    protected final Map<String, ActualCacheEntry<Filtered>> filtered = new HashMap<>();

    public AppElementsFilter(PlatypusServerCore aServerCore) {
        serverCore = aServerCore;
    }

    public synchronized void remove(String aAppElement) {
        filtered.remove(aAppElement);
    }

    public synchronized void clear() {
        filtered.clear();
    }

    public synchronized Filtered get(String aAppElementId) throws Exception {
        AppCache cache = serverCore.getDatabasesClient().getAppCache();
        ActualCacheEntry<Filtered> filteredEntry = filtered.get(aAppElementId);
        Filtered filteredAppElement = filteredEntry != null ? filteredEntry.getValue() : null;
        if (filteredEntry != null && filteredAppElement != null && !cache.isActual(aAppElementId, filteredEntry.getTxtContentSize(), filteredEntry.getTxtContentCrc32())) {
            filteredAppElement = null;
            filtered.remove(aAppElementId);
            cache.remove(aAppElementId);
        }
        if (filteredAppElement == null) {
            ApplicationElement appElement = cache.get(aAppElementId);
            if (appElement != null) {
                filteredAppElement = filter(appElement);
                if (filteredAppElement != null) {
                    filtered.put(aAppElementId, new ActualCacheEntry<>(filteredAppElement, appElement.getTxtContentLength(), appElement.getTxtCrc32()));
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
     * put <source/>tag and so on.
     *
     * @param aAppElement Appelement, whoose content dom to mutated.
     * @return
     * @throws Exception
     */
    protected Filtered filter(ApplicationElement aAppElement) throws Exception {
        if (aAppElement != null && (aAppElement.getType() == ClientConstants.ET_FORM || aAppElement.getType() == ClientConstants.ET_COMPONENT || aAppElement.getType() == ClientConstants.ET_REPORT)) {
            aAppElement = aAppElement.copy();// because of mutations of xml content dom, made by this code.
            String probableScript = null;
            Document doc = aAppElement.getContent();
            if (doc != null) {
                ScriptDocument scriptDoc = Dom2ScriptDocument.dom2ScriptDocument(serverCore.getDatabasesClient(), doc);
                scriptDoc.readScriptAnnotations();
                String moduleId = aAppElement.getId();
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
                            String appSource = docNode.getTextContent();
                            DependenciesWorker transformer = buildTransformer(appSource, scriptDoc.getModel());
                            String source = transformer.transform();
                            switch (aAppElement.getType()) {
                                case ClientConstants.ET_COMPONENT: {
                                    /*
                                    for (String topLevelVarMethod : topLevelModulePropertiesAndMethods) {
                                        transformer.addExternalVariable(topLevelVarMethod);
                                    }
                                    */ 
                                    String constructorName = checkModuleName("Module", moduleId);
                                    probableScript = String.format(BROWSER_MODULE_TEMPLATE,
                                            constructorName, moduleId, source+"\n ; this[\""+ScriptUtils.HANDLERS_PROP_NAME+"\"]="+scriptDoc.generateTopLevelNamedFunctionsContainer()+";", moduleId, constructorName, "Module", "Module");
                                }
                                break;
                                case ClientConstants.ET_FORM: {
                                    /*
                                    for (String topLevelVarMethod : topLevelModulePropertiesAndMethods) {
                                        transformer.addExternalVariable(topLevelVarMethod);
                                    }
                                    for (String topLevelVarMethod : topLevelFormPropertiesAndMethods) {
                                        transformer.addExternalVariable(topLevelVarMethod);
                                    }
                                    addAsExternals(doc.getElementsByTagName("widget"), transformer);
                                    addAsExternals(doc.getElementsByTagName("nonvisual"), transformer);
                                    */ 
                                    String constructorName = checkModuleName("Form", moduleId);
                                    probableScript = String.format(BROWSER_FORM_TEMPLATE,
                                            constructorName, moduleId, source+"\n ; this[\""+ScriptUtils.HANDLERS_PROP_NAME+"\"]="+scriptDoc.generateTopLevelNamedFunctionsContainer()+";", moduleId, constructorName, "Form", "Form");
                                }
                                break;
                                default:
                                    throw new Exception("Application element of unexpected type occured. Only Modules and Forms are allowed be requested by browser.");
                            }
                            docNode.getParentNode().removeChild(docNode);
                            
                            dependencies = transformer.getDependencies();
                            queryDependencies = transformer.getQueryDependencies();
                            serverDependencies = transformer.getServerDependencies();
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
                                ((Element) docNode).setAttribute("title", aAppElement.getName());
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
                return new Filtered(XmlDom2String.transform(doc), probableScript, checkResourceKindAndRoles(aAppElement, scriptDoc));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected DependenciesWorker buildTransformer(String aSource, ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        DependenciesWorker transformer = new DependenciesWorker(aSource, serverCore.getDatabasesClient().getAppCache());
        /*
        transformer.addExternalVariable(Model.DATASOURCE_METADATA_SCRIPT_NAME);
        transformer.addExternalVariable(Model.PARAMETERS_SCRIPT_NAME);
        for (int i = 1; i <= aModel.getParameters().getParametersCount(); i++) {
            transformer.addExternalVariable(aModel.getParameters().get(i).getName());
        }
        for (ApplicationEntity<?, ?, ?> entity : aModel.getEntities().values()) {
            transformer.addExternalVariable(entity.getName());
        }
        */ 
        return transformer;
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

    /*
    private void addAsExternals(NodeList widgets, ScriptTransformer transformer) throws DOMException {
        if (widgets != null) {
            for (int w = 0; w < widgets.getLength(); w++) {
                Node widget = widgets.item(w);
                if (widget instanceof Element) {
                    assert widget.getAttributes() != null;
                    Node name = widget.getAttributes().getNamedItem("name");
                    if (name != null) {
                        transformer.addExternalVariable(name.getNodeValue());
                    }
                }
            }
        }
    }
    */ 
}
