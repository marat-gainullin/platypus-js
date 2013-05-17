package com.eas.client.application;

import java.util.ArrayList;
import java.util.List;

import com.eas.client.ImageResourceCallback;
import com.eas.client.Utils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class PlatypusImageResource implements ImageResource {

	protected AppClient client;
	protected String name;
	protected int width;
	protected int height;
	protected boolean pending = true;
	protected List<ImageResourceCallback> javaCallbacks = new ArrayList();
	protected List<JavaScriptObject> jsCallbacks = new ArrayList();

	public PlatypusImageResource(AppClient aClient, String aName) {
		client = aClient;
		name = aName;
		final Image im = new Image(getSafeUri());
		im.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				width = im.getWidth();
				height = im.getHeight();
				im.removeFromParent();
				try {
					for (ImageResourceCallback javaCallback : javaCallbacks)
						javaCallback.run(PlatypusImageResource.this);
					for (JavaScriptObject jsCallback : jsCallbacks)
						Utils.invokeJsFunction(jsCallback);
				} finally {
					javaCallbacks.clear();
					jsCallbacks.clear();
					pending = false;
				}
			}
		});
		im.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				im.removeFromParent();
				javaCallbacks.clear();
				jsCallbacks.clear();
				pending = false;
			}
		});

		im.setVisible(false);
		im.getElement().getStyle().setPosition(Position.ABSOLUTE);
		im.getElement().getStyle().setLeft(-Integer.MAX_VALUE / 2, Style.Unit.PX);
		im.getElement().getStyle().setTop(-Integer.MAX_VALUE / 2, Style.Unit.PX);
		RootPanel.get().add(im);
	}

	public PlatypusImageResource addJavaCallback(ImageResourceCallback aCallback) {
		if (pending)
			javaCallbacks.add(aCallback);
		return this;
	}

	public PlatypusImageResource addJsCallback(JavaScriptObject aCallback) {
		if (pending)
			jsCallbacks.add(aCallback);
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getLeft() {
		return 0;
	}

	@Override
	public SafeUri getSafeUri() {
		return client.getResourceUri(name);
	}

	@Override
	public int getTop() {
		return 0;
	}

	@Override
	public String getURL() {
		return getSafeUri().asString();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public boolean isAnimated() {
		return true;
	}
}
