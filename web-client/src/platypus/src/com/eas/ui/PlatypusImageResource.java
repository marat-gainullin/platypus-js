package com.eas.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.AppClient;
import com.eas.client.CallbackAdapter;
import com.eas.core.Utils;
import com.google.gwt.core.client.Callback;
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

	protected String name;
	protected int width;
	protected int height;

	public PlatypusImageResource(String aName, int aWidth, int aHeight) {
		super();
		name = aName;
		width = aWidth;
		height = aHeight;
	}

	public static void jsLoad(final String aResourceName, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) {
		final String callerDir = Utils.lookupCallerJsDir();
		String normalizedResourceName = aResourceName.startsWith("./") || aResourceName.startsWith("../") ? AppClient.toFilyAppModuleId(aResourceName, callerDir) : aResourceName;
		load(normalizedResourceName, new CallbackAdapter<ImageResource, String>() {

			@Override
			public void onFailure(String reason) {
				if (aOnFailure != null) {
					try {
						Utils.executeScriptEventVoid(null, aOnFailure, reason);
					} catch (Exception ex) {
						Logger.getLogger(PlatypusImageResource.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

			@Override
			protected void doWork(ImageResource aResult) throws Exception {
				if (aOnSuccess != null) {
					Utils.executeScriptEventVoid(null, aOnSuccess, aResult);
				}
			}

		});
	}

	public static void load(final String aName, final Callback<ImageResource, String> aCallback) {
		if (aName != null && !aName.isEmpty()) {
			SafeUri imageUri = AppClient.getInstance().getResourceUri(aName);
			final Image im = new Image(imageUri);
			im.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					int imWidth = im.getWidth();
					int imHeight = im.getHeight();
					im.removeFromParent();
					ImageResource loaded = new PlatypusImageResource(aName, imWidth, imHeight);
					if (aCallback != null) {
						aCallback.onSuccess(loaded);
					}
				}
			});
			im.addErrorHandler(new ErrorHandler() {
				@Override
				public void onError(ErrorEvent event) {
					im.removeFromParent();
					if (aCallback != null) {
						aCallback.onFailure("Error while loading an image " + aName);
					}
				}
			});
			im.setVisible(false);
			im.getElement().getStyle().setPosition(Position.ABSOLUTE);
			im.getElement().getStyle().setLeft(-Integer.MAX_VALUE / 2, Style.Unit.PX);
			im.getElement().getStyle().setTop(-Integer.MAX_VALUE / 2, Style.Unit.PX);
			RootPanel.get().add(im);
		}
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
		return AppClient.getInstance().getResourceUri(name);
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
