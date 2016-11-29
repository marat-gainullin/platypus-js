/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.processing;

import com.eas.ui.events.CollapsedHandler;
import com.eas.ui.events.ExpandedHandler;
import com.eas.ui.events.HasCollapsedHandlers;
import com.eas.ui.events.HasExpandedHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the table front to a treed data.
 * 
 * @author mg
 * @param <T>
 */
public class TreeDataProvider<T> extends ListDataProvider<T> implements IndexOfProvider<T>, HasExpandedHandlers<T>, HasCollapsedHandlers<T> {

	protected Tree<T> tree;
	protected Set<T> expanded = new HashSet<>();
	protected Map<T, Integer> indicies = new HashMap<>();
	protected final Set<ExpandedHandler<T>> expandedHandlers = new HashSet<>();
	protected final Set<CollapsedHandler<T>> collapsedHandlers = new HashSet<>();
	protected Runnable onResize;

	@Override
	public HandlerRegistration addExpandedHandler(final ExpandedHandler<T> aHandler) {
		expandedHandlers.add(aHandler);
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				expandedHandlers.remove(aHandler);
			}
		};
	}

	@Override
	public HandlerRegistration addCollapsedHandler(final CollapsedHandler<T> aHandler) {
		collapsedHandlers.add(aHandler);
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				collapsedHandlers.remove(aHandler);
			}
		};
	}

	protected void expanded(T aElement) {
		if (onResize != null)
			onResize.run();
		for (ExpandedHandler<?> handler : expandedHandlers.toArray(new ExpandedHandler<?>[] {})) {
			((ExpandedHandler<T>) handler).expanded(aElement);
		}
	}

	protected void collapsed(T aElement) {
		if (onResize != null)
			onResize.run();
		for (CollapsedHandler<?> handler : collapsedHandlers.toArray(new CollapsedHandler<?>[] {})) {
			((CollapsedHandler<T>) handler).collapsed(aElement);
		}
	}

	/**
	 * Table front constructor. Constructs a lazy tree front (asynchronous
	 * case).
	 * 
	 * @param aTreedModel
	 *            - Deep treed model, containing data.
	 */
	public TreeDataProvider(Tree<T> aTreedModel, Runnable aOnResize) {
		super();
		onResize = aOnResize;
		tree = aTreedModel;
		// Let's fill with roots forest
		getList().addAll(tree.getChildrenOf(null));
		tree.addChangesHandler(new Tree.ChangeHandler<T>() {

			@Override
			public void removed(T aSubject) {
				if (indexOf(aSubject) != -1) {
					if (isExpanded(aSubject)) {
						collapse(aSubject);
					} else {
						invalidateFront();
						validateFront();
					}
					if (onResize != null)
						onResize.run();
				}
			}

			@Override
			public void added(T aSubject) {
				T parent = tree.getParentOf(aSubject);
				if(parent == null || isExpanded(parent)){
					invalidateFront();
					validateFront();
					if (onResize != null)
						onResize.run();
				}
			}

			@Override
			public void changed(T aSubject) {
				int idx = indexOf(aSubject);
				if (idx != -1) {
					List<T> targetList = getList();
					targetList.set(idx, targetList.get(idx));
				}
			}

			@Override
			public void everythingChanged() {
				expanded.clear();
				invalidateFront();
				validateFront();
				if (onResize != null)
					onResize.run();
			}
		});
	}

	protected void invalidateFront() {
		getList().clear();
		indicies.clear();
	}

	protected void validateFront() {
		assert tree != null;
		List<T> targetList = getList();
		if (targetList.isEmpty()) {
			List<T> children = tree.getChildrenOf(null);
			targetList.addAll(children);
			int i = 0;
			while (i < targetList.size()) {
				if (expanded.contains(targetList.get(i))) {
					List<T> children1 = tree.getChildrenOf(targetList.get(i));
					targetList.addAll(i + 1, children1);
				}
				++i;
			}
			for (i = 0; i < targetList.size(); i++) {
				indicies.put(targetList.get(i), i);
			}
		}
	}

	public int indexOf(T aItem) {
		validateFront();
		Integer idx = indicies.get(aItem);
		return idx != null ? idx.intValue() : -1;
	}

	@Override
	public void rescan() {
		indicies.clear();
		List<T> targetList = getList();
		for (int i = 0; i < targetList.size(); i++) {
			indicies.put(targetList.get(i), i);
		}
	}
	
	/**
	 * Builds path to specified element if the element belongs to the model.
	 * 
	 * @param anElement
	 *            Element to build path to.
	 * @return List<T> of elements comprising the path, excluding root null. So
	 *         for the roots of the forest path will be a list with one element.
	 */
	public List<T> buildPathTo(T anElement) {
		List<T> path = new ArrayList<>();
		if (anElement != null) {
			T currentParent = anElement;
			Set<T> added = new HashSet<>();
			path.add(currentParent);
			added.add(currentParent);
			while (currentParent != null) {
				currentParent = getParentOf(currentParent);
				if(added.contains(currentParent)){
					break;
				}
				if (currentParent != null) {
					path.add(0, currentParent);
					added.add(currentParent);
				}
			}
		}
		return path;
	}

	protected T getParentOf(T aElement) {
		assert tree != null;
		return tree.getParentOf(aElement);
	}

	public boolean isExpanded(T anElement) {
		return expanded.contains(anElement);
	}

	public void expand(final T anElement) {
		List<T> children = tree.getChildrenOf(anElement);
		if (!expanded.contains(anElement)) {
			if (children != null && !children.isEmpty()) {
				expanded.add(anElement);
				invalidateFront();
				validateFront();
				expanded(anElement);
			}
		}
	}

	public void collapse(T anElement) {
		if (expanded.contains(anElement)) {
			expanded.remove(anElement);
			invalidateFront();
			validateFront();
			collapsed(anElement);
		}
	}

	public Tree<T> getTree() {
		return tree;
	}

}
