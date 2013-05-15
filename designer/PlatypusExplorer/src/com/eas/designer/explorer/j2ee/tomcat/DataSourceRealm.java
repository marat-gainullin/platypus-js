/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The DataSource Database Realm connects Tomcat to a relational database,
 * accessed through a JNDI named JDBC DataSource to perform lookups of
 * usernames, passwords, and their associated roles.
 *
 * @author vv
 */
public class DataSourceRealm extends Realm {

    public static final String DATA_SOURCE_REALM_CLASS_NAME = "org.apache.catalina.realm.DataSourceRealm";//NOI18N
    public static final String ALL_ROLES_MODE_ATTR_NAME = "allRolesMode";//NOI18N
    public static final String DATASOURCE_NAME_ATTR_NAME = "dataSourceName";//NOI18N
    public static final String DIGEST_ATTR_NAME = "digest";//NOI18N
    public static final String LOCAL_DATASOURCE_ATTR_NAME = "localDataSource";//NOI18N
    public static final String ROLE_NAME_ATTR_NAME = "roleNameCol";//NOI18N
    public static final String STRIP_REALM_FOR_GSS_ATTR_NAME = "stripRealmForGss";//NOI18N
    public static final String USER_CRED_COL_ATTR_NAME = "userCredCol";//NOI18N
    public static final String USER_NAME_ATTR_NAME = "userNameCol";//NOI18N
    public static final String USER_ROLE_TABLE_ATTR_NAME = "userRoleTable";//NOI18N
    public static final String USER_TABLE_ATTR_NAME = "userTable";//NOI18N
    public static final String X509_USERNAME_RETRIEVER_CLASS_NAME_ATTR_NAME = "x509UsernameRetrieverClassName";//NOI18N
    public static final String MD5_DIGEST = "MD5";
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
    private String x509UsernameRetrieverClassName;

    /**
     * Gets the attribute controls how the special role name * is handled when
     * processing authorization constraints in web.xml.
     *
     * @return all roles name value
     */
    public String getAllRolesMode() {
        return allRolesMode;
    }

    /**
     * Sets the attribute controls how the special role name * is handled when
     * processing authorization constraints in web.xml.
     *
     * @param anAllRolesMode all roles name value
     */
    public void setAllRolesMode(String anAllRolesMode) {
        allRolesMode = anAllRolesMode;
    }

    /**
     * Gets the name of the JNDI JDBC DataSource for this Realm.
     *
     * @return data source name
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * Sets the name of the JNDI JDBC DataSource for this Realm.
     *
     * @param aDataSourceName data source name
     */
    public void setDataSourceName(String aDataSourceName) {
        dataSourceName = aDataSourceName;
    }

    /**
     * Gets the name of the MessageDigest algorithm used to encode user
     * passwords stored in the database. If not specified, user passwords are
     * assumed to be stored in clear-text.
     *
     * @return digest
     */
    public String getDigest() {
        return digest;
    }

    /**
     * Sets the name of the MessageDigest algorithm used to encode user
     * passwords stored in the database.
     *
     * @param aDigest digest
     */
    public void setDigest(String aDigest) {
        digest = aDigest;
    }

    /**
     * Gets the local data source flag. When the realm is nested inside a
     * Context element, this allows the realm to use a DataSource defined for
     * the Context rather than a global DataSource. If not specified, the
     * default is false: use a global DataSource.
     *
     * @return true if use local data source
     */
    public boolean getLocalDataSource() {
        return Boolean.valueOf(localDataSource);
    }

    /**
     * Sets the local data source flag.
     *
     * @param aLocalDataSource true if use local data source
     */
    public void setLocalDataSource(boolean aLocalDataSource) {
        localDataSource = Boolean.valueOf(aLocalDataSource).toString();
    }

    /**
     * Gets name of the column, in the "user roles" table, which contains a role
     * name assigned to the corresponding user.
     *
     * @return role column name
     */
    public String getRoleNameCol() {
        return roleNameCol;
    }

    /**
     * Sets name of the column, in the "user roles" table, which contains a role
     * name assigned to the corresponding user.
     *
     * @param aRoleNameCol role column name
     */
    public void setRoleNameCol(String aRoleNameCol) {
        roleNameCol = aRoleNameCol;
    }

