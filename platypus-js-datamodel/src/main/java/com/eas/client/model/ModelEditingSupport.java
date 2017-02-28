/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 * @param <E>
 */
public class ModelEditingSupport<E extends Entity<?, ?, E>> {

    protected Set<ModelEditingListener<E>> listeners = new HashSet<>();
    protected Set<ModelEditingValidator<E>> validators = new HashSet<>();

    public ModelEditingSupport() {
        super();
    }

    public void addListener(ModelEditingListener<E> l) {
        listeners.add(l);
    }

    public void removeListener(ModelEditingListener<E> l) {
        listeners.remove(l);
    }

    /**
     * Registers an <code>DatamodelEditingValidator</code>. The validator is
     * notified whenever an datamodel edit action will occur.
     *
     * @param v an <code>DatamodelEditingValidator</code> object
     * @see #removeValidator(com.eas.client.model.ModelEditingValidator) 
     */
    public synchronized void addValidator(ModelEditingValidator<E> v) {
        validators.add(v);
    }

    /**
     * Removes an <code>DatamodelEditingValidator</code>.
     *
     * @param v the <code>DatamodelEditingValidator</code> object to be removed
     * @see #addValidator(com.eas.client.model.ModelEditingValidator) 
     */
    public synchronized void removeValidator(ModelEditingValidator<E> v) {
        validators.remove(v);
    }

    public boolean checkRelationAddingValid(Relation<E> aRelation) {
        try {
            return validators.stream().noneMatch((v) -> (!v.validateRelationAdding(aRelation)));
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkRelationAddingValid) ", ex);
        }
        return false;
    }

    public boolean checkRelationRemovingValid(Relation<E> aRelation) {
        try {
            return validators.stream().noneMatch((v) -> (!v.validateRelationRemoving(aRelation)));
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkRelationRemovingValid) ", ex);
        }
        return false;
    }

    public boolean checkEntityAddingValid(E aEntity) {
        try {
            return validators.stream().noneMatch((v) -> (!v.validateEntityAdding(aEntity)));
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkEntityAddingValid) ", ex);
        }
        return false;
    }

    public boolean checkEntityRemovingValid(E aEntity) {
        try {
            return validators.stream().noneMatch((v) -> (!v.validateEntityRemoving(aEntity)));
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkEntityRemovingValid) ", ex);
        }
        return false;
    }

    public void fireEntityAdded(E ent) {
        try {
            if (listeners != null) {
                listeners.stream().filter((dl) -> (dl != null)).forEachOrdered((dl) -> {
                    dl.entityAdded(ent);
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireEntityAdded) ", ex);
        }
    }

    public void fireEntityRemoved(E ent) {
        try {
            if (listeners != null) {
                listeners.stream().filter((dl) -> (dl != null)).forEachOrdered((dl) -> {
                    dl.entityRemoved(ent);
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireEntityRemoved) ", ex);
        }
    }

    public void fireRelationAdded(Relation<E> aRel) {
        try {
            if (!(aRel instanceof DummyRelation)) {
                if (listeners != null) {
                    listeners.stream().filter((dl) -> (dl != null)).forEachOrdered((dl) -> {
                        dl.relationAdded(aRel);
                    });
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireRelationAdded) ", ex);
        }
    }

    public void fireRelationRemoved(Relation<E> aRel) {
        try {
            if (!(aRel instanceof DummyRelation)) {
                if (listeners != null) {
                    listeners.stream().filter((dl) -> (dl != null)).forEachOrdered((dl) -> {
                        dl.relationRemoved(aRel);
                    });
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireRelationRemoved) ", ex);
        }
    }

    public void fireIndexesChanged(E aEntity) {
        try {
            if (listeners != null) {
                listeners.stream().filter((dl) -> (dl != null)).forEachOrdered((dl) -> {
                    dl.entityIndexesChanged(aEntity);
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireIndexesChanged) ", ex);
        }
    }
}
