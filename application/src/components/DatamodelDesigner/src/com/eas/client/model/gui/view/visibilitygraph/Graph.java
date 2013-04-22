/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view.visibilitygraph;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class Graph<VT> {

    protected Set<VT> vertices = new HashSet<>();

    public Graph() {
        super();
    }

    public boolean addVertex(VT aVertex) {
        return vertices.add(aVertex);
    }

    public boolean removeVertex(VT aVertex) {
        return vertices.remove(aVertex);
    }

    public void clear() {
        vertices.clear();
    }
}
