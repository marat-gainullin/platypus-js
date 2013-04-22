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
 */
public class ModelEditingSupport<E extends Entity<?, ?, E>> {

    protected Set<ModelEditingListener<E>> listeners = new HashSet<>();
    protected Set<ModelEditingValidator<E>> validators = new HashSet<>();
    protected GuiCallback guiCallback;

    public ModelEditingSupport() {
        super();
    }

    public GuiCallback getGuiCallback() {
        return guiCallback;
    }

    public void setGuiCallback(GuiCallback aCallback) {
        guiCallback = aCallback;
    }

    public void addListener(ModelEditingListener<E> l) {
        listeners.add(l);
    }

    public void removeListener(ModelEditingListener<E> l) {
        listeners.remove(l);
    }

    /**
     * Registers an
     * <code>DatamodelEditingValidator</code>. The validator is notified
     * whenever an datamodel edit action will occur.
     *
     * @param v an <code>DatamodelEditingValidator</code> object
     * @see #removeDatamodelEditingValidator
     */
    public synchronized void addValidator(ModelEditingValidator<E> v) {
        validators.add(v);
    }

    /**
     * Removes an
     * <code>DatamodelEditingValidator</code>.
     *
     * @param v the <code>DatamodelEditingValidator</code> object to be removed
     * @see #addDatamodelEditingValidator
     */
    public synchronized void removeValidator(ModelEditingValidator<E> v) {
        validators.remove(v);
    }

    public boolean checkRelationAddingValid(Relation<E> aRelation) {
        try {
            for (ModelEditingValidator<E> v : validators) {
                if (!v.validateRelationAdding(aRelation)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While Validating (checkRelationAddingValid) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkRelationAddingValid) ", ex);
        }
        return false;
    }

    public boolean checkRelationRemovingValid(Relation<E> aRelation) {
        try {
            for (ModelEditingValidator<E> v : validators) {
                if (!v.validateRelationRemoving(aRelation)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While Validating (checkRelationRemovingValid) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkRelationRemovingValid) ", ex);
        }
        return false;
    }

    public boolean checkEntityAddingValid(E aEntity) {
        try {
            for (ModelEditingValidator<E> v : validators) {
                if (!v.validateEntityAdding(aEntity)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While Validating (checkEntityAddingValid) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkEntityAddingValid) ", ex);
        }
        return false;
    }

    public boolean checkEntityRemovingValid(E aEntity) {
        try {
            for (ModelEditingValidator<E> v : validators) {
                if (!v.validateEntityRemoving(aEntity)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While Validating (checkEntityRemovingValid) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (checkEntityRemovingValid) ", ex);
        }
        return false;
    }

    public void fireEntityAdded(E ent) {
        try {
            if (listeners != null) {
                for (ModelEditingListener<E> dl : listeners) {
                    if (dl != null) {
                        dl.entityAdded(ent);
                    }
                }
            }
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While firing an event (fireEntityAdded) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireEntityAdded) ", ex);
        }
    }

    public void fireEntityRemoved(E ent) {
        try {
            if (listeners != null) {
                for (ModelEditingListener<E> dl : listeners) {
                    if (dl != null) {
                        dl.entityRemoved(ent);
                    }
                }
            }
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While firing an event (fireEntityRemoved) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireEntityRemoved) ", ex);
        }
    }

    public void fireRelationAdded(Relation<E> aRel) {
        try {
            if (!(aRel instanceof DummyRelation)) {
                if (listeners != null) {
                    for (ModelEditingListener<E> dl : listeners) {
                        if (dl != null) {
                            dl.relationAdded(aRel);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While firing an event (fireRelationAdded) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireRelationAdded) ", ex);
        }
    }

    public void fireRelationRemoved(Relation<E> aRel) {
        try {
            if (!(aRel instanceof DummyRelation)) {
                if (listeners != null) {
                    for (ModelEditingListener<E> dl : listeners) {
                        if (dl != null) {
                            dl.relationRemoved(aRel);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While firing an event (fireRelationRemoved) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireRelationRemoved) ", ex);
        }
    }

    public void fireIndexesChanged(E aEntity) {
        try {
            if (listeners != null) {
                for (ModelEditingListener<E> dl : listeners) {
                    if (dl != null) {
                        dl.entityIndexesChanged(aEntity);
                    }
                }
            }
        } catch (Exception ex) {
            if (guiCallback != null) {
                guiCallback.showMessageDialog("While firing an event (fireIndexesChanged) " + ex.getMessage(), "datamodel", GuiCallback.ERROR_MESSAGE);
            }
            Logger.getLogger(ModelEditingSupport.class.getName()).log(Level.SEVERE, "While firing an event (fireIndexesChanged) ", ex);
        }
    }
}
