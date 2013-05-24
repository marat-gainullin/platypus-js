/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.engine;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.Gradient;
import com.sencha.gxt.chart.client.draw.Matrix;
import com.sencha.gxt.chart.client.draw.Rotation;
import com.sencha.gxt.chart.client.draw.Scaling;
import com.sencha.gxt.chart.client.draw.Stop;
import com.sencha.gxt.chart.client.draw.Translation;
import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.CurveTo;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;
import com.sencha.gxt.chart.client.draw.sprite.EllipseSprite;
import com.sencha.gxt.chart.client.draw.sprite.ImageSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.TextMetrics;

/**
 * Provides specific methods to draw with VML.
 * 
 */
public class VML extends DomSurface {

  /**
   * Returns the given number rounded to the given precision.
   * 
   * @param number the number to be rounded
   * @param place the decimal place
   * @return the rounded number
   */
  protected static native String toFixed(double number, double place) /*-{
		return number.toFixed(place);
  }-*/;

  private final String baseVMLClass = "x-vml-base";
  private final String groupVMLClass = "x-vml-group";
  private final String hideClass = "x-hide-visibility";
  private final String spriteVMLClass = "x-vml-sprite";
  private final String measureSpanVMLClass = "x-vml-measure-span";
  private int zoom = 21600;
  private double coordsize = 1000;
  private Scaling viewBoxShift = null;
  private RegExp newLineRegExp = RegExp.compile("\n");
  private Map<Sprite, PreciseRectangle> textBBoxCache = new HashMap<Sprite, PreciseRectangle>();
  // store rendered text values to emulate SVG text bbox calls
  private Map<Sprite, PrecisePoint> textRenderedPoints = new HashMap<Sprite, PrecisePoint>();

  private Map<Sprite, TextBaseline> textRenderedBaseline = new HashMap<Sprite, TextBaseline>();

  protected boolean ignoreOptimizations = false;

  @Override
  public void addGradient(Gradient gradient) {
    this.gradients.add(gradient);
  }

  @Override
  public void deleteSprite(Sprite sprite) {
    if (sprite instanceof TextSprite) {
      textBBoxCache.remove(sprite);
      textRenderedPoints.remove(sprite);
      textRenderedBaseline.remove(sprite);
    }
    surfaceElement.removeChild(getElement(sprite));
  }

  @Override
  public void draw() {
    super.draw();
    if (surfaceElement == null) {
      surfaceElement = XElement.as(DOM.createDiv());
      surfaceElement.addClassName(baseVMLClass);
      surfaceElement.setSize(width, height);
      addNamespace("vml", "urn:schemas-microsoft-com:vml");
    }

    container.appendChild(surfaceElement);
    container.setSize(width, height);

    renderAll();
  }

  /**
   * Draws the surface ignoring whether or not sprites are dirty.
   */
  public void drawIgnoreOptimizations() {
    ignoreOptimizations = true;
    draw();
    ignoreOptimizations = false;
  }

  @Override
  public void renderSprite(Sprite sprite) {
    // Does the surface element exist?
    if (surfaceElement == null) {
      return;
    }

    if (!sprite.isDirty() && !ignoreOptimizations) {
      return;
    }
    applyAttributes(sprite);
    if (sprite.isTransformDirty() || ignoreOptimizations) {
      transform(sprite);
    }

    sprite.clearDirtyFlags();
  }

  @Override
  public void setCursor(Sprite sprite, String property) {
    XElement element = getElement(sprite);
    if (element != null) {
      element.getStyle().setProperty("cursor", property);
    }
  }

  @Override
  public void setViewBox(double x, double y, double width, double height) {
    // Handle viewbox sizing
    if (this.width > 0 && this.height > 0) {
      double relativeHeight = this.height / height;
      double relativeWidth = this.width / width;
      if (width * relativeHeight < this.width) {
        x -= (this.width - width * relativeHeight) / 2.0 / relativeHeight;
      }
      if (height * relativeWidth < this.height) {
        y -= (this.height - height * relativeWidth) / 2.0 / relativeWidth;
      }

      double size = 1.0 / Math.max(width / this.width, height / this.height);
      // Scale and translate group
      viewBoxShift = new Scaling(size, size, -x, -y);
      for (Sprite sprite : sprites) {
        transform(sprite);
      }
    }
  }

