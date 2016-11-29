package com.eas.grid.columns.header;

import java.util.ArrayList;
import java.util.List;

import com.eas.grid.GridColumn;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.google.gwt.user.cellview.client.Header;

public class HeaderNode<T> {

	protected GridColumn<T, ?> column;
	protected HeaderNode<T> parent;
	protected List<HeaderNode<T>> children = new ArrayList<>();

	protected PublishedColor background;
	protected PublishedColor foreground;
	protected PublishedFont font;
	protected Header<String> header;
	protected int leavesCount;
	protected int depthRemainder;

	public HeaderNode() {
		super();
	}

	public HeaderNode(Header<String> aHeader) {
		super();
		header = aHeader;
	}

	public HeaderNode<T> lightCopy() {
		HeaderNode<T> copied = new HeaderNode<T>();
		copied.setColumn(column);
		copied.setHeader(header);
		copied.setBackground(background);
		copied.setForeground(foreground);
		copied.setFont(font);
		return copied;
	}

	public PublishedColor getBackground() {
		return background;
	}

	public void setBackground(PublishedColor aValue) {
		background = aValue;
	}

	public PublishedColor getForeground() {
		return foreground;
	}

	public void setForeground(PublishedColor aValue) {
		foreground = aValue;
	}

	public PublishedFont getFont() {
		return font;
	}

	public void setFont(PublishedFont aValue) {
		font = aValue;
	}

	public GridColumn<T, ?> getColumn() {
		return column;
	}

	public void setColumn(GridColumn<T, ?> aValue) {
		column = aValue;
	}

	public HeaderNode<T> getParent() {
		return parent;
	}

	public void setParent(HeaderNode<T> aParent) {
		parent = aParent;
	}

	public List<HeaderNode<T>> getChildren() {
		return children;
	}

	public Header<String> getHeader() {
		return header;
	}

	public void setHeader(Header<String> aHeader) {
		header = aHeader;
	}

	public boolean removeColumnNode(HeaderNode<T> aNode) {
		if (children != null) {
			return children.remove(aNode);
		}else
			return false;
	}

	public void addColumnNode(HeaderNode<T> aNode) {
		if (children == null) {
			children = new ArrayList<>();
		}
		if (!children.contains(aNode)) {
			children.add(aNode);
			aNode.setParent(this);
		}
	}

	public void insertColumnNode(int atIndex, HeaderNode<T> aNode) {
		if (children == null) {
			children = new ArrayList<>();
		}
		if (!children.contains(aNode) && atIndex >= 0 && atIndex <= children.size()) {
			children.add(atIndex, aNode);
			aNode.setParent(this);
		}
	}

	public int getDepthRemainder() {
		return depthRemainder;
	}

	public int getLeavesCount() {
		return leavesCount;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}
}
