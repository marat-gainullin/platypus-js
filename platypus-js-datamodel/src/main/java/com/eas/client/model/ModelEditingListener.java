/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

/**
 *
 * @author mg
 * @param <E>
 */
public interface ModelEditingListener<E extends Entity<?, ?, E>> {

    public void entityAdded(E added);

    public void entityRemoved(E removed);

    public void relationAdded(Relation<E> added);

    public void relationRemoved(Relation<E> removed);

    public void entityIndexesChanged(E changed);
}
