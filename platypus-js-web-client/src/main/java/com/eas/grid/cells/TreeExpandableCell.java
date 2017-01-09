/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eas.grid.processing.TreeDataProvider;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * 
 * @author mg
 * @param <T>
 * @param <C>
 */
public class TreeExpandableCell<T, C> extends DivDecoratorCell<C> {

	public interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div class='{0}' style='{1}'><div style='height:100%'>{2}</div></div>")
		SafeHtml outerDiv(String aClasses, SafeStyles aStyle, SafeHtml cellContents);
	}

	public static int DEAFAULT_INDENT = 24;
	private static final Template template = GWT.create(Template.class);
	protected int indent;
	protected TreeDataProvider<T> treeProvider;
	protected boolean visible = true;

	public TreeExpandableCell(Cell<C> aCell) {
		this(aCell, null, DEAFAULT_INDENT);
	}

	public TreeExpandableCell(Cell<C> aCell, int aIndent) {
		this(aCell, null, aIndent);
	}

	public TreeExpandableCell(Cell<C> aCell, TreeDataProvider<T> aTreeProvider, int aIndent) {
		super(aCell);
		indent = aIndent;
		treeProvider = aTreeProvider;
	}

	public TreeDataProvider<T> getDataProvider() {
		return treeProvider;
	}

	public void setDataProvider(TreeDataProvider<T> aValue) {
		treeProvider = aValue;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean aValue) {
		visible = aValue;
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		if (treeProvider != null) {
			SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
			cell.render(context, value, cellBuilder);
			int deepness = getDeepness(context);
			int outerDivPadding = indent * deepness;
			SafeStylesBuilder styles = new SafeStylesBuilder();
			if (deepness > 0) {
				styles.paddingLeft(outerDivPadding, Style.Unit.PX);
			}
			styles.position(Style.Position.RELATIVE).height(100, Style.Unit.PCT).toSafeStyles();
			sb.append(template.outerDiv(outerDivClasses(context), styles.toSafeStyles(), cellBuilder.toSafeHtml()));
		} else {
			cell.render(context, value, sb);
		}
	}

	@Override
	public Set<String> getConsumedEvents() {
		if (visible) {
			if (treeProvider != null) {
				Set<String> consumed = new HashSet<>();
				consumed.addAll(cell.getConsumedEvents());
				consumed.add(BrowserEvents.MOUSEDOWN);
				return consumed;
			} else {
				return cell.getConsumedEvents();
			}
		} else {
			return null;
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
		if (treeProvider != null) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		} else {
			cell.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	@Override
	protected Element getCellParent(Element parent) {
		if (treeProvider != null) {
			return super.getCellParent(parent);
		} else {
			return parent;
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
		T element = treeProvider.getList().get(aContext.getIndex());
		return treeProvider.isExpanded(element);
	}

	protected boolean isExpandable(Cell.Context aContext) {
		T element = treeProvider.getList().get(aContext.getIndex());
		return !treeProvider.getTree().isLeaf(element);
	}

	/**
	 * 
	 * @param aContext
	 * @return Deepness value, 0 based. 0 means that an element is on the top
	 *         level (e.g. element is root of a forest).
	 */
	protected int getDeepness(Cell.Context aContext) {
		T element = treeProvider.getList().get(aContext.getIndex());
		List<T> path = treeProvider.buildPathTo(element);
		return path != null && !path.isEmpty() ? path.size() - 1 : 0;
	}

	@Override
	protected void onNonCellBrowserEvent(Cell.Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
		if (treeProvider != null) {
			if (BrowserEvents.MOUSEDOWN.equals(event.getType())) {
				T toBeToggled = treeProvider.getList().get(context.getIndex());
				treeProvider.getList().set(context.getIndex(), toBeToggled);
				if (isExpanded(context)) {
					treeProvider.collapse(toBeToggled);
				} else {
					treeProvider.expand(toBeToggled);
				}
			} else if (cell.getConsumedEvents().contains(event.getType())) {
				cell.onBrowserEvent(context, getCellParent(parent), value, event, valueUpdater);
			}
		}
	}
}
