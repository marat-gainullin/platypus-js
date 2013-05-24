/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.toolbar;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar.PagingToolBarAppearance;

public class PagingToolBarBaseAppearance implements PagingToolBarAppearance {

  public interface PagingToolBarResources {
    ImageResource first();

    ImageResource prev();

    ImageResource next();

    ImageResource last();

    ImageResource refresh();

    ImageResource loading();
  }

  private final PagingToolBarResources resources;

  public PagingToolBarBaseAppearance(PagingToolBarResources resources) {
    this.resources = resources;
  }

  @Override
  public ImageResource first() {
    return resources.first();
  }

  @Override
  public ImageResource last() {
    return resources.last();
  }

  @Override
  public ImageResource next() {
    return resources.next();
  }

  @Override
  public ImageResource prev() {
    return resources.prev();
  }

  @Override
  public ImageResource refresh() {
    return resources.refresh();
  }

  @Override
  public ImageResource loading() {
    return resources.loading();
  }

}
