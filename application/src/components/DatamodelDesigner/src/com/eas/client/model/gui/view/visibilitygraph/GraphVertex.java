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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class GraphVertex<C> {

    protected List<C> linked = new ArrayList<>();

    public GraphVertex() {
        super();
    }

    protected boolean addLinkedVertex(C aVertex) {
        if (aVertex != null) {
            return linked.add(aVertex);
        }
        return false;
    }

    public List<C> getLinked() {
        return linked;
    }
}
