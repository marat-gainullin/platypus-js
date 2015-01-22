package com.eas.client.form.grid.rows;

import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.processing.TreeAdapter;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.beans.PropertyChangeEvent;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

public class JsTree extends TreeAdapter<JavaScriptObject> implements JsDataContainer {

	protected JavaScriptObject data;
	protected HandlerRegistration boundToData;
	protected String parentField;
	protected String childrenField;
	protected Runnable onResize;
	protected Callback<Void, String> onError;

	public JsTree(String aParentField, String aChildrenField, Runnable aOnResize, Callback<Void, String> aOnError) {
		super();
		parentField = aParentField;
		childrenField = aChildrenField;
		onResize = aOnResize;
		onError = aOnError;
	}

	protected void bind() {
		if (data != null) {
			boundToData = Utils.listen(data, "length", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (onResize != null)
						onResize.run();
				}
			});
			if (onResize != null)
				onResize.run();
		}
	}

	protected void unbind() {
		if (boundToData != null) {
			boundToData.removeHandler();
			boundToData = null;
		}
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue;
			bind();
		}
	}

	protected boolean hasRowChildren(JavaScriptObject parent) {
		List<JavaScriptObject> children = findChildren(parent);
		return children != null && !children.isEmpty();
	}

	protected List<JavaScriptObject> findChildren(JavaScriptObject aParent) {
		if (aParent == null) {
			return data != null ? new JsArrayList(data) : null;
		} else {
			JavaScriptObject children = aParent.<JsObject> cast().getJs(childrenField);
			return children != null ? new JsArrayList(children) : null;
		}
	}

	// Tree structure of a rowset
	@Override
	public JavaScriptObject getParentOf(JavaScriptObject anElement) {
		return anElement.<JsObject> cast().getJs(parentField);
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
