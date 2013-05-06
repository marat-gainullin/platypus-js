/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

/**
 *
 * @author vy
 */
    public class DbConnection {

        private String url;
        private String schema;
        private String user;
        private String password;

        public DbConnection(String aUrl, String aSchema, String aUser, String aPassword) {
            url = aUrl;
            schema = aSchema;
            user = aUser;
            password = aPassword;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return the schema
         */
        public String getSchema() {
            return schema;
        }

        /**
         * @param schema the schema to set
         */
        public void setSchema(String schema) {
            this.schema = schema;
        }

        /**
         * @return the user
         */
        public String getUser() {
            return user;
        }

        /**
         * @param user the user to set
         */
        public void setUser(String user) {
            this.user = user;
        }

        /**
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password the password to set
         */
        public void setPassword(String password) {
            this.password = password;
        }
    }
