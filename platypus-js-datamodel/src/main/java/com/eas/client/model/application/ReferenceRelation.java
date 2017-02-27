package com.eas.client.model.application;

import com.eas.client.metadata.Field;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.model.visitors.ModelVisitor;

/**
 * Relation for design puporses. It's not serialized and is used only in
 * designer.
 *
 * @author mg
 * @param <E>
 */
public class ReferenceRelation<E extends ApplicationEntity<?, ?, E>> extends Relation<E> {

    protected String scalarPropertyName;
    protected String collectionPropertyName;

    public ReferenceRelation() {
        super();
    }
    
    public ReferenceRelation(E aLeftEntity, Field aLeftField, E aRightEntity, Field aRightField) {
        super(aLeftEntity, aLeftField, aRightEntity, aRightField);
    }

    public String getScalarPropertyName() {
        return scalarPropertyName;
    }

    public void setScalarPropertyName(String aValue) {
        String oldValue = scalarPropertyName;
        scalarPropertyName = aValue;
        changeSupport.firePropertyChange(SCALAR_PROPERTY_NAME, oldValue, scalarPropertyName);
    }
    public static final String SCALAR_PROPERTY_NAME = "scalarPropertyName";

    public String getCollectionPropertyName() {
        return collectionPropertyName;
    }

    public void setCollectionPropertyName(String aValue) {
        String oldValue = collectionPropertyName;
        collectionPropertyName = aValue;
        changeSupport.firePropertyChange(COLLECTION_PROPERTY_NAME, oldValue, collectionPropertyName);
    }
    public static final String COLLECTION_PROPERTY_NAME = "collectionPropertyName";

    @Override
    public Relation<E> copy() {
        ReferenceRelation<E> copied = new ReferenceRelation<>();
        assign(copied);
        return copied;
    }
    
    @Override
    protected void assign(Relation<E> target) {
        super.assign(target);
        ((ReferenceRelation<E>)target).setScalarPropertyName(scalarPropertyName);
        ((ReferenceRelation<E>)target).setCollectionPropertyName(collectionPropertyName);
    }

    @Override
    public void accept(ModelVisitor<E, ?> visitor) {
        ((ApplicationModelVisitor)visitor).visit(this);
    }
 
}
