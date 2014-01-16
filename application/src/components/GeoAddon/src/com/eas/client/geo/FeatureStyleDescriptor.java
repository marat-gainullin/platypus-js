/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.eas.client.model.ModelElementRef;
import com.eas.store.Serial;
import com.eas.store.SerialColor;
import com.eas.store.SerialFont;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.function.DefaultFunctionFactory;
import org.geotools.styling.*;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

/**
 *
 * @author pk
 */
public class FeatureStyleDescriptor implements Cloneable {

    public static final String FILLCOLOR = "fillColor";
    public static final String FONT = "font";
    public static final String HALOCOLOR = "haloColor";
    public static final String LABELFIELD = "labelField";
    public static final String LINECOLOR = "lineColor";
    public static final String OPACITY = "opacity";
    public static final String POINTSYMBOL = "pointSymbol";
    public static final String SIZE = "size";
    private static final StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private static final FilterFactory ff = CommonFactoryFinder.getFilterFactory();
    private final static Color DEFAULT_FILL_COLOR = Color.white;
    private final static Color DEFAULT_LINE_COLOR = Color.black;
    private final static Color DEFAULT_SHADOW_COLOR = new Color(85, 85, 85);
    private final static float DEFAULT_OPACITY_VALUE = 1;
    private final static float DEFAULT_SHADOW_OPACITY_VALUE = 0.8f;
    private final static float DEFAULT_SIZE_VALUE = 1;
    private ModelElementRef labelField;
    private ModelElementRef geometryField;
    private Font font;
    private Color lineColor, fillColor, haloColor;
    private Integer opacity;
    private Float size;
    private PointSymbol pointSymbol;
    private SerialColor serialLineColor;
    private SerialColor serialFillColor;
    private SerialColor serialHaloColor;
    private SerialFont serialFont;
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public boolean isEmpty() {
        return labelField == null
                && font == null
                && lineColor == null
                && fillColor == null
                && haloColor == null
                && opacity == null
                && size == null
                && pointSymbol == null
                && serialLineColor == null
                && serialFillColor == null
                && serialFont == null;
    }

    public boolean isEqual(FeatureStyleDescriptor other) {
        if (this == other) {
            return true;
        }
        if (this.labelField != other.labelField && (this.labelField == null || !this.labelField.equals(other.labelField))) {
            return false;
        }
        if (this.font != other.font && (this.font == null || !this.font.equals(other.font))) {
            return false;
        }
        if (this.lineColor != other.lineColor && (this.lineColor == null || !this.lineColor.equals(other.lineColor))) {
            return false;
        }
        if (this.fillColor != other.fillColor && (this.fillColor == null || !this.fillColor.equals(other.lineColor))) {
            return false;
        }
        if (this.haloColor != other.haloColor && (this.haloColor == null || !this.haloColor.equals(other.lineColor))) {
            return false;
        }
        if (this.opacity != other.opacity && (this.opacity == null || !this.opacity.equals(other.opacity))) {
            return false;
        }
        if (this.size != other.size && (this.size == null || !this.size.equals(other.size))) {
            return false;
        }
        if (this.pointSymbol != other.pointSymbol && (this.pointSymbol == null || this.pointSymbol != other.pointSymbol)) {
            return false;
        }
        return true;
    }

    public Style buildStyle(Class<? extends Geometry> binding, String aWKT) {
        Style style = buildStyleImpl(binding, aWKT);
        if (haloColor != null) {
            StyleBuilder sb = new StyleBuilder();
            for (final FeatureTypeStyle fts : style.featureTypeStyles()) {
                for (final Rule r : fts.rules()) {
                    for (final Symbolizer s : r.symbolizers()) {
                        if (s != null && s instanceof TextSymbolizer) {
                            ((TextSymbolizer) s).setHalo(sb.createHalo(haloColor, 1));
                        }
                    }
                }
            }
        }
        return style;
    }

