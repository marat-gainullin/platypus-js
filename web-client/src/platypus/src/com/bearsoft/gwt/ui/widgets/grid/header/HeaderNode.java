package com.bearsoft.gwt.ui.widgets.grid.header;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.Header;

public class HeaderNode {

	protected HeaderNode parent;
	protected List<HeaderNode> children = new ArrayList<>();

	protected Header<?> header;
	protected int leavesCount;
	protected int depthRemainder;

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
}
