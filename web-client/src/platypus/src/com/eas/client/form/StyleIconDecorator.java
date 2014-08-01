package com.eas.client.form;

import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.eas.client.form.published.PublishedStyle;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class StyleIconDecorator {

	public static String decorate(SafeHtml toDecorate, String aId, PublishedStyle aStyle, VerticalAlignmentConstant valign, SafeHtmlBuilder sb) {
		SafeStylesBuilder stb = new SafeStylesBuilder();
		stb.padding(RenderedEditorCell.CELL_PADDING, Style.Unit.PX);
		if (aStyle != null) {
			stb.append(SafeStylesUtils.fromTrustedString(aStyle.toStyledWOBackground()));
			if (aStyle.getIcon() != null) {
				ImageResource icon = aStyle.getIcon();
				stb.paddingLeft(icon.getWidth() + RenderedEditorCell.CELL_PADDING, Style.Unit.PX).backgroundImage(icon.getSafeUri()).trustedNameAndValue("background-position-y", "center");
			}
		}
		RenderedEditorCell.CellsResources.INSTANCE.tablecell().ensureInjected();
		String decorId;
		if (aId != null && !aId.isEmpty()) {
			decorId = aId;
		} else {
			decorId = Document.get().createUniqueId();
		}
		sb.append(RenderedEditorCell.PaddedCell.INSTANCE.generate(decorId, RenderedEditorCell.CellsResources.INSTANCE.tablecell().padded(), stb.toSafeStyles(), toDecorate));
		return decorId;
	}
}