    private Style createPolygonStyle() {
        Rule rule = sf.createRule();
        if (opacity == null || opacity.intValue() == 100) {
            FunctionFactory funcf = new DefaultFunctionFactory();
            List<Expression> lst = new ArrayList<>(3);
            lst.add(ff.property(geometryField == null ? "" : geometryField.getFieldName()));
            lst.add(ff.literal(0.00004f));
            lst.add(ff.literal(-0.0002f));
            Function f = funcf.function("offset", lst, null);
            PolygonSymbolizer sym = sf.createPolygonSymbolizer(null,
                    sf.createFill(ff.literal(DEFAULT_SHADOW_COLOR),
                            ff.literal(DEFAULT_SHADOW_OPACITY_VALUE)),
                    null);
            sym.setGeometry(f);
            rule.symbolizers().add(sym);
        }

        Stroke stroke = sf.createStroke(ff.literal(lineColor == null ? DEFAULT_LINE_COLOR : lineColor),
                ff.literal(size == null ? DEFAULT_SIZE_VALUE : size.floatValue()),
                ff.literal((float) (opacity == null ? DEFAULT_OPACITY_VALUE : ((Number) (opacity / 100.0)).floatValue())));
        PolygonSymbolizer symPoly = sf.createPolygonSymbolizer(stroke,
                sf.createFill(ff.literal(fillColor == null ? DEFAULT_FILL_COLOR : fillColor),
                        ff.literal((float) (opacity == null ? DEFAULT_OPACITY_VALUE : ((Number) (opacity / 100.0)).floatValue()))),
                null);
        rule.symbolizers().add(symPoly);
        TextSymbolizer textSym = buildTextSymbolizer();
        if (textSym != null) {
            rule.symbolizers().add(textSym);
        }
        FeatureTypeStyle fts = sf.createFeatureTypeStyle(new Rule[]{rule});
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    public TextSymbolizer buildTextSymbolizer() {
        if (labelField != null) {
            org.geotools.styling.Font labelFont = buildFont();
            org.geotools.styling.Font iFont = (labelFont == null ? sf.getDefaultFont() : labelFont);
            Fill labelFill = sf.createFill(ff.literal(Color.BLACK));
            return sf.createTextSymbolizer(
                    labelFill, new org.geotools.styling.Font[]{iFont}, null, ff.property(labelField == null ? null : labelField.getFieldName()), null, null);
        }
        return null;
    }

    private Style createLineStyle(String aWKT) {
//        FunctionFactory funcf = new DefaultFunctionFactory();
//        FunctionFactory arrowFact = new ArrowFunctionFactory();
//        List<Expression> lst = new ArrayList<>(2);
//        lst.add(ff.property(geometryField == null ? "" : geometryField.getFieldName()));
//        Function f = funcf.function("endPoint", lst, null);
//        Stroke stroke = sf.createStroke(ff.literal(lineColor == null ? DEFAULT_LINE_COLOR : lineColor), null);
//        Mark mark = sf.createMark(ff.literal("shape://carrow"),
//                stroke, null, null, null);//sf.createFill(ff.literal(lineColor == null ? DEFAULT_LINE_COLOR : lineColor))
//        List<GraphicalSymbol> graphicalLst = new ArrayList<>(1);
//        graphicalLst.add(mark);
//        lst.add(ff.literal(aWKT));
//        Graphic graphic = sf.graphic(graphicalLst,
//                ff.literal((float) (opacity == null ? DEFAULT_OPACITY_VALUE : ((Number) (opacity / 100.0)).floatValue())),
//                ff.literal(20),
//                arrowFact.function("endArrowAngle", lst, null),
//                sf.createAnchorPoint(ff.literal(0.0f), ff.literal(0.0f)),
//                sf.createDisplacement(ff.literal(0.0f), ff.literal(0.0f)));
//        PointSymbolizer ps = sf.createPointSymbolizer(graphic, null);
//        ps.setGeometry(f);
        Stroke lineStroke = sf.createStroke(ff.literal(lineColor == null ? DEFAULT_LINE_COLOR : lineColor),
                ff.literal(size == null ? DEFAULT_SIZE_VALUE : size.floatValue()),
                ff.literal((float) (opacity == null ? DEFAULT_OPACITY_VALUE : ((Number) (opacity / 100.0)).floatValue())));
        LineSymbolizer lineSym = sf.createLineSymbolizer(lineStroke, null);
        Rule rule = sf.createRule();
        rule.symbolizers().add(lineSym);
        //rule.symbolizers().add(ps);
        TextSymbolizer textSym = buildTextSymbolizer();
        if (textSym != null) {
            rule.symbolizers().add(textSym);
        }
        FeatureTypeStyle fts = sf.createFeatureTypeStyle(new Rule[]{rule});
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    public Style buildStyleImpl(Class<? extends Geometry> binding, String aWKT) {
        if (com.vividsolutions.jts.geom.Point.class.isAssignableFrom(binding)
                || com.vividsolutions.jts.geom.MultiPoint.class.isAssignableFrom(binding)) {
            return SLD.createPointStyle(pointSymbol == null ? "Circle" : pointSymbol.getName(),
                    lineColor == null ? DEFAULT_LINE_COLOR : lineColor,
                    fillColor == null ? DEFAULT_FILL_COLOR : fillColor,
                    (float) (opacity == null ? DEFAULT_OPACITY_VALUE : ((Number) (opacity / 100.0)).floatValue()),
                    size == null ? DEFAULT_SIZE_VALUE : size.floatValue(),
                    labelField == null ? null : labelField.getFieldName(),
                    buildFont());
        }
        if (com.vividsolutions.jts.geom.LineString.class.isAssignableFrom(binding)
                || com.vividsolutions.jts.geom.MultiLineString.class.isAssignableFrom(binding)) {
            return createLineStyle(aWKT);
        } else {// polygon geometry class is assumed by defualt
            return createPolygonStyle();
        }
    }

    private org.geotools.styling.Font buildFont() {
        return font == null ? null : sf.createFont(
                ff.literal(font.getFamily()),
                ff.literal((Font.ITALIC & font.getStyle()) != 0 ? "Italic" : "Normal"),
                ff.literal((Font.BOLD & font.getStyle()) != 0 ? "Bold" : "Normal"),
                ff.literal(String.valueOf(font.getSize())));
    }

    @Override
    public FeatureStyleDescriptor clone() throws CloneNotSupportedException {
        return (FeatureStyleDescriptor) super.clone();
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color aValue) {
        Color old = fillColor;
        fillColor = aValue;
        serialFillColor = aValue == null ? null : new SerialColor(aValue);
        firePropertyChange(FILLCOLOR, old, aValue);
    }

    @Serial
    public SerialColor getSerialFillColor() {
        return serialFillColor;
    }

    @Serial
    public void setSerialFillColor(SerialColor c) {
        serialFillColor = c;
        fillColor = c == null ? null : c.getDelegate();
    }

    public Color getHaloColor() {
        return haloColor;
    }

    public void setHaloColor(Color aValue) {
        Color old = haloColor;
        haloColor = aValue;
        serialHaloColor = aValue == null ? null : new SerialColor(aValue);
        firePropertyChange(HALOCOLOR, old, aValue);
    }

    @Serial
    public SerialColor getSerialHaloColor() {
        return serialHaloColor;
    }

    @Serial
    public void setSerialHaloColor(SerialColor c) {
        this.serialHaloColor = c;
        haloColor = c == null ? null : c.getDelegate();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font aValue) {
        Font old = font;
        font = aValue;
        serialFont = aValue == null ? null : new SerialFont(aValue);
        firePropertyChange(FONT, old, aValue);
    }

    @Serial
    public SerialFont getSerialFont() {
        return serialFont;
    }

    @Serial
    public void setSerialFont(SerialFont f) {
        this.serialFont = f;
        font = f == null ? null : f.getDelegate();
    }

    @Serial
    public ModelElementRef getLabelField() {
        return labelField;
    }

    @Serial
    public void setLabelField(ModelElementRef aValue) {
        ModelElementRef old = labelField;
        labelField = aValue;
        firePropertyChange(LABELFIELD, old, aValue);
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color aValue) {
        Color old = lineColor;
        lineColor = aValue;
        serialLineColor = aValue == null ? null : new SerialColor(aValue);
        firePropertyChange(LINECOLOR, old, aValue);
    }

    @Serial
    public SerialColor getSerialLineColor() {
        return serialLineColor;
    }

    @Serial
    public void setSerialLineColor(SerialColor c) {
        serialLineColor = c;
        lineColor = c == null ? null : c.getDelegate();
    }

    @Serial
    public Integer getOpacity() {
        return opacity;
    }

    @Serial
    public void setOpacity(Integer aValue) {
        Integer old = opacity;
        opacity = aValue;
        firePropertyChange(OPACITY, old, aValue);
    }

    public PointSymbol getPointSymbol() {
        return pointSymbol;
    }

    public void setPointSymbol(PointSymbol aValue) {
        PointSymbol old = pointSymbol;
        pointSymbol = aValue;
        firePropertyChange(POINTSYMBOL, old, aValue);
    }

    @Serial
    public String getPointSymbolName() {
        return pointSymbol == null ? null : pointSymbol.name();
    }

    @Serial
    public void setPointSymbolName(String aValue) {
        setPointSymbol(aValue != null && !aValue.isEmpty() ? PointSymbol.valueOf(aValue) : null);
    }

    @Serial
    public Float getSize() {
        return size;
    }

    @Serial
    public void setSize(Float aValue) {
        Float old = size;
        size = aValue;
        firePropertyChange(SIZE, old, aValue);
    }

    /**
     * @return the geometryField
     */
    public ModelElementRef getGeometryField() {
        return geometryField;
    }

    /**
     * @param aGeometryField the geometryField to set
     */
    public void setGeometryField(ModelElementRef aGeometryField) {
        geometryField = aGeometryField;
    }
}
