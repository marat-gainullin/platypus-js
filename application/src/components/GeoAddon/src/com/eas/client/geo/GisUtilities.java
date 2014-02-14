/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.eas.util.gis.GeometryUtils;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author mg
 */
public class GisUtilities {

    protected static final StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    protected static FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
    protected static StyleBuilder sb = new StyleBuilder();
   
    public static void convertSimpleFeature2SelectionEntries(SimpleFeature aFeature, List<SelectionEntry> aDestination, RowsetFeatureDescriptor aFeatureDescriptor) throws Exception {
        ApplicationEntity<?, ?, ?> entity = aFeatureDescriptor.getEntity();
        Rowset rs = entity.getRowset();
        String featureID = aFeature.getID();
        Object oGeometry = aFeature.getAttribute(aFeature.getFeatureType().getGeometryDescriptor().getLocalName());
        assert oGeometry instanceof Geometry;
        Object oRow = aFeature.getAttribute(DatamodelDataStore.ROW_ATTR_NAME);
        assert oRow instanceof Row;
        Geometry geom = (Geometry) oGeometry;
        String geometryAttrName = aFeature.getFeatureType().getGeometryDescriptor().getLocalName();
        int geometryColIndex = rs.getFields().find(geometryAttrName);
        if (geom instanceof MultiPolygon
                || geom instanceof MultiLineString
                || geom instanceof MultiPoint) {
            MultiPolygon mPolygon = (MultiPolygon) geom;
            for (int g = 0; g < mPolygon.getNumGeometries(); g++) {
                Geometry section = mPolygon.getGeometryN(g);
                convertGeometry2SelectionEntries(entity, (Row) oRow, featureID, geometryColIndex, g, section, aDestination);
            }
        } else {
            convertGeometry2SelectionEntries(entity, (Row) oRow, featureID, geometryColIndex, -1, geom, aDestination);
        }
                }

    public static void convertGeometry2SelectionEntries(ApplicationEntity<?, ?, ?> aEntity, Row aRow, String aFeatureId, int aGeometryColIndex, int aNumGeometry, Geometry aGeometry, List<SelectionEntry> aDestination) {
        if (aGeometry instanceof Polygon && ((Polygon) aGeometry).getNumInteriorRing() > 0) {
            Polygon polygon = (Polygon) aGeometry;
            Polygon shell = GeometryUtils.getPolygonShell(polygon);
            putGeometry2SelectionEntries(aEntity, aRow, aFeatureId, aGeometryColIndex, aNumGeometry, -1, shell, aDestination);
            Polygon[] holes = GeometryUtils.getPolygonHoles(polygon);
            for (int i = 0; i < holes.length; i++) {
                putGeometry2SelectionEntries(aEntity, aRow, aFeatureId, aGeometryColIndex, aNumGeometry, i, holes[i], aDestination);
            }
        } else {
            putGeometry2SelectionEntries(aEntity, aRow, aFeatureId, aGeometryColIndex, aNumGeometry, -1, aGeometry, aDestination);
        }
    }

    public static void putGeometry2SelectionEntries(ApplicationEntity<?, ?, ?> aEntity, Row aRow, String aFeatureId, int aGeometryColIndex, int aNumGeometry, int aNumHole, Geometry aGeometry, List<SelectionEntry> aDestination) {
        Coordinate[] coordinates = aGeometry.getCoordinates();
            for (int i = 0; i < coordinates.length; i++) {
            SelectionEntry entry = new SelectionEntry(aEntity, aRow, aFeatureId, aGeometryColIndex, aNumGeometry, i, aNumHole, coordinates[i]);
                aDestination.add(entry);
            }
        }

    public static Set<Geometry> convertSelectionEntries2Geometries(List<SelectionEntry> aSelection) throws Exception {
        Set<Geometry> collectedGeometries = new HashSet<>();
        for (SelectionEntry sEntry : aSelection) {
            collectedGeometries.add((Geometry) sEntry.getRow().getColumnObject(sEntry.getGeometryColIndex()));
        }
        return collectedGeometries;
    }

    public static boolean inject(Style aStyle, Graphic aGraphic) {
        if (aStyle != null) {
            List<FeatureTypeStyle> fstyles = aStyle.featureTypeStyles();
            if (fstyles != null && fstyles.size() > 0) {
                FeatureTypeStyle fstyle = fstyles.get(0);
                if (fstyle != null) {
                    List<Rule> rules = fstyle.rules();
                    if (rules != null && rules.size() > 0) {
                        Rule rule = rules.get(0);
                        if (rule != null) {
                            Symbolizer[] symbolizers = rule.getSymbolizers();
                            for (Symbolizer symbolizer : symbolizers) {
                                if (symbolizer instanceof PointSymbolizer) {
                                    PointSymbolizer ps = (PointSymbolizer) symbolizers[0];
                                    ps.setGraphic(aGraphic);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Graphic createGraphic(URL aUrl, String mimeType) {
        ExternalGraphic externalGraphic = sb.createExternalGraphic(aUrl, mimeType);
        return sb.createGraphic(externalGraphic, null, null);
    }

    public static Style buildSelectionPhantomStyle() {
        Stroke stroke = sb.createStroke(Color.darkGray, 1, new float[]{7, 5});
        stroke.setOpacity(ff.literal(0.8f));
        LineSymbolizer ls = sf.createLineSymbolizer(stroke, null);
        Style style = sf.createStyle();
        Rule rule1 = sf.createRule();
        rule1.symbolizers().add(ls);
        Rule[] rules = new Rule[]{rule1};
        FeatureTypeStyle tStyle = sf.createFeatureTypeStyle(rules);
        style.featureTypeStyles().add(tStyle);
        return style;
    }

    public static Style buildSelectionStyle() {
        FeatureStyleDescriptor styleHelper = new FeatureStyleDescriptor();
        styleHelper.setFillColor(Color.green.brighter());
        styleHelper.setLineColor(Color.black);
        styleHelper.setSize(5.0f);
        styleHelper.setPointSymbol(PointSymbol.SQUARE);
        return styleHelper.buildStyle(SelectionEntry.SELECTION_ENTRY_GEOMETRY_BINDING_CLASS, null);
    }
}
