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

import com.eas.client.model.Entity;
import com.eas.client.model.gui.edits.MoveEntityEdit;
import com.eas.client.model.gui.view.entities.EntityView;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public class CollapserExpander {

    public static <E extends Entity<?, ?, E>> void expand(PathsFinder<E, EntityView<E>> aPathsFinder, EntityView<E> eView, Rectangle aField, int dy, UndoableEditSupport aUndoSupport) {
        Set<EntityView<E>> lprocessed = new HashSet<>();
        List<EntityView<E>> l2Move = new ArrayList<>();
        l2Move.add(eView);
        int i = 0;
        while (i < l2Move.size()) {
            EntityView<E> lEntityComponent = l2Move.get(i);
            if (!lprocessed.contains(lEntityComponent)) {
                Rectangle tilBottom = new Rectangle();
                Rectangle bounds = lEntityComponent.getBounds();
                bounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                tilBottom.x = bounds.x;
                tilBottom.y = bounds.y + bounds.height;
                tilBottom.width = bounds.width;
                tilBottom.height = aField.height - tilBottom.y;

                Set<EntityView<E>> lstops = aPathsFinder.getInsetsIntersecting(tilBottom);
                lstops.remove(eView);
                l2Move.addAll(lstops);

                if (lEntityComponent != eView) {
                    Point llocation = lEntityComponent.getLocation();
                    Point lnewlocation = new Point(llocation.x, llocation.y + dy);
                    assert lEntityComponent instanceof EntityView<?>;
                    MoveEntityEdit<?> ledit = new MoveEntityEdit<>(lEntityComponent.getEntity(), llocation, lnewlocation);
                    ledit.redo();
                    aUndoSupport.postEdit(ledit);
                }
                lprocessed.add(lEntityComponent);
            }
            ++i;
        }
    }

    protected static <E extends Entity<?, ?, E>> List<EntityView<E>> sortRectsByTop(List<EntityView<E>> toSort) {
        List<EntityView<E>> lres = new ArrayList<>();
        Map<Integer, List<EntityView<E>>> lsorter = new TreeMap<>();
        for (int i = 0; i < toSort.size(); i++) {
            EntityView<E> lelement = toSort.get(i);
            int ltop = lelement.getLocation().y;
            List<EntityView<E>> lsortElement = lsorter.get(ltop);
            if (lsortElement == null) {
                lsortElement = new ArrayList<>();
                lsorter.put(ltop, lsortElement);
            }
            lsortElement.add(lelement);
        }
        Set<Entry<Integer, List<EntityView<E>>>> lentries = lsorter.entrySet();
        if (lentries != null) {
            for (Entry<Integer, List<EntityView<E>>> le : lentries) {
                if (le != null && le.getValue() != null) {
                    List<EntityView<E>> lsortElement = le.getValue();
                    lres.addAll(lsortElement);
                }
            }
        }
        return lres;
    }

    public static <E extends Entity<?, ?, E>> void collapse(PathsFinder<E, EntityView<E>> aPathsFinder, EntityView<E> eView, Rectangle aField, int dy, UndoableEditSupport aUndoSupport) {
        Set<EntityView<E>> lprocessed = new HashSet<>();
        List<EntityView<E>> l2Move = new ArrayList<>();
        l2Move.add(eView);
        int i = 0;
        while (i < l2Move.size()) {
            EntityView<E> lentity = l2Move.get(i);
            if (!lprocessed.contains(lentity)) {
                Rectangle tilBottom = new Rectangle();
                Rectangle bounds = lentity.getBounds();
                bounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                tilBottom.x = bounds.x;
                tilBottom.y = bounds.y + bounds.height;
                tilBottom.width = bounds.width;
                tilBottom.height = aField.height - tilBottom.y;

                Set<EntityView<E>> lstops = aPathsFinder.getInsetsIntersecting(tilBottom);
                l2Move.addAll(lstops);
                lprocessed.add(lentity);
            }
            ++i;
        }
        lprocessed.clear();
        List<EntityView<E>> lsorted = sortRectsByTop(l2Move);
        for (i = 0; i < lsorted.size(); i++) {
            EntityView<E> lEntityView = lsorted.get(i);
            if (lEntityView != eView && !lprocessed.contains(lEntityView)) {
                Rectangle aboveBounds = lEntityView.getBounds();
                aboveBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                aboveBounds.y -= dy;
                aboveBounds.height = dy;

                boolean linsetsContains = true;
                aPathsFinder.remove(lEntityView);
                try {
                    linsetsContains = aPathsFinder.insetsContains(aboveBounds);
                } finally {
                    aPathsFinder.put(lEntityView);
                }
                if (!linsetsContains) {
                    Point llocation = lEntityView.getLocation();
                    Point lnewlocation = new Point(llocation.x, llocation.y - dy);
                    MoveEntityEdit<E> ledit = new MoveEntityEdit<>(lEntityView.getEntity(), llocation, lnewlocation);
                    ledit.redo();
                    aUndoSupport.postEdit(ledit);
                }
            }
            lprocessed.add(lEntityView);
        }
    }

    public static <E extends Entity<?, ?, E>> Point findFreePlaceSquare(PathsFinder<E, EntityView<E>> aPathsFinder, EntityView<E> eView) {
        Rectangle lBounds = eView.getBounds();
        lBounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
        lBounds.x = 2 * EntityView.INSET_ZONE;
        lBounds.y = 2 * EntityView.INSET_ZONE;
        long lMaxEntitiesPerRow = Math.round(Math.sqrt(eView.getEntity().getModel().getAllEntities().size()));
        long lEntitiesPerRow = 1;
        while (aPathsFinder.insetsContains(lBounds)) {
            lBounds.x += (lBounds.width + 4 * EntityView.INSET_ZONE);
            lEntitiesPerRow++;
            if (lEntitiesPerRow > lMaxEntitiesPerRow) {
                lBounds.x = 2 * EntityView.INSET_ZONE;
                lBounds.y += (lBounds.height + 2 * EntityView.INSET_ZONE);
                lEntitiesPerRow = 1;
            }
        }
        return lBounds.getLocation();
    }
}
