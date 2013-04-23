/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.dbdefines;

import com.eas.metadata.dbdefines.IndexColumnDefine;

/**
 *
 * @author vy
 */
    public class IndexDefine {

        private String indexName;
        private boolean clustered;
        private boolean hashed;
        private boolean unique;
        private IndexColumnDefine[] columns;

        public IndexDefine(String aIndexName, boolean isClustered, boolean isHashed, boolean isUnique, IndexColumnDefine[] aColumns) {
            indexName = aIndexName;
            clustered = isClustered;
            hashed = isHashed;
            unique = isUnique;
            columns = aColumns;
        }

        /**
         * @return the indexName
         */
        public String getIndexName() {
            return indexName;
        }

        /**
         * @param indexName the indexName to set
         */
        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        /**
         * @return the clustered
         */
        public boolean isClustered() {
            return clustered;
        }

        /**
         * @param clustered the clustered to set
         */
        public void setClustered(boolean clustered) {
            this.clustered = clustered;
        }

        /**
         * @return the hashed
         */
        public boolean isHashed() {
            return hashed;
        }

        /**
         * @param hashed the hashed to set
         */
        public void setHashed(boolean hashed) {
            this.hashed = hashed;
        }

        /**
         * @return the unique
         */
        public boolean isUnique() {
            return unique;
        }

        /**
         * @param unique the unique to set
         */
        public void setUnique(boolean unique) {
            this.unique = unique;
        }

        /**
         * @return the columns
         */
        public IndexColumnDefine[] getColumns() {
            return columns;
        }

        /**
         * @param columns the columns to set
         */
        public void setColumns(IndexColumnDefine[] columns) {
            this.columns = columns;
        }
        
        
    }
