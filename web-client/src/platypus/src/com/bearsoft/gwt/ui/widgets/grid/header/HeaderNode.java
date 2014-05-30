package com.bearsoft.gwt.ui.widgets.grid.header;

import java.util.ArrayList;
import java.util.List;

import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedFont;
import com.eas.client.form.published.PublishedStyle;
import com.google.gwt.user.cellview.client.Header;

public class HeaderNode {

	protected HeaderNode parent;
	protected List<HeaderNode> children = new ArrayList<>();

	protected Header<?> header;
	protected int leavesCount;
	protected int depthRemainder;
	protected PublishedStyle style;

	public HeaderNode() {
		super();
	}

	public HeaderNode(Header<?> aHeader) {
		super();
		header = aHeader;
	}

	public HeaderNode getParent() {
		return parent;
	}

	public void setParent(HeaderNode aParent) {
		parent = aParent;
	}

	public List<HeaderNode> getChildren() {
		return children;
	}

	public Header<?> getHeader() {
		return header;
	}

	public void setHeader(Header<?> aHeader) {
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
}
