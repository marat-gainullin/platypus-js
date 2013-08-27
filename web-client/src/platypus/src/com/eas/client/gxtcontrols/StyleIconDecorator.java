package com.eas.client.gxtcontrols;

import com.eas.client.gxtcontrols.published.PublishedStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class StyleIconDecorator {

	interface IconDecoratorTemplate extends SafeHtmlTemplates {
		@Template("<div style=\"{0}position:relative;zoom:1;\">{1}<div>{2}</div></div>")
		SafeHtml outerDiv(SafeStyles padding, SafeHtml icon,
				SafeHtml cellContents);

		/**
		 * The wrapper around the image vertically aligned to the bottom.
		 */
		@Template("<div style=\"{0}position:absolute;bottom:0px;line-height:0px;\">{1}</div>")
		SafeHtml imageWrapperBottom(SafeStyles styles, SafeHtml image);

		/**
		 * The wrapper around the image vertically aligned to the middle.
		 */
		@Template("<div style=\"{0}position:absolute;top:50%;line-height:0px;\">{1}</div>")
		SafeHtml imageWrapperMiddle(SafeStyles styles, SafeHtml image);

		/**
		 * The wrapper around the image vertically aligned to the top.
		 */
		@Template("<div style=\"{0}position:absolute;top:0px;line-height:0px;\">{1}</div>")
		SafeHtml imageWrapperTop(SafeStyles styles, SafeHtml image);
	}

	private final static String direction = LocaleInfo.getCurrentLocale()
			.isRTL() ? "right" : "left";
	private static IconDecoratorTemplate iconTemplate = GWT.create(IconDecoratorTemplate.class);

	public static void decorate(SafeHtml toDecorate, PublishedStyle aStyle,
			VerticalAlignmentConstant valign,
			SafeHtmlBuilder sb) {		
		ImageResource icon = aStyle.getIcon();
		String styleString = aStyle.toStyled();		
		styleString += "padding-" + direction + ": "
				+ (icon != null ? icon.getWidth()+2 : 0) + "px;";
		SafeStyles outerDivStyle = SafeStylesUtils
				.fromTrustedString(styleString);
		sb.append(iconTemplate.outerDiv(outerDivStyle,
				icon != null ? getImageHtml(icon, valign, false)
						: getImageHtml(icon, valign, true), toDecorate));
	}
	

	/**
	 * Get the HTML representation of an image. Visible for testing.
	 * 
	 * @param res
	 *            the {@link ImageResource} to render as HTML
	 * @param valign
	 *            the vertical alignment
	 * @param isPlaceholder
	 *            if true, do not include the background image
	 * @return the rendered HTML
	 */
	protected static SafeHtml getImageHtml(ImageResource res,
			VerticalAlignmentConstant valign, boolean isPlaceholder) {
		// Get the HTML for the image.
		SafeHtml image;
		if (isPlaceholder) {
			image = SafeHtmlUtils.fromTrustedString("<div></div>");
		} else {
			AbstractImagePrototype proto = AbstractImagePrototype.create(res);
			image = SafeHtmlUtils.fromTrustedString(proto.getHTML());
		}

		// Create the wrapper based on the vertical alignment.
		SafeStylesBuilder cssStyles = new SafeStylesBuilder()
				.appendTrustedString(direction + ":0px;");
		if (HasVerticalAlignment.ALIGN_TOP == valign) {
			return iconTemplate.imageWrapperTop(cssStyles.toSafeStyles(), image);
		} else if (HasVerticalAlignment.ALIGN_BOTTOM == valign) {
			return iconTemplate.imageWrapperBottom(cssStyles.toSafeStyles(), image);
		} else {
			int halfHeight = res != null ? (int) Math
					.round(res.getHeight() / 2.0) : 0;
			cssStyles.appendTrustedString("margin-top:-" + halfHeight + "px;");
			return iconTemplate.imageWrapperMiddle(cssStyles.toSafeStyles(), image);
		}
	}
}
