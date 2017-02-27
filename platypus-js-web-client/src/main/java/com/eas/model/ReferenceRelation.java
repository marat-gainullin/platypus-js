package com.eas.model;

import com.eas.client.metadata.Field;

public class ReferenceRelation extends Relation {
    protected String scalarPropertyName;
    protected String collectionPropertyName;

    public ReferenceRelation() {
        super();
    }
    
    public ReferenceRelation(Entity aLeftEntity, Field aLeftField, Entity aRightEntity, Field aRightField) {
        super(aLeftEntity, aLeftField, aRightEntity, aRightField);
    }

    @Override
    public void accept(ModelVisitor visitor) {
		if (visitor != null)
			visitor.visit(this);
    }

    public String getScalarPropertyName() {
        return scalarPropertyName;
    }

    public void setScalarPropertyName(String aValue) {
        scalarPropertyName = aValue;
    }

    public String getCollectionPropertyName() {
        return collectionPropertyName;
    }

    public void setCollectionPropertyName(String aValue) {
        collectionPropertyName = aValue;
    }

    @Override
    public Relation copy() {
        ReferenceRelation copied = new ReferenceRelation();
        assign(copied);
        return copied;
    }
    
    @Override
    protected void assign(Relation target) {
        super.assign(target);
        ((ReferenceRelation)target).setScalarPropertyName(scalarPropertyName);
        ((ReferenceRelation)target).setCollectionPropertyName(collectionPropertyName);
    }
}
