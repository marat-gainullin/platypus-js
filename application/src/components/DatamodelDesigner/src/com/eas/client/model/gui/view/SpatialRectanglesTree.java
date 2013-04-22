/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class SpatialRectanglesTree<E> {

    private static final int CHILDREN_COUNT = 4;

    protected class SRTNode<E> {

        Rectangle nodeRect = null;
        int nodeCenterX = 0;
        int nodeCenterY = 0;
        Map<Rectangle, E> subjects = new HashMap<>();
        SRTNode<E> parent = null;
        List<SRTNode<E>> children = new ArrayList<>(CHILDREN_COUNT);
        Rectangle[] childrenRects = new Rectangle[CHILDREN_COUNT];

        public SRTNode(Rectangle bounds, SRTNode<E> aParent) throws Exception {
            super();
            parent = aParent;
            if (bounds == null) {
                throw new Exception("Bad parameter (key). Null is not allowed.");
            }
            if (bounds.isEmpty()) {
                throw new Exception("Bad parameter (key). Too thin rectangle for spatial tree node.");
            }
            nodeRect = new Rectangle(bounds);
            nodeCenterX = nodeRect.x + nodeRect.width / 2;
            nodeCenterY = nodeRect.y + nodeRect.height / 2;
            calcChildrenRects();
            for (int i = 0; i < CHILDREN_COUNT; i++) {
                children.add(null);
            }
        }

        protected void calcChildrenRects() {
            childrenRects[1] = new Rectangle(nodeRect.x, nodeRect.y, nodeCenterX - nodeRect.x, nodeCenterY - nodeRect.y);
            childrenRects[0] = new Rectangle(childrenRects[1].x + childrenRects[1].width, nodeRect.y, nodeRect.width - childrenRects[1].width, childrenRects[1].height);
            childrenRects[2] = new Rectangle(nodeRect.x, childrenRects[1].y + childrenRects[1].height, childrenRects[1].width, nodeRect.height - childrenRects[1].height);
            childrenRects[3] = new Rectangle(childrenRects[0].x, childrenRects[2].y, childrenRects[0].width, childrenRects[2].height);
        }

        protected boolean addSubject(Rectangle key, E element) {
            if (!subjects.containsValue(element)) {
                subjects.put(new Rectangle(key), element);
                return true;
            }
            return false;
        }

        protected boolean removeSubject(Rectangle key, E element) {
            boolean lremoved = false;
            if (subjects != null) {
                int l2DelIdx = -1;
                Rectangle[] l2Delete = new Rectangle[subjects.size()];
                Set<Entry<Rectangle, E>> lentries = subjects.entrySet();
                if (lentries != null) {
                    Iterator<Entry<Rectangle, E>> lit = lentries.iterator();
                    while (lit != null && lit.hasNext()) {
                        Entry<Rectangle, E> lentry = lit.next();
                        if (intersects(key, lentry.getKey()) && element == lentry.getValue()) {
                            l2Delete[++l2DelIdx] = lentry.getKey();
                        }
                    }
                }
                while (l2DelIdx >= 0) {
                    subjects.remove(l2Delete[l2DelIdx]);
                    l2DelIdx--;
                    lremoved = true;
                }
            }
            return lremoved;
        }

        public boolean put(Rectangle key, E element) throws Exception {
            boolean lres = false;
            if (key == null) {
                throw new Exception("Bad parameter (key). Null is not allowed.");
            }
            if (key.width < 0 || key.height < 0) {
                throw new Exception("Bad parameter (key). Empty rectangle is not allowed.");
            }
            if (key != null && intersects(key, nodeRect) && element != null) {
                if (nodeRect.width >= epsilon && nodeRect.height >= epsilon) {
                    for (int i = 0; i < children.size(); i++) {
                        if (intersects(key, childrenRects[i])) {
                            if (children.get(i) == null) {
                                children.set(i, new SRTNode<>(childrenRects[i], this));
                            }
                            boolean lputted = children.get(i).put(key, element);
                            if (lputted) {
                                lres = true;
                            }
                        }
                    }
                } else {
                    lres = addSubject(key, element);
                }
            }
            return lres;
        }

        protected boolean isEmpty() {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) != null) {
                    return false;
                }
            }
            return subjects.isEmpty();
        }

        public boolean remove(Rectangle key, E element) {
            boolean removed = false;
            if (key != null && intersects(key, nodeRect)) {
                boolean isLeaf = true;
                int nulledChildren = 0;
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) != null) {
                        isLeaf = false;
                        if (intersects(key, childrenRects[i])) {
                            removed = children.get(i).remove(key, element) ? true : removed;
                            if (children.get(i) != null && children.get(i).isEmpty()) {
                                children.set(i, null);
                                ++nulledChildren;
                            }
                        }
                    }
                }
                if (isLeaf) {
                    return removeSubject(key, element);
                }
            }
            return removed;
        }

        public Map<Rectangle, E> get(Rectangle key) {
            if (key != null && intersects(key, nodeRect)) {
                Map<Rectangle, E> lsubjects = new HashMap<>();
                lsubjects.putAll(subjects);
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) != null) {
                        if (intersects(key, childrenRects[i])) {
                            Map<Rectangle, E> lmap = children.get(i).get(key);
                            if (lmap != null) {
                                lsubjects.putAll(lmap);
                            }
                        }
                    }
                }
                return lsubjects;
            }
            return null;
        }

        public Iterator<E> subjectsIterator() {
            if (subjects != null && !subjects.isEmpty()) {
                return subjects.values().iterator();
            } else {
                return null;
            }
        }

        public Iterator<SRTNode<E>> childrenIterator() {
            if (children != null && !children.isEmpty()) {
                return children.iterator();
            } else {
                return null;
            }
        }
    }

    protected static boolean intersects(Rectangle rect1, Rectangle rect2) {
        if (rect1 == null || rect2 == null) {
            return false;
        }
        int r1x1 = rect1.x;
        int r1x2 = rect1.x + rect1.width;
        int r1y1 = rect1.y;
        int r1y2 = rect1.y + rect1.height;

        int r2x1 = rect2.x;
        int r2x2 = rect2.x + rect2.width;
        int r2y1 = rect2.y;
        int r2y2 = rect2.y + rect2.height;

        return !((r2x1 > r1x2 && r2x2 > r1x2) || // right
                (r2x1 < r1x1 && r2x2 < r1x1) || // left
                (r2y1 > r1y2 && r2y2 > r1y2) || // bottom
                (r2y1 < r1y1 && r2y2 < r1y1) // top
                );
    }

    protected class SpIterator implements Iterator<E> {

        protected Set<E> toIterate = new HashSet<>();
        protected Iterator<E> toIterateIterator = null;

        public SpIterator(SRTNode<E> aRoot) {
            super();
            addElements(aRoot);
            toIterateIterator = toIterate.iterator();
        }

        public void addElements(SRTNode<E> aRoot) {
            if (aRoot != null) {
                Iterator<E> lsit = aRoot.subjectsIterator();
                if (lsit != null) {
                    while (lsit.hasNext()) {
                        E e = lsit.next();
                        if (!toIterate.contains(e)) {
                            toIterate.add(e);
                        }
                    }
                }
                Iterator<SRTNode<E>> chit = aRoot.childrenIterator();
                if (chit != null) {
                    while (chit.hasNext()) {
                        SRTNode<E> lnode = chit.next();
                        addElements(lnode);
                    }
                }
            }
        }

        public boolean hasNext() {
            return toIterateIterator.hasNext();
        }

        public E next() {
            return toIterateIterator.next();
        }

        public void remove() throws UnsupportedOperationException {
            throw (new UnsupportedOperationException("Deletion throw iterator is not supported. Use remove method of SpatialRectanglesTree."));
        }
    }

    public Iterator<E> iterator() {
        return new SpIterator(root);
    }
    SRTNode<E> root = null;
    protected int epsilon = 32;
    protected int count = 0;
    protected Rectangle bounds = null;

    public SpatialRectanglesTree(Rectangle bounds) {
        super();
        this.bounds = new Rectangle(bounds);
    }

    public SpatialRectanglesTree(Rectangle bounds, int epsilon) {
        this(bounds);
        setEpsilon(epsilon);
    }

    public void setEpsilon(int epsilon) {
        if (epsilon > 0) {
            this.epsilon = epsilon;
        }
    }

    public void clear() {
        root = null;
        count = 0;
    }

    public int size() {
        return count >= 0 ? count : 0;
    }

    public void setBounds(Rectangle aBounds) {
        if (bounds == null || !bounds.equals(aBounds)) {
            clear();
            bounds = new Rectangle(aBounds);
        }
    }

    public boolean put(Rectangle key, E element) {
        try {
            if (root == null) {
                root = new SRTNode<>(bounds, null);
            }
            if (root.put(new Rectangle(key), element)) {
                count++;
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(SpatialRectanglesTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean remove(Rectangle key, E element) {
        if (root != null) {
            if (root.remove(new Rectangle(key), element)) {
                count--;
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getEpsilon() {
        return epsilon;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(Point pt) {
        Rectangle lrt = new Rectangle(pt.x, pt.y, 0, 0);
        return contains(lrt);
    }

    public boolean contains(Rectangle key) {
        if (root != null) {
            Set<E> lresults = get(key);
            return (lresults != null && !lresults.isEmpty());
        }
        return false;
    }

    public E get(Point akey) {
        if (root != null && akey != null) {
            Rectangle key = new Rectangle(akey.x, akey.y, 1, 1);
            key.grow(1, 1);
            Map<Rectangle, E> lresults = root.get(key);
            if (lresults != null && !lresults.isEmpty()) {
                Set<Entry<Rectangle, E>> les = lresults.entrySet();
                if (les != null) {
                    Iterator<Entry<Rectangle, E>> lit = les.iterator();
                    while (lit != null && lit.hasNext()) {
                        Entry<Rectangle, E> le = lit.next();
                        if (le.getKey() != null && le.getKey().contains(akey)) {
                            return le.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    public Set<E> get(Rectangle akey) {
        if (root != null && akey != null) {
            Rectangle key = new Rectangle(akey);
            Map<Rectangle, E> lresults = root.get(key);
            if (lresults != null && !lresults.isEmpty()) {
                Set<E> lfiltered = new HashSet<>();
                for (Entry<Rectangle, E> le : lresults.entrySet()) {
                    if (le.getKey() != null && intersects(key, le.getKey())) {
                        lfiltered.add(le.getValue());
                    }
                }
                return lfiltered;
            }
        }
        return new HashSet<>();
    }
}
