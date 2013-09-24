/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing.graph;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class Vertex<A> {

    public A attribute;
    protected Set<Vertex<A>> ajacent = new HashSet<>();

    public Vertex(A aAttribute) {
        super();
        attribute = aAttribute;
    }

    public Set<Vertex<A>> getAjacent() {
        return ajacent;
    }
}
