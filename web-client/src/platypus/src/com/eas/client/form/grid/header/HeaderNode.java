package com.eas.client.form.grid.header;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HasHTML;

public class HeaderNode implements HasHTML {

	protected HeaderNode parent;
	protected List<HeaderNode> children = new ArrayList<>();

	protected String text;
	protected boolean asHtml;
	protected int leavesCount;
	protected int deepnessRemainder;

	public HeaderNode() {
		super();
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

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String aValue) {
		text = aValue;
		asHtml = false;
	}

	@Override
	public String getHTML() {
		return asHtml ? text : null;
	}

	@Override
	public void setHTML(String aValue) {
		text = aValue;
		asHtml = true;
	}
}
