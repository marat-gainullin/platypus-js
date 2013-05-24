/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.util.Util;

class DNDManager {

  private static DNDManager manager;

  static DNDManager get() {
    if (manager == null) {
      manager = new DNDManager();
    }
    return manager;
  }

  private DropTarget currentTarget;
  private List<DropTarget> targets = new ArrayList<DropTarget>();

  protected DropTarget getTarget(DragSource source, Element elem) {
    DropTarget target = null;
    for (DropTarget t : targets) {
      if (t.isEnabled()
          && Util.equalWithNull(t.getGroup(), source.getGroup())
          && DOM.isOrHasChild(t.component.getElement(), elem)
          && (target == null || (target != null && DOM.isOrHasChild(target.component.getElement(),
              t.component.getElement())))) {
        target = t;
      }
    }
    return target;
  }

  List<DropTarget> getDropTargets() {
    return targets;
  }

  void handleDragCancelled(DragSource source, DndDragCancelEvent event) {
    source.onDragCancelled(event);
    source.ensureHandlers().fireEventFromSource(event, source);
    if (currentTarget != null) {
      currentTarget.onDragCancelled(event);
      currentTarget = null;
    }
  }

  void handleDragEnd(DragSource source, DndDropEvent event) {
    if (currentTarget != null) {
      event.setDropTarget(currentTarget);
      event.setOperation(currentTarget.getOperation());
    }
    if (currentTarget != null && event.getStatusProxy().getStatus()) {
      source.onDragDrop(event);
      source.ensureHandlers().fireEventFromSource(event, source);

      currentTarget.handleDrop(event);
      currentTarget.ensureHandlers().fireEventFromSource(event, currentTarget);
    } else {
      source.onDragFail(event);
      source.ensureHandlers().fireEventFromSource(event, source);

      if (currentTarget != null) currentTarget.onDragFail(event);
    }
    currentTarget = null;
    Insert.get().hide();

  }

  void handleDragMove(DragSource source, DndDragMoveEvent event) {
    DropTarget target = getTarget(source, (Element) event.getDragMoveEvent().getNativeEvent().getEventTarget().cast());

    // no target with current
    if (target == null) {
      if (currentTarget != null) {
        currentTarget.handleDragLeave(event);
        currentTarget = null;
      }
      return;
    }

    // match move
    if (target == currentTarget) {
      event.setCancelled(true);
      event.setDropTarget(currentTarget);
      currentTarget.onDragMove(event);
      currentTarget.ensureHandlers().fireEventFromSource(event, currentTarget);

      if (event.isCancelled()) {
        Insert.get().hide();
      } else {
        currentTarget.showFeedback(event);
      }
      return;
    }

    if (target != currentTarget) {
      if (currentTarget != null) {
        currentTarget.handleDragLeave(event);
        currentTarget = null;
      }

      currentTarget = target;
    }

    if (!currentTarget.isAllowSelfAsSource() && source.getWidget() == currentTarget.getWidget()) {
      currentTarget = null;
      return;
    }

    // entering
    event.setCancelled(true);
    event.setDropTarget(currentTarget);
    currentTarget.handleDragEnter(event);
    if (event.isCancelled()) {
      Insert.get().hide();
      currentTarget = null;
    } else {
      currentTarget.showFeedback(event);
    }
  }

  void handleDragStart(DragSource source, DndDragStartEvent event) {
    source.onDragStart(event);

    source.ensureHandlers().fireEventFromSource(event, source);

    if (event.getData() == null || event.isCancelled()) {
      event.setCancelled(true);
      event.getDragStartEvent().setCancelled(true);
      return;
    }
    source.setData(event.getData());
    source.statusProxy.setStatus(false);
  }

  void registerDropTarget(DropTarget target) {
    targets.add(target);
  }

  void unregisterDropTarget(DropTarget target) {
    targets.remove(target);
  }

}
