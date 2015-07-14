/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Simplified representation of deployment descriptor(web.xml) file.
 *
 * @author vv
 */
public class WebApplication {

    public static final String TAG_NAME = "web-app";//NOI18N
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    private final List<ContextParam> contextParams = new ArrayList<>();
    private final List<AppListener> appListeners = new ArrayList<>();
    private final List<Servlet> servlets = new ArrayList<>();
    private final List<ServletMapping> servletMappings = new ArrayList<>();
    private final List<ResourceRef> resourceRefs = new ArrayList<>();
    private final Set<SecurityConstraint> securityConstraints = new HashSet<>();
    private LoginConfig loginConfig;
    private final List<SecurityRole> securityRoles = new ArrayList<>();
    private final List<WelcomeFile> welcomeFiles = new ArrayList<>();

    public WebApplication() throws ParserConfigurationException {
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
    }

    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(false);
        Element webApp = doc.createElement(TAG_NAME);
        webApp.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee"); //NOI18N
        webApp.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //NOI18N
        webApp.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"); //NOI18N
        webApp.setAttribute("version", "3.0"); //NOI18N
        doc.appendChild(webApp);
        for (ContextParam contextParam : contextParams) {
            webApp.appendChild(contextParam.getElement(doc));
        }
        for (AppListener appListener : appListeners) {
            webApp.appendChild(appListener.getElement(doc));
        }
        for (Servlet servlet : servlets) {
            webApp.appendChild(servlet.getElement(doc));
        }
        for (ServletMapping servletMapping : servletMappings) {
            webApp.appendChild(servletMapping.getElement(doc));
        }
        for (ResourceRef resourceRef : resourceRefs) {
            webApp.appendChild(resourceRef.getElement(doc));
        }
        for (SecurityConstraint constraint : securityConstraints) {
            webApp.appendChild(constraint.getElement(doc));
        }
        if (loginConfig != null) {
            webApp.appendChild(loginConfig.getElement(doc));
        }
        for (SecurityRole role : securityRoles) {
            webApp.appendChild(role.getElement(doc));
        }
        Element welcomeFilesElement = doc.createElement("welcome-file-list");
        webApp.appendChild(welcomeFilesElement);
        for (WelcomeFile wf : welcomeFiles) {
            welcomeFilesElement.appendChild(wf.getElement(doc));
        }
        return doc;
    }

    public List<ContextParam> getContextParams() {
        return Collections.unmodifiableList(contextParams);
    }

    public void addInitParam(ContextParam aParam) {
        contextParams.add(aParam);
    }

    public void removeInitParam(String aParamName) {
        if (aParamName != null) {
            Iterator<ContextParam> i = contextParams.iterator();
            while (i.hasNext()) {
                ContextParam param = i.next();
                if (aParamName.equals(param.getName())) {
                    i.remove();
                }
            }
        }
    }

    public void addAppListener(AppListener aListener) {
        appListeners.add(aListener);
    }

    public List<AppListener> getAppListeners() {
        return Collections.unmodifiableList(appListeners);
    }

    public void removeAppListener(String aListenerClassName) {
        if (aListenerClassName != null) {
            Iterator<AppListener> i = appListeners.iterator();
            while (i.hasNext()) {
                AppListener l = i.next();
                if (aListenerClassName.equals(l.getClassName())) {
                    i.remove();
                }
            }
        }
    }

    public List<WelcomeFile> getWelcomeFiles() {
        return welcomeFiles;
    }

    public void addWelcomeFile(WelcomeFile aWelcomeFile) {
        welcomeFiles.add(aWelcomeFile);
    }

    public void removeWelcomeFile(WelcomeFile toRemove) {
        welcomeFiles.remove(toRemove);
    }

    public void addServlet(Servlet aServlet) {
        servlets.add(aServlet);
    }

    public List<Servlet> getServlets() {
        return Collections.unmodifiableList(servlets);
    }

    public void removeServlet(String aServletName) {
        if (aServletName != null) {
            Iterator<Servlet> i = servlets.iterator();
            while (i.hasNext()) {
                Servlet l = i.next();
                if (aServletName.equals(l.getName())) {
                    i.remove();
                }
            }
        }
    }

    public void addServletMapping(ServletMapping aServletMapping) {
        servletMappings.add(aServletMapping);
    }

    public List<ServletMapping> getServletMappings() {
        return Collections.unmodifiableList(servletMappings);
    }

    public void removeServletMapping(String aServletName) {
        if (aServletName != null) {
            Iterator<ServletMapping> i = servletMappings.iterator();
            while (i.hasNext()) {
                ServletMapping l = i.next();
                if (aServletName.equals(l.getServletName())) {
                    i.remove();
                }
            }
        }
    }

    public void addResourceRef(ResourceRef aResourceRef) {
        resourceRefs.add(aResourceRef);
    }

    public List<ResourceRef> getResourceRefs() {
        return Collections.unmodifiableList(resourceRefs);
    }

    public void removeResourceRef(String aResourceName) {
        if (aResourceName != null) {
            Iterator<ResourceRef> i = resourceRefs.iterator();
            while (i.hasNext()) {
                ResourceRef l = i.next();
                if (aResourceName.equals(l.getResRefName())) {
                    i.remove();
                }
            }
        }
    }

    public Set<SecurityConstraint> getSecurityConstraints() {
        return Collections.unmodifiableSet(securityConstraints);
    }

    public void addSecurityConstraint(SecurityConstraint aSecurityConstraint) {
        securityConstraints.add(aSecurityConstraint);
    }

    public LoginConfig getLoginConfig() {
        return loginConfig;
    }

    public void setLoginConfig(LoginConfig aLoginConfig) {
        loginConfig = aLoginConfig;
    }

    public void addSecurityRole(SecurityRole aSecurityRole) {
        securityRoles.add(aSecurityRole);
    }

    public List<SecurityRole> getSecurityRoles() {
        return Collections.unmodifiableList(securityRoles);
    }

    public void removeSecurityRole(String aSecurityRoleNme) {
        if (aSecurityRoleNme != null) {
            Iterator<SecurityRole> i = securityRoles.iterator();
            while (i.hasNext()) {
                SecurityRole r = i.next();
                if (aSecurityRoleNme.equals(r.getRoleName())) {
                    i.remove();
                }
            }
        }
    }

}
