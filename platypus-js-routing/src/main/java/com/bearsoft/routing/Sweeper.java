/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing;

import com.bearsoft.routing.graph.Vertex;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class Sweeper {

    public static List<Vertex<PathFragment>> build(int width, int height, Set<Rectangle> obstacles, QuadTree aVertexIndex) {
        Sweeper sweeper = new Sweeper();
        for (Rectangle o : obstacles) {
            sweeper.add(o);
        }
        sweeper.build(width, height, aVertexIndex);
        return sweeper.getGraph();
    }

    private void removeSweeped(int aSweepStop, SortedMap<Integer, List<Segment>> aSweepLine) {
        List<Integer> toRemove = new ArrayList<>();
        for (Entry<Integer, List<Segment>> el : aSweepLine.entrySet()) {
            for (int i = el.getValue().size() - 1; i >= 0; i--) {
                Segment s = el.getValue().get(i);
                if (aSweepStop > s.last) {
                    el.getValue().remove(i);
                }
            }
            if (el.getValue().isEmpty()) {
                toRemove.add(el.getKey());
            }
        }
        for (Integer key : toRemove) {
            aSweepLine.remove(key);
        }
    }

    private void addSweepers(SortedMap<Integer, List<Segment>> aDestination, SortedMap<Integer, List<Segment>> aSource) {
        for (Entry<Integer, List<Segment>> sourceEl : aSource.entrySet()) {
            assert sourceEl.getValue() != null;
            assert !sourceEl.getValue().isEmpty();
            List<Segment> destSegs = aDestination.get(sourceEl.getKey());
            if (destSegs == null) {
                destSegs = new ArrayList<>();
                aDestination.put(sourceEl.getKey(), destSegs);
            }
            destSegs.addAll(sourceEl.getValue());
        }
    }

    protected class Segment {

        // first and last y-coordinate, inclusive.
        public int start;
        public int end;
        // last x-coordinate of the segment, inclusive.
        public int last;

        public Segment(boolean aFirst) {
        }

        public Segment(int aStart, int aEnd, int aLast) {
            start = aStart;
            end = aEnd;
            last = aLast;
        }
    }
    public static final int TURN_COST_BIAS = 100;
    protected SortedMap<Integer, SortedMap<Integer, List<Segment>>> sweepedlayers = new TreeMap<>();
    protected List<Vertex<PathFragment>> graph;

    protected Sweeper() {
        super();
    }

    List<Vertex<PathFragment>> getGraph() {
        return graph;
    }

    void add(Rectangle o) {
        // add real segment to sweep stops
        add(o.x, new Segment(o.y >= 0 ? o.y : 0, (o.y >= 0 ? o.y : 0) + o.height, o.x + o.width - 1));
        // give a segment a chance to be closed, while sweeping, by
        // adding another sweep stop, with may stay as a fake or
        // it may be also filled by real segments in future.
        add(o.x + o.width, null);
    }

    void add(int aLayer, Segment aValue) {
        SortedMap<Integer, List<Segment>> sweepedLayer = sweepedlayers.get(aLayer);
        if (sweepedLayer == null) {
            sweepedLayer = new TreeMap<>();
            sweepedlayers.put(aLayer, sweepedLayer);
        }
        if (aValue != null) {
            List<Segment> sameStartedSegments = sweepedLayer.get(aValue.start);
            if (sameStartedSegments == null) {
                sameStartedSegments = new ArrayList<>();
                sweepedLayer.put(aValue.start, sameStartedSegments);
            }
            sameStartedSegments.add(aValue);
        }
    }

    void build(int aRight, int aBottom, QuadTree aVertexIndex) {
        graph = new ArrayList<>();
        add(0, null);
        //add(aRight, null);
        SortedMap<Integer, List<Segment>> sweepLine = new TreeMap<>();
        List<Segment> bottomSegment = new ArrayList<>();
        bottomSegment.add(new Segment(aBottom, aBottom, aRight));
        sweepLine.put(aBottom, bottomSegment);
        SortedMap<Integer, Vertex<PathFragment>> prevFreeTiles = null;
        int prevSweepStop = 0;
        for (Entry<Integer, SortedMap<Integer, List<Segment>>> layer : sweepedlayers.entrySet()) {
            if (aRight < layer.getKey()) {
                aRight = layer.getKey();
            }
            // Let's remove completly sweeped segments
            removeSweeped(layer.getKey(), sweepLine);
            // Let's add segments from new layer to sweepline
            addSweepers(sweepLine, layer.getValue());

            // Let's find holes between generic filled segments
            int top = -1;// top of generic filled segment
            int bottom = -1;// bottom of generic filled segment
            // Holes between them will be free segments
            SortedMap<Integer, Vertex<PathFragment>> freeTiles = new TreeMap<>();
            for (List<Segment> sameTopSegments : sweepLine.values()) {
                for (Segment s : sameTopSegments) {
                    if (s.start - bottom > 1) {
                        int freeTop = bottom + 1;
                        int freeBottom = s.start - 1;
                        assert freeTop <= freeBottom;
                        Vertex<PathFragment> freeVertex = new Vertex<>(new PathFragment(new Rectangle(layer.getKey(), freeTop, 0, freeBottom - freeTop + 1)));
                        freeTiles.put(freeTop, freeVertex);
                        graph.add(freeVertex);
                        top = s.start;
                        bottom = s.end;
                    } else {
                        // max finding - bottom tracking
                        if (s.end > bottom) {
                            bottom = s.end;
                        }
                        assert s.start >= top;
                    }
                }
            }
            if (prevFreeTiles != null) {
                for (Vertex<PathFragment> v : prevFreeTiles.values()) {
                    v.attribute.rect.width = layer.getKey() - prevSweepStop;
                    aVertexIndex.insert(v.attribute.rect, v);
                }
            }
            interconnect(prevFreeTiles, freeTiles);
            prevFreeTiles = freeTiles;
            prevSweepStop = layer.getKey();
        }
        for (Vertex<PathFragment> v : prevFreeTiles.values()) {
            if (v.attribute.rect.width < aRight - prevSweepStop) {
                v.attribute.rect.width = aRight - prevSweepStop;
                aVertexIndex.insert(v.attribute.rect, v);
            }
        }
    }

    protected void interconnect(SortedMap<Integer, Vertex<PathFragment>> aLeftTiles, SortedMap<Integer, Vertex<PathFragment>> aRightTiles) {
        if (aLeftTiles != null && aRightTiles != null) {
            if (!aLeftTiles.isEmpty() && !aRightTiles.isEmpty()) {
                Vertex<PathFragment>[] left = aLeftTiles.values().toArray(new Vertex[]{});
                Vertex<PathFragment>[] right = aRightTiles.values().toArray(new Vertex[]{});
                int leftIdx = 0;
                int rightIdx = 0;
                while (leftIdx < left.length && rightIdx < right.length) {
                    Vertex<PathFragment> leftVertex = left[leftIdx];
                    Vertex<PathFragment> rightVertex = right[rightIdx];
                    if (touches(leftVertex.attribute.rect, rightVertex.attribute.rect)) {
                        leftVertex.getAjacent().add(rightVertex);
                        rightVertex.getAjacent().add(leftVertex);
                    }
                    if (leftVertex.attribute.rect.y + leftVertex.attribute.rect.height < rightVertex.attribute.rect.y + rightVertex.attribute.rect.height) {
                        leftIdx++;
                    } else {
                        rightIdx++;
                    }
                }
            }
        }
    }

    private boolean touches(Rectangle aValue, Rectangle aValue1) {
        return aValue.y >= aValue1.y && aValue.y < aValue1.y + aValue1.height
                || aValue1.y >= aValue.y && aValue1.y < aValue.y + aValue.height;
    }

    /**
     * Computes planar metric of
     *
     * @param aValue
     * @param aValue1
     * @return
     *
    private int edgeCost(Rectangle aValue, Rectangle aValue1) {
        int xLen = Math.abs(aValue.x + aValue.width / 2 - aValue1.x + aValue1.width / 2);
        int yLen = Math.abs(aValue.y + aValue.height / 2 - aValue1.y + aValue1.height / 2);
        if (xLen > 0 && yLen > 0) {
            return xLen + yLen + TURN_COST_BIAS;
        } else {
            return xLen + yLen;
        }
    }
    */ 
}
