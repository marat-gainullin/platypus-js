/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ReferenceRelation;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public interface ApplicationModelVisitor<E extends ApplicationEntity<M, ?, E>, M extends ApplicationModel<E, ?>> extends ModelVisitor<E, M> {

    public void visit(ReferenceRelation<E> aRelation);
}
