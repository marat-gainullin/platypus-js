/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.status.BoxStatusBaseAppearance;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;

public class GrayBoxStatusAppearance extends BoxStatusBaseAppearance implements BoxStatusAppearance {

  public interface GrayBoxStatusStyle extends BoxStatusStyle {

    String status();

    String statusIcon();

    String statusText();

    String statusBox();

  }

  public interface GrayBoxStatusResources extends BoxStatusResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/status/Status.css", "GrayBoxStatus.css"})
    GrayBoxStatusStyle style();

    @Override
    @Source("com/sencha/gxt/theme/base/client/grid/loading.gif")
    ImageResource loading();

  }

  public GrayBoxStatusAppearance() {
    this(GWT.<GrayBoxStatusResources> create(GrayBoxStatusResources.class), GWT.<BoxTemplate> create(BoxTemplate.class));
  }

  public GrayBoxStatusAppearance(GrayBoxStatusResources resources, BoxTemplate template) {
    super(resources, template);
  }

}
