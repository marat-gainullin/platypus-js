/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.controls.DesignInfo;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.store.Serial;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class RowsetFeatureDescriptor extends DesignInfo {

    public static final String ACTIVE = "active";
    public static final String CRSWKT = "crsWkt";
    public static final String ENTITY = "entity";
    public static final String GEOMETRYBINDING = "geometryBinding";
    public static final String REF = "ref";
    public static final String SELECTABLE = "selectable";
    public static final String STYLE = "style";
    public static final String TYPENAME = "typeName";
    public static final String TYPEREF = "typeRef";
    public static final String TYPEVALUE = "typeValue";
    private String typeName;
    private String geometryBinding = Polygon.class.getName();
    private String crsWkt;
    private boolean active;
    private boolean selectable;
    private FeatureStyleDescriptor style = new FeatureStyleDescriptor();
    private ModelEntityRef ref;
    private ModelElementRef typeRef;
    private Integer typeValue;
    private ApplicationEntity<?, ?, ?> entity;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    protected RowsetFeatureDescriptor(String aTypeName, ApplicationEntity<?, ?, ?> aEntity) {
        super();
        typeName = aTypeName;
        entity = aEntity;
        style.setGeometryField(getGeometryField());
    }

    public RowsetFeatureDescriptor(String aTypeName, ApplicationEntity<?, ?, ?> aEntity, ModelEntityRef aRef) {
        super();
        typeName = aTypeName;
        ref = aRef;
        entity = aEntity;
        style.setGeometryField(getGeometryField());
        assert aRef.entityId != null;
        assert aEntity != null;
        assert aRef.entityId.equals(aEntity.getEntityId());
    }

    public RowsetFeatureDescriptor() {
        super();
    }

    @Override
    public RowsetFeatureDescriptor copy() {
        RowsetFeatureDescriptor copy = new RowsetFeatureDescriptor();
        copy.assign(this);
        return copy;
    }

    @Undesignable
    public ApplicationEntity<?, ?, ?> getEntity() {
        return entity;
    }

    public void setEntity(ApplicationEntity<?, ?, ?> aValue) {
        ApplicationEntity<?, ?, ?> old = entity;
        entity = aValue;
        style.setGeometryField(getGeometryField());
        changeSupport.firePropertyChange(ENTITY, old, aValue);
    }

    @Undesignable
    @Serial
    public FeatureStyleDescriptor getStyle() {
        return style;
    }

    @Undesignable
    @Serial
    public void setStyle(FeatureStyleDescriptor aValue) {
        FeatureStyleDescriptor old = style;
        style = aValue == null ? new FeatureStyleDescriptor() : aValue;
        changeSupport.firePropertyChange(STYLE, old, aValue);
    }

    @Serial
    public String getGeometryBinding() {
        return geometryBinding;
    }

    @Serial
    public void setGeometryBinding(String aValue) {
        String old = geometryBinding;
        if (aValue == null) {
            throw new NullPointerException(GEOMETRYBINDING);
        }
        geometryBinding = aValue;
        changeSupport.firePropertyChange(GEOMETRYBINDING, old, aValue);
    }

    @Override
    public void assign(DesignInfo aInfo) {
        RowsetFeatureDescriptor source = (RowsetFeatureDescriptor) aInfo;
        active = source.active;
        crsWkt = source.crsWkt;
        selectable = source.selectable;
        geometryBinding = source.geometryBinding;
        ref = source.ref != null ? source.ref.copy() : null;
        typeRef = source.typeRef != null ? source.typeRef.copy() : null;
        typeValue = source.typeValue;
        typeName = source.typeName;
        try {
            style = source.style != null ? source.style.clone() : null;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(RowsetFeatureDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        entity = source.entity;
    }

    @Override
    public boolean isEqual(Object o) {
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final RowsetFeatureDescriptor other = (RowsetFeatureDescriptor) o;
        if (active != other.active) {
            return false;
        }
        crsWkt = other.crsWkt;
        if (selectable != other.selectable) {
            return false;
        }
        if (this.geometryBinding != other.geometryBinding && (this.geometryBinding == null || !this.geometryBinding.equals(other.geometryBinding))) {
            return false;
        }
        if (this.ref != other.ref && (this.ref == null || !this.ref.equals(other.ref))) {
            return false;
        }
        if (this.typeRef != other.typeRef && (this.typeRef == null || !this.typeRef.equals(other.typeRef))) {
            return false;
        }
        if (this.typeValue != other.typeValue && (this.typeValue == null || !this.typeValue.equals(other.typeValue))) {
            return false;
        }
        if (this.typeName != other.typeName && (this.typeName == null || !this.typeName.equals(other.typeName))) {
            return false;
        }
        if (this.style != other.style && (this.style == null || !this.style.isEqual(other.style))) {
            return false;
        }
        return true;
    }

    @Serial
    public boolean isActive() {
        return active;
    }

    @Serial
    public void setActive(boolean aValue) {
        boolean old = active;
        active = aValue;
        changeSupport.firePropertyChange(ACTIVE, old, aValue);
    }

    @Serial
    public boolean isSelectable() {
        return selectable;
    }

    @Serial
    public void setSelectable(boolean aValue) {
        boolean old = selectable;
        selectable = aValue;
        changeSupport.firePropertyChange(SELECTABLE, old, aValue);
    }

    @Undesignable
    public Class<? extends Geometry> getGeometryBindingClass() throws ClassNotFoundException {
        return (Class<? extends Geometry>) Class.forName(geometryBinding);
    }

    public void setGeometryBindingClass(Class<?> aValue) {
        String old = geometryBinding;
        if (aValue == null) {
            throw new NullPointerException("binding");
        }
        geometryBinding = aValue.getName();
        changeSupport.firePropertyChange(GEOMETRYBINDING, old, aValue);
    }

    /**
     * Returns datamodel reference to entity containing rowset with geometry
     * data.
     *
     * @return ModelElementRef instance.
     */
    @Designable(displayName = "entity", category = "model")
    @Serial
    public ModelEntityRef getRef() {
        if (this.ref != null) {
            return this.ref;
        } else if (this.entity != null) {
            this.ref = new ModelEntityRef();
            this.ref.setEntityId(this.entity.getEntityId());
            this.ref.setField(false);
            return this.ref;
        } else {
            return null;
        }
    }

    /**
     * Sets datamodel reference to entity containing rowset with geometry data.
     *
     * @param aValue DatamodelElementRef instance to set.
     */
    @Serial
    public void setRef(ModelEntityRef aValue) {
        ModelEntityRef old = ref;
        ref = aValue;
        changeSupport.firePropertyChange(REF, old, aValue);
    }

    @Undesignable()
    @Serial
    public String getTypeName() {
        return typeName;
    }

    @Serial
    public void setTypeName(String aValue) {
        String old = typeName;
        typeName = aValue;
        changeSupport.firePropertyChange(TYPENAME, old, aValue);
    }

    /**
     * Returns datamodel element reference. It describes what field of what
     * datasource will be used as type classification source.
     *
     * @return DatamodelElementRef instance.
     */
    @Serial
    public ModelElementRef getTypeRef() {
        return typeRef;
    }

    /**
     * Sets datamodel element reference for type classification. It describes
     * what field of what datasource will be used as type classification source.
     *
     * @param aValue DatamodelElementRef instance to set.
     */
    @Designable(displayName = "typeField", category = "model")
    @Serial
    public void setTypeRef(ModelElementRef aValue) {
        ModelElementRef old = typeRef;
        typeRef = aValue;
        changeSupport.firePropertyChange(TYPEREF, old, aValue);
    }

    /**
     * Returns type classification value. May be null.
     *
     * @return
     */
    @Serial
    public Integer getTypeValue() {
        return typeValue;
    }

    /**
     * Sets type classification value.
     *
     * @param aValue value to use with classification. It may be null. This
     * means that classification will be performed with null values only. In
     * other words null is normal ordinary value, acceptable for classification.
     */
    @Designable(category = "model")
    @Serial
    public void setTypeValue(Integer aValue) {
        Integer old = typeValue;
        typeValue = aValue;
        changeSupport.firePropertyChange(TYPEVALUE, old, aValue);
    }

    @Serial
    public String getCrsWkt() {
        return crsWkt;
    }

    @Serial
    public void setCrsWkt(String aValue) {
        String old = crsWkt;
        crsWkt = aValue;
        changeSupport.firePropertyChange(CRSWKT, old, aValue);
    }

    @Designable(category = "appearance")
    public Color getFillColor() {
        return style.getFillColor();
    }

    public void setFillColor(Color aValue) {
        style.setFillColor(aValue);
    }

    @Designable(category = "appearance")
    public Color getHaloColor() {
        return style.getHaloColor();
    }

    public void setHaloColor(Color aValue) {
        style.setHaloColor(aValue);
    }

    @Designable(category = "appearance")
    public Font getFont() {
        return style.getFont();
    }

    public void setFont(Font aValue) {
        style.setFont(aValue);
    }

    @Designable(category = "appearance")
    public ModelElementRef getLabelField() {
        return style.getLabelField();
    }

    public void setLabelField(ModelElementRef aValue) {
        style.setLabelField(aValue);
    }

    @Designable(category = "appearance")
    public Color getLineColor() {
        return style.getLineColor();
    }

    public void setLineColor(Color aValue) {
        style.setLineColor(aValue);
    }

    @Designable(category = "appearance")
    public Integer getOpacity() {
        return style.getOpacity();
    }

    public void setOpacity(Integer aValue) {
        style.setOpacity(aValue);
    }

    @Designable(displayName = "pointSymbol", category = "appearance")
    public String getPointSymbolName() {
        return style.getPointSymbolName();
    }

    public void setPointSymbolName(String aValue) {
        style.setPointSymbolName(aValue);
    }

    @Designable(category = "appearance")
    public Float getSize() {
        return style.getSize();
    }

    public void setSize(Float aValue) {
        style.setSize(aValue);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    private ModelElementRef getGeometryField() {
        if (entity != null) {
            for (Field field : entity.getFields().toCollection()) {
                if (DatamodelDataStore.isGeometry(field.getTypeInfo())) {
                    return new ModelElementRef(field, true, entity.getEntityId());
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Features rowset, type %s, entity %s", String.valueOf(typeName), String.valueOf(entity)); //NOI18N
    }
}
