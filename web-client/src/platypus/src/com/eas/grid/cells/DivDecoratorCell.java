/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * 
 * @author mg
 * @param <C>
 *            The data object's
 */
public abstract class DivDecoratorCell<C> implements Cell<C> {

	public interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div class=\"{0}\" style=\"padding-left: {1}px; position:relative;zoom:1;\"><div>{2}</div></div>")
		SafeHtml outerDiv(String aClasses, int padding, SafeHtml cellContents);
	}

	private static final Template template = GWT.create(Template.class);

	protected Cell<C> cell;

	public DivDecoratorCell(Cell<C> aCell) {
		super();
		cell = aCell;
	}

	public Cell<C> getCell() {
		return cell;
	}

	public void setCell(Cell<C> aCell) {
		cell = aCell;
	}

	@Override
	public boolean dependsOnSelection() {
		return cell.dependsOnSelection();
	}

	@Override
	public boolean handlesSelection() {
		return cell.handlesSelection();
	}

	@Override
	public boolean isEditing(Context context, Element parent, C value) {
		return cell.isEditing(context, getCellParent(parent), value);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
		if (Element.is(event.getEventTarget()) && getCellParent(parent).isOrHasChild(Element.as(event.getEventTarget()))) {
			if (cell.getConsumedEvents().contains(event.getType())) {
				cell.onBrowserEvent(context, getCellParent(parent), value, event, valueUpdater);
			}
		} else {
			onNonCellBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	protected abstract void onNonCellBrowserEvent(Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater);

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
		cell.render(context, value, cellBuilder);
		sb.append(template.outerDiv(outerDivClasses(context), outerDivPadding(context), cellBuilder.toSafeHtml()));
	}

	@Override
	public boolean resetFocus(Context context, Element parent, C value) {
		return cell.resetFocus(context, getCellParent(parent), value);
	}

	@Override
	public void setValue(Context context, Element parent, C value) {
		cell.setValue(context, getCellParent(parent), value);
	}

	/**
	 * Get the parent element of the decorated cell.
	 * 
	 * @param parent
	 *            the parent of this cell
	 * @return the decorated cell's parent
	 */
	protected Element getCellParent(Element parent) {
		return parent.getFirstChildElement().getFirstChildElement();
	}

	protected abstract int outerDivPadding(Context aContext);

	protected abstract String outerDivClasses(Context aContext);
}
