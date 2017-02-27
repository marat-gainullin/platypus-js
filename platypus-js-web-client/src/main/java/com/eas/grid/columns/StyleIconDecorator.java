package com.eas.grid.columns;

import com.eas.grid.cells.RenderedEditorCell;
import com.eas.ui.PublishedCell;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class StyleIconDecorator {

	public static String decorate(SafeHtml toDecorate, String aId, PublishedCell aCell, VerticalAlignmentConstant valign, SafeHtmlBuilder sb) {
		SafeStylesBuilder stb = new SafeStylesBuilder();
		SafeUri imgSrc = null;
		if (aCell != null) {
			stb.append(SafeStylesUtils.fromTrustedString(aCell.toStyledWOBackground()));
			if (aCell.getIcon() != null) {
				ImageResource icon = aCell.getIcon();
				imgSrc = icon.getSafeUri();
			}
		}
		String decorId;
		if (aId != null && !aId.isEmpty()) {
			decorId = aId;
		} else {
			decorId = Document.get().createUniqueId();
		}
		if (imgSrc != null)
			sb.append(RenderedEditorCell.PaddedCell.INSTANCE.generate(stb.toSafeStyles(), imgSrc, decorId, toDecorate));
		else
			sb.append(RenderedEditorCell.PaddedCell.INSTANCE.generate(stb.toSafeStyles(), decorId, toDecorate));
		return decorId;
	}
}
