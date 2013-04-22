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

import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.model.visitors.ModelVisitor;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author mg
 */
public class Relation<E extends Entity<?, ?, E>> {

    // persistent properties
    protected Long leftEntityId = null;
    protected String leftParameter = null;
    protected String leftField = null;
    protected Long rightEntityId = null;
    protected String rightParameter = null;
    protected String rightField = null;
    // runtime and persistent properties
    protected String fkName;
    protected ForeignKeyRule fkDeleteRule;
    protected ForeignKeyRule fkUpdateRule;
    protected boolean fkDeferrable = true;
    // runtime properties
    protected E leftEntity = null;
    protected E rightEntity = null;
    protected PropertyChangeSupport changeSupport;

    public Relation() {
        super();
        changeSupport = new PropertyChangeSupport(this);
    }

    public Relation(E aLeftEntity, boolean isLeftField, String aLeftField, E aRightEntity, boolean isRightField, String aRightField) {
        this();
        leftEntity = aLeftEntity;
        leftEntityId = leftEntity.getEntityID();
        if (isLeftField) {
            leftField = aLeftField;
        } else {
            leftParameter = aLeftField;
        }
        rightEntity = aRightEntity;
        rightEntityId = rightEntity.getEntityID();
        if (isRightField) {
            rightField = aRightField;
        } else {
            rightParameter = aRightField;
        }
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public ForeignKeyRule getFkDeleteRule() {
        return fkDeleteRule;
    }

    public void setFkDeleteRule(ForeignKeyRule aValue) {
        ForeignKeyRule oldValue = fkDeleteRule;
        fkDeleteRule = aValue;
        changeSupport.firePropertyChange("fkDeleteRule", oldValue, aValue);
    }

    public ForeignKeyRule getFkUpdateRule() {
        return fkUpdateRule;
    }

    public void setFkUpdateRule(ForeignKeyRule aValue) {
        ForeignKeyRule oldValue = fkUpdateRule;
        fkUpdateRule = aValue;
        changeSupport.firePropertyChange("fkUpdateRule", oldValue, aValue);
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String aValue) {
        String oldValue = fkName;
        fkName = aValue;
        changeSupport.firePropertyChange("fkName", oldValue, aValue);
    }

    public boolean isFkDeferrable() {
        return fkDeferrable;
    }

    public void setFkDeferrable(boolean aValue) {
        boolean oldValue = fkDeferrable;
        fkDeferrable = aValue;
        changeSupport.firePropertyChange("fkDeferrable", oldValue, aValue);
    }

    public void accept(ModelVisitor<E> visitor) {
        if (visitor != null) {
            visitor.visit(this);
        }
    }

    public E getLeftEntity() {
        return leftEntity;
    }

    public E getRightEntity() {
        return rightEntity;
    }

    public Long getLeftEntityId() {
        return leftEntityId;
    }

    public void setLeftEntityId(Long aValue) {
        Long oldValue = leftEntityId;
        leftEntityId = aValue;
        changeSupport.firePropertyChange("leftEntityId", oldValue, aValue);
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String aValue) {
        String oldValue = leftField;
        leftField = aValue;
        changeSupport.firePropertyChange("leftField", oldValue, aValue);
    }

    public String getLeftParameter() {
        return leftParameter;
    }

    public void setLeftParameter(String aValue) {
        String oldValue = leftParameter;
        leftParameter = aValue;
        changeSupport.firePropertyChange("leftParameter", oldValue, aValue);
    }

    public Long getRightEntityId() {
        return rightEntityId;
    }

    public void setRightEntityId(Long aValue) {
        Long oldValue = rightEntityId;
        rightEntityId = aValue;
        changeSupport.firePropertyChange("rightEntityId", oldValue, aValue);
    }

    public String getRightField() {
        return rightField;
    }

    public void setRightField(String aValue) {
        String oldValue = rightField;
        rightField = aValue;
        changeSupport.firePropertyChange("rightField", oldValue, aValue);
    }

    public String getRightParameter() {
        return rightParameter;
    }

    public void setRightParameter(String aValue) {
        String oldValue = rightParameter;
        rightParameter = aValue;
        changeSupport.firePropertyChange("rightParameter", oldValue, aValue);
    }

    public boolean isLeftParameter() {
        return !isLeftField();
    }

    public boolean isRightParameter() {
        return !isRightField();
    }

    public boolean isLeftField() {
        return (leftField != null && !leftField.isEmpty());
    }

    public boolean isRightField() {
        return (rightField != null && !rightField.isEmpty());
    }

    public void setLEntity(E aValue) {
        E oldValue = leftEntity;
        leftEntity = aValue;
        changeSupport.firePropertyChange("leftEntity", oldValue, aValue);
    }

    public void setREntity(E aValue) {
        E oldValue = rightEntity;
        rightEntity = aValue;
        changeSupport.firePropertyChange("rightEntity", oldValue, aValue);
    }

    public Relation<E> copy() {
        Relation<E> copied = new Relation<>();
        copied.setLeftEntityId(leftEntityId);
        copied.setLeftField(leftField);
        copied.setLeftParameter(leftParameter);
        copied.setRightEntityId(rightEntityId);
        copied.setRightField(rightField);
        copied.setRightParameter(rightParameter);
        copied.setFkDeferrable(fkDeferrable);
        copied.setFkDeleteRule(fkDeleteRule);
        copied.setFkUpdateRule(fkUpdateRule);
        copied.setFkName(fkName);
        return copied;
    }
}
