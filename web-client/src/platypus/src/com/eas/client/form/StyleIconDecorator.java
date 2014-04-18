package com.eas.client.form;

import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedPopupEditorCell;
import com.eas.client.form.published.PublishedStyle;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class StyleIconDecorator {

	protected static int decorated = -Integer.MAX_VALUE;
	
	public static String decorate(SafeHtml toDecorate, PublishedStyle aStyle, VerticalAlignmentConstant valign, SafeHtmlBuilder sb) {
		SafeStylesBuilder stb = new SafeStylesBuilder();
			stb
			.append(SafeStylesUtils.fromTrustedString(aStyle.toStyled()))
			.padding(RenderedPopupEditorCell.CELL_PADDING, Style.Unit.PX);
		if(aStyle.getIcon() != null){
			ImageResource icon = aStyle.getIcon();
			stb
				.paddingLeft(icon.getWidth() + RenderedPopupEditorCell.CELL_PADDING, Style.Unit.PX)
				.backgroundImage(icon.getSafeUri());
		}
		RenderedPopupEditorCell.CellsResources.INSTANCE.tablecell().ensureInjected();
		String decorId = "decor-" + (++decorated);
		sb.append(RenderedPopupEditorCell.PaddedCell.INSTANCE.generate(decorId, RenderedPopupEditorCell.CellsResources.INSTANCE.tablecell().padded(), stb.toSafeStyles(), toDecorate));
		return decorId;
	}
}
