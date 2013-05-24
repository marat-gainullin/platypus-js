/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.statusproxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.statusproxy.StatusProxyBaseAppearance;

public class GrayStatusProxyAppearance extends StatusProxyBaseAppearance {

  public interface GrayStatusProxyResources extends StatusProxyResources, ClientBundle {

    ImageResource dropAllowed();

    ImageResource dropDisallowed();

    @Source({"com/sencha/gxt/theme/base/client/statusproxy/StatusProxy.css", "GrayStatusProxy.css"})
    GrayStatusProxyStyle style();

  }

  public interface GrayStatusProxyStyle extends StatusProxyStyle {
  }

  public GrayStatusProxyAppearance() {
    this(GWT.<GrayStatusProxyResources> create(GrayStatusProxyResources.class),
        GWT.<StatusProxyTemplates> create(StatusProxyTemplates.class));
  }

  public GrayStatusProxyAppearance(GrayStatusProxyResources resources, StatusProxyTemplates templates) {
    super(resources, templates);
  }

}