  @Override
  protected PreciseRectangle getBBoxText(TextSprite sprite) {
    XElement element = getElement(sprite);
    if (element == null) {
      return new PreciseRectangle();
    }
    PreciseRectangle bbox = textBBoxCache.get(sprite);
    if (bbox == null) {
      Element textPath = element.childElement("textPath").cast();
      if (textPath != null) {
        TextMetrics.get().bind(textPath);
      }

      SplitResult split = newLineRegExp.split(sprite.getText());
      bbox = new PreciseRectangle();

      for (int i = 0; i < split.length(); i++) {
        double width = TextMetrics.get().getWidth(split.get(i));
        bbox.setWidth(Math.max(bbox.getWidth(), width));
      }
      bbox.setHeight(sprite.getFontSize() * split.length());

      PrecisePoint point = textRenderedPoints.get(sprite);
      TextBaseline baseline = textRenderedBaseline.get(sprite);
      bbox.setX(point.getX());
      bbox.setY(point.getY());
      if (baseline == TextBaseline.MIDDLE) {
        bbox.setY(point.getY() - (bbox.getHeight() / 2.0));
      } else if (baseline == TextBaseline.BOTTOM) {
        bbox.setY(point.getY() - bbox.getHeight());
      }
      textBBoxCache.put(sprite, bbox);
    }
    return bbox;
  }

  /**
   * Adds the passed namespace and URN to the document.
   * 
   * @param namespace the namespace to be added
   * @param urn the schema URN of the namespace
   */
  private native void addNamespace(String namespace, String urn) /*-{
		if (!$doc.namespaces[namespace]) {
			$doc.namespaces.add(namespace, urn);
		}
  }-*/;

  /**
   * Applies pending attributes to the DOM element of a {@link Sprite}.
   * 
   * @param sprite the sprite in need of attributes to be set.
   */
  private void applyAttributes(Sprite sprite) {
    String vml = null;
    XElement element = getElement(sprite);

    // Create sprite element if necessary
    if (element == null) {
      element = createSpriteElement(sprite);
    }

    if (sprite instanceof EllipseSprite || sprite instanceof CircleSprite) {
      vml = ellipticalArc(sprite);
    } else if (sprite instanceof RectangleSprite) {
      RectangleSprite rect = (RectangleSprite) sprite;
      if (rect.isXDirty() || rect.isYDirty() || rect.isWidthDirty() || rect.isHeightDirty() || rect.isRadiusDirty()
          || ignoreOptimizations) {
        // faster conversion for rectangles without rounded corners
        if (Double.isNaN(rect.getRadius()) || rect.getRadius() == 0) {
          StringBuilder path = new StringBuilder();
          long x = Math.round(rect.getX() * zoom);
          long y = Math.round(rect.getY() * zoom);
          long width = Math.round((rect.getX() + rect.getWidth()) * zoom);
          long height = Math.round((rect.getY() + rect.getHeight()) * zoom);
          vml = path.append("m").append(x).append(",").append(y).append(" l").append(width).append(",").append(y).append(
              " l").append(width).append(",").append(height).append(" l").append(x).append(",").append(height).append(
              " x e").toString();
        } else {
          vml = path2vml(new PathSprite(rect));
        }
      }
    } else if (sprite instanceof PathSprite) {
      PathSprite pathSprite = (PathSprite) sprite;
      vml = path2vml(pathSprite);

    } else if (sprite instanceof TextSprite) {
      // Handle text (special handling required)
      setTextAttributes((TextSprite) sprite, element);
    } else if (sprite instanceof ImageSprite) {
      ImageSprite image = (ImageSprite) sprite;
      if (image.isXDirty() || ignoreOptimizations) {
        element.setLeft((int) Math.round(image.getX()));
      }
      if (image.isYDirty() || ignoreOptimizations) {
        element.setTop((int) Math.round(image.getY()));
      }
      if (image.isWidthDirty() || image.isHeightDirty() || ignoreOptimizations) {
        element.setSize(new Size((int) Math.round(image.getWidth()), (int) Math.round(image.getHeight())));
      }
      if (image.isResourceDirty() || ignoreOptimizations) {
        ImageResource resource = image.getResource();
        if (resource != null) {
          StringBuilder builder = new StringBuilder();
          builder.append("url(").append(image.getResource().getSafeUri().asString()).append(") ");
          builder.append(-resource.getLeft()).append("px ");
          builder.append(-resource.getTop()).append("px");
          element.getStyle().setProperty("background", builder.toString());
        } else {
          element.getStyle().clearBackgroundImage();
        }
      }
    }

    if (vml != null) {
      element.setAttribute("path", vml);
    }

    if (sprite.isZIndexDirty() || ignoreOptimizations) {
      applyZIndex(sprite, element);
    }

    String id = spriteIds.get(sprite);
    if (id != null) {
      element.setId(id);
    }

    // Apply clip rectangle to the sprite
    if (sprite.getClipRectangle() != null) {
      applyClip(sprite);
    }

    // Handle fill and opacity
    if (sprite.isOpacityDirty() || sprite.isStrokeOpacityDirty() || sprite.isFillDirty() || ignoreOptimizations) {
      setFill(sprite, element);
    }

    // Handle stroke (all fills require a stroke element)
    if (sprite.isStrokeDirty() || sprite.isStrokeWidthDirty() || sprite.isStrokeOpacityDirty() || sprite.isFillDirty()
        || ignoreOptimizations) {
      setStroke(sprite, element);
    }

    // Hide or show the sprite
    if (sprite.isHiddenDirty() || ignoreOptimizations) {
      if (sprite.isHidden()) {
        element.addClassName(hideClass);
      } else {
        element.removeClassName(hideClass);
      }
    }
  }

