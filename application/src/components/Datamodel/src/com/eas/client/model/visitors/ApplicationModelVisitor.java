/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.application.ReferenceRelation;

/**
 *
 * @author mg
 */
public interface ApplicationModelVisitor<E extends ApplicationEntity<?, ?, E>> extends ModelVisitor<E> {

    public void visit(ApplicationModel<E, ?, ?, ?> aModel);

    public void visit(E aEntity);

    public void visit(ApplicationParametersEntity aEntity);
    
    public void visit(ReferenceRelation<E> aRelation);
}
