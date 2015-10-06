package com.eas.ui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.eas.core.Utils;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public abstract class UiReader {

	private static Set<UiWidgetReader> factories = new HashSet<>();

	public static void addFactory(UiWidgetReader aReader) {
		factories.add(aReader);
	}

	public static Set<UiWidgetReader> getFactories() {
		return factories;
	}

	public abstract UIObject readWidget(Element anElement) throws Exception;

	public abstract PublishedFont readFont(Element anElement) throws Exception;

	public abstract void addResolver(Runnable aResolver);

	public abstract Map<String, UIObject> getWidgets();

	public abstract void readImageParagraph(Element anElement, UIObject aTarget) throws Exception;

	public abstract void readGeneralProps(Element anElement, UIObject aTarget) throws Exception;

	public abstract Utils.JsObject resolveEntity(String anEntityName) throws Exception;
}
