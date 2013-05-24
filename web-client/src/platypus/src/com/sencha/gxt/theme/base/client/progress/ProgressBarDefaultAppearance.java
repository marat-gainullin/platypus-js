/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.progress;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.ProgressBarCell.ProgressBarAppearance;
import com.sencha.gxt.cell.core.client.ProgressBarCell.ProgressBarAppearanceOptions;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Format;

public class ProgressBarDefaultAppearance implements ProgressBarAppearance {

  public interface ProgressBarResources {

    ImageResource bar();

    ProgressBarStyle style();
  }
  public interface ProgressBarStyle extends CssResource {

    String progressBar();

    String progressInner();

    String progressText();

    String progressTextBack();

    String progressWrap();

  }

  public interface ProgressBarTemplate extends XTemplates {

    @XTemplate(source = "ProgressBar.html")
    SafeHtml render(SafeHtml text, ProgressBarStyle style, SafeStyles wrapStyles, SafeStyles progressBarStyles, SafeStyles progressTextStyles,
        SafeStyles widthStyles);

  }

  private final ProgressBarStyle style;
  private ProgressBarTemplate template;

  public ProgressBarDefaultAppearance(ProgressBarResources resources, ProgressBarTemplate template) {
    this.style = resources.style();
    this.style.ensureInjected();
    this.template = template;
  }

  @Override
  public void render(SafeHtmlBuilder sb, Double value, ProgressBarAppearanceOptions options) {
    value = value == null ? 0 : value;
    double valueWidth = value * options.getWidth();

    int vw = new Double(valueWidth).intValue();

    String text = options.getProgressText();

    if (text != null) {
      int v = (int) Math.round(Double.valueOf(value) * 100);
      text = Format.substitute(text, v);
    }

    SafeHtml txt;
    if (text == null) {
      txt = SafeHtmlUtils.fromSafeConstant("&#160;");
    } else {
      txt = SafeHtmlUtils.fromString(text);
    }
    
    int adj = GXT.isIE() ? 4 : 2;

    SafeStyles wrapStyles = SafeStylesUtils.fromTrustedString("width:" + (options.getWidth() - adj) + "px;");
    SafeStyles progressBarStyles = SafeStylesUtils.fromTrustedString("width:" + vw + "px;");
    SafeStyles progressTextStyles = SafeStylesUtils.fromTrustedString("width:" + Math.max(vw - 8, 0) + "px;");
    SafeStyles widthStyles = SafeStylesUtils.fromTrustedString("width:" + (Math.max(0, options.getWidth() - adj)) + "px;");
    sb.append(template.render(txt, style, wrapStyles, progressBarStyles, progressTextStyles, widthStyles));
  }

}