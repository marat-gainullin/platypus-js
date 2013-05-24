/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.statusproxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.statusproxy.StatusProxyBaseAppearance;

public class BlueStatusProxyAppearance extends StatusProxyBaseAppearance {

  public interface BlueStatusProxyResources extends StatusProxyResources, ClientBundle {

    ImageResource dropAllowed();

    ImageResource dropDisallowed();

    @Source({"com/sencha/gxt/theme/base/client/statusproxy/StatusProxy.css", "BlueStatusProxy.css"})
    BlueStatusProxyStyle style();

  }

  public interface BlueStatusProxyStyle extends StatusProxyStyle {
  }

  public BlueStatusProxyAppearance() {
    this(GWT.<BlueStatusProxyResources> create(BlueStatusProxyResources.class),
        GWT.<StatusProxyTemplates> create(StatusProxyTemplates.class));
  }

  public BlueStatusProxyAppearance(BlueStatusProxyResources resources, StatusProxyTemplates templates) {
    super(resources, templates);
  }

}
