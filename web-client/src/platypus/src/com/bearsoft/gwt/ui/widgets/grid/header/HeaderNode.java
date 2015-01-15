package com.bearsoft.gwt.ui.widgets.grid.header;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.DraggableHeader;
import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.eas.client.form.published.PublishedStyle;
import com.google.gwt.user.cellview.client.Header;

public class HeaderNode<R, C> {

	protected GridColumn<R, C> column;
	protected HeaderNode<?, ?> parent;
	protected List<HeaderNode<?, ?>> children = new ArrayList<>();

	protected DraggableHeader<?> header;
	protected int leavesCount;
	protected int depthRemainder;
	protected PublishedStyle style;

	public HeaderNode() {
		super();
	}

	public HeaderNode(DraggableHeader<?> aHeader) {
		super();
		header = aHeader;
	}

	public GridColumn<R, C> getColumn() {
		return column;
	}

	public void setColumn(GridColumn<R, C> aValue) {
		column = aValue;
	}

	public HeaderNode<?, ?> getParent() {
		return parent;
	}

	public void setParent(HeaderNode<?, ?> aParent) {
		parent = aParent;
	}

	public List<HeaderNode<?, ?>> getChildren() {
		return children;
	}

	public Header<?> getHeader() {
		return header;
	}

	public void setHeader(DraggableHeader<?> aHeader) {
		header = aHeader;
	}

	public int getDepthRemainder() {
		return depthRemainder;
	}

	public int getLeavesCount() {
		return leavesCount;
	}

	public PublishedStyle getStyle() {
		return style;
	}

	public void setStyle(PublishedStyle aValue) {
		style = aValue;
	}
	
	public String getTitle(){
		return header.getTitle();
	}
	
	public void setTitle(String aTitle){
		header.setTitle(aTitle);
	}
}
