/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DbTableComments {

    protected String tableComment = "";
    protected Map<String, String> fieldsComments = new HashMap<>();

    public DbTableComments() {
        super();
    }

    private DbTableComments(DbTableComments aSource) {
        if (aSource != null) {
            String ltblComment = aSource.getTableComment();
            if (ltblComment != null) {
                tableComment = new String(ltblComment.toCharArray());
            }
            Map<String, String> lfieldsComments = aSource.getFieldsComments();
            if (lfieldsComments != null) {
                Set<Entry<String, String>> lfEntries = lfieldsComments.entrySet();
                if (lfEntries != null) {
                    Iterator<Entry<String, String>> lfEntIt = lfEntries.iterator();
                    if (lfEntIt != null) {
                        fieldsComments.clear();
                        while (lfEntIt.hasNext()) {
                            Entry<String, String> lfEnt = lfEntIt.next();
                            String fieldName = lfEnt.getKey();
                            String fieldComment = lfEnt.getValue();
                            if (fieldName != null && !fieldName.isEmpty()
                                    && fieldComment != null && !fieldComment.isEmpty()) {
                                fieldsComments.put(new String(fieldName.toCharArray()), new String(fieldComment.toCharArray()));
                            }
                        }
                    }
                }
            }
        }
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getTableComment() {
        return tableComment;
    }

    public String getFieldComment(String fieldName) {
        return fieldsComments.get(fieldName);
    }

    public String setFieldComment(String fieldName, String comment) {
        return fieldsComments.put(fieldName, comment);
    }

    public Map<String, String> getFieldsComments() {
        return fieldsComments;
    }

    public void clear() {
        fieldsComments.clear();
    }

    public DbTableComments copy() {
        DbTableComments dbtc = new DbTableComments(this);
        return dbtc;
    }
}
