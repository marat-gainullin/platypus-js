/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import java.util.List;

/**
 * 
 * @author mg
 * @param <T>
 * @param <C>
 */
public class TreeExpandableCell<T, C> extends DivDecoratorCell<C> {

	public interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div class=\"{0}\" style=\"background-position: {1}px; padding-left: {2}px; position:relative;zoom:1;\"><div>{3}</div></div>")
		SafeHtml outerDiv(String aClasses, int iconPadding, int padding, SafeHtml cellContents);
	}

	private static final Template template = GWT.create(Template.class);
	protected int indent;
	protected TreeDataProvider<T> dataProvider;

	public TreeExpandableCell(Cell<C> aCell, TreeDataProvider<T> aDataProvider, int aIndent) {
		super(aCell);
		dataProvider = aDataProvider;
		indent = aIndent;
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		if (dataProvider != null) {
			SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
			cell.render(context, value, cellBuilder);
			sb.append(template.outerDiv(outerDivClasses(context), indent * getDeepness(context), outerDivPadding(context), cellBuilder.toSafeHtml()));
		} else {
			cell.render(context, value, sb);
		}
	}

	@Override
	protected int outerDivPadding(Cell.Context aContext) {
		return indent * (getDeepness(aContext) + 1);
	}

	@Override
	protected String outerDivClasses(Cell.Context aContext) {
		return isExpandable(aContext) ? (isExpanded(aContext) ? "treegrid-expanded-cell" : "treegrid-collapased-cell") : "";
	}

	protected boolean isExpanded(Cell.Context aContext) {
		T element = dataProvider.getList().get(aContext.getIndex());
		return dataProvider.isExpanded(element);
	}

	protected boolean isExpandable(Cell.Context aContext) {
		T element = dataProvider.getList().get(aContext.getIndex());
		return !dataProvider.getTree().isLeaf(element);
	}

	/**
	 * 
	 * @param aContext
	 * @return Deepness value, 0 based. 0 means that an element is on the top
	 *         level (e.g. element is root of a forest).
	 */
	protected int getDeepness(Cell.Context aContext) {
		T element = dataProvider.getList().get(aContext.getIndex());
		List<T> path = dataProvider.buildPathTo(element);
		return path != null && !path.isEmpty() ? path.size() - 1 : 0;
	}

	@Override
	protected void onNonCellBrowserEvent(Cell.Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
		if (dataProvider != null) {
			if (BrowserEvents.MOUSEDOWN.equals(event.getType())) {
				T toBeToggled = dataProvider.getList().get(context.getIndex());
				dataProvider.getList().set(context.getIndex(), toBeToggled);
				if (isExpanded(context)) {
					dataProvider.collapse(toBeToggled);
				} else {
					dataProvider.expand(toBeToggled);
				}
			}
		}
	}
}