    /**
     * Gets flag indicating when processing users authenticated via the GSS-API.
     * @return true if GSS-API
     */
    public String getStripRealmForGss() {
        return stripRealmForGss;
    }
    
    /**
     * Sets flag indicating when processing users authenticated via the GSS-API.
     * @param aStripRealmForGss true if GSS-API
     */
    public void setStripRealmForGss(String aStripRealmForGss) {
        stripRealmForGss = aStripRealmForGss;
    }
    
    /**
     * Gets name of the column, in the users table, which contains the user's credentials (i.e. password).
     * @return credentials column name
     */
    public String getUserCredCol() {
        return userCredCol;
    }

    /**
     * Sets name of the column, in the users table, which contains the user's credentials (i.e. password).
     * @param anUserCredCol credentials column name
     */
    public void setUserCredCol(String anUserCredCol) {
        userCredCol = anUserCredCol;
    }

    /**
     * Gets name of the column, in the users and user roles table, that contains the user's username.
     * @return user name column name
     */
    public String getUserNameCol() {
        return userNameCol;
    }
    
    /**
     * Sets name of the column, in the users and user roles table, that contains the user's username.
     * @param anUserNameCol user name column name
     */
    public void setUserNameCol(String anUserNameCol) {
        userNameCol = anUserNameCol;
    }

    /**
     * Gets name of the user roles table, which must contain columns named by the userNameCol and roleNameCol attributes.
     * @return user role table name
     */
    public String getUserRoleTable() {
        return userRoleTable;
    }
    
    /**
     * Sets name of the user roles table, which must contain columns named by the userNameCol and roleNameCol attributes.
     * @param anUserRoleTable user role table name
     */
    public void setUserRoleTable(String anUserRoleTable) {
        userRoleTable = anUserRoleTable;
    }

    /**
     * Gets name of the users table, which must contain columns named by the userNameCol and userCredCol attributes.
     * @return user table name
     */
    public String getUserTable() {
        return userTable;
    }
    
    /**
     * Sets name of the users table, which must contain columns named by the userNameCol and userCredCol attributes.
     * @param anUserTable user table name
     */
    public void setUserTable(String anUserTable) {
        userTable = anUserTable;
    }
    
    /**
     * Gets class name for using X509 client certificates.
     * @return class name
     */
    public String getX509UsernameRetrieverClassName() {
        return x509UsernameRetrieverClassName;
    }

    /**
     * Sets class name for using X509 client certificates.
     * @param aX509UsernameRetrieverClassName class name
     */
    public void setX509UsernameRetrieverClassName(String aX509UsernameRetrieverClassName) {
        x509UsernameRetrieverClassName = aX509UsernameRetrieverClassName;
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
        if (dataSourceName != null) {
            element.setAttribute(DATASOURCE_NAME_ATTR_NAME, dataSourceName);
        }
        if (digest != null) {
            element.setAttribute(DIGEST_ATTR_NAME, digest);
        }
        if (localDataSource != null) {
            element.setAttribute(LOCAL_DATASOURCE_ATTR_NAME, localDataSource);
        }
        if (roleNameCol != null) {
            element.setAttribute(ROLE_NAME_ATTR_NAME, roleNameCol);
        }
        if (stripRealmForGss != null) {
            element.setAttribute(STRIP_REALM_FOR_GSS_ATTR_NAME, stripRealmForGss);
        }
        if (userCredCol != null) {
            element.setAttribute(USER_CRED_COL_ATTR_NAME, userCredCol);
        }
        if (userNameCol != null) {
            element.setAttribute(USER_NAME_ATTR_NAME, userNameCol);
        }
        if (userRoleTable != null) {
            element.setAttribute(USER_ROLE_TABLE_ATTR_NAME, userRoleTable);
        }
        if (userTable != null) {
            element.setAttribute(USER_TABLE_ATTR_NAME, userTable);
        }
        if (x509UsernameRetrieverClassName != null) {
            element.setAttribute(X509_USERNAME_RETRIEVER_CLASS_NAME_ATTR_NAME, x509UsernameRetrieverClassName);
        }
        return element;
    }
}
