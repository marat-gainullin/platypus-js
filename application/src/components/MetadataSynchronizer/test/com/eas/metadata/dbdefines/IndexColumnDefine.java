/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

/**
 *
 * @author vy
 */
    public class IndexColumnDefine {

        private String columnName;
        private boolean ascending;

        public IndexColumnDefine(String aColumnName, boolean isAscending) {
            columnName = aColumnName;
            ascending = isAscending;
        }

        /**
         * @return the columnName
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * @param columnName the columnName to set
         */
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        /**
         * @return the ascending
         */
        public boolean isAscending() {
            return ascending;
        }

        /**
         * @param ascending the ascending to set
         */
        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }
    }
