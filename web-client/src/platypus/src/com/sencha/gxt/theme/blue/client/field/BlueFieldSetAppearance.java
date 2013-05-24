/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.field;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.field.FieldSetDefaultAppearance;

public class BlueFieldSetAppearance extends FieldSetDefaultAppearance {

  public interface BlueFieldSetResources extends FieldSetResources {
    
    @Override
    @Source({"com/sencha/gxt/theme/base/client/field/FieldSet.css", "BlueFieldSet.css"})
    public BlueFieldSetStyle css();
  }
  
  public interface BlueFieldSetStyle extends FieldSetStyle {
    
  }
  
  
  public BlueFieldSetAppearance() {
    this(GWT.<BlueFieldSetResources>create(BlueFieldSetResources.class));
  }
  
  public BlueFieldSetAppearance(BlueFieldSetResources resources) {
    super(resources);
  }
  
}
