/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.form.FileUploadField.FileUploadFieldAppearance;

public class FileUploadDefaultAppearance implements FileUploadFieldAppearance {

  public interface FileUploadResources extends ClientBundle {
    @Source("FileUpload.css")
    FileUploadStyle css();
  }

  public interface FileUploadStyle extends CssResource {
    String buttonWrap();

    String file();

    String input();

    String wrap();

  }

  public interface FileUploadTemplate extends XTemplates {
    @XTemplate("<div class='{style.wrap}'></div>")
    SafeHtml render(FileUploadStyle style);
  }

  private final FileUploadResources resources;
  private final FileUploadStyle style;
  private final FileUploadTemplate template;

  public FileUploadDefaultAppearance() {
    this(GWT.<FileUploadResources> create(FileUploadResources.class));
  }

  public FileUploadDefaultAppearance(FileUploadResources resources) {
    this.resources = resources;
    this.style = this.resources.css();

    StyleInjectorHelper.ensureInjected(this.style, true);

    this.template = GWT.create(FileUploadTemplate.class);
  }

  @Override
  public String fileInputClass() {
    return style.file();
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.append(template.render(style));
  }

  @Override
  public String wrapClass() {
    return style.wrap();
  }

  @Override
  public String textFieldClass() {
    return style.input();
  }

  @Override
  public String buttonClass() {
    return style.buttonWrap();
  }

}
