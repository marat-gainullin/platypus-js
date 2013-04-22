/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class ModelEntityRef extends ModelElementRef{

    public ModelEntityRef() {
        super();
    }

    public ModelEntityRef(Long aEntityId) {
        super(aEntityId);
    }

    public ModelEntityRef(Field aFieldMd, boolean aIsField, Long aEntityId) {
        super(aFieldMd, aIsField, aEntityId);
    }

    private ModelEntityRef(ModelEntityRef aSource) {
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
    public ModelEntityRef copy() {
        return new ModelEntityRef(this);
    }
}
