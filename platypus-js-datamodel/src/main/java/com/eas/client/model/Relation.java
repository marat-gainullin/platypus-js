package com.eas.client.model;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.visitors.ModelVisitor;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author mg
 * @param <E>
 */
public class Relation<E extends Entity<?, ?, E>> {

    // runtime and persistent properties
    protected String fkName;
    protected ForeignKeyRule fkDeleteRule;
    protected ForeignKeyRule fkUpdateRule;
    protected boolean fkDeferrable = true;
    // runtime properties
    protected E leftEntity = null;
    protected E rightEntity = null;
    protected Field leftField;
    protected Field rightField;
    protected PropertyChangeSupport changeSupport;
    // design properties
    protected int[] xs;
    protected int[] ys;

    public Relation() {
        super();
        changeSupport = new PropertyChangeSupport(this);
    }

    public Relation(E aLeftEntity, Field aLeftField, E aRightEntity, Field aRightField) {
        this();
        leftEntity = aLeftEntity;
        leftField = aLeftField;
        rightEntity = aRightEntity;
        rightField = aRightField;
    }

    public int[] getXs() {
        return xs;
    }

    public int[] getYs() {
        return ys;
    }

    public void setXYs(int[] aXs, int[] aYs) {
        Object[] oldValue = new Object[]{xs, ys};
        xs = aXs;
        ys = aYs;
        changeSupport.firePropertyChange("polyline", oldValue, new Object[]{xs, ys});
    }

    public boolean isManual(){
        return xs != null && ys != null && xs.length == ys.length;
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

    public void accept(ModelVisitor<E, ?> visitor) {
        visitor.visit(this);
    }

    public E getLeftEntity() {
        return leftEntity;
    }

    public E getRightEntity() {
        return rightEntity;
    }

    public Field getLeftField() {
        return leftField;
    }

    public void setLeftField(Field aValue) {
        Field oldValue = leftField;
        leftField = aValue;
        changeSupport.firePropertyChange("leftField", oldValue, aValue);
    }

    public Field getRightField() {
        return rightField;
    }

    public void setRightField(Field aValue) {
        Field oldValue = rightField;
        rightField = aValue;
        changeSupport.firePropertyChange("rightField", oldValue, aValue);
    }

    public boolean isLeftParameter() {
        return leftField instanceof Parameter;
    }

    public boolean isRightParameter() {
        return rightField instanceof Parameter;
    }

    public boolean isLeftField() {
        return !isLeftParameter();
    }

    public boolean isRightField() {
        return !isRightParameter();
    }

    public void setLeftEntity(E aValue) {
        E oldValue = leftEntity;
        leftEntity = aValue;
        changeSupport.firePropertyChange("leftEntity", oldValue, aValue);
    }

    public void setRightEntity(E aValue) {
        E oldValue = rightEntity;
        rightEntity = aValue;
        changeSupport.firePropertyChange("rightEntity", oldValue, aValue);
    }

    public Relation<E> copy() {
        Relation<E> copied = new Relation<>();
        assign(copied);
        return copied;
    }

    public Parameter getLeftParameter() {
        if (isLeftParameter()) {
            return (Parameter) leftField;
        } else {
            return null;
        }
    }
    
    public Parameter getRightParameter() {
        if (isRightParameter()) {
            return (Parameter) rightField;
        } else {
            return null;
        }
    }

    protected void assign(Relation<E> target) {
        target.setLeftEntity(leftEntity);
        target.setLeftField(leftField);
        target.setRightEntity(rightEntity);
        target.setRightField(rightField);
        target.setFkDeferrable(fkDeferrable);
        target.setFkDeleteRule(fkDeleteRule);
        target.setFkUpdateRule(fkUpdateRule);
        target.setFkName(fkName);
    }
}
