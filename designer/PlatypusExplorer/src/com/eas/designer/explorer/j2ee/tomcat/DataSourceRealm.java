/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author vv
 */
public class DataSourceRealm extends Realm {

    public static final String DATA_SOURCE_REALM_CLASS_NAME = "org.apache.catalina.realm.DataSourceRealm";//NOI18N
    public static final String ALL_ROLES_MODE_ATTR_NAME = "allRolesMode";//NOI18N
    private String allRolesMode;
    private String dataSourceName;
    private String digest;
    private String localDataSource;
    private String roleNameCol;
    private String stripRealmForGss;
    private String userCredCol;
    private String userNameCol;
    private String userRoleTable;
    private String userTable;
    private String X509UsernameRetrieverClassName;

    public String getAllRolesMode() {
        return allRolesMode;
    }

    public void setAllRolesMode(String allRolesMode) {
        this.allRolesMode = allRolesMode;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getLocalDataSource() {
        return localDataSource;
    }

    public void setLocalDataSource(String localDataSource) {
        this.localDataSource = localDataSource;
    }

    public String getRoleNameCol() {
        return roleNameCol;
    }

    public void setRoleNameCol(String roleNameCol) {
        this.roleNameCol = roleNameCol;
    }

    public String getStripRealmForGss() {
        return stripRealmForGss;
    }

    public void setStripRealmForGss(String stripRealmForGss) {
        this.stripRealmForGss = stripRealmForGss;
    }

    public String getUserCredCol() {
        return userCredCol;
    }

    public void setUserCredCol(String userCredCol) {
        this.userCredCol = userCredCol;
    }

    public String getUserNameCol() {
        return userNameCol;
    }

    public void setUserNameCol(String userNameCol) {
        this.userNameCol = userNameCol;
    }

    public String getUserRoleTable() {
        return userRoleTable;
    }

    public void setUserRoleTable(String userRoleTable) {
        this.userRoleTable = userRoleTable;
    }

    public String getUserTable() {
        return userTable;
    }

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    public String getX509UsernameRetrieverClassName() {
        return X509UsernameRetrieverClassName;
    }

    public void setX509UsernameRetrieverClassName(String X509UsernameRetrieverClassName) {
        this.X509UsernameRetrieverClassName = X509UsernameRetrieverClassName;
    }

    @Override
    public void load(Element tag) {
        super.load(tag);
        allRolesMode = tag.getAttribute(ALL_ROLES_MODE_ATTR_NAME);
    }

    @Override
    public Element getElement(Document aDoc) {
        Element element = super.getElement(aDoc);
        if (allRolesMode != null) {
            element.setAttribute(ALL_ROLES_MODE_ATTR_NAME, allRolesMode);
        }
        return element;
    }
}
