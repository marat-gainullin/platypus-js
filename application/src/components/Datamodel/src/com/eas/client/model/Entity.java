/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class Entity<M extends Model<E, ?, ?, Q>, Q extends Query<?>, E extends Entity<M, Q, E>> {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean iconified;
    protected String title;
    protected String name; // datasource name
    protected Long entityId = IDGenerator.genID();
    protected String queryId;
    protected String tableDbId;
    protected String tableSchemaName;
    protected String tableName = null;
    protected transient M model;
    protected transient Q query;
    protected transient Set<Relation<E>> inRelations = new HashSet<>();
    protected transient Set<Relation<E>> outRelations = new HashSet<>();
    protected Fields fields;
    protected PropertyChangeSupport changeSupport;
    public static final String MODEL_PROPERTY = "model";
    public static final String ENTITY_ID_PROPERTY = "entityId";
    public static final String X_PROPERTY = "x";
    public static final String Y_PROPERTY = "y";
    public static final String WIDTH_PROPERTY = "width";
    public static final String HEIGHT_PROPERTY = "height";
    public static final String ICONIFIED_PROPERTY = "iconified";
    public static final String TITLE_PROPERTY = "title";
    public static final String NAME_PROPERTY = "name";
    public static final String QUERY_ID_PROPERTY = "queryId";
    public static final String TABLE_DB_ID_PROPERTY = "tableDbId";
    public static final String TABLE_NAME_PROPERTY = "tableName";
    public static final String TABLE_SCHEMA_NAME_PROPERTY = "tableSchemaName";
    public static final String QUERY_PROPERTY = "query";

    public Entity() {
        super();
        changeSupport = new PropertyChangeSupport(this);
    }

    public Entity(M aModel) {
        this();
        model = aModel;
    }

    public Entity(String aQueryId) {
        this();
        setQueryId(aQueryId);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public abstract void validateQuery() throws Exception;

    public Fields getFields() {
        if (fields == null && model != null && model.getClient() != null) {
            try {
                validateQuery();
                if (query != null) {
                    fields = query.getFields();
                }
            } catch (Exception ex) {
                fields = null;
                Logger.getLogger(Entity.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        return fields;
    }

    public boolean validate() throws Exception {
        boolean res = false;
        Q oldQuery = getQuery();
        Fields oldFields = getFields();
        Parameters oldParams = oldQuery != null ? oldQuery.getParameters() : null;
        clearFields();
        validateQuery();
        Q newQuery = getQuery();
        Fields newFields = getFields();
        Parameters newParams = newQuery != null ? newQuery.getParameters() : null;
        if (newFields == null ? oldFields != null : !newFields.isEqual(oldFields)) {
            res = true;
        }
        if (newParams == null ? oldParams != null : !newParams.isEqual(oldParams)) {
            res = true;
        }
        if (!res) {
            query = oldQuery;
            fields = oldFields;
        }else{
            // Let's fire an event about query change
            query = null;
            setQuery(newQuery);
        }
        return res;
    }

    public void clearFields() {
        fields = null;
        query = null;
    }

    public M getModel() {
        return model;
    }

    public void regenerateId() {
        entityId = IDGenerator.genID();
    }

    public void setModel(M aValue) {
        M oldValue = model;
        model = aValue;
        changeSupport.firePropertyChange(MODEL_PROPERTY, oldValue, aValue);
    }

    public abstract void accept(ModelVisitor<E> visitor);

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long aValue) {
        Long oldValue = entityId;
        entityId = aValue;
        changeSupport.firePropertyChange(ENTITY_ID_PROPERTY, oldValue, aValue);
    }

    public int getX() {
        return x;
    }

    public void setX(int aValue) {
        int oldValue = x;
        x = aValue;
        changeSupport.firePropertyChange(X_PROPERTY, oldValue, aValue);
    }

    public int getY() {
        return y;
    }

    public void setY(int aValue) {
        int oldValue = y;
        y = aValue;
        changeSupport.firePropertyChange(Y_PROPERTY, oldValue, aValue);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int aValue) {
        int oldValue = width;
        width = aValue;
        changeSupport.firePropertyChange(WIDTH_PROPERTY, oldValue, aValue);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int aValue) {
        int oldValue = height;
        height = aValue;
        changeSupport.firePropertyChange(HEIGHT_PROPERTY, oldValue, aValue);
    }

    public boolean isIconified() {
        return iconified;
    }

    public void setIconified(boolean aValue) {
        boolean oldValue = iconified;
        iconified = aValue;
        changeSupport.firePropertyChange(ICONIFIED_PROPERTY, oldValue, aValue);
    }

    public String getTitle() {
        String ltitle = title;
        if (ltitle == null || ltitle.isEmpty()) {
            if (queryId != null) {
                try {
                    Q lquery = getQuery();
                    ltitle = lquery.getTitle();
                } catch (Exception ex) {
                    Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
                    ltitle = "";
                }
                title = ltitle;
            } else if (tableName != null) {
                Fields lfields = getFields();
                if (lfields != null) {
                    ltitle = lfields.getTableDescription();
                    if (ltitle == null || ltitle.isEmpty() || ltitle.equalsIgnoreCase(tableName)) {
                        ltitle = getFullTableNameEntityForDescription();
                    }
                    title = ltitle;
                }
            }
        }
        return ltitle;
    }

    public void setTitle(String aValue) {
        if ((aValue == null && title != null) || (aValue != null && !aValue.equals(title))) {
            String oldValue = title;
            title = aValue;
            changeSupport.firePropertyChange(TITLE_PROPERTY, oldValue, title);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        if ((aValue == null && name != null) || (aValue != null && !aValue.equals(name))) {
            String oldValue = name;
            name = aValue;
            changeSupport.firePropertyChange(NAME_PROPERTY, oldValue, name);
        }
    }

    /**
     * Returns fully qualified table name for description purposes. Don't use
     * this method to achieve FQTN!
     *
     * @return Full table descripting name. like testSchema.testTable
     */
    protected String getFullTableNameEntityForDescription() {
        String fullTableName = tableName;
        if (getTableSchemaName() != null && !getTableSchemaName().isEmpty()) {
            fullTableName = getTableSchemaName() + "." + tableName;
        }
        return fullTableName;
    }

    public String getFormattedTableNameAndTitle() {
        String lTableName = getTableName();
        String lTitle = getTitle();
        return (lTableName != null ? lTableName : "") + (lTitle != null && !"".equals(lTitle) ? " (" + lTitle + ")" : ""); //NO18IN
    }

    public String getFormattedNameAndTitle() {
        String lName = getName();
        String lTitle = getTitle();
        return (lName != null ? lName : "") + (lTitle != null && !"".equals(lTitle) ? " (" + lTitle + ")" : ""); //NO18IN
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String aValue) {
        String oldValue = queryId;
        queryId = aValue;
        changeSupport.firePropertyChange(QUERY_ID_PROPERTY, oldValue, aValue);
    }

    public String getTableDbId() {
        return tableDbId;
    }

    public String getTableSchemaName() {
        return tableSchemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableDbId(String aValue) {
        String oldValue = tableDbId;
        tableDbId = aValue;
        changeSupport.firePropertyChange(TABLE_DB_ID_PROPERTY, oldValue, aValue);
    }

    public void setTableName(String aValue) {
        String oldValue = tableName;
        tableName = aValue;
        changeSupport.firePropertyChange(TABLE_NAME_PROPERTY, oldValue, aValue);
    }

    public void setTableSchemaName(String aValue) {
        String oldValue = tableSchemaName;
        tableSchemaName = aValue;
        changeSupport.firePropertyChange(TABLE_SCHEMA_NAME_PROPERTY, oldValue, aValue);
    }

    public Q getQuery() throws Exception {
        try {
            validateQuery();
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.WARNING, null, ex);
            query = null;
            throw ex;
        }
        return query;
    }

    public void setQuery(Q aValue) {
        Q oldValue = query;
        query = aValue;
        changeSupport.firePropertyChange(QUERY_PROPERTY, oldValue, aValue);
    }

    public boolean removeOutRelation(Relation<E> aRelation) {
        return outRelations.remove(aRelation);
    }

    public boolean removeInRelation(Relation<E> aRelation) {
        return inRelations.remove(aRelation);
    }

    public boolean addOutRelation(Relation<E> aRelation) {
        return outRelations.add(aRelation);
    }

    public boolean addInRelation(Relation<E> aRelation) {
        return inRelations.add(aRelation);
    }

    public Set<Relation<E>> getInRelations() {
        return inRelations;
    }

    public Set<Relation<E>> getOutRelations() {
        return outRelations;
    }

    public Set<Relation<E>> getInOutRelations() {
        Set<Relation<E>> lInOutRelations = new HashSet<>();
        lInOutRelations.addAll(inRelations);
        lInOutRelations.addAll(outRelations);
        return lInOutRelations;
    }

    protected boolean isTagValid(String aTagName) {
        return true;
    }

    public E copy() throws Exception {
        E copied;
        if (model != null) {
            copied = model.newGenericEntity();
        } else {
            copied = ((Class<E>) getClass()).newInstance();
        }
        assign(copied);
        return copied;
    }

    protected void assign(E assignTo) throws Exception {
        assignTo.setEntityId(entityId);
        assignTo.setQueryId(queryId);
        assignTo.setTableDbId(tableDbId);
        assignTo.setTableName(tableName);
        assignTo.setTableSchemaName(tableSchemaName);
        assignTo.setTitle(getTitle());
        assignTo.setName(getName());
    }
}
