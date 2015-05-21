/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class ModelElementRef {

    public Long entityId;
    public boolean isField = true;
    public Field field;

    public ModelElementRef() {
        super();
    }

    public ModelElementRef(Long aEntityId) {
        this();
        entityId = aEntityId;
        isField = false;
    }

    public ModelElementRef(Field aFieldMd, boolean aIsField, Long aEntityId) {
        this();
        field = aFieldMd;
        isField = aIsField;
        entityId = aEntityId;
    }

    protected ModelElementRef(ModelElementRef aSource) {
        if (aSource != null) {
            assign(aSource);
        }
    }

    public final void assign(ModelElementRef aSource) {
        entityId = aSource.entityId != null ? new Long(aSource.entityId) : null;
        isField = aSource.isField;
        field = aSource.field != null ? (aSource.isField() ? new Field(aSource.getField()) : new Parameter(aSource.getField())) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelElementRef other = (ModelElementRef) obj;
        if (this.entityId != other.entityId && (this.entityId == null || !this.entityId.equals(other.entityId))) {
            return false;
        }
        if (this.isField != other.isField) {
            return false;
        }
        if (this.field != other.field && (this.field == null || !this.field.isEqual(other.field))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.entityId != null ? this.entityId.hashCode() : 0);
        hash = 71 * hash + (this.isField ? 1 : 0);
        hash = 71 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }

    public ModelElementRef copy() {
        return new ModelElementRef(this);
    }

    @Serial
    public Long getEntityId() {
        return entityId;
    }

    @Serial
    public void setEntityId(Long aValue) {
        entityId = aValue;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field aFieldMd) {
        field = aFieldMd;
    }

    @Serial
    public String getFieldName() {
        return field != null ? field.getName() : null;
    }

    @Serial
    public void setFieldName(String aName) {
        field = isField() ? new Field() : new Parameter();
        field.setName(aName);
    }

    @Serial
    public boolean isField() {
        return isField;
    }

    @Serial
    public void setField(boolean aField) {
        boolean oldValue = isField;
        isField = aField;
        if (oldValue && !isField && field != null) {
            field = new Parameter(field);
        }
    }
}
