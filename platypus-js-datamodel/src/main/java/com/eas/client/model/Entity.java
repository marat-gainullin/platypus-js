package com.eas.client.model;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.util.IdGenerator;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 * @param <M>
 * @param <Q>
 * @param <E>
 */
public abstract class Entity<M extends Model<E, Q>, Q extends Query, E extends Entity<M, Q, E>> {

    public static final String MODEL_PROPERTY = "model";
    public static final String ENTITY_ID_PROPERTY = "entityId";
    public static final String X_PROPERTY = "x";
    public static final String Y_PROPERTY = "y";
    public static final String WIDTH_PROPERTY = "width";
    public static final String HEIGHT_PROPERTY = "height";
    public static final String ICONIFIED_PROPERTY = "iconified";
    public static final String TITLE_PROPERTY = "title";
    public static final String NAME_PROPERTY = "name";
    public static final String QUERY_ID_PROPERTY = "queryName";
    public static final String TABLE_DATASOURCE_NAME_PROPERTY = "tableDatasourceName";
    public static final String TABLE_NAME_PROPERTY = "tableName";
    public static final String TABLE_SCHEMA_NAME_PROPERTY = "tableSchemaName";
    public static final String QUERY_PROPERTY = "query";
    public static final String QUERY_VALID_PROPERTY = "queryValid";
    // stored data
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean iconified;
    protected String title;
    protected String name;
    protected Long entityId = IdGenerator.genId();
    protected String queryName;
    protected String tableDatasourceName;
    protected String tableSchemaName;
    protected String tableName;
    // runtime data
    protected transient M model;
    protected transient Q query;
    protected transient Set<Relation<E>> inRelations = new HashSet<>();
    protected transient Set<Relation<E>> outRelations = new HashSet<>();
    protected PropertyChangeSupport changeSupport;

    public Entity() {
        super();
        changeSupport = new PropertyChangeSupport(this);
    }

    public Entity(M aModel) {
        this();
        model = aModel;
    }

    public Entity(String aQueryName) {
        this();
        setQueryName(aQueryName);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public boolean isQuery() {
        return query != null;
    }

    public abstract void validateQuery() throws Exception;

    public Fields getFields() {
        if (query != null) {
            return query.getFields();
        } else {
            return null;
        }
    }

    /**
     *
     * @return True if entity's contents (Query) changed
     * @throws Exception
     */
    public boolean validate() throws Exception {
        Q oldQuery = query;
        Fields oldFields = oldQuery != null ? oldQuery.getFields() : null;
        Parameters oldParams = oldQuery != null ? oldQuery.getParameters() : null;
        query = null;
        validateQuery();
        Q newQuery = getQuery();
        Fields newFields = newQuery != null ? newQuery.getFields() : null;
        Parameters newParams = newQuery != null ? newQuery.getParameters() : null;

        boolean res = false;
        if (oldFields == null ? newFields != null : !oldFields.isEqual(newFields)) {
            res = true;
        }
        if (oldParams == null ? newParams != null : !oldParams.isEqual(newParams)) {
            res = true;
        }
        if (!res) {
            query = oldQuery;
        }
        return res;
    }

    public void clearFields() {
        query = null;
    }

    public M getModel() {
        return model;
    }

    public void regenerateId() {
        entityId = IdGenerator.genId();
    }

    public void setModel(M aValue) {
        M oldValue = model;
        model = aValue;
        changeSupport.firePropertyChange(MODEL_PROPERTY, oldValue, aValue);
    }

    public abstract void accept(ModelVisitor<E, M> visitor);

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
        return title;
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
    protected String getTableNameForDescription() {
        String fullTableName = tableName;
        if (getTableSchemaName() != null && !getTableSchemaName().isEmpty()) {
            fullTableName = getTableSchemaName() + "." + tableName;
        }
        return fullTableName;
    }

    public String getQueryName() {
        return queryName;
    }

    public final void setQueryName(String aValue) {
        String oldValue = queryName;
        queryName = aValue;
        changeSupport.firePropertyChange(QUERY_ID_PROPERTY, oldValue, aValue);
    }

    public String getTableDatasourceName() {
        return tableDatasourceName;
    }

    public String getTableSchemaName() {
        return tableSchemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableDatasourceName(String aValue) {
        String oldValue = tableDatasourceName;
        tableDatasourceName = aValue;
        changeSupport.firePropertyChange(TABLE_DATASOURCE_NAME_PROPERTY, oldValue, aValue);
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

    public Q getQuery() {
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
        assignTo.setQueryName(queryName);
        assignTo.setTableDatasourceName(tableDatasourceName);
        assignTo.setTableName(tableName);
        assignTo.setTableSchemaName(tableSchemaName);
        assignTo.setTitle(getTitle());
        assignTo.setName(getName());
    }

    public static <E extends Entity<?, ?, E>> Set<Relation<E>> getInOutRelationsByEntityField(E aEntity, Field aField) {
        Set<Relation<E>> result = new HashSet<>();
        Set<Relation<E>> rels = aEntity.getInRelations();
        if (rels != null) {
            for (Relation<E> rel : rels) {
                if (rel.getRightField() == aField) {
                    result.add(rel);
                }
            }
        }
        rels = aEntity.getOutRelations();
        if (rels != null) {
            for (Relation<E> rel : rels) {
                if (rel.getLeftField() == aField) {
                    result.add(rel);
                }
            }
        }
        return result;
    }

}
