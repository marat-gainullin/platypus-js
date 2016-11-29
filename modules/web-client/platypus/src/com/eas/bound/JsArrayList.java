package com.eas.bound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.eas.core.Utils.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

public class JsArrayList implements List<JavaScriptObject> {

	protected class WrapperIterator implements ListIterator<JavaScriptObject> {

		protected Object[] snapshot = toArray();
		protected int cursor = -1;

		public WrapperIterator(int aCursor) {
			super();
			cursor = aCursor;
		}

		public WrapperIterator() {
			super();
		}

		@Override
		public boolean hasNext() {
			return !JsArrayList.this.isEmpty() && cursor < snapshot.length - 1;
		}

		@Override
		public JavaScriptObject next() {
			return (JavaScriptObject) snapshot[++cursor];
		}

		@Override
		public void remove() {
			JsArrayList.this.remove(cursor);
		}

		@Override
		public void set(JavaScriptObject e) {
			JsArrayList.this.set(cursor, e);
		}

		@Override
		public void add(JavaScriptObject e) {
			JsArrayList.this.add(cursor, e);
		}

		@Override
		public boolean hasPrevious() {
			return !JsArrayList.this.isEmpty() && cursor > 0;
		}

		@Override
		public JavaScriptObject previous() {
			return (JavaScriptObject) snapshot[--cursor];
		}

		@Override
		public int nextIndex() {
			return cursor + 1;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}
	};

	protected final JsObject data;
	protected final JsObject splice;

	public JsArrayList(JavaScriptObject aData) {
		super();
		data = aData.cast();
		JavaScriptObject oSplice = data.getJs("splice");
		splice = oSplice != null ? oSplice.<JsObject>cast() : null;
	}

	@Override
	public int size() {
		return data != null ? data.getInteger("length") : 0;
	}

	@Override
	public boolean isEmpty() {
		return data == null || size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public Iterator<JavaScriptObject> iterator() {
		return new WrapperIterator();
	}

	@Override
	public Object[] toArray() {
		Object[] snapshot = new Object[size()];
		for (int i = 0; i < size(); i++)
			snapshot[i] = get(i);
		return snapshot;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) toArray();
	}

	@Override
	public boolean add(JavaScriptObject e) {
		data.<JsArrayMixed> cast().push(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index > -1) {
			remove(index);
			return true;
		} else
			return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends JavaScriptObject> c) {
		if (c != null) {
			for (JavaScriptObject jso : c) {
				add(jso);
			}
			return true;
		} else
			return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends JavaScriptObject> c) {
		if (c != null && index >= 0 && index <= size()) {
			int added = 0;
			for (JavaScriptObject jso : c) {
				add(index + added, jso);
				added++;
			}
			return true;
		} else
			return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		if (c != null) {
			for (Object o : c) {
				boolean removed = remove(o);
				if (removed)
					modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		for (int i = size() - 1; i >= 0; i--) {
			JavaScriptObject item = get(i);
			if (!c.contains(item)) {
				remove(i);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		data.<JsArrayMixed> cast().setLength(0);
	}

	@Override
	public JavaScriptObject get(int index) {
		return data.getSlot(index);
	}

	@Override
	public JavaScriptObject set(int index, JavaScriptObject element) {
		if (index >= 0 && index < size()) {
			JavaScriptObject was = get(index);
			data.setSlot(index, element);
			return was;
		} else
			return null;
	}

	@Override
	public void add(int index, JavaScriptObject element) {
		JsArrayMixed args = JavaScriptObject.createArray().cast();
		args.push(index);
		args.push(0);
		args.push(element);
		splice.apply(data, args);
	}

	@Override
	public JavaScriptObject remove(int index) {
		JavaScriptObject res = data.<JsArrayMixed> cast().getObject(index);
		if(res != null){
			JsArrayMixed args = JavaScriptObject.createArray().cast();
			args.push(index);
			args.push(1);
			splice.apply(data, args);
		}
		return res;
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < size(); i++) {
			JavaScriptObject item = get(i);
			if (item == o)
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size() - 1; i >= 0; i--) {
			JavaScriptObject item = get(i);
			if (item == o)
				return i;
		}
		return -1;
	}

	@Override
	public ListIterator<JavaScriptObject> listIterator() {
		return new WrapperIterator();
	}

	@Override
	public ListIterator<JavaScriptObject> listIterator(int index) {
		return new WrapperIterator(index);
	}

	@Override
	public List<JavaScriptObject> subList(int fromIndex, int toIndex) {
		List<JavaScriptObject> res = new ArrayList<>();
		for (int i = fromIndex; i <= toIndex; i++) {
			if (i >= 0 && i < size())
				res.add(get(i));
		}
		return res;
	}

}
