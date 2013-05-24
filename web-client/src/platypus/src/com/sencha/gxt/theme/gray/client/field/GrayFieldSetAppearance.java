/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.field;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.field.FieldSetDefaultAppearance;

public class GrayFieldSetAppearance extends FieldSetDefaultAppearance {

  public interface GrayFieldSetResources extends FieldSetResources {
    
    @Override
    @Source({"com/sencha/gxt/theme/base/client/field/FieldSet.css", "GrayFieldSet.css"})
    public GrayFieldSetStyle css();
  }
  
  public interface GrayFieldSetStyle extends FieldSetStyle {
    
  }
  
  
  public GrayFieldSetAppearance() {
    this(GWT.<GrayFieldSetResources>create(GrayFieldSetResources.class));
  }
  
  public GrayFieldSetAppearance(GrayFieldSetResources resources) {
    super(resources);
  }
  
}
