package com.jeta.forms.store;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import java.io.Externalizable;
import java.io.IOException;
import java.util.HashMap;

/**
 * Defines an interface that all persitable objects in the forms designer
 * must implement.  It is similar to Exernalizable except that it adds
 * read/write methods that take JETAObjectInput and JETAObjectOutput.  This
 * is needed so that our 'Serializable' objects can be stored using an
 * arbitrary persistence scheme.  Currently, we support standard Java Serialization
 * and XML.  Using this approach, it would be easy to added support for any other
 * type of format.
 *
 * @author Jeff Tassin
 */
public interface JETAPersistable extends Externalizable {

    /**
     * Objects implement this method to restore their state.  Primitives and objects can be read
     * from the JETAObjectInput instance.
     */
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException;

    /**
     * Objects implement this method to store their state.  Primitives and objects can be written
     * using the JETAObjectOutput instance.
     */
    public void write(JETAObjectOutput out) throws IOException;

    /**
     * An abstract way to resolve references onto the grid components in closest
     * parent linked form. Some dynamic properties can resolve there references
     * if they whant, reference for context menu is good example.
     */
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName);
}
