package com.eas.client.form.grid.rows;

import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.processing.TreeAdapter;
import com.bearsoft.rowset.Utils.JsObject;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

public class JsTree extends TreeAdapter<JavaScriptObject> {

	protected String parentField;
	protected String childrenField;
	protected Runnable onLoadStart;
	protected Callback<Void, String> onError;

	public JsTree(JavaScriptObject aData, String aParentField, String aChildrenField, Runnable aOnLoadStart, Callback<Void, String> aOnError) {
		super();
		parentField = aParentField;
		childrenField = aChildrenField;
		onLoadStart = aOnLoadStart;
		onError = aOnError;
	}

	protected boolean hasRowChildren(JavaScriptObject parent) {
		List<JavaScriptObject> children = findChildren(parent);
		return children != null && !children.isEmpty();
	}

	protected List<JavaScriptObject> findChildren(JavaScriptObject aParent) {
		JavaScriptObject children = aParent.<JsObject>cast().getJs(childrenField);
		return children != null ? new JsArrayList(children) : null;
	}

	// Tree structure of a rowset
	@Override
	public JavaScriptObject getParentOf(JavaScriptObject anElement) {
		return anElement.<JsObject>cast().getJs(parentField);
	}

	@Override
	public List<JavaScriptObject> getChildrenOf(JavaScriptObject anElement) {
		return findChildren(anElement);
	}

	@Override
	public boolean isLeaf(JavaScriptObject anElement) {
		return !hasRowChildren(anElement);
	}

	// Tree mutation methods
	@Override
	public void add(JavaScriptObject aParent, JavaScriptObject anElement) {
	}

	@Override
	public void add(int aIndex, JavaScriptObject aParent, JavaScriptObject anElement) {
	}

	@Override
	public void addAfter(JavaScriptObject afterElement, JavaScriptObject anElement) {
	}

	@Override
	public void remove(JavaScriptObject anElement) {
	}
}
