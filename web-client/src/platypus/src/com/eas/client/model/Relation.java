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

/**
 *
 * @author Marat
 */
public class Relation {

    // persistent properties
    protected String leftEntityId = null;
    protected String leftParameter = null;
    protected String leftField = null;    
    
    protected String rightEntityId = null;
    protected String rightParameter = null;
    protected String rightField = null;

    // runtime properties
    protected Entity leftEntity = null;
    protected Entity rightEntity = null;

    public Relation()
    {
        super();
    }

    public Relation(Entity aLeftEntity, boolean isLeftField, String aLeftField, Entity aRightEntity, boolean isRightField, String aRightField)
    {
        this();
        leftEntity = aLeftEntity;
        leftEntityId = leftEntity.getEntityId();
        if(isLeftField)
            leftField = aLeftField;
        else
            leftParameter = aLeftField;
        rightEntity = aRightEntity;
        rightEntityId = rightEntity.getEntityId();
        if(isRightField)
            rightField = aRightField;
        else
            rightParameter = aRightField;
    }

    public void accept(ModelVisitor visitor)
    {
        if(visitor != null)
            visitor.visit(this);
    }

    public Entity getLeftEntity() {
        return leftEntity;
    }
    
    public Entity getRightEntity() {
        return rightEntity;
    }
    
    public String getLeftEntityId() {
        return leftEntityId;
    }

    public void setLeftEntityId(String aValue) {
        leftEntityId = aValue;
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String aValue) {
        leftField = aValue;
    }

    public String getLeftParameter() {
        return leftParameter;
    }

    public void setLeftParameter(String aValue) {
        leftParameter = aValue;
    }

    public String getRightEntityId() {
        return rightEntityId;
    }

    public void setRightEntityId(String aValue) {
        rightEntityId = aValue;
    }

    public String getRightField() {
        return rightField;
    }

    public void setRightField(String aValue) {
        rightField = aValue;
    }

    public String getRightParameter() {
        return rightParameter;
    }

    public void setRightParameter(String aValue) {
        rightParameter = aValue;
    }

    public boolean isLeftParameter()
    {
        return !isLeftField();
    }
    
    public boolean isRightParameter()
    {
        return !isRightField();
    }
    
    public boolean isLeftField()
    {
        return (leftField != null && !leftField.isEmpty());
    }
    
    public boolean isRightField()
    {
        return (rightField != null && !rightField.isEmpty());
    }

    public void setLEntity(Entity aValue) {
        leftEntity = aValue;
    }

    public void setREntity(Entity aValue) {
        rightEntity = aValue;
    }
    
    public Relation copy() {
        Relation copied = new Relation();
        copied.setLeftEntityId(leftEntityId);
        copied.setLeftField(leftField);
        copied.setLeftParameter(leftParameter);
        copied.setRightEntityId(rightEntityId);
        copied.setRightField(rightField);
        copied.setRightParameter(rightParameter);
        return copied;
    }

}
