/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.processing;

import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 * @param <T>
 */
public abstract class TreeAdapter<T> implements Tree<T> {

    protected final Set<Tree.ChangeHandler<T>> changesHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addChangesHandler(final Tree.ChangeHandler<T> aHandler) {
        changesHandlers.add(aHandler);
        return new HandlerRegistration() {

            @Override
            public void removeHandler() {
                changesHandlers.remove(aHandler);
            }
        };
    }

    protected void added(T aElement) {
        for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[]{})) {
            ((Tree.ChangeHandler<T>) handler).added(aElement);
        }
    }

    protected void removed(T aElement, T aRemovedFrom) {
        for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[]{})) {
            ((Tree.ChangeHandler<T>) handler).removed(aElement, aRemovedFrom);
        }
    }
    
	protected void changed(T aElement) {
		for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[] {})) {
			((Tree.ChangeHandler<T>) handler).changed(aElement);
		}
	}

	protected void everythingChanged() {
		for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[] {})) {
			((Tree.ChangeHandler<T>) handler).everythingChanged();
		}
	}

}
