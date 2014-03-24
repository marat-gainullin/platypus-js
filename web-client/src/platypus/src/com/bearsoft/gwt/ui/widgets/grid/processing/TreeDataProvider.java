/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.processing;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is the table front to a treed data.
 *
 * @author mg
 * @param <T>
 */
public class TreeDataProvider<T> extends ListDataProvider<T> {

    public interface ExpandedCollapsedHandler<T> {

        public void expanded(T anElement);

        public void collapsed(T anElement);
    }

    protected Tree<T> tree;
    protected Set<T> expanded = new HashSet<>();
    protected ChildrenFetcher<T> childrenFetcher;
    protected final Set<ExpandedCollapsedHandler<T>> expandCollapseHandlers = new HashSet<>();

    public HandlerRegistration addExpandedCollapsedHandler(final ExpandedCollapsedHandler<T> aHandler) {
        expandCollapseHandlers.add(aHandler);
        return new HandlerRegistration() {

            @Override
            public void removeHandler() {
                expandCollapseHandlers.remove(aHandler);
            }
        };
    }

    protected void expanded(T aElement) {
        for (ExpandedCollapsedHandler<?> handler : expandCollapseHandlers.toArray(new ExpandedCollapsedHandler<?>[]{})) {
            ((ExpandedCollapsedHandler<T>) handler).expanded(aElement);
        }
    }

    protected void collapsed(T aElement) {
        for (ExpandedCollapsedHandler<?> handler : expandCollapseHandlers.toArray(new ExpandedCollapsedHandler<?>[]{})) {
            ((ExpandedCollapsedHandler<T>) handler).collapsed(aElement);
        }
    }

    protected final Set<Tree.ChangeHandler<T>> changesHandlers = new HashSet<>();

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
        for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[]{})) {
            ((Tree.ChangeHandler<T>) handler).changed(aElement);
        }
    }
    
    protected void everythingChanged() {
        for (Tree.ChangeHandler<?> handler : changesHandlers.toArray(new Tree.ChangeHandler<?>[]{})) {
            ((Tree.ChangeHandler<T>) handler).everythingChanged();
        }
    }
    /**
     * Front constructor for synchronous front.
     *
     * @param aTreedModel
     */
    public TreeDataProvider(Tree<T> aTreedModel) {
        this(aTreedModel, null);
    }

    /**
     * Table front constructor. Constructs a lazy tree front (asynchronous
     * case).
     *
     * @param aTreedModel - Deep treed model, containing data.
     * @param aChildrenFetcher - Fetcher object for lazy trees.
     */
    public TreeDataProvider(Tree<T> aTreedModel, ChildrenFetcher<T> aChildrenFetcher) {
        super();
        tree = aTreedModel;
        childrenFetcher = aChildrenFetcher;
        getList().addAll(tree.getChildrenOf(null));// Let's fill with forest roots.
        tree.addChangesHandler(new Tree.ChangeHandler<T>() {

            @Override
            public void removed(T aSubject, T aRemovedFrom) {
                if (isExpanded(aSubject)) {
                    collapse(aSubject);
                } else {
                    invalidateFront();
                    validateFront();
                }
                TreeDataProvider.this.removed(aSubject, aRemovedFrom);
            }

            @Override
            public void added(T aSubject) {
                invalidateFront();
                validateFront();
                TreeDataProvider.this.added(aSubject);
            }
            
            public void changed(T aSubject) {
            }
            
            @Override
            public void everythingChanged() {
            	expanded.clear();
                invalidateFront();
                validateFront();
                TreeDataProvider.this.everythingChanged();
            }
        });
    }

    protected void validateFront() {
        assert tree != null;
        if (getList().isEmpty()) {
            List<T> children = tree.getChildrenOf(null);
            getList().addAll(children);
            int i = 0;
            while (i < getList().size()) {
                if (expanded.contains(getList().get(i))) {
                    List<T> children1 = tree.getChildrenOf(getList().get(i));
                    getList().addAll(i + 1, children1);
                }
                ++i;
            }
        }
    }

    protected void invalidateFront() {
        getList().clear();
    }

    /**
     * Builds path to specified element if the element belongs to the model.
     *
     * @param anElement Element to build path to.
     * @return List<T> of elements comprising the path, excluding root null. So
     * for the roots of the forest path will be a list with one element.
     */
    public List<T> buildPathTo(T anElement) {
        List<T> path = new ArrayList<>();
        if (anElement != null) {
            T currentParent = anElement;
            path.add(currentParent);
            while (currentParent != null) {
                currentParent = getParentOf(currentParent);
                if (currentParent != null) {
                    path.add(0, currentParent);
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
            } else if (childrenFetcher != null) {// children == null || children.isEmpty()
                final T element2Expand = anElement;
                expanded.add(element2Expand); // To prevent re-fetching.
                Runnable completer = new Runnable() {

                    @Override
                    public void run() {
                        List<T> fetchedChildren = tree.getChildrenOf(element2Expand);
                        if (!fetchedChildren.isEmpty()) {
                            invalidateFront();
                            validateFront();
                            expanded(anElement);
                        }
                    }
                };
                childrenFetcher.fetch(element2Expand, completer);
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
