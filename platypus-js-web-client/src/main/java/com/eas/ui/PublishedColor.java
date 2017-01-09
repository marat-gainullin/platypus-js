package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class PublishedColor extends JavaScriptObject {

	protected static RegExp rgbPattern = RegExp.compile("rgb *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *\\)");
	protected static RegExp rgbaPattern = RegExp.compile("rgba *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *, *([0-9]*\\.?[0-9]+) *\\)");

	public static PublishedColor parse(String aInput) {
		if (aInput != null && !aInput.isEmpty()) {
			if (aInput.startsWith("#")) {
				Integer intval = Integer.decode(aInput);
				int i = intval.intValue();
				return PublishedColor.create((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, 0xFF);
			} else {
				MatchResult m = rgbPattern.exec(aInput);
				if (m != null) {
					return PublishedColor.create(Integer.valueOf(m.getGroup(1)), // r
					        Integer.valueOf(m.getGroup(2)), // g
					        Integer.valueOf(m.getGroup(3)), // b
					        0xFF); // a
				} else {
					MatchResult m1 = rgbaPattern.exec(aInput);
					if (m1 != null) {
						return PublishedColor.create(Integer.valueOf(m1.getGroup(1)), // r
						        Integer.valueOf(m1.getGroup(2)), // g
						        Integer.valueOf(m1.getGroup(3)), // b
						        Math.round(Float.valueOf(m1.getGroup(3)) * 255)); // a
					}
				}
			}
		}
		return null;
	}

	protected PublishedColor() {
	}

	public final native int getRed()/*-{
		return this.red != null ? this.red : 0;
	}-*/;

	public final native int getGreen()/*-{
		return this.green != null ? this.green : 0;
	}-*/;

	public final native int getBlue()/*-{
		return this.blue != null ? this.blue : 0;
	}-*/;

	public final native int getAlpha()/*-{
		return this.alpha != null ? this.alpha : 0;
	}-*/;
	
	public final native String toStyled()/*-{
		return this.toStyled();
	}-*/;
	
	public static native PublishedColor create(int aR, int aG, int aB, int aA)/*-{
		var Color = @com.eas.ui.JsUi::Color;
		return new Color(aR, aG, aB, aA);
	}-*/;
}
