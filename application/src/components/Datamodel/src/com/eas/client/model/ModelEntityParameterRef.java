/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.Field;

/**
 * Intended for use with entities' parameters, not with parameters of a model
 * @author mg
 */
public class ModelEntityParameterRef extends ModelElementRef{

    public ModelEntityParameterRef() {
        super();
    }

    public ModelEntityParameterRef(Long aEntityId) {
        super(aEntityId);
    }

    public ModelEntityParameterRef(Field aFieldMd, boolean aIsField, Long aEntityId) {
        super(aFieldMd, aIsField, aEntityId);
    }

    private ModelEntityParameterRef(ModelEntityParameterRef aSource) {
        super(aSource);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public ModelEntityParameterRef copy() {
        return new ModelEntityParameterRef(this);
    }
}