  /**
   * Applies the clip rectangle of the given sprite to its DOM element.
   * 
   * @param sprite the sprite to have its clip rectangle applied
   */
  private void applyClip(Sprite sprite) {
    XElement clipElement = getClipElement(sprite);
    if (clipElement == null) {
      clipElement = this.surfaceElement.insertFirst(Document.get().createDivElement()).cast();
      clipElement.addClassName(spriteVMLClass);
      setClipElement(sprite, clipElement);
    }
    PreciseRectangle rect = sprite.getClipRectangle();
    double width = rect.getX() + rect.getWidth();
    double height = rect.getY() + rect.getHeight();
    clipElement.getStyle().setProperty(
        "clip",
        new StringBuilder().append("rect(").append(rect.getY()).append("px ").append(width + "px ").append(height).append(
            "px ").append(rect.getX() + "px").toString());
  }

  /**
   * Applies the z-index of the given sprite to its DOM element.
   * 
   * @param sprite the sprite to have its z-index applied
   */
  private void applyZIndex(Sprite sprite, XElement element) {
    if (element != null) {
      element.getStyle().setProperty("zIndex", String.valueOf(sprite.getZIndex()));
    }
  }

  /**
   * Determines whether a {@link PathSprite} contains a command not supported by
   * VML.
   * 
   * @param sprite the sprite to be inspected
   * @return true if the sprite contains an unsupported command
   */
  private boolean containsNonVMLCommands(PathSprite sprite) {
    for (PathCommand command : sprite.getCommands()) {
      if (!(command instanceof MoveTo) && !(command instanceof CurveTo) && !(command instanceof LineTo)
          && !(command instanceof ClosePath)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Creates a VML DOM node.
   * 
   * @param tagName the type of node to create.
   * @return the created VML DOM node.
   */
  private XElement createNode(String tagName) {
    Element node = Document.get().createElement("vml:" + tagName);
    node.setClassName("vml");
    return XElement.as(node);
  }

  /**
   * Creates the DOM element of the passed {@link Sprite}.
   * 
   * @param sprite the sprite in need of element creation
   */
  private XElement createSpriteElement(Sprite sprite) {
    XElement element;

    if (sprite instanceof ImageSprite) {
      element = createNode("image");
    } else {
      element = createNode("shape");
      XElement skew = createNode("skew");
      skew.setPropertyBoolean("on", true);
      element.appendChild(skew);
      element.setPropertyJSO("skew", skew);
    }

    element.setPropertyString("coordsize", zoom + " " + zoom);
    element.setPropertyString("coordorigin", "0 0");
    element.addClassName(spriteVMLClass);

    if (sprite instanceof TextSprite) {
      XElement path = createNode("path");
      path.setPropertyBoolean("textpathok", true);
      XElement textPath = createNode("textpath");
      textPath.setPropertyBoolean("on", true);
      element.appendChild(textPath);
      element.appendChild(path);
      Style textStyle = textPath.getStyle();
      textStyle.setProperty("lineHeight", "normal");
      textStyle.setProperty("fontVariant", "normal");
      textRenderedPoints.put(sprite, new PrecisePoint());
    }

    this.surfaceElement.appendChild(element);
    setElement(sprite, element);
    return element;
  }

  /**
   * Generates a path sprite made up of an elliptical arc based on the given
   * {@link CircleSprite} or {@link EllipseSprite}. Returns null if not an
   * Circle
   * 
   * @param sprite the circle or ellipse to be converted to an elliptical arc
   * @return the generated elliptical arc
   */
  private String ellipticalArc(Sprite sprite) {
    final double cx, cy, rx, ry;
    if (sprite instanceof EllipseSprite) {
      EllipseSprite ellipse = (EllipseSprite) sprite;
      if (!ellipse.isCenterXDirty() && !ellipse.isCenterYDirty() && !ellipse.isRadiusXDirty()
          && !ellipse.isRadiusYDirty() && !ignoreOptimizations) {
        return null;
      }
      cx = ellipse.getCenterX();
      cy = ellipse.getCenterY();
      rx = ellipse.getRadiusX();
      ry = ellipse.getRadiusY();
    } else if (sprite instanceof CircleSprite) {
      CircleSprite circle = (CircleSprite) sprite;
      if (!circle.isCenterXDirty() && !circle.isCenterYDirty() && !circle.isRadiusDirty() && !ignoreOptimizations) {
        return null;
      }
      cx = circle.getCenterX();
      cy = circle.getCenterY();
      rx = circle.getRadius();
      ry = circle.getRadius();
    } else {
      return null;
    }
    long centerX = Math.round(cx * zoom);
    long yShift = Math.round((cy - ry) * zoom);
    return new StringBuilder().append("ar").append(Math.round((cx - rx) * zoom)).append(",").append(yShift).append(",").append(
        Math.round((cx + rx) * zoom)).append(",").append(Math.round((cy + ry) * zoom)).append(",").append(centerX).append(
        ",").append(yShift).append(",").append(centerX).append(",").append(yShift).toString();
  }

  /**
   * Returns a string representing a VML path using the the passed
   * {@link PathSprite}.
   * 
   * @param sprite the sprite to be converted
   * @return the converted path
   */
  private String path2vml(PathSprite sprite) {
    StringBuilder path = new StringBuilder();

    if (containsNonVMLCommands(sprite)) {
      sprite = sprite.copy().toCurve();
    } else {
      sprite = sprite.copy().toAbsolute();

    }

    for (PathCommand command : sprite.getCommands()) {
      if (command instanceof CurveTo) {
        CurveTo curveto = (CurveTo) command;
        path.append("c").append(Math.round(curveto.getX1() * zoom)).append(",").append(
            Math.round(curveto.getY1() * zoom)).append(",").append(Math.round(curveto.getX2() * zoom)).append(",").append(
            Math.round(curveto.getY2() * zoom)).append(",").append(Math.round(curveto.getX() * zoom)).append(",").append(
            Math.round(curveto.getY() * zoom)).append(" ");
      } else if (command instanceof MoveTo) {
        MoveTo moveto = (MoveTo) command;
        path.append("m").append(Math.round(moveto.getX() * zoom)).append(",").append(Math.round(moveto.getY() * zoom)).append(
            " ");
      } else if (command instanceof LineTo) {
        LineTo lineto = (LineTo) command;
        path.append("l").append(Math.round(lineto.getX() * zoom)).append(",").append(Math.round(lineto.getY() * zoom)).append(
            " ");
      } else if (command instanceof ClosePath) {
        path.append("x ");
      }
    }
    path.append("e");
    return path.toString();
  }

  /**
   * Applies the fill attribute of a sprite to its VML DOM element.
   * 
   * @param sprite the sprite to have its fill set
   */
  private void setFill(Sprite sprite, Element element) {
    Element fill = element.getPropertyJSO("fill").cast();

    if (fill == null) {
      fill = createNode("fill");
      element.setPropertyJSO("fill", fill);
      element.appendChild(fill);
    }

    if (sprite.getFill() == null || sprite.getFill() == Color.NONE) {
      fill.setPropertyBoolean("on", false);
    } else {
      if (sprite.isFillDirty() || ignoreOptimizations) {
        fill.setPropertyBoolean("on", true);
        if (sprite.getFill() instanceof Gradient) {
          Gradient gradient = (Gradient) sprite.getFill();
          // VML angle is offset and inverted from standard, and must be
          // adjusted
          // to match rotation transform
          final double degrees;
          if (sprite.getRotation() != null) {
            degrees = sprite.getRotation().getDegrees();
          } else {
            degrees = 0;
          }

          double angle;
          angle = -(gradient.getAngle() + 270 + degrees) % 360.0;
          // IE will flip the angle at 0 degrees...
          if (angle == 0) {
            angle = 180;
          }
          fill.setPropertyDouble("angle", angle);
          fill.setPropertyString("type", "gradient");
          fill.setPropertyString("method", "sigma");
          StringBuilder stops = new StringBuilder();
          for (Stop stop : gradient.getStops()) {
            if (stops.length() > 0) {
              stops.append(", ");
            }
            stops.append(stop.getOffset()).append("% ").append(stop.getColor());
          }
          Element colors = fill.getPropertyJSO("colors").cast();
          colors.setPropertyString("value", stops.toString());
        } else {
          fill.setPropertyString("color", sprite.getFill().getColor());
          fill.setPropertyString("src", "");
          fill.setPropertyString("type", "solid");
        }
      }

      if (!Double.isNaN(sprite.getOpacity()) && (sprite.isOpacityDirty() || ignoreOptimizations)) {
        fill.setPropertyString("opacity", String.valueOf(sprite.getOpacity()));
      }
      if (!Double.isNaN(sprite.getFillOpacity()) && (sprite.isFillOpacityDirty() || ignoreOptimizations)) {
        fill.setPropertyString("opacity", String.valueOf(sprite.getFillOpacity()));
      }
    }
  }

  /**
   * Applies the stroke attribute of a sprite to its VML dom element.
   * 
   * @param sprite the sprite to have its stroke set
   */
  private void setStroke(Sprite sprite, XElement element) {
    Element stroke = element.getPropertyJSO("stroke").cast();

    if (stroke == null) {
      stroke = createNode("stroke");
      element.setPropertyJSO("stroke", stroke);
      element.appendChild(stroke);
    }

    Color strokeColor = sprite.getStroke();
    if (strokeColor == null || strokeColor == Color.NONE || sprite.getStrokeWidth() == 0.0) {
      stroke.setPropertyBoolean("on", false);
    } else {
      stroke.setPropertyBoolean("on", true);

      if (strokeColor != null && !(strokeColor instanceof Gradient)) {
        // VML does NOT support a gradient stroke :(
        stroke.setPropertyString("color", strokeColor.getColor());
      }

      if (sprite instanceof PathSprite) {
        PathSprite path = (PathSprite) sprite;
        if (path.getStrokeLineCap() != null) {
          stroke.setPropertyString("endcap", path.getStrokeLineCap().getValue());
        }
        if (path.getStrokeLineJoin() != null) {
          stroke.setPropertyString("joinstyle", path.getStrokeLineJoin().getValue());
        }
        if (!Double.isNaN(path.getMiterLimit())) {
          stroke.setPropertyDouble("miterlimit", path.getMiterLimit());
        }
      }

      double width = sprite.getStrokeWidth();
      double opacity = sprite.getStrokeOpacity();
      if (Double.isNaN(width)) {
        width = 0.75;
      } else {
        width *= 0.75;
      }
      if (Double.isNaN(opacity)) {
        opacity = 1;
      }

      // VML Does not support stroke widths under 1, so we're going to fiddle
      // with stroke-opacity instead.
      if (width < 1) {
        opacity *= width;
        width = 1;
      }

      stroke.setPropertyDouble("weight", width);
      stroke.setPropertyDouble("opacity", opacity);
    }
  }

  /**
   * Sets the text alignment on the given {@link Style}.
   * 
   * @param style the style
   * @param align the text alignment
   */
  private native void setTextAlign(Style style, String align) /*-{
		style["v-text-align"] = align;
  }-*/;

  /**
   * Applies the attributes of the passed {@link TextSprite} to its VML element.
   * 
   * @param sprite the sprite whose attributes to use
   */
  private void setTextAttributes(TextSprite sprite, XElement element) {
    Element textPath = element.childElement("textPath").cast();
    Style textStyle = textPath.getStyle();
    textBBoxCache.remove(sprite);

    if (sprite.isFontSizeDirty() || ignoreOptimizations) {
      if (sprite.getFontSize() > 0) {
        textStyle.setFontSize(sprite.getFontSize(), Unit.PX);
      } else {
        textStyle.clearFontSize();
      }
    }
    if (sprite.isFontStyleDirty() || ignoreOptimizations) {
      if (sprite.getFontStyle() != null) {
        textStyle.setFontStyle(sprite.getFontStyle());
      } else {
        textStyle.clearFontStyle();
      }
    }
    if (sprite.isFontWeightDirty() || ignoreOptimizations) {
      if (sprite.getFontWeight() != null) {
        textStyle.setFontWeight(sprite.getFontWeight());
      } else {
        textStyle.clearFontWeight();
      }
    }
    if (sprite.isFontDirty() || ignoreOptimizations) {
      if (sprite.getFont() != null) {
        textStyle.setProperty("fontFamily", sprite.getFont());
      } else {
        textStyle.clearProperty("fontFamily");
      }
    }

    // text-anchor emulation
    if (sprite.isTextAnchorDirty() || ignoreOptimizations) {
      if (sprite.getTextAnchor() == TextAnchor.MIDDLE) {
        setTextAlign(textStyle, "center");
      } else if (sprite.getTextAnchor() == TextAnchor.END) {
        setTextAlign(textStyle, "right");
      } else {
        setTextAlign(textStyle, "left");
      }
    }

    if (sprite.isTextDirty() || ignoreOptimizations) {
      if (sprite.getText() != null) {
        textPath.setPropertyString("string", sprite.getText());
      } else {
        textPath.setPropertyString("string", "");
      }
    }

    if (sprite.isTextBaselineDirty() || sprite.isXDirty() || sprite.isYDirty() || ignoreOptimizations) {
      double height = sprite.getFontSize();
      if (sprite.getTextBaseline() == TextBaseline.MIDDLE) {
        height = 0;
      } else if (sprite.getTextBaseline() == TextBaseline.BOTTOM) {
        height *= -1;
      }
      Element path = element.childElement("path").cast();
      path.setPropertyString(
          "v",
          new StringBuilder().append("m").append(Math.round(sprite.getX() * zoom)).append(",").append(
              Math.round((sprite.getY() + (height / 2.0)) * zoom)).append(" l").append(
              Math.round(sprite.getX() * zoom) + 1).append(",").append(
              Math.round((sprite.getY() + (height / 2.0)) * zoom)).toString());
      textRenderedPoints.put(sprite, new PrecisePoint(sprite.getX(), sprite.getY()));
      textRenderedBaseline.put(sprite, sprite.getTextBaseline());
    }
  }

  /**
   * Applies transformation to passed sprite
   * 
   * @param sprite the sprite to be transformed
   */
  private void transform(Sprite sprite) {
    double deltaDegrees = 0;
    double deltaScaleX = 1;
    double deltaScaleY = 1;
    Matrix matrix = new Matrix();
    Rotation rotation = sprite.getRotation();
    Scaling scaling = sprite.getScaling();
    Translation translation = sprite.getTranslation();
    XElement element = getElement(sprite);
    Style style = element.getStyle();
    Element skew = element.getPropertyJSO("skew").cast();

    if (rotation != null) {
      matrix.rotate(rotation.getDegrees(), rotation.getX(), rotation.getY());
      deltaDegrees += rotation.getDegrees();
    }
    if (scaling != null) {
      matrix.scale(scaling.getX(), scaling.getY(), scaling.getCenterX(), scaling.getCenterY());
      deltaScaleX *= scaling.getX();
      deltaScaleY *= scaling.getY();
    }
    if (translation != null) {
      matrix.translate(translation.getX(), translation.getY());
    }
    if (viewBoxShift != null) {
      matrix.prepend(viewBoxShift.getX(), 0, 0, viewBoxShift.getX(), viewBoxShift.getCenterX() * viewBoxShift.getX(),
          viewBoxShift.getCenterY() * viewBoxShift.getX());
    }

    if (!(sprite instanceof ImageSprite) && skew != null) {
      // matrix transform via VML skew
      skew.setPropertyString("origin", "0,0");
      skew.setPropertyString(
          "matrix",
          new StringBuilder().append(toFixed(matrix.get(0, 0), 4)).append(", ").append(toFixed(matrix.get(0, 1), 4)).append(
              ", ").append(toFixed(matrix.get(1, 0), 4)).append(", ").append(toFixed(matrix.get(1, 1), 4)).append(
              ", 0, 0").toString());

      // ensure offset is less than or equal to 32767 and greater than or equal
      // to -32768, otherwise VMl crashes
      double offsetX = Math.max(Math.min(matrix.get(0, 2), 32767), -32768);
      double offsetY = Math.max(Math.min(matrix.get(1, 2), 32767), -32768);
      String offset = toFixed(offsetX, 4) + ", " + toFixed(offsetY, 4);
      skew.setPropertyString("offset", offset);
    } else {
      double deltaX = matrix.get(0, 2);
      double deltaY = matrix.get(1, 2);
      // Scale via coordsize property
      double zoomScaleX = zoom / deltaScaleX;
      double zoomScaleY = zoom / deltaScaleY;

      element.setPropertyString("coordsize", Math.abs(zoomScaleX) + " " + Math.abs(zoomScaleY));

      // Rotate via rotation property
      double newAngle = deltaDegrees * (deltaScaleX * ((deltaScaleY < 0) ? -1 : 1));
      if ((style.getProperty("rotation") == null && newAngle != 0)) {
        style.setProperty("rotation", String.valueOf(newAngle));
      } else if (style.getProperty("rotation") != null && newAngle != Double.valueOf(style.getProperty("rotation"))) {
        style.setProperty("rotation", String.valueOf(newAngle));
      }
      if (deltaDegrees != 0) {
        // Compensate x/y position due to rotation
        Matrix compMatrix = new Matrix();
        compMatrix.rotate(-deltaDegrees, deltaX, deltaY);
        deltaX = deltaX * compMatrix.get(0, 0) + deltaY * compMatrix.get(0, 1) + compMatrix.get(0, 2);
        deltaY = deltaX * compMatrix.get(1, 0) + deltaY * compMatrix.get(1, 1) + compMatrix.get(1, 2);
      }

      String flip = "";
      // Handle negative scaling via flipping
      if (deltaScaleX < 0) {
        flip += "x";
      }
      if (deltaScaleY < 0) {
        flip += " y";
      }
      if (flip != "") {
        style.setProperty("flip", flip);
      }

      // Translate via coordorigin property
      element.setPropertyString("coordorigin", (-zoomScaleX * (deltaX / ((ImageSprite) sprite).getWidth())) + " "
          + (-zoomScaleY * (deltaY / ((ImageSprite) sprite).getHeight())));
    }
  }

}
