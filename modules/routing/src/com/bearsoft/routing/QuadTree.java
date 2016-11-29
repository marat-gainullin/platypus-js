/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author mg
 */
public class QuadTree<E> extends Quadtree {

    public QuadTree() {
        super();
    }

    public void insert(Rectangle aKey, E aElement) {
        super.insert(new Envelope(aKey.x, aKey.x + aKey.width, aKey.y, aKey.y + aKey.height), aElement);// JTS uses exclusive coordinates in integer domain
    }

    public List<E> query(Rectangle aCriteria) {
        return super.query(new Envelope(aCriteria.x, aCriteria.x + aCriteria.width, aCriteria.y, aCriteria.y + aCriteria.height));// JTS uses exclusive coordinates in integer domain
    }
    
    public List<E> query(Point aCriteria) {
        return super.query(new Envelope(aCriteria.x, aCriteria.x + 1, aCriteria.y, aCriteria.y + 1));// JTS uses exclusive coordinates in integer domain
    }    
    
    public boolean remove(Rectangle aCriteria, E aElement) {
        Envelope env = new Envelope(aCriteria.x, aCriteria.x + aCriteria.width, aCriteria.y, aCriteria.y + aCriteria.height);
        boolean res = super.remove(env, aElement);// JTS uses exclusive coordinates in integer domain
        while(super.remove(env, aElement)){}
        return res;
    }
    
}
