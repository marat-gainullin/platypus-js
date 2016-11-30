/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a move event.
 * 
 * @param <T>
 *            the type being moved
 * @author mg
 */
public class MoveEvent<T> extends GwtEvent<MoveHandler<T>> {

	/**
	 * Handler type.
	 */
	private static Type<MoveHandler<?>> TYPE;

	/**
	 * Fires a move event on all registered handlers in the handler manager. If
	 * no such handlers exist, this method will do nothing.
	 * 
	 * @param <T>
	 *            the target type
	 * @param source
	 *            the source of the handlers
	 * @param target
	 *            the target
	 */
	public static <T> void fire(HasMoveHandlers<T> source, T aTarget, double aX, double aY) {
		if (TYPE != null) {
			MoveEvent<T> event = new MoveEvent<>(aTarget, aX, aY);
			source.fireEvent(event);
		}
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<MoveHandler<?>> getType() {
		return TYPE != null ? TYPE : (TYPE = new Type<>());
	}

	private final T target;
	private double x;
	private double y;

	/**
	 * Creates a new move event.
	 * 
	 * @param aTarget
	 *            the target
	 */
	protected MoveEvent(T aTarget, double aX, double aY) {
		super();
		target = aTarget;
		x = aX;
		y = aY;
	}

	// The instance knows its of type T, but the TYPE
	// field itself does not, so we have to do an unsafe cast here.
	@SuppressWarnings("unchecked")
	@Override
	public final Type<MoveHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	public T getTarget() {
		return target;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	protected void dispatch(MoveHandler<T> handler) {
		handler.onMove(this);
	}
}
